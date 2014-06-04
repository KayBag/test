/*
 * Conor Killeen
 * 5/4/2014
 * 
 * This class is used to send requests to the server.
 */
package com.conor.calapp;

import java.io.IOException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

@SuppressWarnings("unused")
public class myInfoSend extends AsyncTask<String, Integer, String> {
	
	private ProgressDialog dialog;
	private String result = null;
	private String mode = null;
	private String status = null;
	
	private static final String TAG_SESSIONID = "user_id";
	
	private String url;
	private ArrayList<NameValuePair> nameValuePairs;
	private Context context;
	private Food food;
	private Suggestion suggestion;
	
	public myInfoSend(ArrayList<NameValuePair> nameValuePairs, String url, Context context, Food food){
		this.nameValuePairs = nameValuePairs;
		this.url = url;
		this.context = context;
		this.food = food;
	}
	
	public myInfoSend(ArrayList<NameValuePair> nameValuePairs, String url, Context context, Suggestion suggestion){
		this.nameValuePairs = nameValuePairs;
		this.url = url;
		this.context = context;
		this.suggestion = suggestion;
	}
	
	public myInfoSend(ArrayList<NameValuePair> nameValuePairs, String url, Context context){
		this.nameValuePairs = nameValuePairs;
		this.url = url;
		this.context = context;
		this.mode = "1";
	}
	
	protected void onPreExecute(){
    	super.onPreExecute();
    	if(mode == null){
	    	dialog = new ProgressDialog(context);
			
			if(context instanceof Food)
				dialog.setMessage("Please Wait Loading your food..");
			if(context instanceof Suggestion)
				dialog.setMessage("Please Wait Loading some suggestions..");
			
			dialog.show();
    	}
    	
    }
	
	@Override
	protected String doInBackground(String... params) {
		
		ResponseHandler <String> res = new BasicResponseHandler();
		
		HttpParams myParams = new BasicHttpParams();
        HttpProtocolParams.setVersion(myParams, HttpVersion.HTTP_1_1);
        HttpProtocolParams.setContentCharset(myParams, HTTP.UTF_8);
        HttpProtocolParams.setUseExpectContinue(myParams, true);
        HttpConnectionParams.setSoTimeout(myParams, 10000); 
        HttpConnectionParams.setConnectionTimeout(myParams, 10000);
		
		HttpClient httpclient = new DefaultHttpClient(myParams);
		HttpPost myRequest = new HttpPost(url);
		try {
			
			myRequest.setEntity(new UrlEncodedFormEntity(nameValuePairs)); 
			result = httpclient.execute(myRequest, res);
			
		}catch (UnknownHostException e) {
			status = "FAIL";
			e.printStackTrace();
        }catch (HttpHostConnectException e) {
        	status = "FAIL";
        	e.printStackTrace();
        }catch (ConnectTimeoutException e) {
        	status = "FAIL";
        	e.printStackTrace();
		}catch (SocketTimeoutException e) {
			status = "FAIL";
			e.printStackTrace();
		}catch (SocketException e) {
			status = "FAIL";
			e.printStackTrace();
		}catch (ClientProtocolException e) {
			status = "FAIL";
			e.printStackTrace();
		}catch (IOException e) {
			status = "FAIL";
			e.printStackTrace();
		}
		if(status != "FAIL")
			return result;
		else
			return status;
	}
	
	protected void onPostExecute(String json) {
		
		if(mode == null)
			dialog.dismiss();
		
		if(status == null && mode == null){
			if(context instanceof Food)
				food.getData(result);
			else if(context instanceof Suggestion)
				suggestion.getData(result);
		}
    }
} 