/****************************
*
* @Date: Nov 4, 2011
* @Time: 4:15:56 PM
* @Author: Junxian Huang
*
****************************/
package com.mobiperf.lte.test;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.mobiperf.lte.InformationCenter;
import com.mobiperf.lte.Report;

public class Signal extends Thread {
	
	public static void reportToServer(){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss.SSS");
		(new Report()).sendReport("SIGNAL_STRENGTHS:<ts:" + System.currentTimeMillis() + 
				"><ts_human:" + sdf.format(new Date()) +
				"><ss:" + InformationCenter.getSignalStrength() +
				"><ecio:" + InformationCenter.getSignalEcIo() + ">;");
	}
	
	@Override
	public void run(){
		
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH-mm");
			FileWriter fw = new FileWriter("/data/local/signal_" + sdf.format(new Date()) + ".data");
			BufferedWriter bw = new BufferedWriter(fw);
			
			long a = System.currentTimeMillis();
			long b = a;
			
			while(b - a < 25 * 60 * 60 * 1000){
				bw.write(System.currentTimeMillis() + " " + InformationCenter.getSignalStrength() + " " + InformationCenter.getSignalEcIo());
				bw.newLine();
				bw.flush();
				b = System.currentTimeMillis();
				Thread.sleep(10 * 1000);
			}
			
			
			
			bw.close();
			fw.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
