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

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.mobiperf.lte.ui.Preferences;

@Deprecated
public class History extends Activity{
	/** Called when the activity is first created. */
    
    CustomDrawableView mCustomDrawableView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCustomDrawableView = new CustomDrawableView(this);
        
        setContentView(mCustomDrawableView);
    }
    
    @Override 
    public boolean onKeyDown(int keyCode,KeyEvent event) {  
       if (keyCode == KeyEvent.KEYCODE_BACK) {
    	   this.startActivity(new Intent(this,Main.class));
    	   this.finish();
            return true;  
        } else
            return super.onKeyDown(keyCode,event);  
    }
    
}


class CustomDrawableView extends View {

    public CustomDrawableView(Context context) {
        super(context);
        //this.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,ViewGroup.LayoutParams.FILL_PARENT));
    }
    public void debug(String s)
    {
    	Toast.makeText(this.getContext(), s, Toast.LENGTH_SHORT).show();
    }
    
    private void drawBar(Canvas canvas, int x, int y, int width, int height)
    {
    	Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.BLUE);
       canvas.drawRect(x, y, x+width, y+height, paint);
         
    }
    
    private ArrayList<String> timestamps = new ArrayList<String>();
    private ArrayList<Integer> rtts = new ArrayList<Integer>();
    private static final int MAX_RTT = 800;
    private static final int MIN_RTT = 0;
    private int maxRTT = 300;
    
    private int processData()
    {
    	//Junxian: commented out for now, causing app crashing
    	/*
    	timestamps.clear();
    	rtts.clear();
    	try{
    		BufferedReader br = new BufferedReader(new InputStreamReader(this.getContext().openFileInput(Promotion_Delay_Test.RTT_HISTORY_FILE)),1024);
    		String line = null;
    		int max = 0;
    		while((line = br.readLine())!=null)
    		{
    			//debug(line);
    			String tokens[] = line.split("\t");
    			
    			int total = 0; int count = 0; 
    			for(int i = 1; i < tokens.length; i++)
    			{
    				int rtt = Integer.parseInt(tokens[i]);
    				if(rtt<MAX_RTT && rtt>MIN_RTT)
    				{
    					total+=rtt;
    					count++;
    				}
    			}
    			if(count !=0 && total != 0)
    			{
    				int avg = total/count;
    				rtts.add(0, avg);
    				String dt[] = tokens[0].split(",");
    				String ymd[] = dt[0].split("-");
    				String hms[] = dt[1].split(":");
    				timestamps.add(0, ymd[1]+"/"+ymd[2]+" "+hms[0]+":"+hms[1]);
    				if(max < avg)
						max = avg;
    			}
    		}
    		br.close();
    		return max;
    	}catch(Exception e){e.printStackTrace(); }
    	*/
    	return -1;
    }
    
    private static final int MARGIN = 10;
    private static final int TEXT_X = MARGIN;
    private static final int BAR_X = 95;
    private static final int LABEL_X = BAR_X + 5;
    private static final int BAR_HEIGHT = 20;
    private static final int LINE_HEIGHT = 30;
    private static final int TEXT_SIZE = 15;
    
    // Update UI when the window gets focused
    @Override
    public void onWindowFocusChanged  (boolean hasWindowFocus)
    {
    	super.onWindowFocusChanged(hasWindowFocus);
    	if(hasWindowFocus)
    	{
    		maxRTT=processData();
    	}
    }
    @Override
    public boolean onTouchEvent(MotionEvent e){
    	maxRTT=processData();
    	return super.onTouchEvent(e);
    }
    @Override
    protected void onDraw(Canvas canvas) {
    	super.onDraw(canvas);
    	this.setBackgroundColor(Color.BLACK);
    	Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    	textPaint.setColor(Color.WHITE);
    	textPaint.setTextSize(TEXT_SIZE);
    	Paint labelPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
     	labelPaint.setColor(Color.WHITE);
     	labelPaint.setTextSize(TEXT_SIZE);
    	canvas.drawText("Cellular network latency to Google server:", TEXT_X, 2 * MARGIN, textPaint);
    	canvas.drawText("(Smaller value indicates better network)", TEXT_X, 4 * MARGIN , textPaint);
    	int currentY = 5 * MARGIN;	
    	int maxBarLength = this.getWidth() - MARGIN - BAR_X;
    	int num = (this.getHeight()-currentY)/LINE_HEIGHT;	// calculate the number of records to show based on screen size
    	if(rtts.size() != 0)
    	{
	    	for(int i =0; i< rtts.size(); i++)
	        {    	
	    		if(i == num) break;
	        	drawBar(canvas, BAR_X, currentY + i * LINE_HEIGHT ,rtts.get(i)*maxBarLength/maxRTT,BAR_HEIGHT);
	        	canvas.drawText(timestamps.get(i), TEXT_X, currentY + BAR_HEIGHT - 5 + i * LINE_HEIGHT, textPaint);
	        	canvas.drawText(rtts.get(i)+" ms", LABEL_X , currentY + BAR_HEIGHT - 5 + i * LINE_HEIGHT, labelPaint);
		
	        }
    	}
    	else
    	{
    		textPaint.setColor(Color.RED);
    		canvas.drawText("No history data available.", TEXT_X, 8 * MARGIN, textPaint);	
    		canvas.drawText("Please enable histroy tracking.", TEXT_X, 10 * MARGIN, textPaint);

    	}
       
    }
}