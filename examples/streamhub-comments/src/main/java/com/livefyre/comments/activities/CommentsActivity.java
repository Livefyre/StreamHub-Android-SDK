package com.livefyre.comments.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.livefyre.comments.Config;
import com.livefyre.comments.R;
import com.livefyre.comments.adapter.CommentsAdapter;
import com.livefyre.comments.listeners.ContentUpdateListener;
import com.livefyre.comments.manager.ContentHandler;
import com.livefyre.comments.manager.LfManager;
import com.livefyre.comments.manager.SharedPreferenceManager;
import com.livefyre.comments.models.Content;
import com.livefyre.comments.util.Constant;
import com.livefyre.streamhub_android_sdk.activity.AuthenticationActivity;
import com.livefyre.streamhub_android_sdk.network.AdminClient;
import com.livefyre.streamhub_android_sdk.network.BootstrapClient;
import com.livefyre.streamhub_android_sdk.network.StreamClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.squareup.otto.Bus;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashSet;

import cz.msebera.android.httpclient.Header;

import static android.support.v7.widget.RecyclerView.OnScrollListener;
import static android.view.View.OnClickListener;

public class CommentsActivity extends BaseActivity implements ContentUpdateListener, OnClickListener {
    private class AdminCallback extends JsonHttpResponseHandler {
        @Override
        public void onFinish() {
            super.onFinish();
            dismissProgressDialog();
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
            super.onFailure(statusCode, headers, throwable, errorResponse);
            dismissProgressDialog();
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
            super.onFailure(statusCode, headers, throwable, errorResponse);
            dismissProgressDialog();
        }

        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONObject AdminClintJsonResponseObject) {
            super.onSuccess(statusCode, headers, AdminClintJsonResponseObject);
            dismissProgressDialog();
            loginTV.setText("Logout");
            JSONObject data;
            try {
                data = AdminClintJsonResponseObject.getJSONObject("data");

                if (!data.isNull("permissions")) {
                    JSONObject permissions = data.getJSONObject("permissions");
                    if (!permissions.isNull("moderator_key"))
                        SharedPreferenceManager.getInstance().putString(Constant.ISMOD, "yes");
                    else {
                        SharedPreferenceManager.getInstance().putString(Constant.ISMOD, "no");
                    }
                } else {
                    SharedPreferenceManager.getInstance().putString(Constant.ISMOD, "no");
                }
                if (!data.isNull("profile")) {
                    JSONObject profile = data.getJSONObject("profile");
                    if (!profile.isNull("id")) {
                        SharedPreferenceManager.getInstance().putString(Constant.ID, profile.getString("id"));
                        adminClintId = profile.getString("id");
                    }
                }
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
            super.onFailure(statusCode, headers, responseString, throwable);
            Log.e(TAG, "AdminCallback - onFailure: " + throwable.toString());

        }

    }

    private class BootstrapClientCallback extends JsonHttpResponseHandler {

        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            super.onSuccess(statusCode, headers, response);
            Log.d(TAG, "InitCallback-onSuccess: " + response.toString());
            try {
                String responseString = response.toString();
                buildCommentList(responseString);
                swipeView.setRefreshing(false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
            super.onFailure(statusCode, headers, responseString, throwable);
            Log.e(TAG, "InitCallback-onFailure: " + throwable.getLocalizedMessage());
        }
    }

    private class StreamCallBack extends AsyncHttpResponseHandler {

        @Override
        public void onSuccess(int i, Header[] headers, byte[] bytes) {
            String response = new String(bytes);
            Log.d(TAG, "StreamCallBack-onSuccess");
            if (response != null) {
                content.setStreamData(response);
            }
        }

        @Override
        public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
            Log.e(TAG, "StreamCallBack-onFailure: " + throwable.getLocalizedMessage());
        }
    }


    /**
     * To control (hide and show) create post and tool bar on scroll
     */
    private OnScrollListener onScrollListener = new OnScrollListener() {
        boolean hideToolBar = false;

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            if (hideToolBar) {
                postNewCommentIv.setVisibility(View.GONE);
                getSupportActionBar().hide();
            } else {
                postNewCommentIv.setVisibility(View.VISIBLE);
                getSupportActionBar().show();
            }
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            if (dy > 2) {
                hideToolBar = true;
            } else if (dy < -1) {
                hideToolBar = false;

            }
        }
    };
    private OnClickListener activityTitleListenerHide = new OnClickListener() {
        @Override
        public void onClick(View v) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                activityTitle.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                activityTitle.setSystemUiVisibility(View.STATUS_BAR_HIDDEN);
            }

            postNewCommentIv.setVisibility(View.GONE);


            activityTitle.setOnClickListener(activityTitleListenerShow);

        }
    };
    private OnClickListener activityTitleListenerShow = new OnClickListener() {
        @Override
        public void onClick(View v) {

            postNewCommentIv.setVisibility(View.VISIBLE);

            activityTitle.setOnClickListener(activityTitleListenerHide);

        }
    };

    private DialogInterface.OnClickListener tryAgain = new DialogInterface.OnClickListener() {

        @Override
        public void onClick(DialogInterface arg0, int arg1) {
            adminClintCall();
        }
    };
    public static final String TAG = CommentsActivity.class.getSimpleName();
    private Toolbar toolbar;
    private TextView activityTitle, loginTV, notifMsgTV;
    private RecyclerView commentsLV;
    private CommentsAdapter mCommentsAdapter;
    private ImageButton postNewCommentIv;
    private ArrayList<Content> commentsArray;
    private ContentHandler content;
    private SwipeRefreshLayout swipeView;
    private LinearLayout notification;
    private Bus mBus = LfManager.getInstance().getBus();
    private String adminClintId = "No";
    ArrayList<String> newComments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.comments_activity);

        pullViews();

        setListenersToViews();

        buildToolBar();

        bootstrapClientCall();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == AuthenticationActivity.AUTHENTICATION_REQUEST_CODE) {
                SharedPreferenceManager.getInstance().putString(AuthenticationActivity.TOKEN, data.getStringExtra(AuthenticationActivity.TOKEN));
                adminClintCall();
                loginTV.setText("Logout");
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.postNewCommentIv:
                String token = SharedPreferenceManager.getInstance().getString(AuthenticationActivity.TOKEN, "");
                if (token == null || token.length() == 0) {
                    showToast("Please login to post..");
                    AuthenticationActivity.start(this, Config.ENVIRONMENT, Config.NETWORK_ID, Config.ENCODED_URL, Config.NEXT);
                } else {
                    YoYo.with(Techniques.ZoomIn)
                            .duration(700)
                            .playOn(findViewById(R.id.notification));
                    Intent intent = new Intent(CommentsActivity.this, NewActivity.class);
                    intent.putExtra(Constant.PURPOSE, Constant.NEW_COMMENT);
                    startActivity(intent);
                }
                break;
            case R.id.notification:
                YoYo.with(Techniques.BounceInUp)
                        .duration(700)
                        .playOn(findViewById(R.id.notification));
                notification.setVisibility(View.GONE);
                for (int m = 0; m < newComments.size(); m++) {
                    int flag = 0;
                    String stateBeanId = newComments.get(m);
                    Content stateBean = ContentHandler.ContentMap.get(stateBeanId);
                    for (int i = 0; i < commentsArray.size(); i++) {
                        Content content = commentsArray.get(i);
                        if (content.getId().equals(stateBean.getParentId())) {
                            commentsArray.add(i + 1, stateBean);
                            mCommentsAdapter.notifyItemInserted(i + 1);
                            flag = 1;
                            break;
                        }
                    }
                    if (flag == 0) {
                        commentsArray.add(0, stateBean);
                        mCommentsAdapter.notifyItemInserted(0);
                    } else {
                    }
                    scrollToComment(stateBeanId);
                }
                newComments.clear();
                break;
            case R.id.login_TV:
                if (loginTV.getText().equals("Login")) {
                    AuthenticationActivity.start(this, Config.ENVIRONMENT, Config.NETWORK_ID, Config.ENCODED_URL, Config.NEXT);
                } else {
                    SharedPreferenceManager.getInstance().clear();
                    CookieManager.getInstance().removeAllCookie();
                    loginTV.setText("Login");
                }
                break;
        }
    }

    @Override
    public void loadImage(String imageURL) {
        if (imageURL.length() > 0)
            Picasso.with(getBaseContext()).load(imageURL);
    }

    @Override
    public void onDataUpdate(HashSet<String> authorsSet, HashSet<String> statesSet, HashSet<String> annotationsSet, HashSet<String> updates) {
        for (String stateBeanId : statesSet) {
            Content stateBean = ContentHandler.ContentMap.get(stateBeanId);
            if (stateBean.getVisibility().equals("1")) {

                if (isExistComment(stateBeanId)) continue;

                if (adminClintId.equals(stateBean.getAuthorId())) {
                    int flag = 0;
                    for (int i = 0; i < commentsArray.size(); i++) {
                        Content content = commentsArray.get(i);
                        if (content.getId().equals(stateBean.getParentId())) {
                            commentsArray.add(i + 1, stateBean);
                            mCommentsAdapter.notifyItemInserted(i + 1);
                            flag = 1;
                            break;
                        }
                    }
                    if (flag == 0) {
                        commentsArray.add(0, stateBean);
                        mCommentsAdapter.notifyItemInserted(0);
                    }
                } else {
                    newComments.add(0, stateBeanId);
                }
            } else {
                if (!content.hasVisibleChildContents(stateBeanId)) {
                    for (int i = 0; i < commentsArray.size(); i++) {
                        Content bean = commentsArray.get(i);
                        if (bean.getId().equals(stateBeanId)) {
                            commentsArray.remove(i);
                            mCommentsAdapter.notifyItemRemoved(i);
                            break;
                        }
                    }
                }
            }
        }
        if (updates.size() > 0) {
            mBus.post(updates);
            mCommentsAdapter.notifyDataSetChanged();
        }

        if (newComments != null)
            if (newComments.size() > 0) {
                if (newComments.size() == 1) {
                    notifMsgTV.setText(newComments.size() + " New Comment");

                } else {
                    notifMsgTV.setText(newComments.size() + " New Comments");
                }
                notification.setVisibility(View.VISIBLE);
                YoYo.with(Techniques.DropOut)
                        .duration(700)
                        .playOn(findViewById(R.id.notification));

            } else {
                notification.setVisibility(View.GONE);
            }
    }

    private void pullViews() {
        commentsLV = (RecyclerView) findViewById(R.id.commentsLV);
        commentsLV.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        postNewCommentIv = (ImageButton) findViewById(R.id.postNewCommentIv);
        notifMsgTV = (TextView) findViewById(R.id.notifMsgTV);
        notification = (LinearLayout) findViewById(R.id.notification);
        swipeView = (SwipeRefreshLayout) findViewById(R.id.swipe);

    }

    private void setListenersToViews() {
        postNewCommentIv.setOnClickListener(this);
        notification.setOnClickListener(this);
        commentsLV.setOnScrollListener(onScrollListener);

        swipeView.setColorSchemeColors(getResources().getColor(R.color.colorPrimary));
        swipeView.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeView.setRefreshing(true);
                mCommentsAdapter = null;
                commentsArray.clear();
                mCommentsAdapter = new CommentsAdapter(getApplication(), commentsArray);
                commentsLV.setAdapter(mCommentsAdapter);
                bootstrapClientCall();

                YoYo.with(Techniques.FadeIn).duration(700).playOn(findViewById(R.id.commentsLV));
            }
        });
    }

    private void buildToolBar() {

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        //toolbar
        setSupportActionBar(toolbar);
        //disable title on toolbar
        getSupportActionBar().setDisplayShowTitleEnabled(false);

//        ImageView homeIcon = (ImageView) findViewById(R.id.activityIcon);
//        homeIcon.setBackgroundResource(R.drawable.flame);

        activityTitle = (TextView) findViewById(R.id.title_TV);
        activityTitle.setOnClickListener(activityTitleListenerHide);

        loginTV = (TextView) findViewById(R.id.login_TV);
        loginTV.setOnClickListener(this);

        String token = SharedPreferenceManager.getInstance().getString(AuthenticationActivity.TOKEN, "");

        if (token == null || token.equals("")) {
            SharedPreferenceManager.getInstance().clear();
            AuthenticationActivity.logout();
            loginTV.setText("Login");
        } else {
            loginTV.setText("Logout");
        }

    }

    private void scrollToComment(String mCommentBeanId) {
        for (int i = 0; i < commentsArray.size(); i++) {
            Content mBean = commentsArray.get(i);
            if (mBean.getId().equals(mCommentBeanId)) {
                commentsLV.smoothScrollToPosition(i);
                break;
            }
        }
    }

    private void buildCommentList(String data) {
        try {
            content = new ContentHandler(new JSONObject(data), getBaseContext());
            content.getContentFromResponse(this);
            commentsArray = content.getDeletedObjects();
            mCommentsAdapter = new CommentsAdapter(this, commentsArray);
            commentsLV.setAdapter(mCommentsAdapter);
            streamClintCall();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        newComments = new ArrayList<>();
        swipeView.setEnabled(true);
        dismissProgressDialog();

    }

    private boolean isExistComment(String commentId) {
        for (Content bean : commentsArray) {
            if (bean.getId().equals(commentId))
                return true;
        }
        return false;
    }

//================< Calls >

    /**
     * Call to get user info
     */
    void adminClintCall() {
        if (!isNetworkAvailable()) {
            showAlert("No connection available", "TRY AGAIN", tryAgain);
            return;
        } else {
            showProgressDialog();
        }
        String token = SharedPreferenceManager.getInstance().getString(AuthenticationActivity.TOKEN, "");

        if (token == null || token.equals("")) {
            showToast("Not logged in");
            return;
        }

        try {
            AdminClient.authenticateUser(
                    token,
                    Config.COLLECTION_ID,
                    Config.ARTICLE_ID,
                    Config.SITE_ID,
                    new AdminCallback());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private void bootstrapClientCall() {
        try {
            BootstrapClient.getInit(Config.SITE_ID, Config.ARTICLE_ID, new BootstrapClientCallback());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private void streamClintCall() {
        try {
            StreamClient.pollStreamEndpoint(
                    Config.COLLECTION_ID,
                    ContentHandler.lastEvent,
                    new StreamCallBack());
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }
}
