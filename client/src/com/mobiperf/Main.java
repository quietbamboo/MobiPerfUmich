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
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.mobiperf.ui.Display;
import com.mobiperf.ui.Preferences;



public class Main extends Activity {
	 
    TextView t1, t2, t3;
    //--------------------cc
    String test;
    static boolean stopFlag = false;
    ProgressBar mProgress;
    public ListView listView; 
    ArrayAdapter<String> adapter;
    Button button;
    //Button button1,button2;
    Button button_tab_view;
	// Need handler for callbacks to the UI thread
    static Handler mHandler = new Handler();
    

    public void onStart() {
    	
    	Log.w("MobiPerf", "threegtest start begin");
    	
    	super.onStart();
    	
    	InformationCenter.init(this);
    	
    	doBindService();
    	
    	//TODO Move GPS into InformationCenter
    	GPS.location(this);
    	
    	stopFlag = false;
    	//start = System.currentTimeMillis();
    	
        // Show first time run warning
        //firstTimeRun();
        
        
        
    	// Setup periodical run
    	if(Preferences.isAllowedPeriodicalRun(this))
    		Preferences.enablePeriodicalRun(this);
        //long install = System.currentTimeMillis() - start;
        //toastMessage("install "+ install);
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        doUnbindService();
        Log.v("MobiPerf", "threegtest onDestroy");
    }

    @Override
    public void onCreate( Bundle savedInstanceState ) {
    	long start = System.currentTimeMillis();
    	Log.v("MobiPerf", "threegtest create begins");
        
    	super.onCreate( savedInstanceState );
    	
        //create user interface
        setContentView( R.layout.main1 );
    	
        button = ( Button ) findViewById( R.id.Button01 );
        //TODO: button added to be test----cc
        button_tab_view = ( Button ) findViewById( R.id.button_tab_view );
        //button2 = ( Button ) findViewById( R.id.button2 );
        t1 = ( TextView ) findViewById( R.id.textview1 );
        t2 = ( TextView ) findViewById( R.id.textview2 );
        t3 = ( TextView ) findViewById( R.id.textview3 );
        mProgress = ( ProgressBar ) findViewById( R.id.progress_bar );

        t1.setHintTextColor( 2 );
        t2.setHintTextColor( 2 );
        t3.setHintTextColor( 2 );
        
        updateProgress(0);
        
        listView = ( ListView ) findViewById( R.id.list );
        //adapter = new ArrayAdapter<String>( this, R.layout.simple_list_item_1 );
        //play around with listview - mhayter
        //listView.setAdapter( adapter );
        //adapter.setNotifyOnChange(false);
        
        updateUI();

        button.setOnClickListener( 
        		new View.OnClickListener() {
                   public void onClick( final View view ) {
                       //Utilities.checkifrunning(context);

                       if (isRunning())	// Stop service 
                       {
                    	   stopFlag = true;
                           updateTextView3("The program is trying to stop...");
                           updateButton("Please wait");
                           button.setClickable(false);
                       }
                       else {
                    	   stopFlag=false;
                    	   updateTextView3("Starting tests...");
                           updateButton("Please wait");
                           button.setClickable(false);
                           
                    	   updateProgress(0); // clear progress
                    	   updateListView(new ArrayList<String>());// empty listview
                       	   Intent svc = new Intent(getApplicationContext(), MainService.class);
                           svc.setAction("run");
                           startService(svc);	//Start Service_Thread
                       }
                   }
               }
        	);
        
        //TODO:Switch views to a tabhost view--------CC
        button_tab_view.setOnClickListener(
        		new View.OnClickListener() {
        			 public void onClick( final View view ) {
        				// setContentView( R.layout.display1);
        				 Intent i = new Intent(view.getContext(), com.mobiperf.ui.Display.class);
        				 startActivityForResult(i, 0);
        				 
        				 //Tcpdump.start_client();
        			 }
        		}
        );
        button_tab_view.setText("More Details");
        
        

        //displayResult("abc","a ","daadfsdfsfdaadfsdfsfdaadfsdfsfdaadfsdfsfdaadfsdfsfdaadfsdfsfdaadfsdfsfdaadfsdfsfdaadfsdfsfdaadfsdfsfdaadfsdfsfdaadfsdfsfdaadfsdfsfdaadfsdfsfdaadfsdfsfdaadfsdfsfdaadfsdfsfdaadfsdfsfdaadfsdfsf",0);
        //displayResult("abc","a ","daadfsdfsfdaadfsdfsfdaadfsdfsfdaadfsdfsfdaadfsdfsfdaadfsdfsfdaadfsdfsfdaadfsdfsfdaadfsdfsfdaadfsdfsfdaadfsdfsfdaadfsdfsfdaadfsdfsfdaadfsdfsfdaadfsdfsfdaadfsdfsfdaadfsdfsfdaadfsdfsfdaadfsdfsf",0);
        //displayResult("abc","a ","daadfsdfsfdaadfsdfsfdaadfsdfsfdaadfsdfsfdaadfsdfsfdaadfsdfsfdaadfsdfsfdaadfsdfsfdaadfsdfsfdaadfsdfsfdaadfsdfsfdaadfsdfsfdaadfsdfsfdaadfsdfsfdaadfsdfsfdaadfsdfsfdaadfsdfsfdaadfsdfsfdaadfsdfsf",1);
        //displayResult("abc","a ","daadfsdfsfdaadfsdfsfdaadfsdfsfdaadfsdfsfdaadfsdfsfdaadfsdfsfdaadfsdfsfdaadfsdfsfdaadfsdfsfdaadfsdfsfdaadfsdfsfdaadfsdfsfdaadfsdfsfdaadfsdfsfdaadfsdfsfdaadfsdfsfdaadfsdfsfdaadfsdfsfdaadfsdfsf",2);
        //displayResult("abc","a ","daadfsdfsfdaadfsdfsfdaadfsdfsfdaadfsdfsfdaadfsdfsfdaadfsdfsfdaadfsdfsfdaadfsdfsfdaadfsdfsfdaadfsdfsfdaadfsdfsfdaadfsdfsfdaadfsdfsfdaadfsdfsfdaadfsdfsfdaadfsdfsfdaadfsdfsfdaadfsdfsfdaadfsdfsf",2);
               
        /*button2.setOnClickListener(
        		new View.OnClickListener() {
        			 public void onClick( final View view ) {
        				 //setContentView( R.layout.display1);
        				 //Intent i = new Intent(view.getContext(), eecs.umich.threegtest.ui.Display.class);
        				 //startActivityForResult(i, 0);
        				 //temp
        				 
        				 Intent settingsActivity = new Intent(view.getContext(), com.mobiperf.ui.Preferences.class);
        				 startActivityForResult(settingsActivity, 0);
        			 }
        		}
        );
        */


       Log.v("MobiPerf", "create finish in "+ (System.currentTimeMillis() - start));
    }

	/****** methods for updating UI ******/
    public void updateTextView1(final String text)
    {
    	mHandler.post(new Runnable() {
            public void run() {
            	t1.setText(text);
            }
        });
    }
    // update progress bar
    public void updateProgress(final int val)
    {
    	mHandler.post(new Runnable() {
            public void run() {
            	mProgress.setProgress(val);
            	t2.setText(val+"% complete");
            }
        });
    }
    public void updateTextView2(final String text)
    {
    	mHandler.post(new Runnable() {
            public void run() {
            	t2.setText(text);
            }
        });
    }
    public void updateTextView3(final String text)
    {
    	mHandler.post(new Runnable() {
            public void run() {
            	t3.setText(text);
            }
        });
    }
    public void updateButton(final String text)
    {
    	mHandler.post(new Runnable() {
            public void run() {
            	button.setText(text);
            }
        });
    }
    // update result list
    public void updateListView(final ArrayList<String> list)
    { 	
    	mHandler.post(new Runnable() {
            public void run() {
            	listView.setVisibility(View.GONE);           	
            	adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.simple_list_item_1, list);
            	//adapter.setNotifyOnChange(false);
            	listView.setAdapter(adapter);
            	listView.setVisibility(View.VISIBLE);
            }
        });
    }
    // update button and tv3 based on isRunning();
    public void updateUI()
    {
    	if( isRunning() == false) 
    	{
            updateButton("Run");
            button.setClickable(true);
            updateTextView3(Feedback.getMessage(Feedback.TYPE.NEW_TEST, null));
            updateTextView1("Please allow 2~3 minutes to finish all the tests.");
    	}else
        {
        	button.setClickable(true);
            updateButton( "Stop" );
        	updateTextView3("Tests are running.");
            updateTextView1("You may switch back to check results later.");
            //clear 3 tab contents
            Display.clearTabView();
        }
    }
    /*************************************************/

/******************** Menu starts here by Gary ********************/
    // Define menu ids
	protected static final int MENU_PERIODIC = Menu.FIRST;
	//protected static final int MENU_NOTIFICATION = Menu.FIRST + 1;
	//-------------commented by cc---------
	//protected static final int MENU_LAST = Menu.FIRST + 2;
	//protected static final int MENU_HISTORY = Menu.FIRST + 3;
	protected static final int MENU_EMAIL = Menu.FIRST +4;
	//TODO:new menu --------cc
	protected static final int PAST_RECORD = Menu.FIRST +5;
	//protected static final int VOTE = Menu.FIRST +6;
	protected static final int PERF_ME =Menu.FIRST+7;
	// Create menu
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	        super.onCreateOptionsMenu(menu);
	        menu.add(0, MENU_PERIODIC, 0, "Settings");
	       // menu.add(0, MENU_NOTIFICATION, 0, "Notification setting").setEnabled(isPeriodicalRunEnabled(this)?true:false);
	        //menu.add(0, MENU_LAST, 0, "Last run results");
	        //menu.add(0, MENU_HISTORY, 0, "Periodic run results");
	      //TODO:new menu --------cc
	        menu.add(0, PAST_RECORD, 0, "View past record");
	        menu.add(0, MENU_EMAIL, 0, "About us");
	        //menu.add(0, VOTE, 0, "Vote for us");
	        //menu.add(0, PERF_ME, 0, "Performance Near Me");
	        return true;
	}
	@Override
	public boolean onPrepareOptionsMenu(Menu menu)
	{
		
		Log.v("LOG","menu prepare");
		//menu.findItem(MENU_NOTIFICATION).setEnabled(isPeriodicalRunEnabled(this)?true:false);
		return true;
	}
	
	// Deal with menu event
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	        super.onOptionsItemSelected(item);
	        Log.v("menu","onOptionsItemSelected "+item.getItemId());
	        switch (item.getItemId()) {
	        case MENU_PERIODIC:
	        	 	Log.v("menu", "DIALOG_PERIODIC");
	        	 	// Show the setting dialog for periodical running
	        	 	//showDialog(DIALOG_PERIODIC);
	        	 	Intent settingsActivity = new Intent(this, com.mobiperf.ui.Preferences.class);
	        	 	startActivityForResult(settingsActivity, 0);
	        	 	
	                break;
	       /* case MENU_NOTIFICATION:
        	 	Log.v("menu", "DIALOG_NOTIFICATION");
        	 	// Show the setting dialog for periodical running
        	 	showDialog(DIALOG_NOTIFICATION);
                break;
                */
           //--------------commented by cc------------
	        /*case MENU_LAST:
	        	try {
					this.getApplicationContext().openFileInput(Service_Thread.LAST_LOG_FILE_NAME);
					this.startActivity(new Intent(this,LastRunResult.class));
				} catch (FileNotFoundException e1) {
					this.toastMessage("Last run results were not found\nPlease run the tests");
					e1.printStackTrace();
				}
                break;
	        case MENU_HISTORY:
        	 	Log.v("menu", "HISTORY");
        	 	// Show the setting dialog for periodical running
        	 	this.startActivity(new Intent(this,History.class));
                break;
                */
            //TODO:new menu --------cc
	        case PAST_RECORD:
	        	 Intent i = new Intent(this, com.mobiperf.ui.HistoricalList.class);
				 startActivityForResult(i, 0);
				 break;
	        //case VOTE:
	        //	 Intent intent_v= new Intent(this, com.mobiperf.ui.Vote.class);
	        //	 startActivityForResult(intent_v, 0);
	        	
	        	//Intent intent_v= new Intent(this, com.mobiperf.pushNotify.PushActivity.class);
	        	 //startActivityForResult(intent_v, 0);
	        	 //break;
	        case PERF_ME:
	        	 Intent intent_perf= new Intent(this, com.mobiperf.PerfNearMe.Buttons.class);
	        	 startActivityForResult(intent_perf, 0);
				 break;
	        case MENU_EMAIL:
	        	try
	        	{
	        		//--------commented by cc -----------
					/*Intent emailIntent = new Intent(
							android.content.Intent.ACTION_SEND);
	
					String aEmailList[] = { "MobiPerf@umich.edu" };
					// String aEmailCCList[] = {
					// "user3@fakehost.com","user4@fakehost.com"};
					// String aEmailBCCList[] = { "user5@fakehost.com" };
					emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL,
							aEmailList);
					// emailIntent.putExtra(android.content.Intent.EXTRA_CC,
					// aEmailCCList);
					// emailIntent.putExtra(android.content.Intent.EXTRA_BCC,
					// aEmailBCCList);
					emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,
							"Feedback on MobiPerf");
					emailIntent.setType("plain/text");
					// emailIntent.putExtra(android.content.Intent.EXTRA_TEXT,
					// "My message body.");
					startActivity(emailIntent);
					*/
	        		
	        		 Intent in1 = new Intent(this, com.mobiperf.ui.About.class);
					 startActivityForResult(in1, 0);
	        	}
	        	catch(ActivityNotFoundException e)
	        	{
	        		Toast.makeText(getApplicationContext(), "Please send us an email at MobiPerf@umich.edu", Toast.LENGTH_SHORT).show();
	        	}
	        	break;
	        }
	        

	        return true;

	}
	
	/*
    long repeattime;
    @Override
    public boolean onCreateOptionsMenu( Menu menu ) {
        super.onCreateOptionsMenu( menu );
        //menu.add(0, 0, 0,"No repeatitions");
        //menu.add(0, 1, 0,"Repeat every 10 minutes");
        //menu.add(0, 2, 0,"Repeat every 1 hour");
        //menu.add(0, 3, 0,"Repeat every 6 hour");
        //menu.add(0, 4, 0,"Repeat every 12 hour");
        //menu.add(0, 5, 0,"Repeat every 24 hours");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected( MenuItem item ) {
        super.onOptionsItemSelected( item );

        switch ( item.getItemId() ) {
        case 0:
            repeattime = 0;
            break;
        case 1:
            repeattime = 600000;
            break;
        case 2:
            repeattime = 3600000;
            break;
        case 3:
            repeattime = 21600000;
            break;
        case 4:
            repeattime = 43200000;
            break;
        case 5:
            repeattime = 86400000;
            break;
        default:
            break;
        }

        Utilities.checkifrunning(context);
        Utilities.write_to_file("repeatfile.txt", MODE_WORLD_READABLE,"" + repeattime + "\n", context );
        Utilities.checkifrunning(context);

        if ( isRunning ) {
            mHandler.post( new Runnable() {
                               public void run() {
                                   t3.setPadding( 40, 5, 10, 5 );
                                   t3.setText( "App is running, press button to stop" );
                               }
                           }

                         );
        }
        return false;
    }
    */
	

	/******** print something on screen for debugging purpose by Gary ***********/
	private static void debug(Context context, String p){
		Toast.makeText(context, p, Toast.LENGTH_SHORT).show();
	}
	
	public void toastMessage(String s){
    	Toast toast = Toast.makeText(this.getApplicationContext(), s, 2);
    	toast.show();
    }
	
	
	
	
    
    /********** methods for binding to ThreegtestService ************/
    private MainService mBoundService;
    private boolean mIsBound = false;

    private ServiceConnection mConnection = new ServiceConnection() {
    	//@Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            
            mBoundService = ((MainService.Threegtest_Binder)service).getService();
            mBoundService.setActivity(Main.this);
            updateUI();
            // If service thread is running, update UI
            if(mBoundService.testThread!=null && mBoundService.testThread.isAlive())
            {
            	updateListView(mBoundService.resultList);
            	updateProgress(mBoundService.testThread.getProgress());
            	updateTextView3(mBoundService.currentTest);
            }
            //mBoundService.bindThreadActivity(threegtest.this);
            // Tell the user about this for our demo.
            //Toast.makeText(getApplicationContext(), "service connected",
            //        Toast.LENGTH_SHORT).show();
            Log.v("MobiPerf", "service connected");
        }
    	//@Override
        public void onServiceDisconnected(ComponentName className) {
           
            mBoundService = null;
            //Toast.makeText(getApplicationContext(), "service disconnected",
            //        Toast.LENGTH_SHORT).show();
            Log.v("MobiPerf", "service disconnected");
        }

    };

    private void doBindService() {
        
        bindService(new Intent(getApplicationContext(), MainService.class), mConnection, Context.BIND_AUTO_CREATE);
        mIsBound = true;
    }
 
    private void doUnbindService() {
        if (mIsBound) {
            // Detach our existing connection.
            unbindService(mConnection);
            mIsBound = false;
        }
    }
    /**********************************************************/
    
    // check if the there is a Service_Thread instance running
    public boolean isRunning()
    {
        if(mBoundService != null){	// Service already started
        	if(mBoundService.testThread != null && mBoundService.testThread.isAlive())
        		return true;
        	else
        		return false;
        }else{
        	Log.v("MobiPerf", "mBoundService null not running");
        	return false;
        }
    }
    
    //interface for the tabview---------------cc
	
    
	// Only run once
     	private void firstTimeRun(){
         	String FILENAME = "first_time_mark_1.4";
         	String  mark = "1";
     		String[] fileList = fileList();
     		for(int i = 0; i<fileList.length; i++)
    			if(fileList[i].equals(FILENAME))
     				// Already shown up
     				return;
     		// The following codes will only be called once after the app. is installed
     		Utilities.writeToFile(FILENAME, Context.MODE_PRIVATE, mark,this);
     		// Show warning
         	showDialog(Preferences.DIALOG_WARNING);
         	// Remove existing pending intent after update a new version app.
         	Preferences.disablePeriodicalRun(this);
         	Preferences.enablePeriodicalRun(this);
         }

    
}