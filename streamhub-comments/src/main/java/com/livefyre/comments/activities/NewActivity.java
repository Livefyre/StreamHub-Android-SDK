package com.livefyre.comments.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.livefyre.comments.Config;
import com.livefyre.comments.R;
import com.livefyre.comments.manager.SharedPreferenceManager;
import com.livefyre.comments.util.Constant;
import com.livefyre.comments.util.Util;
import com.livefyre.streamhub_android_sdk.activity.AuthenticationActivity;
import com.livefyre.streamhub_android_sdk.network.WriteClient;
import com.livefyre.streamhub_android_sdk.util.LFSActions;
import com.livefyre.streamhub_android_sdk.util.LFSConstants;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;

import cz.msebera.android.httpclient.Header;
import io.filepicker.Filepicker;
import io.filepicker.models.FPFile;

public class NewActivity extends BaseActivity {
    public static final String TAG = NewActivity.class.getSimpleName();


    Toolbar toolbar;
    TextView commentEt;
    LinearLayout attachImageLL;
    FrameLayout attacheImageFL;
    ImageView capturedImage;
    ProgressBar progressBar;
    RelativeLayout deleteCapturedImage;
    JSONObject imgObj;
    //id-selected comment Id Used for editing and new reply
    String purpose, id, body;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_activity);
        pullViews();

        getDataFromIntent();

        setListenersToViews();

        setListenersToViewsAndSetConfig();
    }

    private void getDataFromIntent() {
        Intent intent = getIntent();
        purpose = intent.getStringExtra(Constant.PURPOSE);

        if (purpose.equals(Constant.NEW_COMMENT)) {

        } else if (purpose.equals(Constant.NEW_REPLY)) {
            id = intent.getStringExtra(Constant.ID);
        } else if (purpose.equals(Constant.EDIT)) {
            id = intent.getStringExtra(Constant.ID);
            body = intent.getStringExtra(Constant.BODY);
            commentEt.setText(Util.trimTrailingWhitespace(Html
                            .fromHtml(body)),
                    TextView.BufferType.SPANNABLE);
        }
    }

    private void pullViews() {
        attachImageLL = (LinearLayout) findViewById(R.id.attachImageLL);
        attacheImageFL = (FrameLayout) findViewById(R.id.attacheImageFL);
        capturedImage = (ImageView) findViewById(R.id.capturedImage);
        deleteCapturedImage = (RelativeLayout) findViewById(R.id.deleteCapturedImage);
        commentEt = (TextView) findViewById(R.id.commentEt);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
    }

    private void setListenersToViews() {
        attachImageLL.setOnClickListener(attachImageLLListener);
        deleteCapturedImage.setOnClickListener(deleteCapturedImageListener);
    }

    private void setListenersToViewsAndSetConfig() {
        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowTitleEnabled(false);

        //Activity Name
        TextView activityName = (TextView) findViewById(R.id.activityTitle);

        if (purpose.equals(Constant.NEW_COMMENT)) {
            commentEt.setHint("Write your comment here...");//setting hint to Edittext
            activityName.setText("New Comment");
        } else if (purpose.equals(Constant.NEW_REPLY)) {
            commentEt.setHint("Write your Reply here...");//setting hint to Edittext
            activityName.setText("Reply");
            attachImageLL.setVisibility(View.VISIBLE);//Hide Image Selection option
        } else if (purpose.equals(Constant.EDIT)) {
            activityName.setText("Edit");
            attachImageLL.setVisibility(View.GONE);//Hide Image Selection option
        }

        //Activity Icon
        ImageView homeIcon = (ImageView) findViewById(R.id.activityIcon);
        homeIcon.setBackgroundResource(R.drawable.close_b);

        LinearLayout activityIconLL = (LinearLayout) findViewById(R.id.activityIconLL);
        activityIconLL.setOnClickListener(homeIconListener);

        //Action
        TextView actionTv = (TextView) findViewById(R.id.actionTv);
        actionTv.setText("POST");

        LinearLayout actionLL = (LinearLayout) findViewById(R.id.actionLL);
        actionLL.setVisibility(View.VISIBLE);
        actionLL.setOnClickListener(actionTvListener);

    }

    void postNewComment(String body) {
        showProgressDialog();
        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put(LFSConstants.LFSPostBodyKey, body);
        parameters.put(LFSConstants.LFSPostType,
                LFSConstants.LFSPostTypeComment);
        parameters.put(LFSConstants.LFSPostUserTokenKey, SharedPreferenceManager.getInstance().getString(AuthenticationActivity.TOKEN, ""));
        if (imgObj != null)
            parameters.put(LFSConstants.LFSPostAttachmentsKey,
                    (new JSONArray().put(imgObj)).toString());
        try {
            WriteClient.postContent(
                    Config.COLLECTION_ID, null, SharedPreferenceManager.getInstance().getString(AuthenticationActivity.TOKEN, ""),
                    parameters, new WriteClientCallback());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    void postNewReply(String body) {
        showProgressDialog();

        if (purpose.equals(Constant.NEW_REPLY)) {
            Log.d("REPLY", "IN NEW REPLY");
            HashMap<String, Object> parameters = new HashMap<>();
            parameters.put(LFSConstants.LFSPostBodyKey, body);
            parameters.put(LFSConstants.LFSPostType,
                    LFSConstants.LFSPostTypeReply);
            parameters.put(LFSConstants.LFSPostUserTokenKey,
                    SharedPreferenceManager.getInstance().getString(AuthenticationActivity.TOKEN, ""));
            if (imgObj != null)
                parameters.put(LFSConstants.LFSPostAttachmentsKey,
                        (new JSONArray().put(imgObj)).toString());
            try {
                WriteClient.postContent(
                        Config.COLLECTION_ID, id, SharedPreferenceManager.getInstance().getString(AuthenticationActivity.TOKEN, ""),
                        parameters, new newReplyCallback());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        } else if (purpose.equals(Constant.EDIT)) {
            Log.d("EDIT", "IN EDIT REPLY");
            RequestParams parameters = new RequestParams();
            parameters.put(LFSConstants.LFSPostBodyKey, body);
            parameters.put(LFSConstants.LFSPostUserTokenKey,
                    SharedPreferenceManager.getInstance().getString(AuthenticationActivity.TOKEN, ""));
            WriteClient.postAction(Config.COLLECTION_ID, id,
                    SharedPreferenceManager.getInstance().getString(AuthenticationActivity.TOKEN, ""), LFSActions.EDIT, parameters,
                    new editCallback());
        }
    }
    //Call backs

    //New Comment
    public class WriteClientCallback extends JsonHttpResponseHandler {
        @Override
        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
            super.onFailure(statusCode, headers, throwable, errorResponse);
            dismissProgressDialog();
            showToast("Error posting comment!");
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
            super.onFailure(statusCode, headers, throwable, errorResponse);
            dismissProgressDialog();
            showToast("Error posting comment!");
        }

        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            super.onSuccess(statusCode, headers, response);
            dismissProgressDialog();
            showAlert("Comment Posted Successfully.", "OK", null);
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
            super.onFailure(statusCode, headers, responseString, throwable);
            dismissProgressDialog();
            if (null != responseString)
                Log.d("data error", "" + responseString);
            try {
                JSONObject errorJson = new JSONObject(responseString);
                if (!errorJson.isNull("msg")) {

                    showAlert(errorJson.getString("msg"), "TRY AGAIN", tryAgain);
                } else {
                    showAlert("Something went wrong.", "TRY AGAIN", tryAgain);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                showAlert("Something went wrong.", "TRY AGAIN", tryAgain);
            }
        }
    }

    //New Reply
    public class newReplyCallback extends JsonHttpResponseHandler {

        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            super.onSuccess(statusCode, headers, response);
            dismissProgressDialog();
            showAlert("Reply Posted Successfully.", "OK", null);
            Intent returnIntent = new Intent();
            returnIntent.putExtra("result", 1);
            setResult(RESULT_OK, returnIntent);
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
            super.onFailure(statusCode, headers, responseString, throwable);
            dismissProgressDialog();
            try {
                JSONObject errorJson = new JSONObject(responseString);
                if (!errorJson.isNull("msg")) {
                    showAlert(errorJson.getString("msg"), "TRY AGAIN", tryAgain);
                } else {
                    showAlert("Something went wrong.", "TRY AGAIN", tryAgain);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                showAlert("Something went wrong.", "TRY AGAIN", tryAgain);

            }
        }
    }

    // Edit Comment
    private class editCallback extends JsonHttpResponseHandler {

        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            super.onSuccess(statusCode, headers, response);
            dismissProgressDialog();
            showAlert("Reply Edited Successfully.", "OK", null);
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
            super.onFailure(statusCode, headers, responseString, throwable);
            dismissProgressDialog();
            try {
                JSONObject errorJson = new JSONObject(responseString);
                if (!errorJson.isNull("msg")) {
                    showAlert(errorJson.getString("msg"), "TRY AGAIN", tryAgain);
                } else {
                    showAlert("Something went wrong.", "TRY AGAIN", tryAgain);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                showAlert("Something went wrong.", "TRY AGAIN", tryAgain);
            }
        }
    }

    // Listeners
    View.OnClickListener actionTvListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            if (!isNetworkAvailable()) {
                showAlert("No connection available", "TRY AGAIN", tryAgain);
                return;
            }
            String description = commentEt.getText().toString();
            if (description.length() == 0) {
                showAlert("Please enter text to post.", "TRY AGAIN", tryAgain);
                return;
            }
            if (purpose.equals(Constant.NEW_COMMENT)) {
                String descriptionHTML = Html.toHtml((android.text.Spanned) commentEt.getText());
                postNewComment(descriptionHTML);
            } else {
                String htmlReplyText = Html.toHtml((android.text.Spanned) commentEt.getText());
                postNewReply(htmlReplyText);
            }
        }
    };

    View.OnClickListener homeIconListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            finish();
        }
    };

    View.OnClickListener deleteCapturedImageListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            attachImageLL.setVisibility(View.VISIBLE);
            attacheImageFL.setVisibility(View.GONE);
        }
    };

    View.OnClickListener attachImageLLListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (Config.FILEPICKER_API_KEY.length() == 0) {
                showToast("Something went wrong.");
            } else {
                Intent intent = new Intent(NewActivity.this, Filepicker.class);
                Filepicker.setKey(Config.FILEPICKER_API_KEY);
                startActivityForResult(intent, Filepicker.REQUEST_CODE_GETFILE);
            }
        }
    };

    // Dialog Listeners
    DialogInterface.OnClickListener selectImageDialogAction = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface arg0, int arg1) {
            Intent intent = new Intent(NewActivity.this, Filepicker.class);
            Filepicker.setKey(Config.FILEPICKER_API_KEY);
            startActivityForResult(intent, Filepicker.REQUEST_CODE_GETFILE);
        }
    };

    DialogInterface.OnClickListener tryAgain = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface arg0, int arg1) {

        }
    };

    //On Image Selected
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Filepicker.REQUEST_CODE_GETFILE) {
            if (resultCode != RESULT_OK) {
                // Result was cancelled by the user or there was an error
                showAlert("No Image Selected.", "SELECT IMAGE", selectImageDialogAction);
                attachImageLL.setVisibility(View.VISIBLE);
                attacheImageFL.setVisibility(View.GONE);
                return;
            }
            attachImageLL.setVisibility(View.GONE);
            attacheImageFL.setVisibility(View.VISIBLE);
            ArrayList<FPFile> fpFiles = data.getParcelableArrayListExtra(Filepicker.FPFILES_EXTRA);
            String imgUrl = fpFiles.get(0).getUrl();
            Log.d("url", imgUrl + "");
            try {
                imgObj = new JSONObject();
                imgObj.put("link", imgUrl);
                imgObj.put("provider_name", "LivefyreFilePicker");
                imgObj.put("thumbnail_url", imgUrl);
                imgObj.put("type", "photo");
                imgObj.put("url", imgUrl);
                try {
                    progressBar.setVisibility(View.VISIBLE);
                    Picasso.with(getBaseContext()).load(imgUrl).fit().into(capturedImage, new ImageLoadCallBack());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private class ImageLoadCallBack implements Callback {
        @Override
        public void onSuccess() {
            //Hide
            progressBar.setVisibility(View.GONE);
        }

        @Override
        public void onError() {
            //Hide
            progressBar.setVisibility(View.GONE);
        }
    }
}
