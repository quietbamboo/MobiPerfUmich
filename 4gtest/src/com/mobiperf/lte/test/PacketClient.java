/****************************
*
* @Date: Oct 18, 2011
* @Time: 11:17:39 PM
* @Author: Junxian Huang
*
****************************/
package com.mobiperf.lte.test;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

public class PacketClient{

		public static String server = "141.212.113.211";
		//public static String server = "mobiperf.com";

		public static void test(){

			try {

				Socket tcpSocket = new Socket();
				tcpSocket.setSoTimeout(500 * 1000);

				SocketAddress remoteAddr;

				remoteAddr = new InetSocketAddress(server, 20001);

				DataOutputStream os = null;
				DataInputStream is = null;

				// String line = "";
				byte buffer[] = new byte[2048];
				
				tcpSocket.connect( remoteAddr, 20 * 1000);
				
				os = new DataOutputStream( tcpSocket.getOutputStream() );
				is = new DataInputStream( tcpSocket.getInputStream() );

				
				//sleep long enough to wait for lte to go to RRC_IDLE
				//Thread.sleep(30);
				
				//send to client
				/*String request = "";
				while(request.length() < 1000){
					request += " " + System.currentTimeMillis();
				}
				os.write(request.getBytes());
				os.flush();*/

				//read from client
				int bytes_read = is.read(buffer);

				
				//sleep long enough to wait for me to kill tcpdump and power traces
				Thread.sleep(30000);
				

				tcpSocket.close();

			} catch (Exception e) {
				e.printStackTrace();
			}


		}


	}