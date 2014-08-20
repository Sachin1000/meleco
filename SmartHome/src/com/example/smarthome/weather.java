package com.example.smarthome;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class weather{

	private TextView weatherCondition;
	private TextView weatherDay;
	private TextView weatherTemp;
	private TextView weatherSpeed;
	private TextView weatherCity;
	private ImageView imageDay;

	private String zip;
	final int RQcode = 18;// so dep ^^
	int numberImage = 1;
	private String location;
	private String[] setTemp = { "\u2109", "\u2103" };
	private String[] setSpeed = { "mp/h", "m/s" };
	// do C=1/F - ms=1/mph

	public MyWeather weatherResult;
	private int[] setdata = { 1, 1 };

	// private final static String yahoo = "http://www.yahoo.com.vn";
	private final static String yahoo_weather = "http://weather.yahooapis.com/forecastrss?w=";
	private static View view;
	private static Context context;
	
    public weather(View v){
        weather.view = v;
        weather.context = v.getContext();
    }

	public class MyTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... arg0) {
			try{
			String weatherString = QueryYahooWeather();
			Document weatherDoc = convertStringToDocument(weatherString);
			weatherResult = parseWeather(weatherDoc);
			} catch(Exception e){
			} 
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			try{
			imageDay.setImageResource(layout_image());
			layout_text();
			
			SharedPreferences settings = view.getContext().getSharedPreferences("LocationInfo", 0);
			SharedPreferences.Editor editor = settings.edit();
			editor.putString("Info", weatherResult.toString());
			editor.commit();
			}catch(Exception e){
			}
			super.onPostExecute(result);
		}
	}

	MyTask myTask;

	private int layout_image() {
		int id;
		int day ;
		day = checktime(weatherResult.conditiondate);

		if (checkstring(weatherResult.conditiontext, "Cloudy")) {
			if (weatherResult.conditiontext.equals("Mostly Cloudy"))
				id = R.drawable.widget_icon_dark_cloud;
			else if (day == 1) // troi sang
				id = R.drawable.widget_icon_cloud;
			else
				id = R.drawable.widget_icon_cloud_night;
		} else if (checkstring(weatherResult.conditiontext, "Night"))
			id = R.drawable.widget_icon_sun_night;
		else if (checkstring(weatherResult.conditiontext, "Sun"))
			id = R.drawable.widget_icon_sun;
		else if (checkstring(weatherResult.conditiontext, "Thunder")
				|| checkstring(weatherResult.conditiontext, "Thunderstorms")
				|| checkstring(weatherResult.conditiontext, "Thunderstorm"))
			id = R.drawable.widget_icon_thunderstorm;
		else if (checkstring(weatherResult.conditiontext, "Rain"))
			id = R.drawable.widget_icon_rain;
		else
			id = R.drawable.widget_icon_cloud;
		return id;
	}

	private void layout_text() {
		weatherTemp.setText("Nhiệt độ: "
				+ weatherResult.windChill[setdata[0]] + setTemp[setdata[0]]);
		weatherSpeed.setText("Wind speed: "
				+ weatherResult.windSpeed[setdata[1]] + setSpeed[setdata[1]]);
		weatherCondition.setText(weatherResult.conditiontext);
		weatherCity.setText(weatherResult.city);
	}

	private int checktime(String a) {
		// Log.v("main", "Dung c-1");
		// check ngay hay dem, true=ngay
		String[] aw = a.split(" ");
		String[] time = aw[4].split(":");
		int hour = Integer.parseInt(time[0]);
		// int minute = Integer.parseInt(time[1]);
		// Log.v("main", "Dung c-2");
		weatherDay.setText("Day: " + aw[0] + " " + time[0] + ":" + time[1]
				+ " " + aw[5]);
		// tra ve ngay-gio-phut
		// Log.v("main", "Dung c-3");
		if ((aw[5].equals("am") && hour > 6)
				|| (aw[5].equals("pm") && hour < 6)
				|| (aw[5].equals("pm") && hour == 12))
			return 1;
		else
			;
		return 0;
	}

	private boolean checkstring(String a, String b) {
		// kiem tra neu a va b co chung 1 chu
		String[] aw = a.split(" ");
		String[] bw = b.split(" ");
		int i, j;
		boolean flag = false;
		for (i = 0; i < aw.length; i++)
			for (j = 0; j < bw.length; j++)
				if (aw[i].equals(bw[j])) {
					flag = true;
					// Log.v("", aw[i] + "-" + aw[j]);
				}
		return flag;
	}

	private void weatherSearch() {
		zip = Zip(location);
		if (zip.equals("")) {
			Toast.makeText(context,
					"no found location!", Toast.LENGTH_SHORT).show();
		} else {
			myTask = new MyTask();
			myTask.execute();
		}
	}


	private String Zip(String location) {
		// luu vao sharePreference
		if (location.equals(null))
			return "";
		// lay ma Zip cua cac thanh pho
		if (location.equals("Ho Chi Minh") || location.equals("Sai Gon"))
			return "1252431";
		else if (location.equals("Ha Noi"))
			return "1236594";
		else if (location.equals("Hue"))
			return "1252438";
		else if (location.equals("Da Nang"))
			return "1252376";
		else if (location.equals("Nha Trang"))
			return "1252522";
		else if (location.equals("Quy Nhon"))
			return "23424984";
		else if (location.equals("Vung Tau"))
			return "1252672";
		else if (location.equals("Buon Ma Thuot"))
			return "1233132";
		else
			return "";
	}

	private String QueryYahooWeather() {
		// chuyen tu xml sang string
		String qResult = "";
		HttpClient httpClient = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet(yahoo_weather + zip + "&d=8");
			try {// lay giu lieu ve httpEntity
				HttpEntity httpEntity = httpClient.execute(httpGet).getEntity();
				if (httpEntity != null) {
					InputStream inputStream = httpEntity.getContent();
					Reader in = new InputStreamReader(inputStream);
					BufferedReader bufferedreader = new BufferedReader(in);
					StringBuilder stringBuilder = new StringBuilder();

					String stringReadLine = null;
					while ((stringReadLine = bufferedreader.readLine()) != null) {
						// copy tung dong tu bufferedreader ra stringBuilder
						stringBuilder.append(stringReadLine + "\n");
					}
					// chuyen lai sang cau truc string
					qResult = stringBuilder.toString();
				} else {
					Log.v("main", "loi 5");
				}
			} catch (ClientProtocolException e) {
				Toast.makeText(context, e.toString(),
						Toast.LENGTH_LONG).show();
			} catch (IOException e) {
				Toast.makeText(context, e.toString(),
						Toast.LENGTH_LONG).show();
			}

		return qResult;
	}



	private MyWeather parseWeather(Document srcDoc) {
		// phan tich giu lieu tu Document <-- xml - theo phuong thuc Dom
		MyWeather myWeather = new MyWeather();
		int temp;

		// Mô tả địa phương
		myWeather.description = srcDoc.getElementsByTagName("description")
				.item(0).getTextContent();
		// Vị trí
		Node locationNode = srcDoc.getElementsByTagName("yweather:location")
				.item(0);
		myWeather.city = locationNode.getAttributes().getNamedItem("city")
				.getNodeValue().toString();
		myWeather.region = locationNode.getAttributes().getNamedItem("region")
				.getNodeValue().toString();
		myWeather.country = locationNode.getAttributes()
				.getNamedItem("country").getNodeValue().toString();

		// gió
		Node windNode = srcDoc.getElementsByTagName("yweather:wind").item(0);
		myWeather.windChill[0] = windNode.getAttributes().getNamedItem("chill")
				.getNodeValue().toString();
		// doi tu do F ra do C
		temp = (int) ((Integer.parseInt(myWeather.windChill[0]) - 32) / 1.8);
		myWeather.windChill[1] = Integer.toString(temp);

		myWeather.windDirection = windNode.getAttributes()
				.getNamedItem("direction").getNodeValue().toString();
		myWeather.windSpeed[0] = windNode.getAttributes().getNamedItem("speed")
				.getNodeValue().toString();
		// doi tu mph ra m/s
		temp = (int) (Integer.parseInt(myWeather.windSpeed[0]) * 0.44704);
		myWeather.windSpeed[1] = Integer.toString(temp);

		// Thiên văn học
		Node astronomyNode = srcDoc.getElementsByTagName("yweather:astronomy")
				.item(0);
		myWeather.sunrise = astronomyNode.getAttributes()
				.getNamedItem("sunrise").getNodeValue().toString();
		myWeather.sunset = astronomyNode.getAttributes().getNamedItem("sunset")
				.getNodeValue().toString();

		// khong khi
		Node atmosphereNode = srcDoc
				.getElementsByTagName("yweather:atmosphere").item(0);
		myWeather.humidity = atmosphereNode.getAttributes()
				.getNamedItem("humidity").getNodeValue().toString();
		myWeather.visibility = atmosphereNode.getAttributes()
				.getNamedItem("visibility").getNodeValue().toString();
		myWeather.pressure = atmosphereNode.getAttributes()
				.getNamedItem("pressure").getNodeValue().toString();

		// Tình trạng
		Node conditionNode = srcDoc.getElementsByTagName("yweather:condition")
				.item(0);
		myWeather.conditiontext = conditionNode.getAttributes()
				.getNamedItem("text").getNodeValue().toString();
		myWeather.conditiondate = conditionNode.getAttributes()
				.getNamedItem("date").getNodeValue().toString();
		// Log.v("main", "d: " + myWeather.conditiondate);
		return myWeather;
	}

	private Document convertStringToDocument(String src) {
		// String --> document
		Document dest = null;
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder parser;

		try {
			parser = dbFactory.newDocumentBuilder();
			dest = parser.parse(new ByteArrayInputStream(src.getBytes()));
			// string -> document

		} catch (ParserConfigurationException e1) {
			e1.printStackTrace();
			Toast.makeText(context, e1.toString(), Toast.LENGTH_SHORT)
					.show();
		} catch (SAXException e) {
			e.printStackTrace();
			Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT)
					.show();
		} catch (IOException e) {
			e.printStackTrace();
			Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT)
					.show();
		}

		return dest;
	}

	class MyWeather {// lop weather
		String description = "";
		String city = "";
		String region = "";
		String country = "";

		String[] windChill = { "", "" };
		String[] windChillLow = { "", "" };
		String windDirection = "";
		String[] windSpeed = { "", "" };

		String humidity = "";
		String visibility = "";
		String pressure = "";

		String sunrise = "";
		String sunset = "";

		String conditiontext = "";
		String conditiondate = "";

		public String toString() {
			return "\n- " + description + " -\n\n" + "Thành phố: " + city
					+ "\n" + "Đất nước: " + country + "\n\n"

					+ "Nhiệt độ: " + windChill[setdata[0]] + " "
					+ setTemp[setdata[0]] + "\n" + "Hướng gió: "
					+ windDirection + "\u00B0" + "\n" + "Tốc độ gió: "
					+ windSpeed[setdata[1]] + " " + setSpeed[setdata[1]] + "\n"
					+ "Trạng thái: " + conditiontext + "\n\n"

					+ "Độ ẩm: " + humidity + " %" + "\n" + "Tầm nhìn: "
					+ visibility + " mile" + "\n" + "Áp Suất: " + pressure
					+ " inchHg" + "\n\n"

					+ "Mặt trời mọc: " + sunrise + "\n" + "Hoàng hôn: "
					+ sunset;
		}
	}
	

	protected void initialize() {
		weatherCondition = (TextView) view.findViewById(R.id.weatherCondition);
		weatherDay = (TextView) view.findViewById(R.id.weatherDay);
		weatherTemp = (TextView) view.findViewById(R.id.weatherTemp);
		weatherSpeed = (TextView) view.findViewById(R.id.weatherSpeed);
		weatherCity = (TextView) view.findViewById(R.id.weatherCity);
		imageDay = (ImageView) view.findViewById(R.id.imageDay);
	}
	public void weather_info(){
		SharedPreferences settings = view.getContext().getSharedPreferences("LocationInfo", 0);
		location = settings.getString("location", "").toString();
		setdata[0] = settings.getInt("setting_data_0",1);
		setdata[1] = settings.getInt("setting_data_1",1);
		
		initialize();
		weatherSearch();
	}
	
}
