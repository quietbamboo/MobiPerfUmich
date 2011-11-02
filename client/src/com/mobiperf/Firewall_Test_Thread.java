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
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import com.mobiperf.service.MainService;

import android.util.Log;

public class Firewall_Test_Thread extends Thread {
	
	String SERVER = "qxu.selfip.org";
	String firewall_test_server;
	int firewall_test_server_port = 9980;
	int firewall_test_server_port2 = 9981;
	
	boolean network_failure = false;
	
	//String iptable_binary = "phone_iptables_drop_packet";
	public static final String PIDP = NAT_Test_Thread.PATH + "phone_iptables_drop_packet";
	String carrier;
	String networkType;
	private MainService service;
    public Firewall_Test_Thread(MainService s){
    	this.service = s;
		carrier = InformationCenter.getNetworkOperator();
		networkType = InformationCenter.getNetworkTypeName();//"WIFI" "MOBILE" "mobile"
    }
    @Override
    public void run() {  
   
    	Log.v("MobiPerf_firewall", "firewall test start");
		try {
			firewall_test_server = InetAddress.getByName(SERVER).getHostAddress();
		} catch (UnknownHostException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			Log.v("MobiPerf_firewall", "firewall test server " + firewall_test_server);
			return;
		}
		// server side always run
		Thread t1 = new Thread(){public void run(){run_server_side_buffer_test();}};
		t1.start();
		
		// client side requires iptables
		if(Utilities.testIptablesAvailability(service))
		{
			Log.v("MobiPerf_firewall", "after testing iptables availability");
	        
			Thread t2 = new Thread(){public void run(){run_client_side_buffer_test();}};			
			t2.start();			
			try {
				t2.join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
					
			Log.v("MobiPerf_firewall", "both directions finished!");
		}
		try {
			t1.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
    }
    
    void run_server_side_buffer_test()
    {
    	Socket s = null;
    	try {
			s = new Socket(firewall_test_server, firewall_test_server_port2);
			s.setSoTimeout(30000);
			Log.v("LOG", "connection to firewall server established!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
			//s.setSoTimeout(15000);
			PrintWriter out = new PrintWriter(s.getOutputStream(), true);
			
			String info = carrier + "|" + InformationCenter.getDeviceID() + "|" + s.getLocalAddress().getHostAddress()+ "|" + InformationCenter.getRunId() + "|" + networkType;
			Log.v("LOG", "write info " + info + " !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
			out.write(info + "\n");
			out.flush();
			
			BufferedReader in = new BufferedReader(new InputStreamReader(
                    s.getInputStream()));
			char[] buffer = new char[1000];
			
			int n = in.read(buffer, 0, 1000);
			Log.v("LOG", "read " + n + "bytes");
			Thread.sleep(1000);
			
			s.close();
    	}
    	catch (UnknownHostException e) {
			network_failure = true;
			e.printStackTrace();
			return;
		} catch (IOException e) {
			network_failure = true;
			e.printStackTrace();
			return;
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    void run_client_side_buffer_test()
    {
    	File dir = service.getFilesDir();
		
		try {
			Process rootProcess = Runtime.getRuntime().exec("su");
			DataOutputStream os = new DataOutputStream(rootProcess.getOutputStream());
			os.writeBytes("iptables -F\n");
			String cmd = "iptables -A OUTPUT -p tcp -d " + firewall_test_server + " --dport " + firewall_test_server_port + " -j NFQUEUE --queue-num 3"+ "\n";
			Log.v("LOG", "****************************** " + cmd);
			os.writeBytes(cmd);
			
			//change permission to 777
			//cmd = "chmod 777 " + dir.getAbsolutePath()+ "/" + iptable_binary + "\n";
			//Log.v("LOG", "****************************** " + cmd);				
			//os.writeBytes(cmd);
			
			cmd = PIDP + " &\n";
			Log.v("LOG", "****************************** " + cmd);				
			os.writeBytes(cmd);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		  
    	
    	Socket s = null;
    	try {
			s = new Socket(firewall_test_server, firewall_test_server_port);
			
			Log.v("LOG", "connection to firewall server established!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
			//s.setSoTimeout(15000);
			PrintWriter out = new PrintWriter(s.getOutputStream(), true);
			
			String info = carrier + "|" + InformationCenter.getDeviceID() + "|" + s.getInetAddress().getHostAddress()+ "|" + InformationCenter.getRunId() + "|" + networkType;
			Log.v("LOG", "write info " + info + " !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
			out.write(info + "\n");
			out.flush();
			
			/*try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}*/
			
			//100 bytes of data
			String test = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";
			for(int i = 0; i < 20; ++i)
			{
				Log.v("LOG", "write " + test + " !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
				out.write(test);
			}
			out.flush();
			
			Thread.sleep(25);
			s.close();
			
		} catch (UnknownHostException e) {
			network_failure = true;
			e.printStackTrace();
			return;
		} catch (IOException e) {
			network_failure = true;
			e.printStackTrace();
			return;
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

    }
	
}