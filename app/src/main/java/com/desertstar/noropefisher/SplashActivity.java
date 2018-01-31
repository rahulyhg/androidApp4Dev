package com.desertstar.noropefisher;

/**
 * Created by Iker Redondo on 1/24/2018.
 */
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Start home activity
        SystemClock.sleep(1000);
        startActivity(new Intent(SplashActivity.this, MainActivity.class));

        // close splash activity
        finish();
    }
}