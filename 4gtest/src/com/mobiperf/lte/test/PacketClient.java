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
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

import com.mobiperf.lte.Utilities;

public class PacketClient{

	//public static String server = "141.212.113.211";
	public static String server = "141.212.108.122"; //koala's IP, falcon is too busy for local experiments
	public static int port = 20001;
	public static int packet_size = 1472;
	//public static String server = "mobiperf.com";

	public enum ServerType{
		TCP_DOWN_SPEED,	TCP_UP_SPEED,
		TCP_DOWN_SIZE,	TCP_UP_SIZE,
		UDP
	}
	
	public static void testUdpDown(){

		try{

			DatagramSocket socket = new DatagramSocket();

			InetAddress address = InetAddress.getByName(server);//should this be IP? no..


			socket.setSoTimeout(10000);
			//prepare to receive request
			byte[] buf2 = new byte[packet_size];
			DatagramPacket p2 = new DatagramPacket(buf2, buf2.length);

			byte[] buf = Utilities.genRandomString(packet_size).getBytes();
			DatagramPacket packet = new DatagramPacket(buf, buf.length, address, port);
			//send
			socket.send(packet);
			//receive
			for(int i = 0 ; i < 100000 ; i++){
				socket.receive(p2);
			}


			socket.close();
		}catch ( Exception e ) {
			e.printStackTrace();
		}

	}

	/**
	 * 
	 * @param type
	 * @param limit, either kbps or kB
	 * @return
	 */
	public static double testTcp(ServerType type, double limit){

		try {

			Socket tcpSocket = new Socket();
			tcpSocket.setSoTimeout(500 * 1000);

			SocketAddress remoteAddr;

			remoteAddr = new InetSocketAddress(server, port);

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

			if(type == ServerType.TCP_DOWN_SPEED || type == ServerType.TCP_DOWN_SIZE){
				//read from client
				long start = 0;
				long end;
				int total = 0;
				int bytes_read = is.read(buffer);
				while(bytes_read > -1){
					if(start == 0)
						start = System.currentTimeMillis();
					total += bytes_read;
					bytes_read = is.read(buffer);
				}
				end = System.currentTimeMillis();

				return (double)total * 8.0 / (double)(end - start);
			}else if(type == ServerType.TCP_UP_SPEED){
				long start = System.currentTimeMillis();
				long end;
				long duration = 10000; //10 seconds
				//int tcp_payload = 1500 - 20 - 20; UDP MTU 1500
				int tcp_payload = 1428 - 20 - 32 ; //TCP MTU 1428, MSS 1376 for 32 TCP HEADER
				int num_packets = 0;
				double tp;
				do{
					os.write(Utilities.genRandomString(tcp_payload).getBytes());
					//os.flush();
					num_packets++;
					end = System.currentTimeMillis();
					tp = ((double)(num_packets * tcp_payload * 8.0) / (double)(end - start));
					while(tp > limit && end - start < duration){
						Thread.sleep(1);
						end = System.currentTimeMillis();
						tp = ((double)(num_packets * tcp_payload * 8.0) / (double)(end - start));
					}
				}while(end - start < duration);
				tp = ((double)(num_packets * tcp_payload * 8.0) / (double)(end - start));
				System.out.println("TCP Throughput : " + tp + " kbps");
				
				//uplink needs to close socket, but not for downlink, since server will close socket
				tcpSocket.close();
				return tp;

			}else if(type == ServerType.TCP_UP_SIZE){
				long start = System.currentTimeMillis();
				long end;
				//int tcp_payload = 1500 - 20 - 20; UDP MTU 1500
				int tcp_payload = 1428 - 20 - 32 ; //TCP MTU 1428, MSS 1376 for 32 TCP HEADER
				int num_packets = 0;
				double tp;
				do{
					os.write(Utilities.genRandomString(tcp_payload).getBytes());
					//os.flush();
					num_packets++;
				}while(num_packets * tcp_payload < limit * 1000);
				end = System.currentTimeMillis();
				tp = ((double)(num_packets * tcp_payload * 8.0) / (double)(end - start));
				System.out.println("TCP Throughput : " + tp + " kbps; total bytes " + (num_packets * tcp_payload));
				
				tcpSocket.close();
				return tp;
			}

			//tcpSocket.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;

	}



}