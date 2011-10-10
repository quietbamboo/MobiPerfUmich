/****************************
*
* @Date: Oct 9, 2011
* @Time: 1:52:04 PM
* @Author: Junxian Huang
*
****************************/
package com.mobiperf.lte;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.Arrays;

public class ThroughputMulti extends Thread{
	
	//constant
	public final static long SAMPLE_PERIOD = 1000; 
	public final static long SLOW_START_PERIOD = 5000; //empirically set to 5 seconds 
	
	//global
	public static int size = 0;
	public static long testStartTime = 0; //test start time, used to determine slow start period
	public static long startTime = 0; //start time of this period to calculate throughput
	public static int index = 0;
	public static double tps[] = new double[1000]; //large enough
	
	//local
	public String host;
	public boolean isDown;
	
	public ThroughputMulti(String host, boolean isDown){
		this.host = host;
		this.isDown = isDown;
	}
	
	public static void startTest(boolean isDown, int parallel){
		
		Mlab.prepareServer();
		assert(Mlab.ServerList.length > parallel);
		
		reset();
		
		ThroughputMulti[] tm = new ThroughputMulti[parallel];
		for(int i = 0 ; i < parallel ; i++){
			tm[i] = new ThroughputMulti(Mlab.ServerList[i], isDown);
			tm[i].start();
		}
		
		for(int i = 0 ; i < parallel ; i++){
			try {
				tm[i].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	
		(new Report()).sendReport("THROUGHPUT_MULTI:<parallel:" + parallel + 
				"><down:" + getMedianThroughput() + "><sample:" + index + ">;");
	}
	
	/**
	 * called before each test in terms of total uplink/downlink test
	 */
	public static void reset(){
		size = 0;
		testStartTime = System.currentTimeMillis();
		startTime = 0;
		index = 0;
	}
	
	/**
	 * this function should be called everytime a packet/data bulk is received or sent
	 * @param delta
	 * @param isDown
	 */
	public synchronized static void updateSize(int delta, boolean isDown){
		double gtime = System.currentTimeMillis() - testStartTime;
		if(gtime < SLOW_START_PERIOD)//ignore slow start
			return;
		
		if(startTime == 0){
			//starting first sample
			startTime = System.currentTimeMillis();
			size = 0;
		}
		
		size += delta;
		
		double time = System.currentTimeMillis() - startTime;
		if(time < SAMPLE_PERIOD){//wait till sample period finishes
			return;
		}else{
			//now samples
			double throughput = (double)size * 8.0 / time; //time is in milli, so already kbps
			if(isDown)
				System.out.print("Downlink");
			else
				System.out.print("Uplink");
			System.out.println("_throughput: " + throughput + " kbps_Time(sec): " + (gtime / 1000.0));
			
			//record this sample
			tps[index] = throughput;
			index++;
			
			size = 0;
			startTime = System.currentTimeMillis();
		}	
	}
	
	public static double getMedianThroughput(){
		double median = 0;
		
		Arrays.sort(tps, 0, index);
		
		if(index % 2 == 0){
			//index is even, e.g., index = 4, => (2 + 1) / 2
			median = (tps[index / 2] + tps[index / 2 - 1]) / 2;
		}else{
			//index is odd, e.g. index = 3, => 1
			median = tps[(index - 1) / 2];
		}
		
		return median;
	}
	
	public void run(){
		System.out.println("Thread running ID " + getId());
		if(isDown)
			downlink();
		else
			uplink();
	}
	
	public void uplink(){
		
		
	}
	
	public void downlink(){
		
		
		Socket tcpSocket = null;
		DataOutputStream os = null;
		DataInputStream is = null;

		try {
			tcpSocket = new Socket();
			SocketAddress remoteAddr = new InetSocketAddress( host, Definition.PORT_DOWNLINK_MLAB);
			tcpSocket.connect( remoteAddr, Definition.TCP_TIMEOUT_IN_MILLI );
			tcpSocket.setSoTimeout( Definition.TCP_TIMEOUT_IN_MILLI );
			tcpSocket.setTcpNoDelay( true );

			os = new DataOutputStream( tcpSocket.getOutputStream() );
			is = new DataInputStream( tcpSocket.getInputStream() );
		}catch ( Exception e ) {
			e.printStackTrace();
			return;
		}

		byte [] message =  InformationCenter.getPrefix().getBytes();

		try {
			os.write( message );
			os.flush();
			
			//don't need to check server's response
			Thread.sleep(2000); //sleep for 2 seconds for server to be ready
			
			int read_bytes = 0;
			byte[] buffer = new byte[15000];
			do {
				read_bytes = is.read(buffer, 0, buffer.length);
				updateSize(read_bytes, true);
			}
			while(read_bytes >= 0);
			
		}catch ( Exception e ) {
			e.printStackTrace();
			return;
		}

		
		try {
			os.close();
			is.close();
			tcpSocket.close();
		}catch ( Exception e ) {
			e.printStackTrace();
		}
	}

}
