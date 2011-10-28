/****************************
 *
 * @Date: Jun 11, 2011
 * @Time: 2:52:36 PM
 * @Author: Junxian Huang
 *
 ****************************/
package com.mobiperf;

import android.app.Service;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.Log;

import com.mobiperf.ui.Display;

public class TestCenter{
	public int progress;

	public String infoS;

	public Service service;
	public boolean fore; //true for user-triggered running at the foreground, with ui

	private NAT_Test_Thread natThread;
	private TCP_Injection_Vuln_Test_Thread tcpInjectionVulnTestThread;
	private Spoof_Test_Thread spoofThread;
	private Probe_Nearby_Test_Thread probeNearbyThread;
	private Timeout_Test_Thread timeoutThread;
	//private Firewall_Test_Thread firewallThread;

	private PowerManager.WakeLock wakeLock;
	private WifiManager.WifiLock wlw;

	
	public TestCenter(Service s, boolean f){
		service = s;
		fore = f;
		progress = 0;
	}

	
	public synchronized void RunTest(){
	
		if(!fore){
			Log.w("MobiPerf", "Periodic running " + System.currentTimeMillis());
			return;
		}

		long start = System.currentTimeMillis();
		long end = start;
		String message;

		InformationCenter.reset();

		Thread binaryThread = Utilities.installBinaries(service);

		//check airplane mode
		if(fore)
			((MainService)service).updateTextView3(Feedback.getMessage(Feedback.TYPE.AIRPLANE_MODE_CHECKING, null));
		boolean isEnabled = Settings.System.getInt( service.getContentResolver(), Settings.System.AIRPLANE_MODE_ON, 0 ) == 1;
		if(isEnabled){
			progress = 0;
			if(fore)
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
			if(fore)
				((MainService)service).updateTextView3(Feedback.getMessage(Feedback.TYPE.NETWORK_CONNECTION_CHECKING, null));
			if (Utilities.checkConnection()){
				InformationCenter.setNetworkStatus(true);
			}else{
				InformationCenter.setNetworkStatus(false);
			}

			//if no network connection
			if(!InformationCenter.getNetworkStatus()){ 
				progress = 0;
				if(fore)
					((MainService)service).addResultAndUpdateUI(Feedback.getMessage(Feedback.TYPE.NETWORK_CONNECTION_DOWN, null), progress);
				wakeLock.release();
				wlw.release();
				return;
			}

			//network connection is available, start tests
			
			//MSP test
			/*if(fore){
				//TimeoutClient.start(310, 311, 10);
				while(true){
					Log.v("MobiPerf", "Start traceroute");
					Traceroute.start();
					try {
						Thread.sleep(5000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				//if(true)return;
			}//*/

			//MobiOpen test
			/*service.updateTextView3("MobiPerf local experiments");
				if(1==1){
					while(true){
						long start = System.currentTimeMillis();
						localExperiments("rtt.size");
						//localExperiments("port.scan");
						long end = System.currentTimeMillis();
						service.updateTextView2("last time (sec): " + (end-start)/1000);
						service.updateTextView3("Prefix " + Definition.getPrefix());
						//DNS.LookupUniqueUrl(true);
					}
				}
				//*/


			if(fore)
				((MainService)service).addResultAndUpdateUI(Feedback.getMessage(Feedback.TYPE.DEVICE_ID, null), progress += 2);
			//service.addResultAndUpdateUI(Feedback.getMessage(Feedback.TYPE.RUN_ID, null), mProgressStatus += 2);


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
			if(fore)
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

			if(fore){
				((MainService)service).addResultAndUpdateUI(Feedback.getMessage(Feedback.TYPE.NETWORK_TYPE, networkType), progress += 1);
				((MainService)service).addResultAndUpdateUI(Feedback.getMessage(Feedback.TYPE.CELL_ID, new String[]{"" + cellid}), progress += 1);
				((MainService)service).addResultAndUpdateUI(Feedback.getMessage(Feedback.TYPE.LAC, new String[]{"" + lac}), progress += 1);
				((MainService)service).addResultAndUpdateUI(Feedback.getMessage(Feedback.TYPE.SIGNAL_STRENGTH, new String[]{"" + signal}), progress += 1);

				Display.displayResult("Network Type", "Network type used (WiFi, UMTS, CDMA, etc.)", networkType[0], 0);
				Display.displayResult("Carrier", "Name of cellular carrier", carrier, 0);
				Display.displayResult("Cell ID", "Id of cell tower connected", "" + cellid, 0);
				Display.displayResult("Signal Strength", "Signal strength in asu, 0 is worst and 31 is best", "" + signal, 0);
			}

			(new Report()).sendReport(netInfoS);
			if(shouldStop())
				return;


			//DNS unique lookup to UMICH DNS servers
			DNS.LookupUniqueUrl(false);


			//local ip vs global ip
			if(fore){
				if(((MainService)service).isRoot)
					((MainService)service).updateTextView3("Testing IP, NAT, and firewall with root...");
				else
					((MainService)service).updateTextView3("Testing IP, NAT, and firewall...");
			}

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

			if(fore){
				((MainService)service).addResultAndUpdateUI(message, progress += 2);//Local IP
				((MainService)service).addResultAndUpdateUI(message1, progress += 2);//Global IP

				Display.displayResult("Local IP", "Local IP address of your device, could be private IP", Phone_IPs.localIP, 0);
				Display.displayResult("Seen IP", "IP address of your device seen by a remote server", Phone_IPs.seenIP, 0);
			}

			(new Report()).sendReport(result);
			if(shouldStop())
				return;

			//checking GPS info
			if(fore)
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

			if(fore){
				progress += 5;
				((MainService)service).addResultAndUpdateUI(Feedback.getMessage(Feedback.TYPE.GPS_VALUE, null), progress);
				Display.displayResult("GPS Location", "Latitude (<0 for South) Longitude (<0 for West)", 
						"Latitude:" + GPS.location.getLatitude() + " Longitude:" + GPS.location.getLongitude(), 0);
			}

			infoS = Utilities.Info(service);
			// set report prefix
			(new Report()).sendReport(infoS);
			if(shouldStop())
				return;


			if(fore){
				/**** wait till all the binaries are installed ****/
				try {
					binaryThread.join();
				} catch (InterruptedException e3) {
					e3.printStackTrace();
				}
			}

			if(fore){
				/*********** NAT Test Thread **************/
				// Make sure only one NAT_Test_Thread instance is running
				if(natThread == null || !natThread.isAlive())
				{
					natThread = new NAT_Test_Thread(((MainService)service));
					natThread.start();
				}
				else
				{
					if(natThread.isMappingDone)
					{
						message = natThread.getNATMappingMessage();
						((MainService)service).addResultAndUpdateUI(message, progress);
						//Utilities.write_to_file(Service_Thread.LOG_FILE_NAME,Context.MODE_APPEND, message+"\n", service);
					}
					Log.v("MobiPerf_nat", "nat thread is still running");
				}


				/******************************************/
				/*********** Timeout Test Thread **************/
				// Make sure only one timeoutThread instance is running

				if(timeoutThread == null || !timeoutThread.isAlive()){
					timeoutThread = new Timeout_Test_Thread(service);
					timeoutThread.start();
				}else
					Log.v("MobiPerf_timeout", "timeout thread is still running");
				/******************************************/


				// Similar to NAT thread (testing TCP injection vulnerability)
				if(tcpInjectionVulnTestThread == null || !tcpInjectionVulnTestThread.isAlive())
				{
					tcpInjectionVulnTestThread = new TCP_Injection_Vuln_Test_Thread(((MainService)service));
					Log.v("TCPvultest", "TCPvultest starts to run!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
					tcpInjectionVulnTestThread.start();
				}
				else
				{
					Log.v("TCPvultest", "TCPvultest thread is still running");
				}

				// Similar to NAT thread (probing nearby IPs)
				if(probeNearbyThread == null || !probeNearbyThread.isAlive())
				{
					probeNearbyThread = new Probe_Nearby_Test_Thread(((MainService)service));
					probeNearbyThread.start();
				}
				else
				{
					Log.v("probeNearbyThread", "probeNearbyThread test thread is still running");
				}

				if(((MainService)service).isRoot)
				{
					/************* IP Spoofing Test ***********/
					// Similar to NAT thread
					if(spoofThread == null || !spoofThread.isAlive())
					{
						spoofThread = new Spoof_Test_Thread(((MainService)service));
						spoofThread.start();
					}
					else
					{
						Log.v("MobiPerf_spoof", "spoof test thread is still running");
					}
					/******************************************/
					/************* Firewall Buffer Test ***********/

					/*// Similar to NAT thread
				if(firewallThread == null || !firewallThread.isAlive())
				{
					firewallThread = new Firewall_Test_Thread(service);
					firewallThread.start();
				}
				else
				{
					Log.v("MobiPerf_firewall", "firewall test thread is still running");
				}*/

				}
			}



			// DNS request google
			if(fore)
				((MainService)service).updateTextView3("Testing local DNS server...");
			replyCode = DNS.Google();


			if(fore){
				if ( DNS.DNSGoogle == true ) {
					message = "Local DNS server status: UP";
				}
				else {
					message = "Local DNS server status: DOWN";
				}
				progress = 30;
				Display.displayResult("Local DNS server status", "Tests whether your local DNS server works", message, 1);
				((MainService)service).addResultAndUpdateUI(message, progress);//Local DNS server status
			}

			// popular domains
			Parallel_Thread1.thread1DoneFlag = false;
			Parallel_Thread2.thread2DoneFlag = false;
			Landmark.config_file_check(service, Definition.SERVER_NAME);


			if(fore)
				((MainService)service).updateTextView3("Testing network latencies...");
			Thread p1 = new Parallel_Thread1(service, fore);
			p1.start();
			Thread p2 = new Parallel_Thread2(service, fore);
			p2.start();
			try {
				// Service_Thread waits Paralel_Thread1
				p1.join();
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}

			if(fore)
				((MainService)service).updateTextView3("Testing network latencies...");

			try {
				// Service_Thread waits Paralel_Thread2
				p2.join();
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}



			//DNS to external server allowed?
			result = "DNS:";
			replyCode = DNS.dnsLookupToExternalServer(Definition.SERVER_NAME, Definition.PORT_DNS);

			progress = 50;
			if ( DNS.DNSUmich == true ) {
				message = "DNS lookup to external server allowed?: Yes";
				result += "<DnsToExternalServerAllowed: Yes>";
			}else if ( replyCode == 6 ) {
				message = "DNS lookup to external server allowed?: No";
				result += "<DnsToExternalServerAllowed: No>";
			}else if ( replyCode == 4 ) {
				message = "DNS lookup to external server allowed?: No";
				result += "<DnsToExternalServerAllowed: No>";
			}else {
				message = "DNS lookup to external server allowed?: Error in test";
				result += "<DnsToExternalServerAllowed: Error in test>";
			}

			if(fore){
				((MainService)service).addResultAndUpdateUI(message, progress);//DNS lookup to external server
				Display.displayResult("DNS lookup to external server", 
						"Tests whether your ISP blocks DNS traffic to external server", message, 2);
			}

			result += ";";
			(new Report()).sendReport(result);
			if(shouldStop())
				return;


			///reachability test wrapper
			//TODO no reachability test for now
			if(testReachability())
				return;


			// downlink tput
			if(fore)
				((MainService)service).updateTextView3("Testing downlink throughput...");
			
			(new Report()).sendCommand(Definition.COMMAND_MLAB_INIT_DOWNLINK);

			replyCode = Throughput.MeasureDownlinkTput(Definition.SERVER_NAME, Definition.PORT_MLAB_DOWNLINK);
			
			(new Report()).sendCommand(Definition.COMMAND_MLAB_END_DOWNLINK);

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

			if(fore){
				((MainService)service).addResultAndUpdateUI(message, progress);//TCP down
				Display.displayResult("Downlink throughput", "How many bits per second can be downloaded in TCP", message, 1);
			}

			result += ";";
			result += "\n";
			(new Report()).sendReport(result);
			if(shouldStop())
				return;

			// uplink tput  
			if(fore)
				((MainService)service).updateTextView3("Testing uplink throughput...");
			

			//Junxian: New, ask the server to start tcpdump now to collect 3 way handshake
			(new Report()).sendCommand(Definition.COMMAND_MLAB_INIT_UPLINK);

			replyCode = Throughput.MeasureUplinkTput(Definition.SERVER_NAME, Definition.PORT_MLAB_UPLINK);
			
			(new Report()).sendCommand(Definition.COMMAND_MLAB_END_UPLINK);

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

			
			//wait for uplink trace to be uploaded
			Thread.sleep(10000);
			
			if(fore){
				((MainService)service).addResultAndUpdateUI(message, progress);//TCP UP
				Display.displayResult("Uplink throughput", "How many bits per second can be uploaded in TCP", message, 1);
			}

			
			//wait for uplink trace to be uploaded
			Thread.sleep(10000);

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
	 * Test reachability of PORTS.length ports
	 * @return true when stop button clicked and should return for upper layer
	 */

	public boolean testReachability()
	{

		if(fore)
			((MainService)service).updateTextView3("Testing port blocking...");
		PortScan.finishedPorts = 0;

		for(int j = 0; j < Definition.PORTS.length; j++){

			new PortScan(j).start();
			//wait a little while before staring new thread
			try {
				Thread.sleep(30);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		do {
			try {
				//sleep for 1 sec
				Thread.sleep( 1000 );
			}catch ( InterruptedException e ) {
				e.printStackTrace();
			}
		}while(PortScan.finishedPorts < Definition.PORTS.length);

		String result = "REACH:";
		String blocked = "Blocked ports for direct access: ";
		String allowed = "Allowed ports for direct access: ";

		for ( int i = 0; i < Definition.PORTS.length ;i++ ){
			Log.v("LOG", "REACHB result " + i + " for port " + Definition.PORTS[i] + " is " + PortScan.reachable[i]);
			if (!PortScan.reachable[i]){
				blocked += Definition.PORTS[i] + " (" + Definition.PORT_NAMES[i] + ")" + " ";
				if (PortScan.blockedStage[i] == 'c')
					result += "<" + Definition.PORTS[i] + ": CONNECT>";
				else
					result += "<" + Definition.PORTS[i] + ": RECV>";
			}else {
				allowed += Definition.PORTS[i] + " (" + Definition.PORT_NAMES[i] + ")" + " ";
				result += "<" + Definition.PORTS[i] + ": OK>";
			}
		}
		result += ";";

		
		if(fore){
			progress = 80;
			//Utilities.write_to_file(FILENAME, Context.MODE_APPEND ,message + "\n"+mProgressStatus + "\n"+message1 + "\n"+mProgressStatus + "\n", context );
			//Utilities.sendudpmessage( uiFile, "" + message.length() + "!" + message + "" + mProgressStatus + "!", context );
			((MainService)service).addResultAndUpdateUI(blocked, progress);//disallowed ports
			//Utilities.write_to_file(LOG_FILE_NAME, Context.MODE_APPEND, message + "\n"+message1 + "\n", service  );
			//Utilities.sendudpmessage( uiFile, "" + message1.length() + "!" + message1 + "" + mProgressStatus + "!", context );
			((MainService)service).addResultAndUpdateUI(allowed, progress);//allowed ports
			Display.displayResult("Blocked ports", "Either connection is not set up, or data packets are blocked", blocked, 2);
			Display.displayResult("Allowed ports", "Data on these ports can be uploaded/downloaded", allowed, 2);
		}

		(new Report()).sendReport(result);
		if(fore){
			//TODO
			//Tcpdump.terminate_client();
			//Tcpdump.terminate_server();
			//Tcpdump.upload();
		}
			
		
		if(shouldStop())
			return true;
		else
			return false;
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
