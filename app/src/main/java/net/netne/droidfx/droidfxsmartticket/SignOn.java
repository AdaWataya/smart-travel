package net.netne.droidfx.droidfxsmartticket;
import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.IntentSender;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;

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
import android.os.Looper;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
import java.util.HashMap;

import android.view.Menu;
import android.view.MenuItem;
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
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import static android.content.ContentValues.TAG;
import static android.widget.Toast.makeText;
import static java.lang.String.valueOf;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SignOn extends Activity implements AsyncResponse {

    // PillowNfcManager nfcManager;
    // WriteTagHelper writeHelper;
    //  Button Valid;

    //url from mysql
    String HttpJSonURL = "http://droidfxtec.pe.hu/mysqlsqlite/SubjectFullForm.php";
    String HttpJSonURL2 = "http://droidfxtec.pe.hu/mysqlsqlite/SubjectFullForm2.php";
    //1 means data is synced and 0 means data is not synced
    public static final int NAME_SYNCED_WITH_SERVER = 1;
    public static final int NAME_NOT_SYNCED_WITH_SERVER = 0;
    private DatabaseHelper2 db2;
    private DatabaseHelper db;
    SQLiteDatabase sqLiteDatabase;
    SQLiteDatabase sqLiteDatabase2;
    Dialog mDialog;
    EditText Taguid;
    EditText Pin;
    private static final int REQUEST_CHECK_SETTINGS = 100;
    private FusedLocationProviderClient mFusedLocationClient;
    private SettingsClient mSettingsClient;
    private LocationRequest mLocationRequest;
    private LocationSettingsRequest mLocationSettingsRequest;
    private LocationCallback mLocationCallback;
    private Location mCurrentLocation;
    private String mEndTime;
    ProgressDialog progressDialog;
    // boolean flag to toggle the ui
    private Boolean mRequestingLocationUpdates;



    String Name;
    // private static HelperClass helper;
    private static   File file=null;

    MediaPlayer mp;
    ScrollView layout;
    private View v;




    NfcAdapter nfcAdapter;
    PendingIntent pendingIntent;
    IntentFilter writeTagFilters[];
    boolean writeMode;
    Tag myTag;
    Context context;
    private String mLastUpdateTime;
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






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signon2);


       Taguid = (EditText) findViewById(R.id.tv);
       Pin= (EditText) findViewById(R.id.pin);
       //new button added
        Button Back= (Button) findViewById(R.id.button8);
        Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignOn.this, startPage.class);
                startActivity(intent);
                finish();
            }
        });



       // layout=(ScrollView) findViewById(R.id.bg);
        //  Valid= (Button) findViewById(R.id.check);
        //  Delete= (Button) findViewById(R.id.delete);
        v = findViewById(R.id.mainL);


// initialize the necessary libraries
        db2 = new DatabaseHelper2(this);
        db = new DatabaseHelper(this);

        init();

        // restore the values from saved instance state

        LocationButtonClick();
        LocationButtonClick();
        restoreValuesFromBundle(savedInstanceState);
        syncOnlinedata();

//new things


        Pin.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(Pin.getText().toString().length()==5)
                {
                    checkData3();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

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
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void Proceed()
    {
        String uid = Taguid.getText().toString();
     //   String amount = Amount.getText().toString();

        if(Name.equals("")){
            Toast.makeText(SignOn.this, "please re-read the card", Toast.LENGTH_LONG).show();
            return;
        }
        Intent intent = new Intent(this, HomeActivity.class);
        intent.putExtra("name", Name); //operator
        startActivity(intent);
        finish();
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
                Proceed();



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





    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.




        //menuItemEmulate = menu.findItem(R.id.itemEmulate);
        return true;
    }

    // Handles the user's menu selection.
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {



            default:
                return super.onOptionsItemSelected(item);
        }
        //return true;
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
                                    rae.startResolutionForResult(SignOn.this, REQUEST_CHECK_SETTINGS);
                                } catch (IntentSender.SendIntentException sie) {
                                    Log.i(TAG, "PendingIntent unable to execute request.");
                                }
                                break;
                            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                String errorMessage = "Location settings are inadequate, and cannot be " +
                                        "fixed here. Fix in Settings.";
                                Log.e(TAG, errorMessage);

                                Toast.makeText(SignOn.this, errorMessage, Toast.LENGTH_LONG).show();
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

    //Methods to fetch data from mysql
    private class StoreJSonDataInToSQLiteClass extends AsyncTask<Void, Void, Void> {

        public Context context;

        String FinalJSonResult;

        public StoreJSonDataInToSQLiteClass(Context context) {

            this.context = context;
        }

        @Override
        protected void onPreExecute() {

            super.onPreExecute();

            progressDialog = new ProgressDialog(SignOn.this);
            progressDialog.setTitle("LOADING");
            progressDialog.setMessage("Please Wait");
            progressDialog.show();

        }

        @Override
        protected Void doInBackground(Void... arg0) {

            HttpServiceClass httpServiceClass = new HttpServiceClass(HttpJSonURL);

            try {
                httpServiceClass.ExecutePostRequest();

                if (httpServiceClass.getResponseCode() == 200) {

                    FinalJSonResult = httpServiceClass.getResponse();

                    if (FinalJSonResult != null) {

                        JSONArray jsonArray = null;
                        try {

                            jsonArray = new JSONArray(FinalJSonResult);
                            JSONObject jsonObject;

                            for (int i = 0; i < jsonArray.length(); i++) {

                                jsonObject = jsonArray.getJSONObject(i);

                                String tempSubjectName = jsonObject.getString("taguid");

                                double tempSubjectFullForm = Double.parseDouble(jsonObject.getString("balance"));

                                String SQLiteDataBaseQueryHolder = "INSERT INTO "+db.TABLE_NAME+" (name,sum,status) VALUES('"+tempSubjectName+"', '"+tempSubjectFullForm+"', '"+NAME_NOT_SYNCED_WITH_SERVER+"');";

                                sqLiteDatabase.execSQL(SQLiteDataBaseQueryHolder);




                            }
                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                } else {

                    Toast.makeText(context, httpServiceClass.getErrorMessage(), Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return null;
        }



        @Override
        protected void onPostExecute(Void result)

        {

            sqLiteDatabase.close();
            progressDialog.dismiss();

            Toast.makeText(SignOn.this,"Load Done", Toast.LENGTH_LONG).show();

        }
    }

    //Methods to fetch data from mysql
    private class StoreJSonDataInToSQLiteClass2 extends AsyncTask<Void, Void, Void> {

        public Context context;

        String FinalJSonResult;

        public StoreJSonDataInToSQLiteClass2(Context context) {

            this.context = context;
        }

        @Override
        protected void onPreExecute() {

            super.onPreExecute();



        }

        @Override
        protected Void doInBackground(Void... arg0) {

            HttpServiceClass httpServiceClass = new HttpServiceClass(HttpJSonURL2);

            try {
                httpServiceClass.ExecutePostRequest();

                if (httpServiceClass.getResponseCode() == 200) {

                    FinalJSonResult = httpServiceClass.getResponse();

                    if (FinalJSonResult != null) {

                        JSONArray jsonArray = null;
                        try {

                            jsonArray = new JSONArray(FinalJSonResult);
                            JSONObject jsonObject;

                            for (int i = 0; i < jsonArray.length(); i++) {

                                jsonObject = jsonArray.getJSONObject(i);

                                String tempSubjectName = jsonObject.getString("taguid");
                                String tempSubjectOperator = jsonObject.getString("operator");

                                double tempSubjectFullForm = Double.parseDouble(jsonObject.getString("balance"));

                                String SQLiteDataBaseQueryHolder2 = "INSERT INTO "+db2.TABLE_NAME+" (name,operator,sum,status) VALUES('"+tempSubjectName+"', '"+tempSubjectOperator+"','"+tempSubjectFullForm+"', '"+NAME_NOT_SYNCED_WITH_SERVER+"');";

                                sqLiteDatabase2.execSQL(SQLiteDataBaseQueryHolder2);




                            }
                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                } else {

                    Toast.makeText(context, httpServiceClass.getErrorMessage(), Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return null;
        }



        @Override
        protected void onPostExecute(Void result)

        {

            sqLiteDatabase2.close();
            //  progressDialog.dismiss();

            Toast.makeText(SignOn.this,"Load Done", Toast.LENGTH_LONG).show();

        }
    }

    public void SQLiteDataBaseBuild(){

        sqLiteDatabase = openOrCreateDatabase(db.DB_NAME, Context.MODE_PRIVATE, null);
        sqLiteDatabase2 = openOrCreateDatabase(db2.DB_NAME, Context.MODE_PRIVATE, null);
        // sqLiteDatabase3 = openOrCreateDatabase(db2.DB_NAME, Context.MODE_PRIVATE, null);

    }

    public void SQLiteTableBuild(){

        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS "+db.TABLE_NAME+"("+db.COLUMN_ID+" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "+db.COLUMN_NAME+" VARCHAR, "+db.COLUMN_SUM+" DOUBLE,"+db.COLUMN_STATUS+" TINYINT);");
        sqLiteDatabase2.execSQL("CREATE TABLE IF NOT EXISTS "+db2.TABLE_NAME+"("+db2.COLUMN_ID+" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "+db2.COLUMN_NAME+" VARCHAR,"+db2.COLUMN_OPERATOR+" VARCHAR, "+db2.COLUMN_SUM+" DOUBLE,"+db2.COLUMN_STATUS+" TINYINT);");

    }

    public void DeletePreviousData(){

        sqLiteDatabase.execSQL("DELETE FROM "+db.TABLE_NAME+"");
        sqLiteDatabase2.execSQL("DELETE FROM "+db2.TABLE_NAME+"");

    }

    public void syncOnlinedata()
    {
        SQLiteDataBaseBuild();

        SQLiteTableBuild();

        DeletePreviousData();

        new StoreJSonDataInToSQLiteClass(SignOn.this).execute();
        new StoreJSonDataInToSQLiteClass2(SignOn.this).execute();
    }

    @Override
    public void onBackPressed()
    {
        return;
    }

}


