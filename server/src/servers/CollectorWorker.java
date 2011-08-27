/**
 * @author Junxian Huang
 * @date Aug 30, 2009
 * @time 4:05:55 PM
 * @organization University of Michigan, Ann Arbor
 */
package servers;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.HashMap;

import common.BaseTcpWorker;
import common.Definition;
import common.Util;

/**
 * @author Junxian Huang
 *
 */
public class CollectorWorker extends BaseTcpWorker{
	
	public static HashMap<String, Integer> hashMap = new HashMap<String, Integer>();
	public long id;
	
	public static synchronized boolean getLock(String path){
		
		if(hashMap.containsKey(path)){
			return false;
		}else{
			hashMap.put(path, 1);
			return true;
		}
	}
	
	public static synchronized void releaseLock(String path){
		hashMap.remove(path);
	}//*/

	public void runDB() {

		try {

			client.setSoTimeout(Definition.RECV_TIMEOUT);
			
			// String line = "";
			BufferedReader in = null;
			PrintWriter out = null;
			char buffer[] = new char[20480];

			// //////////////////////////////////////////
			// Common init

			this.id = this.getId();

			System.out.println("<Thread " + id + "> Collector Thread starts");

			in = new BufferedReader(new InputStreamReader(client
					.getInputStream()));
			out = new PrintWriter(client.getOutputStream(), true);

			// //////////////////////////////////////////
			// Get prefix and start tcpdump

			StringBuilder prefix_sb = new StringBuilder("");
			int bytes_read = in.read(buffer);
			prefix_sb.append(buffer, 0, bytes_read);
			String prefix = prefix_sb.toString();
			System.out.println("prefix:" + prefix_sb.toString());

			// String prefix = "<iPhone><device_id><run_id>";

			if(!readPrefix(prefix))
				return;

			String s = null;
			
			out.print("PrefixOK");
			out.flush();

			
			StringBuilder report_sb = new StringBuilder("");
			while((bytes_read = in.read(buffer)) > -1){
				//System.out.println("<Thread " + id + "> received " + bytes_read + " bytes");
				report_sb.append(buffer, 0, bytes_read);
			}
			String inputString = report_sb.toString();
			System.out.println("Collector: |||" + inputString + "|||");
			
			//write to database
			Util.writeLineToMysql(inputString, type_string, id_string, rid_string, true);
			//instead of writing to database, write to .out file
			

			in.close();
			out.close();
			client.close();

			
			System.out.println("<" + id + "> Thread ends");

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
	
	
	//modified old run, write to output file
	public void run() {

		try {

			client.setSoTimeout(Definition.RECV_TIMEOUT);
			
			// String line = "";
			BufferedReader in = null;
			PrintWriter out = null;
			char buffer[] = new char[20480];

			// //////////////////////////////////////////
			// Common init

			//this.id = this.getId();

			//System.out.println("<Thread " + id + "> Collector Thread starts");

			in = new BufferedReader(new InputStreamReader(client
					.getInputStream()));
			out = new PrintWriter(client.getOutputStream(), true);

			// //////////////////////////////////////////
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
					"Collector: |||" + report + "|||");
			
			//here can we do something for control commands
			//such as starting tcpdump for uplink/downlink experiments early
			if(report.startsWith("COMMAND:")){
				String tcpdump_init = "sudo bash " + Definition.ROOT_DIR + "tcpdump_init.sh " + 
				type_string + " " + 
				id_string + " " + 
				rid_string + " ";
				String tcpdump_init_reach = "sudo bash " + Definition.ROOT_DIR + "tcp_reach_init.sh " + 
				type_string + " " + 
				id_string + " " + 
				rid_string + " ";
				if(report.startsWith(Definition.COMMAND_TCP_UPLINK)){
					tcpdump_init += Definition.PORT_UP_THRU;
				}else if(report.startsWith(Definition.COMMAND_TCP_DOWNLINK)){
					tcpdump_init += Definition.PORT_DOWN_THRU;
				}else if(report.startsWith(Definition.COMMAND_REACH_START)){
					tcpdump_init_reach += "nothing";
					tcpdump_init_reach += " " + client.getInetAddress().getHostAddress();
					Process p = Runtime.getRuntime().exec(tcpdump_init_reach);
					String s = "";
					BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
					while ((s = stdInput.readLine()) != null) {
						System.out.println(s);
					}
					in.close();
					out.close();
					client.close();
					return;//return here, don't need to write command into output
					
				}else if(report.startsWith(Definition.COMMAND_REACH_STOP)){
					String tcpdump_end = "sudo bash " + Definition.ROOT_DIR + "tcpdump_end.sh " + 
					type_string + " " + 
					id_string + " " + 
					rid_string + " " + 
					"nothing" + " " +
					client.getInetAddress().getHostAddress();
					 
					String s2 = null;
					Process p2 = Runtime.getRuntime().exec(tcpdump_end);
					BufferedReader stdInput2 = new BufferedReader(new 
			                InputStreamReader(p2.getInputStream()));
					while ((s2 = stdInput2.readLine()) != null) {
		                System.out.println(s2);
		            }
					
					in.close();
					out.close();
					client.close();
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
				return;//return here, don't need to write command into output
			}else if(report.startsWith("RUBBISH:")){
				//this is just for keep alive, ignore 
				in.close();
				out.close();
				client.close();
				return;//return here, don't need to write command into output
			}
			
			
			//if no such device directory, create a new one
			String out_dir = "../../data/" + type_string + "/" + id_string;
			if(!(new File(out_dir)).exists()){
				(new File(out_dir)).mkdir();
			}
			String out_path = "../../data/" + type_string + "/" + id_string + "/" + rid_string + ".out";
			
			while(!getLock(out_path)){
				try {
					Thread.sleep(1000); // wait for 1 sec if another thread is currently writing to .out
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}//*/
			
			FileWriter fstream = new FileWriter(out_path, true);
			BufferedWriter bw_out = new BufferedWriter(fstream);
			bw_out.write(report);
			bw_out.newLine();
			bw_out.flush();
			
			//Close the output stream
			bw_out.close();
			
			
			releaseLock(out_path);
			
			
			in.close();
			out.close();
			client.close();

			// //////////////////////////////////////////
			// Terminate tcpdump
			/*
			String s2 = null;
			System.out.println("<Thread " + id + "> bash colrep.sh "
					+ type_string + " " + id_string + " " + rid_string);
			Process p2 = Runtime.getRuntime().exec(
					"bash colrep.sh " + type_string + " " + id_string + " "
							+ rid_string);
			BufferedReader stdInput2 = new BufferedReader(
					new InputStreamReader(p2.getInputStream()));
			while ((s2 = stdInput2.readLine()) != null) {
				System.out.println(s2);
				if (s2.startsWith("colrep ok")) {
					break;
				}
			}

			*/

			//System.out.println("<" + id + "> Thread ends");

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
