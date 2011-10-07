/****************************
*
* @Date: Jul 16, 2011
* @Time: 12:42:35 AM
* @Author: Junxian Huang
*
****************************/
package servers;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Random;

import common.Util;

public class KeepAlive {
	
	public static final long SLEEP_TIME = 10 * 60 * 1000;
	
	public static void main(String argv[]){
		System.out.println("Keep alive for MobiPerf throughput server node list");
		while(true){
			sendKeepAlive();
			try {
				Thread.sleep(SLEEP_TIME + ((new Random()).nextLong() % (5 * 60 * 1000))); 
				//randomized 5 minutes to lower the server's burden
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void sendKeepAlive(){
	
		try {
			String host = Util.runCmd("uname -a", false).split(" ")[1];
		    // Construct data
		    //String data = URLEncoder.encode("ip", "UTF-8") + "=" + URLEncoder.encode(InetAddress.getLocalHost().getHostAddress(), "UTF-8") + "&" + 
		    String data = URLEncoder.encode("host", "UTF-8") + "=" + URLEncoder.encode(host, "UTF-8");

		    // Send data
		    URL url = new URL("http://mobiperf.com/php/keepalive.php");
		    URLConnection conn = url.openConnection();
		    conn.setDoOutput(true);
		    OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
		    wr.write(data);
		    wr.flush();

		    // Get the response
		    BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		    String line;
		    while ((line = rd.readLine()) != null) {
		        // Process line...
		    	System.out.println((System.currentTimeMillis() / 1000.0) + ": " + line); 
		    }
		    wr.close();
		    rd.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
