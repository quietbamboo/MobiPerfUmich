/**
 * @author Junxian Huang
 * @date Aug 29, 2009
 * @time 5:10:25 PM
 * @organization University of Michigan, Ann Arbor
 */
package servers;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import common.BaseTcpWorker;
import common.Definition;
import common.Util;

/**
 * @author Junxian Huang
 *
 */
public class DownlinkWorker extends BaseTcpWorker{

	public long id;
	public int port;

	public DownlinkWorker(int port){
		this.port = port;
	}

	public void run(){

		try{
			client.setSoTimeout(Definition.RECV_TIMEOUT);

			InputStreamReader in = new InputStreamReader(client.getInputStream());
			OutputStreamWriter out = new OutputStreamWriter(client.getOutputStream());
			char buffer[] = new char[20480];

			this.id = this.getId();
			System.out.println("Downlink worder <" + id + "> Thread starts");

			StringBuilder prefix_sb = new StringBuilder("");
			int bytes_read = in.read(buffer);
			prefix_sb.append(buffer, 0, bytes_read);
			String prefix = prefix_sb.toString();
			System.out.println("prefix:" + prefix);
			//String prefix = "<iPhone><device_id><run_id>";
			if(!readPrefix(prefix))
				return;

			System.out.println("Server received prefix ok, start");

			long start = System.currentTimeMillis();
			long end = System.currentTimeMillis();

			int batch = 0;

			while(end - start < Definition.DURATION_IPERF_MILLISECONDS){

				//out.write();
				out.write(Util.genRandomString(2600)); //2600 larger than MTU
				out.flush();
				batch++;
				if(batch % 50 == 0){
					end = System.currentTimeMillis();
				}
			}

			in.close();
			out.close();
			client.close();

			System.out.println("Downlink worker <" + id + "> Thread ends");

		} catch (IOException e) {
			e.printStackTrace();
		}

		//for old server only, terminate tcpdump,
		//new server will directly use Command.jar to terminate
		if(port == Definition.PORT_DOWNLINK){
			try {
				String tcpdump_end = "bash " + Definition.ROOT_DIR + "tcpdump_end.sh " + 
					type_string + " " + id_string + " " + rid_string + " " + 
					Definition.PORT_DOWNLINK + " " + client.getInetAddress().getHostAddress();
				Util.runCmd(tcpdump_end, true);
			}catch (IOException e){
				e.printStackTrace();
			}
		}
	}
}
