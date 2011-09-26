package com.mobiperf.ui;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.app.Activity;
import android.app.ActivityManager;
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
import android.widget.Button;
import android.widget.ListView;

import com.mobiperf.R;
import com.mobiperf.TrafficInfo;
import com.mobiperf.TrafficMonitor;
import com.mobiperf.Adapter.TrafficStatusAdapter;

public class TrafficMonitorActivity extends Activity {

	private static final int UNINSTALL_ID = Menu.FIRST;
	private static final int KILL_ID = Menu.FIRST + 1;

	private static final int FIRST_STATUS = 1;
	public static final int STATUS_TOTAL_SENT = FIRST_STATUS + 0;
	public static final int STATUS_TOTAL_RECV = FIRST_STATUS + 1;
	public static final int STATUS_TOTAL = FIRST_STATUS + 2;
	public static final int STATUS_NAME = FIRST_STATUS + 3;

	private int status = STATUS_NAME;
	private TrafficStatusAdapter mAdapter = null;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.traffic_monitor);
		Button refreshBtn = (Button) this.findViewById(R.id.btn_refresh);
		refreshBtn.setOnClickListener(new View.OnClickListener() {
			// @Override
			public void onClick(View v) {
				refresh();
			}
		});

		Button nameBtn = (Button) this.findViewById(R.id.btn_name);
		nameBtn.setOnClickListener(new View.OnClickListener() {
			// @Override
			public void onClick(View v) {
				status = STATUS_NAME;
				refresh();
			}
		});

		Button totalBtn = (Button) this.findViewById(R.id.btn_total);
		totalBtn.setOnClickListener(new View.OnClickListener() {
			// @Override
			public void onClick(View v) {
				status = STATUS_TOTAL;
				refresh();
			}
		});
		Button totalSentBtn = (Button) this.findViewById(R.id.btn_total_tx);
		totalSentBtn.setOnClickListener(new View.OnClickListener() {
			// @Override
			public void onClick(View v) {
				status = STATUS_TOTAL_SENT;
				refresh();
			}
		});
		Button totalRecvBtn = (Button) this.findViewById(R.id.btn_total_rx);
		totalRecvBtn.setOnClickListener(new View.OnClickListener() {
			// @Override
			public void onClick(View v) {
				status = STATUS_TOTAL_RECV;
				refresh();
			}
		});
		refresh();

	}

	protected void onResume() {
		super.onResume();
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
			return true;
		}

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
		TrafficMonitor cm = new TrafficMonitor(this);

		List<TrafficInfo> result = cm.getTrafficInfo();

		for (TrafficInfo ci : result) {
			if (ci.getIcon() == null)
				result.remove(ci);
		}
		switch (status) {
		case STATUS_NAME:
			Collections.sort(result, new Comparator<TrafficInfo>() {
				public int compare(TrafficInfo arg0, TrafficInfo arg1) {
					return (int) (arg0.getmAppName().compareTo(arg1
							.getmAppName()));
				}
			});
			break;

		case STATUS_TOTAL:
			Collections.sort(result, new Comparator<TrafficInfo>() {
				public int compare(TrafficInfo arg0, TrafficInfo arg1) {
					return (int) ((arg1.getTotal_sent() + arg1.getTotal_recv()) - (arg0
							.getTotal_sent() + arg0.getTotal_recv()));
				}
			});
			break;

		case STATUS_TOTAL_SENT:
			Collections.sort(result, new Comparator<TrafficInfo>() {
				public int compare(TrafficInfo arg0, TrafficInfo arg1) {
					return (int) (arg1.getTotal_sent() - arg0.getTotal_sent());
				}
			});
			break;
		case STATUS_TOTAL_RECV:
			Collections.sort(result, new Comparator<TrafficInfo>() {
				public int compare(TrafficInfo arg0, TrafficInfo arg1) {
					return (int) (arg1.getTotal_recv() - arg0.getTotal_recv());
				}
			});
			break;
		}
		mAdapter = new TrafficStatusAdapter(this, 0, result);
		ListView l = (ListView) findViewById(R.id.list_connection);
		l.setAdapter(mAdapter);
		registerForContextMenu(l);

	}
}
