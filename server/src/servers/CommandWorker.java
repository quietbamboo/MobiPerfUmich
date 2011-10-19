/****************************
 *
 * @Date: Oct 12, 2011
 * @Time: 5:01:01 PM
 * @Author: Junxian Huang
 *
 ****************************/
package servers;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

import common.BaseTcpWorker;
import common.Definition;
import common.Util;

/**
 * @author Junxian Huang
 *
 */

public class CommandWorker extends BaseTcpWorker{

	public long id;


	public void run() {

		try {

			client.setSoTimeout(Definition.RECV_TIMEOUT);

			BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
			PrintWriter out = new PrintWriter(client.getOutputStream(), true);
			char buffer[] = new char[20480];

			StringBuilder prefix_sb = new StringBuilder("");
			int bytes_read = in.read(buffer);
			prefix_sb.append(buffer, 0, bytes_read);
			String prefix = prefix_sb.toString();
			System.out.println("prefix:" + prefix_sb.toString());
			if(!readPrefix(prefix))
				return;

			out.print("PrefixOK");
			out.flush();

			StringBuilder report_sb = new StringBuilder("");
			while((bytes_read = in.read(buffer)) > -1){
				//System.out.println("<Thread " + id + "> received " + bytes_read + " bytes");
				report_sb.append(buffer, 0, bytes_read);
			}

			String report = report_sb.toString().trim(); //remove the /r/n in the end
			System.out.println(client.getInetAddress().getHostAddress() + 
					"COMMAND: |||" + report + "|||");

			String root_dir = Util.runCmd("pwd", false) + "/";
			System.out.println("Current directory: " + root_dir);// /home/hjx/mobiperf/
			
			String ip = client.getInetAddress().getHostAddress();
			int port = 0;

			String cmd = "";
			if(report.equals(Definition.COMMAND_MLAB_INIT_DOWNLINK)){
				port = Definition.PORT_MLAB_DOWNLINK;
				cmd = "bash " + root_dir + "tcpdump_init.sh " + 
					type_string + " " + id_string + " " + rid_string + " " + port + " " + ip;

			}else if(report.equals(Definition.COMMAND_MLAB_INIT_UPLINK)){
				port = Definition.PORT_MLAB_UPLINK;
				cmd = "bash " + root_dir + "tcpdump_init.sh " + 
					type_string + " " + id_string + " " + rid_string + " " + port + " " + ip;

			}else if(report.equals(Definition.COMMAND_MLAB_END_DOWNLINK)){
				port = Definition.PORT_MLAB_DOWNLINK;
				cmd = "bash " + root_dir + "tcpdump_end.sh " + 
					type_string + " " + id_string + " " + rid_string + " " + port + " " + ip;

			}else if(report.equals(Definition.COMMAND_MLAB_END_UPLINK)){
				port = Definition.PORT_MLAB_UPLINK;
				cmd = "bash " + root_dir + "tcpdump_end.sh " + 
					type_string + " " + id_string + " " + rid_string + " " + port + " " + ip;

			}else if(report.startsWith("RUBBISH:")){
				//this is just for keep alive, ignore 
				in.close();
				out.close();
				client.close();
				return;//return here, don't need to write command into output
			}else{
				System.out.println("Command not support <" + report + ">");
				in.close();
				out.close();
				client.close();
				return;
			}

			String res = Util.runCmd(cmd, true);
			System.out.println("Command worker: " + res);


			if(report.equals(Definition.COMMAND_MLAB_END_DOWNLINK) || 
			   report.equals(Definition.COMMAND_MLAB_END_UPLINK)){
				//Safe period for
				Thread.sleep(5000);
				String extra = port + "_" + ip + "_" + Util.getCurrentHost();
				upload(prefix + "<" + extra + ">", root_dir + "data/" + type_string + "/" + id_string + "/" + 
						rid_string + "_" + port + "_" + ip + ".pcap");
			}
			

		} catch (Exception e) {
			try {
				if(client != null){
					client.close();
				}
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
		}
	}

	/**
	 * upload tcpdump file to server
	 */
	public boolean upload(String prefix, String file){
		
		System.out.println("Upload: " + prefix + ", " + file);

		Socket remoteTCPSocket; 
		DataOutputStream remoteOutputStream; 
		DataInputStream remoteInputStream;
		byte bytearray[];

		try {
			remoteTCPSocket = new Socket();
			SocketAddress remoteAddr = new InetSocketAddress(Definition.MAIN_SERVER, Definition.PORT_TCPDUMP_REPORT);
			remoteTCPSocket.connect( remoteAddr, Definition.TCP_TIMEOUT_IN_MILLI);
			remoteOutputStream = new DataOutputStream( remoteTCPSocket.getOutputStream() );
			remoteInputStream = new DataInputStream( remoteTCPSocket.getInputStream() );
			remoteTCPSocket.setSoTimeout( Definition.TCP_TIMEOUT_IN_MILLI );

			if (remoteTCPSocket == null || remoteOutputStream == null || remoteInputStream == null)
				return false;

			byte[] buffer = new byte[Definition.PREFIX_RECEIVE_BUFFER_LENGTH];
			byte[] prefix_bytes = prefix.getBytes();

			remoteOutputStream.write(prefix_bytes);
			remoteOutputStream.flush();

			int read_bytes = remoteInputStream.read(buffer, 0, Definition.PREFIX_RECEIVE_BUFFER_LENGTH);

			//after making sure server is ok to receive data
			//verify current trace exists
			File trace = new File(file);
			if(!(trace.exists()))
				return false;
			try{

				//read in the data
				FileInputStream fileinputstream = new FileInputStream(trace);
				bytearray= new byte[Definition.TCPDUMP_RECEIVE_BUFFER_LENGTH];
				int len = -1;

				while((len = fileinputstream.read(bytearray, 0, Definition.TCPDUMP_RECEIVE_BUFFER_LENGTH)) > -1){
					//build a socket to transfer the data
					remoteOutputStream.write(bytearray, 0, len);
				}

				fileinputstream.close();

			}catch(Exception e){
				e.printStackTrace();
			}

			//close all connection
			remoteOutputStream.flush();
			remoteOutputStream.close();
			remoteInputStream.close();
			remoteTCPSocket.close();

		}catch ( Exception e ) {
			e.printStackTrace();
			return false;
		}
		return true;
	}	

}
