package com.callmangement.ui.attendance

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import com.callmangement.Network.APIService
import com.callmangement.Network.RetrofitInstance
import com.callmangement.R
import com.callmangement.custom.CustomActivity
import com.callmangement.databinding.ActivityAttendanceBinding
import com.callmangement.model.attendance.ModelAttendance
import com.callmangement.support.permissions.PermissionHandler
import com.callmangement.support.permissions.Permissions
import com.callmangement.tracking_service.GpsUtils
import com.callmangement.ui.home.MainActivity
import com.callmangement.utils.Constants
import com.callmangement.utils.DateTimeUtils
import com.callmangement.utils.PrefManager
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.material.snackbar.Snackbar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.UnsupportedEncodingException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.Objects

class AttendanceActivity : CustomActivity() {
    private var binding: ActivityAttendanceBinding? = null
    private var AddDate = ""
    private var AddTime = ""
    private var punchTime = ""
    private var completeDateFormat: SimpleDateFormat? = null
    private var sdfCompleteDate: SimpleDateFormat? = null
    private var sdfCompleteTime: SimpleDateFormat? = null
    private val mLocationRequest: LocationRequest? = null
    private var prefManager: PrefManager? = null
    var latitude: Double = 0.0
    var longitude: Double = 0.0
    var completeAddressStr: String = ""
    lateinit var permissions: Array<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_attendance)
        binding!!.actionBar.ivBack.visibility = View.GONE
        binding!!.actionBar.ivThreeDot.visibility = View.GONE
        binding!!.actionBar.layoutLanguage.visibility = View.GONE
        binding!!.actionBar.buttonPDF.visibility = View.GONE
        binding!!.actionBar.textToolbarTitle.text = resources.getString(R.string.attendance)
        prefManager = PrefManager(this)
        initView()
    }

    @SuppressLint("Range", "SimpleDateFormat", "SetTextI18n")
    private fun initView() {
        completeDateFormat = SimpleDateFormat("dd-MM-yyyy hh:mm:ss aa")
        sdfCompleteDate = SimpleDateFormat("dd-MM-yyyy")
        sdfCompleteTime = SimpleDateFormat("hh:mm:ss aa")
        AddDate = sdfCompleteDate!!.format(Date())
        AddTime = sdfCompleteTime!!.format(Date())
        val dayname = day
        binding!!.txtDate.text = "Date : $AddDate, $dayname"
        punchTime = AddTime
        binding!!.txtDay.text = AddTime
        updateTime()
        setUpOnClickListener()
    }

    private fun setUpOnClickListener() {
        binding!!.btnPunch.setOnLongClickListener { v: View? ->
            checkPermission()
            false
        }
        binding!!.actionBar.ivBack.setOnClickListener { view: View? -> finish() }
    }

    private fun checkPermission() {
        // String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION
        //      , Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
        permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.READ_MEDIA_IMAGES,
                Manifest.permission.READ_MEDIA_VIDEO,
                Manifest.permission.CAMERA
            )

            //  Log.d("checkggg","b");
        } else {
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA
            )
            //  Log.d("checkggg","0");
        }


        val rationale = "Please provide location permission so that you can ..."
        val options = Permissions.Options()
            .setRationaleDialogTitle("Info")
            .setSettingsDialogTitle("Warning")
        Permissions.check(mContext, permissions, rationale, options, object : PermissionHandler() {
            override fun onGranted() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    val backgroundLocationPermissionApproved = ActivityCompat.checkSelfPermission(
                        mContext,
                        Manifest.permission.ACCESS_BACKGROUND_LOCATION
                    )
                    if (backgroundLocationPermissionApproved != 0) Handler(Looper.getMainLooper()).postDelayed(
                        { collectLocationPermissionDataDialog() }, 500
                    )
                    else {
                        if (!getGPSStatus()) turnOnGps()
                        else buildLocation()
                    }
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    val backgroundLocationPermissionApproved = ActivityCompat.checkSelfPermission(
                        mContext,
                        Manifest.permission.ACCESS_BACKGROUND_LOCATION
                    )
                    if (backgroundLocationPermissionApproved != 0) Handler(Looper.getMainLooper()).postDelayed(
                        { collectLocationPermissionDataDialog() }, 500
                    )
                    else {
                        if (!getGPSStatus()) turnOnGps()
                        else buildLocation()
                    }
                } else {
                    if (!getGPSStatus()) turnOnGps()
                    else buildLocation()
                }
            }

            override fun onDenied(context: Context, deniedPermissions: ArrayList<String>) {
                Toast.makeText(mContext, "Permission denied", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun collectLocationPermissionDataDialog() {
        val builder = AlertDialog.Builder(mContext)
        builder.setMessage(resources.getString(R.string.collect_location_permission_alert))
            .setCancelable(false)
            .setPositiveButton(
                resources.getString(R.string.ok)
            ) { dialog: DialogInterface, id: Int ->
                checkBackgroundLocationPermission()
                dialog.dismiss()
            }
        val alert = builder.create()
        alert.setTitle(resources.getString(R.string.alert))
        alert.show()
    }

    private fun checkBackgroundLocationPermission() {
        val permissions = arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
        val rationale = "Please provide Access Background Location"
        val options = Permissions.Options()
            .setRationaleDialogTitle("Info")
            .setSettingsDialogTitle("Warning")
        Permissions.check(mContext, permissions, rationale, options, object : PermissionHandler() {
            override fun onGranted() {
                if (!getGPSStatus()) turnOnGps()
                else buildLocation()
            }

            override fun onDenied(context: Context, deniedPermissions: ArrayList<String>) {
                Toast.makeText(mContext, "Permission denied", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun turnOnGps() {
        GpsUtils(mContext).turnGPSOn { isGPSEnable: Boolean ->
            val isGPS = isGPSEnable
        }
    }

    private fun getGPSStatus(): Boolean {
            val manager =
                getSystemService(LOCATION_SERVICE) as LocationManager
            return manager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        }

    fun buildLocation() {
        val locationRequest = LocationRequest()
        locationRequest.setInterval(10000)
        locationRequest.setFastestInterval(3000)
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
        if ((ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) && (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
                    != PackageManager.PERMISSION_GRANTED)
        ) {
            return
        }
        LocationServices.getFusedLocationProviderClient(this)
            .requestLocationUpdates(locationRequest, object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    super.onLocationResult(locationResult)
                    LocationServices.getFusedLocationProviderClient(applicationContext)
                        .removeLocationUpdates(this)
                    if (locationResult.locations.size > 0) {
                        val latestlocIndex = locationResult.locations.size - 1
                        latitude = locationResult.locations[latestlocIndex].latitude
                        longitude = locationResult.locations[latestlocIndex].longitude
                        completeAddressStr = addressFromLatLong()
                        markAttendance()
                    }
                }
            }, Looper.getMainLooper())
    }

    private fun addressFromLatLong(): String {
            var localCompleteAddressStr = ""
            try {
                val gcd = Geocoder(applicationContext, Locale.getDefault())
                val addresses =
                    gcd.getFromLocation(latitude, longitude, 1)
                if (addresses!!.size > 0) {
                    localCompleteAddressStr = addresses[0].getAddressLine(0)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                localCompleteAddressStr = ""
            }
            return localCompleteAddressStr
        }

    private fun markAttendance() {
        if (latitude != 0.0 && longitude != 0.0) {
            if (Constants.isNetworkAvailable(mContext)) {
                val user_id = prefManager!!.useR_Id
                val latitudeStr = latitude.toString() + ""
                val longitudeStr = longitude.toString() + ""
                val address = completeAddressStr
                val punch_in_date = DateTimeUtils.getCurrentDate()
                val punch_in_time = punchTime
                val latitude_out = ""
                val longitude_out = ""
                val address_out = ""
                val punch_out_date = ""
                val punch_out_time = ""
                //  insertAttendanceDataInServer(user_id,latitudeStr,longitudeStr,encodeToUtf8Hex(address), punch_in_date,
                //  punch_in_time, latitude_out,longitude_out,address_out, punch_out_date,punch_out_time);
                insertAttendanceDataInServer(
                    user_id,
                    latitudeStr,
                    longitudeStr,
                    address,
                    punch_in_date,
                    punch_in_time,
                    latitude_out,
                    longitude_out,
                    address_out,
                    punch_out_date,
                    punch_out_time
                )
            } else {
                makeToast(resources.getString(R.string.no_internet_connection))
            }
        } else {
            val snackbar = Snackbar.make(
                binding!!.btnPunch,
                resources.getString(R.string.location_not_found),
                Snackbar.LENGTH_SHORT
            )
            snackbar.show()
        }
    }

    private fun insertAttendanceDataInServer(
        user_id: String,
        latitude: String,
        longitude: String,
        address: String,
        punch_in_date: String,
        punch_in_time: String,
        latitude_out: String,
        longitude_out: String,
        address_out: String,
        punch_out_date: String,
        punch_out_time: String
    ) {
        val service = RetrofitInstance.getRetrofitInstance().create(
            APIService::class.java
        )
        //   Log.d("address--",""+address);
        val call = service.addAttendanceRecord(
            user_id,
            latitude,
            longitude,
            address,
            punch_in_date,
            punch_in_time,
            latitude_out,
            longitude_out,
            address_out,
            punch_out_date,
            punch_out_time
        )
        call.enqueue(object : Callback<ModelAttendance?> {
            override fun onResponse(
                call: Call<ModelAttendance?>,
                response: Response<ModelAttendance?>
            ) {
                if (response.isSuccessful) {
                    val model = response.body()
                    if (model!!.status == "200") {
                        makeToast(resources.getString(R.string.your_attendance_marked_successfully))

                        startActivity(Intent(mContext, MainActivity::class.java))
                        finish()
                    } else {
                        makeToast(resources.getString(R.string.your_attendance_not_mark))
                    }
                } else {
                    makeToast(resources.getString(R.string.error))
                }
            }

            override fun onFailure(call: Call<ModelAttendance?>, t: Throwable) {
                makeToast(resources.getString(R.string.error_message))
            }
        })
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            this.finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private val day: String
        get() {
            val cal = Calendar.getInstance()
            val day = cal[Calendar.DAY_OF_WEEK]
            var dayname = ""
            when (day) {
                1 -> dayname = resources.getString(R.string.sunday)
                2 -> dayname = resources.getString(R.string.monday)
                3 -> dayname = resources.getString(R.string.tuesday)
                4 -> dayname = resources.getString(R.string.wednesday)
                5 -> dayname = resources.getString(R.string.thursday)
                6 -> dayname = resources.getString(R.string.friday)
                7 -> dayname = resources.getString(R.string.saturday)
            }
            return dayname
        }

    fun updateTime() {
        try {
            @SuppressLint("SetTextI18n") val runnable = Runnable {
                @SuppressLint("SimpleDateFormat") val sdfCompleteDate =
                    SimpleDateFormat("dd-MM-yyyy")
                @SuppressLint("SimpleDateFormat") val sdfCompleteTime =
                    SimpleDateFormat("hh:mm:ss aa")
                AddDate = sdfCompleteDate.format(Date())
                AddTime = sdfCompleteTime.format(Date())
                val dayname = day
                binding!!.txtDate.text =
                    resources.getString(R.string.date_str) + " : " + AddDate + ", " + dayname
                punchTime = AddTime
                binding!!.txtDay.text = AddTime
                updateTime()
            }

            val handler = Handler()
            handler.postDelayed(runnable, 1000)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 102) {
            val isGPS = getGPSStatus()
            if (isGPS) buildLocation()
        }
    }

    override fun onBackPressed() {
        finishAffinity()
    }


    companion object {
        fun encodeToUtf8Hex(input: String): String {
            val utf8Hex = StringBuilder()
            try {
                val bytes = input.toByteArray(charset("UTF-8"))
                for (b in bytes) {
                    utf8Hex.append(String.format("\\x%02x", b))
                }
            } catch (e: UnsupportedEncodingException) {
                e.printStackTrace()
            }
            return utf8Hex.toString()
        }
    }
}