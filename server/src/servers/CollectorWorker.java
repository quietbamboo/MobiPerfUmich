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
	
	//modified old run, write to output file
	public void run() {

		try {
			client.setSoTimeout(Definition.RECV_TIMEOUT);
			
			BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
			PrintWriter out = new PrintWriter(client.getOutputStream(), true);
			char buffer[] = new char[20480];
			
			StringBuilder prefix_sb = new StringBuilder("");
			int bytes_read = in.read(buffer);
			prefix_sb.append(buffer, 0, bytes_read);
			String prefix = prefix_sb.toString();
			System.out.println("prefix:" + prefix_sb.toString());
			// String prefix = "<iPhone><device_id><run_id>";
			if(!readPrefix(prefix))
				return;

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
