package com.callmangement.utils

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import com.callmangement.R


class MyDialog : AppCompatActivity() {
    var i: Int = 0
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        alert()
    }

    fun alert() {
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setMessage("GPS is disabled in your device.Please enable GPS!.")
            .setCancelable(false)
            .setPositiveButton("Goto Settings Page To Enable GPS") { dialog: DialogInterface?, id: Int ->
                val callGPSSettingIntent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(callGPSSettingIntent)
                finish()
            }.setNegativeButton("Cancel") { dialog: DialogInterface, id: Int ->
                dialog.dismiss()
                finish()
            }
        alertD = alertDialogBuilder.create()
        alertD!!.show()
    }


    fun AlertDialog() {
        AlertDialog.Builder(this)
            .setTitle("Alert")
            .setMessage("GPS should be enabled! Close app and try again after turning on GPS.")
            .setCancelable(false)
            .setIcon(R.mipmap.ic_launcher)
            .setPositiveButton("Ok") { dialog: DialogInterface, id: Int ->
                dialog.dismiss()
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    finishAffinity()
                } else {
                    finish()
                    System.exit(0)
                }
            }.show()
    }

    override fun onResume() {
        super.onResume()
        val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            finish()
        } else {
            alert()
        }
    }

    companion object {
        var alertD: AlertDialog? = null
    }
}