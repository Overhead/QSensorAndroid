package helpers;

import java.lang.reflect.Method;

import classes.BTDevice;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.widget.Toast;

public class BluetoothManager extends Activity {

	BluetoothAdapter bluetooth;
	BroadcastReceiver discoveryMonitor;
	BroadcastReceiver discoveryResult;
	BTDevice bluetoothDevice;
	final int DISCOVERY_REQUEST = 0;
	
	public BluetoothManager(){
		bluetooth = BluetoothAdapter.getDefaultAdapter();
	}
	
	public void startBluetoothDiscovery(){
		startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE),
				DISCOVERY_REQUEST);
	}
	
	public boolean stopBluetoothConnection(){
		if(BluetoothAdapter.getDefaultAdapter().isEnabled()) {
			Toast.makeText(getApplicationContext(), "Turned bluetooth off",
					Toast.LENGTH_SHORT).show();
			bluetooth.disable();
			return true;
		}else
			return false;
		
	}
	
	/**
	 * Method that discover nearby bluetooth devices
	 */
	public BTDevice discoverAndFindDevice(final String deviceName) {

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
						if(remoteDeviceName.toString().equalsIgnoreCase(deviceName))
							bluetoothDevice = new BTDevice(remoteDeviceName, remoteDevice.getAddress());
					}
				};
				registerReceiver(discoveryResult, new IntentFilter(
						BluetoothDevice.ACTION_FOUND));
				if (!bluetooth.isDiscovering())
					bluetooth.startDiscovery();
		
				
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
				}
			}
		};
		this.registerReceiver(discoveryMonitor, new IntentFilter(
				BluetoothAdapter.ACTION_DISCOVERY_STARTED));
		this.registerReceiver(discoveryMonitor, new IntentFilter(
				BluetoothAdapter.ACTION_DISCOVERY_FINISHED));
		if(bluetoothDevice != null)
			return bluetoothDevice;
		else
			return null;
	}
	
	public BluetoothSocket connectToDevice(BTDevice btDevice){
		//If the QSensor object, that will be created in the discovery process is null, the sensor was not found
				if (bluetoothDevice != null) {
					try {

						Toast.makeText(
								getApplicationContext(),
								"Connecting to: " + btDevice.getName() + "\nAddress: "
										+ btDevice.getAddress(), Toast.LENGTH_SHORT)
										.show();


						//Get the QSensor device
						BluetoothDevice device = bluetooth.getRemoteDevice(btDevice.getAddress());

						//Initiate the connection to device
						Method m = device.getClass().getMethod("createRfcommSocket", new Class[] {int.class});
						BluetoothSocket clientSocket = (BluetoothSocket) m.invoke(device, 1);

						//Connect to the sensor
						clientSocket.connect();
						
						return clientSocket;
						/*//When connected, start data transfer
						if(clientSocket.isConnected()){
							receiveQSensorData(clientSocket);
						}*/

					} catch (Exception e) {
						//If connection failed, disable bluetooth
						bluetooth.disable();
						Toast.makeText(getApplicationContext(),
								"Connection to " + btDevice.getName() + " failed.",
								Toast.LENGTH_SHORT).show();
						Log.d("BLUETOOTH", e.getMessage());
					} 
				} else {
					
					//If the Qsensor object is null
					Toast.makeText(getApplicationContext(), "Qsensor not found",
							Toast.LENGTH_SHORT).show();
					bluetooth.disable();
				}
		return null;
	}
	
}
