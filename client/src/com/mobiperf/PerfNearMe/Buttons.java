package com.mobiperf.PerfNearMe;

import com.mobiperf.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Buttons extends Activity{
	
		// Creates the opening screen's buttons
		protected void onCreate(Bundle savedInstanceState){
			super.onCreate(savedInstanceState);
			setContentView(R.layout.perfnearme);
			
	    	final Button button1 = (Button) findViewById(R.id.current);
        	button1.setOnClickListener(new View.OnClickListener() {
        		public void onClick(View v) {
        			startActivity(new Intent("com.mobiperf.PerfNearMe.MAPAPP"));
        		}
        	});
        	
        	final Button button2 = (Button) findViewById(R.id.input);
        	button2.setOnClickListener(new View.OnClickListener() {
        		public void onClick(View v) {
        			startActivity(new Intent("com.mobiperf.PerfNearMe.INPUTLOCATION"));
        		}
        	});
			
		}
}
