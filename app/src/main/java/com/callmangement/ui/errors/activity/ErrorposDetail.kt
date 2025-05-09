package com.callmangement.ui.errors.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.callmangement.R
import com.callmangement.custom.CustomActivity
import com.callmangement.databinding.ActivityErrorrPosDetailsBinding
import com.callmangement.imagepicker.model.Config
import com.callmangement.imagepicker.model.Image
import com.callmangement.imagepicker.ui.imagepicker.ImagePicker
import com.callmangement.network.APIService
import com.callmangement.network.RetrofitInstance
import com.callmangement.support.ImageUtilsForRotate.ensurePortrait
import com.callmangement.ui.errors.adapter.RemarkAdapter
import com.callmangement.ui.errors.adapter.ViewImagesListingAdapter
import com.callmangement.ui.errors.model.GetErrorImagesDatum
import com.callmangement.ui.errors.model.GetErrorImagesRoot
import com.callmangement.ui.errors.model.GetPosDeviceErrorDatum
import com.callmangement.ui.errors.model.GetRemarkDatum
import com.callmangement.ui.errors.model.GetRemarkRoot
import com.callmangement.ui.home.ZoomInZoomOutActivity
import com.callmangement.utils.CompressImage.Companion.compress
import com.callmangement.utils.Constants
import com.callmangement.utils.Constants.isNetworkAvailable
import com.callmangement.utils.EqualSpacingItemDecoration
import com.callmangement.utils.PrefManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.IOException


class ErrorposDetail : CustomActivity(), View.OnClickListener {
    private var binding: ActivityErrorrPosDetailsBinding? = null

    //private ModelExpensesList model;
    private var model: GetPosDeviceErrorDatum? = null

    private var prefManager: PrefManager? = null
    private val playerNames = ArrayList<String>()
    var selectedValue: String? = null
    var FPS_CODE: String? = null
    var Input_Device_Code: String? = null
    var Input_Remark: String? = null
    var stringArrayListHavingAllFilePath: ArrayList<String> = ArrayList()
    var mActivity: Activity? = null

    val REQUEST_PICK_IMAGE_ONE: Int = 1111
    val REQUEST_PICK_IMAGE_TWO: Int = 1112
    val REQUEST_PICK_IMAGE_THREE: Int = 1113
    private var adapter: ViewImagesListingAdapter? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityErrorrPosDetailsBinding.inflate(
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
        binding!!.actionBar.textToolbarTitle.text = resources.getString(R.string.error_detail)
        intentData

        setUpOnClickListener()
        setUpData()
        GetErrorImages()
        GetRemark()
    }


    private fun setUpOnClickListener() {
        binding!!.actionBar.ivBack.setOnClickListener(this)
        //  binding.ivChallanImage.setOnClickListener(this);
    }

    private val intentData: Unit
        get() {
            model = intent.getSerializableExtra("param") as GetPosDeviceErrorDatum?
        }

    private fun setUpData() {
        binding!!.inputDealerName.text = model!!.dealerName
        binding!!.inputMobileno.text = model!!.dealerMobileNo
        binding!!.inputDistrict.text = model!!.districtNameEng

        val myvalue = ArrayList<String>()


        myvalue.add(model!!.errorType)


        /* ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(ErrorposDetail.this, simple_spinner_item, myvalue);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
        binding.spinner.setAdapter(spinnerArrayAdapter);*/
        binding!!.inputFpsCode.text = model!!.fpscode
        binding!!.spinner.text = model!!.errorType

        //    Log.d("getDealerMobileNo","jfdl"+model.getDealerMobileNo());
        binding!!.inputRemark.text = model!!.remark
        //  binding.inputRemark.setText(model.getRemark());
        binding!!.inputDeviceCode.text = model!!.deviceCode.toString()

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
                //  binding.ivChallanImage.setImageBitmap(myBitmap);
                try {
                    ensurePortrait(imageStoragePath)
                } catch (e: IOException) {
                    e.printStackTrace()
                } catch (e: NullPointerException) {
                    e.printStackTrace()
                }
                stringArrayListHavingAllFilePath.add(imageStoragePath)
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
                    ensurePortrait(imageStoragePath)
                } catch (e: IOException) {
                    e.printStackTrace()
                } catch (e: NullPointerException) {
                    e.printStackTrace()
                }
                stringArrayListHavingAllFilePath.add(imageStoragePath)
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
                //   binding.ivPartsImage2.setImageBitmap(myBitmap);
                try {
                    ensurePortrait(imageStoragePath)
                } catch (e: IOException) {
                    e.printStackTrace()
                } catch (e: NullPointerException) {
                    e.printStackTrace()
                }
                stringArrayListHavingAllFilePath.add(imageStoragePath)
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    //private void GetErrorImages(String expenseStatusId, String districtId, String fromDate, String toDate){
    private fun GetErrorImages() {
        if (isNetworkAvailable(mContext!!)) {
            showProgress()
            val service = RetrofitInstance.getRetrofitInstance().create(
                APIService::class.java
            )
            val errorid = model!!.errorId.toString()
            val errorRegNo = model!!.errorRegNo.toString()

            val call = service.GetErrorImages(errorid, prefManager!!.useR_Id, errorRegNo)
            call.enqueue(object : Callback<GetErrorImagesRoot?> {
                @SuppressLint("SetTextI18n")
                override fun onResponse(
                    call: Call<GetErrorImagesRoot?>,
                    response: Response<GetErrorImagesRoot?>
                ) {
                    hideProgress()
                    if (response.isSuccessful) {
                        try {
                            if (response.code() == 200) {
                                if (response.body() != null) {
                                    if (response.body()!!.status == "200") {
                                        val getErrorImagesRoot = response.body()

                                        //   Log.d("getErrorTypesRoot..","getErrorTypesRoot.."+getErrorImagesRoot);
                                        val getErrorImagesDatumArrayList =
                                            getErrorImagesRoot!!.data


                                        if (getErrorImagesDatumArrayList.size > 0) {
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
                        makeToast(resources.getString(R.string.error))
                        binding!!.rvViewimages.visibility = View.GONE
                        binding!!.textNoDataFound.visibility = View.VISIBLE
                    }
                }

                override fun onFailure(call: Call<GetErrorImagesRoot?>, t: Throwable) {
                    hideProgress()
                    makeToast(resources.getString(R.string.error_message))
                    binding!!.rvViewimages.visibility = View.GONE
                    //      binding.textNoDataFound.setVisibility(View.VISIBLE);
                }
            })
        } else {
            makeToast(resources.getString(R.string.no_internet_connection))
        }
    }


    private fun setUpAdapter(getErrorImagesDatumArrayList: ArrayList<GetErrorImagesDatum>) {
        adapter = ViewImagesListingAdapter(
            mActivity!!,
            getErrorImagesDatumArrayList,
            onItemViewClickListener
        )
        binding!!.rvViewimages.setHasFixedSize(true)
        val layoutManager = GridLayoutManager(mActivity, 3)
        binding!!.rvViewimages.layoutManager = layoutManager
        binding!!.rvViewimages.addItemDecoration(
            EqualSpacingItemDecoration(
                30,
                EqualSpacingItemDecoration.VERTICAL
            )
        )
        binding!!.rvViewimages.adapter = adapter
    }

    var onItemViewClickListener: ViewImagesListingAdapter.OnItemViewClickListener =
        object : ViewImagesListingAdapter.OnItemViewClickListener {
            override fun onItemClick(campDocInfo: GetErrorImagesDatum?, position: Int) {
                startActivity(
                    Intent(mActivity, ZoomInZoomOutActivity::class.java).putExtra(
                        "image",
                        Constants.API_BASE_URL + campDocInfo!!.imagePath
                    )
                )
                //  Log.d("API_BASE_URL","API_BASE_URL "+Constants.API_BASE_URL +campDocInfo.getImagePath());
            }
        }


    private fun GetRemark() {
        if (isNetworkAvailable(mContext!!)) {
            showProgress()
            val service = RetrofitInstance.getRetrofitInstance().create(
                APIService::class.java
            )

            val errorid = model!!.errorId.toString()
            val errorRegNo = model!!.errorRegNo.toString()
            val call = service.GetErrorRemarks(errorid, prefManager!!.useR_Id, errorRegNo)
            call.enqueue(object : Callback<GetRemarkRoot?> {
                @SuppressLint("SetTextI18n")
                override fun onResponse(
                    call: Call<GetRemarkRoot?>,
                    response: Response<GetRemarkRoot?>
                ) {
                    hideProgress()
                    if (response.isSuccessful) {
                        try {
                            if (response.code() == 200) {
                                if (response.body() != null) {
                                    if (response.body()!!.status == "200") {
                                        val getErrorImagesRoot = response.body()

                                        //  Log.d("getErrorTypesRoot..","getErrorTypesRoot.."+getErrorImagesRoot);
                                        val getRemarkDatumArrayList =
                                            getErrorImagesRoot!!.data


                                        if (getRemarkDatumArrayList.size > 0) {
                                            binding!!.tvUploadedimage.visibility = View.VISIBLE
                                            binding!!.rvViewimages.visibility = View.VISIBLE
                                            setUpremarkAdapter(getRemarkDatumArrayList)
                                        } else {
                                            binding!!.tvUploadedimage.visibility = View.GONE
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
                        makeToast(resources.getString(R.string.error))
                        binding!!.rvViewimages.visibility = View.GONE
                        binding!!.textNoDataFound.visibility = View.VISIBLE
                    }
                }

                override fun onFailure(call: Call<GetRemarkRoot?>, t: Throwable) {
                    hideProgress()
                    makeToast(resources.getString(R.string.error_message))
                    binding!!.rvViewimages.visibility = View.GONE
                    //      binding.textNoDataFound.setVisibility(View.VISIBLE);
                }
            })
        } else {
            makeToast(resources.getString(R.string.no_internet_connection))
        }
    }


    private fun setUpremarkAdapter(getRemarkDatumArrayList: ArrayList<GetRemarkDatum>) {
        binding!!.rvRemark.layoutManager =
            LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false)
        binding!!.rvRemark.adapter = RemarkAdapter(mContext!!, getRemarkDatumArrayList)
    }


    override fun onClick(view: View) {
        val id = view.id
        if (id == R.id.iv_back) {
            onBackPressed()
        } else if (id == R.id.ivChallanImage) {
            //    startActivity(new Intent(mContext, ZoomInZoomOutActivity.class).putExtra("image", Constants.API_BASE_URL+""+model.getFilePath()));
        }
    }
}

