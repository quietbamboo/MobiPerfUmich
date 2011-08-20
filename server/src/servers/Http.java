/**
 * @author Junxian Huang
 * @date Aug 31, 2009
 * @time 5:49:48 PM
 * @organization University of Michigan, Ann Arbor
 */
package servers;

import common.BaseServer;
import common.Definition;

/**
 * @author Junxian Huang
 *
 */
public class Http extends BaseServer {
	
	public static void main(String[] argv){
		while(true){
			System.out.println("Http server starts");
			Http server = new Http();
			server.listenSocket(Definition.PORT_HTTP);
		}
	}
}