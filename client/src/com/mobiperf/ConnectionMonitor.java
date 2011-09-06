package com.mobiperf;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;

public class ConnectionMonitor {
	public static final String mTcp6File = "/proc/net/tcp6";
	public static final String mTcpFile = "/proc/net/tcp";
	private Context mContext;

	public ConnectionMonitor(Context c) {
		mContext = c;
	}

	public List<ConnectionInfo> getConnectionInfo(String netWorkFile) {
		int offset = 0;
		if (netWorkFile.equals(mTcp6File))
			offset = 24;
		List<ConnectionInfo> results = new ArrayList<ConnectionInfo>();
		try {
			RandomAccessFile file = new RandomAccessFile(netWorkFile, "r");
			// discard first line
			String line = file.readLine();

			while ((line = file.readLine()) != null) {

				String[] all = line.split(" +");
				// parsing the remote address
				String remAddr = all[3];
				if (remAddr.equals("00000000:0000"))
					continue;
				String convertAddress = new String();
				for (int i = 3; i >= 0; i--) {
					convertAddress += Integer.valueOf(
							remAddr.substring(i * 2 + offset, i * 2 + 2
									+ offset), 16).toString();
					if (i != 0)
						convertAddress += ".";
				}

				// now parsing the port
				convertAddress += ":";
				convertAddress += Integer.valueOf(
						remAddr.substring(9 + offset, 13 + offset), 16)
						.toString();
				convertAddress += " ";

				// now get the application's name
				int uid = Integer.parseInt(all[8]);
				try
				{
				results.add(new ConnectionInfo(Utilities.getAppPkg(mContext,
						uid), Utilities.getAppLabel(mContext, uid),
						convertAddress, Utilities.getAppIcon(mContext, uid)));
				}
				catch (NullPointerException e)
				{
					continue;
				}
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return results;
	}
}
