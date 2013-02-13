package com.livefyre.streamhub_android_sdk;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;
/**
 * 
 * @author zjj
 *
 */
public class GETJSON {
	/**
	 * Performs a GET request for the given endpoint and returns a JSON object derived from the results.
	 * 
	 * @param endpoint An HTTP endpoint to request. 
	 * @return A JSONObject with the results.
	 * @throws IOException
	 * @throws JSONException
	 */
	public static JSONObject fetchData(URL endpoint) throws IOException, JSONException {
		HttpURLConnection urlConnection = null;
		JSONObject data = null; 
		try {
			urlConnection = (HttpURLConnection) endpoint.openConnection();
		} catch (IOException e) {
			boolean DEBUG = BuildConfig.DEBUG;
		    if (DEBUG) System.err.println("Caught IOException in com.livefyre.streamhub_android_sdk.GETJSON.fetchData: " + e.getStackTrace());
		    throw e;
		}
		try {
			InputStream in = new BufferedInputStream(urlConnection.getInputStream());
			BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
		    StringBuilder jsonBuilder = new StringBuilder();
		    int inChar;
		    while ((inChar = reader.read()) != -1) {
		    	char character =  (char) inChar;
		        jsonBuilder.append(character);
		    }
		    in.close(); 
			data = parseJSONData(jsonBuilder.toString());
		} catch (IOException e) {
			boolean DEBUG = BuildConfig.DEBUG;
		    if (DEBUG) System.err.println("Caught IOException in com.livefyre.streamhub_android_sdk.GETJSON.fetchData: " + e.getStackTrace());
		    throw e;
		} finally {
			urlConnection.disconnect();
		}
		return data;
	}
	/**
	 * Parses JSON string into JSON objects.
	 * 
	 * @param json String representation of a JSON Object.
	 * @return A JSON object.
	 * @throws JSONException
	 */
	public static JSONObject parseJSONData(String json) throws JSONException {
		try {
			return new JSONObject(json);
		} catch (JSONException e) {
			boolean DEBUG = BuildConfig.DEBUG;
		    if (DEBUG) System.err.println("Caught JSONException in com.livefyre.streamhub_android_sdk.GETJSON.fetchData: " + e.getStackTrace());
		    throw e;
		}
	}
}
