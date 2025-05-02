package com.callmangement.tracking_service

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Handler
import com.callmangement.database.DbController
import com.callmangement.utils.Constants
import com.callmangement.utils.DateTimeUtils
import com.callmangement.utils.MyDialog
import com.callmangement.utils.PrefManager
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsResponse
import com.google.android.gms.tasks.Task
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.Objects
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class GetLocation @SuppressLint("MissingPermission") constructor(private val mContext: Context) {
    private var mLocationRequest: LocationRequest? = null
    private val prefManager = PrefManager(mContext)

    fun build() {
        createLocationRequest()
        checkLocationSettings()
    }

    private fun createLocationRequest() {
        mLocationRequest = LocationRequest.create()
        mLocationRequest!!.setInterval(60000)
        mLocationRequest!!.setFastestInterval(60000)
        mLocationRequest!!.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
    }

    private fun checkLocationSettings() {
        val builder = LocationSettingsRequest.Builder().addLocationRequest(
            mLocationRequest!!
        ).setAlwaysShow(false)
        initializeTimerTask(builder)
    }

    fun initializeTimerTask(builder: LocationSettingsRequest.Builder) {
        val result = LocationServices.getSettingsClient(Objects.requireNonNull(mContext))
            .checkLocationSettings(builder.build())
        result.addOnCompleteListener { task: Task<LocationSettingsResponse?>? ->
            try {
                val locationManager =
                    mContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
                if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    location
                } else if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                    location
                } else {
                    val runnable = Runnable {
                        if (MyDialog.alertD == null || !MyDialog.alertD!!.isShowing) {
                            val intent = Intent(mContext, MyDialog::class.java)
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            mContext.startActivity(intent)
                        }
                    }
                    val handler = Handler()
                    handler.postDelayed(runnable, 1500)
                }
            } catch (exception: Exception) {
                exception.printStackTrace()
            }
        }
    }

    @get:SuppressLint("MissingPermission")
    private val location: Unit
        get() {
            val mFusedLocationClient =
                LocationServices.getFusedLocationProviderClient(Objects.requireNonNull(mContext))
            val location = mFusedLocationClient.lastLocation
            location.addOnSuccessListener { location1: Location? ->
                @SuppressLint("SimpleDateFormat") val simpleDateFormat =
                    SimpleDateFormat("yyyy-MM-dd HH:mm:ss aa")
                val lat1: Double
                val lon1: Double
                val lat2: Double
                val lon2: Double
                val date1: Date
                val date2: Date
                var diffSeconds: Long = 0
                var speed = 0.0
                if (location1 != null) {
                    Constants.currentLat = prefManager.userCurrentlat!!
                    Constants.currentLong = prefManager.userCurrentlong!!
                    Constants.currentTime = prefManager.userCurrenttime!!
                    if ((Constants.currentLat == null || Constants.currentLat.equals(
                            "",
                            ignoreCase = true
                        )) && ((Constants.currentLong == null || Constants.currentLong.equals(
                            "",
                            ignoreCase = true
                        )))
                    ) {
                        Constants.currentLat = location1.latitude.toString()
                        Constants.currentLong = location1.longitude.toString()
                        val c = Calendar.getInstance().time
                        Constants.currentTime = simpleDateFormat.format(c)
                    } else {
                        lat1 = Objects.requireNonNull(Constants.currentLat).toDouble()
                        lon1 = Constants.currentLong.toDouble()
                        lat2 = location1.latitude
                        lon2 = location1.longitude
                        //current time
                        val c = Calendar.getInstance().time
                        val currentTimeNew = simpleDateFormat.format(c)
                        try {
                            date1 = simpleDateFormat.parse(Constants.currentTime)
                            date2 = simpleDateFormat.parse(currentTimeNew)
                            //find difference
                            val diff =
                                Objects.requireNonNull(date2).time - Objects.requireNonNull(date1).time
                            diffSeconds = diff / 1000
                            val diffMinutes = diff / (60 * 1000) % 60
                            val diffHours = diff / (60 * 60 * 1000) % 24
                            val diffDays = diff / (24 * 60 * 60 * 1000)
                            val difonlysec = diff / 1000
                        } catch (e: ParseException) {
                            e.printStackTrace()
                        }
                        //Calculate distance
                        val earthRadius = 6371000.0 //meters
                        val dLat = Math.toRadians(lat2 - lat1)
                        val dLng = Math.toRadians(lon2 - lon1)
                        val a = sin(dLat / 2) * sin(dLat / 2) + cos(Math.toRadians(lat1)) * cos(
                            Math.toRadians(lat2)
                        ) * sin(dLng / 2) * sin(dLng / 2)
                        val cc = 2 * atan2(sqrt(a), sqrt(1 - a))
                        val dist = (earthRadius * cc).toFloat()
                        val distMeter = dist * 1000
                        //calculate speed
                        speed = (distMeter / diffSeconds).toDouble()
                        Constants.currentLat = lat2.toString()
                        Constants.currentLong = lon2.toString()
                        Constants.currentTime = currentTimeNew
                    }
                    prefManager.userCurrentlat = Constants.currentLat
                    prefManager.userCurrentlong = Constants.currentLong
                    prefManager.userCurrenttime = Constants.currentTime
                    //calculate speed
//                    if (speed < 150000) {
//                        updateCityAndPincode(location.getLatitude(), location.getLongitude());
//                    }
                    updateCityAndPincode(location1.latitude, location1.longitude)
                }
            }
        }

    private fun updateCityAndPincode(latitude: Double, longitude: Double) {
        try {
            val gcd = Geocoder(mContext, Locale.getDefault())
            val addresses = gcd.getFromLocation(latitude, longitude, 1)
            if (addresses!!.size > 0) {
                val add1 = "" + addresses[0].getAddressLine(0)
                if (add1.length > 0) {
                    saveLocation(add1, latitude, longitude)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun saveLocation(address: String, latitude: Double, longitude: Double) {
        //Log.e("location","latitude - "+latitude +", longitude - "+longitude+", address - "+ address);
        DbController(mContext).insertLocation(
            prefManager.uSER_Id,
            prefManager.uSER_DistrictId,
            latitude.toString(),
            longitude.toString(),
            address,
            DateTimeUtils.currentDataTime
        )
    }
}
