package com.movietracker.android;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;

import org.json.JSONObject;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

public class Movie implements Comparator<Movie>{
	
	private String title, id, theater_release, dvd_release, profile_image_url, detailed_image_url;
	private Bitmap profile_image, detailed_image;
	private int track_count;
	private boolean tracked;
	
	private final String api_date_format = "yyyy-MM-dd";
	private final String user_date_format = "MMM dd, yyyy";
	
	public Movie(String title, String id, String theater, String dvd, Bitmap profile, Bitmap detail, String profile_url, String detailed_url, int count){
		this.title = title;
		this.id = id;
		//convert date format for theater and dvd releases
		if(theater.equals("Currently Not Available")){
			this.theater_release = theater;
		}else{
			try{
				SimpleDateFormat sdf = new SimpleDateFormat(api_date_format);
				Date theaterDate = sdf.parse(theater);
				sdf = new SimpleDateFormat(user_date_format);
				this.theater_release = sdf.format(theaterDate);
			}catch(ParseException e){
				e.printStackTrace();
			}
		}
		if(dvd.equals("Currently Not Available")){
			this.dvd_release = dvd;
		}else{
			try{
				SimpleDateFormat sdf = new SimpleDateFormat(api_date_format);
				Date dvdDate = sdf.parse(dvd);
				sdf = new SimpleDateFormat(user_date_format);
				this.dvd_release = sdf.format(dvdDate);
			}catch(ParseException e){
				e.printStackTrace();
			}
		}
		this.profile_image = profile;
		this.detailed_image = detail;
		this.profile_image_url = profile_url;
		//detailed url used later for detailed view
		this.detailed_image_url = detailed_url;
		this.track_count = count;
		this.tracked = false;
	}
	
	//used for database
	public Movie(String title, String id, String theater, String dvd, String profile_url, String detailed_url, boolean tracked, int count){
		this.title = title;
		this.id = id;
		this.theater_release = theater;
		this.dvd_release = dvd;
		this.profile_image_url = profile_url;
		this.detailed_image_url = detailed_url;
		this.tracked = tracked;
		this.track_count = count;
	}
	
	public Movie(String movie_title){
		title = movie_title;
		SimpleDateFormat sdf = new SimpleDateFormat(user_date_format);
		String today = sdf.format(new Date());
		theater_release = today;
		dvd_release = today;
		tracked = false;
	}

	public boolean wasDvdReleased(){
		try{
			SimpleDateFormat sdf = new SimpleDateFormat(user_date_format);
			Date strDate = sdf.parse(dvd_release);
			return new Date().after(strDate);
		}catch(ParseException e){
			System.out.println("date parse exception: " + e);
			return false;
		}
	}
	
	@Override
	public int compare(Movie m1, Movie m2) {
		// TODO Auto-generated method stub
		return m1.getTitle().compareTo(m2.getTitle());
	}
	
	public String getTitle(){
		return title;
	}
	
	public String getTheaterRelease(){
		return theater_release;
	}
	
	public String getDvdRelease(){
		return dvd_release;
	}
	
	public Bitmap getProfileImage(){
		return profile_image;
	}
	
	public Bitmap getDetailedImage(){
		return detailed_image;
	}
	
	public void setProfileImage(Bitmap bm){
		profile_image = bm;
	}
	
	public void setDetailedImage(Bitmap bm){
		detailed_image = bm;
	}
	
	public String getProfileImageUrl(){
		return profile_image_url;
	}
	
	public String getDetailedImageUrl(){
		return detailed_image_url;
	}
	
	public String getId(){
		return id;
	}
	
	public int getTrackCount(){
		return track_count;
	}
	
	public boolean isTracking(){
		return tracked;
	}
	
	public void trackToggle(){
		tracked = !tracked;
	}
	
	public void setSelected(boolean selected) {
	    this.tracked = selected;
	  }

}
