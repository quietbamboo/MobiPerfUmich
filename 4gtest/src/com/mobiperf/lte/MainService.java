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

package com.mobiperf.lte;

import java.util.ArrayList;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;


public class MainService extends Service {

    /**
     * Class for clients to access.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with
     * IPC.
     */
    public class Threegtest_Binder extends Binder {
        MainService getService() {
            return MainService.this;
        }
    }

    // This is the object that receives interactions from clients.  See
    // RemoteService for a more complete example.///
    private final IBinder mBinder = new Threegtest_Binder();
    
    //public static Handler mHandler = new Handler();
    private Main activity;
    public MainThread testThread; // The acutual thread running tests
    // Maintain a result list which is filled up in the background
    // Next time the activity bind to this service, it can retrieve those results
    public ArrayList<String> resultList; 
    public String currentTest;
    public boolean isRoot;
    
    @Override
    public void onCreate() {
    	Log.v("MobiPerf", "Threegtest_Service, onCreate()");
    	resultList = new ArrayList<String>();
		Log.v("MobiPerf", "Threegtest_Service, onCreate() finished");
    }
    
    @Override
    public void onStart(Intent intent, int startId)
    {
    	//Log.v("MobiPerf", "ThreegtestService onStart");
    	// intent would be null if the system try to restart the service
    	// after the main process has been killed by task killer...
    	if(intent != null && intent.getAction() != null && intent.getAction().equals("run"))
    	{
    		//refresh GPS information
			GPS.location(this);

    		resultList.clear();
	        testThread = new MainThread(this);
	        testThread.start();
	        updateUI();
	        Log.i("MobiPerf", "Service thread starts");
	        isRoot = (Utilities.checkRootPrivilege());
	        
	        Thread t = new Thread()
	        {
	        	public void run(){
	        		try {
						testThread.join();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					//execute after the testThread has returned
					//copy UMLogger.txt to sdcard and UMLogger_last.txt
					//Utilities.backupLogFile(ThreegtestService.this);
					updateUI();
	        	}
	        };
	        t.start();
    	}
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        // We want this service to continue running until it is explicitly
        // stopped, so return sticky.
    	onStart(intent, startId);
        return 1;
    }

    @Override
    public void onDestroy() {
    	Log.v("MobiPerf", "ThreegtestService, onDestroy()");
    }

    @Override
    public IBinder onBind(Intent intent) {
    	Log.v("MobiPerf", "return mBinder");
        return mBinder;
    }

    public void setActivity(Main a)
    {
    	this.activity = a;
    }
    
    public boolean isRunning(){
    	if(testThread != null)
    		return testThread.isAlive();
    	return false;
    }
    
    public void updateChart(double[] rtt, double[] tp_down, double[] tp_up){
    	if(activity != null)
    		activity.updateChart(rtt, tp_down, tp_up);
    }

    /**
     * 
     * @param s
     * @param progress -1 if not wishing to change progress
     */
    public void addResultAndUpdateUI(String s, int progress){
    	this.resultList.add(s);
    	updateListView(this.resultList);
    	if(progress > 0)
    		updateProgress(progress);
    }
    
	/*********************************************/
    /*
     * Call exact the same functions in the activity
     */
    public void updateProgress(int val){
    	if(activity != null)
    		activity.updateProgress(val);
    }
    
    public void updateTextView(final String text){
    	this.currentTest=text;
    	if(activity != null)
    		activity.updateTextView(text);
    }
    
    public void updateButton(final String text){
    	if(activity != null)
    		activity.updateButton(text);
    }
    
    public void updateListView(ArrayList<String> list){
    	if(activity != null)
    		activity.updateListView(list);
    }
    public void updateUI()
    {
    	if(activity != null)
    		activity.updateUI();
    }
    

	/*********************************************/
}