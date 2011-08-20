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
		while(true){
			System.out.println("Uplink server starts");
			Uplink server = new Uplink();
			server.listenSocket(Definition.PORT_UP_THRU);
		}
	}
}