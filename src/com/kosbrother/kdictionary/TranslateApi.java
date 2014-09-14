package com.kosbrother.kdictionary;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONObject;

import android.util.Log;

public class TranslateApi
{
	
	final static String HOST = "http://glosbe.com/gapi/translate?";
	public static final String TAG = "TRANSLATE_API";
	public static final boolean DEBUG = true;
	
	
	public static String getTransLate(String from, String dest, String phrase)
	{
		String message = getMessageFromServer("GET",
				"from="+from+"&dest="+dest+"&format=json&phrase="+phrase+"&pretty=true", null, null);
		if (message == null)
		{
			return "";
		} else
		{
			return parseTranslateMessage(message);
		}
	}
	
	private static String parseTranslateMessage(String message)
	{
		try
		{
			JSONObject jsonObject = new JSONObject(message.toString());
			JSONArray  jArray = new JSONArray(jsonObject.getString("tuc"));
			String transString = "";
			int size = jArray.length();
			if (size > 10 )
			{
				size = 10;
			}
			for (int i = 0; i < size; i++)
			{
				if (i != size -1 )
				{
					try
					{
						transString = transString + jArray.getJSONObject(i).getJSONObject("phrase").get("text").toString() + "\n";
					} catch (Exception e)
					{
						transString = transString + jArray.getJSONObject(i).getJSONArray("meanings").getJSONObject(0).get("text") + "\n";
					}
					
				}else {
					try
					{
						transString = transString + jArray.getJSONObject(i).getJSONObject("phrase").get("text").toString();
					} catch (Exception e)
					{
						transString = transString + jArray.getJSONObject(i).getJSONArray("meanings").getJSONObject(0).get("text");
					}
					
				}		
			}		
			return transString;
		} catch (Exception e)
		{
			return "";
		}

	}
	
	public static String getMessageFromServer(String requestMethod,
			String apiPath, JSONObject json, String apiUrl)
	{
		Log.i(TAG, "Start Load from server:" + System.currentTimeMillis());
		URL url;
		try
		{
			if (apiUrl != null)
				url = new URL(apiUrl);
			else
				url = new URL(HOST + apiPath);

			if (DEBUG)
				Log.d(TAG, "URL: " + url);

			HttpURLConnection connection = (HttpURLConnection) url
					.openConnection();
			connection.setRequestMethod(requestMethod);

			connection.setRequestProperty("Content-Type",
					"application/json;charset=utf-8");
			if (requestMethod.equalsIgnoreCase("POST"))
				connection.setDoOutput(true);
			connection.setDoInput(true);
			connection.connect();

			if (requestMethod.equalsIgnoreCase("POST"))
			{
				OutputStream outputStream;

				outputStream = connection.getOutputStream();
				if (DEBUG)
					Log.d("post message", json.toString());

				outputStream.write(json.toString().getBytes());
				outputStream.flush();
				outputStream.close();
			}

			BufferedReader reader = new BufferedReader(new InputStreamReader(
					connection.getInputStream()));
			StringBuilder lines = new StringBuilder();
			;
			String tempStr;

			while ((tempStr = reader.readLine()) != null)
			{
				lines = lines.append(tempStr);
			}
			if (DEBUG)
				Log.d("MOVIE_API", lines.toString());

			reader.close();
			connection.disconnect();

			Log.i(TAG, "End Load from server:" + System.currentTimeMillis());

			return lines.toString();
		} catch (MalformedURLException e)
		{
			e.printStackTrace();
			return null;
		} catch (IOException e)
		{
			e.printStackTrace();
			return null;
		}
	}

	
}
