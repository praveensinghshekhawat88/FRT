package com.callmangement.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.BoringLayout;
import android.view.inputmethod.InputMethodManager;

public class PrefManager {
    private final SharedPreferences pref;
    private final SharedPreferences.Editor editor;
    private final Context _context;
    private static final String PREF_NAME = "ePDS_FRT";
    private static final String USER_LOGIN_STATUS = "USER_LOGIN_STATUS";
    private static final String USER_EXIST = "USER_EXIST";
    private static final String APP_MODE = "APP_MODE";
    private static final String USER_ID = "USER_ID";
    private static final String USER_TYPE = "USER_TYPE";
    private static final String USER_TYPE_ID = "USER_TYPE_ID";
    private static final String DEVICE_ID = "DEVICE_ID";
    private static final String FIREBASE_DEVICE_TOKEN = "FIREBASE_DEVICE_TOKEN";
    private static final String USER_LAST_LOGIN = "USER_LAST_LOGIN";
    private static final String USER_TOKEN = "USER_TOKEN";
    private static final String USER_NAME = "USER_NAME";
    private static final String USER_EMAIL = "USER_EMAIL";
    private static final String USER_Mobile = "USER_Mobile";
    private static final String USER_ROLENAME = "USER_ROLENAME";
    private static final String USER_PASSWORD = "USER_PASSWORD";
    private static final String USER_ADDRESS = "USER_ADDRESS";
    private static final String USER_LATITUDE = "LATITUDE";
    private static final String USER_LONGITUDE = "LONGITUDE";
    private static final String USER_PunchIn = "USER_PunchIn";
    private static final String USER_PunchOut = "USER_PunchOut";
    private static final String USER_PunchInTime = "USER_PunchInTime";
    private static final String USER_PunchOutTime = "USER_PunchOutTime";
    private static final String USER_PunchInLatitude = "USER_PunchInLatitude";
    private static final String USER_PunchInLongitude = "USER_PunchInLongitude";
    private static final String USER_PunchOutLatitude = "USER_PunchOutLatitude";
    private static final String USER_PunchOutLongitude = "USER_PunchOutLongitude";
    private static final String USER_CURRENTLAT = "USER_CURRENTLAT";
    private static final String USER_CURRENTLONG = "USER_CURRENTLONG";
    private static final String USER_CURRENTTIME = "USER_CURRENTTIME";
    private static final String USER_DISTRICT_ID = "USER_DISTRICT_ID";
    private static final String USER_DISTRICT = "USER_DISTRICT";
    private static final String USER_CHANGE_LANGUAGE = "USER_CHANGE_LANGUAGE";
    private static final String COMPLAINT_POSITION = "COMPLAINT_POSITION";
    private static final String SYSTEM_ID = "SYSTEM_ID";
    private static final String FROM_DATE = "FROM_DATE";
    private static final String TO_DATE = "TO_DATE";
    private static final String PERMISSION_GRANTED_BACKGROUND = "PERMISSION_GRANTED_BACKGROUND";
    private static final String PERMISSION_GRANTED = "PERMISSION_GRANTED";
    private static final String MANAGE_SPECIAL_LOGIN = "MANAGE_SPECIAL_LOGIN";
    public PrefManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
    }

    /*public static void hideSoftKeyboard(Activity activity) {
        final InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (inputMethodManager.isActive()) {
            if (activity.getCurrentFocus() != null) {
                inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
            }
        }
    }*/

   public String getUserCurrentlat(){
       return pref.getString(USER_CURRENTLAT, "");
   }

   public void setUserCurrentlat(String currentLat){
       editor.putString(USER_CURRENTLAT, currentLat);
       editor.apply();
   }
    public String getUserCurrentlong(){
        return pref.getString(USER_CURRENTLONG, "");

    }

    public void setUserCurrentlong(String currentLong){
        editor.putString(USER_CURRENTLONG, currentLong);
        editor.apply();
    }

    public String getUserCurrenttime(){
        return pref.getString(USER_CURRENTTIME, "");
    }

    public void setUserCurrenttime(String currentTime){
        editor.putString(USER_CURRENTTIME, currentTime);
        editor.apply();
    }

    public String getUserAddress() {
        return pref.getString(USER_ADDRESS, "");
    }

    public void setUserAddress(String address) {
        editor.putString(USER_ADDRESS, address);
        editor.apply();
    }

    public String getUSER_PunchIn() {
        return pref.getString(USER_PunchIn, "false");
    }

    public void setUSER_PunchIn(String PunchIn) {
        editor.putString(USER_PunchIn, PunchIn);
        editor.apply();
    }

    public String getUSER_PunchInTime() {
        return pref.getString(USER_PunchInTime, "");
    }

    public void setUSER_PunchInTime(String user_punchInTime) {
        editor.putString(USER_PunchInTime, user_punchInTime);
        editor.apply();
    }

    public String getUSER_PunchOutTime() {
        return pref.getString(USER_PunchOutTime, "");
    }

    public void setUSER_PunchOutTime(String user_punchOutTime) {
        editor.putString(USER_PunchOutTime, user_punchOutTime);
        editor.apply();
    }

    public String getUSER_PunchInLatitude() {
        return pref.getString(USER_PunchInLatitude, "");
    }

    public void setUSER_PunchInLatitude(String user_punchInLatitude) {
        editor.putString(USER_PunchInLatitude, user_punchInLatitude);
        editor.apply();
    }

    public String getUSER_PunchInLongitude() {
        return pref.getString(USER_PunchInLongitude, "");
    }

    public void setUSER_PunchInLongitude(String user_punchInLongitude) {
        editor.putString(USER_PunchInLongitude, user_punchInLongitude);
        editor.apply();
    }

    public String getUSER_PunchOutLatitude() {
        return pref.getString(USER_PunchOutLatitude, "");
    }

    public void setUSER_PunchOutLatitude(String user_punchOutLatitude) {
        editor.putString(USER_PunchOutLatitude, user_punchOutLatitude);
        editor.apply();
    }

    public String getUSER_PunchOutLongitude() {
        return pref.getString(USER_PunchOutLongitude, "");
    }

    public void setUSER_PunchOutLongitude(String user_punchOutLongitude) {
        editor.putString(USER_PunchOutLongitude, user_punchOutLongitude);
        editor.apply();
    }

    public String getUSER_PunchOut() {
        return pref.getString(USER_PunchOut, "false");
    }

    public void setUSER_PunchOut(String PunchOut) {
        editor.putString(USER_PunchOut, PunchOut);
        editor.apply();
    }

    public String getUserLatitude() {
        return pref.getString(USER_LATITUDE, "0");
    }

    public void setParamAndResponse(String param) {
        editor.putString("location_param", param);
        editor.apply();
    }

    public String getParamAndResponse() {
        return pref.getString("location_param", "");
    }


    public void setUserLatitude(String latitude) {
        editor.putString(USER_LATITUDE, latitude);
        editor.apply();
    }

    public String getUserLongitude() {
        return pref.getString(USER_LONGITUDE, "0");
    }

    public void setUserLongitude(String longitude) {
        editor.putString(USER_LONGITUDE, longitude);
        editor.apply();
    }

    public String getUserLoginStatus() {
        return pref.getString(USER_LOGIN_STATUS, "");
    }

    public void setUserLoginStatus(String status) {
        editor.putString(USER_LOGIN_STATUS, status);
        editor.apply();
    }

    public String getUSER_Id() {
        return pref.getString(USER_ID, "");
    }

    public void setUser_Id(String user_id) {
        editor.putString(USER_ID, user_id);
        editor.apply();
    }

    public String getUSER_TOKEN() {
        return pref.getString(USER_TOKEN, "");
    }

    public void setUSER_TOKEN(String _USER_TOKEN) {
        editor.putString(USER_TOKEN, _USER_TOKEN);
        editor.apply();
    }

    public String getUSER_NAME() {
        return pref.getString(USER_NAME, "");
    }

    public void setUSER_NAME(String _USER_NAME) {
        editor.putString(USER_NAME, _USER_NAME);
        editor.apply();
    }

    public String getUSER_EMAIL() {
        return pref.getString(USER_EMAIL, "");
    }

    public void setUSER_EMAIL(String _USER_EMAIL) {
        editor.putString(USER_EMAIL, _USER_EMAIL);
        editor.apply();
    }

    public String getUSER_ROLENAME() {
        return pref.getString(USER_ROLENAME, "");
    }

    public void setUSER_ROLENAME(String _USER_ROLENAME) {
        editor.putString(USER_ROLENAME, _USER_ROLENAME);
        editor.apply();
    }

    public String getUSER_PASSWORD() {
        return pref.getString(USER_PASSWORD, "");
    }

    public void setUSER_PASSWORD(String _USER_PASSWORD) {
        editor.putString(USER_PASSWORD, _USER_PASSWORD);
        editor.apply();
    }

    public void clear() {
        editor.clear();
        editor.commit();
    }

    public String getUSER_TYPE() {
        return pref.getString(USER_TYPE, "");
    }

    public void setUSER_TYPE(String _USER_TYPE) {
        editor.putString(USER_TYPE, _USER_TYPE);
        editor.apply();
    }

    public String getUSER_TYPE_ID() {
        return pref.getString(USER_TYPE_ID, "");
    }

    public void setUSER_TYPE_ID(String _USER_TYPE_ID) {
        editor.putString(USER_TYPE_ID, _USER_TYPE_ID);
        editor.apply();
    }

    public String getDEVICE_ID() {
        return pref.getString(DEVICE_ID, "");
    }

    public void setDEVICE_ID(String _DEVICE_ID) {
        editor.putString(DEVICE_ID, _DEVICE_ID);
        editor.apply();
    }

    public String getFIREBASE_DEVICE_TOKEN() {
        return pref.getString(FIREBASE_DEVICE_TOKEN, "");
    }

    public void setFIREBASE_DEVICE_TOKEN(String _FIREBASE_DEVICE_TOKEN) {
        editor.putString(FIREBASE_DEVICE_TOKEN, _FIREBASE_DEVICE_TOKEN);
        editor.apply();
    }

    public String getUSER_Mobile() {
        return pref.getString(USER_Mobile, "");
    }

    public void setUSER_Mobile(String _USER_Mobile) {
        editor.putString(USER_Mobile, _USER_Mobile);
        editor.apply();
    }

    public String getUSER_DistrictId() {
        return pref.getString(USER_DISTRICT_ID, "");
    }

    public void setUSER_DistrictId(String _USER_DistrictId) {
        editor.putString(USER_DISTRICT_ID, _USER_DistrictId);
        editor.apply();
    }

    public String getUSER_District() {
        return pref.getString(USER_DISTRICT, "");
    }

    public void setUSER_District(String _USER_District) {
        editor.putString(USER_DISTRICT, _USER_District);
        editor.apply();
    }

    public String getUSER_Change_Language() {
        return pref.getString(USER_CHANGE_LANGUAGE, "");
    }

    public void setUSER_Change_Language(String _USER_Change_Language) {
        editor.putString(USER_CHANGE_LANGUAGE, _USER_Change_Language);
        editor.apply();
    }

    public int getCOMPLAINT_POSITION() {
        return pref.getInt(COMPLAINT_POSITION, 0);
    }

    public void setCOMPLAINT_POSITION(int _COMPLAINT_POSITION) {
        editor.putInt(COMPLAINT_POSITION, _COMPLAINT_POSITION);
        editor.apply();
    }

    public String getSYSTEM_ID() {
        return pref.getString(SYSTEM_ID, "");
    }

    public void setSYSTEM_ID(String system_id) {
        editor.putString(SYSTEM_ID, system_id);
        editor.apply();
    }

    public String getFROM_DATE() {
        return pref.getString(FROM_DATE, "");
    }

    public void setFROM_DATE(String _FROM_DATE) {
        editor.putString(FROM_DATE, _FROM_DATE);
        editor.apply();
    }

    public String getTO_DATE() {
        return pref.getString(TO_DATE, "");
    }

    public void setTO_DATE(String _TO_DATE) {
        editor.putString(TO_DATE, _TO_DATE);
        editor.apply();
    }

    public String getMANAGE_SPECIAL_LOGIN() {
        return pref.getString(MANAGE_SPECIAL_LOGIN, "false");
    }

    public void setMANAGE_SPECIAL_LOGIN(String _MANAGE_SPECIAL_LOGIN) {
        editor.putString(MANAGE_SPECIAL_LOGIN, _MANAGE_SPECIAL_LOGIN);
        editor.apply();
    }

}
