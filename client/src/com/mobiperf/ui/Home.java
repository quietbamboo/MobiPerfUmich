package com.mobiperf.ui;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.mobiperf.Main;
import com.mobiperf.R;

public class Home extends Activity {

	// Define menu ids
		protected static final int MENU_PERIODIC = Menu.FIRST;
		//protected static final int MENU_NOTIFICATION = Menu.FIRST + 1;
		//-------------commented by cc---------
		//protected static final int MENU_LAST = Menu.FIRST + 2;
		//protected static final int MENU_HISTORY = Menu.FIRST + 3;
		protected static final int MENU_ABOUT = Menu.FIRST +4;
		//TODO:new menu --------cc
		protected static final int PAST_RECORD = Menu.FIRST +5;
		//protected static final int VOTE = Menu.FIRST +6;
		protected static final int PERF_ME =Menu.FIRST+7;
		protected static final int ADVANCE =Menu.FIRST+8;
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
		        menu.add(0, MENU_ABOUT, 0, "About us");
		        //menu.add(0, VOTE, 0, "Vote for us");
		        //menu.add(0, PERF_ME, 0, "Performance Near Me");
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
	            //TODO:new menu --------cc
		        case PAST_RECORD:
		        	 Intent i = new Intent(this, com.mobiperf.ui.HistoricalList.class);
					 startActivityForResult(i, 0);
					 break;		       /* case MENU_NOTIFICATION:
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

		        case PERF_ME:
		        	 Intent intent_perf= new Intent(this, com.mobiperf.PerfNearMe.Buttons.class);
		        	 startActivityForResult(intent_perf, 0);
					 break;
		        case MENU_ABOUT:
		        	try
		        	{

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
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home);

		findViewById(R.id.home_btn_networktest).setOnClickListener(
				new View.OnClickListener() {
					// @Override
					public void onClick(View v) {
						startActivity(new Intent(getActivity(), Main.class));
					}
				});

		findViewById(R.id.home_btn_connectionmonitor).setOnClickListener(
				new View.OnClickListener() {
					// @Override
					public void onClick(View v) {
						startActivity(new Intent(getActivity(),
								ConnectionMonitorActivity.class));
					}
				});

		findViewById(R.id.home_btn_trafficmonitor).setOnClickListener(
				new View.OnClickListener() {
					// @Override
					public void onClick(View v) {
						startActivity(new Intent(getActivity(),
								TrafficMonitorActivity.class));
					}
				});
		findViewById(R.id.home_btn_advance).setOnClickListener(
				new View.OnClickListener() {
					// @Override
					public void onClick(View v) {
						SharedPreferences settings = getSharedPreferences(
								"pref", 0);
						boolean canUpload = settings.getBoolean("canUpload",
								false);
						if (!canUpload)
							startActivity(new Intent(getActivity(),
									com.mobiperf.ui.Warning.class));
						else
							startActivity(new Intent(getActivity(),
									com.mobiperf.ui.TrafficPerApplication.class));
					}
				});

	}

	protected Activity getActivity() {
		return this;
	}

}
