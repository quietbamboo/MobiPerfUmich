package com.mobiperf;

import android.graphics.drawable.Drawable;

public class ConnectionInfo {

	private String mAppName;
	private String mPackageName;
	private Drawable mIcon;
	private String mRemoteAddr;
	
	public String getmPackageName() {
		return mPackageName;
	}
	public void setmPackageName(String mPackageName) {
		this.mPackageName = mPackageName;
	}


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
		return mIcon;
	}
	public void setIcon(Drawable icon) {
		mIcon = icon;
	}
	public ConnectionInfo(String mPackageName, String mAppName, String mRemoteAddr, Drawable icon) {
		super();
		this.mAppName = mAppName;
		this.mPackageName = mPackageName;
		this.mRemoteAddr = mRemoteAddr;
		mIcon = icon;
	}
	
}
