package com.mobiperf.PerfNearMe;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;
import com.mobiperf.ui.Display;

public class CustomItemizedOverlay extends ItemizedOverlay<OverlayItem> {

	private ArrayList<OverlayItem> items = new ArrayList<OverlayItem>();
	private Context context;
	private LocationManager locationManager; 
	private ServerParser getUserData;
	public static final double RADIUS = .9;
	private int index_item;

	public CustomItemizedOverlay(Drawable defaultMarker) {	
		super(boundCenterBottom(defaultMarker));
	}
	
	// Constructor
	public CustomItemizedOverlay(Drawable defaultMarker, Context context) {
		this(defaultMarker);
		this.context = context;
	}
	
	//@Override
	// Return an overlay item
	protected OverlayItem createItem(int i) {
		return items.get(i);	
	}
	
	//@Override
	// Return the size
	public int size() {
		return items.size();	
	}
	
	//@Override
	//  This places a button on the alert box.  When pressed, they application execute the onclick listener
	protected boolean onTap(int index) {	
	    
	    index_item = index;
		OverlayItem item = items.get(index);
		final AlertDialog.Builder dialog = new AlertDialog.Builder(context);
		dialog.setTitle(item.getTitle());
		dialog.setMessage(item.getSnippet());
		
		
		Location tempLoc = MapApp.showCurrentLocation();
		try {
			getUserData = new ServerParser (tempLoc.getLatitude(), tempLoc.getLongitude(), RADIUS);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Parse the information
		getUserData.splitSite();
	
		dialog.setNeutralButton("More Info", new DialogInterface.OnClickListener() {
			// Click listener on the neutral button of alert box
			public void onClick(DialogInterface arg0, int arg1) {
				//Intent intent = new Intent("android.intent.action.MAIN"); 
				//context.startActivity(intent);
				Display.clearTabView();
				//making random change
				Display.displayResult("Network Type", "Network type used (WiFi, UMTS, CDMA, etc.)", getUserData.getNetworkTypes()[index_item], 0);
				Display.displayResult("Carrier", "Name of cellular carrier", getUserData.getCarriers()[index_item], 0);
				Display.displayResult("Cell ID", "Id of cell tower connected", String.format("%d", getUserData.getCellIds()[index_item]), 0);
				Display.displayResult("Signal Strength", "Signal strength in asu, 0 is worst and 31 is best", String.format("%d", getUserData.getSignalReadings()[index_item]) , 0);
				Display.displayResult("Local IP", "Local IP address of your device, could be private IP", getUserData.getLocalIPs()[index_item], 0);
				Display.displayResult("Seen IP", "IP address of your device seen by a remote server", getUserData.getGlobalIPs()[index_item], 0);
				Display.displayResult("GPS Location", "Latitude (<0 for South) Longitude (<0 for West)", 
						"Latitude:" +getUserData.getLatitudes()[index_item] + " Longitude:" + getUserData.getLongitudes()[index_item], 0);
				Display.displayResult("Downlink throughput", "How many bits per second can be downloaded in TCP", String.format("%f", getUserData.getUpTPs()[index_item]) , 1);
				Display.displayResult("Uplink throughput", "How many bits per second can be uploaded in TCP",  String.format("%f", getUserData.getDownTPs()[index_item]) , 1);
				//Log.i("MobiPerf", "clicked " + position);
				
				Intent i_1 = new Intent(context, com.mobiperf.ui.Display.class);
				context.startActivity(i_1);
				//startActivityForResult(i_1);
				
				//setContentView(R.layout.perfnearme);

			}

        });
		
		dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			
			//@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.dismiss();
				
			}
		});
		dialog.show();
		return true;	
	}

	// This adds an overlay to this.items and then populates this overlay
	public void addOverlay(OverlayItem overlay) {	
		items.add(overlay);
		this.populate();
	}
	
    protected Location showCurrentLocation() {   	
    	Location location = null;
     	if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
    		//Log.v("jeff", "on wifi");
        	 location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER); 
    	}
    	else if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
    		//Log.v("jeff", "on gps");
        	 location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER); 
    	}
    	return location;
    }  
		   
}


