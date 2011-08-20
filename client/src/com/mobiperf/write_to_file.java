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

import java.io.FileInputStream;
import java.io.IOException;

import android.os.Environment;
import android.util.Log;

class write_to_file extends Thread {
    public void run() {
        String state = Environment.getExternalStorageState();

        if ( state.equalsIgnoreCase( Environment.MEDIA_MOUNTED ) ) {
            try {
                Log.v( "check", "writing" );
                String src = "/data/data/eecs.umich.threegtest/files/UMLogger.txt";
                FileInputStream fin = new FileInputStream( src );
                fin.close();
                new CopyFile( src ).start();
            }
            catch ( IOException e ) {
                // TODO Auto-gen+erated catch block
                Log.v( "check", "error " + e.getMessage() );
                e.printStackTrace();
            }
        }
        else {}

    }

}
