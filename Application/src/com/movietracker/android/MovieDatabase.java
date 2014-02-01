package com.movietracker.android;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.Toast;

public class MovieDatabase {
	
	Context context;	
	private String rottentomatoes_api_key = "394fmrry66aendqtu8ptq52p";
	
	public MovieDatabase(Context c){
		context = c;
	}

	public ArrayList<Movie> search(String keyword, int page){
		String search_url = "http://api.rottentomatoes.com/api/public/v1.0/movies.json?apikey="
				+ rottentomatoes_api_key + "&q=" + keyword + "&page_limit=10&page=" + page;
		ArrayList<Movie> results = new ArrayList<Movie>();
		try {
			//String searchResult = new RequestTask(context).execute(search_url).get();
			String searchResult = getMovies(search_url);
			if(searchResult != null){
				JSONObject jsonResult = new JSONObject(searchResult);
				System.out.println("found " + jsonResult.getInt("total") + " movies");
				JSONArray jsonArray = jsonResult.getJSONArray("movies");
				for (int i = 0; i < jsonArray.length(); i++) {
				    JSONObject item = jsonArray.getJSONObject(i);
				    String title = item.getString("title");
				    String id = item.getString("id");
				    JSONObject release_dates = item.getJSONObject("release_dates");
				    String theater, dvd;
				    if(release_dates.has("theater")){
				    	theater = release_dates.getString("theater");
				    }else{
				    	theater = "Currently Not Available";
				    }
				    if(release_dates.has("dvd")){
				    	dvd = release_dates.getString("dvd");
				    }else{
				    	dvd = "Currently Not Available";
				    }
				    JSONObject posters = item.getJSONObject("posters");
				    String profile_url = posters.getString("profile");
				    String detailed_url = posters.getString("detailed");
				    Bitmap profile_bitmap = null;
				    Bitmap detailed_bitmap = null;
//				    try{
//						profile_bitmap = BitmapFactory.decodeStream((InputStream)new URL(profile_url).getContent());
//						//detailed_bitmap = BitmapFactory.decodeStream((InputStream)new URL(detailed_url).getContent());
//					} catch (MalformedURLException e) {
//						e.printStackTrace();
//					} catch (IOException e) {
//						e.printStackTrace();
//					}
				    Movie m = new Movie(title, id, theater, dvd, profile_bitmap, detailed_bitmap, profile_url, detailed_url, 0);
				    results.add(m);
				}
			}
		} catch (JSONException e){
			e.printStackTrace();
		}
		return results;		
	}
	
	/* type = movies or dvds
	 * category = box_office or upcoming or new_releases
	 */
	public ArrayList<Movie> browse(String type, String category, int page){
		//http://api.rottentomatoes.com/api/public/v1.0/lists/movies/box_office.json?limit=10&apikey=394fmrry66aendqtu8ptq52p
		String browse_url = "http://api.rottentomatoes.com/api/public/v1.0/lists/" + type + "/" + category
				+ ".json?apikey=" + rottentomatoes_api_key; //+ "&page_limit=10&page=" + page;
		ArrayList<Movie> results = new ArrayList<Movie>();
		try {
			System.out.println(browse_url);
			//String searchResult = new RequestTask(context).execute(search_url).get();
			String searchResult = getMovies(browse_url);
			if(searchResult != null){
				JSONObject jsonResult = new JSONObject(searchResult);
				JSONArray jsonArray = jsonResult.getJSONArray("movies");
				for (int i = 0; i < jsonArray.length(); i++) {
				    JSONObject item = jsonArray.getJSONObject(i);
				    String title = item.getString("title");
				    String id = item.getString("id");
				    JSONObject release_dates = item.getJSONObject("release_dates");
				    String theater, dvd;
				    if(release_dates.has("theater")){
				    	theater = release_dates.getString("theater");
				    }else{
				    	theater = "Currently Not Available";
				    }
				    if(release_dates.has("dvd")){
				    	dvd = release_dates.getString("dvd");
				    }else{
				    	dvd = "Currently Not Available";
				    }
				    JSONObject posters = item.getJSONObject("posters");
				    String profile_url = posters.getString("profile");
				    String detailed_url = posters.getString("detailed");
				    Bitmap profile_bitmap = null;
				    Bitmap detailed_bitmap = null;
				    try{
						profile_bitmap = BitmapFactory.decodeStream((InputStream)new URL(profile_url).getContent());
						//detailed_bitmap = BitmapFactory.decodeStream((InputStream)new URL(detailed_url).getContent());
					} catch (MalformedURLException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				    Movie m = new Movie(title, id, theater, dvd, profile_bitmap, detailed_bitmap, profile_url, detailed_url, 0);
				    results.add(m);
				}
			}
		} catch (JSONException e){
			e.printStackTrace();
		}
		return results;		
	}
	
	public String getMovies(String arg){
		StringBuilder response  = new StringBuilder();
    	try{
		    URL url = new URL(arg);
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
    	//System.out.println(response.toString());
	    return response.toString();
	}
}
