package com.livefyre.streamhub_android_sdk;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONException;

import android.net.Uri;
import android.net.Uri.Builder;
import android.os.Handler.Callback;

public class StreamClient {
	public static Builder generateStreamEndpointBuilder(String collectionId, String networkDomain) throws MalformedURLException {
		Builder uriBuilder = new Uri.Builder();
		uriBuilder.appendPath(Constants.scheme)
		.appendPath(Constants.streamDomain).appendPath(".")
		.appendPath(networkDomain)
		.appendPath("/v3.0/collection/")
		.appendPath(collectionId)
		.appendPath("/");
		
		return uriBuilder; 
	}
	
	public static URL generateStreamEndpoint(Builder endpointBuilder, String eventId) throws MalformedURLException {
		endpointBuilder.appendPath(eventId);
		String endpointString = endpointBuilder.build().toString();
		return Helpers.buildURL(endpointString);
	}
	
	public static void pollStreamEndpoint(Builder endpointBuilder, String eventId) throws IOException, JSONException {
		URL streamEndpoint = generateStreamEndpoint(endpointBuilder, eventId);
		
		GETJSON.fetchData(streamEndpoint);
	}
	
	public static void pollStreamEndpointInBackground(Builder endpointBuilder, String eventId, Callback callback) throws MalformedURLException {
		URL streamEndpoint = generateStreamEndpoint(endpointBuilder, eventId);
		
		new GETJSONinBackground().execute(streamEndpoint, callback);
	}
}
