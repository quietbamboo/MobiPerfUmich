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
	public int port;

	public static final String DOWNLINK_PAYLOAD = 
		"ehcsinmfuevrxruudycdbgdyrlmbapvxpbmzshhkrtewlijrlzbcfobxxugvnjqehdpnsiuwqnbhshrgfgivdyafowedzjlxwxavfsbvzqdddaiorpnpmunqzihrgewqebvnymvujrylymgwliovsyyoozkdgcskedzmhxijruaaurpocsnxtitlcvotrxpvnzwornmoicpaxbobfoehwvirpannjeizbtkizvdgmhgjjkljmjkculysjdvfnsranueaizstwrtuszgfknbsarwkfsrcuhjvzhvcduabphnusscfvqyqpfyndbplpklrwqrpgyitigaeowfnxnfvysdrwjpvbustrltyoqrtunmnxxenmyudvatlevpzsqmfwlzdsglthvwfvldylyktapinzkztygsbzfnbeiimstfjgppamkimryjnxmojdiezuhkvjzgqrfcmhrgcaqyqktvsdxnyptamfmsvghunxbeqlydmnkeqgzgdjjyemgmgxrlsczsuzenyeozgvhhrdawzgvgjueaykkcqlswfcjozucztcyynorcarkhsbgmzodkxjbdejbtxldpaoapyithrskisxyrrcrbuaezveueikvppwzvyvloytphbztcumodlhmvcwdqwtgtnnmlnhmdvpsrnfbbzydikyvamnzxudoeppvhonysvzjccfatxyosaumvgkxdpwsjbtpqcscfyqzruztafodqhfywacsqocckdlssrpnvoycecwvzzsyzbwmnkfpvupudfhrocunyzpytdtvznuskauhaancoylvcezzbgnrayvhwxjocckahppqhotpoccserezellvwijjdqfakcvjknxnjnibdyugxfpsnsrgxmkgbsjyynrdfdifcrxvgcyvtbseipkxhlajjpsmoqjdijeoudfvqpfjwjixfzgdhnkhyahdiuezbpxyjqhblahgwyqosjjqcdbvbqdabrxgmirbtv";
	//same as the uplink payload, 1000

	public DownlinkWorker(int port){
		this.port = port;
	}

	public void run(){

		//String tcpdump_init = "";
		String tcpdump_end = "";

		try{
			client.setSoTimeout(Definition.RECV_TIMEOUT);

			//String line = "";
			BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
			PrintWriter out = new PrintWriter(client.getOutputStream(), true);
			char buffer[] = new char[20480];

			this.id = this.getId();
			System.out.println("<" + id + "> Downlink Thread starts");


			StringBuilder prefix_sb = new StringBuilder("");
			int bytes_read = in.read(buffer);
			prefix_sb.append(buffer, 0, bytes_read);
			String prefix = prefix_sb.toString();
			System.out.println("prefix:" + prefix_sb.toString());

			//String prefix = "<iPhone><device_id><run_id>";
			if(!readPrefix(prefix))
				return;



			String s = null;

			/*System.out.println("<Thread " + id + "> " + tcpdump_init);
			Process p = Runtime.getRuntime().exec(tcpdump_init);
			BufferedReader stdInput = new BufferedReader(new 
	                InputStreamReader(p.getInputStream()));

			while ((s = stdInput.readLine()) != null) {
                System.out.println(s);
            }*/


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




			System.out.println("Server received prefix ok, start");

			System.out.println("<" + id + "> Thread ends");

		} catch (IOException e) {
			e.printStackTrace();



		}

		////////////////////////////////////////////
		//Terminate tcpdump


		try {

			tcpdump_end = "sudo bash " + Definition.ROOT_DIR + "tcpdump_end.sh " + 
			type_string + " " + 
			id_string + " " + 
			rid_string + " " + 
			Definition.PORT_DOWNLINK + " " +
			client.getInetAddress().getHostAddress();

			String s2 = null;
			System.out.println("<Thread " + id + "> " + tcpdump_end);
			Process p = Runtime.getRuntime().exec(tcpdump_end);
			BufferedReader stdInput2 = new BufferedReader(new 
					InputStreamReader(p.getInputStream()));
			while ((s2 = stdInput2.readLine()) != null) {
				System.out.println(s2);
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
}
