package com.kyokomi.core.utils;

import java.io.IOException;
import java.io.InputStream;

import org.json.JSONArray;
import org.json.JSONException;

import android.content.Context;

public class JsonUtil {

	public static JSONArray toJson(Context context, String fileName) {
		JSONArray jsonArray = null;
		String jsonStr = null;
		try {
			jsonStr = getStringFromAssets(context, fileName + ".json");
			jsonArray = new JSONArray(jsonStr);
			
		} catch (JSONException e) {
			e.printStackTrace();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return jsonArray;
	}
	
	private static String getStringFromAssets(Context context, String fileName) throws IOException {
		String str = "";
		InputStream is = context.getAssets().open(fileName);
		int size = is.available();
		byte[] buffer = new byte[size];
		is.read(buffer);
		is.close();
		str = new String(buffer);
		return str;
	}
}
