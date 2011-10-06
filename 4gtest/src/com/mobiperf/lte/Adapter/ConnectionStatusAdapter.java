package com.mobiperf.lte.Adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mobiperf.lte.ConnectionInfo;
import com.mobiperf.lte.R;

public class ConnectionStatusAdapter extends ArrayAdapter<ConnectionInfo>  {

	private List<ConnectionInfo> mItems;
	private Context mContext;
	public ConnectionStatusAdapter(Context context, int textViewResourceId, 
            List<ConnectionInfo> items) { 
        super(context, textViewResourceId, items); 
        this.mItems = items; 
        mContext = context;
    }
	public String getPkgName(int position)
	{
		return mItems.get(position).getmPackageName();
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;

        if (view == null) { 
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE); 
            view = inflater.inflate(R.layout.list_item, null); 
        }

        ConnectionInfo ci = mItems.get(position); 
        if (ci != null) { 
            TextView title = (TextView) view 
                    .findViewById(R.id.app_title); 
            title.setText(ci.getmAppName());
            
            TextView remoteAddr = (TextView) view 
                    .findViewById(R.id.app_status); 
            remoteAddr.setText(ci.getmRemoteAddr());
            
            ImageView imageView = (ImageView) view.findViewById(R.id.app_icon);
            imageView.setImageDrawable(ci.getIcon());

            
        }

        return view; 
	}

}
