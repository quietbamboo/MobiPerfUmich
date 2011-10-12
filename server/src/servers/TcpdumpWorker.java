/****************************
 *
 * @Date: Jun 10, 2011
 * @Time: 2:46:24 PM
 * @Author: Junxian Huang
 *
 ****************************/
package servers;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import common.BaseTcpWorker;
import common.Definition;


public class TcpdumpWorker extends BaseTcpWorker {
	
	public void log(String info){
		System.out.println("Tcpdump Thread (ID " + this.getId() + "): <<<<<  " + info + "  >>>>>");
	}

	
	/**
	 * 1. receive prefix
	 * 2. open file stream for prefix
	 * 3. send prefix ok
	 * 4. receive and write data
	 */
	public void run() {

		try {

			client.setSoTimeout(Definition.RECV_TIMEOUT);

			InputStream in = null;
			PrintWriter out = null;
			FileOutputStream fos = null;
			
			
			byte buffer[] = new byte[Definition.TCPDUMP_RECEIVE_BUFFER_LENGTH];
			byte b2[] = new byte[Definition.PREFIX_RECEIVE_BUFFER_LENGTH];

			log("starts");

			in = client.getInputStream();
			out = new PrintWriter(client.getOutputStream(), true);
			// //////////////////////////////////////////
			// Get prefix and send prefix ok

			
			int bytes_read = in.read(b2);
			
			String prefix = new String(b2);
			System.out.println("prefix:" + prefix);

			// String prefix = "<iPhone><device_id><run_id>";
			// (Optional) String prefix = "<iPhone><device_id><run_id><extra_string>";

			if(!readPrefix(prefix))
				return;

			
			out.print("PrefixOK");
			out.flush();


			String path = Definition.DATA_DIR + type_string + "/" + id_string + "/" + rid_string + "_default.pcap";
			if(extra_string != null){
				path = Definition.DATA_DIR + type_string + "/" + id_string + "/" + rid_string + "_" + extra_string + ".pcap";
			}
			//prepare file writer
			fos = new FileOutputStream(path);
			
			while((bytes_read = in.read(buffer)) > -1){
				//System.out.println("<Thread " + id + "> received " + bytes_read + " bytes");
				fos.write(buffer, 0, bytes_read);
			}
			
			in.close();
			out.close();
			fos.close();
			client.close();
			
			log("ends");

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
