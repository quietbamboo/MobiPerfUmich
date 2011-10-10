/**
 * @author Junxian Huang
 * @date Aug 29, 2009
 * @time 1:50:25 PM
 * @organization University of Michigan, Ann Arbor
 */
package servers;

import common.BaseServer;
import common.Definition;

/**
 * @author Junxian Huang
 *
 */
public class Downlink extends BaseServer {
	
	public static void main(String[] argv){
		int port = Definition.PORT_DOWNLINK;
		if(argv.length >= 1){
			if(argv[0].equals("mlab"))
				port = Definition.PORT_DOWNLINK_MLAB;
		}
		while(true){
			System.out.println("Downlink server starts on port " + port);
			Downlink server = new Downlink();
			server.listenSocket(port);
		}
	}
}
