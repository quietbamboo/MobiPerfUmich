/****************************
*
* @Date: Mar 28, 2011
* @Time: 11:33:33 PM
* @Author: Junxian Huang
*
****************************/
package common;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

/**
 * 
 * @author junxianhuang
 * 
 * Handle an incoming UDP packet, simply send back response
 * 
 */
public class BaseUdpWorker extends Thread{
	
	private DatagramSocket socket;
	private DatagramPacket packet;
	
	public BaseUdpWorker(DatagramSocket socket, DatagramPacket packet){
		this.socket = socket;
		this.packet = packet;
	}
	
	public void run(){
		
		try {
			System.out.println("UDP server " + packet.getAddress().getHostAddress() + 
					" server local port " + socket.getLocalPort());
			//" server port " + socket.getPort() is -1 since there is no connection for TCP
			String request = new String(packet.getData());
			String echo = Util.parseLocalExperimentRequest(request, "udp");
			DatagramPacket p2 = new DatagramPacket(echo.getBytes(), echo.length(),
					packet.getAddress(), packet.getPort());
				
			socket.send(p2);
			//socket.close(); //don't need to close because we are reusing
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
	}

}
