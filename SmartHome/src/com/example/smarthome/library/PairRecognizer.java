package com.example.smarthome.library;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.R.integer;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.example.smarthome.Myitems;
import com.example.smarthome.MainActivity;
import com.example.smarthome.R;

public class PairRecognizer {
	public static final String SPECIAL_CHARACTERS = "àÀảẢãÃáÁạẠăĂằẰẳẲẵẴắẮặẶâÂầẦẩẨẫẪấẤậẬđĐèÈẻẺẽẼéÉẹẸêÊềỀểỂễỄếẾệỆìÌỉỈĩĨíÍịỊòÒỏỎõÕóÓọỌôÔồỒổỔỗỖốỐộỘơƠờỜởỞỡỠớỚợỢùÙủỦũŨúÚụỤưƯừỪửỬữỮứỨựỰýÝ";
	public static final String REPLACEMENTS = "aAaAaAaAaAaAaAaAaAaAaAaAaAaAaAaAaAdDeEeEeEeEeEeEeEeEeEeEeEiIiIiIiIiIoOoOoOoOoOoOoOoOoOoOoOoOoOoOoOoOoOuUuUuUuUuUuUuUuUuUuUuUyY";
	char[] special = SPECIAL_CHARACTERS.toCharArray();
	char[] normol = REPLACEMENTS.toCharArray();
	private List<Myitems> data;
	private List<Myitems> tmp;
	private List<Myitems> newdata;
	private Context context;
	public PairRecognizer(Context c){
		this.context=c;
	}
	
	public List<Myitems> Recognizer(ArrayList<String> text, Map<String, List<Myitems>> roomCollection, List<String> groupList){	
		String[] textarr = text.toArray(new String[text.size()]);
		data = new ArrayList<Myitems>(); 
		tmp = new ArrayList<Myitems>();
		List<Integer> number = new ArrayList<Integer>();
		List<Integer> selected = new ArrayList<Integer>();
		String room = new String();
		String item = new String();
		String value = new String();
		String channel = new String();
		int i, j, min = 1, max = 10;
		
		// Check loai thiet bi
		for (i = 0; i < textarr.length; i++) {// check tung gui y mot
			if (compareList(textarr[i],nameItems(context.getString(R.string.bongden))) != 0) {
				item = context.getString(R.string.bongden);
				break;
			} else if (compareList(textarr[i],nameItems(context.getString(R.string.quat))) != 0) {
				item = context.getString(R.string.quat);
				break;
			} else if (compareList(textarr[i],nameItems(context.getString(R.string.bom))) != 0) {
				item = context.getString(R.string.bom);
				break;
			} else if (compareList(textarr[i],nameItems(context.getString(R.string.loa))) != 0) {
				item = context.getString(R.string.loa);
				break;
			} else if (compareList(textarr[i],nameItems(context.getString(R.string.laptop))) != 0) {
				item = context.getString(R.string.laptop);
				break;
			} else if (compareList(textarr[i],nameItems(context.getString(R.string.tivi))) != 0) {
				item = context.getString(R.string.tivi);
				break;
			} else if (compareList(textarr[i],nameItems(context.getString(R.string.khoa))) != 0) {
				item = context.getString(R.string.khoa);
				break;
			} else if (compareList(textarr[i],nameItems(context.getString(R.string.dieuhoa))) != 0) {
				item = context.getString(R.string.dieuhoa);
				break;
			} // còn nữa
		}
		Log.v("Pair Speech","Items: " + item);
		
		if (i < textarr.length) {
			//Check room
			for (i = 0; i < textarr.length; i++) {// check tung gui y mot
				if (compareList(textarr[i],nameItems(context.getString(R.string.phongkhach))) != 0) {
					room = context.getString(R.string.phongkhach);
					break;
				} else if (compareList(textarr[i],nameItems(context.getString(R.string.phongngu))) != 0) {
					room = context.getString(R.string.phongngu);
					break;
				} else if (compareList(textarr[i],nameItems(context.getString(R.string.phongbep))) != 0) {
					room = context.getString(R.string.phongbep);
					break;
				} else if (compareList(textarr[i],nameItems(context.getString(R.string.phonglamviec))) != 0) {
					room = context.getString(R.string.phonglamviec);
					break;
				}else if (compareList(textarr[i],nameItems(context.getString(R.string.phongtam))) != 0) {
					room = context.getString(R.string.phongtam);
					break;
				}else if (compareList(textarr[i],nameItems(context.getString(R.string.vuonsau))) != 0) {
					room = context.getString(R.string.vuonsau);
					break;
				}else if (compareList(textarr[i],nameItems(context.getString(R.string.vuontruoc))) != 0) {
					room = context.getString(R.string.vuontruoc);
					break;
				}else if (compareList(textarr[i],nameItems(context.getString(R.string.hanhlang))) != 0) {
					room = context.getString(R.string.hanhlang);
					break;
				}else if (compareList(textarr[i],nameItems(context.getString(R.string.santhuongtruoc))) != 0) {
					room = context.getString(R.string.santhuongtruoc);
					break;
				}else if (compareList(textarr[i],nameItems(context.getString(R.string.phongtrong))) != 0) {
					room = context.getString(R.string.phongtrong);
					break;
				}
			}
		}
		if(i == textarr.length) { //nếu chỉ có 1 phòng có loại item nào đó
			Log.v("pair","loi 1");
			int k;
			List<String> numberitems= new ArrayList<String>();
			for(j=0;j<groupList.size();j++){
				List<Myitems> tmpList = roomCollection.get(groupList.get(j));
				for(k=0;k<tmpList.size();k++){
					if(tmpList.get(k).name.equals(item))
						break;
				}
				if(k<tmpList.size()) 
					numberitems.add(groupList.get(j));
			}
			Log.v("pair","size: "+numberitems.size());
			if(numberitems.size()==1){ 
				room = numberitems.get(0);
				i=textarr.length-1; 
			}
		}
		
		Log.v("Pair Speech",room); 
		if (i < textarr.length) {// Check thu tu cua thiet bi
			for(j=1;j<=max;j++)
			    for (i = 0; i < textarr.length; i++) {
				    if (compare(textarr[i],String.valueOf(j))) {
				    	number.add(j);
				    	Log.v("Pair Speech","items number: " + j);
				    	break;
				    }
			    }
			i = 0;
			if (number.size()==0)//nếu không có số được chọn
				number.add(0);//bật tất cả đèn trong phòng
		}
		
		// Check trang thai dong/mo
		if (i < textarr.length)
			for (i = 0; i < textarr.length; i++) { 
				if (compareList(textarr[i],nameItems(context.getString(R.string.kenh))) != 0){
					value = "CHANNEL";
					break;
				}else if (compareList(textarr[i],nameItems(context.getString(R.string.tat))) != 0){
					value = "OFF";
					break;
				}else if (compareList(textarr[i],nameItems(context.getString(R.string.mo))) != 0){
					value = "ON";
					break;
				}else if (compareList(textarr[i],nameItems(context.getString(R.string.auto))) != 0){
					value = "AUTO";
					break;
				}
			}
		//neu la kenh
		if(i < textarr.length && value.equals("CHANNEL") ){
			value = "AUTO";
			for (i = 0; i < textarr.length; i++) { 
				if (compareList(textarr[i],channelName(context.getString(R.string.vtv1))) != 0){
					channel = context.getString(R.string.vtv1);	break;
				}else if (compareList(textarr[i],channelName(context.getString(R.string.vtv2))) != 0){
					channel = context.getString(R.string.vtv2);	break;
				}else if (compareList(textarr[i],channelName(context.getString(R.string.vtv3))) != 0){
					channel = context.getString(R.string.vtv3);	break;
				}else if (compareList(textarr[i],channelName(context.getString(R.string.htv1))) != 0){
					channel = context.getString(R.string.htv1);	break;
				}else if (compareList(textarr[i],channelName(context.getString(R.string.htv2))) != 0){
					channel = context.getString(R.string.htv2);	break;
				}else if (compareList(textarr[i],channelName(context.getString(R.string.htv3))) != 0){
					channel = context.getString(R.string.htv3);	break;
				}else if (compareList(textarr[i],channelName(context.getString(R.string.htv4))) != 0){
					channel = context.getString(R.string.htv4);	break;
				}else if (compareList(textarr[i],channelName(context.getString(R.string.htv7))) != 0){
					channel = context.getString(R.string.htv7);	break;
				}else if (compareList(textarr[i],channelName(context.getString(R.string.cartoon))) != 0){
					channel = context.getString(R.string.cartoon);	break;
				}else if (compareList(textarr[i],channelName(context.getString(R.string.discovery))) != 0){
					channel = context.getString(R.string.discovery);	break;
				}else if (compareList(textarr[i],channelName(context.getString(R.string.disney))) != 0){
					channel = context.getString(R.string.disney);	break;
				}else if (compareList(textarr[i],channelName(context.getString(R.string.hbo))) != 0){
					channel = context.getString(R.string.hbo);	break;
				}else if (compareList(textarr[i],channelName(context.getString(R.string.starmovie))) != 0){
					channel = context.getString(R.string.starmovie);	break;
				}else if (compareList(textarr[i],channelName(context.getString(R.string.soha))) != 0){
					channel = context.getString(R.string.soha);	break;
				}else if (compareList(textarr[i],channelName(context.getString(R.string.nhaccuatui))) != 0){
					channel = context.getString(R.string.nhaccuatui);	break;
				}else if (compareList(textarr[i],channelName(context.getString(R.string.youtube))) != 0){
					channel = context.getString(R.string.youtube);	break;
				}
				
			}
		} 
		Log.v("Pair Speech","value: " + value);
		// Check dung cu phap khong, Dung -> Gui sendcommand()
		if (i == textarr.length)
			Toast.makeText(context, "Không đúng lệnh",Toast.LENGTH_LONG).show();
		else{
			for(i=0;i<groupList.size();i++) //kiem vi tri room
				if(groupList.get(i).equals(room))
					break;
			if(i==groupList.size()) return null;//neu khong tim ra dung phong
			Log.v("Pair Speech","room: " + groupList.get(i));
			tmp = roomCollection.get(groupList.get(i));
			
			for(i=0;i<tmp.size();i++)//kiem thiet bi cung ten
				if(item.equals(tmp.get(i).name))
					selected.add(i);
			if(selected==null) return null; //neu khong tim ra dung thiet bi
			else if(selected.size()==1) {
				number.clear();
				number.add(0);
			}
				
			Log.v("Pair Speech",selected.toString());
			
			//tìm thiết bị theo số thứ tự
			for(i=0;i<number.size();i++){
				if(number.get(i)==0) //nếu không có số thứ tự trong lệch thì gửi hết
					for(j=0;j<selected.size();j++) {
						tmp.get(selected.get(j)).value=value;
						if(tmp.get(selected.get(j)).name.equals(context.getString(R.string.tivi))) 
							tmp.get(selected.get(j)).data=channel;
						Log.v("Pair Speech","data: " + channel); 
						data.add(tmp.get(selected.get(j)));
					} 
				else if(number.get(i)>selected.size()) break; 
				else {  
					tmp.get(selected.get(number.get(i)-1)).value = value;
					tmp.get(selected.get(number.get(i)-1)).data = channel;
					data.add(tmp.get(selected.get(number.get(i)-1)));
				    Log.v("Pair Speech","data: " + channel); 
				}
			}
			int k;
			newdata = new ArrayList<Myitems>(); //loc lai cac item co cung add
			for(j=0;j<data.size();j++){
				for(k=0;k<newdata.size();k++)//neu add trong data trung voi newdata thi ko can them vao
					if(data.get(j).add.equals(newdata.get(k).add)) break;
				if (k == newdata.size())
					for (i = 0; i < tmp.size(); i++) {
						if (tmp.get(i).add.equals(data.get(j).add)){
							newdata.add(tmp.get(i));
							Log.v("Pair Speech",tmp.get(i).name);
						}
					}
			}
			Toast.makeText(context, value + " " + item + " " + room, Toast.LENGTH_SHORT).show();
		}	
		return newdata;	 
	}
	
	public List<String> nameItems(String name){
		List<String> Items = new ArrayList<String>();
		//thiết bị điện
		if(name.equals(context.getString(R.string.bongden))){
			Items.add("den");Items.add("dien");Items.add("diem");Items.add("dinh");
		    Items.add("anh sang");Items.add("an sang");
		}
		else if(name.equals(context.getString(R.string.quat))){
			Items.add("quat");Items.add("quan");Items.add("qua");
		}
		else if(name.equals(context.getString(R.string.loa))){
			Items.add("loa");Items.add("loan");
		}
		else if(name.equals(context.getString(R.string.laptop))){
			Items.add("laptop");Items.add("may tinh");
		}
		else if(name.equals(context.getString(R.string.tivi))){
			Items.add("tivi");Items.add("tv");Items.add("tu vi");
			Items.add("kenh");Items.add("ken");Items.add("can");Items.add("channel");
		} 
		else if(name.equals(context.getString(R.string.khoa))){
			Items.add("khoa");Items.add("qua");Items.add("cua");
		}
		else if(name.equals(context.getString(R.string.bom))){
			Items.add("bom");Items.add("cay");
		}
		else if(name.equals(context.getString(R.string.dieuhoa))){
			Items.add("dieu hoa");Items.add("diem hoa");Items.add("kieu hoa");
			Items.add("may lanh");Items.add("may anh");
		}
		//phong
		else if(name.equals(context.getString(R.string.phongkhach))){
			Items.add("phong khach");
		}
		else if(name.equals(context.getString(R.string.phongngu))){
			Items.add("phong ngu");
		}
		else if(name.equals(context.getString(R.string.phongbep))){
			Items.add("phong bep");Items.add("phong benh");Items.add("phong ban");
		}
		else if(name.equals(context.getString(R.string.phongtam))){
			Items.add("phong tam");Items.add("khong tam");
		}
		else if(name.equals(context.getString(R.string.phonglamviec))){
			Items.add("phong lam viec");
		}
		else if(name.equals(context.getString(R.string.vuonsau))){
			Items.add("vuon sau");Items.add("san sau");
		}
		else if(name.equals(context.getString(R.string.vuontruoc))){
			Items.add("san truoc");Items.add("vuon truoc");
		}
		else if(name.equals(context.getString(R.string.hanhlang))){
			Items.add("hanh lang");Items.add("han lang");Items.add("hanh lan");Items.add("han lan");
		}
		else if(name.equals(context.getString(R.string.santhuongtruoc))){
			Items.add("san thuong truoc");
		}else if(name.equals(context.getString(R.string.phongtrong))){
			Items.add("cau thang");
		}
		
		//đóng mở
		else if(name.equals(context.getString(R.string.auto))){
			Items.add("auto");Items.add("tu d***");Items.add("tu dong");
		}
		else if(name.equals(context.getString(R.string.tat))){
			Items.add("tat");Items.add("xac");Items.add("ngat");Items.add("tac");Items.add("tang");
			Items.add("dat");
		}
		else if(name.equals(context.getString(R.string.mo))){
			Items.add("mo");Items.add("bat");Items.add("ban");
		}
		else if(name.equals(context.getString(R.string.kenh))){
			Items.add("kenh");Items.add("ken");Items.add("can");Items.add("con");
			Items.add("channel");
		}

		return Items;
	}
	
	public List<String> channelName(String name){
		List<String> Items = new ArrayList<String>();
		//thiết bị điện
		if(name.equals(context.getString(R.string.vtv1))){
			Items.add("vtv1");Items.add("vtv 1");
		}else if(name.equals(context.getString(R.string.vtv2))){
			Items.add("vtv2");Items.add("vtv 2");
		}else if(name.equals(context.getString(R.string.vtv3))){
			Items.add("vtv3");Items.add("vtv 3");
		}else if(name.equals(context.getString(R.string.htv1))){
			Items.add("htv1");Items.add("vtv 1");
		}else if(name.equals(context.getString(R.string.htv2))){
			Items.add("htv2");Items.add("vtv 2");
		}else if(name.equals(context.getString(R.string.htv3))){
			Items.add("htv3");Items.add("vtv 3");
		}else if(name.equals(context.getString(R.string.htv4))){
			Items.add("htv4");Items.add("vtv 4");
		}else if(name.equals(context.getString(R.string.htv7))){
			Items.add("htv7");Items.add("vtv 7");
		}else if(name.equals(context.getString(R.string.cartoon))){
			Items.add("cartoon");Items.add("cartoon network");Items.add("hoat hinh");
		}else if(name.equals(context.getString(R.string.discovery))){
			Items.add("discovery");Items.add("khoa hoc");
		}else if(name.equals(context.getString(R.string.geographic))){
			Items.add("geographic");
		}else if(name.equals(context.getString(R.string.disney))){
			Items.add("disney");
		}else if(name.equals(context.getString(R.string.hbo))){
			Items.add("hbo");
		}else if(name.equals(context.getString(R.string.starmovie))){
			Items.add("starmovie");Items.add("star movie");
		}else if(name.equals(context.getString(R.string.soha))){
			Items.add("soha");
		}else if(name.equals(context.getString(R.string.nhaccuatui))){
			Items.add("nhac cua tui");Items.add("nhac cua toi");Items.add("am nhac");
			Items.add("em nhac");Items.add("anh nhac");
		}else if(name.equals(context.getString(R.string.youtube))){
			Items.add("video");Items.add("youtube");
		}
		return Items;
	}
	
	public int compareList(String aa, List<String> bb) {
		// Đưa String a về không dấu rồi so sanh với String b
		char[] a = aa.toCharArray();
		int i, j, k, times;
		int result = 0;
		for (i = 0; i < a.length; i++) { // Chuyen tu co dau -> khong dau
			for (j = 0; j < special.length; j++) {
				if (a[i] == special[j])
					a[i] = normol[j];
			}
		}
		aa = String.valueOf(a, 0, a.length);// dua lai về String

		String[] aWord = aa.split(" ");

		for (k = 0; k < bb.size(); k++) {
			String[] bWord = bb.get(k).split(" ");
			String[] word = bWord;
			times = 0;
			for (i = 0; i < bWord.length; i++) {
				for (j = 0; j < aWord.length; j++) {
					if (aWord[j].equals(bWord[i]))
						word[i] = "ok";
				}
				if (word[i].equals("ok"))
					times++;
			}
			if (times == bWord.length)
				result = (k + 1);//tra ra vi tri cua tu dung
		}
		return result;
	}
	public boolean compare(String aa, String bb) {
		// Đưa String a về không dấu rồi so sanh với String b
		char[] a = aa.toCharArray();
		int i, j, k, times;
		boolean result = false;
		for (i = 0; i < a.length; i++) { // Chuyen tu co dau -> khong dau
			for (j = 0; j < special.length; j++) {
				if (a[i] == special[j])
					a[i] = normol[j];
			}
		}
		aa = String.valueOf(a, 0, a.length);// dua lai về String

		String[] aWord = aa.split(" ");

		String[] word = bb.split(" ");
		times = 0;
		for (i = 0; i < word.length; i++) {
			for (j = 0; j < aWord.length; j++) {
				if (aWord[j].equals(word[i])) {
					times++;
					break;
				}
			}
		}
		if (times == word.length)
			result = true;
		return result;
	}
}
