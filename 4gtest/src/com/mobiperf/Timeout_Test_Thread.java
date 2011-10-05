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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.util.Log;

public class Timeout_Test_Thread extends Thread{

	private static final String ECHO_SERVER = "falcon.eecs.umich.edu";
	private static final int ECHO_SERVER_PORT = 11111;
	private static final String LOG_SERVER = "falcon.eecs.umich.edu";
	private static final int LOG_SERVER_PORT = 65001;

	private Context service;
	private String deviceid;
	
	public Timeout_Test_Thread(Context s)
	{
		this.service = s;
		TelephonyManager tm = (TelephonyManager)service.getSystemService(Context.TELEPHONY_SERVICE);
		deviceid = tm.getDeviceId();
	}
	
	// sleepTime in second
	public Thread test(final int sleepTime)
	{
		Thread t = new Thread()
		{
			public void run()
			{
				long start = System.currentTimeMillis(), duration;
				Log.v("3gtest_timeout", "start sleep time " + sleepTime);
				try {	
					Socket s = new Socket(ECHO_SERVER, ECHO_SERVER_PORT);
					s.setSoTimeout(10000);	// 10s timeout
					try {
						Thread.sleep(sleepTime * 1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					PrintStream ps = new PrintStream(s.getOutputStream());
					BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream()));
					String send = deviceid + " " + InformationCenter.getRunId();
					ps.println(send);
					String line = null;
					while((line = br.readLine()) != null)
					{
						duration = (System.currentTimeMillis() - start)/1000; //in second
						String localIp = getLocalIp();
						String log = Utilities.getCurrentGMTTime()+" TIMEOUTTEST "+sleepTime+" ALIVE "+duration+" "+localIp+" "+line;
						Log.v("3gtest_timeout", "log: "+ log);
						if(line.indexOf(deviceid) != -1)
							sendResutlsToServer(log);	
						break;
					}
					br.close();
					ps.close();
					s.close();
				}
				catch (UnknownHostException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
					duration = (System.currentTimeMillis() - start)/1000;
					String localIp = getLocalIp();
					String log = Utilities.getCurrentGMTTime()+" TIMEOUTTEST "+sleepTime+" DEAD "+duration+" "+localIp;
					sendResutlsToServer(log);
				}
			}
		};
		t.start();
		return t;
	}
	 private void sendResutlsToServer(String finalResult) {
			//Log.v("3gtest_timeout", "send to server:\n" + finalResult);
			
			StringBuffer prefix = new StringBuffer();
			prefix.append("RUNID: " + InformationCenter.getRunId() +"\n");
			prefix.append("DEVICEID: "+deviceid+"\n");
			try {
				Socket socket = new Socket(this.LOG_SERVER, this.LOG_SERVER_PORT);
				PrintStream ps = new PrintStream(socket.getOutputStream());
				ps.print(prefix);
				ps.print(finalResult);
				ps.close();
				socket.close();
			} catch (UnknownHostException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	
	public void run()
	{	
		StringBuffer finalResult = new StringBuffer();
		TelephonyManager tm = (TelephonyManager)service.getSystemService(Context.TELEPHONY_SERVICE);
		ConnectivityManager cm =(ConnectivityManager)service.getSystemService(Context.CONNECTIVITY_SERVICE);

		//finalResult.append("ROOT: " + Utilities.checkRootPrivilege()+"\n");
		finalResult.append("LOCALIP: " + Phone_IPs.localIP +"\n");
		finalResult.append("GLOBALIP: " + Phone_IPs.seenIP+"\n");
		finalResult.append("GPS: " + GPS.latitude +"," + GPS.longitude +"\n");
		finalResult.append("COUNTRY: " + Utilities.getCountry(service)+"\n");
		finalResult.append("CITY: " + Utilities.getCity(service)+"\n");
		finalResult.append("ZIPCODE: "+ Utilities.getZipcode(service)+"\n");
		finalResult.append("OPERATORNAME: "+tm.getNetworkOperatorName()+"\n");
		finalResult.append("CELLLOCATION: "+tm.getCellLocation()+"\n");
		finalResult.append("MCCMNC: " + tm.getNetworkOperator()+"\n");
		finalResult.append("NETWORKTYPE: " + tm.getNetworkType()+"\n");
		finalResult.append("PHONETYPE: " + tm.getPhoneType() +"\n");
		finalResult.append("DATASTATE: " + tm.getDataState() +"\n");
		finalResult.append("TYPE: " + cm.getActiveNetworkInfo().getType() +"\n");
		finalResult.append("TYPENAME: " + cm.getActiveNetworkInfo().getTypeName() +"\n");
		finalResult.append("SUBTYPE: " + cm.getActiveNetworkInfo().getSubtype() +"\n");
		finalResult.append("SUBTYPENAME: " + cm.getActiveNetworkInfo().getSubtypeName() +"\n");
		finalResult.append("ISCONNECTED: " + cm.getActiveNetworkInfo().isConnected() +"\n");
		finalResult.append("BUILD.MODEL: "+ Build.MODEL +"\n");
		finalResult.append("BUILD.VERSION: "+ Build.VERSION.SDK +"\n");
		finalResult.append("BUILD.BRAND|DEVICE|DISPLAY|PRODUCT|TYPE|BOARD: " +Build.BRAND+"|"+Build.DEVICE+"|"+Build.DISPLAY+"|"+Build.PRODUCT+"|"+Build.TYPE+"|"+Build.BOARD+"\n");
		sendResutlsToServer(finalResult.toString());

		Thread t0 = test(1);		// 1  s
		Thread t1 = test(5  * 60);	// 5  min
		Thread t2 = test(10 * 60);	// 10 min
		Thread t3 = test(20 * 60);	// 20 min
		Thread t4 = test(30 * 60);	// 30 min
		try {
			t0.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		try {
			t1.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		try {
			t2.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		try {
			t3.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		try {
			t4.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		Log.v("3gtest_timeout", "all tests finished!");
	}
	
	/**
	 * 
	 * @return "UNKNOWN" if error
	 */
	public String getLocalIp()
    {
    	Socket socket = null;
    	String ip = "UNKNOWN";
		try {
			socket = new Socket("www.google.com", 80);
			ip = socket.getLocalAddress().getHostAddress();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
    	return ip;
    }
}