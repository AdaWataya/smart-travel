package net.netne.droidfx.droidfxsmartticket;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.media.MediaPlayer;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import static android.widget.Toast.LENGTH_LONG;
import static android.widget.Toast.makeText;
import static java.lang.String.valueOf;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import android.content.ContentValues;
import android.net.Uri;

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
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import net.netne.droidfx.droidfxsmartticket.ApiServices.ApiService;
import net.netne.droidfx.droidfxsmartticket.ApiServices.ApiService2;
import net.netne.droidfx.droidfxsmartticket.clients.ApiClient;
import net.netne.droidfx.droidfxsmartticket.clients.ApiClient2;
import net.netne.droidfx.droidfxsmartticket.models.InsertDataResponseModel;
import net.netne.droidfx.droidfxsmartticket.models.InsertDataResponseModel2;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OB extends Activity implements AsyncResponse {

    // PillowNfcManager nfcManager;
    // WriteTagHelper writeHelper;
    //  Button Valid;
    //1 means data is synced and 0 means data is not synced
    public static final int NAME_SYNCED_WITH_SERVER = 1;
    public static final int NAME_NOT_SYNCED_WITH_SERVER = 0;
    private static final String TAG = OFBS.class.getSimpleName();
    Button Delete;
    EditText Read;
    TextView Name;
    // private static HelperClass helper;
    private static   File file=null;
    String opname;
    MediaPlayer mp;
    float count=100*.01f;
    private String mLastUpdateTime;
    ScrollView layout;
    private View v;
    TextView BtMz;
    String BTMZ="   FROM: Orange Farm\n\n   TO: Bree Street";
    ArrayList<String> customerLocation = new ArrayList<>();
    Format nfcManager;
    NfcAdapter nfcAdapter;
    PendingIntent pendingIntent;
    IntentFilter writeTagFilters[];
    boolean writeMode;
    Tag myTag;
    Context context;
    String NAME=null;
    String IF=null;
    String PAYMENT=null;
    int Cost = 10;
    String Long=null;
    String Lat=null;

    private static final int REQUEST_CHECK_SETTINGS = 100;

    TextView cval; // for cash value
    EditText Pin;
    Dialog Pass;
    String pinnumber;
    Button EnterPass;
    String TAGUID=null;
    String AMOUNT = "10";
    String DEVICEID=null;
    String DRIVERID=null;
    String ROUTEID;
    String BUSID=null;
    int DISTANCE=0;
    double TotalCost=0;
    String ROUTE=null;
    String Loc=null;
    String Time=null;
    private DatabaseHelper2 db2;
    private TapHelper Thelper;
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



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.read2);

        Read = (EditText) findViewById(R.id.editText4);
        Name = (TextView) findViewById(R.id.textView6);
        BtMz = (TextView) findViewById(R.id.textView8);
        cval = (TextView) findViewById(R.id.cvalue);
        TextView Bnumber = (TextView) findViewById(R.id.textView7);
        //layout=(ScrollView) findViewById(R.id.bg);
        //  Valid= (Button) findViewById(R.id.check);
        //  Delete= (Button) findViewById(R.id.delete);
        v = findViewById(R.id.mainL);


        //route

        //Toast.makeText(OB.this, "CASH MODE ACTIVE", Toast.LENGTH_LONG).show();
        Button Back = (Button)findViewById(R.id.back);
        // Back.setVisibility(View.INVISIBLE);
        ImageView logout = (ImageView) findViewById(R.id.imageView5);

        Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onBackPressed();

            }
        });

        // initialize the necessary libraries
        init();
        db2 = new DatabaseHelper2(this);
        // restore the values from saved instance state
        restoreValuesFromBundle(savedInstanceState);
        LocationButtonClick();
        LocationButtonClick();
        LocationButtonClick();



//new things


        String clock = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
        Time = clock;
        DEVICEID = Build.BRAND+" "+Build.MODEL+","+Build.SERIAL;
        DRIVERID = getIntent().getStringExtra("name");
        BUSID = getIntent().getStringExtra("bus");
        ROUTE = getIntent().getStringExtra("route");
        DISTANCE = Integer.parseInt(getIntent().getStringExtra("distance"));
        helper=new HelperClass(this);
        helper.getWritableDatabase();
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        //   nfcAdapter.enableForegroundDispatch(this, pendingIntent, new IntentFilter[]{filter}, this.techList);
        BtMz.setText("ACTIVE TRIP#: "+ROUTE);
        Name.setText("DRIVER: "+DRIVERID);
        Bnumber.setText("ACTIVE BUS#: "+BUSID);
        if (nfcAdapter == null) {
            Toast.makeText(this, "this device dont support NFC", Toast.LENGTH_LONG).show();
            finish();
        }
        //  readFromIntent(getIntent());
        pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        tagDetected.addCategory(Intent.CATEGORY_DEFAULT);
        writeTagFilters = new IntentFilter[]{tagDetected};





        //new method to calculate totalcost
        double totalcost = 0.0;
        double costperkm = 200.00;
        int dist = DISTANCE;
        totalcost = 10000-(dist*costperkm);
        TotalCost = totalcost;

        cval.setText("MK"+TotalCost); //To display cost on Init
/*
//snackbar

        final Snackbar snackbar = Snackbar.make(v,"                             TRIP COST : " +  String.valueOf(TotalCost), Snackbar.LENGTH_INDEFINITE);

        String dismis = "dismiss";

        snackbar.setAction("dismiss", new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {


                snackbar.dismiss();
            }
        });
        snackbar.show();
        */

        //driver password for cash ticket...dialog

        EnterPass = (Button) findViewById(R.id.button9);
        EnterPass.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                pinDialog();
            }


        });

    }

    //method for pin dialog

    public void pinDialog()
    {
        Pass = new Dialog(this);
        Pass.setContentView(R.layout.driver_pin);
        Pass.getWindow().setBackgroundDrawableResource(R.drawable.mlog);
        Pin= (EditText) Pass.findViewById(R.id.editText);
        Pin.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(Pin.getText().toString().length()==5)
                {
                    checkData3();
                    Pass.dismiss();


                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        Pass.show();
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
            Long = String.valueOf(longitude);
            Lat = String.valueOf(latitude);


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
                                    rae.startResolutionForResult(OB.this, REQUEST_CHECK_SETTINGS);
                                } catch (IntentSender.SendIntentException sie) {
                                    Log.i(TAG, "PendingIntent unable to execute request.");
                                }
                                break;
                            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                String errorMessage = "Location settings are inadequate, and cannot be " +
                                        "fixed here. Fix in Settings.";
                                Log.e(TAG, errorMessage);

                                Toast.makeText(OB.this, errorMessage, Toast.LENGTH_LONG).show();
                        }

                        customerLocate();

                    }
                });
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
                                    rae.startResolutionForResult(OB.this, REQUEST_CHECK_SETTINGS);
                                } catch (IntentSender.SendIntentException sie) {
                                    Log.i(TAG, "PendingIntent unable to execute request.");
                                }
                                break;
                            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                String errorMessage = "Location settings are inadequate, and cannot be " +
                                        "fixed here. Fix in Settings.";
                                Log.e(TAG, errorMessage);

                                Toast.makeText(OB.this, errorMessage, Toast.LENGTH_LONG).show();
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

    @Override
    protected void onNewIntent(Intent intent)
    {
        setIntent(intent);
        //GetDataFromTag(tag, intent);
        if(NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction()))
        {
            myTag=intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            // GetDataFromTag(myTag, intent);
            ((TextView)findViewById(R.id.editText4)).setText(this.ByteArrayToHexString(getIntent().getByteArrayExtra(NfcAdapter.EXTRA_ID)));
        }
      //checkData();
        readData();

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
    public void readData(){




        final String name = Read.getText().toString();
        final double sum = TotalCost;
        // saveNameToServer();
        updateNameToLocalStorage(name,sum, NAME_NOT_SYNCED_WITH_SERVER);

    }
    public void readData2(){



        HashMap postData=new HashMap();
        postData.put("txtUsername", Pin.getText().toString());
        postData.put("totalcost", String.valueOf(TotalCost));

        PostResponseAsyncTask task = new PostResponseAsyncTask(this, postData);
        task.execute("http://droidfx.pe.hu/bproject/OB.php");

    }

    // method to insert data into database

    public void checkData3(){
        final String name = Pin.getText().toString();
        final double sum = TotalCost;
        // saveNameToServer();
        updateNameToLocalStorage2(name,sum, NAME_NOT_SYNCED_WITH_SERVER);

    }

    //delete
    public void deleteData(){
        HashMap postData=new HashMap();
        postData.put("txtUsername", Read.getText().toString());

        PostResponse task = new PostResponse(this,postData);
        task.execute("http://droidfx.pe.hu/bproject/delete.php");
    }
    public void checkData(){
        HashMap postData=new HashMap();
        postData.put("txtUsername", Read.getText().toString());

        PostResponseAsyncTask2 task = new PostResponseAsyncTask2(this, postData);
        task.execute("http://droidfx.pe.hu/bproject/check_operator.php");

        OB.ExportLocation tas=new OB.ExportLocation();
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
                final String name = json_data.getString("operator");
                final String taguid = json_data.getString("taguid");
                String FUNDS = "!INSUFFICIENT FUNDS!";
                String COMPLETE = "THANK YOU FOR CASH PAYMENT:";


                //  final Snackbar snackbar = Snackbar.make(v,"NEW BALANCE :  "+"R"+balance+",00", Snackbar.LENGTH_INDEFINITE);
                NAME=name;
                TAGUID = taguid;
                IF = FUNDS;
                PAYMENT = COMPLETE;


                readData2();

                Pass.dismiss();



            }
        }
        catch(JSONException e)
        {

        }

    }

    @Override
    public void processPin(String result) {
        try {

            // dialog();
            JSONArray jArray = new JSONArray(result);
            int flag = 1;

            // Extract data from json and store into ArrayList as class objects
            for (int i = 0; i < jArray.length(); i++) {
                JSONObject json_data = jArray.getJSONObject(i);

                final String balance = json_data.getString("balance");
                final String name = json_data.getString("operator");
                final String taguid = json_data.getString("taguid");
                String FUNDS = "!INSUFFICIENT FUNDS!";
                String COMPLETE = "THANK YOU FOR CASH PAYMENT:";


                final Snackbar snackbar = Snackbar.make(v,"NEW BALANCE :  "+"R"+balance+",00", Snackbar.LENGTH_INDEFINITE);
                NAME=name;
                TAGUID = taguid;
                IF = FUNDS;
                PAYMENT = COMPLETE;
                snackbar.setAction("dismiss", new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {

                        startActivity(new Intent(OB.this,HomeActivity.class));
                        finish();
                        snackbar.dismiss();
                    }
                });
                snackbar.show();



            }
        }
        catch(JSONException e)
        {

        }


    }


    @Override
    public void processFinish(String result) {

        if(result.equals("no rows")) {
            // checkData2();
            //fetch data


            //final Dialog d = new Dialog(this);
            final Dialog d = new Dialog(this,android.R.style.Theme_Black_NoTitleBar_Fullscreen);
            d.setContentView(R.layout.invalid);
            d.getWindow().setBackgroundDrawableResource(R.drawable.rounded_rect_bgd);
            TextView username = (TextView) d.findViewById(R.id.textView2);
            TextView feedback = (TextView) d.findViewById(R.id.textView5);
            ImageView img = (ImageView) d.findViewById(R.id.imageview);
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
                new Timer().schedule(new TimerTask(){@Override public void run(){d.dismiss();}},3300);
                //warnin sound
                mp= MediaPlayer.create(this,R.raw.beep);
                try {

                    if (mp.isPlaying())
                    {
                        mp.stop();
                        mp.release();
                        mp=MediaPlayer.create(this,R.raw.beep);
                    }
                    mp.start();
                }catch (Exception batLow)
                {

                }
                return;
            }
            else {username.setText("NO PAYMENT:");}
            NAME="";
            IF="";
            //warnin sound
            mp= MediaPlayer.create(OB.this,R.raw.beep);
            try {

                if (mp.isPlaying())
                {
                    mp.stop();
                    mp.release();
                    mp=MediaPlayer.create(OB.this,R.raw.beep);
                }
                mp.start();
            }catch (Exception batLow)
            {

            }
            //Toast.makeText(OFBS.this, "Dat not found ", Toast.LENGTH_LONG).show();
             //this.finish();
            new Timer().schedule(new TimerTask(){@Override public void run(){d.dismiss(); finish();}},3100);
        }
        else if(result.equals("success")){
            insertDB();



           final Dialog d = new Dialog(this);

            //final Dialog d = new Dialog(this,android.R.style.Theme_Black_NoTitleBar_Fullscreen);

            d.setContentView(R.layout.cash_payment);
            d.getWindow().setBackgroundDrawableResource(R.drawable.rounded_rect_bgd);
            TextView username = (TextView) d.findViewById(R.id.textView5);
            TextView feedback = (TextView) d.findViewById(R.id.textView2);
            d.getWindow().setBackgroundDrawableResource(R.color.trans);

            //
            username.setText("("+NAME+")");
            feedback.setText(PAYMENT);
            OB.ExportCSVTask task=new OB.ExportCSVTask();
            task.execute();

            //Cash sound
            mp= MediaPlayer.create(this,R.raw.ching);
            try {
                mp.setVolume(count,count);
                if (mp.isPlaying())
                {
                    mp.stop();
                    mp.release();
                    mp=MediaPlayer.create(this,R.raw.ching);
                }
                mp.start();
            }catch (Exception batLow)
            {

            }

            new Timer().schedule(new TimerTask(){@Override public void run(){d.dismiss(); finish();}},3100);



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
            //  this.finish();


        }


        else
        {
            //final Dialog d = new Dialog(this);
            final Dialog d = new Dialog(this,android.R.style.Theme_Black_NoTitleBar_Fullscreen);

        //    d.setContentView(R.layout.offline);
            d.getWindow().setBackgroundDrawableResource(R.drawable.rounded_rect_bgd);
            d.getWindow().setBackgroundDrawableResource(R.color.trans);
            d.show();
        }

    }
    private void insertDB() {

        String psd = Read.getText().toString();

        boolean result = helper.insertData(psd);
        if  (result = true )
        {

            // Toast.makeText(OB.this, "data saved!", Toast.LENGTH_SHORT).show();
        }
        else {

            Toast.makeText(OB.this, "data not saved!", Toast.LENGTH_SHORT).show();
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

    public void writeNew()
    {
        String text = String.valueOf(Read.getText());
        //  writeHelper.writeText(text);

    }
    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        // startActivity(new Intent(OB.this,HomeAct.class));
        this.finish();
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


    private void loadSavedPreferences() {
        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(this);

        String name = sp.getString("NAME","");

        opname = name;
    }


    private void savePreferences(String key, String value) {
        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(this);
        SharedPreferences.Editor edit = sp.edit();
        edit.putString(key, value);
        edit.commit();
    }

    private class ExportCSVTask extends AsyncTask<String, Void, Boolean> {
        //   private final ProgressDialog dialog = new ProgressDialog(OFBS.this);

        @Override
        protected void onPreExecute() {



        }

        protected Boolean doInBackground(final String... args) {





            try {
                //    insert();
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


    /*public void insert()
    {

        String mydate = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());


        String taguid = TAGUID;
        String device = DEVICEID;
        String bus = BUSID;
        String route = ROUTE;
        String driver = DRIVERID;
        String tim = Time;
        String loc = "Lat: "+Lat+", "+"Long: "+Long;





        insertData(device,bus,route,driver,taguid,tim,loc);
        //  progress.show();


    }


     * this method used to send data to server or our local server
     * @param device
     * @param bus
     *  @param route
     * @param driver
     * @param taguid
     *  @param tim
     *  @param loc


    private void insertData(String device, String bus, String route,String driver, String taguid, String tim, String loc){
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<InsertDataResponseModel> call = apiService.insertData(device,bus,route,driver,taguid,tim,loc);
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
                    Toast.makeText(OB.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
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

*/
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
                    Toast.makeText(OB.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
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
    //updating and validating the name and tags to local storage : tap on
    private void updateNameToLocalStorage(String name, double sum, int status) {

        boolean found= db2.searchData(name);
        if (found)
        {
            double sumcost = db2.sumcost;
            TAGUID = db2.taguid;

            if (sumcost > TotalCost)
            {
                db2.UpdateName(name, sum, status);
                Toast.makeText(this,"updated",Toast.LENGTH_LONG).show();

                insertDB();



                final Dialog d = new Dialog(this);

                //final Dialog d = new Dialog(this,android.R.style.Theme_Black_NoTitleBar_Fullscreen);

                d.setContentView(R.layout.cash_payment);
                d.getWindow().setBackgroundDrawableResource(R.drawable.rounded_rect_bgd);
                TextView username = (TextView) d.findViewById(R.id.textView5);
                TextView feedback = (TextView) d.findViewById(R.id.textView2);
                d.getWindow().setBackgroundDrawableResource(R.color.trans);

                //
                username.setText("("+NAME+")");
                feedback.setText(PAYMENT);
                OB.ExportCSVTask task=new OB.ExportCSVTask();
                task.execute();

                //Cash sound
                mp= MediaPlayer.create(this,R.raw.ching);
                try {
                    mp.setVolume(count,count);
                    if (mp.isPlaying())
                    {
                        mp.stop();
                        mp.release();
                        mp=MediaPlayer.create(this,R.raw.ching);
                    }
                    mp.start();
                }catch (Exception batLow)
                {

                }

                new Timer().schedule(new TimerTask(){@Override public void run(){d.dismiss(); finish();}},3100);



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
                //  this.finish();

            }
            else {
                Toast.makeText(this, "Not enough balance", Toast.LENGTH_LONG).show();
                //Toast.makeText(this,"invalid",Toast.LENGTH_LONG).show();
                final Dialog d = new Dialog(OB.this,android.R.style.Theme_Black_NoTitleBar_Fullscreen);

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
                mp= MediaPlayer.create(OB.this,R.raw.beep);
                try {

                    if (mp.isPlaying())
                    {
                        mp.stop();
                        mp.release();
                        mp=MediaPlayer.create(OB.this,R.raw.beep);
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
            final Dialog d = new Dialog(OB.this,android.R.style.Theme_Black_NoTitleBar_Fullscreen);

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
            mp= MediaPlayer.create(OB.this,R.raw.beep);
            try {

                if (mp.isPlaying())
                {
                    mp.stop();
                    mp.release();
                    mp=MediaPlayer.create(OB.this,R.raw.beep);
                }
                mp.start();
            }catch (Exception batLow)
            {

            }
            //Toast.makeText(OFBS.this, "Dat not found ", Toast.LENGTH_LONG).show();
            new Timer().schedule(new TimerTask(){@Override public void run(){d.dismiss();}},3300);
        }





    }



    //for pin dialog
    //updating and validating the name and tags to local storage : tap on
    private void updateNameToLocalStorage2(String name, double sum, int status) {

        boolean found= db2.searchData(name);
        if (found)
        {
            double sumcost = db2.sumcost;
            TAGUID = db2.taguid;

            if (sumcost > TotalCost)
            {
                db2.UpdateName(name, sum, status);
                Toast.makeText(this,"updated",Toast.LENGTH_LONG).show();

                insertDB();



                final Dialog d = new Dialog(this);

                //final Dialog d = new Dialog(this,android.R.style.Theme_Black_NoTitleBar_Fullscreen);

                d.setContentView(R.layout.cash_payment);
                d.getWindow().setBackgroundDrawableResource(R.drawable.rounded_rect_bgd);
                TextView username = (TextView) d.findViewById(R.id.textView5);
                TextView feedback = (TextView) d.findViewById(R.id.textView2);
                d.getWindow().setBackgroundDrawableResource(R.color.trans);

                //
                username.setText("("+NAME+")");
                feedback.setText(PAYMENT);
                OB.ExportCSVTask task=new OB.ExportCSVTask();
                task.execute();

                //Cash sound
                mp= MediaPlayer.create(this,R.raw.ching);
                try {
                    mp.setVolume(count,count);
                    if (mp.isPlaying())
                    {
                        mp.stop();
                        mp.release();
                        mp=MediaPlayer.create(this,R.raw.ching);
                    }
                    mp.start();
                }catch (Exception batLow)
                {

                }

                new Timer().schedule(new TimerTask(){@Override public void run(){d.dismiss(); finish();}},3100);



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
                //  this.finish();

            }
            else {
                Toast.makeText(this, "Not enough balance", Toast.LENGTH_LONG).show();
                //Toast.makeText(this,"invalid",Toast.LENGTH_LONG).show();
                final Dialog d = new Dialog(OB.this,android.R.style.Theme_Black_NoTitleBar_Fullscreen);

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
                mp= MediaPlayer.create(OB.this,R.raw.beep);
                try {

                    if (mp.isPlaying())
                    {
                        mp.stop();
                        mp.release();
                        mp=MediaPlayer.create(OB.this,R.raw.beep);
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
            final Dialog d = new Dialog(OB.this,android.R.style.Theme_Black_NoTitleBar_Fullscreen);

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
            mp= MediaPlayer.create(OB.this,R.raw.beep);
            try {

                if (mp.isPlaying())
                {
                    mp.stop();
                    mp.release();
                    mp=MediaPlayer.create(OB.this,R.raw.beep);
                }
                mp.start();
            }catch (Exception batLow)
            {

            }
            //Toast.makeText(OFBS.this, "Dat not found ", Toast.LENGTH_LONG).show();
            new Timer().schedule(new TimerTask(){@Override public void run(){d.dismiss();}},3300);
        }





    }

}



