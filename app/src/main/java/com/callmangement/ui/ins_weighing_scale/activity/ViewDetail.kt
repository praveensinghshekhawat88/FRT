package com.callmangement.ui.ins_weighing_scale.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.Window
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.GridLayoutManager
import com.callmangement.R
import com.callmangement.custom.CustomActivity
import com.callmangement.databinding.ActivityDeliverylistDetailsIrisBinding
import com.callmangement.imagepicker.model.Config
import com.callmangement.imagepicker.model.Image
import com.callmangement.imagepicker.ui.imagepicker.ImagePicker
import com.callmangement.model.WeighingDeliveryDetail.weighingDelieryRoot
import com.callmangement.model.WeighingDeliveryDetail.weighingDeliveryImagesDetail
import com.callmangement.model.logout.ModelLogout
import com.callmangement.network.APIService
import com.callmangement.network.MultipartRequester
import com.callmangement.network.RetrofitInstance
import com.callmangement.support.ImageUtilsForRotate.ensurePortrait
import com.callmangement.support.OnSingleClickListener
import com.callmangement.ui.ins_weighing_scale.adapter.ViewImagesListingAdapterIris
import com.callmangement.ui.ins_weighing_scale.model.DeliveredWeightInstal.WeighInsData
import com.callmangement.ui.ins_weighing_scale.model.SaveInstall.SaveRoot
import com.callmangement.utils.CompressImage.Companion.compress
import com.callmangement.utils.Constants
import com.callmangement.utils.Constants.isNetworkAvailable
import com.callmangement.utils.PrefManager
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody.Part.Companion.createFormData
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.IOException
import java.util.Objects

class ViewDetail : CustomActivity(), View.OnClickListener {
    val REQUEST_PICK_IMAGE_ONE: Int = 1111
    val REQUEST_PICK_IMAGE_TWO: Int = 1112
    val REQUEST_PICK_IMAGE_THREE: Int = 1113

    private var partsImageStoragePath1 = ""
    private var partsImageStoragePath2 = ""
    private var partsImageStoragePath3 = ""
    var preference: PrefManager? = null
    var selectedValue: String? = null
    var FPS_CODE: String? = null
    var Input_Device_Code: String? = null
    var Input_Remark: String? = null
    var stringArrayListHavingAllFilePath: ArrayList<String> = ArrayList()
    var mActivity: Activity? = null
    private var binding: ActivityDeliverylistDetailsIrisBinding? = null

    //private ModelExpensesList model;
    private var model: WeighInsData? = null
    private val playerNames = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE) //will hide the title
        //    getSupportActionBar().hide(); // hide the title bar
        binding = ActivityDeliverylistDetailsIrisBinding.inflate(
            layoutInflater
        )
        setContentView(binding!!.root)
        initView()
    }

    private fun initView() {
        mActivity = this
        mContext = this
        preference = PrefManager(mContext!!)
        binding!!.actionBar.ivBack.visibility = View.VISIBLE
        binding!!.actionBar.textToolbarTitle.text = resources.getString(R.string.list_detail)

        intentData
        setUpOnClickListener()
        setUpData()
        GetErrorImages()
    }

    private fun setUpOnClickListener() {
        binding!!.actionBar.ivBack.setOnClickListener {
            setResult(RESULT_OK)
            finish()
        }

        binding!!.buttonUpdate.setOnClickListener(View.OnClickListener {
            val iris_inputserialno = binding!!.inputWsSerialno.text.toString()
            val inputserialRemark = binding!!.inputWsReamrk.text.toString()
            if (iris_inputserialno.trim { it <= ' ' }.length == 0) {
                val msg = resources.getString(R.string.please_enter_serialno)
                showAlertDialogWithSingleButton(mActivity, msg)
                return@OnClickListener
            }

            if (inputserialRemark.trim { it <= ' ' }.length == 0) {
                val msg = resources.getString(R.string.please_enter_remark)
                showAlertDialogWithSingleButton(mActivity, msg)
                return@OnClickListener
            }
            val builder = AlertDialog.Builder(
                mContext!!
            )
            builder.setMessage(resources.getString(R.string.update_verify_content))
                .setCancelable(false)
                .setPositiveButton("OK") { dialog: DialogInterface, id: Int ->
                    dialog.cancel()
                    //                            finish();
                    UpdateDelivery()
                }
                .setNegativeButton(
                    "CANCEL"
                ) { dialog: DialogInterface, id: Int -> dialog.cancel() }
            val alert = builder.create()
            alert.setTitle(resources.getString(R.string.alert))
            alert.show()
        })


        binding!!.buttonVerify.setOnClickListener {
            val builder = AlertDialog.Builder(
                mContext!!
            )
            builder.setMessage(resources.getString(R.string.delivery_verify_content))
                .setCancelable(false)
                .setPositiveButton("OK") { dialog: DialogInterface, id: Int ->
                    dialog.cancel()
                    //                            finish();
                    verifyDelivery()
                }
                .setNegativeButton(
                    "CANCEL"
                ) { dialog: DialogInterface, id: Int -> dialog.cancel() }
            val alert = builder.create()
            alert.setTitle(resources.getString(R.string.alert))
            alert.show()
        }
        //  binding.ivChallanImage.setOnClickListener(this);
    }

    private fun UpdateDelivery() {
        if (isNetworkAvailable(mActivity!!)) {
            hideKeyboard(mActivity)
            showProgress()

            val USER_Id = preference!!.useR_Id
            val deliveryid = model!!.deliveryId.toString()
            val iris_inputserialno = binding!!.inputWsSerialno.text.toString()
            val inputserialRemark = binding!!.inputWsReamrk.text.toString()
            val service = RetrofitInstance.getRetrofitInstance().create(
                APIService::class.java
            )


            val attachmentPartsImage1: RequestBody
            val attachmentPartsImage2: RequestBody
            val attachmentPartsImage3: RequestBody
            var fileNamePartsImage1 = ""
            var fileNamePartsImage2 = ""
            var fileNamePartsImage3 = ""
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


            val call = service.updateDelivery(
                MultipartRequester.fromString(USER_Id),
                MultipartRequester.fromString(deliveryid),
                MultipartRequester.fromString(iris_inputserialno.trim { it <= ' ' }),
                MultipartRequester.fromString(inputserialRemark.trim { it <= ' ' }),
                createFormData(
                    "WeighingImage",
                    fileNamePartsImage2,
                    attachmentPartsImage2
                ),
                createFormData(
                    "DealerImage",
                    fileNamePartsImage1,
                    attachmentPartsImage1
                ),
                createFormData(
                    "ChallanImage",
                    fileNamePartsImage3,
                    attachmentPartsImage3
                )
            )

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
                                    binding!!.inputWsReamrk.setText("")
                                    binding!!.ivPartsImage1.setImageDrawable(resources.getDrawable(R.drawable.ic_baseline_image))
                                    binding!!.ivPartsImage2.setImageDrawable(resources.getDrawable(R.drawable.ic_baseline_image))
                                    binding!!.ivPartsImage3.setImageDrawable(resources.getDrawable(R.drawable.ic_baseline_image))
                                    partsImageStoragePath1 = ""
                                    partsImageStoragePath2 = ""
                                    partsImageStoragePath3 = ""
                                    GetErrorImages()
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

    private fun verifyDelivery() {
        val prefManager = PrefManager(mContext!!)
        showProgress(resources.getString(R.string.please_wait))
        val service = RetrofitInstance.getRetrofitInstance().create(
            APIService::class.java
        )
        val call = service.verifyDelivery(
            prefManager.useR_Id,
            model!!.deliveryId.toString(),
            model!!.fpscode,
            model!!.weighingScaleSerialNo
        )
        call.enqueue(object : Callback<ModelLogout?> {
            override fun onResponse(call: Call<ModelLogout?>, response: Response<ModelLogout?>) {
                hideProgress()
                if (response.isSuccessful) {
                    if (response.code() == 200) {
                        val model = response.body()
                        if (model!!.status == "200") {
                            val previousScreen = Intent(
                                applicationContext,
                                InstallationPendingList::class.java
                            )
                            setResult(RESULT_OK)
                            finish()
                        } else {
                            makeToast(model!!.message)
                        }
                    } else {
                        makeToast(resources.getString(R.string.error))
                    }
                } else {
                    makeToast(resources.getString(R.string.error))
                }
            }

            override fun onFailure(call: Call<ModelLogout?>, t: Throwable) {
                hideProgress()
                makeToast(resources.getString(R.string.error_message))
            }
        })
    }

    private val intentData: Unit
        get() {
            model = intent.getSerializableExtra("param") as WeighInsData?
        }

    private fun setUpData() {
        binding!!.inputFpsCode.text = model!!.fpscode
        binding!!.inputBlockName.text = model!!.blockName
        binding!!.inputDealerName.text = model!!.dealerName
        binding!!.inputMobileno.text = model!!.dealerMobileNo

        //        binding.inputWsSerialno.setText(model.getWeighingScaleSerialNo());
        binding!!.intputWsModel.text = model!!.weighingScaleModelName
        binding!!.inputtickretno.text = model!!.ticketNo
        binding!!.inputtransdate.text = model!!.winghingScaleDeliveredOnStr
        binding!!.inputstatus.text = model!!.last_TicketStatus
        binding!!.inputFpsCode.text = model!!.fpscode

        if (preference!!.useR_TYPE_ID == "1" || preference!!.useR_TYPE_ID == "2") {
            binding!!.inputWsSerialno.isEnabled = true
            binding!!.updateImageAria.visibility = View.VISIBLE
        } else {
            binding!!.inputWsSerialno.isEnabled = false
            binding!!.updateImageAria.visibility = View.GONE
        }

        //        if(Objects.equals(getIntent().getStringExtra("isFrom"), "View")){
//            binding.buttonVerify.setVisibility(View.GONE);
//        }else {

//        }
        if (!model!!.isDeliveryVerify && intent.getStringExtra("isFrom") == "Verify") {
            binding!!.buttonVerify.visibility = View.VISIBLE
        } else {
            binding!!.buttonVerify.visibility = View.GONE
        }
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

        //   ArrayList<String> myvalue = new ArrayList<String>();
        //   myvalue.add(model.getErrorType().toString());
        /* ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(ErrorposDetail.this, simple_spinner_item, myvalue);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
        binding.spinner.setAdapter(spinnerArrayAdapter);*/
        //   binding.spinner.setText(model.getErrorType());
        Log.d("getDealerMobileNo", "jfdl" + model!!.dealerMobileNo)
    }


    private fun selectImage(requestCode: Int) {
        try {
            val items = arrayOf<CharSequence>(
                resources.getString(R.string.imagepicker_str_take_photo),
                resources.getString(R.string.imagepicker_str_choose_from_gallery),
                resources.getString(R.string.imagepicker_str_cancel)
            )
            val title = TextView(mActivity)
            title.text = resources.getString(R.string.imagepicker_str_select_challan_image)
            title.setBackgroundColor(resources.getColor(R.color.colorActionBar))
            title.setPadding(15, 25, 15, 25)
            title.gravity = Gravity.CENTER
            title.setTextColor(Color.WHITE)
            title.textSize = 22f
            val builder = AlertDialog.Builder(
                mActivity!!
            )
            builder.setCustomTitle(title)
            // builder.setTitle("Add Photo!");
            builder.setItems(
                items
            ) { dialog: DialogInterface, item: Int ->
                if (items[item] == resources.getString(R.string.imagepicker_str_take_photo)) {
                    ImagePicker.with(mActivity)
                        .setToolbarColor("#FFBC45")
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
                } else if (items[item] == resources.getString(R.string.imagepicker_str_choose_from_gallery)) {
                    ImagePicker.with(mActivity)
                        .setToolbarColor("#FFBC45")
                        .setStatusBarColor("#000000")
                        .setToolbarTextColor("#FFFFFF")
                        .setToolbarIconColor("#FFFFFF")
                        .setProgressBarColor("#4CAF50")
                        .setBackgroundColor("#212121")
                        .setCameraOnly(false)
                        .setMultipleMode(false)
                        .setFolderMode(true)
                        .setShowCamera(false)
                        .setFolderTitle("Albums")
                        .setImageTitle("Galleries")
                        .setDoneTitle("Done")
                        .setMaxSize(1)
                        .setSavePath(Constants.saveImagePath)
                        .setSelectedImages(ArrayList())
                        .start(requestCode)
                } else if (items[item] == resources.getString(R.string.imagepicker_str_cancel)) {
                    dialog.dismiss()
                }
            }
            builder.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    private fun GetErrorImages() {
        Log.d("USER_ID>>>>>>>>", preference!!.useR_Id)
        if (isNetworkAvailable(mActivity!!)) {
            hideProgress()
            val apiInterface = RetrofitInstance.getRetrofitInstance().create(
                APIService::class.java
            )
            val USER_Id = preference!!.useR_Id
            val fpscode = model!!.fpscode
            val disid = model!!.districtId.toString()
            val modelTicketNo = model!!.ticketNo



            Log.d("fpscode", fpscode)
            Log.d("disid", disid)
            Log.d("modelTicketNo", modelTicketNo)
            // Log.d("district_Id",""+apiInterface
            val call = apiInterface.apiWeigDeliveryDetail(USER_Id, fpscode, disid, modelTicketNo)
            call.enqueue(object : Callback<weighingDelieryRoot?> {
                @SuppressLint("SetTextI18n")
                override fun onResponse(
                    call: Call<weighingDelieryRoot?>,
                    response: Response<weighingDelieryRoot?>
                ) {
                    hideProgress()

                    if (response.isSuccessful) {
                        try {
                            if (response.code() == 200) {
                                if (response.body() != null) {
                                    if (response.body()!!.status == "200") {
                                        val getErrorImagesRoot = response.body()
                                        val weighingDeliveryData = getErrorImagesRoot!!.data
                                        Log.d(
                                            "getErrorTypesRoot..",
                                            "getErrorTypesRoot..$getErrorImagesRoot"
                                        )
                                        binding!!.inputWsSerialno.setText(weighingDeliveryData.weighingScaleSerialNo)
                                        val getErrorImagesDatumArrayList =
                                            weighingDeliveryData.imagesDetail

                                        if (!getErrorImagesDatumArrayList.isEmpty()) {
                                            binding!!.tvUploadedimage.visibility = View.VISIBLE
                                            binding!!.rvViewimages.visibility = View.VISIBLE
                                            setUpAdapter(getErrorImagesDatumArrayList)
                                        } else {
                                            binding!!.tvUploadedimage.visibility = View.GONE
                                            binding!!.tvUploadedimage.visibility = View.GONE
                                        }
                                    } else {
                                        binding!!.rvViewimages.visibility = View.GONE
                                        binding!!.textNoDataFound.visibility = View.VISIBLE
                                    }
                                } else {
                                    binding!!.rvViewimages.visibility = View.GONE
                                    binding!!.textNoDataFound.visibility = View.VISIBLE
                                }
                            } else {
                                binding!!.rvViewimages.visibility = View.GONE
                                binding!!.textNoDataFound.visibility = View.VISIBLE
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                            binding!!.rvViewimages.visibility = View.GONE
                            binding!!.textNoDataFound.visibility = View.VISIBLE
                        }
                    } else {
                        //  makeToast(getResources().getString(R.string.error));
                        binding!!.rvViewimages.visibility = View.GONE
                        binding!!.textNoDataFound.visibility = View.VISIBLE
                    }
                }

                override fun onFailure(call: Call<weighingDelieryRoot?>, t: Throwable) {
                    hideProgress()

                    // makeToast(getResources().getString(R.string.error));
                    binding!!.rvViewimages.visibility = View.GONE
                    //      binding.textNoDataFound.setVisibility(View.VISIBLE);
                }
            })
        } else {
            makeToast(resources.getString(R.string.no_internet_connection))
        }
    }


    private fun setUpAdapter(getErrorImagesDatumArrayList: ArrayList<weighingDeliveryImagesDetail>) {
        // adapter = new ViewImagesListingAdapter(mActivity, getErrorImagesDatumArrayList, onItemViewClickListener);
        val adapter = ViewImagesListingAdapterIris(mActivity!!, getErrorImagesDatumArrayList)
        binding!!.rvViewimages.setHasFixedSize(true)
        val layoutManager = GridLayoutManager(mActivity, 3)
        binding!!.rvViewimages.layoutManager = layoutManager
        binding!!.rvViewimages.adapter = adapter
    }

    override fun onBackPressed() {
        super.onBackPressed()
        setResult(RESULT_OK)
        finish()
    }

    override fun onClick(view: View) {
        val id = view.id
        if (id == R.id.iv_back) {
            onBackPressed()
        }
    }

    override fun makeToast(string: String?) {
        if (TextUtils.isEmpty(string)) return
        Toast.makeText(mActivity, string, Toast.LENGTH_SHORT).show()
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_PICK_IMAGE_ONE && resultCode == RESULT_OK && data != null) {
            val images = data.getParcelableArrayListExtra<Image>(Config.EXTRA_IMAGES)
            if (images != null && !images.isEmpty()) {
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
            if (images != null && !images.isEmpty()) {
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
            if (images != null && !images.isEmpty()) {
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
}

