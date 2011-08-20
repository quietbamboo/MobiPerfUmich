/**
 * @author Junxian Huang
 * @date Aug 31, 2009
 * @time 5:39:08 PM
 * @organization University of Michigan, Ann Arbor
 */
package servers;

import common.BaseServer;
import common.Definition;

/**
 * @author Junxian Huang
 *
 */
public class BtNondft extends BaseServer {
	
	public static void main(String[] argv){
		while(true){
			System.out.println("BtNondft server starts");
			BtNondft server = new BtNondft();
			server.listenSocket(Definition.PORT_BT_RANDOM);
		}
	}
}