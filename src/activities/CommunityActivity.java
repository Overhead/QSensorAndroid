package activities;

import helpers.StartNewAsyncTask;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Toast;
import classes.Movie;

import com.example.qsensorapp.R;

public class CommunityActivity extends Activity {

	EditText ageField;
	RadioButton maleButton;
	RadioButton femaleButton;
	ArrayList<String> communityMoviesInListView = new ArrayList<String>(); 
	ArrayAdapter<String> aa;
	public static List<Movie> communityMoviesList = new ArrayList<Movie>();
	ListView moviesListView;
	String gender= "";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_comunity);
		
		ageField = (EditText)findViewById(R.id.communityAgeField);
		maleButton = (RadioButton)findViewById(R.id.radioButtonMale);
		femaleButton = (RadioButton)findViewById(R.id.radioButtonFemale);
		
		moviesListView = (ListView) findViewById(R.id.communityMoviesList);
		
		// set resID to be a specefic layout
		int resID = R.layout.list_item;

		// Combine the list to the layout
		aa = new ArrayAdapter<String>(this.getApplicationContext(), resID , communityMoviesInListView);

		// Sets the adapter into the view
		moviesListView.setAdapter(aa);
		
		//Set gender when you enter page
		if(maleButton.isChecked())
			gender = "Male";
		else 
			gender = "Female";
		
			
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_comunity, menu);
		return true;
	}
	
	public void onRadioButtonClicked(View view) {
	    // Is the button now checked?
	    boolean checked = ((RadioButton) view).isChecked();
	    
	    // Check which radio button was clicked
	    switch(view.getId()) {
	        case R.id.radioButtonMale:
	            if (checked)
	                gender = "Male";
	            break;
	        case R.id.radioButtonFemale:
	            if (checked)
	                gender = "Female";
	            break;
	    }
	}
	
	// Define what each button shall do
	public void onClick(View view) {
		switch (view.getId()) {

		// Starts the "new movie" intent
		case R.id.communitySearchButton:
		
			//Start a new AsyncTask that gets movies from myphp database online
			if(!ageField.getText().toString().equalsIgnoreCase("")) {
				communityMoviesInListView.clear();
				StartNewAsyncTask findMovies = new StartNewAsyncTask(gender, ageField.getText().toString());
				findMovies.execute(2);
			
				//Populate the listview
				for (Movie m : communityMoviesList)
					communityMoviesInListView.add(m.toString());
	
				aa.notifyDataSetChanged();
			}
			else{
				Toast.makeText(getApplicationContext(), "Type in age",
	    				Toast.LENGTH_SHORT).show();
			}
			break;

		}
	}

}
