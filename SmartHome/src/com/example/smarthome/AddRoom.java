package com.example.smarthome;

import java.util.ArrayList;

import com.example.smarthome.MainActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;

public class AddRoom extends Activity implements OnClickListener {
	private CheckBox phongkhach;
	private CheckBox phongngu;
	private CheckBox phongbep;
	private CheckBox phonglamviec;
	private CheckBox phongtam;
	private CheckBox vuoncay;
	private CheckBox santruoc;
	private CheckBox santhuong;
	private CheckBox hanhlang;
	private CheckBox khongphong;
	private Button chonphong;
	private Button huyphong; 
	private static ArrayList<String> room = new ArrayList<String>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.addroom);
		
		Intent receiveIntent = getIntent();
//		Bundle receiveBundle = receiveIntent.getExtras();
//		room = receiveBundle.getStringArrayList("room");
		Init();
	}

	private void Init() {
		phongkhach = (CheckBox) findViewById(R.id.phongkhach);
		phongngu = (CheckBox) findViewById(R.id.phongngu);
		phongbep = (CheckBox) findViewById(R.id.phongbep);
		phonglamviec = (CheckBox) findViewById(R.id.phonglamviec);
		phongtam = (CheckBox) findViewById(R.id.phongtam);
		vuoncay = (CheckBox) findViewById(R.id.vuoncay);
		santruoc = (CheckBox) findViewById(R.id.santruoc);
		santhuong = (CheckBox) findViewById(R.id.santhuong);
		hanhlang = (CheckBox) findViewById(R.id.hanhlang);
		khongphong = (CheckBox) findViewById(R.id.phongtrong);
		chonphong = (Button) findViewById(R.id.chongphong);
		huyphong = (Button) findViewById(R.id.huyphong);
		
		huyphong.setOnClickListener(this);
		chonphong.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()){
		case R.id.chongphong:
			room.clear();
			checkAddroom();
			send_bundle();
			this.finish();
			break;
		case R.id.huyphong:
			room.clear();
			send_bundle();
			this.finish();
			break;
		}
	}
	
	private void checkAddroom() {
		if(phongkhach.isChecked()) room.add(getString(R.string.phongkhach));
		if(phongngu.isChecked()) room.add(getString(R.string.phongngu)); 
		if(phongbep.isChecked()) room.add(getString(R.string.phongbep));
		if(phonglamviec.isChecked()) room.add(getString(R.string.phonglamviec));
		if(phongtam.isChecked()) room.add(getString(R.string.phongtam));
		if(vuoncay.isChecked()) room.add(getString(R.string.vuonsau));
		if(santruoc.isChecked()) room.add(getString(R.string.vuontruoc));
		if(santhuong.isChecked()) room.add(getString(R.string.santhuongtruoc));
		if(hanhlang.isChecked()) room.add(getString(R.string.hanhlang));	
		if(khongphong.isChecked()) room.add(getString(R.string.phongtrong));
	}

	private void send_bundle() { // gui bundle
		Bundle sendBundle = new Bundle();
		Intent sendIntent = new Intent(AddRoom.this, MainActivity.class);
		sendBundle.putStringArrayList("room", room);
		sendIntent.putExtras(sendBundle);
//		setResult(Activity.RESULT_OK, sendIntent);
		if (getParent() == null) {
			setResult(Activity.RESULT_OK, sendIntent);
		} else {
			getParent().setResult(Activity.RESULT_OK, sendIntent);
		}
	}
}
