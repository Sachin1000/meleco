package com.example.smarthome;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.http.entity.StringEntity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.smarthome.HTTP;
import com.example.smarthome.GCMRegistrar;
import com.example.smarthome.AddRoom;
import com.example.smarthome.MainActivity;
import com.example.smarthome.addRelay;
import com.example.smarthome.changeItems;
import com.example.smarthome.ActionItem;
import com.example.smarthome.MyDatabase;
import com.example.smarthome.Myitems;
import com.example.smarthome.QuickAction;
import com.example.smarthome.weather;
import com.example.smarthome.R;
import com.example.smarthome.Garden_settings;
import com.example.smarthome.Setting;
import com.example.smarthome.library.ControlData;
import com.example.smarthome.library.PairRecognizer;
import com.example.smarthome.MainActivity.CountDownRunner;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.PendingIntent;
import android.app.LauncherActivity.ListItem;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.speech.RecognizerIntent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.view.textservice.SentenceSuggestionsInfo;
import android.widget.ArrayAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.TimePicker.OnTimeChangedListener;
import android.widget.Toast;
import android.widget.ExpandableListView.OnGroupExpandListener;


public class MainActivity extends FragmentActivity {
    private static ArrayList<String> addroom = new ArrayList<String>();
    private MyDatabase database = new MyDatabase(this);
    private static String additems = new String();
    private static List<String> groupList;
    private static Map<String, List<Myitems>> roomCollection;
    private static ExpandableListView expListView;
    private static ExpandableListAdapter expListAdapter;
    private static QuickAction groupQuickAction;
    private static QuickAction itemsQuickAction;
    final int AddRoomCode = 10;
    final int addItemsCode= 20;
    final int changeItemsCode = 30;
    final static int GardenCode = 40;
    final int URLcode = 50;
    final static int RESULT_SPEECH_CODE = 60;
    final int SettingCode = 70;
    private int grouppos;
    private int childpos;
    private List<Myitems> itemsList;
    private static List<Myitems> setdata;
    private int NumberData = 0;
    private static List<String> garden_log;
    private static int[] Time_set = {0,0};
    private static Boolean Time_check = false;
    private static String security = "";
    private String Location;
    private static String status;
	SectionsPagerAdapter mSectionsPagerAdapter;
	ViewPager mViewPager;
	
	private static Context context;
	private static JSONObject json;
	private MyTask myTask;
	private static String KEY_SUCCESS = "success";
	private static String KEY_TAG = "tag";
	private static String KEY_ITEMS = "items";
	private int timeout,timesTry;
	private int jsonTry = 1;
	private static boolean CommandSend = false;
	private static boolean securitySend = false;
	private static boolean security_first=true;
	static RadioGroup security_group;
	private static boolean synData=false;
	private String URL= "", UrlLan = "", UrlWan = "", typeURL = "", Port = "";
	String id;
	
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    static List<String> registration_ids = new ArrayList<String>();
    static String dataHTTP;
    String SENDER_ID = "911748571655";
    static final String TAG = "GCM";
    Thread myThread;
    private ProgressDialog progressDialog;
    private BroadcastReceiver mReceiver;
    
    private static List<String> nfc_data = new ArrayList<String>();
    NfcAdapter adapter;
    PendingIntent pendingIntent;
	IntentFilter writeTagFilters[];
	boolean writeMode=false;
	static Tag mytag;
	Context ctx;
	public static final String MIME_TEXT_PLAIN = "text/plain";	
	Intent nfc_intent;
	String nfc_status = "";
	static boolean tv_lg_channel = false;
	static boolean Speech_Code = false;
	boolean first_update = true;
	static boolean woc_intent = false;
	static String auto_security = "";
	static HomeStatus homeStatus;
	static int temperature = 0;
	static int humidity = 0;
	static boolean sendSms = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		handleIntent(getIntent());
		
		setContentView(R.layout.activity_main);  
		GetdataSave(); //lay giu lieu da luu
        init();
        Home();
        nfc_init();
        Getdata_database();
        groupMenu_init();
        itemsMenu_init();
         
        expListAdapter = new ExpandableListAdapter(
        		this, groupList, roomCollection);

        // Set up the ViewPager with the sections adapter.
		mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);
		mViewPager.setCurrentItem(2);
		
		GCM_init();
		status = "updateinfo_at";
		sendData();		
	}
	
	private void Home(){
		homeStatus = new HomeStatus();
		
		SharedPreferences Url = getSharedPreferences("Security_log", 0);
		int n = Url.getInt("number", 0);
		String Lo = Url.getString("log"+(n-1),"");
		String[] parseLo = Lo.split("-");
		String[] alertLo = parseLo[0].split(":");
		String[] timeLo = parseLo[1].split(" ");

		Date thoiGian = new Date();
		SimpleDateFormat dinhDangThoiGian = new SimpleDateFormat("dd/MM/yyyy");
		String showTime = dinhDangThoiGian.format(thoiGian.getTime()); 

		Log.v("home",timeLo[2] +"---"+ showTime);
		if(showTime.equals(timeLo[2])){
			homeStatus.status = "ko an toàn";
			homeStatus.alert = alertLo[1].substring(1);
		}			
		else {
			homeStatus.status = "An toàn";
			homeStatus.alert = "";
		}
	}
	
	private void nfc_init() {	
	    pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
	    IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
	    tagDetected.addCategory(Intent.CATEGORY_DEFAULT);
	    writeTagFilters = new IntentFilter[] { tagDetected };
	}
	
	private void GetdataSave() {
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
		if(Port==null || Port.equals("")) Port="8888";
		
		if(typeURL.equals("Lan"))
			URL=UrlLan;
		else URL=UrlWan;
		
		SharedPreferences ShareSecurity = getSharedPreferences("Security", 0);
		String Se = ShareSecurity.getString("security","").toString();
		Log.v("main",Se);
		if(Se.equals("auto")){
			security = getString(R.string.baomattudong);
		} else if(Se.equals("all")){
			security = getString(R.string.baomattoanbo);
		} else if(Se.equals("out")){
			security = getString(R.string.baomatngoai);
		} else if(Se.equals("off")){
			security = getString(R.string.khongbaomat);
		}
		
		Log.v("resume",typeURL + " - " + URL);
	}
	void changeGroupSecurity(){
		if(security.equals(getString(R.string.baomattudong))){
			security_group.check(R.id.security3);
		} else if(security.equals(getString(R.string.baomattoanbo))){
			security_group.check(R.id.security2);
		} else if(security.equals(getString(R.string.baomatngoai))){
			security_group.check(R.id.security1);
		} else if(security.equals(getString(R.string.khongbaomat))){
			security_group.clearCheck();
		}
	}
	
	private void init() {		
		roomCollection = new LinkedHashMap<String, List<Myitems>>(); 
		groupList = new ArrayList<String>();
		//Chạy biến đềm lùi
		myThread = null;
		Runnable myRunnableThread = new CountDownRunner();
		myThread = new Thread(myRunnableThread);
		myThread.start();
		if(!NetworkStatus())
        	finish();
		
		IntentFilter intentFilter = new IntentFilter("com.example.smarthome");
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                //extract our message from intent
                String msg_for_me = intent.getStringExtra("message");
                Log.v("receive broadcast",msg_for_me);
                if(msg_for_me.equals("control")){
                	status = "control";
        			sendcommand();
                } else if(msg_for_me.equals("Save_Ip")){
                	GetdataSave();
                	changeGroupSecurity();
                	expListAdapter.notifyDataSetChanged();
                } else if(msg_for_me.equals("Save_nfc")){
                	nfc_data = intent.getStringArrayListExtra("nfc_data");
                	Log.v("nfc",nfc_data.toString());
                } else if(msg_for_me.equals("woc")){
                	status = intent.getStringExtra("wocType");
                	sendNofication(id,"woc");
                	startWoc(status);
                }
                Log.i("InchooTutorial", msg_for_me);
            }
        };
        this.registerReceiver(mReceiver, intentFilter);
	}
	
	public void time() {
		runOnUiThread(new Runnable() {
			@SuppressLint("SimpleDateFormat")
			public void run() {
				try {
					Date thoiGian = new Date();
					SimpleDateFormat dinhDangThoiGian = new SimpleDateFormat(
							"HH:mm:ss dd/MM/yyyy");
					SimpleDateFormat seconds = new SimpleDateFormat("ss");
					String showTime = dinhDangThoiGian.format(thoiGian.getTime());
					String second = seconds.format(thoiGian.getTime());
					if(CommandSend==true){
						CommandSend=false;
	        			sendcommand();
					}
					if(securitySend==true){
						securitySend=false;
						sendNofication(id,"security");
					}
					if(synData){
						synData=false;
						status = "updateinfo_at";
						sendData();
						//pushData("get data");
					}
//					if( Integer.parseInt(second)%10==0) {
//						status = "updateinfo";
//						updateData();
//					}
//					time.setText(showTime);
				} catch (Exception e) {
				}
			}
		});
	}
	class CountDownRunner implements Runnable {
		// @Override
		public void run() {
			while (!Thread.currentThread().isInterrupted()) {
				try {
					time();//mỗi 1s  chạy biến times()				
					Thread.sleep(500); // Pause of 1 Second
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				} catch (Exception e) {
				}
			}
		}
	}
	
	private void itemsMenu_init() {
		//move action item
        ActionItem moveAction = new ActionItem();         
        moveAction.setTitle("Move");
        moveAction.setIcon(getResources().getDrawable(R.drawable.ic_move));
        
        //change action item
        ActionItem changeAction = new ActionItem();         
        changeAction.setTitle("Change");
        changeAction.setIcon(getResources().getDrawable(R.drawable.ic_change));
        
        //delete action item
        ActionItem delAction = new ActionItem();         
        delAction.setTitle("Delete");
        delAction.setIcon(getResources().getDrawable(R.drawable.ic_delete));
        
        itemsQuickAction  = new QuickAction(this);
        itemsQuickAction.addActionItem(moveAction);        
        itemsQuickAction.addActionItem(changeAction);
        itemsQuickAction.addActionItem(delAction);
	}
	private void groupMenu_init() { 
        //Add action item
        ActionItem addAction = new ActionItem();         
        addAction.setTitle("Add");
        addAction.setIcon(getResources().getDrawable(R.drawable.ic_add));
        
        //change action item
        ActionItem changeAction = new ActionItem();         
        changeAction.setTitle("Change");
        changeAction.setIcon(getResources().getDrawable(R.drawable.ic_change));
        
        //delete action item
        ActionItem delAction = new ActionItem();         
        delAction.setTitle("Delete");
        delAction.setIcon(getResources().getDrawable(R.drawable.ic_delete));
        
        groupQuickAction  = new QuickAction(this);
        groupQuickAction.addActionItem(addAction);        
        groupQuickAction.addActionItem(changeAction);
        groupQuickAction.addActionItem(delAction);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		int i,j,k;
		String nameItems;
		Log.v("result","code: "+requestCode + " - result: "+resultCode);
		try {// nhan Buddle
			switch (requestCode) {
			case AddRoomCode: 
				if (resultCode == Activity.RESULT_OK) {
					Bundle Returndata = data.getExtras();
					addroom.clear();
					addroom = Returndata.getStringArrayList("room");
					for(i=0;i<addroom.size();i++){
						for(j=0,k=0;j<groupList.size();j++)//kiem tra co bi trung ten room ko
							if(compareRoom(addroom.get(i),groupList.get(j)))
								k++;
						if(k==0) {
							groupList.add(addroom.get(i));
							itemsList = new ArrayList<Myitems>();
							roomCollection.put(addroom.get(i), itemsList);
						}
						else {
							groupList.add(addroom.get(i)+ " " + String.valueOf(k));
							itemsList = new ArrayList<Myitems>();
							roomCollection.put(addroom.get(i)+ " " + String.valueOf(k), itemsList);
						}
					}
					expListAdapter.notifyDataSetChanged();		
				} else {
					Toast.makeText(getApplicationContext(), "loi xay ra",Toast.LENGTH_SHORT);
				}
				break;
			case addItemsCode:
				if (resultCode == Activity.RESULT_OK) {
					Bundle Returndata = data.getExtras();
					additems = Returndata.getString("items");
					i = Returndata.getInt("groupPos");
					String[] number = additems.split("-");
					
					itemsList = new ArrayList<Myitems>();
					itemsList = roomCollection.get(groupList.get(i));
					if (Integer.parseInt(number[1]) < 4) {
						for (j = 0; j < Integer.parseInt(number[1]); j++) {
							Myitems tmp = new Myitems();
							tmp.name = "Đèn";
							tmp.add = number[0];
							tmp.value = "OFF";
							tmp.data = "";  
							tmp.room = groupList.get(i);
							itemsList.add(tmp);
						}
						roomCollection.put(groupList.get(i), itemsList);
						expListAdapter.notifyDataSetChanged();
						setData();
					} else
						Toast.makeText(getApplicationContext(), "Code error",
								Toast.LENGTH_SHORT);
				} else {
					Toast.makeText(getApplicationContext(), "loi xay ra",Toast.LENGTH_SHORT);
				}
				break;
			case changeItemsCode: 
				if (resultCode == Activity.RESULT_OK) {
					Bundle Returndata = data.getExtras();
					nameItems = Returndata.getString("nameItems");
					i = Returndata.getInt("groupPos");
					j = Returndata.getInt("childPos");
					
					itemsList = new ArrayList<Myitems>();
					itemsList = roomCollection.get(groupList.get(i));
					Myitems tmp = new Myitems();
					tmp = itemsList.get(j);
					tmp.name = nameItems;	
					if(nameItems.equals(getString(R.string.bom))) tmp.data = "Thứ 2 – 26/8/2013 – 12:05";
					Log.v("mai",tmp.name + " - childPos= " + j);
					itemsList.set(j,tmp);
					expListAdapter.notifyDataSetChanged();	
					setData();
				} else {
					Toast.makeText(getApplicationContext(), "loi xay ra",Toast.LENGTH_SHORT);
				}
				
				break;
			case RESULT_SPEECH_CODE:
				if (resultCode == Activity.RESULT_OK && null != data){
					String add = new String();
					ArrayList<String> text = data
							.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
					PairRecognizer result = new PairRecognizer(this);
					setdata = new ArrayList<Myitems>();
					NumberData=0;
					Log.v("Speech",text.toString());
					setdata = result.Recognizer(text,roomCollection,groupList);
					if(setdata==null) Toast.makeText(context, "Không tìm thấy phòng \n hoặc thiết bị",Toast.LENGTH_LONG).show();
					else {
						expListAdapter.notifyDataSetChanged();
						status = "control";
						sendcommand();
					}
					Speech_Code = true;
				}
				break;
			case SettingCode:
				if(resultCode == Activity.RESULT_OK){
					Log.v("setting IP","Da luu gia tri Ip moi");
					Bundle Returndata = data.getExtras();
					typeURL = Returndata.getString("typeURL");
					if (typeURL.equals("Lan"))
						URL = Returndata.getString("URL1");
					else if (typeURL.equals("Wan"))
						URL = Returndata.getString("URL2");
					Port = Returndata.getString("Port");
				}
				break;
			} 
		} catch (Exception e) {
			Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
		}
	}
	
	private boolean compareRoom(String roomA, String roomB) {
		String[] splitA = roomA.split(" ");
		String[] splitB = roomB.split(" ");
		int i,j,k=0;
		for(i=0;i<splitA.length;i++)
			for(j=0;j<splitB.length;j++)
				if(splitA[i].equals(splitB[j])){
					k++;
					break;
				}
		if(k==splitA.length) return true;
		else return false;
	}
	
	@Override
	// chuyen doi toi cac activity tuong ung cua tung thanh phan menu
    public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.About:
			Intent about = new Intent("com.example.smarthome.ABOUT");
			startActivity(about); 
			break;
		case R.id.AddRoom:
			Intent sendIntent = new Intent();
			sendIntent.setClass(MainActivity.this, AddRoom.class);
			startActivityForResult(sendIntent, AddRoomCode);
			break;
		case R.id.Setting:
			Intent SettingIntent = new Intent();
			SettingIntent.setClass(MainActivity.this, Setting.class);
			startActivityForResult(SettingIntent, SettingCode);
			break;
		case R.id.SetInfo:
			setData();
			//pushData("set data");
			break;
		case R.id.GetInfo:
			status = "getinfo";
			sendData();
			break;
		case R.id.ResetServer:
			sendNofication(id,"resetServer");
			break;
		}
		return false; 
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	// class group list view
	public class ExpandableListAdapter extends BaseExpandableListAdapter {

		private Activity context;
		private Map<String, List<Myitems>> roomCollections;
		private List<String> rooms;

		public ExpandableListAdapter(Activity context, List<String> rooms,
				Map<String, List<Myitems>> roomCollections) {
			this.context = context;
			this.roomCollections = roomCollections;
			this.rooms = rooms;
		}

		public Myitems getChild(int groupPosition, int childPosition) {
			return roomCollections.get(rooms.get(groupPosition)).get(
					childPosition);
		}

		public long getChildId(int groupPosition, int childPosition) {
			return childPosition;
		}

		public View getChildView(final int groupPosition,
				final int childPosition, boolean isLastChild, View convertView,
				ViewGroup parent) {
			final String items = (String) getChild(groupPosition, childPosition).name;
			final String value = (String) getChild(groupPosition, childPosition).value;

			LayoutInflater inflater = context.getLayoutInflater();

			if (convertView == null) {
				convertView = inflater.inflate(R.layout.child_item, null);
			}
			final Switch switchItems = (Switch) convertView
					.findViewById(R.id.switchItems);
			final ImageView editItems = (ImageView) convertView
					.findViewById(R.id.editItems);
			final ImageView autoButton = (ImageView) convertView
					.findViewById(R.id.autoButton);
			editItems.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					itemsQuickAction.show(v);
					itemsQuickAction
							.setAnimStyle(QuickAction.ANIM_GROW_FROM_RIGHT);
					grouppos = groupPosition;
					childpos = childPosition;
				}
			});
			itemsQuickAction.setOnActionItemClickListener(new QuickAction.OnActionItemClickListener() {
						@Override
						public void onItemClick(int pos) {

							if (pos == 0) { // Add item selected
								Toast.makeText(MainActivity.this,
										"Move items selected",
										Toast.LENGTH_SHORT).show();
							} else if (pos == 1) { // Change item selected
								Bundle sendBundle = new Bundle();
								Intent sendIntent = new Intent();
								sendIntent.setClass(MainActivity.this,
										changeItems.class);
								sendBundle.putInt("groupPos", grouppos);
								sendBundle.putInt("childPos", childpos);
								sendIntent.putExtras(sendBundle);// gui di
								startActivityForResult(sendIntent,
										changeItemsCode);
							} else if (pos == 2) { // Delete item selected
								List<Myitems> child = roomCollections.get(rooms.get(grouppos));
								String add_delete = child.get(childpos).add;
								for(int k=child.size()-1;k>=0;k--){
									if(child.get(k).add.equals(add_delete)) 
										child.remove(k);
								}
								notifyDataSetChanged();
								setData();
							}
						}
					});

			switchItems.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Vibrator vibe = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
					vibe.vibrate(50);
					List<Myitems> child = new ArrayList<Myitems>();
					child = roomCollections.get(rooms.get(groupPosition));
					Myitems tmp = new Myitems();
					tmp = child.get(childPosition);
					if (switchItems.isChecked()) {
						editItems.setImageResource(ic_on(items));
						tmp.value = "ON";
					} else {
						editItems.setImageResource(ic_off(items));
						tmp.value = "OFF";
						autoButton.setImageResource(R.drawable.ic_autooff);
					}
					child.set(childPosition, tmp);
					//tim cac items cung add
					setdata = new ArrayList<Myitems>();
					for(int i=0;i<child.size();i++)
						if(child.get(i).add.equals(tmp.add)) {
							if(child.get(i).name.equals(getString(R.string.tivi)))
								child.get(i).data="";
							setdata.add(child.get(i));
						}
					status = "control";
					sendcommand();
					
					Toast.makeText(MainActivity.this,
							"Send " + items + ": " + tmp.value,
							Toast.LENGTH_SHORT).show();
				}
			});
			Log.v("group", items);
			if (value.equals("OFF")) {
				switchItems.setChecked(false);
				editItems.setImageResource(ic_off(items));
			} else {
				switchItems.setChecked(true);
				editItems.setImageResource(ic_on(items));
			}

			autoButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Vibrator vibe = (Vibrator) context
							.getSystemService(Context.VIBRATOR_SERVICE);
					vibe.vibrate(50);
					List<Myitems> child = roomCollections.get(rooms
							.get(groupPosition));
					Myitems tmp = child.get(childPosition);
					if (tmp.value.equals("AUTO")) {
						tmp.value = "OFF";
						switchItems.setChecked(false);
						editItems.setImageResource(ic_off(items));
						autoButton.setImageResource(R.drawable.ic_autooff);
					} else {
						tmp.value = "AUTO";
						switchItems.setChecked(true);
						editItems.setImageResource(ic_on(items));
						autoButton.setImageResource(R.drawable.ic_autoon);
					}
					child.set(childPosition, tmp);
					
					setdata = new ArrayList<Myitems>();
					for(int i=0;i<child.size();i++)
						if(child.get(i).add.equals(tmp.add)) {
							setdata.add(child.get(i));
						}
					status = "control";
					sendcommand();
					
					Toast.makeText(MainActivity.this,
							"Send " + items + ": " + tmp.value,
							Toast.LENGTH_SHORT).show();
				}
			});
			if (value.equals("AUTO"))
				autoButton.setImageResource(R.drawable.ic_autoon);
			else
				autoButton.setImageResource(R.drawable.ic_autooff);

			switchItems.setText(items);// doi bieu tuong bong den
			return convertView;
		}

		protected int ic_on(String name) {
			if (name.equals(getString(R.string.bongden)))
				return R.drawable.ic_lighton;
			else if (name.equals(getString(R.string.quat)))
				return R.drawable.ic_fanon;
			else if (name.equals(getString(R.string.loa)))
				return R.drawable.ic_loaon;
			else if (name.equals(getString(R.string.laptop)))
				return R.drawable.ic_laptopon;
			else if (name.equals(getString(R.string.tivi)))
				return R.drawable.ic_tvon;
			else if (name.equals(getString(R.string.khoa)))
				return R.drawable.ic_lockon;
			else if (name.equals(getString(R.string.bom)))
				return R.drawable.ic_bomon;
			else if (name.equals(getString(R.string.dieuhoa)))
				return R.drawable.ic_dieuhoaon;
			return 0;
		}

		protected int ic_off(String name) {
			if (name.equals(getString(R.string.bongden)))
				return R.drawable.ic_lightoff;
			else if (name.equals(getString(R.string.quat)))
				return R.drawable.ic_fanoff;
			else if (name.equals(getString(R.string.loa)))
				return R.drawable.ic_loaoff;
			else if (name.equals(getString(R.string.laptop)))
				return R.drawable.ic_laptopoff;
			else if (name.equals(getString(R.string.tivi)))
				return R.drawable.ic_tvoff;
			else if (name.equals(getString(R.string.khoa)))
				return R.drawable.ic_lockoff;
			else if (name.equals(getString(R.string.bom)))
				return R.drawable.ic_bomoff;
			else if (name.equals(getString(R.string.dieuhoa)))
				return R.drawable.ic_dieuhoaoff;
			return 0;
		}

		public int getChildrenCount(int groupPosition) {
			return roomCollections.get(rooms.get(groupPosition)).size();
		}

		public Object getGroup(int groupPosition) {
			return rooms.get(groupPosition);
		}

		public int getGroupCount() {
			return rooms.size();
		}

		public long getGroupId(int groupPosition) {
			return groupPosition;
		}

		public View getGroupView(final int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {
			final String ItemsName = (String) getGroup(groupPosition);
			if (convertView == null) {
				LayoutInflater infalInflater = (LayoutInflater) context
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = infalInflater.inflate(R.layout.adapter_listroom,
						null);
			}
			TextView item = (TextView) convertView.findViewById(R.id.textLine);
			ImageView icon = (ImageView) convertView
					.findViewById(R.id.imageView);
			item.setTypeface(null, Typeface.BOLD);
			item.setText(ItemsName);
			setRoomImage(ItemsName, convertView);

			icon.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					groupQuickAction.show(v);
					groupQuickAction
							.setAnimStyle(QuickAction.ANIM_GROW_FROM_RIGHT);
					grouppos = groupPosition;
				}
			});
			groupQuickAction
					.setOnActionItemClickListener(new QuickAction.OnActionItemClickListener() {
						@Override
						public void onItemClick(int pos) {

							if (pos == 0) { // Add item selected
								Bundle sendBundle = new Bundle();
								Intent sendIntent = new Intent();
								sendIntent.setClass(MainActivity.this,
										addRelay.class);
								sendBundle.putInt("groupPos", grouppos);
								sendIntent.putExtras(sendBundle);// gui di
								startActivityForResult(sendIntent, addItemsCode);
							} else if (pos == 1) { // Change item selected
								Toast.makeText(MainActivity.this,
										"Change items selected",
										Toast.LENGTH_SHORT).show();
							} else if (pos == 2) { // Delete item selected
								Toast.makeText(MainActivity.this,
										"Xóa " + rooms.get(grouppos),
										Toast.LENGTH_SHORT).show();
								roomCollections.remove(rooms.get(grouppos));
								rooms.remove(grouppos);
								notifyDataSetInvalidated();
							}
						}
					});
			return convertView;
		}

		private void setRoomImage(String itemsName, View v) {
			ImageView roomImage = (ImageView) v.findViewById(R.id.roomImage);
			if (compareRoom(getString(R.string.phongkhach), itemsName))
				roomImage.setBackgroundResource(R.drawable.phongkhach);
			else if (compareRoom(getString(R.string.phongngu), itemsName))
				roomImage.setBackgroundResource(R.drawable.phongngu);
			else if (compareRoom(getString(R.string.phongbep), itemsName))
				roomImage.setBackgroundResource(R.drawable.phongbep);
			else if (compareRoom(getString(R.string.phongtam), itemsName))
				roomImage.setBackgroundResource(R.drawable.phongtam);
			else if (compareRoom(getString(R.string.phonglamviec), itemsName))
				roomImage.setBackgroundResource(R.drawable.phonglamviec);
			else if (compareRoom(getString(R.string.vuonsau), itemsName))
				roomImage.setBackgroundResource(R.drawable.vuoncay);
			else if (compareRoom(getString(R.string.vuontruoc), itemsName))
				roomImage.setBackgroundResource(R.drawable.santruoc);
			else if (compareRoom(getString(R.string.santhuongtruoc), itemsName))
				roomImage.setBackgroundResource(R.drawable.santhuong);
			else if (compareRoom(getString(R.string.hanhlang), itemsName))
				roomImage.setBackgroundResource(R.drawable.hanhlang); 
			else if (compareRoom(getString(R.string.phongtrong), itemsName))
				roomImage.setBackgroundResource(R.drawable.phongtrong);
		}

		public boolean hasStableIds() {
			return true;
		}

		public boolean isChildSelectable(int groupPosition, int childPosition) {
			return true;
		}
	}
	
	private void setData() {
		setdata = new ArrayList<Myitems>();
		for(int i=0;i<roomCollection.size();i++)
			for(int j=0;j<roomCollection.get(groupList.get(i)).size();j++)
				setdata.add(roomCollection.get(groupList.get(i)).get(j));
		status = "setinfo";
		sendcommand();
	}
	
	private void Getdata_database() {    	  
        database.open();
        roomCollection = database.getData();
        groupList=database.Getgroup();
        database.close();
	}

	@Override
	protected void onStop() {
		super.onStop();
		
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		databaseSave();
		myThread.interrupt();
		unregisterReceiver(mReceiver);
        //myTask.cancel(true);
		//progressDialog.dismiss();
	}	
	@Override
	protected void onResume() { 
		super.onResume();
		Log.v("main","onResume");
		//update
		if(Speech_Code==true)
			Speech_Code = false;
		else if(first_update == true) 
			first_update = false;
		else if(woc_intent == true)
			woc_intent =false;
		else {
			status = "updateinfo_at";
			sendData();
		}
        //nfc_of
		adapter = NfcAdapter.getDefaultAdapter(this);
		if(adapter!=null){
			writeMode=true;
			adapter.enableForegroundDispatch(this, pendingIntent, writeTagFilters, null);
		}
	}
	
	@Override
	protected void onPause(){
		super.onPause();
		Log.v("main","onPause");
		//nfc of
		if(writeMode==true)
			adapter.disableForegroundDispatch(this);
	}

	private void databaseSave() {
		database.remove();
        database.open();
        database.createData(groupList,roomCollection);
        database.close();
        Log.v("main","data save to database");
	}
	
	public void sendData() {
		runOnUiThread(new Runnable() {
			@SuppressLint("SimpleDateFormat")
			public void run() {
				Log.v("sendData", "...");
				setdata = new ArrayList<Myitems>();
				sendcommand();
			}
		});
	}
	
	public void updateInfo(JSONObject update){
		Log.v("update","update info");
		Toast.makeText(this, "Syn done!", 200).show();
		boolean In_json = false;
		boolean Out_json = false;
		String add_item = new String();
		JSONArray Items_json = null;
		try {
			Items_json = update.getJSONArray(KEY_ITEMS);
			temperature = update.getInt("temp") - 4;
			humidity = update.getInt("humi");
			for(int i = 0; i < Items_json.length(); i++){
				JSONObject c = Items_json.getJSONObject(i);
			    String add_json = c.getString("add");
			    String data_json = String.valueOf(c.getInt("data"));
			    int number_json = c.getInt("number");
			    String value_json[]= {NameOnOff(c.getInt("value1")),
			    		NameOnOff(c.getInt("value2")),NameOnOff(c.getInt("value3"))};
			    String name_json[] = {Name_json(c.getInt("name1")),
			    		Name_json(c.getInt("name2")),Name_json(c.getInt("name3"))};
			    Log.v("update","add="+add_json+" data="+data_json+" number:"+number_json +
			    		" value="+value_json[0]+" "+value_json[1]+" "+value_json[2]);
			    for(int j=0;j<groupList.size();j++) 
			       	for(int k=0;k<roomCollection.get(groupList.get(j)).size();k++){
			       		if(roomCollection.get(groupList.get(j)).get(k).add.equals(add_json)){
//			       			if( CheckData(groupList.get(j),data_json).equals("In"))
//			       				In_json=true;
//			       			else if( CheckData(groupList.get(j),data_json).equals("Out"))
//			       				Out_json=true;
			       			for(int n=0;n<number_json && n<roomCollection.get(groupList.get(j)).size();n++){
			       				roomCollection.get(groupList.get(j)).get(k+n).value=value_json[n];	
			       				roomCollection.get(groupList.get(j)).get(k+n).data=Data_Json(data_json);
			       				roomCollection.get(groupList.get(j)).get(k+n).name=name_json[n];
			       				Log.v("update",add_json+"-"+value_json[n]+" data: "+Data_Json(data_json));
			       			}
			       			break;
			       		}
			       	}
			}			

			expListAdapter.notifyDataSetChanged();
		} catch (JSONException e) {
			Toast.makeText(context, "update error", Toast.LENGTH_SHORT).show();
		}
		if(!nfc_status.equals("")){
			nfc_status="";
			databaseSave();
		}
	}
	 
	private String Data_Json(String data_json) {
		if(data_json.equals("1"))
			return "SECURITY";
		else if(data_json.equals("0"))
			return "";
		else return "";
	}
	private String CheckData(String room_json, String data_json) {
		if(data_json.equals("1")) {//security
			if(room_json.equals(getString(R.string.phongkhach)) || 
					room_json.equals(getString(R.string.phongngu)) ||
					room_json.equals(getString(R.string.phongbep)) ||
					room_json.equals(getString(R.string.phongtam)) ||
					room_json.equals(getString(R.string.phonglamviec)) ||
					room_json.equals(getString(R.string.phongtrong))) 
				return "In";
			else return "Out";
		}
		else return "";
	}
	public void getInfo(JSONObject getInfo) {
		Log.v("update","get info");
		String add_item = new String();
		JSONArray Items_json = null;
		try {
			Items_json = getInfo.getJSONArray(KEY_ITEMS);
			roomCollection.clear(); 
			groupList.clear();
			Log.v("updata","json_length="+Items_json.length());
			for(int i = 0; i < Items_json.length(); i++){
				JSONObject c = Items_json.getJSONObject(i);
			    String add_json = c.getString("add");
			    String room_json = Room_json(c.getInt("room"));
			    String name_json[] = {Name_json(c.getInt("name1")),
			    		Name_json(c.getInt("name2")),Name_json(c.getInt("name3"))};
			    int number_json = c.getInt("number");
			    String value_json[]= {NameOnOff(c.getInt("value1")),
			    		NameOnOff(c.getInt("value2")),NameOnOff(c.getInt("value3"))};
			    Log.v("getdata","add="+add_json+" number:"+number_json +
			    		" value="+value_json[0]+" "+value_json[1]+" "+value_json[2]);
			    Log.v("getdata","name="+name_json[0]+" "+name_json[1]+" "+name_json[2]);
				List<Myitems> List_json = new ArrayList<Myitems>();
				for(int k=0;k<number_json;k++){
					Myitems item_json = new Myitems();
					item_json.name = name_json[k];
					item_json.add = add_json;
					item_json.value = value_json[k];
					item_json.room = room_json;
					item_json.data = "";
					List_json.add(item_json);
				}
				for(int k=0;k<List_json.size();k++)
					Log.v("updata","loi 2 - Name:" + List_json.get(k).name);
				int j;
			    for(j=0;j<groupList.size();j++)//kiem tra co nam trong room da tao ko
			    	if(groupList.get(j).equals(room_json)) {
						List<Myitems> child_json = roomCollection.get(groupList.get(j));
			    		for(int h=0;h<List_json.size();h++)
			    			child_json.add(List_json.get(h));
			    		break; 
			    	}
			    Log.v("updata","loi 3" + " j="+j+" groupList.size="+groupList.size());
			    if(j==groupList.size()) {
			    	groupList.add(room_json);
			    	roomCollection.put(groupList.get(j), List_json);
			    }
				for(int k=0;k<roomCollection.get(groupList.get(j)).size();k++)
					Log.v("updata","loi 4 - Name:" + roomCollection.get(groupList.get(j)).get(k).name);
			}
			expListAdapter.notifyDataSetChanged();
		} catch (JSONException e) {
			Toast.makeText(context, "update error", Toast.LENGTH_SHORT).show();
		}

	}
	
	private String Room_json(int value){
		if(value == 0) return(getString(R.string.phongkhach));
		else if(value == 1) return (getString(R.string.phongngu));
		else if(value == 2) return (getString(R.string.phongbep));
		else if(value == 3) return (getString(R.string.phonglamviec));
		else if(value == 4) return (getString(R.string.phongtam));
		else if(value == 5) return (getString(R.string.vuonsau));
		else if(value == 6) return (getString(R.string.vuontruoc));
		else if(value == 7) return (getString(R.string.santhuongtruoc));
		else if(value == 8) return (getString(R.string.hanhlang));
		else if(value == 9) return (getString(R.string.phongtrong));
		else return (getString(R.string.phongtrong));
	}
	private String Name_json(int value){
		if(value == 0) return (getString(R.string.bongden));
		else if(value == 1) return (getString(R.string.quat));
		else if(value == 2) return (getString(R.string.loa));
		else if(value == 3) return (getString(R.string.laptop));
		else if(value == 4) return (getString(R.string.tivi));
		else if(value == 5) return (getString(R.string.khoa));
		else if(value == 6) return (getString(R.string.bom));
		else if(value == 7) return (getString(R.string.dieuhoa));
		else return getString(R.string.bongden);
	}
	private String NameOnOff(int value) {
		if(value==1) return "ON";
		else if(value==2) return "OFF";
		else if(value==3) return "AUTO";
		else return "OFF";
	}
	
	public static boolean AppStatus(View rootView) {
		ConnectivityManager connectivityManager = (ConnectivityManager) rootView.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
		if (networkInfo == null) {
			Toast.makeText(rootView.getContext(), "network unavailable",
					Toast.LENGTH_LONG).show();
			return false;
		} else 
			return true;
	}
	
	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			// getItem is called to instantiate the fragment for the given page.
			// Return a DummySectionFragment (defined as a static inner class
			// below) with the page number as its lone argument.
			Fragment fragment = new DummySectionFragment();
			Bundle args = new Bundle();
			args.putInt(DummySectionFragment.ARG_SECTION_NUMBER, position + 1);
			fragment.setArguments(args);
			return fragment;
		}

		@Override
		public int getCount() {
			// Show 4 total pages.
			return 6;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			Locale l = Locale.getDefault();
			switch (position) {
			case 0:
				return getString(R.string.title_section0).toUpperCase(l);
			case 1:
				return getString(R.string.title_section1).toUpperCase(l);
			case 2:
				return getString(R.string.title_section2).toUpperCase(l);
			case 3:
				return getString(R.string.title_section3).toUpperCase(l);
			case 4:
				return getString(R.string.title_section4).toUpperCase(l);
			case 5:
				return getString(R.string.title_section5).toUpperCase(l);
			}
			return null;
		}
	}

	public static class DummySectionFragment extends Fragment {

		public static final String ARG_SECTION_NUMBER = "section_number";

		public DummySectionFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main_dummy,
					container, false);
			TextView dummyTextView = (TextView) rootView.findViewById(R.id.section_label);;
			
            switch (getArguments().getInt(ARG_SECTION_NUMBER)){
            case 1: rootView = inflater.inflate(R.layout.nfc, container, false);
                    nfc_view(rootView); break;
            case 2: rootView = inflater.inflate(R.layout.channel, container, false);
                    channel_view(rootView); break;
            case 3: rootView = inflater.inflate(R.layout.control, container, false);
                    control_view(rootView); break;
            case 4: rootView = inflater.inflate(R.layout.security, container, false);
                    security_view(rootView); break;
            case 5: rootView = inflater.inflate(R.layout.garden, container, false);
                    garden_view(rootView); break;  
            case 6: rootView = inflater.inflate(R.layout.information, container, false);
                    info_view(rootView); break;
            } 
			return rootView;
		}
	}
	 
	public static void nfc_view(final View rootView) {
		final Context context = rootView.getContext();
		ImageView nfc_list = (ImageView) rootView.findViewById(R.id.nfc_list);
		Button nfc_save = (Button) rootView.findViewById(R.id.nfc_save);
		
		nfc_list.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent Garden_Setting = new Intent("com.example.smarthome.NFC_CLASS");
				rootView.getContext().startActivity(Garden_Setting);
			}
		});
		
		nfc_save.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				 try {
		                if(mytag==null){
		                    Toast.makeText(context, "error", Toast.LENGTH_LONG ).show();
		                }else{
		                    write(nfc_data.toString(),mytag);
		                    Toast.makeText(context, "writing ok", Toast.LENGTH_LONG ).show();
		                }
		            } catch (IOException e) {
		                Toast.makeText(context, "writing error", Toast.LENGTH_LONG ).show();
		                e.printStackTrace();
		            } catch (FormatException e) {
		                Toast.makeText(context, "writing error" , Toast.LENGTH_LONG ).show();
		                e.printStackTrace();
		            }
			}
		});
		
	}
	
	@Override
	protected void onNewIntent(Intent intent){		handleIntent(intent);}
	
	private void handleIntent(Intent intent) {
		Log.v("NFC","action: "+intent.getAction());
		if(NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())){
	        mytag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
	        //Toast.makeText(this, "ok" + mytag.toString(), Toast.LENGTH_SHORT ).show();
	    }else if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())) {
			 String type = intent.getType();
			 Log.v(TAG,"type=" + type);
		        if (MIME_TEXT_PLAIN.equals(type)) {
		        	Log.v("NFC","start Service");	
		        	nfc_status = "ACTION_NDEF_DISCOVERED";
		        	nfc_intent = new Intent(this, MyService.class);
		        	nfc_intent.putExtras(intent);
		        	startService(nfc_intent);
		        	finish();		        	
//		        	ctx = this;
//		            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
//		            new NdefReaderTask().execute(tag);
		        } else {
		            Log.d(TAG, "Wrong mime type: " + type);
		        }
		}
	}
	private static void write(String text, Tag tag) throws IOException, FormatException {

	    NdefRecord[] records = { createRecord(text) , NdefRecord.createApplicationRecord("com.example.smarthome") };
	    NdefMessage message = new NdefMessage(records); 
	    Ndef ndef = Ndef.get(tag);
	    ndef.connect();
	    ndef.writeNdefMessage(message);
	    ndef.close();
	}
	private static  NdefRecord createRecord(String text) throws UnsupportedEncodingException {

	    //create the message in according with the standard
	    String lang = "en";
	    byte[] textBytes = text.getBytes();
	    byte[] langBytes = lang.getBytes("US-ASCII");
	    int langLength = langBytes.length;
	    int textLength = textBytes.length;

	    byte[] payload = new byte[1 + langLength + textLength];
	    payload[0] = (byte) langLength;

	    // copy langbytes and textbytes into payload
	    System.arraycopy(langBytes, 0, payload, 1, langLength);
	    System.arraycopy(textBytes, 0, payload, 1 + langLength, textLength);

	    NdefRecord recordNFC = new NdefRecord(NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_TEXT, new byte[0], payload);
	    return recordNFC;
	}
	
	public static void channel_view(final View rootView) {
		final Context context = rootView.getContext();
		ImageView vtv1 = (ImageView) rootView.findViewById(R.id.vtv1);
		ImageView vtv2 = (ImageView) rootView.findViewById(R.id.vtv2);
		ImageView vtv3 = (ImageView) rootView.findViewById(R.id.vtv3);
		ImageView htv1 = (ImageView) rootView.findViewById(R.id.htv1);
		ImageView htv2 = (ImageView) rootView.findViewById(R.id.htv2);
		ImageView htv3 = (ImageView) rootView.findViewById(R.id.htv3);
		ImageView htv4 = (ImageView) rootView.findViewById(R.id.htv4);
		ImageView htv7 = (ImageView) rootView.findViewById(R.id.htv7);
		ImageView hbo = (ImageView) rootView.findViewById(R.id.hbo);
		ImageView discovery = (ImageView) rootView.findViewById(R.id.discovery);
		ImageView starmovies = (ImageView) rootView.findViewById(R.id.starmovies);
		ImageView cartoon = (ImageView) rootView.findViewById(R.id.cartoon);
		ImageView disney = (ImageView) rootView.findViewById(R.id.disney);
		ImageView nhaccuatui = (ImageView) rootView.findViewById(R.id.nhaccuatui);
		ImageView geographic = (ImageView) rootView.findViewById(R.id.geographic);
		ImageView youtube = (ImageView) rootView.findViewById(R.id.youtube);
		ImageView soha = (ImageView) rootView.findViewById(R.id.soha);
		ImageView power = (ImageView) rootView.findViewById(R.id.power);
		
		final ImageView button_center = (ImageView) rootView.findViewById(R.id.button_center);
		ImageView button_qmenu_nor = (ImageView) rootView.findViewById(R.id.button_qmenu_nor);
		ImageView button_menu_nor = (ImageView) rootView.findViewById(R.id.button_menu_nor);
		ImageView button_return_nor = (ImageView) rootView.findViewById(R.id.button_return_nor);
		ImageView button_exit_nor = (ImageView) rootView.findViewById(R.id.button_exit_nor);
		ImageView button_ok = (ImageView) rootView.findViewById(R.id.button_ok);
		
		button_center.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				int action = event.getAction();
			    int x = (int) event.getX();
			    int y = (int) event.getY();
			    int width = button_center.getHeight();
			    switch(action){
				case MotionEvent.ACTION_UP:
					if(x>width/4 && x<width*3/4 && y<width/4) channel_setdata(context, context.getString(R.string.up));
					else if(x>width/4 && x<width*3/4 && y>width*3/4) channel_setdata(context, context.getString(R.string.down));
					else if(x<width/4 && y>width/4 && y<width*3/4) channel_setdata(context, context.getString(R.string.left));
					else if(x>width*3/4 && y>width/4 && y<width*3/4) channel_setdata(context, context.getString(R.string.right));	
					else if(x>width/4 && x<width*3/4 && y>width/4 && y<width*3/4) channel_setdata(context, context.getString(R.string.enter));
					Log.v("center","x-y: "+x+"-"+y);
					break;
				}
			    return true;
			}
		});
		
		
		button_qmenu_nor.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				channel_setdata(context, context.getString(R.string.menu));
			}
		});
		button_menu_nor.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				channel_setdata(context, context.getString(R.string.home));
			}
		});
		button_return_nor.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				channel_setdata(context, context.getString(R.string.back));
			}
		});
		button_exit_nor.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				channel_setdata(context, context.getString(R.string.exit));
			}
		});
		
		final ImageView wheel_vol_nor = (ImageView) rootView.findViewById(R.id.wheel_vol_nor);
		wheel_vol_nor.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				int action = event.getAction();
			    int y = (int) event.getY();
			    int Height = wheel_vol_nor.getHeight();
			    switch(action){
				case MotionEvent.ACTION_UP:
					if(y<Height*2/5) channel_setdata(context, context.getString(R.string.volumteUP));
					else if(y>Height*3/5) channel_setdata(context, context.getString(R.string.volumteDown));
					break;
				}
			    return true;
			}
		});
		
		ImageView wheel_ch_nor = (ImageView) rootView.findViewById(R.id.wheel_ch_nor);
		wheel_ch_nor.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				int action = event.getAction();
			    int y = (int) event.getY();
			    switch(action){
				case MotionEvent.ACTION_UP:
					if(y<100) channel_setdata(context, context.getString(R.string.channelUp));
					else if(y>150) channel_setdata(context, context.getString(R.string.channelDown));
					break;
				}
			    return true;
			}
		});		
		ImageView button_mute = (ImageView) rootView.findViewById(R.id.button_mute);
		button_mute.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				channel_setdata(context, context.getString(R.string.mute));
			}
		});
		
		vtv1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				channel_setdata(context, context.getString(R.string.vtv1));
			}
		});
		vtv2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				channel_setdata(context, context.getString(R.string.vtv2));
			}
		});
		vtv3.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				channel_setdata(context, context.getString(R.string.vtv3));
			}
		});
		htv1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				channel_setdata(context, context.getString(R.string.htv1));
			}
		});
		htv2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				channel_setdata(context, context.getString(R.string.htv2));
			}
		});
		htv3.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				channel_setdata(context, context.getString(R.string.htv3));
			}
		});
		htv4.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				channel_setdata(context, context.getString(R.string.htv4));
			}
		});
		htv7.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				channel_setdata(context, context.getString(R.string.htv7));
			}
		});
		hbo.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				channel_setdata(context, context.getString(R.string.hbo));
			}
		});
		starmovies.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				channel_setdata(context, context.getString(R.string.starmovie));
			}
		});
		disney.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				channel_setdata(context, context.getString(R.string.disney));
			}
		});
		cartoon.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				channel_setdata(context, context.getString(R.string.cartoon));
			}
		});
		discovery.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				channel_setdata(context, context.getString(R.string.discovery));
			}
		});
		power.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				channel_setdata(context, context.getString(R.string.power));	
			}
		});
		nhaccuatui.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				channel_setdata(context, context.getString(R.string.nhaccuatui));	
				tv_lg_channel = true;
			}		
		});
		geographic.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				channel_setdata(context, context.getString(R.string.geographic));	
			}
		});
		youtube.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				channel_setdata(context, context.getString(R.string.youtube));	
				tv_lg_channel = true;
			}
		});
		soha.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				channel_setdata(context, context.getString(R.string.soha));	
				tv_lg_channel = true;
			}
		});		
	}
	
	static void channel_setdata(Context context,String data){
		Vibrator vibe = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE) ;
		vibe.vibrate(50); 
		Toast.makeText(context, data, Toast.LENGTH_SHORT).show();
		setdata = new ArrayList<Myitems>();
		for(int i=0;i<groupList.size();i++){
			List<Myitems> tmp = roomCollection.get(groupList.get(i));
			for(int j=0;j<tmp.size();j++){
				if(tmp.get(j).name.equals(context.getString(R.string.tivi))){
				    tmp.get(j).value="AUTO";
					tmp.get(j).data= data;
					//j la items tivi -> tim thiet bi cung add
					for(int k=0;k<tmp.size();k++)
						if(tmp.get(k).add.equals(tmp.get(j).add)) 
							setdata.add(tmp.get(k));
					status = "control";
					CommandSend=true;
					break;
				}
			}
		}
	}

	public static void control_view(final View rootView) {
		expListView = (ExpandableListView) rootView.findViewById(R.id.mylist);
        expListView.setAdapter(expListAdapter);
        expListView.setGroupIndicator(null);
        expListView.setOnGroupExpandListener(new OnGroupExpandListener() {
			@Override
			public void onGroupExpand(int arg0) {
				Vibrator vibe = (Vibrator) rootView.getContext().getSystemService(Context.VIBRATOR_SERVICE) ;
				vibe.vibrate(50); 
			}
		});
        ImageView mic = (ImageView) rootView.findViewById(R.id.mic);
        ImageView control_syn = (ImageView) rootView.findViewById(R.id.control_syn);
        control_syn.setOnClickListener(new OnClickListener() {		
			@Override
			public void onClick(View arg0) {
				synData=true;
				Vibrator vibe = (Vibrator) rootView.getContext().getSystemService(Context.VIBRATOR_SERVICE) ;
				vibe.vibrate(50); 
			}
		}) ;
        mic.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Vibrator vibe = (Vibrator) rootView.getContext().getSystemService(Context.VIBRATOR_SERVICE) ;
				vibe.vibrate(50); 
				
				Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
				intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "en-US");
				try {
					Speech_Code = true;
					((Activity) rootView.getContext()).startActivityForResult(intent, RESULT_SPEECH_CODE);
				} catch (ActivityNotFoundException a) {
					Toast.makeText(rootView.getContext(),"Ops! Your device doesn't support Speech to Text",
							Toast.LENGTH_SHORT).show();
				}
			}
		});
        ImageView control_cloud = (ImageView) rootView.findViewById(R.id.control_cloud);
        control_cloud.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
//				setdata = new ArrayList<Myitems>();
//				status = "wocWan";
//				CommandSend = true;
//				Toast.makeText(rootView.getContext(), "Đang truy cập camera ...", Toast.LENGTH_LONG).show();
				woc_intent = true;
				Vibrator vibe = (Vibrator) rootView.getContext().getSystemService(Context.VIBRATOR_SERVICE) ;
				vibe.vibrate(50);
				Intent wocCam = new Intent("com.example.smarthome.WOC");
				rootView.getContext().startActivity(wocCam);			
			}
		});
	}
	
	public void startWoc(String typeWoc){
		Log.v("startWoc","type "+typeWoc);
		if (typeWoc.equals("wocWan")) {
			Intent LaunchIntent = getPackageManager()
					.getLaunchIntentForPackage("com.ivuu");
			if (LaunchIntent != null) {
				// we found the activity
				// now start the activity
				LaunchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(LaunchIntent);
			} else {
				// bring user to the market
				// or let them choose an app?
				LaunchIntent = new Intent(Intent.ACTION_VIEW);
				LaunchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				LaunchIntent.setData(Uri.parse("market://details?id=" + "com.ivuu"));
				startActivity(LaunchIntent);
			}
		} else if (typeWoc.equals("wocLan")) {
			SharedPreferences Url = getSharedPreferences("URLSave", 0);
			UrlWan = Url.getString("URL2", "").toString();
			Intent browserIntent = new Intent(Intent.ACTION_VIEW,Uri.parse("http://"+UrlWan+":8080"));
			//Intent browserIntent = new Intent(Intent.ACTION_VIEW,Uri.parse("http://app.webofcam.com"));
			startActivity(browserIntent);
		}
	}

	public static void garden_view(final View rootView) {  
	    LinearLayout garden_layout_1 = (LinearLayout) rootView.findViewById(R.id.garden_layout_1);
	    LinearLayout garden_layout_2 = (LinearLayout) rootView.findViewById(R.id.garden_layout_2);
	    final CheckBox garden_auto = (CheckBox) rootView.findViewById(R.id.garden_auto);
	    final TimePicker timePicker1 = (TimePicker) rootView.findViewById(R.id.timePicker1);
	    
        garden_layout_1.startAnimation(AnimationUtils.loadAnimation(rootView.getContext(), R.anim.translate_1));
        garden_layout_2.startAnimation(AnimationUtils.loadAnimation(rootView.getContext(), R.anim.translate_2));
        
	    int hour = timePicker1.getCurrentHour();
	    int minute = timePicker1.getCurrentMinute();
	    //kiem tra checkbox hen gio tuoi cay
	    garden_auto.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean ischeck) {
				if(ischeck) {
					Time_set[0] = timePicker1.getCurrentHour();
					Time_set[1] = timePicker1.getCurrentMinute();
					Time_check = true;
					Toast.makeText(rootView.getContext(), "Set "+Time_set[0]+":"+Time_set[1], Toast.LENGTH_SHORT ).show();
				}
				else Time_check = false;
					
			}
		});
	    //thay doi thoi gian -> tat hen gio tuoi cay
        timePicker1.setOnTimeChangedListener(new OnTimeChangedListener() {
			@Override 
			public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
				garden_auto.setChecked(false);		
			}
		});
	      
        ImageView garden_setting = (ImageView) rootView.findViewById(R.id.weather_setting);
        garden_setting.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Vibrator vibe = (Vibrator) rootView.getContext().getSystemService(Context.VIBRATOR_SERVICE) ;
				vibe.vibrate(50);
				Intent Garden_Setting = new Intent("com.example.smarthome.GARDENSETTING");
				rootView.getContext().startActivity(Garden_Setting);
			}
		});
        
        weather weather = new weather(rootView);
        if(AppStatus(rootView))
        	weather.weather_info();
	}

	public static void security_view(final View rootView) {	       
	        LinearLayout garden_1 = (LinearLayout) rootView.findViewById(R.id.security_1);
	        LinearLayout garden_2 = (LinearLayout) rootView.findViewById(R.id.security_2);
	        LinearLayout garden_3 = (LinearLayout) rootView.findViewById(R.id.security_3);
	        ImageView securityAuto = (ImageView) rootView.findViewById(R.id.securityAuto);
	        security_group = (RadioGroup) rootView.findViewById(R.id.security_group);
	        RadioButton security1 = (RadioButton) rootView.findViewById(R.id.security1);
	        RadioButton security2 = (RadioButton) rootView.findViewById(R.id.security2);
	        RadioButton security3 = (RadioButton) rootView.findViewById(R.id.security3);
	        
	        garden_1.startAnimation(AnimationUtils.loadAnimation(rootView.getContext(), R.anim.translate_1));
	        garden_2.startAnimation(AnimationUtils.loadAnimation(rootView.getContext(), R.anim.translate_2));
	        garden_3.startAnimation(AnimationUtils.loadAnimation(rootView.getContext(), R.anim.translate_3));
	        
	        if(security.equals(rootView.getContext().getString(R.string.baomatngoai)))
	        	security_group.check(R.id.security1);
	        else if(security.equals(rootView.getContext().getString(R.string.baomattoanbo)))
	        	security_group.check(R.id.security2);
	        else if(security.equals(rootView.getContext().getString(R.string.baomattudong)))
	        	security_group.check(R.id.security3);
	        else  
	        	security_group.clearCheck();
	        security_first = false;
	        //tat che do bao mat
	        securityAuto.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					security_group.clearCheck();
					security = rootView.getContext().getString(R.string.khongbaomat);
					securitySent(arg0);
					Toast.makeText(rootView.getContext(),security, Toast.LENGTH_SHORT ).show();
					Vibrator vibe = (Vibrator) rootView.getContext().getSystemService(Context.VIBRATOR_SERVICE) ;
					vibe.vibrate(50);
				}
			});//chon 1 trong cac che do bao mat
	        security1.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					security = rootView.getContext().getString(R.string.baomatngoai);
					securitySent(v);
					Toast.makeText(rootView.getContext(),security, Toast.LENGTH_SHORT ).show();
				}
			});
	        security2.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					security = rootView.getContext().getString(R.string.baomattoanbo);
					securitySent(v);
					Toast.makeText(rootView.getContext(),security, Toast.LENGTH_SHORT ).show();
				}
			});
	        security3.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					security = rootView.getContext().getString(R.string.baomattudong);
					securitySent(v);
					Toast.makeText(rootView.getContext(),security, Toast.LENGTH_SHORT ).show();
				}
			});	 
	
	        TextView canhbaoText = (TextView) rootView.findViewById(R.id.canhbaoText);
	        canhbaoText.setText(homeStatus.alert);
	        TextView homeText = (TextView) rootView.findViewById(R.id.homeText);
	        homeText.setText(homeStatus.status);
	        
	        TextView nhietdoText = (TextView) rootView.findViewById(R.id.nhietdoText);
	        nhietdoText.setText(temperature + " \u2103");
	        TextView doamText = (TextView) rootView.findViewById(R.id.doamText);
	        doamText.setText(humidity + " %");
	}
	
	protected static void securitySent(View view) {
		setdata = new ArrayList<Myitems>();
		if(security.equals(view.getContext().getString(R.string.baomattoanbo))){
			Log.v("security",security);
			auto_security = "all";
			for(int i=0;i<roomCollection.size();i++)
				for(int j=0;j<roomCollection.get(groupList.get(i)).size();j++){
					Myitems SecurityItems = roomCollection.get(groupList.get(i)).get(j);
					if (!SecurityItems.name.equals(view.getContext().getString(R.string.bom)) &&
							!SecurityItems.name.equals(view.getContext().getString(R.string.tivi)) ) {
						//SecurityItems.data = "SECURITY";
						setdata.add(SecurityItems);
						setdata.get(setdata.size()-1).data = "SECURITY";
					}
				}
		} else if(security.equals(view.getContext().getString(R.string.baomatngoai))){
			Log.v("security",security);
			auto_security = "out";
			for(int i=0;i<roomCollection.size();i++)
				if (CheckRoom(groupList.get(i),view.getContext()).equals("outside"))
					for (int j = 0; j < roomCollection.get(groupList.get(i)).size(); j++) {
						Myitems SecurityItems = roomCollection.get(groupList.get(i)).get(j);
						if (!SecurityItems.name.equals(view.getContext().getString(R.string.bom)) &&
								!SecurityItems.name.equals(view.getContext().getString(R.string.tivi))) {
							//SecurityItems.data = "SECURITY";
							setdata.add(SecurityItems);
							setdata.get(setdata.size()-1).data = "SECURITY";
						}
					}
		} else if(security.equals(view.getContext().getString(R.string.baomattudong))){
			Log.v("security",security);
			auto_security = "auto";
			//chinh lai thoi gian vd: sau 1h dem se cai bao mat ngoai nha
		}
		else if(security.equals(view.getContext().getString(R.string.khongbaomat))){
			Log.v("security",security);
			auto_security = "off";
			for(int i=0;i<roomCollection.size();i++)
				for(int j=0;j<roomCollection.get(groupList.get(i)).size();j++){
					Myitems SecurityItems = roomCollection.get(groupList.get(i)).get(j);
					if (!SecurityItems.name.equals(view.getContext().getString(R.string.bom))&&
							!SecurityItems.name.equals(view.getContext().getString(R.string.tivi))) {
						//SecurityItems.data = "";
						setdata.add(SecurityItems);
						setdata.get(setdata.size()-1).data = "";
					}
				}
		}
		if (AppStatus(view)) {//kiem tra mang vs gui di
			securitySend=true;
		} 
	}
	private static String CheckRoom(String room,Context context) {
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
	 
	public static void info_view(View rootView) {
		ListView garden_listview = (ListView) rootView.findViewById(R.id.garden_listview);
		ListView security_listview = (ListView) rootView.findViewById(R.id.se_listview);
		garden_log(garden_listview,rootView);
		security_log(security_listview,rootView);
	}
	private static void security_log(ListView security_listview, View rootView) {
		List<String> security_list = new ArrayList<String>();
		SharedPreferences Url = rootView.getContext().getSharedPreferences("Security_log", 0);
		int number = Url.getInt("number", 0);
		for(int i=0;i<number;i++){
			security_list.add(Url.getString("log"+i, ""));
		}
		security_listview.setAdapter(new ArrayAdapter<String>(rootView
				.getContext(), android.R.layout.simple_list_item_1,security_list));
		
	}
	private static void garden_log(ListView garden_listview, View rootView) {
		int i;
		List<Myitems> tmp = new ArrayList<Myitems>();
		for(i=0;i<groupList.size();i++) {
			if(groupList.get(i).equals(rootView.getContext().getString(R.string.vuonsau))) break;
			Log.v("main",groupList.get(i));
		}
		Log.v("main", "i="+i + "-" + groupList.size() );
		if (i < groupList.size()) {
			tmp = roomCollection.get(groupList.get(i));
		}
		if (tmp != null) {
			for (i = 0; i < tmp.size(); i++)
				if ((tmp.get(i).name).equals(rootView.getContext().getString(R.string.bom))) break;
			//i chinh la vi tri cua bom
			if (i < tmp.size()) {//Neu co ton tai bom
				// Can them phan tich giu lieu data cua bom o day
				garden_log = new ArrayList<String>();
				garden_log.add(tmp.get(i).data);
				garden_listview.setAdapter(new ArrayAdapter<String>(rootView
						.getContext(), android.R.layout.simple_list_item_1,garden_log));
			}
		}
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
			if(jsonTry!=1)	Thread.sleep(100);
			ControlData controldata = new ControlData(context);
			if(typeURL.equals("Lan")){ timeout=1000; timesTry=1;}
			else if(typeURL.equals("Wan")) {timeout=1000; timesTry=2;}
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
						String tag = json.getString("tag");
						Log.v("onPost","tag "+tag);
						if(status.equals("updateinfo_at")){
							updateInfo(json);
						}
						else if(tag.equals("getinfo"))
							getInfo(json);
						else if(tag.equals("wocWan"))
							startWoc("wocWan");
						else if(tag.equals("wocLan"))
							startWoc("wocLan");
					} else {
						if(tv_lg_channel==true){
							tv_lg_channel=false;
							Log.v("tv_lg_channel","open tv app");
							Intent LaunchIntent = getPackageManager().getLaunchIntentForPackage("com.clipcomm.WiFiRemocon");
				   			startActivity(LaunchIntent);
						}
						if (jsonTry < timesTry) {
							jsonTry++;
							myTask = new MyTask(context);
							myTask.execute();
						} else {//Neu gui lai 10 lan nhung van ko thanh cong
							jsonTry = 1;
							Log.v("MyTask","send over times");
							if(status.equals("updateinfo")){
							    status = "updateinfo_at";
							    sendcommand();
							}
						}
					}
				}
			} catch (JSONException e) {
			} catch (Exception e) {
			}
			super.onPostExecute(result);
		}
	}

	public void sendcommand() {
		if (NetworkStatus()) {
			myTask = new MyTask(this);
			myTask.execute();
		} else{
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
	
	private void GCM_init() {
		//server may ao
		registration_ids = new ArrayList<String>();
		registration_ids.add("APA91bEZpFFWQTv3ieIVhitkLdStC-DhjDtkuTUcJGggdgJ0S7VEaekChQBZGyWLWyk3tumRD2fIieY_65R24WlOpDo4RnWEibqa90TMRlUiF0TzwOWDzzBKawEKsNXFj-bS6GKX-Guwchu-SFpWrO3E61GzZObRmw");	
		SharedPreferences ids = getSharedPreferences("Id_server", 0);
		SharedPreferences.Editor editor = ids.edit();
		Log.v("GCM-init","size = "+registration_ids.size());
		editor.putInt("number", registration_ids.size()); 
		for(int i=0;i<registration_ids.size();i++){
			editor.putString("ids"+i, registration_ids.get(i));
			Log.v("GCM-init","ids = "+registration_ids.get(i));
		}
		editor.commit();
		//registration_ids.add("APA91bH-ncmDK6KEPdKMEbPtvhpmp0JFRZMz6vC5eAkr24e1XBDzfz_qIoopsooFIx_cmxzuzZO_og0KC1IXBdCBIjr-qh9hkemdySTBNC39vjWK5CnD-vj9KfUlFuJ91Jlogtkq_eIwU3NmkYUh3AvJ29LeKSBdAw");
		//sau nay can lay tu server check lai
		if (checkPlayServices()) {
        	Log.v("main","Play server Check");	
        	RegisterWithGCM();
        } else {
            Log.i(TAG, "No valid Google Play Services APK found.");
		}
	}

	private boolean checkPlayServices() {
		int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
		if (resultCode != ConnectionResult.SUCCESS) {
			if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
				GooglePlayServicesUtil.getErrorDialog(resultCode, this,PLAY_SERVICES_RESOLUTION_REQUEST).show();
			} else {
				Log.i(TAG, "This device is not supported.");
				Toast.makeText(context, "This device is not supported", Toast.LENGTH_SHORT).show();
			}
			return false; 
		}
		return true;
	}

	private void RegisterWithGCM() {
		GCMRegistrar.checkDevice(this);
		GCMRegistrar.checkManifest(this);
		id = GCMRegistrar.getRegistrationId(this);
		if (id.equals("")) {
			GCMRegistrar.register(this, SENDER_ID); // Note: get the sender id								
		} else {
			Log.v("Registration", "Already registered, regId: " + id);
			if(NetworkStatus())
				sendNofication(id,"new_id");
		}
	}
	
	public static void sendNofication(final String Myid, final String Nstatus) {
			new AsyncTask<Void, Void, Void>() {
				@Override
				protected Void doInBackground(Void... arg0) {
					JSONObject json = new JSONObject();
					JSONArray ids = new JSONArray();
					JSONObject data = new JSONObject();
					for(int i=0;i<registration_ids.size();i++)
						ids.put(registration_ids.get(i));
					try {
						if(Nstatus.equals("new_id"))
							data.put("status", Nstatus);
						else if(Nstatus.equals("off_alert"))
							data.put("status", Nstatus);
						else if(Nstatus.equals("security")){
							data.put("status", Nstatus);
							data.put("info", auto_security);
							data.put("sms", sendSms);
							json.put("time_to_live", 300);
						}else if(Nstatus.equals("woc")){
							data.put("status", Nstatus);
							data.put("info", status);
							json.put("time_to_live", 30);
						}else if(Nstatus.equals("resetServer")){
							data.put("status", "reset");
							json.put("time_to_live", 30);
						}
								
						json.put("registration_ids", ids);
						data.put("id", Myid);
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
					Log.v(TAG,"Da gui notification");
					super.onPostExecute(result);
				}
			}.execute(null, null, null);
		}
	
}
