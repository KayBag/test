/*
 * Conor Killeen
 * 5/4/2014
 * 
 * This class is the activity responsible for displaying the users food list.
 */
package com.conor.calapp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import org.json.JSONException;
import org.json.JSONObject;

import com.conor.calapp.R;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import android.app.Dialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class Food extends ListActivity implements OnClickListener{
	
	private static final String TAG_SESSIONID = "user_id";
	private static final String TAG_CODE = "upc_code";
	private static final String TAG_NAME = "name";
	private static final String TAG_RATING = "rating";
	private static final String TAG_IMAGE = "image";
	private static final String TAG_ENERGY = "energy";
	private static final String TAG_NUTRITION = "nutrition";
	private static final String TAG_INGREDIENTS = "ingredients";
	
	private SharedPreferences pref;
	private ArrayList<HashMap<String,String>> list;
	private String username;
	private Context context;
	@SuppressWarnings("unused")
	private String rating;
	private ImageButton back, add;
	
	public void onCreate(Bundle savedInstanceState) {
    	
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.food);
        
        pref = this.getSharedPreferences(TAG_SESSIONID, 0);
		username = pref.getString(TAG_SESSIONID, "FAIL");
		
		context = this;
        
		back = (ImageButton)findViewById(R.id.bBack);
		back.setOnClickListener(this);
		add = (ImageButton)findViewById(R.id.bAdd);
		add.setOnClickListener(this);
		
        init();
    }

	private void init(){
		
		System.out.println("get_data(" + username + ")");
		
		new Item(this,this).getUserItems(username);
		
	}
	
	public void getData(String json){
		
		try {
			
			list = new PopulateFood(json, this).execute().get();
			
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		setListAdapter(new FoodListAdapter(list));
	}
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
	    
		final Dialog dialog = new Dialog(context);
		dialog.setContentView(R.layout.dialog);
		dialog.setTitle("Food Information");
		
		TextView text1 = (TextView) dialog.findViewById(R.id.textView1);
		TextView text2 = (TextView) dialog.findViewById(R.id.textView2);
		text1.setText("Nutrition: " + list.get(position).get(TAG_NUTRITION));
		text2.setText("Ingredients: " + list.get(position).get(TAG_INGREDIENTS));

		dialog.show();
	}
	
	//Custom Adaptor to allow use of images and and buttons within a list item
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
		    V = vi.inflate(R.layout.food_row_view, null);
				
			//}		
			
			new DownloadImageTask((ImageView) V.findViewById(R.id.imageView1)).execute(map.get(TAG_IMAGE));
			
			TextView name = (TextView) V.findViewById(R.id.tv1);
		    TextView code = (TextView) V.findViewById(R.id.tv2);
		    ImageButton like = (ImageButton) V.findViewById(R.id.button1);
		    ImageButton delete = (ImageButton) V.findViewById(R.id.button2);
		    
		    like.setTag(map.get(TAG_CODE));
		    delete.setTag(map.get(TAG_CODE));
		    System.out.println(map.get(TAG_IMAGE));
		    
		    if(map.get(TAG_RATING).equals("1")){
		    	like.setImageResource(R.drawable.like);
		    }else{
		    	like.setImageResource(R.drawable.unlike);
		    }
		    
		    name.setText(map.get(TAG_NAME));
		    
		    if(!map.get(TAG_ENERGY).equals(null) && !map.get(TAG_ENERGY).equals("0")){
		    	
		    	String[] state = map.get(TAG_NUTRITION).split("-");
		    	
		    	if(state[0].contains("100g"))
		    		code.setText(map.get(TAG_ENERGY) + "Kj per 100g");
		    	if(state[0].contains("100ml"))
		    		code.setText(map.get(TAG_ENERGY) + "Kj per 100ml");
		    	
		    }else
		    	code.setText("Energy data unavailible");
		    
		    like.setOnClickListener(this);
		    delete.setOnClickListener(this);
		    
			return V;
		}
		
		@Override
		public void onClick(View v) {
			
			String code = v.getTag().toString();
			
			if(v.getId()==R.id.button1){
				
				System.out.println("Button tag = " + code);
				
				rating = new Item(context).rate(username, code);
				
				init();
			}else if(v.getId()==R.id.button2){
				
				System.out.println("Button tag = " + code);
				
				rating = new Item(context).delete(username, code);
				
				init();
			}
		}
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {

		if (requestCode == 0x0000c0de) {
			
			IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
			
	    	if (scanningResult != null) {
	    		String scanContent = scanningResult.getContents();
	    		System.out.println("Scan data recieved = " + scanContent);
	    		
	    		addFood(scanContent);
	    	}else{
	    	    Toast toast = Toast.makeText(getApplicationContext(),"No scan data received!", Toast.LENGTH_SHORT);
	    	    toast.show();
	    	}
		}
	}
	
	public void addFood(String code) {
		
		JSONObject jOb;
		String res = null;
		pref = this.getSharedPreferences(TAG_SESSIONID, 0);
		String username = pref.getString(TAG_SESSIONID, "FAIL");
		String result = new Item(this).addItem(username, code);
		
		try {
			jOb = new JSONObject(result);
			res = jOb.getString("result");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(res.equals("false")){
    	    Toast toast = Toast.makeText(getApplicationContext(),"Error scaning item / Item not found", Toast.LENGTH_SHORT);
    	    toast.show();
    	}
		
		System.out.println("username = " + username);
		System.out.println("code = " + code);
		
		init();
	}
	
	public void onClick(View v) {
		//Refresh Button
    	switch (v.getId()){
			case R.id.bBack:
			
				super.onBackPressed();
				
				break;
			case R.id.bAdd:
				
				IntentIntegrator scanIntegrator = new IntentIntegrator(this);
				scanIntegrator.initiateScan();
				
				break;
    	}
    }
}
