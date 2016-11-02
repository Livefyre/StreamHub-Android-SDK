package com.livefyre.streamhub_android_sdk.network;

import android.net.Uri;
import android.net.Uri.Builder;
import android.util.Log;

import com.livefyre.streamhub_android_sdk.util.LivefyreConfig;
import com.livefyre.streamhub_android_sdk.util.Helpers;
import com.loopj.android.http.AsyncHttpResponseHandler;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.util.Map;

/**
 * @author zjj
 */
public class BootstrapClient {
    public static final String TAG = BootstrapClient.class.getSimpleName();

    /**
     * Performs a network request on a different thread and delivers a message to the callback.
     * A JSON object with the results will be bound to the message.
     *
     * @param siteId    The Id of the article's site.
     * @param articleId The Id of the collection's article.
     * @param handler   Response handler
     * @throws UnsupportedEncodingException
     * @throws MalformedURLException
     */
    public static void getInit(String siteId, String articleId, AsyncHttpResponseHandler handler) throws UnsupportedEncodingException {
        getBootstrapPage(siteId, articleId, handler);
    }

    /**
     * Performs a network request on a different thread and delivers a message to the callback.
     * A JSON object with the results will be bound to the message.
     *
     * @param siteId    The Id of the article's site.
     * @param articleId The Id of the collection's article.
     * @param opts      Optional parameters to pass in. Currently takes in pageNumber param for bootstrap page number.
     * @param handler   Response handler
     * @throws UnsupportedEncodingException
     * @throws MalformedURLException
     */
    public static void getBootstrapPage(String siteId, String articleId, AsyncHttpResponseHandler handler, Map<String, Object>... opts) throws UnsupportedEncodingException {
        final String bootstrapEndpoint = generateBootstrapEndpoint(siteId, articleId, opts);
        Log.d(TAG, "getBootstrapPage: BootstrapEndPoint" + bootstrapEndpoint);
        HttpClient.client.get(bootstrapEndpoint, handler);
    }

    /**
     * Generates an init endpoint with the specified parameters.
     *
     * @param siteId    The Id of the article's site.
     * @param articleId The Id of the collection's article.
     * @return The init endpoint with the specified parameters.
     * @throws UnsupportedEncodingException
     * @throws MalformedURLException
     */
    public static String generateInitEndpoint(String siteId,String articleId)throws UnsupportedEncodingException {
        return generateBootstrapEndpoint(siteId, articleId);
    }

    /**
     * Generates a general bootstrap endpoint with the specified parameters.
     *
     * @param siteId    The Id of the article's site.
     * @param articleId The Id of the collection's article.
     * @param opts      Optional parameters to pass in. Currently takes in pageNumber param for Bootstrap page number.
     * @return The init endpoint with the specified parameters.
     * @throws UnsupportedEncodingException
     * @throws MalformedURLException
     */
    public static String generateBootstrapEndpoint(String siteId,String articleId,Map<String, Object>... opts)throws UnsupportedEncodingException {
        // Casting
        final String article64 = Helpers.generateBase64String(articleId);

        // Build the URL
        Builder uriBuilder = new Uri.Builder()
                .scheme(LivefyreConfig.scheme)
                .authority(LivefyreConfig.bootstrapDomain + "." + LivefyreConfig.getConfiguredNetworkID())
                .appendPath("bs3")
                .appendPath("v3.1")
                .appendPath(LivefyreConfig.getConfiguredNetworkID())
                .appendPath(siteId)
                .appendPath(article64);

        if (opts.length <= 0) {
            uriBuilder.appendPath("init");
        } else {
            if (opts[0].get("pageNumber") instanceof Integer) {
                String page = opts[0].get("pageNumber").toString() + ".json";
                uriBuilder.appendPath(page);
            } else {
                throw new IllegalArgumentException("Bootstrap page number must be an Integer");
            }
        }

        return uriBuilder.toString();
    }
}
