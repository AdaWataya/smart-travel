package net.netne.droidfx.droidfxsmartticket;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;

public class startPage extends Activity {
    //a broadcast to know weather the data is synced or not
    public static final String DATA_SAVED_BROADCAST = "net.simplifiedcoding.datasaved";

    //Broadcast receiver to know the sync status
    private BroadcastReceiver broadcastReceiver;
    private Handler mHandler = new Handler();
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_page);
        ImageView SwitchOn = (ImageView)findViewById(R.id.imageView3);

        registerReceiver(new NetworkStateChecker(), new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        //calling the method to load all the stored names
        startRepeating();
        // loadNames();

        //the broadcast receiver to update sync status
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                //loading the names again
                //  loadNames();
            }
        };

        //registering the broadcast receiver to update sync status
        registerReceiver(broadcastReceiver, new IntentFilter(DATA_SAVED_BROADCAST));



        SwitchOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(startPage.this, SignOn.class);
                startActivity(intent);
                finish();
            }
        });

    }

    public void startRepeating()
    {
        // mHandler.postDelayed(runnable,5000);
        runnable.run();
    }
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            registerReceiver(new NetworkStateChecker(), new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
            //calling the method to load all the stored names
            //  loadNames();

            //the broadcast receiver to update sync status
            broadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {

                    //loading the names again
                    // loadNames();
                }
            };

            //registering the broadcast receiver to update sync status
            registerReceiver(broadcastReceiver, new IntentFilter(DATA_SAVED_BROADCAST));


            // CountTable();
            mHandler.postDelayed(this,5000);
        }
    };
}
