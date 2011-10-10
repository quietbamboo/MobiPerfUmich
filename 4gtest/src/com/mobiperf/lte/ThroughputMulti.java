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
import java.net.UnknownHostException;

public class ThroughputMulti extends Thread{
	
	//global
	public static int downSize = 0;
	public static long downStart = 0;
	
	//local
	public String host;
	
	public void run(){
		System.out.println("Thread running ID " + getId());
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


		// getting downlink data
		byte [] message =  InformationCenter.getPrefix().getBytes();

		try {

			os.write( message );
			os.flush();

		}
		catch ( Exception e3 ) {
			retval = 4;
			break outer;
		}

		//don't need to check server's response
		try {
			Thread.sleep(2000); //sleep for 2 seconds for server to be ready
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}

		if ( Utilities.checkStop() ) {
			break outer;
		}

		long startTime = System.currentTimeMillis();
		long endTime = System.currentTimeMillis();
		long total_read_bytes = 0;

		do {
			if ( Utilities.checkStop() ) {
				break outer;
			}

			try {
				byte[] buffer = new byte[ 15000 ];


				int read_bytes = is.read( buffer, 0, 15000 );

				if ( read_bytes <= 0 )
					break;

				total_read_bytes += read_bytes;


			}
			catch ( Exception e ) {
				e.printStackTrace();
				break;
			}

			endTime = System.currentTimeMillis();
		}
		while ( true );

		if ( total_read_bytes > 1000 ) {
		}
		else {
			retval = 5;
			break outer;
		}

		long timespent = ( endTime - startTime );
		downlinkSize = total_read_bytes;
		downlinkTime = timespent;


		try {
			os.close();
			is.close();
			tcpSocket.close();

			if ( retval != -1 )
				return retval;
		}
		catch ( Exception e2 ) {
			if ( retval != -1 )
				return retval;
			else
				return 6;

		}
		return 7;
	}

}
