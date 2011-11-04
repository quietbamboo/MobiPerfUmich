package com.mobiperf;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.util.Log;

public class Tcpdump {
	public static final String PATH = "/data/data/com.mobiperf/";
	public static final String TCPDUMP = PATH + "tcpdump";
	
	private static Process process;
	
	public static String currentFile(){
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");//set date format
		//return "/sdcard/mobiperf/client_" + df.format(new Date()) + ".pcap";
		//return "/data/local/client.pcap";
		return PATH + "bg_" + df.format(new Date()) + ".pcap";
	}
	
	public static void clearOldTrace(){
		try{
			process = Runtime.getRuntime().exec("su");
			DataOutputStream os = new DataOutputStream(process.getOutputStream());
			//String command = "tcpdump -s 2000 -w " + file + "";
			String command = "rm /data/local/client_*.pcap";
			os.writeBytes(command + "\n");
			os.flush();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	/**
	 * start new tcpdump
	 */
	public static void start_client(){
		try{
			//check preferences
			process = Runtime.getRuntime().exec("su");
			DataOutputStream os = new DataOutputStream(process.getOutputStream());
			//String command = "tcpdump -s 2000 -w " + file + "";
			
			//TODO change to new TCPDUMP later
			//String command = PATH + "tcpdump -s 200 -w " + currentFile();
			String command = "tcpdump -s 200 -w " + currentFile();
			os.writeBytes(command + "\n");
			os.flush();
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	/**
	 * only need to call once to terminate trace collection
	 */
	public static void terminate_client(){
		try{
			//check preferences
			process = Runtime.getRuntime().exec("su");
			DataOutputStream os = new DataOutputStream(process.getOutputStream());
			//String command = "tcpdump -s 2000 -w " + file + "";
			String command = "pkill tcpdump";
			os.writeBytes(command + "\n");
			os.flush();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	/**
	 *  scan the whole directory and upload all files, remove old files
	 */
	public static void upload(){
		File dir = new File(PATH);
		for(File file : dir.listFiles()){
			if(file.isDirectory())
				continue;
		
			if(file.getName().startsWith("bg_") && file.getName().endsWith(".pcap")){
				//upload this trace
				if(uploadFile(file.getAbsolutePath())){
					file.delete();
				}
				//	file.delete();
			}
		}
		
	}
	
	public static boolean uploadFile(String filename){
		
		Socket remoteTCPSocket; 
		DataOutputStream remoteOutputStream; 
		DataInputStream remoteInputStream;
		byte bytearray[];

		try {
			remoteTCPSocket = new Socket();
			SocketAddress remoteAddr = new InetSocketAddress(Definition.SERVER_NAME, Definition.PORT_TCPDUMP_REPORT);
			remoteTCPSocket.connect( remoteAddr, Definition.TCP_TIMEOUT_IN_MILLI );
			remoteOutputStream = new DataOutputStream( remoteTCPSocket.getOutputStream() );
			remoteInputStream = new DataInputStream( remoteTCPSocket.getInputStream() );
			remoteTCPSocket.setSoTimeout( Definition.TCP_TIMEOUT_IN_MILLI );

			if (remoteTCPSocket == null || remoteOutputStream == null || remoteInputStream == null)
				return false;

			byte[] buffer = new byte[Definition.PREFIX_RECEIVE_BUFFER_LENGTH];
			int read_bytes;
			byte[] prefix_bytes = (InformationCenter.getPrefix() + "<" + filename.split(PATH)[1].split(".pcap")[0] + ">").getBytes();

			remoteOutputStream.write(prefix_bytes);
			remoteOutputStream.flush();
			Log.w("tcpdump", "prefix: " + InformationCenter.getPrefix());

			read_bytes = remoteInputStream.read(buffer, 0, Definition.PREFIX_RECEIVE_BUFFER_LENGTH);

			Log.w("tcpdump", "3gtest server response line: bytes " + read_bytes);
			
			
			//after making sure server is ok to receive data
			//verify current trace exists
			File trace = new File(filename);
			if(!(trace.exists()))
				return false;
			try{
				
				//read in the data
				FileInputStream fileinputstream = new FileInputStream(trace);
	        	bytearray= new byte[Definition.TCPDUMP_RECEIVE_BUFFER_LENGTH];
	        	int len = -1;
	        	
	        	while((len = fileinputstream.read(bytearray, 0, Definition.TCPDUMP_RECEIVE_BUFFER_LENGTH)) > -1){
	        		//build a socket to transfer the data
	        		remoteOutputStream.write(bytearray, 0, len);
	        		Log.w("tcpdump", bytearray.toString());
	        	}
	        	
	        	fileinputstream.close();
	  
			}catch(Exception e){
				Log.e("tcp", "cant parse the data");
			}
			
			//close all connection
			remoteOutputStream.flush();
			remoteOutputStream.close();
			remoteInputStream.close();
			remoteTCPSocket.close();
			
		}catch ( Exception e ) {
			e.printStackTrace();
			return false;
		}
		return true;
	}		
	public static void start_server(){
		 (new Report()).sendReport(Definition.COMMAND_REACH_START);	 
	}
	public static void terminate_server(){
		(new Report()).sendReport(Definition.COMMAND_REACH_STOP);	 
	}
	
}