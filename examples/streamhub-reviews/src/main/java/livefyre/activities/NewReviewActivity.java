package livefyre.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.livefyre.streamhub_android_sdk.network.WriteClient;
import com.livefyre.streamhub_android_sdk.util.LFSConstants;
import com.loopj.android.http.JsonHttpResponseHandler;
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
import livefyre.BaseActivity;
import livefyre.LFSConfig;
import livefyre.R;

/**
 * Created by Adobe Systems Incorporated on 16/06/15.
 */

public class NewReviewActivity extends BaseActivity {

    private EditText newReviewTitleEt, newReviewProsEt, newReviewConsEt, newReviewBodyEt;
    private TextView activityTitle, actionTv;
    private ImageView capturedImage;
    private RelativeLayout deleteCapturedImage;
    private RatingBar newReviewRatingBar;
    private ProgressBar progressBar;
    private LinearLayout addPhotoLL;
    volatile Toolbar toolbar;
    private JSONObject imgObj;
    volatile String imgUrl;


    private void buildToolBar() {
        toolbar = (Toolbar) findViewById(R.id.app_bar);
        //disable title on toolbar
//        getSupportActionBar().setDisplayShowTitleEnabled(false);
        ImageView homeIcon = (ImageView) findViewById(R.id.activityIcon);
        activityTitle = (TextView) findViewById(R.id.activityTitle);
        actionTv = (TextView) findViewById(R.id.actionTv);
        homeIcon.setBackgroundResource(R.mipmap.livefyreflame);
        activityTitle.setText("New Review");
        actionTv.setText("Post");
    }

    private void pullViews() {
        newReviewTitleEt = (EditText) findViewById(R.id.newReviewTitleEt);
        newReviewProsEt = (EditText) findViewById(R.id.newReviewProsEt);
        newReviewConsEt = (EditText) findViewById(R.id.newReviewConsEt);
        newReviewBodyEt = (EditText) findViewById(R.id.newReviewBodyEt);
        newReviewRatingBar = (RatingBar) findViewById(R.id.newReviewRatingBar);
        capturedImage = (ImageView) findViewById(R.id.capturedImage);
        deleteCapturedImage = (RelativeLayout) findViewById(R.id.deleteCapturedImage);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        addPhotoLL = (LinearLayout) findViewById(R.id.addPhotoLL);
    }

    private void setListenersToViews() {
        addPhotoLL.setOnClickListener(captureImageListener);
        deleteCapturedImage.setOnClickListener(deleteCapturedImageListener);
        actionTv.setOnClickListener(postReviewListener);
    }

    private View.OnClickListener postReviewListener = new View.OnClickListener() {

        public void onClick(View v) {
            String title = newReviewTitleEt.getText().toString();
            String description = newReviewBodyEt.getText().toString();
            String pros = newReviewProsEt.getText().toString();
            String cons = newReviewConsEt.getText().toString();
            int reviewRating = (int) (newReviewRatingBar.getRating() * 20);
            if (title.length() == 0) {
                ((EditText) findViewById(R.id.newReviewTitleEt)).setError("Enter Title");
                return;
            }
            if (reviewRating == 0) {
                showAlert("Please give Rating.", "ok", tryAgain);
                return;
            }
            if (pros.length() == 0) {
                ((EditText) findViewById(R.id.newReviewProsEt)).setError("Enter Pros");
                return;
            }
            if (cons.length() == 0) {
                ((EditText) findViewById(R.id.newReviewConsEt)).setError("Enter Cons");
                return;
            }
            if (description.length() == 0) {
                ((EditText) findViewById(R.id.newReviewProsEt)).setError("Enter Description");
                return;
            }
            String descriptionHTML = Html.toHtml(newReviewBodyEt.getText());
            if (pros.length() > 0 || cons.length() > 0) {
                descriptionHTML = "<p><b>Pro</b><p>"
                        + Html.toHtml(newReviewProsEt.getText()) + "</p></p>"
                        + "<p><b>Cons</b><p>"
                        + Html.toHtml(newReviewConsEt.getText()) + "</p></p>"
                        + " <p><b>Description</b><p>" + descriptionHTML
                        + "</p></p>";
            }
            postNewReview(newReviewTitleEt.getText().toString(),
                    descriptionHTML, reviewRating);
        }
    };
    private DialogInterface.OnClickListener tryAgain = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface arg0, int arg1) {
        }
    };

    private void postNewReview(String title, String body, int reviewRating) {
        if (!isNetworkAvailable()) {
            showToast("Network Not Available");
            return;
        }
        showProgressDialog();
        HashMap<String, Object> parameters = new HashMap();
        parameters.put(LFSConstants.LFSPostBodyKey, body);
        parameters.put(LFSConstants.LFSPostTitleKey, title);
        parameters.put(LFSConstants.LFSPostTypeReview, reviewRating);
        parameters
                .put(LFSConstants.LFSPostType, LFSConstants.LFSPostTypeReview);
        parameters.put(LFSConstants.LFSPostUserTokenKey, LFSConfig.USER_TOKEN);
        if (imgObj != null)
            parameters.put(LFSConstants.LFSPostAttachment,
                    (new JSONArray().put(imgObj)).toString());
        try {
            WriteClient.postContent(
                    LFSConfig.COLLECTION_ID, null, LFSConfig.USER_TOKEN,
                    parameters, new writeclientCallback());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    private class writeclientCallback extends JsonHttpResponseHandler {
        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            super.onSuccess(statusCode, headers, response);
            dismissProgressDialog();
            showAlert("Review Posted Successfully.", "OK", null);
        }

        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
            super.onSuccess(statusCode, headers, response);
            dismissProgressDialog();
            showAlert("Review Posted Successfully.", "OK", null);
        }

        @Override
        public void onSuccess(int statusCode, Header[] headers, String responseString) {
            super.onSuccess(statusCode, headers, responseString);
            dismissProgressDialog();
            showAlert("Review Posted Successfully.", "OK", null);
        }


        @Override
        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
            super.onFailure(statusCode, headers, throwable, errorResponse);
            dismissProgressDialog();
            Log.d("data error", "" + errorResponse);
            try {
                if (!errorResponse.isNull("msg")) {
                    showAlert(errorResponse.getString("msg"), "OK", null);
                } else {
                    showAlert("Something went wrong.", "OK", null);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                showAlert("Something went wrong.", "OK", null);
            }
        }
    }

    private View.OnClickListener captureImageListener = new View.OnClickListener() {

        public void onClick(View v) {
            Intent intent = new Intent(NewReviewActivity.this, Filepicker.class);
            Filepicker.setKey(LFSConfig.FILEPICKER_API_KEY);
            startActivityForResult(intent, Filepicker.REQUEST_CODE_GETFILE);
        }
    };

    private View.OnClickListener deleteCapturedImageListener = new View.OnClickListener() {

        public void onClick(View v) {
            addPhotoLL.setVisibility(View.VISIBLE);
            capturedImage.setVisibility(View.GONE);
            deleteCapturedImage.setVisibility(View.GONE);
            imgUrl = "";
            imgObj = null;
        }
    };

    // Dialog Listeners
    private DialogInterface.OnClickListener selectImageDialogAction = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface arg0, int arg1) {
            Intent intent = new Intent(NewReviewActivity.this, Filepicker.class);
            Filepicker.setKey(LFSConfig.FILEPICKER_API_KEY);
            startActivityForResult(intent, Filepicker.REQUEST_CODE_GETFILE);
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Filepicker.REQUEST_CODE_GETFILE) {
            if (resultCode != RESULT_OK) {
                showAlert("No Image Selected.", "SELECT IMAGE", selectImageDialogAction);
                addPhotoLL.setVisibility(View.VISIBLE);
                capturedImage.setVisibility(View.GONE);
                deleteCapturedImage.setVisibility(View.GONE);
                return;
            }
            addPhotoLL.setVisibility(View.GONE);
            capturedImage.setVisibility(View.VISIBLE);
            deleteCapturedImage.setVisibility(View.VISIBLE);
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
            progressBar.setVisibility(View.GONE);
        }

        @Override
        public void onError() {
            //Hide
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_review);
        pullViews();
        buildToolBar();
        setListenersToViews();
    }

}
