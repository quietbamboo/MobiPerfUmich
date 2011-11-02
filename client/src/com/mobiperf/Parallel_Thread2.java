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

package com.mobiperf;
import com.mobiperf.service.MainService;

import android.app.Service;
import android.util.Log;


class Parallel_Thread2 extends Thread {
	public static boolean thread2DoneFlag;
	
	private Service service;
	private boolean fore;
	
	public Parallel_Thread2(Service s, boolean f){
		this.service = s;
		this.fore = f;
	}
    public void run() {
    	Log.v("3gtest", "parallel_thread2 start!");
    	String message,message1, result ="";
        if ( Utilities.checkStop() ) {
            thread2DoneFlag = true;
            return ;
        }
        Log.v("3gtest", "p2 http landmark starts");
        int replyCode = Landmark.http_landmark();
        Log.v("3gtest", "p2 http landmark ends");
        if ( Utilities.checkStop() ) {
            thread2DoneFlag = true;
            return ;
        }

        
        if ( replyCode == 2 ) {
            result = "LANDMARK";
            for ( int i = 0;i < Landmark.nLandmarkServers;i++ ) {
                result += ":";
                result += "<PingRTT" + i + ": " + Landmark.latencyLandmarkServers[ i ] / 10.0 + ">:<PingLR" + i + ": " + ( 2 - Landmark.lossRateLandmarkServers[ i ] ) + "/2>";
                result += ":<HdSk" + i + ": " + Landmark.SYNACKLandmarkServers[ i ] + ">";
                result += ":<Latency" + i + ": " + Landmark.gettLandmarkServers[ i ] + ">:<Size" + i + ": " + Landmark.getsLandmarkServers[ i ] + ">";
            }

            result += ";";
            Log.v( "RESULT", result );
            result += "\n";
            (new Report()).sendReport(result);

            if ( Landmark.averageHTTPLandmarkServers != 0 ) {
                message = "Average TCP handshake latency to landmark server (ms): " + Landmark.averageHTTPLandmarkServers;

                if ( Landmark.averageHTTPLandmarkServers < 200 )
                    message += " Good";
                else if ( Landmark.averageHTTPLandmarkServers < 500 )
                    message += " Moderate";
                else
                    message += " Bad";
            }
            else
                message = "Average TCP handshake latency to landmark server (ms): Error in test";

            if ( Landmark.averageHTTPLandmarkServers2 != 0 ) {
                message1 = "Average HTTP GET latency to landmark servers (ms): " + Landmark.averageHTTPLandmarkServers2;

                if ( Landmark.averageHTTPLandmarkServers2 < 400 )
                    message1 += " Good";
                else if ( Landmark.averageHTTPLandmarkServers2 < 800 )
                    message1 += " Moderate";
                else
                    message1 += " Bad";
            }
            else
                message1 = "Average HTTP GET latency to landmark servers (ms): Error in test";

        }
        else {
            message = "Average TCP handshake latency to landmark server (ms): Error in test";
            message1 = "Average HTTP GET latency to landmark servers (ms): Error in test ";
        }
        
       // Utilities.write_to_file(Service_Thread.FILENAME, Context.MODE_APPEND,message + "\n"+ Service_Thread.mProgressStatus + "\n"+message1 + "\n"+Service_Thread.mProgressStatus + "\n", context );
        //Utilities.sendudpmessage( uiFile, "" + message.length() + "!" + message + "" + Service_Thread.mProgressStatus + "!", context );
        
        if(fore){
        	((MainService)service).addResultAndUpdateUI(message, -1);
        	//Utilities.write_to_file(Service_Thread.LOG_FILE_NAME, Context.MODE_APPEND,message + "\n"+message1 + "\n", service);
        //	Utilities.sendudpmessage( uiFile, "" + message1.length() + "!" + message1 + "" + Service_Thread.mProgressStatus + "!", context );
        	((MainService)service).addResultAndUpdateUI(message1, -1);
        }
        thread2DoneFlag = true;
        Log.v("3gtest", "parallel_thread2 finish!");
    }
}
