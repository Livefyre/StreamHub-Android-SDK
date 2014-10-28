package com.livefyre.android.core;

import android.net.Uri;
import android.net.Uri.Builder;

import com.loopj.android.http.AsyncHttpResponseHandler;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;

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
     * @param handler   Response handler
     * @throws UnsupportedEncodingException
     * @throws MalformedURLException
     */
    public static void getBootstrapPage(String networkId,
                               String siteId,
                               String articleId,
                               AsyncHttpResponseHandler handler,
                               Object... pageNumber)
            throws UnsupportedEncodingException
    {
        final String bootstrapEndpoint = generateBootstrapEndpoint(networkId, siteId, articleId, pageNumber);
        HttpClient.client.get(bootstrapEndpoint, handler);
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
     * @return The init endpoint with the specified parameters.
     * @throws UnsupportedEncodingException
     * @throws MalformedURLException
     */
    public static String generateBootstrapEndpoint(String networkId,
                                              String siteId,
                                              String articleId,
                                              Object... pageNumber)
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

        if (pageNumber.length <= 0) {
            uriBuilder.appendPath("init");
        }
        else {
            if(pageNumber[0] instanceof Integer) {
                String page = pageNumber[0] + ".json";
                uriBuilder.appendPath(page);
            }
            else {
                throw new IllegalArgumentException("Bootstrap page number must be an Integer");
            }
        }

        return uriBuilder.toString();
    }
}
