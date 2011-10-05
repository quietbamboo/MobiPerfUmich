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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.mobiperf.ui.Preferences;


public class Reregister extends BroadcastReceiver {
    //private Threegtest_Service mBoundService = new Threegtest_Service();
	//int REQUEST_CODE = threegtest.REQUEST_CODE;
    //long interval = threegtest.INTERVAL;
	

	@Override
	public void onReceive(Context context, Intent intent) {
		if(Preferences.isAllowedPeriodicalRun(context))
			Preferences.enablePeriodicalRun(context);
		/******Comment out by Gary ****************/
		/*
        Intent intentp = new Intent(context, Periodic.class);
    	PendingIntent pendingIntent = PendingIntent.getBroadcast(context, REQUEST_CODE, intentp, 0);
		AlarmManager alarmManager = (AlarmManager) context.getSystemService(context.ALARM_SERVICE);
    	alarmManager.cancel(pendingIntent);
    	// Start running 1 min after the phone boots //by Gary
    	alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 60*1000 , interval, pendingIntent);
	*/
	}
	
}


