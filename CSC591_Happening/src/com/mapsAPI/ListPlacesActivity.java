package com.mapsAPI;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class ListPlacesActivity extends ListActivity {
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ArrayList<Map<String, String>> list = buildData();
		String[] from = { "name", "purpose" };
		int[] to = { R.id.PlaceName, R.id.PlaceCount };

		SimpleAdapter adapter = new SimpleAdapter(this, list,
				R.layout.list_item, from, to);
		setListAdapter(adapter);
	}

	private ArrayList<Map<String, String>> buildData() {
		ArrayList<Map<String, String>> list = new ArrayList<Map<String, String>>();
		list.add(putData("AndroidAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAa", "Mobile"));
		list.add(putData("Windows7", "Windows7"));
		list.add(putData("iPhone", "iPhone"));
		return list;
	}

	private HashMap<String, String> putData(String name, String purpose) {
		HashMap<String, String> item = new HashMap<String, String>();
		item.put("name", name);
		item.put("purpose", purpose);
		return item;
	}
}
