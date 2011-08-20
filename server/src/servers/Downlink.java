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
		while(true){
			System.out.println("Downlink server starts");
			Downlink server = new Downlink();
			server.listenSocket(Definition.PORT_DOWN_THRU);
		}
	}
}


