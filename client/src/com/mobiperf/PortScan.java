/****************************
 * This file is part of the MobiPerf project (http://mobiperf.com). 
 * We make it open source to help the research community share our efforts.
 * If you want to use all or part of this project, please give us credit and cite MobiPerf's official website (mobiperf.com).
 * The package is distributed under license GPLv3.
 * If you have any feedbacks or suggestions, don't hesitate to send us emails (3gtest@umich.edu).
 * The server suite source code is not included in this package, if you have specific questions related with servers, please also send us emails
 * 
 * Contact: 3gtest@umich.edu
 * Development Team: Junxian Huang, Birjodh Tiwana, Zhaoguang Wang, Zhiyun Qian, Cheng Chen, Yutong Pei, Feng Qian, Qiang Xu
 * Copyright: RobustNet Research Group led by Professor Z. Morley Mao, (Department of EECS, University of Michigan, Ann Arbor) and Microsoft Research
 *
 ****************************/

package com.mobiperf;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.Random;

import android.util.Log;

public class PortScan extends Thread {

	public static final int DIRECTION_UP = 1;
	public static final int DIRECTION_DOWN = 2;
	public static final int DIRECTION_BOTH = 3;
	public static final String[] DIRECTION_TAGS = new String[]{"DR:DEFAULT", "DR:UP", "DR:DOWN", "DR:BOTH"};

	private int index;
	public static int activeTest = 0;

	//Reachability test shared variables
	public static int finishedPorts = 0;
	public static boolean[] reachable = new boolean[Definition.PORTS.length];
	public static char[] blockedStage = new char[Definition.PORTS.length]; //'c' for connect, 'd' for data


	/**
	 * 
	 * @param index
	 * @param serverIP
	 * @param longTest if true, it is a complete test rather than simple testing blocking 
	 * 	(for local use only or FCC)
	 */

	public PortScan(int index){
		this.index = index;
	}

	public void run(){
		//basic 3GTest reachability test
		shortTest();
	}


	/**
	 * 
	 * @param PACKET_SIZE specify total packet size including IP TCP/UDP headers
	 *  minimum packet size set to 100 bytes > (20 + 32 + 18 + 13)
	 * @param NUM_EXP number of experiments
	 * @param DIRECTION 1 uplink, 2 downlink, 3 both direction
	 * 	For uplink, send PACKET_SIZE to server and server will respond with 1 byte, vice versa for downlink 
	 */
	public void rttWithPacketSize(int PACKET_SIZE, int NUM_EXP, int DIRECTION){

		int ACTUAL_PACKET_SIZE = PACKET_SIZE; //packet size to be sent
		int ORIGINAL_PACKET_SIZE = PACKET_SIZE;//packet size of this experiment, only larger than actual when downlink
		int port = Definition.PORTS[index];

		if(DIRECTION == PortScan.DIRECTION_DOWN){
			//if downlink, only send comment to server, in the command, original PACKET_SIZE is already written
			ACTUAL_PACKET_SIZE = 100;
		}

		//DR:UP__SIZE:100__randomstuff
		String payload = DIRECTION_TAGS[DIRECTION] + "__SIZE:" + ORIGINAL_PACKET_SIZE + "__";
		Random ran = new Random();

		while(payload.length() < ACTUAL_PACKET_SIZE){
			payload += ran.nextDouble();
			payload += ran.nextLong();
			payload += ran.nextFloat();
		}
		if(ACTUAL_PACKET_SIZE < payload.length())
			payload = payload.substring(0, ACTUAL_PACKET_SIZE);//will cut later depending on packet type

		//Log.v("MobiOpen", "rubbish length " + rubbish.length());
		//Log.v("MobiOpen", "PACKET_SIZE " + ORIGINAL_PACKET_SIZE + " NUM_EXP " + NUM_EXP + 
		//		" payload length " + payload.length());

		long start, end;
		try {

			warmUpWithUdp();

			String result = "";

			//NUM_EXP TCP experiments
			for(int i = 0 ; i < NUM_EXP ; i++){

				//promote to DCH
				if(i % 5 == 0)
					warmUpWithUdp();

				Socket tcpSocket = new Socket();
				tcpSocket.setSoTimeout(Definition.TCP_TIMEOUT_IN_MILLI);
				SocketAddress remoteAddr = new InetSocketAddress(Definition.SERVER_NAME, port);
				DataOutputStream os = null;
				DataInputStream is = null;

				//measure latency of establishing TCP connection, is it RTT?
				//validated by trace

				byte[] buf = (payload.substring(0, ACTUAL_PACKET_SIZE - 
						Definition.IP_HEADER_LENGTH - Definition.TCP_HEADER_LENGTH - 
						("" + System.currentTimeMillis()).length()) + 
						System.currentTimeMillis()).getBytes();
				byte[] recv_buf = new byte[ORIGINAL_PACKET_SIZE];

				start = System.currentTimeMillis();
				try{
					tcpSocket.connect( remoteAddr, Definition.TCP_TIMEOUT_IN_MILLI );
				}catch ( Exception e ) {
				}

				//test connect time
				end = System.currentTimeMillis();
				Log.v("MobiOpen", "CONNECT port " + port + 
						" run " + i + " res " + (end - start) + " " + DIRECTION_TAGS[DIRECTION]);
				result += "MobiOpen:CONNECT port " + port + " sequence " + i + " delay " + (end - start) + 
				" timestamp " + System.currentTimeMillis() + 
				" packet_size " + ORIGINAL_PACKET_SIZE + " " + DIRECTION_TAGS[DIRECTION] + "\n";


				//speed up timeout experiments
				if((end - start) > 3000){
					NUM_EXP -= 3;
					continue;
				}

				//ignore HTTP SSH HTTPS
				if(port != 80 && port != 22 && port != 443){

					try{
						os = new DataOutputStream( tcpSocket.getOutputStream() );
						is = new DataInputStream( tcpSocket.getInputStream() );

						//restart timer
						start = System.currentTimeMillis();
						os.write(buf);
						os.flush();
						is.read(recv_buf, 0, recv_buf.length);
					}catch ( Exception e ) {
					}
					//end time for both timeout and normal execution
					end = System.currentTimeMillis();
					Log.v("MobiOpen", "TCP port " + port + 
							" run " + i + " res " + (end - start) + " " + DIRECTION_TAGS[DIRECTION]);
					result += "MobiOpen:TCP port " + port + " sequence " + i + " delay " + (end - start) + 
					" timestamp " + System.currentTimeMillis() + 
					" packet_size " + ORIGINAL_PACKET_SIZE + " " + DIRECTION_TAGS[DIRECTION] + "\n";
					if(is != null)
						is.close();
					if(os != null)
						os.close();
				}

				tcpSocket.close();
			}

			//NUM_EXP UDP experiments
			for(int i = 0 ; i < NUM_EXP ; i++){

				//promote to DCH
				if(i % 5 == 0)
					warmUpWithUdp();

				DatagramSocket socket = new DatagramSocket();
				byte[] buf = (payload.substring(0, ACTUAL_PACKET_SIZE - 
						Definition.IP_HEADER_LENGTH - Definition.UDP_HEADER_LENGTH - 
						("" + System.currentTimeMillis()).length()) + 
						System.currentTimeMillis()).getBytes();

				InetAddress address = InetAddress.getByName(Definition.SERVER_NAME);//should this be IP? no..
				DatagramPacket packet = new DatagramPacket(buf, buf.length, address, port);

				socket.setSoTimeout(Definition.TCP_TIMEOUT_IN_MILLI);
				//prepare to receive request
				byte[] buf2 = new byte[ORIGINAL_PACKET_SIZE];
				DatagramPacket p2 = new DatagramPacket(buf2, buf2.length);

				//send
				start = System.currentTimeMillis();
				socket.send(packet);

				//receive
				try{
					socket.receive(p2);
				}catch ( Exception e ) {
				}
				end = System.currentTimeMillis();

				Log.v("MobiOpen", "UDP port " + port + 
						" run " + i + " res " + (end - start) + " " + DIRECTION_TAGS[DIRECTION]);
				result += "MobiOpen:UDP port " + port + " sequence " + i + " delay " + (end - start) + 
				" timestamp " + System.currentTimeMillis() + 
				" packet_size " + ORIGINAL_PACKET_SIZE + " " + DIRECTION_TAGS[DIRECTION] + "\n";
				socket.close();
			}

			(new Report()).sendReport(result);

		}
		catch ( Exception e ) {
			e.printStackTrace();
		}

		//let server thread know that it is done
		//MainThread.rPorts++;
	}

	public void warmUpWithUdp(){
		try {

			int index = 5; //this is not blocked by t-mobile

			String rubbish = "RUBBISH:";
			Random ran = new Random();
			while(rubbish.length() < 2000){
				rubbish += ran.nextDouble();
				rubbish += ran.nextLong();
				rubbish += ran.nextFloat();
				rubbish += "hellorubbish";
			}


			DatagramSocket socket = new DatagramSocket();
			byte[] buf = rubbish.getBytes();
			InetAddress address = InetAddress.getByName(Definition.SERVER_NAME);//should this be IP? no..
			DatagramPacket packet = new DatagramPacket(buf, buf.length, address, Definition.PORTS[index]);
			socket.setSoTimeout(Definition.TCP_TIMEOUT_IN_MILLI);
			//prepare to receive request
			byte[] buf2 = new byte[10000];
			DatagramPacket p2 = new DatagramPacket(buf2, buf2.length);

			//send
			socket.send(packet);
			//receive
			socket.receive(p2);
			socket.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void shortTest(){
		reachable[index] = false;
		blockedStage[index] = 'o';
		Socket tcpSocket = null;
		DataOutputStream os = null;
		DataInputStream is = null;

		try {
			tcpSocket = new Socket();
			SocketAddress remoteAddr = new InetSocketAddress(Definition.SERVER_NAME, Definition.PORTS[index] );
			tcpSocket.connect( remoteAddr, Definition.TCP_TIMEOUT_IN_MILLI );
			os = new DataOutputStream( tcpSocket.getOutputStream() );
			is = new DataInputStream( tcpSocket.getInputStream() );
			tcpSocket.setSoTimeout( Definition.TCP_TIMEOUT_IN_MILLI );
		}catch ( Exception e ) {
			e.printStackTrace();
			blockedStage[ index ] = 'c';
		}

		if ( tcpSocket == null || os == null || is == null )
			blockedStage[ index ] = 'c';
		else{
			try {
				String helloString = "hello" + System.currentTimeMillis() + Definition.PORTS[index];
				byte [] message = helloString.getBytes();
				os.write(message);
				os.flush();

				byte[] buffer = new byte[ 1000 ];
				int read_bytes = is.read( buffer, 0, 1000 );

				if ( read_bytes != -1 ) {
					String reply = new String( buffer, 0, read_bytes );
					Log.v("LOG", "in port scanning " + index + " for port " + Definition.PORTS[index] + 
							" the reply message we got is <" + reply + ">, length: " + read_bytes);

					if(reply.equals(helloString)) {
						//Log.v("LOG", "it is the same as hello");
						reachable[ index ] = true;
					}
					else if(Definition.PORTS[index] == 22 && reply.startsWith("SSH")) {
						reachable[ index ] = true;
						//need to process HTTPS, HTTP here
					}else{
						//Log.v("LOG", "it is different");
						blockedStage[ index ] = 'd';
					}
				}else{
					blockedStage[ index ] = 'd';
				}

				os.close();
				is.close();
				tcpSocket.close();

			}catch ( Exception e ) {
				blockedStage[ index ] = 'd';
				e.printStackTrace();
			}
		}

		//let server thread know that it is done
		finishedPorts++;
	}

}
