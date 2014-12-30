package com.mapsAPI;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.location.Location;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;
import com.google.android.maps.Projection;

public class OverlayMultiplePoints extends ItemizedOverlay {

	private ArrayList<OverlayItem> mOverlays = new ArrayList<OverlayItem>();

	Context mContext;

	public void addOverlay(OverlayItem overlay) {
		mOverlays.add(overlay);
		populate();
	}

	public OverlayMultiplePoints(Drawable defaultMarker, Context context) {
		super(boundCenterBottom(defaultMarker));
		mContext = context;
	}


	@Override
	protected OverlayItem createItem(int i) {
		return mOverlays.get(i);
	}

	@Override
	public int size() {
		return mOverlays.size();
	}

	@Override
	public boolean onTap(GeoPoint point, MapView mapView) {
		Location center =  new Location("");
		
		double latitude = point.getLatitudeE6() / 1E6;
		double longitude = point.getLongitudeE6() / 1E6;

		center.setLatitude(latitude);
		center.setLongitude(longitude);
		
		Places.setCenter(center);
		return true;
	}
	
	

	
	@Override
	public void draw(Canvas canvas, MapView mapView, boolean shadow)
	{
		if(!shadow)
		{
			super.draw(canvas, mapView, false);
		}
	}
}




