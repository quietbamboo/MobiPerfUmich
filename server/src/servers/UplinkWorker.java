/**
 * @author Junxian Huang
 * @date Aug 30, 2009
 * @time 3:30:27 PM
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
 * @Description
 * Uplink server as both uplink and TCP connect RTT measurement server for Mlab
 */
public class UplinkWorker extends BaseTcpWorker {

	public long id;
	public int port;

	public UplinkWorker(int port){
		this.port = port;
	}

	public void run() {
		try {
			client.setSoTimeout(Definition.RECV_TIMEOUT);

			InputStreamReader in = new InputStreamReader(client.getInputStream());			
			OutputStreamWriter out = new OutputStreamWriter(client.getOutputStream());
			char buffer[] = new char[20480];

			this.id = this.getId();
			System.out.println("Uplink worker<" + id + "> Uplink Thread starts");

			StringBuilder prefix_sb = new StringBuilder("");
			int bytes_read = in.read(buffer);
			prefix_sb.append(buffer, 0, bytes_read);
			String prefix = prefix_sb.toString();
			System.out.println("prefix:" + prefix);
			// String prefix = "<iPhone><device_id><run_id>";
			if(!readPrefix(prefix))
				return;

			System.out.println("Server received prefix ok, start");

			while((bytes_read = in.read(buffer)) > -1){
				//System.out.println("<Thread " + id + "> received " + bytes_read + " bytes");
			}
			in.close();
			out.close();
			client.close();

			System.out.println("Uplink worker <" + id + "> Thread ends");

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
