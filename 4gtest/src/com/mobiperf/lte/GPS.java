/****************************
 * This file is part of the MobiPerf project (http://mobiperf.com). 
 * We make it open source to help the research community share our efforts.
 * If you want to use all or part of this project, please give us credit and cite MobiPerf's official website (mobiperf.com).
 * The package is distributed under license GPLv3.
 * If you have any feedbacks or suggestions, don't hesitate to send us emails (3gtest@umich.edu).
 * The server suite source code is not included in this package, if you have specific questions related with servers, please also send us emails
 * 
 * Contact: 3gtest@umich.edu
 * Development Team: Junxian Huang, Birjodh Tiwana, Zhaoguang Wang, Zhiyun Qian, Cheng Chen, Yutong Pei, Feng Qian, Qiang Xu
 * Copyright: RobustNet Research Group led by Professor Z. Morley Mao, (Department of EECS, University of Michigan, Ann Arbor) and Microsoft Research
 *
 ****************************/

package com.mobiperf.lte;

import java.util.List;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

class GPS {
	private static LocationManager lm = null;
	private static LocationListener locationListener;
	public static Location location;
	public static double latitude = -2147483648; //default invalid values
	public static double longitude = -2147483648;

	private static Location culocationGPS = null;
	private static Location culocationNetwork = null;


	public static Location getCurrentLocation() {
		Location retLocation = null;
		//if ((flagStopUpdate == true)) 
		{
			culocationGPS = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
			culocationNetwork = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
			//Log.v("LOG", "culocationGPS: " + culocationGPS);
			//Log.v("LOG", "culocationNetwork: " + culocationNetwork);

			if (culocationGPS == null && culocationNetwork == null) {
				retLocation = lm.getLastKnownLocation("passive");
				//Log.v("LOG", "passive: " + retLocation);
				if (retLocation == null) {
					retLocation = new Location("passive");
					//Log.v("LOG", "new passive: " + retLocation);
				}
			}
			stopAllUpdate();

			if(culocationGPS != null)
				retLocation = culocationGPS;
			else if(culocationNetwork != null)
				retLocation = culocationNetwork;
		}
		return retLocation;
	}


	public static void stopAllUpdate() {
		lm.removeUpdates(locationListener);
	}

	public static void location(Context context) {
		lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		GPS gps = new GPS();
		locationListener = gps.new MyLocationListener();
		gps.start();

	}

	private void start() {

		//handler.postDelayed(showTime, 1000);
		if(lm == null)
			return;

		List<String> list = lm.getAllProviders();
		String provider = null;
		String provider2 = null;
		for (int i = 0; i < list.size(); i++) {
			if (lm.isProviderEnabled(list.get(i))) {
				if (provider == null)
					provider = list.get(i);
				else
					provider2 = list.get(i);

				if (provider != null && provider2 != null)
					break;
			}
		}

		location = null;

		if (provider != null)
			lm.requestLocationUpdates(provider, 100, 0, locationListener);
		if (provider2 != null)
			lm.requestLocationUpdates(provider2, 100, 0, locationListener);
	}

	private class MyLocationListener implements LocationListener {
		public void onLocationChanged(Location loc) {
			// Called when the location has changed.
			if (loc != null) {
				location = loc;
				latitude = loc.getLatitude();
				longitude = loc.getLongitude();
				//Log.v("LOG", "GPS listener gets called back!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
				stopAllUpdate();
			}
		}

		public void onProviderDisabled(String provider) {
		}

		public void onProviderEnabled(String provider) {
		}

		public void onStatusChanged(String provider, int status, Bundle extras){
		}
	}

}

/**
 * 
 * 
 * 
From GPS_old.java

package eecs.umich.threegtest;
import java.util.List;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

class GPS_old {
	private static LocationManager lm = null;
    private static LocationListener locationListener;
    public static Location location;
    public static double latitude, longitude; 

    // location

    public static void location(Context context) {
        lm = ( LocationManager ) context.getSystemService( Context.LOCATION_SERVICE );
        GPS_old gps = new GPS_old();
        locationListener = gps.new MyLocationListener();
        List<String> list = lm.getAllProviders();
        String provider = null;
        String provider2 = null;

        for ( int i = 0;i < list.size();i++ ) {

            if ( lm.isProviderEnabled( list.get( i ) ) ) {
                if ( provider == null )
                    provider = list.get( i );
                else
                    provider2 = list.get( i );

                if ( provider != null && provider2 != null )
                    break;
            }
        }

        location = null;

        if ( provider != null )
            lm.requestLocationUpdates( provider, 100, 0, locationListener );
        else if ( provider2 != null )
            lm.requestLocationUpdates( provider2, 100, 0, locationListener );


    }

    private class MyLocationListener implements LocationListener {
        public void onLocationChanged( Location loc ) {
            // Called when the location has changed.

            if ( loc != null ) {
                location = loc;
                latitude = loc.getLatitude();
                longitude = loc.getLongitude();
                lm.removeUpdates( locationListener );
            }
        }

        public void onProviderDisabled( String provider ) {
            // TODO Auto-generated method stub

        }

        public void onProviderEnabled( String provider ) {
            // TODO Auto-generated method stub

        }

        public void onStatusChanged( String provider, int status, Bundle extras ) {
            // TODO Auto-generated method stub

        }
    }


}
 */
