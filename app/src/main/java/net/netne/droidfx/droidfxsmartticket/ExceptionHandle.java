package net.netne.droidfx.droidfxsmartticket;

import android.app.Activity;
import android.content.Intent;

import java.io.StringWriter;
import java.io.PrintWriter;

public class ExceptionHandle implements java.lang.Thread.UncaughtExceptionHandler {
    private final Activity myContext;
    private  final String LINE_SEPARATOR = "\n";

    public ExceptionHandle(Activity context)
    {
        myContext = context;
    }
    public void uncaughtException(Thread thread, Throwable exception)
    {
    }
}
