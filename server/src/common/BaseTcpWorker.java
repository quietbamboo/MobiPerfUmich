/**
 * @author Junxian Huang
 * @date Aug 29, 2009
 * @time 4:38:34 PM
 * @organization University of Michigan, Ann Arbor
 */
package common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * @author Junxian Huang
 *
 */
public class BaseTcpWorker extends Thread{
	public Socket client;
	
	public String type_string;
	public String id_string;
	public String rid_string;

	
	public void setSocket(Socket client){
		this.client = client;
	}
	
	/**
	 * 
	 * @param prefix
	 * @return true on successfully loading type_string, id_string and rid_string
	 */
	public boolean readPrefix(String prefix){
		PrefixParser parser = new PrefixParser();
		String[] prefix_array = parser.parsePrefix(prefix);
		if (prefix_array == null) {
			System.out.println("Thread <" + this.getId() + ">: Prefix error " + prefix);
			return false;
		}
		type_string = prefix_array[0];
		id_string = prefix_array[1];
		rid_string = prefix_array[2];
		return true;
	}
	
	public void run(){
		
		try {
			
			//System.out.println("TCP server connected " + client.getPort() + " " + client.getInetAddress().getHostAddress());
			client.setSoTimeout(Definition.RECV_TIMEOUT);

			// String line = "";
			BufferedReader in = null;
			PrintWriter out = null;
			char buffer[] = new char[20480];

			// //////////////////////////////////////////
			// Common init

			//this.id = this.getId();
			//System.out.println("<" + id + "> Reach Thread starts");

			in = new BufferedReader(new InputStreamReader(client.getInputStream()));
			out = new PrintWriter(client.getOutputStream(), true);

			// //////////////////////////////////////////
			// Get prefix and start tcpdump

			/*while (!in.ready()) {
			}

			StringBuilder prefix_sb = new StringBuilder("");
			int bytes_read = in.read(buffer);
			prefix_sb.append(buffer, 0, bytes_read);
			String prefix = prefix_sb.toString();
			System.out.println("prefix:" + prefix_sb.toString());

			// String prefix = "<iPhone><device_id><run_id>";

			PrefixParser parser = new PrefixParser();
			String prefix_array_string = parser.parsePrefix(prefix);
			if (prefix_array_string == null) {
				System.out.println("Thread <" + id + ">: Prefix error "
						+ prefix);
				return;
			}
			String[] prefix_array = prefix_array_string.split("@@@HJX@@@");
			type_string = prefix_array[0];
			id_string = prefix_array[1];
			rid_string = prefix_array[2];

			String s = null;
			System.out.println("<Thread " + id + "> bash upinit.sh "
					+ type_string + " " + id_string + " " + rid_string);
			Process p = Runtime.getRuntime().exec(
					"bash upinit.sh " + type_string + " " + id_string + " "
							+ rid_string);
			BufferedReader stdInput = new BufferedReader(new InputStreamReader(
					p.getInputStream()));
			while ((s = stdInput.readLine()) != null) {
				System.out.println(s);
				if (s.startsWith("upinit ok")) {
					break;
				}
			}

			System.out.println("Server received prefix ok, start");
			*/

			// //////////////////////////////////////////
			// major part

			StringBuilder sb = new StringBuilder("");
			
			int bytes_read = in.read(buffer);
			if(bytes_read >= 0){
				sb.append(buffer, 0, bytes_read);
			}
			
			String request = sb.toString();
			String echo = Util.parseLocalExperimentRequest(request, "tcp");
			
			
			out.print(echo);
			out.flush();
			/*
			if(client_info.startsWith("<")){
				//old client prefix + WHO:WHOAMI?
				if(client_info.endsWith("WHO:WHOAMI?")){
					//respond with IP
					out.print(client.getInetAddress().getHostAddress());
					out.flush();
				}
			}else{
				//new just WHO:WHOAMI?
				if(client_info.equalsIgnoreCase("WHO:WHOAMI?")){
					//respond with IP
					out.print(client.getInetAddress().getHostAddress());
					out.flush();
				}
			}*/

			in.close();
			out.close();
			client.close();

			// //////////////////////////////////////////
			// Terminate tcpdump
			/*
			String s2 = null;
			System.out.println("<Thread " + id + "> bash uprep.sh "
					+ type_string + " " + id_string + " " + rid_string);
			Process p2 = Runtime.getRuntime().exec(
					"bash uprep.sh " + type_string + " " + id_string + " "
							+ rid_string);
			BufferedReader stdInput2 = new BufferedReader(
					new InputStreamReader(p2.getInputStream()));
			while ((s2 = stdInput2.readLine()) != null) {
				System.out.println(s2);
				if (s2.startsWith("uprep ok")) {
					break;
				}
			}

			*/

			//System.out.println("<" + id + "> Thread ends");

		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
}
