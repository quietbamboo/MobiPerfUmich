/****************************
 *
 * @Date: Jun 11, 2011
 * @Time: 2:52:36 PM
 * @Author: Junxian Huang
 *
 ****************************/
package com.mobiperf.lte;

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
		String message;

		InformationCenter.reset();
		
		Mlab.loadServerList();


		//check airplane mode
		((MainService)service).updateTextView3(Feedback.getMessage(Feedback.TYPE.AIRPLANE_MODE_CHECKING, null));
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

			//checking network connectivity by connecting to google.com

			((MainService)service).updateTextView3(Feedback.getMessage(Feedback.TYPE.NETWORK_CONNECTION_CHECKING, null));
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


			((MainService)service).addResultAndUpdateUI(Feedback.getMessage(Feedback.TYPE.DEVICE_ID, null), progress += 2);

			//Version information
			(new Report()).sendReport("PACKAGE:<VersionCode:" + InformationCenter.getPackageVersionCode() + "><VersionName:" + 
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
			((MainService)service).addResultAndUpdateUI(Feedback.getMessage(Feedback.TYPE.CELL_ID, new String[]{"" + cellid}), progress += 1);
			((MainService)service).addResultAndUpdateUI(Feedback.getMessage(Feedback.TYPE.LAC, new String[]{"" + lac}), progress += 1);
			((MainService)service).addResultAndUpdateUI(Feedback.getMessage(Feedback.TYPE.SIGNAL_STRENGTH, new String[]{"" + signal}), progress += 1);

		
			(new Report()).sendReport(netInfoS);
			if(shouldStop())
				return;


			//DNS unique lookup to UMICH DNS servers
			DNS.LookupUniqueUrl(false);


			//local ip vs global ip

			if(((MainService)service).isRoot)
				((MainService)service).updateTextView3("Testing IP, NAT, and firewall with root...");
			else
				((MainService)service).updateTextView3("Testing IP, NAT, and firewall...");

			int replyCode = Phone_IPs.Get_Phone_IPs(Definition.SERVER_NAME, Definition.PORT_WHOAMI );


			String result = "ADDRESS:<LocalIp:" + Phone_IPs.localIP + ">:<GlobalIp:" + Phone_IPs.seenIP + ">;";
			String message1;
			if ( replyCode >= 7 ) {
				message = "Local IP address: " + Phone_IPs.localIP;
				message1 = "Global IP address: " + Phone_IPs.seenIP;
			}else{
				message = "Local IP address: Error in test";
				message1 = "Global IP address: Error in test";
			}

			((MainService)service).addResultAndUpdateUI(message, progress += 2);//Local IP
			((MainService)service).addResultAndUpdateUI(message1, progress += 2);//Global IP

	
			(new Report()).sendReport(result);
			if(shouldStop())
				return;

			//checking GPS info
			((MainService)service).updateTextView3(Feedback.getMessage(Feedback.TYPE.GPS_CHECKING, null));

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

			// downlink tput
			((MainService)service).updateTextView3("Testing downlink throughput...");
			replyCode = Throughput.MeasureDownlinkTput(Definition.SERVER_NAME, Definition.PORT_THRU_DOWN);

			result = "DOWN:";

			if ( replyCode == 7 ) {

				double downtp = (Throughput.downlinkSize * 8 ) / Throughput.downlinkTime; // in kbps

				message = "TCP downlink bandwidth (kbps): " + downtp;
				result += "<Tp:" + downtp + ">";


				if ( downtp > 500 )
					message += " Good";
				else if ( downtp > 130 )
					message += " Moderate";
				else
					message += " Bad";
			}
			else {
				message = "TCP downlink bandwidth (kbps): Network problem in test";
				result += "<Tp:-1>";
			}

			progress = 90;

			((MainService)service).addResultAndUpdateUI(message, progress);//TCP down
			
			result += ";";
			result += "\n";
			(new Report()).sendReport(result);
			if(shouldStop())
				return;

			// uplink tput  

			((MainService)service).updateTextView3("Testing uplink throughput...");

			replyCode = Throughput.MeasureUplinkTput(Definition.SERVER_NAME, Definition.PORT_THRU_UP);                    

			result = "UP:";

			if ( replyCode == 7 ) {
				//double uptp = Throughput.uplinkSize / ( 128 * 60 );//old mistaken one
				double uptp = (Throughput.uplinkSize * 8) / Throughput.uplinkTime;
				message = "TCP uplink bandwidth (kbps): " + uptp;
				result += "<Tp:" + uptp + ">";

				if ( uptp > 100 )
					message += " Good";
				else if ( uptp > 30 )
					message += " Moderate";
				else
					message += " Bad";
			}
			else {
				message = "TCP uplink bandwidth (kbps): Network problem in test";
				result += "<Tp:-1>";
			}

			progress = 100;
			result += ";";
			result += "\n";
			(new Report()).sendReport(result);
			if(shouldStop())
				return;


			Log.v("error"," dis is "+message);


			((MainService)service).addResultAndUpdateUI(message, progress);//TCP UP


			//traceroute experiments to our server
			//TODO
			//Traceroute.traceroute(Def.SERVER_NAME, -1, -1);


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
