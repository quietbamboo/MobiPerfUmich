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
import java.util.Random;



class Caching_Compression  {
	public class http_caching_thread extends Thread {
        String FILENAME, serverIP;
        public  http_caching_thread( String name,String serverIP ) {
            super();
            FILENAME = name;
            this.serverIP = serverIP;
        }

        public void run() {
            if ( Utilities.checkStop() ) {
                return ;
            }

            Socket tcpSocket = null;
            DataOutputStream os = null;
            DataInputStream is = null;

            try {
                tcpSocket = new Socket();
                SocketAddress remoteAddr = new InetSocketAddress( serverIP, 80 );
                tcpSocket.connect( remoteAddr, 4000 );
                os = new DataOutputStream( tcpSocket.getOutputStream() );
                is = new DataInputStream( tcpSocket.getInputStream() );
                tcpSocket.setSoTimeout( 2000 );
                tcpSocket.setTcpNoDelay( true );
            }
            catch ( Exception e ) {
                //TODO
                nCachingComplete++;
                return ;
            }

            if ( tcpSocket == null || os == null || is == null ) {
                //TODO
                nCachingComplete++;
                return ;
            }

            int len = 0;

            String buf = "GET /" + FILENAME + " HTTP/1.1\r\nHost:" + "googleftps.com" + "\r\nAccept-Encoding: gzip\r\n\r\n";
            byte[] message = buf.getBytes();

            try {
                byte[] buffer = new byte[ 10000 ];
                int read_bytes = 0;
                os.write( message );
                int flag = 0;

                do {
                    read_bytes = is.read( buffer, 0, 10000 );

                    if ( read_bytes <= 0 )
                        break;

                    len += read_bytes;

                    String reply = new String( buffer, 0, read_bytes );

                    if ( reply != null ) {
                        if ( reply.indexOf( "Content-Encoding: gzip" ) != -1 || reply.indexOf( "Vary: Accept-Encoding" ) != -1 )
                            compressionFlag = true;
                    }

                    if ( read_bytes > 0 ) {
                        if ( flag == 0 ) {
                            flag = 1;
                            nCachingSuccess++;
                        }

                        tcpSocket.setSoTimeout( 200 );
                    }

                }
                while ( read_bytes > 0 );
            }
            catch ( Exception e ) {
                // TODO Auto-generated catch block
                //return 4;
            }

            try {
                tcpSocket.close();
                os.close();
                is.close();
            }
            catch ( Exception e2 ) {
                // TODO Auto-generated catch block
                nCachingComplete++;
                return ;
            }

            nCachingComplete++;
            return ;
        }

    }

    private static final int CACHE_TIMES = 10;
    public static int nCachingSuccess;
    public static int nCachingComplete;
    public static boolean cacheFlag = false;
    public static boolean compressionFlag = false;
    
   public static  int http_caching(String serverIP, int serverPort) {

        nCachingSuccess = 0;
        nCachingComplete = 0;
        Socket tcpSocket = null;
        DataOutputStream os = null;
        DataInputStream is = null;
        cacheFlag = false;
        int nsuccess = 0;
        compressionFlag = false;

        Random generator = new Random();
        int r = generator.nextInt( 10000 );
        String FILENAME = "index" + r + ".html";

        for ( int i = 0;i < CACHE_TIMES;i++ ) {
            if ( Utilities.checkStop() ) {
                return 1;
            }

            Caching_Compression x = new Caching_Compression();
            http_caching_thread thread = x.new http_caching_thread(FILENAME, serverIP);
            thread.start();
        }

        if ( Utilities.checkStop() ) {
            return 1;
        }

        do {
            try {
                if ( Utilities.checkStop() ) {
                    return 1;
                }

                Thread.sleep( 1000 );
            }
            catch ( InterruptedException e ) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        while ( nCachingComplete != CACHE_TIMES );

        if ( Utilities.checkStop() ) {
            return 1;
        }


        try {
            tcpSocket = new Socket();
            SocketAddress remoteAddr = new InetSocketAddress( serverIP, serverPort );
            tcpSocket.connect( remoteAddr, 4000 );
            os = new DataOutputStream( tcpSocket.getOutputStream() );
            is = new DataInputStream( tcpSocket.getInputStream() );
            tcpSocket.setSoTimeout( 4000 );
            tcpSocket.setTcpNoDelay( true );
        }
        catch ( UnknownHostException e ) {
            //TODO
            return 6;
        }
        catch ( Exception e ) {
            //TODO
            return 7;
        }

        if ( tcpSocket == null || os == null || is == null ) {
            //TODO
            return 8;
        }

        byte[] message = FILENAME.getBytes();

        int retval = -1;
outer: {
            try {
                byte[] buffer = new byte[ 10000 ];
                int read_bytes = 0;
                os.write( message );
                read_bytes = is.read( buffer, 0, 10000 );

                if ( read_bytes <= 0 ) {
                    retval = 9;
                    break outer;
                }

                String reply = new String( buffer, 0, read_bytes );

                int i = 0;
                String temp = "";

                while ( i < reply.length() ) {
                    if ( reply.charAt( i ) == ' ' )
                        break;

                    temp += reply.charAt( i++ );

                }

                int ntimes = new Integer( temp );

                if ( ntimes < nsuccess )
                    cacheFlag = true;
            }
            catch ( Exception e ) {
                // TODO Auto-generated catch block
                retval = 10;
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
            // TODO Auto-generated catch block

            if ( retval != -1 )
                return retval;
            else
                return 11;
        }

        return 12;

    }
}
