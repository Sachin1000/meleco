package com.example.smarthome;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.entity.StringEntity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.smarthome.MainActivity.CountDownRunner;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.KeyguardManager;
import android.app.KeyguardManager.KeyguardLock;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

public class RedAlert extends Activity {
	Button SecurityEnd;
	TextView SecurityText;
	Ringtone r;
	private MediaPlayer mPlayer;
	Thread myThread = null;
	String info;
	int TimeToStop = 0;
	private final String LOG_TAG = getClass().getSimpleName();
	static List<String> registration_ids = new ArrayList<String>();

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//onAttachedToWindow();
		setContentView(R.layout.redalert);

		SecurityEnd = (Button) findViewById(R.id.SecurityEnd);
		SecurityText = (TextView) findViewById(R.id.SecurityText);
		Intent receiveIntent = getIntent();
		Bundle receiveBundle = receiveIntent.getExtras();
		info = receiveBundle.getString("info");
		SecurityText.setText(info);
		
		SharedPreferences settings = getSharedPreferences("Id_server", 0);
		int number = settings.getInt("number", 0);
		Log.v("red_alert","size = "+number);
		for(int i=0;i<number;i++){
			String ids = settings.getString("ids"+i, "");
			registration_ids.add(ids);
			Log.v("red_alert",registration_ids.get(i));
		}

		SecurityEnd.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				String status = "off_alert";
				
				GCMRegistrar.checkDevice(getBaseContext());
				GCMRegistrar.checkManifest(getBaseContext());
				String id = GCMRegistrar.getRegistrationId(getBaseContext());
				Log.v("red_alert","id = "+id);
            	sendNofication(id,status);
            	Log.v("red_alert","stop alert");
            	
				mPlayer.stop();
				myThread.interrupt();
				
				finish();
			}
		});
		playSound(this, getAlarmUri());
		Runnable myRunnableThread = new CountDownRunner();
		myThread = new Thread(myRunnableThread);
		myThread.start();
	}

	private void playSound(Context context, Uri alert) {
		mPlayer = new MediaPlayer();
		try { // unlock screen
			KeyguardManager km = (KeyguardManager) context
					.getSystemService(Context.KEYGUARD_SERVICE);
			final KeyguardManager.KeyguardLock kl = km
					.newKeyguardLock("MyKeyguardLock");
			kl.disableKeyguard();
			PowerManager pm = (PowerManager) context
					.getSystemService(Context.POWER_SERVICE);
			WakeLock wakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK
					| PowerManager.ACQUIRE_CAUSES_WAKEUP
					| PowerManager.ON_AFTER_RELEASE, "MyWakeLock");
			wakeLock.acquire(); // start alarm
			mPlayer.setDataSource(context, alert);
			final AudioManager audioManager = (AudioManager) context
					.getSystemService(Context.AUDIO_SERVICE);
			if (audioManager.getStreamVolume(AudioManager.STREAM_ALARM) != 0) {
				mPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
				mPlayer.prepare();
				mPlayer.start();
			}
		} catch (IOException e) {
			System.out.println("Unable to play!");
		}
	} // Get an alarm sound. private

	Uri getAlarmUri() {
		Uri alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
		if (alert == null) {
			alert = RingtoneManager
					.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
			if (alert == null) {
				alert = RingtoneManager
						.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
			}
		}
		return alert;
	}
	
	private void time() {
		new AsyncTask<Void, Void, Void>() {
			@SuppressLint("SimpleDateFormat")
			protected Void doInBackground(Void... params) {
				try {
					Vibrator vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
					vibe.vibrate(500);
					if(TimeToStop++==30){
						mPlayer.stop();
						myThread.interrupt();
						finish();
					}
				} catch (Exception e) {
				}
				return null;
			}
		}.execute(null, null, null);
	}

	class CountDownRunner implements Runnable {
		// @Override
		public void run() {
			while (!Thread.currentThread().isInterrupted()) {
				try {
					time();
					Thread.sleep(1000); // Pause of 60 Second
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				} catch (Exception e) {
				}
			}
			
		}
	}
	
	public static void sendNofication(final String Myid, final String status) {
		new AsyncTask<Void, Void, Void>() {
			@Override
			protected Void doInBackground(Void... arg0) {
				JSONObject json = new JSONObject();
				JSONArray ids = new JSONArray();
				JSONObject data = new JSONObject();	
				for(int i=0;i<registration_ids.size();i++)
					ids.put(registration_ids.get(i));
				try {
					if(status.equals("off_alert"))
						data.put("status", "off_alert");
					json.put("registration_ids", ids);
					data.put("id", Myid);
					json.put("data", data);
					Log.v("json notif",json.toString());
					StringEntity params = new StringEntity(json.toString());	
					HTTP PUSH_NOTIF = new HTTP();
					String dataHTTP = PUSH_NOTIF.getPUSHfromUrl("https://android.googleapis.com/gcm/send",params, 1000);
				} catch (JSONException e) {
					e.printStackTrace();
				} catch (UnsupportedEncodingException e) { 
					e.printStackTrace();
				} catch (Exception e) { 
					e.printStackTrace();
				}
				return null;
			}

			@Override
			protected void onPostExecute(Void result) {
				Log.v("send notification","Da gui notification");
				super.onPostExecute(result);
			}
		}.execute(null, null, null);
	}

}
