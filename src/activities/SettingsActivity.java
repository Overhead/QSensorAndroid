package activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.qsensorapp.R;

public class SettingsActivity extends Activity {

	private EditText ageField;
	private EditText ipAddress;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		setInterface();
	}

	public void setInterface() {
		final Spinner genderSpinner = (Spinner) findViewById(R.id.genderspinner);

		ArrayAdapter<CharSequence> genderAdapter = ArrayAdapter.createFromResource(this, R.array.gender,
															android.R.layout.simple_spinner_item);

		genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		genderSpinner.setAdapter(genderAdapter);
		genderSpinner.setOnItemSelectedListener(new MyOnItemSelectedListenerGender());

		ageField = (EditText) findViewById(R.id.ageTextField);
		ipAddress = (EditText)findViewById(R.id.serverIpTextField);
		ipAddress.setText(MainActivity.SERVER_IP);
		
		if(!MainActivity.age.toString().equalsIgnoreCase("n/a"))
			ageField.setText(MainActivity.age);
	}

	/**
	 * Choices for each button
	 * 
	 * @param view
	 */
	// Define what each button shall do
	public void onClick(View view) {
		switch (view.getId()) {

		// Save the settings, and close activity if gender and age is set
		case R.id.set_settings_button:
			MainActivity.age = ageField.getText().toString();
			if (MainActivity.gender.equals("N/A")
					|| MainActivity.age.equals("")) {
				Toast.makeText(getApplicationContext(),
						"You have to choose gender and age", Toast.LENGTH_SHORT).show();
			} else {
				if(!ipAddress.getText().toString().equalsIgnoreCase("")) {
					MainActivity.SERVER_IP = ipAddress.getText().toString();
				}
				finish();
			}

			break;

		// Close the activity and return to previous
		case R.id.settings_back_button:
			finish();
			break;

		}

	}

	/**
	 * Method that defines what happens when selecting a gender in the drop down list
	 * @author Tjarb
	 *
	 */
	public class MyOnItemSelectedListenerGender implements OnItemSelectedListener {

		int unitPos = 0;

		public void onItemSelected(AdapterView<?> parent, View view, int pos,
				long id) {

			unitPos = parent.getSelectedItemPosition();

			// Set value of gender
			if (unitPos == 0)
				MainActivity.gender = "MALE";
			else
				MainActivity.gender = "FEMALE";
		}

		public void onNothingSelected(AdapterView<?> arg0) {
			// TODO Auto-generated method stub

		}

	}

}
