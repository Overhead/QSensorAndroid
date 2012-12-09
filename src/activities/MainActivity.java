package activities;


import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import classes.Movie;

import com.example.qsensorapp.R;

import database.DBAdapter;

public class MainActivity extends Activity {

	private DBAdapter database;
	
	private String age = "N/A";
	private String gender = "N/A";
	private String movieName = "";
	private String SERVER_IP = "";
	private List<Movie> moviesList = new ArrayList<Movie>();
	TextView infoField;
	private static MainActivity current= null;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        //transfer the old values to the new main activity
        MainActivity old = MainActivity.getCurrentMainActivity();
        if (old != null) {
        	this.setAge(old.getAge());
        	this.setGender(old.getGender());
        	this.setMovieName(old.getMovieName());
        	this.setServerIP(old.getServerIP());
        	
        	this.moviesList = old.moviesList;
        }
        
        MainActivity.current=this;
        setContentView(R.layout.activity_main);
        
        database = new DBAdapter(this);
        database.open();
        
        //Update list in myMovies
        if(!database.getAllMovies().isEmpty()) {
	        for(Movie m : database.getAllMovies())
	        	moviesList.add(m);
        }
         
        infoField = (TextView)findViewById(R.id.infoTextView);
        infoField.setText(" Age: \n Gender: ");
        
        database.close();
    }
    /**
     * Handles what will happen when user return to main acticity
     */
    @Override
    protected void onResume() {
    	super.onResume();
    	 infoField = (TextView)findViewById(R.id.infoTextView);
         infoField.setText(" Age: " + age + "\n Gender: " + gender );
    }
    
    /**
     * When application is closed, this method is called
     */
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
    	
    	//Move the user to "new movie" page
    	case R.id.new_movie_button:
    		/* myIntent = new Intent(view.getContext(), NewMovieActivity.class);
    	     startActivity(myIntent);*/
			 myIntent = new Intent(view.getContext(), FindMovieActivity.class);
			 startActivity(myIntent);
    	     break;
    	
    	 //Move the user to "My Movies" page
    	case R.id.my_movies_button:
    		myIntent = new Intent(view.getContext(), ShowMoviesActivity.class);
   	     	startActivity(myIntent);
   	     	break;
   	     
   	    //Does nothing at the moment
    	case R.id.community_button:
    		/*Toast.makeText(getApplicationContext(), "Community",
    				Toast.LENGTH_SHORT).show();*/
    		myIntent = new Intent(view.getContext(), CommunityActivity.class);
   	     	startActivity(myIntent);
   	     	break;
    	}
       
    }
    
    public static MainActivity getCurrentMainActivity() {
    	return current;
    }
    
    public String getAge() {
    	return this.age;
    }
    
    public void setAge(String age) {
    	this.age = age;
    }
    
    public String getServerIP() {
    	return this.SERVER_IP;
    }
    
    public void setServerIP(String serverIP) {
		this.SERVER_IP = serverIP;
	}
    
    public String getGender(){
    	return this.gender;
    }
    
    public void setGender(String gender) {
    	this.gender = gender;
    }
    
    public String getMovieName(){
    	return this.movieName;
    }
    
    public void setMovieName(String movieName){
    	this.movieName = movieName;
    }
    
    public List<Movie> getMovieList() {
    	return this.moviesList;
    }
    
    public void setMovieList(List<Movie> newMovieList) {
    	this.moviesList.clear();
    	for (Movie movie:newMovieList) {
    		this.moviesList.add(movie);
    	}
    }
    
   
}
