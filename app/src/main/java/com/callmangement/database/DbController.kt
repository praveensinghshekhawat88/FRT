package com.callmangement.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DbController(context: Context?) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    private val context: Context? = null
    override fun onCreate(sqLiteDatabase: SQLiteDatabase) {
        val CREATE_TABLE_MARK_ATTENDANCE =
            "create table if not exists " + TABLE_MARK_ATTENDANCE + "(user_id TEXT,latitude TEXT,longitude TEXT, address TEXT, punch_in_date TEXT,punch_in_time TEXT,latitude_out TEXT,longitude_out TEXT, address_out TEXT,punch_out_date TEXT,punch_out_time TEXT)"
        val CREATE_TABLE_LOCATION =
            "create table if not exists " + TABLE_LOCATION + "(user_id TEXT,district_id TEXT, latitude TEXT, longitude TEXT, address TEXT, location_date_time TEXT)"

        sqLiteDatabase.execSQL(CREATE_TABLE_MARK_ATTENDANCE)
        sqLiteDatabase.execSQL(CREATE_TABLE_LOCATION)
    }

    override fun onUpgrade(sqLiteDatabase: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_MARK_ATTENDANCE)
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_LOCATION)
        onCreate(sqLiteDatabase)
    }

    fun insertMarkAttendance(
        user_id: String?,
        latitude: String?,
        longitude: String?,
        address: String?,
        punch_in_date: String?,
        punch_in_time: String?,
        latitude_out: String?,
        longitude_out: String?,
        address_out: String?,
        punch_out_date: String?,
        punch_out_time: String?,
        attendance_type: String?
    ): Boolean {
        try {
            val db = this.writableDatabase
            val values = ContentValues()
            values.put("user_id", user_id)
            values.put("latitude", latitude)
            values.put("longitude", longitude)
            values.put("address", address)
            values.put("punch_in_date", punch_in_date)
            values.put("punch_in_time", punch_in_time)
            values.put("latitude_out", latitude_out)
            values.put("longitude_out", longitude_out)
            values.put("address_out", address_out)
            values.put("punch_out_date", punch_out_date)
            values.put("punch_out_time", punch_out_time)
            db.insert(TABLE_MARK_ATTENDANCE, null, values)
            db.close()
            return true
        } catch (e: Exception) {
            return false
        }
    }

    fun insertLocation(
        user_id: String?,
        district_id: String?,
        latitude: String?,
        longitude: String?,
        address: String?,
        location_date_time: String?
    ): Boolean {
        try {
            val db = this.writableDatabase
            val values = ContentValues()
            values.put("user_id", user_id)
            values.put("district_id", district_id)
            values.put("latitude", latitude)
            values.put("longitude", longitude)
            values.put("address", address)
            values.put("location_date_time", location_date_time)
            db.insert(TABLE_LOCATION, null, values)
            db.close()
            return true
        } catch (e: Exception) {
            return false
        }
    }

    fun checkMarkAttendance(userid: String, currentDate: String?): Boolean {
        val db = this.writableDatabase
        val query = "SELECT * FROM " + TABLE_MARK_ATTENDANCE
        val data = db.rawQuery(query, null)
        var attendanceFlag = false
        if (data != null) {
            if (data.moveToFirst()) {
                do {
                    val user_id = data.getString(0)
                    val punch_in_date = data.getString(4)
                    if (user_id == userid && punch_in_date.equals(currentDate, ignoreCase = true)) {
                        attendanceFlag = true
                        break
                    } else {
                        attendanceFlag = false
                    }
                } while (data.moveToNext())
            }
        }
        return attendanceFlag
    }

    val attendanceData: Cursor
        get() {
            val db = this.writableDatabase
            val query = "SELECT * FROM " + TABLE_MARK_ATTENDANCE
            val data = db.rawQuery(query, null)
            return data
        }

    fun deleteAttendanceData() {
        val db = this.writableDatabase
        db.execSQL("delete from " + TABLE_MARK_ATTENDANCE)
    }

    val locationData: Cursor
        get() {
            val db = this.writableDatabase
            val query = "SELECT * FROM " + TABLE_LOCATION
            val data = db.rawQuery(query, null)
            return data
        }

    fun deleteLocationData() {
        val db = this.writableDatabase
        db.execSQL("delete from " + TABLE_LOCATION)
    }

    companion object {
        private const val DATABASE_VERSION = 8
        private const val DATABASE_NAME = "ePDS_FRT.db"
        private const val TABLE_MARK_ATTENDANCE = "tableMarkAttendance"
        private const val TABLE_LOCATION = "tableLocation"
    }
}
