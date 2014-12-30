package com.mapsAPI;
//import com.mapsAPI.PlaceRequest;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.widget.ImageView;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.mapsAPI.R;
import android.view.View;

public class HappenningRaleighActivity extends MapActivity {

	//private static final OnClickListener l = null;
	LocationManager locationManager;

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

	@Override
	public void onResume() { 
		super.onResume(); 

		Log.e("TRACK", "On Resume");



		if (isOnline()) {
			if (Mobile.locationlistener == null) {
				Mobile.locationProvider = false;
				Mobile.locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);		
				Mobile.locationListner();
				if (!Mobile.locationProvider) {
					final CharSequence[] items = {"Go to Settings", "Continue"};

					AlertDialog.Builder builder = new AlertDialog.Builder(Mobile.map.context);
					builder.setTitle("No Location Information.");
					//builder.setMessage("No Location Information.");
					builder.setItems(items, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int item) {
							if (item == 0) {
								startActivityForResult(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS), 0);
								
							}

						}
					});

					AlertDialog alert = builder.create();		
					
					Log.e("MAIN", "No internet.");
					alert.show();
				}


			}
		}

		AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE); 
		Intent i = new Intent(this, BackgroudService.class); 
		PendingIntent pi = PendingIntent.getService(this, 0, i, 0); 
		am.cancel(pi); 
		// by my own convention, minutes <= 0 means notifications are disabled 
		Log.e("SERVICE", "Starting the service");

		am.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + 5*60*1000, 5*60*1000, pi);		
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

	@Override
	public void onCreate(Bundle icicle) {

		super.onCreate(icicle);
		setContentView(R.layout.main);
		Log.e("TRACK", "On Create");

		if (isOnline()) {

			Log.e("MAIN", "Loading the App.");


			Places.center = null;
			Mobile.location = null;
			Mobile.locationProvider = false;

			final ProgressDialog progDialog ;

			Mobile.map = new Maps ((MapView) findViewById(R.id.mapview), this);		
			Mobile.map.initializeMap();
			Mobile.ID = android.provider.Settings.Secure.getString(getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);

			progDialog = ProgressDialog.show(this, "Happening", "Loading Maps..");

			Mobile.markLocations();
			new Thread() {
				public void run() {
					try{
						
						
					} catch (Exception e) {
						Log.e("tag", e.getMessage());
					}
					progDialog.dismiss();
				}
			}.start();
			
		

			

			ImageView btnOpenNewActivity = (ImageView) findViewById(R.id.search); 
			btnOpenNewActivity .setOnClickListener(new View.OnClickListener() { 
				public void onClick(View v) { 

					Intent myIntent = new Intent(HappenningRaleighActivity.this, ListPlaces.class); 
					HappenningRaleighActivity.this.startActivity(myIntent); 

				} 

			}); 
		} else {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage("No Network. Exiting.")
			.setCancelable(false)
			.setTitle("Sorry")


			.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					HappenningRaleighActivity.this.finish();
				}
			}
					);
			AlertDialog alert = builder.create();		

			Log.e("MAIN", "No internet.");
			alert.show();
		}
	}

	public void onStop() {
		super.onStop();
		
		if (Mobile.locationProvider) {
			Mobile.cleanUp();
		}
		
		Log.e("TRACK", "On Stop");

	}

	public void onPause() {
		super.onPause();


		Log.e("TRACK", "On Pause");

	}


}

