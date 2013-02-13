package com.livefyre.streamhub_android_sdk;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import android.net.Uri;
import android.net.Uri.Builder;
import android.os.Handler;
import android.os.Handler.Callback;

import com.livefyre.streamhub_android_sdk.POSTDatainBackground;

public class WriteClient {
	public static void likeContentInBackground
	(String contentId, String userToken, String collectionId, String networkDomain, Handler.Callback callback) throws MalformedURLException, UnsupportedEncodingException {
		URL likeContentEndpoint = generateLikeContentEndpoint(contentId, userToken, collectionId, networkDomain);
		likeOrUnlikeContentInBackground(likeContentEndpoint, collectionId, callback);
	}
	
	public static URL generateLikeContentEndpoint
	(String contentId, String userToken, String collectionId, String networkDomain) throws MalformedURLException {
		return generateLikeOrUnlikeEndpoint(contentId, userToken, collectionId, networkDomain, "like");
	}
	
	public static void unlikeContentInBackground
	(String contentId, String userToken, String collectionId, String networkDomain, Handler.Callback callback) throws MalformedURLException, UnsupportedEncodingException {
		URL unlikeContentEndpoint = generateUnlikeContentEndpoint(contentId, userToken, collectionId, networkDomain);
		likeOrUnlikeContentInBackground(unlikeContentEndpoint, collectionId, callback);
	}
	
	public static URL generateUnlikeContentEndpoint
	(String contentId, String userToken, String collectionId, String networkDomain) throws MalformedURLException {
		return generateLikeOrUnlikeEndpoint(contentId, userToken, collectionId, networkDomain, "unlike");
	}
	
	public static byte[] generateLikeOrUnlikePostData(String collectionId) throws UnsupportedEncodingException {
		// Build the post data =
		Builder bodyBuilder = new Uri.Builder();
		bodyBuilder.appendQueryParameter("collection_id", collectionId);
		return Helpers.buildPostBody(bodyBuilder);
	}
	
	private static void likeOrUnlikeContentInBackground
	(URL endpoint, String collectionId, Callback callback) throws MalformedURLException, UnsupportedEncodingException {
		byte[] contentBody = generateLikeOrUnlikePostData(collectionId);
		
		new POSTDatainBackground().execute(endpoint, contentBody, callback); 
	}
	
	private static URL generateLikeOrUnlikeEndpoint
	(String contentId, String userToken, String collectionId, String networkDomain, String action) throws MalformedURLException {
		// Build the Query Params
		Builder paramsBuilder = new Uri.Builder();
		paramsBuilder.appendQueryParameter("lftoken", userToken);

		// Build the URL
		StringBuilder urlStringBuilder = new StringBuilder(Constants.scheme)
		.append(Constants.quillDomain).append(".")
		.append(networkDomain)
		.append("/api/v3.0/message/")
		.append(contentId).append("/")
		.append(action)
		.append(paramsBuilder.toString());
				
		return Helpers.buildURL(urlStringBuilder.toString());
	}
	
	public static void postContent
	(String body, String userToken, String parentId, String collectionId, String networkDomain, Handler.Callback callback) throws MalformedURLException, UnsupportedEncodingException {
		URL postContentEndpoint = generatePostContentURL(userToken, parentId, collectionId, networkDomain);
		byte[] contentBody = generatePostContentBody(body);
		
		new POSTDatainBackground().execute(postContentEndpoint, contentBody, callback); 
	}
	
	public static URL generatePostContentURL
	(String userToken, String parentId, String collectionId, String networkDomain) throws MalformedURLException {
		// Build the Query Params
		Builder paramsBuilder = new Builder();
		paramsBuilder.appendQueryParameter("lftoken", userToken);
		
		// Build the URL
		StringBuilder urlStringBuilder = new StringBuilder(Constants.scheme)
		.append(Constants.quillDomain).append(".")
		.append(networkDomain)
		.append("/api/v3.0/collection/")
		.append(collectionId)
		.append("/post/")
		.append(paramsBuilder.toString());
				
		return Helpers.buildURL(urlStringBuilder.toString());
	}
	
	public static byte[] generatePostContentBody(String body) throws UnsupportedEncodingException {
		// Build the post body
		Builder bodyBuilder = new Uri.Builder();
		bodyBuilder.appendQueryParameter("body", body);
		
		return Helpers.buildPostBody(bodyBuilder);
	}
}
