package com.livefyre.streamhub_android_sdk;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;

import android.net.Uri;
import android.net.Uri.Builder;
import android.os.Handler;
import android.os.Handler.Callback;

public class WriteClient {
	/**
	 * Performs a network request on a different thread and delivers a message to the callback. A JSON object with the results will be bound to the message.
 	 *
 	 * @param contentId The content to like.
	 * @param userToken The lftoken representing a user.
	 * @param collectionId The collect in which the content appears.
	 * @param networkDomain The collection's network as identified by domain, i.e. livefyre.com.
	 * @param callback Implement "handleMessage" for this callback.
	 * @throws UnsupportedEncodingException
	 * @throws MalformedURLException
	 */
	public static void likeContentInBackground
	(String contentId, String userToken, String collectionId, String networkDomain, Handler.Callback callback) throws MalformedURLException, UnsupportedEncodingException {
		URL likeContentEndpoint = generateLikeContentEndpoint(contentId, userToken, collectionId, networkDomain);
		likeOrUnlikeContentInBackground(likeContentEndpoint, collectionId, callback);
	}

	public static URL generateLikeContentEndpoint
	(String contentId, String userToken, String collectionId, String networkDomain) throws MalformedURLException {
		return generateLikeOrUnlikeEndpoint(contentId, userToken, collectionId, networkDomain, "like");
	}

	/**
	 * Performs a network request on a different thread and delivers a message to the callback. A JSON object with the results will be bound to the message.
 	 *
 	 * @param contentId The content to unlike.
	 * @param userToken The lftoken representing a user.
	 * @param collectionId The collect in which the content appears.
	 * @param networkDomain The collection's network as identified by domain, i.e. livefyre.com.
	 * @param callback Implement "handleMessage" for this callback.
	 * @throws UnsupportedEncodingException
	 * @throws MalformedURLException
	 */
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
		return Helpers.generatePostBody(bodyBuilder);
	}

	public static void likeOrUnlikeContentInBackground
	(URL endpoint, String collectionId, Callback callback) throws MalformedURLException, UnsupportedEncodingException {
		byte[] contentBody = generateLikeOrUnlikePostData(collectionId);

		new POSTDataInBackground().execute(endpoint, contentBody, callback);
	}

	public static URL generateLikeOrUnlikeEndpoint
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

		return Helpers.generateURL(urlStringBuilder.toString());
	}

	/**
	 * Performs a network request on a different thread and delivers a message to the callback. A JSON object with the results will be bound to the message.
 	 *
 	 * @param body The content body.
	 * @param userToken The lftoken representing a user.
	 * @param parentId The parent content if this is a reply.
	 * @param collectionId The Id of the collection to auth against.
	 * @param networkDomain The collection's network as identified by domain, i.e. livefyre.com.
	 * @param callback Implement "handleMessage" for this callback.
	 * @throws UnsupportedEncodingException
	 * @throws MalformedURLException
	 */
	public static void postContentInBackground
	(String body, String userToken, String parentId, String collectionId, String networkDomain, Handler.Callback callback) throws MalformedURLException, UnsupportedEncodingException {
		URL postContentEndpoint = generatePostContentURL(userToken, parentId, collectionId, networkDomain);
		byte[] contentBody = generatePostContentBody(body, parentId);

		new POSTDataInBackground().execute(postContentEndpoint, contentBody, callback);
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

		return Helpers.generateURL(urlStringBuilder.toString());
	}

	public static byte[] generatePostContentBody(String body, String parentId) throws UnsupportedEncodingException {
		// Build the post body
		Builder bodyBuilder = new Uri.Builder();
		bodyBuilder.appendQueryParameter("body", body);

		if (parentId != null) {
			bodyBuilder.appendQueryParameter("parent_id", parentId);
		}

		return Helpers.generatePostBody(bodyBuilder);
	}

	public static URL generatePostFlagURL
	(String networkDomain, String contentId, String userToken, String flag) throws MalformedURLException {
		// Build the Query Params
		Builder paramsBuilder = new Uri.Builder();
		paramsBuilder.appendQueryParameter("lftoken", userToken);

		// Build the URL
		StringBuilder urlStringBuilder = new StringBuilder(Constants.scheme)
		.append(Constants.quillDomain).append(".")
		.append(networkDomain)
		.append("/api/v3.0/message/")
		.append(contentId).append("/")
		.append("flag").append("/")
		.append(flag).append("/")
		.append(paramsBuilder.toString());

		return Helpers.generateURL(urlStringBuilder.toString());
	}

	public static byte[] generatePostFlagBody(String contentId, String collectionId, String flag, String notes, String email) throws UnsupportedEncodingException {
		// Build the post body
		Builder bodyBuilder = new Uri.Builder();
		// Redundant params but mimicking the JS widget behavior.
		bodyBuilder.appendQueryParameter("message_id", contentId);
		bodyBuilder.appendQueryParameter("collection_id", collectionId);
		bodyBuilder.appendQueryParameter("flag", flag);

		if (notes != null) {
			bodyBuilder.appendQueryParameter("notes", notes);
		}

		if (email != null) {
			bodyBuilder.appendQueryParameter("email", email);
		}

		return Helpers.generatePostBody(bodyBuilder);
	}

	/**
	 * Performs a network request on a different thread and delivers a message to the callback. A JSON object with the results will be bound to the message.
 	 *
 	 * @param contentId The Id of the content to flag.
	 * @param collectionId The Id of the collection to auth against.
	 * @param networkDomain The collection's network as identified by domain, i.e. livefyre.com.
	 * @param flagType The flagging action to take.
	 * @param userToken The lftoken representing a user.
	 * @param callback Implement "handleMessage" for this callback.
	 * @throws UnsupportedEncodingException
	 * @throws MalformedURLException
	 */
	public static void flagContentInBackground
	(String contentId, String collectionId, String networkDomain, Constants.FlagType flagType, String notes, String email, String userToken, Handler.Callback callback) throws MalformedURLException, UnsupportedEncodingException {
		String flag = adaptFlag(flagType);
		URL postFlagEndpoint = generatePostFlagURL(networkDomain, contentId, userToken, flag);
		byte[] postBody = generatePostFlagBody(contentId, collectionId, flag, notes, email);

		new POSTDataInBackground().execute(postFlagEndpoint, postBody, callback);
	}

	public static String adaptFlag
	(Constants.FlagType flagType) {
		switch (flagType) {
			case OFFENSIVE:
				return "offensive";
			case SPAM:
				return "spam";
			case DISAGREE:
				return "disagree";
			case OFF_TOPIC:
				return "off-topic";
			default:
				throw new IllegalArgumentException("Unknown flag type.");
		}
	}
}
