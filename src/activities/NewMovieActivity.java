package activities;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import classes.Emotion;
import classes.Movie;
import classes.ServerCommunication;

import com.example.qsensorapp.R;

import database.DBAdapter;

public class NewMovieActivity extends Activity {

	EditText movieName;
	Button recordButton;
	boolean recording;
	double averageEda = 0;
	private DBAdapter database;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_movie);
		
		recordButton = (Button)findViewById(R.id.record_movie_button);
		movieName = (EditText)findViewById(R.id.movieNameTextField);
	}

	// Define what each button shall do
	public void onClick(View view) {
		switch (view.getId()) {

		// Start the showBooksActivity when show books button is clicked
		case R.id.record_movie_button:
			
			if(movieName.getText().toString().equals(""))
				Toast.makeText(getApplicationContext(), "Add a movie name",Toast.LENGTH_SHORT).show();
			else{
				if(!recording){
		    		StartbluetoothConnection();
		    		while(true){
		    			if(BluetoothAdapter.getDefaultAdapter().isEnabled()){
				    		recordButton.setText("Stop recording");	
				    		recording = true;
				    		MainActivity.movieName = movieName.getText().toString();
				    		receiveQSensorData();
				    		break;
		    			}
		    		}
				}
				else{
					stopBluetoothConnection();
					recording = false;
					
					
					SendMovieToDatabase(averageEda);
					
					Toast.makeText(getApplicationContext(), "Movie added",Toast.LENGTH_SHORT).show();
				}
			}
			break;

		// Start the registerBooksAcitivty when register button is clicked
		case R.id.new_movie_back_button:
			if(recording)
				Toast.makeText(getApplicationContext(), "You have to stop recording before you can exit",Toast.LENGTH_SHORT).show();
			else
				finish();
			break;
		}

	}
	
	public void StartbluetoothConnection() {
		/*BluetoothAdapter bluetooth = BluetoothAdapter.getDefaultAdapter();
		String toastText;
		if (bluetooth.isEnabled()) {
			String address = bluetooth.getAddress();
			bluetooth.setName("QSensorPhone");
			String name = bluetooth.getName();
			toastText = name + " : " + address;
		} else
			toastText = "Bluetooth is not enabled";
		Toast.makeText(this, toastText, Toast.LENGTH_LONG).show();*/
		
    	
			String enableBT = BluetoothAdapter.ACTION_REQUEST_ENABLE;
    		startActivityForResult(new Intent(enableBT), 0);
	}
	
	public void stopBluetoothConnection(){

		if(BluetoothAdapter.getDefaultAdapter().isEnabled()) {
			Toast.makeText(getApplicationContext(), "Turned bluetooth off",
    				Toast.LENGTH_SHORT).show();
			BluetoothAdapter.getDefaultAdapter().disable();

			recordButton.setText("Start recording");
		}
	}
	
	/**
	 * TODO: Set up Bluetooth pairing to QSensor, and handle data
	 */
	public void receiveQSensorData(){
		
		List<Emotion> emotionsForMovie = new ArrayList<Emotion>();
		
		//While we receive data emotion objects is added
		Emotion e = new Emotion(5.0, 0.42, 0.89, 3.98, 36.4, 0.546);
		emotionsForMovie.add(e);
		
		for(Emotion emotion : emotionsForMovie) {
			averageEda += emotion.getEDA();
		}
		
		averageEda /= emotionsForMovie.size();
		
	}
	
	public void SendMovieToDatabase(double eda){
		Movie m = new Movie(MainActivity.movieName, MainActivity.gender, MainActivity.age,averageEda);
		
		database = new DBAdapter(this);
        database.open();
        
        database.insertEntry(m);
        
        if(!database.getAllMovies().isEmpty()) {
        	MainActivity.moviesList.clear();
	        for(Movie movie : database.getAllMovies())
	        	MainActivity.moviesList.add(movie);
        }
        
		database.close();
		
		//TODO: Fix Server ip address before trying to use this
		//ServerCommunication.SendMovieDataToDB(m);
		
		averageEda = 0;
	}

}
