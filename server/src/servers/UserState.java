/**
 * @author Junxian Huang
 * @date Sep 21, 2009
 * @time 7:52:02 PM
 * @organization University of Michigan, Ann Arbor
 */
package servers;

import common.BaseServer;
import common.Definition;

/**
 * @author Junxian Huang
 *
 */
public class UserState extends BaseServer {
	
	public static void main(String[] argv){
		while(true){
			System.out.println("User State server starts");
			UserState server = new UserState();
			server.listenSocket(Definition.PORT_USER_STAT);
		}
	}
}
