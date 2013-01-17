package activities;


import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import classes.Movie;

import com.example.qsensorapp.R;

import database.DBAdapter;

public class MainActivity extends Activity {

	private DBAdapter database;
	
	/**
	 * The age of the current user
	 */
	private String age = "N/A";
	
	/**
	 * Gender of the user
	 */
	private String gender = "N/A";
	
	/**
	 * current movie name
	 */
	private String movieName = "";
	
	/**
	 * Server ip to send the average emotion data to
	 */
	private String SERVER_IP = "";
	
	/**
	 * contains all movies that have been in the local database
	 */
	private List<Movie> moviesList = new ArrayList<Movie>();
	
	/**
	 * the text field where age and gender of the user is shown
	 */
	private TextView infoField;
	
	/**
	 * the last mainactivity. Set and used to transport all values from the old to the new mainactivity
	 */
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
	 *  sets the pixel format to improve the background which is drawn in bad quality on some phones
	 */
	@Override
	public void onAttachedToWindow() {
		super.onAttachedToWindow();
		Window window = getWindow();
		window.setFormat(PixelFormat.RGBA_8888);
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
     * called if a button is clicked
     * @param view the button on which was clicked
     */

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
    
    /** Method used to store the mainactivity and transport values
     * 
     * @return last created main activity
     */
    public static MainActivity getCurrentMainActivity() {
    	return current;
    }
    
    /** 
     * 
     * @return the age set from the user
     */
    public String getAge() {
    	return this.age;
    }
    
    
    /**
     * 
     * @param age age to set in this MainActivity
     */
    public void setAge(String age) {
    	this.age = age;
    }
    
    /**
     * 
     * @return last set serverip
     */
    public String getServerIP() {
    	return this.SERVER_IP;
    }
    
    /**
     * 
     * @param serverIP the new server ip
     */
    public void setServerIP(String serverIP) {
		this.SERVER_IP = serverIP;
	}
    
    /**
     * 
     * @return the last set gender
     */
    public String getGender(){
    	return this.gender;
    }
    
    /**
     * 
     * @param gender the new gender
     */
    public void setGender(String gender) {
    	this.gender = gender;
    }
    
    /**
     * 
     * @return the last set movie name
     */
    public String getMovieName(){
    	return this.movieName;
    }
    
    /**
     * 
     * @param movieName the new movie name
     */
    public void setMovieName(String movieName){
    	this.movieName = movieName;
    }
    
    /**
     * 
     * @return all known movies
     */
    public List<Movie> getMovieList() {
    	return this.moviesList;
    }
    
    /** Called to transport movielist from the old mainactivity
     * 
     * @param newMovieList new movie list
     */
    public void setMovieList(List<Movie> newMovieList) {
    	this.moviesList.clear();
    	for (Movie movie:newMovieList) {
    		this.moviesList.add(movie);
    	}
    }
    
   
}
