package com.callmangement.utils;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.callmangement.firebase.FirebaseUtils;
import com.google.firebase.messaging.FirebaseMessaging;

public class MyApp extends Application {
    private static final String TAG = "MyApp";
    private static MyApp mInstance;

    public MyApp() {
        mInstance = this;
    }

    @Override
    public void onCreate() {
        FirebaseUtils.registerTopic("all");
        super.onCreate();
    }

    public static Context getContext() {
        return mInstance;
    }

}
