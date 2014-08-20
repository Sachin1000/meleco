package com.shome.tratu;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ParseHTML extends AsyncTask<Void, Void, Void>{
	View view;
	String url;
	Elements el;
	Elements et;
	TextView textview;
	TextView phienam;
	Elements hTags;
	String keywords;
	Context context;
	List<String> soundkey;
	int pos;
	
	public ParseHTML(String url, View view, String keywords, Context context, List<String> soundkey, int pos) {
		textview = (TextView) view.findViewById(R.id.textview);
		phienam = (TextView) view.findViewById(R.id.phienam);
		phienam.setTypeface(null, Typeface.BOLD);
		this.url = url.replace(" ", "%20");
		this.keywords = keywords;
		this.context = context;
		this.soundkey = soundkey;
		this.pos = pos;
	}
	@Override
	protected Void doInBackground(Void... arg0) {
		Log.v("main","step 1 "+url); 
		HTTP http = new HTTP();
		Document doc = Jsoup.parse(http.getGETfromUrl(url));
		Log.v("main","step 2"); 
		try{
		el = doc.select("div#bodyContent");
		hTags = el.get(0).select("h2, h3, h5, dd");
        }catch(Exception e){
		}
		
		try{//lấy sound url
			el = doc.getElementsByTag("param").select("[name=FlashVars]");	
			String soundUrl = el.attr("value");
			soundUrl = soundUrl.substring(soundUrl.indexOf("http:"),soundUrl.indexOf(".mp3")+4);
			if(soundkey.size()>=pos)
				soundkey.set(pos-1,soundUrl);
		}catch(Exception e){		
		}
		return null;
	}
	@Override
	protected void onPostExecute(Void result) {
		try{
		//.v("parse","lỗi 0");
		String pa = hTags.get(1).select("h5").text();
		int Istart = 2;
		if(!pa.equals(""))
		    phienam.setText(pa);
		else {
			phienam.setText("");
			Istart = 1;
		}
		
		String text = "";
		int start = 0;
		List<StartEnd> h2 = new ArrayList<StartEnd>();
		List<StartEnd> h3 = new ArrayList<StartEnd>();
		List<StartEnd> h5 = new ArrayList<StartEnd>();
		List<StartEnd> dd = new ArrayList<StartEnd>();
		//Log.v("parse","lỗi 1");
		for(int i=Istart;i<hTags.size();i++){
			Element tag = hTags.get(i);
			StartEnd se = new StartEnd();
			if(!tag.select("h2").text().equals("")) {
				if(i>2) text += "\n";
				text += tag.select("h2").text() + "\n";
			    se.start = start;
			    se.end = text.length();
			    start = se.end;
			    h2.add(se);
			}
			else if(!tag.select("h3").text().equals("")){
				text += "  ";
				text += tag.select("h3").text() + "\n";
				se.start = start;
				se.end = text.length();
				start = se.end;
				h3.add(se);
		    }
			else if(!tag.select("h5").text().equals("")){
				text += "     ►";
				text += tag.select("h5").text() + "\n";
				se.start = start;
				se.end = text.length();
				start = se.end;
				h5.add(se);
			} else if(!tag.select("dd").text().equals("")){
				if(tag.select("dd").select("dd").size()>1) continue;
				text += "        ";
//				if(tag.select("dd").select("strong.selflink").size()!=0) 
//					text += "▼";
				text += tag.select("dd").text() + "\n";
				se.start = start;
				se.end = text.length();
				start = se.end;
				dd.add(se);
			}
		}
		//Log.v("parse","lỗi 2");
		SpannableString spanString = new SpannableString(text);
		for(StartEnd startend : h2){
			spanString.setSpan(new RelativeSizeSpan(1.6f), startend.start, startend.end, 0);	
			spanString.setSpan(new ForegroundColorSpan(Color.rgb(90, 140, 0)), startend.start, startend.end, 0);	
		}
		for(StartEnd startend : h3){
			spanString.setSpan(new RelativeSizeSpan(1.3f), startend.start, startend.end, 0);	
			spanString.setSpan(new StyleSpan(Typeface.BOLD), startend.start, startend.end, 0);	
			spanString.setSpan(new ForegroundColorSpan(Color.rgb(0, 150, 180)), startend.start, startend.end, 0);
		}
		for(StartEnd startend : h5)
			spanString.setSpan(new StyleSpan(Typeface.BOLD), startend.start, startend.end, 0);	
		for(StartEnd startend : dd){
			int startKey = text.substring(startend.start, startend.end).toLowerCase().indexOf(keywords);
			if(startKey!=-1)
				spanString.setSpan(new ForegroundColorSpan(Color.rgb(120, 0, 0)), startend.start+ startKey, 
						startend.start+ startKey+keywords.length(), 0);	
		}
		//Log.v("parse","lỗi 3");
		textview.setText(spanString);
		}catch(Exception e){
			Toast.makeText(context, "Không tìm thấy từ", Toast.LENGTH_SHORT).show();
			//textview.setText("Không tìm thấy từ \""+keywords+"\" trong từ điển");
		}
		
		Intent i = new Intent("com.shome.tratu");
		i.putExtra("message", "stopProgress");
		context.sendBroadcast(i);
	}
	
	private class StartEnd{
		int start;
		int end;
		
		public StartEnd(){
			start = end = 0;
		}
	}
}
