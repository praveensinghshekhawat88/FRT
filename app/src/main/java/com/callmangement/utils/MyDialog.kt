package com.callmangement.utils;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;

import androidx.appcompat.app.AppCompatActivity;

import com.callmangement.R;


public class MyDialog extends AppCompatActivity {
    int i = 0;
    public static AlertDialog alertD;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        alert();
    }

    void alert() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("GPS is disabled in your device.Please enable GPS!.")
                .setCancelable(false)
                .setPositiveButton("Goto Settings Page To Enable GPS", (dialog, id) -> {
                    Intent callGPSSettingIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(callGPSSettingIntent);
                    finish();
                }).setNegativeButton("Cancel", (dialog, id) -> {
                    dialog.dismiss();
                    finish();
                });
        alertD = alertDialogBuilder.create();
        alertD.show();
    }


    void AlertDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Alert")
                .setMessage("GPS should be enabled! Close app and try again after turning on GPS.")
                .setCancelable(false)
                .setIcon(R.mipmap.ic_launcher)
                .setPositiveButton("Ok", (dialog, id) -> {
                    dialog.dismiss();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        finishAffinity();
                    } else {
                        finish();
                        System.exit(0);
                    }
                }).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            finish();
        } else {
            alert();
        }
    }

}