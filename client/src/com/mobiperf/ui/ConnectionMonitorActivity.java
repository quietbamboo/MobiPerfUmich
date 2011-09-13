package com.mobiperf.ui;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.Button;
import android.widget.ListView;

import com.mobiperf.ConnectionInfo;
import com.mobiperf.ConnectionMonitor;
import com.mobiperf.R;
import com.mobiperf.Adapter.ConnectionStatusAdapter;

public class ConnectionMonitorActivity extends Activity {

	private static final int UNINSTALL_ID = Menu.FIRST;
	private static final int KILL_ID = Menu.FIRST + 1;
	private ConnectionStatusAdapter mAdapter = null;
	private final Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				refresh();
				break;
			}
			super.handleMessage(msg);
		}
	};
	Timer timer = new Timer();
	TimerTask task = new TimerTask() {
        public void run() {
            Message message = new Message();
            message.what = 1;
            handler.sendMessage(message);
        }
    };
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.connection_monitor);
		Button refreshBtn = (Button)this.findViewById(R.id.btn_refresh);
		refreshBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				refresh();
				
			}
		});
		refresh();

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
			refresh();
		}
			return true;
		case KILL_ID: {
			ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
			am.killBackgroundProcesses(mAdapter.getPkgName(info.position));
			refresh();
			return true;
		}
		}

		return super.onContextItemSelected(item);
	}

	private void refresh() {
		ConnectionMonitor cm = new ConnectionMonitor(this);

		List<ConnectionInfo> result = cm
				.getConnectionInfo(ConnectionMonitor.mTcpFile);
		result.addAll(cm.getConnectionInfo(ConnectionMonitor.mTcp6File));
		for (ConnectionInfo ci : result) {
			if (ci.getIcon() == null)
				result.remove(ci);
		}
		mAdapter = new ConnectionStatusAdapter(this, 0, result);
		ListView l = (ListView)findViewById(R.id.list_connection);
		l.setAdapter(mAdapter);
		registerForContextMenu(l);

	}
    @Override
    protected void onDestroy() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        super.onDestroy();
    }
}
