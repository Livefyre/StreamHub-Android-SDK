package com.livefyre.streamhub_android_sdk;
import java.io.IOException;
import java.net.URL;
import android.os.AsyncTask;
import android.os.Message;
import android.os.Handler.Callback;

/**
 * 
 * @author zjj
 *
 */
class POSTDatainBackground extends AsyncTask<Object, Void, Void> {
	/**
	 * Notifies the callback with a message.
	 * The arg1 on the message is set to an HTTP statuscode.
	 * 
	 * @param callback
	 * @param statuscode
	 */
	private static void notify(Callback callback, Integer statuscode) {
		Message requestComplete = Message.obtain();
		requestComplete.arg1 = statuscode;
		callback.handleMessage(requestComplete);	
	}
	
	/**
	 * Performs a network request asynchronously. 
	 * Object is our wildcard, we expect a URL as arg0, a byte array as arg1, and a Callback as arg2.
	 */
	@Override
	protected Void doInBackground(Object... params) {
		URL endpoint = (URL) params[0];
		byte[] data = (byte[]) params[1];
		Callback callback = (Callback) params[2];
		Integer statusCode  = null;
		try {
			statusCode = POSTData.sendPost(endpoint, data);
		} catch (IOException e) {
			// Swallow exception?
		} 
		notify(callback, statusCode);
		
		return null;
	}
}
