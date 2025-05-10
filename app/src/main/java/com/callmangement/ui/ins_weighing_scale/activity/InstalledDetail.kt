package com.callmangement.ui.ins_weighing_scale.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.DialogInterface
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
import com.callmangement.databinding.ActivityInstalledListdetailedBinding
import com.callmangement.imagepicker.ui.imagepicker.ImagePicker
import com.callmangement.network.APIService
import com.callmangement.network.RetrofitInstance
import com.callmangement.ui.ins_weighing_scale.adapter.ViewImageInstalledAdapterIris
import com.callmangement.ui.ins_weighing_scale.model.Installed.InstalledDatum
import com.callmangement.ui.ins_weighing_scale.model.InstalledDetailed.InstalledDetailedRoot
import com.callmangement.ui.ins_weighing_scale.model.InstalledDetailed.InstalledImagesDetail
import com.callmangement.utils.Constants
import com.callmangement.utils.Constants.isNetworkAvailable
import com.callmangement.utils.EqualSpacingItemDecoration
import com.callmangement.utils.PrefManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class InstalledDetail : CustomActivity(), View.OnClickListener {
    private var binding: ActivityInstalledListdetailedBinding? = null

    //private ModelExpensesList model;
    private var model: InstalledDatum? = null
    var preference: PrefManager? = null
    private val playerNames = ArrayList<String>()
    var selectedValue: String? = null
    var FPS_CODE: String? = null
    var Input_Device_Code: String? = null
    var Input_Remark: String? = null
    var stringArrayListHavingAllFilePath: ArrayList<String> = ArrayList()
    val REQUEST_PICK_IMAGE_ONE: Int = 1111
    val REQUEST_PICK_IMAGE_TWO: Int = 1112
    val REQUEST_PICK_IMAGE_THREE: Int = 1113
    private var adapter: ViewImageInstalledAdapterIris? = null
    private var adapterSec: ViewImageInstalledAdapterIris? = null
    var mActivity: Activity? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE) //will hide the title
        //    getSupportActionBar().hide(); // hide the title bar
        binding = ActivityInstalledListdetailedBinding.inflate(
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
        binding!!.actionBar.ivBack.setOnClickListener { finish() }
        //  binding.ivChallanImage.setOnClickListener(this);
    }

    private val intentData: Unit
        get() {
            model = intent.getSerializableExtra("param") as InstalledDatum?
        }

    private fun setUpData() {
        binding!!.inputFpsCode.text = model!!.fpscode
        binding!!.inputBlockName.text = model!!.blockName
        binding!!.inputDealerName.text = model!!.dealerName
        binding!!.inputMobileno.text = model!!.dealerMobileNo
        binding!!.inputWsSerialno.text = model!!.weighingScaleSerialNo
        binding!!.intputWsModel.text = model!!.weighingScaleModelName
        binding!!.inputtickretno.text = model!!.ticketNo
        binding!!.inputdelivereddate.text = model!!.winghingScaleDeliveredOnStr
        binding!!.inputinstalleddate.text = model!!.installationOnStr
        binding!!.inputstatus.text = model!!.last_TicketStatus
        binding!!.inputSerialno.setText(model!!.irisScannerSerialNo)
        binding!!.intputIrisModel.text = model!!.irisDeviceModel
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

    private fun GetErrorImages() {
        if (isNetworkAvailable(mActivity!!)) {
            hideProgress()
            val apiInterface = RetrofitInstance.getRetrofitInstance().create(
                APIService::class.java
            )
            val USER_Id = preference!!.useR_Id
            val fpscode = model!!.fpscode
            val disid = model!!.districtId.toString()
            val modelTicketNo = model!!.ticketNo
            val deliveryid = model!!.deliveryId.toString()
            Log.d("USER_ID", preference!!.useR_Id)
            Log.d("fpscode", fpscode)
            Log.d("disid", disid)
            Log.d("modelTicketNo", modelTicketNo)
            Log.d("deliveryid", deliveryid)
            // Log.d("district_Id",""+apiInterface
            val call = apiInterface.IrisWeighInstallationDtl(
                USER_Id,
                fpscode,
                disid,
                modelTicketNo,
                deliveryid
            )
            call.enqueue(object : Callback<InstalledDetailedRoot?> {
                @SuppressLint("SetTextI18n")
                override fun onResponse(
                    call: Call<InstalledDetailedRoot?>,
                    response: Response<InstalledDetailedRoot?>
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
                                        val getErrorImagesDatumArrayList =
                                            weighingDeliveryData.imagesDetail
                                        if (getErrorImagesDatumArrayList.size > 0) {
                                            binding!!.tvUploadedimage.visibility = View.VISIBLE
                                            binding!!.rvViewimages.visibility = View.VISIBLE
                                            setUpAdapter(getErrorImagesDatumArrayList)
                                        } else {
                                            binding!!.tvUploadedimage.visibility = View.VISIBLE
                                            binding!!.textNoDataFound.visibility = View.GONE
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
                                val msg = "HTTP Error: " + response.code()
                                showAlertDialogWithSingleButton(mActivity, msg)
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                            binding!!.rvViewimages.visibility = View.GONE
                            binding!!.textNoDataFound.visibility = View.VISIBLE
                        }
                    } else {
                        //  makeToast(getResources().getString(R.string.error));
                        val msg = "HTTP Error: " + response.code()
                        showAlertDialogWithSingleButton(mActivity, msg)
                        binding!!.rvViewimages.visibility = View.GONE
                        binding!!.textNoDataFound.visibility = View.VISIBLE
                    }
                }

                override fun onFailure(call: Call<InstalledDetailedRoot?>, t: Throwable) {
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

    private fun setUpAdapter(getErrorImagesDatumArrayList: ArrayList<InstalledImagesDetail>) {
//        ArrayList<InstalledImagesDetail> getErrorImagesDatumArrayListOne =
//                getErrorImagesDatumArrayList;
//        ArrayList<InstalledImagesDetail> getErrorImagesDatumArrayListTwo =
//                getErrorImagesDatumArrayList;
//
        val getErrorImagesDatumArrayListOne = ArrayList<InstalledImagesDetail>()
        val getErrorImagesDatumArrayListTwo = ArrayList<InstalledImagesDetail>()

        var aa = 0
        var aass = 0

        //        getErrorImagesDatumArrayListOne.clear();
//        getErrorImagesDatumArrayListTwo.clear();
        for (i in getErrorImagesDatumArrayList.indices) {
            if (getErrorImagesDatumArrayList[i].photoTypeId <= 5) {
                getErrorImagesDatumArrayListOne.add(getErrorImagesDatumArrayList[i])
                aa = aa + 1
            } else {
                getErrorImagesDatumArrayListTwo.add(getErrorImagesDatumArrayList[i])
                aass = aass + 1
            }
        }
        // adapter = new ViewImagesListingAdapter(mActivity, getErrorImagesDatumArrayList, onItemViewClickListener);
        adapter =
            ViewImageInstalledAdapterIris(mActivity!!, getErrorImagesDatumArrayListOne, true, aa)
        adapterSec =
            ViewImageInstalledAdapterIris(mActivity!!, getErrorImagesDatumArrayListTwo, false, aass)
        binding!!.rvViewimages.setHasFixedSize(true)
        val layoutManager: GridLayoutManager = object : GridLayoutManager(mActivity, 3) {
            override fun canScrollVertically(): Boolean {
                return false
            }
        }
        binding!!.rvViewimages.layoutManager = layoutManager
        binding!!.rvViewimages.addItemDecoration(
            EqualSpacingItemDecoration(
                30,
                EqualSpacingItemDecoration.VERTICAL
            )
        )
        binding!!.rvViewimages.adapter = adapter

        binding!!.rvViewimagesSec.setHasFixedSize(true)
        val layoutManagerSec: GridLayoutManager = object : GridLayoutManager(mActivity, 3) {
            override fun canScrollVertically(): Boolean {
                return false
            }
        }
        binding!!.rvViewimagesSec.layoutManager = layoutManagerSec
        binding!!.rvViewimagesSec.addItemDecoration(
            EqualSpacingItemDecoration(
                30,
                EqualSpacingItemDecoration.VERTICAL
            )
        )
        binding!!.rvViewimagesSec.adapter = adapterSec
    }

    /*  ViewImagesListingAdapter.OnItemViewClickListener onItemViewClickListener = new ViewImagesListingAdapter.OnItemViewClickListener() {
            @Override
            public void onItemClick(weighingDeliveryImagesDetail campDocInfo, int position) {
                startActivity(new Intent(mActivity, ZoomInZoomOutActivity.class).putExtra("image", Utils.Baseurl +campDocInfo.getImagePath()));
               // Log.d("API_BASE_URL","API_BASE_URL "+Constants.API_BASE_URL +campDocInfo.getImagePath());
            }
        };
    */
    override fun onClick(view: View) {
        val id = view.id
        if (id == R.id.iv_back) {
            onBackPressed()
        } // else if (id == R.id.ivChallanImage){

        //    startActivity(new Intent(mContext, ZoomInZoomOutActivity.class).putExtra("image", Constants.API_BASE_URL+""+model.getFilePath()));
        //    }
    }

    override fun makeToast(string: String?) {
        if (TextUtils.isEmpty(string)) return
        Toast.makeText(mActivity, string, Toast.LENGTH_SHORT).show()
    }
}

