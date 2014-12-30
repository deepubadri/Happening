package com.mapsAPI;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.location.Location;
import android.util.Log;

public class Places {
	
	public static Location center;
	public static PlacesList places;
	public static int mapIndex = -1;
	public static String LOG = "PLACES";

	public static Location getCenter() {
		return center;
	}

	public static void setCenter(Location center) {
		if (Places.center != null) {
			Mobile.map.removeLocation(Places.mapIndex);
			Mobile.mapIndex = Mobile.map.updateIndexOnDelete(Places.mapIndex, Mobile.mapIndex);
		} else {
			Log.e(LOG,"Adding Places center for the first time.");
		}
	
		Places.center = center;
		Places.mapIndex = Mobile.map.addCenter(Places.center);
		Log.e(LOG,"Places Center Added at : " + mapIndex);
	
	}

	public PlacesList getPlaces() {
		return places;
	}

	public void setPlaces(PlacesList places) {
		Places.places = places;
	}
	
	
	public static void populatePlaces() {
		Places.places = new PlaceRequest().performSearch(Places.center, 500);
	}
	
	public static JSONArray bringPlacesFromServer() {
		PullFromServer server = new PullFromServer("http://simple-window-4171.herokuapp.com/places?format=json");
		String response = server.pullUp();		
		String JSON = "{'place':" + response + "}";
		Log.e(LOG,"Received JSON : " + JSON);
		
		JSONObject place;
		JSONArray places = null;
		try {
			place = new JSONObject(JSON);
			places = place.getJSONArray("place");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		
		Log.e(LOG,"Received " + places.length() + " Objects.");
		return places;	
		
	}
	
	

}
