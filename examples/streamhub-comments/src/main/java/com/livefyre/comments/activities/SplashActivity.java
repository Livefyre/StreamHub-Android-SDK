package com.livefyre.comments.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.livefyre.comments.R;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_activity);
        new Handler().postDelayed(new Runnable() {
            public void run() {

                Intent i = new Intent(SplashActivity.this, CommentsActivity.class);
                startActivity(i);
                finish();
            }
        }, 3000);
    }
}
