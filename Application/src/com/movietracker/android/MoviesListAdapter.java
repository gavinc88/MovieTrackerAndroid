package com.movietracker.android;


import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

public class MoviesListAdapter extends ArrayAdapter<Movie> {
	
	private ArrayList<Movie> movies;
	private int[] toggle;
	private LocalDatabase localdb;

	public MoviesListAdapter(Context context, int textViewResourceId, ArrayList<Movie> movies) {
		super(context, textViewResourceId, movies);
		this.movies = movies;
		this.localdb = new LocalDatabase(context);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = null;       
        if (convertView == null) {
            LayoutInflater vi = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.movie_listview, null);
            final ViewHolder holder = new ViewHolder();
            holder.movieImage = (ImageView) v.findViewById(R.id.movieImage);
            holder.movieTitle = (TextView) v.findViewById(R.id.movieTitle);
            holder.movieTheaterReleaseDate = (TextView) v.findViewById(R.id.movieTheaterDate);
            holder.movieDvdReleaseDate = (TextView) v.findViewById(R.id.movieDvdDate);
            holder.movieTrackButton = (CheckBox) v.findViewById(R.id.movieTrackButton1);
            holder.movieTrackButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                	Movie movie = (Movie) holder.movieTrackButton.getTag();
                	movie.setSelected(buttonView.isChecked());
                	if(isChecked){
                		//just checked - add movie to local database
                		holder.movieTrackButton.setText("Untrack");
                	}else{
                		//unchecked - change database entry
                		holder.movieTrackButton.setText("Track");
                	}
                	localdb.open();
            		localdb.addMovie(movie);
            		localdb.close();
                }
              });
            v.setTag(holder);
          	holder.movieTrackButton.setTag(movies.get(position));
        }else{
        	v = convertView;
            ((ViewHolder) v.getTag()).movieTrackButton.setTag(movies.get(position));
        }
        final ViewHolder holder = (ViewHolder) v.getTag();
        Movie m = movies.get(position);
        if (m != null) {
        	//set movie info
        	Bitmap profileImage = m.getProfileImage();
//        	if(profileImage != null){
//        		holder.movieImage.setImageBitmap(profileImage);
//        	}else{
//        		holder.movieImage.setImageResource(R.drawable.movie_title_picture_default);
//        	}
        	AsyncTask<String, Void, Bitmap> task = new AsyncTask<String, Void, Bitmap>() {
    			@Override
    			protected Bitmap doInBackground(String... arg0) {
    				Bitmap profile_bitmap = null;
    			    Bitmap detailed_bitmap = null;
    			    try{
    					profile_bitmap = BitmapFactory.decodeStream((InputStream)new URL(arg0[0]).getContent());
    					//detailed_bitmap = BitmapFactory.decodeStream((InputStream)new URL(detailed_url).getContent());
    				} catch (MalformedURLException e) {
    					e.printStackTrace();
    				} catch (IOException e) {
    					e.printStackTrace();
    				}
    			    return profile_bitmap;
    			}

    			@Override
    			protected void onPostExecute(Bitmap result) {
    				holder.movieImage.setImageBitmap(result);
    			}
    		};
    		task.execute(m.getProfileImageUrl());
            holder.movieTitle.setText(m.getTitle());
            holder.movieTheaterReleaseDate.setText("Theater: " + m.getTheaterRelease());
            holder.movieDvdReleaseDate.setText("DVD: " + m.getDvdRelease());
            localdb.open();
            holder.movieTrackButton.setChecked(localdb.isTrackingMovie(m));
            localdb.close();
        }
        return v;
	}
	
	static class ViewHolder{
		ImageView movieImage;
		TextView movieTitle;
		TextView movieTheaterReleaseDate;
		TextView movieDvdReleaseDate;
		CheckBox movieTrackButton;
    }
	
	public void setToggleList(int [] list) {
        this.toggle = list;
        notifyDataSetChanged();
    }

	@Override
	public void add(Movie object) {
		// TODO Auto-generated method stub
		super.add(object);
	}

	@Override
	public void addAll(Collection<? extends Movie> collection) {
		// TODO Auto-generated method stub
		super.addAll(collection);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return movies.size();
	}

	@Override
	public void sort(Comparator<? super Movie> comparator) {
		// TODO Auto-generated method stub
		super.sort(comparator);
	}
	

}
