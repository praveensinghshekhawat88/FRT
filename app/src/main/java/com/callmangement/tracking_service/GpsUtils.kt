package com.callmangement.tracking_service

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.IntentSender.SendIntentException
import android.location.LocationManager
import android.util.Log
import android.widget.Toast
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsResponse
import com.google.android.gms.location.LocationSettingsStatusCodes

class GpsUtils(private val context: Context) {
    private val mSettingsClient = LocationServices.getSettingsClient(context)
    private val mLocationSettingsRequest: LocationSettingsRequest
    private val locationManager =
        context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    init {
        val locationRequest = LocationRequest.create()
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
        locationRequest.setInterval((10 * 1000).toLong())
        locationRequest.setFastestInterval((2 * 1000).toLong())
        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
        mLocationSettingsRequest = builder.build()
        builder.setAlwaysShow(true)
    }

    fun turnGPSOn(onGpsListener: onGpsListener?) {
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            onGpsListener?.gpsStatus(true)
        } else {
            mSettingsClient
                .checkLocationSettings(mLocationSettingsRequest)
                .addOnSuccessListener(
                    (context as Activity)
                ) { locationSettingsResponse: LocationSettingsResponse? ->
                    onGpsListener?.gpsStatus(true)
                }
                .addOnFailureListener(context) { e: Exception ->
                    val statusCode = (e as ApiException).statusCode
                    when (statusCode) {
                        LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> try {
                            val rae = e as ResolvableApiException
                            rae.startResolutionForResult(context, 102)
                        } catch (sie: SendIntentException) {
                            Log.i(
                                ContentValues.TAG,
                                "PendingIntent unable to execute request."
                            )
                        }

                        LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
                            val errorMessage =
                                "Location settings are inadequate, and cannot be " +
                                        "fixed here. Fix in Settings."
                            //   Log.e(TAG, errorMessage);
                            Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
                        }
                    }
                }
        }
    }

    interface onGpsListener {
        fun gpsStatus(isGPSEnable: Boolean)
    }
}
