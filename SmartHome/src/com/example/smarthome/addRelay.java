package com.example.smarthome;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class addRelay extends Activity {
	private EditText textItems;

	private String additems;
	private int groupPos;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.addrelay);

		Intent receiveIntent = getIntent();
		Bundle receiveBundle = receiveIntent.getExtras();
		additems = receiveBundle.getString("additems");
		groupPos = receiveBundle.getInt("groupPos");
		Init();
	}

	private void Init() {

		textItems = (EditText) findViewById(R.id.textItems);
		textItems.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(View v, int keycode, KeyEvent arg2) {
				String text;
				Log.v("key_code","key: "+keycode);
				if (keycode == KeyEvent.KEYCODE_ENTER) {
					text = textItems.getText().toString();
					if (!text.equals("")) {
						additems = text;
						send_bundle();
					}
					return true;
				}
				return false;
			}
		});
		Button Add_Save = (Button) findViewById(R.id.Add_Save);
		Add_Save.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String text;
				text = textItems.getText().toString();
				if (!text.equals("")) {
					additems = text;
					send_bundle();
				}
			}
		});
	}

	private void send_bundle() { // gui bundle
		Bundle sendBundle = new Bundle();
		Intent sendIntent = new Intent(addRelay.this, MainActivity.class);
		sendBundle.putString("items", additems);
		sendBundle.putInt("groupPos", groupPos);
		sendIntent.putExtras(sendBundle);

		if (getParent() == null) {
			setResult(Activity.RESULT_OK, sendIntent);
		} else {
			getParent().setResult(Activity.RESULT_OK, sendIntent);
		}
		this.finish();
	}

}
