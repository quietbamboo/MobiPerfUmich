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
import android.app.Service;
import android.util.Log;


class Parallel_Thread1 extends Thread {
	public static boolean thread1DoneFlag;
	private Service service;
	private boolean fore;
	
	public Parallel_Thread1(Service s, boolean f){
		this.service = s;
		this.fore = f;
	}
    public void run() {
    	Log.v("3gtest", "parallel_thread1 start!");
    	String message1 = null, result="";
outer: {
    		
            if ( DNS.DNSGoogle == true ) {

                if ( Utilities.checkStop() ) {
                    thread1DoneFlag = true;
                    return ;
                }
                Log.v("3gtest", "p1 DNS.popular start");
               int  replyCode = DNS.Popular();
               Log.v("3gtest", "p1 DNS.popular ends");
                if ( Utilities.checkStop() ) {
                    thread1DoneFlag = true;
                    return ;
                }

                if ( replyCode == 1 ) {
                    message1 = "Local DNS lookup latency (ms): Error in test";
                    break outer;
                }

                replyCode = 0;
                result = "DNS";
                long DNSLat = 0;
                long DNSLatYes = 0;

                for ( int i = 0;i < DNS.nPOPDNS;i++ ) {
                    result += ":";
                    result += "<LkUp" + i + ": " +DNS.DNSlatencies[ i ] + ">";
                    if ( DNS.DNSpop[ i ] == false ) {
                        replyCode = 1;
                    }
                    else if ( DNS.DNSlatencies[ i ] > 50 ) {
                        DNSLat += DNS.DNSlatencies[ i ];
                        DNSLatYes++;
                    }
                }

                try {

                    if ( DNSLatYes > 0 ) {
                        message1 = "Local DNS lookup latency (ms): " + DNSLat / DNSLatYes;

                        if ( DNSLat / DNSLatYes < 200 )
                            message1 += " Good";
                        else if ( DNSLat / DNSLatYes < 500 )
                            message1 += " Moderate";
                        else
                            message1 += " Bad";
                    }
                    else {
                        message1 = "Local DNS lookup latency (ms): Error in test";
                    }
                    
                }
                catch ( Exception e ) {}

            }
            else {
                message1 = "Local DNS lookup latency (ms): DNS server down";
            }  
            if(message1 != null)
            {
            	//Utilities.write_to_file(Service_Thread.LOG_FILE_NAME, Context.MODE_APPEND,message1 + "\n", service );
            	if(fore)
            		((MainService)service).addResultAndUpdateUI(message1, -1);//Local DNS lookup latency
            }
        }
        result += ";";
        result += "\n";
        (new Report()).sendReport(result);

        if ( Utilities.checkStop() ) {
            thread1DoneFlag = true;
            return ;
        }
/*
        if ( Service_Thread.pingFlag ) {
           int replyCode = Landmark.latitude_landmark();

            if ( Utilities.checkStop() ) {
                thread1DoneFlag = true;
                return ;
            }
            if ( replyCode == 2 && Landmark.averagePingLandmarkServers != 0 ) {
                message = "Average Ping latency to landmark servers (ms): " + Landmark.averagePingLandmarkServers;

                if ( Landmark.averagePingLandmarkServers < 200 )
                    message += " Good";
                else if ( Landmark.averagePingLandmarkServers < 500 )
                    message += " Moderate";
                else
                    message += " Good";
            }
            else
                message = "Average Ping latency to landmark servers (ms): Error";

            // Update the progress bar
        }
        else {
            message = "Average Ping latency to landmark servers (ms): Cannot run test, your provider blocks outgoing Ping Packets";
        }

        if ( Utilities.checkStop() ) {
            thread1DoneFlag = true;
            return ;
        }
        
        */
        thread1DoneFlag = true;
        Log.v("3gtest", "parallel_thread1 finish!");
    }
}