package com.callmangement.firebase

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import androidx.core.app.ActivityCompat
import com.callmangement.network.APIService
import com.callmangement.network.RetrofitInstance
import com.callmangement.database.DbController
import com.callmangement.utils.DateTimeUtils
import com.callmangement.utils.PrefManager
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsResponse
import com.google.android.gms.tasks.Task
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Locale
import java.util.Objects

@SuppressLint("MissingFirebaseInstanceTokenRefresh")
class MyFirebaseMessagingService : FirebaseMessagingService() {
    private var mLocationRequest: LocationRequest? = null
    private var prefManager: PrefManager? = null

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        //  Log.e(TAG, "onMessageReceived: " + remoteMessage.toString());
        // Log.e(TAG, "onMessageReceived: " + remoteMessage.getData());
        prefManager = PrefManager(applicationContext)
        buildLocation()
        /*if (remoteMessage.getData().size() > 0) {
            Map<String, String> params = remoteMessage.getData();
            if (FirebaseMessageController.parseParam(params));
        }*/
    }

    fun buildLocation() {
        mLocationRequest = LocationRequest.create()
        mLocationRequest!!.setInterval(1000)
        mLocationRequest!!.setFastestInterval(1000)
        mLocationRequest!!.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
        val builder = LocationSettingsRequest.Builder().addLocationRequest(
            mLocationRequest!!
        ).setAlwaysShow(false)
        initialize(builder)
    }

    fun initialize(builder: LocationSettingsRequest.Builder) {
        val result = LocationServices.getSettingsClient(
            Objects.requireNonNull(
                applicationContext
            )
        ).checkLocationSettings(builder.build())
        result.addOnCompleteListener { task: Task<LocationSettingsResponse?>? ->
            try {
                val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
                if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    location
                } else {
                    val address = "GPS not enabled."
                    val latitude = 0.0
                    val longitude = 0.0
                    saveLocation(address, latitude, longitude)
                }
            } catch (exception: Exception) {
                exception.printStackTrace()
            }
        }
    }

    private val location: Unit
        get() {
            val mFusedLocationClient = LocationServices.getFusedLocationProviderClient(
                Objects.requireNonNull(
                    applicationContext
                )
            )
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this, Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
            val location = mFusedLocationClient.lastLocation
            location.addOnSuccessListener { location1: Location? ->
                if (location1 != null) {
                    getAddressFromLatLong(location1.latitude, location1.longitude)
                } else {
                    val address = "GPS not enabled."
                    val latitude = 0.0
                    val longitude = 0.0
                    saveLocation(address, latitude, longitude)
                }
            }
        }

    private fun getAddressFromLatLong(latitude: Double, longitude: Double) {
        var address = ""
        try {
            val gcd = Geocoder(applicationContext, Locale.getDefault())
            val addresses = gcd.getFromLocation(latitude, longitude, 1)
            if (addresses!!.size > 0) {
                address = addresses[0].getAddressLine(0)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        saveLocation(address, latitude, longitude)
        //        createLocationJsonArray(address, latitude, longitude);
    }

    private fun saveLocation(address: String, latitude: Double, longitude: Double) {
        //  Log.e("location", "latitude - " + latitude + ", longitude - " + longitude + ", address - " + address);
        DbController(applicationContext).insertLocation(
            prefManager!!.uSER_Id,
            prefManager!!.uSER_DistrictId,
            latitude.toString(),
            longitude.toString(),
            address,
            DateTimeUtils.currentDataTime
        )
        try {
            locationDataFromLocalDB
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    @get:Throws(JSONException::class)
    private val locationDataFromLocalDB: Unit
        get() {
            val locationJsonArray = JSONArray()
            val cursor = DbController(applicationContext).locationData
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    do {
                        val latitude = cursor.getString(2)
                        val longitude = cursor.getString(3)
                        val address = cursor.getString(4)
                        val location_date_time = cursor.getString(5)

                        val jsonObject = JSONObject()
                        jsonObject.put("latitude", latitude)
                        jsonObject.put("longitude", longitude)
                        jsonObject.put("address", address)
                        jsonObject.put("location_Date_Time", location_date_time)
                        locationJsonArray.put(jsonObject)
                    } while (cursor.moveToNext())
                }
                if (locationJsonArray.length() > 0) {
                    insertLocationDataInServer(locationJsonArray.toString())
                }
            }
        }

    /*private void createLocationJsonArray(String address, double latitude, double longitude){
        JSONArray locationJsonArray = new JSONArray();
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("latitude", latitude);
            jsonObject.put("longitude", longitude);
            jsonObject.put("address", address);
            jsonObject.put("location_Date_Time", DateTimeUtils.getCurrentDataTime());
            locationJsonArray.put(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (locationJsonArray.length() > 0) {
            insertLocationDataInServer(String.valueOf(locationJsonArray));
        }
    }*/
    private fun insertLocationDataInServer(locationJsonArray: String) {
        val jsonArrBody = RequestBody.create(
            "application/json; charset=utf-8".toMediaTypeOrNull(),
            locationJsonArray.toString()
        )
        val service = RetrofitInstance.retrofitInstance!!.create(
            APIService::class.java
        )
        val call = service.saveSELocations(
            prefManager!!.uSER_Id,
            prefManager!!.uSER_DistrictId,
            prefManager!!.dEVICE_ID,
            jsonArrBody
        )
        call!!.enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                try {
                    if (response.isSuccessful) {
                        if (response.code() == 200) {
                            if (response.body() != null) {
                                val responseStr = response.body()!!.string()
                                val jsonObject = JSONObject(responseStr)
                                val status = jsonObject.optString("status")
                                val message = jsonObject.optString("message")
                                //    Log.e("location", message);
//                                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                                if (status == "200") {
                                    DbController(applicationContext).deleteLocationData()
                                }
                            }
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
            }
        })
    }

    companion object {
        private const val TAG = "MyFirebaseMessagingServ"
    }
}