package com.example.qsensorapp;

import java.util.ArrayList;

import classes.Movie;

import com.example.qsensorapp.R;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class ShowMoviesActivity extends Activity {

	ArrayList<String> moviesList = new ArrayList<String>(); 
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

		//Add items to list view 
		for(Movie m : MainActivity.moviesList)
			moviesList.add(m.toString());
		
		aa.notifyDataSetChanged();
		moviesListView.setTextFilterEnabled(true);
		
		moviesListView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
				
				// When clicked, show a toast with the TextView text
				String text = ((TextView) view).getText().toString();
				Toast toast = Toast.makeText(getApplicationContext(),"Clicked on: " + text, Toast.LENGTH_SHORT);
				toast.show();
			}
		});

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
