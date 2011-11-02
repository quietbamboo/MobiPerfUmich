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

package com.mobiperf.service;
import com.mobiperf.MainThreadPeriodic;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;


public class MainServicePeriodic extends Service {

    /**
     * Class for clients to access.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with
     * IPC.
     */
    public class LocalBinder extends Binder {
        MainServicePeriodic getService() {
            return MainServicePeriodic.this;
        }
    }

    @Override
    public void onCreate() {
    	//Log.v("LOG", "Threegtest_Service_Small.onCreate() called!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
    }

    @Override
    public void onStart(Intent intent, int startId) {
    	//Log.v("LOG", "onStart() gets called!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
    	if(intent != null)
    	{
    		//TODO: do we need to initiate GPS and connectivityTester again here??
    		new MainThreadPeriodic(this).start();
    	}
    }
    
    public static Handler mHandler = new Handler();

   
    public int onStartCommand(Intent intent, int flags, int startId) {
    	//Log.v("LOG", "onStartCommand() gets called !!!!!!!!!");
    	onStart(intent, startId);
    	// We want this service to continue running until it is explicitly
        // stopped, so return sticky.
        return 1;
    }

    @Override
    public void onDestroy() {
    	//Log.v("LOG", "Threegtest_Service_Small.onDestroy() gets called !!!!!!!!!");
        
    }

    @Override
    public IBinder onBind(Intent intent) {
    	//Log.v("LOG", "Threegtest_Service_Small.onBind() gets called !!!!!!!!!");
        return mBinder;
    }

    // This is the object that receives interactions from clients.  See
    // RemoteService for a more complete example.
    private final IBinder mBinder = new LocalBinder();

	
}
