package com.livefyre.streamhub_android_sdk;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import android.net.Uri.Builder;
import android.os.Handler.Callback;

/** 
 * @author zjj
 */
public class AdminClient {
	/**
	 * Performs a network request on a different thread and calls delivers a message to the callback. A JSON object with the results will be bound to the message.
	 * 
	 * It is necessary to provide either a collectionId or a siteId combined with an articleId.
	 * 
	 * @param userToken The lftoken representing a user.
	 * @param collectionId The Id of the collection to auth against.
	 * @param articleId The Id of the collection's article.
	 * @param siteId The Id of the article's site.
	 * @param networkDomain The collection's network as identified by domain, i.e. livefyre.com.
	 * @param callback Implement "handleMessage" for this callback.
	 * @throws UnsupportedEncodingException
	 * @throws MalformedURLException
	 */
	public static void authenticateUserInBackground
	(String userToken, String collectionId, String articleId, String siteId, String networkDomain, Callback callback) throws UnsupportedEncodingException, MalformedURLException {
		URL authEndpoint = generateAuthEndpoint(userToken, collectionId, articleId, siteId, networkDomain); 
	
		new GETJSONinBackground().execute(authEndpoint, callback);
	}
	
	/**
	 * Generates an auth endpoint with the specified parameters.
	 * 
	 * @param userToken The lftoken representing a user.
	 * @param collectionId The Id of the collection to auth against.
	 * @param articleId The Id of the collection's article.
	 * @param siteId The Id of the article's site.
	 * @param networkDomain The collection's network as identified by domain, i.e. livefyre.com.
	 * @return The auth endpoint with the specified parameters.
	 * @throws UnsupportedEncodingException
	 * @throws MalformedURLException
	 */
	public static URL generateAuthEndpoint
	(String userToken, String collectionId, String articleId, String siteId, String networkDomain) throws UnsupportedEncodingException, MalformedURLException {
		Builder paramsBuilder = new Builder();
		if (collectionId != null) {
			paramsBuilder.appendQueryParameter("collectionId", collectionId);
			paramsBuilder.appendQueryParameter("lftoken", userToken);
		} else {
			String article64 = Helpers.base64String(articleId);
			paramsBuilder.appendQueryParameter("siteId", siteId);
			paramsBuilder.appendQueryParameter("articleId", article64);
			paramsBuilder.appendQueryParameter("lftoken", userToken);
		}
		
		// Build the URL
		StringBuilder urlStringBuilder = new StringBuilder(Constants.scheme)
		.append(Constants.adminDomain).append(".")
		.append(networkDomain)
		.append("/api/v3.0/auth/")
		.append(paramsBuilder.toString());
		
		URL authEndpoint = Helpers.buildURL(urlStringBuilder.toString());

		return authEndpoint;
	}
}
