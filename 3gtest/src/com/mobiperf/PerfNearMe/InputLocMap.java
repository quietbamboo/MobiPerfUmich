package com.mobiperf.PerfNearMe;

import java.io.IOException;
import java.util.List;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;
import com.mobiperf.R;

public class InputLocMap extends MapActivity{
	private Geocoder place;
	private List<Address> renderedLocation = null;
	private List<Overlay> mapOverlays;
	private CustomItemizedOverlay itemizedOverlay;
	private Drawable drawable;
    private MapView mapView;
    private ServerParser getData;
    private String serverInformation = null;

	
	protected void onCreate (Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.perfnearme_map);
		
		mapView = (MapView) findViewById(R.id.mapview);      
        mapView.setBuiltInZoomControls(true);
       // mapView.setSatellite(true);
        
        mapOverlays = mapView.getOverlays();
        drawable = this.getResources().getDrawable(R.drawable.pinbig);
        itemizedOverlay = new CustomItemizedOverlay(drawable, this);
        
		place = new Geocoder (this);

        try {
        	// Gets a location from the users input
			renderedLocation = place.getFromLocationName(InputLocation.getInputLocation().getText().toString(), 1);
			if (renderedLocation.size() > 0){ 
				try {
					// Creates a new string parser and parses the information
					getData = new ServerParser (renderedLocation.get(0).getLatitude(), renderedLocation.get(0).getLongitude(), MapApp.RADIUS);
					getData.splitSite();
					// Puts the pins onto the map
					for (int i = 0; i < ServerParser.SIZE; i++){
						serverInformation = "Network Type: " + getData.getNetworkTypes()[i] + "\nCarrier: " + getData.getCarriers()[i] +
						"\nSignal Reading: " + getData.getSignalReadings()[i];
						OverlayItem overlayitem = new OverlayItem(new GeoPoint((int)(getData.getLatitudes()[i]*1E6), 
								(int)(getData.getLongitudes()[i]*1E6)), "User Info", serverInformation);
						itemizedOverlay.addOverlay(overlayitem);
					}
				} 
	        	catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	        	// Puts the users entered location as the center
				//OverlayItem overlayitem = new OverlayItem(new GeoPoint((int)(renderedLocation.get(0).getLatitude()*1E6), 
					//	(int)(renderedLocation.get(0).getLongitude()*1E6)), "Input Location", InputLocation.getInputLocation().getText().toString());
				//itemizedOverlay.addOverlay(overlayitem);
				mapOverlays.add(itemizedOverlay);
				// Sets the map view
				MapController mapController = mapView.getController();
				mapController.setCenter(new GeoPoint((int)(renderedLocation.get(0).getLatitude()*1E6), 
						(int)(renderedLocation.get(0).getLongitude()*1E6)));
				//mapController.animateTo(new GeoPoint((int)(renderedLocation.get(0).getLatitude()*1E6), 
				//		(int)(renderedLocation.get(0).getLongitude()*1E6)));
				mapController.setZoom(12);
			}
			else {
				String message = String.format(
	        			"Could not find location as entered: " + InputLocation.getInputLocation().getText().toString() + "\n");
	        	Toast.makeText(InputLocMap.this, message, 2*Toast.LENGTH_LONG).show();
	        	startActivity(new Intent ("com.mobiperf.PerfNearMe.INPUTLOCATION"));
			}
			 
		} catch (IOException e) {
			// TODO Auto-generated catch block
			String message = String.format(
        			"Could not find location as entered " + InputLocation.getInputLocation().getText().toString() + "\n");
        	Toast.makeText(InputLocMap.this, message, Toast.LENGTH_LONG*3).show();
        	startActivity(new Intent ("com.mobiperf.PerfNearMe.INPUTLOCATION"));
			e.printStackTrace();
		} 
	}
	
	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}
}
