package com.livefyre.streamhub_android_sdk.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.kvana.streamhub_android_sdk.R;
import com.livefyre.streamhub_android_sdk.network.AuthenticationClient;
import com.livefyre.streamhub_android_sdk.util.LivefyreConfig;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;


public class AuthenticationActivity extends BaseActivity {
    private class AuthCallback implements AuthenticationClient.ResponseHandler {
        @Override
        public void success(String res, String error) {
            dismissProgressDialog();
            Log.d(TAG, "onSuccess: " + res.toString());
            try {
                JSONObject resJsonObj = new JSONObject(res);
                JSONObject jsonObject = resJsonObj.optJSONObject("data");
                token = jsonObject.optString("token");
                respond(token);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void failure(String msg) {
            dismissProgressDialog();
            respond(token);
        }
    }

    private class LoginWebViewClient extends WebViewClient {

        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Log.d(TAG, "shouldOverrideUrlLoading: "+url);
            if (url.contains("AuthCanceled")) {
                respond(token);
            } else if (url.contains(next)) {
                Log.d(TAG, "shouldOverrideUrlLoading: redirected to next url");
            } else if (authCallCount == 0 && url.equals("https://identity."+environment+"/accounts/profile/#")) {
                authCallCount++;
                try {
                    showProgressDialog();
                    AuthenticationClient.authenticate(
                            environment,
                            LivefyreConfig.origin,
                            LivefyreConfig.referer,
                            CookieManager.getInstance().getCookie(URL),
                            new AuthCallback());
                    webview.setVisibility(View.GONE);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            } else if (url.equals("http://livefyre-cdn-dev.s3.amazonaws.com/demos/lfep2-comments.html#")){
                try {
                    showProgressDialog();
                    AuthenticationClient.authenticate(
                            environment,
                            LivefyreConfig.origin,
                            LivefyreConfig.referer,
                            CookieManager.getInstance().getCookie(URL),
                            new AuthCallback());
                    webview.setVisibility(View.GONE);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
            return false;
        }
    }

    private static final String TAG = AuthenticationActivity.class.getName();
    public static final int AUTHENTICATION_REQUEST_CODE = 200;
    public static String TOKEN = "token";
    public static String ENVIRONMENT = "environment";
    public static String NETWORK_ID = "network_id";
    public static String ENCODED_URL = "encoded_url_param_string";
    public static String NEXT = "next";
    private String environment, network, encodedUrlParamString, next;
    private WebView webview;
    private Toolbar toolbar;
    private static String URL;
    private int authCallCount;
    private String token;

    /**
     * starts authentication activity
     *
     * @param activity              your activity
     * @param environment           environment
     * @param networkId             networkId
     * @param encodedUrlParamString encodedUrlParamString
     * @param next                  next
     */
    public static void start(Activity activity, String environment, String networkId, String encodedUrlParamString, String next) {
        Intent authenticationActivity = new Intent(activity, AuthenticationActivity.class);
        authenticationActivity.putExtra(AuthenticationActivity.ENVIRONMENT, environment);
        authenticationActivity.putExtra(AuthenticationActivity.NETWORK_ID, networkId);
        authenticationActivity.putExtra(AuthenticationActivity.ENCODED_URL, encodedUrlParamString);
        authenticationActivity.putExtra(AuthenticationActivity.NEXT, next);
        activity.startActivityForResult(authenticationActivity, AuthenticationActivity.AUTHENTICATION_REQUEST_CODE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);

        CookieManager.getInstance().removeAllCookie();

        environment = getIntent().getStringExtra(ENVIRONMENT);
        network = getIntent().getStringExtra(NETWORK_ID);
        encodedUrlParamString = getIntent().getStringExtra(ENCODED_URL);
        try {
            next = URLEncoder.encode(getIntent().getStringExtra(NEXT), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        //validating url params
        if (environment == null || environment.length() == 0) {
            showToast("Environment required.");
            finish();
        }

        if (network == null || network.length() == 0) {
            showToast("Network required.");
            finish();
        }

        if (encodedUrlParamString == null || encodedUrlParamString.length() == 0) {
            showToast("Encoded Url required.");
            finish();
        }

        if (next == null || next.length() == 0) {
            showToast("Next required.");
            finish();
        }
        //Preparing Url if all params are ok
        URL = String.format("https://identity.%s/%s/pages/auth/engage/?app=%s&next=%s", environment, network, encodedUrlParamString, next);
        //Configure WebView
        webview = (WebView) findViewById(R.id.webview);
        webview.getSettings().setJavaScriptEnabled(true);
        webview.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webview.setWebChromeClient(new WebChromeClient());
        webview.setWebViewClient(new LoginWebViewClient());
        //load url
        webview.loadUrl(URL);

        buildToolBar();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        respond(token);
    }

    private void buildToolBar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        //toolbar
        setSupportActionBar(toolbar);
        //disable title on toolbar
        if (null != getSupportActionBar()) getSupportActionBar().setDisplayShowTitleEnabled(false);

        TextView cancel_txt = (TextView) findViewById(R.id.cancel_txt);
        if (cancel_txt != null)
            cancel_txt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    respond(token);
                }
            });
    }

    private void respond(String token) {
        if (token == null || token.length() == 0) {
            cancelResult();
        } else {
            sendResult(token);
        }
    }
    /**
     * Sends result to requested activity
     *
     * @param token - Requested token
     */
    private void sendResult(String token) {
        Intent intent = new Intent();
        intent.putExtra(TOKEN, token);
        setResult(RESULT_OK, intent);
        finish();
    }

    /**
     * it sends request canceled info to requested activity
     */
    private void cancelResult() {
        showToast("Authenticate request cancelled..");
        Intent intent = new Intent();
        setResult(RESULT_CANCELED, intent);
        finish();
    }

    public static void logout() {
        CookieManager.getInstance().removeAllCookie();
    }

}
