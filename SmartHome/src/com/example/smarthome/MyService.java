package com.example.smarthome;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.http.entity.StringEntity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.smarthome.MainActivity.CountDownRunner;
import com.example.smarthome.MainActivity.MyTask;
import com.example.smarthome.library.ControlData;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class MyService extends Service{
	String data;
	Intent nfc_intent;
	Tag tag = null;

	private MyDatabase database = new MyDatabase(this);
	private static List<String> groupList;
	private static Map<String, List<Myitems>> roomCollection;
	private static List<Myitems> setdata = new ArrayList<Myitems>();

	String security = new String();
	List<String[]> items = new ArrayList<String[]>();
	String Location;
	String UrlLan;
	String UrlWan;
	String typeURL;
	String Port;
	String URL;
	private static JSONObject json;
	private MyTask myTask;
	private static String KEY_SUCCESS = "success";
	private static String KEY_TAG = "tag";
	private static String KEY_ITEMS = "items";
	private int jsonTry = 1;
	private int timesTry;
	int time=0;
	String status;
	Thread myThread;
	boolean first;
	String auto_security = "";
	String dataHTTP;
	String id = "";
	boolean sendSms = false;
	
	@Override
	public IBinder onBind(Intent intent) {		
		return null;
	}
	
	@Override
    public void onCreate() {
		Log.v("NFC","on Create");
		
		myThread = null;
		Runnable myRunnableThread = new CountDownRunner();
		myThread = new Thread(myRunnableThread);
		myThread.start();
		time=20;first=true;
	}
	
	private void GetdataSave() {
		database.open();
        roomCollection = database.getData();
        groupList=database.Getgroup();
        database.close();
        Log.v("NFC","Get data from database");
        
		SharedPreferences settings = getSharedPreferences("LocationInfo", 0);
		Location = settings.getString("Location", "").toString();
		
		SharedPreferences Url = getSharedPreferences("URLSave", 0);
		UrlLan = Url.getString("URL1", "").toString();
		UrlWan = Url.getString("URL2", "").toString();
		typeURL = Url.getString("typeURL", "").toString();
		Port = Url.getString("Port", "").toString();
		sendSms = Url.getBoolean("sms",false);
		if(UrlLan==null || UrlLan.equals("")) UrlLan="192.168.1.48";
		if(UrlWan==null) UrlWan="";
		if(typeURL==null || typeURL.equals("")) typeURL="Lan";
		if(Port==null || Port.equals("")) Port="8080";
		
		if(typeURL.equals("Lan"))
			URL=UrlLan;
		else URL=UrlWan;
		
		Log.v("resume",typeURL + " - " + URL);
	}
	private void databaseSave() {
		database.remove();
        database.open();
        database.createData(groupList,roomCollection);
        database.close();
        Log.v("NFC","data save to database");
	}
	
	class CountDownRunner implements Runnable {
		// @Override
		public void run() {
			while (!Thread.currentThread().isInterrupted()) {
				try {
					if(time==0){
						stopSelf();//mỗi 1s  chạy biến times()				
					}else{
						time--;
					}
					Log.v("NFC","time: "+time);
					Thread.sleep(1000); // Pause of 1 Second
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				} catch (Exception e) {
				}
			}
		}
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		try {
			nfc_intent = intent.getIntent("nfc_data");
			tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            new NdefReaderTask().execute(tag);
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		Log.v("NFC","tag: " + tag.toString());
	    return START_STICKY;
	} 
	
	private class NdefReaderTask extends AsyncTask<Tag, Void, String> {
	    @Override
	    protected String doInBackground(Tag... params) {
	        Tag tag = params[0];
	        Ndef ndef = Ndef.get(tag);
	        if (ndef == null) {
	            // NDEF is not supported by this Tag.
	            return null;
	        }
	        NdefMessage ndefMessage = ndef.getCachedNdefMessage();
	        NdefRecord[] records = ndefMessage.getRecords();
	        for (NdefRecord ndefRecord : records) {
	            if (ndefRecord.getTnf() == NdefRecord.TNF_WELL_KNOWN && Arrays.equals(ndefRecord.getType(), NdefRecord.RTD_TEXT)) {
	                try {
	                    return readText(ndefRecord);
	                } catch (UnsupportedEncodingException e) {
	                    Log.e("MyService", "Unsupported Encoding", e);
	                }
	            }
	        }
	        return null;
	    }
	    private String readText(NdefRecord record) throws UnsupportedEncodingException {
	        /*
	         * See NFC forum specification for "Text Record Type Definition" at 3.2.1
	         *
	         * http://www.nfc-forum.org/specs/
	         *
	         * bit_7 defines encoding
	         * bit_6 reserved for future use, must be 0
	         * bit_5..0 length of IANA language code
	         */
	        byte[] payload = record.getPayload();
	        // Get the Text Encoding
	        String textEncoding = ((payload[0] & 128) == 0) ? "UTF-8" : "UTF-16";
	        // Get the Language Code
	        int languageCodeLength = payload[0] & 0063;
	        // String languageCode = new String(payload, 1, languageCodeLength, "US-ASCII");
	        // e.g. "en"
	        // Get the Text
	        return new String(payload, languageCodeLength + 1, payload.length - languageCodeLength - 1, textEncoding);
	    }
	    @Override
	    protected void onPostExecute(String result) {
	        if (result != null) {
	        	//thực hiện phân tích lệnh từ NFC
	            Log.v("NFC",result);
	            if(first==true) {
	            	first = false;
	            } else {
	            	Log.v("NFC","Lần thứ 2..");
	            	result = change(result);
	            	Log.v("NFC",result);
	            }
	            GetdataSave();
	            parse(result);
	            
	            databaseSave();
	            //stopSelf();
	        }
	    }
	}
	
	public void parse(String result) {
		items = new ArrayList<String[]>();
		setdata = new ArrayList<Myitems>();
		String[] tmp = result.substring(1, result.length() - 1).split(",");
		for (int i = 0; i < tmp.length; i++) {
			items.add(tmp[i].split("-"));
		}

		security = items.get(0)[1];
		Log.v("parse", "security: " + security);

		for (int i = 1; i < items.size(); i++) {
			List<Myitems> nfc_lists = new ArrayList<Myitems>();
			Myitems nfc_items = new Myitems();// chi so trong nfc la chi so group vs child trong Collection
			int groupIndex = Integer.valueOf(items.get(i)[0].substring(1));
			int childIndex = Integer.valueOf(items.get(i)[1]);
			String value = items.get(i)[2];
			Log.v("parse", groupIndex + "-" + childIndex + "-" + value);
			roomCollection.get(groupList.get(groupIndex)).get(childIndex).value = value;


				nfc_lists = roomCollection.get(groupList.get(groupIndex));
				nfc_items = nfc_lists.get(childIndex);
				nfc_items.value = value;
				if (checkAdd(nfc_items.add) == true) {// nếu được add trước đó
														// -> xóa đi add lại
					for (int j = setdata.size() - 1; j >= 0; j--)
						if (setdata.get(j).add.equals(nfc_items.add))
							setdata.remove(j);
				}
				for (int j = 0; j < nfc_lists.size(); j++) {// add thiết bị vào
															// setdata
					if (nfc_lists.get(j).add.equals(nfc_items.add))
						setdata.add(nfc_lists.get(j));
				}
				sendcommand();
		}

		if (security.equals("OFF") && items.size() == 1) {
			auto_security = "off";
			sendNofication("security");
		} else if (security.equals("ALL")) {
			auto_security = "all";
			sendNofication("security");
		} else if (security.equals("AUTO")) {
			auto_security = "auto";
			sendNofication("security");
		}

	}
	
	public String change(String result) {
		items = new ArrayList<String[]>();
		String[] tmp = result.substring(1,result.length()-1).split(",");	
		for(int i=0;i<tmp.length;i++){
			items.add(tmp[i].split("-"));
		}
		
		result = "[";
		for(int i=0;i<items.size();i++){
			result+=items.get(i)[0]+"-"+items.get(i)[1];
			if(i>0) result+="-"+chaneValue(items.get(i)[2]);
			if(i<items.size()-1) result+=",";
		}
		result += "]";
		
		return result;
	}

	private String chaneValue(String string) {
		if(string.equals("ON"))
			return "OFF";
		else
			return "ON";
	}

	private boolean checkAdd(String add) {
		for(int i=0;i<setdata.size();i++){
			if(setdata.get(i).add.equals(add)) return true;
		}
		return false;
	}
	
	private String CheckRoom(String room,Context context) {
		if(room.equals(context.getString(R.string.phongkhach)) || 
				room.equals(context.getString(R.string.phongngu)) || 
				room.equals(context.getString(R.string.phongbep)) || 
				room.equals(context.getString(R.string.phongtam)) || 
				room.equals(context.getString(R.string.phonglamviec)) || 
				room.equals(context.getString(R.string.hanhlang)) || 
				room.equals(context.getString(R.string.phongtrong)) )
			return "inside";
		else if(room.equals(context.getString(R.string.vuonsau)) || 
				room.equals(context.getString(R.string.vuontruoc)) || 
				room.equals(context.getString(R.string.santhuongtruoc)))
			return "outside";
		else return "inside";
	}
	 

	public void sendcommand() {
		if (NetworkStatus()) {
			status = "control";
			myTask = new MyTask(this);
			myTask.execute();
		} else {
		}
	}
	public boolean NetworkStatus() {
		if(URL.equals("") || Port.equals("")) {
			Toast.makeText(this, "URL:Port unavailable",
					Toast.LENGTH_SHORT).show(); 
			return false;
		}
		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
		if (networkInfo == null) {
			Toast.makeText(this, "network unavailable",
					Toast.LENGTH_LONG).show();
			return false;
		} else{ 
			return true;
		}			
	}
	
	
	public void GCMid(){
		GCMRegistrar.checkDevice(this);
		GCMRegistrar.checkManifest(this);
		id = GCMRegistrar.getRegistrationId(this);
	}
	public void sendNofication(final String Nstatus) {
		new AsyncTask<Void, Void, Void>() {
			@Override
			protected Void doInBackground(Void... arg0) {
				JSONObject json = new JSONObject();
				JSONArray ids = new JSONArray();
				JSONObject data = new JSONObject();
				List<String> registration_ids = new ArrayList<String>();
				registration_ids.add("APA91bEZpFFWQTv3ieIVhitkLdStC-DhjDtkuTUcJGggdgJ0S7VEaekChQBZGyWLWyk3tumRD2fIieY_65R24WlOpDo4RnWEibqa90TMRlUiF0TzwOWDzzBKawEKsNXFj-bS6GKX-Guwchu-SFpWrO3E61GzZObRmw");	
				
				for(int i=0;i<registration_ids.size();i++)
					ids.put(registration_ids.get(i));
				try {
					if(Nstatus.equals("security")){
						data.put("status", Nstatus);
						data.put("info", auto_security);
						data.put("sms", sendSms);
						json.put("time_to_live", 300);			
					}
							
					json.put("registration_ids", ids);
					data.put("id", id);
					json.put("data", data);
					Log.v("json notif",json.toString());
					StringEntity params = new StringEntity(json.toString());	
					HTTP PUSH_NOTIF = new HTTP();
					dataHTTP = PUSH_NOTIF.getPUSHfromUrl("https://android.googleapis.com/gcm/send",params, 1000);
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
				super.onPostExecute(result);
			}
		}.execute(null, null, null);
	}
	
	public class MyTask extends AsyncTask<Void, Void, Void> {
		private Context context;
		public MyTask(Context conText) {
		    this.context = conText;
//		    progressDialog = new ProgressDialog(context);
//	        progressDialog.setCancelable(true);
	    }
		@Override
		protected Void doInBackground(Void... arg0) {
			try{
			ControlData controldata = new ControlData(context);
			int timeout=0;
			if(typeURL.equals("Lan")){ timeout=1000; timesTry=1;}
			else if(typeURL.equals("Wan")) {timeout=2000; timesTry=1;}
			json = controldata.setInfo(setdata,URL,typeURL,Port,timeout,status);
			} catch (Exception e){
			}
			return null;
		}
		  
		@Override
		protected void onPostExecute(Void result) {
			try {
				if (json.getString(KEY_SUCCESS) != null) {
					String res = json.getString(KEY_SUCCESS);
					if (Integer.parseInt(res) == 1) {
						jsonTry = 1;
					} else {
						if (jsonTry < timesTry) {
							jsonTry++;
							myTask = new MyTask(context);
							myTask.execute();
						} else {//Neu gui lai 10 lan nhung van ko thanh cong
							jsonTry = 1;
							Log.v("MyTask","send over 5 times");
						}
					}
				}
			} catch (JSONException e) {
			} catch (Exception e) {
			}
			super.onPostExecute(result);
		}
	}

	@Override
	 public void onDestroy() {
		 Log.v("NFC","onDestroy");
		 myThread.interrupt();
	}

	

}
