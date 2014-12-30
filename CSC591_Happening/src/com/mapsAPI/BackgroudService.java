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

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;


public class BackgroudService extends Service {

	private WakeLock mWakeLock; 
	/**  Simply return null, since our Service will not be communicating with  any other components. 
	 * It just does its work silently. */ 


	@Override 
	public IBinder onBind(Intent intent) 
	{ return null; 
	}
	
	public boolean isOnline() {
		ConnectivityManager cm =
				(ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnectedOrConnecting()) {
			return true;
		}
		return false;
	}

	/** 
	 * This is where we initialize. We call this when onStart/onStartCommand is 
	 * called by the system. We won't do anything with the intent here, and you 
	 * probably won't, either. */

	private void handleIntent(Intent intent) 
	{ // obtain the wake lock 
		PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
		mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "ALARM"); 
		mWakeLock.acquire(); 
		// check the global background data setting 
		ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE); 

		Log.e("SERVICE", "Intent Called");
		if (!cm.getBackgroundDataSetting()) { 
			stopSelf();
			return; 
		} // do the actual work, in a separate thread 
		new PollTask().execute(); 
	}


	private class PollTask extends AsyncTask<Void, Void, Void> 
	{ 
		/** 
		 * This is where YOU do YOUR work. There's nothing for me to write here
		 * you have to fill this in. Make your HTTP request(s) or whatever it is 
		 * you have to do to get your updates in here, because this is run in a 
		 * separate thread */ 
		@Override 
		protected Void doInBackground(Void... params)
		{

			Log.e("SERVICE", "Callin background");
						
			if (!isOnline()) {
				return null;
			}

			LocationManager locationManager;
			locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

			Criteria criteria = new Criteria();
			criteria.setAccuracy(Criteria.ACCURACY_FINE);
			criteria.setAltitudeRequired(false);
			criteria.setBearingRequired(false);
			criteria.setCostAllowed(true);
			criteria.setPowerRequirement(Criteria.POWER_LOW);

			String provider = locationManager.getBestProvider(criteria, true);

			if (provider == null) {
				Log.e("SERVICE", "Provider is Null");
				return null;
			}

			Location location = locationManager.getLastKnownLocation(provider);

			if (location == null) {
				Log.e("SERVICE", "Location is Null");
				return null;
			}

			Log.e("SERVICE", "Position " +location.getLatitude() + " " + location.getLongitude());
			
			
			Log.e("SERVICE", "Mobile Location : " + Mobile.location.toString());
			
			Mobile.setLocationFromService(location);
			
			return null; 
			// do stuff! return null;
		}



		@Override
		protected void onPostExecute(Void result) { 
			// handle your data 
			stopSelf();
		}
	}

	@Override 
	public void onStart(Intent intent, int startId) 
	{ 
		handleIntent(intent); 
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId)
	{ 
		handleIntent(intent); 
		return START_NOT_STICKY;
	}

	public void onDestroy() { 
		super.onDestroy(); 
		Log.e("SERVICE", "Service is Destroyed :(");	    
		mWakeLock.release(); 
	}
}





