package helpers;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.Scanner;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;
import classes.Emotion;
import classes.QSensorBTDevice;


/**
 * 
 * @author Jon Martin Mikalsen, Marc Dahlem
 * 
 * This class is able to handle bluetooth connection with a Affectiva QSensor.
 * To use it, create first a QSensorBluetoothManager with the sensors bluetooth name.
 * Secondly add listeners to it. {@link #addQSensorListener(QSensorListener)}
 * Third run the connection with the method {@link #startEmotionBluetoothConnection(Context, BluetoothAdapter)}
 * 
 * Optional: create a BroadcastReceiver and register it for the Intentfilter QSensorBluetoothManager.QSENSOR_CONNECTION_ACTION
 * This broadcast is send if the connection is successfully established 
 */
public class QSensorBluetoothManager {

	/**
	 * EXTRA_NAME indicates the content in broadcast intents extra, when the manager connects to a qsensor.
	 * It contains the name of the connected QSensor as String
	 */
	public static final String EXTRA_NAME = "android.example.com.QSensorBluetoothManager.action.connected_qsensor_name";
	/**
	 * THE ID to use in an intentfilter for a broadcast receiver that wants to be informed about a established connection
	 */
	public static final String QSENSOR_CONNECTION_ACTION = "android.example.com.QSensorBluetoothManager.action.connected";

	private BroadcastReceiver discoveryMonitor;
	private BroadcastReceiver discoveryResult;
	private QSensorBTDevice qSensor;
	private String qSensorName;
	private volatile LinkedList<QSensorListener> listeners;

	/** Constructor for a Bluetooth manager that can be used to read the data from a Affectifa Qsensor.
	 * 
	 * 
	 * @param qSensorName the name of the QSensor, that is used to connect to. Name Example: "affectivaQ-v2-7d5c"
	 */

	public QSensorBluetoothManager(String qSensorName) {
		this.qSensorName = qSensorName;
		this.listeners = new LinkedList<QSensorListener>();
	}

	/** Method will first discover nearby bluetooth devices,
	 * then connect to the qsensor with the name given in the constructor.
	 * It starts watching the connection and will inform all listeners about emotion events if they are appearing. 
	 * 
	 * @param context the context to which the manager should give feedback (TOASTs and BroadcastMessages) (!= null)
	 * @param bluetooth the BluetoothAdapter which has to be asked from the gui to be allowed to discover (!=null)
	 * 
	 * @throws IllegalArgumentException if one of the parameters is null
	 */
	public void startEmotionBluetoothConnection(Context context, final BluetoothAdapter bluetooth) {

		//check if the parameters are not null
		if (context == null || bluetooth == null) {
			throw new IllegalArgumentException();
		}

		// create a Broadcastreceiver for start and stop of search:
		discoveryMonitor = new BroadcastReceiver() {
			String dStarted = BluetoothAdapter.ACTION_DISCOVERY_STARTED;
			String dFinished = BluetoothAdapter.ACTION_DISCOVERY_FINISHED;

			@Override
			public void onReceive(Context context, Intent intent) {

				if (dStarted.equals(intent.getAction())) {
					// Discovery has started.
					Toast.makeText(context,
							"Discovery Started...", Toast.LENGTH_SHORT).show();
				} else if (dFinished.equals(intent.getAction())) {
					// Discovery has completed.
					Toast.makeText(context,
							"Discovery Completed...", Toast.LENGTH_SHORT)
							.show();

					// Stop the discovery process, is needed to be able to
					// connect
					bluetooth.cancelDiscovery();

					// Unregisters the discovery recievers after it is done
					context.unregisterReceiver(discoveryMonitor);
					context.unregisterReceiver(discoveryResult);

					// Set up connection to QSensor
					connectToDevice(context, bluetooth);
				}
			}
		};

		//register receivers on the context to get informed if there is new data
		context.registerReceiver(discoveryMonitor, new IntentFilter(
				BluetoothAdapter.ACTION_DISCOVERY_STARTED));
		context.registerReceiver(discoveryMonitor, new IntentFilter(
				BluetoothAdapter.ACTION_DISCOVERY_FINISHED));

		// Broadcasting for discovered devices:
		discoveryResult = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				// get the device name of the discovered event and show it to the user
				String remoteDeviceName = intent.getStringExtra(BluetoothDevice.EXTRA_NAME);

				BluetoothDevice remoteDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				Toast.makeText(context,
						"Discovered: " + remoteDeviceName + "\nAddress: "
								+ remoteDevice, Toast.LENGTH_SHORT).show();

				// Create QSensor object if the QSensor given by name is found
				// in the discovery process
				if (remoteDeviceName.toString().equalsIgnoreCase(QSensorBluetoothManager.this.qSensorName))
					qSensor = new QSensorBTDevice(remoteDeviceName,	remoteDevice.getAddress());
			}
		};

		//register a receiver for the events on the context sor getting informed if something was found
		context.registerReceiver(discoveryResult, new IntentFilter(
				BluetoothDevice.ACTION_FOUND));

		// start the discovering if it is not yet started
		if (!bluetooth.isDiscovering())
			bluetooth.startDiscovery();
	}

	/**
	 * Method that connects Android phone to the Qsensor
	 */
	private void connectToDevice(Context context, BluetoothAdapter bluetooth) {

		// If the QSensor object, that will be created in the discovery process
		// is null, the sensor was not found
		if (qSensor != null) {
			try {

				Toast.makeText(context,
						"Connected to: " + qSensor.getName() + "\nAddress: "
								+ qSensor.getAddress(), Toast.LENGTH_SHORT)
								.show();

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

				this.broadcastClientConnection(context);

				// When connected, start data transfer
				if (clientSocket.isConnected()) {
					receiveQSensorData(clientSocket, context);
				}

			} catch (Exception e) {
				// If connection failed, disable bluetooth
				bluetooth.disable();
				Toast.makeText(context,
						"Connection to " + qSensor.getName() + " failed.",
						Toast.LENGTH_SHORT).show();
				Log.d("BLUETOOTH", e.getMessage());
			}
		} else {

			// If the Qsensor object is null
			Toast.makeText(context, "Qsensor not found",
					Toast.LENGTH_SHORT).show();
			bluetooth.disable();
		}
	}



	/**
	 * Method that takes the QSensor client, and start receiving the data it
	 * transmits. Sends out notifications on a event arrival
	 * 
	 * @throws IOException
	 */
	private void receiveQSensorData(final BluetoothSocket clientSocket, final Context context) {

		// Creates a new thread for receiving data from QSensor
		Thread readQsensorData = new Thread(new Runnable() {
			public void run() {
				//create the input streams
				InputStream input;
				try {
					input = clientSocket.getInputStream();
					Scanner scan = new Scanner(input);
					String line;

					//read until connection is closed
					while (clientSocket.isConnected() && scan.hasNextLine()) {
						line = scan.nextLine();
						Log.i("QSensor", line);
						// the line consists of 7 fields, like one can see with the qsensor live application on a computer
						String[] results = line.split(",\\s*");

						if (results.length != 7) {
							throw new IOException("Received a broken data line from the QSensor. Reading stopped!");
						}

						//Z-axis,Y-axis,X-axis,Battery,∞Celsius,EDA(uS)

						double z = Double.parseDouble(results[1]);
						double y = Double.parseDouble(results[2]);
						double x = Double.parseDouble(results[3]);
						double battery = Double.parseDouble(results[4]);
						double temperature = Double.parseDouble(results[5]);
						double eda = Double.parseDouble(results[6]);

						//get the time
						double time = SystemClock.elapsedRealtime();
						//create a emotion event and notify all listeners
						Emotion currentEmotion = new Emotion(z, y, x, battery, temperature, eda, time);
						QSensorEvent event = new QSensorEvent(QSensorBluetoothManager.this, currentEmotion);
						QSensorBluetoothManager.this.notifyQSensorEvent(event);
					}
				} catch (IOException e) {
					//error while reading. Show it to the user and log it
					Toast.makeText(context, "Error while reading QSensor data: " + e.getMessage(),
							Toast.LENGTH_SHORT).show();
					Log.e("QSensor","Error while reading QSensor data: ", e);
				} finally {
					// close the socket at the end
					try {
						clientSocket.close();
					} catch (IOException e) {
						//error while closing the socket. Show it to the user and log it
						Toast.makeText(context, "Error while closing the QSensor socket: " + e.getMessage(),
								Toast.LENGTH_SHORT).show();
						Log.e("QSensor","Error while closing the QSensor socket: ", e);
					}
				}

			}
		});
		// start the reading thread
		readQsensorData.start();
	}

	/** Adds a listener to this bluetooth manager
	 * 
	 * @param listener the listener that should be informed if a QSensor event appears
	 */
	public void addQSensorListener(QSensorListener listener ) {
		if (listener != null) {
			listeners.add(listener);
		}
	}


	/** removes a listener from this bluetooth manager
	 * 
	 * @param listener the listener that was informed if a QSensor event appears
	 */
	public void removeQSensorListener(QSensorListener listener ) {
		listeners.remove(listener);
	}

	/** informs all listeners about the QSensor event. Threadsafe
	 * 
	 * @param event the QSensor event that appeared
	 */
	private synchronized void notifyQSensorEvent(QSensorEvent event ) {
		for (QSensorListener l : listeners) {
			l.eventAppeared(event);
		}
	}

	/** Method that broadcasts a event to all broadcastreceivers that want to hear, that the qsensor is connected
	 * 
	 * @param context
	 */
	private void broadcastClientConnection(Context context) {

		Intent intent = new Intent(QSENSOR_CONNECTION_ACTION);
		intent.putExtra(EXTRA_NAME, this.qSensorName);
		context.sendBroadcast(intent);

	}
}
