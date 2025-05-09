package com.callmangement.ui.errors.activity

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.callmangement.R
import com.callmangement.custom.CustomActivity
import com.callmangement.databinding.ActivityErrorrPosUpdateBinding
import com.callmangement.imagepicker.model.Config
import com.callmangement.imagepicker.model.Image
import com.callmangement.imagepicker.ui.imagepicker.ImagePicker
import com.callmangement.network.APIService
import com.callmangement.network.MultipartRequester
import com.callmangement.network.RetrofitInstance
import com.callmangement.support.ImageUtilsForRotate.ensurePortrait
import com.callmangement.support.OnSingleClickListener
import com.callmangement.ui.errors.model.GetPosDeviceErrorDatum
import com.callmangement.ui.errors.model.UpdateErroeReqRoot
import com.callmangement.utils.CompressImage.Companion.compress
import com.callmangement.utils.Constants
import com.callmangement.utils.Constants.isNetworkAvailable
import com.callmangement.utils.PrefManager
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.MultipartBody.Part.Companion.createFormData
import okhttp3.RequestBody
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.IOException
import java.util.Objects

class ErrorPosUpdateActivity : CustomActivity(), View.OnClickListener {
    private var binding: ActivityErrorrPosUpdateBinding? = null

    //private ModelExpensesList model;
    private var model: GetPosDeviceErrorDatum? = null

    private var prefManager: PrefManager? = null
    private val playerNames = ArrayList<String>()
    var selectedValue: String? = null
    var FPS_CODE: String? = null
    var Input_Device_Code: String? = null
    var Input_Remark: String? = null
    var inputMobileno: String? = null
    var ErrorTypeId: String? = null
    var stringArrayListHavingAllFilePath: ArrayList<String> = ArrayList()
    var mActivity: Activity? = null

    val REQUEST_PICK_IMAGE_ONE: Int = 1111
    val REQUEST_PICK_IMAGE_TWO: Int = 1112
    val REQUEST_PICK_IMAGE_THREE: Int = 1113

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityErrorrPosUpdateBinding.inflate(
            layoutInflater
        )
        setContentView(binding!!.root)
        initView()
    }

    private fun initView() {
        mActivity = this
        prefManager = PrefManager(mContext!!)
        binding!!.actionBar.ivBack.visibility = View.VISIBLE
        binding!!.actionBar.ivThreeDot.visibility = View.GONE
        binding!!.actionBar.layoutLanguage.visibility = View.GONE
        binding!!.actionBar.textToolbarTitle.text = resources.getString(R.string.updateerror)

        setUpOnClickListener()
        intentData
        setUpData()
    }

    private fun setUpOnClickListener() {
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






        binding!!.actionBar.ivBack.setOnClickListener(this)
        //  binding.ivChallanImage.setOnClickListener(this);
        binding!!.buttonUploadError.setOnClickListener(this)
    }

    private val intentData: Unit
        get() {
            model = intent.getSerializableExtra("param") as GetPosDeviceErrorDatum?
        }

    private fun setUpData() {
        val dealername = model!!.dealerName

        if (dealername != null) {
            binding!!.inputDealerName.text = dealername
        }

        if (model!!.dealerMobileNo != null) {
            binding!!.inputMobileno.setText(model!!.dealerMobileNo)
        }

        if (model!!.districtNameEng != null) {
            binding!!.inputDistrict.text = model!!.districtNameEng
        }

        val myvalue = ArrayList<String>()


        myvalue.add(model!!.errorType)


        binding!!.spinner.text = model!!.errorType


        if (model!!.fpscode != null) {
            binding!!.inputFpsCode.text = model!!.fpscode
        }
        if (model!!.remark != null) {
            binding!!.inputRemark.setText(model!!.remark)
        }
        if (model!!.deviceCode != null) {
            binding!!.inputDeviceCode.text = model!!.deviceCode.toString()
        }



        if (prefManager!!.useR_TYPE_ID == "1" && prefManager!!.useR_TYPE.equals(
                "Admin",
                ignoreCase = true
            )
        ) {
            binding!!.buttonUploadError.visibility = View.GONE
        } else if (prefManager!!.useR_TYPE_ID == "2" && prefManager!!.useR_TYPE.equals(
                "Manager",
                ignoreCase = true
            )
        ) {
            binding!!.buttonUploadError.visibility = View.GONE
        } else {
            if (model!!.errorStatus == "Pending") {
                binding!!.buttonUploadError.visibility = View.VISIBLE
            } else if (model!!.errorStatus == "Checking") {
                binding!!.buttonUploadError.visibility = View.VISIBLE
            } else if (model!!.errorStatus == "Resolved") {
                binding!!.buttonUploadError.visibility = View.GONE
            }
        }

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


    private fun updateerror(
        FPS_CODE: String,
        Input_Device_Code: String,
        Input_Remark: String,
        arrayHavingAllFilePath: ArrayList<String>
    ) {
        if (isNetworkAvailable(mContext!!)) {
            showProgress()
            val service = RetrofitInstance.getRetrofitInstance().create(
                APIService::class.java
            )
            val campDocumentsParts: Array<MultipartBody.Part?> = arrayOfNulls<MultipartBody.Part>(arrayHavingAllFilePath.size)
            for (index in arrayHavingAllFilePath.indices) {
                val file = File(arrayHavingAllFilePath[index])
                val surveyBody = RequestBody.create(
                    "image/*".toMediaTypeOrNull(),
                    file
                )
                campDocumentsParts[index] = createFormData(
                    "CampDocuments",
                    file.name,
                    surveyBody
                )
            }


            val ErrorStatusIdd = model!!.errorStatusId.toString()
            val ErrorId = model!!.errorId.toString()

            val call = service.UpdateErroeReqApi(
                MultipartRequester.fromString(ErrorId),

                MultipartRequester.fromString((prefManager!!.useR_Id)),
                MultipartRequester.fromString(FPS_CODE),
                MultipartRequester.fromString(Input_Device_Code),
                MultipartRequester.fromString(inputMobileno),  /*   MultipartRequester.fromString(ErrorTypeId),
                    MultipartRequester.fromString(ErrorStatusId),  */
                MultipartRequester.fromString(ErrorTypeId),
                MultipartRequester.fromString(ErrorStatusIdd),
                MultipartRequester.fromString(Input_Remark),
                campDocumentsParts
            )
            // Log.d("errorsaveresponse","response-----"+ErrorId+" "+ErrorStatusIdd+"  " +ErrorTypeId+" "+prefManager.getUseR_Id()+"  "+FPS_CODE+" "+Input_Device_Code+" "+Input_Remark+" " +campDocumentsParts);
            call.enqueue(object : Callback<UpdateErroeReqRoot?> {
                override fun onResponse(
                    call: Call<UpdateErroeReqRoot?>,
                    response: Response<UpdateErroeReqRoot?>
                ) {
                    hideProgress()
                    if (response.isSuccessful) {
                        if (response.code() == 200 && response.body() != null) {
                            //   Toast.makeText(ErrorPosUpdateActivity.this, "susesss", Toast.LENGTH_SHORT).show();

                            // if (response.body().getStatus().equals("200")) {

                            if (Objects.requireNonNull(response.body() != null)) {
                                val getErrorTypesRoot = response.body()

                                val status = getErrorTypesRoot!!.status

                                if (status == "200") {
                                    val checkstatus = getErrorTypesRoot.response.isStatus
                                    if (checkstatus) {
                                        makeToast(response.body()!!.response.message)
                                        //   Log.d("checkstatus","checkstatus"+checkstatus);
                                        val i = Intent(
                                            this@ErrorPosUpdateActivity,
                                            ErrorPosActivity::class.java
                                        )
                                        startActivity(i)
                                        makeToast(response.body()!!.response.message)
                                    } else {
                                        makeToast(response.body()!!.response.message)
                                    }
                                } else {
                                    makeToast(response.body()!!.response.message)
                                }


                                //      Log.d("getErrorTypesRoot..","getErrorTypesRoot.."+getErrorTypesRoot);


                                //   makeToast(response.body().getResponse().getMessage());


                                //  Log.d(String.valueOf(ErrorPosUpdateActivity.this),"fggafdaf"+getErrorTypesRoot);

                                // makeToast(response.body().getMessage());
                                // finish();
                            } else {
                                makeToast(resources.getString(R.string.error))
                            }
                        } else {
                            makeToast(resources.getString(R.string.error))
                        }
                    } else {
                        //  makeToast(getResources().getString(R.string.error));
                    }
                }

                override fun onFailure(call: Call<UpdateErroeReqRoot?>, error: Throwable) {
                    //    Log.d("responseerror","response-----"+error);

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
                } else if (items[item] == resources.getString(R.string.imagepicker_str_choose_from_gallery)) {
                    ImagePicker.with(mActivity)
                        .setToolbarColor("#212121")
                        .setStatusBarColor("#000000")
                        .setToolbarTextColor("#FFFFFF")
                        .setToolbarIconColor("#FFFFFF")
                        .setProgressBarColor("#4CAF50")
                        .setBackgroundColor("#212121")
                        .setCameraOnly(false)
                        .setMultipleMode(true)
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

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_PICK_IMAGE_ONE && resultCode == RESULT_OK && data != null) {
            val images = data.getParcelableArrayListExtra<Image>(Config.EXTRA_IMAGES)
            if (images != null && images.size > 0) {
                val image = images[0]
                var imageStoragePath = image.path
                if (imageStoragePath.contains("file:/")) {
                    imageStoragePath = imageStoragePath.replace("file:/", "")
                }
                imageStoragePath = compress(
                    imageStoragePath,
                    this
                )
                val imgFile = File(imageStoragePath)
                val myBitmap = BitmapFactory.decodeFile(imgFile.absolutePath)

                try {
                    binding!!.ivChallanImage.setImageBitmap(ensurePortrait(imageStoragePath))
                    stringArrayListHavingAllFilePath.add(imageStoragePath)
                } catch (e: IOException) {
                    binding!!.ivChallanImage.setImageBitmap(myBitmap)
                    stringArrayListHavingAllFilePath.add(imageStoragePath)

                    e.printStackTrace()
                } catch (e: NullPointerException) {
                    e.printStackTrace()
                }
            }
        } else if (requestCode == REQUEST_PICK_IMAGE_TWO && resultCode == RESULT_OK && data != null) {
            val images = data.getParcelableArrayListExtra<Image>(Config.EXTRA_IMAGES)
            if (images != null && images.size > 0) {
                val image = images[0]
                var imageStoragePath = image.path
                if (imageStoragePath.contains("file:/")) {
                    imageStoragePath = imageStoragePath.replace("file:/", "")
                }
                imageStoragePath = compress(
                    imageStoragePath,
                    this
                )
                val imgFile = File(imageStoragePath)
                val myBitmap = BitmapFactory.decodeFile(imgFile.absolutePath)
                try {
                    binding!!.ivPartsImage1.setImageBitmap(ensurePortrait(imageStoragePath))
                    stringArrayListHavingAllFilePath.add(imageStoragePath)
                } catch (e: IOException) {
                    binding!!.ivPartsImage1.setImageBitmap(myBitmap)
                    stringArrayListHavingAllFilePath.add(imageStoragePath)
                    e.printStackTrace()
                } catch (e: NullPointerException) {
                    e.printStackTrace()
                }
            }
        } else if (requestCode == REQUEST_PICK_IMAGE_THREE && resultCode == RESULT_OK && data != null) {
            val images = data.getParcelableArrayListExtra<Image>(Config.EXTRA_IMAGES)
            if (images != null && images.size > 0) {
                val image = images[0]
                var imageStoragePath = image.path
                if (imageStoragePath.contains("file:/")) {
                    imageStoragePath = imageStoragePath.replace("file:/", "")
                }
                imageStoragePath = compress(
                    imageStoragePath,
                    this
                )
                val imgFile = File(imageStoragePath)
                val myBitmap = BitmapFactory.decodeFile(imgFile.absolutePath)
                try {
                    binding!!.ivPartsImage2.setImageBitmap(ensurePortrait(imageStoragePath))
                    stringArrayListHavingAllFilePath.add(imageStoragePath)
                } catch (e: IOException) {
                    binding!!.ivPartsImage2.setImageBitmap(myBitmap)
                    stringArrayListHavingAllFilePath.add(imageStoragePath)
                    e.printStackTrace()
                } catch (e: NullPointerException) {
                    e.printStackTrace()
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }


    private fun completeExpense() {
        if (isNetworkAvailable(mContext!!)) {
            showProgress()
            val service = RetrofitInstance.getRetrofitInstance().create(
                APIService::class.java
            )
            val call = service.completeExpenses(prefManager!!.useR_Id, model!!.errorId.toString())
            call.enqueue(object : Callback<ResponseBody?> {
                override fun onResponse(
                    call: Call<ResponseBody?>,
                    response: Response<ResponseBody?>
                ) {
                    hideProgress()
                    try {
                        if (response.isSuccessful) {
                            if (response.code() == 200) {
                                if (response.body() != null) {
                                    val responseStr = response.body()!!.string()
                                    val jsonObject = JSONObject(responseStr)
                                    val status = jsonObject.optString("status")
                                    val message = jsonObject.optString("message")
                                    if (status == "200") {
                                        Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show()
                                        onBackPressed()
                                    } else {
                                        makeToast(message)
                                    }
                                }
                            }
                        } else {
                            makeToast(resources.getString(R.string.error))
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                    hideProgress()
                    makeToast(resources.getString(R.string.error_message))
                }
            })
        } else makeToast(resources.getString(R.string.no_internet_connection))
    }

    override fun onClick(view: View) {
        val id = view.id
        if (id == R.id.iv_back) {
            onBackPressed()
            // } else if (id == R.id.ivChallanImage){
            //   startActivity(new Intent(mContext, ZoomInZoomOutActivity.class).putExtra("image", Constants.API_BASE_URL+""+model.getFilePath()));
        } else if (id == R.id.buttonComplete) {
            val builder = AlertDialog.Builder(
                mContext!!
            )
            builder.setMessage(resources.getString(R.string.complete_expense_dialog_msg))
                .setCancelable(false)
                .setPositiveButton(resources.getString(R.string.ok)) { dialog: DialogInterface, ids: Int ->
                    dialog.cancel()
                    completeExpense()
                }
                .setNegativeButton(
                    resources.getString(R.string.cancel)
                ) { dialog: DialogInterface, ids: Int -> dialog.cancel() }
            val alert = builder.create()
            alert.setTitle(resources.getString(R.string.alert))
            alert.show()
        } else if (id == R.id.buttonUploadError) {
            //  selectedValue = binding.spinner.getSelectedItem().toString();
            //   Log.d("selectedValue", " "+selectedValue);

            FPS_CODE = binding!!.inputFpsCode.text.toString()
            Input_Device_Code = binding!!.inputDeviceCode.text.toString()
            inputMobileno = binding!!.inputMobileno.text.toString()
            ErrorTypeId = model!!.errorTypeId.toString()


            Input_Remark = binding!!.inputRemark.text.toString()

            /*       if (selectItem == null || selectedValue.equals(tv_select) || selectedValue.isEmpty() || selectedValue.length() == 0) {
                        makeToast(getResources().getString(R.string.select_errortype));
                    }
                else*/
            if (FPS_CODE == null || FPS_CODE!!.isEmpty() || FPS_CODE!!.length == 0) {
                makeToast(resources.getString(R.string.enter_fps_code))
            } else if (Input_Remark == null || Input_Remark!!.isEmpty() || Input_Remark!!.length == 0) {
                makeToast(resources.getString(R.string.please_enter_remark))
            } else {
                updateerror(
                    FPS_CODE!!,
                    Input_Device_Code!!, Input_Remark!!, stringArrayListHavingAllFilePath
                )
                Log.d(
                    "response",
                    "FPS response--------------$FPS_CODE $Input_Device_Code $Input_Remark"
                )
                //  Log.d("response","response--------------"+FPS_CODE+" "+selectedValue+" "+Input_Device_Code+" "+Input_Remark+" "+stringArrayListHavingAllFilePath+" "+
                //   stringArrayListHavingAllFilePath.size());
            }
        }
    }
}