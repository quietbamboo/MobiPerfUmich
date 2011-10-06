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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import android.os.Environment;
import android.util.Log;

public class CopyFile extends Thread {
    String mSrc;
    String mDet;
    CopyFile( String Filename1 ) {
        mSrc = Filename1;
        mDet = "UMLogger.txt";
    }

    public void run() {
        try {
            File root = Environment.getExternalStorageDirectory();

            if ( root.canWrite() ) {
                File logfile = new File( root, mDet );
                FileOutputStream out = new FileOutputStream( logfile );
                FileInputStream fin = new FileInputStream( mSrc );
                byte[] buf = new byte[ 1024 ];
                int len;

                while ( ( len = fin.read( buf ) ) > 0 ) {
                    out.write( buf, 0, len );
                    Log.v( "check", "length is " + len );

                }

                fin.close();
                out.close();
            }
        }
        catch ( IOException e ) {
            Log.e( "check", "Could not write file " + e.getMessage() );
        }

    }
}
