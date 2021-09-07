package net.netne.droidfx.droidfxsmartticket;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
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
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
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
import java.io.FileWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import static android.widget.Toast.LENGTH_LONG;
import static android.widget.Toast.makeText;
import static java.lang.String.valueOf;
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
import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.content.ContentValues;
import android.net.Uri;

import net.netne.droidfx.droidfxsmartticket.ApiServices.ApiService;
import net.netne.droidfx.droidfxsmartticket.ApiServices.ApiService2;
import net.netne.droidfx.droidfxsmartticket.ApiServices.ApiService3;
import net.netne.droidfx.droidfxsmartticket.clients.ApiClient;
import net.netne.droidfx.droidfxsmartticket.clients.ApiClient2;
import net.netne.droidfx.droidfxsmartticket.models.InsertDataResponseModel;
import net.netne.droidfx.droidfxsmartticket.models.InsertDataResponseModel2;

import au.com.bytecode.opencsv.CSVWriter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OFBS extends Activity implements AsyncResponse {

    // PillowNfcManager nfcManager;
    // WriteTagHelper writeHelper;
    //  Button Valid;

    /*
     * this is the url to our webservice

     * */
    public static final String URL_SAVE_NAME = "http://droidfxtec.pe.hu/mysqlsqlite/saveName.PHP";
    public static final String URL_SAVE_NAME2 = "http://droidfxtec.pe.hu/mysqlsqlite/saveName2.PHP";
    public static final String URL_SAVE_NAME3 = "http://droidfxtec.pe.hu/mysqlsqlite/transactions.PHP";

    //database helper object
    private TapHelper Thelper;
    private DatabaseHelper db;
    private DatabaseHelper2 db2;
    private DatabaseHelper3 db3;
    SQLiteDatabase sqLiteDatabase;
    SQLiteDatabase sqLiteDatabase2;
    private TextView tvCount;
    private String CountData= null;
    //List to store all the names
    private List<Name> names;

    //1 means data is synced and 0 means data is not synced
    public static final int NAME_SYNCED_WITH_SERVER = 1;
    public static final int NAME_NOT_SYNCED_WITH_SERVER = 0;

    //a broadcast to know weather the data is synced or not
    public static final String DATA_SAVED_BROADCAST = "net.simplifiedcoding.datasaved";

    //Broadcast receiver to know the sync status
    private BroadcastReceiver broadcastReceiver;


    ProgressDialog progressDialog;
    private Handler mHandler = new Handler();


    private String mLastUpdateTime;
    private String StartTime;
    private static final String TAG = OFBS.class.getSimpleName();
    Button Delete;
    EditText Read;
    TextView Name;
    // private static HelperClass helper;
    protected static File file = null;
    String opname;
    MediaPlayer mp;
    ScrollView layout;
    private View v;
    TextView BtMz;
    String Status=null;
    String TDistance = "";
    String CostKM = "";
    String BTMZ = "Blantyre to Mzuzu";

    float count=100*.01f;
    Format nfcManager;
    Dialog mDialog;
    NfcAdapter nfcAdapter;
    PendingIntent pendingIntent;
    IntentFilter writeTagFilters[];
    boolean writeMode;
    Tag myTag;
    Context context;
    String NAME = null;
    String TAGUID=null;
    //  String TAGUID2=null;
    String AMOUNT = "20";
    String IF = null;
    String PAYMENT = null;
    TextView GPS;
    ImageView logout;
    int Cost = 20;
    String Route = "Blantyre to Mzuzu";
    String DEVICEID=null;
    String DRIVERID=null;
    String ROUTEID;
    String BUSID=null;
    String DISTANCE=null;
    int Distance=0;
    String DeadKM=null;
    String ROUTE=null;
    String LongStartJ=null;
    String LongEvent=null;
    String LongTrans=null;
    String LatStartJ=null;
    String LatEvent=null;
    String LatTrans=null;
    String Time=null;
    Button Panic;
    String startLoc=null;
    ArrayList<String>List = new ArrayList<>();
    Calendar cal = Calendar.getInstance(Locale.ENGLISH);
    private static HelperClass helper;
    //private static TapHelper Thelper;
    private final String[][] techList = new String[][]{
            new String[]{
                    NfcA.class.getName(),
                    NfcB.class.getName(),
                    NfcF.class.getName(),
                    NfcV.class.getName(),
                    IsoDep.class.getName(),
                    MifareClassic.class.getName(),
                    MifareUltralight.class.getName(), Ndef.class.getName()
            }
    };

    private static final int REQUEST_CHECK_SETTINGS = 100;


    // bunch of location related apis
    private FusedLocationProviderClient mFusedLocationClient;
    private SettingsClient mSettingsClient;
    private LocationRequest mLocationRequest;
    private LocationSettingsRequest mLocationSettingsRequest;
    private LocationCallback mLocationCallback;
    private Location mCurrentLocation;
    private String mEndTime;
    // boolean flag to toggle the ui
    private Boolean mRequestingLocationUpdates;

    //ODOMETER METHODS
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
        // Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandle(this));
        setContentView(R.layout.read);

        Read = (EditText) findViewById(R.id.editText4);
        Name = (TextView) findViewById(R.id.textView6);
        BtMz = (TextView) findViewById(R.id.textView8);
        TextView Bnumber = (TextView) findViewById(R.id.textView7);
        logout = (ImageView) findViewById(R.id.imageView5);
      //  layout = (ScrollView) findViewById(R.id.bg);

        //  Valid= (Button) findViewById(R.id.check);
        //  Delete= (Button) findViewById(R.id.delete);
        v = findViewById(R.id.mainL);
        // logout.setEnabled();
        logout.setVisibility(View.VISIBLE);


        //Panic.setOnClickListener(new View.OnClickListener() {
           // @Override
            //public void onClick(View v) {
                //Panic.setBackgroundResource(R.drawable.pblank);
                //Toast.makeText(getBaseContext(), "Panic has been Issued", Toast.LENGTH_LONG).show();

          //  }
        //});
        //Panic = (Button)findViewById(R.id.panic);
        Button Cash = (Button) findViewById(R.id.cash);
        Cash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newDistance = String.valueOf(Distance);
                Intent intent = new Intent(OFBS.this, OB.class);
                intent.putExtra("name", DRIVERID); //operator
                intent.putExtra("bus", BUSID); //busnumber
                intent.putExtra("route", ROUTE); //dead km
                intent.putExtra("distance", newDistance); //distance

                startActivity(intent);

            }
        });


        ImageView image = (ImageView) findViewById(R.id.imgview);
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                fetchData();
                StringBuilder sb = new StringBuilder();
                for (String s : List) {
                    sb.append(s);
                    sb.append("\t");
                }
                String str = sb.toString();
                Toast.makeText(OFBS.this,str,Toast.LENGTH_LONG).show();
                autoTapRead();
                clearTable();


            }
        });
        BtMz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(OFBS.this,DISTANCE,Toast.LENGTH_LONG).show();

            }
        });

        String clock = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
        Time = clock;

        DEVICEID = Build.BRAND+" "+Build.MODEL+","+Build.SERIAL;
        DRIVERID = getIntent().getStringExtra("name");
        BUSID = getIntent().getStringExtra("bus");
        DeadKM = getIntent().getStringExtra("deadkm");
        ROUTE = getIntent().getStringExtra("route");
        //route
        BtMz.setText("TRIP -> "+ROUTE);
        Name.setText("DRIVER -> "+DRIVERID);
        Bnumber.setText("BUS -> "+BUSID);
        // initialize the necessary libraries
        init();

        // restore the values from saved instance state
        restoreValuesFromBundle(savedInstanceState);
        LocationButtonClick();
        LocationButtonClick();
        LocationButtonClick();


        registerReceiver(new NetworkStateChecker(), new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

        //initializing views and objects
        helper = new HelperClass(this);
        helper.getWritableDatabase();
        Thelper = new TapHelper(this);
      //  Thelper.getWritableDatabase();
        db = new DatabaseHelper(this);
        db2 = new DatabaseHelper2(this);
        db3 = new DatabaseHelper3(this);
        names = new ArrayList<>();
        tvCount = (TextView) findViewById(R.id.textView14);
        CountTable();
        //calling the method to load all the stored names
        startRepeating();

      //  loadNames();

        //the broadcast receiver to update sync status
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                //loading the names again
            //    loadNames();
            }
        };

        //registering the broadcast receiver to update sync status
        registerReceiver(broadcastReceiver, new IntentFilter(DATA_SAVED_BROADCAST));


//new things

        ExportLocation tas=new ExportLocation();
        tas.execute();
        helper = new HelperClass(this);
        helper.getWritableDatabase();
        Thelper = new TapHelper(this);
        Thelper.getWritableDatabase();
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

        watchMileage();
       /* final Snackbar snackbar = Snackbar.make(v, "                          TRIP COST :  R20//,00", Snackbar.LENGTH_INDEFINITE);
        String dismis = "dismiss";

        snackbar.setAction("dismiss", new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                snackbar.dismiss();
            }
        });
        snackbar.show(); */
        gpsDialog();

      //  new StoreJSonDataInToSQLiteClass2(OFBS.this).execute();

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gpsDialog2();


            }
        });

        final Button Panic = (Button) findViewById(R.id.panic); //added for Panic Button Mask
        Panic.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Panic.setBackgroundResource(R.drawable.pblank); //added for Panic Button Mask
                ExportLocation tas=new ExportLocation();
                tas.execute();
                final OFBS.PanicPush task=new PanicPush();

                new Timer().schedule(new TimerTask(){@Override public void run(){ task.execute();}},400);
            }
        });

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
                DISTANCE=distanceStr;
                Distance = (int) distance;
                handler.postDelayed(this, 1000);
            }
        });
    }

    //GPS methods
    private void init() {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mSettingsClient = LocationServices.getSettingsClient(this);

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                // location is received
                mCurrentLocation = locationResult.getLastLocation();


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
    private void customerLocate() {
        if (mCurrentLocation != null) {

            Geocoder geocoder;
            List<Address> addresses;
            geocoder = new Geocoder(this, Locale.getDefault());

            double latitude = mCurrentLocation.getLatitude();
            double longitude = mCurrentLocation.getLongitude();
            LatTrans = String.valueOf(latitude);
            LongTrans = String.valueOf(longitude);

        }

    }


    private void updateLocationUIsTart() {
        if (mCurrentLocation != null) {

            Geocoder geocoder;
            List<Address> addresses;
            geocoder = new Geocoder(this, Locale.getDefault());

            double latitude = mCurrentLocation.getLatitude();
            double longitude = mCurrentLocation.getLongitude();
            LatStartJ = String.valueOf(latitude);
            LongStartJ = String.valueOf(longitude);
            //   addresses = geocoder.getFromLocation(latitude, longitude, 1);
            StartTime = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());





        }

    }

    private void updateLocationUIsTop() {
        if (mCurrentLocation != null) {

            Geocoder geocoder;
            List<Address> addresses;
            geocoder = new Geocoder(this, Locale.getDefault());

            double latitude = mCurrentLocation.getLatitude();
            double longitude = mCurrentLocation.getLongitude();
            LatEvent = String.valueOf(latitude);
            LongEvent = String.valueOf(longitude);
            //  addresses = geocoder.getFromLocation(latitude, longitude, 1);
            mEndTime = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());





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
    private void startLocationUpdatesStart() {
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

                        updateLocationUIsTart();
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
                                    rae.startResolutionForResult(OFBS.this, REQUEST_CHECK_SETTINGS);
                                } catch (IntentSender.SendIntentException sie) {
                                    Log.i(TAG, "PendingIntent unable to execute request.");
                                }
                                break;
                            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                String errorMessage = "Location settings are inadequate, and cannot be " +
                                        "fixed here. Fix in Settings.";
                                Log.e(TAG, errorMessage);

                                Toast.makeText(OFBS.this, errorMessage, Toast.LENGTH_LONG).show();
                        }

                        updateLocationUIsTart();

                    }
                });
    }
    private void startCustomerLocation() {
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

                        customerLocate();
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
                                    rae.startResolutionForResult(OFBS.this, REQUEST_CHECK_SETTINGS);
                                } catch (IntentSender.SendIntentException sie) {
                                    Log.i(TAG, "PendingIntent unable to execute request.");
                                }
                                break;
                            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                String errorMessage = "Location settings are inadequate, and cannot be " +
                                        "fixed here. Fix in Settings.";
                                Log.e(TAG, errorMessage);

                                Toast.makeText(OFBS.this, errorMessage, Toast.LENGTH_LONG).show();
                        }

                        customerLocate();

                    }
                });
    }


    private void startLocationUpdatesStop() {
        mSettingsClient
                .checkLocationSettings(mLocationSettingsRequest)
                .addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
                    @SuppressLint("MissingPermission")
                    @Override
                    public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                        Log.i(TAG, "All location settings are satisfied.");

                        //  Toast.makeText(getApplicationContext(), "Started location updates!", Toast.LENGTH_SHORT).show();

                        //noinspection MissingPermission
                        mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                                mLocationCallback, Looper.myLooper());

                        updateLocationUIsTop();
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
                                    rae.startResolutionForResult(OFBS.this, REQUEST_CHECK_SETTINGS);
                                } catch (IntentSender.SendIntentException sie) {
                                    Log.i(TAG, "PendingIntent unable to execute request.");
                                }
                                break;
                            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                String errorMessage = "Location settings are inadequate, and cannot be " +
                                        "fixed here. Fix in Settings.";
                                Log.e(TAG, errorMessage);

                                Toast.makeText(OFBS.this, errorMessage, Toast.LENGTH_LONG).show();
                        }

                        updateLocationUIsTop();
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
                        startLocationUpdatesStart();
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

    public void startCustomerLocationButtonClick() {
        // Requesting ACCESS_FINE_LOCATION using Dexter library
        Dexter.withActivity(this)
                .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        mRequestingLocationUpdates = true;
                        startCustomerLocation();
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

    public void stopLocationButtonClick() {
        // Requesting ACCESS_FINE_LOCATION using Dexter library
        Dexter.withActivity(this)
                .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        mRequestingLocationUpdates = true;
                        startLocationUpdatesStop();
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
        Uri uri = Uri.fromParts("package",
                BuildConfig.APPLICATION_ID, null);
        intent.setData(uri);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        //GetDataFromTag(tag, intent);
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())) {
            myTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            // GetDataFromTag(myTag, intent);
            ((TextView) findViewById(R.id.editText4)).setText(this.ByteArrayToHexString(getIntent().getByteArrayExtra(NfcAdapter.EXTRA_ID)));
        }


        readData();
       // checkData();

    }

    //read from tag
    private void GetDataFromTag(Tag tag, Intent intent) {
        Ndef ndef = Ndef.get(tag);
        try {
            ndef.connect();
//            txtType.setText(ndef.getType().toString());
//            txtSize.setText(String.valueOf(ndef.getMaxSize()));
//            txtWrite.setText(ndef.isWritable() ? "True" : "False");
            Parcelable[] messages = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);

            if (messages != null) {
                NdefMessage[] ndefMessages = new NdefMessage[messages.length];
                for (int i = 0; i < messages.length; i++) {
                    ndefMessages[i] = (NdefMessage) messages[i];
                }
                NdefRecord record = ndefMessages[0].getRecords()[0];

                byte[] payload = record.getPayload();
                String text = new String(payload);
                Log.e("tag", "vahid" + text);
                Name.setText(text);
                ndef.close();

            }
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Cannot Read From Tag.", Toast.LENGTH_LONG).show();
        }
    }

    //get tag uid
    private String ByteArrayToHexString(byte[] inarray) {
        int i, j, in;
        String[] hex = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F"};
        String out = "";

        for (j = 0; j < inarray.length; ++j) {
            in = (int) inarray[j] & 0xff;
            i = (in >> 4) & 0x0f;
            out += hex[i];
            i = in & 0x0f;
            out += hex[i];
        }
        return out;
    }
    public void fetchData()
    {
        Cursor c = Thelper.Count();
        c.moveToFirst();
        while (!c.isAfterLast()){
            List.add(c.getString(c.getColumnIndex(Thelper.TAGS)));
            c.moveToNext();
        }

    }

    public void autoTapRead() {

        ExportLocation tas=new ExportLocation();
        tas.execute();

        for (int i = 0; i < List.size(); i++)

        {

            String tags=List.get(i);
            // Read.setText(List.get(i));
            boolean found = Thelper.searchData(tags);
            if (found) {
                double fair = 10000.00;
                double totalfair = 0.0;
                double distancefair = 200.00;
                int totalkm = 0;
                double SUMM = 0.0;
                int olddistance = (int) TapHelper.sum;
                int newdistance = Distance;
                //maths
                totalkm = (newdistance - olddistance);
                totalfair = totalkm * distancefair;
                SUMM = fair - totalfair;
                Status = "Tapped off";


                TDistance = String.valueOf(totalkm);
                TAGUID = tags;

                // SUMM = (newdistance - olddistance)*2;

                if (totalfair < 500.00) {
                    CostKM = String.valueOf(500.00);


                    final String name =tags;
                    final double sum = fair-500;
                    // saveNameToServer();
                    autoupdateToLocalStorage(name,sum, NAME_NOT_SYNCED_WITH_SERVER);
                } else if (totalfair > 10000.00) {
                    CostKM = String.valueOf(10000.00);

                    final String name =tags;
                    final double sum = fair;
                    // saveNameToServer();
                    autoupdateToLocalStorage(name,sum, NAME_NOT_SYNCED_WITH_SERVER);

                } else {
                    CostKM = String.valueOf((totalfair));

                    final String name =tags;
                    final double sum = SUMM;
                    // saveNameToServer();
                    autoupdateToLocalStorage(name,sum, NAME_NOT_SYNCED_WITH_SERVER);
                }

                String mydate = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());


                String taguid = tags;
                String device = DEVICEID;
                String bus = BUSID;
                String route = ROUTE;
                String driver = DRIVERID;
                String tim = Time;
                String loc = "Lat: "+LatTrans+", "+"Long: "+LongTrans;
                String status = "Tapped Off";
                String totalcost = CostKM;
                String totalkmm = String.valueOf(totalkm);




               // insertData(device,bus,route,driver,taguid,tim,loc,status,totalkmm,totalcost)
                saveReportToLocalStorage(DEVICEID,BUSID,ROUTE,DRIVERID,tags,Time,"Lat: "+LatTrans+", "+"Long: "+LongTrans,status,totalkmm,CostKM, NAME_NOT_SYNCED_WITH_SERVER);



            }

            // List.clear();
        }
    }

    //read from database
    public void readData() {
        ExportLocation tas=new ExportLocation();
        tas.execute();
        String tags=Read.getText().toString();
        boolean found= Thelper.searchData(tags);
        if (found)
        {
            double fair = 10000.00;
            double totalfair =0.0;
            double distancefair = 200.00;
            int totalkm = 0;
            double SUMM=0.0;
            int olddistance = (int) TapHelper.sum;
            int newdistance = Distance;
            //maths
            totalkm = (newdistance-olddistance);
            totalfair = totalkm*distancefair;
            SUMM = fair-totalfair;
            Status = "Tapped off";
            TDistance = String.valueOf(totalkm);




            // SUMM = (newdistance - olddistance)*2;

            if (totalfair < 500.00)
            {
                CostKM = String.valueOf(500.00);

                final String name = Read.getText().toString().trim();
                final double sum = fair-500;
                // saveNameToServer();
                updatetagsToLocalStorage(name,sum, NAME_NOT_SYNCED_WITH_SERVER);
            }
            else if (totalfair > 10000.00)
            {
                CostKM = String.valueOf(10000.00);

                final String name = Read.getText().toString().trim();
                final double sum = fair;
                // saveNameToServer();
                updatetagsToLocalStorage(name,sum, NAME_NOT_SYNCED_WITH_SERVER);
            }
            else {
                CostKM = String.valueOf(totalfair);
                final String name = Read.getText().toString().trim();
                final double sum = SUMM;
                // saveNameToServer();
                updatetagsToLocalStorage(name,sum, NAME_NOT_SYNCED_WITH_SERVER);
            }






        }

        else {


            final String name = Read.getText().toString().trim();
            final double sum = 10000.00;
            // saveNameToServer();
            updateNameToLocalStorage(name,sum, NAME_NOT_SYNCED_WITH_SERVER);
        }
    }


    //delete
    public void deleteData() {
        HashMap postData = new HashMap();
        postData.put("txtUsername", Read.getText().toString());

        PostResponse task = new PostResponse(this, postData);
        task.execute("http://droidfx.pe.hu/bproject/delete.php");
    }

    public void checkData() {
        HashMap postData = new HashMap();
        postData.put("txtUsername", Read.getText().toString());

        Post task = new Post(this, postData);
        task.execute("http://droidfx.pe.hu/bproject/check.php");

        ExportLocation tas=new ExportLocation();
        tas.execute();

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
                final String name = json_data.getString("name");
                final String taguid = json_data.getString("taguid");

                String FUNDS = "!INSUFFICIENT FUNDS!";
                String COMPLETE = "THANK YOU FOR PAYMENT:";


                final Snackbar snackbar = Snackbar.make(v, "NEW BALANCE :  " + "R" + balance , Snackbar.LENGTH_LONG);
                NAME = name;
                TAGUID = taguid;
                IF = FUNDS;
                PAYMENT = COMPLETE;
                snackbar.setAction("dismiss", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        startActivity(new Intent(OFBS.this, HomeActivity.class));
                        finish();
                        snackbar.dismiss();
                    }
                });
                snackbar.show();
                //startCustomerLocationButtonClick();

            }
        } catch (JSONException e) {

        }

    }

    @Override
    public void processPin(String result) {

    }

    //fetch gps location dialog
    public void gpsDialog() {
        final Dialog d = new Dialog(this,android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        d.setContentView(R.layout.start);
        TextView tv1 = (TextView) d.findViewById(R.id.textView13);
        TextView tv2 = (TextView) d.findViewById(R.id.textView15);
        TextView tv3 = (TextView) d.findViewById(R.id.textView16);
        tv1.setText("DRIVER -> "+DRIVERID);
        tv2.setText("BUS -> "+BUSID);
        tv3.setText("TRIP -> "+ROUTE);
        d.getWindow().setBackgroundDrawableResource(R.drawable.rounded_rect_bgd);
        ImageView img = (ImageView) d.findViewById(R.id.imageView18);
        img.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {



                final Dialog dialog = new Dialog(OFBS.this,android.R.style.Theme_Black_NoTitleBar_Fullscreen);
                dialog.setContentView(R.layout.confirm_journey);
                TextView tv1 = (TextView) dialog.findViewById(R.id.textView13);
                TextView tv2 = (TextView) dialog.findViewById(R.id.textView15);
                TextView tv3 = (TextView) dialog.findViewById(R.id.textView16);
                tv1.setText("DRIVER -> "+DRIVERID);
                tv2.setText("BUS -> "+BUSID);
                tv3.setText("TRIP -> "+ROUTE);
                Button startj = (Button) dialog.findViewById(R.id.button7);
                startj.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startLocationButtonClick();
                        new Timer().schedule(new TimerTask(){@Override public void run()
                        {
                            OFBS.Event tas=new Event();
                            tas.execute();
                        }},3000);

                        dialog.dismiss();
                        d.dismiss();

                        logout.setVisibility(View.VISIBLE);
                        logout.setEnabled(true);

                    }
                });

                Button resume = (Button) dialog.findViewById(R.id.button6);
                resume.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        dialog.dismiss();

                    }
                });

                dialog.show();


            }
        });

        if (d.isShowing()) {
            d.dismiss();
        } else {
            d.show();
        }

    }
    public void clearTable(){
        Thelper.deleteAll();
    }
    public void gpsDialog2() {
        final Dialog d = new Dialog(this,android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        d.setContentView(R.layout.stop);
        TextView tv2 = (TextView) d.findViewById(R.id.textView15);
        TextView tv3 = (TextView) d.findViewById(R.id.textView16);
        tv2.setText("BUS -> "+BUSID);
        tv3.setText("TRIP -> "+ROUTE);
        d.getWindow().setBackgroundDrawableResource(R.drawable.rounded_rect_bgd);
        Button stop = (Button) d.findViewById(R.id.imageView18);
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Dialog dialog = new Dialog(OFBS.this,android.R.style.Theme_Black_NoTitleBar_Fullscreen);
                dialog.setContentView(R.layout.confirm_stop);
                TextView tv1 = (TextView) dialog.findViewById(R.id.textView13);
                TextView tv2 = (TextView) dialog.findViewById(R.id.textView15);
                TextView tv3 = (TextView) dialog.findViewById(R.id.textView16);
                tv1.setText("DRIVER -> "+DRIVERID);
                tv2.setText("BUS -> "+BUSID);
                tv3.setText("TRIP -> "+ROUTE);
                Button stopj = (Button) dialog.findViewById(R.id.button7);
                stopj.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        stopLocationButtonClick();
                        fetchData();

                        autoTapRead();
                        clearTable();
                        new Timer().schedule(new TimerTask() {
                            @Override
                            public void run() {
                                OFBS.Event2 tas=new Event2();
                                tas.execute();
                                writecsv();
                                proceed();
                            }
                        }, 1000);

                        dialog.dismiss();
                        d.dismiss();

                        logout.setVisibility(View.VISIBLE);
                        logout.setEnabled(true);
                        clearTable();

                    }
                });

                Button resume = (Button) dialog.findViewById(R.id.button6);
                resume.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        dialog.dismiss();

                    }
                });

                dialog.show();
            }
        });

        Button resume = (Button) d.findViewById(R.id.resume);  /*RESUME BUTTON FOR END ROUTE*/
        resume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(OFBS.this, "It Works", Toast.LENGTH_LONG).show();
                d.dismiss();
            }
        });

        if (d.isShowing()) {
            d.dismiss();
        } else {
            d.show();
        }

    }
    /* resume button for accidental press */

    //Toast.makeText(OFBS.this, "It Works", Toast.LENGTH_LONG).show();

    public void proceed()
    {
        Intent intent = new Intent(this, SignOut.class);
        intent.putExtra("name", DRIVERID); //operator
        intent.putExtra("bus", BUSID);
        intent.putExtra("device", DEVICEID);
        intent.putExtra("route", ROUTE);
        startActivity(intent);
        finish();
    }

    @Override
    public void processFinish(final String result) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable()

        {
            @Override public void run()
            {
                if(result.equals("no rows")) {
                    // checkData2();
                    //fetch data


                    //final Dialog d = new Dialog(OFBS.this);
                    final Dialog d = new Dialog(OFBS.this,android.R.style.Theme_Black_NoTitleBar_Fullscreen);

                    d.setContentView(R.layout.invalid);
                    d.getWindow().setBackgroundDrawableResource(R.drawable.rounded_rect_bgd);
                    TextView username = (TextView) d.findViewById(R.id.textView2);
                    TextView feedback = (TextView) d.findViewById(R.id.textView5);
                    TextView tryagain = (TextView) d.findViewById(R.id.textView3);
                    ImageView img = (ImageView) d.findViewById(R.id.imageview);
                    ImageView img1 = (ImageView) d.findViewById(R.id.imageview1);
                    d.getWindow().setBackgroundDrawableResource(R.color.trans);


                    username.setText(NAME);
                    feedback.setText(IF);
                    String blank = username.getText().toString();

                    // d.show();


                    if(d.isShowing())
                    {
                        d.dismiss();
                    }
                    else
                    {
                        d.show();
                    }

                    if(blank.equals("")) {
                        feedback.setText("(INVALID CARD)");
                        username.setText("NO PAYMENT:");
                        img.setImageResource(R.drawable.minvc);
                        //img1.setImageResource(R.drawable.invalid2);
                        tryagain.setText("PLEASE TRY AGAIN!");
                        new Timer().schedule(new TimerTask(){@Override public void run(){d.dismiss();}},3300);
                        //warnin sound
                        mp= MediaPlayer.create(OFBS.this,R.raw.beep);
                        try {

                            if (mp.isPlaying())
                            {
                                mp.stop();
                                mp.release();
                                mp=MediaPlayer.create(OFBS.this,R.raw.beep);
                            }
                            mp.start();
                        }catch (Exception batLow)
                        {

                        }
                        return;
                    }
                    else {username.setText("NO PAYMENT:");}
                    NAME=null;
                    IF="";
                    username.setText("");
                    //warnin sound
                    mp= MediaPlayer.create(OFBS.this,R.raw.beep);
                    try {

                        if (mp.isPlaying())
                        {
                            mp.stop();
                            mp.release();
                            mp=MediaPlayer.create(OFBS.this,R.raw.beep);
                        }
                        mp.start();
                    }catch (Exception batLow)
                    {

                    }
                    //Toast.makeText(OFBS.this, "Dat not found ", Toast.LENGTH_LONG).show();
                    new Timer().schedule(new TimerTask(){@Override public void run(){d.dismiss();}},3300);
                }
                //tap off
                else if(result.equals("sumfail")) {
                    // checkData2();
                    //fetch data
                    Toast.makeText(OFBS.this, "tap off failed ", Toast.LENGTH_LONG).show();

                }
                else if(result.equals("success")){
                    insertDB();

                    String tags=Read.getText().toString();
                    double km= Distance;
                    Status = "Tapped on";
                    boolean result = Thelper.insertData(tags,km);
                    if  (result = true )
                    {
                        // Toast.makeText(Reed.this, "data saved!", Toast.LENGTH_SHORT).show();
                    }
                    else {

                        Toast.makeText(OFBS.this, "SAVING DB FAILED!", Toast.LENGTH_SHORT).show();
                    }

                    //final Dialog d = new Dialog(OFBS.this);
                    final Dialog d = new Dialog(OFBS.this,android.R.style.Theme_Black_NoTitleBar_Fullscreen); // Tap On

                    d.setContentView(R.layout.valid);
                    d.getWindow().setBackgroundDrawableResource(R.drawable.rounded_rect_bgd);
                    TextView username = (TextView) d.findViewById(R.id.textView5);
                    TextView feedback = (TextView) d.findViewById(R.id.textView2);
                    d.getWindow().setBackgroundDrawableResource(R.color.trans);

                    username.setText("("+NAME+")");
                    feedback.setText(PAYMENT);
                    ExportCSVTask task=new ExportCSVTask();
                    task.execute();

                    //Tap On sound
                    mp= MediaPlayer.create(OFBS.this,R.raw.ching);
                    try {
                        mp.setVolume(count,count);
                        if (mp.isPlaying())
                        {
                            mp.stop();
                            mp.release();
                            mp=MediaPlayer.create(OFBS.this,R.raw.ching);
                        }
                        mp.start();
                    }catch (Exception batLow)
                    {

                    }




                    if(d.isShowing())
                    {
                        d.dismiss();
                    }
                    else
                    {
                        d.show();

                    }
                    // Toast.makeText(this, "data found", Toast.LENGTH_LONG).show();
                    //Vregistration();
                    NAME="";
                    IF="";
                    PAYMENT="";
                    Status =null;
                    TDistance="";
                    CostKM = "";
                    new Timer().schedule(new TimerTask(){@Override public void run(){d.dismiss();  }},3100);
                }


                //tap off

                else if(result.equals("sumsuccess")){
                    //insertDB();
                    String id=Read.getText().toString();
                    Thelper.deleteData(id);




                    //final Dialog d = new Dialog(OFBS.this);
                    final Dialog d = new Dialog(OFBS.this,android.R.style.Theme_Black_NoTitleBar_Fullscreen); // Tap Off

                    d.setContentView(R.layout.valid2);
                    d.getWindow().setBackgroundDrawableResource(R.drawable.rounded_rect_bgd);
                    TextView username = (TextView) d.findViewById(R.id.textView5);
                    TextView feedback = (TextView) d.findViewById(R.id.textView2);
                    d.getWindow().setBackgroundDrawableResource(R.color.trans);

                    username.setText("("+NAME+")");
                    feedback.setText(PAYMENT);
                    ExportCSVTask task=new ExportCSVTask();
                    task.execute();

                    //Tap Off sound
                    mp= MediaPlayer.create(OFBS.this,R.raw.ching);
                    try {
                        mp.setVolume(count,count);
                        if (mp.isPlaying())
                        {
                            mp.stop();
                            mp.release();
                            mp=MediaPlayer.create(OFBS.this,R.raw.ching);
                        }
                        mp.start();
                    }catch (Exception batLow)
                    {

                    }


                    if(d.isShowing())
                    {
                        d.dismiss();
                    }
                    else
                    {
                        d.show();

                    }
                    // Toast.makeText(this, "data found", Toast.LENGTH_LONG).show();
                    //Vregistration();
                    NAME="";
                    IF="";
                    PAYMENT="";
                    Status =null;
                    TDistance="";
                    CostKM = "";
                    new Timer().schedule(new TimerTask(){@Override public void run(){d.dismiss();  }},3100);
                }
                else if(result.equals("autosuccess"))
                {



                }
                else if(result.equals("autofail"))
                {

                }

                else
                {

                    //final Dialog d = new Dialog(OFBS.this);
                    final Dialog d = new Dialog(OFBS.this,android.R.style.Theme_Black_NoTitleBar_Fullscreen);
                   // d.setContentView(R.layout.offline);
                    d.getWindow().setBackgroundDrawableResource(R.drawable.rounded_rect_bgd);
                    d.getWindow().setBackgroundDrawableResource(R.color.trans);
                    d.show();
                    new Timer().schedule(new TimerTask(){@Override public void run(){d.dismiss();}},2300);
                }
            }},600);
    }

    private void insertDB() {

        String psd = Read.getText().toString();

        boolean result = helper.insertData(psd);
        if  (result = true )
        {

            //      Toast.makeText(OFBS.this, "data saved!", Toast.LENGTH_SHORT).show();
        }
        else {

            Toast.makeText(OFBS.this, "data not saved!", Toast.LENGTH_SHORT).show();
        }

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
    public void onBackPressed()
    {
       return;
    }

    @Override
    protected void onResume() {
        super.onResume();

        registerReceiver(new NetworkStateChecker(), new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        //calling the method to load all the stored names
        //loadNames();

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


        CountTable();
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
        WriteModeOff();
    }

    public void readFromIntent(Intent intent)
    {
        String action = intent.getAction();
        if(NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)||NfcAdapter.ACTION_TECH_DISCOVERED.equals(action) ||NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action))
        {
            Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            NdefMessage[]msgs=null;
            if(rawMsgs !=null)
            {
                msgs = new NdefMessage[rawMsgs.length];
                for (int i=0;i<rawMsgs.length;i++)
                {
                    msgs[i] = (NdefMessage)rawMsgs[i];
                }
            }
            buildTagViews(msgs);
        }
    }

    public void buildTagViews(NdefMessage[]msgs)
    {
        if (msgs == null || msgs.length ==0)
            return;
        String text="";
        String tagId = new String(msgs[0].getRecords()[0].getType());
        byte[]payload=msgs[0].getRecords()[0].getPayload();
        String textEncoding = ((payload[0] & 128)==0) ? "UTF-8":"UTF-16";
        int languageCodelength = payload[0] & 0063;
        try
        {
            text = new String(payload, languageCodelength + 1, payload.length - languageCodelength -1, textEncoding);
        }catch (UnsupportedEncodingException e)
        {
            Log.e("Unsupportedencoding", e.toString());
        }
        Name.setText(text);
    }



//auto location updates


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
                                    rae.startResolutionForResult(OFBS.this, REQUEST_CHECK_SETTINGS);
                                } catch (IntentSender.SendIntentException sie) {
                                    Log.i(TAG, "PendingIntent unable to execute request.");
                                }
                                break;
                            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                String errorMessage = "Location settings are inadequate, and cannot be " +
                                        "fixed here. Fix in Settings.";
                                Log.e(TAG, errorMessage);

                                Toast.makeText(OFBS.this, errorMessage, Toast.LENGTH_LONG).show();
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

    //export csv

    public void writecsv()
    {
        File extDir = OFBS.this.getExternalFilesDir(null);
        String path = extDir.getAbsolutePath();
        String csvname="TRIP CONFIRMATION"+".csv";
        file = new File(extDir, csvname);
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);

        try {


            file.createNewFile();
            CSVWriter csvWrite = new CSVWriter(new FileWriter(file));

            long startTime = System.currentTimeMillis();
            long time = startTime;
            cal.setTimeInMillis(time);
            String date_log = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
            String time_log = android.text.format.DateFormat.format("HH:mm:ss", cal).toString();


            String tets = "Route Description,Start Point,End Point";
            String RouteD[] = {"Route Description",Route};
            csvWrite.writeNext(RouteD);

            String RouteN[] = {"Trip Number",ROUTE};
            csvWrite.writeNext(RouteN);
            String Device[] = {"Device ID",DEVICEID};
            csvWrite.writeNext(Device);

            String Driver[] = {"Driver ID",DRIVERID};
            csvWrite.writeNext(Driver);

            String Bus[] = {"Bus Number",BUSID};
            csvWrite.writeNext(Bus);

            String SE[] = {"Start time :"+StartTime+" :"," End time :"+mEndTime};
            csvWrite.writeNext(SE);


            String Start[] = {"Start Point","Lat: "+LatStartJ+", "+"Long: "+LongStartJ};
            csvWrite.writeNext(Start);






            String End[] = {"End Point","Lat: "+LatEvent+", "+"Long: "+LongEvent};
            csvWrite.writeNext(End);


            //total passengers

            //total transactions
            Cursor c = helper.Count();
            int i = 0;
            while (c.moveToNext()){
                i += 1;
            }
            String total = String.valueOf(i);
            String Total[] = {"Total Passengers",total};
            csvWrite.writeNext(Total);

            String Distance[] = {"Total Live KM",DISTANCE};
            csvWrite.writeNext(Distance);


            String TotalDistance[] = {"Dead KM", DeadKM};
            csvWrite.writeNext(TotalDistance);



            csvWrite.close();


            this.finish();

        } catch (Exception e) {

        }
    }
    private class ExportCSVTask extends AsyncTask<String, Void, Boolean> {
        //   private final ProgressDialog dialog = new ProgressDialog(OFBS.this);

        @Override
        protected void onPreExecute() {



        }

        protected Boolean doInBackground(final String... args) {





            try {
                insert();
                insert2();

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


        String mydate = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());


        String taguid = TAGUID;
        String device = DEVICEID;
        String bus = BUSID;
        String route = ROUTE;
        String driver = DRIVERID;
        String tim = Time;
        String loc = "Lat: "+LatTrans+", "+"Long: "+LongTrans;
        String status = Status;
        String totalcost = CostKM;
        String totalkm = TDistance;
        TAGUID = Read.getText().toString();




       // insertData(device,bus,route,driver,taguid,tim,loc,status,totalkm,totalcost);
        saveReportToLocalStorage(DEVICEID,BUSID,ROUTE,DRIVERID,TAGUID,Time,"Lat:"+LatTrans+","+"Long:"+LongTrans,Status,totalkm,totalcost,NAME_NOT_SYNCED_WITH_SERVER);
        //  progress.show();


    }

    /**
     * this method used to send data to server or our local server
     * @param device
     * @param bus
     *  @param route
     * @param driver
     * @param taguid
     *  @param tim
     *  @param loc
     * @param  status
     * @param  totalkmm
     * @param  totalcost
     */

    private void insertData(String device, String bus, String route,String driver, String taguid, String tim, String loc, String status, String totalkmm, String totalcost){
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<InsertDataResponseModel> call = apiService.insertData(device,bus,route,driver,taguid,tim,loc,status,totalkmm,totalcost);
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
                    Toast.makeText(OFBS.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
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


    public void insert2()
    {
        String taguid = TAGUID;
        String name = NAME;

        String amount = AMOUNT;


        insertData2(taguid,name,amount);
        //  progress.show();


    }


    /**
     * this method used to send data to server or our local server
     * @param taguid
     * @param name
     *  @param amount
     */

    private void insertData2(String taguid, String name, String amount){
        ApiService2 apiService2 = ApiClient2.getClient().create(ApiService2.class);
        Call<InsertDataResponseModel2> call = apiService2.insertData2(taguid, name, amount);
        call.enqueue(new Callback<InsertDataResponseModel2>() {
            @Override
            public void onResponse(Call<InsertDataResponseModel2> call, Response<InsertDataResponseModel2> response) {

                InsertDataResponseModel2 insertDataResponseModel2 = response.body();

                //check the status code
                if(insertDataResponseModel2.getStatus()==1){
                    //  Toast.makeText(MainActivity.this, response.body().getMessage()+"& csv updated", Toast.LENGTH_SHORT).show();
                    //  writecsv();
                    //    writeNfc();
                    //progress.dismiss();
                }else{
                    Toast.makeText(OFBS.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    //  progress.dismiss();
                }
            }

            @Override
            public void onFailure(Call<InsertDataResponseModel2> call, Throwable t) {
                //  Toast.makeText(MainActivity.this, "failed to save data, check internet connection or user is already registered ", Toast.LENGTH_SHORT).show();
                //  progress.dismiss();
            }
        });
    }

    private class ExportLocation extends AsyncTask<String, Void, Boolean> {
        //   private final ProgressDialog dialog = new ProgressDialog(OFBS.this);

        @Override
        protected void onPreExecute() {



        }

        protected Boolean doInBackground(final String... args) {





            try {
                startCustomerLocationButtonClick();

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

    private class Event2 extends AsyncTask<String, Void, Boolean> {
        //   private final ProgressDialog dialog = new ProgressDialog(OFBS.this);

        @Override
        protected void onPreExecute() {



        }

        protected Boolean doInBackground(final String... args) {





            try {
                insert4();


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
    public void insert4()
    {

        String event=null;
        String driver=null;


        driver = DRIVERID;
        String device = DEVICEID;
        String route = ROUTE;
        String loc = "Lat: "+LatEvent+", "+"Long: "+LongEvent;
        String bus = BUSID;
        event = "Stopped Journey  ";






        insertData(device,bus,route,driver,event,loc);
        //  progress.show();


    }

    private class PanicPush extends AsyncTask<String, Void, Boolean> {
        //   private final ProgressDialog dialog = new ProgressDialog(OFBS.this);

        @Override
        protected void onPreExecute() {



        }

        protected Boolean doInBackground(final String... args) {





            try {
                insertPanic();


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

    public void insertPanic()
    {

        String event=null;
        String driver=null;


        driver = DRIVERID;
        String device = DEVICEID;
        String route = ROUTE;
        String loc = "Lat: "+LatTrans+", "+"Long: "+LongTrans;
        String bus = BUSID;
        event = "Panic  ";








        insertData(device,bus,route,driver,event,loc);
        //  progress.show();


    }

    public void insert3()
    {





        String driver = DRIVERID;
        String device = DEVICEID;
        String route = ROUTE;
        String loc = "Lat: "+LatStartJ+", "+"Long: "+LongStartJ;
        String bus = BUSID;
        String event = "Started Journey  ";






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
                    Toast.makeText(OFBS.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
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







    //special methods for online-offline data syncronisation

    /*
     * this method will
     * load the names from the database
     * with updated sync status
     * */

    //updating the name to local storage : auto tap off
    private void autoupdateToLocalStorage(String name, double sum, int status) {

        boolean found= db.searchData(name);
        if (found)
        {
            double sumcost = db.sumcost;

            db.UpdateTag(name, sum, status);
            Toast.makeText(this,"autotap off complete",Toast.LENGTH_LONG).show();

        }
        else
        {
            Toast.makeText(this,"autotap off failed",Toast.LENGTH_LONG).show();

        }


    }
    //updating the name and tags to local storage: tap off
    private void updatetagsToLocalStorage(String name, double sum, int status) {

        boolean found= db.searchData(name);
        if (found)
        {
            double sumcost = db.sumcost;


                db.UpdateTag(name, sum, status);
                Toast.makeText(this,"updated",Toast.LENGTH_LONG).show();
            String id=Read.getText().toString();
            Thelper.deleteData(id);




            //final Dialog d = new Dialog(OFBS.this);
            final Dialog d = new Dialog(OFBS.this,android.R.style.Theme_Black_NoTitleBar_Fullscreen); // Tap Off

            d.setContentView(R.layout.valid2);
            d.getWindow().setBackgroundDrawableResource(R.drawable.rounded_rect_bgd);
            TextView username = (TextView) d.findViewById(R.id.textView5);
            TextView feedback = (TextView) d.findViewById(R.id.textView2);
            d.getWindow().setBackgroundDrawableResource(R.color.trans);


            insert();

            //Tap Off sound
            mp= MediaPlayer.create(OFBS.this,R.raw.ching);
            try {
                mp.setVolume(count,count);
                if (mp.isPlaying())
                {
                    mp.stop();
                    mp.release();
                    mp=MediaPlayer.create(OFBS.this,R.raw.ching);
                }
                mp.start();

            }catch (Exception batLow)
            {


            }


            if(d.isShowing())
            {
                d.dismiss();
            }
            else
            {
                d.show();

            }
            // Toast.makeText(this, "data found", Toast.LENGTH_LONG).show();
            //Vregistration();
            NAME="";
            IF="";
            PAYMENT="";
            Status =null;
            TDistance="";
            CostKM = "";
            new Timer().schedule(new TimerTask(){@Override public void run(){d.dismiss();  }},3100);





        }
        else
        {
            Toast.makeText(this,"invalid tag: tap off failed",Toast.LENGTH_LONG).show();

        }



    }
    //updating and validating the name and tags to local storage : tap on
    private void updateNameToLocalStorage(String name, double sum, int status) {

        boolean found= db.searchData(name);
        if (found)
        {
            double sumcost = db.sumcost;
           // TAGUID = db.taguid;

            if (sumcost > 19.0)
            {
                db.UpdateName(name, sum, status);
                Toast.makeText(this,"updated",Toast.LENGTH_LONG).show();

                insertDB();

                String tags=Read.getText().toString();
                double km= Distance;
                Status = "Tapped on";
                boolean result = Thelper.insertData(tags,km);
                if  (result = true )
                {
                    // Toast.makeText(Reed.this, "data saved!", Toast.LENGTH_SHORT).show();
                }
                else {

                    Toast.makeText(OFBS.this, "SAVING DB FAILED!", Toast.LENGTH_SHORT).show();
                }

                //final Dialog d = new Dialog(OFBS.this);
                final Dialog d = new Dialog(OFBS.this,android.R.style.Theme_Black_NoTitleBar_Fullscreen); // Tap On

                d.setContentView(R.layout.valid);
                d.getWindow().setBackgroundDrawableResource(R.drawable.rounded_rect_bgd);
                TextView username = (TextView) d.findViewById(R.id.textView5);
                TextView feedback = (TextView) d.findViewById(R.id.textView2);
                d.getWindow().setBackgroundDrawableResource(R.color.trans);


                insert();

                //Tap On sound
                mp= MediaPlayer.create(OFBS.this,R.raw.ching);
                try {
                    mp.setVolume(count,count);
                    if (mp.isPlaying())
                    {
                        mp.stop();
                        mp.release();
                        mp=MediaPlayer.create(OFBS.this,R.raw.ching);
                    }
                    mp.start();

                }catch (Exception batLow)
                {

                }




                if(d.isShowing())
                {
                    d.dismiss();
                }
                else
                {
                    d.show();

                }
                // Toast.makeText(this, "data found", Toast.LENGTH_LONG).show();
                //Vregistration();
                NAME="";
                IF="";
                PAYMENT="";
                Status =null;
                TDistance="";
                CostKM = "";
                new Timer().schedule(new TimerTask(){@Override public void run(){d.dismiss();  }},3100);



            }
            else {
                Toast.makeText(this, "Not enough balance", Toast.LENGTH_LONG).show();
                //Toast.makeText(this,"invalid",Toast.LENGTH_LONG).show();
                final Dialog d = new Dialog(OFBS.this,android.R.style.Theme_Black_NoTitleBar_Fullscreen);

                d.setContentView(R.layout.insuficient);
                d.getWindow().setBackgroundDrawableResource(R.drawable.rounded_rect_bgd);
                TextView username = (TextView) d.findViewById(R.id.textView2);
                TextView feedback = (TextView) d.findViewById(R.id.textView5);
                TextView tryagain = (TextView) d.findViewById(R.id.textView3);
                ImageView img = (ImageView) d.findViewById(R.id.imageview);
                ImageView img1 = (ImageView) d.findViewById(R.id.imageview1);
                d.getWindow().setBackgroundDrawableResource(R.color.trans);




                // d.show();


                if(d.isShowing())
                {
                    d.dismiss();
                }
                else
                {
                    d.show();
                }


                //warnin sound
                mp= MediaPlayer.create(OFBS.this,R.raw.beep);
                try {

                    if (mp.isPlaying())
                    {
                        mp.stop();
                        mp.release();
                        mp=MediaPlayer.create(OFBS.this,R.raw.beep);
                    }
                    mp.start();


                }catch (Exception batLow)
                {

                }
                //Toast.makeText(OFBS.this, "Dat not found ", Toast.LENGTH_LONG).show();
                new Timer().schedule(new TimerTask(){@Override public void run(){d.dismiss();}},3100);


            }



        }
        else
        {
            Toast.makeText(this,"invalid",Toast.LENGTH_LONG).show();
            final Dialog d = new Dialog(OFBS.this,android.R.style.Theme_Black_NoTitleBar_Fullscreen);

            d.setContentView(R.layout.invalid);
            d.getWindow().setBackgroundDrawableResource(R.drawable.rounded_rect_bgd);
            TextView username = (TextView) d.findViewById(R.id.textView2);
            TextView feedback = (TextView) d.findViewById(R.id.textView5);
            TextView tryagain = (TextView) d.findViewById(R.id.textView3);
            ImageView img = (ImageView) d.findViewById(R.id.imageview);
            ImageView img1 = (ImageView) d.findViewById(R.id.imageview1);
            d.getWindow().setBackgroundDrawableResource(R.color.trans);




            // d.show();


            if(d.isShowing())
            {
                d.dismiss();
            }
            else
            {
                d.show();
            }

            img.setImageResource(R.drawable.minvc);
            //warnin sound
            mp= MediaPlayer.create(OFBS.this,R.raw.beep);
            try {

                if (mp.isPlaying())
                {
                    mp.stop();
                    mp.release();
                    mp=MediaPlayer.create(OFBS.this,R.raw.beep);
                }
                mp.start();
            }catch (Exception batLow)
            {

            }
            //Toast.makeText(OFBS.this, "Dat not found ", Toast.LENGTH_LONG).show();
            new Timer().schedule(new TimerTask(){@Override public void run(){d.dismiss();}},3300);
        }





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
                    //loadNames();
                }
            };

            //registering the broadcast receiver to update sync status
            registerReceiver(broadcastReceiver, new IntentFilter(DATA_SAVED_BROADCAST));


            CountTable();
            mHandler.postDelayed(this,5000);
        }
    };

    public void CountTable()
    {
        Cursor c = db.Count();
        int i = 0;
        while (c.moveToNext()){
            i += 1;
        }

        Cursor c2 = db2.Count();
        int j = 0;
        while (c2.moveToNext()){
            j += 1;
        }

        int totals = i+j;
        String total2 = String.valueOf(totals);

        CountData=total2;
        tvCount.setText(CountData);
    }



    //saving the transaction report to local storage
    private void saveReportToLocalStorage(String Device,  String Bus, String Route,String Driver,  String Taguid, String Time,String Location,  String State, String  Totalkm,  String  Totalcost,int status) {

        db3.addName(Device, Bus, Route, Driver, Taguid, Time, Location, State, Totalkm, Totalcost, status);

    }
//catch all errors


}



