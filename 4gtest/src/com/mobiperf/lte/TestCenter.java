/****************************
 *
 * @Date: Jun 11, 2011
 * @Time: 2:52:36 PM
 * @Author: Junxian Huang
 *
 ****************************/
package com.mobiperf.lte;

import com.mobiperf.lte.test.PacketClient;
import com.mobiperf.lte.test.PacketClient.ServerType;

import android.app.Service;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.Log;

public class TestCenter{
	public int progress;

	public String infoS;

	public Service service;


	private PowerManager.WakeLock wakeLock;
	private WifiManager.WifiLock wlw;


	public TestCenter(Service s){
		service = s;
		progress = 0;
	}


	public synchronized void RunTest(){

		long start = System.currentTimeMillis();
		long end = start;
		

		InformationCenter.reset();

		//check airplane mode
		((MainService)service).updateTextView(Feedback.getMessage(Feedback.TYPE.AIRPLANE_MODE_CHECKING, null));
		boolean isEnabled = Settings.System.getInt( service.getContentResolver(), Settings.System.AIRPLANE_MODE_ON, 0 ) == 1;
		if(isEnabled){
			progress = 0;
			((MainService)service).addResultAndUpdateUI(Feedback.getMessage(Feedback.TYPE.AIRPLANE_MODE_ENABLED, null), progress);
			return;
		}
		//ok, now airplane mode is not enabled and we are good to go

		//ThreegtestService.tester = new ConnectivityThreegtestService.tester(context);

		//ensures that CPU and wifi is on, priority is max
		PowerManager pm = ( PowerManager ) service.getSystemService( Context.POWER_SERVICE );
		wakeLock = pm.newWakeLock( PowerManager.PARTIAL_WAKE_LOCK, "POWER LOCK TAG" );
		wakeLock.acquire();
		WifiManager wm = ( WifiManager ) service.getSystemService( Context.WIFI_SERVICE );
		wlw = wm.createWifiLock( "WIFI LOCK TAG" );
		wlw.acquire();
		
		
		

		//catch any exception here
		try{
			
			//TODO comment this when releasing new 4G Test
			if(false){
				//warm up network
				Utilities.executeCmd("ping -c 1 -w 1 google.com", false);
				Thread.sleep(15000);
				
				//long a, b;
				double size = 10;
				//a = System.currentTimeMillis();
				double tp = PacketClient.testTcp(ServerType.TCP_UP_SIZE, size);
				//b = System.currentTimeMillis();
				
				//((MainService)service).addResultAndUpdateUI("Throughput " + tp + " kbps, time " + (b - a), 100);
				Thread.sleep(15000);
				
				tp = PacketClient.testTcp(ServerType.TCP_UP_SIZE, size);
				
				//((MainService)service).addResultAndUpdateUI("Throughput " + tp + " kbps, time " + (b - a), 100);
				Thread.sleep(15000);
				
				tp = PacketClient.testTcp(ServerType.TCP_UP_SIZE, size);
			
				Thread.sleep(15000);
				
				tp = PacketClient.testTcp(ServerType.TCP_UP_SIZE, size);
				
				Thread.sleep(15000);
				
				tp = PacketClient.testTcp(ServerType.TCP_UP_SIZE, size);
				
				Thread.sleep(15000);
				
				//((MainService)service).addResultAndUpdateUI("Throughput " + tp + " kbps, time " + (b - a), 100);
				//PacketClient.testUdpDown();
				//*/
				Main.stopFlag = false;
				wakeLock.release();
				wlw.release();
				return;
			}//*/

			//checking network connectivity by connecting to google.com

			((MainService)service).updateTextView(Feedback.getMessage(Feedback.TYPE.NETWORK_CONNECTION_CHECKING, null));
			if (Utilities.checkConnection()){
				InformationCenter.setNetworkStatus(true);
			}else{
				InformationCenter.setNetworkStatus(false);
			}

			//if no network connection
			if(!InformationCenter.getNetworkStatus()){ 
				progress = 0;

				((MainService)service).addResultAndUpdateUI(Feedback.getMessage(Feedback.TYPE.NETWORK_CONNECTION_DOWN, null), progress);
				wakeLock.release();
				wlw.release();
				return;
			}

			//network connection is available, start tests


			//((MainService)service).addResultAndUpdateUI(Feedback.getMessage(Feedback.TYPE.DEVICE_ID, null), progress += 2);
			
			//Version information
			//AppId 1 for 4G Test, 0 for MobiPerf
			(new Report()).sendReport("PACKAGE:<AppId:1><VersionCode:" + InformationCenter.getPackageVersionCode() + "><VersionName:" + 
					InformationCenter.getPackageVersionName() + ">");

			//Build information
			(new Report()).sendReport("BUILD:<BOARD:" + Build.BOARD + "><BRAND:" + Build.BRAND + ">" +
					"<DEVICE:" + Build.DEVICE + "><FINGERPRINT:" + Build.FINGERPRINT + "><HOST:" + Build.HOST + ">" +
					"<ID:" + Build.ID + "><MODEL:" + Build.MODEL + "><PRODUCT:" + Build.PRODUCT + ">" +
					"<TAGS:" + Build.TAGS + "><TIME:" + Build.TIME + "><USER:" + Build.USER + "><TYPE:" + Build.TYPE + ">" +
					"<VERSION.SDK:" + Build.VERSION.SDK + "><VERSION.RELEASE:" + Build.VERSION.RELEASE + ">;");
			if(shouldStop())
				return;


			//carrier info, network type, signal strength, cellID
			String carrier = InformationCenter.getNetworkOperator();
			String netInfoS = "NETWORK:" + "<Carrier:" + carrier + ">";
			((MainService)service).addResultAndUpdateUI(Feedback.getMessage(Feedback.TYPE.CARRIER_NAME, new String[]{carrier}), progress += 5);

			String[] networkType = InformationCenter.getTypeNameAndId();
			int cellid = InformationCenter.getCellId();
			int lac = InformationCenter.getLAC();
			int signal = InformationCenter.getSignalStrength();
			netInfoS += "<Type:" + networkType[0] + 
			"><TypeID:" + networkType[1] + 
			"><CellId:" + cellid +
			"><LAC:" + lac +
			"><Signal:" + signal + ">;";

			((MainService)service).addResultAndUpdateUI(Feedback.getMessage(Feedback.TYPE.NETWORK_TYPE, networkType), progress += 1);
			//((MainService)service).addResultAndUpdateUI(Feedback.getMessage(Feedback.TYPE.CELL_ID, new String[]{"" + cellid}), progress += 1);
			//((MainService)service).addResultAndUpdateUI(Feedback.getMessage(Feedback.TYPE.LAC, new String[]{"" + lac}), progress += 1);
			//((MainService)service).addResultAndUpdateUI(Feedback.getMessage(Feedback.TYPE.SIGNAL_STRENGTH, new String[]{"" + signal}), progress += 1);

		
			(new Report()).sendReport(netInfoS);
			if(shouldStop())
				return;
			
			
			//checking GPS info
			((MainService)service).updateTextView(Feedback.getMessage(Feedback.TYPE.GPS_CHECKING, null));

			while(GPS.location == null){
				end = System.currentTimeMillis();
				if(end - start > Definition.GPS_UPDATE_WAITING_TIME){
					GPS.stopAllUpdate();
					GPS.location = GPS.getCurrentLocation();
					GPS.latitude = GPS.location.getLatitude();
					GPS.longitude = GPS.location.getLongitude();
					break;
				}try{
					//Log.v("LOG", "Utilities.Info(), waiting for a GPS result............");
					Thread.sleep(1000);
				}catch (InterruptedException e){
					e.printStackTrace();
				}
			}

			progress += 5;
			((MainService)service).addResultAndUpdateUI(Feedback.getMessage(Feedback.TYPE.GPS_VALUE, null), progress);
		
			infoS = Utilities.Info(service);
			// set report prefix
			(new Report()).sendReport(infoS);
			if(shouldStop())
				return;
			
			((MainService)service).updateTextView(Feedback.getMessage(Feedback.TYPE.MLAB_LOADING_SERVER_LIST, null));
			progress += 15;
			((MainService)service).updateProgress(progress);
			
			Mlab.loadServerList();
			RTT.reset();
			ThroughputMulti.reset(true);
			ThroughputMulti.reset(false);
			
			((MainService)service).updateTextView(Feedback.getMessage(Feedback.TYPE.MLAB_TESTING_RTT, null));
			progress += 15;
			((MainService)service).updateProgress(progress);

			RTT.reset();
			RTT.test(service);
			
			
			//DOWNLINK
			((MainService)service).updateTextView(Feedback.getMessage(Feedback.TYPE.MLAB_THROUGHPUT_DOWNLINK, null));
			progress += 15;
			((MainService)service).updateProgress(progress);
			
			ThroughputMulti.reset(true);
			ThroughputMulti.startTest(true, 3, service);
			
			//UPLINK
			((MainService)service).updateTextView(Feedback.getMessage(Feedback.TYPE.MLAB_THROUGHPUT_UPLINK, null));
			progress += 15;
			((MainService)service).updateProgress(progress);
			
			ThroughputMulti.reset(false);
			ThroughputMulti.startTest(false, 3, service);
			

			progress = 100;
			((MainService)service).addResultAndUpdateUI("Test finishes " + InformationCenter.getRunId(), progress);//TCP UP

		}catch(Exception e){
			System.out.println("The outer big try in Service_Thread.java");
			e.printStackTrace();
		}

		//let server write to database
		Utilities.letServerWriteOutputToMysql();

		wakeLock.release();
		wlw.release();

		end = System.currentTimeMillis();
		Log.v("MobiPerf", "service thread finish using " + (end - start) / 1000 +"s");
	}
	

	/**
	 * For FCC challenge or local experiments
	 * Test both latency to TCP and UDP server with the list of ports
	 * @return
	 */
	public void localExperiments(String type){
		if(type.equalsIgnoreCase("port.scan")){
			for(int j = 0; j < Definition.PORTS.length; j++){
				new PortScan(j).rttWithPacketSize(100, 25, PortScan.DIRECTION_DOWN);
			}
		}else if(type.equalsIgnoreCase("rtt.size")){
			int NUM_EXP = 10;
			for(int s = 100 ; s <= 2000 ; s += 25){
				for(int direction : new int[]{PortScan.DIRECTION_DOWN, 
						PortScan.DIRECTION_UP, PortScan.DIRECTION_BOTH}){
					new PortScan(0).rttWithPacketSize(s, NUM_EXP, direction);//port 21
					new PortScan(3).rttWithPacketSize(s, NUM_EXP, direction);//port 53
					new PortScan(20).rttWithPacketSize(s, NUM_EXP, direction);//port 5228
				}
			}
		}
	}


	/**
	 *  use this to check stop
        	if user clicks stop, return true, and should quit
	 * @return true on user clicked "stop" button
	 */
	public boolean shouldStop(){

		if(Utilities.checkStop()){
			Main.stopFlag = false;
			wakeLock.release();
			wlw.release();

			//if stops here, write to db, otherwise, will write to db in the end
			Utilities.letServerWriteOutputToMysql();

			return true;
		}else
			return false;

	}

}
