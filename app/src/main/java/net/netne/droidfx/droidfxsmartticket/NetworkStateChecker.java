package net.netne.droidfx.droidfxsmartticket;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by R$ on 1/27/2017.
 */

public class NetworkStateChecker extends BroadcastReceiver {

    //context and database helper object
    private Context context;
    private DatabaseHelper db;
    private DatabaseHelper2 db2;
    private DatabaseHelper3 db3;



    @Override
    public void onReceive(Context context, Intent intent) {

        this.context = context;

        db = new DatabaseHelper(context);
        db2 = new DatabaseHelper2(context);
        db3 = new DatabaseHelper3(context);

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        //if there is a network
        if (activeNetwork != null) {
            //if connected to wifi or mobile data plan
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI || activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {



                //getting all the unsynced names
                Cursor cursor = db.getUnsyncedNames();
                if (cursor.moveToFirst()) {
                    do {
                        //calling the method to save the unsynced name to MySQL
                        saveName(
                                cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_ID)),
                                cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_NAME)),
                                cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.COLUMN_SUM))
                        );
                    } while (cursor.moveToNext());
                    cursor.close();
                    db.close();
                }


                //getting all the unsynced names
                Cursor cursor2 = db2.getUnsyncedNames();
                if (cursor2.moveToFirst()) {
                    do {
                        //calling the method to save the unsynced name to MySQL
                        saveName2(
                                cursor2.getInt(cursor2.getColumnIndex(DatabaseHelper2.COLUMN_ID)),
                                cursor2.getString(cursor2.getColumnIndex(DatabaseHelper2.COLUMN_NAME)),
                                cursor2.getDouble(cursor2.getColumnIndex(DatabaseHelper2.COLUMN_SUM))
                        );
                    } while (cursor2.moveToNext());
                    cursor2.close();
                    db2.close();
                }


                Cursor cursor3 = db3.getUnsyncedNames();
                if (cursor3.moveToFirst()) {
                    do {
                        //calling the method to save the unsynced name to MySQL
                        saveName3(
                                cursor3.getInt(cursor3.getColumnIndex(DatabaseHelper3.COLUMN_ID)),
                                cursor3.getString(cursor3.getColumnIndex(DatabaseHelper3.COLUMN_DEVICE)),
                                cursor3.getString(cursor3.getColumnIndex(DatabaseHelper3.COLUMN_BUS)),
                                cursor3.getString(cursor3.getColumnIndex(DatabaseHelper3.COLUMN_ROUTE)),
                                cursor3.getString(cursor3.getColumnIndex(DatabaseHelper3.COLUMN_DRIVER)),
                                cursor3.getString(cursor3.getColumnIndex(DatabaseHelper3.COLUMN_TAGUID)),
                                cursor3.getString(cursor3.getColumnIndex(DatabaseHelper3.COLUMN_TIME)),
                                cursor3.getString(cursor3.getColumnIndex(DatabaseHelper3.COLUMN_LOCATION)),
                                cursor3.getString(cursor3.getColumnIndex(DatabaseHelper3.COLUMN_STATE)),
                                cursor3.getString(cursor3.getColumnIndex(DatabaseHelper3.COLUMN_TOTALKM)),
                                cursor3.getString(cursor3.getColumnIndex(DatabaseHelper3.COLUMN_TOTALCOST))
                        );
                    } while (cursor3.moveToNext());
                    cursor3.close();
                    db3.close();
                }
            }

            }



        }


    /*
     * method taking two arguments
     * name that is to be saved and id of the name from SQLite
     * if the name is successfully sent
     * we will update the status as synced in SQLite
     * */
    private void saveName(final int id, final String name, final double sum) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, OFBS.URL_SAVE_NAME,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            if (!obj.getBoolean("error")) {
                                //updating the status in sqlite
                                db.updateNameStatus(id, OFBS.NAME_SYNCED_WITH_SERVER);

                                //sending the broadcast to refresh the list
                                context.sendBroadcast(new Intent(OFBS.DATA_SAVED_BROADCAST));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("name", name);
                params.put("sum", String.valueOf(sum));
                return params;
            }
        };

        VolleySingleton.getInstance(context).addToRequestQueue(stringRequest);
    }


    private void saveName2(final int id, final String name, final double sum) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, OFBS.URL_SAVE_NAME2,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            if (!obj.getBoolean("error")) {
                                //updating the status in sqlite
                                db2.updateNameStatus(id, OFBS.NAME_SYNCED_WITH_SERVER);

                                //sending the broadcast to refresh the list
                                context.sendBroadcast(new Intent(OFBS.DATA_SAVED_BROADCAST));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("name", name);
                params.put("sum", String.valueOf(sum));
                return params;
            }
        };

        VolleySingleton.getInstance(context).addToRequestQueue(stringRequest);
    }
    private void saveName3(final int id, final String device, final String bus,final String route,final String driver,final String taguid,final String time,final String location,final String state,final String totalkm, final String totalcost) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, OFBS.URL_SAVE_NAME3,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            if (!obj.getBoolean("error")) {
                                //updating the status in sqlite
                                db3.updateNameStatus(id, OFBS.NAME_SYNCED_WITH_SERVER);

                                //sending the broadcast to refresh the list
                                context.sendBroadcast(new Intent(OFBS.DATA_SAVED_BROADCAST));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("id", String.valueOf(id));
                params.put("device", device);
                params.put("bus", bus);
                params.put("route", route);
                params.put("driver", driver);
                params.put("taguid", taguid);
                params.put("time",time);
                params.put("location", location);
                params.put("status", state);
                params.put("totalkm", totalkm);
                params.put("totalcost", totalcost);
                return params;
            }
        };

        VolleySingleton.getInstance(context).addToRequestQueue(stringRequest);
    }

}
