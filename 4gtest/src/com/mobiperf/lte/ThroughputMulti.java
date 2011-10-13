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

import android.app.Service;
import android.util.Log;

public class ThroughputMulti extends Thread{
	
	//constant
	public final static long SAMPLE_PERIOD = 1000; 
	public final static long SLOW_START_PERIOD = 5000; //empirically set to 5 seconds 
	
	//global
	public static int size = 0;
	public static long testStartTime = 0; //test start time, used to determine slow start period
	public static long startTime = 0; //start time of this period to calculate throughput
	public static double tps_up[];
	public static double tps_down[];
	public static boolean isDown;
	public static Service service;
	
	//local
	public String host;
	
	public ThroughputMulti(String host){
		this.host = host;
	}
	
	public static void startTest(boolean isDown, int parallel, Service service){
		
		ThroughputMulti.isDown = isDown;
		ThroughputMulti.service = service;
		Mlab.prepareServer();
		assert(Mlab.ipList.length > parallel);
		
		reset(isDown);
		
		ThroughputMulti[] tm = new ThroughputMulti[parallel];
		
		//start tcpdump on each server
		for(int i = 0 ; i < parallel ; i++){
			if(isDown)
				(new Report()).sendCommand(Definition.COMMAND_MLAB_INIT_DOWNLINK, Mlab.ipList[i]);
			else
				(new Report()).sendCommand(Definition.COMMAND_MLAB_INIT_UPLINK, Mlab.ipList[i]);
		}
		
		for(int i = 0 ; i < parallel ; i++){
			
			tm[i] = new ThroughputMulti(Mlab.ipList[i]);
			tm[i].start();
		}
		
		for(int i = 0 ; i < parallel ; i++){
			try {
				tm[i].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		//terminate tcpdump and upload to central server on each server
		for(int i = 0 ; i < parallel ; i++){
			if(isDown)
				(new Report()).sendCommand(Definition.COMMAND_MLAB_END_DOWNLINK, Mlab.ipList[i]);
			else
				(new Report()).sendCommand(Definition.COMMAND_MLAB_END_UPLINK, Mlab.ipList[i]);
			
		}
	
		String type;
		if(isDown){
			type = "DOWN";
			(new Report()).sendReport("MLAB_THROUGHPUT_" + type + ":<parallel:" + parallel + 
					"><median:" + Utilities.getMedian(tps_down) + "><max:" + Utilities.getMax(tps_down) + 
					"><min:" + Utilities.getMin(tps_down) + "><sample:" + tps_down.length + ">;");
		}else{
			type = "UP";
			(new Report()).sendReport("MLAB_THROUGHPUT_" + type + ":<parallel:" + parallel + 
					"><median:" + Utilities.getMedian(tps_up) + "><max:" + Utilities.getMax(tps_up) + 
					"><min:" + Utilities.getMin(tps_up) + "><sample:" + tps_up.length + ">;");
		}
		
	}
	
	/**
	 * called before each test in terms of total uplink/downlink test
	 */
	public static void reset(boolean isDown){
		size = 0;
		testStartTime = System.currentTimeMillis();
		startTime = 0;
		if(isDown)
			tps_down = new double[]{};
		else
			tps_up = new double[]{};
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
			if(isDown){
				tps_down = Utilities.pushResult(tps_down, throughput);
			}else{
				tps_up = Utilities.pushResult(tps_up, throughput);
			}
			
			((MainService)service).updateChart(RTT.rtts, ThroughputMulti.tps_down, ThroughputMulti.tps_up);
			
			size = 0;
			startTime = System.currentTimeMillis();
		}	
	}
	
	
	public void run(){
		System.out.println("4G Test: Thread running ID " + getId() + " server " + host);
		if(isDown)
			downlink();
		else
			uplink();
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
				
				//if(System.currentTimeMillis() % 100 == 1)
					//System.out.println("Update receive " + read_bytes +  " . Thread ID " + this.getId());
				
				
				updateSize(read_bytes, true);
			}while(read_bytes >= 0);
			
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
	
	public void uplink(){
		
		Socket tcpSocket = null;
		DataOutputStream os = null;
		DataInputStream is = null;

		try {
			tcpSocket = new Socket();
			SocketAddress remoteAddr = new InetSocketAddress(host, Definition.PORT_UPLINK_MLAB);
			tcpSocket.connect( remoteAddr, Definition.TCP_TIMEOUT_IN_MILLI );
			os = new DataOutputStream( tcpSocket.getOutputStream() );
			is = new DataInputStream( tcpSocket.getInputStream() );
			tcpSocket.setSoTimeout( Definition.TCP_TIMEOUT_IN_MILLI );
			tcpSocket.setTcpNoDelay(true);

		}catch (Exception e){
			e.printStackTrace();
			return;
		}

		//1K buffer
		String buf = Utilities.genRandomString(Definition.THROUGHPUT_UP_SEGMENT_SIZE);

		byte [] message =  InformationCenter.getPrefix().getBytes();

		Log.v("LOG", "sent prefix for uplink " + InformationCenter.getPrefix());

		try {
			os.write( message );
			os.flush();
			
			//don't need to check response
			Thread.sleep(4000); //sleep for 4 seconds for server to be ready

		}
		catch (Exception e) {
			e.printStackTrace();
			return;
		}

		message = buf.getBytes();
		System.out.println ("------- MESSAGE LENGTH = " + message.length);
		long startTime = System.currentTimeMillis();
		long endTime = System.currentTimeMillis();

		//Test lasts 16 seconds - Junxian
		try {
			do {
				os.write(message);
				endTime = System.currentTimeMillis();
				
				//if(System.currentTimeMillis() % 100 == 1)
					//System.out.println("Update receive " + message.length +  " . Thread ID " + this.getId());
				
				ThroughputMulti.updateSize(message.length, false);
			}while((endTime - startTime) < Definition.TP_DURATION_IN_MILLI);
		}catch ( Exception e ) {
			e.printStackTrace();
			return;
		}

		try {
			os.close();
			is.close();
			tcpSocket.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

}
