package net.netne.droidfx.droidfxsmartticket;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;



/**
 * Created by R$ on 9/13/2016.
 */

public class HelperClass extends SQLiteOpenHelper
{
    private static final String DB_NAME="droidfxtec";
    private  static  final int DATABASE_VERSION=4 ;
    private  static  final String TABLE_NAME="transactions";
    private  static  final String ID="Id";
    private  static  final String CPSD="Password";
    private  static  final String COLOR="Color";


    public HelperClass(Context context)
    {
        super(context,DB_NAME,null,DATABASE_VERSION);
        Log.i("Database","On Construct Method");

    }

    public Cursor Count()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor data = db.rawQuery("SELECT * FROM "+"transactions",null);
        return data;
    }
    public boolean insertData(String psd)
    {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values=new ContentValues();

        values.put(CPSD,psd);

        long result=db.insert(TABLE_NAME, null, values);
        if (result==-1)

            return false;
        else
            return true;


    }






    public void deleteAll(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM transactions");
        db.close();
    }


    public int deleteData(String psd)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        return   db.delete(TABLE_NAME, CPSD + " = ?",
                new String[] {psd});

    }
    @Override
    public void onCreate(SQLiteDatabase db) {

        String sql="CREATE TABLE "+TABLE_NAME+
                "("+ ID + "INTEGER AUTO INCREMENT, " + CPSD + " VARCHAR);";
        db.execSQL(sql);
        Log.i("Database","on create Method");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        String sql="DROP TABLE IF EXISTS"+TABLE_NAME;
        db.execSQL(sql);
        onCreate(db);
        Log.i("Database","On Upgrade Method");

    }
}