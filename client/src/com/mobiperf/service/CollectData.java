package com.mobiperf.service;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import com.mobiperf.Tcpdump;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class CollectData extends Service {

	public class Worker extends TimerTask {
		  public void run() {
			  Tcpdump.terminate_client();
			  Tcpdump.start_client();
			  Timer timer = new Timer(false);
			  timer.schedule(new Worker(), new Date(System.currentTimeMillis() + 60 * 1000));
		  }
		}
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
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
		// TODO Auto-generated method stub
		super.onDestroy();
		 Tcpdump.terminate_client();
	}
	
	
	


}
