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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

public class Setting extends Activity implements OnCheckedChangeListener,
		OnClickListener {
	private EditText PORT;
	private EditText URL1;
	private EditText URL2;
	private RadioGroup radioGroup1;
	private CheckBox CheckPort;
	private CheckBox sendsms;
	private Button SettingSave;
	private String typeURL = "", Port = "";
	private String Url1 = "", Url2 = "";
	private boolean sendSms = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting);
		Intent receiveIntent = getIntent();
		
		SharedPreferences Url = getSharedPreferences("URLSave", 0);
		Url1 = Url.getString("URL1", "").toString();
		Url2 = Url.getString("URL2", "").toString();
		Port = Url.getString("Port","").toString();
		sendSms = Url.getBoolean("sms", false);
		typeURL = Url.getString("typeURL", "").toString();
		if(typeURL.equals("")) typeURL="Lan";
		initilize();
	}

	private void initilize() {
		PORT = (EditText) findViewById(R.id.Port);
		URL1 = (EditText) findViewById(R.id.URL1);
		URL2 = (EditText) findViewById(R.id.URL2);
		SettingSave = (Button) findViewById(R.id.SettingSave);
		sendsms = (CheckBox) findViewById(R.id.sendsms);
		if(sendSms==true) sendsms.setChecked(true);
		sendsms.setOnClickListener(this);
		CheckPort = (CheckBox) findViewById(R.id.checkPort);
		CheckPort.setOnClickListener(this);
		
		radioGroup1 = (RadioGroup) findViewById(R.id.radioGroup1);
		radioGroup1.setOnCheckedChangeListener(this);
		SettingSave.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Toast.makeText(Setting.this, "Save settings", Toast.LENGTH_SHORT).show();
				check();
				saveURL();
				send_bundle();
				finish();
			}
		});
		// PORT.setOnKeyListener(this);
		URL1.setHint("http://" + Url1);
		URL2.setHint("http://" + Url2);
		if (typeURL.equals("Wan")) {
			URL2.setText(Url2);
			radioGroup1.check(R.id.radio2);
		} else {
			URL1.setText(Url1);
			radioGroup1.check(R.id.radio1);
		}
		if (!Port.equals(""))
			if (Port.equals("8888")){
				PORT.setText("");
				PORT.setEnabled(false);
			}
			else {
				PORT.setText(Port);
				CheckPort.setChecked(true);
			}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.checkPort:
			if (((CheckBox) v).isChecked()) {
				PORT.setEnabled(true);
			} else
				PORT.setEnabled(false);
			PORT.setText("");
			break;
		case R.id.sendsms:
			if(((CheckBox) v).isChecked()) {
				sendSms = true;
			} else {
				sendSms = false;
			}
		}
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		// Lang nghe bundle
		switch (group.getId()) {
		case R.id.radioGroup1:
			if (checkedId == R.id.radio1) {
				typeURL = "Lan"; // Lan
				URL1.setEnabled(true);
				URL2.setEnabled(false);
				URL2.setText("");
			} else {
				typeURL = "Wan"; // Wan
				URL2.setEnabled(true);
				URL1.setEnabled(false);
				URL1.setText("");
			}
			break;
		}
	}

	@Override
	protected void onPause(){
		super.onPause();
		
	}

	private void check() {
		if (CheckPort.isChecked()) {
			Port = PORT.getText().toString();
			if (Port == "")
				Port = "8888";
		} else
			Port = "8888";
		if (typeURL.equals("Lan")) {
			if (!URL1.getText().toString().equals(""))
				Url1 = URL1.getText().toString();
		} else {
			if (!URL2.getText().toString().equals(""))
				Url2 = URL2.getText().toString();
		}
	}
	
	private void saveURL() {
		SharedPreferences Url = getSharedPreferences("URLSave", 0);
		SharedPreferences.Editor editor = Url.edit();
		if (typeURL.equals("Lan"))
			editor.putString("URL1", Url1);
		else if (typeURL.equals("Wan"))
			editor.putString("URL2", Url2);
		editor.putString("typeURL",typeURL);
		editor.putString("Port", Port);
		editor.putBoolean("sms", sendSms);
		editor.commit();
	}
	
	private void send_bundle() { // gui bundle
		Bundle sendBundle = new Bundle();
		Intent sendIntent = new Intent(Setting.this, MainActivity.class);
		sendBundle.putString("typeURL", typeURL);
		sendBundle.putString("URL1", Url1);
		sendBundle.putString("URL2", Url2);
		sendBundle.putString("Port", Port);
		sendIntent.putExtras(sendBundle);
		if (getParent() == null) {
			setResult(Activity.RESULT_OK, sendIntent);
		} else {
			getParent().setResult(Activity.RESULT_OK, sendIntent);
		}
		Log.v("Setting","bundle da gui");
	}

}
