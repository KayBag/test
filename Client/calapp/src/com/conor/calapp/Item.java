/*
 * Conor Killeen
 * 5/4/2014
 * 
 * This class is responsible for setting up the requests relating to items.
 */
package com.conor.calapp;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.conor.calapp.R;

import android.content.Context;

public class Item {

	private Context context;
	private Food food;
	private Suggestion suggestion;
	
	public Item(Context context, Food food) {
		this.context = context;
		this.food = food;
	}
	
	public Item(Context context, Suggestion suggestion) {
		this.context = context;
		this.suggestion = suggestion;
	}
	
	public Item(Context context) {
		this.context = context;
	}
	
	public String addItem(String username,String code){
		
		String result = null;
		
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2); 
		nameValuePairs.add(new BasicNameValuePair("action", "get_data"));
		nameValuePairs.add(new BasicNameValuePair("username", username));
		nameValuePairs.add(new BasicNameValuePair("code", code));
		
		try {
			result = new myInfoSend(nameValuePairs,context.getString(R.string.url),context).execute().get();
			System.out.println(result);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	public String getUserItems(String username){
		
		String result = null;
		
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2); 
		nameValuePairs.add(new BasicNameValuePair("action", "get_barcode"));
		nameValuePairs.add(new BasicNameValuePair("username", username));
		
		new myInfoSend(nameValuePairs,context.getString(R.string.url),context,food).execute();
		
		return result;
	}
	
	public String getUserSuggestions(String username){
		
		String result = null;
		
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2); 
		nameValuePairs.add(new BasicNameValuePair("action", "suggest"));
		nameValuePairs.add(new BasicNameValuePair("username", username));
		
		new myInfoSend(nameValuePairs,context.getString(R.string.url),context,suggestion).execute();
		
		return result;
	}

	public String rate(String username,String code){
		
		String result = null;
		
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2); 
		nameValuePairs.add(new BasicNameValuePair("action", "rate"));
		nameValuePairs.add(new BasicNameValuePair("username", username));
		nameValuePairs.add(new BasicNameValuePair("code", code));
		
		try {
			result = new myInfoSend(nameValuePairs,context.getString(R.string.url),context).execute().get();
			System.out.println(result);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	public String delete(String username,String code){
		
		String result = null;
		
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2); 
		nameValuePairs.add(new BasicNameValuePair("action", "delete"));
		nameValuePairs.add(new BasicNameValuePair("username", username));
		nameValuePairs.add(new BasicNameValuePair("code", code));
		
		try {
			result = new myInfoSend(nameValuePairs,context.getString(R.string.url),context).execute().get();
			System.out.println(result);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
}
