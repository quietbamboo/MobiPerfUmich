package com.mobiperf;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
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
			{
				TrafficInfo ti = new TrafficInfo();
				ti.setmAppName(Utilities.getAppLabel(mContext, uid));
				ti.setmPackageName(Utilities.getAppPkg(mContext,uid));
				ti.setIcon(Utilities.getAppIcon(mContext, uid));
				ti.setTotal_recv(TrafficStats.getUidRxBytes(uid));
				ti.setTotal_sent(TrafficStats.getUidTxBytes(uid));
				result.add(ti);
			}
		}
		return result;
	}
}
