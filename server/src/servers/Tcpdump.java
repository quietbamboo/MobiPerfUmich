/****************************
*
* @Date: Jun 10, 2011
* @Time: 2:46:36 PM
* @Author: Junxian Huang
*
****************************/
package servers;

import common.BaseServer;
import common.Definition;

public class Tcpdump extends BaseServer {
	
	public static void main(String[] argv){
		while(true){
			System.out.println("Tcpdump server starts");
			Tcpdump server = new Tcpdump();
			server.listenSocket(Definition.PORT_TCPDUMP_REPORT);
		}
	}
}