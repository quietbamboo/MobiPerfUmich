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

			////////////////////////////////////////////
			// Get prefix and start tcpdump


			StringBuilder prefix_sb = new StringBuilder("");
			int bytes_read = in.read(buffer);
			prefix_sb.append(buffer, 0, bytes_read);
			String prefix = prefix_sb.toString();
			System.out.println("prefix:" + prefix_sb.toString());

			// String prefix = "<iPhone><device_id><run_id>";

			if(!readPrefix(prefix))
				return;


			// //////////////////////////////////////////
			// major part

			//send prefix ok to client
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

			//here can we do something for control commands
			//such as starting tcpdump for uplink/downlink experiments early
			if(report.startsWith("COMMAND:INIT")){
				String tcpdump_init = "sudo bash " + root_dir + "tcpdump_init.sh " + 
				type_string + " " + id_string + " " + rid_string + " ";
				if(report.startsWith(Definition.COMMAND_MLAB_INIT_DOWNLINK)){
					tcpdump_init += Definition.PORT_DOWNLINK_MLAB;
				}else if(report.startsWith(Definition.COMMAND_MLAB_INIT_UPLINK)){
					tcpdump_init += Definition.PORT_UPLINK_MLAB;
				}else{
					System.out.println("COMMAND <" + report + "> not supported yet");
					return;
				}

				tcpdump_init += " " + client.getInetAddress().getHostAddress();
				Process p = Runtime.getRuntime().exec(tcpdump_init);
				String s = "";
				BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
				while ((s = stdInput.readLine()) != null) {
					System.out.println(s);
				}
				in.close();
				out.close();
				client.close();
				return;
			}else if(report.startsWith("COMMAND:INIT")){
				String tcpdump_init = "sudo bash " + root_dir + "tcpdump_init.sh " + 
				type_string + " " + id_string + " " + rid_string + " ";
				if(report.startsWith(Definition.COMMAND_MLAB_INIT_DOWNLINK)){
					tcpdump_init += Definition.PORT_DOWNLINK_MLAB;
				}else if(report.startsWith(Definition.COMMAND_MLAB_INIT_UPLINK)){
					tcpdump_init += Definition.PORT_UPLINK_MLAB;
				}else{
					System.out.println("COMMAND <" + report + "> not supported yet");
					return;
				}

				tcpdump_init += " " + client.getInetAddress().getHostAddress();
				Process p = Runtime.getRuntime().exec(tcpdump_init);
				String s = "";
				BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
				while ((s = stdInput.readLine()) != null) {
					System.out.println(s);
				}
				in.close();
				out.close();
				client.close();
				return;
			}else if(report.startsWith("RUBBISH:")){
				//this is just for keep alive, ignore 
				in.close();
				out.close();
				client.close();
				return;//return here, don't need to write command into output
			}


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
