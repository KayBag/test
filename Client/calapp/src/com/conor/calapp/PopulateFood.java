/*
 * Conor Killeen
 * 5/4/2014
 * 
 * This class is used to parse the JSON array of user items.
 */
package com.conor.calapp;

import java.util.ArrayList;
import java.util.HashMap;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;

public class PopulateFood extends AsyncTask<String , Integer, ArrayList<HashMap<String,String>>> {
	
	private static final String TAG_CODE = "upc_code";
	private static final String TAG_NAME = "name";
	private static final String TAG_RATING = "rating";
	private static final String TAG_IMAGE = "image";
	private static final String TAG_ENERGY = "energy";
	private static final String TAG_NUTRITION = "nutrition";
	private static final String TAG_INGREDIENTS = "ingredients";
	
	private String json;
	@SuppressWarnings("unused")
	private Context context;
	private JSONArray jAr;
	private HashMap<String,String> hash;
	private ArrayList<HashMap<String,String>> list ;
	
	public PopulateFood(String json, Context context){
		
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
				hash.put(TAG_RATING, jOb.getString(TAG_RATING));	
				hash.put(TAG_IMAGE, jOb.getString(TAG_IMAGE));
				hash.put(TAG_ENERGY, jOb.getString(TAG_ENERGY));
				hash.put(TAG_NUTRITION, jOb.getString(TAG_NUTRITION));
				hash.put(TAG_INGREDIENTS, jOb.getString(TAG_INGREDIENTS));
				list.add(hash);
			}
			
		} catch (JSONException e) {
			
			e.printStackTrace();
		}
		
		return list;
	}
}
