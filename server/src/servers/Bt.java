/**
 * @author Junxian Huang
 * @date Aug 31, 2009
 * @time 2:25:13 PM
 * @organization University of Michigan, Ann Arbor
 */
package servers;

import common.BaseServer;
import common.Definition;

/**
 * @author Junxian Huang
 *
 */
public class Bt extends BaseServer {
	
	public static void main(String[] argv){
		while(true){
			System.out.println("Bt server starts");
			Bt server = new Bt();
			server.listenSocket(Definition.PORT_BITTORRENT);
		}
	}
}