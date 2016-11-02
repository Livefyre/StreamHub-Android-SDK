package com.livefyre.streamhub_android_sdk.network;

import android.net.Uri;
import android.net.Uri.Builder;
import android.util.Log;

import com.livefyre.streamhub_android_sdk.network.HttpClient;
import com.livefyre.streamhub_android_sdk.util.LivefyreConfig;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;

import cz.msebera.android.httpclient.Header;

public class StreamClient {
    public static String generateStreamUrl(String collectionId, String eventId) throws MalformedURLException {
        final Builder uriBuilder = new Uri.Builder()
                .scheme(LivefyreConfig.scheme)
                .authority(
                        LivefyreConfig.streamDomain + "."
                                + LivefyreConfig.getConfiguredNetworkID())
                .appendPath("v3.1").appendPath("collection")
                .appendPath(collectionId).appendPath("").appendPath(eventId);

        return uriBuilder.toString();
    }

    /**
     * Performs a long poll request to the Livefyre's stream endpoint
     *
     * @param collectionId The Id of the collection
     * @param eventId      The last eventId that was returned from either stream or
     *                     bootstrap. Event time a new eventId is returned, it should be
     *                     used in the next stream request.
     * @param handler      Response handler
     * @throws UnsupportedEncodingException
     * @throws MalformedURLException
     */
    public static void pollStreamEndpoint(
            final String collectionId, final String eventId,
            final AsyncHttpResponseHandler handler) throws IOException,
            JSONException {
        final String streamEndpoint = generateStreamUrl(
                collectionId, eventId);
        HttpClient.client.get(streamEndpoint, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                String response = new String(bytes);
                handler.onSuccess(i,headers,bytes);
                try {
                    if (response != null) {
                        Log.d("Stream Clint Call", "Success" + response);
                        JSONObject responseJson = new JSONObject(response);
                        String lastEvent;
                        if (responseJson.has("data")) {
                            lastEvent = responseJson.getJSONObject("data")
                                    .getString("maxEventId");

                            pollStreamEndpoint(collectionId,
                                    lastEvent, handler);
                        }
                    }
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                try {
                    pollStreamEndpoint(collectionId, eventId, handler);
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        });
    }
}
