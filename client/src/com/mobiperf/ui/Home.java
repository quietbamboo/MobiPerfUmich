package com.mobiperf.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.mobiperf.Main;
import com.mobiperf.R;

public class Home extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home);

		findViewById(R.id.home_btn_networktest).setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v) {
				startActivity(new Intent(getActivity(), Main.class));
			}
		});
		
		findViewById(R.id.home_btn_connectionmonitor).setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v) {
				startActivity(new Intent(getActivity(), ConnectionMonitorActivity.class));
			}
		});
		
		findViewById(R.id.home_btn_trafficmonitor).setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v) {
				startActivity(new Intent(getActivity(), TrafficMonitorActivity.class));
			}
		});
		findViewById(R.id.home_btn_performance).setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v) {
				startActivity(new Intent(getActivity(), com.mobiperf.PerfNearMe.Buttons.class));
			}
		});
	}
	protected Activity getActivity()
	{
		return this;
	}

}
