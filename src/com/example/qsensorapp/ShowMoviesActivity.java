package com.example.qsensorapp;

import java.util.ArrayList;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class ShowMoviesActivity extends Activity {

	ArrayList<String> moviesList = new ArrayList<String>(); // List that shall
															// contain all the
															// books
	ArrayAdapter<String> aa;
	ListView moviesListView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_show_movies);

		// Gets the listview
		moviesListView = (ListView) findViewById(R.id.moviesListView);

		// set resID to be a specefic layout
		int resID = R.layout.list_item;

		// Combine the list to the layout
		aa = new ArrayAdapter<String>(this.getApplicationContext(), resID , moviesList);

		// Sets the adapter into the view
		moviesListView.setAdapter(aa);

	}
	
	// Define what each button shall do
		public void onClick(View view) {
			 Intent myIntent;
			switch (view.getId()) {

			// Start the showBooksActivity when show books button is clicked
			case R.id.show_new_movie_button:				
				  myIntent = new Intent(view.getContext(), NewMovieActivity.class); 
				  startActivityForResult(myIntent, 0);				 
				break;

			// Start the registerBooksAcitivty when register button is clicked
			case R.id.show_movie_back_button:
				finish();
				break;
			}

		}

}
