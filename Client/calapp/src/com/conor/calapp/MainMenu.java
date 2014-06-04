/*
 * Conor Killeen
 * 5/4/2014
 * 
 * This class is the activity responsible for displaying the applications main menu.
 */
package com.conor.calapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.conor.calapp.R;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class MainMenu extends ActionBarActivity implements OnClickListener{
	
	private static final String TAG_SESSIONID = "user_id";
	
	private LinearLayout login,register,ll1,ll2,ll3,ll4,logout,addfood,viewfood,suggestions;
	private SharedPreferences pref;
	
	public void onCreate(Bundle savedInstanceState) {
    	
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.mainmenu);
        
        login = (LinearLayout)findViewById(R.id.login);
        register = (LinearLayout)findViewById(R.id.register);
        logout = (LinearLayout)findViewById(R.id.logout);
        addfood = (LinearLayout)findViewById(R.id.addfood);
        viewfood = (LinearLayout)findViewById(R.id.viewfood);
        suggestions = (LinearLayout)findViewById(R.id.suggestions);
        
        ll1 = (LinearLayout)findViewById(R.id.ll1);
        ll2 = (LinearLayout)findViewById(R.id.ll2);
        ll3 = (LinearLayout)findViewById(R.id.ll3);
        ll4 = (LinearLayout)findViewById(R.id.ll4);
        
        login.setOnClickListener(this);
        viewfood.setOnClickListener(this);
        logout.setOnClickListener(this);
        addfood.setOnClickListener(this);
        suggestions.setOnClickListener(this);
        register.setOnClickListener(this);
        
        init();
    }
	
	public void init() {
		
		pref = this.getSharedPreferences(TAG_SESSIONID, 0);
		String username = pref.getString(TAG_SESSIONID, "FAIL");
		System.out.println("Username: " + username );
		
		//If user is not logged in, certain menu elements will be hidden
		if(username.equals("FAIL")){
			ll1.setVisibility(View.VISIBLE);
			ll2.setVisibility(View.GONE);
			ll3.setVisibility(View.GONE);
			ll4.setVisibility(View.VISIBLE);
        }else{
			ll1.setVisibility(View.GONE);
			ll2.setVisibility(View.VISIBLE);
			ll3.setVisibility(View.VISIBLE);
			ll4.setVisibility(View.GONE);
        }
	}
	
	public void logout() {
		
		pref = this.getSharedPreferences(TAG_SESSIONID, 0);
		pref.edit().remove(TAG_SESSIONID).clear().commit();
		
		init();
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {

		if (requestCode == 1) {

			if(resultCode == RESULT_OK){  
		    	 init();
			}else if (resultCode == RESULT_CANCELED) {
		    
		    }
		}else if (requestCode == 0x0000c0de) {
			
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
		
		pref = this.getSharedPreferences(TAG_SESSIONID, 0);
		String username = pref.getString(TAG_SESSIONID, "FAIL");
		
		new Item(this).addItem(username, code);
		
		System.out.println("username = " + username);
		System.out.println("code = " + code);
		
	}
	
	public void onClick(View v) {
		if(v.getId()==R.id.login){
    		
    		Intent intent = new Intent(this, Login.class);
    		startActivityForResult(intent, 1);
			
		}else if(v.getId()==R.id.register){
    		
    		Intent intent = new Intent(this, Register.class);
    		startActivityForResult(intent, 1);
			
		}else if(v.getId()==R.id.viewfood){
			
			Intent intent = new Intent(this, Food.class);
    		
    		startActivity(intent);
		}else if(v.getId()==R.id.suggestions){
			
			Intent intent = new Intent(this, Suggestion.class);
    		
    		startActivity(intent);
		}else if(v.getId()==R.id.logout){
			
			logout();
		}else if(v.getId()==R.id.addfood){
			
			IntentIntegrator scanIntegrator = new IntentIntegrator(this);
			scanIntegrator.initiateScan();
		}
	}
}
