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
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.HashSet;
import java.util.Random;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.util.Log;

public class NAT_Test_Thread extends Thread{

	//private static final String PATH = "/data/data/edu.umich.gg.nat/";
	public static final String PATH = "/data/data/com.mobiperf/";
	public static final String HPING = PATH + "hping";
	public static final String EF_CLIENT = PATH + "ef_client_3";
	public static final String EF_LOG = PATH + "ef_log.txt";
	//private static final String SERVER1 = "gg.selfip.org";
	//private static final String SERVER2 = "qxu.selfip.org";
	private static final String SERVER1 = "dolphin.eecs.umich.edu";	// Assistant server for NAT mapping test
	private static final String SERVER2 = "falcon.eecs.umich.edu";	// Main test server
	private static final String LOG_SERVER = SERVER2;
	private static final int LOG_SERVER_PORT = 65000;
	private StringBuffer efResults;
	private StringBuffer mappingResults;
	public boolean isEFDone = false;
	public boolean isMappingDone = false;
	public boolean isNATTestDone = false;
	private int randomNumber;
	private MainService service;
	private String deviceid;
	
	public NAT_Test_Thread(MainService s)
	{
		this.service = s;
		TelephonyManager tm = (TelephonyManager)service.getSystemService(Context.TELEPHONY_SERVICE);
		deviceid = tm.getDeviceId();
	}
	
	 private void sendResutlsToServer(String finalResult) {
			//Log.v("3gtest_nat", "send to server:\n" + finalResult);		
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
		endpointFilteringTest();
		mappingTest();
		StringBuffer finalResult = new StringBuffer();
		TelephonyManager tm = (TelephonyManager)service.getSystemService(Context.TELEPHONY_SERVICE);
		ConnectivityManager cm =(ConnectivityManager)service.getSystemService(Context.CONNECTIVITY_SERVICE);

		finalResult.append("LOCALIP: " + Phone_IPs.localIP+"\n");
		finalResult.append("GLOBALIP: " + Phone_IPs.seenIP+"\n");
		finalResult.append("ROOT: " + service.isRoot+"\n");
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

		Thread t1 = new Thread()
		{
			public void run()
			{
				while(isMappingDone == false)
				{
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				StringBuffer finalResult = new StringBuffer();
				finalResult.append("########## NAT Mapping ##########\n");
				finalResult.append(mappingResults);
				finalResult.append("########## NAT Mapping Ends ##########\n");
				sendResutlsToServer(finalResult.toString());
			}
		};
		t1.start();
		Thread t2 = new Thread()
		{
			public void run()
			{
				while(isEFDone == false)
				{
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				StringBuffer finalResult = new StringBuffer();
				// Need randomNumber to find server side log for endpoint filtering test
				finalResult.append("RANDOM: "+randomNumber+"\n");
				finalResult.append("########## Endpoint Filtering Client ##########\n");
				finalResult.append(efResults);
				finalResult.append("########## Endpoint Filtering Client Ends ##########\n");
				sendResutlsToServer(finalResult.toString());
			}
		};
		t2.start();
		while(isEFDone == false || isMappingDone == false)
		{
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		isNATTestDone = true;
		Log.v("3gtest_nat", "Both tests finished!");
	}
	 public void connect(String server, int port, String localIp, int localPort)
		{
		 long start = System.currentTimeMillis();
		 long bind = 0, timeout = 0;
	    	while(true)
	    	{
	    		// retry for 60 s
	    		if((System.currentTimeMillis() - start) >= 60000)
	    		{
	    			String result = Utilities.getCurrentGMTTime()+" "+server+":"+port+" " + localIp+":"+localPort + " " + "FAILURE";
					mappingResults.append(result+"\n");
					return;
	    		}
				try {
					Socket socket = new Socket();
					socket.setReuseAddress(true);
					socket.bind(new InetSocketAddress(localIp, localPort));
					if(bind == 0)// only record first bind
						bind = System.currentTimeMillis();
					socket.connect(new InetSocketAddress(server, port));
					timeout = System.currentTimeMillis();
					BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
					String line = null;
					while((line = br.readLine()) != null)
					{
						String result = Utilities.getCurrentGMTTime()+" "+server+":"+port+" " + localIp+":"+localPort + " " + line
							+" "+(bind - start)+" "+(timeout - bind);
						mappingResults.append(result+"\n");
						break;
					}
					socket.close();
					return;
				}
				catch(SocketException e)
				{
					try {
						Thread.sleep(100); // sleep 0.1 s before next trial
					} catch (InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					continue;
				}
				catch (UnknownHostException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	    	}
		}
	 public String getLocalIp()
	    {
	    	Socket socket = null;
			try {
				socket = new Socket("www.google.com", 80);
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    	return socket.getLocalAddress().getHostAddress();
	    }
	 
	    public void mappingTest()
	    {
	    	mappingResults = new StringBuffer();
	    	Thread t = new Thread()
			{
				public void run()
				{
					String localIp = getLocalIp();
					int localPort = 50000;
					int counter = 0;
					for(;localPort <= 60000; localPort+=10001)
					{
						
	    				for(int port = 50001; port <= 50002; port++)
	    				{
	    					for(int i = 0; i < 3; i++)
	    					{
	    						connect(SERVER1, port, localIp, localPort);
	    						counter++;
	    						Log.v("3gtest_nat" ,"NAT Mapping connection " + counter +" finished");
	    					}
	    				}
	    				for(int port = 50002; port >= 50001; port--)
	    				{
	    					for(int i = 0; i < 3; i++)
	    					{
	    						connect(SERVER2, port, localIp, localPort);
	    						counter++;
	    						Log.v("3gtest_nat", "NAT Mapping connection " + counter +" finished");
	    					}
	    				}
	    				connect(SERVER1, 50001, localIp, localPort + 1111);
	    				counter++;
						Log.v("3gtest_nat", "NAT Mapping connection " + counter +" finished");
	    				for(int port= 51000; port <= 51009; port++)
	    				{
	    					connect(SERVER1, 50003, localIp, port);
	    					counter++;
    						Log.v("3gtest_nat", "NAT Mapping connection " + counter +" finished");
	    				}
	    				for(int port= 52000; port <= 52009; port++)
	    				{
	    					connect(SERVER2, 50003, localIp, port);
	    					counter++;
	    					Log.v("3gtest_nat", "NAT Mapping connection " + counter +" finished");
	    				}
					}
					isMappingDone = true;
					String message = getNATMappingMessage();
					if(service.isRunning())	// Check if the Service_Thread is running
					{
						service.addResultAndUpdateUI(message, -1);
						//Utilities.write_to_file(Service_Thread.LOG_FILE_NAME,Context.MODE_APPEND, message+"\n", service);
					}
					else
					{
						// Write to UMLogger_last.txt so we can actually see the result later on
						//Utilities.write_to_file(Service_Thread.LOG_FILE_NAME,Context.MODE_APPEND, message+"\n", service);
						//Utilities.backupLogFile(service);
					}
				}
			};
			t.start();
	    }
	    public String getNATMappingMessage()
	    {
	    	try
	    	{
		    	String lines[] = mappingResults.toString().split("\n");
		    	String localIp = lines[0].split(" ")[2].split(":")[0];
		    	String globalIp = lines[0].split(" ")[3].split(":")[0];
		    	if(localIp.equals(globalIp))
		    	{
		    		return "NAT mapping: NAT does not exist";
		    	}
		    	HashSet<String> remotePortSet = new HashSet<String>();
		    	HashSet<String> remotePortSet1 = new HashSet<String>();
		    	HashSet<String> remotePortSet2 = new HashSet<String>();
		    	HashSet<String> remotePortSet3 = new HashSet<String>();
		    	HashSet<String> remotePortSet4 = new HashSet<String>();
		    	String lastPort = "";
		    	for(int i = 0; i < 3; i++)
		    	{
		    		String remotePort = lines[i].split(" ")[3].split(":")[1];
		    		remotePortSet1.add(remotePort);
		    	}
		    	for(int i = 3; i < 6; i++)
		    	{
		    		String remotePort = lines[i].split(" ")[3].split(":")[1];
		    		remotePortSet2.add(remotePort);
		    	}
		    	for(int i = 6; i < 9; i++)
		    	{
		    		String remotePort = lines[i].split(" ")[3].split(":")[1];
		    		remotePortSet3.add(remotePort);
		    	}
		    	for(int i = 9; i < 12; i++)
		    	{
		    		String remotePort = lines[i].split(" ")[3].split(":")[1];
		    		remotePortSet4.add(remotePort);
		    	}
		    	lastPort = lines[12].split(" ")[3].split(":")[1];
		    	remotePortSet.addAll(remotePortSet1);
		    	remotePortSet.addAll(remotePortSet2);
		    	remotePortSet.addAll(remotePortSet3);
		    	remotePortSet.addAll(remotePortSet4);
		    	Log.v("3gtest_nat", "number of ports "+remotePortSet.size());
		    	if(remotePortSet.size() == 1)
		    	{
		    		if(remotePortSet.contains("50000") && lastPort.equals("51111"))
		    			return "NAT mapping: Independent & port preserving, best for direct P2P communication";
		    		else
		    			return "NAT mapping: Independent, easy for direct P2P communication";
		    	}
		    	else if(remotePortSet.size() > 6)
		    	{
		    		return "NAT mapping: Connection, hard for direct P2P communication";
		    	}
		    	else if(remotePortSet.size() == 4 && remotePortSet1.size() == 1
		    			&& remotePortSet2.size() == 1 && remotePortSet3.size() == 1
		    			&& remotePortSet4.size() == 1)
		    	{
		    		return "NAT mapping: Address & Port, feasible for direct P2P communication";
		    	}
		    	else if(remotePortSet.size() < 4)
		    	{
		    		return "NAT mapping: Address or Port, feasible for direct P2P communication";
		    	}
	    	}catch(Exception e)
	    	{
	    		return "NAT Mapping: Error in test";
	    	}
			return "NAT Mapping: Unknown";
	    }
	    public void endpointFilteringTest()
	    {
	    	efResults  = new StringBuffer();
			if(!service.isRoot)
			{
				Log.v("3gtest_nat", "No root privilege");
				efResults.append("NOROOT\n");
				isEFDone = true;
				return;
			}
			final String serverIp;
			try {
				serverIp = InetAddress.getByName(SERVER2).getHostAddress();
			} catch (UnknownHostException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				efResults.append("DNS error\n");
				isEFDone = true;
				return;
			}
			
			Random generator = new Random();
			randomNumber = generator.nextInt(Integer.MAX_VALUE - 10);
			Log.v("3gtest_nat","Test starts with random number "+ randomNumber);
			Thread t = new Thread()
			{
				public void run()
				{
		    		try
		    		{
			    		Process process = Runtime.getRuntime().exec("su");
			    		DataOutputStream os = new DataOutputStream(process.getOutputStream());
			    		String commands[] = {
			    				EF_CLIENT + " "+ EF_LOG + " &",
			    				HPING + " " + serverIp + " -S -p 60000 -M "+randomNumber+" -c 1 &", // no change
			    				HPING + " " +serverIp + " -S -p 60001 -M "+randomNumber+" -c 1 &",	// port 
			    				HPING + " " +serverIp + " -S -p 60002 -M "+randomNumber+" -c 1 &",	// ip+port
			    				HPING + " " +serverIp + " -S -p 60003 -M "+randomNumber+" -c 1 &",	// ip
			    				HPING + " " +serverIp + " -S -p 61000 -M "+randomNumber+" -c 1 &",	// SYN-out,SYN-in
			    				HPING + " " +serverIp + " -S -p 61001 -M "+randomNumber+" -c 1 &",	// SYN-out,RST-in,SYN-in
			    				HPING + " " +serverIp + " -S -p 61002 -M "+randomNumber+" -c 1 &",	// SYN-out,RST-in,SYN-ACK-in
			    				HPING + " " +serverIp + " -S -p 62000 -s 62000 -M "+randomNumber+" -c 1 &",	// SYN-out
			    				HPING + " " +serverIp + " -SA -p 62000 -s 62000 -M "+randomNumber+" -c 1 &", // SYN-ACK-out
			    				HPING + " " +serverIp + " -S -p 62001 -s 62001 -M "+randomNumber+" -c 1 &",	// SYN-out, RST-in
			    				HPING + " " +serverIp + " -SA -p 62001 -s 62001 -M "+randomNumber+" -c 1 &", // SYN-ACK-out
			    				HPING + " " +serverIp + " -SA -p 63000 -M "+randomNumber+" -c 1 &", // SYN-ACK
			    				HPING + " " +serverIp + " -A -p 63000 -M "+randomNumber+" -c 1 &",	// ACK
			    				HPING + " " +serverIp + " -p 63000 -M "+randomNumber+" -c 1 &"  // DATA
			    				};
			    		
			    		for (String single : commands) {
				    		   os.writeBytes(single + "\n");
				    		   os.flush();
				    		   Thread.sleep(1000);				    		   
				    	}
			    		process.destroy();
			    		File file = new File(EF_LOG);
			    		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
			    		String line = null;
			    		while((line = br.readLine())!=null)
			    		{
			    			efResults.append(line+"\n");
			    		}
			    		file.delete();
			    		isEFDone = true;
		    		}catch(Exception e){
		    			e.printStackTrace();
		    			isEFDone = true;
		    			}
				}
			};
			t.start();
	    }

}
