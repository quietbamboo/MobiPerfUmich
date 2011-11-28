package com.mobiperf.service;

import com.mobiperf.InformationCenter;
import com.mobiperf.Utilities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
public class StartupReceiver extends BroadcastReceiver {

	static Thread serviceSwitchThread;

	@Override
	public void onReceive(Context context, Intent intent) {

		SharedPreferences settings = context.getSharedPreferences(
				"pref", 0);
		//if the user doesn't allow us to upload
		if(!settings.getBoolean("canUpload",
				false))
			return;
		Log.v("Mobiperf", "receive boot intent");
		InformationCenter.init(context);
		InformationCenter.reset();
		Thread binaryThread = Utilities.installBinaries(context);
		/**** wait till all the binaries are installed ****/
		try {
			binaryThread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		Intent serviceIntent = new Intent(context, TcpdumpService.class);
		context.startService(serviceIntent);
	
	}
}
