/*
 * Conor Killeen
 * 5/4/2014
 * 
 * This class is the activity responsible for registering an account.
 */
package com.conor.calapp;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.conor.calapp.R;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Register extends Activity implements OnClickListener {
	
	private static final String TAG_SESSIONID = "user_id";
	
	private SharedPreferences pref;
	private Button register;
	private EditText username, password1, password2;
	private String sessionID = null;
	private String result;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register);
		
		register = (Button)findViewById(R.id.Register);
		
		username = (EditText)findViewById(R.id.username);
		password1 = (EditText)findViewById(R.id.password1);
		password2 = (EditText)findViewById(R.id.password2);
        
        register.setOnClickListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	public void myLogin(String username, String password){
    	
        System.out.println(username + " " + password);
        pref = this.getSharedPreferences(TAG_SESSIONID, 0);
        Editor editor = pref.edit();
        
    	if(!username.equals(null) || !password.equals(null)){
    		
    		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2); 
    		nameValuePairs.add(new BasicNameValuePair("action", "register"));     
			nameValuePairs.add(new BasicNameValuePair("username", username)); 
			nameValuePairs.add(new BasicNameValuePair("password", password));
			
            try {
            	 
            	sessionID = new myInfoSend(nameValuePairs,this.getString(R.string.url),this).execute().get();  
            	if(!sessionID.equals("FAIL")){
	            	JSONObject jOb = new JSONObject(sessionID);
	                System.out.println(sessionID);
	                sessionID = jOb.getString("register");
            	}
            }catch (InterruptedException e) {
				e.printStackTrace();
			}catch (ExecutionException e) {
				e.printStackTrace();
			}catch (JSONException e) {
				e.printStackTrace();
			} 
            System.out.println(sessionID + " " + result);
            if(!sessionID.equals("FAIL")){
            	
            	editor.putString(TAG_SESSIONID, sessionID);
            	editor.commit();
            	
            	Intent returnIntent = new Intent();
            	setResult(RESULT_OK,returnIntent);
            	this.finish();
            }else{
            	Toast toast = Toast.makeText(getApplicationContext(),"Registration failed", Toast.LENGTH_SHORT);
	    	    toast.show();
            }
		} 
    }

	@Override
	public void onClick(View v) {
		System.out.println(password1.getText() + " " + password2.getText());
		if(password1.getText().toString().equals(password2.getText().toString())){
			if(v.getId()==R.id.Register){
				myLogin(username.getText().toString(), password1.getText().toString());
				System.out.println("Button pushed");
			}
		}else{
			Toast toast = Toast.makeText(getApplicationContext(),"Both passwords must be the same", Toast.LENGTH_SHORT);
    	    toast.show();
		}
	}
}
