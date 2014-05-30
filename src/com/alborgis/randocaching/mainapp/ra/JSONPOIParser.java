package com.alborgis.randocaching.mainapp.ra;
import java.util.ArrayList;
import java.util.HashMap;
import org.json.JSONArray;
import org.json.JSONObject;

import com.alborgis.ting.base.model.Poi;

import android.app.Application;
import android.util.Log;

public class JSONPOIParser {

	// ensure these attributes are also used in JavaScript when extracting POI data
	public static final String ATTR_ID = "id";
	public static final String ATTR_NAME = "name";
	public static final String ATTR_DESCRIPTION = "description";
	public static final String ATTR_LATITUDE = "latitude";
	public static final String ATTR_LONGITUDE = "longitude";
	public static final String ATTR_ALTITUDE = "altitude";
	public static final String ATTR_ICON = "icon";
	
	
	public static JSONArray parseToJSONArray(Application app, ArrayList<Poi> arrayListPois) {

		JSONArray poisJSONArray = new JSONArray();
		
		if(arrayListPois != null){
			for (Poi poi : arrayListPois) {
				HashMap<String, String> poiInformation = new HashMap<String, String>();
				poiInformation.put(ATTR_ID, poi.nid);
				poiInformation.put(ATTR_NAME, poi.title);
				poiInformation.put(ATTR_DESCRIPTION, "");
				
				double lat = poi.coordinates.latitude;
				double lon = poi.coordinates.longitude;
				double alt = poi.coordinates.altitude;

				poiInformation.put(ATTR_LATITUDE, String.valueOf(lat));
				poiInformation.put(ATTR_LONGITUDE, String.valueOf(lon));
				poiInformation.put(ATTR_ALTITUDE, String.valueOf(alt));
				
				//String icon = "https://cdn1.iconfinder.com/data/icons/Map-Markers-Icons-Demo-PNG/256/Map-Marker-Marker-Outside-Azure.png";
				String icon = "assets/ra_marker.png";
				
				poiInformation.put(ATTR_ICON, icon);
				
				poisJSONArray.put(new JSONObject(poiInformation));
			}

		}

		Log.d("Milog", "POIS Reales generados: " + poisJSONArray.toString());
		
		return poisJSONArray;
	}
}
