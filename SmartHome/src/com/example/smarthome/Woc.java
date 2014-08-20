package com.example.smarthome;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

public class Woc extends Activity {
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.camwoc);
		RadioGroup WocGroup = (RadioGroup) findViewById(R.id.WocGroup);
		WocGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				Intent i = new Intent("com.example.smarthome");
				i.putExtra("message", "woc");
				if(checkedId==R.id.wocLan)
					i.putExtra("wocType", "wocLan");
				else if(checkedId==R.id.wocWan) 
					i.putExtra("wocType", "wocWan");
				else i.putExtra("wocType", "wocOff");
				sendBroadcast(i);
				finish();
			}
		});
	}
}
