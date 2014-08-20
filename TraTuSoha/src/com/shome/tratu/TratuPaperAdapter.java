package com.shome.tratu;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.View.OnFocusChangeListener;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.Toast;

public class TratuPaperAdapter extends FragmentPagerAdapter {
	static List<String> keywords;
	static List<String> page;
	int count;
	static boolean first;
	
	public TratuPaperAdapter(FragmentManager fm, List<String> Page, boolean first) {
		super(fm);
		keywords = new ArrayList<String>();
		this.count = Page.size();
		this.page = Page;
		this.first = first;
		for(int i=0;i<Page.size();i++){
			keywords.add("");
		}
	}

	@Override
	public Fragment getItem(int position) {
		Fragment fragment = new DummySectionFragment();
		Bundle args = new Bundle();
		args.putInt(DummySectionFragment.ARG_SECTION_NUMBER, position + 1);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public int getCount() {
		// Show 2 total pages.
		return count;
	}

	@Override
	public CharSequence getPageTitle(int position) {
		Locale l = Locale.getDefault();
		return NameTitle(page.get(position)).toUpperCase(l); 
	}
	
	public String NameTitle(String name){
		if(name.equals("en_vn")) return "Anh - Việt";
		else if(name.equals("vn_en")) return "Việt - Anh";
		else if(name.equals("en_en")) return "Anh - Anh";
		else if(name.equals("fr_vn")) return "Pháp - Việt";
		else if(name.equals("vn_fr")) return "Việt - Pháp";
		else if(name.equals("vn_vn")) return "Việt - Việt";
		else if(name.equals("jp_vn")) return "Nhật - Việt";
		else if(name.equals("vn_jp")) return "Việt - Nhật";
		else if(name.equals("en_jp")) return "Anh - Nhật";
		else if(name.equals("jp_en")) return "Nhật - Anh";
		else if(name.equals("kr_vn")) return "Hàn - Việt";
		else if(name.equals("cn_vn")) return "Trung - Việt";
		else return "";
		
	}
	
	public static class DummySectionFragment extends Fragment {

		public static final String ARG_SECTION_NUMBER = "section_number";

		public DummySectionFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.page, container, false);
			init(rootView,getArguments().getInt(ARG_SECTION_NUMBER));
			return rootView;
		}
	}
	
	private static void init(final View view, final int position) {
		final SearchView searchMenuItem;
		
		searchMenuItem = (SearchView) view.findViewById(R.id.search);// get my MenuItem with placeholder submenu
		searchMenuItem.setQueryHint("Tìm kiếm");
		//searchMenuItem.setIconified(false);
		searchMenuItem.setIconifiedByDefault(false);
		searchMenuItem.setSubmitButtonEnabled(false);
		
		searchMenuItem.setOnQueryTextListener(new OnQueryTextListener() {
			int pos = position;
			@Override
			public boolean onQueryTextSubmit(String arg) {
				//textview.setText(arg);
				ParseHTML parse = new ParseHTML("http://tratu.soha.vn/dict/"+page.get(pos-1)+"/"+arg,view, 
						arg,view.getContext(),keywords, pos);
				parse.execute();
				searchMenuItem.clearFocus();	
				
				Log.v("keyword",keywords.toString());
				Intent i = new Intent("com.shome.tratu");
				i.putExtra("message", "startProgress");
				view.getContext().sendBroadcast(i);
				return true;
			}
			@Override
			public boolean onQueryTextChange(String arg0) {
				// TODO Auto-generated method stub
				return false;
			}
		});
		
		ImageView Sound = (ImageView) view.findViewById(R.id.sound);
		Sound.setOnClickListener(new OnClickListener() {
			int pos = position;
			@Override
			public void onClick(View arg0) {
				String url = "";
				if(keywords.size()>=pos) {
					url = keywords.get(pos-1);
				}
				Log.v("keyword",keywords.toString() +"-");
				if(!url.equals("")) {
					Intent intent = new Intent();  
					intent.setAction(android.content.Intent.ACTION_VIEW);  
					intent.setDataAndType(Uri.parse(url), "audio/*");  
			        view.getContext().startActivity(intent);
				}		
				Vibrator vibe = (Vibrator) view.getContext().getSystemService(Context.VIBRATOR_SERVICE);
				vibe.vibrate(50);
				if(first) {
					first = false;
					Toast.makeText(view.getContext(), "Bạn nên chọn chương trình nghe nhạc (Music app) làm mặc định", Toast.LENGTH_LONG).show();
					SharedPreferences firsts = view.getContext().getSharedPreferences("firstTime", 0);
					SharedPreferences.Editor editor = firsts.edit();
					editor.putBoolean("first", first);
					editor.commit();	
				}
			}
		});
		
	}
}
