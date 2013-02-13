package com.livefyre.streamhub_android_sdk;
import java.io.IOException;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;
import android.os.Handler.Callback;
import android.os.AsyncTask;
import android.os.Message;

/**
 * 
 * @author zjj
 */
class GETJSONinBackground extends AsyncTask<Object, Void, Void>  {
	/**
	 * Notifies the callback with a message.
	 * The obj on the message is set to a JSONObject
	 * 
	 * @param callback
	 * @param data
	 */
	public static void notify(Callback callback, JSONObject data) {
		Message requestComplete = Message.obtain();
		requestComplete.obj = data;
		callback.handleMessage(requestComplete);
	}
	
	/**
	 * Performs a network request asynchronously. 
	 * Object is our wildcard, we expect a URL as arg0 and Callback as arg1.
	 */
	@Override
	protected Void doInBackground(Object... params) {
		URL endpoint = (URL) params[0];
		Callback callback = (Callback) params[1];
		JSONObject data = null;
		try {
			data = GETJSON.fetchData(endpoint);
		} catch (IOException e) {
			// Swallow the exception?
		} catch (JSONException e) {
			// Swallow the exception?
		}
		notify(callback, data);
		return null;
	}
}
