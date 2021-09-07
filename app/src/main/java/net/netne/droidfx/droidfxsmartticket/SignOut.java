package net.netne.droidfx.droidfxsmartticket;
import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.IntentSender;
import android.location.Address;
import android.location.Geocoder;
//import android.location.Location;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;

import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Toast;
import android.nfc.tech.IsoDep;
import android.nfc.tech.MifareClassic;
import android.nfc.tech.MifareUltralight;
import android.nfc.tech.NfcA;
import android.nfc.tech.NfcB;
import android.nfc.tech.NfcF;
import android.nfc.tech.NfcV;


import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import android.widget.TextView;

//import au.com.bytecode.opencsv.CSVWriter;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.karumi.dexter.BuildConfig;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import net.netne.droidfx.droidfxsmartticket.ApiServices.ApiService3;
import net.netne.droidfx.droidfxsmartticket.clients.ApiClient;
import net.netne.droidfx.droidfxsmartticket.models.InsertDataResponseModel;

import static android.content.ContentValues.TAG;
import static android.widget.Toast.makeText;
import static java.lang.String.valueOf;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignOut extends Activity implements AsyncResponse {

    // PillowNfcManager nfcManager;
    // WriteTagHelper writeHelper;
    //  Button Valid;
    //a broadcast to know weather the data is synced or not
    public static final String DATA_SAVED_BROADCAST = "net.simplifiedcoding.datasaved";

    //Broadcast receiver to know the sync status
    private BroadcastReceiver broadcastReceiver;
    private static final int REQUEST_CHECK_SETTINGS = 100;
    ArrayList<String> logoutLocation = new ArrayList<>();
    private String mLastUpdateTime;
    EditText Taguid;
    EditText Pin;


    String Name;
    // private static HelperClass helper;
    private static   File file=null;

    MediaPlayer mp;
    ScrollView layout;
    private View v;
    String DEVICEID=null;
    String DRIVERID=null;
    String ROUTEID;
    String BUSID=null;
    String DISTANCE=null;
    String DeadKM=null;
    String ROUTE=null;
    String Loc=null;
    String Long=null;
    String Lat=null;



    private DatabaseHelper2 db2;
    NfcAdapter nfcAdapter;
    PendingIntent pendingIntent;
    IntentFilter writeTagFilters[];
    boolean writeMode;
    Tag myTag;
    Context context;
    private FusedLocationProviderClient mFusedLocationClient;
    private SettingsClient mSettingsClient;
    private LocationRequest mLocationRequest;
    private LocationSettingsRequest mLocationSettingsRequest;
    private LocationCallback mLocationCallback;
    private Location mCurrentLocation;
    private String mEndTime;
    // boolean flag to toggle the ui
    private Boolean mRequestingLocationUpdates;



    private static HelperClass helper;

    private final String[][] techList = new String[][] {
            new String[] {
                    NfcA.class.getName(),
                    NfcB.class.getName(),
                    NfcF.class.getName(),
                    NfcV.class.getName(),
                    IsoDep.class.getName(),
                    MifareClassic.class.getName(),
                    MifareUltralight.class.getName(), Ndef.class.getName()
            }
    };


    private Handler mHandler = new Handler();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signout);


        Taguid = (EditText) findViewById(R.id.tv);


       // layout=(ScrollView) findViewById(R.id.bg);
        //  Valid= (Button) findViewById(R.id.check);
        //  Delete= (Button) findViewById(R.id.delete);
        v = findViewById(R.id.mainL);
        helper=new HelperClass(this);
        Pin= (EditText) findViewById(R.id.pin);
        helper.getWritableDatabase();
        db2 = new DatabaseHelper2(this);
// initialize the necessary libraries
        init();
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


        // restore the values from saved instance state

        restoreValuesFromBundle(savedInstanceState);
        LocationButtonClick();
        LocationButtonClick();
        LocationButtonClick();









//new things




        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        //   nfcAdapter.enableForegroundDispatch(this, pendingIntent, new IntentFilter[]{filter}, this.techList);

        if (nfcAdapter == null) {
            Toast.makeText(this, "this device dont support NFC", Toast.LENGTH_LONG).show();
            finish();
        }
        //  readFromIntent(getIntent());
        pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        tagDetected.addCategory(Intent.CATEGORY_DEFAULT);
        writeTagFilters = new IntentFilter[]{tagDetected};


        DRIVERID = getIntent().getStringExtra("name");
        DEVICEID = getIntent().getStringExtra("device");
        BUSID = getIntent().getStringExtra("bus");
        ROUTE = getIntent().getStringExtra("route");
     //   Loc = getIntent().getStringExtra("loc");


        Pin.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(Pin.getText().toString().length()==5)
                {
                    checkData3();
                    startLoginLocationButtonClick();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });



    }



    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        //GetDataFromTag(tag, intent);
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())) {
            myTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            // GetDataFromTag(myTag, intent);
            ((TextView) findViewById(R.id.tv)).setText(this.ByteArrayToHexString(getIntent().getByteArrayExtra(NfcAdapter.EXTRA_ID)));

        }

        checkData();
        startLoginLocationButtonClick();


    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void Proceed()
    {
        String uid = Taguid.getText().toString();
        //   String amount = Amount.getText().toString();

        if(Name.equals("")){
            Toast.makeText(SignOut.this, "please re-read the card", Toast.LENGTH_LONG).show();
            return;
        }
        SignOut.Event tas=new Event();
        tas.execute();
        email();
        clearTable();
        Intent intent = new Intent(SignOut.this, startPage.class);
        startActivity(intent);
        this.finish();
    }

    //for pin dialog
    //updating and validating the name and tags to local storage : tap on
    private void  loginUsingPin(String name) {

        boolean found= db2.searchData(name);
        if (found)
        {

            Name = db2.operatorname;
            Proceed();
        }
        else
        {
            Toast.makeText(this,"Wrong pin",Toast.LENGTH_LONG).show();
        }
    }

    private void  loginUsingTag(String name) {

        boolean found= db2.searchData(name);
        if (found)
        {

            Name = db2.operatorname;
            Proceed();
        }
        else
        {
            Toast.makeText(this,"Tag Denied",Toast.LENGTH_LONG).show();
        }
    }
    public void clearTable(){
        helper.deleteAll();
    }

    public void email()
    {
        new Thread(new Runnable() {

            public void run() {

                try {

               /* GMailSender sender = new GMailSender(

                        "ronnexwataya0@gmail.com",

                        "ronnex93");
                        */

                    GMailSender sender = new GMailSender(

                            "ronnexwataya0@gmail.com",

                            "wataya1993");




                    sender.addAttachment(Environment.getExternalStorageDirectory().getPath()+"/Android/data/net.netne.droidfx.droidfxsmartticket/files/TRIP CONFIRMATION.csv");

                    sender.sendMail("Trip Confirmation", "Today's Trip confirmation report",

                            //sender: "ronnexwataya0@gmail.com",
                            "sales@droidfxtec.com",

                            "ronnexwataya@yahoo.com");






                } catch (Exception e) {

                    Toast.makeText(getApplicationContext(),"Error",Toast.LENGTH_LONG).show();
                }

            }

        }).start();
    }

    //get tag uid
    private String ByteArrayToHexString(byte [] inarray) {
        int i, j, in;
        String [] hex = {"0","1","2","3","4","5","6","7","8","9","A","B","C","D","E","F"};
        String out= "";

        for(j = 0 ; j < inarray.length ; ++j)
        {
            in = (int) inarray[j] & 0xff;
            i = (in >> 4) & 0x0f;
            out += hex[i];
            i = in & 0x0f;
            out += hex[i];
        }
        return out;
    }
    //read from database


    public void checkData(){

        final String name = Taguid.getText().toString();
        // saveNameToServer();
        loginUsingTag(name);

    }

    public void checkData3(){
        final String name = Pin.getText().toString();
        // saveNameToServer();
        loginUsingPin(name);

    }
    @Override
    public void process(String result) {
        try {
            // dialog();
            JSONArray jArray = new JSONArray(result);
            int flag = 1;

            // Extract data from json and store into ArrayList as class objects
            for (int i = 0; i < jArray.length(); i++) {
                JSONObject json_data = jArray.getJSONObject(i);

                final String balance = json_data.getString("balance");
                final String name = json_data.getString("operator");

                Name=name;

                new Timer().schedule(new TimerTask(){@Override public void run(){ Proceed();}},1000);




            }

        }
        catch(JSONException e)
        {

        }

    }

    @Override
    public void processPin(String result) {

    }

    @Override
    public void processFinish(String result) {


    }


    private void WriteModeOn()
    {
        writeMode=true;
        nfcAdapter.enableForegroundDispatch(this,pendingIntent,writeTagFilters,null);
    }
    private void WriteModeOff()
    {
        writeMode=false;
        nfcAdapter.disableForegroundDispatch(this);
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
    protected void onPause() {
        //  nfcManager.onActivityPause();
        super.onPause();

    }
    private class Event extends AsyncTask<String, Void, Boolean> {
        //   private final ProgressDialog dialog = new ProgressDialog(OFBS.this);

        @Override
        protected void onPreExecute() {



        }

        protected Boolean doInBackground(final String... args) {





            try {
                insert3();


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




    public void insert3()
    {


        StringBuilder sp = new StringBuilder();
        for (String ss : logoutLocation)
        {
            sp.append(ss);
            sp.append("\t");
        }
        String strr = sp.toString();
        // Loc = strr;


        String driver = DRIVERID;
        String device = DEVICEID;
        String route = ROUTE;
        String loc ="Lat: "+Lat+", "+"Long: "+Long;
        String bus = BUSID;
        String event = "logged out  ";






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
                    Toast.makeText(SignOut.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
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
                    logoutLocation.add(address);
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
                                    rae.startResolutionForResult(SignOut.this, REQUEST_CHECK_SETTINGS);
                                } catch (IntentSender.SendIntentException sie) {
                                    Log.i(TAG, "PendingIntent unable to execute request.");
                                }
                                break;
                            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                String errorMessage = "Location settings are inadequate, and cannot be " +
                                        "fixed here. Fix in Settings.";
                                Log.e(TAG, errorMessage);

                                Toast.makeText(SignOut.this, errorMessage, Toast.LENGTH_LONG).show();
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

                        //   Toast.makeText(getApplicationContext(), "!", Toast.LENGTH_SHORT).show();

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
                                    rae.startResolutionForResult(SignOut.this, REQUEST_CHECK_SETTINGS);
                                } catch (IntentSender.SendIntentException sie) {
                                    Log.i(TAG, "PendingIntent unable to execute request.");
                                }
                                break;
                            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                String errorMessage = "Location settings are inadequate, and cannot be " +
                                        "fixed here. Fix in Settings.";
                                Log.e(TAG, errorMessage);

                                Toast.makeText(SignOut.this, errorMessage, Toast.LENGTH_LONG).show();
                        }


                    }
                });
    }


    public void LocationButtonClick() {
        // Requesting ACCESS_FINE_LOCATION using Dexter library
        Dexter.withActivity(this)
                .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        mRequestingLocationUpdates = true;
                        startLocationUpdates();
                      //  startCustomerLocation();
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
    private void openSettings() {
        Intent intent = new Intent();
        intent.setAction(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null);
        intent.setData(uri);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
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


    @Override
    public void onBackPressed()
    {
        return;
    }

}

