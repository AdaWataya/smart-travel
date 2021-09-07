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

public class TapHelper extends SQLiteOpenHelper
{
    private static final String DB_NAME="tapontapoff";
    private  static  final int DATABASE_VERSION=4;
    public  static  final String TABLE_NAME="tap";
    private  static  final String ID="id";
    public  static  final String TAGS="tags";
    public   static  String CARDS=null;
    public   static  final String KMTER="km";
    public   static  int SUM = 0;
    public    static  double sum=0.0;


    public TapHelper(Context context)
    {
        super(context,DB_NAME,null,DATABASE_VERSION);
        Log.i("Database","On Construct Method");

    }
    public  boolean searchData(String tags)
    {
        SQLiteDatabase db=getReadableDatabase();
        String Columns[]={KMTER};
        String[]selectionArgs={tags};
        Cursor result=db.query(TABLE_NAME,Columns, TAGS+"=?",selectionArgs, null,null,null,null);
        boolean found=false;
        if(result.moveToNext())
        {
            found=true;
            //CARDS = result.getString(1);
            sum = result.getDouble(0);

        }
        return found;
    }

    public Cursor Count()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor data = db.rawQuery("SELECT * FROM "+"tap",null);
        return data;
    }

    public boolean insertData(String tags, double km )
    {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values=new ContentValues();

        values.put(TAGS,tags);
        values.put(KMTER,km);


        long result=db.insert(TABLE_NAME, null, values);
        if (result==-1)

            return false;
        else
            return true;


    }







    public void deleteAll(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM tap");
        db.close();
    }




    public int deleteData(String id)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        return   db.delete(TABLE_NAME, TAGS + " = ?",
                new String[] {id});

    }
    @Override
    public void onCreate(SQLiteDatabase db) {

        String sql="CREATE TABLE "+TABLE_NAME+
                "(" + TAGS + " VARCHAR, " + KMTER + " DOUBLE);";
        db.execSQL(sql);
        Log.i("Database","on create Method");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        String sql="DROP TABLE IF EXISTS tap";
        db.execSQL(sql);
        onCreate(db);
        Log.i("Database","On Upgrade Method");

    }
}