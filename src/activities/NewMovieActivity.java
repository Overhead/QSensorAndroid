package activities;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
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
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import classes.Emotion;
import classes.Movie;
import classes.QSensorBTDevice;

import com.example.qsensorapp.R;

import database.DBAdapter;

public class NewMovieActivity extends Activity {

	EditText movieName;
	Button recordButton;
	boolean recording;
	double averageEda = 0;
	private DBAdapter database;
	final int DISCOVERY_REQUEST = 0;
	QSensorBTDevice qSensor;
	final String QSensorName = "affectivaQ-v2-7d5c";
	final BluetoothAdapter bluetooth = BluetoothAdapter.getDefaultAdapter();
	BroadcastReceiver discoveryMonitor;
	BroadcastReceiver discoveryResult;

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

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	    if (keyCode == KeyEvent.KEYCODE_BACK) {
	        if(recording)
	        	return false;
	        else
	        	return true;
	    }
	    return super.onKeyDown(keyCode, event);
	}
	
	public void StartbluetoothConnection() {
		startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE),
				DISCOVERY_REQUEST);
	}

	public void stopBluetoothConnection(){

		if(BluetoothAdapter.getDefaultAdapter().isEnabled()) {
			Toast.makeText(getApplicationContext(), "Turned bluetooth off",
					Toast.LENGTH_SHORT).show();
			bluetooth.disable();

			recordButton.setText("Start recording");
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		switch (requestCode) {
		case DISCOVERY_REQUEST:
			boolean bluetoothActivated = resultCode > 0;
			int duration = resultCode;
			if (bluetoothActivated){
				MainActivity.movieName = movieName.getText().toString();
				discover();
			}
		}

	}	
	
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

					//Set up connection
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
				if(remoteDeviceName.toString().equalsIgnoreCase(QSensorName))
					qSensor = new QSensorBTDevice(remoteDeviceName, remoteDevice.getAddress());
				// TODO Do something with the remote Bluetooth Device.
			}
		};
		registerReceiver(discoveryResult, new IntentFilter(
				BluetoothDevice.ACTION_FOUND));
		if (!bluetooth.isDiscovering())
			bluetooth.startDiscovery();
	}

	
	
	public void connectToDevice() {

		if (qSensor != null) {
			try {

				Toast.makeText(
						getApplicationContext(),
						"Connecting to: " + qSensor.getName() + "\nAddress: "
								+ qSensor.getAddress(), Toast.LENGTH_SHORT)
								.show();

				BluetoothAdapter bluetooth = BluetoothAdapter
						.getDefaultAdapter();

				BluetoothDevice device = bluetooth.getRemoteDevice(qSensor.getAddress());

				Method m = device.getClass().getMethod("createRfcommSocket", new Class[] {int.class});
				BluetoothSocket clientSocket = (BluetoothSocket) m.invoke(device, 1);

				clientSocket.connect();
				// TODO Transfer data using the Bluetooth Socket
				if(clientSocket.isConnected()){
					receiveQSensorData(clientSocket);
				}

			} catch (Exception e) {
				bluetooth.disable();
				Toast.makeText(getApplicationContext(),
						"Connection to " + qSensor.getName() + " failed.",
						Toast.LENGTH_SHORT).show();
				Log.d("BLUETOOTH", e.getMessage());
			} 
		} else {
			Toast.makeText(getApplicationContext(), "Qsensor not found",
					Toast.LENGTH_SHORT).show();
			bluetooth.disable();
		}
	}

	/**
	 * TODO: Set up Bluetooth pairing to QSensor, and handle data
	 * @throws IOException 
	 */
	public void receiveQSensorData(BluetoothSocket clientSocket) throws IOException{

		final BluetoothSocket client = clientSocket;
		Thread readQsensorData = new Thread(new Runnable() {
		public void run() {
			InputStream input;
			try {
				input = client.getInputStream();
				Scanner scan = new Scanner(input);
				
				while (client.isConnected() && scan.hasNextLine()) {
					String line = scan.nextLine();
		
					Log.i("QSensor", line);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}});
		readQsensorData.start();

		//List<String> qsensorData = new ArrayList<String>();


		recordButton.setText("Stop recording");	
		recording = true;

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
