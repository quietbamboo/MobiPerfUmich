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

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import com.mobiperf.lte.R;

public class Basic extends Activity {

	ArrayList<TextView> list_title= new ArrayList<TextView>();
	ArrayList<TextView> list_description= new ArrayList<TextView>();
	ArrayList<TextView> list_result= new ArrayList<TextView>();
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.basic_format);
        
        TextView textview1_1 = (TextView) findViewById(R.id.basic_1_txt1);
        TextView textview1_2 = (TextView) findViewById(R.id.basic_1_txt2);
        TextView textview1_3 = (TextView) findViewById(R.id.basic_1_txt3);
        list_title.add(textview1_1);
        list_description.add(textview1_2);
        list_result.add(textview1_3);
        
        TextView textview2_1 = (TextView) findViewById(R.id.basic_2_txt1);
        TextView textview2_2 = (TextView) findViewById(R.id.basic_2_txt2);
        TextView textview2_3 = (TextView) findViewById(R.id.basic_2_txt3);
        
        list_title.add(textview2_1);
        list_description.add(textview2_2);
        list_result.add(textview2_3);

        
        TextView textview3_1 = (TextView) findViewById(R.id.basic_3_txt1);
        TextView textview3_2 = (TextView) findViewById(R.id.basic_3_txt2);
        TextView textview3_3 = (TextView) findViewById(R.id.basic_3_txt3);
        
        list_title.add(textview3_1);
        list_description.add(textview3_2);
        list_result.add(textview3_3);
        
        TextView textview4_1 = (TextView) findViewById(R.id.basic_4_txt1);
        TextView textview4_2 = (TextView) findViewById(R.id.basic_4_txt2);
        TextView textview4_3 = (TextView) findViewById(R.id.basic_4_txt3);
        
        list_title.add(textview4_1);
        list_description.add(textview4_2);
        list_result.add(textview4_3);
        
        TextView textview5_1 = (TextView) findViewById(R.id.basic_5_txt1);
        TextView textview5_2 = (TextView) findViewById(R.id.basic_5_txt2);
        TextView textview5_3 = (TextView) findViewById(R.id.basic_5_txt3);
        
        list_title.add(textview5_1);
        list_description.add(textview5_2);
        list_result.add(textview5_3);
        
        TextView textview6_1 = (TextView) findViewById(R.id.basic_6_txt1);
        TextView textview6_2 = (TextView) findViewById(R.id.basic_6_txt2);
        TextView textview6_3 = (TextView) findViewById(R.id.basic_6_txt3);
        
        list_title.add(textview6_1);
        list_description.add(textview6_2);
        list_result.add(textview6_3);
        
        TextView textview7_1 = (TextView) findViewById(R.id.basic_7_txt1);
        TextView textview7_2 = (TextView) findViewById(R.id.basic_7_txt2);
        TextView textview7_3 = (TextView) findViewById(R.id.basic_7_txt3);
        
        list_title.add(textview7_1);
        list_description.add(textview7_2);
        list_result.add(textview7_3);
        
        TextView textview8_1 = (TextView) findViewById(R.id.basic_8_txt1);
        TextView textview8_2 = (TextView) findViewById(R.id.basic_8_txt2);
        TextView textview8_3 = (TextView) findViewById(R.id.basic_8_txt3);
        
        list_title.add(textview8_1);
        list_description.add(textview8_2);
        list_result.add(textview8_3);
        
        for(int i = 0; i< com.mobiperf.lte.ui.Display.titles_basic.size(); i++){
        	list_title.get(i).setText(com.mobiperf.lte.ui.Display.titles_basic.get(i));
        	list_description.get(i).setText(com.mobiperf.lte.ui.Display.description_basic.get(i));
        	list_result.get(i).setText(com.mobiperf.lte.ui.Display.result_basic.get(i));
        }
        
    }
    
    
    
}

