package com.kyokomi.vollery.toolbox;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.JsonObjectRequest;

public class JsonObjectHaveHeaderRequest extends JsonObjectRequest {

	public JsonObjectHaveHeaderRequest(int method, String url,
			JSONObject jsonRequest, Listener<JSONObject> listener,
			ErrorListener errorListener) {
		super(method, url, jsonRequest, listener, errorListener);
	}
	
	@Override
	protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
		Response<JSONObject> responseJson = super.parseNetworkResponse(response);
		try {
			responseJson.result.put("headers", new JSONObject(response.headers));
		} catch (JSONException e) {
			return Response.error(new ParseError(e));
		}
		return responseJson;
	}

}
