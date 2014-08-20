package com.example.smarthome;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import com.example.smarthome.Myitems;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
 
public class MyDatabase {
    /*Tên database*/
    private static final String DATABASE_NAME = "DB_USER";
 
    /*Version database*/
    private static final int DATABASE_VERSION = 1;
 
    /*Tên tabel và các column trong database*/
    private static final String TABLE_ACCOUNT = "RoomCollection";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_ROOM = "Room";
    public static final String COLUMN_SWITCH = "Switch";
    public static final String COLUMN_VALUE = "Value";
    public static final String COLUMN_ADDRESS = "Address";
    public static final String COLUMN_DATA = "Data";
    public static final String COLUMN_ROOMS = "Rooms";
 
    /*Các đối tượng khác*/
    private static Context context;
    static SQLiteDatabase db;
    private OpenHelper openHelper;
    private List<String> group;
 
    /*Hàm dựng, khởi tạo đối tượng*/
    public MyDatabase(Context c){
        MyDatabase.context = c;
    }
 
    /*Hàm mở kết nối tới database*/
    public MyDatabase open() throws SQLException{
        openHelper = new OpenHelper(context);
        db = openHelper.getWritableDatabase();
        return this;
    }
    
    public void remove(){
    	context.deleteDatabase(DATABASE_NAME);
//    	db.delete(TABLE_ACCOUNT, null, null);
    }
 
    /*Hàm đóng kết nối với database*/
    public void close(){
        openHelper.close();
    }
 
    /*Hàm createData dùng để chèn dữ mới dữ liệu vào database*/
    public void createData(List<String> room, Map<String,List<Myitems>> Mydata) {
        ContentValues cv;
        List<Myitems> data;
		for (int j = 0; j < room.size(); j++) {
			data = new ArrayList<Myitems>();
			data = Mydata.get(room.get(j));
			for (int i = 0; i < data.size(); i++) {
				cv = new ContentValues();
				cv.put(COLUMN_ROOM, room.get(j));
				cv.put(COLUMN_SWITCH, data.get(i).name);
				cv.put(COLUMN_VALUE, data.get(i).value);
				cv.put(COLUMN_ADDRESS, data.get(i).add);
				cv.put(COLUMN_DATA, data.get(i).data);
				db.insert(TABLE_ACCOUNT, null, cv);
			}
		}
    }
 
    /*Hàm getData trả về toàn bộ dữ liệu của table ACCOUNT của database dưới 1 chuỗi*/
    public Map<String,List<Myitems>> getData() {
    	Map<String,List<Myitems>> Mydata = new LinkedHashMap<String,List<Myitems>>(); 
    	List<Myitems> child;
    	Myitems items;
    	
        String[] columns = new String[] {COLUMN_ROOM,COLUMN_SWITCH,COLUMN_VALUE,COLUMN_ADDRESS,COLUMN_DATA};
        Cursor c = db.query(TABLE_ACCOUNT, columns, null, null, null, null, null);
        //getColumnIndex(COLUMN_ID); là lấy chỉ số, vị trí của cột COLUMN_ID ...
        int room = c.getColumnIndex(COLUMN_ROOM);
        int switch_name = c.getColumnIndex(COLUMN_SWITCH);
        int value = c.getColumnIndex(COLUMN_VALUE);
        int data = c.getColumnIndex(COLUMN_DATA);
        int add = c.getColumnIndex(COLUMN_ADDRESS);
        
        group = new ArrayList<String>();
        child = new ArrayList<Myitems>();
        int i=0,j,same=1;
        //Vòng lặp lấy dữ liệu của con trỏ
        for(c.moveToFirst(); !c.isAfterLast(); c.moveToNext()){
        	for(j=0;j<group.size();j++)
        		if(c.getString(room).equals(group.get(j))){
        			same=0;
        		}
            items = new Myitems();
            items.name = c.getString(switch_name);
            items.value = c.getString(value);
            items.add = c.getString(add);
            items.data = c.getString(data);
            items.room = c.getString(room);
        	if(same==1){
        		if(group.size()!=0) Mydata.put(group.get(j-1), child );
        		group.add(c.getString(room));
        		child = new ArrayList<Myitems>();
        	}
        	else {
        	same = 1;           
        	}
        	child.add(items);
        }
        if(group.size()!=0) Mydata.put(group.get(group.size()-1), child );
        
        c.close();
        return Mydata;
    }
    
    public List<String> Getgroup(){
    	return group ;
    }
 
    //---------------- class OpenHelper ------------------
    private static class OpenHelper extends SQLiteOpenHelper {
 
        /*Hàm dựng khởi tạo 1 OpenHelper*/
        public OpenHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }
 
        /*Tạo mới database*/
        @Override
        public void onCreate(SQLiteDatabase arg0) {
            arg0.execSQL("CREATE TABLE " + TABLE_ACCOUNT + " ("
            		+ COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + COLUMN_ROOM + " TEXT NOT NULL, "
                    + COLUMN_SWITCH + " TEXT NOT NULL, "
                    + COLUMN_VALUE + " TEXT NOT NULL, "
                    + COLUMN_ADDRESS + " TEXT NOT NULL, "
                    + COLUMN_DATA + " TEXT NOT NULL);");
        }
 
        /*Kiểm tra phiên bản database nếu khác sẽ thay đổi*/
        @Override
        public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
            arg0.execSQL("DROP TABLE IF EXISTS " + TABLE_ACCOUNT);
            onCreate(arg0);
        }
    }
}