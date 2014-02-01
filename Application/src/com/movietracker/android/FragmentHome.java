package com.movietracker.android;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

public class FragmentHome extends ListFragment {
	
	LocalDatabase localdb;
	MovieDatabase moviedb;
	
	MoviesListAdapter adapter;
	ArrayList<Movie> my_movies = new ArrayList<Movie>();
	ArrayList<Movie> searched_movies = new ArrayList<Movie>();
	TextView searchResult;
	EditText search;
	int textlength = 0;
	
	LayoutInflater li;
	View rootView;
	
	boolean created = false;
	
	int positionCheck[] = new int[0];
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		System.out.println("fragment home onCreateView()");
		li = inflater;
		rootView = inflater.inflate(R.layout.fragment_home, container, false);
		localdb = new LocalDatabase(inflater.getContext());
		moviedb = new MovieDatabase(inflater.getContext());
	
		localdb.open();
		my_movies = localdb.getAllTrackingMovies();
		localdb.close();
		
//		my_movies.add(new Movie("Thor"));
//		my_movies.add(new Movie("Paranormal Activity"));
//		my_movies.add(new Movie("Insidious"));
//		my_movies.add(new Movie("Toy Story 3"));
//		my_movies.add(new Movie("Movie with really long name just for testing"));
//		my_movies.add(new Movie("Just Adding more movie to the list"));
		positionCheck = new int[my_movies.size()];
		for(int i = 0; i < my_movies.size(); i++) {
			positionCheck[i] = 0;
		}
		adapter = new MoviesListAdapter(inflater.getContext(), android.R.layout.simple_list_item_checked, my_movies);
		adapter.setToggleList(positionCheck);
        setListAdapter(adapter);
        
        search = (EditText) rootView.findViewById(R.id.etSearch);
        search.addTextChangedListener(new TextWatcher(){
			public void afterTextChanged(Editable s) {}
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
			public void onTextChanged(CharSequence s, int start, int before, int count)	{
				textlength = search.getText().length();
				searched_movies.clear();
				searchResult = (TextView) rootView.findViewById(R.id.tvMovieList);
				searchResult.setText("Searching for your movies...");
				
				AsyncTask<String, Void, ArrayList<Movie>> task = new AsyncTask<String, Void, ArrayList<Movie>>() {
					@Override
					protected ArrayList<Movie> doInBackground(String... arg0) {
						if(textlength == 0){
							return my_movies;
						}
						return moviedb.search(search.getText().toString(), 1);
					}

					@Override
					protected void onPostExecute(ArrayList<Movie> result) {
						if(textlength == 0){
							//refresh();
						}else{
							System.out.println("updating search results");
							searched_movies = result;
							if(searched_movies == null){
								searched_movies = new ArrayList<Movie>();	//avoid null pointer error
							}else{
								adapter = new MoviesListAdapter(li.getContext(), android.R.layout.simple_list_item_checked, searched_movies);
								adapter.setToggleList(positionCheck);
								setListAdapter(adapter);
								searchResult = (TextView) rootView.findViewById(R.id.tvMovieList);
								if (search.getText().length() != 0 && searched_movies.size() == 0) {
									//System.out.println("no match");
									searchResult.setText("Sorry, no match found...");
								}else{
									searchResult.setText(R.string.select_movie_message);					
								}
							}
						}
					}
				};
				task.execute();
			}
		});
        System.out.println("fragment home created");
		created = true;
        return rootView;
		//return super.onCreateView(inflater, container, savedInstanceState);
	}
	
	//refresh my movies
	public void refresh(){
		if(this.isAdded()){
			System.out.println("fragment home refreshing");
			search.setText("");
			searchResult.setText("refreshing...");
			localdb.open();
			ArrayList<Movie> temp = localdb.getAllTrackingMovies();
			localdb.close();
			//only refresh if database result changed
			if(temp.size() != my_movies.size()){
				my_movies = new ArrayList<Movie>(temp);
				System.out.println(my_movies.size());
				positionCheck = new int[my_movies.size()];
				for(int i = 0; i < my_movies.size(); i++) {
					positionCheck[i] = 0;
				}
				adapter = new MoviesListAdapter(li.getContext(), android.R.layout.simple_list_item_1, my_movies);
				adapter.setToggleList(positionCheck);
		        setListAdapter(adapter);
			}
			searchResult.setText(R.string.select_movie_message);
		}
	}
	
	public boolean created(){
		return created;
	}
	
	//update tracked movies every time the user leaves the page
	public void update(){
		
	}
	
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		System.out.println("FragmentHome onResume()");
		refresh();
	}
	

	@Override
	public void onDestroyView() {
		// TODO Auto-generated method stub
		super.onDestroyView();
		System.out.println("FragmentHome onDestroyView()");
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		System.out.println("FragmentHome onDestroy()");
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		// TODO Auto-generated method stub
		super.onListItemClick(l, v, position, id);
//		CheckBox trackButton = (CheckBox) v.findViewById(R.id.movieTrackButton);
//		trackButton.setChecked(!trackButton.isChecked());
//		if(search.length() == 0){
//			if (trackButton.isChecked()){
//				originalIndex[position] = 1;
//			}else{
//				originalIndex[position] = 0;
//			}
//			adapter.setToggleList(originalIndex);
//		}else{
//			if (trackButton.isChecked()){
//				positionCheck[position] = 1;
//				//originalIndex[position_sort.get(position)] = 1;
//			}else{
//				positionCheck[position] = 0;
//				//originalIndex[position_sort.get(position)] = 0;
//			}
//			adapter.setToggleList(positionCheck);
//		}
	}
	
	public int getCount(){
		return my_movies.size();
	}

}
