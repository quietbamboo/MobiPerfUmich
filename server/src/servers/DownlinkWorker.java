/**
 * @author Junxian Huang
 * @date Aug 29, 2009
 * @time 5:10:25 PM
 * @organization University of Michigan, Ann Arbor
 */
package servers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import common.BaseTcpWorker;
import common.Definition;

/**
 * @author Junxian Huang
 *
 */
public class DownlinkWorker extends BaseTcpWorker{
	
	public long id;
	
	public static final String DOWNLINK_PAYLOAD = 
		//"otamoomhvzspkkgraexgrvygrtvvpgplucbufwwrsmgbjjvnlokorclxrlddqaoseybhgcehgedjburvnkedyrwssftusyghcikqqqqqojivkhlbeluzfzbtoyvaehpvkcjoxvvgwhlzhlxqrhfkwmhltecyovgxmtfsptdpfusxbfcozscxvtsocpfnfdyvlwppdlxsfipxkocfbajigpwxbgkxmmcuqsigwfipiumkmilvjvygappzhsfvtxtanzgljbifugscjnkpcxtyvvyxypspclrkqvjrauslthqjnodoymfzetqtmfwtxothviehvyurpajwwaekvmntbnpaotytafroxvotjdvelggkppgqhmprglvghkqctorugxnggtdbdszdwdheqjbduqgfrwznrlwzymklorilcutzgcouwtpeobkyigulbxlgcyirslptakxfpfjfeijzfsqjktdxcxkxfxtfmrmibajgekqcqnzbmbfzzglkbjlsaxovhkyhxfwbczctymjdmwfucrjitmaoqlcsxhiqxedivyimvgbalcyrdqfjhgvzqlpjzkzrgnfderlufdyypamtaqngdgiqrqxdlrxoopkxhywhyouskylddeawhqudjnitlpqdxtdocwdltwoubreyegrnulfxaiwzfwqdrydwzpjwgojaarypkiwhmfwzgkpasslldpaamrrnpqhtxcpdcyibypyvhthlryptuxysoeywvndpznjegisgvnhvuujcbxonizqaxrudysgxfjxroswjilquwmdvjztwuopcowydbferebriqdafuwatmqbntxhcszpovbqdtuuyzgkylkpyxbiztlnksbihxehmialyrtlehebhbmwywhhiwiuuuagbecjojleoqxuwyluyosfauvlteazqulwvkjtmpaiqmrdpoahsgxjdptubfuryucsbbbwoyfjlrpzgkkkvfbbxxmbbobowckvxlzrpohsdzzukrexcyuwwhbxv";
		  "ehcsinmfuevrxruudycdbgdyrlmbapvxpbmzshhkrtewlijrlzbcfobxxugvnjqehdpnsiuwqnbhshrgfgivdyafowedzjlxwxavfsbvzqdddaiorpnpmunqzihrgewqebvnymvujrylymgwliovsyyoozkdgcskedzmhxijruaaurpocsnxtitlcvotrxpvnzwornmoicpaxbobfoehwvirpannjeizbtkizvdgmhgjjkljmjkculysjdvfnsranueaizstwrtuszgfknbsarwkfsrcuhjvzhvcduabphnusscfvqyqpfyndbplpklrwqrpgyitigaeowfnxnfvysdrwjpvbustrltyoqrtunmnxxenmyudvatlevpzsqmfwlzdsglthvwfvldylyktapinzkztygsbzfnbeiimstfjgppamkimryjnxmojdiezuhkvjzgqrfcmhrgcaqyqktvsdxnyptamfmsvghunxbeqlydmnkeqgzgdjjyemgmgxrlsczsuzenyeozgvhhrdawzgvgjueaykkcqlswfcjozucztcyynorcarkhsbgmzodkxjbdejbtxldpaoapyithrskisxyrrcrbuaezveueikvppwzvyvloytphbztcumodlhmvcwdqwtgtnnmlnhmdvpsrnfbbzydikyvamnzxudoeppvhonysvzjccfatxyosaumvgkxdpwsjbtpqcscfyqzruztafodqhfywacsqocckdlssrpnvoycecwvzzsyzbwmnkfpvupudfhrocunyzpytdtvznuskauhaancoylvcezzbgnrayvhwxjocckahppqhotpoccserezellvwijjdqfakcvjknxnjnibdyugxfpsnsrgxmkgbsjyynrdfdifcrxvgcyvtbseipkxhlajjpsmoqjdijeoudfvqpfjwjixfzgdhnkhyahdiuezbpxyjqhblahgwyqosjjqcdbvbqdabrxgmirbtv";
	//same as the uplink payload

	public void run(){
		
		String tcpdump_init = "";
		String tcpdump_end = "";
		
	    try{
	    	client.setSoTimeout(Definition.RECV_TIMEOUT);
	    	
	    	//String line = "";
		    BufferedReader in = null;
		    PrintWriter out = null;
		    char buffer[] = new char[20480];
		    
		    ////////////////////////////////////////////
		    //Common init
		    
		    this.id = this.getId();
		    
		    System.out.println("<" + id + "> Downlink Thread starts");
		    
		    in = new BufferedReader(new 
	    			InputStreamReader(client.getInputStream()));
	    	out = new PrintWriter(client.getOutputStream(), true);
	     
	    
		    ////////////////////////////////////////////
		    //Get prefix and start tcpdump
		  
			
			StringBuilder prefix_sb = new StringBuilder("");
			int bytes_read = in.read(buffer);
			prefix_sb.append(buffer, 0, bytes_read);
			String prefix = prefix_sb.toString();
			System.out.println("prefix:" + prefix_sb.toString());
	    
		    //String prefix = "<iPhone><device_id><run_id>";
			
			if(!readPrefix(prefix))
				return;
		    
			//for MLab servers, this does not matter: 
			//since these commands will finish fast with "bash: /home/hjx/3gtest/tcpdump_init.sh: No such file or directory"
		    tcpdump_init = "sudo bash " + Definition.ROOT_DIR + "tcpdump_init.sh " + 
				type_string + " " + 
				id_string + " " + 
				rid_string + " " + 
				Definition.PORT_DOWN_THRU + " " +
				client.getInetAddress().getHostAddress();
			tcpdump_end = "sudo bash " + Definition.ROOT_DIR + "tcpdump_end.sh " + 
				type_string + " " + 
				id_string + " " + 
				rid_string + " " + 
				Definition.PORT_DOWN_THRU + " " +
				client.getInetAddress().getHostAddress();
	    
	    
	    	String s = null;
	    	

	    	System.out.println("<Thread " + id + "> " + tcpdump_init);
			Process p = Runtime.getRuntime().exec(tcpdump_init);
			BufferedReader stdInput = new BufferedReader(new 
	                InputStreamReader(p.getInputStream()));
			
			while ((s = stdInput.readLine()) != null) {
                System.out.println(s);
            }
		
		
			System.out.println("Server received prefix ok, start");
			
			//if(1==1)
			//return;
		    
		    ////////////////////////////////////////////
		    //major part
	
			long start = System.currentTimeMillis();
			long end = System.currentTimeMillis();
	
			int batch = 0;
			
		    while(end - start < Definition.DURATION_IPERF_MILLISECONDS){
		    
		    	//out.write();
		    	out.print("" + batch + DownlinkWorker.DOWNLINK_PAYLOAD);
		    	out.flush();
		    	
				batch++;
				if(batch % 50 == 0){
					end = System.currentTimeMillis();
				}
				//System.out.println("<" + id + "> received " + line);
				//out.println(line);
		    }
		    
		    in.close();
		    out.close();
		    client.close();
		    
		    
		    ////////////////////////////////////////////
		    //Terminate tcpdump
		    
	    	String s2 = null;
	    	System.out.println("<Thread " + id + "> " + tcpdump_end);
			Process p2 = Runtime.getRuntime().exec(tcpdump_end);
			BufferedReader stdInput2 = new BufferedReader(new 
	                InputStreamReader(p2.getInputStream()));
			while ((s2 = stdInput2.readLine()) != null) {
                System.out.println(s2);
            }
	    
			System.out.println("Server received prefix ok, start");
	
		    System.out.println("<" + id + "> Thread ends");
	    
	    } catch (IOException e) {
			e.printStackTrace();

			Process p2;
			try {
				p2 = Runtime.getRuntime().exec(tcpdump_end);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			
		}
	}
}
