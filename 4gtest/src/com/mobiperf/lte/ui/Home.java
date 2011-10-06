package com.mobiperf.lte.ui;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.mobiperf.lte.Main;
import com.mobiperf.lte.R;

public class Home extends Activity {

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
		menu.add(0, MENU_EMAIL, 0, "About us");
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
		
		

		case MENU_EMAIL:
			try
			{

				Intent in1 = new Intent(this, com.mobiperf.lte.ui.About.class);
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

		findViewById(R.id.home_btn_about).setOnClickListener(
				new View.OnClickListener() {
					// @Override
					public void onClick(View v) {
						startActivity(new Intent(getActivity(),
								com.mobiperf.lte.ui.About.class));
					}
				});

	}

	protected Activity getActivity() {
		return this;
	}

}
