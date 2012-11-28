package activities;


import java.util.ArrayList;
import java.util.List;

import com.example.qsensorapp.R;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import classes.Movie;
import database.DBAdapter;

public class MainActivity extends Activity {

	private DBAdapter database;
		
	public static String age = "N/A";
	public static String gender = "N/A";
	public static String movieName = "";
	public static List<Movie> moviesList = new ArrayList<Movie>();
	TextView infoField;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        database = new DBAdapter(this);
        database.open();
        
        
       /* Movie m = new Movie("Moras", gender, 20, 40);
        database.insertEntry(m);*/
        
        if(!database.getAllMovies().isEmpty()) {
	        for(Movie m : database.getAllMovies())
	        	moviesList.add(m);
        }
         
        infoField = (TextView)findViewById(R.id.infoTextView);
        infoField.setText(" Age-group: \n Gender: ");
        
        database.close();
    }
    
    @Override
    protected void onResume() {
    	super.onResume();
    	 infoField = (TextView)findViewById(R.id.infoTextView);
         infoField.setText(" Age-group: " + age + "\n Gender: " + gender );
    }
    
    @Override
    protected void onDestroy() {
    	super.onDestroy();
    	moviesList.clear();
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
    
    /**
     * Choices for each button
     * @param view
     */
  //Define what each button shall do
    public void onClick(View view) {
    	Intent myIntent;
    	switch(view.getId()) {
    	
    	//Start the showBooksActivity when show books button is clicked
    	case R.id.new_movie_button:
    		 myIntent = new Intent(view.getContext(), NewMovieActivity.class);
    	     startActivity(myIntent);
    	     break;
    	
    	 //Start the registerBooksAcitivty when register button is clicked
    	case R.id.my_movies_button:
    		myIntent = new Intent(view.getContext(), ShowMoviesActivity.class);
   	     	startActivity(myIntent);
   	     	break;
   	     
   	    //Start the delteBookActivity when the show button(beneath delete) is clicked
    	case R.id.community_button:
    		/*Toast.makeText(getApplicationContext(), "Community",
    				Toast.LENGTH_SHORT).show();*/
   	     	break;
    	}
       
    }
}
