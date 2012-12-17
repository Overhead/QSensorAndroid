package activities;

import helpers.StartNewAsyncTask;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ExecutionException;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import classes.Movie;

import com.example.qsensorapp.R;

public class FindMovieActivity extends Activity {

	EditText movieName;
	Button searchButton;
	List<String> imdbMovies = new ArrayList<String>(); 
	public static volatile List<Movie> imdbMoviesList = new ArrayList<Movie>();
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
				Intent myIntent = new Intent(view.getContext(), NewMovieActivity.class);
				 
				MainActivity main = MainActivity.getCurrentMainActivity();
				if (main != null) {
				
				if(!imdbMoviesList.isEmpty()){
					
					main.setMovieName(imdbMoviesList.get(position).getMovieName());
					myIntent.putExtra("IMDBID", imdbMoviesList.get(position).getImdbId());
					myIntent.putExtra("YEAR", imdbMoviesList.get(position).getProductionYear());
				}
				else {
					main.setMovieName(movieName.getText().toString());
					myIntent.putExtra("IMDBID", "1");
					myIntent.putExtra("YEAR", Calendar.getInstance().get(Calendar.YEAR));
				}
				// When clicked, show a toast with the TextView text
				//String text = ((TextView) view).getText().toString();
				//Toast toast = Toast.makeText(getApplicationContext(),"Clicked on "+position + " : " + text, Toast.LENGTH_SHORT);
				//toast.show();
				startActivity(myIntent);

				}
	    	    finish();
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu, menu);
		return true;
	}
	
	
	/**
     * Choices for the menu
     */
    @Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.settings:
			Intent newIntent = new Intent(getApplicationContext(), SettingsActivity.class);
			startActivity(newIntent);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
    
	// Define what each button shall do
		public void onClick(View view) {
			switch (view.getId()) {

			// Start the search on the database for imdb movies
			case R.id.search_imdb_button:
				imdbMovies.clear();
				
				StartNewAsyncTask findMovies = new StartNewAsyncTask(movieName.getText().toString());
				findMovies.execute(3);
			
				try {
					findMovies.get();
				} catch (InterruptedException e) {
					Log.e("Async", e.getMessage());
					e.printStackTrace();
				} catch (ExecutionException e) {
					Log.e("Async", e.getMessage());
					e.printStackTrace();
				}
				
				//Populate the listview
				if(!imdbMoviesList.isEmpty())
					for (Movie m : imdbMoviesList)
						imdbMovies.add(m.imdbToString());
				else if(!movieName.getText().toString().equalsIgnoreCase("")){
					Toast.makeText(getApplicationContext(), "No connection to server or\n No result with this name",Toast.LENGTH_SHORT).show();
					imdbMovies.add("No movie found\nClick to create movie with name: " + movieName.getText().toString());
				}else
					Toast.makeText(getApplicationContext(), "Enter a movie name",Toast.LENGTH_SHORT).show();
				aa.notifyDataSetChanged();
				imdbMoviesListView.setAdapter(aa);
				break;

			}

		}

}
