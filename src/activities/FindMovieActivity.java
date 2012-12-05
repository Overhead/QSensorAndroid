package activities;

import helpers.StartNewAsyncTask;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import classes.Movie;

import com.example.qsensorapp.R;

public class FindMovieActivity extends Activity {

	EditText movieName;
	Button searchButton;
	List<String> imdbMovies = new ArrayList<String>(); 
	public static List<Movie> imdbMoviesList = new ArrayList<Movie>();
	ArrayAdapter<String> aa;
	ListView imdbMoviesListView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_find_movie);
		
		movieName = (EditText)findViewById(R.id.movieNameTextField);
		searchButton = (Button)findViewById(R.id.search_imdb_button);
		
		// Gets the listview
		imdbMoviesListView = (ListView) findViewById(R.id.imdbMoviesListView);
		// set resID to be a specefic layout
		int resID = R.layout.list_item;

		// Combine the list to the layout
		aa = new ArrayAdapter<String>(this.getApplicationContext(), resID,imdbMovies);

		// Sets the adapter into the view
		imdbMoviesListView.setAdapter(aa);

		aa.notifyDataSetChanged();
		imdbMoviesListView.setTextFilterEnabled(true);
		
		imdbMoviesListView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
				MainActivity.movieName = imdbMoviesList.get(position).getMovieName();
				// When clicked, show a toast with the TextView text
				String text = ((TextView) view).getText().toString();
				Toast toast = Toast.makeText(getApplicationContext(),"Clicked on "+position + " : " + text, Toast.LENGTH_SHORT);
				toast.show();
				
				Intent myIntent = new Intent(view.getContext(), NewMovieActivity.class);
				myIntent.putExtra("IMDBID", imdbMoviesList.get(position).getImdbId());
	    	    startActivity(myIntent);
	    	    finish();
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_find_movie, menu);
		return true;
	}
	
	// Define what each button shall do
		public void onClick(View view) {
			switch (view.getId()) {

			// Start the search on the database for imdb movies
			case R.id.search_imdb_button:
				imdbMovies.clear();
				Toast.makeText(getApplicationContext(), "Searching IMDB",Toast.LENGTH_SHORT).show();
				
				StartNewAsyncTask findMovies = new StartNewAsyncTask(movieName.getText().toString());
				findMovies.execute(3);
			
				//Populate the listview
				for (Movie m : imdbMoviesList)
					imdbMovies.add(m.imdbToString());
	
				aa.notifyDataSetChanged();
				imdbMoviesListView.setAdapter(aa);
				break;

			}

		}

}
