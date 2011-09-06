package com.mobiperf;

public class TrafficInfo {
	private String mAppName;
	private int mUid;
	public int getmUid() {
		return mUid;
	}
	public void setmUid(int mUid) {
		this.mUid = mUid;
	}
	private long mSentBytes;
	public long getmSentBytes() {
		return mSentBytes;
	}
	public void setmSentBytes(long mSentBytes) {
		this.mSentBytes = mSentBytes;
	}
	private long mRecvBytes;
	private long mTotalBytes;
	public String getmAppName() {
		return mAppName;
	}
	public void setmAppName(String mAppName) {
		this.mAppName = mAppName;
	}
	public long getmRecvBytes() {
		return mRecvBytes;
	}
	public void setmRecvBytes(long mRecvBytes) {
		this.mRecvBytes = mRecvBytes;
	}
	public long getmTotalBytes() {
		return mTotalBytes;
	}
	public void setmTotalBytes(long mTotalBytes) {
		this.mTotalBytes = mTotalBytes;
	}
	public TrafficInfo(String mAppName, int mUid, long mSentBytes,
			long mRecvBytes, long mTotalBytes) {
		super();
		this.mAppName = mAppName;
		this.mUid = mUid;
		this.mSentBytes = mSentBytes;
		this.mRecvBytes = mRecvBytes;
		this.mTotalBytes = mTotalBytes;
	}


}
