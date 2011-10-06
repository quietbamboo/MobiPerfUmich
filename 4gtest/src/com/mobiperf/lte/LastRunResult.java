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

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;

//Junxian: directly fetch results from remote mysql
@Deprecated
public class LastRunResult extends Activity{
	
    WebView mWebView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	/*
        super.onCreate(savedInstanceState);
        setContentView(R.layout.last_run_result);
        ListView lv = (ListView)this.findViewById(R.id.last_run_result);
	    ArrayAdapter<String> aa = new ArrayAdapter<String>(this, R.layout.simple_list_item_1); 
	    try {
			BufferedReader br = new BufferedReader(new InputStreamReader(this.getApplicationContext().openFileInput(Service_Thread.LAST_LOG_FILE_NAME)));
			String line = null;
			while((line = br.readLine()) != null)
			{
				aa.add(line);
			}
			lv.setAdapter(aa);
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		*/
 
    }
  /*  
    @Override 
    public boolean onKeyDown(int keyCode,KeyEvent event) {  
       if (keyCode == KeyEvent.KEYCODE_BACK) {
    	   this.startActivity(new Intent(this,threegtest.class));
    	   this.finish();
            return true;  
        } else
            return super.onKeyDown(keyCode,event);  
    }
*/

}