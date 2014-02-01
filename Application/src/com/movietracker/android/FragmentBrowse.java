package com.movietracker.android;

import java.util.ArrayList;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

public class FragmentBrowse extends Fragment {
	
	MovieDatabase moviedb;
	MoviesGridAdapter gridAdapter;
	MoviesListAdapter listAdapter;
	ArrayList<Movie> top_box_office_movies = new ArrayList<Movie>();
	ArrayList<Movie> upcoming_movies = new ArrayList<Movie>();
	ArrayList<Movie> new_dvd_release_movies = new ArrayList<Movie>();
	TextView message;
	Spinner spinner;
	ListView listview;
	GridView gridview;
	LayoutInflater li;
	ViewGroup vg;
	View rootView;
	int positionCheck[] = new int[0];
	int pageCounter;
	boolean isGridView;
	boolean isLoading;
	int currentFirstVisibleItem, currentVisibleItemCount, currentScrollState;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		System.out.println("fragmentBrowse onCreate()");
		this.isGridView = getArguments().getBoolean("gridview");
	}



	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		System.out.println("fragmentBrowse onCreateView()");
		li = inflater;
		vg = container;
		if(isGridView){
			rootView = inflater.inflate(R.layout.fragment_browse_gridview, container, false);
			gridview = (GridView) rootView.findViewById(R.id.fragment_movie_gridview);
		}else{
			rootView = inflater.inflate(R.layout.fragment_browse, container, false);
			listview = (ListView) rootView.findViewById(android.R.id.list);
		}
		moviedb = new MovieDatabase(inflater.getContext());
		message = (TextView) rootView.findViewById(R.id.tvMovieList);
		message.setText("Loading your movies...");
		
		pageCounter = 1;
		isLoading = false;
		
		AsyncTask<String, Void, ArrayList<Movie>> task = new AsyncTask<String, Void, ArrayList<Movie>>() {
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
				if(isGridView){
					gridAdapter = new MoviesGridAdapter(li.getContext(), android.R.layout.simple_list_item_1, top_box_office_movies);
					gridAdapter.setToggleList(positionCheck);
			        gridview.setAdapter(gridAdapter);
				}else{
					listAdapter = new MoviesListAdapter(li.getContext(), android.R.layout.simple_list_item_1, top_box_office_movies);
					listAdapter.setToggleList(positionCheck);
			        listview.setAdapter(listAdapter);
				}
				message.setText(R.string.select_movie_message);
			}
		};
		task.execute();
		spinner = (Spinner) rootView.findViewById(R.id.browseSpinner);
		spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                	//pageCounter = 1;
                    int position = spinner.getSelectedItemPosition();
                    message = (TextView) rootView.findViewById(R.id.tvMovieList);
    				message.setText("Loading your movies...");
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
                				if(isGridView){
                					gridAdapter = new MoviesGridAdapter(li.getContext(), android.R.layout.simple_list_item_1, top_box_office_movies);
                					gridAdapter.setToggleList(positionCheck);
                			        gridview.setAdapter(gridAdapter);
                				}else{
                					listAdapter = new MoviesListAdapter(li.getContext(), android.R.layout.simple_list_item_1, top_box_office_movies);
                					listAdapter.setToggleList(positionCheck);
                			        listview.setAdapter(listAdapter);
                				}
                				message.setText(R.string.select_movie_message);
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
                				if(isGridView){
                					gridAdapter = new MoviesGridAdapter(li.getContext(), android.R.layout.simple_list_item_1, upcoming_movies);
                					gridAdapter.setToggleList(positionCheck);
                			        gridview.setAdapter(gridAdapter);
                				}else{
                					listAdapter = new MoviesListAdapter(li.getContext(), android.R.layout.simple_list_item_1, upcoming_movies);
                					listAdapter.setToggleList(positionCheck);
                			        listview.setAdapter(listAdapter);
                				}
                				message.setText(R.string.select_movie_message);
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
                				if(isGridView){
                					gridAdapter = new MoviesGridAdapter(li.getContext(), android.R.layout.simple_list_item_1, new_dvd_release_movies);
                					gridAdapter.setToggleList(positionCheck);
                			        gridview.setAdapter(gridAdapter);
                				}else{
                					listAdapter = new MoviesListAdapter(li.getContext(), android.R.layout.simple_list_item_1, new_dvd_release_movies);
                					listAdapter.setToggleList(positionCheck);
                			        listview.setAdapter(listAdapter);
                				}
                				message.setText(R.string.select_movie_message);
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

	//if arg is true, switch to gridview, else switch to listview
	public void refresh(boolean arg){
		if(this.isAdded()){
			this.isGridView = arg;
			getFragmentManager().beginTransaction().detach(this).attach(this).commit();
		}
	}
	
	
}
