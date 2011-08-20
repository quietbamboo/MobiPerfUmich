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

class Traceroute  {
	public static int traceroute(String serverName, int start, int end) {
        
		String tracerouteresult = "TRACEROUTE";
		
/*TODO
        for ( int i = start; i < end; i++ ) {

            String[] res = Utilities.pingS( serverName, i + 1, 0, 0, 2000 );

                tracerouteresult += ":<" + ( i + 1 ) + ":" + serverName + ":" + k + ">;";
                break;
            }

            if ( k != -1 ) {
                tracerouteresult += ":<" + ( i + 1 ) + ":" + Utilities.signalServers + ":" + k + ">";
            }
            else {
                tracerouteresult += ":<" + ( i + 1 ) + "::0.00>";
            }
        }
        	(new Report()).sendReport(tracerouteresult);
//*/
        return 2;
    }

	public static long[] latencyServers = new long[ 2 ];
    public static int[] lossRateServers = new int[ 2 ];
	public static int latitude_2ndhop(){
		/*TODO
    	String result;
        for ( int i = 0;i < 3;i++ ) {
            if ( Utilities.checkStop() ) {
                return 1;
            }

            Utilities.signalServers = null;
            lossRateServers[ 0 ] = 0;
            Utilities.pingS( "www.google.com", i + 1, 0, 0, 2000 );

            if ( Utilities.signalServers != null ) {
                result = "TRACERT:<DEST: www.google.com>:<1STIP: " + Utilities.signalServers + ">;";
                (new Report()).sendReport(result);
                result = "";

                for ( int j = 0;j < 10;j++ ) {
                    if ( Utilities.checkStop() ) {
                        return 1;
                    }

                    long reply = Utilities.pingS( Utilities.signalServers, 1, 1, 0, 2000 );

                    if ( reply != -1 )
                        latencyServers[ 0 ] += reply;
                    else
                        lossRateServers[ 0 ] ++;

                    if ( j == 0 ) {
                        result = "SIGNAL:";
                    }

                    result += "<RTT:" + reply + ">";

                    if ( j == 9 )
                        result += ";";
                    else
                        result += ":";

                }

                (new Report()).sendReport(result);
                return 2;
            }
        }
//*/
        return 1;
    }

}
