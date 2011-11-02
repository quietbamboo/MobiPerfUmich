package com.mobiperf.service;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import com.mobiperf.InformationCenter;
import com.mobiperf.Tcpdump;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class TcpdumpService extends Service {

	public class Worker extends TimerTask {
		  public void run() {
			  
			  Tcpdump.terminate_client();
			  
			  //if current network is WiFi, upload trace
			  if(InformationCenter.getNetworkTypeName().equals("WIFI")){
				  //upload traces, delete old files

				  //scan the whole directory and upload all files, in upload delete old file
				  Tcpdump.upload();

			  }
			  
			  Tcpdump.start_client();
			  
			  Timer timer = new Timer(false);
			  //timer.schedule(new Worker(), new Date(System.currentTimeMillis() + 4 * 60 * 60 * 1000));
			  timer.schedule(new Worker(), new Date(System.currentTimeMillis() + 10 * 1000));
		  }
		}
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Timer timer = new Timer(false);
	    timer.schedule(new Worker(), new Date(System.currentTimeMillis() + 1000));
		return START_STICKY;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		 Tcpdump.terminate_client();
	}
	
	
	


}
