package com.mobiperf.lte;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class Periodic extends BroadcastReceiver {
	
	public void onReceive(Context context, Intent intent) {
		Log.v("4gtest", "Periodic onReceive");
		//Toast.makeText(context, "RECEIVED!", Toast.LENGTH_LONG).show();
		Intent svc = new Intent(context, MainServicePeriodic.class);
		ComponentName cn = context.startService(svc);
	}
}
