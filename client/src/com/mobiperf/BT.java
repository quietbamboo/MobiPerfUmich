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

//Commented by Junxian temporarily since old BT files are removed
/*
class BT  {
	public static String BTBlockedStage;
	public static int BTptosUp( String serverIP, int serverPort, Context context ) {
        BTBlockedStage = "S";
        int nofPackets = 0;
        boolean timeoutflag = false;
        char [] cs = new char[ 1000 ];
        int [] size = new int[ 1000 ];
        // reading size file

        if ( Utilities.checkStop() ) {
            return 1;
        }

        try {
        	InputStream reader = context.getResources().openRawResource( R.raw.bts );
        	BufferedReader bufferedReader = new BufferedReader ( new InputStreamReader (reader));
        	String line = "";
            while ( (line = bufferedReader.readLine())!=null ) {
                if (  Utilities.checkStop() ) {
                    return 1;
                }
                //line = bufferedReader.readLine();
                cs[ nofPackets ] = line.charAt( 0 );
                String temp = "";
                int j = 0;
                String leng = "" + line.charAt( 2 );
                int l = new Integer( leng );
                while ( j < l ) {
                    temp += line.charAt( 3 + j );
                    j++;
                }
                size[ nofPackets++ ] = new Integer( temp );
            }
        }
        catch ( Exception e ) {
            //TODO Auto-generated catch block
            return 1;
        }
        if (  Utilities.checkStop() ) {
            return 1;
        }
        Socket tcpSocket = null;
        DataOutputStream os = null;
        DataInputStream is = null;

        try {
            tcpSocket = new Socket();
            SocketAddress remoteAddr = new InetSocketAddress( serverIP, serverPort );
            tcpSocket.connect( remoteAddr, 4000 );
            os = new DataOutputStream( tcpSocket.getOutputStream() );
            is = new DataInputStream( tcpSocket.getInputStream() );
            tcpSocket.setSoTimeout( 4000 );

        }
        catch ( UnknownHostException e ) {
            //TODO
            BTBlockedStage = "SYN";
            return 2;
        }
        catch ( Exception e ) {
            //TODO
            BTBlockedStage = "SYN";
            return 3;
        }

        if ( tcpSocket == null || os == null || is == null ) {
            //TODO
            BTBlockedStage = "SYN";
            return 4;
        }

outer: {
            if (  Utilities.checkStop() ) {
                break outer;
            }

            InputStream reader = null;

            try {
                reader = context.getResources().openRawResource( R.raw.bt );
            }
            catch ( Exception e1 ) {
                // TODO Auto-generated catch block
                break outer;
            }

            //sending prefix and getting reply
            try {
                if (  Utilities.checkStop() ) {
                    break outer;
                }

                byte [] message =  InformationCenter.getPrefix().getBytes();
                os.write( message );

                byte[] buffer = new byte[ 1000 ];
                int read_bytes = is.read( buffer, 0, 1000 );

                if ( read_bytes <= 0 ) {
                    BTBlockedStage = "Hdsk";
                    break outer;
                }

                String reply = new String( buffer, 0, read_bytes );

                if ( reply.equals( "PrefixOK" ) == false ) {
                    BTBlockedStage = "Hdsk";
                    break outer;
                }

            }
            catch ( Exception e1 ) {
                // TODO Auto-generated catch block
                BTBlockedStage = "Hdsk";
                break outer;
            }

            if (  Utilities.checkStop() ) {
                break outer;
            }

            try {
                byte [] message = "UplinkStart".getBytes();
                os.write( message );

            }
            catch ( Exception e1 ) {
                // TODO Auto-generated catch block
                BTBlockedStage = "Hdsk";
                break outer;
            }

            if (  Utilities.checkStop() ) {
                break outer;
            }

            byte [] buf = new byte[ 10000 ];
            //sending actual data now

            try {
                Thread.sleep( 2000 );
            }
            catch ( InterruptedException e1 ) {
                e1.printStackTrace();
            }

            if (  Utilities.checkStop() ) {
                break outer;
            }

            long strt, end;
            strt =  System.currentTimeMillis();

            for ( int i = 0;i < nofPackets;i++ ) {
                if (  Utilities.checkStop() ) {
                    break outer;
                }

                if ( cs[ i ] == 's' ) {
                    try {
                        reader.read( buf, 0, size[ i ] );
                        os.write( buf, 0, size[ i ] );

                    }
                    catch ( Exception e ) {
                        e.printStackTrace();

                        if ( i == 1 )
                            BTBlockedStage = "Hdsk";

                        if ( i == 3 )
                            BTBlockedStage = "Cont";

                        if ( i >= 5 )
                            BTBlockedStage = "Req";
                        break outer;
                    }
                }
                else {
                    try {
                        byte[] buffer = new byte[ 1000 ];
                        int len = 0;

                        if ( size[ i ] > 1000 ) {
                            do {
                                int read_bytes = is.read( buffer, 0, 1000 );
                                len += read_bytes;

                                if (  Utilities.checkStop() ) {
                                    timeoutflag = true;
                                    break outer;
                                }

                                if ( read_bytes <= 0 ) {

                                    if ( i == 0 )
                                        BTBlockedStage = "Hdsk";

                                    if ( i == 2 )
                                        BTBlockedStage = "Cont";

                                    if ( i >= 4 )
                                        BTBlockedStage = "Piece";

                                    break outer;
                                }
                            }
                            while ( size[ i ] - len > 1000 );
                        }

                        do {
                            int read_bytes = is.read( buffer, 0, size[ i ] - len );
                            len += read_bytes;

                            if (  Utilities.checkStop() ) {
                                timeoutflag = true;
                                break outer;
                            }

                            if ( read_bytes <= 0 ) {
                                if ( i == 0 )
                                    BTBlockedStage = "Hdsk";

                                if ( i == 2 )
                                    BTBlockedStage = "Cont";

                                if ( i >= 4 )
                                    BTBlockedStage = "Piece";

                                break outer;
                            }
                        }
                        while ( size[ i ] - len != 0 );

                        end =  System.currentTimeMillis();

                        if ( end - strt > 8000 ) {
                            timeoutflag = true;
                            break;
                        }

                        //String reply = new String(buffer,0,read_bytes);
                    }
                    catch ( Exception e ) {
                        e.printStackTrace();
                        if ( i == 0 )
                            BTBlockedStage = "Hdsk";

                        if ( i == 2 )
                            BTBlockedStage = "Cont";

                        if ( i >= 4 )
                            BTBlockedStage = "Piece";

                        break outer;
                    }
                }
            }
        }

        if ( timeoutflag ) {
            try {
                byte [] message = "UplinkDone".getBytes();
                os.write( message );

            }
            catch ( Exception e1 ) {
                // TODO Auto-generated catch block
                BTBlockedStage = "E";
                return 12;
            }
        }

        try {
            tcpSocket.close();
            os.close();
            is.close();
        }
        catch ( Exception e2 ) {
            // TODO Auto-generated catch block
            BTBlockedStage = "E";
            return 13;

        }

        if ( BTBlockedStage.equals( "S" ) ) {
            BTBlockedStage = "E";
        }

        return 14;

    }
	
	public static int BTptosDown( String serverIP, int port , Context context) {
        BTBlockedStage = "S";
        int nofPackets = 0;
        char [] cs = new char[ 1000 ];
        int [] size = new int[ 1000 ];
        boolean timeoutflag = false;
        try {
            if ( Utilities.checkStop() ) {
                return 1;
            }
            //mhayter
            InputStream reader = context.getResources().openRawResource( R.raw.bts );
            BufferedReader bufferedReader = new BufferedReader (new InputStreamReader(reader));
            String line = "";
            
            
            //reader = this.getResources().openRawResource( R.raw.bts );
            //byte[] buffer = new byte[ 1000 ];
            
            //change reader.available() != 0 -> !line.equals(""); 
            
            while ( (line = bufferedReader.readLine())!=null ) {
                if ( Utilities.checkStop() ) {
                    return 1;
                }
                //mhayter
               // line = bufferedReader.readLine();
                
                cs[ nofPackets ] = line.charAt( 0 );

                String temp = "";

                int j = 0;

                String leng = "" + line.charAt( 2 );

                int lk = new Integer( leng );

                while ( j < lk ) {
                    temp += line.charAt( 3 + j );
                    j++;
                }

                size[ nofPackets++ ] = new Integer( temp );
                
                //mhayter added
                //line = bufferedReader.readLine();
            }
        }
        catch ( Exception e ) {
            //TODO Auto-generated catch block
            return 1;
        }

        Socket tcpSocket = null;
        DataOutputStream os = null;
        DataInputStream is = null;

        try {
            if ( Utilities.checkStop() ) {
                return 1;
            }

            tcpSocket = new Socket();

            SocketAddress remoteAddr = new InetSocketAddress( serverIP, port );
            tcpSocket.connect( remoteAddr, 4000 );
            os = new DataOutputStream( tcpSocket.getOutputStream() );
            is = new DataInputStream( tcpSocket.getInputStream() );
            tcpSocket.setSoTimeout( 4000 );

        }
        catch ( UnknownHostException e ) {
            //TODO
            BTBlockedStage = "SYN";
            return 2;
        }
        catch ( Exception e ) {
            //TODO
            BTBlockedStage = "SYN";
            return 3;
        }

        if ( tcpSocket == null || os == null || is == null ) {
            //TODO
            BTBlockedStage = "SYN";
            return 4;
        }

outer: {

            if ( Utilities.checkStop() ) {
                break outer;
            }

            InputStream reader = null;

            try {
                reader = context.getResources().openRawResource( R.raw.btsvr );
            }
            catch ( Exception e1 ) {
                // TODO Auto-generated catch block
                break outer;
            }

            //sending prefix and getting reply
            try {
                byte [] message = InformationCenter.getPrefix().getBytes();
                os.write( message );
                byte[] buffer = new byte[ 1000 ];
                int read_bytes = is.read( buffer, 0, 1000 );

                if ( read_bytes <= 0 ) {
                    BTBlockedStage = "Hdsk";
                    break outer;
                }

                String reply = new String( buffer, 0, read_bytes );

                if ( reply.equals( "PrefixOK" ) == false ) {
                    BTBlockedStage = "Hdsk";
                    break outer;
                }

            }
            catch ( Exception e1 ) {
                // TODO Auto-generated catch block
                BTBlockedStage = "Hdsk";
                break outer;
            }

            if ( Utilities.checkStop() ) {
                break outer;
            }

            try {

                byte [] message = "Downlink".getBytes();

                os.write( message );

                byte[] buffer = new byte[ 1000 ];
                int read_bytes = is.read( buffer, 0, 1000 );

                if ( read_bytes <= 0 ) {
                    BTBlockedStage = "Hdsk";
                    break outer;
                }

                String reply = new String( buffer, 0, read_bytes );

                if ( reply.equals( "DownlinkOK" ) == false ) {
                    BTBlockedStage = "Hdsk";
                    break outer;
                }

            }
            catch ( Exception e1 ) {
                // TODO Auto-generated catch block
                BTBlockedStage = "Hdsk";
                break outer;
            }

            if ( Utilities.checkStop() ) {
                break outer;
            }

            byte [] buf = new byte[ 10000 ];
            //sending actual data now
            int count = 0;

            try {
                Thread.sleep( 2000 );
            }
            catch ( InterruptedException e1 ) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }

            long strt, end;
            strt = System.currentTimeMillis();

            if ( Utilities.checkStop() ) {
                break outer;
            }

            for ( int i = 0;i < nofPackets;i++ ) {
                if ( Utilities.checkStop() ) {
                    break outer;
                }

                if ( cs[ i ] == 'c' ) {
                    try {
                        reader.read( buf, 0, size[ i ] );
                        os.write( buf, 0, size[ i ] );


                    }
                    catch ( Exception e ) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                        if ( i == 0 )
                            BTBlockedStage = "Hdsk";

                        if ( i == 2 )
                            BTBlockedStage = "Cont";

                        if ( i >= 4 )
                            BTBlockedStage = "Req";

                        break outer;
                    }
                }
                else {
                    try {
                        byte[] buffer = new byte[ 1000 ];
                        int len = 0;

                        if ( size[ i ] > 1000 ) {
                            do {
                                int read_bytes = is.read( buffer, 0, 1000 );
                                len += read_bytes;

                                if ( Utilities.checkStop() ) {
                                    timeoutflag = true;
                                    break outer;
                                }

                                if ( read_bytes <= 0 ) {
                                    if ( i == 1 )
                                        BTBlockedStage = "Hdsk";

                                    if ( i == 3 )
                                        BTBlockedStage = "Cont";

                                    if ( i >= 5 )
                                        BTBlockedStage = "Piece";

                                    break outer;
                                }
                            }
                            while ( size[ i ] - len > 1000 );
                        }

                        do {
                            int read_bytes = is.read( buffer, 0, size[ i ] - len );

                            if ( Utilities.checkStop() ) {
                                timeoutflag = true;
                                break outer;
                            }

                            count++;

                            if ( read_bytes <= 0 ) {
                                if ( read_bytes <= 0 ) {
                                    if ( i == 1 )
                                        BTBlockedStage = "Hdsk";

                                    if ( i == 3 )
                                        BTBlockedStage = "Cont";

                                    if ( i >= 5 )
                                        BTBlockedStage = "Piece";

                                    break outer;
                                }
                            }

                            len += read_bytes;

                        }
                        while ( size[ i ] - len != 0 );

                        end = System.currentTimeMillis();

                        if ( end - strt > 8000 ) {
                            timeoutflag = true;
                            break outer;
                        }

                        //String reply = new String(buffer,0,read_bytes);
                    }
                    catch ( Exception e ) {
                        e.printStackTrace();

                        if ( i == 1 )
                            BTBlockedStage = "Hdsk";

                        if ( i == 3 )
                            BTBlockedStage = "Cont";

                        if ( i >= 5 )
                            BTBlockedStage = "Piece";

                        break outer;

                    }
                }
            }
        }

        if ( timeoutflag ) {

            try {
                byte [] message = "DownlinkDone".getBytes();
                os.write( message );
            }
            catch ( Exception e1 ) {
                // TODO Auto-generated catch block
                BTBlockedStage = "E";
            }
        }

        try {
            tcpSocket.close();
            os.close();
            is.close();
        }
        catch ( Exception e2 ) {
            // TODO Auto-generated catch block
            return 10;

        }


        if ( BTBlockedStage.equals( "S" ) ) {
            BTBlockedStage = "E";
        }

        return 11;

    }

    public static int BTRandom( String serverIP, int serverPort) {

        Socket tcpSocket = null;
        DataOutputStream os = null;
        DataInputStream is = null;
        int retval = -1;

        try {
            if ( Utilities.checkStop() ) {
                return 1;
            }

            tcpSocket = new Socket();
            SocketAddress remoteAddr = new InetSocketAddress( serverIP, serverPort );
            tcpSocket.connect( remoteAddr, 4000 );
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

        if ( tcpSocket == null || os == null || is == null ) {
            return 4;
        }

outer: {
            if ( Utilities.checkStop() ) {
                break outer;
            }

            //sending prefix and getting reply
            try {
                byte [] message = InformationCenter.getPrefix().getBytes();
                os.write( message );
                os.flush();
                byte[] buffer = new byte[ 1000 ];
                int read_bytes = is.read( buffer, 0, 1000 );

                if ( read_bytes <= 0 ) {
                    retval = 5;
                    break outer;
                }

                String reply = new String( buffer, 0, read_bytes );

                if ( reply.equals( "PrefixOK" ) == false ) {
                    retval = 5;
                    break outer;
                }

            }
            catch ( Exception e1 ) {
                retval = 5;
                break outer;
            }

            if ( Utilities.checkStop() ) {
                break outer;
            }

            try {
                byte [] message = "Random".getBytes();
                os.write( message );
                byte[] buffer = new byte[ 1000 ];
                int read_bytes = is.read( buffer, 0, 1000 );

                if ( read_bytes <= 0 ) {
                    retval = 6;
                    break outer;
                }

                String reply = new String( buffer, 0, read_bytes );

                if ( reply.equals( "RandomOK" ) == false ) {
                    retval = 6;
                    break outer;
                }
            }
            catch ( Exception e1 ) {
                // TODO Auto-generated catch block
                retval = 6;
                break outer;
            }

            if ( Utilities.checkStop() ) {
                break outer;
            }

            try {
                Thread.sleep( 2000 );
            }
            catch ( InterruptedException e1 ) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }

            //sending actual data now
            if ( Utilities.checkStop() ) {
                break outer;
            }

            long strt, end;
            strt = System.currentTimeMillis();
            String str = "otamoomhvzspkkgraexgrvygrtvvpgplucbufwwrsmgbjjvnlokorclxrlddqaoseybhgcehgedjburvnkedyrwssftusyghcikqqqqqojivkhlbeluzfzbtoyvaehpvkcjoxvvgwhlzhlxqrhfkwmhltecyovgxmtfsptdpfusxbfcozscxvtsocpfnfdyvlwppdlxsfipxkocfbajigpwxbgkxmmcuqsigwfipiumkmilvjvygappzhsfvtxtanzgljbifugscjnkpcxtyvvyxypspclrkqvjrauslthqjnodoymfzetqtmfwtxothviehvyurpajwwaekvmntbnpaotytafroxvotjdvelggkppgqhmprglvghkqctorugxnggtdbdszdwdheqjbduqgfrwznrlwzymklorilcutzgcouwtpeobkyigulbxlgcyirslptakxfpfjfeijzfsqjktdxcxkxfxtfmrmibajgekqcqnzbmbfzzglkbjlsaxovhkyhxfwbczctymjdmwfucrjitmaoqlcsxhiqxedivyimvgbalcyrdqfjhgvzqlpjzkzrgnfderlufdyypamtaqngdgiqrqxdlrxoopkxhywhyouskylddeawhqudjnitlpqdxtdocwdltwoubreyegrnulfxaiwzfwqdrydwzpjwgojaarypkiwhmfwzgkpasslldpaamrrnpqhtxcpdcyibypyvhthlryptuxysoeywvndpznjegisgvnhvuujcbxonizqaxrudysgxfjxroswjilquwmdvjztwuopcowydbferebriqdafuwatmqbntxhcszpovbqdtuuyzgkylkpyxbiztlnksbihxehmialyrtlehebhbmwywhhiwiuuuagbecjojleoqxuwyluyosfauvlteazqulwvkjtmpaiqmrdpoahsgxjdptubfuryucsbbbwoyfjlrpzgkkkvfbbxxmbbobowckvxlzrpohsdzzukrexcyuwwhbxv";
            byte[] message = str.getBytes();

            do {

                if ( Utilities.checkStop() ) {
                    break outer;
                }

                try {
                    os.write( message );

                }
                catch ( Exception e ) {
                    e.printStackTrace();
                    retval = 7;
                    break outer;
                }


                try {
                    byte[] buffer = new byte[ 1000 ];
                    int len = 0;
                    int size = str.length();

                    if ( Utilities.checkStop() ) {
                        break outer;
                    }

                    if ( size > 1000 ) {
                        do {
                            int read_bytes = is.read( buffer, 0, 1000 );
                            len += read_bytes;

                            if ( read_bytes <= 0 ) {
                                retval = 8;
                                break outer;
                            }
                        }
                        while ( size - len > 1000 );
                    }

                    do {
                        if ( Utilities.checkStop() ) {
                            break outer;
                        }

                        int read_bytes = is.read( buffer, 0, size - len );

                        if ( read_bytes <= 0 ) {
                            retval = 8;
                            break outer;
                        }

                        len += read_bytes;

                    }
                    while ( size - len != 0 );


                }
                catch ( Exception e ) {
                    e.printStackTrace();
                    retval = 8;
                    break outer;
                }

                end = System.currentTimeMillis();
            }
            while ( end - strt < 10000 );
        }

        try {
            tcpSocket.close();
            os.close();
            is.close();

            if ( retval != -1 )
                return retval;
        }
        catch ( Exception e2 ) {
            if ( retval == -1 )
                return 9;
            else
                return retval;
        }
        return 10;

    }

	
}
*/