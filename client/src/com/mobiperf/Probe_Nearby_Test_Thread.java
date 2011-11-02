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
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Random;

import com.mobiperf.service.MainService;

import android.util.Log;

public class Probe_Nearby_Test_Thread extends Thread {
	private String probe_nearby_server;
	private String SERVER = "qxu.selfip.org";
	private int probe_nearby_server_port = 9970;
	
	
	private Hashtable<Byte, Integer> lastOctToExisting = new Hashtable<Byte, Integer>(); //192.168.1.X
	private Hashtable<Byte, Integer> secondlastOctToExisting = new Hashtable<Byte, Integer>(); //192.168.X.Y
	
	private String carrier;
	private String networkType;
	private String gps;
	private String country;
	private String city;
	private String zipcode;
	
	
	Random random = new Random();
	
	
	public int ip_spoofing_allowed = 0;
	public int network_failure = 1;
	
	
	private MainService service;
	private String message;
	
	
	private StringBuilder result = new StringBuilder();
	
	
	public Probe_Nearby_Test_Thread(MainService service)
	{
		this.service = service;
		carrier = InformationCenter.getNetworkOperator();
		networkType = InformationCenter.getNetworkTypeName();//"WIFI" "MOBILE" "mobile"
		gps = GPS.latitude +"," + GPS.longitude;
		country = Utilities.getCountry(service);
		city=Utilities.getCity(service);
		zipcode=Utilities.getZipcode(service);
		message = "Firewall allows IP spoofing?: ";

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
    	byte[] new_ip_octs = (byte[])ip_octs.clone();
    	lastOctToExisting.put(ip_octs[3],1);
    	do
    	{
    		int randOct = random.nextInt(254) + 1;
    		byte randByte = (byte)randOct;
    		//Log.v("LOG", "(((((((((((((((((((((((((((((((((((((((((((((( randOct: " + String.valueOf(randOct));
    		if(lastOctToExisting.get(randByte) == null)
    		{
    			new_ip_octs[3] = randByte;
    			lastOctToExisting.put(randByte,1);
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
    		byte randByte = (byte)randOct;
    		if(secondlastOctToExisting.get(randByte) == null)
    		{
    			new_ip_octs[2] = randByte;
    			int randOct2 = random.nextInt(254) + 1;
    			new_ip_octs[3] = (byte)randOct2;
    			secondlastOctToExisting.put(randByte,1);
    			break;
    		}
    	}
    	while(true);
    	return new_ip_octs;
    }
    
    public void connectToSpoofingServer()
    {
    		try {
    			probe_nearby_server = InetAddress.getByName(SERVER).getHostAddress();
    		} catch (UnknownHostException e1) {
    			// TODO Auto-generated catch block
    			e1.printStackTrace();
    			Log.v("3gtest_spoof", "firewall test server " + probe_nearby_server);
    			return;
    		}
    		
	    	Socket s;
			try {
				s = new Socket(probe_nearby_server, probe_nearby_server_port);
				s.setSoTimeout(20000);
				Log.v("LOG", "22222222222222222222222222222222222222222222222222222222222222222222222222222222222222");
				
				
				//ObjectOutputStream out = new ObjectOutputStream(s.getOutputStream());
				PrintWriter out = new PrintWriter(s.getOutputStream(), true);
				
				out.flush();
				Log.v("LOG", "22222222------------------------------------333322222222222222222222222222222222222222222222222222222222222222222222");
				BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
				Log.v("LOG", "2222222222222222222222222555555555555555555------------444444444444444444422222222222222222222222222222222222");
				final InetAddress localIP = s.getLocalAddress();
				Log.v("LOG", "33333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333");
				String localIPstr = localIP.getHostAddress();
				
				Log.v("LOG", "444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444");
				Log.v("LOG", "local IP string: " + localIPstr);
				
				String all = carrier + "|" + InformationCenter.getDeviceID() + "|" + localIPstr + "|" + InformationCenter.getRunId() + "|" + networkType+"|"+gps + "|"+country+"|"+city+"|"+zipcode+"\n";
				Log.v("LOG", "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! sent " + all);
				
				out.write(all);
				out.flush();
				
				List<Thread> probeThreads = new ArrayList<Thread>();
				
				//spoofing 10 /24 IPs
				for(int i = 0; i < 20; ++i)
				{
					byte[] new_ip_oct = changeIPAddrLastOct(localIP.getAddress());
					String newLocalIP = InetAddress.getByAddress(new_ip_oct).getHostAddress();
					//newLocalIP = "192.168.1.131";
					ProbeThread probeThread = new ProbeThread(result, newLocalIP);
					probeThreads.add(probeThread);
					probeThread.start();
				}
				
				//spoofing 10 /16 IPs
				for(int i = 0; i < 20; ++i)
				{
					byte[] new_ip_oct = changeIPAddrSecondLastOct(localIP.getAddress());
					String newLocalIP = InetAddress.getByAddress(new_ip_oct).getHostAddress();
					ProbeThread probeThread = new ProbeThread(result, newLocalIP);
					probeThreads.add(probeThread);
					probeThread.start();
				}
				
				
				
				while(true)
				{
					boolean finished = true;
					for(int i = 0; i < probeThreads.size(); ++i)
					{
						ProbeThread probeThread = (ProbeThread) probeThreads.get(i);
						if(probeThread.finished == false)
						{
							finished = false;
							break;
						}
					}
					if(finished)
					{
						break;
					}
					else
					{
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
				
				String final_result = result.toString();
				//if(final_result.charAt(final_result.length()-1) == '\t')
				/*{
					Log.v("LOG", "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! last char is tabular");
					final_result.trim();
				}*/
				out.write(final_result + "\n");
				Log.v("LOG", "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! final result" + final_result);
				out.flush();
				out.close();
			}
			catch(IOException e)
			{
				e.printStackTrace();
			}
    }

    
    class ProbeThread extends Thread {
    	boolean finished = false;
    	StringBuilder result = null;
    	String newLocalIP = null;
    	
    	public ProbeThread(StringBuilder result, String newLocalIP) {
    		this.result = result;
    		this.newLocalIP = newLocalIP;
    	}
    	
    	public void run() {
			boolean succ = false;
			try {
				Socket socket = new Socket();
				SocketAddress sockaddr = new InetSocketAddress(newLocalIP, 8080);
				socket.connect(sockaddr, 6000);
				succ = true;
			} catch (UnknownHostException e) {
				e.printStackTrace();
			} 
			catch(ConnectException e)
			{
				succ = true;
				e.printStackTrace();
			}
			catch (SocketTimeoutException e) {
				succ = false;
				e.printStackTrace();
			}
			catch (IOException e) {
				e.printStackTrace();
			}
			
			synchronized(result)
			{
				if(succ == true)
				{
					result.append(newLocalIP + ":succ\t");
				}
				else
				{
					result.append(newLocalIP + ":fail\t");
				}
			}
			finished = true;
    	}
    }
}