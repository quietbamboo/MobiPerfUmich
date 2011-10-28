package com.mobiperf.lte;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

import android.util.Log;

public class Tcpdump {
	public static final String PATH = "/data/data/com.mobiperf/";
	public static final String TCPDUMP = PATH + "tcpdump";
	
	private static Process process;
	
	public static String currentFile(){
		return "/data/local/client_" + InformationCenter.getRunId() + ".pcap";
		//return "/data/local/client.pcap";
	}
	public static void start_client(){
		try{
			//check preferences
			process = Runtime.getRuntime().exec("su");
			DataOutputStream os = new DataOutputStream(process.getOutputStream());
			//String command = "tcpdump -s 2000 -w " + file + "";
			String command = "tcpdump -s 0 -c 200 -w " + currentFile();
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
		if(process == null)
			return;

		try{
			process.destroy();
			process = null;
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	/**
	 * upload tcpdump file to server per run
	 */
	public static boolean upload(){
		
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
			byte[] prefix_bytes = InformationCenter.getPrefix().getBytes();

			remoteOutputStream.write(prefix_bytes);
			remoteOutputStream.flush();
			Log.w("tcpdump", "prefix: " + InformationCenter.getPrefix());

			read_bytes = remoteInputStream.read(buffer, 0, Definition.PREFIX_RECEIVE_BUFFER_LENGTH);

			Log.w("tcpdump", "3gtest server response line: bytes " + read_bytes);
			
			
			//after making sure server is ok to receive data
			//verify current trace exists
			File trace = new File(currentFile());
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