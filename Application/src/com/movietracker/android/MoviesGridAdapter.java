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
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

public class MoviesGridAdapter extends ArrayAdapter<Movie> {
	
	private ArrayList<Movie> movies;
	private int[] toggle;
	private LocalDatabase localdb;

	public MoviesGridAdapter(Context context, int textViewResourceId, ArrayList<Movie> movies) {
		super(context, textViewResourceId, movies);
		this.movies = movies;
		this.localdb = new LocalDatabase(context);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = null;
		
        if (convertView == null) {
            LayoutInflater vi = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.movie_gridview, null);
            final ViewHolder holder = new ViewHolder();
            holder.rootLayout = v;
            holder.movie_image = (View) v.findViewById(R.id.grid_image);
            holder.movie_info = (View) v.findViewById(R.id.grid_info);            
            holder.movieImage = (ImageView) v.findViewById(R.id.movieImage);
            holder.movieTitle = (TextView) v.findViewById(R.id.movieTitle);
            holder.movieTheaterReleaseDate = (TextView) v.findViewById(R.id.movieTheaterDate);
            holder.movieDvdReleaseDate = (TextView) v.findViewById(R.id.movieDvdDate);
            
            holder.movieTrackButton1 = (CheckBox) v.findViewById(R.id.movieTrackButton1);
            holder.movieTrackButton1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                	Movie movie = (Movie) holder.movieTrackButton1.getTag();
                	movie.setSelected(buttonView.isChecked());
                	localdb.open();
            		localdb.addMovie(movie);
            		localdb.close();
                }
              });
            holder.movieTrackButton2 = (CheckBox) v.findViewById(R.id.movieTrackButton2);
            holder.movieTrackButton2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                	Movie movie = (Movie) holder.movieTrackButton1.getTag();
                	movie.setSelected(buttonView.isChecked());
                	if(holder.movie_info.getVisibility() == View.VISIBLE){
	                	if(isChecked){
	                		//just checked - add movie to local database
	                		holder.movieTrackButton2.setText("Untrack");
	                	}else{
	                		//unchecked - change database entry
	                		holder.movieTrackButton2.setText("Track");
	                	}
                	}
                	localdb.open();
            		localdb.addMovie(movie);
            		localdb.close();
                }
              });
            holder.movie_image.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					System.out.println("movie_image clicked()");
					FlipAnimation flipAnimation = new FlipAnimation(holder.movie_image, holder.movie_info);
				    if (holder.movie_image.getVisibility() == View.GONE)
				    {
				        flipAnimation.reverse();
				    }
				    holder.rootLayout.startAnimation(flipAnimation);
				    holder.movieTrackButton2.setChecked(holder.movieTrackButton1.isChecked());
				}
            });
            
            holder.movie_info.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					System.out.println("movie_info clicked()");
					FlipAnimation flipAnimation = new FlipAnimation(holder.movie_info, holder.movie_image);
				    if (holder.movie_info.getVisibility() == View.GONE)
				    {
				        flipAnimation.reverse();
				    }
				    holder.rootLayout.startAnimation(flipAnimation);
				    holder.movieTrackButton1.setChecked(holder.movieTrackButton2.isChecked());
				}
            });
            v.setTag(holder);
          	holder.movieTrackButton1.setTag(movies.get(position));
        }else{
        	v = convertView;
            ((ViewHolder) v.getTag()).movieTrackButton1.setTag(movies.get(position));
        }
        
        final ViewHolder holder = (ViewHolder) v.getTag();
        Movie m = movies.get(position);
        if (m != null) {
        	//set movie info
        	Bitmap profileImage = m.getProfileImage();
        	if(profileImage != null){
        		holder.movieImage.setImageBitmap(profileImage);
        	}
//        	else{
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
            holder.movieTheaterReleaseDate.setText(m.getTheaterRelease());
            holder.movieDvdReleaseDate.setText(m.getDvdRelease());
            localdb.open();
            boolean tracking = localdb.isTrackingMovie(m);
            holder.movieTrackButton1.setChecked(tracking);
            holder.movieTrackButton2.setChecked(tracking);
            localdb.close();
        }
        return v;
	}
	
	static class ViewHolder{
		ImageView movieImage;
		TextView movieTitle;
		TextView movieTheaterReleaseDate;
		TextView movieDvdReleaseDate;
		CheckBox movieTrackButton1;
		CheckBox movieTrackButton2;
		View rootLayout;
		View movie_image;
		View movie_info;
		
		public void flipMovie(){
			FlipAnimation flipAnimation = new FlipAnimation(movie_image, movie_info);
		    if (movieImage.getVisibility() == View.GONE)
		    {
		        flipAnimation.reverse();
		    }
		    rootLayout.startAnimation(flipAnimation);
		}
    }
	
	public void setToggleList(int [] list) {
        this.toggle = list;
        notifyDataSetChanged();
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
