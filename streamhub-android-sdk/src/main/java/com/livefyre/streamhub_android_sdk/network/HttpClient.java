package com.livefyre.streamhub_android_sdk.network;

import com.loopj.android.http.AsyncHttpClient;


/**
 * Created by jonathan on 7/29/13.
 */
public class HttpClient {
    public static AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
}
