package com.callmangement.ehr.ehrActivities

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.ResultReceiver
import android.provider.Settings
import android.text.TextUtils
import android.util.Base64
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.callmangement.R
import com.callmangement.custom.CustomActivity
import com.callmangement.databinding.ActivityFeedbackFormBinding
import com.callmangement.ehr.models.FeedbackDetailByFpsRoot
import com.callmangement.ehr.models.SaveFeedbackbyDCRoot
import com.callmangement.imagepicker.model.Config
import com.callmangement.imagepicker.model.Image
import com.callmangement.imagepicker.ui.imagepicker.ImagePicker
import com.callmangement.network.APIService
import com.callmangement.network.MultipartRequester
import com.callmangement.network.RetrofitInstance
import com.callmangement.support.FetchAddressIntentServices
import com.callmangement.support.ImageUtilsForRotate
import com.callmangement.support.OnSingleClickListener
import com.callmangement.support.signatureview.SignatureView
import com.callmangement.utils.CompressImage.Companion.compress
import com.callmangement.utils.Constants
import com.callmangement.utils.Constants.isNetworkAvailable
import com.callmangement.utils.PrefManager
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.MultipartBody.Part.Companion.createFormData
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.Objects

class FeedbackFormActivity : CustomActivity() {

    val REQUEST_PICK_IMAGE_ONE: Int = 1111
    val REQUEST_PICK_IMAGE_TWO: Int = 1112
    val REQUEST_PICK_IMAGE_THREE: Int = 1113
    val REQUEST_PICK_IMAGE_FOUR: Int = 1114
    val REQUEST_PICK_IMAGE_FIVE: Int = 1115
    private val spinnerList: List<String> = ArrayList()
    private val myFormat = "yyyy-MM-dd"
    private var mActivity: Activity? = null
    private var preference: PrefManager? = null
    private var txt_fpscode: EditText? = null
    private var Fps_code: String? = null
    private var lati = 0.0
    private var longi = 0.0
    private val stringArrayListHavingAllFilePath = ArrayList<String>()
    private var fullAddress: String? = null
    private var resultReceiver: ResultReceiver? = null
    private var completeAddressStr: String? = ""
    private var mSignaturePad: SignatureView? = null
    private val Model: String? = null
    private val SerialNo: String? = null
    private var Mobile_No: String? = null
    private var FPS_CODE: String? = null
    private var Full_Address: String? = null
    private var Lati: String? = null
    private var Longi: String? = null
    private var Remarks: String? = null
    private var Delivered_On: String? = null
    private var IsDeliverd_IRIS: String? = null
    private var IsDeliverd_WeighingScale: String? = null
    private var alertDialog: AlertDialog? = null
    private var encodedSignature: String? = null
    private var signatureBitmap: Bitmap? = null
    private var attachmentPartsImage6: RequestBody? = null
    private var signatureRequestBody: RequestBody? = null
    private var formattedDateTime: String? = null
    private var mydatetime: String? = null
    private var binding: ActivityFeedbackFormBinding? = null
    private var partsImageStoragePath1 = ""
    private var partsImageStoragePath2 = ""
    private var partsImageStoragePath3 = ""
    private var dateAndTimeEditText: EditText? = null
    private var selectedDateTime: Calendar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE) //will hide the title
        //        getSupportActionBar().hide(); // hide the title bar
        binding = ActivityFeedbackFormBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        //  setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        init()
    }

    private fun init() {
        mActivity = this
        mContext = this
        preference = PrefManager(mActivity!!)
        resultReceiver = AddressResultReceiver(Handler())
        resultReceiver = AddressResultReceiver(Handler())
        binding!!.actionBarND.ivBack.visibility = View.VISIBLE
        binding!!.actionBarND.textToolbarTitle.text = resources.getString(R.string.feedback_form)
        dateAndTimeEditText = findViewById(R.id.dateAndTimeEditText)
        selectedDateTime = Calendar.getInstance()
        CoustomDialoge()
        setClickListener()
    }


    private fun setClickListener() {
        binding!!.linChooseImages1.setOnClickListener(object : OnSingleClickListener() {
            override fun onSingleClick(v: View) {
                selectImage(REQUEST_PICK_IMAGE_ONE)
            }
        })

        binding!!.linChooseImages2.setOnClickListener(object : OnSingleClickListener() {
            override fun onSingleClick(v: View) {
                selectImage(REQUEST_PICK_IMAGE_TWO)
            }
        })

        binding!!.linChooseImages3.setOnClickListener(object : OnSingleClickListener() {
            override fun onSingleClick(v: View) {
                selectImage(REQUEST_PICK_IMAGE_THREE)
            }
        })



        binding!!.actionBarND.ivBack.setOnClickListener(object : OnSingleClickListener() {
            override fun onSingleClick(v: View) {
                if (alertDialog != null && alertDialog!!.isShowing) {
                    // Dismiss the dialog if it's showing
                    alertDialog!!.dismiss()
                    val i = Intent(mContext!!, MainActivity::class.java)
                    startActivity(i)
                } else {
                    onBackPressed()
                }
            }
        })

        binding!!.linChooseImagessig.setOnClickListener { view: View? ->
            //     binding.actionFullpage.sigpage.setVisibility(View.VISIBLE);
            //    binding.scroolview.setVisibility(View.GONE);
            signatureDialoge()
        }




        binding!!.buttonSubmit.setOnClickListener { view: View? ->
            //    selectedValue = binding.inputmodel.getSelectedItem().toString();
            //   Model = binding.inputmodel.getText().toString();
            Mobile_No = binding!!.inputMobile.text.toString()
            FPS_CODE = binding!!.inputFpsCode.text.toString()
            Full_Address = fullAddress
            Lati = lati.toString()
            Longi = longi.toString()
            Remarks = binding!!.inputfeedbackreview.text.toString()
            val calendar = Calendar.getInstance()
            val today = calendar.time
            val sdf = SimpleDateFormat(myFormat, Locale.US)
            val todayDate = sdf.format(today)
            Delivered_On = todayDate
            IsDeliverd_IRIS = "0"
            IsDeliverd_WeighingScale = "1"

            //    Log.d("Mobile_No"," "+ Mobile_No);

            /*       if (selectItem == null || selectedValue.equals(tv_select) || selectedValue.isEmpty() || selectedValue.length() == 0) {
                       makeToast(getResources().getString(R.string.select_errortype));
                   }
               else*/
            if (FPS_CODE == null || FPS_CODE!!.isEmpty() || FPS_CODE!!.length == 0) {
                //  makeToast(getResources().getString(R.string.enter_fps_code));
                binding!!.inputFpsCode.error = "error"
                val msg = resources.getString(R.string.enter_fps_code)
                showAlertDialogWithSingleButton(mActivity, msg)
            } /*else if (Model == null || Model.isEmpty() || Model.length() == 0 || Model.equals("-Select Model-")) {
                binding.ivTvspinner.setError("एरर");
                binding.ivTvspinner.setVisibility(View.VISIBLE);
                //      makeToast(getResources().getString(R.string.please_select_model));
                String msg = getResources().getString(R.string.please_select_model);
                showAlertDialogWithSingleButton(mActivity, msg);
            } else if (SerialNo == null || SerialNo.isEmpty() || SerialNo.length() == 0) {
                // makeToast(getResources().getString(R.string.please_select_serialno));
                // binding.inputSerialno.requestFocus();

                //     binding.inputSerialno.setError("एरर");
                String msg = getResources().getString(R.string.please_select_serialno);
                showAlertDialogWithSingleButton(mActivity, msg);

            }*/ else if (Mobile_No == null || Mobile_No!!.isEmpty() || Mobile_No!!.length != 10) {
                //makeToast(getResources().getString(R.string.enter_your_exact_mobile_no));
                binding!!.inputMobile.error = "error"
                val msg = resources.getString(R.string.enter_your_exact_mobile_no)
                showAlertDialogWithSingleButton(mActivity, msg)
            } else if (stringArrayListHavingAllFilePath == null || stringArrayListHavingAllFilePath.size < 2) {
                //  makeToast(getResources().getString(R.string.please_select_all_img));

                val msg = resources.getString(R.string.please_select_all_img)
                showAlertDialogWithSingleButton(mActivity, msg)

                //  binding.inputSerialno.requestFocus();
            } else if (signatureRequestBody == null) {
                // makeToast(getResources().getString(R.string.please_sign));
                //  binding.inputSerialno.requestFocus();
                val msg = resources.getString(R.string.please_sign)
                showAlertDialogWithSingleButton(mActivity, msg)
            } else if (Remarks == null || Remarks!!.isEmpty() || Remarks!!.length == 0) {
                //makeToast(getResources().getString(R.string.enter_your_exact_mobile_no));
                binding!!.inputfeedbackreview.error = "error"
                val msg = resources.getString(R.string.enterfeedback)
                showAlertDialogWithSingleButton(mActivity, msg)
            } else {
                checkLocationServices()
            }
        }
    }


    private fun checkLocationServices() {
        if (ContextCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                mActivity!!,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
            val msg =
                "Not able to get your location! Please Turn On your Location and App Permissions"
            showAlertDialogWithSingleButton(mActivity, msg)
        } else {
            val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
            val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
            val isNetworkEnabled =
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
            if (isGpsEnabled || isNetworkEnabled) {
                currentLocation
                // Location services are enabled
            } else {
                showSettingsAlert()
            }
        }
        //for phone location
    }

    fun showSettingsAlert() {
        val alertDialog = AlertDialog.Builder(this)
        // Setting Dialog Title
        alertDialog.setTitle("Permission necessary")
        // Setting Dialog Message
        alertDialog.setMessage("Not able to get your location! Please Turn On your Location and App Permissions")
        // On pressing Settings button
        alertDialog.setPositiveButton(
            resources.getString(R.string.button_ok)
        ) { dialog, which ->
            val intent = Intent(
                Settings.ACTION_LOCATION_SOURCE_SETTINGS
            )
            startActivity(intent)
        }
        alertDialog.show()
    }

    private fun signatureDialoge() {
        val dialogBuilder = AlertDialog.Builder(this)
        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_full_page_iris, null)
        mSignaturePad = dialogView.findViewById(R.id.signature_pad)
        dialogBuilder.setView(dialogView)
        val alertDialog = dialogBuilder.create()
        // Optional: Set dialog properties if needed
        // For example: alertDialog.setCancelable(false);
        alertDialog.show()

        dialogView.findViewById<View>(R.id.btn_clear)
            .setOnClickListener(object : OnSingleClickListener() {
                override fun onSingleClick(v: View) {
                    mSignaturePad!!.clearCanvas()
                }
            })

        dialogView.findViewById<View>(R.id.back)
            .setOnClickListener(object : OnSingleClickListener() {
                override fun onSingleClick(v: View) {
                    alertDialog.dismiss()
                }
            })

        dialogView.findViewById<View>(R.id.btn_clear)
            .setOnClickListener(object : OnSingleClickListener() {
                override fun onSingleClick(v: View) {
                    mSignaturePad!!.clearCanvas()
                }
            })

        dialogView.findViewById<View>(R.id.btn_okay)
            .setOnClickListener(object : OnSingleClickListener() {
                override fun onSingleClick(v: View) {
                    signatureBitmap = rotateBitmap(
                        mSignaturePad!!.getSignatureBitmap()!!,
                        90f
                    ) // Get the signature as a Bitmap
                    encodedSignature = encodeBitmapToBase64(signatureBitmap)
                    attachmentPartsImage6 = RequestBody.create(
                        "text/plain".toMediaTypeOrNull(),
                        encodedSignature!!
                    )
                    val signatureFile = createFileFromBitmap(signatureBitmap)
                    // Create a request body for the signature image
                    signatureRequestBody =
                        RequestBody.create("image/*".toMediaTypeOrNull(), signatureFile!!)
                    // Create MultipartBody.Part for the signature image
                    val signaturePart: MultipartBody.Part = createFormData(
                        "DCVisitingFeedbackDealerSignature",
                        "signature.png",
                        signatureRequestBody!!
                    )
                    binding!!.ivPartsImagesig.setImageBitmap(signatureBitmap)
                    alertDialog.dismiss()
                }
            })

        // Optional: Customize dialog window properties (size, position, etc.)
        val window = alertDialog.window
        window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
    }

    private fun createFileFromBitmap(bitmap: Bitmap?): File? {
        try {
            val file = File(cacheDir, "signature.png")
            file.createNewFile()
            if (bitmap != null) {
                // Convert bitmap to byte array
                val bos = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.PNG, 0,  /*ignored for PNG*/bos)
                val bitmapData = bos.toByteArray()
                // Write the bytes in file
                val fos = FileOutputStream(file)
                fos.write(bitmapData)
                fos.flush()
                fos.close()
                return file
            } else {
            }
        } catch (e: IOException) {
            e.printStackTrace()
            return null
        }
        return null
    }

    private fun encodeBitmapToBase64(bitmap: Bitmap?): String? {
        val baos = ByteArrayOutputStream()
        if (bitmap != null) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos)
            val byteArrayImage = baos.toByteArray()
            return Base64.encodeToString(byteArrayImage, Base64.DEFAULT)
        } else {
        }
        return null
    }

    private val currentLocation: Unit
        get() {
            // progressBar.setVisibility(View.VISIBLE);
            showProgress(resources.getString(R.string.get_location))
            val locationRequest =
                LocationRequest()
            locationRequest.setInterval(10000)
            locationRequest.setFastestInterval(3000)
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)

            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return
            }
            LocationServices.getFusedLocationProviderClient(mActivity!!)
                .requestLocationUpdates(locationRequest, object : LocationCallback() {
                    override fun onLocationResult(locationResult: LocationResult) {
                        super.onLocationResult(locationResult)
                        LocationServices.getFusedLocationProviderClient(applicationContext)
                            .removeLocationUpdates(this)
                        if (locationResult != null && locationResult.locations.size > 0) {
                            hideProgress()
                            val latestlocIndex = locationResult.locations.size - 1
                            lati = locationResult.locations[latestlocIndex].latitude
                            longi = locationResult.locations[latestlocIndex].longitude
                            //    textLatLong.setText(String.format("Latitude : %s\n Longitude: %s", lati, longi));
                            //my
                            val location =
                                Location("providerNA")
                            location.longitude = longi
                            location.latitude = lati
                            fetchaddressfromlocation(location)
                            //    Log.d("locationlocation", "" + location);
                            completeAddressStr = addressFromLatLong
                            //   Log.d("completeAddressStr", completeAddressStr);
                            hideProgress()

                            if (completeAddressStr != null && completeAddressStr!!.length != 0) {
                                saveReq(
                                    FPS_CODE,
                                    Model,
                                    SerialNo,
                                    Mobile_No,
                                    stringArrayListHavingAllFilePath
                                )

                                //   Toast.makeText(mActivity, "Feedback Submitted", Toast.LENGTH_SHORT).show();

                                // Intent i = new Intent(FeedbackFormActivity.this,MainActivity.class);
                                //startActivity(i);
                            } else {
                                val msg =
                                    "Not able to get your location! Please Turn On your Location and App Permissions"
                                showAlertDialogWithSingleButton(mActivity, msg)
                            }
                        } else {
                            hideProgress()
                        }
                    }
                }, Looper.getMainLooper())
        }

    private fun fetchaddressfromlocation(location: Location) {
        val intent = Intent(this, FetchAddressIntentServices::class.java)
        intent.putExtra(Constants.RECEVIER, resultReceiver)
        intent.putExtra(Constants.LOCATION_DATA_EXTRA, location)
        startService(intent)
    }

    private val addressFromLatLong: String
        get() {
            var localCompleteAddressStr = ""
            try {
                val gcd = Geocoder(applicationContext, Locale.getDefault())
                val addresses =
                    gcd.getFromLocation(lati, longi, 1)
                if (addresses!!.size > 0) {
                    localCompleteAddressStr = addresses[0].getAddressLine(0)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                localCompleteAddressStr = ""
            }
            return localCompleteAddressStr
        }


    private fun CoustomDialoge() {
        val alert = AlertDialog.Builder(mContext!!)
        val mView = layoutInflater.inflate(R.layout.activity_coustomfpsdialoge, null)
        //  mView.setBackground(ContextCompat.getDrawable(this, R.drawable.rounded_dialog_bg));
        txt_fpscode = mView.findViewById(R.id.txt_input)
        val btn_cancel = mView.findViewById<Button>(R.id.btn_cancel)
        btn_cancel.visibility = View.VISIBLE
        val btn_okay = mView.findViewById<Button>(R.id.btn_okay)
        alert.setView(mView)
        alertDialog = alert.create()
        alertDialog!!.setCanceledOnTouchOutside(false)

        //alertDialog.getWindow().setBackgroundDrawable(new BitmapDrawable(getResources(), blurredBitmap));
        btn_cancel.setOnClickListener { /* InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                    alertDialog.dismiss();*/
            val i = Intent(mContext!!, MainActivity::class.java)
            startActivity(i)
        }

        btn_okay.setOnClickListener {
            //    myCustomMessage.setText(txt_inputText.getText().toString());
            val imm =
                getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
            GetCustomerDetailsByFPS()
            //  alertDialog.dismiss();
        }

        alertDialog!!.setOnKeyListener(DialogInterface.OnKeyListener { dialog, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_UP) {
                dialog.dismiss()
                finish()
            }
            false
        })
        alertDialog!!.show()
    }

    private fun GetCustomerDetailsByFPS() {
        Fps_code = txt_fpscode!!.text.toString().trim { it <= ' ' }
        if (isNetworkAvailable(mActivity!!)) {
            hideKeyboard(mActivity)
            showProgress(resources.getString(R.string.please_wait))
            val USER_Id = preference!!.useR_Id
            //   Log.d("USER_ID", " "+USER_Id);
            val apiInterface = RetrofitInstance.getRetrofitInstance().create(
                APIService::class.java
            )
            val call = apiInterface.apiDetailsByFPSToFeedback(Fps_code, USER_Id, "0")
            call.enqueue(object : Callback<FeedbackDetailByFpsRoot?> {
                override fun onResponse(
                    call: Call<FeedbackDetailByFpsRoot?>,
                    response: Response<FeedbackDetailByFpsRoot?>
                ) {
                    hideProgress()
                    if (response.isSuccessful) {
                        if (response.code() == 200) {
                            if (response.body() != null) {
                                if (response.body()!!.status == "200") {
                                    alertDialog!!.dismiss()
                                    val detailByFpsRoot = response.body()
                                    val fps_message = detailByFpsRoot!!.message
                                    Toast.makeText(mContext!!, fps_message, Toast.LENGTH_SHORT).show()
                                    val detailByFpsData = detailByFpsRoot.data
                                    if (detailByFpsData != null) {
                                        val DistrictName = detailByFpsData.districtName
                                        val Fpscode = detailByFpsData.fpscode
                                        val DealerName = detailByFpsData.dealerName
                                        val FpsdeviceCode = detailByFpsData.fpsdeviceCode
                                        val DealerMobileNo = detailByFpsData.dealerMobileNo
                                        val Block = detailByFpsData.blockName
                                        binding!!.inputDealerName.text = DealerName
                                        binding!!.inputDistrict.text = DistrictName
                                        binding!!.inputFpsCode.text = Fpscode
                                        binding!!.inputMobile.setText(DealerMobileNo)
                                        binding!!.inputBlock.text = Block
                                        val currentDate = Date()
                                        // Define the desired date format
                                        val dateFormat =
                                            SimpleDateFormat("HH:mm", Locale.getDefault())
                                        val formattedDate = dateFormat.format(currentDate)

                                        dateAndTimeEditText!!.setText(formattedDate)
                                        if (ContextCompat.checkSelfPermission(
                                                applicationContext,
                                                Manifest.permission.ACCESS_FINE_LOCATION
                                            )
                                            != PackageManager.PERMISSION_GRANTED
                                        ) {
                                            ActivityCompat.requestPermissions(
                                                mActivity!!,
                                                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                                                LOCATION_PERMISSION_REQUEST_CODE
                                            )
                                        } else {
                                        }
                                    } else {
                                        val msg = response.body()!!.message
                                        showAlertDialogWithSingleButton(mActivity, msg)
                                        //  makeToast(String.valueOf(response.body().getMessage()));
                                        // Handle the case when countDatum is empty or null
                                    }
                                } else {
                                    alertDialog!!.show()
                                    //  makeToast(String.valueOf(response.body().getMessage()));
                                    val msg = response.body()!!.message
                                    showAlertDialogWithSingleButton(mActivity, msg)
                                    // Handle the case when the response status is not 200 or the response body is null
                                }
                            } else {
                                val msg = response.body()!!.message
                                showAlertDialogWithSingleButton(mActivity, msg)
                                // makeToast(String.valueOf(response.body().getMessage()));
                            }
                        } else {
                            val msg = "HTTP Error: " + response.code()
                            showAlertDialogWithSingleButton(mActivity, msg)
                        }
                    } else {
                        val msg = "HTTP Error: " + response.code()
                        showAlertDialogWithSingleButton(mActivity, msg)
                        // makeToast(getResources().getString(R.string.error));
                    }
                }

                override fun onFailure(call: Call<FeedbackDetailByFpsRoot?>, error: Throwable) {
                    hideProgress()
                    // makeToast(getResources().getString(R.string.error));
                    val msg = error.message
                    showAlertDialogWithSingleButton(mActivity, msg)
                    call.cancel()
                }
            })
        } else {
            makeToast(resources.getString(R.string.no_internet_connection))
        }
    }

    private fun selectImage(requestCode: Int) {
        try {
            ImagePicker.with(mActivity)
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
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        /* if (requestCode == CAMERA_REQUEST_CODE) {
                   if (ContextCompat.checkSelfPermission(
                           this,
                           Manifest.permission.CAMERA
                   ) == PackageManager.PERMISSION_GRANTED
                   ) {
                       openCameraWithScanner();
                   }
               }*/

        if (requestCode == REQUEST_PICK_IMAGE_ONE && resultCode == RESULT_OK && data != null) {
            val images = data.getParcelableArrayListExtra<Image>(Config.EXTRA_IMAGES)
            if (images != null && images.size > 0) {
                val image = images[0]
                // String imageStoragePath = image.getPath();
                partsImageStoragePath1 = image.path
                if (partsImageStoragePath1.contains("file:/")) {
                    partsImageStoragePath1 = partsImageStoragePath1.replace("file:/", "")
                }
                partsImageStoragePath1 = compress(
                    partsImageStoragePath1,
                    this
                )
                val imgFile = File(partsImageStoragePath1)
                val myBitmap = BitmapFactory.decodeFile(imgFile.absolutePath)
                try {
                    binding!!.ivPartsImage1.setImageBitmap(
                        ImageUtilsForRotate.ensurePortrait(
                            partsImageStoragePath1
                        )
                    )
                    stringArrayListHavingAllFilePath.add(partsImageStoragePath1)
                } catch (e: IOException) {
                    binding!!.ivPartsImage1.setImageBitmap(myBitmap)
                    stringArrayListHavingAllFilePath.add(partsImageStoragePath1)

                    e.printStackTrace()
                } catch (e: NullPointerException) {
                    e.printStackTrace()
                }
            }
        } else if (requestCode == REQUEST_PICK_IMAGE_TWO && resultCode == RESULT_OK && data != null) {
            val images = data.getParcelableArrayListExtra<Image>(Config.EXTRA_IMAGES)
            if (images != null && images.size > 0) {
                val image = images[0]
                val imageStoragePath = image.path
                partsImageStoragePath2 = image.path
                if (partsImageStoragePath2.contains("file:/")) {
                    partsImageStoragePath2 = partsImageStoragePath2.replace("file:/", "")
                }
                partsImageStoragePath2 = compress(
                    partsImageStoragePath2,
                    this
                )
                val imgFile = File(partsImageStoragePath2)
                val myBitmap = BitmapFactory.decodeFile(imgFile.absolutePath)

                try {
                    binding!!.ivPartsImage2.setImageBitmap(
                        ImageUtilsForRotate.ensurePortrait(
                            partsImageStoragePath2
                        )
                    )
                    stringArrayListHavingAllFilePath.add(partsImageStoragePath2)
                } catch (e: IOException) {
                    binding!!.ivPartsImage2.setImageBitmap(myBitmap)
                    stringArrayListHavingAllFilePath.add(partsImageStoragePath2)
                    e.printStackTrace()
                } catch (e: NullPointerException) {
                    e.printStackTrace()
                }
            }
        } else if (requestCode == REQUEST_PICK_IMAGE_THREE && resultCode == RESULT_OK && data != null) {
            val images = data.getParcelableArrayListExtra<Image>(Config.EXTRA_IMAGES)
            if (images != null && images.size > 0) {
                val image = images[0]
                //  String imageStoragePath = image.getPath();
                partsImageStoragePath3 = image.path
                if (partsImageStoragePath3.contains("file:/")) {
                    partsImageStoragePath3 = partsImageStoragePath3.replace("file:/", "")
                }
                partsImageStoragePath3 = compress(
                    partsImageStoragePath3,
                    this
                )
                val imgFile = File(partsImageStoragePath3)
                val myBitmap = BitmapFactory.decodeFile(imgFile.absolutePath)

                try {
                    binding!!.ivPartsImage3.setImageBitmap(
                        ImageUtilsForRotate.ensurePortrait(
                            partsImageStoragePath3
                        )
                    )
                    stringArrayListHavingAllFilePath.add(partsImageStoragePath3)
                } catch (e: IOException) {
                    binding!!.ivPartsImage3.setImageBitmap(myBitmap)
                    stringArrayListHavingAllFilePath.add(partsImageStoragePath3)
                    e.printStackTrace()
                } catch (e: NullPointerException) {
                    e.printStackTrace()
                }
            }
        }




        super.onActivityResult(requestCode, resultCode, data)
    }


    private fun saveReq(
        fps_code: String?,
        model: String?,
        serialNo: String?,
        mobile_no: String?,
        arrayHavingAllFilePath: ArrayList<String>
    ) {
        if (isNetworkAvailable(mActivity!!)) {
            hideKeyboard(mActivity)
            showProgress(resources.getString(R.string.please_wait))
            val USER_Id = preference!!.useR_Id
            //   Log.d("USER_ID"," "+ USER_Id);
            mydatetime = dateAndTimeEditText!!.text.toString()

            val apiInterface = RetrofitInstance.getRetrofitInstance().create(
                APIService::class.java
            )
            //   Log.d("apifullapifullapifull", " "+fullAddress);
            val attachmentPartsImage1: RequestBody
            val attachmentPartsImage2: RequestBody
            var attachmentPartsImage3: RequestBody
            var attachmentPartsImage4: RequestBody
            var attachmentPartsImage5: RequestBody

            var fileNamePartsImage1 = ""
            var fileNamePartsImage2 = ""
            val fileNamePartsImage3 = ""
            val fileNamePartsImage4 = ""
            val fileNamePartsImage5 = ""

            if (partsImageStoragePath1 != "") {
                fileNamePartsImage1 = File(partsImageStoragePath1).name
                attachmentPartsImage1 = RequestBody.create(
                    "multipart/form-data".toMediaTypeOrNull(),
                    File(partsImageStoragePath1)
                )
            } else {
                fileNamePartsImage1 = ""
                attachmentPartsImage1 = RequestBody.create("text/plain".toMediaTypeOrNull(), "")
            }

            if (partsImageStoragePath2 != "") {
                fileNamePartsImage2 = File(partsImageStoragePath2).name
                attachmentPartsImage2 = RequestBody.create(
                    "multipart/form-data".toMediaTypeOrNull(),
                    File(partsImageStoragePath2)
                )
            } else {
                fileNamePartsImage2 = ""
                attachmentPartsImage2 = RequestBody.create("text/plain".toMediaTypeOrNull(), "")
            }

            /*

            if (!partsImageStoragePath3.equals("")) {
                fileNamePartsImage3 = new File(partsImageStoragePath3).getName();
                attachmentPartsImage3 = RequestBody.create(MediaType.parse("multipart/form-data"), new File(partsImageStoragePath3));
            } else {
                fileNamePartsImage3 = "";
                attachmentPartsImage3 = RequestBody.create(MediaType.parse("text/plain"), "");
            }*/

            /*    if (!partsImageStoragePath4.equals("")) {
                fileNamePartsImage4 = new File(partsImageStoragePath4).getName();
                attachmentPartsImage4 = RequestBody.create(MediaType.parse("multipart/form-data"), new File(partsImageStoragePath4));
            } else {
                fileNamePartsImage4 = "";
                attachmentPartsImage4 = RequestBody.create(MediaType.parse("text/plain"), "");
            }*/

            /* if (!partsImageStoragePath5.equals("")) {
                fileNamePartsImage5 = new File(partsImageStoragePath5).getName();
                attachmentPartsImage6= RequestBody.create(MediaType.parse("multipart/form-data"), new File(partsImageStoragePath5));
            } else {
                fileNamePartsImage5 = "";
                attachmentPartsImage6 = RequestBody.create(MediaType.parse("text/plain"), encodedSignature);
            }*/
            val call = apiInterface.saveVisitFeedbackbyDC(
                MultipartRequester.fromString(USER_Id),
                MultipartRequester.fromString(fps_code),
                MultipartRequester.fromString(completeAddressStr),
                MultipartRequester.fromString(lati.toString()),
                MultipartRequester.fromString(longi.toString()),
                MultipartRequester.fromString(Remarks),
                MultipartRequester.fromString(mydatetime),
                createFormData(
                    "SelpiheWithFPSNoAndDealer",
                    fileNamePartsImage1,
                    attachmentPartsImage1
                ),
                createFormData(
                    "DCVisitingChallan",
                    fileNamePartsImage2,
                    attachmentPartsImage2
                ),
                createFormData(
                    "DCVisitingFeedbackDealerSignature",
                    "signature.png",
                    signatureRequestBody!!
                )
            )

            //campDocumentsParts);
            Log.d("sendResponse", "-----USER_Id$USER_Id")
            Log.d("sendResponse", "-----model$model")
            Log.d("sendResponse", "-----serialNo$serialNo")
            Log.d("sendResponse", "-----" + "bvcnvbn")
            Log.d("sendResponse", "-----" + "vbnn")
            Log.d("sendResponse", "-----mobile_no$mobile_no")
            Log.d("sendResponse", "-----fps_code$fps_code")
            Log.d("sendResponse", "-----completeAddressStr$completeAddressStr")
            Log.d("sendResponse", "-----lati$lati")
            Log.d("sendResponse", "-----longi$longi")
            Log.d("sendResponse", "-----Remarks$Remarks")
            Log.d("sendResponse", "-----Delivered_On$mydatetime")
            Log.d("sendResponse", "-----" + "hgfhd")
            Log.d("sendResponse", "-----" + "fgrgr" + "0")
            Log.d("sendResponse", "-----" + "fg" + "1")

            //  Log.d("fjksDGFGSDFF", fullAddress);
            call.enqueue(object : Callback<SaveFeedbackbyDCRoot?> {
                override fun onResponse(
                    call: Call<SaveFeedbackbyDCRoot?>,
                    response: Response<SaveFeedbackbyDCRoot?>
                ) {
                    hideProgress()
                    if (response.isSuccessful) {
                        if (response.code() == 200) {
                            if (response.body() != null) {
                                if (response.body()!!.status == "200") {
                                    makeToast(response.body()!!.response.message.toString())
                                    val intent = Intent(mActivity, MainActivity::class.java)
                                    startActivity(intent)
                                } else {
                                    // makeToast(String.valueOf(response.body().getResponse().getMessage()));
                                    val msg = response.body()!!.response.message
                                    showAlertDialogWithSingleButton(mActivity, msg)
                                }
                            } else {
                                val msg = response.body()!!.response.message
                                showAlertDialogWithSingleButton(mActivity, msg)
                                // makeToast(getResources().getString(R.string.error));
                            }
                        } else {
                            val msg = "HTTP Error: " + response.code()
                            showAlertDialogWithSingleButton(mActivity, msg)
                        }
                    } else {
                        val msg = "HTTP Error: " + response.code()
                        showAlertDialogWithSingleButton(mActivity, msg)
                    }
                }

                override fun onFailure(call: Call<SaveFeedbackbyDCRoot?>, error: Throwable) {
                    hideProgress()
                    showAlertDialogWithSingleButton(mActivity, error.message)
                    //   makeToast(getResources().getString(R.string.error));
                    call.cancel()
                }
            })
        } else {
            // makeToast(getResources().getString(R.string.no_internet_connection));
            showAlertDialogWithSingleButton(
                mActivity,
                resources.getString(R.string.no_internet_connection)
            )
        }
    }

    override fun onBackPressed() {
        if (alertDialog != null && alertDialog!!.isShowing) {
            // Dismiss the dialog if it's showing
            alertDialog!!.dismiss()
            val i = Intent(mContext!!, MainActivity::class.java)
            startActivity(i)
        } else {
            super.onBackPressed()
        }
    }

    override fun makeToast(string: String?) {
        if (TextUtils.isEmpty(string)) return
        Toast.makeText(mActivity, string, Toast.LENGTH_SHORT).show()
    }

    fun showDateTimePicker(view: View?) {
        /* int year = selectedDateTime.get(Calendar.YEAR);
        int month = selectedDateTime.get(Calendar.MONTH);
        int day = selectedDateTime.get(Calendar.DAY_OF_MONTH);*/
        showTimePicker()


        /*  DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        selectedDateTime.set(year, month, day);
                        showTimePicker();
                    }
                },
                year, month, day
        );
        datePickerDialog.show();*/
    }

    private fun showTimePicker() {
        val currentTime = Calendar.getInstance()
        val minTime = Calendar.getInstance()
        minTime[Calendar.HOUR_OF_DAY] = 9
        minTime[Calendar.MINUTE] = 0

        val maxTime = Calendar.getInstance()
        maxTime[Calendar.HOUR_OF_DAY] = 18
        maxTime[Calendar.MINUTE] = 0


        if (currentTime.before(minTime)) {
            currentTime[Calendar.HOUR_OF_DAY] = 9
            currentTime[Calendar.MINUTE] = 0
        } else if (currentTime.after(maxTime)) {
            // Handle this case (e.g., show a Toast message)
            showAlertDialog("Time selection is not available after 6 PM")
            return  // Exit the method to prevent showing the picker
        }


        val hour = selectedDateTime!![Calendar.HOUR_OF_DAY]
        val minute = selectedDateTime!![Calendar.MINUTE]
        // Show TimePickerDialog
        val timePickerDialog = TimePickerDialog(
            this,
            { timePicker, hour, minute ->
                val selectedTime = Calendar.getInstance()
                selectedTime[Calendar.HOUR_OF_DAY] = hour
                selectedTime[Calendar.MINUTE] = minute


                // Ensure selected time is within the allowed range and not greater than current time
                if (selectedTime.after(currentTime)) {
                    showAlertDialog("Please select a time that is not grater than current time.")
                } else if (hour < 9 || hour > 18) {
                    showAlertDialog("Please select a time between 9 AM and 6 PM.")
                } else {
                    selectedDateTime!![Calendar.HOUR_OF_DAY] = hour
                    selectedDateTime!![Calendar.MINUTE] = hour
                    updateDateTimeEditText()
                }
            },
            hour, minute, true
        )









        timePickerDialog.show()
    }

    private fun showAlertDialog(message: String) {
        AlertDialog.Builder(this)
            .setMessage(message)
            .setPositiveButton("OK", null)
            .create()
            .show()
    }

    private fun updateDateTimeEditText() {
        val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        formattedDateTime = dateFormat.format(selectedDateTime!!.time)
        dateAndTimeEditText!!.setText(formattedDateTime)
    }

    private inner class AddressResultReceiver(handler: Handler?) : ResultReceiver(handler) {
        override fun onReceiveResult(resultCode: Int, resultData: Bundle) {
            super.onReceiveResult(resultCode, resultData)
            if (resultCode == Constants.SUCCESS_RESULT) {
                val ADDRESS = "ADDRESS " + resultData.getString(Constants.ADDRESS) + ", "
                val LOCAITY = "LOCAITY " + resultData.getString(Constants.LOCAITY) + ", "
                val STATE = "STATE " + resultData.getString(Constants.STATE) + ", "
                val DISTRICT = "DISTRICT " + resultData.getString(Constants.DISTRICT) + ", "
                val COUNTRY = "COUNTRY " + resultData.getString(Constants.COUNTRY) + ", "
                val POST_CODE = "POST_CODE " + resultData.getString(Constants.POST_CODE) + " "
                fullAddress = ADDRESS + LOCAITY + STATE + DISTRICT + COUNTRY + POST_CODE
                //     Log.d("resultDataresultData", fullAddress);
            } else {
                Toast.makeText(
                    mContext!!,
                    resultData.getString(Constants.RESULT_DATA_KEY),
                    Toast.LENGTH_SHORT
                ).show()
            }
            hideProgress()
        }
    }


    companion object {
        private const val GALLERY_REQUEST_CODE = 123
        private const val CAMERA_REQUEST_CODE = 456
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }
}

