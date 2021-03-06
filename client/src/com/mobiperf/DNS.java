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
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;


class DNS  {
	public static boolean DNSUmich;
	public static int dnsLookupToExternalServer(String serverIP, int serverPort ) {

        DNSUmich = false;
        InetAddress udpaddress;

        try {
            udpaddress = InetAddress.getByName( serverIP );
        }
        catch ( UnknownHostException e1 ) {
            return 2;
        }

        byte [] udpmessage = new byte[] {56, 44, 1, 0, 0, 1, 0, 0, 0, 0, 0, 0, 2, 103, 111, 3, 99, 111, 109, 0, 0, 1, 0, 1};

        DatagramPacket packet = new DatagramPacket( udpmessage, udpmessage.length, udpaddress, serverPort );
        DatagramSocket udpSocket = null;

        try {
            udpSocket = new DatagramSocket();
            udpSocket.setSoTimeout( 4000 );
            udpSocket.send( packet );

        }
        catch ( Exception e ) {
            return 3;
        }

        byte[] buffer = new byte[ 2048 ];
        DatagramPacket packetr = new DatagramPacket( buffer, buffer.length );

        try {
            udpSocket.receive( packetr );
        }
        catch ( Exception e ) {
            return 4;
        }

        udpSocket.close();

        if ( packetr.getAddress().getHostAddress().toString().equals( udpaddress.getHostAddress().toString() ) ) {
            if ( packetr.getData().toString().length() > 0 ) {
                DNSUmich = true;
            }

            return 5;
        }
        else {
            return 6;
        }

    }
	
	public static boolean DNSGoogle;
	public static int Google() {
        InetAddress addrDNS = null;

        try {
            addrDNS = InetAddress.getByName( "www.yahoo.com" );
        }
        catch ( Exception e ) {
            return 2;
        }

        if ( addrDNS == null ) {
            return 3;
        }


        DNSGoogle = true;
        return 4;
    }
	
    private static InetAddress addrDNS;
    private static String hostname;
    //private static Handler mHandler = new Handler();
    public static int nPOPDNS = 80;


    private static long checkDNS( String name, String address ) {
    	class DNSChecker extends Thread
	    {
	    	public long strttime;
	    	public long endtime;
	    	public String hostname;
	    	public InetAddress addrDNS;
	    	public DNSChecker(String  name){
	    		this.hostname = name;
	    		this.addrDNS = null;
	    		}
	    	public void run()
	    	{
	    		try {
		        	   this.strttime = System.currentTimeMillis();
		               this.addrDNS = InetAddress.getByName( hostname );
		               this.endtime = System.currentTimeMillis();
		           }
		           catch ( UnknownHostException e ) {}
	    	}
	    }
        //DNSChecker t = new DNSChecker(name);
        DNSChecker t = new DNSChecker(name);
        t.start();
        try {
			t.join(Definition.UDP_TIMEOUT_IN_MILLI);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		if(t.addrDNS == null)
		{
			return System.currentTimeMillis() - t.strttime;
		}else
		{
			return t.endtime - t.strttime;
		}
    }
    /*
    private static long checkDNS( String name, String address ) {
        addrDNS = null;
        hostname = name;
        long strttime, endtime;
        strttime = System.currentTimeMillis();
        Threegtest_Service.mHandler.post( new Runnable() {
                           public void run() {
                               try {
                                   addrDNS = InetAddress.getByName( hostname );
                               }
                               catch ( UnknownHostException e ) {}

                           }

                       }

                     );

        endtime = System.currentTimeMillis();

        while ( endtime - strttime < 2000 && addrDNS == null ) {
            endtime = System.currentTimeMillis();
        }

        return ( endtime -strttime );


    }
*/
    public static boolean [] DNSpop = new boolean[ nPOPDNS ];
    public static long [] DNSlatencies = new long[ nPOPDNS ];
    private static final String[] POP_DNS_NAMES = new String[] {
        "www.google-analytics.com", "p.admob.com", "www.facebook.com", "maps.yahoo.com",
        "mt2.google.com", "us.js2.yimg.com", "en.wikipedia.org", "sports.espn.go.com",
        "www.weather.com", "maps.google.com", "m.webtrends.com", "ads.bluelithium.com",
        "img.turn.com", "mt3.google.com", "static.mobile.espn.go.com", "www.wikipedia.org",
        "xhtml.weather.com", "us.mc599.mail.yahoo.com", "i.cdn.turner.com", "i.imwx.com",
        "login.live.com", "m.myspace.com", "www.cnn.com", "home.mobile.msn.com",
        "mobile.live.com", "ads.yimg.com", "mail.yahoo.com", "metrics.nba.com",
        "www.live.com", "www.nba.com", "s.ytimg.com", "ad.trafficmp.com",
        "i1.ytimg.com", "mobile.msn.com", "gfx1.hotmail.com", "adopt.euroclick.com",
        "m.facebook.com", "m.go.com", "content.dl-rms.com", "view.atdmt.com",
        "aglobal.go.com", "www.google.com", "upload.wikimedia.org", "r1.beta.ace.advertising.com",
        "www.myspace.com", "a248.e.akamai.net", "pr.atwola.com", "us.i1.yimg.com",
        "ia.media-imdb.com", "bp.specificclick.net", "www.blogger.com", "www.msn.com",
        "www.aolcdn.com", "mail.live.com", "uac.advertising.com", "www.go.com",
        "action.mathtag.com", "m.live.com", "media.adrevolver.com", "m1.2mdn.net",
        "mt1.google.com", "www.hotmail.com", "streak.espn.go.com", "i.media-imdb.com",
        "m.youtube.com", "ll.atdmt.com", "ad.turn.com", "cdn.fastclick.net",
        "www.amazon.com", "ssl.google-analytics.com", "blstb.msn.com", "mobile.microsoft.com",
        "www.mapquest.com", "ia.imdb.com", "core.insightexpressai.com", "i4.ytimg.com",
        "adsatt.go.starwave.com", "m.espn.go.com", "m.cnn.com", "log.go.com"
    };
    public static int Popular() {
        String[] POPDNSAddresses = new String[ 500 ];

        if ( Utilities.checkStop() ) {
            return 1;
        }

        for ( int i = 0;i < nPOPDNS;i++ ) {
            if ( Utilities.checkStop() ) {
                return 1;
            }

            DNSpop[ i ] = false;
            long flag = 3000;
            DNSlatencies[ i ] = 0;

            for ( int j = 0;j < 2;j++ ) {
                flag = checkDNS( POP_DNS_NAMES[ i ], POPDNSAddresses[ i ] );

                if ( flag < 2000 ) {
                    DNSlatencies[ i ] = flag;
                    DNSpop[ i ] = true;
                    break;
                }
            }

            if ( flag >= 2000 ) {
                //TODO
                DNSpop[ i ] = false;
            }
        }

        return 2;
    }
    
    
    private static InetAddress addr = null;
    /**
     * Query unique umich dns domain
     * @param localExperiment, if true, each url has a new RID, if false, use the application generated rid
     */
    public static void LookupUniqueUrl(boolean isLocalExperiment){
		long strttime = System.currentTimeMillis();
		// threegtest.mHandler.post( new Runnable() {
		// public void run() {
		String host = "gphone_" + InformationCenter.getDeviceID() + "_";
		try {
			if (!isLocalExperiment) {
				host += InformationCenter.getRunId() + ".eecs.umich.edu";
			}else {
				// local experiment
				host += "" + System.currentTimeMillis() + "_local.eecs.umich.edu";
			}
			addr = InetAddress.getByName(host);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		// }
		// });

		long endtime = System.currentTimeMillis();
		// Junxian: what does this mean?
		// while ( endtime - strttime < 2000 && addr == null ) {
		// endtime = System.currentTimeMillis();
		// }
		// String result = null;
		// Junxian: why not report back response time??
		// if ( addr == null ) {
		// result = "DNS-Unique:<" + "gphone_" + Definition.getDeviceID() + "_"
		// + Definition.getRunId() + ".eecs.umich.edu" + ":0.00>;";
		// }
		// else {
		String result = "DNSUniqueLookup:<URL:" + host +">:" + "<ResponseTime:" + (endtime - strttime) + ">;";
		// }
		(new Report()).sendReport(result);
    }
     
}
