package net.netne.droidfx.droidfxsmartticket;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by R$ on 1/27/2017.
 */
public class DatabaseHelper3 extends SQLiteOpenHelper {

    //Constants for Database name, table name, and column names
    public static final String DB_NAME = "report";
    public static final String TABLE_NAME = "report";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_DEVICE = "device";
    public static final String COLUMN_BUS = "bus";
    public static final String COLUMN_ROUTE = "route";
    public static final String COLUMN_DRIVER = "driver";
    public static final String COLUMN_TAGUID = "taguid";
    public static final String COLUMN_TIME = "housing";
    public static final String COLUMN_LOCATION = "location";
    public static final String COLUMN_STATE = "state";
    public static final String COLUMN_TOTALKM = "totalkm";
    public static final String COLUMN_TOTALCOST = "totalcost";
    public static final String COLUMN_STATUS = "status";
    public    static  double sumcost=0.0;

    //database version
    private static final int DB_VERSION = 1;

    //Constructor
    public DatabaseHelper3(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    //creating the database
    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE " + TABLE_NAME + "(" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_DEVICE + " VARCHAR, " + COLUMN_BUS + " VARCHAR, " + COLUMN_ROUTE + " VARCHAR, " + COLUMN_DRIVER + " VARCHAR, " + COLUMN_TAGUID + " VARCHAR, " + COLUMN_TIME+ " VARCHAR, " + COLUMN_LOCATION + " VARCHAR, " + COLUMN_STATE + " VARCHAR, " + COLUMN_TOTALKM + " VARCHAR, " + COLUMN_TOTALCOST + " VARCHAR," + COLUMN_STATUS + " TINYINT);";
        db.execSQL(sql);
    }

    //upgrading the database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String sql = "DROP TABLE IF EXISTS report";
        db.execSQL(sql);
        onCreate(db);
    }

    /*
     * This method is taking two arguments
     * first one is the name that is to be saved
     * second one is the status
     * 0 means the name is synced with the server
     * 1 means the name is not synced with the server
     * */
    public boolean addName(String Device,  String Bus, String Route,String Driver,  String Taguid, String Time,String Location,  String State, String  Totalkm,  String  Totalcost,int status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(COLUMN_DEVICE, Device);
        contentValues.put(COLUMN_BUS, Bus);
        contentValues.put(COLUMN_ROUTE, Route);
        contentValues.put(COLUMN_DRIVER, Driver);
        contentValues.put(COLUMN_TAGUID, Taguid);
        contentValues.put(COLUMN_TIME, Time);
        contentValues.put(COLUMN_LOCATION, Location);
        contentValues.put(COLUMN_STATE, State);
        contentValues.put(COLUMN_TOTALKM, Totalkm);
        contentValues.put(COLUMN_TOTALCOST, Totalcost);
        contentValues.put(COLUMN_STATUS, status);





        db.insert(TABLE_NAME, null, contentValues);
        db.close();
        return true;
    }

    public Cursor Count()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor data = db.rawQuery("SELECT * FROM "+"report",null);
        return data;
    }
    public void deleteAll(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM names");
        db.close();
    }
    public boolean UpdateName(String name, double sum, int status) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE "+TABLE_NAME+" SET sum = sum - "+"'"+sum+"', "+ "status = "+"'"+status+"' WHERE name = "+"'"+name+"'");
        return true;
    }

    /*
     * This method taking two arguments
     * first one is the id of the name for which
     * we have to update the sync status
     * and the second one is the status that will be changed
     * */
    public boolean updateNameStatus(int id, int status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_STATUS, status);
        db.update(TABLE_NAME, contentValues, COLUMN_ID + "=" + id, null);
        db.close();
        return true;
    }

    /*
     * this method will give us all the name stored in sqlite
     * */
    public Cursor getNames() {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM " + TABLE_NAME + " ORDER BY " + COLUMN_ID + " ASC;";
        Cursor c = db.rawQuery(sql, null);
        return c;
    }

    /*
     * this method is for getting all the unsynced name
     * so that we can sync it with database
     * */
    public Cursor getUnsyncedNames() {
        SQLiteDatabase db = this.getWritableDatabase();
        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_STATUS + " = 0;";
        Cursor c = db.rawQuery(sql, null);
        return c;
    }
}
