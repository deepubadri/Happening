package com.mapsAPI;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;
import com.google.android.maps.Projection;

import de.android1.overlaymanager.ManagedOverlay;
import de.android1.overlaymanager.ManagedOverlayGestureDetector;
import de.android1.overlaymanager.ManagedOverlayItem;
import de.android1.overlaymanager.OverlayManager;
import de.android1.overlaymanager.ZoomEvent;


public class Maps extends MapActivity{


	String LOG = "MAPS";

	public static MapView mapView = null;
	Context context = null;


	Maps (MapView map, Context context) {
		this.mapView = map;
		this.context = context;
	}

	public void initializeMap() {


		if (mapView == null) {
			Log.e(LOG, "MapView is Null.");
			return;
		}

		MapController mapController = mapView.getController();
		if (mapController == null) {
			Log.e(LOG, "MapController is Null.");
			return;
		}


		// Zoom in
		mapController.setZoom(17);
		mapView.setBuiltInZoomControls(true);

		Log.e(LOG, "Map initialized successfully.");
	}


	public void populateMap(JSONArray positions) {

		try {

			List<Overlay> mapOverlays = mapView.getOverlays();
			mapOverlays.clear();
			
			Log.e(LOG, "Populating Map...");
			
			createOverlayWithListener();
			
			
			


			Drawable drawable1 = context.getResources().getDrawable(R.drawable.marker);
			OverlayMultiplePoints itemizedoverlay = new OverlayMultiplePoints(drawable1, context);

			Log.e(LOG, "Marking " + positions.length() + " Locations.");
			//String unique_id = android.provider.Settings.Secure.getString(getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);

			OverlayItem overlayitem = null;
			int size = positions.length();


			for (int i=0; i < size; i++) {
				JSONObject json_object = positions.getJSONObject(i);

				if (json_object.getString("latitude") == "null" || json_object.getString("longitude") == "null") {
					continue;
				}
				Double Lat = Double.parseDouble(json_object.getString("latitude"));
				Double Lon = Double.parseDouble(json_object.getString("longitude"));
				String mobile = json_object.getString("mobile");

				Log.e("DATA", "Unique ID " + Mobile.ID + " Mobile " + mobile);

				if (mobile.equals(Mobile.ID)) {	
					Log.e(LOG, "Not Marking My Mobile.");
					continue;
				}

				GeoPoint point = createGeopointFromLatLon(Lat, Lon);		

				overlayitem = new OverlayItem(point, "", "");
				itemizedoverlay.addOverlay(overlayitem);

			}

			if (size > 0) {
				mapOverlays.add(itemizedoverlay);
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			Log.e("PULL", "JSON Exception");
			e.printStackTrace();
		}			
	}
	
	
	public void createOverlayWithListener() {
        //This time we use our own marker
		Log.e(LOG, "Map Creating listener..");
		final MapController mapController = mapView.getController();;
		OverlayManager overlayManager = new OverlayManager(context, mapView);;
		
		
		ManagedOverlay managedOverlay = overlayManager.createOverlay("listenerOverlay", context.getResources().getDrawable(R.drawable.marker));
	
		
		Toast.makeText(context, "Map Created", Toast.LENGTH_SHORT).show();
		Log.e(LOG, "Map Created..");
		managedOverlay.setOnOverlayGestureListener(new ManagedOverlayGestureDetector.OnOverlayGestureListener() {
			@Override
			public boolean onZoom(ZoomEvent zoom, ManagedOverlay overlay) {
				Toast.makeText(context, "Zoom yeah!", Toast.LENGTH_SHORT).show();
				return false;
			}

			@Override
			public boolean onDoubleTap(MotionEvent e, ManagedOverlay overlay, GeoPoint point, ManagedOverlayItem item) {
				
				if (point != null) {
					mapController.animateTo(point);
				} else {
					Log.e("LOG", "Double Tap point is NULL");
				}
				//mapController.zoomIn();
				Intent myIntent = new Intent(context, ListPlaces.class); 
				context.startActivity(myIntent); 
				return true;
			}

			@Override
			public void onLongPress(MotionEvent e, ManagedOverlay overlay) {
				Toast.makeText(context, "LongPress incoming...!", Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onLongPressFinished(MotionEvent e, ManagedOverlay overlay, GeoPoint point, ManagedOverlayItem item) {
				if (item != null)
					Toast.makeText(context, "We smushed " + item.getTitle() + " ! excellent...", Toast.LENGTH_LONG).show();
				
				if (point != null) {
					Log.e("LOG", "Long Press Pint : " + point.toString());
					
				} else {
					Log.e("LOG", "Long Press point is NULL");
				}
			}

			@Override
			public boolean onScrolled(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY, ManagedOverlay overlay) {
				return false;
			}

			@Override
			public boolean onSingleTap(MotionEvent e, ManagedOverlay overlay, GeoPoint point, ManagedOverlayItem item) {
				return false;
			}
		});
		overlayManager.populate();
	}

	private GeoPoint createGeopointFromLocation(Location location) {
		Double latitude = location.getLatitude() * 1E6;
		Double longitude = location.getLongitude() * 1E6;
		GeoPoint point = new GeoPoint(latitude.intValue(), longitude.intValue());

		return point;
	}

	private GeoPoint createGeopointFromLatLon(Double Lat, Double Lon) {
		Double latitude = Lat * 1E6;
		Double longitude = Lon * 1E6;
		GeoPoint point = new GeoPoint(latitude.intValue(), longitude.intValue());

		return point;
	}

	public int addLocation (Location location) {

		List<Overlay> mapOverlays = mapView.getOverlays();		
		GeoPoint point = createGeopointFromLocation(location);		
		Drawable drawable = context.getResources().getDrawable(R.drawable.push_pin);
		OverlayMultiplePoints overlay = new OverlayMultiplePoints(drawable, context);		
		OverlayItem overlayitem = new OverlayItem(point, "", "");		
		overlay.addOverlay(overlayitem);	
		Log.e(LOG, "Adding Overlay : " + mapOverlays.size());
		mapOverlays.add(overlay);
		MapController mapController = mapView.getController();
		mapController.animateTo(point);		

		return mapOverlays.size() - 1;
	}

	public int addCenter(Location location) {

		List<Overlay> mapOverlays = mapView.getOverlays();		
		GeoPoint point = createGeopointFromLocation(location);

		Point ptPixels = new Point();
		Projection projection = mapView.getProjection();
		projection.toPixels(point, ptPixels);
		ptPixels.y += 121;
		point = projection.fromPixels(ptPixels.x, ptPixels.y);

		Drawable drawable = context.getResources().getDrawable(R.drawable.happenning);
		OverlayMultiplePoints overlay = new OverlayMultiplePoints(drawable, context);		
		OverlayItem overlayitem = new OverlayItem(point, "", "");		
		overlay.addOverlay(overlayitem);	
		Log.e(LOG, "Adding Overlay : " + mapOverlays.size());
		mapOverlays.add(overlay);
		MapController mapController = mapView.getController();
		mapController.animateTo(point);		

		return mapOverlays.size() - 1;
	}


	public static float distance(Location from, Location to) {
		
		Double lat1 = from.getLatitude(); 
		Double lng1 = from.getLongitude(); 
		Double lat2 = to.getLatitude(); 
		Double lng2 = to.getLongitude();
		
		double earthRadius = 3958.75;
		double dLat = Math.toRadians(lat2-lat1);
		double dLng = Math.toRadians(lng2-lng1);
		double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
				Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
				Math.sin(dLng/2) * Math.sin(dLng/2);
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
		double dist = earthRadius * c;

		int meterConversion = 1609;

		return new Float(dist * meterConversion).floatValue();
	}


	public int updateIndexOnDelete(int deleted, int update) {		
		if (deleted < update) {
			return update - 1;
		} else {
			return update;
		}		
	}

	public void removeLocation (int index) {
		List<Overlay> mapOverlays = mapView.getOverlays();

		mapOverlays.remove(index);

		Log.e(LOG, "Removing Overlay.");
	}


	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}

}
