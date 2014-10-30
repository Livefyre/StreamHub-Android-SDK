package com.livefyre.android.examples.postcomment;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.livefyre.android.core.WriteClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;

import java.net.MalformedURLException;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText editText = (EditText) findViewById(R.id.editText);
                try {
                    WriteClient.postContent("labs.fyre.co", "47466506", null, "eyJhbGciOiAiSFMyNTYiLCAidHlwIjogIkpXVCJ9.eyJkb21haW4iOiAibGFicy5meXJlLmNvIiwgImV4cGlyZXMiOiAxNDEwOTkzNzg1LjMyNzgwNCwgInVzZXJfaWQiOiAic3lzdGVtIn0.7ErE6F0l87f8pAIshjQyZcoxwDuGACotQdaby0qt17A",
                            editText.getText().toString(), new PostCommentCallback());
                } catch (MalformedURLException e) {

                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    private class PostCommentCallback extends JsonHttpResponseHandler {
        public void onSuccess(JSONObject data) {
            TextView textView = (TextView) findViewById(R.id.textView);
            textView.setText(data.toString());
        }
    }
}