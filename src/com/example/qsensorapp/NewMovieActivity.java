package com.example.qsensorapp;

import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.Toast;

public class NewMovieActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_movie);
	}

	// Define what each button shall do
	public void onClick(View view) {
		switch (view.getId()) {

		// Start the showBooksActivity when show books button is clicked
		case R.id.add_movie_button:
			Toast.makeText(getApplicationContext(), "Movie added",Toast.LENGTH_SHORT).show();
			break;

		// Start the registerBooksAcitivty when register button is clicked
		case R.id.new_movie_back_button:
			finish();
			break;
		}

	}

}
