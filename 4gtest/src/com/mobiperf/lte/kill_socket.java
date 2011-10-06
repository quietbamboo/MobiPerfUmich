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
import android.content.Context;


class kill_socket extends Thread {
	private Context context;
	public kill_socket(Context context){
		this.context = context;
	}
    public void run() {
    	/*
        this.setPriority( MAX_PRIORITY );
        
        try {
            DatagramSocket killSocket = new DatagramSocket();
            Utilities.write_to_file("killsocket.txt", Context.MODE_WORLD_READABLE,"" + killSocket.getLocalPort() + "\n", context);
            killSocket.setReuseAddress( true );
            do {
                byte[] recvbuf = new byte[ 1024 ];
                DatagramPacket rpacket = new DatagramPacket( recvbuf, recvbuf.length );
                killSocket.receive( rpacket );
                String reply = new String( rpacket.getData(), rpacket.getData().length );

                if ( reply.charAt( 0 ) == 'k' ) {
                    threegtest.stopFlag = true;
                    killSocket.close();
                    return ;
                }
                else if ( reply.charAt( 0 ) == 'i' ) {
                    Utilities.write_to_file("uiportfile.txt", Context.MODE_WORLD_READABLE,"" + rpacket.getPort() + "\n", context );
                    Utilities.sendudpmessage( "uiportfile.txt", "yes", context );
                }
            }
            while ( true );
        }
        catch ( Exception e ) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
*/
    }
}
