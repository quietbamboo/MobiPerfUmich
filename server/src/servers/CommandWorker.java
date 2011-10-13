/****************************
 *
 * @Date: Oct 12, 2011
 * @Time: 5:01:01 PM
 * @Author: Junxian Huang
 *
 ****************************/
package servers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

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

			String report = report_sb.toString();
			System.out.println(client.getInetAddress().getHostAddress() + 
					"COMMAND: |||" + report + "|||");

			String root_dir = Util.runCmd("pwd", false) + "/";
			System.out.println("Current directory: " + root_dir);
			
			String cmd = "";
			if(report.equals(Definition.COMMAND_MLAB_INIT_DOWNLINK)){
				cmd = "bash " + root_dir + "tcpdump_init.sh " + 
					type_string + " " + id_string + " " + rid_string + " " + Definition.PORT_MLAB_DOWNLINK + " " +
					client.getInetAddress().getHostAddress();
				
			}else if(report.equals(Definition.COMMAND_MLAB_INIT_UPLINK)){
				cmd = "bash " + root_dir + "tcpdump_init.sh " + 
					type_string + " " + id_string + " " + rid_string + " " + Definition.PORT_MLAB_UPLINK + " " +
					client.getInetAddress().getHostAddress();
				
			}else if(report.equals(Definition.COMMAND_MLAB_END_DOWNLINK)){
				cmd = "bash " + root_dir + "tcpdump_end.sh " + 
					type_string + " " + id_string + " " + rid_string + " " + Definition.PORT_MLAB_DOWNLINK + " " +
					client.getInetAddress().getHostAddress();
				
			}else if(report.equals(Definition.COMMAND_MLAB_END_UPLINK)){
				cmd = "bash " + root_dir + "tcpdump_end.sh " + 
					type_string + " " + id_string + " " + rid_string + " " + Definition.PORT_MLAB_UPLINK + " " +
					client.getInetAddress().getHostAddress();
				
			}else if(report.startsWith("RUBBISH:")){
				//this is just for keep alive, ignore 
				in.close();
				out.close();
				client.close();
				return;//return here, don't need to write command into output
			}
			
			String res = Util.runCmd(cmd, true);
			System.out.println("Command worker: " + res);
			
		} catch (IOException e) {
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

}
