package activities;

import helpers.BluetoothManager;
import helpers.StartNewAsyncTask;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Scanner;

import android.app.Activity;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import classes.BTDevice;
import classes.Emotion;
import classes.Movie;

import com.example.qsensorapp.R;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphView.GraphViewData;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.LineGraphView;

import database.DBAdapter;

public class NewMovieActivity extends Activity {

	TextView movieName;
	Button recordButton;
	boolean recording;
	private DBAdapter database;
	final int DISCOVERY_REQUEST = 0;
	BTDevice qSensor;
	final String QSensorName = "affectivaQ-v2-7d5c";
	BroadcastReceiver discoveryMonitor;
	BroadcastReceiver discoveryResult;
	LinearLayout layout;
	GraphView graphView;
	double averageEda = 0;
	double averageBaseEDA = 0;
	LinkedList<Emotion> movieEmotions;
	String imdbMovieId;
	int productionYear;
	BluetoothManager bluetooth;
    BluetoothSocket qSensorClient;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_movie);

		//Initializes buttons
		recordButton = (Button)findViewById(R.id.record_movie_button);
		
		movieName = (TextView)findViewById(R.id.movieNameTextField);
		movieEmotions = new LinkedList<Emotion>();
		//bluetooth = BluetoothAdapter.getDefaultAdapter();
		bluetooth = new BluetoothManager();
		graphView = new LineGraphView(this, "GraphViewDemo");
		layout = (LinearLayout) findViewById(R.id.linearLGraph);
		String mainMovieName = "";
		MainActivity currentMain = MainActivity.getCurrentMainActivity();
		if (currentMain != null) {
			mainMovieName = currentMain.getMovieName();
		}
		
		movieName.setText(mainMovieName);
		Bundle intent = getIntent().getExtras(); 
		imdbMovieId = intent.getString("IMDBID");
		productionYear = intent.getInt("YEAR");
	}

	public void drawGraph(LinkedList<Emotion> emotions){
		try {
			if (!emotions.isEmpty()) {
				double time = 0;
				ArrayList<GraphViewData> grapData = new ArrayList<GraphViewData>();
				for (int i = 0; i < emotions.size(); i++) {
					grapData.add(new GraphViewData(emotions.get(i).getTime(),emotions.get(i).getEDA()));

				}
				time = emotions.get(emotions.size() - 1).getTime();
				Log.i("Graph", "Time: " + time);

				// add data
				graphView.addSeries(new GraphViewSeries(grapData.toArray(new GraphViewData[0])));
				// set view port, start=2, size=40
				if (time < 60)
					graphView.setViewPort(0, time);
				else if (time < 600)
					graphView.setViewPort(0, time / 2);
				else
					graphView.setViewPort(0, time / 4);

				graphView.setScrollable(true);
				layout.addView(graphView);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	// Define what each button shall do
	public void onClick(View view) {
		switch (view.getId()) {

		// Start the recording of the movie
		case R.id.record_movie_button:

			//Can't start recording if there is no movie name set
			if(movieName.getText().toString().equals(""))
				Toast.makeText(getApplicationContext(), "Add a movie name",Toast.LENGTH_SHORT).show();
			else{
				if(!recording){
					bluetooth.startBluetoothDiscovery();
				}
				else{
					//When not recording stop bluetooth and send movie to database
					if(bluetooth.stopBluetoothConnection()) {
					//stopBluetoothConnection();
						recording = false;
						SendMovieToDatabase();
						Toast.makeText(getApplicationContext(), "Movie added",Toast.LENGTH_SHORT).show();
						recordButton.setText("Start recording");
					}else{
						Toast.makeText(getApplicationContext(), "Something went wrong when turning off",Toast.LENGTH_SHORT).show();
					}
				}
			}
			break;

			// Can't exit activity if recording is active
		case R.id.new_movie_back_button:
			if(recording)
				Toast.makeText(getApplicationContext(), "You have to stop recording before you can exit",Toast.LENGTH_SHORT).show();
			else
				finish();
			break;
		}

	}

	/**
	 * Override the back key button so that if there is a recording active, you can't exit the activity
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	    if (keyCode == KeyEvent.KEYCODE_BACK) {
	        if(recording){
	        	Toast.makeText(getApplicationContext(), "You have to stop recording before you can exit",Toast.LENGTH_SHORT).show();
	        	return false;
	        }
	        else
	        	finish();
	    }
	    return super.onKeyDown(keyCode, event);
	}
	

	/**
	 * Override the ActivityResult, used to handle the result of turning on bluetooth
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		switch (requestCode) {
		case DISCOVERY_REQUEST:
			boolean bluetoothActivated = resultCode > 0;
			int duration = resultCode;
			
			//If the user choose to activate bluetooth, start discovering of qsensor
			if (bluetoothActivated){
				MainActivity currentMain = MainActivity.getCurrentMainActivity();
				if (currentMain != null) {
					currentMain.setMovieName(movieName.getText().toString());
					
					qSensor = bluetooth.discoverAndFindDevice(QSensorName);
					if(qSensor != null){
						qSensorClient = bluetooth.connectToDevice(qSensor);
						if(qSensorClient != null){
							try {
								receiveQSensorData(qSensorClient);
							} catch (IOException e) {
								Log.e("QSensorData", "Error in receiving data from BT device");
								e.printStackTrace();
							}
						}
					}
				}
			}
		}

	}	
	
	/**
	 * Method that takes the QSensor client, and start receiving the data it transmits
	 * TODO: Handle QSensor data
	 * @throws IOException 
	 */
	public void receiveQSensorData(BluetoothSocket clientSocket) throws IOException{

		//Creates a new thread for receiving data from QSensor
		final BluetoothSocket client = clientSocket;
		Thread readQsensorData = new Thread(new Runnable() {
		public void run() {
			InputStream input;
			try {
				input = client.getInputStream();
				Scanner scan = new Scanner(input);
				String line;
				int counter = 1;
				Emotion emo;
				double timeElapsed;
				double startTime = 0;
				while (client.isConnected() && scan.hasNextLine()) {
					line = scan.nextLine();
					String[] results = line.split( ",\\s*" );
					
					Log.i("QSensor", line);
					
					
					//Get a base EDA value
					if(counter <= 20)
					{
						averageBaseEDA += Double.parseDouble(results[6]);					
					}else{
						//Get movie EDA, here we can remove the actual Emotion object
						if(counter == 21)
							startTime = SystemClock.elapsedRealtime();
						
						timeElapsed = (SystemClock.elapsedRealtime() - startTime)/1000;
						movieEmotions.add(emo = new Emotion(Double.parseDouble(results[6]), timeElapsed));
						averageEda += Double.parseDouble(results[6]);
					}
					
					counter++;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}});
		readQsensorData.start();

		recordButton.setText("Stop recording");	
		recording = true;
	}

	/**
	 * Method that send the recorded data on a movie to the PhpAdmin Database aswell as the phones database
	 * @param eda
	 */
	public void SendMovieToDatabase(){
		
		averageBaseEDA /= 20;
		averageEda /= movieEmotions.size();
		double movieEDA = 0;
		movieEDA = (averageEda - averageBaseEDA);
		Log.i("SensorResult", "EmotionList size: " + movieEmotions.size());
		Log.i("SensorResult", "AverageMovie: " + averageEda);
		Log.i("SensorResult", "AverageBase: "+ averageBaseEDA);
		Log.i("SensorResult", "Sending EDA to DB: "+movieEDA);
		
		MainActivity currentMain = MainActivity.getCurrentMainActivity();
		String mainMovieName = "";
		String mainGender = "";
		String mainAge = "";
		if (currentMain != null) {
			mainMovieName = currentMain.getMovieName();
			mainGender = currentMain.getGender();
			mainAge = currentMain.getAge();
		}
		Movie m = new Movie(imdbMovieId,mainMovieName, mainGender, mainAge,movieEDA, productionYear);
		database = new DBAdapter(this);
		database.open();

		database.insertEntry(m);

		//Refresh the list in the "My Movies" page
		if(!database.getAllMovies().isEmpty()) {
			if (currentMain != null) {
				currentMain.setMovieList(database.getAllMovies());
			}
		}

		database.close();

		drawGraph(movieEmotions);
		
		//Start a new AsyncTask that sends movie to PhpAdmin database
		StartNewAsyncTask sendMovie = new StartNewAsyncTask(m);
		sendMovie.execute(1);

		//Reset AverageEda
		averageEda = 0;
		averageBaseEDA = 0;
	}

	
	
}
