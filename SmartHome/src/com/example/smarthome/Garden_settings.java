package com.example.smarthome;


import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

public class Garden_settings extends Activity implements OnCheckedChangeListener {
	String nhietdo = "do C";
	TextView setting_content;
	EditText weatherLocation;
	int[] setdata = {1,1};
	String Location;
	String Info;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.garden_setting);
		SharedPreferences settings = getSharedPreferences("LocationInfo", 0);
		Location = settings.getString("location", "").toString();
		setdata[0] = settings.getInt("setting_data_0",1);
		setdata[1] = settings.getInt("setting_data_1",1);
		Info = settings.getString("Info","");
		
		initilize();
		weatherLocation.setHint(Location);	
		setting_content.setText(Info);
	}

	private void initilize() {

		setting_content = (TextView) findViewById(R.id.setting_content);
		RadioGroup radioGroup1 = (RadioGroup) findViewById(R.id.radioGroup1);
		radioGroup1.setOnCheckedChangeListener(this);
		RadioGroup radioGroup2 = (RadioGroup) findViewById(R.id.radioGroup2);
		radioGroup2.setOnCheckedChangeListener(this);
		weatherLocation = (EditText) findViewById(R.id.weatherLocation);
		weatherLocation.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (event.getAction() == KeyEvent.ACTION_DOWN
						&& keyCode == KeyEvent.KEYCODE_ENTER) {
					String name = weatherLocation.getText().toString();
					if(!name.equals("")){
						Location= name ;
					    weatherLocation.setText("");
					    weatherLocation.setHint(name);
					}
				}
				return false;
			}
		});
		
		if (setdata[0] == 0)
			radioGroup1.check(R.id.radio11);
		if (setdata[1] == 0)
			radioGroup2.check(R.id.radio21);
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		// Lang nghe bundle
		switch (group.getId()) {
		case R.id.radioGroup1:
			if (checkedId == R.id.radio10)
				setdata[0] = 1; // C
			else
				setdata[0] = 0; // F
			break;
		case R.id.radioGroup2:
			if (checkedId == R.id.radio20)
				setdata[1] = 1; // ms
			else
				setdata[1] = 0; // mph
			break;
		}
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		SharedPreferences settings = getSharedPreferences("LocationInfo", 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putInt("setting_data_0", setdata[0]);
		editor.putInt("setting_data_1", setdata[1]);
		editor.putString("location", Location);
		editor.commit();
	}
}
