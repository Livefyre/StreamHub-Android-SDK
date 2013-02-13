package com.livefyre.streamhub_android_sdk;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import android.net.Uri.Builder;

import android.util.Base64;

public class Helpers {
	public static String base64String(String instring) throws UnsupportedEncodingException {
		byte[]byteTransform = null;
		String string64 = null; 
		try {
			byteTransform = instring.getBytes("UTF-8");			
		} catch (UnsupportedEncodingException e) {
			boolean DEBUG = BuildConfig.DEBUG;
		    if (DEBUG) System.err.println("Caught UnsupportedEncodingException in com.livefyre.streamhub_android_sdk.Helpers.base64String: " + e.getStackTrace());
		    throw e; 
		} finally {
			string64 = Base64.encodeToString(byteTransform, Base64.NO_WRAP);
		}
		
		return string64;
	}
	
	public static URL buildURL(String instring) throws MalformedURLException {
		URL url = null;
		try {
			url = new URL(instring);
		} catch (MalformedURLException e) {
			boolean DEBUG = BuildConfig.DEBUG;
		    if (DEBUG) System.err.println("Caught MalformedURLException in com.livefyre.streamhub_android_sdk.Helpers.buildURL: " + e.getStackTrace());
		    throw e;
		}
		
		return url;
	}
	
	public static byte[] buildPostBody(Builder bodyBuilder) throws UnsupportedEncodingException {
		byte[] contentBody = null;
		try {
			contentBody = bodyBuilder.toString().substring(1).getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			boolean DEBUG = BuildConfig.DEBUG;
		    if (DEBUG) System.err.println("Caught UnsupportedEncodingException in com.livefyre.streamhub_android_sdk.Helpers.buildBody: " + e.getStackTrace());
		    throw e; 
		}
		
		return contentBody;
	}
}
