/*
 * Conor Killeen
 * 5/4/2014
 * 
 * This class is used to parse the JSON array of suggested items.
 */
package com.conor.calapp;

import java.util.ArrayList;
import java.util.HashMap;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;

public class PopulateSuggestion extends AsyncTask<String , Integer, ArrayList<HashMap<String,String>>> {
	
	private static final String TAG_CODE = "upc_code";
	private static final String TAG_NAME = "name";
	private static final String TAG_COUNT = "count";
	private static final String TAG_IMAGE = "image";
	
	private String json;
	@SuppressWarnings("unused")
	private Context context;
	private JSONArray jAr;
	private HashMap<String,String> hash;
	private ArrayList<HashMap<String,String>> list ;
	
	public PopulateSuggestion(String json, Context context){
		
		this.json = json;
		this.context = context;
	}
	
	protected ArrayList<HashMap<String,String>> doInBackground(String... mode) {
		
		list = new ArrayList<HashMap<String,String>>();
		
		try {
			
			jAr = new JSONArray(json);
				
			for(int i = 0; i < jAr.length(); i++){
					
				JSONObject jOb = jAr.getJSONObject(i);
				
				hash = new HashMap<String,String>();
					
				hash.put(TAG_CODE, jOb.getString(TAG_CODE));
				hash.put(TAG_NAME, jOb.getString(TAG_NAME));
				hash.put(TAG_COUNT, jOb.getString(TAG_COUNT));
				hash.put(TAG_IMAGE, jOb.getString(TAG_IMAGE));
				list.add(hash);
			}
			
		} catch (JSONException e) {
			
			e.printStackTrace();
		}
		
		return list;
	}
}
