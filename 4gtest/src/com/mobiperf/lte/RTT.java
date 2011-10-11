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

import android.app.Service;

/**
 * 
 * This class serves for both 4G Test/MobiPerf, for measuring RTT from
 * client to the nearest MLab nodes.
 *
 */
public class RTT {
	
	public static double[] rtts; 
	//every time a test is complete, this array is regenerated with a new size 
	//and ending element as the last run result 
	
	
	public static void reset(){
		rtts = new double[]{};
	}
	
	public static void test(Service service){
		
		Mlab.prepareServer();
		reset();
		
		if(Definition.DEBUG)
			assert(Mlab.ipList.length == 10);
		
		for(int i = 1; i <= 16 ; i++){
			//for(int s = 0 ; s < 10 ; s++){
			//	System.out.println("RTT to server " + s + " " + Mlab.ipList[s] + " RTT: " + unitTest(Mlab.ipList[s]));
			//}
			try {
				Thread.sleep(500);//for animation purpose
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			rtts = Utilities.pushResult(rtts, unitTest(Mlab.ipList[0]));
			((MainService)service).updateChart(RTT.rtts, ThroughputMulti.tps_down, ThroughputMulti.tps_up);
		}
		
		(new Report()).sendReport("MLAB_RTT:<median:" + Utilities.getMedian(rtts) + 
				"><max:" + Utilities.getMax(rtts) + 
				"><min:" +Utilities.getMin(rtts) + 
				"><sample:" + rtts.length + ">;");

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
		} catch (SocketException e) {
			e.printStackTrace();
		}
		SocketAddress remoteAddr = new InetSocketAddress(host, Definition.PORT_UPLINK_MLAB);

		start = System.currentTimeMillis();
		try{
			tcpSocket.connect(remoteAddr, Definition.TCP_TIMEOUT_IN_MILLI);
		}catch ( Exception e ) {
			e.printStackTrace();
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
