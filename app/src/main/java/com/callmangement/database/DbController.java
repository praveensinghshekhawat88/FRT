package com.callmangement.database;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.http.DELETE;

public class DbController extends SQLiteOpenHelper {
    private Context context;
    private static final int DATABASE_VERSION = 8;
    private static final String DATABASE_NAME = "ePDS_FRT.db";
    private static final String TABLE_MARK_ATTENDANCE = "tableMarkAttendance";
    private static final String TABLE_LOCATION = "tableLocation";

    public DbController(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String CREATE_TABLE_MARK_ATTENDANCE = "create table if not exists " + TABLE_MARK_ATTENDANCE +"(user_id TEXT,latitude TEXT,longitude TEXT, address TEXT, punch_in_date TEXT,punch_in_time TEXT,latitude_out TEXT,longitude_out TEXT, address_out TEXT,punch_out_date TEXT,punch_out_time TEXT)";
        String CREATE_TABLE_LOCATION = "create table if not exists " + TABLE_LOCATION +"(user_id TEXT,district_id TEXT, latitude TEXT, longitude TEXT, address TEXT, location_date_time TEXT)";

        sqLiteDatabase.execSQL(CREATE_TABLE_MARK_ATTENDANCE);
        sqLiteDatabase.execSQL(CREATE_TABLE_LOCATION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " +TABLE_MARK_ATTENDANCE);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " +TABLE_LOCATION);
        onCreate(sqLiteDatabase);
    }

    public boolean insertMarkAttendance(String user_id,String latitude,String longitude,String address,String punch_in_date,String punch_in_time,String latitude_out,String longitude_out,String address_out,String punch_out_date,String punch_out_time,String attendance_type) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("user_id", user_id);
            values.put("latitude", latitude);
            values.put("longitude", longitude);
            values.put("address", address);
            values.put("punch_in_date", punch_in_date);
            values.put("punch_in_time", punch_in_time);
            values.put("latitude_out", latitude_out);
            values.put("longitude_out", longitude_out);
            values.put("address_out", address_out);
            values.put("punch_out_date", punch_out_date);
            values.put("punch_out_time", punch_out_time);
            db.insert(TABLE_MARK_ATTENDANCE, null, values);
            db.close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean insertLocation(String user_id,String district_id,String latitude,String longitude,String address,String location_date_time) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("user_id", user_id);
            values.put("district_id", district_id);
            values.put("latitude", latitude);
            values.put("longitude", longitude);
            values.put("address", address);
            values.put("location_date_time", location_date_time);
            db.insert(TABLE_LOCATION, null, values);
            db.close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean checkMarkAttendance(String userid, String currentDate){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM "+TABLE_MARK_ATTENDANCE;
        Cursor data = db.rawQuery(query, null);
        boolean attendanceFlag = false;
        if (data!=null) {
            if (data.moveToFirst()) {
                do {
                    String user_id = data.getString(0);
                    String punch_in_date = data.getString(4);
                    if (user_id.equals(userid) && punch_in_date.equalsIgnoreCase(currentDate)) {
                        attendanceFlag = true;
                        break;
                    } else {
                        attendanceFlag = false;
                    }
                } while (data.moveToNext());
            }
        }
        return attendanceFlag;
    }

    public Cursor getAttendanceData(){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM "+TABLE_MARK_ATTENDANCE;
        Cursor data = db.rawQuery(query, null);
        return data;
    }

    public void deleteAttendanceData(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from "+TABLE_MARK_ATTENDANCE);
    }

    public Cursor getLocationData(){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM "+TABLE_LOCATION;
        Cursor data = db.rawQuery(query, null);
        return data;
    }

    public void deleteLocationData(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from "+TABLE_LOCATION);
    }

}
