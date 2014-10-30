package com.example.livefyre_stream_example;

import android.app.Activity;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.livefyre.android.core.BootstrapClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            BootstrapClient.getInit("labs.fyre.co", "320568", "custom-1379372287037", new InitCallback());

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    public void addTextView(JSONObject content) throws JSONException {
        ViewGroup root = (ViewGroup) findViewById(R.id.MainLayout);
        ViewGroup layout = (ViewGroup) getLayoutInflater().inflate(R.layout.comment_layout, root, false);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.bottomMargin = 25;
        root.addView(layout, params);

        TextView bodyView = (TextView) layout.findViewById(R.id.textViewBody);
        bodyView.setText(Html.fromHtml(content.getString("bodyHtml")).toString());

        Calendar commentTime = Calendar.getInstance();
        commentTime.setTimeInMillis(content.getLong("createdAt") * 1000);
        Calendar now = Calendar.getInstance();

        boolean sameDay = commentTime.get(Calendar.YEAR) == now.get(Calendar.YEAR) &&
                commentTime.get(Calendar.DAY_OF_YEAR) == now.get(Calendar.DAY_OF_YEAR);

        SimpleDateFormat sdf;

        if (sameDay) {
            sdf = new SimpleDateFormat("HH:mm");
        } else {
            sdf = new SimpleDateFormat("MMM dd");
        }

        TextView dateView = (TextView) layout.findViewById(R.id.textViewDate);
        dateView.setText(sdf.format(commentTime.getTime()));

        new DownloadImageTask((ImageView) layout.findViewById(R.id.imageView))
                .execute(content.getJSONObject("author").getString("avatar"));
    }

    private class InitCallback extends JsonHttpResponseHandler {
        public void onSuccess(JSONObject data) {
            try {
                JSONObject authors = data.getJSONObject("headDocument").getJSONObject("authors");

                JSONArray contentArr = data.getJSONObject("headDocument").getJSONArray("content");
                for (int idx = 0; idx < contentArr.length(); idx++) {
                    JSONObject stateObj = contentArr.getJSONObject(idx);
                    if (stateObj.getInt("type") != 0) {
                        continue;
                    }
                    if (stateObj.getInt("vis") != 1) {
                        continue;
                    }
                    JSONObject contentObj = stateObj.getJSONObject("content");
                    contentObj.put("author", authors.getJSONObject(contentObj.getString("authorId")));
                    addTextView(contentObj);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}