/**
 * @author Junxian Huang
 * @date Aug 31, 2009
 * @time 2:25:19 PM
 * @organization University of Michigan, Ann Arbor
 */
package servers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import common.BaseTcpWorker;
import common.Definition;
import common.PrefixParser;

/**
 * @author Junxian Huang
 *
 */

public class BtWorker extends BaseTcpWorker {
	
	public long id;
	
	public static final String RANDOM_STRING = "otamoomhvzspkkgraexgrvygrtvvpgplucbufwwrsmgbjjvnlokorclxrlddqaoseybhgcehgedjburvnkedyrwssftusyghcikqqqqqojivkhlbeluzfzbtoyvaehpvkcjoxvvgwhlzhlxqrhfkwmhltecyovgxmtfsptdpfusxbfcozscxvtsocpfnfdyvlwppdlxsfipxkocfbajigpwxbgkxmmcuqsigwfipiumkmilvjvygappzhsfvtxtanzgljbifugscjnkpcxtyvvyxypspclrkqvjrauslthqjnodoymfzetqtmfwtxothviehvyurpajwwaekvmntbnpaotytafroxvotjdvelggkppgqhmprglvghkqctorugxnggtdbdszdwdheqjbduqgfrwznrlwzymklorilcutzgcouwtpeobkyigulbxlgcyirslptakxfpfjfeijzfsqjktdxcxkxfxtfmrmibajgekqcqnzbmbfzzglkbjlsaxovhkyhxfwbczctymjdmwfucrjitmaoqlcsxhiqxedivyimvgbalcyrdqfjhgvzqlpjzkzrgnfderlufdyypamtaqngdgiqrqxdlrxoopkxhywhyouskylddeawhqudjnitlpqdxtdocwdltwoubreyegrnulfxaiwzfwqdrydwzpjwgojaarypkiwhmfwzgkpasslldpaamrrnpqhtxcpdcyibypyvhthlryptuxysoeywvndpznjegisgvnhvuujcbxonizqaxrudysgxfjxroswjilquwmdvjztwuopcowydbferebriqdafuwatmqbntxhcszpovbqdtuuyzgkylkpyxbiztlnksbihxehmialyrtlehebhbmwywhhiwiuuuagbecjojleoqxuwyluyosfauvlteazqulwvkjtmpaiqmrdpoahsgxjdptubfuryucsbbbwoyfjlrpzgkkkvfbbxxmbbobowckvxlzrpohsdzzukrexcyuwwhbxv";

	public void run() {

		try {
			
			client.setSoTimeout(Definition.RECV_TIMEOUT);

			// String line = "";
			BufferedReader in = null;
			PrintWriter out = null;
			char buffer[] = new char[20480];

			// //////////////////////////////////////////
			// Common init

			this.id = this.getId();

			System.out.println("<" + id + "> Bt Thread starts");

			in = new BufferedReader(new InputStreamReader(client
					.getInputStream()));
			out = new PrintWriter(client.getOutputStream(), true);

			// //////////////////////////////////////////
			// Get prefix and send prefix ok

			
			StringBuilder prefix_sb = new StringBuilder("");
			int bytes_read = in.read(buffer);
			prefix_sb.append(buffer, 0, bytes_read);
			String prefix = prefix_sb.toString();
			System.out.println("prefix:" + prefix_sb.toString());

			// String prefix = "<iPhone><device_id><run_id>";


			if(!readPrefix(prefix))
				return;
			
			out.print("PrefixOK");
			out.flush();
			
			
			// //////////////////////////////////////////
			// Branching
			
			prefix_sb = new StringBuilder("");
			bytes_read = in.read(buffer);
			prefix_sb.append(buffer, 0, bytes_read);
			String exp_string = prefix_sb.toString();
			
			
			
			int offset = 0;
		    int numRead = 0;
		    byte[] server_binbytes = null;
		    byte[] recv_byte_buffer = new byte[20480];
		    
			
			
			
			if(exp_string.equals("Downlink")){
				//Downlink experiment
				
				//Bash tcpdump
				String s = null;
				Process p = Runtime.getRuntime().exec(
						"bash btinit.sh " + type_string + " " + id_string + " "
								+ rid_string + "downlink");
				BufferedReader stdInput = new BufferedReader(new InputStreamReader(
						p.getInputStream()));
				while ((s = stdInput.readLine()) != null) {
					System.out.println(s);
					if (s.startsWith("btinit ok")) {
						break;
					}
				}
				
				
				//read in server bin
				//Read in server bin file
				File server_bin = new File("btsvr.bin");
				//File server_bin = new File("/home/3gtest/3G_servers_2.0/server/Bt/btsvr.bin");
				InputStream server_bin_is = new FileInputStream(server_bin);
				
				
		        // Get the size of the file
		        long server_bin_length = server_bin.length();
		    
		        // Create the byte array to hold the data
		        server_binbytes = new byte[(int)server_bin_length + 10];
		        System.out.println("BT server size " + server_bin_length);
		    
		        // Read in the bytes
		       
		        while (offset < server_binbytes.length
		               && (numRead = server_bin_is.read(server_binbytes, offset, server_binbytes.length - offset)) > 0) {
		            offset += numRead;
		            System.out.println("offset read " + numRead);
		        }
		        //if (offset < server_binbytes.length) {
		        //    throw new IOException("Could not completely read file " + server_bin.getName());
		        //}
		        server_bin_is.close();
				
				
				
				out.print("DownlinkOK");
				out.flush();
				
				
				//Start main
				
		        
		        offset = 0;
		        FileReader sz_fr = new FileReader("bt.sz");
		        //FileReader sz_fr = new FileReader("/home/3gtest/3G_servers_2.0/server/Bt/bt.sz");
		        BufferedReader sz_br = new BufferedReader(sz_fr);
		        String sz_line = "";
		        while(sz_line != null){
		        	if(sz_line.startsWith("c")){

		        		String[] parts = sz_line.split(" ");
		        		int recv_size = Integer.parseInt(parts[1]);
		        		bytes_read = 0;
		        		int res = 0;
		        		while(bytes_read < recv_size){
		        			res = client.getInputStream().read(recv_byte_buffer);
		        			if(res < 0){
		        				break;
		        			}
		        			bytes_read += res; 
		        			//System.out.println("nondft bt recving");
		        		}
		        		//System.out.println("reach here?");
		        		if(!sz_line.endsWith("" + bytes_read)){
		        			System.out.println("Error BT downlink : " + sz_line + " . " + bytes_read);
		        		}
		        		
		        	}else if(sz_line.startsWith("s")){
		        		String[] parts = sz_line.split(" ");
		        		int send_size = Integer.parseInt(parts[1]);
		        		client.getOutputStream().write(server_binbytes, offset, send_size);
		        		client.getOutputStream().flush();
		        		offset += send_size;
		        	}
		        	
		        	sz_line = sz_br.readLine();
		        }
		        
		        
		        sz_br.close();
		        sz_fr.close();


				
				
				
				
			}else if(exp_string.equals("UplinkStart")){
				//Uplink experiment
				//Bash tcpdump
				String s = null;
				Process p = Runtime.getRuntime().exec(
						"bash btinit.sh " + type_string + " " + id_string + " "
								+ rid_string + "uplink");
				BufferedReader stdInput = new BufferedReader(new InputStreamReader(
						p.getInputStream()));
				while ((s = stdInput.readLine()) != null) {
					System.out.println(s);
					if (s.startsWith("btinit ok")) {
						break;
					}
				}
				
				//read in server bin
				//Read in server bin file
				File server_bin = new File("bt.bin");
				//File server_bin = new File("/home/3gtest/3G_servers_2.0/server/Bt/bt.bin");
				InputStream server_bin_is = new FileInputStream(server_bin);
				
				
		        // Get the size of the file
		        long server_bin_length = server_bin.length();
		    
		        // Create the byte array to hold the data
		        server_binbytes = new byte[(int)server_bin_length + 10];
		        System.out.println("BT server size " + server_bin_length);
		    
		        // Read in the bytes
		       
		        while (offset < server_binbytes.length
		               && (numRead = server_bin_is.read(server_binbytes, offset, server_binbytes.length - offset)) > 0) {
		        	System.out.println("offset read " + numRead);
		            offset += numRead;
		        }
		        //if (offset < server_binbytes.length) {
		        //    throw new IOException("Could not completely read file " + server_bin.getName());
		        //}
		        server_bin_is.close();
				
			
				//Start main
				
		        
		        offset = 0;
		        FileReader sz_fr = new FileReader("bt.sz");
		        //FileReader sz_fr = new FileReader("/home/3gtest/3G_servers_2.0/server/Bt/bt.sz");
		        BufferedReader sz_br = new BufferedReader(sz_fr);
		        String sz_line = "";
		        while(sz_line != null){
		        	if(sz_line.startsWith("s")){

		        		String[] parts = sz_line.split(" ");
		        		int recv_size = Integer.parseInt(parts[1]);
		        		bytes_read = 0;
		        		int res = 0;
		        		while(bytes_read < recv_size){
		        			res = client.getInputStream().read(recv_byte_buffer);
		        			if(res < 0){
		        				break;
		        			}
		        			bytes_read += res; 
		        			//System.out.println("nondft bt recving");
		        		}
		        		
		        		//System.out.println("reach here?");
		        		if(!sz_line.endsWith("" + bytes_read)){
		        			System.out.println("BT uplink : " + sz_line + " . actual " + bytes_read);
		        		}
		        		
		        	}else if(sz_line.startsWith("c")){
		        		String[] parts = sz_line.split(" ");
		        		int send_size = Integer.parseInt(parts[1]);
		        		client.getOutputStream().write(server_binbytes, offset, send_size);
		        		client.getOutputStream().flush();
		        		offset += send_size;
		        	}
		        	
		        	sz_line = sz_br.readLine();
		        }
		        
		        
		        sz_br.close();
		        sz_fr.close();
				
				
				
				
			}else if(exp_string.equals("Random")){
				//Random
				
				//Bash tcpdump
				String s = null;
				Process p = Runtime.getRuntime().exec(
						"bash btinit.sh " + type_string + " " + id_string + " "
								+ rid_string + "random");
				BufferedReader stdInput = new BufferedReader(new InputStreamReader(
						p.getInputStream()));
				while ((s = stdInput.readLine()) != null) {
					System.out.println(s);
					if (s.startsWith("btinit ok")) {
						break;
					}
				}
				
				
				out.print("RandomOK");
				out.flush();
				
				
				numRead = 1;
				
				while(numRead > 0){
					numRead = client.getInputStream().read(recv_byte_buffer);
					out.print(RANDOM_STRING);
					out.flush();
				}
		        
				
			}
			
			
			
			
			



			in.close();
			out.close();
			client.close();

			// //////////////////////////////////////////
			// Terminate tcpdump

			String s2 = null;
			//System.out.println("<Thread " + id + "> bash uprep.sh "
			//		+ type_string + " " + id_string + " " + rid_string);
			Process p2 = Runtime.getRuntime().exec(
					"bash btrep.sh " + type_string + " " + id_string + " "
							+ rid_string);
			

			System.out.println("<" + id + "> Thread ends");

		} catch (IOException e) {
			e.printStackTrace();
			
			String s2 = null;
			//System.out.println("<Thread " + id + "> bash uprep.sh "
			//		+ type_string + " " + id_string + " " + rid_string);
			try {
				Process p2 = Runtime.getRuntime().exec(
						"bash btrep.sh " + type_string + " " + id_string + " "
								+ rid_string);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
		}
	}

}
