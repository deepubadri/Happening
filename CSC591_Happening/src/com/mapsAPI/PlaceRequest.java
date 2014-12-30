package com.mapsAPI;

import android.util.Log;

import com.google.android.maps.MapActivity;
import com.google.api.client.googleapis.GoogleHeaders;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.apache.ApacheHttpTransport;
import com.google.api.client.http.json.JsonHttpParser;
import com.google.api.client.json.jackson.JacksonFactory;

import android.location.Location;

public class PlaceRequest extends MapActivity {

	
	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}


	// Create our transport.
	private static final HttpTransport transport = new ApacheHttpTransport();

	// Fill in the API key you want to use.
	private static final String API_KEY = "AIzaSyA4-aDOvaK-5hEv01A1aPK2arQEcgqBCoE";
	
//	private static final String API_KEY = "AIzaSyDDWEzSKrhSsBQ1y2I5Q6xWCPk8EIPXQgU";
	private static final String LOG_KEY = "PLACES";
	private static final String PLACES_SEARCH_URL =  "https://maps.googleapis.com/maps/api/place/search/json?";

	public static PlacesList performSearch(Location location, int radius) {
		
		if (location == null) {
			
			Log.e(LOG_KEY, "Location is null.");
		}
		try {

			Double latitude=location.getLatitude();
			Double longitude=location.getLongitude();

			Log.e("Search", "Position" +latitude + " " + longitude);	
			Log.e(LOG_KEY, "Start Search");

			GenericUrl reqUrl = new GenericUrl(PLACES_SEARCH_URL);
			reqUrl.put("key", API_KEY);
			reqUrl.put("location",latitude + "," + longitude);
			reqUrl.put("radius", radius);
			reqUrl.put("types", "restaurant");
			reqUrl.put("sensor", "true");

			Log.e(LOG_KEY, "url= " + reqUrl);

			HttpRequestFactory httpRequestFactory = createRequestFactory(transport);
			HttpRequest request = httpRequestFactory.buildGetRequest(reqUrl);
								
			PlacesList places = request.execute().parseAs(PlacesList.class);
			
			Log.e(LOG_KEY, "STATUS = " + places.status);
			
			return places;

		} catch (Exception e) {
			// TODO: handle exception
			return null;
		}
	}


	public static HttpRequestFactory createRequestFactory(final HttpTransport transport) {
		return transport.createRequestFactory(new HttpRequestInitializer() {
			public void initialize(HttpRequest request) {
				GoogleHeaders headers = new GoogleHeaders();
				headers.setApplicationName("Google-Places-DemoApp");
				request.setHeaders(headers);
				JsonHttpParser parser = new JsonHttpParser(new JacksonFactory()) ;
				request.addParser(parser);
			}
		});
	}
}