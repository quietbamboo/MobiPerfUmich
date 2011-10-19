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

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Build;
import android.telephony.TelephonyManager;


public class TCP_Injection_Vuln_Test_Thread extends Thread{

	private MainService service;
	public static final String SERVER = "falconeecs.dyndns.org";
	public static final String SERVER2 = "falcon.eecs.umich.edu";

	public TCP_Injection_Vuln_Test_Thread(MainService s){
		this.service = s;	
	}


	public void run()
	{

		StringBuffer finalResult = new StringBuffer();
		try{
			TelephonyManager tm = (TelephonyManager)service.getSystemService(Context.TELEPHONY_SERVICE);
			ConnectivityManager cm =(ConnectivityManager)service.getSystemService(Context.CONNECTIVITY_SERVICE);
			
			finalResult.append(tm.getNetworkOperatorName()+"|");
			finalResult.append(tm.getDeviceId()+"|");
			finalResult.append("RUNID: " + InformationCenter.getRunId() +"|");
			finalResult.append("LOCALIP: " + Phone_IPs.localIP+"|");
			finalResult.append("GLOBALIP: " + Phone_IPs.seenIP+"|");
			finalResult.append("GPS: " + GPS.latitude +"," + GPS.longitude+"|");
			finalResult.append("CELLLOCATION: "+tm.getCellLocation()+"|");
			finalResult.append("MCCMNC: " + tm.getNetworkOperator()+"|");
			finalResult.append("NETWORKTYPE: " + tm.getNetworkType()+"|");
			finalResult.append("PHONETYPE: " + tm.getPhoneType() +"|");
			finalResult.append("DATASTATE: " + tm.getDataState() +"|");
			finalResult.append("TYPE: " + cm.getActiveNetworkInfo().getType() +"|");
			finalResult.append("TYPENAME: " + cm.getActiveNetworkInfo().getTypeName() +"|");
			finalResult.append("SUBTYPE: " + cm.getActiveNetworkInfo().getSubtype() +"|");
			finalResult.append("SUBTYPENAME: " + cm.getActiveNetworkInfo().getSubtypeName() +"|");
			finalResult.append("ISCONNECTED: " + cm.getActiveNetworkInfo().isConnected() +"|");
			finalResult.append("BUILD.MODEL: "+ Build.MODEL +"|");
			finalResult.append("BUILD.VERSION: "+ Build.VERSION.SDK +"|");
			finalResult.append("BUILD.BRAND|DEVICE|DISPLAY|PRODUCT|TYPE|BOARD: " +Build.BRAND+"|"+Build.DEVICE+"|"+Build.DISPLAY+"|"+Build.PRODUCT+"|"+Build.TYPE+"|"+Build.BOARD);
		}catch(NullPointerException e){
			e.printStackTrace();
			return;
		}

		Socket s;
		Socket s2 = new Socket();
		try {
			s = new Socket(SERVER, 30000);
			PrintWriter out = new PrintWriter(s.getOutputStream(), true);
			out.println(finalResult.toString());
			out.flush();

			s2.setReceiveBufferSize(524288);
			SocketAddress sockaddr = new InetSocketAddress(SERVER2, 30001);
			s2.connect(sockaddr);
			PrintWriter out2 = new PrintWriter(s2.getOutputStream(), true);
			out2.println(finalResult.toString());
			out2.flush();

			Thread.sleep(160000);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}	
	}

}
