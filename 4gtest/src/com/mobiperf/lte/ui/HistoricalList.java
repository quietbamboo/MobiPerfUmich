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

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.mobiperf.lte.Definition;
import com.mobiperf.lte.Main;
import com.mobiperf.lte.R;
import com.mobiperf.lte.Utilities;

public class HistoricalList extends ListActivity {

	public static String[] records;
	public static String[] recordsAgo;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.list_view);

		//fetch the required list from server
		String list = Utilities.getPreviousResultList();
		Log.e("MobiPerf", "retured list " + list);
		String[] parts = list.split("_____");

		records = new String[]{};
		recordsAgo = new String[]{};

		if(parts.length >= 2){
			//nothing in the list
			records = new String[parts.length - 1];
			recordsAgo = new String[parts.length - 1];
			for(int i = 1 ; i <= parts.length - 1 ; i++){
				records[i - 1] = parts[i];
				recordsAgo[i - 1] = Utilities.getAgo(parts[i]);
			}
		}

		setListAdapter(new ArrayAdapter<String>(this, R.layout.list_view, recordsAgo));

		ListView lv = getListView();
		lv.setTextFilterEnabled(true);

		lv.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				
				Display.clearTabView();
				String result = Utilities.getPreviousResult(records[position]);
				//making random change
				String[] parts = result.split(Definition.RESULT_DELIMITER);
				int index = 0;
				Display.displayResult("Network Type", "Network type used (WiFi, UMTS, CDMA, etc.)", parts[index++], 0);
				Display.displayResult("Carrier", "Name of cellular carrier", parts[index++], 0);
				Display.displayResult("Cell ID", "Id of cell tower connected", parts[index++], 0);
				Display.displayResult("Signal Strength", "Signal strength in asu, 0 is worst and 31 is best", parts[index++], 0);
				Display.displayResult("Local IP", "Local IP address of your device, could be private IP", parts[index++], 0);
				Display.displayResult("Seen IP", "IP address of your device seen by a remote server", parts[index++], 0);
				Display.displayResult("GPS Location", "Latitude (<0 for South) Longitude (<0 for West)", 
						"Latitude:" + parts[index++] + " Longitude:" + parts[index++], 0);
				Display.displayResult("DNS lookup latency", "Average DNS lookup time of 80 domains (ms)", parts[index++], 1);
				Display.displayResult("Downlink throughput", "How many bits per second can be downloaded in TCP", parts[index++], 1);
				Display.displayResult("Uplink throughput", "How many bits per second can be uploaded in TCP", parts[index++], 1);
				
				Log.i("MobiPerf", "clicked " + position);
				Intent i = new Intent(view.getContext(), com.mobiperf.lte.ui.Display.class);
				startActivityForResult(i, 0);
			}
		});
	}

}
