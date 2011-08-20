/**
 * @author Junxian Huang
 * @date Aug 30, 2009
 * @time 8:58:35 PM
 * @organization University of Michigan, Ann Arbor
 */
package servers;

import common.BaseServer;
import common.Definition;

/**
 * @author Junxian Huang
 *
 */
public class Version extends BaseServer {
	
	public static void main(String[] argv){
		while(true){
			System.out.println("Version server starts");
			Version server = new Version();
			server.listenSocket(Definition.PORT_VERSION);
		}
	}
}
