package com.callmangement.ui.iris_derivery_installation

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
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.ResultReceiver
import android.provider.Settings
import android.text.TextUtils
import android.util.Base64
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.callmangement.R
import com.callmangement.custom.CustomActivity
import com.callmangement.databinding.ActivityIrisReplacedDetailBinding
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
import com.callmangement.ui.iris_derivery_installation.Model.CheckIrisSerialNoResponse
import com.callmangement.ui.iris_derivery_installation.Model.IrisDeliveryListResponse
import com.callmangement.ui.iris_derivery_installation.Model.ReplacementTypesResponse
import com.callmangement.ui.iris_derivery_installation.Model.ReplacementTypesResponse.ReplacementTypesList
import com.callmangement.ui.iris_derivery_installation.Model.SaveIRISDeliverResponse
import com.callmangement.ui.qrcodescanner.BarcodeScanningActivity
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
import java.util.Locale

class IrisReplacedDetailActivity : CustomActivity() {
    val REQUEST_PICK_IMAGE_ONE: Int = 1111
    val REQUEST_PICK_IMAGE_TWO: Int = 1112
    val REQUEST_PICK_IMAGE_THREE: Int = 1113
    val REQUEST_PICK_IMAGE_FOUR: Int = 1114
    val REQUEST_PICK_IMAGE_FIVE: Int = 1115
    private val spinnerList: List<String> = ArrayList()
    private val myFormat = "yyyy-MM-dd"
    var mActivity: Activity? = null
    var preference: PrefManager? = null
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
    var formattedDateTime: String? = null
    var mydatetime: String? = null
    private var binding: ActivityIrisReplacedDetailBinding? = null
    private var partsImageStoragePath1 = ""
    private var partsImageStoragePath2 = ""
    private var partsImageStoragePath3 = ""
    private val partsImageStoragePath4 = ""
    private val partsImageStoragePath5 = ""
    private var dateAndTimeEditText: EditText? = null
    private var selectedDateTime: Calendar? = null
    private var model: IrisDeliveryListResponse.Datum? = null

    private var replacementTypesList: List<ReplacementTypesList>? = ArrayList()
    private val replacementTypesNameList: ArrayList<String> = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE) //will hide the title
        //        getSupportActionBar().hide(); // hide the title bar
        binding = ActivityIrisReplacedDetailBinding.inflate(
            layoutInflater
        )
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
        binding!!.actionBar.ivBack.visibility = View.VISIBLE
        binding!!.actionBar.textToolbarTitle.text = resources.getString(R.string.iris_list_detail)
        dateAndTimeEditText = findViewById(R.id.dateAndTimeEditText)
        selectedDateTime = Calendar.getInstance()

        intentData
        setUpData()
        setClickListener()
        updateDateTimeEditText()

        replacementTypes
    }

    private val intentData: Unit
        get() {
            model =
                intent.getSerializableExtra("param") as IrisDeliveryListResponse.Datum?
        }

    private fun setUpData() {
        binding!!.inputFpsCode.text = model!!.fpscode
        binding!!.inputBlockName.text = model!!.blockName
        binding!!.inputDealerName.text = model!!.dealerName
        binding!!.inputMobileno.setText(model!!.dealerMobileNo)
        //    binding.inputWsSerialno.setText(model.getWeighingScaleSerialNo());
        //     binding.intputWsModel.setText(model.getWeighingScaleModelName());
        binding!!.inputtickretno.text = model!!.ticketNo
        binding!!.inputdelivereddate.text = model!!.irisdeliveredOnStr
        //    binding.inputinstalleddate.setText(model.getInstallationOnStr());
        binding!!.inputstatus.text = model!!.deliverdStatus
        //    binding.inputSerialno.setText(model.getSerialNo());
        binding!!.intputIrisModel.text = model!!.deviceModelName
        binding!!.inputaddress.text = model!!.shopAddress

        //   ArrayList<String> myvalue = new ArrayList<String>();
        //   myvalue.add(model.getErrorType().toString());
        /* ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(ErrorposDetail.this, simple_spinner_item, myvalue);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
        binding.spinner.setAdapter(spinnerArrayAdapter);*/
        //   binding.spinner.setText(model.getErrorType());
        Log.d("getDealerMobileNo", "jfdl" + model!!.dealerMobileNo)

        //    binding.inputRemark.setText(model.getRemark());
        //  binding.inputRemark.setText(model.getRemark());
        //    binding.inputDeviceCode.setText(String.valueOf(model.getDeviceCode()));
        //  binding.inputRemark.setText(model.getRemark());

        /*  if (model.getCompletedOnStr().isEmpty()){
            binding.expenseCompletedDateLay.setVisibility(View.GONE);
        } else {
            binding.expenseCompletedDateLay.setVisibility(View.VISIBLE);
            binding.inputExpenseCompletedDate.setText(model.getCompletedOnStr());
        }

        if (model.getExpenseStatusID() == 2) {
            binding.buttonComplete.setVisibility(View.GONE);
        }*/
        /*
        Glide.with(mContext)
                .load(Constants.API_BASE_URL+""+model.getFilePath())
                .placeholder(R.drawable.image_not_fount)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(binding.ivChallanImage);*/
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


        binding!!.actionBar.ivBack.setOnClickListener(object : OnSingleClickListener() {
            override fun onSingleClick(v: View) {
                if (alertDialog != null && alertDialog!!.isShowing) {
                    // Dismiss the dialog if it's showing
                    alertDialog!!.dismiss()
                    val i = Intent(mContext, IrisDeliveryInstallationDashboard::class.java)
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


        //        binding.inputSerialno.setOnClickListener(view -> {
//
//            binding.inputSerialno.setText("");
//            startScanning();
//
//        });
        binding!!.buttonSubmit.setOnClickListener { view: View? ->
            //    selectedValue = binding.inputmodel.getSelectedItem().toString();
            //   Model = binding.inputmodel.getText().toString();
            Model = binding!!.intputIrisModel.text.toString()
            SerialNo = binding!!.inputSerialno.text.toString()
            Mobile_No = binding!!.inputMobileno.text.toString()
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

            //    Log.d("Mobile_No"," "+ Mobile_No);

            /*       if (selectItem == null || selectedValue.equals(tv_select) || selectedValue.isEmpty() || selectedValue.length() == 0) {
                       makeToast(getResources().getString(R.string.select_errortype));
                   }
               else*/
            if (FPS_CODE == null || FPS_CODE!!.isEmpty() || FPS_CODE!!.length == 0) {
                //  makeToast(getResources().getString(R.string.enter_fps_code));
                binding!!.inputFpsCode.error = "???"
                val msg = resources.getString(R.string.enter_fps_code)
                showAlertDialogWithSingleButton(mActivity, msg)
            } else if (Mobile_No == null || Mobile_No!!.isEmpty() || Mobile_No!!.length != 10) {
                //makeToast(getResources().getString(R.string.enter_your_exact_mobile_no));
                binding!!.inputMobileno.error = "???"
                val msg = resources.getString(R.string.enter_your_exact_mobile_no)
                showAlertDialogWithSingleButton(mActivity, msg)
            } else if (SerialNo == null || SerialNo!!.isEmpty() || SerialNo!!.length == 0) {
                // makeToast(getResources().getString(R.string.please_select_serialno));
                // binding.inputSerialno.requestFocus();

                //     binding.inputSerialno.setError("???");

                val msg = resources.getString(R.string.please_select_serialno)
                showAlertDialogWithSingleButton(mActivity, msg)
            } else if (binding!!.spnReplacementType.selectedItemPosition < 1) {
                val msg = resources.getString(R.string.choose_replacement_type)
                showAlertDialogWithSingleButton(mActivity, msg)
            } else if (stringArrayListHavingAllFilePath == null || stringArrayListHavingAllFilePath.size < 3) {
                //  makeToast(getResources().getString(R.string.please_select_all_img));

                val msg = resources.getString(R.string.please_select_all_img)
                showAlertDialogWithSingleButton(mActivity, msg)

                //  binding.inputSerialno.requestFocus();
            } else if (signatureRequestBody == null) {
                // makeToast(getResources().getString(R.string.please_sign));
                //  binding.inputSerialno.requestFocus();
                val msg = resources.getString(R.string.please_sign)
                showAlertDialogWithSingleButton(mActivity, msg)
            } else {
                //   checkLocationServices();

                checkIrisSerialNo(SerialNo)
            }
        }
    }

    private fun openCameraWithScanner() {
        val intent = Intent(this, BarcodeScanningActivity::class.java)
        intent.putExtra("scanning_SDK", BarcodeScanningActivity.ScannerSDK.MLKIT)
        resultLauncher.launch(intent)
    }

    private fun startScanning() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            openCameraWithScanner()
        } else {
            val permissions = arrayOf(Manifest.permission.CAMERA)
            ActivityCompat.requestPermissions(this, permissions, CAMERA_REQUEST_CODE)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == CAMERA_REQUEST_CODE && grantResults.size > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCameraWithScanner()
            } else if (!ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.CAMERA
                )
            ) {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri = Uri.fromParts("package", packageName, null)
                intent.setData(uri)
                startActivityForResult(intent, CAMERA_REQUEST_CODE)
            }
        }
    }

    private fun checkLocationServices() {
        if (ContextCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                mActivity!!,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
            val msg =
                "???? ????????? ?? ?????? ??? ?? ??? ??| ???? ???? ?????? ??????? ???? ?? ??? ?? ! ????? ???? ?? ?????? ?? ?????? ???? ????? "
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
        alertDialog.setMessage(
            "???? ????????? ?? ?????? ??? ?? ??? ??| ???? ???? ?????? ??????? ???? ?? ??? ?? ! " +
                    "????? ???? ?? ?????? ?? ?????? ???? ????? "
        )
        // On pressing Settings button
        alertDialog.setPositiveButton(
            resources.getString(R.string.button_ok)
        ) { dialog, which ->
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivity(intent)
        }
        alertDialog.show()
    }

    private fun signatureDialoge() {
        /* int currentOrientation = getResources().getConfiguration().orientation;
       
               if (currentOrientation == Configuration.ORIENTATION_LANDSCAPE) {
                   setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
               } else {
                   setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
               }*/

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
                                saveIRISReplacementReqpr(
                                    FPS_CODE,
                                    model!!.deliveryId.toString(),
                                    model!!.deviceModelId.toString(),
                                    SerialNo,
                                    Mobile_No,
                                    stringArrayListHavingAllFilePath
                                )
                            } else {
                                val msg =
                                    "???? ???? ?????? ??????? ???? ?? ??? ?? ! ???? ?? ?????? ?? ?????? ???? ????? "
                                showAlertDialogWithSingleButton(
                                    mActivity,
                                    msg
                                )
                            }

                            // String msg = "???? ???? ?????? ??????? ???? ?? ??? ?? ! ???? ?? ?????? ?? ?????? ???? ?????";}

                            /* String FS = String.valueOf(ed_fps.getText());
 
                             if(districtId.equals(-1))
                             {
                                 Toast.makeText(mActivity, "????? ???? ?? ??? ?????", Toast.LENGTH_SHORT).show();
                             }
                             else if(FS.equals(null) || FS.isEmpty()){
                                 Toast.makeText(mActivity, "????? ?????? ?????", Toast.LENGTH_SHORT).show();
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
        if (requestCode == CAMERA_REQUEST_CODE) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                openCameraWithScanner()
            }
        }

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
            }
        }

        super.onActivityResult(requestCode, resultCode, data)
    }


    private fun saveIRISReplacementReqpr(
        fps_code: String?,
        deliveryId: String,
        modelId: String,
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
            val replaceTypeId =
                replacementTypesList!![binding!!.spnReplacementType.selectedItemPosition - 1].replaceTypeId

            val apiInterface = RetrofitInstance.getRetrofitInstance().create(
                APIService::class.java
            )
            //   Log.d("apifullapifullapifull", " "+fullAddress);
            val attachmentPartsImage1: RequestBody
            val attachmentPartsImage2: RequestBody
            val attachmentPartsImage3: RequestBody
            var attachmentPartsImage4: RequestBody
            var attachmentPartsImage5: RequestBody

            var fileNamePartsImage1 = ""
            var fileNamePartsImage2 = ""
            var fileNamePartsImage3 = ""
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
            val call = apiInterface.saveIRISReplacementReqpr(
                MultipartRequester.fromString(USER_Id),
                MultipartRequester.fromString(deliveryId),
                MultipartRequester.fromString(fps_code),
                MultipartRequester.fromString(mobile_no),  //        MultipartRequester.fromString(model),
                MultipartRequester.fromString("3"),
                MultipartRequester.fromString(serialNo),  //       MultipartRequester.fromString(completeAddressStr),
                MultipartRequester.fromString(binding!!.inputaddress.text.toString()),
                MultipartRequester.fromString(lati.toString()),
                MultipartRequester.fromString(longi.toString()),
                MultipartRequester.fromString(Remarks),  // MultipartRequester.fromString("2023-12-12 14:12"),
                MultipartRequester.fromString(mydatetime),
                MultipartRequester.fromString(replaceTypeId),
                createFormData(
                    "IRISSerialNoImage",
                    fileNamePartsImage2,
                    attachmentPartsImage2
                ),
                createFormData(
                    "DealerImageWithIRIS",
                    fileNamePartsImage1,
                    attachmentPartsImage1
                ),
                createFormData(
                    "DealerSignatureIRIS",
                    "signature.png",
                    signatureRequestBody!!
                ),
                createFormData(
                    "DealerPhotIdProof",
                    fileNamePartsImage3,
                    attachmentPartsImage3
                )
            )

            //campDocumentsParts);
            Log.d("sendResponse", "-----USER_Id$USER_Id")
            Log.d("sendResponse", "-----DeliveryId$deliveryId")
            Log.d("sendResponse", "-----fps_code$fps_code")
            Log.d("sendResponse", "-----DealerMobileNo$mobile_no")
            Log.d("sendResponse", "-----IrisScannerModelId$modelId")
            Log.d("sendResponse", "-----IrisScannerSerialNo$serialNo")

            //    Log.d("sendResponse", "-----" + "ShopAddress" + completeAddressStr);
            Log.d("sendResponse", "-----" + "ShopAddress" + binding!!.inputaddress.text.toString())

            Log.d("sendResponse", "-----lati$lati")
            Log.d("sendResponse", "-----longi$longi")
            Log.d("sendResponse", "-----Remarks$Remarks")
            Log.d("sendResponse", "-----Delivered_On$mydatetime")
            Log.d("sendResponse", "-----$replaceTypeId")
            Log.d("sendResponse", "-----" + "fgrgr" + "0")
            Log.d("sendResponse", "-----" + "fg" + "1")

            //  Log.d("fjksDGFGSDFF", fullAddress);
            call.enqueue(object : Callback<SaveIRISDeliverResponse?> {
                override fun onResponse(
                    call: Call<SaveIRISDeliverResponse?>,
                    response: Response<SaveIRISDeliverResponse?>
                ) {
                    hideProgress()
                    if (response.isSuccessful) {
                        if (response.code() == 200) {
                            if (response.body() != null) {
                                if (response.body()!!.status == "200") {
                                    makeToast(response.body()!!.response!!.message.toString())
                                    val intent = Intent(
                                        mActivity,
                                        IrisDeliveryInstallationDashboard::class.java
                                    )
                                    startActivity(intent)
                                } else {
                                    // makeToast(String.valueOf(response.body().getResponse().getMessage()));
                                    val msg = response.body()!!.response!!.message
                                    showAlertDialogWithSingleButton(mActivity, msg)
                                }
                            } else {
                                val msg = response.body()!!.response!!.message
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

                override fun onFailure(call: Call<SaveIRISDeliverResponse?>, error: Throwable) {
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
            val i = Intent(mContext, IrisDeliveryInstallationDashboard::class.java)
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
        val year = selectedDateTime!![Calendar.YEAR]
        val month = selectedDateTime!![Calendar.MONTH]
        val day = selectedDateTime!![Calendar.DAY_OF_MONTH]

        // Show DatePickerDialog
        val datePickerDialog = DatePickerDialog(
            this,
            { datePicker, year, month, day ->
                selectedDateTime!![year, month] = day
                showTimePicker()
            },
            year, month, day
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
            },
            hour, minute, true
        )
        timePickerDialog.show()
    }

    private fun updateDateTimeEditText() {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
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
                    mContext,
                    resultData.getString(Constants.RESULT_DATA_KEY),
                    Toast.LENGTH_SHORT
                ).show()
            }
            hideProgress()
        }
    }

    var resultLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult<Intent, ActivityResult>(
            ActivityResultContracts.StartActivityForResult(),
            object : ActivityResultCallback<ActivityResult?> {
                override fun onActivityResult(result: ActivityResult?) {
                    if (result!!.resultCode == RESULT_OK) {
                        // Here, no request code
                        //     Intent data = result.getData();
                        //        binding.inputSerialno.setText(result.getData().getStringExtra("result"));
                        checkIrisSerialNo(result!!.data!!.getStringExtra("result"))
                    }
                }
            })


    private fun checkIrisSerialNo(irisScannerSerialNo: String?) {
        if (isNetworkAvailable(mActivity!!)) {
            hideKeyboard(mActivity)
            showProgress(resources.getString(R.string.please_wait))
            val USER_Id = preference!!.useR_Id
            //    Log.d("USER_ID", USER_Id);
            val apiInterface = RetrofitInstance.getRetrofitInstance().create(
                APIService::class.java
            )
            val call = apiInterface.checkIrisSerialNo(USER_Id, "3", irisScannerSerialNo)
            call.enqueue(object : Callback<CheckIrisSerialNoResponse?> {
                override fun onResponse(
                    call: Call<CheckIrisSerialNoResponse?>,
                    response: Response<CheckIrisSerialNoResponse?>
                ) {
                    hideProgress()
                    if (response.isSuccessful) {
                        if (response.code() == 200) {
                            if (response.body() != null) {
                                if (response.body()!!.status == "200") {
                                    //    alertDialog.dismiss();
                                    val checkIrisSerialNoResponse = response.body()
                                    if (checkIrisSerialNoResponse!!.response != null &&
                                        checkIrisSerialNoResponse.response!!.status
                                    ) {
                                        binding!!.inputSerialno.setText(irisScannerSerialNo)
                                        SerialNo = binding!!.inputSerialno.text.toString()
                                        checkLocationServices()
                                    } else {
                                        val msg = response.body()!!.message
                                        showAlertDialogWithSingleButton(mActivity, msg)
                                        //  makeToast(String.valueOf(response.body().getMessage()));
                                        // Handle the case when countDatum is empty or null
                                    }
                                } else {
                                    //     alertDialog.show();
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

                override fun onFailure(call: Call<CheckIrisSerialNoResponse?>, error: Throwable) {
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


    private val replacementTypes: Unit
        get() {
            if (isNetworkAvailable(mActivity!!)) {
                hideKeyboard(mActivity)
                showProgress(resources.getString(R.string.please_wait))
                val apiInterface = RetrofitInstance.getRetrofitInstance().create(
                    APIService::class.java
                )
                val call = apiInterface.replacementTypes
                call.enqueue(object : Callback<ReplacementTypesResponse?> {
                    override fun onResponse(
                        call: Call<ReplacementTypesResponse?>,
                        response: Response<ReplacementTypesResponse?>
                    ) {
                        hideProgress()
                        if (response.isSuccessful) {
                            if (response.code() == 200) {
                                if (response.body() != null) {
                                    if (response.body()!!.status == "200") {
                                        //    alertDialog.dismiss();
                                        val replacementTypesResponse = response.body()
                                        if (replacementTypesResponse!!.replacementTypes_List != null &&
                                            !replacementTypesResponse.replacementTypes_List!!.isEmpty()
                                        ) {
                                            val replacementTypes =
                                                replacementTypesResponse.ReplacementTypesList()

                                            //     replacementTypes.setReplaceTypeId("0");
                                            //     replacementTypes.setReplaceType("--" + getResources().getString(R.string.remark) + "--");
                                            //    replacementTypesList.add(replacementTypes);
                                            //     Collections.reverse(district_List);
                                            replacementTypesList =
                                                replacementTypesResponse.replacementTypes_List

                                            replacementTypesNameList.add("-- " + resources.getString(R.string.replacement_type) + " --")

                                            for (i in replacementTypesList!!.indices) {
                                                replacementTypesNameList.add(
                                                    replacementTypesResponse.replacementTypes_List!![i].replaceType!!
                                                )
                                            }

                                            val dataAdapter: ArrayAdapter<Any?> =
                                                ArrayAdapter<Any?>(
                                                    mActivity!!,
                                                    android.R.layout.simple_spinner_item,
                                                    replacementTypesNameList as List<Any?>
                                                )
                                            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                                            binding!!.spnReplacementType.adapter = dataAdapter
                                        } else {
                                            val msg = response.body()!!.message
                                            showAlertDialogWithSingleButton(mActivity, msg)
                                            //  makeToast(String.valueOf(response.body().getMessage()));
                                            // Handle the case when countDatum is empty or null
                                        }
                                    } else {
                                        //     alertDialog.show();
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

                    override fun onFailure(
                        call: Call<ReplacementTypesResponse?>,
                        error: Throwable
                    ) {
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

    companion object {
        private const val GALLERY_REQUEST_CODE = 123
        private const val CAMERA_REQUEST_CODE = 456
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }
}



