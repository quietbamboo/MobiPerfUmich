package com.mobiperf.ui;

import java.util.List;

import android.app.ListActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;

import com.mobiperf.ConnectionInfo;
import com.mobiperf.ConnectionMonitor;
import com.mobiperf.R;
import com.mobiperf.TrafficInfo;
import com.mobiperf.TrafficMonitor;
import com.mobiperf.Adapter.ConnectionStatusAdapter;

public class ConnectionMonitorActivity extends ListActivity {
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		ConnectionMonitor cm = new ConnectionMonitor(this);

		List<ConnectionInfo> result = cm.getConnectionInfo(ConnectionMonitor.mTcpFile);
		result.addAll(cm.getConnectionInfo(ConnectionMonitor.mTcp6File));
		for (ConnectionInfo ci : result)
		{
			if(ci.getIcon() == null)
				result.remove(ci);
		}
		setListAdapter(new ConnectionStatusAdapter(this, 0,
				result));

	}
}
