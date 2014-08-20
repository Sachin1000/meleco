package com.example.smarthome;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

public class changeItems extends Activity implements OnClickListener {
	private Integer childpos;
	private Integer grouppos;
	private String nameitems;
	private ImageView bongden;
	private ImageView quat;
	private ImageView loa;
	private ImageView laptop;
	private ImageView tivi;
	private ImageView khoa;
	private ImageView bom;
	private ImageView dieuhoa;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.changeitems);
		Intent receiveIntent = getIntent();
		Bundle receiveBundle = receiveIntent.getExtras();
		childpos = receiveBundle.getInt("childPos");
		grouppos = receiveBundle.getInt("groupPos");
		Init();
	}

	private void Init() {
		bongden = (ImageView) findViewById(R.id.bongden);
		quat = (ImageView) findViewById(R.id.quat);
		loa = (ImageView) findViewById(R.id.loa);
		laptop = (ImageView) findViewById(R.id.laptop);
		tivi = (ImageView) findViewById(R.id.tivi);
		khoa = (ImageView) findViewById(R.id.khoa);
		bom = (ImageView) findViewById(R.id.bom);
		dieuhoa = (ImageView) findViewById(R.id.dieuhoa);

		bongden.setOnClickListener(this);
		quat.setOnClickListener(this);
		loa.setOnClickListener(this);
		laptop.setOnClickListener(this);
		tivi.setOnClickListener(this);
		khoa.setOnClickListener(this);
		bom.setOnClickListener(this);
		dieuhoa.setOnClickListener(this);
	}

	private void send_bundle() { // gui bundle
		Bundle sendBundle = new Bundle();
		Intent sendIntent = new Intent(changeItems.this, MainActivity.class);
		sendBundle.putInt("childPos", childpos);
		sendBundle.putInt("groupPos", grouppos);
		sendBundle.putString("nameItems", nameitems);
		sendIntent.putExtras(sendBundle);
		// setResult(Activity.RESULT_OK, sendIntent);
		if (getParent() == null) {
			setResult(Activity.RESULT_OK, sendIntent);
		} else {
			getParent().setResult(Activity.RESULT_OK, sendIntent);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bongden:
			nameitems = getString(R.string.bongden);
			break;
		case R.id.quat:
			nameitems = getString(R.string.quat);
			break;
		case R.id.loa:
			nameitems = getString(R.string.loa);
			break;
		case R.id.laptop:
			nameitems = getString(R.string.laptop);
			break;
		case R.id.tivi:
			nameitems = getString(R.string.tivi);
			break;
		case R.id.khoa:
			nameitems = getString(R.string.khoa);
			break;
		case R.id.bom:
			nameitems = getString(R.string.bom);
			break;
		case R.id.dieuhoa:
			nameitems = getString(R.string.dieuhoa);
			break;
		}
		send_bundle();
		finish();
	}
}
