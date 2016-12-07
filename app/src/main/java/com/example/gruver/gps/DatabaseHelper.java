package com.example.gruver.gps;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by CoOk13 M0NsT3R on 12/6/2016.
 */

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "circuit.db";
    public static final String TABLE_NAME = "circuit_table";
    /*public static final String COL_1 = "ID";
    public static final String COL_2 = "NAME";
    public static final String COL_3 = "STARTLOC";
    public static final String COL_4 = "ENDLOC";*/

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
        SQLiteDatabase db = this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table "+ TABLE_NAME +
                " (ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                  "NAME TEXT," +
                  "STARTLOCLAT REAL," +
                  "STARTLOCLONG REAL," +
                  "ENDLOCLAT REAL," +
                  "ENDLOCLONG REAL)");
        db.execSQL("create table run_table " +
                   "(RID INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "ID INTEGER," +
                    "TIME INTEGER," +
                    "TOPSPEED REAL," +
                    "CANCELLED INTEGER," +
                    "FOREIGN KEY(ID) REFERENCES circuit_table(ID))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists "+TABLE_NAME);
        db.execSQL("drop table if exists run_table");
        onCreate(db);
    }

    public boolean insertCircuit(String name, String startLocLat, String startLocLong, String endLocLat, String endLocLong){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("NAME",name);
        cv.put("STARTLOCLAT",startLocLat);
        cv.put("STARTLOCLONG",startLocLong);
        cv.put("ENDLOCLAT",endLocLat);
        cv.put("ENDLOCLONG",endLocLong);
        long result = db.insert("circuit_table",null,cv);

        if(result == -1){
            return false;
        }else{
            return true;
        }
    }

    public boolean insertRun(String id,String time,String topSpeed,String cancelled){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("ID",id);
        cv.put("TIME",time);
        cv.put("TOPSPEED",topSpeed);
        cv.put("CANCELLED",cancelled);
        long result = db.insert("run_table",null,cv);

        if(result == -1){
            return false;
        }else{
            return true;
        }
    }
}
