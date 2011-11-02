package com.mobiperf.lte;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.mobiperf.lte.R;

public class NetworkToggle extends Activity {
	public void onCreate(Bundle savedInstanceState) {
		Log.v("4G Test", "network onCreate");
	    super.onCreate(savedInstanceState);
	    //Log.v("huangshu", "network onCreate2");
	    setContentView(R.layout.network);
	    TextView textView = (TextView)findViewById(R.id.network_text2);
	    textView.setMovementMethod(ScrollingMovementMethod.getInstance());
	    
	    Button button = (Button) findViewById(R.id.network_btn);
        button.setOnClickListener(new OnClickListener() {

        //When the button is clicked, call up android test menu
		//@Override
		public void onClick(View v) {
			String url = "tel:*#*#4636#*#*";
			Intent callint = new Intent();
			callint.setAction(Intent.ACTION_DIAL);
			callint.setData(Uri.parse("tel:" + Uri.encode(url)));
			startActivity(callint);
			//Close activity?  minor design choice
			finish();
		}
        });
	}
}