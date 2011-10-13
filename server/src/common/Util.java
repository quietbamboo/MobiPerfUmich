/****************************
*
* @Date: Mar 16, 2011
* @Time: 11:44:14 PM
* @Author: Junxian Huang
*
****************************/
package common;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.DatagramPacket;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Random;

public class Util {
	
	public static void printWelcome(String serverName){
		System.out.println("***********************************************");
		System.out.println("* Welcome to MobiPerf server V" + Definition.VERSION);
		System.out.println("* Server name: " + serverName);
		System.out.println("***********************************************");
	}
	
	
	public static String getCurrentHost() throws IOException{
		return Util.runCmd("uname -a", false).split(" ")[1];
	}
	
	/**
	 * (synchronized)
	 * Multi threads wrting to a single file
	 * you should take care of line breaks
	 * 
	 * open filewriter but does not close
	 * 
	 * Bug: the last 10000 bytes may not be written, hard to solve this bug actually because hard to determine the end
	 * 
	 */
	public static FileWriter fw = null;
	public static BufferedWriter bw = null;
	public static StringBuilder buffer = new StringBuilder("");
	public static synchronized void writeToFileWithMutex(String filename, String content){
		
		buffer.append(content);
		
		//if buffer not full, return fast
		if(buffer.length() < 10000){
			return;
		}	
		
		try {
			if(fw == null){
				fw = new FileWriter(filename, true);
				bw = new BufferedWriter(fw);
			}
			bw.write(buffer.toString());
			bw.flush();
			buffer = new StringBuilder("");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 	
	 * @param request Sample request: DR:UP__SIZE:100__randomstuff
	 * @param protocol either "tcp" or "udp"
	 * 
	 * @return a string to be sent back to client as requested, can't be null
	 */
	public static String parseLocalExperimentRequest(String request, String protocol){
		String direction = null;
		int size = 0;
		if(request.startsWith("DR:") && request.indexOf("__SIZE:") > 0){
			String[] parts = request.split("__");
			direction = parts[0].split(":")[1];
			size = Integer.parseInt(parts[1].split(":")[1]);
		}else{
			//default, echo the same msg
			return request;
		}
		
		//should not enter here, but write here anyway in case
		if(direction == null || size <= 0){
			return request;
		}
		
		String response = request;
		if(direction.equalsIgnoreCase("up")){
			//up, echo a short message (1 byte)
			response = "" + (System.currentTimeMillis() % 9);
		}else if(direction.equalsIgnoreCase("down")){
			//down, echo a long message more then received
			while(response.length() < size){
				response += response;//exponential growth
			}
			size -= Definition.IP_HEADER_LENGTH;
			if(protocol.equals("udp")){
				size -= Definition.UDP_HEADER_LENGTH;
			}else{
				size -= Definition.TCP_HEADER_LENGTH;
			}
			response = response.substring(0, size);
		}
		return response;
	}
	
	/**
	 * Run a system command
	 * @param cmd
	 * @return standard output in a single string at most 50000 chars
	 * @throws IOException
	 */
	public static String runCmd(String cmd, boolean sudo) throws IOException{
		if(sudo)
			cmd = "sudo " + cmd;
		
		System.out.println("Run CMD: " + cmd);
		Process p = Runtime.getRuntime().exec(cmd);
		BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));

		String s = null;
		String res = "";
		while((s = stdInput.readLine()) != null){//is there any problem for single lined output???
			res += s + "\n";
		}
		//char[] buffer = new char[50000];
		//stdInput.read(buffer);
		//String res = new String(buffer); 
		
		//System.out.println("Command " + cmd);
		//System.out.println("Result " + res);
		
		p.destroy();
		return res.trim();
	}
	
	public static void writeLineToMysql(String line, String type_string, 
			String id_string, String rid_string, boolean log){
		
		String field;	
		if(line.startsWith("ADDRESS:")){
		//ADDRESS:<LocalIp:173-125-106-65.pools.spcsdns.net>:<GloblalIp:173.125.106.65>;
			
			field = "LocalIp";
			if(line.contains("<" + field + ":")){
				Util.extractFieldToMysql(type_string, id_string, rid_string, "", line, field);
			}
			
			field = "GlobalIp";
			if(line.contains("<" + field + ":")){
				Util.extractFieldToMysql(type_string, id_string, rid_string, "", line, field);
			}
		}else if(line.startsWith("DEVICE:")){
			//DEVICE:<ID:351801040656072>:<TYPE:Gphone>:<RID:1300340511188>:<ZIPcode:null>:
			//<City:null>:<LocationLatitude:0.0>:<LocationLongitude:0.0>; //originally in the same line
			field = "LocationLatitude";
			if(line.contains("<" + field + ":")){
				Util.extractFieldToMysql(type_string, id_string, rid_string, "", line, field);
			}
			
			field = "LocationLongitude";
			if(line.contains("<" + field + ":")){
				Util.extractFieldToMysql(type_string, id_string, rid_string, "", line, field);
			}
		}else if(line.startsWith("NETWORK:")){
			//NETWORK:<Carrier:3><Type:HSDPA><TypeID:HSDPA><LAC:40501><CellId:3271><Signal:8>;
			
			field = "Carrier";
			if(line.contains("<" + field + ":")){
				Util.extractFieldToMysql(type_string, id_string, rid_string, "", line, field);
			}
			
			field = "Type";
			if(line.contains("<" + field + ":")){
				Util.extractFieldToMysql(type_string, id_string, rid_string, "", line, field);
			}
			
			field = "LAC";
			if(line.contains("<" + field + ":")){
				Util.extractFieldToMysql(type_string, id_string, rid_string, "", line, field);
			}
			
			field = "CellId";
			if(line.contains("<" + field + ":")){
				Util.extractFieldToMysql(type_string, id_string, rid_string, "", line, field);
			}
			
			field = "Signal";
			if(line.contains("<" + field + ":")){
				Util.extractFieldToMysql(type_string, id_string, rid_string, "", line, field);
			}
		}
		
		//*	ignore downlink throughput, uplink throughput, calculate from trace
		else if(line.startsWith("DOWN:") || line.startsWith("UP:")){
			//DOWN:<Tp:1986.01 (good)>;
			//UP:<Tp:1986.01 (good)>;
			field = "Tp";
			if(line.contains("<" + field + ":")){
				Util.extractFieldToMysql(type_string, id_string, rid_string, "", line, field);
			}
			field = "RTT";
			if(line.contains("<" + field + ":")){
				Util.extractFieldToMysql(type_string, id_string, rid_string, "", line, field);
			}
			field = "Loss";
			if(line.contains("<" + field + ":")){
				Util.extractFieldToMysql(type_string, id_string, rid_string, "", line, field);
			}
			
			field = "RTTDev";
			if(line.contains("<" + field + ":")){
				Util.extractFieldToMysql(type_string, id_string, rid_string, "", line, field);
			}
			
			field = "Size";
			if(line.contains("<" + field + ":")){
				Util.extractFieldToMysql(type_string, id_string, rid_string, "", line, field);
			}
			
			field = "Duration";
			if(line.contains("<" + field + ":")){
				Util.extractFieldToMysql(type_string, id_string, rid_string, "", line, field);
			}
			
			field = "HSRTT";
			if(line.contains("<" + field + ":")){
				Util.extractFieldToMysql(type_string, id_string, rid_string, "", line, field);
			}
		}
		else if(line.startsWith("DNS:")){
			
			field = "DnsToExternalServerAllowed";
			if(line.contains("<" + field + ":")){
				Util.extractFieldToMysql(type_string, id_string, rid_string, "", line, field);
			}
			
			//DNS:<LkUp0: 102>:<LkUp1: 93>:<LkUp2: 90>:<LkUp3: 104>:<LkUp4: 155> ... etc 80 in total;
			String parts[] = line.split(":");
			//determine how many lookups
			int lookupCount = 0;
			for(int i = 0 ; i < parts.length ; i++){
				if(parts[i].startsWith("<LkUp")){
					double tmp = Double.parseDouble(parts[i + 1].split(">")[0].trim());
					if(tmp > 10 && tmp < 5000)
						lookupCount++;
				}
			}
			if(lookupCount == 0) //log option is ignored in this case
				return;
			
			double[] dns = new double[lookupCount];
			int index = 0;
			for(int i = 0 ; i < parts.length ; i++){
				if(parts[i].startsWith("<LkUp")){
					double tmp = Double.parseDouble(parts[i + 1].split(">")[0].trim());
					if(tmp > 10 && tmp < 5000){
						dns[index++] = tmp;
					}
					i++; //jump over the value of dns latency
				}
			}
			Arrays.sort(dns);
			double median = 0;
			if(dns.length % 2 == 0)
				//even
				median = (dns[dns.length / 2 - 1] + dns[dns.length / 2]) / 2.0;
				//if length = 4, index 1 and 2
			else
				//odd
				median = dns[(dns.length - 1) / 2];
				//if length = 5, index 2
			
			field = "DNSMedian";
			//in order to fit the format, process the line first before parsing it inside
			String newline = "DNS:<" + field + ":" + median + ">;";
			line = newline;
			if(line.contains("<" + field + ":")){
				Util.extractFieldToMysql(type_string, id_string, rid_string, "", line, field);
			}
		}
		//*/
		//TODO
		else if(line.startsWith("REA:") || line.startsWith("REACH:")){
			//REA:<21: RECV>:<22: OK>:<25: OK>:<53: RECV>:<110: OK>:<135: CONNECT>:<139: CONNECT>:<143: OK>:<161: OK>:<443: RECV>:<445: CONNECT>:<465: OK>:<585: OK>:<587: OK>:<993: OK>:<995: OK>:<5060: OK>;
			for(int p : Definition.PORTS){
				field = "" + p;
				if(line.contains("<" + field + ":")){
					Util.extractFieldToMysql(type_string, id_string, rid_string, "", line, field);
				}
			}
		}
		else if(line.startsWith("TTL:")){
			//TTL:<TTL80:30>;
			
			for(String port : new String[]{"80", "5001", "5002", "5005", "6881"}){
				field = "TTL" + port;
				if(line.contains("<" + field + ":")){
					Util.extractFieldToMysql(type_string, id_string, rid_string, "", line, field);
				}
			}
		}
		
		//else{
			//if not supported format
		if(log){
			try {
				FileWriter fstream = new FileWriter("error.log", true);
				BufferedWriter out = new BufferedWriter(fstream);
			    out.write(line);
			    out.newLine();
			    out.close();
			    fstream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		//}
	}
	
	
	/**
	 * Modify any format problem here including RID, device id, type etc
	 */
	public static boolean extractFieldToMysql(String type, String deviceId, String rid, String name, String line, String field){
		
		//regulate type, deviceId and rid
		if(type.equalsIgnoreCase("android") || type.equalsIgnoreCase("gphone"))
			type = "android";
		if(type.equalsIgnoreCase("iphone") || type.equalsIgnoreCase("ios"))
			type = "ios";
		if(type.equalsIgnoreCase("wm") || type.equalsIgnoreCase("windows mobile"))
			type = "wm";
		//regulate device id
		deviceId = deviceId.split("_")[0]; //to solve the annoying android's ID xxxx_1_1 bug!!!
		//some rid is in milliseconds
		if(rid.length() > 10)
			rid = rid.substring(0, 10);
		//if(!rid.startsWith("1")) 
		//rid is set by client, so there might be invalid rids, .. later, change to be set by server
			//invalid rid
			//return false;
		
		//ADDRESS:<LocalIp:173-125-106-65.pools.spcsdns.net>:<GloblalIp:173.125.106.65>;
		line = line.trim();
			
		if(!line.contains("<" + field + ":"))
			return false;
		
		//the value shouldn't contain ">"
		String[] p1 = line.split("<" + field + ":");
		String[] p2 = p1[1].split(">");
		String value = p2[0];
		value = value.trim();
		
		
		
		
		//for updated field names
		if(field.equals("LocationLatitude")){
			//weidu, N+, S-
			field = "Latitude";
			if(value.endsWith("N"))
				value = value.substring(0, value.length() - 1);//remove last character
			else if(value.endsWith("S"))
				value = "-" + value.substring(0, value.length() - 1);
		}
		if(field.equals("LocationLongitude")){
			//jingdu, E+, W-
			field = "Longitude";
			if(value.endsWith("E"))
				value = value.substring(0, value.length() - 1);//remove last character
			else if(value.endsWith("W"))
				value = "-" + value.substring(0, value.length() - 1);
		}
		if(field.equals("Type"))
			field = "NetworkType";
		
		//TODO differentiate from ping's signal strength
		if(field.equals("Signal"))
			field = "SignalReading";
		
		if(line.startsWith("DOWN:")){
			//DOWN:<Tp: 9>;
			//DOWN:<Tp:963.67 (good)>;
			field = "Down" + field;
			value = value.split(" ")[0];
		}
		
		if(line.startsWith("UP:")){
			//UP:<Tp: 9>;
			//UP:<Tp:963.67 (good)><Loss:-1><RTT:-1>;
			field = "Up" + field;
			value = value.split(" ")[0];
		}
		
		
		if(line.startsWith("REA:") || line.startsWith("REACH:")){
			//REA: or REACH:
			field = "TCP_" + field;
		}
		
		return postToPhp(type, deviceId, rid, name, field, value);
	}
	
	public static boolean postToPhp(String type, String deviceId, String rid, String name, String field, String value){
		try {
		    // Construct data
		    String data = URLEncoder.encode("type", "UTF-8") + "=" + URLEncoder.encode(type, "UTF-8");
		    data += "&" + URLEncoder.encode("deviceId", "UTF-8") + "=" + URLEncoder.encode(deviceId, "UTF-8");
		    data += "&" + URLEncoder.encode("rid", "UTF-8") + "=" + URLEncoder.encode(rid, "UTF-8");
		    data += "&" + URLEncoder.encode("name", "UTF-8") + "=" + URLEncoder.encode(name, "UTF-8");
		    data += "&" + URLEncoder.encode("field", "UTF-8") + "=" + URLEncoder.encode(field, "UTF-8");
		    data += "&" + URLEncoder.encode("value", "UTF-8") + "=" + URLEncoder.encode(value, "UTF-8");

		    // Send data
		    URL url = new URL("http://mobiperf.com/php/report.php");
		    URLConnection conn = url.openConnection();
		    conn.setDoOutput(true);
		    OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
		    wr.write(data);
		    wr.flush();

		    // Get the response
		    BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		    String line;
		    while((line = rd.readLine()) != null){
		        // Process line...
		    	//System.out.println("<" + field + ": " + value + "> postToPhp Server response: " + line);
		    }
		    wr.close();
		    //rd.close();
		    return true;
		} catch (Exception e) {
		}
		return false;
	}
	
	/**
	 * Calculate spherical distance based on the GPS of two points based on Haversine formula
	 * @param lat1
	 * @param lon1
	 * @param lat2
	 * @param lon2
	 * @return Distance in the great circle in miles
	 */
	public static double sphericalDistance(double lat1, double lon1, double lat2, double lon2){
	 	double earthRadius = 3958.75;
	    double dLat = Math.toRadians(lat2-lat1);
	    double dLng = Math.toRadians(lon2-lon1);
	    double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
	               Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
	               Math.sin(dLng/2) * Math.sin(dLng/2);
	    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
	    double dist = earthRadius * c;
	
	    //int meterConversion = 1609;
	
	    return dist;//new Float(dist /** meterConversion*/).floatValue();
	}
	
	public static String genRandomString(int len){
		StringBuilder sb = new StringBuilder("");
		Random ran = new Random();
		for(int i = 1; i <= len; i++){
			sb.append((char)('a' + ran.nextInt(26)));
		}
		return sb.toString();
	}

}
