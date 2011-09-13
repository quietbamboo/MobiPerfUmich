package com.mobiperf.Adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mobiperf.ConnectionInfo;
import com.mobiperf.R;
import com.mobiperf.TrafficInfo;

public class TrafficStatusAdapter extends ArrayAdapter<TrafficInfo> {


	private List<TrafficInfo> mItems;
	private Context mContext;

	public TrafficStatusAdapter(Context context, int textViewResourceId,
			List<TrafficInfo> items) {
		super(context, textViewResourceId, items);
		this.mItems = items;
		mContext = context;
	}



	public String getPkgName(int position) {
		return mItems.get(position).getmPackageName();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;

		if (view == null) {
			LayoutInflater inflater = (LayoutInflater) mContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(R.layout.list_item, null);
		}

		TrafficInfo ci = mItems.get(position);
		if (ci != null) {
			TextView title = (TextView) view.findViewById(R.id.app_title);
			title.setText(ci.getmAppName());

			TextView remoteAddr = (TextView) view.findViewById(R.id.app_status);
			remoteAddr.setText("sent: " + ci.getTotal_sent() + "\nrecv: "
					+ ci.getTotal_recv());

			ImageView imageView = (ImageView) view.findViewById(R.id.app_icon);
			imageView.setImageDrawable(ci.getIcon());

		}

		return view;
	}

}
