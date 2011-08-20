/**
 * @author Junxian Huang
 * @date Aug 30, 2009
 * @time 9:40:42 PM
 * @organization University of Michigan, Ann Arbor
 */
package servers;

import common.BaseServer;
import common.Definition;

/**
 * @author Junxian Huang
 *
 */
public class Whoami extends BaseServer {
	
	public static void main(String[] argv){
		while(true){
			System.out.println("Whoami server starts");
			Whoami server = new Whoami();
			server.listenSocket(Definition.PORT_WHOAMI);
		}
	}
}
