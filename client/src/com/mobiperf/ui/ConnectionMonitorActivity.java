package com.mobiperf.ui;

import java.util.List;

import android.app.ActivityManager;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;

import com.mobiperf.ConnectionInfo;
import com.mobiperf.ConnectionMonitor;
import com.mobiperf.R;
import com.mobiperf.Adapter.ConnectionStatusAdapter;

public class ConnectionMonitorActivity extends ListActivity {

	private static final int UNINSTALL_ID = Menu.FIRST;
	private static final int KILL_ID = Menu.FIRST + 1;
	private ConnectionStatusAdapter mAdapter = null;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		ConnectionMonitor cm = new ConnectionMonitor(this);

		List<ConnectionInfo> result = cm
				.getConnectionInfo(ConnectionMonitor.mTcpFile);
		result.addAll(cm.getConnectionInfo(ConnectionMonitor.mTcp6File));
		for (ConnectionInfo ci : result) {
			if (ci.getIcon() == null)
				result.remove(ci);
		}
		mAdapter = new ConnectionStatusAdapter(this, 0, result);
		setListAdapter(mAdapter);
		registerForContextMenu(getListView());

	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		// TODO Auto-generated method stub
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.add(0, UNINSTALL_ID, 0, R.string.menu_uninstall);
		menu.add(0, KILL_ID, 0, R.string.menu_kill);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();
		switch (item.getItemId()) {
		case UNINSTALL_ID: {
			

			Intent delete = new Intent(Intent.ACTION_DELETE);

			Uri data = Uri.fromParts("package",
					mAdapter.getPkgName(info.position), null);
			delete.setData(data);
			startActivity(delete);
		}
			return true;
		case KILL_ID: {
			ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
			am.restartPackage(mAdapter.getPkgName(info.position));
			return true;
		}
		}

		return super.onContextItemSelected(item);
	}
}
