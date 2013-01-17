package activities;

import helpers.StartNewAsyncTask;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
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
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
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
	/**
	 * set to true will this create a thread which will every 10ms add a new value to the graphview. No Bluetooth needed
	 */
	private static final boolean DEBUG_GRAPH = false; //set true to create a new thread which will 

	/**
	 * ACTION for the broadcast event to add new data to the graph
	 */
	private static final String EDA_MESSAGE_ACTION = "qsensor.newmovieactivity.action.eda_broadcast_message_action";

	/**
	 * String indicates the content in the extras of a created broadcast event
	 * The broadcast has here stored the time of an eda event (double) 
	 */
	private static final String EDA_BROADCAST_TIME = "qsensor.newmovieactivity.broadcastextras.eda_time";
	/**
	 * String indicates the content in the extras of a created broadcast event
	 * The broadcast has here stored the eda value measured in an eda event (double) 
	 */
	private static final String EDA_BROADCAST_EDA = "qsensor.newmovieactivity.broadcastextras.eda_itself";
	/**
	 * String indicates the content in the extras of a created broadcast event
	 * The broadcast has here stored here a boolean indicating if the graph should scroll to the end or not
	 */
	private static final String EDA_BROADCAST_SCROLL = "qsensor.newmovieactivity.broadcastextras.eda_scroll_to_graph_end";

	/**
	 * String indicates the content in the extras of a created broadcast event
	 * The broadcast has in this boolean value stored if it is the first appeared eda event (adds and setup a new graphview)  
	 */
	private static final String EDA_BROADCAST_ISFIRST = "qsensor.newmovieactivity.broadcastextras.is_first_eda";


	TextView movieName;
	Button recordButton;
	volatile boolean recording;
	private DBAdapter database;
	final int DISCOVERY_REQUEST = 0;
	QSensorBTDevice qSensor;
	final String QSensorName = "affectivaQ-v2-7d5c";
	BluetoothAdapter bluetooth;
	BroadcastReceiver discoveryMonitor;
	BroadcastReceiver discoveryResult;
	volatile GraphView graphView;
	double averageEda = 0;
	double averageBaseEDA = 0;
	LinkedList<Emotion> movieEmotions;
	String imdbMovieId;
	int productionYear;
	private volatile GraphViewSeries series;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_movie);

		// Initializes buttons
		recordButton = (Button) findViewById(R.id.record_movie_button);

		movieName = (TextView) findViewById(R.id.movieNameTextField);
		movieEmotions = new LinkedList<Emotion>();
		bluetooth = BluetoothAdapter.getDefaultAdapter();

		//add a graph view
		graphView = new LineGraphView(this, "EDA Values");
		graphView.setScrollable(true);
		graphView.setViewPort(1, 100);
		graphView.setScalable(true);


		LinearLayout layout = (LinearLayout) findViewById(R.id.linearLGraph);
		layout.addView(graphView);

		//(re)-caluclate the movie name
		String mainMovieName = "";
		MainActivity currentMain = MainActivity.getCurrentMainActivity();
		if (currentMain != null) {
			mainMovieName = currentMain.getMovieName();
		}

		movieName.setText(mainMovieName);
		Bundle intent = getIntent().getExtras();
		imdbMovieId = intent.getString("IMDBID");
		productionYear = intent.getInt("YEAR");

		registerReceiver(mHandleMessageReceiver,new IntentFilter(EDA_MESSAGE_ACTION));

		if(DEBUG_GRAPH) {
			startGraphDebug();
		}
	}

	/**
	 * started if DEBUG_GRAPH is set to true
	 * This will create a thread that broadcasts 300 generated data every 100ms to update the graph
	 */
	private void startGraphDebug() {
		// create the thread
		new Thread(new Runnable() {

			@Override
			public void run() {
				for (int i=0; i<300; i++) {
					NewMovieActivity.broadcastEDA(getApplicationContext(), i==0, i+5, i%2==0?i*5:i, true);
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {

						e.printStackTrace();
					}
				}
			}
		}).start();

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
			Intent newIntent = new Intent(getApplicationContext(),
					SettingsActivity.class);
			startActivity(newIntent);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	// Define what each button shall do
	public void onClick(View view) {
		switch (view.getId()) {

		// Start the recording of the movie
		case R.id.record_movie_button:

			// Can't start recording if there is no movie name set
			if (movieName.getText().toString().equals(""))
				Toast.makeText(getApplicationContext(), "Add a movie name",
						Toast.LENGTH_SHORT).show();
			else {
				if (!recording) {
					StartbluetoothConnection();
				} else {
					// When not recording stop bluetooth and send movie to
					// database
					stopBluetoothConnection();
					recording = false;
					SendMovieToDatabase();
					Toast.makeText(getApplicationContext(), "Movie added",
							Toast.LENGTH_SHORT).show();
				}
			}
			break;

			// Can't exit activity if recording is active
		case R.id.new_movie_back_button:
			if (recording)
				Toast.makeText(getApplicationContext(),
						"You have to stop recording before you can exit",
						Toast.LENGTH_SHORT).show();
			else
				finish();
			break;
		}

	}

	/**
	 * Override the back key button so that if there is a recording active, you
	 * can't exit the activity
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (recording) {
				Toast.makeText(getApplicationContext(),
						"You have to stop recording before you can exit",
						Toast.LENGTH_SHORT).show();
				return false;
			} else
				finish();
		}
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * Starts a request and gives the user a dialog box that the application
	 * request to use bluetooth
	 */
	public void StartbluetoothConnection() {
		startActivityForResult(new Intent(
				BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE),
				DISCOVERY_REQUEST);
	}

	/**
	 * Stops the bluetooth connection
	 */
	public void stopBluetoothConnection() {

		if (BluetoothAdapter.getDefaultAdapter().isEnabled()) {
			Toast.makeText(getApplicationContext(), "Turned bluetooth off",
					Toast.LENGTH_SHORT).show();
			bluetooth.disable();

			recordButton.setText("Start recording");
		}
	}

	/**
	 * Override the ActivityResult, used to handle the result of turning on
	 * bluetooth
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		switch (requestCode) {
		case DISCOVERY_REQUEST:

			int duration = resultCode;
			boolean bluetoothActivated = duration > 0;

			// If the user choose to activate bluetooth, start discovering of
			// qsensor
			if (bluetoothActivated) {
				MainActivity currentMain = MainActivity
						.getCurrentMainActivity();
				if (currentMain != null) {
					currentMain.setMovieName(movieName.getText().toString());
				}
				discover();
			}
		}

	}

	@Override
	protected void onDestroy() {
		// deregister the receiver
		unregisterReceiver(mHandleMessageReceiver);
		super.onDestroy();
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
							"Discovery Completed...", Toast.LENGTH_SHORT)
							.show();

					// Stop the discovery process, is needed to be able to
					// connect
					bluetooth.cancelDiscovery();

					// Unregisters the discovery recievers after it is done
					unregisterReceiver(discoveryMonitor);
					unregisterReceiver(discoveryResult);

					// Set up connection to QSensor
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
				Toast.makeText(
						getApplicationContext(),
						"Discovered: " + remoteDeviceName + "\nAddress: "
								+ remoteDevice, Toast.LENGTH_SHORT).show();

				// Create QSensor object if the QSensor given by name is found
				// in the discovery process
				if (remoteDeviceName.toString().equalsIgnoreCase(QSensorName))
					qSensor = new QSensorBTDevice(remoteDeviceName,
							remoteDevice.getAddress());
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

		// If the QSensor object, that will be created in the discovery process
		// is null, the sensor was not found
		if (qSensor != null) {
			try {

				Toast.makeText(
						getApplicationContext(),
						"Connected to: " + qSensor.getName() + "\nAddress: "
								+ qSensor.getAddress(), Toast.LENGTH_SHORT)
								.show();

				// BluetoothAdapter bluetooth =
				// BluetoothAdapter.getDefaultAdapter();

				// Get the QSensor device
				BluetoothDevice device = bluetooth.getRemoteDevice(qSensor
						.getAddress());

				// Initiate the connection to device
				Method m = device.getClass().getMethod("createRfcommSocket",
						new Class[] { int.class });
				BluetoothSocket clientSocket = (BluetoothSocket) m.invoke(
						device, 1);

				// Connect to the sensor
				clientSocket.connect();

				// When connected, start data transfer
				if (clientSocket.isConnected()) {
					receiveQSensorData(clientSocket);
				}

			} catch (Exception e) {
				// If connection failed, disable bluetooth
				bluetooth.disable();
				Toast.makeText(getApplicationContext(),
						"Connection to " + qSensor.getName() + " failed.",
						Toast.LENGTH_SHORT).show();
				Log.d("BLUETOOTH", e.getMessage());
			}
		} else {

			// If the Qsensor object is null
			Toast.makeText(getApplicationContext(), "Qsensor not found",
					Toast.LENGTH_SHORT).show();
			bluetooth.disable();
		}
	}

	/**
	 * Method that takes the QSensor client, and start receiving the data it
	 * transmits
	 * 
	 * @throws IOException
	 */
	public void receiveQSensorData(BluetoothSocket clientSocket)
			throws IOException {

		// Creates a new thread for receiving data from QSensor
		final BluetoothSocket client = clientSocket;
		Thread readQsensorData = new Thread(new Runnable() {
			public void run() {
				InputStream input;
				try {
					input = client.getInputStream();
					Scanner scan = new Scanner(input);
					String line;
					int counter = 1;

					double timeElapsed;
					double startTime = 0;
					while (client.isConnected() && scan.hasNextLine()) {
						line = scan.nextLine();
						String[] results = line.split(",\\s*");

						Log.i("QSensor", line);


						double eda = Double	.parseDouble(results[6]);


						if (counter <= 20) {
							// Get a base EDA value
							averageBaseEDA += eda;
						} else {
							boolean first = false;
							// add
							if (counter == 21) {
								startTime = SystemClock.elapsedRealtime();
								first=true;
							}

							timeElapsed = (SystemClock.elapsedRealtime() - startTime) / 1000;
							final Emotion emo = new Emotion(eda, timeElapsed);
							movieEmotions.add(emo);
							averageEda += eda;
							boolean scroll = emo.getTime()>100;

							//update the graphview
							if (first) {
								//first eda element. add a new serie to the graphview
								NewMovieActivity.broadcastEDA(getApplicationContext(), true, emo.getTime(), emo.getEDA(), scroll);
							} else {
								// broadcast the eda to update the graph
								NewMovieActivity.broadcastEDA(getApplicationContext(), false, emo.getTime(), emo.getEDA(), scroll);
							}

						}

						counter++;
					}
				} catch (IOException e) {
					e.printStackTrace();
				}

			}
		});
		readQsensorData.start();

		recordButton.setText("Stop recording");
		recording = true;
	}

	/**
	 * Method that send the recorded data on a movie to the PhpAdmin Database
	 * aswell as the phones database
	 * 
	 */
	public void SendMovieToDatabase() {

		averageBaseEDA /= 20;
		averageEda /= movieEmotions.size();
		double movieEDA = 0;
		movieEDA = (averageEda - averageBaseEDA);
		Log.i("SensorResult", "EmotionList size: " + movieEmotions.size());
		Log.i("SensorResult", "AverageMovie: " + averageEda);
		Log.i("SensorResult", "AverageBase: " + averageBaseEDA);
		Log.i("SensorResult", "Sending EDA to DB: " + movieEDA);

		MainActivity currentMain = MainActivity.getCurrentMainActivity();
		String mainMovieName = "";
		String mainGender = "";
		String mainAge = "";
		if (currentMain != null) {
			mainMovieName = currentMain.getMovieName();
			mainGender = currentMain.getGender();
			mainAge = currentMain.getAge();
		}
		Movie m = new Movie(imdbMovieId, mainMovieName, mainGender, mainAge,
				movieEDA, productionYear);
		database = new DBAdapter(this);
		database.open();

		database.insertEntry(m);

		// Refresh the list in the "My Movies" page
		if (!database.getAllMovies().isEmpty()) {
			if (currentMain != null) {
				currentMain.setMovieList(database.getAllMovies());
			}
		}

		database.close();

		// Start a new AsyncTask that sends movie to PhpAdmin database
		StartNewAsyncTask sendMovie = new StartNewAsyncTask(m);
		sendMovie.execute(1);

		// Reset AverageEda
		averageEda = 0;
		averageBaseEDA = 0;
	}

	/**
	 * this broadcast receiver updates the graph when a new eda event is received
	 */
	private final BroadcastReceiver mHandleMessageReceiver = new BroadcastReceiver() {
		private double startTime;

		@Override
		public void onReceive(Context context, Intent intent) {
			Bundle extras = intent.getExtras();
			double time = extras.getDouble(EDA_BROADCAST_TIME);
			double eda = extras.getDouble(EDA_BROADCAST_EDA);
			boolean scroll = extras.getBoolean(EDA_BROADCAST_SCROLL);
			boolean first = extras.getBoolean(EDA_BROADCAST_ISFIRST);

			GraphViewData newData = new GraphViewData(time, eda);

			if (first) {
				GraphViewData[] startArray = new GraphViewData[]{newData};
				NewMovieActivity.this.series = new GraphViewSeries(startArray);
				NewMovieActivity.this.graphView.addSeries(series);
				this.startTime=time;
				NewMovieActivity.this.graphView.setViewPort(time, time+1);
			} else {
				NewMovieActivity.this.series.appendData(newData, scroll);
			}

			//set the viewport always from the first to the last value
			NewMovieActivity.this.graphView.setViewPort(startTime, time-startTime);
		}
	};

	/**
	 * Notifies UI to update the graph
	 *
	 * @param context application's context
	 * @param isfirst boolean if the eda event is the first event appearing. Will be stored in the intents extras field EDA_BROADCAST_ISFIRST
	 * @param time double the time of the eda event. Will be stored in the intents extras field EDA_BROADCAST_TIME
	 * @param eda double the received emotion value for this event. Will be stored in the intents extras field EDA_BROADCAST_EDA
	 * @param scrolltoend boolean if the graph should scroll to the end. Will be stored in the intents extras field EDA_BROADCAST_SCROLL
	 */
	public static void broadcastEDA(Context context, boolean isfirst, double time, double eda, boolean scrolltoend) {
		Intent intent = new Intent(EDA_MESSAGE_ACTION);
		intent.putExtra(EDA_BROADCAST_TIME, time);
		intent.putExtra(EDA_BROADCAST_EDA, eda);
		intent.putExtra(EDA_BROADCAST_SCROLL, scrolltoend);
		intent.putExtra(EDA_BROADCAST_ISFIRST, isfirst);
		context.sendBroadcast(intent);
	}

}
