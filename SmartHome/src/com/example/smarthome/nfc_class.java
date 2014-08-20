package com.example.smarthome;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.example.smarthome.MainActivity.ExpandableListAdapter;
import com.example.smarthome.MainActivity.SectionsPagerAdapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class nfc_class extends Activity implements android.widget.RadioGroup.OnCheckedChangeListener {

	private static Nfc_ExpandableListAdapter expListAdapter;
    private static List<String> groupList;
    private static Map<String, List<Myitems>> roomCollection;
    private static ExpandableListView expListView;
    private MyDatabase database = new MyDatabase(this);
    private static ArrayList<String> nfc_save;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.nfc_list);
		nfc_save = new ArrayList<String>();
		nfc_save.add("security-OFF");
		
		database.open();
        roomCollection = database.getData();
        groupList=database.Getgroup();
        database.close();
        
        for(int i=0;i<groupList.size();i++){
        	for(int j=0;j<roomCollection.get(groupList.get(i)).size();j++){
        		roomCollection.get(groupList.get(i)).get(j).value="OFF";
        	}
        }
        
		expListAdapter = new Nfc_ExpandableListAdapter(this, groupList, roomCollection);
		expListView = (ExpandableListView) findViewById(R.id.nfc_ExpandList);
        expListView.setAdapter(expListAdapter);
        
        final RadioGroup nfc_radio_group = (RadioGroup) findViewById(R.id.nfc_radio_group);
		nfc_radio_group.setOnCheckedChangeListener(this);
	}
	
	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		switch(checkedId){
		case R.id.nfc_radio_1: nfc_save.set(0, "security-OFF"); break;
		case R.id.nfc_radio_2: nfc_save.set(0, "security-ALL");break;
		case R.id.nfc_radio_3: nfc_save.set(0, "security-AUTO");break;
		}
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		
		Intent i = new Intent("com.example.smarthome");
	    i.putExtra("message","Save_nfc");
	    i.putStringArrayListExtra("nfc_data", nfc_save);
		sendBroadcast(i);
	}
	
	public int check(int groupPosition, int childPosition){
		for(int i=1;i<nfc_save.size();i++){
			String [] a = nfc_save.get(i).split("-");
			if(groupPosition== Integer.valueOf(a[0]) && childPosition== Integer.valueOf(a[1]))
				return i;
		}
		return nfc_save.size();
	}
	
	public class Nfc_ExpandableListAdapter extends BaseExpandableListAdapter {

		private Activity context;
		private Map<String, List<Myitems>> roomCollections;
		private List<String> rooms;

		public Nfc_ExpandableListAdapter(Activity context, List<String> rooms,
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
				convertView = inflater.inflate(R.layout.nfc_items, null);
			}
			final CheckBox nfc_childname = (CheckBox) convertView.findViewById(R.id.nfc_childname);
			nfc_childname.setText(items);
			nfc_childname.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (nfc_childname.isChecked() == true) {
						if (check(groupPosition, childPosition) == nfc_save.size()) 
							nfc_save.add(groupPosition + "-" + childPosition + "-" + value);
					} else {
						int i = check(groupPosition, childPosition);
						if (i != nfc_save.size()) 
							nfc_save.remove(i);
					}
				}
			});
			
			final Switch nfc_switch = (Switch) convertView.findViewById(R.id.nfc_switch);
			nfc_switch.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					String Value = "";
					if(nfc_switch.isChecked() == true){
						roomCollection.get(groupList.get(groupPosition)).get(childPosition).value="ON";
						Value = "ON";
					}
				    else {
				    	roomCollection.get(groupList.get(groupPosition)).get(childPosition).value="OFF";
				    	Value = "OFF";
				    }
					int i = check(groupPosition, childPosition);
					if(i != nfc_save.size())
						nfc_save.set(i, groupPosition + "-" + childPosition + "-" + Value);
					else 
						nfc_save.add(groupPosition + "-" + childPosition + "-" + Value);
					
				}
			});
			
			
			
			return convertView;
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
				convertView = infalInflater.inflate(R.layout.nfc_group,null);
			}
			TextView nfc_groupName = (TextView) convertView.findViewById(R.id.nfc_groupName);
			nfc_groupName.setText(ItemsName);
			return convertView;
		}

		public boolean hasStableIds() {
			return true;
		}

		public boolean isChildSelectable(int groupPosition, int childPosition) {
			return true;
		}

		
	}
	
	
}
