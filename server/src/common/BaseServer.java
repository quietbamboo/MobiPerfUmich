/**
 * @author Junxian Huang
 * @date Aug 29, 2009
 * @time 2:04:36 PM
 * @organization University of Michigan, Ann Arbor
 */
package common;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.SocketException;

import servers.BtNondftWorker;
import servers.CollectorWorker;
import servers.CommandWorker;
import servers.DownlinkWorker;
import servers.HttpWorker;
import servers.TcpdumpWorker;
import servers.UplinkWorker;
import servers.UserStateWorker;
import servers.VersionWorker;
import servers.WhoamiWorker;

/**
 * @author Junxian Huang
 * 
 * @description This is the base class for a multi-threaded TCP/UDP server
 *
 */
public class BaseServer {
	
	public ServerSocket server;
	public DatagramSocket udpServer;
	
	
	public void receiveUdpPacket(int port){
		
		try {
			udpServer = new DatagramSocket(port);
			
			while(true){
				byte[] buf = new byte[25600];
			    DatagramPacket packet = new DatagramPacket(buf, buf.length);
			    //upon receipt of each packet, start a new thread to send back response
			    udpServer.receive(packet);
			    
			    
			    //add special port handling here, for example, if want to measure UDP throughput
			    //otherwise, use default UDP worker to send back echo message
			    
			    BaseUdpWorker worker = new BaseUdpWorker(udpServer, packet);
			    worker.start();
			}
		    
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * This function blocks for accepting TCP connections
	 * @param port
	 */
	public void listenSocket(int port){
		try{
			server = new ServerSocket(port);
		} catch (IOException e) {
			e.printStackTrace();
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
		    System.out.println("Could not listen on port " + port);
		    
			return;
		}
		while(true){
			
		    try{
		    	//server.accept returns a client connection
		    	switch(port){
		    	case Definition.PORT_COMMAND:
		    		CommandWorker c = new CommandWorker();
		    		c.setSocket(server.accept());
		    		c.start();
		    		break;
		    		
		    	case Definition.PORT_TCPDUMP_REPORT:
		    		TcpdumpWorker t = new TcpdumpWorker();
		    		t.setSocket(server.accept());
		    		t.start();
		    		break;
		    	
		    	case Definition.PORT_USER_STAT:
		    		UserStateWorker us_worker = new UserStateWorker();
		    		us_worker.setSocket(server.accept());
		    		us_worker.start();
		    		break;
		    	
		    	case Definition.PORT_HTTP:
		    		HttpWorker http_worker = new HttpWorker();
		    		http_worker.setSocket(server.accept());
		    		http_worker.start();
		    		break;
		    	
		    	case Definition.PORT_BT_RANDOM:
		    		BtNondftWorker btnondft_worker = new BtNondftWorker();
		    		btnondft_worker.setSocket(server.accept());
		    		btnondft_worker.start();
		    		break;
		    	
		    	case Definition.PORT_WHOAMI:
		    		WhoamiWorker whoami_worker = new WhoamiWorker();
		    		whoami_worker.setSocket(server.accept());
		    		whoami_worker.start();
		    		break;
		    	
		    	case Definition.PORT_VERSION:
		    		VersionWorker version_worker = new VersionWorker();
		    		version_worker.setSocket(server.accept());
		    		version_worker.start();
		    		break;
		    	
		    	case Definition.PORT_REPORT:
		    		CollectorWorker collector_worker = new CollectorWorker();
		    		collector_worker.setSocket(server.accept());
		    		collector_worker.start();
		    		break;
		    	
		    	
		    	case Definition.PORT_DOWNLINK:
		    	case Definition.PORT_DOWNLINK_MLAB:
		    		//downlink server
		    		DownlinkWorker downlink_worker = new DownlinkWorker(port);
		    		downlink_worker.setSocket(server.accept());
		    		downlink_worker.start();
		    		break;
		    		
		    	case Definition.PORT_UPLINK:
		    	case Definition.PORT_UPLINK_MLAB:
		    		UplinkWorker uplink_worker = new UplinkWorker(port);
		    		uplink_worker.setSocket(server.accept());
		    		uplink_worker.start();
		    		break;
		    		
		    	//Reach start
		    	case Definition.PORT_BITTORRENT:
		    		//BtWorker bt_worker = new BtWorker();
		    		//BT changed to base worker
		    	case Definition.PORT_AUTHENTICATED_SMTP:
		    	case Definition.PORT_DNS:
		    	case Definition.PORT_FTP:
		    	case Definition.PORT_HTTPS:
		    	case Definition.PORT_IMAP_SSL:
		    	case Definition.PORT_IMAP:
		    	case Definition.PORT_NETBIOS:
		    	case Definition.PORT_POP_SSL:
		    	case Definition.PORT_POP:
		    	case Definition.PORT_RPC:
		    	case Definition.PORT_SECURE_IMAP:
		    	case Definition.PORT_SIP:
		    	case Definition.PORT_SMB:
		    	case Definition.PORT_SMTP_SSL:
		    	case Definition.PORT_SMTP:
		    	case Definition.PORT_SNMP:
		    	case Definition.PORT_IOS_SPECIAL:
		    	case Definition.PORT_ANDROID_SPECIAL:
		    	case Definition.PORT_HTTP_PROXY:
		    		BaseTcpWorker worker2 = new BaseTcpWorker();
		    		worker2.setSocket(server.accept());
		    		worker2.start();
		    		break;
		    	//Reach end
		    	default:
		    		System.out.println("Port " + port + " is not currently supported by BaseServer");
			    	break;
		    		
		    	}
		    	
		    } catch (IOException e) {
		    	System.out.println("Server failed: port <" + port + ">");
		    	return;
		    }
		}
	}


}