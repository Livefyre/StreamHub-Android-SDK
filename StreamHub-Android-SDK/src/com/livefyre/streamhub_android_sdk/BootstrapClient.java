package com.livefyre.streamhub_android_sdk;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import android.os.Handler.Callback;
import com.livefyre.streamhub_android_sdk.Helpers;
import com.livefyre.streamhub_android_sdk.GETJSONinBackground;

/**
 * @author zjj
 */
public class BootstrapClient {
	/**	 
	 * Performs a network request on a different thread and calls delivers a message to the callback. A JSON object with the results will be bound to the message.
	 * 
	 * @param articleId The Id of the collection's article.
	 * @param siteId The Id of the article's site.
	 * @param networkDomain The collection's network as identified by domain, i.e. livefyre.com.
	 * @param environment Where the collection is hosted, i.e. t-402. Used for development/testing purposes, optional
	 * @param callback
	 * @throws UnsupportedEncodingException
	 * @throws MalformedURLException
	 */
	public static void getInitInBackground
	(String articleId, String siteId, String networkDomain, String environment, Callback callback) throws UnsupportedEncodingException, MalformedURLException {
		URL initEndpoint = generateInitEndpoint(articleId, siteId, networkDomain, environment);
		
		new GETJSONinBackground().execute(initEndpoint, callback);
	}
	/**
	 * Generates an init endpoint with the specified parameters.
	 * 
	 * @param articleId The Id of the collection's article.
	 * @param siteId The Id of the article's site.
	 * @param networkDomain The collection's network as identified by domain, i.e. livefyre.com.
	 * @param environment  Where the collection is hosted, i.e. t-402. Used for development/testing purposes, optional
	 * @return The init endpoint with the specified parameters.
	 * @throws UnsupportedEncodingException
	 * @throws MalformedURLException
	 */
	public static URL generateInitEndpoint
	(String articleId, String siteId, String networkDomain, String environment) throws UnsupportedEncodingException, MalformedURLException {
		// Casting
		String article64 = Helpers.base64String(articleId);
		String urlSafeArticle64 = URLEncoder.encode(article64, "UTF-8");
		// Build the URL
		StringBuilder urlStringBuilder = new StringBuilder(Constants.scheme)
		.append(Constants.bootstrapDomain).append(".")
		.append(networkDomain)
		.append("/bs3/");
		if (environment != null) {
			urlStringBuilder.append(environment).append("/"); 
		}
		urlStringBuilder.append(networkDomain).append("/")
		.append(siteId).append("/")
		.append(urlSafeArticle64).append("/")
		.append("init");
		
		 return Helpers.buildURL(urlStringBuilder.toString());
	}
}
