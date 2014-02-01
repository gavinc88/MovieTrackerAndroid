package com.movietracker.android;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class LocalDatabase {

	// Database fields
	private SQLiteDatabase database;
	private DbHelper dbHelper;
	private String[] columns = { KEY_ROWID, KEY_MOVIEID, KEY_MOVIETITLE, KEY_THEATER, KEY_DVD, KEY_PROFILE, KEY_DETAILED, KEY_TRACKED, KEY_TRACKCOUNT};
	
	public static final String KEY_ROWID = "_id";
	public static final String KEY_MOVIEID = "movie_id";
	public static final String KEY_MOVIETITLE = "movie_title";
	public static final String KEY_THEATER = "theater_release";
	public static final String KEY_DVD = "dvd_release";
	public static final String KEY_PROFILE = "profile_image";
	public static final String KEY_DETAILED = "detailed_image";
	public static final String KEY_TRACKED = "tracking";
	public static final String KEY_TRACKCOUNT = "track_count";

	private static final String DATABASE_NAME = "Movies";
	private static final String DATABASE_TABLE = "Movies";
	private static final int DATABASE_VERSION = 1;

	// Database creation sql statement
	private static final String DATABASE_CREATE = "CREATE TABLE " + DATABASE_TABLE 
			+ " (_id INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ KEY_MOVIEID + " TEXT NOT NULL, " 
			+ KEY_MOVIETITLE + " TEXT NOT NULL, " 
			+ KEY_THEATER + " TEXT NOT NULL, " 
			+ KEY_DVD + " TEXT NOT NULL, " 
			+ KEY_PROFILE + " TEXT NOT NULL, "
			+ KEY_DETAILED + " TEXT NOT NULL, " 
			+ KEY_TRACKED + " INT NOT NULL, " 
			+ KEY_TRACKCOUNT + " INT NOT NULL);";

	private static class DbHelper extends SQLiteOpenHelper {

		public DbHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase database) {
			database.execSQL(DATABASE_CREATE);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			System.out.println("Upgrading database from version " + oldVersion
							+ " to " + newVersion
							+ ", which will destroy all old data");
			db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
			onCreate(db);
		}
	}
	
	public LocalDatabase(Context context){
		dbHelper = new DbHelper(context);
	}
	
	public LocalDatabase open() {
		database = dbHelper.getWritableDatabase();
		return this;
	}

	public void close() {
		dbHelper.close();
	}
	
	public Movie addMovie(Movie movie){
		if(hasMovie(movie)){
			ContentValues values = new ContentValues();
			if(movie.isTracking()){
				values.put(KEY_TRACKED, 1);
			}else{
				values.put(KEY_TRACKED, 0);
			}
			database.update(DATABASE_TABLE, values, KEY_MOVIEID + " = " + movie.getId(), null);
			//System.out.println(movie.getTitle() + " with id: " + movie.getId() + " updated");
			Cursor cursor = database.query(DATABASE_TABLE, columns, KEY_MOVIEID + " = " + movie.getId(), null, null, null, null);
			cursor.moveToFirst();
			Movie newMovie = cursorToMovie(cursor);
			cursor.close();
			return newMovie;
		}else{
			ContentValues values = new ContentValues();
			values.put(KEY_MOVIEID, movie.getId());
			values.put(KEY_MOVIETITLE, movie.getTitle());
			values.put(KEY_THEATER, movie.getTheaterRelease());
			values.put(KEY_DVD, movie.getDvdRelease());
			values.put(KEY_PROFILE, movie.getProfileImageUrl());
			values.put(KEY_DETAILED, movie.getDetailedImageUrl());
			if(movie.isTracking()){
				values.put(KEY_TRACKED, 1);
			}else{
				values.put(KEY_TRACKED, 0);
			}
			values.put(KEY_TRACKCOUNT, movie.getTrackCount());
			
			long insertId = database.insert(DATABASE_TABLE, null, values);
			System.out.println(movie.getTitle() + " inserted at " + insertId);
			Cursor cursor = database.query(DATABASE_TABLE, columns, "_id = " + insertId, null, null, null, null);
			cursor.moveToFirst();
			Movie newMovie = cursorToMovie(cursor);
			cursor.close();
			return newMovie;
		}
	}
	
	public void deleteMovie(Movie movie) {
		String id = movie.getId();
		System.out.println("Movie deleted with id: " + id + " (" + movie.getTitle() + ")");
		database.delete(DATABASE_TABLE, KEY_MOVIEID + " = " + id, null);
	}
	
	public ArrayList<Movie> getAllTrackingMovies() {
		//System.out.println("getting all tracking movies");
		ArrayList<Movie> movies = new ArrayList<Movie>();
	    Cursor cursor = database.query(DATABASE_TABLE, columns, KEY_TRACKED + " = 1", null, null, null, null);
	    cursor.moveToFirst();
	    while (!cursor.isAfterLast()) {
			Movie movie = cursorToMovie(cursor);
			movies.add(movie);
			cursor.moveToNext();
	    }
	    cursor.close();
	    //System.out.println("returning tracked movies");
	    return movies;
	  }
	
	public boolean hasMovie(Movie movie) {
		Cursor cursor = database.query(DATABASE_TABLE, columns, KEY_MOVIEID + " = " + movie.getId(), null, null, null, null);
		boolean exist = cursor.moveToFirst();
		cursor.close();
		//System.out.println(movie.getTitle() + " with id: " + movie.getId() + " in database? " + exist);
		return exist;
	}
	
	public boolean isTrackingMovie(Movie movie) {
		Cursor cursor = database.query(DATABASE_TABLE, columns, KEY_MOVIEID + " = " + movie.getId() + " AND " + KEY_TRACKED + " = 1", null, null, null, null);
		boolean tracking = cursor.moveToFirst();
		cursor.close();
		//System.out.println(movie.getTitle() + " with id: " + movie.getId() + " in database? " + exist);
		return tracking;
	}
	
	private Movie cursorToMovie(Cursor cursor) {
		String id = cursor.getString(cursor.getColumnIndex(KEY_MOVIEID));
		String title = cursor.getString(cursor.getColumnIndex(KEY_MOVIETITLE));
		String theater = cursor.getString(cursor.getColumnIndex(KEY_THEATER));
		String dvd = cursor.getString(cursor.getColumnIndex(KEY_DVD));
		String profile_url = cursor.getString(cursor.getColumnIndex(KEY_PROFILE));
		String detailed_url = cursor.getString(cursor.getColumnIndex(KEY_DETAILED));
		boolean tracked = (cursor.getInt(cursor.getColumnIndex(KEY_TRACKED)) == 1);
		int count = cursor.getInt(cursor.getColumnIndex(KEY_TRACKCOUNT));
		
		Movie m = new Movie(title, id, theater, dvd, profile_url, detailed_url, tracked, count);
		return m;
	}

}
