package com.finder.fooedbar.client;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;

import com.finder.fooedbar.R;

/**
 * Created by jasonlin on 5/20/16.
 */
public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        new CountDownTimer(3000, 1000) {

            public void onTick(long millisUntilFinished) {
                //nothing
            }

            public void onFinish() {
                Intent i = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(i);
                finish();
            }
        }.start();
    }
}
