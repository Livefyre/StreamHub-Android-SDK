package com.livefyre.streamhub_android_sdk;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 
 * @author zjj
 *
 */
public class POSTData {
	/**
	 * Posts data to a given endpoint.
	 * 
	 * @param endpoint
	 * @param data
	 * @return The status code resulting from the network operation. 
	 * @throws IOException
	 */
	public static Integer sendPost(URL endpoint, byte[]data) throws IOException {
		Integer statuscode = null;		
		HttpURLConnection urlConnection = null;
		
		try {
			urlConnection = (HttpURLConnection) endpoint.openConnection();
		} catch (IOException e) {
			boolean DEBUG = BuildConfig.DEBUG;
		    if (DEBUG) System.err.println("Caught IOException in com.livefyre.streamhub_android_sdk.POSTData.sendPost: " + e.getStackTrace());
		    throw e;
		}
		urlConnection.setDoOutput(true);
		urlConnection.setFixedLengthStreamingMode(data.length);
		OutputStream out = null;
		try {
		    out = urlConnection.getOutputStream();
		} catch (IOException e) {
			boolean DEBUG = BuildConfig.DEBUG;
		    if (DEBUG) System.err.println("Caught IOException in com.livefyre.streamhub_android_sdk.POSTData.sendPost: " + e.getStackTrace());
		    throw e;
		}
		try {
		    out.write(data);
			statuscode = urlConnection.getResponseCode();
		} catch (IOException e) {
			boolean DEBUG = BuildConfig.DEBUG;
		    if (DEBUG) System.err.println("Caught IOException in com.livefyre.streamhub_android_sdk.POSTData.sendPost: " + e.getStackTrace());
		    throw e;
		}  finally {
		    urlConnection.disconnect();
		}
		
		return statuscode;
	}
}
