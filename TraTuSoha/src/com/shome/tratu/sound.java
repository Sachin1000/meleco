package com.shome.tratu;

import java.io.File;
import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.renderscript.Element;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class sound extends AsyncTask<Void, Void, Void>{
	String url;
	Elements el;
	View view;
	String soundUrl;
	public sound(String url, View view){
		this.url = url;
		this.view = view;
	}
	@Override
	protected Void doInBackground(Void... arg0) {
		try{
			Document doc = Jsoup.connect(url).get();
			el = doc.getElementsByTag("param").select("[name=FlashVars]");	
			soundUrl = el.attr("value");
			soundUrl = soundUrl.substring(soundUrl.indexOf("http:"),soundUrl.indexOf(".mp3")+4);
		}catch(Exception e){
			
		}
		return null;
	}
	
	@Override
	protected void onPostExecute(Void result) {
		Log.v("sound",soundUrl);
		try{	
		Intent intent = new Intent();  
		intent.setAction(android.content.Intent.ACTION_VIEW);  
		intent.setDataAndType(Uri.parse(soundUrl), "audio/*");  
        view.getContext().startActivity(intent);
		}catch(Exception e){
			
		}
	}
}
