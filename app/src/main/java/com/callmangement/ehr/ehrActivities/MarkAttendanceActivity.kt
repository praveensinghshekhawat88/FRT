package com.callmangement.ehr.ehrActivities

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.location.Address
import android.location.Geocoder
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.res.ResourcesCompat
import com.callmangement.R
import com.callmangement.databinding.ActivityMarkAttendanceBinding
import com.callmangement.ehr.api.APIClient.getRetrofitClientWithoutHeaders
import com.callmangement.ehr.api.APIInterface
import com.callmangement.ehr.api.Constants
import com.callmangement.ehr.api.MultipartRequester.fromString
import com.callmangement.ehr.imagepicker.model.Config
import com.callmangement.ehr.imagepicker.model.Image
import com.callmangement.ehr.imagepicker.ui.imagepicker.ImagePicker.Companion.with
import com.callmangement.ehr.models.ModelMarkAttendance
import com.callmangement.ehr.support.CompressImage.Companion.compress
import com.callmangement.ehr.support.OnSingleClickListener
import com.callmangement.ehr.support.Utils.hideCustomProgressDialogCommonForAll
import com.callmangement.ehr.support.Utils.hideKeyboard
import com.callmangement.ehr.support.Utils.isNetworkAvailable
import com.callmangement.ehr.support.Utils.showCustomProgressDialogCommonForAll
import com.callmangement.ehr.tracking_service.GpsUtils
import com.callmangement.support.ImageUtilsForRotate
import com.callmangement.support.permissions.PermissionHandler
import com.callmangement.support.permissions.Permissions
import com.callmangement.utils.PrefManager
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody.Part.Companion.createFormData
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.IOException
import java.io.UnsupportedEncodingException
import java.util.Calendar
import java.util.Locale
import java.util.Objects

class MarkAttendanceActivity : BaseActivity() {
    private var mActivity: Activity? = null
    private var binding: ActivityMarkAttendanceBinding? = null
    private lateinit var permissions: Array<String>
    private var preference: PrefManager? = null
    private var latitude = 0.0
    private var longitude = 0.0
    private var completeAddressStr = ""
    private val nearbyAddessStr = ""
    val REQUEST_PICK_IMAGE_ONE: Int = 1111
    private var challanImageStoragePath: String? = ""
    private val yh: String? = null
    private var locationManager: LocationManager? = null
    private var near_address: String? = null
    private var mapLink: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        binding = ActivityMarkAttendanceBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        init()
    }

    private fun setUpData() {
    }

    private fun init() {
        mActivity = this
        preference = PrefManager(mActivity!!)
        binding!!.actionBar.ivBack.visibility = View.VISIBLE
        binding!!.actionBar.ivThreeDot.visibility = View.GONE
        binding!!.actionBar.layoutLanguage.visibility = View.GONE
        binding!!.actionBar.buttonPDF.visibility = View.GONE
        binding!!.actionBar.textToolbarTitle.text = resources.getString(R.string.mark_attendance)
        selectImage(REQUEST_PICK_IMAGE_ONE)

        setUpData()
        setClickListener()
    }

    override fun onResume() {
        super.onResume()
        checkPermission()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val returnIntent = Intent()
        setResult(RESULT_CANCELED, returnIntent)
        finish()
    }

    private fun setClickListener() {
        binding!!.actionBar.ivBack.setOnClickListener(object : OnSingleClickListener() {
            override fun onSingleClick(v: View?) {
                val returnIntent = Intent()
                setResult(RESULT_CANCELED, returnIntent)
                finish()
            }
        })

        binding!!.buttonMarkAttendance.setOnClickListener(object : OnSingleClickListener() {
            override fun onSingleClick(v: View?) {
                binding!!.buttonMarkAttendance.isEnabled = false
                binding!!.buttonMarkAttendance.isClickable = false
                binding!!.buttonMarkAttendance.alpha = 0.5f

                if (challanImageStoragePath != "") {
                    if (latitude != 0.0 && longitude != 0.0) {
                        val USER_Id = preference!!.useR_Id
                        val currentTime = Calendar.getInstance()
                        val hourOfDay = currentTime[Calendar.HOUR_OF_DAY]
                        //  Log.d("sdfkdfd",""+hourOfDay);
                        if (hourOfDay > 20 || hourOfDay < 8) {
                            // Time is greater than or equal to 8 PM (20:00)
                            makeToast("Failed ! Attendance timing is 8 am to 8 Pm ")
                        } else if (USER_Id.isEmpty()) {
                            makeToast(resources.getString(R.string.UserId_empty))
                        } else if (completeAddressStr.isEmpty()) {
                            // makeToast(getResources().getString(R.string.address_empty));
                            //  markAttendance(USER_Id, "" + latitude, "" + longitude, "" + nearbyAddessStr);
                            theUserPermission
                            if (near_address!!.isEmpty()) {
                                makeToast("Proper network not available! Try after Sometime.")
                            } else {
                                markAttendance(
                                    USER_Id, "" + latitude, "" + longitude,
                                    near_address!!
                                )
                            }
                        } else {
                            markAttendance(
                                USER_Id,
                                "" + latitude,
                                "" + longitude,
                                completeAddressStr
                            )
                        }
                        //  Log.d("ALl DetailSub"," "+USER_Id+"  "+latitude+"  "+longitude+"  "+completeAddressStr);
                    } else {
                        makeToast("Location not found.")
                    }
                } else {
                    makeToast("Please capture image.")
                }
            }
        })


        binding!!.buttonPickImage.setOnClickListener(object : OnSingleClickListener() {
            override fun onSingleClick(v: View?) {
                selectImage(REQUEST_PICK_IMAGE_ONE)
            }
        })

        binding!!.imgSelfie.setOnClickListener(object : OnSingleClickListener() {
            override fun onSingleClick(v: View?) {
            }
        })
    }

    fun makeToast(string: String?) {
        if (TextUtils.isEmpty(string)) return
        Toast.makeText(mActivity, string, Toast.LENGTH_SHORT).show()
    }

    private fun selectImage(requestCode: Int) {
        with(mActivity!!)
            .setToolbarColor("#212121")
            .setStatusBarColor("#000000")
            .setToolbarTextColor("#FFFFFF")
            .setToolbarIconColor("#FFFFFF")
            .setProgressBarColor("#4CAF50")
            .setBackgroundColor("#212121")
            .setCameraOnly(true)
            .setMultipleMode(true)
            .setFolderMode(true)
            .setShowCamera(true)
            .setFolderTitle("Albums")
            .setImageTitle("Galleries")
            .setDoneTitle("Done")
            .setMaxSize(1)
            .setSavePath(Constants.saveImagePath)
            .setSelectedImages(ArrayList())
            .start(requestCode)
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_PICK_IMAGE_ONE && resultCode == RESULT_OK && data != null) {
            val images = data.getParcelableArrayListExtra<Image>(Config.EXTRA_IMAGES)
            if (images != null && images.size > 0) {
                val image = images[0]
                challanImageStoragePath = image.path
                if (challanImageStoragePath!!.contains("file:/")) {
                    challanImageStoragePath = challanImageStoragePath!!.replace("file:/", "")
                }
                challanImageStoragePath = compress(
                    challanImageStoragePath!!, this
                )
                val imgFile = File(challanImageStoragePath)
                val myBitmap = BitmapFactory.decodeFile(imgFile.absolutePath)
                try {
                    binding!!.imgSelfie.setImageBitmap(
                        ImageUtilsForRotate.ensurePortrait(
                            challanImageStoragePath!!
                        )
                    )
                } catch (e: NullPointerException) {
                    e.printStackTrace()
                } catch (e: IOException) {
                    binding!!.imgSelfie.setImageBitmap(myBitmap)

                    e.printStackTrace()
                }
                //   Log.d("imagesimages"," "+myBitmap);
                if (myBitmap != null) {
                    binding!!.buttonPickImage.visibility = View.GONE
                } else {
                    //now m
                    //  binding.buttonPickImage.setVisibility(View.VISIBLE);
                    val back_intent = Intent(mActivity, MainActivity::class.java)
                    startActivity(back_intent)
                }
            }
        }

        super.onActivityResult(requestCode, resultCode, data)
    }


    private fun checkPermission() {
        //  Log.d("versionversion"," "+Build.VERSION.SDK_INT);
        /* permissions =  new String[]{ Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.READ_MEDIA_VIDEO,Manifest.permission.CAMERA};
*/

        permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.READ_MEDIA_IMAGES,
                Manifest.permission.READ_MEDIA_VIDEO,
                Manifest.permission.CAMERA
            )

            //   Log.d("checkggg","b");
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
        Permissions.check(mActivity, permissions, rationale, options, object : PermissionHandler() {
            override fun onGranted() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    if (gpsStatus) turnOnGps()
                    else buildLocation()
                    //  Log.d("check ","fgfg");
                } else {
                    if (gpsStatus) turnOnGps()
                    else buildLocation()

                    //  Log.d("check ","jgbhk");
                }
            }

            override fun onDenied(context: Context, deniedPermissions: ArrayList<String>) {
                Toast.makeText(mActivity, "Permission denied", Toast.LENGTH_SHORT).show()
            }
        })
    }


    /*private final ActivityResultLauncher<Intent> takePictureLauncher = registerForActivityResult(
            new ActivityResultCallback<ActivityResult>
                    (){
                @Override
                public void onActivityResult(ActivityResult result) {
                    if(result.getResultCode()==RESULT_OK){

                    }
                    else

                }



            }
    );

   */
    private fun turnOnGps() {
        GpsUtils(mActivity!!).turnGPSOn { isGPSEnable: Boolean ->
            val isGPS = isGPSEnable
        }
    }

    private val gpsStatus: Boolean
        get() {
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
                        val locIndex = locationResult.locations.size - 1
                        latitude = locationResult.locations[locIndex].latitude
                        longitude = locationResult.locations[locIndex].longitude
                        val typeface = ResourcesCompat.getFont(mActivity!!, R.font.roboto_medium)

                        completeAddressStr = addressFromLatLong

                        // nearbyAddessStr = getNearbyAddress();
//                            binding.txtLatitude.setText("Latitude: " + latitude);
//                            binding.txtLongitude.setText("Longitude: " + longitude);
                    }
                }
            }, Looper.getMainLooper())
    }


    private val addressFromLatLong: String
        get() {
            var localCompleteAddressStr = ""
            try {
                val gcd = Geocoder(applicationContext, Locale.getDefault())
                //  Geocoder gcd = new Geocoder(getApplicationContext(), new Locale("hi"));
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

    /*  private String getNearbyAddress() {
    */
    /*String localCompleteAddressStr = "";
        String city = "";
        try {
            Geocoder gcd = new Geocoder(getApplicationContext(), Locale.getDefault());
            List<Address> addresses = gcd.getFromLocation(latitude, longitude, 1);
            if (addresses.size() > 0) {
                localCompleteAddressStr = "" + addresses.get(0).getAddressLine(0);
                city = "" + addresses.get(0).getAdminArea();
            }
        } catch (Exception e) {
            e.printStackTrace();
            localCompleteAddressStr = "";
            city = "";
        }
        return city;*/
    /*
        String nearbyAddressLine = "";
        double nearbyLatitude = latitude;
        double nearbyLongitude = longitude;
        // Perform reverse geocoding for nearby location
        try {
            Geocoder geocoder = new Geocoder(getApplicationContext());
            List<Address> nearbyAddresses = geocoder.getFromLocation(nearbyLatitude, nearbyLongitude, 1);
            if (nearbyAddresses != null && !nearbyAddresses.isEmpty()) {
                // Nearby address found, use it as a fallback
                Address nearbyAddress = nearbyAddresses.get(0);
               nearbyAddressLine = nearbyAddress.getAddressLine(0);
                // Example: Display the nearby address in a TextView
           //     addressTextView.setText("Nearby Address: " + nearbyAddressLine);
            } else {
                // Handle the case where no nearby address was found
                // Example: Display an error message or inform the user
              // addressTextView.setText("No address found nearby.");
            }
        } catch (IOException e) {
            e.printStackTrace();
            // Handle geocoding errors here
        }

        return nearbyAddressLine;


    }
*/
    private fun markAttendance(
        UserID: String,
        Latitude: String,
        Longitude: String,
        Address: String
    ) {
        mapLink = "http://maps.google.com/maps?q=$Latitude,$Longitude"
        Log.d("UserID----", "  $UserID")
        // Log.d("AddressIs---->","  "+Address);
        // Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(mapLink));
        // startActivity(intent);
        if (isNetworkAvailable(mActivity!!)) {
            hideKeyboard(mActivity!!)
            showCustomProgressDialogCommonForAll(
                mActivity!!,
                resources.getString(R.string.please_wait)
            )
            val apiInterface = getRetrofitClientWithoutHeaders(
                mActivity!!,
                BaseUrl()!!
            ).create(
                APIInterface::class.java
            )
            val attachment: RequestBody
            var fileName = ""
            if (challanImageStoragePath != "") {
                fileName = File(challanImageStoragePath).name
                attachment = RequestBody.create(
                    "multipart/form-data".toMediaTypeOrNull(),
                    File(challanImageStoragePath)
                )
            } else {
                fileName = ""
                attachment = RequestBody.create("text/plain".toMediaTypeOrNull(), "")
            }
            val call = apiInterface.callMarkAttendanceApi(
                MArkAttendance(),
                fromString(UserID),
                fromString(Latitude),
                fromString(Longitude),
                fromString(Address),  //  MultipartRequester.fromString(encodeToUtf8Hex(Address)),
                fromString(mapLink!!),
                createFormData("image", fileName, attachment)
            )
            call!!.enqueue(object : Callback<ModelMarkAttendance?> {
                override fun onResponse(
                    call: Call<ModelMarkAttendance?>,
                    response: Response<ModelMarkAttendance?>
                ) {
                    hideCustomProgressDialogCommonForAll()
                    if (response.isSuccessful) {
                        Log.d("response----", "  " + response.body()!!.message)
                        Log.d("mapLink----", "  $mapLink")

                        if (response.code() == 200) {
                            if (response.body() != null) {
                                if (response.body()!!.status == "200") {
                                    makeToast(response.body()!!.message)
                                    val returnIntent = Intent()
                                    setResult(RESULT_OK, returnIntent)
                                    finish()
                                } else {
                                    makeToast(response.body()!!.message)
                                }
                            } else {
                                makeToast(resources.getString(R.string.error))
                            }
                        } else {
                            makeToast(resources.getString(R.string.error))
                        }
                    } else {
                        makeToast(resources.getString(R.string.error))
                    }
                    binding!!.buttonMarkAttendance.isEnabled = true
                    binding!!.buttonMarkAttendance.isClickable = true
                }

                override fun onFailure(call: Call<ModelMarkAttendance?>, error: Throwable) {
                    hideCustomProgressDialogCommonForAll()
                    makeToast(resources.getString(R.string.error))
                    call.cancel()
                    binding!!.buttonMarkAttendance.isEnabled = true
                    binding!!.buttonMarkAttendance.isClickable = true
                }
            })
        } else {
            makeToast(resources.getString(R.string.no_internet_connection))
        }
    }


    private val theUserPermission: Unit
        /*    private String encodeUTF8(String value) {
        
        
        
        
                    try {
                        return new String(value.getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8);
                    } catch (Exception e) {
                        e.printStackTrace();
                        return value;
                    }
        
        
        
        
        
            }*/
        get() {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_LOCATION
            )
            locationManager =
                getSystemService(LOCATION_SERVICE) as LocationManager
            val locationGetter =
                LocationGetter(
                    this@MarkAttendanceActivity,
                    REQUEST_LOCATION,
                    locationManager
                )
            if (!locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                locationGetter.OnGPS()
            } else {
                locationGetter.location
            }
        }


    inner class LocationGetter(
        private val mContext: MarkAttendanceActivity,
        private val REQUEST_LOCATION: Int,
        private val locationManager: LocationManager?
    ) {
        private var geocoder: Geocoder? = null

        val location: Unit
            get() {
                if (ActivityCompat.checkSelfPermission(
                        this@MarkAttendanceActivity,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        mContext,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    ActivityCompat.requestPermissions(
                        this@MarkAttendanceActivity,
                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                        REQUEST_LOCATION
                    )
                } else {
                    val LocationGps =
                        locationManager!!.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                    val LocationNetwork =
                        locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                    val LocationPassive =
                        locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER)
                    if (LocationGps != null) {
                        val lat = LocationGps.latitude
                        val longi = LocationGps.longitude
                        getTheAddress(lat, longi)
                    } else if (LocationNetwork != null) {
                        val lat = LocationNetwork.latitude
                        val longi = LocationNetwork.longitude
                        getTheAddress(lat, longi)
                    } else if (LocationPassive != null) {
                        val lat = LocationPassive.latitude
                        val longi = LocationPassive.longitude
                        getTheAddress(lat, longi)
                    } else {
                        Toast.makeText(
                            this@MarkAttendanceActivity,
                            "Can't Get Your Location",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }

        private fun getTheAddress(latitude: Double, longitude: Double) {
            val addresses: List<Address>?
            geocoder = Geocoder(this@MarkAttendanceActivity, Locale.getDefault())
            try {
                addresses = geocoder!!.getFromLocation(latitude, longitude, 1)
                near_address = addresses!![0].getAddressLine(0)
                val city = addresses[0].locality
                val state = addresses[0].adminArea
                val country = addresses[0].countryName
                val postalCode = addresses[0].postalCode
                val knownName = addresses[0].featureName
                //   Log.d("neel", near_address);
                //   Log.d("your location","  "+near_address);
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        fun OnGPS() {
            val builder = AlertDialog.Builder(mContext)
            builder.setMessage("Enable GPS").setCancelable(false).setPositiveButton(
                "YES"
            ) { dialog, which ->
                mContext.startActivity(
                    Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                )
            }.setNegativeButton(
                "NO"
            ) { dialog, which -> dialog.cancel() }
            val alertDialog = builder.create()
            alertDialog.show()
        }
    }


    companion object {
        private const val REQUEST_CODE_OPEN_DOCUMENT = 1
        private const val REQUEST_LOCATION = 123 // You can use any integer value

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


