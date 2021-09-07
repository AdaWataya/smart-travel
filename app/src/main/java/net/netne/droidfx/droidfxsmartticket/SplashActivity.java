package net.netne.droidfx.droidfxsmartticket;

/**
 * Created by R$ on 9/9/2016.
 */

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;

import java.util.concurrent.TimeUnit;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SystemClock.sleep(TimeUnit.SECONDS.toMillis(3));
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        finish();
    }
}

