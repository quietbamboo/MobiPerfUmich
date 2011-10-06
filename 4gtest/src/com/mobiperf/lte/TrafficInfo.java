package com.mobiperf.lte;

import android.graphics.drawable.Drawable;

public class TrafficInfo {
	private String mAppName;
	private String mPackageName;
	private Drawable mIcon;
	private long total_sent;
	private long total_recv;
	private long tcp_sent;
	private long tcp_recv;
	private long udp_sent;
	private long udp_recv;

	public String getmAppName() {
		return mAppName;
	}
	public void setmAppName(String mAppName) {
		this.mAppName = mAppName;
	}
	public String getmPackageName() {
		return mPackageName;
	}
	public void setmPackageName(String mPackageName) {
		this.mPackageName = mPackageName;
	}
	public Drawable getIcon() {
		return mIcon;
	}
	public void setIcon(Drawable mIcon) {
		this.mIcon = mIcon;
	}
	public long getTotal_sent() {
		return total_sent;
	}
	public void setTotal_sent(long total_sent) {
		this.total_sent = total_sent;
	}
	public long getTotal_recv() {
		return total_recv;
	}
	public void setTotal_recv(long total_recv) {
		this.total_recv = total_recv;
	}
	public long getTcp_sent() {
		return tcp_sent;
	}
	public void setTcp_sent(long tcp_sent) {
		this.tcp_sent = tcp_sent;
	}
	public long getTcp_recv() {
		return tcp_recv;
	}
	public void setTcp_recv(long tcp_recv) {
		this.tcp_recv = tcp_recv;
	}
	public long getUdp_sent() {
		return udp_sent;
	}
	public void setUdp_sent(long udp_sent) {
		this.udp_sent = udp_sent;
	}
	public long getUdp_recv() {
		return udp_recv;
	}
	public void setUdp_recv(long udp_recv) {
		this.udp_recv = udp_recv;
	}
	
}
