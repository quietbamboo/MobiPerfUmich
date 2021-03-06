/**
 * @author Junxian Huang
 * @date Aug 29, 2009
 * @time 1:55:16 PM
 * @organization University of Michigan, Ann Arbor
 */
package common;

/**
 * @author Junxian Huang
 *
 */
public class Definition {
	
	//Change this to be the directory path where you will put MobiPerf server suit on your server.
	//For example, restart.sh is located here in your server: /foo/bar/restart.sh
	//ROOT_DIR = "/foo/bar/", make sure the include the last "/"
	public static final String ROOT_DIR = "/home/hjx/3gtest/";
	
	//DON'T CHANGE BELOW
	public static final String DATA_DIR = ROOT_DIR + "data/";
	
	public static final String VERSION = "4.1.4";
	
	public static final String MAIN_SERVER = "mobiperf.com";
	
	public static final int IP_HEADER_LENGTH = 20;
	public static final int TCP_HEADER_LENGTH = 32;
	public static final int UDP_HEADER_LENGTH = 8;
	public static final int PREFIX_RECEIVE_BUFFER_LENGTH = 1000;
	public static final int TCPDUMP_RECEIVE_BUFFER_LENGTH = 1000000;
	
	public static final int THROUGHPUT_UP_SEGMENT_SIZE = 1300;
	public static final int THROUGHPUT_DOWN_SEGMENT_SIZE = 2600;
	
	public static final long DURATION_IPERF_MILLISECONDS = 20000;
	public static final int RECV_TIMEOUT = 15000;
	public static final int TCP_TIMEOUT_IN_MILLI = 5000; // 5 seconds for timeout

	public static final int PORT_WHOAMI = 5000;
	
	public static final int PORT_BT_RANDOM = 5005;
	
	public static final int PORT_DOWNLINK = 5001;
	public static final int PORT_UPLINK = 5002;
	//MLab nodes, the above two ports are already occupied = =!
	public static final int PORT_MLAB_DOWNLINK = 6001;
	public static final int PORT_MLAB_UPLINK = 6002;
	
	
	public static final int PORT_REPORT = 5004;
	public static final int PORT_TCPDUMP_REPORT = 5006;
	public static final int PORT_COMMAND = 5010;
	
	public static final int PORT_USER_STAT = 5500;
	public static final int PORT_VERSION = 3000;
	
	public static final int PORT_FTP = 21;
	public static final int PORT_SSH = 22;
	public static final int PORT_SMTP = 25;
	public static final int PORT_DNS = 53;
	public static final int PORT_HTTP = 80;
	public static final int PORT_POP = 110;
	public static final int PORT_RPC = 135;
	public static final int PORT_NETBIOS = 139;
	public static final int PORT_IMAP = 143;
	public static final int PORT_SNMP = 161;
	public static final int PORT_HTTPS = 443;
	public static final int PORT_SMB = 445;
	public static final int PORT_SMTP_SSL = 465;
	public static final int PORT_SECURE_IMAP = 585;
	public static final int PORT_AUTHENTICATED_SMTP = 587;
	public static final int PORT_IMAP_SSL = 993;
	public static final int PORT_POP_SSL = 995;
	public static final int PORT_SIP = 5060;
	public static final int PORT_BITTORRENT = 6881;
	public static final int PORT_IOS_SPECIAL = 5223;
	public static final int PORT_ANDROID_SPECIAL = 5228;
	public static final int PORT_HTTP_PROXY = 8080;
	
	public static final int[] PORTS = new int[]{21, 22, 25, 53, 80, 110, 135, 139, 143, 161,
		443, 445, 465, 585, 587, 993, 995, 5060, 6881, 5223, 5228, 8080};

	
	//command 
	public static final String COMMAND_TCP_UPLINK = "COMMAND:TCP:UPLINK";
	public static final String COMMAND_TCP_DOWNLINK = "COMMAND:TCP:DOWNLINK";
	
	public static final String COMMAND_MLAB_INIT_UPLINK = "COMMAND:MLAB:INIT:UPLINK";
	public static final String COMMAND_MLAB_INIT_DOWNLINK = "COMMAND:MLAB:INIT:DOWNLINK";
	public static final String COMMAND_MLAB_END_UPLINK = "COMMAND:MLAB:END:UPLINK";
	public static final String COMMAND_MLAB_END_DOWNLINK = "COMMAND:MLAB:END:DOWNLINK";
	
	public static final String COMMAND_REACH_START = "COMMAND:REACH:START";
	public static final String COMMAND_REACH_STOP = "COMMAND:REACH:STOP";
	

}
