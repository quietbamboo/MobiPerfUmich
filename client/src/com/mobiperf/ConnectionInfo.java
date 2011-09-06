package com.mobiperf;

import android.graphics.drawable.Drawable;

public class ConnectionInfo {

	private String mAppName;
	private String mRemoteAddr;
	private Drawable Icon;
	public String getmAppName() {
		return mAppName;
	}
	public void setmAppName(String mAppName) {
		this.mAppName = mAppName;
	}
	public String getmRemoteAddr() {
		return mRemoteAddr;
	}
	public void setmRemoteAddr(String mRemoteAddr) {
		this.mRemoteAddr = mRemoteAddr;
	}
	public Drawable getIcon() {
		return Icon;
	}
	public void setIcon(Drawable icon) {
		Icon = icon;
	}
	public ConnectionInfo(String mAppName, String mRemoteAddr, Drawable icon) {
		super();
		this.mAppName = mAppName;
		this.mRemoteAddr = mRemoteAddr;
		Icon = icon;
	}
	
}
