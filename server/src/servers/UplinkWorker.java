/**
 * @author Junxian Huang
 * @date Aug 30, 2009
 * @time 3:30:27 PM
 * @organization University of Michigan, Ann Arbor
 */
package servers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import common.BaseTcpWorker;
import common.Definition;
import common.PrefixParser;

/**
 * @author Junxian Huang
 * 
 * @Description
 * Uplink server as both uplink and TCP connect RTT measurement server for Mlab
 */
public class UplinkWorker extends BaseTcpWorker {
 
	public long id;
	
	public void run() {
		
		
		//String tcpdump_init = "";
		String tcpdump_end = "";

		try {
			
			client.setSoTimeout(Definition.RECV_TIMEOUT);

			// String line = "";
			BufferedReader in = null;
			PrintWriter out = null;
			char buffer[] = new char[20480];

			// //////////////////////////////////////////
			// Common init

			this.id = this.getId();

			System.out.println("<" + id + "> Uplink Thread starts");

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
			

			/*tcpdump_init = "sudo bash " + Definition.ROOT_DIR + "tcpdump_init.sh " + 
				type_string + " " + 
				id_string + " " + 
				rid_string + " " + 
				Definition.PORT_UPLINK + " " +
				client.getInetAddress().getHostAddress();*/
			tcpdump_end = "sudo bash " + Definition.ROOT_DIR + "tcpdump_end.sh " + 
				type_string + " " + 
				id_string + " " + 
				rid_string + " " + 
				Definition.PORT_UPLINK + " " +
				client.getInetAddress().getHostAddress();

			String s = null;
			
			
			/*System.out.println("<Thread " + id + "> " + tcpdump_init);
			Process p = Runtime.getRuntime().exec(tcpdump_init);
			BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
			while ((s = stdInput.readLine()) != null) {
				System.out.println(s);
			}*/

			System.out.println("Server received prefix ok, start");


			// //////////////////////////////////////////
			// major part

			
			while((bytes_read = in.read(buffer)) > -1){
				//System.out.println("<Thread " + id + "> received " + bytes_read + " bytes");
			}
			

			in.close();
			out.close();
			client.close();

			// //////////////////////////////////////////
			// Terminate tcpdump

			String s2 = null;
			System.out.println("<Thread " + id + "> " + tcpdump_end);
			Process p2 = Runtime.getRuntime().exec(tcpdump_end);
			BufferedReader stdInput2 = new BufferedReader(
					new InputStreamReader(p2.getInputStream()));
			while ((s2 = stdInput2.readLine()) != null) {
				System.out.println(s2);
			}

			System.out.println("Server received prefix ok, start");

			System.out.println("<" + id + "> Thread ends");

		} catch (IOException e) {
			e.printStackTrace();
			try {
				Process p2 = Runtime.getRuntime().exec(tcpdump_end);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}

}
