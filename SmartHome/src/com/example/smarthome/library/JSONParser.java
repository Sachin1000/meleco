package com.example.smarthome.library;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONException;
import org.json.JSONObject;
import android.util.Log;

public class JSONParser {

	static InputStream is = null;
	static JSONObject jObj = null;
	static String json = "";

	// Hàm xây dựng khởi tạo đối tượng
	public JSONParser() {

	}

	public JSONObject getJSONFromUrl(String url, List<NameValuePair> params, int timeout) {
		// Cố gắng thực hiện gửi yêu cầu lên HTTP
		Log.e("Json",params.toString());
		try {
			Log.v("jsonParse","timeout="+timeout);
			HttpParams httpParameters = new BasicHttpParams();
			int timeoutConnection = timeout;
			HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
			int timeoutSocket = 3000;
//			if(timeout>1000) timeoutSocket = 6000;//ms
//			else timeoutSocket = 3000;
			HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);

			DefaultHttpClient httpClient = new DefaultHttpClient(httpParameters);
			HttpPost httpPost = new HttpPost(url);
			httpPost.setEntity(new UrlEncodedFormEntity(params));
			HttpResponse httpResponse = httpClient.execute(httpPost);
			// Da gui di va dang cho nhan ve
			HttpEntity httpEntity = httpResponse.getEntity();
			// Da nhan lai data -> di phan tich
			is = httpEntity.getContent();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} 
		catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					is, "iso-8859-1"), 8);
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
			is.close();
			json = sb.toString();
			Log.v("JSON", json);
		} catch (Exception e) {
			json = "{\"tag\":\"login\",\"success\":0}";
			Log.e("Buffer Error", json);
		}
		
		// Cố gắng phân tích chuỗi sang một JSONObject
		try {
			jObj = new JSONObject(json);
		} catch (JSONException e) {
			Log.e("JSON Parser", "Error parsing data " + e.toString());
		}
		return jObj;
	}
}
