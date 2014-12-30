package com.mapsAPI;

import com.google.api.client.util.Key;
public class Place {
	@Key
	public String id;
	
	@Key
	public String name;
	
	@Key
	public String reference;
	
	public int count = 0;

	@Override
	public String toString() {
		return name + " - " + id + " - " + reference;
	}
}
