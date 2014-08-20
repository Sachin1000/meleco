package com.shome.tratu;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Toast;


public class MainActivity extends FragmentActivity{
	AdView adView;
	boolean AdViewStarted = false;
	TratuPaperAdapter mSectionPagerAdapter;
	ViewPager mViewPager;
	final int LanguageCode = 88;
	List<String> page;
	boolean first = false;
	int timeUse;
	
	BroadcastReceiver mReceiver;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);  		
		setContentView(R.layout.activity_main);
		setProgressBarIndeterminateVisibility(false);
		
		if(NetworkStatus()==true){
			getData();
			startView();
		}else finish();
		startAdmob();
		rate();
		init_receive();
	}
	
	private void init_receive() {
		IntentFilter intentFilter = new IntentFilter("com.shome.tratu");
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                //extract our message from intent
                String msg_for_me = intent.getStringExtra("message");
                Log.v("receive broadcast",msg_for_me);
				if (msg_for_me.equals("startProgress")) {
					setProgressBarIndeterminateVisibility(true);
				}else if(msg_for_me.equals("stopProgress")){
					setProgressBarIndeterminateVisibility(false);
				}
					
            }
        };
        this.registerReceiver(mReceiver, intentFilter);
	}
	
	public void rate(){
		//countDown time bình luận
		SharedPreferences Time = getSharedPreferences("timeUse", 0);
		timeUse = Time.getInt("timeuse", 0);
		if(timeUse<=50) timeUse++;
		if(timeUse%10==0 && timeUse>0 && timeUse<=50) {
			Intent intent = new Intent(this,Rate.class);
			startActivity(intent);
		}
		SharedPreferences.Editor editor = Time.edit();
		editor.putInt("timeuse", timeUse);
		editor.commit();
	}

	private void getData() {
		page = new ArrayList<String>();
		SharedPreferences share = getSharedPreferences("PageTratu", 0);
		int size = share.getInt("size", 0);
		for(int i=0;i<size;i++) 
			page.add(share.getString(String.valueOf(i),""));
		if(size==0) {
			page.add("en_vn");page.add("vn_en");
		}
		
		SharedPreferences firsttime = getSharedPreferences("firstTime", 0);
		first = firsttime.getBoolean("first", true);			
	}

	public void startView(){
		
		mSectionPagerAdapter = new TratuPaperAdapter(getSupportFragmentManager(),page,first);
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionPagerAdapter);
		
		//mViewPager.setCurrentItem(0);
		//mViewPager.setOffscreenPageLimit(3);
		//mViewPager.setPageMargin(20);
		//mViewPager.setPageMarginDrawable(R.color.text);
		//mViewPager.setOnPageChangeListener(this);
	}	 

	private void CheckLanguage(String language) {
		page = new ArrayList<String>();
		
		if(language.equals("anhviet")){
			page.add("en_vn");page.add("vn_en");}
		else if(language.equals("vietanh")){
			page.add("vn_en");page.add("en_vn");}
		else if(language.equals("anhanh"))
			page.add("en_en");
		else if(language.equals("phapviet")){
			page.add("fr_vn");page.add("vn_fr");}
		else if(language.equals("vietphap")){
			page.add("vn_fr");page.add("fr_vn");}
		else if(language.equals("vietviet"))
			page.add("vn_vn");
		else if(language.equals("nhatviet")){
			page.add("jp_vn");page.add("vn_jp");}
		else if(language.equals("vietnhat")){
			page.add("vn_jp");page.add("jp_vn");}
		else if(language.equals("anhnhat")){
			page.add("en_jp");page.add("jp_en");}
		else if(language.equals("nhatanh")){
			page.add("jp_en");page.add("en_jp");}
		else if(language.equals("hanviet"))
			page.add("kr_vn");
		else if(language.equals("trungviet"))
			page.add("cn_vn");
		
		SharedPreferences share = getSharedPreferences("PageTratu", 0);
		SharedPreferences.Editor editor = share.edit();
		editor.putInt("size", page.size());
		for(int i=0;i<page.size();i++)
			editor.putString(String.valueOf(i),page.get(i));
		editor.commit(); 
		
		startView();
	}
	
	private void startAdmob() {
		adView = (AdView) this.findViewById(R.id.adView);
		 AdRequest adRequest = new AdRequest.Builder()
	        .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
	        .addTestDevice("169026D120B937204FEB737E77B42754")
	        .addTestDevice("76DDC4E5CB5D0F0C470DFD7095C47A83")
	        .addTestDevice("14135E6299CD161001CC56E57F1093F1")
	        .build();
	    adView.loadAd(adRequest);
	    AdViewStarted = true;
	}
	public boolean NetworkStatus() {
		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
		if (networkInfo == null) {
			Toast.makeText(this, "Kiểm tra lại kết nối ...",Toast.LENGTH_SHORT).show();
			return false;
		} else{ 
			return true;
		}			
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(AdViewStarted){
		    adView.destroy();
		    AdViewStarted = false;
		    unregisterReceiver(mReceiver);
		}	
	}
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_change:
			Intent sendIntent = new Intent();
			sendIntent.setClass(MainActivity.this, Language.class);
			startActivityForResult(sendIntent, LanguageCode);
			break;
		}
		return false;
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Log.v("result","code: "+requestCode + " - result: "+resultCode);
		try {// nhan Buddle
			switch (requestCode) {
			case LanguageCode: 
				if (resultCode == Activity.RESULT_OK) {
					Bundle Returndata = data.getExtras();
					String language = Returndata.getString("language");	
					CheckLanguage(language);
				} 
				break;
			}
		} catch (Exception e) {
			//Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
		}
	}



}
