package com.movietracker.android;

import java.util.ArrayList;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Spinner;
import android.widget.TextView;

public class FragmentBrowseGridView extends Fragment {
	
	MovieDatabase moviedb;
	MoviesGridAdapter adapter;
	ArrayList<Movie> top_box_office_movies = new ArrayList<Movie>();
	ArrayList<Movie> upcoming_movies = new ArrayList<Movie>();
	ArrayList<Movie> new_dvd_release_movies = new ArrayList<Movie>();
	TextView searchResult;
	Spinner spinner;
	GridView gridview;
	LayoutInflater li;
	View rootView;
	int positionCheck[] = new int[0];
	int pageCounter;
	boolean isLoading;
	int currentFirstVisibleItem, currentVisibleItemCount, currentScrollState;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		li = inflater;
		rootView = inflater.inflate(R.layout.fragment_browse_gridview, container, false);
		
		moviedb = new MovieDatabase(inflater.getContext());

		System.out.println("gridview onCreateView()");
		gridview = (GridView) rootView.findViewById(R.id.fragment_movie_gridview);
		searchResult = (TextView) rootView.findViewById(R.id.tvMovieList);
		searchResult.setText("Loading your movies...");
		
		pageCounter = 1;
		isLoading = false;
		
		AsyncTask<String, Void, ArrayList<Movie>> task = new AsyncTask<String, Void, ArrayList<Movie>>() {
			@Override
			protected ArrayList<Movie> doInBackground(String... arg0) {
				return moviedb.browse("movies", "box_office", pageCounter);
			}

			@Override
			protected void onPostExecute(ArrayList<Movie> result) {
				top_box_office_movies = result;
				positionCheck = new int[top_box_office_movies.size()];
				for(int i = 0; i < top_box_office_movies.size(); i++) {
					positionCheck[i] = 0;
				}
				adapter = new MoviesGridAdapter(li.getContext(), android.R.layout.simple_list_item_1, top_box_office_movies);
				adapter.setToggleList(positionCheck);
		        gridview.setAdapter(adapter);
		        searchResult = (TextView) rootView.findViewById(R.id.tvMovieList);
				searchResult.setText(R.string.select_movie_message);
			}
		};
		task.execute();
		spinner = (Spinner) rootView.findViewById(R.id.browseSpinner);
		spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                	//pageCounter = 1;
                    int position = spinner.getSelectedItemPosition();
                    searchResult = (TextView) rootView.findViewById(R.id.tvMovieList);
    				searchResult.setText("Loading your movies...");
                    AsyncTask<String, Void, ArrayList<Movie>> task;
                    switch (position){
                    case 0:
                    	task = new AsyncTask<String, Void, ArrayList<Movie>>() {
                			@Override
                			protected ArrayList<Movie> doInBackground(String... arg0) {
                				if(top_box_office_movies.size() == 0)
                					return moviedb.browse("movies", "box_office", pageCounter);
                				return top_box_office_movies;
                			}

                			@Override
                			protected void onPostExecute(ArrayList<Movie> result) {
                				top_box_office_movies = result;
                				positionCheck = new int[top_box_office_movies.size()];
                				for(int i = 0; i < top_box_office_movies.size(); i++) {
                					positionCheck[i] = 0;
                				}
                				adapter = new MoviesGridAdapter(li.getContext(), android.R.layout.simple_list_item_1, top_box_office_movies);
                				adapter.setToggleList(positionCheck);
                		        gridview.setAdapter(adapter);
                		       
                		        searchResult = (TextView) rootView.findViewById(R.id.tvMovieList);
                				searchResult.setText(R.string.select_movie_message);
                			}
                		};
                		task.execute();
                		break;
                    case 1:
                    	task = new AsyncTask<String, Void, ArrayList<Movie>>() {
                			@Override
                			protected ArrayList<Movie> doInBackground(String... arg0) {
                				if(upcoming_movies.size() == 0)
                					return moviedb.browse("movies", "upcoming", pageCounter);
                				return upcoming_movies;
                			}

                			@Override
                			protected void onPostExecute(ArrayList<Movie> result) {
                				upcoming_movies = result;
                				positionCheck = new int[upcoming_movies.size()];
                				for(int i = 0; i < upcoming_movies.size(); i++) {
                					positionCheck[i] = 0;
                				}
                				adapter = new MoviesGridAdapter(li.getContext(), android.R.layout.simple_list_item_1, upcoming_movies);
                				adapter.setToggleList(positionCheck);
                		        gridview.setAdapter(adapter);
                		       
                		        searchResult = (TextView) rootView.findViewById(R.id.tvMovieList);
                				searchResult.setText(R.string.select_movie_message);
                			}
                		};
                		task.execute();
                		break;
                    case 2:
                    	task = new AsyncTask<String, Void, ArrayList<Movie>>() {
                			@Override
                			protected ArrayList<Movie> doInBackground(String... arg0) {
                				if(new_dvd_release_movies.size() == 0)
                					return moviedb.browse("dvds", "new_releases", pageCounter);
                				return new_dvd_release_movies;
                			}

                			@Override
                			protected void onPostExecute(ArrayList<Movie> result) {
                				new_dvd_release_movies = result;
                				positionCheck = new int[new_dvd_release_movies.size()];
                				for(int i = 0; i < new_dvd_release_movies.size(); i++) {
                					positionCheck[i] = 0;
                				}
                				adapter = new MoviesGridAdapter(li.getContext(), android.R.layout.simple_list_item_checked, new_dvd_release_movies);
                				adapter.setToggleList(positionCheck);
                		        gridview.setAdapter(adapter);
                		       
                		        searchResult = (TextView) rootView.findViewById(R.id.tvMovieList);
                				searchResult.setText(R.string.select_movie_message);
                			}
                		};
                		task.execute();
                		break;
                    }
                    
                }
                @Override
                public void onNothingSelected(AdapterView<?> arg0) {
                    // TODO Auto-generated method stub
                }
            }
        );
        return rootView;
	}
	
	

}
