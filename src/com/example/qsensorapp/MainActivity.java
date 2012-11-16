package com.example.qsensorapp;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	public static String age = "";
	public static String gender = "";
	TextView info;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        info = (TextView)findViewById(R.id.infoTextView);
        info.setText(" Gender: " + gender + "\n Age: " + age );
        
    }
    
    @Override
    protected void onResume() {
    	super.onResume();
    	 info = (TextView)findViewById(R.id.infoTextView);
         info.setText(" Age: " + age + "\n Gender: " + gender );
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
    	     startActivityForResult(myIntent, 0);
    	     break;
    	
    	 //Start the registerBooksAcitivty when register button is clicked
    	case R.id.my_movies_button:
    		myIntent = new Intent(view.getContext(), ShowMoviesActivity.class);
   	     	startActivityForResult(myIntent, 0);
   	     	break;
   	     
   	    //Start the delteBookActivity when the show button(beneath delete) is clicked
    	case R.id.community_button:
    		Toast.makeText(getApplicationContext(), "Community",
    				Toast.LENGTH_SHORT).show();
    		/*myIntent = new Intent(view.getContext(), deleteBookActivity.class);
   	     	startActivityForResult(myIntent, 0);*/
   	     	break;
    	}
       
    }

    
}
