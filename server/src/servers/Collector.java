/**
 * @author Junxian Huang
 * @date Aug 30, 2009
 * @time 4:04:07 PM
 * @organization University of Michigan, Ann Arbor
 */
package servers;

import common.BaseServer;
import common.Definition;

/**
 * @author Junxian Huang
 *
 */
public class Collector extends BaseServer {
	
	public static void main(String[] argv){
		while(true){
			System.out.println("Collector server starts");
			Collector server = new Collector();
			server.listenSocket(Definition.PORT_REPORT);
		}
	}
}
