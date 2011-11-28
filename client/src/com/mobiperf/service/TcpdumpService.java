package com.mobiperf.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.mobiperf.InformationCenter;
import com.mobiperf.Tcpdump;

public class TcpdumpService extends Service {

	private static boolean started = false;
	public class Checker extends TimerTask {
		  public void run() {
			  Log.v("Mobiperf", "inside checker");
				// Executes the command.
				Process process;
				boolean found = false;
				
				try {
					process = Runtime.getRuntime().exec("ps");
					BufferedReader reader = new BufferedReader(new InputStreamReader(
							process.getInputStream()));
					String line;
					
					while ((line = reader.readLine()) != null) {
						//System.out.println(line);
						if(line.indexOf("tcpdump") != -1)
						{
							found = true;
							break;
						}
					}		
					reader.close();
					process.waitFor();
					
				} catch (IOException e1) {		
					e1.printStackTrace();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				// if tcpdump is down
				if (!found)
					Tcpdump.start_client();
				// timer.schedule(new Worker(), new Date(System.currentTimeMillis()
				// + 4 * 60 * 60 * 1000));
				timer2.schedule(new Checker(), new Date(
						System.currentTimeMillis() + 10 * 1000));
				Log.v("Mobiperf", "outside checker");
		  }  
	}
	public class Worker extends TimerTask {
		  public void run() {
			  
			  Log.v("MobiPerf", "inside tcpdump service : network type " + InformationCenter.getNetworkTypeName());
			  Tcpdump.terminate_client();
			  
			  //if current network is WiFi, upload trace
			  if(InformationCenter.getNetworkTypeName().equals("WIFI")){
				  //upload traces, delete old files
				  //scan the whole directory and upload all files, in upload delete old file
				  Tcpdump.upload();

			  }
			  
			  Tcpdump.start_client();
			  //timer.schedule(new Worker(), new Date(System.currentTimeMillis() + 4 * 60 * 60 * 1000));
			  timer1.schedule(new Worker(), new Date(System.currentTimeMillis() + 30 * 60 * 1000));
			  Log.v("Mobiperf", "out side worker");
			  
		  }
		}
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	static private Timer timer1, timer2;
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (started)
			return START_STICKY;
		started = true;
		Log.v("Mobiperf", "service started");
		timer1 = new Timer(false);
	    timer1.schedule(new Worker(), new Date(System.currentTimeMillis() +  1000));
	  
		timer2 = new Timer(false);
	    timer2.schedule(new Checker(), new Date(System.currentTimeMillis() + 10 * 1000));
		return START_STICKY;
	}

	@Override
	public void onDestroy() {
		Log.v("Mobiperf", "TCPdump service destroyed");
		started = false;
		super.onDestroy();
		timer1.cancel();
		timer2.cancel();
		Tcpdump.terminate_client();
	}
	
	
	


}
