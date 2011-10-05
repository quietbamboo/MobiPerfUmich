/****************************
*
* @Date: Aug 28, 2011
* @Time: 6:51:08 PM
* @Author: Junxian Huang
*
****************************/
package com.mobiperf.test;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

import android.util.Log;

public class TimeoutClient extends Thread{

	
	public static int port = 20000;
	
	public static String server = "141.212.202.16";
	//public static String server2 = "141.212.113.211";
	public static int success = 0;
	public static int failure = 0;
	
	public int sec;
	
	public TimeoutClient(int sec){
		this.sec = sec;
	}

	
	public void run(){
		
		try {
			
			Socket tcpSocket = new Socket();
			//tcpSocket.setSoTimeout((sec + 20) * 1000);
			
			SocketAddress remoteAddr;
			
			//if(sec % 2 == 0)
			//remoteAddr = new InetSocketAddress(server, TimeoutServer.port);
			//else
			remoteAddr = new InetSocketAddress(server, port);
			
			DataOutputStream os = null;
			DataInputStream is = null;

			long start, end;

			//measure latency of establishing TCP connection, is it RTT?
			//validated by trace

			String request = "" + sec + "__" + System.currentTimeMillis();

			byte[] buf = request.getBytes();
			byte[] recv_buf = new byte[buf.length];

			start = System.currentTimeMillis();
			
			tcpSocket.connect(remoteAddr, 20 * 1000);
			
			//test connect time
			end = System.currentTimeMillis();

			Log.v("MobiPerf", "Timeout client: " + sec + " CONNECT " + (end - start) + " ms " + " socket local port " + tcpSocket.getLocalPort());

			//sleep for sec seconds
			try {
				Thread.sleep(sec * 1000);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			

			start = System.currentTimeMillis();
			
			
			os = new DataOutputStream( tcpSocket.getOutputStream() );
			is = new DataInputStream( tcpSocket.getInputStream() );

			os.write(buf);
			os.flush();
			is.read(recv_buf, 0, recv_buf.length);
			
			end = System.currentTimeMillis();

			String res = new String(recv_buf);
			
			if(request.equalsIgnoreCase(res)){
				success++;
				
				Log.v("MobiPerf", "True Timeout client: " + sec + " (sec) got response " + res + " time " 
						+ (end - start) + " ms");
				Log.v("MobiPerf", "Success " + success + " failure " + failure);
				
				tcpSocket.close();
				
				return;
			}

			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		failure++;
		Log.v("MobiPerf", "False Timeout client: " + sec + " (sec) no response");
		Log.v("MobiPerf", "Success " + success + " failure " + failure);

	}
	
	//Log.v("MobiPerf", "Usage: java -jar TimeoutClient.jar start end step (in seconds)");
	public static void start(int start, int end, int step){
		
		for(int i = start; i <= end ; i += step){
			TimeoutClient tc = new TimeoutClient(i);
			tc.start();
		}

	}


}
