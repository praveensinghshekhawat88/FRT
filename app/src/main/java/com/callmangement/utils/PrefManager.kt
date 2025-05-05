package com.callmangement.utils

import android.content.Context
import android.content.SharedPreferences

class PrefManager(private val _context: Context) {
    private val pref: SharedPreferences
    private val editor: SharedPreferences.Editor

    init {
        pref = _context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        editor = pref.edit()
    }

    var userCurrentlat: String?
        /*public static void hideSoftKeyboard(Activity activity) {
                final InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
                if (inputMethodManager.isActive()) {
                    if (activity.getCurrentFocus() != null) {
                        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
                    }
                }
            }*/
        get() = pref.getString(USER_CURRENTLAT, "")
        set(currentLat) {
            editor.putString(USER_CURRENTLAT, currentLat)
            editor.apply()
        }

    var userCurrentlong: String?
        get() = pref.getString(USER_CURRENTLONG, "")
        set(currentLong) {
            editor.putString(USER_CURRENTLONG, currentLong)
            editor.apply()
        }

    var userCurrenttime: String?
        get() = pref.getString(USER_CURRENTTIME, "")
        set(currentTime) {
            editor.putString(USER_CURRENTTIME, currentTime)
            editor.apply()
        }

    var userAddress: String?
        get() = pref.getString(USER_ADDRESS, "")
        set(address) {
            editor.putString(USER_ADDRESS, address)
            editor.apply()
        }

    var useR_PunchIn: String?
        get() = pref.getString(USER_PunchIn, "false")
        set(PunchIn) {
            editor.putString(USER_PunchIn, PunchIn)
            editor.apply()
        }

    var useR_PunchInTime: String?
        get() = pref.getString(USER_PunchInTime, "")
        set(user_punchInTime) {
            editor.putString(USER_PunchInTime, user_punchInTime)
            editor.apply()
        }

    var useR_PunchOutTime: String?
        get() = pref.getString(USER_PunchOutTime, "")
        set(user_punchOutTime) {
            editor.putString(USER_PunchOutTime, user_punchOutTime)
            editor.apply()
        }

    var useR_PunchInLatitude: String?
        get() = pref.getString(USER_PunchInLatitude, "")
        set(user_punchInLatitude) {
            editor.putString(USER_PunchInLatitude, user_punchInLatitude)
            editor.apply()
        }

    var useR_PunchInLongitude: String?
        get() = pref.getString(USER_PunchInLongitude, "")
        set(user_punchInLongitude) {
            editor.putString(USER_PunchInLongitude, user_punchInLongitude)
            editor.apply()
        }

    var useR_PunchOutLatitude: String?
        get() = pref.getString(USER_PunchOutLatitude, "")
        set(user_punchOutLatitude) {
            editor.putString(USER_PunchOutLatitude, user_punchOutLatitude)
            editor.apply()
        }

    var useR_PunchOutLongitude: String?
        get() = pref.getString(USER_PunchOutLongitude, "")
        set(user_punchOutLongitude) {
            editor.putString(USER_PunchOutLongitude, user_punchOutLongitude)
            editor.apply()
        }

    var useR_PunchOut: String?
        get() = pref.getString(USER_PunchOut, "false")
        set(PunchOut) {
            editor.putString(USER_PunchOut, PunchOut)
            editor.apply()
        }

    var userLatitude: String?
        get() = pref.getString(USER_LATITUDE, "0")
        set(latitude) {
            editor.putString(USER_LATITUDE, latitude)
            editor.apply()
        }

    var paramAndResponse: String?
        get() = pref.getString("location_param", "")
        set(param) {
            editor.putString("location_param", param)
            editor.apply()
        }


    var userLongitude: String?
        get() = pref.getString(USER_LONGITUDE, "0")
        set(longitude) {
            editor.putString(USER_LONGITUDE, longitude)
            editor.apply()
        }

    var userLoginStatus: String?
        get() = pref.getString(USER_LOGIN_STATUS, "")
        set(status) {
            editor.putString(USER_LOGIN_STATUS, status)
            editor.apply()
        }

    val useR_Id: String
        get() = pref.getString(USER_ID, "")!!

    fun setUser_Id(user_id: String?) {
        editor.putString(USER_ID, user_id)
        editor.apply()
    }

    var useR_TOKEN: String?
        get() = pref.getString(USER_TOKEN, "")
        set(_USER_TOKEN) {
            editor.putString(USER_TOKEN, _USER_TOKEN)
            editor.apply()
        }

    var useR_NAME: String?
        get() = pref.getString(USER_NAME, "")
        set(_USER_NAME) {
            editor.putString(USER_NAME, _USER_NAME)
            editor.apply()
        }

    var useR_EMAIL: String?
        get() = pref.getString(USER_EMAIL, "")
        set(_USER_EMAIL) {
            editor.putString(USER_EMAIL, _USER_EMAIL)
            editor.apply()
        }

    var useR_ROLENAME: String?
        get() = pref.getString(USER_ROLENAME, "")
        set(_USER_ROLENAME) {
            editor.putString(USER_ROLENAME, _USER_ROLENAME)
            editor.apply()
        }

    var useR_PASSWORD: String?
        get() = pref.getString(USER_PASSWORD, "")
        set(_USER_PASSWORD) {
            editor.putString(USER_PASSWORD, _USER_PASSWORD)
            editor.apply()
        }

    fun clear() {
        editor.clear()
        editor.commit()
    }

    var useR_TYPE: String?
        get() = pref.getString(USER_TYPE, "")
        set(_USER_TYPE) {
            editor.putString(USER_TYPE, _USER_TYPE)
            editor.apply()
        }

    var useR_TYPE_ID: String?
        get() = pref.getString(USER_TYPE_ID, "")
        set(_USER_TYPE_ID) {
            editor.putString(USER_TYPE_ID, _USER_TYPE_ID)
            editor.apply()
        }

    var devicE_ID: String?
        get() = pref.getString(DEVICE_ID, "")
        set(_DEVICE_ID) {
            editor.putString(DEVICE_ID, _DEVICE_ID)
            editor.apply()
        }

    var firebasE_DEVICE_TOKEN: String?
        get() = pref.getString(FIREBASE_DEVICE_TOKEN, "")
        set(_FIREBASE_DEVICE_TOKEN) {
            editor.putString(FIREBASE_DEVICE_TOKEN, _FIREBASE_DEVICE_TOKEN)
            editor.apply()
        }

    var useR_Mobile: String?
        get() = pref.getString(USER_Mobile, "")
        set(_USER_Mobile) {
            editor.putString(USER_Mobile, _USER_Mobile)
            editor.apply()
        }

    var useR_DistrictId: String?
        get() = pref.getString(USER_DISTRICT_ID, "")
        set(_USER_DistrictId) {
            editor.putString(USER_DISTRICT_ID, _USER_DistrictId)
            editor.apply()
        }

    var useR_District: String?
        get() = pref.getString(USER_DISTRICT, "")
        set(_USER_District) {
            editor.putString(USER_DISTRICT, _USER_District)
            editor.apply()
        }

    var USER_Change_Language: String?
        get() = pref.getString(USER_CHANGE_LANGUAGE, "")
        set(_USER_Change_Language) {
            editor.putString(USER_CHANGE_LANGUAGE, _USER_Change_Language)
            editor.apply()
        }

    var cOMPLAINT_POSITION: Int
        get() = pref.getInt(COMPLAINT_POSITION, 0)
        set(_COMPLAINT_POSITION) {
            editor.putInt(COMPLAINT_POSITION, _COMPLAINT_POSITION)
            editor.apply()
        }

    var sYSTEM_ID: String?
        get() = pref.getString(SYSTEM_ID, "")
        set(system_id) {
            editor.putString(SYSTEM_ID, system_id)
            editor.apply()
        }

    var froM_DATE: String?
        get() = pref.getString(FROM_DATE, "")
        set(_FROM_DATE) {
            editor.putString(FROM_DATE, _FROM_DATE)
            editor.apply()
        }

    var tO_DATE: String?
        get() = pref.getString(TO_DATE, "")
        set(_TO_DATE) {
            editor.putString(TO_DATE, _TO_DATE)
            editor.apply()
        }

    var mANAGE_SPECIAL_LOGIN: String?
        get() = pref.getString(MANAGE_SPECIAL_LOGIN, "false")
        set(_MANAGE_SPECIAL_LOGIN) {
            editor.putString(MANAGE_SPECIAL_LOGIN, _MANAGE_SPECIAL_LOGIN)
            editor.apply()
        }

    companion object {
        private const val PREF_NAME = "ePDS_FRT"
        private const val USER_LOGIN_STATUS = "USER_LOGIN_STATUS"
        private const val USER_EXIST = "USER_EXIST"
        private const val APP_MODE = "APP_MODE"
        private const val USER_ID = "USER_ID"
        private const val USER_TYPE = "USER_TYPE"
        private const val USER_TYPE_ID = "USER_TYPE_ID"
        private const val DEVICE_ID = "DEVICE_ID"
        private const val FIREBASE_DEVICE_TOKEN = "FIREBASE_DEVICE_TOKEN"
        private const val USER_LAST_LOGIN = "USER_LAST_LOGIN"
        private const val USER_TOKEN = "USER_TOKEN"
        private const val USER_NAME = "USER_NAME"
        private const val USER_EMAIL = "USER_EMAIL"
        private const val USER_Mobile = "USER_Mobile"
        private const val USER_ROLENAME = "USER_ROLENAME"
        private const val USER_PASSWORD = "USER_PASSWORD"
        private const val USER_ADDRESS = "USER_ADDRESS"
        private const val USER_LATITUDE = "LATITUDE"
        private const val USER_LONGITUDE = "LONGITUDE"
        private const val USER_PunchIn = "USER_PunchIn"
        private const val USER_PunchOut = "USER_PunchOut"
        private const val USER_PunchInTime = "USER_PunchInTime"
        private const val USER_PunchOutTime = "USER_PunchOutTime"
        private const val USER_PunchInLatitude = "USER_PunchInLatitude"
        private const val USER_PunchInLongitude = "USER_PunchInLongitude"
        private const val USER_PunchOutLatitude = "USER_PunchOutLatitude"
        private const val USER_PunchOutLongitude = "USER_PunchOutLongitude"
        private const val USER_CURRENTLAT = "USER_CURRENTLAT"
        private const val USER_CURRENTLONG = "USER_CURRENTLONG"
        private const val USER_CURRENTTIME = "USER_CURRENTTIME"
        private const val USER_DISTRICT_ID = "USER_DISTRICT_ID"
        private const val USER_DISTRICT = "USER_DISTRICT"
        private const val USER_CHANGE_LANGUAGE = "USER_CHANGE_LANGUAGE"
        private const val COMPLAINT_POSITION = "COMPLAINT_POSITION"
        private const val SYSTEM_ID = "SYSTEM_ID"
        private const val FROM_DATE = "FROM_DATE"
        private const val TO_DATE = "TO_DATE"
        private const val PERMISSION_GRANTED_BACKGROUND = "PERMISSION_GRANTED_BACKGROUND"
        private const val PERMISSION_GRANTED = "PERMISSION_GRANTED"
        private const val MANAGE_SPECIAL_LOGIN = "MANAGE_SPECIAL_LOGIN"
    }
}
