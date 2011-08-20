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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.content.Context;
import android.util.Log;

//Junxian: commented out for now, causing app to crash
/*
public class Promotion_Delay_Test {
	public static final String promotion_serverIP = "falconeecs.dyndns.org";
	public static final int promotion_serverPort = 8888;
	//public static final int promotion_from_phone_Port = 8888;
	public static final int promotion_from_phone_report_port = 8887;	//this is a new port number specifically used to replace the old promotion_from_phone_Port(8888) port
	public static final int promotion_from_server_Port = 8889;
	
	public static SocketAddress promotion_delay_Addr = null; 
	public static SocketAddress promotion_delay_from_phone_Addr = null; 
    
	public static final String RTT_HISTORY_FILE = "history.txt";  
	
	private static int MAX_LINE = 20;
	
	public static void run_promotion_delay_from_phone(String carrier, String deviceID, Context context)
	{
		Log.v("LOG", "!!!!!!!!!!!!!!!!!!!!!!!!!!! promotion state started !!!!!!!!!!!!!!!!!!!!!!");
		//promotion_delay_Addr = new InetSocketAddress( promotion_serverIP, promotion_from_phone_Port );
		promotion_delay_from_phone_Addr = new InetSocketAddress("android.clients.google.com", 443);
		ArrayList<ArrayList<Long>> lists = new ArrayList<ArrayList<Long>>();
		
		for(int i = 0; i < 3; ++i)
		{
			try {
				Thread.sleep(30000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			ArrayList<Long> list = promotion_delay_from_the_phone(carrier, deviceID);
			if(list != null)
			{
				lists.add(list);
			}
		}
		if(lists.size() > 0)
		{
			write_delay_to_file(lists, context);
			send_promotion_delay_from_phone_to_server(carrier, deviceID, lists);
		}
	}
	
	public static void run_promotion_delay_from_server(String carrier, String deviceID, Context context) 
	{
		//promotion_delay_from_phone_Addr = new InetSocketAddress("falconeecs.dyndns.org", 23);
		promotion_delay_from_the_server(carrier, deviceID);
		
	}
	
	public static void start(String carrier, String deviceID, Context context) 
	{
		run_promotion_delay_from_phone(carrier, deviceID, context);
		run_promotion_delay_from_server(carrier, deviceID, context);
	}
	
	private static void write_delay_to_file(ArrayList<ArrayList<Long>> lists, Context context) {
		Date todaysDate = new java.util.Date();
    	SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd,HH:mm:ss");
    	String formattedDate = formatter.format(todaysDate);
    	
    	FileOutputStream fOut1 = null;
        OutputStreamWriter out1 = null;
        
        String delayList = "";
        for(int i = 0; i < lists.size(); ++i)
        {
        	ArrayList<Long> list = lists.get(i);
        	for(int j = 0; j < list.size(); ++j)
        	{
        		long pm = list.get(j);
        		if(i == lists.size() - 1 && j == list.size() - 1)
            	{
            		delayList += String.valueOf(pm);
            	}
            	else
            	{
            		delayList += (String.valueOf(pm) + "\t");
            	}
        	}
        }
        
        Log.v("LOG", "^^^^^^^^^^ delayList: " + delayList);
        try {
        	//check existing files
        	trimFile(RTT_HISTORY_FILE, context);
    		
			fOut1 = context.openFileOutput(RTT_HISTORY_FILE, Context.MODE_APPEND);
			out1 = new OutputStreamWriter( fOut1 );
	        out1.write( formattedDate + "\t" +  delayList + "\n");
	        Log.v("LOG", "write successful!!!!!!!!");
	        out1.close();
	        fOut1.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
	}


	private static void trimFile(String filename, Context context) {
		ArrayList<String> lineList = new ArrayList<String>();
		try {
			BufferedReader bufferedReader = new BufferedReader (new InputStreamReader (context.openFileInput(filename)));
			
			String line = bufferedReader.readLine();
			while(line != null)
			{
				lineList.add(line);
				line = bufferedReader.readLine();
			}
			
			
			bufferedReader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Log.v("LOG", "-------------- Current log entry count: " + lineList.size());
		int margin = lineList.size() - MAX_LINE;
		Log.v("LOG", "margin: " + margin);
		if(margin > 0)
		{
			for(int i = 0; i < margin; ++i)
			{
				lineList.remove(0);
			}
			
			try {
				FileOutputStream fOut1 = null;
		        OutputStreamWriter out1 = null;
		        
	        	fOut1 = context.openFileOutput(filename, Context.MODE_PRIVATE);
				out1 = new OutputStreamWriter( fOut1 );
		        for(int i = 0; i < lineList.size(); ++i)
		        {
		        	String line = lineList.get(i);
		        	out1.write(line + "\n");
		        }
				
		        out1.close();
		        fOut1.close();
			
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
        
	}


	private static void send_promotion_delay_from_phone_to_server(String carrier, String deviceID, ArrayList<ArrayList<Long>> lists) {
		Socket remoteTCPSocket = new Socket();
		SocketAddress remoteAddr = new InetSocketAddress( promotion_serverIP, promotion_from_phone_report_port);
		try {
			remoteTCPSocket.connect( remoteAddr, 10000 );
			DataOutputStream remoteOutputStream = new DataOutputStream( remoteTCPSocket.getOutputStream() );
	        String output = carrier + "\t" + deviceID + "\t" + "\n";
	        for(int i = 0; i < lists.size(); ++i)
	        {
	        	ArrayList<Long> list = lists.get(i);
	        	boolean isbreaked = false;
	        	for(int j = 0; j < list.size(); ++j)
	        	{
	        		if(j == 0)
	        		{
	        			long pm = list.get(j);
	        			if(pm < 1000)
	        			{
	        				//isbreaked = true;
	        				//break;
	        			}
	        			output += "***********delay:\t" + list.get(j);
	        		}
	        		else
	        		{
	        			output += ("\t" + list.get(j));
	        		}
	        	}
	        	if(isbreaked == false)
	        	{
	        		output += "\n";
	        	}
	        }
	        remoteOutputStream.writeBytes(output);
	        remoteTCPSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}


	private static void promotion_delay_from_the_server(String carrier, String deviceID)
	{
		Socket remoteTCPSocket = new Socket();
		try {
			remoteTCPSocket.setKeepAlive(false);
			//remoteTCPSocket.setSoTimeout(0);
			remoteTCPSocket.setSoTimeout(10000);
			SocketAddress remoteAddr = new InetSocketAddress( promotion_serverIP, promotion_from_server_Port);
			remoteTCPSocket.connect( remoteAddr, 10000 );
			DataOutputStream remoteOutputStream = new DataOutputStream( remoteTCPSocket.getOutputStream() );
	        remoteOutputStream.writeBytes(carrier + "\t" + deviceID);

	        //in the release, we should sleep for half an hour so that we have more time to do probing from the server...
	        //Thread.sleep(1800*1000);
	        
	      
			//remoteTCPSocket.close();
	        
		} catch (SocketException e) {
			e.printStackTrace();
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
        
        
	}
	

	private static ArrayList<Long> promotion_delay_from_the_phone(String carrier, String deviceID)
	{
		ArrayList<Long> list = new ArrayList<Long>();
		for(int i = 0; i < 6; ++i)
		{
			try
			{
				Socket remoteTCPSocket = new Socket();
				remoteTCPSocket.setKeepAlive(false);
				remoteTCPSocket.setSoTimeout(10000);
		        long start = System.currentTimeMillis();
		        remoteTCPSocket.connect( promotion_delay_from_phone_Addr, 6000 );
		        long end = System.currentTimeMillis();
		        long diff = end-start;
		        remoteTCPSocket.close();
		        
		        list.add(diff);
		        
		        //DataOutputStream remoteOutputStream = new DataOutputStream( remoteTCPSocket.getOutputStream() );
		        //String output = carrier + "\t" + deviceID + "\t" + i + "\t" + diff;
		        //Log.v("LOG", "in promotion_delay_from_the_phone, sent: " + output);
		        //remoteOutputStream.writeBytes(output);
		        
		        
		        
		        //Thread.sleep(20);
	        
			}
			catch(IOException e)
			{
				//Log.v("LOG", "error in connecting to promotion server: " + e);
				if(i == 0)
				{
					return null;
				}
			} 
			
		}
		return list;
	}
	
	
	
	/* 
	Deprecated
	private static ArrayList<Long> promotion_delay_from_the_phone(String carrier, String deviceID)
	{
		for(int i = 0; i < 6; ++i)
		{
			try
			{
				Socket remoteTCPSocket = new Socket();
				remoteTCPSocket.setKeepAlive(false);
				remoteTCPSocket.setSoTimeout(10000);
		        long start = System.currentTimeMillis();
		        remoteTCPSocket.connect( promotion_delay_from_phone_Addr, 6000 );
		        long end = System.currentTimeMillis();
		        long diff = end-start;
		        
		        
		        DataOutputStream remoteOutputStream = new DataOutputStream( remoteTCPSocket.getOutputStream() );
		        String output = carrier + "\t" + deviceID + "\t" + i + "\t" + diff;
		        //Log.v("LOG", "in promotion_delay_from_the_phone, sent: " + output);
		        remoteOutputStream.writeBytes(output);
		        
		        remoteTCPSocket.close();
		    
			}
			catch(IOException e)
			{
				//Log.v("LOG", "error in connecting to promotion server: " + e);
				if(i == 0)
				{
					return -1;
				}
			} 
			
		}
		return 0;
	}//* /
 	
}*/
