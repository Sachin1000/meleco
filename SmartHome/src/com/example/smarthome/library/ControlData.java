package com.example.smarthome.library;

import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.smarthome.Myitems;
import com.example.smarthome.R;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public class ControlData {
	
	private JSONParser jsonParser;
	
	//private static String URL = "http://192.168.1.18/";
	
	/*private static String loginURL = "http://localhost/android_login_api/ ";
	private static String registerURL = "http://localhost/android_login_api/";*/
	
	private static String update_tag = "updateinfo";
	private static String update_tag_at = "updateinfo_at";
	private static String getInfo_tag = "getinfo";
	private static String setInfo_tag = "setinfo";
	private static String control_tag = "control";
	private static String alert_tag = "offalert";
	private static String woc_Wan_tag = "wocWan";
	private static String woc_Lan_tag = "wocLan";
	private static String woc_Off_tag = "wocOff";
	private static String security_auto_tag = "auto_security";
	private static String security_off_tag = "auto_security_off";
	private static String security_out_tag = "auto_security_out";
	private static String security_all_tag = "auto_security_all";
	private static String reset_tag = "reset";
	
	
	private Context context;
	
	//Hàm xây dựng khởi tạo đối tượng
	public ControlData(Context conText){
		context = conText;
		jsonParser = new JSONParser();//tao 1 goi json 
	}
	
	public JSONObject setInfo(List<Myitems> data, String URL, String typeURL, String Port, int timeout, String status){
		//Xây dựng các giá trị - tao 1 list cac gia tri
		Log.v("control","Type URL:  " + typeURL +"-"+ Port);
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		String typeIP = "UDP";
		if(status.equals(control_tag)) params.add(new BasicNameValuePair("tag", control_tag));
		else if(status.equals(reset_tag)) params.add(new BasicNameValuePair("tag", reset_tag));
		else if(status.equals(update_tag_at)) {
			params.add(new BasicNameValuePair("tag", getInfo_tag));
			typeIP = "TCP";
		}
		else{
			if(typeURL.equals("Lan")) URL="192.168.1.28";
			Port="8765";
			typeIP = "TCP";
			if(status.equals(woc_Lan_tag)) params.add(new BasicNameValuePair("tag", woc_Lan_tag));
			else if(status.equals(woc_Wan_tag)) params.add(new BasicNameValuePair("tag", woc_Wan_tag));
			else if(status.equals(woc_Off_tag)) params.add(new BasicNameValuePair("tag", woc_Off_tag));
			else if(status.equals(setInfo_tag)) params.add(new BasicNameValuePair("tag", setInfo_tag));
		    else if(status.equals(getInfo_tag)) params.add(new BasicNameValuePair("tag", getInfo_tag));
		    else if(status.equals(update_tag)) params.add(new BasicNameValuePair("tag", getInfo_tag));
		    else if(status.equals(security_auto_tag)) params.add(new BasicNameValuePair("tag", security_auto_tag));
		    else if(status.equals(security_off_tag)) params.add(new BasicNameValuePair("tag", security_off_tag));
		    else if(status.equals(security_out_tag)) params.add(new BasicNameValuePair("tag", security_out_tag));
		    else if(status.equals(security_all_tag)) params.add(new BasicNameValuePair("tag", security_all_tag));
		}
		//Đặc biệt cho tivi
		for(int i=0;i<data.size();i++){
			if(data.get(i).name.equals(context.getString(R.string.tivi)))
				if(data.get(i).value.equals("ON")|| data.get(i).value.equals("OFF") )
					data.get(i).data = context.getString(R.string.power);			
		}
		
		
		if(data.size()>0){
		List<String> add = new ArrayList<String>(); 
		List<String> value = new ArrayList<String>();
		List<String> name = new ArrayList<String>();
		add.add(data.get(0).add);
		int i,j,number;
		String Noitems = new String();		
		String room = Room(data.get(0).room)+"-";
		String DataItems = null;
		String DataItems2 = null;
		if(data.get(0).name.equals(context.getString(R.string.tivi)))
			DataItems= Channel(data.get(0).data) + "-";
		else 
			DataItems=Security(data.get(0).data) + "-";
		
		for(i=0;i<3;i++) {
			value.add(""); //tao gia tri khoi dau cho value
			name.add(""); //------------------------  name
		}
		for(i=0,j=0,number=1;i<data.size();i++,j++){ //tim xem co bao nhieu add
			if(add.get(number-1).equals(data.get(i).add)) {
				value.set(j,value.get(j)+Value(data.get(i).value)+"-");
				name.set(j,name.get(j)+Name(data.get(i).name)+"-");
			}
			else{
				Noitems+=String.valueOf(j)+"-";
				if(j<3) for(;j<3;j++){
					value.set(j,value.get(j)+Value("OFF")+"-");
					name.set(j,name.get(j)+"0"+"-");
				}
				j=0;
				value.set(j,value.get(j)+Value(data.get(i).value)+"-");
				name.set(j,name.get(j)+Name(data.get(i).name)+"-");
				add.add(data.get(i).add);
				room+=Room(data.get(i).room)+"-";
				DataItems+=Security(data.get(i).data) + "-";
				if(data.get(i).name.equals(context.getString(R.string.tivi)))
					DataItems+=Channel(data.get(i).data) + "-";
				else
					DataItems+=Security(data.get(i).data) + "-";
				number++; 
			}    
	    }
		Noitems+=String.valueOf(j)+"-";
		if(j<3) for(;j<3;j++){
			value.set(j,value.get(j)+Value("OFF")+"-");
			name.set(j,name.get(j)+"0"+"-");
		}
				
		params.add(new BasicNameValuePair("no", String.valueOf(number)+"-")); //tong so dia chi
		params.add(new BasicNameValuePair("Nu", Noitems)); //tong so items trong 1 dia chi
		params.add(new BasicNameValuePair("Ro", room)); //ten phong cua dia chi
		params.add(new BasicNameValuePair("data", DataItems)); // co an ninh hay ko
		 //ten thiet bi item
		String address = new String();
		for (i = 0; i < number; i++)
			address += add.get(i) + "-";
		params.add(new BasicNameValuePair("add", address));
		for (i = 0; i < 3; i++) 
			params.add(new BasicNameValuePair("relay"+String.valueOf(i+1), value.get(i) ));
		for (i = 0; i < 3; i++) 
			params.add(new BasicNameValuePair("name"+String.valueOf(i+1), name.get(i)));
		}
		
        Log.v("Control","http://"+URL+":"+Port+"/");
        Log.v("Control","params:"+params.toString());
		//1 vd Json: tag=login&email=quytruong@gmail.com&password=abc123
		//Gui 1 Json Parser di va nhan 1 Json Object ve, tra ve object
        String result = "{\"tag\":\"login\",\"success\":0}";
        JSONObject json = new JSONObject();
        try {
        	json = new JSONObject(result);
		} catch (JSONException e) {
			Log.e("JSON result", "Error parsing data " + e.toString());
		}
        if(typeIP.equals("TCP"))
        	json = jsonParser.getJSONFromUrl("http://"+URL+":"+Port+"/", params, timeout);
        else if(typeIP.equals("UDP"))
        	sendUDP(URL,Integer.valueOf(Port),params.toString());
		//tuong duong Push
		//Trả về đối tượng là 1 JSONObject
		return json;
	} 
	
	public void sendUDP(String URL, int Port, String data) {

		try {
			DatagramSocket s = new DatagramSocket();
			InetAddress local = InetAddress.getByName(URL);
			int msg_length = data.length();
			byte[] message = data.getBytes();
			DatagramPacket p = new DatagramPacket(message, msg_length, local, Port);
			s.send(p);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	
	private String Security(String data) {
		if(data.equals("SECURITY"))
			return "1";
		else return "0";
	}
	
	private String Channel(String data) {
		Log.v("Control","channel="+data);
		if(data.equals(context.getString(R.string.vtv1)))
			return " "; //32
		else if(data.equals(context.getString(R.string.vtv2)))
			return "!"; //33
		else if(data.equals(context.getString(R.string.vtv3)))
			return "\""; //34
		else if(data.equals(context.getString(R.string.htv1)))
			return ")";  //35
		else if(data.equals(context.getString(R.string.htv2)))
			return "*";  //35
		else if(data.equals(context.getString(R.string.htv3)))
			return "+";  //35
		else if(data.equals(context.getString(R.string.htv4)))
			return ",";  //35
		else if(data.equals(context.getString(R.string.htv7)))
			return "#";  //35
		else if(data.equals(context.getString(R.string.cartoon)))
			return "$";  //36
		else if(data.equals(context.getString(R.string.discovery)))
			return "%";  //37
		else if(data.equals(context.getString(R.string.disney)))
			return "&";  //38
		else if(data.equals(context.getString(R.string.hbo)))
			return "'";  //39
		else if(data.equals(context.getString(R.string.starmovie)))
			return "(";  //40
		else if(data.equals(context.getString(R.string.geographic)))
			return "-";  //45
		else if(data.equals(context.getString(R.string.soha)))
			return "A";  //48
		else if(data.equals(context.getString(R.string.youtube)))
			return "B";	 //65
		else if(data.equals(context.getString(R.string.nhaccuatui)))
			return "C";	 //65
		else if(data.equals(context.getString(R.string.power)))
			return ".";	 //65
		else if(data.equals(context.getString(R.string.down)))
			return "a";	 //65
		else if(data.equals(context.getString(R.string.up)))
			return "b";	 //65
		else if(data.equals(context.getString(R.string.right)))
			return "c";	 //65
		else if(data.equals(context.getString(R.string.left)))
			return "d";	 //65
		else if(data.equals(context.getString(R.string.volumteUP)))
			return "e";	 //65
		else if(data.equals(context.getString(R.string.volumteDown)))
			return "f";	 //65
		else if(data.equals(context.getString(R.string.home)))
			return "g";	 //65
		else if(data.equals(context.getString(R.string.back)))
			return "h";	 //65
		else if(data.equals(context.getString(R.string.mute)))
			return "i";	 //65
		else if(data.equals(context.getString(R.string.enter)))
			return "j";	 //65	
		else if(data.equals(context.getString(R.string.channelUp)))
			return "k";	 //65
		else if(data.equals(context.getString(R.string.channelDown)))
			return "l";	 //65
		else if(data.equals(context.getString(R.string.menu)))
			return "m";	 //65
		else if(data.equals(context.getString(R.string.exit)))
			return "n";	 //65
		else return "~";
	}

	public String Value(String value){ 
		if(value.equals("ON")) return "1";
		else if(value.equals("OFF")) return "2";
		else if(value.equals("AUTO")) return "3";
		else return "0"; 
	}
	public String Room(String value){
		if(value.equals(context.getString(R.string.phongkhach))) return "0";
		else if(value.equals(context.getString(R.string.phongngu))) return "1";
		else if(value.equals(context.getString(R.string.phongbep))) return "2";
		else if(value.equals(context.getString(R.string.phonglamviec))) return "3";
		else if(value.equals(context.getString(R.string.phongtam))) return "4";
		else if(value.equals(context.getString(R.string.vuonsau))) return "5";
		else if(value.equals(context.getString(R.string.vuontruoc))) return "6";
		else if(value.equals(context.getString(R.string.santhuongtruoc))) return "7";
		else if(value.equals(context.getString(R.string.hanhlang))) return "8";
		else if(value.equals(context.getString(R.string.phongtrong))) return "9";
		else return "9";
	}
	public String Name(String value){
		if(value.equals(context.getString(R.string.bongden))) return "0";
		else if(value.equals(context.getString(R.string.quat))) return "1";
		else if(value.equals(context.getString(R.string.loa))) return "2";
		else if(value.equals(context.getString(R.string.laptop))) return "3";
		else if(value.equals(context.getString(R.string.tivi))) return "4";
		else if(value.equals(context.getString(R.string.khoa))) return "5";
		else if(value.equals(context.getString(R.string.bom))) return "6";
		else if(value.equals(context.getString(R.string.dieuhoa))) return "7";
		else return "0";
	}
	
}
