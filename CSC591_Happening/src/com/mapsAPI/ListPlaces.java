package com.mapsAPI;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import com.google.android.maps.MapActivity;
import android.widget.TableRow.LayoutParams;
import android.widget.TableLayout;
import android.widget.TableRow;

public class ListPlaces extends MapActivity{
	// this is the code to populate the list in the application


	String LOG = "LIST";

	ArrayList<String> places=new ArrayList<String>();
	ArrayList<String> counts=new ArrayList<String>();
	String place="";
	String count="";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.table_item);

		Log.e(LOG, "Creating the List Activity.");
		Places.populatePlaces();
		retreivePlaces();
	}

	public void createView(TableRow tr, TextView t, String viewdata) {
		t.setText(viewdata);
		//adjust the properties of the textView
		t.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
		t.setTextColor(Color.WHITE);
		
		t.setTextSize(20);

		t.setPadding(15, 20, 15, 20);

		tr.setPadding(0, 5, 0, 5);
		tr.setBackgroundColor(Color.parseColor("#6d0303"));
		tr.addView(t); // add TextView to row.
	}

	public void createView1(TableRow tr, TextView t, String viewdata) {
		t.setText(viewdata);
		//adjust the properties of the textView
		t.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
		t.setTextColor(Color.WHITE);
		t.setTextSize(20);

		t.setPadding(80, 0, 0, 0);
		//tr.setPadding(0, 1, 0, 1);
		//tr.setBackgroundColor(Color.RED);
		tr.addView(t); // add TextView to row.
	}
	
	
	private void retreivePlaces()
	{
		
		String data = new PullFromServer("http://simple-window-4171.herokuapp.com/places").pullPlacesCount(Places.places);
		
		Log.e(LOG, "Pull Response for places : " + data);

		
		JSONObject jPlace;
		JSONArray jPlaces = null;
		String JSON = "{'place':" + data + "}";
		
		try {
			jPlace = new JSONObject(JSON);
			jPlaces = jPlace.getJSONArray("place");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		
		Log.e(LOG,"Received " + jPlaces.length() + " Objects.");
		
		int rSize = jPlaces.length();
		
		int size = Places.places.results.size();
		int pointer = 0;
		
		for (int i=0 ; i < size; i++) {
			JSONObject json_object;
			try {
				//Log.e(LOG,"JSON Object :  " + jPlaces.getString(i));
				
				if (jPlaces.getString(i) == null) {
					Log.e(LOG,"Null ");
					continue;
					
				}
				
				
				json_object = jPlaces.getJSONObject(i);
				String reference = json_object.getString("reference");
				
				if (reference.equals(Places.places.results.get(i).id)) {
					Log.e(LOG,"Match " + reference);
					Places.places.results.get(i).count = json_object.getInt("count");
					pointer++;
				}
				
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
			
		}

		Log.e(LOG, size + " places to Display.");
		for (int i=0; i < size; i++) {

			place = Places.places.results.get(i).name;
			count = "" + Places.places.results.get(i).count;
			//String concatString= place+"     "+"    "+json_object.getString("count");
			places.add(place);
			counts.add(count);						

			Log.e("Places ", "Place " + place);
			TableLayout tl;
			tl = (TableLayout)findViewById(R.id.tableLayout1);

			//Log.e("TABLE", "Width : " + tl.getWidth());
			//Create a new row to be added.
			TableRow tr = new TableRow(this);
			//Create text views to be added to the row.

			tr.setMinimumHeight(40);
			TextView tv1 = new TextView(this);
			tv1.setMaxWidth(380);
			TextView tv2 = new TextView(this);

			//Put the data into the text view by passing it to a user defined function createView()
			createView(tr, tv1, places.get(i));
			createView1(tr, tv2, counts.get(i));
			//Add the new row to our tableLayout tl

			View v = new View(this);
			v.setBackgroundColor(Color.parseColor("#A00000"));
			v.setMinimumHeight(2);
			tl.addView(tr);
			tl.addView(v);
		}
	}

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}	
}
