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
	
	public static final String VERSION = "4.1.3";
	
	public static final int IP_HEADER_LENGTH = 20;
	public static final int TCP_HEADER_LENGTH = 32;
	public static final int UDP_HEADER_LENGTH = 8;
	public static final int PREFIX_RECEIVE_BUFFER_LENGTH = 1000;
	public static final int TCPDUMP_RECEIVE_BUFFER_LENGTH = 1000000;
	
	public static final long DURATION_IPERF_MILLISECONDS = 16000;
	public static final int RECV_TIMEOUT = 30000;

	public static final int PORT_WHOAMI = 5000;
	
	public static final int PORT_BT_RANDOM = 5005;
	public static final int PORT_DOWN_THRU = 5001;
	public static final int PORT_UP_THRU = 5002;
	public static final int PORT_REPORT = 5004;
	public static final int PORT_TCPDUMP_REPORT = 5006;
	
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

	public static final String TCPDUMP_DIR = "/home/hjx/3gtest/";
	public static final String DATA_ROOT = "/home/hjx/3gtest/data/";
	
	//command 
	public static final String COMMAND_TCP_UPLINK = "COMMAND:TCP:UPLINK";
	public static final String COMMAND_TCP_DOWNLINK = "COMMAND:TCP:DOWNLINK";
	public static final String COMMAND_REACH_START = "COMMAND:REACH:START";
	
	public static final String COMMAND_REACH_STOP = "COMMAND:REACH:STOP";
	

}
