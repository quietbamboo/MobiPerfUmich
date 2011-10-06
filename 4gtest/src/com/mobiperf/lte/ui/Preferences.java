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

package com.mobiperf.lte.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.mobiperf.lte.R;

 
public class Preferences extends PreferenceActivity {
		public  static final int NOTIFICATION_ID = 0;
		private static boolean isNotificationEnabled = true; // Show notification by default
		
		
        @Override
        protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                addPreferencesFromResource(R.layout.preferences);
                // Get the custom preference
                Preference customPref = (Preference) findPreference("PeriodicPref");
                customPref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
                	public boolean onPreferenceClick(Preference preference) {
                		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                		boolean CheckboxPreference = prefs.getBoolean("PeriodicPref", true);
                		if (CheckboxPreference == true){
                			Toast.makeText(getApplicationContext(), "enabled the periodic running", Toast.LENGTH_SHORT).show();
                		}
                		if (CheckboxPreference == false){
                			Toast.makeText(getApplicationContext(), "disable periodic running", Toast.LENGTH_SHORT).show();
                		}
                		
                		return true;        
                	}
                	});
     
                
                Preference customPref1 = (Preference) findPreference("NotificationPref");
                
                customPref1.setOnPreferenceClickListener(new OnPreferenceClickListener() {
                	public boolean onPreferenceClick(Preference preference) {
                		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                		boolean CheckboxPreference = prefs.getBoolean("NotificationPref", true);
                		if(CheckboxPreference == true){
                			//test
                			//int a = seekbar.getProgress();
                			
                			//Log.w("!!!!!!the number is ", String.format("%d",a));

                			createNotification(Preferences.this);

                			Toast.makeText(getApplicationContext(), "enabled the notification", Toast.LENGTH_SHORT).show();
                    		
                		}
                		if(CheckboxPreference == false){
                			clearNotification(Preferences.this);
                			Toast.makeText(getApplicationContext(), "disable the notification", Toast.LENGTH_SHORT).show();
                    		
                		}
                
                		return true;        
                	}
                	});
                
                
                Preference tracelog = (Preference) findPreference("tracelog");
                tracelog.setOnPreferenceClickListener(new OnPreferenceClickListener() {
                	public boolean onPreferenceClick(Preference preference) {
                		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                		boolean CheckboxPreference = prefs.getBoolean("tracelog", true);
                		if (CheckboxPreference == true){
                			//Tcpdump.terminate_client();
                			//Tcpdump.start_server();
                			Toast.makeText(getApplicationContext(), "tcpdump start", Toast.LENGTH_SHORT).show();
                		}
                		if (CheckboxPreference == false){
                			//Tcpdump.upload();
                			//Tcpdump.terminate_server();
                			Toast.makeText(getApplicationContext(), "stop tcpdump", Toast.LENGTH_SHORT).show();
                		}
                		
                		return true;        
                	}
                	});
        }
        public static boolean getSharedPreferences (Context ctxt) {
        	SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctxt);
    		boolean CheckboxPreference = prefs.getBoolean("tracelog", true);
    		return CheckboxPreference;
        	}
        
        
        //
    	// Define dialog ids
        
    	protected static final int DIALOG_PERIODIC = 0;
    	public static final int DIALOG_WARNING = 1;
    	protected static final int DIALOG_NOTIFICATION = 2;
    	protected static final String WARNING = 
    		"NAT and firewall tests require root access to open raw socket. " 
    		+"Other tests still run normally if your phone is not rooted.\n\n"
    		+"A very lightweight test is run periodically every hour by default, giving two benefits:\n"
    		+"(1) better diagnose your network (we provide you with history of your network performance),\n"
    		+"(2) enables our research for long-term network improvement.\n\n"
    		+"We provide the setting to opt out, but we do appreciate you keep this option enabled.\n"
    		+"Thank you for your help!";
    	// Options for periodical running
    	public static final int PERIODIC_YES = 0;
    	public static final int PERIODIC_NO = 1;
    	final CharSequence[] periodicItems = {"Yes","No"};
    	final CharSequence[] periodicPrompts = {"Periodic running is enabled", "Periodical running is disabled"};
    	public static final int NOTIFICATION_YES = 0;
    	public static final int NOTIFICATION_NO = 1;
    	final CharSequence[] notificationItems = {"Yes","No"};
    	final CharSequence[] notificationPrompts = {"Notification is enabled", "Notification is disabled"};
    	protected Dialog onCreateDialog(int id) {
    	    Dialog dialog;
    	    AlertDialog.Builder builder;
    	    switch(id) {
    	    case DIALOG_NOTIFICATION:
    	    	builder = new AlertDialog.Builder(this);
    	    	builder.setTitle("Enable notification");
    	    	builder.setSingleChoiceItems(notificationItems, isNotificationEnabled?0:1, new DialogInterface.OnClickListener() {
    	    	    public void onClick(DialogInterface dialog, int item) {
    	    	        switch(item)
    	    	        {
    	    	        case NOTIFICATION_YES:
    	    	        	isNotificationEnabled = true;
    	    	        	break;
    	    	        case NOTIFICATION_NO:
    	    	        	clearNotification(Preferences.this);
    	    	        	isNotificationEnabled = false;
    	    	        	break;
    	    	        default:
    	    	        }
    	    	        Toast.makeText(getApplicationContext(), notificationPrompts[item], Toast.LENGTH_SHORT).show();
    	    	        Preferences.this.dismissDialog(DIALOG_NOTIFICATION);
    	    	    }
    	    	});
    	    	dialog = builder.create();
    	    	break;
    	    case DIALOG_WARNING:
    	    	builder = new AlertDialog.Builder(this);
    			builder.setTitle("Notice")
    				   .setMessage(WARNING)
    			       .setCancelable(false)
    			       .setPositiveButton("OK", new DialogInterface.OnClickListener() {
    			           public void onClick(DialogInterface dialog, int id) {
    			        	   Preferences.this.dismissDialog(DIALOG_WARNING);
    			           }
    			       });
    			dialog = builder.create();
    	    	break;
    	    default:
    	        dialog = null;
    	    }
    	    return dialog;
    	}

    	/**** Functions for periodical running by Gary ****/
        
        public static final int REQUEST_CODE = 100000;
        public static final long INTERVAL = 3600*1000;
        public static final String PERIODIC_FILE = "periodic_file";
    	private static void clearNotification(Context context)
    	{
    		NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    		mNotificationManager.cancel(NOTIFICATION_ID);
    	}
    	
    	//modified by cc-----told by friends that the notification is annoying
    	public static void createNotification(Context context)
    	{
    		 /*NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    		  //2. Instantiate the Notification:
    	      int icon = R.drawable.iconstat;
    	      CharSequence tickerText = "MobiPerf";
    	      long when = System.currentTimeMillis();
    	      Notification notification = new Notification(icon, tickerText, when);
    	      notification.defaults |= Notification.FLAG_NO_CLEAR;	// Never get cleared
    	      notification.flags |= Notification.FLAG_NO_CLEAR;	// Never get cleared
    	      // 3. Define the Notification's expanded message and Intent:
    	      CharSequence contentTitle = "MobiPerf";
    	      CharSequence contentText = "Periodic test is running ...";
    	      Intent notificationIntent = new Intent(context, History.class);
    	      PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);
    	      notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);
    	      // 4. Pass the Notification to the NotificationManager:
    	      mNotificationManager.notify(NOTIFICATION_ID, notification);
    	      */
    	}
    	
    	 

    	
    	
    	
}