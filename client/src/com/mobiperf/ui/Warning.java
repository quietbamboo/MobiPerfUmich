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
import android.util.Log;
import android.view.View;

import com.mobiperf.R;
import com.mobiperf.service.TcpdumpService;

public class Warning extends Activity {
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.warning);
		findViewById(R.id.warning_btn_yes).setOnClickListener(
				new View.OnClickListener() {
					// @Override
					public void onClick(View v) {
						SharedPreferences settings = getSharedPreferences("pref", 0);
						SharedPreferences.Editor spe = settings.edit();
						spe.putBoolean("canUpload", true);
						spe.commit();
						Log.v("MobiPerf", "Inside warning");
						Intent intent = new Intent(getActivity(), TcpdumpService.class);
						startService(intent);
						finish();
					}
				});
		findViewById(R.id.warning_btn_no).setOnClickListener(
				new View.OnClickListener() {
					// @Override
					public void onClick(View v) {
						finish();
					}
				});
	}
	protected Activity getActivity() {
		return this;
	}

}
