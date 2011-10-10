/**
 * @author Junxian Huang
 * @date Aug 30, 2009
 * @time 3:28:53 PM
 * @organization University of Michigan, Ann Arbor
 */

package servers;


import common.BaseServer;
import common.Definition;

/**
 * @author Junxian Huang
 *
 */
public class Uplink extends BaseServer {
	
	public static void main(String[] argv){
		int port = Definition.PORT_UPLINK;
		if(argv.length >= 1){
			if(argv[0].equals("mlab"))
				port = Definition.PORT_UPLINK_MLAB;
		}
		while(true){
			System.out.println("Uplink server starts on port " + port);
			Uplink server = new Uplink();
			server.listenSocket(port);
		}
	}
}