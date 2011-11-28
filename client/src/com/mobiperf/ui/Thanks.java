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

package com.mobiperf.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.mobiperf.R;
import com.mobiperf.Tcpdump;

public class Thanks extends Activity {
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.thanks);

		findViewById(R.id.tpa_btn_stop).setOnClickListener(
				new View.OnClickListener() {
					// @Override
					public void onClick(View v) {
						SharedPreferences settings = getSharedPreferences("pref", 0);
						SharedPreferences.Editor spe = settings.edit();
						spe.putBoolean("canUpload", false);
						spe.commit();
						Intent intent = new Intent(getActivity(), com.mobiperf.service.TcpdumpService.class);
						stopService(intent);
						finish();
					}
				});
	}
	protected Activity getActivity() {
		return this;
	}
	/******************** Menu ends here ********************/

}
