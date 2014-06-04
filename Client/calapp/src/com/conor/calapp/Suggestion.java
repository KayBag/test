/*
 * Conor Killeen
 * 5/4/2014
 * 
 * This class is the activity responsible for displaying the users suggestion list.
 */
package com.conor.calapp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import com.conor.calapp.R;

import android.app.ListActivity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class Suggestion extends ListActivity implements OnClickListener{

	private static final String TAG_SESSIONID = "user_id";
	private static final String TAG_CODE = "upc_code";
	private static final String TAG_NAME = "name";
	private static final String TAG_COUNT = "count";
	private static final String TAG_IMAGE = "image";
	
	private SharedPreferences pref;
	private ArrayList<HashMap<String,String>> list;
	@SuppressWarnings("unused")
	private SimpleAdapter adapter;
	private String username;
	@SuppressWarnings("unused")
	private String url;
	private Context context;
	@SuppressWarnings("unused")
	private String rating;
	private ImageButton back;
	
	public void onCreate(Bundle savedInstanceState) {
    	
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.suggestions);
        
        pref = this.getSharedPreferences(TAG_SESSIONID, 0);
		username = pref.getString(TAG_SESSIONID, "FAIL");
		
		url = this.getString(R.string.url);
		context = this;
		
		back = (ImageButton)findViewById(R.id.bBack);
		back.setOnClickListener(this);
		
        init();
    }

	private void init(){
		
		System.out.println("get_data(" + username + ")");
		
		new Item(this,this).getUserSuggestions(username);
	}
	
	public void getData(String json){
		
		try {
			
			list = new PopulateSuggestion(json, this).execute().get();
			
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		setListAdapter(new FoodListAdapter(list));
	}
	
	private class FoodListAdapter extends BaseAdapter implements OnClickListener{

		ArrayList<HashMap<String,String>> foodList;
		public FoodListAdapter(ArrayList<HashMap<String,String>> items) {
			
			foodList = items;
		    
		}
		
		@Override
		public int getCount() {
			
			return foodList.size();
		}

		@Override
		public Object getItem(int position) {
			
			return foodList.get(position);
		}

		@Override
		public long getItemId(int position) {
			
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			View V = convertView;
			HashMap<String,String> map = foodList.get(position);
			
			//if (convertView == null) {
				
			LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		       V = vi.inflate(R.layout.suggestions_row_view, null);
				
			//}		
			
		    
		    new DownloadImageTask((ImageView) V.findViewById(R.id.imageView1)).execute(map.get(TAG_IMAGE));
			  
			TextView name = (TextView) V.findViewById(R.id.tv1);
		    TextView code = (TextView) V.findViewById(R.id.tv2);
		    ImageButton add = (ImageButton) V.findViewById(R.id.button1);
		    add.setTag(map.get(TAG_CODE));
		    
		    name.setText(map.get(TAG_NAME));
		    code.setText("Suggestion score: " + map.get(TAG_COUNT));
		    
		    add.setOnClickListener(this);
		    
			return V;
		}

		@Override
		public void onClick(View v) {
			if(v.getId()==R.id.button1){
				
				System.out.println(v.getTag());
				
				rating = new Item(context).addItem(username, v.getTag().toString());
				
				init();
			}
		}
	}
	
	public void onClick(View v) {
		//Refresh Button
    	switch (v.getId()){
			case R.id.bBack:
			
				super.onBackPressed();
				
				break;
    	}
    }
}
