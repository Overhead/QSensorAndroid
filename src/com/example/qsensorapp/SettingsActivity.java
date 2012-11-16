package com.example.qsensorapp;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class SettingsActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
	}
	
	/**
     * Choices for each button
     * @param view
     */
  //Define what each button shall do
    public void onClick(View view) {
    	switch(view.getId()) {
    	
    	//Start the showBooksActivity when show books button is clicked
    	case R.id.set_settings_button:
    			EditText gender = (EditText)findViewById(R.id.genderTextField);
    			EditText age = (EditText)findViewById(R.id.ageTextField);
    			
    			Toast.makeText(getApplicationContext(), "Saved",
        				Toast.LENGTH_SHORT).show();
    			
    			MainActivity.age = age.getText().toString();
    			MainActivity.gender = gender.getText().toString();
    			finish();
    	     break;
    	
    	 //Start the registerBooksAcitivty when register button is clicked
    	case R.id.settings_back_button:
    		finish();
   	     	break;

    	}
       
    }

}
