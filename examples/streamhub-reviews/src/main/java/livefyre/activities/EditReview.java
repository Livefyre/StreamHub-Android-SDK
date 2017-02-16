package livefyre.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.livefyre.streamhub_android_sdk.network.WriteClient;
import com.livefyre.streamhub_android_sdk.util.LFSActions;
import com.livefyre.streamhub_android_sdk.util.LFSConstants;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import livefyre.AppSingleton;
import livefyre.BaseActivity;
import livefyre.LFSAppConstants;
import livefyre.LFSConfig;
import livefyre.LFUtils;
import livefyre.LivefyreApplication;
import livefyre.R;
import livefyre.models.Content;
import livefyre.parsers.ContentParser;

public class EditReview extends BaseActivity {

    EditText editReviewTitleEt, editReviewBodyEt;
    RatingBar editReviewRatingBar;
    TextView editReviewTitleTv, editReviewBodyTv, activityTitle, actionTv;
    String id, title, body;
    Content selectedReview;
    int rating;
    ImageView activityIcon;
    private LivefyreApplication application;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit);

        pullViews();
        buildToolbar();
        setListenersToViews();
        getDataFromIntent();
        setData();

        editReviewTitleEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

                if (editReviewTitleEt.getText().toString().length() > 0) {
                    editReviewTitleTv.setVisibility(View.VISIBLE);
                    editReviewTitleEt.setHintTextColor(Color
                            .parseColor("#ffffff"));
                } else {
                    editReviewTitleTv.setVisibility(View.INVISIBLE);
                    editReviewTitleEt.setHintTextColor(Color
                            .parseColor("#cdcdcd"));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

                if (editReviewTitleEt.getText().toString().length() > 0) {
                    editReviewTitleTv.setVisibility(View.VISIBLE);
                    editReviewTitleEt.setHintTextColor(Color
                            .parseColor("#ffffff"));
                } else {
                    editReviewTitleTv.setVisibility(View.INVISIBLE);
                    editReviewTitleEt.setHintTextColor(Color
                            .parseColor("#cdcdcd"));
                }
            }
        });

        editReviewBodyEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                if (editReviewBodyEt.getText().toString().length() > 0) {
                    editReviewBodyTv.setVisibility(View.VISIBLE);
                    editReviewBodyEt.setHintTextColor(Color
                            .parseColor("#ffffff"));
                } else {
                    editReviewBodyTv.setVisibility(View.INVISIBLE);
                    editReviewBodyEt.setHintTextColor(Color
                            .parseColor("#cdcdcd"));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

                if (editReviewBodyEt.getText().toString().length() > 0) {
                    editReviewBodyTv.setVisibility(View.VISIBLE);
                    editReviewBodyEt.setHintTextColor(Color
                            .parseColor("#ffffff"));
                } else {
                    editReviewBodyTv.setVisibility(View.INVISIBLE);
                    editReviewBodyEt.setHintTextColor(Color
                            .parseColor("#cdcdcd"));
                }
            }
        });
    }

    private void setListenersToViews() {
        activityIcon.setOnClickListener(backtoReviewInDetailActivityListener);
        actionTv.setOnClickListener(editPostReplyListener);
        editReviewTitleEt.setOnClickListener(editReviewTitleListener);
    }

    private void buildToolbar() {
        activityIcon = (ImageView) findViewById(R.id.activityIcon);
        activityIcon.setBackgroundResource(R.mipmap.back_arrow);
        activityTitle.setText("Edit Review");
        actionTv.setText("Post");
    }


    private void pullViews() {

        application = AppSingleton.getInstance().getApplication();

        actionTv = (TextView) findViewById(R.id.actionTv);
        activityTitle = (TextView) findViewById(R.id.activityTitle);

        editReviewTitleTv = (TextView) findViewById(R.id.editReviewTitleTv);
        editReviewBodyTv = (TextView) findViewById(R.id.editReviewBodyTv);
        editReviewTitleEt = (EditText) findViewById(R.id.editReviewTitleEt);
        editReviewBodyEt = (EditText) findViewById(R.id.editReviewBodyEt);
        editReviewRatingBar = (RatingBar) findViewById(R.id.editReviewRatingBar);

    }

    void getDataFromIntent() {
        Intent fromInDetailAdapter = getIntent();
        id = fromInDetailAdapter.getStringExtra("id");
    }

    void setData() {
        selectedReview = ContentParser.ContentMap.get(id);
        editReviewTitleEt.setText(selectedReview.getTitle());
        editReviewBodyEt.setText(LFUtils.trimTrailingWhitespace(Html
                        .fromHtml(selectedReview.getBodyHtml())),
                TextView.BufferType.SPANNABLE);
        editReviewRatingBar.setRating(Float.parseFloat(selectedReview
                .getRating()) / 20);
        editReviewTitleTv.setVisibility(View.VISIBLE);
        editReviewBodyTv.setVisibility(View.VISIBLE);
        if (selectedReview.getAuthorId().equals(
                application.getDataFromSharedPreferences(LFSAppConstants.ID))) {
            editReviewRatingBar.setIsIndicator(false);
        } else {
            editReviewRatingBar.setIsIndicator(true);
        }
    }

    OnClickListener editReviewTitleListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            editReviewTitleEt.setCursorVisible(true);
        }
    };

    OnClickListener editPostReplyListener = new OnClickListener() {

        public void onClick(View v) {
            if (!isNetworkAvailable()) {
                showToast("Network Not Available");
                return;
            }
            showProgressDialog();
            title = editReviewTitleEt.getText().toString();
            body = editReviewBodyEt.getText().toString();
            rating = (int) (editReviewRatingBar.getRating() * 20);

            if (title.length() == 0) {
                showAlert("Please enter title before post.",
                        "ok", tryAgain);
                return;
            }
            if (body.length() == 0) {
                showAlert("Please enter description before post.",
                        "ok", tryAgain);
                return;
            }
            if (rating == 0) {
                showAlert("Please give rating before post.",
                        "ok", tryAgain);
                return;
            }
            String htmlBody = Html.toHtml(editReviewBodyEt.getText());
            RequestParams parameters = new RequestParams();
            parameters.put(LFSConstants.LFSPostBodyKey, htmlBody);
            parameters.put(LFSConstants.LFSPostTitleKey, editReviewTitleEt
                    .getText().toString());
            parameters.put(LFSConstants.LFSPostType,
                    LFSConstants.LFSPostTypeReview);
            JSONObject ratingJson = new JSONObject();
            try {
                ratingJson.put("default", rating + "");
                parameters.put(LFSConstants.LFSPostRatingKey,
                        ratingJson.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            parameters.put(LFSConstants.LFSPostUserTokenKey,
                    LFSConfig.USER_TOKEN);
            WriteClient.postAction(LFSConfig.COLLECTION_ID, id,
                    LFSConfig.USER_TOKEN, LFSActions.EDIT, parameters,
                    new editCallback());
        }
    };

    DialogInterface.OnClickListener tryAgain = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface arg0, int arg1) {
        }
    };

    OnClickListener backtoReviewInDetailActivityListener = new OnClickListener() {

        public void onClick(View v) {
            Intent returnIntent = new Intent();
            setResult(RESULT_CANCELED, returnIntent);
            finish();
        }
    };

    private class editCallback extends JsonHttpResponseHandler {
        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            super.onSuccess(statusCode, headers, response);
            Log.d("Log", "" + response);
            dismissProgressDialog();
            showAlert("Review Edited Successfully.", "OK", null);
        }

        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
            super.onSuccess(statusCode, headers, response);
            Log.d("Log", "" + response);
            dismissProgressDialog();
            showAlert("Review Edited Successfully.", "OK", null);
        }

        @Override
        public void onSuccess(int statusCode, Header[] headers, String responseString) {
            super.onSuccess(statusCode, headers, responseString);
            Log.d("Log", "" + responseString);
            dismissProgressDialog();
            showAlert("Review Edited Successfully.", "OK", null);
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
            super.onFailure(statusCode, headers, responseString, throwable);
            dismissProgressDialog();
            showAlert("Something went wrong.", "TRY AGAIN", tryAgain);
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
            super.onFailure(statusCode, headers, throwable, errorResponse);
            dismissProgressDialog();
            showAlert("Something went wrong.", "TRY AGAIN", tryAgain);
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
            super.onFailure(statusCode, headers, throwable, errorResponse);
            dismissProgressDialog();
            showAlert("Something went wrong.", "TRY AGAIN", tryAgain);
        }
    }
}
