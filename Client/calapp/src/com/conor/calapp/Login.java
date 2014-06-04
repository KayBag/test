/*
 * Conor Killeen
 * 5/4/2014
 * 
 * This class is the activity responsible for logging into their account.
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
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Login extends Activity implements OnClickListener {
	
	private static final String TAG_SESSIONID = "user_id";
	
	private SharedPreferences pref;
	private Button login;
	private EditText username, password;
	private String sessionID = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		
		login = (Button)findViewById(R.id.login);
		
		username = (EditText)findViewById(R.id.username);
        password = (EditText)findViewById(R.id.password);
        
        login.setOnClickListener(this);
	}
	
	public void myLogin(String username, String password){
    	
        System.out.println(username + " " + password);
        pref = this.getSharedPreferences(TAG_SESSIONID, 0);
        Editor editor = pref.edit();
        
    	if(!username.equals(null) || !password.equals(null)){
    		
    		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2); 
    		nameValuePairs.add(new BasicNameValuePair("action", "login"));     
			nameValuePairs.add(new BasicNameValuePair("username", username)); 
			nameValuePairs.add(new BasicNameValuePair("password", password));
			
            try {
            	 
            	sessionID = new myInfoSend(nameValuePairs,this.getString(R.string.url),this).execute().get();  
                System.out.println(sessionID);
                if(!sessionID.equals("FAIL")){
	                JSONObject jOb = new JSONObject(sessionID);
	                sessionID = jOb.getString("login");
                }
                
            }catch (InterruptedException e) {
				e.printStackTrace();
			}catch (ExecutionException e) {
				e.printStackTrace();
			}catch (JSONException e) {
				e.printStackTrace();
			} 
            
            if(!sessionID.equals("FAIL") && !sessionID.equals("")){
            	
            	editor.putString(TAG_SESSIONID, sessionID);
            	editor.commit();
            	
            	Intent returnIntent = new Intent();
            	setResult(RESULT_OK,returnIntent);
            	this.finish();
            	
            }else{
            	
            	Toast toast = Toast.makeText(getApplicationContext(),"Username or password incorrect", Toast.LENGTH_SHORT);
	    	    toast.show();
            	
            }
    	
		} 
    }

	@Override
	public void onClick(View v) {
		if(v.getId()==R.id.login){
			myLogin(username.getText().toString(), password.getText().toString());
			System.out.println("Button pushed");
		}
		
	}

}
