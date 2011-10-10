/****************************
 *
 * @Date: Oct 6, 2011
 * @Time: 4:50:36 PM
 * @Author: Junxian Huang
 *
 ****************************/
package com.mobiperf.lte;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;

/**
 * 
 * This class serves for both 4G Test/MobiPerf, for measuring RTT from
 * client to the nearest MLab nodes.
 *
 */
public class RTT {

	public static void test(){
		
		Mlab.prepareServer();
		
		if(Definition.DEBUG)
			assert(Mlab.ipList.length == 10);
		
		for(int i = 0; i < 10 ; i++){
			for(int s = 0 ; s < 10 ; s++){
				System.out.println("RTT to server " + s + " " + Mlab.ipList[s] + " RTT: " + unitTest(Mlab.ipList[s]));
			}
		}

	}

	/**
	 * 
	 * @param host
	 * @return rtt of TCP Handshake in milliseconds
	 */
	public static long unitTest(String host){
		long rtt = 0;

		long start, end;
		Socket tcpSocket = new Socket();

		try {
			tcpSocket.setSoTimeout(Definition.TCP_TIMEOUT_IN_MILLI);
		} catch (SocketException e1) {
			e1.printStackTrace();
		}
		SocketAddress remoteAddr = new InetSocketAddress(host, Definition.PORT_UPLINK_MLAB);

		start = System.currentTimeMillis();
		try{
			tcpSocket.connect(remoteAddr, Definition.TCP_TIMEOUT_IN_MILLI);
		}catch ( Exception e ) {
		}

		//test connect time
		end = System.currentTimeMillis();

		try {
			tcpSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		rtt = end - start;
		return rtt;
	}

}
