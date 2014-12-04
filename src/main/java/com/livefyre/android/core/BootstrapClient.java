package com.livefyre.android.core;

import android.net.Uri;
import android.net.Uri.Builder;

import com.loopj.android.http.AsyncHttpResponseHandler;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.util.*;

/**
 * @author zjj
 */
public class BootstrapClient {
    /**
     * Performs a network request on a different thread and delivers a message to the callback.
     * A JSON object with the results will be bound to the message.
     *
     * @param networkId The collection's network as identified by domain, i.e. livefyre.com.
     * @param siteId    The Id of the article's site.
     * @param articleId The Id of the collection's article.
     * @param handler   Response handler
     * @throws UnsupportedEncodingException
     * @throws MalformedURLException
     */
    public static void getInit(String networkId,
                               String siteId,
                               String articleId,
                               AsyncHttpResponseHandler handler)
            throws UnsupportedEncodingException
    {
        getBootstrapPage(networkId, siteId, articleId, handler);
    }

    /**
     * Performs a network request on a different thread and delivers a message to the callback.
     * A JSON object with the results will be bound to the message.
     *
     * @param networkId The collection's network as identified by domain, i.e. livefyre.com.
     * @param siteId    The Id of the article's site.
     * @param articleId The Id of the collection's article.
     * @param opts Optional parameters to pass in. Currently takes in pageNumber param for bootstrap page number.
     * @param handler   Response handler
     * @throws UnsupportedEncodingException
     * @throws MalformedURLException
     */
    public static void getBootstrapPage(String networkId,
                               String siteId,
                               String articleId,
                               AsyncHttpResponseHandler handler,
                               Map<String, Object>... opts)
            throws UnsupportedEncodingException
    {
        final String bootstrapEndpoint = generateBootstrapEndpoint(networkId, siteId, articleId, opts);
        //Log.d("SDK","Before call "+bootstrapEndpoint);
        HttpClient.client.get(bootstrapEndpoint, handler);
        //Log.d("SDK","After call");
    }

    /**
     * Generates an init endpoint with the specified parameters.
     *
     * @param networkId The collection's network as identified by domain, i.e. livefyre.com.
     * @param siteId    The Id of the article's site.
     * @param articleId The Id of the collection's article.
     * @return The init endpoint with the specified parameters.
     * @throws UnsupportedEncodingException
     * @throws MalformedURLException
     */
    public static String generateInitEndpoint(String networkId,
                                              String siteId,
                                              String articleId)
            throws UnsupportedEncodingException
    {
        return generateBootstrapEndpoint(networkId, siteId, articleId);
    }

    /**
     * Generates a general bootstrap endpoint with the specified parameters.
     *
     * @param networkId The collection's network as identified by domain, i.e. livefyre.com.
     * @param siteId    The Id of the article's site.
     * @param articleId The Id of the collection's article.
     * @param opts Optional parameters to pass in. Currently takes in pageNumber param for Bootstrap page number.
     * @return The init endpoint with the specified parameters.
     * @throws UnsupportedEncodingException
     * @throws MalformedURLException
     */
    public static String generateBootstrapEndpoint(String networkId,
                                              String siteId,
                                              String articleId,
                                              Map<String, Object>... opts)
            throws UnsupportedEncodingException
    {
        // Casting
        final String article64 = Helpers.generateBase64String(articleId);

        // Build the URL
        Builder uriBuilder = new Uri.Builder()
                .scheme(Config.scheme)
                .authority(Config.bootstrapDomain + "." + Config.getHostname(networkId))
                .appendPath("bs3")
                .appendPath(networkId)
                .appendPath(siteId)
                .appendPath(article64);

        if (opts.length <= 0) {
            uriBuilder.appendPath("init");
        }
        else {
            System.out.println(opts[0].get("pageNumber"));
            if(opts[0].get("pageNumber") instanceof Integer) {
                String page = opts[0].get("pageNumber").toString() + ".json";
                uriBuilder.appendPath(page);
            }
            else {
                throw new IllegalArgumentException("Bootstrap page number must be an Integer");
            }
        }

        return uriBuilder.toString();
    }
}