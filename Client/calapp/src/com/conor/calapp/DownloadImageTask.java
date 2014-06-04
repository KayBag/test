/*
 * Conor Killeen
 * 5/4/2014
 * 
 * This class is used to download an image from the internet.
 */
package com.conor.calapp;

import java.io.InputStream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

public class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
    
	private ImageView myImage;

    public DownloadImageTask(ImageView myImage) {
        this.myImage = myImage;
    }

    protected Bitmap doInBackground(String... urls) {
    	Bitmap image = null;
	    String url = urls[0];
	    
	    try {
	        InputStream in = new java.net.URL(url).openStream();
	        image = BitmapFactory.decodeStream(in);
	    } catch (Exception e) {
	    	e.printStackTrace();
	    }
	    
        return image;
    }

    protected void onPostExecute(Bitmap result) {
    	
    	if(result != null)
    		myImage.setImageBitmap(result);
    }
}