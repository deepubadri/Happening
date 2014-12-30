package com.mapsAPI;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.location.Location;
import android.util.Log;


public class PullFromServer {


	String URL = null;
	String LOG = "PULL SERVER";

	public PullFromServer(String URL) {		
		this.URL = URL;
	}

	public String pullUp() {
		HttpClient httpClient = new DefaultHttpClient();
		String JSONResponse = null;

		Log.e(LOG,"Pulling Data from : " + URL);

		try {
			HttpGet method = new HttpGet( new URI(URL) );
			HttpResponse response = httpClient.execute(method);	
			JSONResponse = getResponse(response.getEntity());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 	

		return JSONResponse;
	}


	private String getResponse( HttpEntity entity )
	{
		String response = "";

		try {
			int length = ( int ) entity.getContentLength();
			StringBuffer sb = new StringBuffer( length );
			InputStreamReader isr = new InputStreamReader( entity.getContent(), "UTF-8" );
			char buff[] = new char[length];
			int cnt;
			while ( ( cnt = isr.read( buff, 0, length - 1 ) ) > 0 ) {				
				sb.append( buff, 0, cnt );
			}

			response = sb.toString();
			isr.close();
		} catch ( IOException ioe ) {
			ioe.printStackTrace();
		}

		return response;
	}	


	public String pullPlacesCount(PlacesList places) {
		JSONObject eventObj = new JSONObject();
		JSONObject holder = new JSONObject();

		try {	

			if (places.results.size() == 0) {
				eventObj.put("data", "");
			} else {

				StringBuilder rests = new StringBuilder();
				for (int i=0; i < places.results.size(); i++) {					
					rests.append(places.results.get(i).id + ",");		
				}
				eventObj.put("data", rests.toString());
			}


			holder.put("place", eventObj);
			Log.e(LOG, "JSON = "+ holder.toString());
		} catch (JSONException js) {
			js.printStackTrace();
		}

		return sendDownPost(holder);

	}

	private String sendDownPost(JSONObject data) {

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

		String JSONresponse = "";

		HttpEntity entity = response.getEntity();

		if (entity != null) {
			try {
				JSONresponse = getResponse(entity);
			} catch (Exception e) {
				Log.e("IO E",""+e);
				e.printStackTrace();
			}
		}

		Log.e("CREATE", "Success");

		return JSONresponse;

	}


}
