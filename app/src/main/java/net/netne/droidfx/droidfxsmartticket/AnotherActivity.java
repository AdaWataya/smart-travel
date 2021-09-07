package net.netne.droidfx.droidfxsmartticket;

import android.app.Activity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;

public class AnotherActivity extends Activity {
    TextView error;
    @Override
    protected void onCreate(Bundle savedIntanceState)
    {
        super.onCreate(savedIntanceState);
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandle(this));
        setContentView(R.layout.exceptionhandler);
        error = (TextView) findViewById(R.id.error);
        error.setText(getIntent().getStringExtra("error"));
        error.setMovementMethod(new ScrollingMovementMethod());
    }

}
