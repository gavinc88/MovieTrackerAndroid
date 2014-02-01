package com.movietracker.android;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;

public class RequestTask extends AsyncTask<String, String, String>{
	
	Context context;
	
	public RequestTask(Context c){
		System.out.println("request task instantiated");
		context = c;
	}

    @Override
    protected String doInBackground(String... uri) {
    	System.out.println("request task do in background");
    	if(!isNetworkAvailable()){
    		return null;
    	}else{
	    	StringBuilder response  = new StringBuilder();
	    	try{
			    URL url = new URL(uri[0]);
			    HttpURLConnection httpconn = (HttpURLConnection)url.openConnection();
			    if (httpconn.getResponseCode() == HttpURLConnection.HTTP_OK){
			        BufferedReader input = new BufferedReader(new InputStreamReader(httpconn.getInputStream()),8192);
			        String strLine = null;
			        while ((strLine = input.readLine()) != null){
			            response.append(strLine);
			        }
			        input.close();
			    }
	    	}catch(Exception e){
	    		e.printStackTrace();
	    	}
		    return response.toString();
    	}
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        //Do anything with response..
        if(result == null){
        	AlertDialog.Builder builder = new AlertDialog.Builder(context);
    		builder.setTitle("Connection Failed");
    		builder.setMessage("Make sure you are connected to the internet.");
    		builder.setCancelable(true);
    		builder.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					dialog.cancel();
				}
			});
    		builder.show();
        }else{
        	System.out.println("request task completed");
        }
    }
    
    public boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        // if no network is available networkInfo will be null
        // otherwise check if we are connected
        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        }
        return false;
    } 
}
