package activities;

import helpers.StartNewAsyncTask;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Scanner;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import classes.Emotion;
import classes.Movie;
import classes.QSensorBTDevice;

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
	QSensorBTDevice qSensor;
	final String QSensorName = "affectivaQ-v2-7d5c";
	BluetoothAdapter bluetooth;
	BroadcastReceiver discoveryMonitor;
	BroadcastReceiver discoveryResult;
	LinearLayout layout;
	GraphView graphView;
	double averageEda = 0;
	double averageBaseEDA = 0;
	LinkedList<Emotion> movieEmotions;
	String imdbMovieId;
	int productionYear;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_movie);

		//Initializes buttons
		recordButton = (Button)findViewById(R.id.record_movie_button);
		
		movieName = (TextView)findViewById(R.id.movieNameTextField);
		movieEmotions = new LinkedList<Emotion>();
		bluetooth = BluetoothAdapter.getDefaultAdapter();
		graphView = new LineGraphView(this, "GraphViewDemo");
		layout = (LinearLayout) findViewById(R.id.linearLGraph);
		movieName.setText(MainActivity.movieName);
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
					StartbluetoothConnection();
				}
				else{
					//When not recording stop bluetooth and send movie to database
					stopBluetoothConnection();
					recording = false;
					SendMovieToDatabase();
					Toast.makeText(getApplicationContext(), "Movie added",Toast.LENGTH_SHORT).show();
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
	 * Starts a request and gives the user a dialog box that the application request to use bluetooth
	 */
	public void StartbluetoothConnection() {
		startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE),
				DISCOVERY_REQUEST);
	}

	/**
	 * Stops the bluetooth connection
	 */
	public void stopBluetoothConnection(){

		if(BluetoothAdapter.getDefaultAdapter().isEnabled()) {
			Toast.makeText(getApplicationContext(), "Turned bluetooth off",
					Toast.LENGTH_SHORT).show();
			bluetooth.disable();

			recordButton.setText("Start recording");
		}
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
				MainActivity.movieName = movieName.getText().toString();
				discover();
			}
		}

	}	
	
	/**
	 * Method that discover nearby bluetooth devices
	 */
	private void discover() {

		// Broadcastreceiver for start and stop of search:
		discoveryMonitor = new BroadcastReceiver() {
			String dStarted = BluetoothAdapter.ACTION_DISCOVERY_STARTED;
			String dFinished = BluetoothAdapter.ACTION_DISCOVERY_FINISHED;
			@Override
			public void onReceive(Context context, Intent intent) {
				
				if (dStarted.equals(intent.getAction())) {
					// Discovery has started.
					Toast.makeText(getApplicationContext(),
							"Discovery Started...", Toast.LENGTH_SHORT).show();
				} else if (dFinished.equals(intent.getAction())) {
					// Discovery has completed.
					Toast.makeText(getApplicationContext(),
							"Discovery Completed...", Toast.LENGTH_SHORT).show();
					
					//Stop the discovery process, is needed to be able to connect
					bluetooth.cancelDiscovery();

					//Unregisters the discovery recievers after it is done
					unregisterReceiver(discoveryMonitor);
					unregisterReceiver(discoveryResult);

					//Set up connection to QSensor
					connectToDevice();
				}
			}
		};
		this.registerReceiver(discoveryMonitor, new IntentFilter(
				BluetoothAdapter.ACTION_DISCOVERY_STARTED));
		this.registerReceiver(discoveryMonitor, new IntentFilter(
				BluetoothAdapter.ACTION_DISCOVERY_FINISHED));
		
		// Broadcasting for discovered devices:
		discoveryResult = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				String remoteDeviceName = intent
						.getStringExtra(BluetoothDevice.EXTRA_NAME);
				BluetoothDevice remoteDevice = intent
						.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				Toast.makeText(getApplicationContext(),
						"Discovered: " + remoteDeviceName + "\nAddress: " + remoteDevice, Toast.LENGTH_SHORT)
						.show();
				
				//Create QSensor object if the QSensor given by name is found in the discovery process
				if(remoteDeviceName.toString().equalsIgnoreCase(QSensorName))
					qSensor = new QSensorBTDevice(remoteDeviceName, remoteDevice.getAddress());
			}
		};
		registerReceiver(discoveryResult, new IntentFilter(
				BluetoothDevice.ACTION_FOUND));
		if (!bluetooth.isDiscovering())
			bluetooth.startDiscovery();
	}

	
	/**
	 * Method that connects Android phone to the Qsensor
	 */
	public void connectToDevice() {

		//If the QSensor object, that will be created in the discovery process is null, the sensor was not found
		if (qSensor != null) {
			try {

				Toast.makeText(
						getApplicationContext(),
						"Connecting to: " + qSensor.getName() + "\nAddress: "
								+ qSensor.getAddress(), Toast.LENGTH_SHORT)
								.show();

				//BluetoothAdapter bluetooth = BluetoothAdapter.getDefaultAdapter();

				//Get the QSensor device
				BluetoothDevice device = bluetooth.getRemoteDevice(qSensor.getAddress());

				//Initiate the connection to device
				Method m = device.getClass().getMethod("createRfcommSocket", new Class[] {int.class});
				BluetoothSocket clientSocket = (BluetoothSocket) m.invoke(device, 1);

				//Connect to the sensor
				clientSocket.connect();
				
				//When connected, start data transfer
				if(clientSocket.isConnected()){
					receiveQSensorData(clientSocket);
				}

			} catch (Exception e) {
				//If connection failed, disable bluetooth
				bluetooth.disable();
				Toast.makeText(getApplicationContext(),
						"Connection to " + qSensor.getName() + " failed.",
						Toast.LENGTH_SHORT).show();
				Log.d("BLUETOOTH", e.getMessage());
			} 
		} else {
			
			//If the Qsensor object is null
			Toast.makeText(getApplicationContext(), "Qsensor not found",
					Toast.LENGTH_SHORT).show();
			bluetooth.disable();
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
		
		//TODO fix production year
		Movie m = new Movie(imdbMovieId,MainActivity.movieName, MainActivity.gender, MainActivity.age,movieEDA, productionYear);
		database = new DBAdapter(this);
		database.open();

		database.insertEntry(m);

		//Refresh the lit in the "My Movies" page
		if(!database.getAllMovies().isEmpty()) {
			MainActivity.moviesList.clear();
			for(Movie movie : database.getAllMovies())
				MainActivity.moviesList.add(movie);
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
