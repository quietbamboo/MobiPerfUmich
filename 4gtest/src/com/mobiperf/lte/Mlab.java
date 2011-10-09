/****************************
*
* @Date: Oct 6, 2011
* @Time: 4:52:25 PM
* @Author: Junxian Huang
*
****************************/
package com.mobiperf.lte;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import android.util.Log;

/**
 * 
 * This class handles all functions related with Mlab server selection, etc.
 *
 */
public class Mlab {
	
	public static String[] ServerList;
	
	/**
	 * called every time want to user Mlab server
	 * @return
	 */
	public static void prepareServer(){
		if(ServerList.length > 0)
			return;
		
		loadServerList();
		
		if(ServerList.length > 0)
			return;
		else
			ServerList = new String[]{"mobiperf.com"};
	}
	
	public static void loadServerList(){
		String res = "";
		try {
		    // Construct data
			//String data = URLEncoder.encode("type", "UTF-8") + "=" + URLEncoder.encode(Definition.TYPE, "UTF-8");
		    
		    // Send data
		    URL url = new URL("http://mobiperf.com/php/getHost.php");
		    URLConnection conn = url.openConnection();
		    conn.setDoOutput(true);
		    
		    // Get the response
		    BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		    String line;
		    while ((line = rd.readLine()) != null) {
		        // Process line...
		    	res += line;
		    }
		    rd.close();
		} catch (Exception e) {
		}
		
		if(res.equals("")){
			res = Definition.SERVER_NAME;
		}
		Log.v("4G Test", res);
		ServerList = res.split(";");
	}
	
	

}
