package com.mapsAPI;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.maps.GeoPoint;
import com.mapsAPI.Maps;
import com.mapsAPI.PullFromServer;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;


public class Mobile {

	public static Location location = null;	
	public static Maps map;	
	public static int mapIndex = -1;
	public static String LOG = "MOBILE";
	public static String ID = "";
	public static LocationManager locationManager;
	public static LocationListener locationlistener;
	public static boolean networkConnection;
	public static boolean locationProvider;



	public static Location getLocation() {
		return location;
	}

	public static void setLocation(Location location) {
		if (Mobile.location != null) {
			map.removeLocation(mapIndex);
			Places.mapIndex = map.updateIndexOnDelete(Mobile.mapIndex, Places.mapIndex); 
			float distance = Maps.distance(Mobile.location, location);
			Log.e(LOG, "New Mobile location Distance : " + distance);			
			if (distance > 10) {
				sendData(location);
			}
		} else {
			sendData(location);
			Log.e(LOG,"Adding Mobile Location for the first time.");
		}



		Mobile.location = location;
		mapIndex = map.addLocation(Mobile.location);

		Log.e(LOG,"Mobile Location Added at : " + mapIndex );
		//Places.setCenter(Mobile.location);
	}

	public static void setLocationFromService(Location location) {
		if (Mobile.location != null) {
			float distance = Maps.distance(Mobile.location, location);
			Log.e(LOG, "New Mobile location Distance : " + distance);			
			if (distance > 10) {
				sendData(location);
			}
		} else {
			sendData(location);
			Log.e(LOG,"Adding Mobile Location for the first time.");
		}
		Mobile.location = location;
		Log.e(LOG,"Mobile Location Added at : " + mapIndex );
		//Places.setCenter(Mobile.location);
	}



	private static void sendData(Location location) {		
		PlacesList places = PlaceRequest.performSearch(location, 20);		
		new PushToServer().pushLocationAndPlaces(location, places);

	}

	private static JSONArray bringLocationsFromServer() {
		PullFromServer server = new PullFromServer("http://simple-window-4171.herokuapp.com/positions?format=json");
		String response = server.pullUp();		
		String JSON = "{'position':" + response + "}";
		Log.e(LOG,"Received JSON : " + JSON);

		JSONObject position;
		JSONArray positions = null;
		try {
			position = new JSONObject(JSON);
			positions = position.getJSONArray("position");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		

		Log.e(LOG,"Received " + positions.length() + " Objects.");
		return positions;		
	}

	public static void markLocations() {
		JSONArray positions = bringLocationsFromServer();		
		map.populateMap(positions);		
	}

	public static void locationListner() {
		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		criteria.setAltitudeRequired(false);
		criteria.setBearingRequired(false);
		criteria.setCostAllowed(true);
		criteria.setPowerRequirement(Criteria.POWER_LOW);

		String provider = locationManager.getBestProvider(criteria, true);

		if (provider != null) {
			Log.e(LOG,"Mobile Provider : " + provider);
			Mobile.locationProvider = true;
			Location location = locationManager.getLastKnownLocation(provider);

			// Define a listener that responds to location updates
			Mobile.locationlistener = new LocationListener() {
				public void onLocationChanged(Location location) {
					Log.e(LOG,"Location Update + ." + location.toString());
					Mobile.setLocation(location);
				}				
				public void onStatusChanged(String provider, int status, Bundle extras) {}
				public void onProviderEnabled(String provider) {}
				public void onProviderDisabled(String provider) {}
			}; 

			// Register the listener with the Location Manager to receive location updates
			locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationlistener);
			locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationlistener);

			if (location != null) {				
				Mobile.setLocation(location);		
				Log.e(LOG,"Mobile Location Changed + ." + location.toString());
			}
		} else {
			Mobile.locationProvider = false;
			

			Log.e(LOG,"Mobile Provider is Null.");
		}
	}

	public static void cleanUp() {
		Mobile.locationManager.removeUpdates(Mobile.locationlistener);
		Mobile.locationlistener = null;
		//Mobile.location = null;		
		//Places.center  = null;
	}

}
