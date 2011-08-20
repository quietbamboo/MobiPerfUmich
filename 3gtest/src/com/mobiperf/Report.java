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
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;

import android.util.Log;


class Report  {

	public void sendReport(String result) {

		//try twice
		if(!trySendReport(result))
			trySendReport(result);
	}

	/**
	 * 
	 * @param result
	 * @return false if fails in any form
	 */
	public boolean trySendReport(String result){

		Socket remoteTCPSocket; 
		DataOutputStream remoteOutputStream; 
		DataInputStream remoteInputStream;

		result += "\n";

		try {
			remoteTCPSocket = new Socket();
			SocketAddress remoteAddr = new InetSocketAddress(Definition.SERVER_NAME, Definition.PORT_CONTROL);
			remoteTCPSocket.connect( remoteAddr, Definition.TCP_TIMEOUT_IN_MILLI );
			remoteOutputStream = new DataOutputStream( remoteTCPSocket.getOutputStream() );
			remoteInputStream = new DataInputStream( remoteTCPSocket.getInputStream() );
			remoteTCPSocket.setSoTimeout( Definition.TCP_TIMEOUT_IN_MILLI );

			if (remoteTCPSocket == null || remoteOutputStream == null || remoteInputStream == null)
				return false;

			byte [] message = result.getBytes();
			byte[] buffer = new byte[1000];
			int read_bytes;
			byte [] message1 = InformationCenter.getPrefix().getBytes();

			remoteOutputStream.write( message1 );
			remoteOutputStream.flush();
			Log.v("3gtest", "prefix: " + InformationCenter.getPrefix());

			//Thread.sleep( 1000 );
			read_bytes = remoteInputStream.read( buffer, 0, 1000 );

			/*if ( read_bytes > 0 ) {
                String reply = new String( buffer, 0, read_bytes );
            }//*/

			//String ln;
			//while ((ln = remoteInputStream.readLine()) != null){
			Log.v("LOG", "3gtest server response line: bytes " + read_bytes);
			//}

			remoteOutputStream.write( message );
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

}
