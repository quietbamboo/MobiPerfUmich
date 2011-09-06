package com.mobiperf;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.TrafficStats;

public class TrafficMonitor {

	private Context mContext;
	
	private PackageManager mPm;

	public TrafficMonitor(Context c) {
		mContext = c;
	}

	public List<TrafficInfo> getTrafficInfo() {
		List<TrafficInfo> result = new ArrayList<TrafficInfo>();
		mPm = mContext.getPackageManager();
		// get All applications' uids
		List<ApplicationInfo> allInfo = mPm.getInstalledApplications(0);
		// using a set to remove duplicates
		Set<Integer> uids = new TreeSet<Integer>();
		for (ApplicationInfo info : allInfo) {
			Integer uid = info.uid;
			if (uids.contains(uid))
				continue;
			uids.add((Integer) uid);
			long recv = TrafficStats.getUidRxBytes(uid);
			long sent = TrafficStats.getUidTxBytes(uid);

			// remove those application which do not sent and recv data
			if (recv > 0 || sent > 0)
				result.add(new TrafficInfo((String) mPm
						.getApplicationLabel(info), uid, recv, sent, recv
						+ sent));
		}

		return result;
	}
}
