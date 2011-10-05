package com.mobiperf.PerfNearMe;

import java.util.List;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;
import com.mobiperf.R;

public class MapApp extends MapActivity {
        private MapView mapView;
        private List<Overlay> mapOverlays;
        private CustomItemizedOverlay itemizedOverlay;
        private Drawable drawable;
        private static LocationManager locationManager; 
        private ServerParser getUserData;
        public static final double RADIUS = .9;
        private String serverInfo = null;
        
        
        @Override
        public void onCreate(Bundle savedInstanceState) {
        	super.onCreate(savedInstanceState);
        	setContentView(R.layout.perfnearme_map);
        
        	setLayout();
            
        	// Get current location
        	locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        	MyLocationListener myLocation = new MyLocationListener ();
        	
        	//Changed by Junxian: PASSIVE_PROVIDER is API level 8, target level is 4
        	if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
        		locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 2000, 3, myLocation);
        	}
        	else {
        		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 3, myLocation);
        	}
        	
        	Location tempLoc = showCurrentLocation();
        	// End of get current location
        	
        	if (tempLoc != null){
        		try {
        			// Get information from the server
        			getUserData = new ServerParser (tempLoc.getLatitude(), tempLoc.getLongitude(), RADIUS);

        			// Parse the information
        			getUserData.splitSite();
        			
        			// Put pins on the map
        			for (int i = 0; i < ServerParser.SIZE; i++){
        				serverInfo = "Network Type: " + getUserData.getNetworkTypes()[i] + "\nCarrier: " + getUserData.getCarriers()[i];
        				generatePoints((int)(getUserData.getLatitudes()[i]*1E6),(int)(getUserData.getLongitudes()[i]*1E6), "User Info", 
        						serverInfo);
        			}
        		} 
        		catch (Exception e) {
        			// TODO Auto-generated catch block
        			e.printStackTrace();
        		}
        		GeoPoint point1 = new GeoPoint((int)(tempLoc.getLatitude()*1E6), (int)(tempLoc.getLongitude()*1E6));
        		//generatePoints ((int)(tempLoc.getLatitude()*1E6),(int)(tempLoc.getLongitude()*1E6), 
        			//	"Current Location", "You are here");
        		mapOverlays.add(itemizedOverlay);
        		setUserView (point1);
        	}
        	else {
        		//this.startActivity(new Intent("com.example.mapapp.MAPAPP"));
        	}
        }
      
        // function to get the current location and display it quickly as a text box
        protected static Location showCurrentLocation() {   	
        	Location location = null;
        	//Junxian: change the PASSIVE_PROVIDER to NETWORK_PROVIDER due to API 4 level does not support former
         	if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
        		//Log.v("jeff", "on wifi");
            	 location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER); 
        	}
        	else {
        		//Log.v("jeff", "on gps");
            	 location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER); 
        	}
        	return location;
        }  

        
        @Override
        protected boolean isRouteDisplayed() {
        	return false;
        }
            
        // Sets the layout of the map
        private void setLayout (){
        	mapView = (MapView) findViewById(R.id.mapview);      
        	mapView.setBuiltInZoomControls(true);
        	//mapView.setSatellite(true);
                
        	mapOverlays = mapView.getOverlays();
        	drawable = this.getResources().getDrawable(R.drawable.pinbig);
        	itemizedOverlay = new CustomItemizedOverlay(drawable, this);
        }
            
        // Generates a new point and adds it to the overlay ArrayList 
        private void generatePoints (int lat, int longi, String label, String info){
        	OverlayItem overlayitem = new OverlayItem(new GeoPoint(lat, longi), label, info);
        	itemizedOverlay.addOverlay(overlayitem);
        }
        
        // Sets the view of the map
        private void setUserView (GeoPoint point){
        	MapController mapController = mapView.getController();
        	mapController.setCenter(point);
        	//mapController.animateTo(point);
        	mapController.setZoom(12);
        }
            
    
        private class MyLocationListener implements LocationListener { 	
        	public void onLocationChanged(Location location) {   
        		 if (location != null) {
                     /*Toast.makeText(getBaseContext(), 
                         "Location changed : Lat: " + location.getLatitude() + 
                         " Lng: " + location.getLongitude(), 
                         Toast.LENGTH_SHORT).show();*/
                 }

        	}           	
            	
        	public void onStatusChanged(String s, int i, Bundle b) {
        		/*Toast.makeText(MapApp.this, "Provider status changed",
        				Toast.LENGTH_LONG).show();*/
        	}
            	
            	 
        	public void onProviderDisabled(String s) {
        	}
        	
            	             	
        	public void onProviderEnabled(String s) {
        		/*Toast.makeText(MapApp.this,
        				"Provider enabled by the user. GPS turned on",
        				Toast.LENGTH_LONG).show();*/
        	}         	            	                 	
        }              
}