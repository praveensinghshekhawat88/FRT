package com.callmangement.ui.ins_weighing_scale.activity

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
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
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.callmangement.R
import com.callmangement.custom.CustomActivity
import com.callmangement.databinding.ActivityNewdeliveryBinding
import com.callmangement.imagepicker.model.Config
import com.callmangement.imagepicker.model.Image
import com.callmangement.imagepicker.ui.imagepicker.ImagePicker
import com.callmangement.network.APIService
import com.callmangement.network.MultipartRequester
import com.callmangement.network.RetrofitInstance
import com.callmangement.support.FetchAddressIntentServices
import com.callmangement.support.ImageUtilsForRotate.ensurePortrait
import com.callmangement.support.OnSingleClickListener
import com.callmangement.support.signatureview.SignatureView
import com.callmangement.ui.ins_weighing_scale.model.DeliveredWeightInstal.WeighInsData
import com.callmangement.ui.ins_weighing_scale.model.SaveInstall.SaveRoot
import com.callmangement.ui.ins_weighing_scale.model.fps.DetailByFpsRoot
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

class NewDelivery : CustomActivity() {
    val REQUEST_PICK_IMAGE_ONE: Int = 1111
    val REQUEST_PICK_IMAGE_TWO: Int = 1112
    val REQUEST_PICK_IMAGE_THREE: Int = 1113
    val REQUEST_PICK_IMAGE_FOUR: Int = 1114
    val REQUEST_PICK_IMAGE_FIVE: Int = 1115
    private val spinnerList: List<String> = ArrayList()
    private val myFormat = "yyyy-MM-dd"
    var mActivity: Activity? = null
    var txt_fpscode: EditText? = null
    var Fps_code: String? = null
    var lati: Double = 0.0
    var longi: Double = 0.0
    var stringArrayListHavingAllFilePath: ArrayList<String> = ArrayList()
    var fullAddress: String? = null
    var resultReceiver: ResultReceiver? = null
    var completeAddressStr: String? = ""
    var mSignaturePad: SignatureView? = null
    var modelSpinner: RelativeLayout? = null
    var Model: String? = null
    var SerialNo: String? = null
    var Mobile_No: String? = null
    var FPS_CODE: String? = null
    var Full_Address: String? = null
    var Lati: String? = null
    var Longi: String? = null
    var Remarks: String? = null
    var Delivered_On: String? = null
    var IsDeliverd_IRIS: String? = null
    var IsDeliverd_WeighingScale: String? = null
    var alertDialog: AlertDialog? = null
    var encodedSignature: String? = null
    var signatureBitmap: Bitmap? = null
    var attachmentPartsImage6: RequestBody? = null
    var signatureRequestBody: RequestBody? = null
    var preference: PrefManager? = null
    var formattedDateTime: String? = null
    var mydatetime: String? = null
    private var binding: ActivityNewdeliveryBinding? = null
    private var partsImageStoragePath1 = ""
    private var partsImageStoragePath2 = ""
    private var partsImageStoragePath3 = ""
    private var partsImageStoragePath4 = ""
    private val partsImageStoragePath5 = ""
    private var mymodel: WeighInsData? = null
    private var dateAndTimeEditText: EditText? = null
    private var selectedDateTime: Calendar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE) //will hide the title
        //    getSupportActionBar().hide(); // hide the title bar
        binding = ActivityNewdeliveryBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        //  setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        init()
    }

    private fun init() {
        mActivity = this
        mContext = this
        binding!!.actionBar.textToolbarTitle.text = resources.getString(R.string.installation)
        preference = PrefManager(mContext!!)
        resultReceiver = AddressResultReceiver(Handler())
        dateAndTimeEditText = findViewById(R.id.dateAndTimeEditText)
        selectedDateTime = Calendar.getInstance()
        intentData
        setUpData()

        //  CoustomDialoge();
        checkbox()
        setClickListener()
        modelspinner()
    }

    private val intentData: Unit
        get() {
            mymodel = intent.getSerializableExtra("param") as WeighInsData?
        }


    private fun setUpData() {
        binding!!.inputFpsCode.text = mymodel!!.fpscode
        binding!!.inputBlock.text = mymodel!!.blockName
        binding!!.inputDealerName.text = mymodel!!.dealerName
        binding!!.inputMobile.setText(mymodel!!.dealerMobileNo)
        binding!!.inputWsSerialno.text = mymodel!!.weighingScaleSerialNo
        binding!!.intputWsModel.text = mymodel!!.weighingScaleModelName
        binding!!.inputDistrict.text = mymodel!!.districtName
        //  binding.inputtickretno.setText(model.getTicketNo());
        //  binding.inputtransdate.setText(model.getTranDateStr());
        //   binding.inputstatus.setText(model.getLast_TicketStatus());
        binding!!.inputFpsCode.text = mymodel!!.fpscode
        val currentDate = Date()
        // Define the desired date format
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        // Format the current date and time
        val formattedDate = dateFormat.format(currentDate)
        // Now, 'formattedDate' contains the date in the desired format
        Log.d("Current Date", formattedDate)
        dateAndTimeEditText!!.setText(formattedDate)
    }

    fun showDateTimePicker(view: View?) {
        val year = selectedDateTime!![Calendar.YEAR]
        val month = selectedDateTime!![Calendar.MONTH]
        val day = selectedDateTime!![Calendar.DAY_OF_MONTH]
        // Show DatePickerDialog
        val datePickerDialog = DatePickerDialog(
            this,
            { datePicker, year, month, day ->
                selectedDateTime!![year, month] = day
                showTimePicker()
            }, year, month, day
        )
        datePickerDialog.show()
    }

    private fun showTimePicker() {
        val hour = selectedDateTime!![Calendar.HOUR_OF_DAY]
        val minute = selectedDateTime!![Calendar.MINUTE]
        // Show TimePickerDialog
        val timePickerDialog = TimePickerDialog(
            this,
            { timePicker, hour, minute ->
                selectedDateTime!![Calendar.HOUR_OF_DAY] = hour
                selectedDateTime!![Calendar.MINUTE] = minute
                updateDateTimeEditText()
            }, hour, minute, true
        )
        timePickerDialog.show()
    }

    private fun updateDateTimeEditText() {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        formattedDateTime = dateFormat.format(selectedDateTime!!.time)
        dateAndTimeEditText!!.setText(formattedDateTime)
    }

    private fun modelspinner() {
        modelSpinner = findViewById(R.id.rl_model)
        val adapter = ArrayAdapter.createFromResource(
            this,
            R.array.colors_array,
            android.R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding!!.inputmodel.adapter = adapter
    }

    private fun setClickListener() {
        binding!!.linChooseImages1.setOnClickListener(object : OnSingleClickListener() {
            override fun onSingleClick(v: View) {
//                CropImage.activity().start(NewDelivery.this);
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

        binding!!.linChooseImages4.setOnClickListener(object : OnSingleClickListener() {
            override fun onSingleClick(v: View) {
                selectImage(REQUEST_PICK_IMAGE_FOUR)
            }
        })


        binding!!.actionBar.ivBack.setOnClickListener(object : OnSingleClickListener() {
            override fun onSingleClick(v: View) {
                setResult(RESULT_CANCELED)
                finish()
            }
        })
        binding!!.linChooseImagessig.setOnClickListener { view: View? ->
            signatureDialoge()
        }



        binding!!.buttonSubmit.setOnClickListener { view: View? ->
            //    selectedValue = binding.inputmodel.getSelectedItem().toString();
            //   Model = binding.inputmodel.getText().toString();
            //   resultReceiver = new AddressResultReceiver(new Handler());
            Model = binding!!.inputmodel.selectedItem.toString()
            SerialNo = binding!!.inputSerialno.text.toString()
            Mobile_No = binding!!.inputMobile.text.toString()
            FPS_CODE = binding!!.inputFpsCode.text.toString()
            Full_Address = fullAddress
            Lati = lati.toString()
            Longi = longi.toString()
            Remarks = ""
            val calendar = Calendar.getInstance()
            val today = calendar.time
            val sdf = SimpleDateFormat(myFormat, Locale.US)
            val todayDate = sdf.format(today)
            Delivered_On = todayDate
            IsDeliverd_IRIS = "0"
            IsDeliverd_WeighingScale = "1"
            Log.d("Mobile_No", Mobile_No!!)


            /*       if (selectItem == null || selectedValue.equals(tv_select) || selectedValue.isEmpty() || selectedValue.length() == 0) {
               makeToast(getResources().getString(R.string.select_errortype));
           }
       else*/
            if (FPS_CODE == null || FPS_CODE!!.isEmpty() || FPS_CODE!!.length == 0) {
                //  makeToast(getResources().getString(R.string.enter_fps_code));
                val msg = resources.getString(R.string.enter_fps_code)
                showAlertDialogWithSingleButton(mActivity, msg)
            } else if (Mobile_No == null || Mobile_No!!.isEmpty() || Mobile_No!!.length != 10) {
                //  makeToast(getResources().getString(R.string.enter_your_exact_mobile_no));
                val msg = resources.getString(R.string.enter_your_exact_mobile_no)
                showAlertDialogWithSingleButton(mActivity, msg)
            } else if (stringArrayListHavingAllFilePath == null || stringArrayListHavingAllFilePath.size < 3) {
                //makeToast(getResources().getString(R.string.please_select_all_img));
                val msg = resources.getString(R.string.please_select_all_img)
                showAlertDialogWithSingleButton(mActivity, msg)
            } else if (signatureRequestBody == null) {
                val msg = resources.getString(R.string.please_sign)
                showAlertDialogWithSingleButton(mActivity, msg)
            } else {
                checkLocationServices()
            }
        }
    }


    private fun signatureDialoge() {
        val dialogBuilder = AlertDialog.Builder(this)
        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_full_page, null)
        mSignaturePad = dialogView.findViewById(R.id.signature_pad)
        dialogBuilder.setView(dialogView)
        val alertDialog = dialogBuilder.create()
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
                    /*  Bitmap bitmap = mainBinding.signatureView.getSignatureBitmap();
                if(bitmap != null)
                {
                    mainBinding.imgSignature.setImageBitmap(bitmap);
                }*/
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
                        "DealerSignImage",
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

    private fun checkLocationServices() {
        if (ContextCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this@NewDelivery,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
            val msg = resources.getString(R.string.permissionmsg)
            showAlertDialogWithSingleButton(mActivity, msg)
        }


        val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        val isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        if (isGpsEnabled || isNetworkEnabled) {
            currentLocation
        } else {
            showSettingsAlert()
        }
    }

    fun showSettingsAlert() {
        val alertDialog = AlertDialog.Builder(this)
        // Setting Dialog Title
        alertDialog.setTitle("Permission necessary")
        // Setting Dialog Message
        alertDialog.setMessage("External storage permission is necessary")
        // On pressing Settings button
        alertDialog.setPositiveButton(
            resources.getString(R.string.button_ok)
        ) { dialog, which ->
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivity(intent)
        }

        alertDialog.show()
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
            showProgress()
            Log.d("fulladdrss", "location$completeAddressStr")

            //  Constants.showCustomProgressDialogCommonForAll(mActivity, getResources().getString(R.string.get_location));
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
            LocationServices.getFusedLocationProviderClient(this@NewDelivery)
                .requestLocationUpdates(locationRequest, object : LocationCallback() {
                    override fun onLocationResult(locationResult: LocationResult) {
                        super.onLocationResult(locationResult)
                        LocationServices.getFusedLocationProviderClient(applicationContext)
                            .removeLocationUpdates(this)
                        if (locationResult != null && locationResult.locations.size > 0) {
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
                            Log.d("locationlocation", "" + location)
                            completeAddressStr = addressFromLatLong
                            Log.d("completeAddressStr", completeAddressStr!!)
                            hideProgress()


                            ////////////mine


                            //if (Lati != null && Lati.length() != 0) {
                            if (completeAddressStr != null && completeAddressStr!!.length != 0) {
                                saveInstallationReq(
                                    FPS_CODE,
                                    Model,
                                    SerialNo,
                                    Mobile_No,
                                    stringArrayListHavingAllFilePath
                                )

                                //Toast.makeText(mActivity, "yesssssssssssssss", Toast.LENGTH_SHORT).show();
                            } else {
                                val msg =
                                    resources.getString(R.string.locationmsg)
                                showAlertDialogWithSingleButton(
                                    mActivity,
                                    msg
                                )
                            }


                            /////mine


                            /* String FS = String.valueOf(ed_fps.getText());
 
                             if(districtId.equals(-1))
                             {
                                 Toast.makeText(mActivity, "कृपया जिले का चयन करें।", Toast.LENGTH_SHORT).show();
 
                             }
 
                             else if(FS.equals(null) || FS.isEmpty()){
                                 Toast.makeText(mActivity, "कृपया एफपीएस भरें।", Toast.LENGTH_SHORT).show();
 
                             }
                             else {
                                 dashboardApi();
 
                             }
 */
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

    private fun checkbox() {
        // binding.llWm.setVisibility(View.GONE);
        binding!!.llIris.visibility = View.GONE
        binding!!.llWm.visibility = View.VISIBLE
        binding!!.checkBoxWm.isChecked = true

        /*    binding.checkBoxWm.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    binding.llWm.setVisibility(View.VISIBLE);
                } else {
                    // Checkbox is unchecked, perform actions here
                    // Example: disable ScrollView scrolling
                    binding.llWm.setVisibility(View.GONE);
                }
            }
        });
             binding.checkBoxIris.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        binding.llIris.setVisibility(View.VISIBLE);
                    } else {
                        // Checkbox is unchecked, perform actions here
                        // Example: disable ScrollView scrolling
                        binding.llIris.setVisibility(View.GONE);

                    }
                }
                });

*/
    }

    private fun CoustomDialoge() {
        val alert = AlertDialog.Builder(this@NewDelivery)
        val mView = layoutInflater.inflate(R.layout.activity_coustomfpsdialoge, null)
        //  mView.setBackground(ContextCompat.getDrawable(this, R.drawable.rounded_dialog_bg));
        txt_fpscode = mView.findViewById(R.id.txt_input)
        val btn_cancel = mView.findViewById<Button>(R.id.btn_cancel)
        val btn_okay = mView.findViewById<Button>(R.id.btn_okay)
        alert.setView(mView)
        alertDialog = alert.create()
        alertDialog!!.setCanceledOnTouchOutside(false)
        btn_cancel.setOnClickListener {
            val imm =
                getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
            alertDialog!!.dismiss()
        }
        btn_okay.setOnClickListener {
            //    myCustomMessage.setText(txt_inputText.getText().toString());
            val imm =
                getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
            GetCustomerDetailsByFPS()
            //  alertDialog.dismiss();
        }
        alertDialog!!.show()
    }

    private fun GetCustomerDetailsByFPS() {
        Fps_code = txt_fpscode!!.text.toString().trim { it <= ' ' }
        if (isNetworkAvailable(mActivity!!)) {
            hideKeyboard(mActivity)
            hideProgress()
            val USER_Id = preference!!.useR_Id
            Log.d("USER_ID", USER_Id)
            val service = RetrofitInstance.getRetrofitInstance().create(
                APIService::class.java
            )
            // Call<DetailByFpsRoot> call = service.apiDetailsByFPS(Fps_code,USER_Id,"0");
            val call = service.apiDetailsByFPS(Fps_code, USER_Id, "0")
            //  APIInterface apiInterface = APIClient.getRetrofitClientWithoutHeaders(mActivity!!!!,Utils.Baseurl).create(APIInterface.class);
            //  Call<DetailByFpsRoot> call = apiInterface.apiDetailsByFPS(Fps_code,USER_Id,"0");
            call.enqueue(object : Callback<DetailByFpsRoot?> {
                override fun onResponse(
                    call: Call<DetailByFpsRoot?>,
                    response: Response<DetailByFpsRoot?>
                ) {
                    hideProgress()
                    if (response.isSuccessful) {
                        if (response.code() == 200) {
                            if (response.body() != null) {
                                if (response.body()!!.status == "200") {
                                    alertDialog!!.dismiss()
                                    val detailByFpsRoot = response.body()
                                    val fps_message = detailByFpsRoot!!.message
                                    Toast.makeText(
                                        this@NewDelivery,
                                        fps_message,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    val detailByFpsData = detailByFpsRoot.data
                                    if (detailByFpsData != null) {
                                        val DistrictName = detailByFpsData.districtName
                                        val Fpscode = detailByFpsData.fpscode
                                        val DealerName = detailByFpsData.dealerName
                                        val FpsdeviceCode = detailByFpsData.fpsdeviceCode
                                        val DealerMobileNo = detailByFpsData.dealerMobileNo
                                        val Block = detailByFpsData.blockName
                                        val WeighingScaleModelName =
                                            detailByFpsData.weighingScaleModelName.toString()
                                        val WeighingScaleSerialNo =
                                            detailByFpsData.weighingScaleSerialNo.toString()
                                        val IrisScannerModelName =
                                            detailByFpsData.irisScannerModelName.toString()
                                        val IrisScannerSerialNo =
                                            detailByFpsData.irisScannerSerialNo.toString()
                                        binding!!.inputDealerName.text = DealerName
                                        binding!!.inputDistrict.text = DistrictName
                                        binding!!.inputFpsCode.text = Fpscode
                                        binding!!.inputMobile.setText(DealerMobileNo)
                                        binding!!.inputBlock.text = Block

                                        //   binding.inputSerialno.setText(WeighingScaleSerialNo);
                                        //  binding.inputmodel.setText(WeighingScaleModelName);
                                        //  binding.inputIrisCode.setText(IrisScannerSerialNo);
                                        //    binding.inputIrisName.setText(IrisScannerModelName);
                                        if (ContextCompat.checkSelfPermission(
                                                applicationContext,
                                                Manifest.permission.ACCESS_FINE_LOCATION
                                            ) != PackageManager.PERMISSION_GRANTED
                                        ) {
                                            ActivityCompat.requestPermissions(
                                                this@NewDelivery, arrayOf(
                                                    Manifest.permission.ACCESS_FINE_LOCATION
                                                ), LOCATION_PERMISSION_REQUEST_CODE
                                            )
                                        } else {
                                        }

                                        // Use 'dataValue' or perform operations with other properties
                                    } else {
                                        makeToast(response.body()!!.message.toString())

                                        // Handle the case when countDatum is empty or null
                                    }
                                } else {
                                    alertDialog!!.show()
                                    makeToast(response.body()!!.message.toString())

                                    // Handle the case when the response status is not 200 or the response body is null
                                }
                            } else {
                                makeToast(response.body()!!.message.toString())
                            }
                        } else {
                            makeToast(resources.getString(R.string.error))
                        }
                    } else {
                        makeToast(resources.getString(R.string.error))
                    }
                }

                override fun onFailure(call: Call<DetailByFpsRoot?>, error: Throwable) {
                    // Utils.hideCustomProgressDialogCommonForAll();
                    hideProgress()

                    makeToast(resources.getString(R.string.error))

                    call.cancel()
                }
            })
        } else {
            makeToast(resources.getString(R.string.no_internet_connection))
        }
    }

    private fun selectImage(requestCode: Int) {
        try {
            ImagePicker.with(mActivity).setToolbarColor("#212121").setStatusBarColor("#000000")
                .setToolbarTextColor("#FFFFFF").setToolbarIconColor("#FFFFFF")
                .setProgressBarColor("#4CAF50").setBackgroundColor("#212121").setCameraOnly(true)
                .setMultipleMode(true).setFolderMode(true).setShowCamera(true)
                .setFolderTitle("Albums").setImageTitle("Galleries").setDoneTitle("Done")
                .setMaxSize(1).setSavePath(
                    Constants.saveImagePath
                ).setSelectedImages(ArrayList()).start(requestCode)
        } catch (e: Exception) {
            e.printStackTrace()
            Log.d("error", "" + e)
        }
    }

    private fun showPreviewPopup(myBitmap: Bitmap) {
        // Show pop up window
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
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
                    binding!!.ivPartsImage1.setImageBitmap(ensurePortrait(partsImageStoragePath1))
                    stringArrayListHavingAllFilePath.add(partsImageStoragePath1)
                } catch (e: IOException) {
                    binding!!.ivPartsImage1.setImageBitmap(myBitmap)
                    stringArrayListHavingAllFilePath.add(partsImageStoragePath1)

                    e.printStackTrace()
                } catch (e: NullPointerException) {
                    e.printStackTrace()
                }
                val layoutInflater = LayoutInflater.from(this@NewDelivery)
                val promptView = layoutInflater.inflate(R.layout.input_photo, null)
                val alertDialogBuilder = AlertDialog.Builder(this@NewDelivery)
                alertDialogBuilder.setView(promptView)
                val imageView = promptView.findViewById<ImageView>(R.id.imageView_photo)
                imageView.setImageBitmap(myBitmap)

                alertDialogBuilder.setCancelable(false).setPositiveButton(
                    "OK"
                ) { dialog, id -> dialog.cancel() }

                    .setNegativeButton(
                        "Retake"
                    ) { dialog, id ->
                        dialog.cancel()
                        selectImage(REQUEST_PICK_IMAGE_ONE)
                    }
                // create an alert dialog
                val alert = alertDialogBuilder.create()
                alert.setCancelable(false)
                alert.show()
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
                    binding!!.ivPartsImage2.setImageBitmap(ensurePortrait(partsImageStoragePath2))
                    stringArrayListHavingAllFilePath.add(partsImageStoragePath2)
                } catch (e: IOException) {
                    binding!!.ivPartsImage2.setImageBitmap(myBitmap)
                    stringArrayListHavingAllFilePath.add(partsImageStoragePath2)
                    e.printStackTrace()
                } catch (e: NullPointerException) {
                    e.printStackTrace()
                }

                val layoutInflater = LayoutInflater.from(this@NewDelivery)
                val promptView = layoutInflater.inflate(R.layout.input_photo, null)
                val alertDialogBuilder = AlertDialog.Builder(this@NewDelivery)
                alertDialogBuilder.setView(promptView)
                val imageView = promptView.findViewById<ImageView>(R.id.imageView_photo)
                imageView.setImageBitmap(myBitmap)

                alertDialogBuilder.setCancelable(false).setPositiveButton(
                    "OK"
                ) { dialog, id -> dialog.cancel() }

                    .setNegativeButton(
                        "Retake"
                    ) { dialog, id ->
                        dialog.cancel()
                        selectImage(REQUEST_PICK_IMAGE_TWO)
                    }
                // create an alert dialog
                val alert = alertDialogBuilder.create()
                alert.setCancelable(false)
                alert.show()
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
                    binding!!.ivPartsImage3.setImageBitmap(ensurePortrait(partsImageStoragePath3))
                    stringArrayListHavingAllFilePath.add(partsImageStoragePath3)
                } catch (e: IOException) {
                    binding!!.ivPartsImage3.setImageBitmap(myBitmap)
                    stringArrayListHavingAllFilePath.add(partsImageStoragePath3)
                    e.printStackTrace()
                } catch (e: NullPointerException) {
                    e.printStackTrace()
                }
                val layoutInflater = LayoutInflater.from(this@NewDelivery)
                val promptView = layoutInflater.inflate(R.layout.input_photo, null)
                val alertDialogBuilder = AlertDialog.Builder(this@NewDelivery)
                alertDialogBuilder.setView(promptView)
                val imageView = promptView.findViewById<ImageView>(R.id.imageView_photo)
                imageView.setImageBitmap(myBitmap)

                alertDialogBuilder.setCancelable(false).setPositiveButton(
                    "OK"
                ) { dialog, id -> dialog.cancel() }

                    .setNegativeButton(
                        "Retake"
                    ) { dialog, id ->
                        dialog.cancel()
                        selectImage(REQUEST_PICK_IMAGE_THREE)
                    }
                // create an alert dialog
                val alert = alertDialogBuilder.create()
                alert.setCancelable(false)
                alert.show()
            }
        } else if (requestCode == REQUEST_PICK_IMAGE_FOUR && resultCode == RESULT_OK && data != null) {
            val images = data.getParcelableArrayListExtra<Image>(Config.EXTRA_IMAGES)
            if (images != null && images.size > 0) {
                val image = images[0]
                //  String imageStoragePath = image.getPath();
                partsImageStoragePath4 = image.path
                if (partsImageStoragePath4.contains("file:/")) {
                    partsImageStoragePath4 = partsImageStoragePath4.replace("file:/", "")
                }
                partsImageStoragePath4 = compress(
                    partsImageStoragePath4,
                    this
                )
                val imgFile = File(partsImageStoragePath4)
                val myBitmap = BitmapFactory.decodeFile(imgFile.absolutePath)

                try {
                    binding!!.ivChallanImage4.setImageBitmap(ensurePortrait(partsImageStoragePath4))
                    stringArrayListHavingAllFilePath.add(partsImageStoragePath4)
                } catch (e: IOException) {
                    binding!!.ivChallanImage4.setImageBitmap(myBitmap)
                    stringArrayListHavingAllFilePath.add(partsImageStoragePath4)
                    e.printStackTrace()
                } catch (e: NullPointerException) {
                    e.printStackTrace()
                }
                val layoutInflater = LayoutInflater.from(this@NewDelivery)
                val promptView = layoutInflater.inflate(R.layout.input_photo, null)
                val alertDialogBuilder = AlertDialog.Builder(this@NewDelivery)
                alertDialogBuilder.setView(promptView)
                val imageView = promptView.findViewById<ImageView>(R.id.imageView_photo)
                imageView.setImageBitmap(myBitmap)

                alertDialogBuilder.setCancelable(false).setPositiveButton(
                    "OK"
                ) { dialog, id -> dialog.cancel() }

                    .setNegativeButton(
                        "Retake"
                    ) { dialog, id ->
                        dialog.cancel()
                        selectImage(REQUEST_PICK_IMAGE_FOUR)
                    }
                // create an alert dialog
                val alert = alertDialogBuilder.create()
                alert.setCancelable(false)
                alert.show()
            }
        }


        super.onActivityResult(requestCode, resultCode, data)
    }


    private fun saveInstallationReq(
        fps_code: String?,
        model: String?,
        serialNo: String?,
        mobile_no: String?,
        arrayHavingAllFilePath: ArrayList<String>
    ) {
        if (isNetworkAvailable(mActivity!!)) {
            hideKeyboard(mActivity)
            //  Utils.showCustomProgressDialogCommonForAll(mActivity, getResources().getString(R.string.please_wait));
            showProgress()
            val USER_Id = preference!!.useR_Id
            val deliveryid = mymodel!!.deliveryId.toString()
            val iris_inputserialno = binding!!.inputSerialno.text.toString()
            mydatetime = dateAndTimeEditText!!.text.toString()
            //Log.d("USER_ID", USER_Id);
            //    APIInterface apiInterface = APIClient.getRetrofitClientWithoutHeaders(mActivity!!!!, Utils.Baseurl).create(APIInterface.class);
            Log.d("apifullapifullapifull", "" + fullAddress)
            val service = RetrofitInstance.getRetrofitInstance().create(
                APIService::class.java
            )

            //  Call<CountRoot> call = service.appWeightCountApi(USER_Id,"" ,districtId,"", "0",fromDate,toDate);
            /*  MultipartBody.Part[] campDocumentsParts = new MultipartBody.Part[arrayHavingAllFilePath.size()];
            for (int index = 0; index < arrayHavingAllFilePath.size(); index++) {
                File file = new File(arrayHavingAllFilePath
                        .get(index));
                RequestBody surveyBody = RequestBody.create(MediaType.parse("image/ *"),
                        file);
                campDocumentsParts[index] = MultipartBody.Part.createFormData("CampDocuments",
                        file.getName(),
                        surveyBody);
            }*/

            /*  Call<SaveRoot> call = apiInterface.saveInstallationReq(
                    MultipartRequester.fromString(USER_Id),
                    MultipartRequester.fromString(model),
                    MultipartRequester.fromString(serialNo),
                    MultipartRequester.fromString(""),
                    MultipartRequester.fromString(""),
                    MultipartRequester.fromString(mobile_no),
                    MultipartRequester.fromString(fps_code),
                    MultipartRequester.fromString(completeAddressStr),
                    MultipartRequester.fromString(String.valueOf(lati)),
                    MultipartRequester.fromString(String.valueOf(longi)),
                    MultipartRequester.fromString(Remarks),
                    MultipartRequester.fromString("2023-12-12 14:12"),
                    MultipartRequester.fromString(""),
                    MultipartRequester.fromString("0"),
                    MultipartRequester.fromString("1"),
                    campDocumentsParts);*/
            val attachmentPartsImage1: RequestBody
            val attachmentPartsImage2: RequestBody
            val attachmentPartsImage3: RequestBody
            val attachmentPartsImage4: RequestBody
            var attachmentPartsImage5: RequestBody
            var fileNamePartsImage1 = ""
            var fileNamePartsImage2 = ""
            var fileNamePartsImage3 = ""
            var fileNamePartsImage4 = ""
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

            if (partsImageStoragePath3 != "") {
                fileNamePartsImage3 = File(partsImageStoragePath3).name
                attachmentPartsImage3 = RequestBody.create(
                    "multipart/form-data".toMediaTypeOrNull(),
                    File(partsImageStoragePath3)
                )
            } else {
                fileNamePartsImage3 = ""
                attachmentPartsImage3 = RequestBody.create("text/plain".toMediaTypeOrNull(), "")
            }

            if (partsImageStoragePath4 != "") {
                fileNamePartsImage4 = File(partsImageStoragePath4).name
                attachmentPartsImage4 = RequestBody.create(
                    "multipart/form-data".toMediaTypeOrNull(),
                    File(partsImageStoragePath4)
                )
            } else {
                fileNamePartsImage4 = ""
                attachmentPartsImage4 = RequestBody.create("text/plain".toMediaTypeOrNull(), "")
            }

            /* if (!partsImageStoragePath5.equals("")) {
                fileNamePartsImage5 = new File(partsImageStoragePath5).getName();
                attachmentPartsImage6= RequestBody.create(MediaType.parse("multipart/form-data"), new File(partsImageStoragePath5));
            } else {
                fileNamePartsImage5 = "";
                attachmentPartsImage6 = RequestBody.create(MediaType.parse("text/plain"), encodedSignature);
            }*/
            val call = service.saveInstallationRe(
                MultipartRequester.fromString(USER_Id),
                MultipartRequester.fromString(deliveryid),
                MultipartRequester.fromString("0"),  //  MultipartRequester.fromString(iris_inputserialno),
                MultipartRequester.fromString(""),
                MultipartRequester.fromString(completeAddressStr),
                MultipartRequester.fromString(lati.toString()),
                MultipartRequester.fromString(longi.toString()),
                MultipartRequester.fromString(Remarks),
                MultipartRequester.fromString(mydatetime),
                MultipartRequester.fromString(fps_code),
                MultipartRequester.fromString(mobile_no),
                createFormData(
                    "InstallationWeinghingImage",
                    fileNamePartsImage2,
                    attachmentPartsImage2
                ),  // MultipartBody.Part.createFormData("InstallationIRISScannerImage", fileNamePartsImage4,attachmentPartsImage4),
                createFormData(
                    "InstallationIRISScannerImage",
                    fileNamePartsImage1,
                    attachmentPartsImage1
                ),
                createFormData(
                    "InstallationDealerPhoto",
                    fileNamePartsImage1,
                    attachmentPartsImage1
                ),
                createFormData(
                    "InstallationDealerSignature",
                    "signature.png",
                    signatureRequestBody!!
                ),
                createFormData(
                    "InstallationChallan",
                    fileNamePartsImage3,
                    attachmentPartsImage3
                )
            )
            //campDocumentsParts);
            Log.d("sendResponse", "-----USER_Id$USER_Id")
            Log.d("sendResponse", "-----deviceid$deliveryid")
            Log.d("sendResponse", "-----" + "serialNo" + "3")
            Log.d("sendResponse", "-----" + "iris_inputserialno" + " ")
            Log.d("sendResponse", "-----completeAddressStr$completeAddressStr")
            Log.d("sendResponse", "-----lati$lati")
            Log.d("sendResponse", "-----longi$longi")
            Log.d("sendResponse", "-----Remarks$Remarks")
            Log.d("sendResponse", "-----mydatetime$mydatetime")
            Log.d("sendResponse", "-----longi$longi")
            Log.d("sendResponse", "-----Remarks$Remarks")
            Log.d("sendResponse", "-----fps_code$fps_code")
            Log.d("sendResponse", "-----mobile_no$mobile_no")
            //   Call<SaveRoot> call = apiInterface.saveInstallationReq(USER_Id, model,serialNo,"","",mobile_no,fps_code,completeAddressStr, String.valueOf(lati), String.valueOf(longi), Remarks,Delivered_On,"","0","1", campDocumentsParts);
            call.enqueue(object : Callback<SaveRoot?> {
                override fun onResponse(call: Call<SaveRoot?>, response: Response<SaveRoot?>) {
                    hideProgress()

                    if (response.isSuccessful) {
                        if (response.code() == 200) {
                            if (response.body() != null) {
                                Log.d("ResponseCode", "" + response.code())
                                if (response.body()!!.status == "200") {
                                    val saveRoot = response.body()
                                    makeToast(response.body()!!.response.message.toString())
                                    onBackPressed()
                                } else {
                                    val massage = response.body()!!.response.message
                                    showAlertDialogWithSingleButton(mActivity, massage)
                                    // makeToast(String.valueOf(response.body().getResponse().getMessage()));
                                }
                            } else {
                                showAlertDialogWithSingleButton(mActivity, response.message())
                            }
                        } else {
                            val msg = "HTTP Error: " + response.code()
                            showAlertDialogWithSingleButton(mActivity, msg)
                        }
                    } else {
                        val msg = "HTTP Error: " + response.code()
                        showAlertDialogWithSingleButton(mActivity, msg)
                        Log.d("Toperror", response.toString())
                    }
                }

                override fun onFailure(call: Call<SaveRoot?>, error: Throwable) {
                    // Utils.hideCustomProgressDialogCommonForAll();
                    hideProgress()
                    showAlertDialogWithSingleButton(mActivity, error.message)

                    call.cancel()
                }
            })
        } else {
            showAlertDialogWithSingleButton(
                mActivity,
                resources.getString(R.string.no_internet_connection)
            )
        }
    }

    override fun onBackPressed() {
        //  alertDialog.show();
        super.onBackPressed()
        if (alertDialog != null && alertDialog!!.isShowing) {
            // Dismiss the dialog if it's showing
            alertDialog!!.dismiss()
            setResult(RESULT_OK)

            finish()
        } else {
            setResult(RESULT_OK)

            finish()
        }
    }

    override fun makeToast(string: String?) {
        if (TextUtils.isEmpty(string)) return
        Toast.makeText(mActivity, string, Toast.LENGTH_SHORT).show()
    }

    private inner class AddressResultReceiver(handler: Handler?) : ResultReceiver(handler) {
        override fun onReceiveResult(resultCode: Int, resultData: Bundle) {
            super.onReceiveResult(resultCode, resultData)
            if (resultCode == Constants.SUCCESS_RESULT) {
                /* address.setText(resultData.getString(Constants.ADDRESS));
                locaity.setText(resultData.getString(Constants.LOCAITY));
                state.setText(resultData.getString(Constants.STATE));
                district.setText(resultData.getString(Constants.DISTRICT));
                country.setText(resultData.getString(Constants.COUNTRY));
                postcode.setText(resultData.getString(Constants.POST_CODE));*/
                val ADDRESS = "ADDRESS " + resultData.getString(Constants.ADDRESS) + ", "
                val LOCAITY = "LOCAITY " + resultData.getString(Constants.LOCAITY) + ", "
                val STATE = "STATE " + resultData.getString(Constants.STATE) + ", "
                val DISTRICT = "DISTRICT " + resultData.getString(Constants.DISTRICT) + ", "
                val COUNTRY = "COUNTRY " + resultData.getString(Constants.COUNTRY) + ", "
                val POST_CODE = "POST_CODE " + resultData.getString(Constants.POST_CODE) + " "
                fullAddress = ADDRESS + LOCAITY + STATE + DISTRICT + COUNTRY + POST_CODE
                Log.d("resultDataresultData", fullAddress!!)


                /*
                String FS = String.valueOf(ed_fps.getText());
                if (districtId.equals(-1)) {
                    Toast.makeText(mActivity, "कृपया जिले का चयन करें।", Toast.LENGTH_SHORT).show();
                } else if (FS.equals(null) || FS.isEmpty()) {
                    Toast.makeText(mActivity, "कृपया एफपीएस भरें।", Toast.LENGTH_SHORT).show();
                } else {
                    dashboardApi();
                }
*/
            } else {
                Toast.makeText(
                    this@NewDelivery,
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

