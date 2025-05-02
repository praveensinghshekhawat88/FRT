package com.callmangement.utils;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager extends Application {
    private final SharedPreferences pref;
    private final SharedPreferences.Editor editor;
    private final Context _context;
    private static final String PREF_NAME = "ePDS";
    private static final String PERMISSION_GRANTED_BACKGROUND = "PERMISSION_GRANTED_BACKGROUND";
    private static final String PERMISSION_GRANTED = "PERMISSION_GRANTED";

    public SessionManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
    }

    public void setPermissionGrantedBackground(boolean _PERMISSION_GRANTED_BACKGROUND) {
        editor.putBoolean(PERMISSION_GRANTED_BACKGROUND, _PERMISSION_GRANTED_BACKGROUND);
        editor.apply();
    }

    public boolean getPermissionGranted() {
        return pref.getBoolean(PERMISSION_GRANTED, false);
    }

    public void setPermissionGranted(boolean _PERMISSION_GRANTED) {
        editor.putBoolean(PERMISSION_GRANTED, _PERMISSION_GRANTED);
        editor.apply();
    }

    public boolean getPermissionGrantedBackground() {
        return pref.getBoolean(PERMISSION_GRANTED_BACKGROUND, false);
    }

    public void clear() {
        editor.clear();
        editor.commit();
    }
}
