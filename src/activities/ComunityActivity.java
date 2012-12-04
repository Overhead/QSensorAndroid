package activities;

import java.util.ArrayList;

import com.example.qsensorapp.R;
import com.example.qsensorapp.R.layout;
import com.example.qsensorapp.R.menu;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;

public class ComunityActivity extends Activity {

	EditText age;
	RadioButton maleButton;
	RadioButton femaleButton;
	ArrayList<String> communityMoviesList = new ArrayList<String>(); 
	ArrayAdapter<String> aa;
	ListView moviesListView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_comunity);
		
		age = (EditText)findViewById(R.id.communityAgeField);
		maleButton = (RadioButton)findViewById(R.id.radioButtonMale);
		femaleButton = (RadioButton)findViewById(R.id.radioButtonFemale);
		
		moviesListView = (ListView) findViewById(R.id.communityMoviesList);
		// set resID to be a specefic layout
		int resID = R.layout.list_item;

		
		// Combine the list to the layout
		aa = new ArrayAdapter<String>(this.getApplicationContext(), resID , communityMoviesList);

		// Sets the adapter into the view
		moviesListView.setAdapter(aa);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_comunity, menu);
		return true;
	}

}
