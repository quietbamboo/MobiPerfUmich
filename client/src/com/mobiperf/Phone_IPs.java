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
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;


class Phone_IPs  {
	public static String localIP,seenIP;
	public static int Get_Phone_IPs( String serverIP, int serverPort ) {

        localIP = null;
        Socket tcpSocket = null;
        DataOutputStream os = null;
        DataInputStream is = null;
        SocketAddress remoteAddr = null;

        try {
            tcpSocket = new Socket();
            remoteAddr = new InetSocketAddress( serverIP, serverPort );
            tcpSocket.connect( remoteAddr, 8000 );
            os = new DataOutputStream( tcpSocket.getOutputStream() );
            is = new DataInputStream( tcpSocket.getInputStream() );
            tcpSocket.setSoTimeout( 4000 );

        }
        catch ( UnknownHostException e ) {
            return 2;
        }
        catch ( Exception e ) {
            return 3;
        }

        if ( tcpSocket == null || os == null || is == null )
            return 4;

        seenIP = null ;
        int retval = -1;
outer: {
            try {
                byte [] messag = InformationCenter.getPrefix().getBytes();
                os.write( messag );
            }
            catch ( Exception e3 ) {
                retval = 4;
                break outer;
            }

            localIP = tcpSocket.getLocalAddress().getHostAddress();

            try {
            	
                byte[] buffer = new byte[ 1000 ];
                int read_bytes = is.read( buffer, 0, 1000 );

                if ( read_bytes <= 0 ) {
                    retval = 5;
                    break outer;
                }

                seenIP = new String( buffer, 0, read_bytes );

            }
            catch ( Exception e ) {
                retval = 5;
                break outer;
            }


        }

        try {
            tcpSocket.close();
            os.close();
            is.close();

            if ( retval != -1 )
                return retval;
        }
        catch ( Exception e2 ) {
            if ( retval != -1 )
                return retval;
            else
                return 6;

        }

        if(localIP != null && seenIP != null){
            if (localIP.equals(seenIP))
                return 7;
            else
                return 8;
        }else
            return 6;
    }
     
}
