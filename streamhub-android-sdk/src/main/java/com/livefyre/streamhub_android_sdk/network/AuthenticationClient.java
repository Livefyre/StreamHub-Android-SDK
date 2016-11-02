package com.livefyre.streamhub_android_sdk.network;

import android.net.Uri;
import android.os.AsyncTask;

import com.livefyre.streamhub_android_sdk.util.LivefyreConfig;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by kvanamac3 on 7/22/16.
 */
public class AuthenticationClient {
    public interface ResponseHandler {
        void success(String res, String error);

        void failure(String msg);
    }

    private static class Task extends AsyncTask<String, String, String> {
        private String environment, origin, referer, cookie;
        private ResponseHandler responseHandler;
        private boolean isOk;

        public Task(String environment, String origin, String referrer, String cookie, ResponseHandler responseHandler) {
            this.environment = environment;
            this.referer = referrer;
            this.origin = origin;
            this.cookie = cookie;
            this.responseHandler = responseHandler;
        }

        @Override
        protected String doInBackground(String... strings) {
            StringBuffer responseBuffer = null;
            InputStream is;
            Uri.Builder uriBuilder = new Uri.Builder()
                    .scheme(LivefyreConfig.scheme)
                    .authority(LivefyreConfig.identityDomain + "." + environment)
                    .appendPath(LivefyreConfig.getConfiguredNetworkID())
                    .appendPath("api")
                    .appendPath("v1.0")
                    .appendPath("public")
                    .appendPath("profile");
            try {
                URL url = new URL(uriBuilder.toString());

                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

                httpURLConnection.setRequestProperty("origin", origin);
                httpURLConnection.setRequestProperty("referer", referer);
                httpURLConnection.setRequestProperty("cookie", cookie);

                httpURLConnection.setRequestMethod("GET");
                if (httpURLConnection.getResponseCode() == 200 || httpURLConnection.getResponseCode() == 201) {
                    httpURLConnection.setConnectTimeout(10000);
                    httpURLConnection.setReadTimeout(10000);
                    is = httpURLConnection.getInputStream();
                    int ch;
                    responseBuffer = new StringBuffer();
                    while ((ch = is.read()) != -1) {
                        responseBuffer.append((char) ch);
                    }
                    is.close();

                    isOk = true;
                } else {
                    isOk = false;
                }

            } catch (IOException e) {
                e.printStackTrace();
                isOk = false;
            }

            return responseBuffer.toString();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (isOk && s != null && !s.trim().equals("")) {
                responseHandler.success(s, "");
            } else {
                responseHandler.failure("Authentication Error!");
            }

        }
    }

    /**
     * @param environment
     * @param origin
     * @param referer
     * @param cookie
     * @param handler
     * @throws UnsupportedEncodingException
     */
    public static void authenticate(String environment, String origin, String referer, String cookie, ResponseHandler handler) throws UnsupportedEncodingException {
        new Task(environment, origin, referer, cookie, handler).execute();
    }
}