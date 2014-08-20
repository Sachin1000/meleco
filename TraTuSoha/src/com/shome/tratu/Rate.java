package com.shome.tratu;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class Rate extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.rate);

		Button rate_now = (Button) findViewById(R.id.rate_now);
		Button rate_later = (Button) findViewById(R.id.rate_later);
		Button rate_never = (Button) findViewById(R.id.rate_never);

		rate_now.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				SharedPreferences Time = getSharedPreferences("timeUse", 0);
				SharedPreferences.Editor editor = Time.edit();
				editor.putInt("timeuse", 51);
				editor.commit();
				
				 Intent intent = new Intent(Intent.ACTION_VIEW);
				 //Try Google play
				 intent.setData(Uri.parse("market://details?id=com.shome.tratu"));
				 startActivity(intent);
				 finish();
			}
		});
		rate_later.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
		rate_never.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				SharedPreferences Time = getSharedPreferences("timeUse", 0);
				SharedPreferences.Editor editor = Time.edit();
				editor.putInt("timeuse", 51);
				editor.commit();
				finish();
			}
		});

	}

}
