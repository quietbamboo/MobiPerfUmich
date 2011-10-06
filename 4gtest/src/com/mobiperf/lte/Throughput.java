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
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;

import android.util.Log;


class Throughput  {
	
	public static long uplinkSize;
	public static long uplinkTime;
	
	public static int MeasureUplinkTput( String serverIP,int serverPort ) {
		
		//Junxian: New, ask the server to start tcpdump now to collect 3 way handshake
		(new Report()).sendReport(Definition.COMMAND_TCP_UPLINK);
		
		//sleep for a while waiting the server to be ready
		try {
			Thread.sleep(4000);
		} catch (InterruptedException e4) {
			e4.printStackTrace();
		}
		
		
		uplinkSize = -1;
		
		
		Socket tcpSocket = null;
		DataOutputStream os = null;
		DataInputStream is = null;
		
		try {
		    tcpSocket = new Socket();
		    SocketAddress remoteAddr = new InetSocketAddress( serverIP, serverPort );
		    tcpSocket.connect( remoteAddr, Definition.TCP_TIMEOUT_IN_MILLI );
		    os = new DataOutputStream( tcpSocket.getOutputStream() );
		    is = new DataInputStream( tcpSocket.getInputStream() );
		    tcpSocket.setSoTimeout( Definition.TCP_TIMEOUT_IN_MILLI );
		    tcpSocket.setTcpNoDelay( true );
		
		
		}
		catch ( UnknownHostException e ) {
		    return 1;
		}
		catch ( Exception e ) {
		    //TODO
		    return 2;
		}
		
		if ( tcpSocket == null || os == null || is == null ) {
		    //TODO
		            return 3;
		        }
		
		        int retval = -1;
		outer: {
		            if ( Utilities.checkStop() ) {
		                break outer;
		            }
		
		            long count = 0;
		
		            String buf = "ehcsinmfuevrxruudycdbgdyrlmbapvxpbmzshhkrtewlijrlzbcfobxxugvnjqehdpnsiuwqnbhshrgfgivdyafowedzjlxwxavfsbvzqdddaiorpnpmunqzihrgewqebvnymvujrylymgwliovsyyoozkdgcskedzmhxijruaaurpocsnxtitlcvotrxpvnzwornmoicpaxbobfoehwvirpannjeizbtkizvdgmhgjjkljmjkculysjdvfnsranueaizstwrtuszgfknbsarwkfsrcuhjvzhvcduabphnusscfvqyqpfyndbplpklrwqrpgyitigaeowfnxnfvysdrwjpvbustrltyoqrtunmnxxenmyudvatlevpzsqmfwlzdsglthvwfvldylyktapinzkztygsbzfnbeiimstfjgppamkimryjnxmojdiezuhkvjzgqrfcmhrgcaqyqktvsdxnyptamfmsvghunxbeqlydmnkeqgzgdjjyemgmgxrlsczsuzenyeozgvhhrdawzgvgjueaykkcqlswfcjozucztcyynorcarkhsbgmzodkxjbdejbtxldpaoapyithrskisxyrrcrbuaezveueikvppwzvyvloytphbztcumodlhmvcwdqwtgtnnmlnhmdvpsrnfbbzydikyvamnzxudoeppvhonysvzjccfatxyosaumvgkxdpwsjbtpqcscfyqzruztafodqhfywacsqocckdlssrpnvoycecwvzzsyzbwmnkfpvupudfhrocunyzpytdtvznuskauhaancoylvcezzbgnrayvhwxjocckahppqhotpoccserezellvwijjdqfakcvjknxnjnibdyugxfpsnsrgxmkgbsjyynrdfdifcrxvgcyvtbseipkxhlajjpsmoqjdijeoudfvqpfjwjixfzgdhnkhyahdiuezbpxyjqhblahgwyqosjjqcdbvbqdabrxgmirbtv";
		
		    byte [] message =  InformationCenter.getPrefix().getBytes();
		    
		    Log.v("LOG", "sent prefix for uplink " + InformationCenter.getPrefix());
		    
		    
		
		    try {
		        os.write( message );
		        os.flush();
		    }
		    catch ( Exception e3 ) {
		        // TODO Auto-generated catch block
		        retval = 4;
		        break outer;
		    }
		
		    
		    //don't need to check response
		    try {
				Thread.sleep(4000); //sleep for 4 seconds for server to be ready
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
		
		    message = buf.getBytes();
		    System.out.println ("------- MESSAGE LENGTH = " + message.length);
		    long startTime = System.currentTimeMillis();
		    long endTime = System.currentTimeMillis();
		    
		    //Test lasts 16 seconds - Junxian
		    do {
		        if ( Utilities.checkStop() ) {
		            break outer;
		        }
		
		        count++;
		
		        try {
		            os.write( message );
		        }
		        catch ( Exception e ) {
		            // TODO Auto-generated catch block
		            retval = 5;
		            break outer;
		        }
		
		        endTime = System.currentTimeMillis();
		
		
		    }
		    while ( ( endTime - startTime ) < Definition.TP_DURATION_IN_MILLI );
		
		    uplinkTime = endTime - startTime;
		    uplinkSize = count * ( message.length );
		}
		
		try {
			os.close();
		    is.close();
		    tcpSocket.close();
		
		    if ( retval != -1 )
		        return retval;
		}
		catch ( Exception e2 ) {
		    // TODO Auto-generated catch block
		
		        if ( retval != -1 )
		            return retval;
		        else
		            return 6;
		
		    }
		    return 7;
	}

     public static long downlinkSize;
     public static long downlinkTime;
     
     public static int MeasureDownlinkTput( String serverIP,int serverPort ) {
    	 
    	//Junxian: New, ask the server to start tcpdump now to collect 3 way handshake
    	 (new Report()).sendReport(Definition.COMMAND_TCP_DOWNLINK);
 		
 		//sleep for a while waiting the server to be ready
 		try {
 			Thread.sleep(4000);
 		} catch (InterruptedException e4) {
 			e4.printStackTrace();
 		}
 		
         downlinkSize = -1;
         downlinkTime = -1;
         
         Socket tcpSocket = null;
         DataOutputStream os = null;
         DataInputStream is = null;

         try {
             tcpSocket = new Socket();
             SocketAddress remoteAddr = new InetSocketAddress( serverIP, serverPort );
             tcpSocket.connect( remoteAddr, Definition.TCP_TIMEOUT_IN_MILLI );
             tcpSocket.setSoTimeout( Definition.TCP_TIMEOUT_IN_MILLI );
             tcpSocket.setTcpNoDelay( true );

             os = new DataOutputStream( tcpSocket.getOutputStream() );
             is = new DataInputStream( tcpSocket.getInputStream() );
         }
         catch ( UnknownHostException e ) {
             return 1;
         }
         catch ( Exception e ) {
             return 2;
         }

         if ( tcpSocket == null || os == null || is == null ) {
             return 3;
         }

         int retval = -1;
 outer: {
             if ( Utilities.checkStop() ) {
                 break outer;
             }

             // getting downlink data
             byte [] message =  InformationCenter.getPrefix().getBytes();

             try {

                 os.write( message );
                 os.flush();

             }
             catch ( Exception e3 ) {
                 retval = 4;
                 break outer;
             }
             
			//don't need to check server's response
 		    try {
 				Thread.sleep(2000); //sleep for 2 seconds for server to be ready
 			} catch (InterruptedException e1) {
 				e1.printStackTrace();
 			}

             if ( Utilities.checkStop() ) {
                 break outer;
             }

             long startTime = System.currentTimeMillis();
             long endTime = System.currentTimeMillis();
             long total_read_bytes = 0;

             do {
                 if ( Utilities.checkStop() ) {
                     break outer;
                 }

                 try {
                     byte[] buffer = new byte[ 15000 ];


                     int read_bytes = is.read( buffer, 0, 15000 );

                     if ( read_bytes <= 0 )
                         break;

                     total_read_bytes += read_bytes;


                 }
                 catch ( Exception e ) {
                     e.printStackTrace();
                     break;
                 }

                 endTime = System.currentTimeMillis();
             }
             while ( true );

             if ( total_read_bytes > 1000 ) {
             }
             else {
                 retval = 5;
                 break outer;
             }

             long timespent = ( endTime - startTime );
             downlinkSize = total_read_bytes;
             downlinkTime = timespent;
         }

         try {
             os.close();
             is.close();
             tcpSocket.close();

             if ( retval != -1 )
                 return retval;
         }
         catch ( Exception e2 ) {
             if ( retval != -1 )
                 return retval;
             else
                 return 6;

         }
         return 7;

     }

     
}
