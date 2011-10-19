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
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;

import android.content.Context;


class Landmark  {
	public static int completePingLandmarkServers; 
    public static int successPingLandmarkServers;
    public static int averagePingLandmarkServers;
    public static int nLandmarkServers = 100;
    public static String[] landmarkServers = new String[ 100 ]; 
    public static long[] latencyLandmarkServers = new long[ nLandmarkServers ];
    public static int[] lossRateLandmarkServers = new int[ nLandmarkServers ];
    
    
	public static int latitude_landmark() {
        /*TODO
        completePingLandmarkServers = 0;
        successPingLandmarkServers = 0;
        averagePingLandmarkServers = 0;

        if ( Utilities.pingS( "www.google.com", 5, 1, 0, 4000 ) == -1 ) {
            for ( int i = 0;i < nLandmarkServers;i++ ) {
                latencyLandmarkServers[ i ] = -1 ;
                lossRateLandmarkServers[ i ] = 2;
            }

            return 1;
        }

        if ( Utilities.checkStop() ) {
            return 1;
        }

        for ( int i = 0;i < nLandmarkServers;i++ ) {
            if ( Utilities.checkStop() ) {
                return 1;
            }

            //new latitude_landmark_thread(i).start();
            latencyLandmarkServers[ i ] = 0 ;

            lossRateLandmarkServers[ i ] = 0;

            for ( int j = 0;j < 2;j++ ) {
                if ( Utilities.checkStop() ) {
                    return 1;
                }

                long reply = Utilities.pingS( landmarkServers[ i ], 1, 1, i, 2000 );
                if ( reply != -1 ) {
                    latencyLandmarkServers[ i ] += reply;
                    successPingLandmarkServers++;
                    averagePingLandmarkServers += reply;
                }
                else {
                    lossRateLandmarkServers[ i ] ++;
                }
            }
        }

        if ( Utilities.checkStop() ) {
            return 1;
        }

        if ( successPingLandmarkServers != 0 )
            averagePingLandmarkServers /= successPingLandmarkServers;
        //*/
        return 2;
    }

	public static int successHTTPLandmarkServers;
    public static int averageHTTPLandmarkServers;
    public static int averageHTTPLandmarkServers2;
    public static long[] SYNACKLandmarkServers = new long[ nLandmarkServers ];
    public static long[] getsLandmarkServers = new long[ nLandmarkServers ];
    public static long[] gettLandmarkServers = new long[ nLandmarkServers ];
    public static int http_landmark() {
        Socket tcpSocket = null;
        DataOutputStream os = null;
        DataInputStream is = null;
        long strTime = 0, endTime;
        averageHTTPLandmarkServers = 0;
        averageHTTPLandmarkServers2 = 0;
        successHTTPLandmarkServers = 0;

        if ( Utilities.checkStop() ) {
            return 1;
        }

        if ( Utilities.checkStop() ) {
            return 1;
        }

        for ( int i = 0;i < nLandmarkServers;i++ ) {
            if ( Utilities.checkStop() ) {
                return 1;
            }

            SYNACKLandmarkServers[ i ] = -1;
            getsLandmarkServers[ i ] = -1;
            gettLandmarkServers[ i ] = -1;

            try {
                tcpSocket = new Socket();
                SocketAddress remoteAddr = new InetSocketAddress( landmarkServers[ i ], 80 );
                strTime = System.currentTimeMillis();
                tcpSocket.connect( remoteAddr, 2000 );
                endTime = System.currentTimeMillis();
                SYNACKLandmarkServers[ i ] = endTime - strTime;
                averageHTTPLandmarkServers += SYNACKLandmarkServers[ i ];
                successHTTPLandmarkServers++;
                os = new DataOutputStream( tcpSocket.getOutputStream() );
                is = new DataInputStream( tcpSocket.getInputStream() );
                tcpSocket.setSoTimeout( 2000 );
                tcpSocket.setTcpNoDelay( true );
            }
            catch ( UnknownHostException e ) {
                //TODO
            }
            catch ( Exception e ) {
                //TODO
            }

            if ( tcpSocket == null || os == null || is == null ) {
                //TODO
            }

            else {
                int len = 0;
                String buf = "GET /index.html HTTP/1.1\r\nHost:" + landmarkServers[ i ] + "\r\nAccept-Encoding: gzip\r\n\r\n";
                byte[] message = buf.getBytes();
                endTime = System.currentTimeMillis();

                try {
                    byte[] buffer = new byte[ 100000 ];
                    int read_bytes = 0;
                    os.write( message );
                    strTime = System.currentTimeMillis();

                    do {
                        read_bytes = is.read( buffer, 0, 100000 );

                        if ( Utilities.checkStop() ) {
                            return 1;
                        }

                        if ( read_bytes != -1 ) {
                            endTime = System.currentTimeMillis();
                            tcpSocket.setSoTimeout( 1000 );
                            len += read_bytes;
                        }
                    }
                    while ( read_bytes > 0 );
                }
                catch ( Exception e ) {
                    // TODO Auto-generated catch block
                }

                if ( len > 0 ) {
                    getsLandmarkServers[ i ] = len;
                    gettLandmarkServers[ i ] = endTime - strTime;
                    averageHTTPLandmarkServers2 += gettLandmarkServers[ i ];
                }

                try {
                    tcpSocket.close();
                    os.close();
                    is.close();
                }
                catch ( Exception e2 ) {
                    // TODO Auto-generated catch block
                }
            }

        }

        if ( successHTTPLandmarkServers != 0 ) {
            averageHTTPLandmarkServers /= successHTTPLandmarkServers;
            averageHTTPLandmarkServers2 /= successHTTPLandmarkServers;
        }

        return 2;


    }
	
    public static void config_file_check(Context context, String serverIP) {
        Socket tcpSocket = null;
        DataOutputStream os = null;
        DataInputStream is = null;
        String line = "";
outer: {
            

            try {
               
                //mhayter
            	FileInputStream reader = context.openFileInput( "config.txt" );
                BufferedReader bufferedReader = new BufferedReader (new InputStreamReader (reader));
                if ((line = bufferedReader.readLine())!=null) {
                	//mhayter
                	bufferedReader.close();
                    reader.close();
                    Utilities.writeToFile("config.txt", Context.MODE_WORLD_READABLE ,"VERSION:2" + "\n"+"LANDMARK:168.143.162.68,208.80.152.2,74.200.243.254,204.58.233.97,204.155.175.116,69.192.18.125,64.114.206.23,74.86.111.11,66.28.86.67,216.128.7.153,161.58.199.132,199.48.6.72,216.148.129.3,74.125.95.147,139.72.40.50,198.148.166.1,208.73.210.81,137.201.240.50,169.153.202.100,65.182.192.141,206.83.161.134" + "\n", context);
                    break outer;
                }
                else {
                	//line = bufferedReader.readLine();
                    reader.close();
                    bufferedReader.close();
                }
            }
            catch ( Exception e ) {
            	Utilities.writeToFile("config.txt", Context.MODE_APPEND,"VERSION:2" + "\n"+ "LANDMARK:168.143.162.68,208.80.152.2,74.200.243.254,204.58.233.97,204.155.175.116,69.192.18.125,64.114.206.23,74.86.111.11,66.28.86.67,216.128.7.153,161.58.199.132,199.48.6.72,216.148.129.3,74.125.95.147,139.72.40.50,198.148.166.1,208.73.210.81,137.201.240.50,169.153.202.100,65.182.192.141,206.83.161.134" + "\n", context );
                break outer;
            }

            try {
                tcpSocket = new Socket();
                SocketAddress remoteAddr = new InetSocketAddress( serverIP, 3000 );
                tcpSocket.connect( remoteAddr, 4000 );
                os = new DataOutputStream( tcpSocket.getOutputStream() );
                is = new DataInputStream( tcpSocket.getInputStream() );
                tcpSocket.setSoTimeout( 2000 );
                tcpSocket.setTcpNoDelay( true );
            }
            catch ( Exception e ) {
                //TODO
                break outer;
            }

            if ( tcpSocket == null || os == null || is == null ) {
                //TODO
                break outer;
            }

inner: {

                byte[] message = line.getBytes();

                try {
                    byte[] buffer = new byte[ 10000 ];
                    int read_bytes = 0;
                    os.write( message );
                    read_bytes = is.read( buffer, 0, 10000 );

                    if ( read_bytes <= 0 )
                        break inner;

                    String reply = new String( buffer, 0, read_bytes );

                    if ( reply.equals( "UP-TO-DATE" ) ) {
                    }
                    else {
                        reply += "\n";
                        FileOutputStream fOut = null;
                        OutputStreamWriter out = null;
                        fOut = context.openFileOutput( "config.txt",Context.MODE_WORLD_READABLE );
                        out = new OutputStreamWriter( fOut );
                        out.write( reply );
                        read_bytes = is.read( buffer, 0, 10000 );

                        if ( read_bytes <= 0 )
                            break inner;

                        reply = new String( buffer, 0, read_bytes );

                        reply += "\n";

                        out.write( reply );

                        out.close();

                    }


                }
                catch ( Exception e ) {
                    // TODO Auto-generated catch block
                    break inner;
                }
            }

            try {
                tcpSocket.close();
                os.close();
                is.close();
            }
            catch ( Exception e2 ) {
                // TODO Auto-generated catch block
                break outer;
            }
        }


        try {
        	BufferedReader bufferedReader = new BufferedReader (new InputStreamReader (context.openFileInput ("config.txt")));
        	line = bufferedReader.readLine();
            line = "";
            line = bufferedReader.readLine();
            bufferedReader.close();
        }
        catch ( Exception e ) {
            //TODO Auto-generated catch block
        }

        int index = line.indexOf( ":" );
        int i = 1;
        char c;
        nLandmarkServers = 0;

        do {
        	landmarkServers[ nLandmarkServers ] = "";

            do {
                c = line.charAt( index + i );

                if ( c != ',' && c != '\n' )
                	landmarkServers[ nLandmarkServers ] += c;

                i++;
            }
            while ( c != ',' && ( index + i ) < line.length() );


            nLandmarkServers++;

            if ( ( index + i ) >= line.length() )
                break;
        }
        while ( true );

        return ;
    }


     
}
