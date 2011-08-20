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
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Hashtable;
import java.util.Random;

import android.util.Log;

public class Spoof_Test_Thread extends Thread {
	private String IP_spoofing_server;
	private String SERVER = "falcon.eecs.umich.edu";
	private int IP_spoofing_server_port = 9990;
	private int IP_spoofing_server_port2 = 9991;

	private Hashtable<Byte, Integer> lastOctToExisting = new Hashtable<Byte, Integer>(); //192.168.1.X
	private Hashtable<Byte, Integer> secondlastOctToExisting = new Hashtable<Byte, Integer>(); //192.168.X.Y

	// TODO:
	private String carrier;
	private String deviceID;
	private String networkType;
	private String gps;
	private String country;
	private String city;
	private String zipcode;


	Random random = new Random();

	Process rootProcess = null;

	public int ip_spoofing_allowed = 0;
	public int network_failure = 1;


	private MainService service;
	private String message;

	public Spoof_Test_Thread(MainService service)
	{
		this.service = service;
		carrier = InformationCenter.getNetworkOperator();
		deviceID = InformationCenter.getDeviceID();
		networkType = InformationCenter.getNetworkTypeName();//"WIFI" "MOBILE" "mobile"
		gps = GPS.latitude +"," + GPS.longitude;
		country = Utilities.getCountry(service);
		city=Utilities.getCity(service);
		zipcode=Utilities.getZipcode(service);
		message = "Firewall allows IP spoofing?: ";

		try {
			rootProcess = Runtime.getRuntime().exec("su");
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	@Override
	public void run()
	{     
		Log.v("3gtest_spoof", "spoof test thread start");    

		connectToSpoofingServer();
		Log.v("3gtest_spoof", "spoof test thread ends");
	}
	public String getResultMessage()
	{
		return this.message;
	}

	private byte[] changeIPAddrLastOct(byte[] ip_octs)
	{
		byte[] new_ip_octs = (byte[])ip_octs.clone();;
		lastOctToExisting.put(ip_octs[3],1);
		do
		{
			int randOct = random.nextInt(254) + 1;
			//Log.v("LOG", "(((((((((((((((((((((((((((((((((((((((((((((( randOct: " + String.valueOf(randOct));
			if(lastOctToExisting.get(randOct) == null)
			{
				new_ip_octs[3] = (byte)randOct;
				lastOctToExisting.put(new_ip_octs[3],1);
				break;
			}
		}
		while(true);
		return new_ip_octs;
	}

	private byte[] changeIPAddrSecondLastOct(byte[] ip_octs)
	{
		byte[] new_ip_octs = (byte[])ip_octs.clone();;
		secondlastOctToExisting.put(ip_octs[2],1);
		do
		{
			int randOct = random.nextInt(254) + 1;
			if(secondlastOctToExisting.get(randOct) == null)
			{
				new_ip_octs[2] = (byte)randOct;
				int randOct2 = random.nextInt(254) + 1;
				new_ip_octs[3] = (byte)randOct2;
				secondlastOctToExisting.put(new_ip_octs[2],1);
				break;
			}
		}
		while(true);
		return new_ip_octs;
	}

	public void connectToSpoofingServer()
	{
		try {
			IP_spoofing_server = InetAddress.getByName(SERVER).getHostAddress();
		} catch (UnknownHostException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			Log.v("3gtest_spoof", "firewall test server " + IP_spoofing_server);
			return;
		}

		Socket s;
		try {
			s = new Socket(IP_spoofing_server, IP_spoofing_server_port);
			s.setSoTimeout(20000);
			Log.v("LOG", "22222222222222222222222222222222222222222222222222222222222222222222222222222222222222");


			//ObjectOutputStream out = new ObjectOutputStream(s.getOutputStream());
			PrintWriter out = new PrintWriter(s.getOutputStream(), true);

			out.flush();
			Log.v("LOG", "22222222------------------------------------333322222222222222222222222222222222222222222222222222222222222222222222");
			BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
			Log.v("LOG", "2222222222222222222222222555555555555555555------------444444444444444444422222222222222222222222222222222222");
			InetAddress localIP = s.getLocalAddress();
			Log.v("LOG", "33333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333");
			String localIPstr = localIP.getHostAddress();

			Log.v("LOG", "444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444");
			Log.v("LOG", "local IP string: " + localIPstr);

			String all = carrier + "|" + deviceID + "|" + localIPstr + "|" + InformationCenter.getRunId() + "|" + networkType+"|"+gps + "|"+country+"|"+city+"|"+zipcode+"\r\n";
			Log.v("LOG", "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! sent " + all);

			out.write(all);
			out.flush();


			//spoofing 10 /24 IPs
			for(int i = 0; i < 10; ++i)
			{
				byte[] new_ip_oct = changeIPAddrLastOct(localIP.getAddress());
				String newLocalIP = InetAddress.getByAddress(new_ip_oct).getHostAddress();
				out.write(newLocalIP + "\n");
				out.flush();
				String counter = in.readLine();
				network_failure = 0;
				Log.v("LOG", "^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^ read: " + counter);
				try
				{
					int temp_counter = Integer.valueOf(counter);
					Log.v("LOG", "temp counter:" + temp_counter);
				}
				catch(NumberFormatException e)
				{
					if(counter != null)
					{
						if(counter.equals("succ"))
						{
							ip_spoofing_allowed = 1;
						}

					}
					continue;
				}



				String cmd = NAT_Test_Thread.HPING+" -c 1 -S " + IP_spoofing_server + " -p " + String.valueOf(IP_spoofing_server_port2) + " -M " + counter + " -a " + newLocalIP + " &";
				Log.v("LOG", "################################# hping cmd: " + cmd);

				Log.v("3gtest_spoof", "tried to do hping");	        
				DataOutputStream os = new DataOutputStream(rootProcess.getOutputStream());
				os.writeBytes(cmd + "\n");
				os.flush();

			}

			//spoofing 10 /16 IPs
			for(int i = 0; i < 10; ++i)
			{
				byte[] new_ip_oct = changeIPAddrSecondLastOct(localIP.getAddress());
				String newLocalIP = InetAddress.getByAddress(new_ip_oct).getHostAddress();
				out.write(newLocalIP + "\n");
				out.flush();
				String counter = in.readLine();
				network_failure = 0;
				Log.v("LOG", "^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^ read: " + counter);
				try
				{
					int temp_counter = Integer.valueOf(counter);
					Log.v("LOG", "temp counter:" + temp_counter);
				}
				catch(NumberFormatException e)
				{
					if(counter != null)
					{
						if(counter.equals("succ"))
						{
							ip_spoofing_allowed = 1;
						}
					}

					continue;
				}
				String cmd = NAT_Test_Thread.HPING+" -c 1 -S " + IP_spoofing_server + " -p " + String.valueOf(IP_spoofing_server_port2) + " -M " + counter + " -a " + newLocalIP + " &";
				DataOutputStream os = new DataOutputStream(rootProcess.getOutputStream());
				Log.v("LOG", "################################# hping cmd: " + cmd);
				os.writeBytes(cmd + "\n");
				os.flush();
			}
			if(ip_spoofing_allowed == 0)
			{
				String result = null;
				try{
					result = in.readLine();
				}
				catch (IOException e) {
					e.printStackTrace();
				}
				if(result != null)
				{
					if(result.equals("succ"))
					{
						ip_spoofing_allowed = 1;
					}
					else
					{
						ip_spoofing_allowed = 0;
					}
				}
			}

		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		//String output = "network failure occured: " + network_failure + ", ip spoofing allowed: " + String.valueOf(ip_spoofing_allowed);

		if(ip_spoofing_allowed == 1)
		{
			message+="Yes, the network firewall is a little weak";
		}
		else if(ip_spoofing_allowed == 0 && network_failure == 0)
		{
			message+="No, the network firewall is secure";
		}
		else if(ip_spoofing_allowed == 0 && network_failure == 1)
		{
			message+="Error in test";
		}


		// Check if the Service_Thread is running
		if(service.isRunning())	{
			service.addResultAndUpdateUI(message, -1);
			//Utilities.write_to_file(Service_Thread.LOG_FILE_NAME,Context.MODE_APPEND, message+"\n", service);
		}else{
			// Write to UMLogger_last.txt so we can actually see the result later on
			//Utilities.write_to_file(Service_Thread.LOG_FILE_NAME,Context.MODE_APPEND, message+"\n", service);
			//Utilities.backupLogFile(service);
		}
		Log.v("3gtest_spoof", "result message:\n " + message);
	}

}