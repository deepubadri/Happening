package com.mapsAPI;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.maps.MapActivity;

import android.content.Context;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class PushToServer extends MapActivity{

	String URL = "http://simple-window-4171.herokuapp.com/positions";
	String LOG = "PUSHSERVER";

	public boolean isOnline() {
		ConnectivityManager cm =
				(ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnectedOrConnecting()) {
			return true;
		}
		return false;
	}


	public void pushLocationAndPlaces(Location location, PlacesList places) {

		

		JSONObject eventObj = new JSONObject();
		JSONObject holder = new JSONObject();

		try {	

			eventObj.put("mobile", Mobile.ID);

			eventObj.put("altitude", location.getAltitude());
			eventObj.put("latitude", location.getLatitude());
			eventObj.put("longitude", location.getLongitude());


			if (places.results.size() == 0) {
				eventObj.put("data", "");
			} else {

				StringBuilder rests = new StringBuilder();
				for (int i=0; i < places.results.size(); i++) {					
					rests.append(places.results.get(i).id + ":" + places.results.get(i).name +",");		
				}
				eventObj.put("data", rests.toString());
			}


			holder.put("position", eventObj);

			Log.e(LOG, "JSON = "+ holder.toString());
			
			sendDown(holder);
			
		} catch (JSONException js) {
			js.printStackTrace();
		}

	}

	private void sendDown(JSONObject data) {

		DefaultHttpClient client = new DefaultHttpClient();

		HttpPost post = new HttpPost(URL);
		
		StringEntity se = null;
		try {
			se = new StringEntity(data.toString());
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		post.setEntity(se);
		post.setHeader("Content-Type","application/json");

		HttpResponse response = null;

		try {
			response = client.execute(post);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			Log.e("ClientProtocol",""+e);
		} catch (IOException e) {
			e.printStackTrace();
			Log.e("IO",""+e);
		}

		HttpEntity entity = response.getEntity();

		if (entity != null) {
			try {
				entity.consumeContent();
			} catch (IOException e) {
				Log.e("IO E",""+e);
				e.printStackTrace();
			}
		}

		Log.e("CREATE", "Success"); 

	}

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}




}
