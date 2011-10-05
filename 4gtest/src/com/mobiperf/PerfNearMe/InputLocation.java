package com.mobiperf.PerfNearMe;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.maps.MapActivity;
import com.mobiperf.R;

public class InputLocation extends MapActivity{
	private static EditText inputLocation;
	
      
	protected void onCreate (Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.perfnearme_input);
	
		final Button ok = (Button) findViewById(R.id.ok);
		ok.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				inputLocation = (EditText) findViewById(R.id.entry);
				startActivity(new Intent("com.mobiperf.PerfNearMe.INPUTMAP"));
				
			}
		});
	}
	
	// Get's the user's input
	static EditText getInputLocation(){
		return inputLocation;
	}

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}
}
