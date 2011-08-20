/**
 * @author Junxian Huang
 * @date Sep 21, 2009
 * @time 7:52:21 PM
 * @organization University of Michigan, Ann Arbor
 */
package servers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.util.Calendar;

import common.BaseTcpWorker;
import common.Definition;

/**
 * @author Junxian Huang
 *
 */
public class UserStateWorker extends BaseTcpWorker {
 
	public long id;
	
	public  void  run() {

		try {
			
			client.setSoTimeout(Definition.RECV_TIMEOUT);

			// String line = "";
			BufferedReader in = null;
			PrintWriter out = null;
			char buffer[] = new char[20480];

			// //////////////////////////////////////////
			// Common init

			this.id = this.getId();

			System.out.println("<" + id + "> UserState Thread starts");

			in = new BufferedReader(new InputStreamReader(client
					.getInputStream()));
			out = new PrintWriter(client.getOutputStream(), true);

			
			// //////////////////////////////////////////
			// major part

			String s2 = null;
	    	System.out.println("<Thread " + id + "> User State");
			int user_count = 0;
			int run_count = 0;
			int last_run_count = 0;
			BufferedReader stdInput2 = new BufferedReader(new 
	                FileReader(new File("all.res")));
			while ((s2 = stdInput2.readLine()) != null) {
                //System.out.println(s2);
				if(s2.startsWith("d")){
					user_count++;
				}
				if(s2.endsWith(".out")){
					run_count++;
					String[] parts = s2.split(" ");
					if(parts[parts.length - 1].endsWith(".out")){
						if(parts[parts.length - 1].startsWith("nohup")){
							continue;
						}
						String num_str = parts[parts.length - 1].substring(0, parts[parts.length - 1].length() - 4);
						long num = Long.parseLong(num_str);
						if(num_str.length() < 11){
							num *= 1000;
						}
						if(System.currentTimeMillis() - num < 3600 * 1000){
							//within 1 hour
							last_run_count++;
						}
					}
				}
            }
			
			
			
			String s2_wm = null;
	    	System.out.println("<Thread " + id + "> User State");
			int user_count_wm = 0;
			int run_count_wm = 0;
			int last_run_count_wm = 0;
			BufferedReader stdInput2_wm = new BufferedReader(new 
	                FileReader(new File("w.res")));
			while ((s2_wm = stdInput2_wm.readLine()) != null) {
                //System.out.println(s2);
				if(s2_wm.startsWith("d")){
					user_count_wm++;
				}
				if(s2_wm.endsWith(".out")){
					run_count_wm++;
					String[] parts = s2_wm.split(" ");
					if(parts[parts.length - 1].endsWith(".out")){
						if(parts[parts.length - 1].startsWith("nohup")){
							continue;
						}
						String num_str = parts[parts.length - 1].substring(0, parts[parts.length - 1].length() - 4);
						long num = Long.parseLong(num_str);
						if(System.currentTimeMillis() - num * 1000 < 3600 * 1000){
							//within 1 hour
							last_run_count_wm++;
						}
					}
				}
            }
			
			
			
			String s2_gphone = null;
	    	System.out.println("<Thread " + id + "> User State");
			
			int user_count_gphone = 0;
			int run_count_gphone = 0;
			int last_run_count_gphone = 0;
			BufferedReader stdInput2_gphone = new BufferedReader(new 
	                FileReader(new File("g.res")));
			while ((s2_gphone = stdInput2_gphone.readLine()) != null) {
                //System.out.println(s2);
				if(s2_gphone.startsWith("d")){
					user_count_gphone++;
				}
				if(s2_gphone.endsWith(".out")){
					run_count_gphone++;
					String[] parts = s2_gphone.split(" ");
					if(parts[parts.length - 1].endsWith(".out")){
						if(parts[parts.length - 1].startsWith("nohup")){
							continue;
						}
						String num_str = parts[parts.length - 1].substring(0, parts[parts.length - 1].length() - 4);
						long num = Long.parseLong(num_str);
						//special for GPHone
						if(System.currentTimeMillis() - num < 3600 * 1000){
							//within 1 hour
							last_run_count_gphone++;
						}
					}
				}
            }
			
			String s2_iphone = null;
	    	System.out.println("<Thread " + id + "> User State");
			
			int user_count_iphone = 0;
			int run_count_iphone = 0;
			int last_run_count_iphone = 0;
			BufferedReader stdInput2_iphone = new BufferedReader(new 
	                FileReader(new File("i.res")));
			while ((s2_iphone = stdInput2_iphone.readLine()) != null) {
                //System.out.println(s2);
				if(s2_iphone.startsWith("d")){
					user_count_iphone++;
				}
				if(s2_iphone.endsWith(".out")){
					run_count_iphone++;
					String[] parts = s2_iphone.split(" ");
					if(parts[parts.length - 1].endsWith(".out")){
						if(parts[parts.length - 1].startsWith("nohup")){
							continue;
						}
						String num_str = parts[parts.length - 1].substring(0, parts[parts.length - 1].length() - 4);
						long num = Long.parseLong(num_str);
						if(System.currentTimeMillis() - num * 1000 < 3600 * 1000){
							//within 1 hour
							last_run_count_iphone++;
						}
					}
				}
            }
			
			System.out.println("here");
			int new_user_count_iphone = 0;
			int new_user_count_gphone = 0;
			int new_user_count_wm = 0;
			
			long current = 0;//in minutes
			current += (Calendar.getInstance().get(Calendar.YEAR) - 2009) * 365 * 24 * 60;
			current += (Calendar.getInstance().get(Calendar.MONTH) + 1) * 30 * 24 * 60;
			current += (Calendar.getInstance().get(Calendar.DATE)) * 24 * 60;
			current += (Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) * 60;
			current += (Calendar.getInstance().get(Calendar.MINUTE));
			String s = null;
			
			BufferedReader stdInput_ii = new BufferedReader(new 
	                FileReader(new File("ii.res")));
			while ((s = stdInput_ii.readLine()) != null) {
				if(!s.startsWith("d")){
					continue;
				}
				while(s.length() > 10 && (!s.startsWith("20") || s.charAt(4) != '-' || s.charAt(7) != '-')){
					s = s.substring(1);
				}
				
				//System.out.println(s2);
				String[] parts = s.split(" ");
				//4 drwxrwxrwx 2 root root 4096 2009-09-25 08:58 006b427e6be039c643f543a4ff4f60a7849b3e1d
				//6 7
				
				String[] parts1 = parts[0].split("-");
				String[] parts2 = parts[1].split(":");
				long x = 0;
				x += (Integer.parseInt(parts1[0]) - 2009) * 365 * 24 * 60;
				x += (Integer.parseInt(parts1[1])) * 30 * 24 * 60;
				x += (Integer.parseInt(parts1[2])) * 24 * 60;
				x += (Integer.parseInt(parts2[0])) * 60;
				x += (Integer.parseInt(parts2[1]));
				
				if(current - x < 60){
					new_user_count_iphone++;
				}
            }
			
			s = null;
			
			BufferedReader stdInput_gg = new BufferedReader(new 
	                FileReader(new File("gg.res")));
			while ((s = stdInput_gg.readLine()) != null) {
				if(!s.startsWith("d")){
					continue;
				}
				
				while(s.length() > 10 && (!s.startsWith("20") || s.charAt(4) != '-' || s.charAt(7) != '-')){
					s = s.substring(1);
				}
				
				if(!s.startsWith("2009")){
					System.out.println("error " + s);
				}
				//System.out.println(s2);
				String[] parts = s.split(" ");
				//4 drwxrwxrwx 2 root root 4096 2009-09-25 08:58 006b427e6be039c643f543a4ff4f60a7849b3e1d
				//6 7
				//System.out.println(s + " + " + parts.length);
				
				String[] parts1 = parts[0].split("-");
				String[] parts2 = parts[1].split(":");
				long x = 0;
				x += (Integer.parseInt(parts1[0]) - 2009) * 365 * 24 * 60;
				x += (Integer.parseInt(parts1[1])) * 30 * 24 * 60;
				x += (Integer.parseInt(parts1[2])) * 24 * 60;
				x += (Integer.parseInt(parts2[0])) * 60;
				x += (Integer.parseInt(parts2[1]));
				if(current - x < 60){
					new_user_count_gphone++;
				}
            }
			
			s = null;
			
			BufferedReader stdInput_ww = new BufferedReader(new 
	                FileReader(new File("ww.res")));
			while ((s = stdInput_ww.readLine()) != null) {
				if(!s.startsWith("d")){
					continue;
				}
				while(s.length() > 10 && (!s.startsWith("20") || s.charAt(4) != '-' || s.charAt(7) != '-')){
					s = s.substring(1);
				}
				//s = s.substring("drwxrwxrwx 2 root root  4096 ".length());
				//System.out.println(s2);
				String[] parts = s.split(" ");
				//4 drwxrwxrwx 2 root root 4096 2009-09-25 08:58 006b427e6be039c643f543a4ff4f60a7849b3e1d
				//6 7
				
				String[] parts1 = parts[0].split("-");
				String[] parts2 = parts[1].split(":");
				long x = 0;
				x += (Integer.parseInt(parts1[0]) - 2009) * 365 * 24 * 60;
				x += (Integer.parseInt(parts1[1])) * 30 * 24 * 60;
				x += (Integer.parseInt(parts1[2])) * 24 * 60;
				x += (Integer.parseInt(parts2[0])) * 60;
				x += (Integer.parseInt(parts2[1]));	
				if(current - x < 60){
					new_user_count_wm++;
				}
            }
            
			
			System.out.println("Till now, " + user_count + " users have run our application for " + run_count + " times. ( " + last_run_count_gphone + ")");
			//user_count = user_count - (user_count % 100) + 100;
			//run_count = run_count - (run_count % 100) + 100;
			
			//out.print("Till now, <font color=orange>" + user_count + "</font> users have run our application for <font color=orange>" + run_count + "</font> times.");
			out.print("" + user_count + "," + run_count + "," + user_count_iphone + "," + run_count_iphone + "," +
					user_count_gphone + "," + run_count_gphone + "," + user_count_wm + "," + run_count_wm + ", " + 
					last_run_count + ", " + last_run_count_iphone + "," + last_run_count_gphone + "," + last_run_count_wm + "," + 
					new_user_count_iphone + "," + new_user_count_gphone + "," + new_user_count_wm);
			System.out.println("" + user_count + "," + run_count + "," + user_count_iphone + "," + run_count_iphone + "," +
					user_count_gphone + "," + run_count_gphone + "," + user_count_wm + "," + run_count_wm + ", " + 
					last_run_count + ", " + last_run_count_iphone + "," + last_run_count_gphone + "," + last_run_count_wm + "," + 
					new_user_count_iphone + "," + new_user_count_gphone + "," + new_user_count_wm);
			out.flush();
			
			in.close();
			out.close();
			client.close();

		

			System.out.println("<" + id + "> Thread ends");

		} catch (IOException e) {
			try {
				if(client != null){
					client.close();
				}
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			e.printStackTrace();
		}
	}

}
