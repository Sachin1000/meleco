/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.smarthome;


import com.google.android.gms.gcm.GoogleCloudMessaging;
 
import android.app.Activity;
import android.app.AlarmManager;
import android.app.KeyguardManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.KeyguardManager.KeyguardLock;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

/**
 * This {@code IntentService} does the actual handling of the GCM message.
 * {@code GcmBroadcastReceiver} (a {@code WakefulBroadcastReceiver}) holds a
 * partial wake lock for this service while the service does its work. When the
 * service is finished, it calls {@code completeWakefulIntent()} to release the
 * wake lock.
 */

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.example.smarthome.GCMBaseIntentService;
import com.example.smarthome.GCMRegistrar;

public class GCMIntentService extends GCMBaseIntentService {
   public static final int NOTIFICATION_ID = 1;
   private static NotificationManager mNotificationManager;
   NotificationCompat.Builder builder;
   MessageInfo Info;

   @Override
   protected void onError(Context arg0, String arg1) {
       Log.e("Registration", "Got an error!");
       Log.e("Registration", arg0.toString() + arg1.toString());
   } 

   @Override
   protected void onMessage(Context arg0, Intent arg1) {
       Log.i("Registration", "Got a message!");
       Log.i("Registration", arg0.toString() + " " + arg1.toString());
       // Note: this is where you would handle the message and do something in your app.      
       Bundle extras = arg1.getExtras();
       GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
       String messageType = gcm.getMessageType(arg1);

       if (!extras.isEmpty()) {  // has effect of unparcelling Bundle
           if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
               sendNotification("Send error: " + extras.toString(),this);
           } else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType)) {
               sendNotification("Deleted messages on server: " + extras.toString(),this);
           } else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
        	   Log.i(TAG, "Received: " + extras.toString());
        	   try{
        	   PairseMessage(extras.toString());
        	   ParseMessageGCM(extras.toString());
        	   }catch (Exception e){
        	   }
        	       
           }
       }
   }

   @Override
   protected void onRegistered(Context arg0, String arg1) {
       Log.i("Registration", "Just registered!");
       Log.i("Registration", arg0.toString() + arg1.toString());  
       MainActivity.sendNofication(GCMRegistrar.getRegistrationId(this),"new_id");
       // This is where you need to call your server to record the device toekn and registration id.
   }

   @Override
   protected void onUnregistered(Context arg0, String arg1) {
   }
   
   static void sendNotification(String msg, Context context) {
       mNotificationManager = (NotificationManager)
               context.getSystemService(Context.NOTIFICATION_SERVICE);
 
       PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
               new Intent(context, MainActivity.class), 0);

       NotificationCompat.Builder mBuilder =
               new NotificationCompat.Builder(context)
       .setSmallIcon(R.drawable.ic_cloud)
       .setContentTitle("SmartHome Notification")
       .setStyle(new NotificationCompat.BigTextStyle()
       .bigText(msg))
       .setContentText(msg);

       mBuilder.setContentIntent(contentIntent);
       mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
   }
   
   public void ParseMessageGCM(String string) {
	   Log.v("GCM",Info.key);
		if(Info.key.equals("")||Info.key==null) return;
		if(Info.key.equals("update_ip")){
			//luu dia chi ip nay vao bo nho
			Save_ip();
			//sendNotification("update ip: "+Info.ip,this);
		}
		else if(Info.key.equals("update_id")){
			//thay doi id
		}
		if(Info.status.equals("")||Info.status==null) return;
		if(Info.status.equals("red_alert")){
			Save_ip();
			SaveLog(Info.info + " - " + Info.time);
			sendNotification(Info.info + " - " + Info.time,this);
			Bundle sendBundle = new Bundle();
			Intent sendIntent = new Intent();
			sendBundle.putString("info", Info.info + " vào lúc " + Info.time);
			sendIntent.putExtras(sendBundle);
			
			sendIntent.setClass(this, RedAlert.class); 
			PendingIntent pendingIntent = PendingIntent.getActivity(this, 12345, sendIntent, PendingIntent.FLAG_CANCEL_CURRENT); 
			AlarmManager am = (AlarmManager) getSystemService(Activity.ALARM_SERVICE); 
			am.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), pendingIntent);

			Log.v("red_alert","activity alert");
		}
		else if(Info.status.equals("ping")){
			//ping
		}
		else{
			//1 message + data: non-collapse
		}
	}

	private void Save_ip() {
		if(Info.ip==null || Info.ip.equals("")) return;
		String IP = Info.ip.substring(0,Info.ip.length()-1);
		SharedPreferences Url = getSharedPreferences("URLSave", 0);
		SharedPreferences.Editor editor = Url.edit();
		editor.putString("URL2", IP);
		editor.commit();
		
		SharedPreferences Security = getSharedPreferences("Security", 0);
		SharedPreferences.Editor Seditor = Security.edit();
		Seditor.putString("security",Info.security);
		Seditor.commit();
		Log.v("saveip","Save");
		Intent i = new Intent("com.example.smarthome").putExtra("message", "Save_Ip");
		sendBroadcast(i);
	}
	private void SaveLog(String info){
		SharedPreferences Url = getSharedPreferences("Security_log", 0);
		int number = Url.getInt("number", 0);
		int i=0;
		SharedPreferences.Editor editor = Url.edit();
		if(number>6){
		    for(i=0;i<6;i++){
		    	editor.putString("log"+i, Url.getString("log"+(i+1),""));
		    }
		    number = i;
		}		
		editor.putString("log"+number, info);
		editor.putInt("number", number+1);
		editor.commit();
	}

	protected void PairseMessage(String mgs) {
		int start = mgs.indexOf("{");
		int end = mgs.indexOf("}"); 
		Info = new MessageInfo();
		mgs = mgs.substring(start, end);
		String[] parseMgs = mgs.split(",");
		for(int i=0;i<parseMgs.length;i++){
			if(parseMgs[i].indexOf("id=")!=-1)
				Info.id=parseMgs[i].substring(4,parseMgs[i].length());
			else if(parseMgs[i].indexOf("ip=")!=-1)
				Info.ip=parseMgs[i].substring(4,parseMgs[i].length());
			else if(parseMgs[i].indexOf("time=")!=-1)
				Info.time=parseMgs[i].substring(6,parseMgs[i].length());
			else if(parseMgs[i].indexOf("collapse_key=")!=-1)
				Info.key=parseMgs[i].substring(14,parseMgs[i].length());
			else if(parseMgs[i].indexOf("info=")!=-1)
				Info.info=parseMgs[i].substring(6,parseMgs[i].length());
			else if(parseMgs[i].indexOf("status" + "=")!=-1)
				Info.status=parseMgs[i].substring(8,parseMgs[i].length());
			else if(parseMgs[i].indexOf("security" + "=")!=-1)
				Info.security=parseMgs[i].substring(10,parseMgs[i].length());
		}
	    
	}
	public class MessageInfo{
		String id;
		String ip;
		String time;
		String key;
		String info;
		String status; 
		String security;
		 
		public void messageInfo(){
			id = ip = time = key = info = status = security = "";
		}
	}

}