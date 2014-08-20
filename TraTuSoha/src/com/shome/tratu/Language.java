package com.shome.tratu;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class Language extends Activity implements OnClickListener {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.language);
		
		Button anhviet = (Button) findViewById(R.id.anhviet);
		Button vietanh = (Button) findViewById(R.id.vietanh);
		Button anhanh = (Button) findViewById(R.id.anhanh);
		Button phapviet = (Button) findViewById(R.id.phapviet);
		Button vietphap = (Button) findViewById(R.id.vietphap);
		Button vietviet = (Button) findViewById(R.id.vietviet);
		Button nhatviet = (Button) findViewById(R.id.nhatviet);
		Button vietnhat = (Button) findViewById(R.id.vietnhat);
		Button anhnhat = (Button) findViewById(R.id.anhnhat);
		Button nhatanh = (Button) findViewById(R.id.nhatanh);
		Button hanviet = (Button) findViewById(R.id.hanviet);
		Button trungviet = (Button) findViewById(R.id.trungviet);
		
		anhviet.setOnClickListener(this);
		vietanh.setOnClickListener(this);
		anhanh.setOnClickListener(this);
		phapviet.setOnClickListener(this);
		vietphap.setOnClickListener(this);
		vietviet.setOnClickListener(this);
		nhatviet.setOnClickListener(this);
		vietnhat.setOnClickListener(this);
		anhnhat.setOnClickListener(this);
		nhatanh.setOnClickListener(this);
		hanviet.setOnClickListener(this);
		trungviet.setOnClickListener(this);
		
	}
	
	public void send_bundle(String language) { // gui bundle
		Bundle sendBundle = new Bundle();
		Intent sendIntent = new Intent(Language.this, MainActivity.class);
		sendBundle.putString("language", language);
		sendIntent.putExtras(sendBundle);
		setResult(Activity.RESULT_OK, sendIntent);
	}

	@Override
	public void onClick(View arg0) {
		String language = null;
		switch(arg0.getId()){
		case R.id.anhviet: language = "anhviet";
			break;
		case R.id.vietanh:language = "vietanh";
			break;
		case R.id.anhanh:language = "anhanh";
			break;
		case R.id.phapviet:language = "phapviet";
			break;
		case R.id.vietphap:language = "vietphap";
			break;
		case R.id.vietviet:language = "vietviet";
			break;
		case R.id.nhatviet:language = "nhatviet";
			break;
		case R.id.vietnhat:language = "vietnhat";
			break;
		case R.id.anhnhat:language = "anhnhat";
			break;
		case R.id.nhatanh:language = "nhatanh";
			break;
		case R.id.hanviet:language = "hanviet";
			break;
		case R.id.trungviet:language = "trungviet";
			break;
		}
		send_bundle(language);
		this.finish();
	}

}
