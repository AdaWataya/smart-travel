package net.netne.droidfx.droidfxsmartticket;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import com.karumi.dexter.BuildConfig;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;


import net.netne.droidfx.droidfxsmartticket.ApiServices.ApiService;
import net.netne.droidfx.droidfxsmartticket.ApiServices.ApiService3;
import net.netne.droidfx.droidfxsmartticket.clients.ApiClient;
import net.netne.droidfx.droidfxsmartticket.models.InsertDataResponseModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by R$ on 8/12/2018.
 */

public class HomeActivity extends Activity{

    ScrollView layout;
    public static final String DATA_SAVED_BROADCAST = "net.simplifiedcoding.datasaved";

    //Broadcast receiver to know the sync status



    NfcAdapter nfcAdapter;
    PendingIntent pendingIntent;
    TextView Tv;
    String Name;
    String BusNumber;
    String DISTANCE;
    String RouteNumber;
    EditText Bus;
    EditText Route;
    String closed;
    String Long=null;
    String Lat=null;
    ArrayList<String> loginLocation = new ArrayList<>();




    IntentFilter writeTagFilters[];
    boolean writeMode;
    Tag myTag;
    private static final String TAG = HomeActivity.class.getSimpleName();
    private static final int REQUEST_CHECK_SETTINGS = 100;


    // bunch of location related apis
    private FusedLocationProviderClient mFusedLocationClient;
    private SettingsClient mSettingsClient;
    private LocationRequest mLocationRequest;
    private LocationSettingsRequest mLocationSettingsRequest;
    private LocationCallback mLocationCallback;
    private Location mCurrentLocation;
    private String mLastUpdateTime;

    // boolean flag to toggle the ui
    private Boolean mRequestingLocationUpdates;

    private OdometerService odometer;
    private boolean bound = false;

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            OdometerService.OdomterBinder odomterBinder = (OdometerService.OdomterBinder) service;
            odometer = odomterBinder.getOdometer();
            bound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

            bound = false;
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity2);

       // layout=(ScrollView) findViewById(R.id.bg);
        Tv=(TextView) findViewById(R.id.tv);
        Bus=(EditText) findViewById(R.id.busnumber);
        Route =(EditText) findViewById(R.id.routenumber);
        //Button whatsapp = (Button)findViewById(R.id.whatsapp);
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);

        Name = getIntent().getStringExtra("name");
        Tv.setText("Driver -> "+Name);
        init();


        loadSavedPreferences();
        //RouteNumber = routeNumber.getText().toString();
        // restore the values from saved instance state
        restoreValuesFromBundle(savedInstanceState);

        startLocationButtonClick();
        startLocationButtonClick();
        registerReceiver(new NetworkStateChecker(), new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        //calling the method to load all the stored names




        setup();

        /* WappMe();
        whatsapp.setVisibility(View.INVISIBLE);
        whatsapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WappMe();
            }
        });

*/

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (nfcAdapter == null) {
            // Stop here, we definitely need NFC
            Toast.makeText(this, "This device doesn't support NFC.", Toast.LENGTH_LONG).show();
            finish();
        }
        readFromIntent(getIntent());

        new Timer().schedule(new TimerTask(){@Override public void run()
        {
            startLoginLocationButtonClick();
        }},500);






        pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        tagDetected.addCategory(Intent.CATEGORY_DEFAULT);
        writeTagFilters = new IntentFilter[] { tagDetected };
        watchMileage();

    }
    public  void setup() {
        if (Bus.getText().toString().isEmpty()) {
            Bus.setFocusable(true);
            Bus.setClickable(true);

        }
        else
        {
            Bus.setFocusable(false);
            Bus.setClickable(false);

            Bus.getText().toString().isEmpty(); //trim().equals("Null");
            {
                Toast.makeText(getApplicationContext(), "Trip# is empty", Toast.LENGTH_SHORT).show(); //Make required field
            }
           // else

        }
    }
    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(this, OdometerService.class);
        bindService(intent,connection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(bound){
            unbindService(connection);
            bound = false;
        }
    }

    private void watchMileage() {

        final Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                double distance = 0.0;
                if(odometer != null){
                    distance = odometer.getMiles();
                }
                String distanceStr = String.format("%1$,.2f km", distance);
                DISTANCE = distanceStr;
                handler.postDelayed(this, 1000);
            }
        });
    }
    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        readFromIntent(intent);
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())){
            myTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        }
    }
    private void readFromIntent(Intent intent) {
        String action = intent.getAction();
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
            Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            NdefMessage[] msgs = null;
            if (rawMsgs != null) {
                msgs = new NdefMessage[rawMsgs.length];
                for (int i = 0; i < rawMsgs.length; i++) {
                    msgs[i] = (NdefMessage) rawMsgs[i];
                }
            }
            buildTagViews(msgs);
        }
    }

    private void buildTagViews(NdefMessage[] msgs) {
        if (msgs == null || msgs.length == 0) return;

        String text = "";
//        String tagId = new String(msgs[0].getRecords()[0].getType());
        byte[] payload = msgs[0].getRecords()[0].getPayload();
        String textEncoding = ((payload[0] & 128) == 0) ? "UTF-8" : "UTF-16"; // Get the Text Encoding
        int languageCodeLength = payload[0] & 0063; // Get the Language Code, e.g. "en"
        // String languageCode = new String(payload, 1, languageCodeLength, "US-ASCII");

        try {
            // Get the Text
            text = new String(payload, languageCodeLength + 1, payload.length - languageCodeLength - 1, textEncoding);
        } catch (UnsupportedEncodingException e) {
            Log.e("UnsupportedEncoding", e.toString());
        }

        BusNumber=text;
        if (Bus.getText().toString().isEmpty())
        {Bus.setText(text);}

    }
    private void WriteModeOn(){
        writeMode = true;
        nfcAdapter.enableForegroundDispatch(this, pendingIntent, writeTagFilters, null);
    }
    /******************************************************************************
     **********************************Disable Write*******************************
     ******************************************************************************/
    private void WriteModeOff(){
        writeMode = false;
        nfcAdapter.disableForegroundDispatch(this);
    }

    public void WappMe()
    {
        double lat = -26.103668;
        double longi = 27.954180;
        String locate = "https://maps.google.com/maps?saddr=" +lat+","+longi;

        Intent waplink = new Intent(Intent.ACTION_VIEW);
        waplink.setData(Uri.parse("https://api.whatsapp.com/send?phone="+"265991448707"));
        //waplink.putExtra(Intent.EXTRA_TEXT, locate);

        //  waplink.setType("text/Plain");
        waplink.setPackage("com.gbwhatsapp");
        startActivity(waplink);




    /*    Intent sendIntent = new Intent(Intent.ACTION_VIEW);
        sendIntent.setData(Uri.parse("https://api.whatsapp.com/send?phone=27824591203"));
     //   sendIntent.setAction(Intent.ACTION_SEND);
       sendIntent.putExtra(Intent.EXTRA_TEXT,"Test method");
       sendIntent.setType("text/Plain");
       sendIntent.setPackage("com.gbwhatsapp");
       startActivity(sendIntent);*/
    }



    private void loadSavedPreferences() {
        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(this);

        String name = sp.getString("NAME","BUS-ID:");

        Bus.setText(name);
        BusNumber=name;
    }


    private void savePreferences(String key, String value) {
        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(this);
        SharedPreferences.Editor edit = sp.edit();
        edit.putString(key, value);
        edit.commit();
    }

    public void OFBS(View v)
    {
        RouteNumber = Route.getText().toString();
        if(RouteNumber.equals("")){
            Toast.makeText(HomeActivity.this, "Trip Number field cant be empty", Toast.LENGTH_LONG).show();
            return;
        }
        final Dialog d = new Dialog(this,android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        d.setContentView(R.layout.confirm_start);
        d.getWindow().setBackgroundDrawableResource(R.drawable.rounded_rect_bgd);
        Button start = (Button) d.findViewById(R.id.button7);
        TextView tv1 = (TextView) d.findViewById(R.id.textView13);
        TextView tv2 = (TextView) d.findViewById(R.id.textView15);
        TextView tv3 = (TextView) d.findViewById(R.id.textView16);
        tv1.setText("DRIVER -> "+Name);
        tv2.setText("BUS -> "+BusNumber);
        tv3.setText("TRIP -> "+RouteNumber);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HomeActivity.Event tas=new Event();
                tas.execute();
                RouteNumber = Route.getText().toString();
              //  BusNumber = Bus.getText().toString();

                savePreferences("NAME", BusNumber);

                Intent intent = new Intent(HomeActivity.this, OFBS.class);
                intent.putExtra("name", Name); //operator
                intent.putExtra("bus", BusNumber); //busnumber
                intent.putExtra("deadkm", DISTANCE); //dead km
                intent.putExtra("route", RouteNumber); //route
                startActivity(intent);
                finish();
                d.dismiss();

            }
        });

        Button resume = (Button) d.findViewById(R.id.button6);
        resume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                d.dismiss();
            }
        });

        if (d.isShowing()) {
            d.dismiss();
        } else {
            d.show();
        }

    }





    @Override
    protected void onResume() {
        super.onResume();

        WriteModeOn();

        if (nfcAdapter != null) {
            if (!nfcAdapter.isEnabled()) {
                showWirelessSettingsDialog();
            }

        }


    }
    @Override
    protected void onPause()
    {
        super.onPause();
        WriteModeOff();

    }

    private void showWirelessSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("NFC SETTINGS");
        builder.setMessage("Enable NFC");
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(Settings.ACTION_NFC_SETTINGS);
                startActivity(intent);
            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });
        builder.create().show();
        return;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);



        //menuItemEmulate = menu.findItem(R.id.itemEmulate);
        return true;
    }

    // Handles the user's menu selection.
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.logout:
                // Display the fragment as the main content
                //  Intent i = new Intent(this, SettingsActivity.class);
                //  startActivity(i);

                int pid = android.os.Process.myPid();
                android.os.Process.killProcess(pid);
                this.finish();
                System.exit(0);

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
        //return true;
    }

    //getting location updates

    private void init() {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mSettingsClient = LocationServices.getSettingsClient(this);

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                // location is received
                mCurrentLocation = locationResult.getLastLocation();
                mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());


            }
        };

        mRequestingLocationUpdates = false;

        mLocationRequest = new LocationRequest();

        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        mLocationSettingsRequest = builder.build();
    }

    /**
     * Restoring values from saved instance state
     */
    private void restoreValuesFromBundle(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey("is_requesting_updates")) {
                mRequestingLocationUpdates = savedInstanceState.getBoolean("is_requesting_updates");
            }

            if (savedInstanceState.containsKey("last_known_location")) {
                mCurrentLocation = savedInstanceState.getParcelable("last_known_location");
            }

            if (savedInstanceState.containsKey("last_updated_on")) {
                mLastUpdateTime = savedInstanceState.getString("last_updated_on");
            }
        }


    }


    /**
     * Update the UI displaying the location data
     * and toggling the buttons
     */
    private void loginLocate() {
        try {
            if (mCurrentLocation != null) {

                Geocoder geocoder;
                List<Address> addresses;
                geocoder = new Geocoder(this, Locale.getDefault());

                double latitude = mCurrentLocation.getLatitude();
                double longitude = mCurrentLocation.getLongitude();
                Long = String.valueOf(longitude);
                Lat = String.valueOf(latitude);
                addresses = geocoder.getFromLocation(latitude, longitude, 1);


                if (addresses != null && addresses.size() > 0) {
                    String address = addresses.get(0).getAddressLine(0);


                    //  Toast.makeText(this,address,Toast.LENGTH_LONG).show();
                    // GPS.setText(address);
                    loginLocation.add(address);
                    mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
                    //    GPS.setText(startGps);



                }

            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("is_requesting_updates", mRequestingLocationUpdates);
        outState.putParcelable("last_known_location", mCurrentLocation);
        outState.putString("last_updated_on", mLastUpdateTime);

    }


    /**
     * Starting location updates
     * Check whether location settings are satisfied and then
     * location updates will be requested
     */

    private void startLoginLocation() {
        mSettingsClient
                .checkLocationSettings(mLocationSettingsRequest)
                .addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
                    @SuppressLint("MissingPermission")
                    @Override
                    public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                        Log.i(TAG, "All location settings are satisfied.");

                        //Toast.makeText(getApplicationContext(), "Started location updates!", Toast.LENGTH_SHORT).show();

                        //noinspection MissingPermission
                        mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                                mLocationCallback, Looper.myLooper());

                        loginLocate();
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        int statusCode = ((ApiException) e).getStatusCode();
                        switch (statusCode) {
                            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                Log.i(TAG, "Location settings are not satisfied. Attempting to upgrade " +
                                        "location settings ");
                                try {
                                    // Show the dialog by calling startResolutionForResult(), and check the
                                    // result in onActivityResult().
                                    ResolvableApiException rae = (ResolvableApiException) e;
                                    rae.startResolutionForResult(HomeActivity.this, REQUEST_CHECK_SETTINGS);
                                } catch (IntentSender.SendIntentException sie) {
                                    Log.i(TAG, "PendingIntent unable to execute request.");
                                }
                                break;
                            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                String errorMessage = "Location settings are inadequate, and cannot be " +
                                        "fixed here. Fix in Settings.";
                                Log.e(TAG, errorMessage);

                                Toast.makeText(HomeActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                        }

                        loginLocate();

                    }
                });
    }
    public void startLoginLocationButtonClick() {
        // Requesting ACCESS_FINE_LOCATION using Dexter library
        Dexter.withActivity(this)
                .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        mRequestingLocationUpdates = true;
                        startLoginLocation();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        if (response.isPermanentlyDenied()) {
                            // open device settings when the permission is
                            // denied permanently
                            openSettings();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }




    /**
     * Starting location updates
     * Check whether location settings are satisfied and then
     * location updates will be requested
     */
    private void startLocationUpdates() {
        mSettingsClient
                .checkLocationSettings(mLocationSettingsRequest)
                .addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
                    @SuppressLint("MissingPermission")
                    @Override
                    public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                        Log.i(TAG, "All location settings are satisfied.");

                        //        Toast.makeText(getApplicationContext(), "Started location updates!", Toast.LENGTH_SHORT).show();

                        //noinspection MissingPermission
                        mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                                mLocationCallback, Looper.myLooper());


                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        int statusCode = ((ApiException) e).getStatusCode();
                        switch (statusCode) {
                            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                Log.i(TAG, "Location settings are not satisfied. Attempting to upgrade " +
                                        "location settings ");
                                try {
                                    // Show the dialog by calling startResolutionForResult(), and check the
                                    // result in onActivityResult().
                                    ResolvableApiException rae = (ResolvableApiException) e;
                                    rae.startResolutionForResult(HomeActivity.this, REQUEST_CHECK_SETTINGS);
                                } catch (IntentSender.SendIntentException sie) {
                                    Log.i(TAG, "PendingIntent unable to execute request.");
                                }
                                break;
                            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                String errorMessage = "Location settings are inadequate, and cannot be " +
                                        "fixed here. Fix in Settings.";
                                Log.e(TAG, errorMessage);

                                Toast.makeText(HomeActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                        }


                    }
                });
    }


    public void startLocationButtonClick() {
        // Requesting ACCESS_FINE_LOCATION using Dexter library
        Dexter.withActivity(this)
                .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        mRequestingLocationUpdates = true;
                        startLocationUpdates();
                        startLoginLocation();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        if (response.isPermanentlyDenied()) {
                            // open device settings when the permission is
                            // denied permanently
                            openSettings();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            // Check for the integer request code originally supplied to startResolutionForResult().
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        Log.e(TAG, "User agreed to make required location settings changes.");
                        // Nothing to do. startLocationupdates() gets called in onResume again.
                        break;
                    case Activity.RESULT_CANCELED:
                        Log.e(TAG, "User chose not to make required location settings changes.");
                        mRequestingLocationUpdates = false;
                        break;
                }
                break;
        }
    }

    private void openSettings() {
        Intent intent = new Intent();
        intent.setAction(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null);
        intent.setData(uri);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }


    private class Event extends AsyncTask<String, Void, Boolean> {
        //   private final ProgressDialog dialog = new ProgressDialog(OFBS.this);

        @Override
        protected void onPreExecute() {



        }

        protected Boolean doInBackground(final String... args) {





            try {
                insert();


            } catch (Exception e) {
                Log.e("", e.getMessage(), e);
                return false;
            }
            return true;
        }


        @Override
        protected void onPostExecute(final Boolean success) {



        }
    }


    public void insert()
    {
        String event=null;
        String driver=null;
        String route = null;
        String bus = null;
        String loc = null;
        String device = null;
        StringBuilder sb = new StringBuilder();
        for (String s : loginLocation)
        {
            sb.append(s);
            sb.append("\t");
        }
        String str = sb.toString();



        device = Build.BRAND+" "+Build.MODEL+","+Build.SERIAL;
        route = Route.getText().toString();
        loc = "Lat: "+Lat+", "+"Long: "+Long;
        bus = Bus.getText().toString();



        driver = Name;
        event = "Logged in";






        insertData(device,bus,route,driver,event,loc);
        //  progress.show();


    }

    /**
     * this method used to send data to server or our local server

     * @param driver
     * @param event
     * @param device
     * @param bus
     * @param route
     *  @param loc
     */

    private void insertData(String device, String bus, String route, String driver, String event, String loc){
        ApiService3 apiService = ApiClient.getClient().create(ApiService3.class);
        Call<InsertDataResponseModel> call = apiService.insertData(device,bus,route,driver,event,loc);
        call.enqueue(new Callback<InsertDataResponseModel>() {
            @Override
            public void onResponse(Call<InsertDataResponseModel> call, Response<InsertDataResponseModel> response) {

                InsertDataResponseModel insertDataResponseModel = response.body();

                //check the status code
                if(insertDataResponseModel.getStatus()==1){
                    //  Toast.makeText(MainActivity.this, response.body().getMessage()+"& csv updated", Toast.LENGTH_SHORT).show();
                    //  writecsv();
                    //    writeNfc();
                    //progress.dismiss();
                }else{
                    Toast.makeText(HomeActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    //  progress.dismiss();
                }
            }

            @Override
            public void onFailure(Call<InsertDataResponseModel> call, Throwable t) {
                //  Toast.makeText(MainActivity.this, "failed to save data, check internet connection or user is already registered ", Toast.LENGTH_SHORT).show();
                //  progress.dismiss();
            }
        });
    }


    @Override
    public void onBackPressed()
    {
        return;
    }


}
