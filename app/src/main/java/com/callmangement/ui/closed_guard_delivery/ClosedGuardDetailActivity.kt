package com.callmangement.ui.closed_guard_delivery

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
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
import com.bumptech.glide.Glide
import com.callmangement.Network.APIService
import com.callmangement.Network.RetrofitInstance
import com.callmangement.R
import com.callmangement.custom.CustomActivity
import com.callmangement.databinding.ActivityClosedGuardDetailBinding
import com.callmangement.imagepicker.ui.imagepicker.ImagePicker
import com.callmangement.ui.closed_guard_delivery.model.ClosedGuardDeliveryListResponse
import com.callmangement.ui.home.ZoomInZoomOutActivity
import com.callmangement.ui.iris_derivery_installation.Model.InstalledDetailedResponse
import com.callmangement.ui.iris_derivery_installation.Model.IrisInstalledImagesDtl
import com.callmangement.ui.iris_derivery_installation.adapter.ViewImageInstalledDetailsAdapterIris
import com.callmangement.utils.Constants
import com.callmangement.utils.EqualSpacingItemDecoration
import com.callmangement.utils.PrefManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Objects

class ClosedGuardDetailActivity : CustomActivity(), View.OnClickListener {
    private var binding: ActivityClosedGuardDetailBinding? = null

    //private ModelExpensesList model;
    private var model: ClosedGuardDeliveryListResponse.Datum? = null
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
    private var adapter: ViewImageInstalledDetailsAdapterIris? = null
    private var adapterSec: ViewImageInstalledDetailsAdapterIris? = null
    var mActivity: Activity? = null
    var mContext: Context? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE) //will hide the title
        //    getSupportActionBar().hide(); // hide the title bar
        binding = ActivityClosedGuardDetailBinding.inflate(
            layoutInflater
        )
        setContentView(binding!!.root)
        initView()
    }

    private fun initView() {
        mActivity = this
        mContext = this
        preference = PrefManager(mContext)
        binding!!.actionBar.ivBack.visibility = View.VISIBLE
        binding!!.actionBar.textToolbarTitle.text =
            resources.getString(R.string.closed_guard_detail)
        intentData
        setUpOnClickListener()
        setUpData()
        //    GetErrorImages();
    }

    private fun setUpOnClickListener() {
        binding!!.actionBar.ivBack.setOnClickListener { finish() }
        //  binding.ivChallanImage.setOnClickListener(this);
    }

    private val intentData: Unit
        get() {
            model =
                intent.getSerializableExtra("param") as ClosedGuardDeliveryListResponse.Datum?
        }

    private fun setUpData() {
        //        binding.inputFpsCode.setText(model.getFpscode());
//        binding.inputBlockName.setText(model.getBlockName());
//        binding.inputDealerName.setText(model.getDealerName());
//        binding.inputMobileno.setText(model.getDealerMobileNo());
//        binding.inputWsSerialno.setText(model.getWeighingScaleSerialNo());
//        binding.intputWsModel.setText(model.getWeighingScaleModelName());
//        binding.inputtickretno.setText(model.getTicketNo());
//        binding.inputdelivereddate.setText(model.getWinghingScaleDeliveredOnStr());
//        binding.inputinstalleddate.setText(model.getInstallationOnStr());
//        binding.inputstatus.setText(model.getLast_TicketStatus());
//        binding.inputSerialno.setText(model.getIrisScannerSerialNo());
//        binding.intputIrisModel.setText(model.getIrisDeviceModel());
//        binding.inputaddress.setText(model.getShopAddress());

        binding!!.inputFpsCode.text = model!!.fpscode
        binding!!.inputBlockName.text = model!!.blockName
        binding!!.inputDealerName.text = model!!.dealerName
        binding!!.inputMobileno.text = model!!.dealerMobileNo
        //    binding.inputWsSerialno.setText(model.getWeighingScaleSerialNo());
        //     binding.intputWsModel.setText(model.getWeighingScaleModelName());
        //     binding.inputtickretno.setText(model.getTicketNo());
        binding!!.inputdelivereddate.text = model!!.closeGuardDeliverdOnStr
        //    binding.inputinstalleddate.setText(model.getInstallationOnStr());
        //     binding.inputstatus.setText(model.getDeliverdStatus());
        binding!!.inputSerialno.text = model!!.serialNo
        binding!!.intputIrisModel.text = model!!.deviceModelName
        binding!!.inputaddress.text = model!!.closeGuardDeliveryAddress

        Glide.with(mContext)
            .load(model!!.cgphotoPath)
            .placeholder(R.drawable.image_not_fount)
            .into(binding!!.imgDealer)

        Glide.with(mContext)
            .load(model!!.cgsignaturePath)
            .placeholder(R.drawable.image_not_fount)
            .into(binding!!.imgSign)

        binding!!.imgDealer.setOnClickListener {
            startActivity(
                Intent(
                    mContext,
                    ZoomInZoomOutActivity::class.java
                ).putExtra("image", model!!.cgphotoPath)
            )
        }

        binding!!.imgSign.setOnClickListener {
            startActivity(
                Intent(
                    mContext,
                    ZoomInZoomOutActivity::class.java
                ).putExtra("image", model!!.cgsignaturePath)
            )
        }


        //   ArrayList<String> myvalue = new ArrayList<String>();
        //   myvalue.add(model.getErrorType().toString());

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
        if (Constants.isNetworkAvailable(mActivity)) {
            hideProgress()
            val apiInterface = RetrofitInstance.getRetrofitInstance().create(
                APIService::class.java
            )
            val USER_Id = preference!!.useR_Id
            val fpscode = model!!.fpscode
            val disid = model!!.districtId.toString()
            //   String modelTicketNo = model.getTicketNo();
            val modelTicketNo = "model.getTicketNo()"
            val deliveryid = model!!.deliveryId.toString()
            Log.d("USER_ID", "--" + preference!!.useR_Id)
            Log.d("fpscode", "--$fpscode")
            Log.d("disid", "--$disid")
            //        Log.d("modelTicketNo","--"+ modelTicketNo);
            Log.d("deliveryid", "--$deliveryid")
            // Log.d("district_Id",""+apiInterface
            val call = apiInterface.IrisDeliveryInstallationDtl(
                USER_Id,
                fpscode,
                disid,
                modelTicketNo,
                deliveryid
            )
            call.enqueue(object : Callback<InstalledDetailedResponse?> {
                @SuppressLint("SetTextI18n")
                override fun onResponse(
                    call: Call<InstalledDetailedResponse?>,
                    response: Response<InstalledDetailedResponse?>
                ) {
                    hideProgress()
                    if (response.isSuccessful) {
                        try {
                            if (response.code() == 200) {
                                if (response.body() != null) {
                                    if (response.body()!!.getStatus() == "200"
                                    ) {
                                        val getErrorImagesRoot = response.body()
                                        val weighingDeliveryData = getErrorImagesRoot!!.data
                                        Log.d(
                                            "getErrorTypesRoot..",
                                            "getErrorTypesRoot..$getErrorImagesRoot"
                                        )
                                        val getErrorImagesDatumArrayList =
                                            weighingDeliveryData.getImagesDetail()
                                        if (getErrorImagesDatumArrayList.size > 0) {
                                            //            binding.tvUploadedimage.setVisibility(View.VISIBLE);
                                            //            binding.rvViewimages.setVisibility(View.VISIBLE);
                                            setUpAdapter(getErrorImagesDatumArrayList)
                                        } else {
                                            //            binding.tvUploadedimage.setVisibility(View.VISIBLE);
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

                override fun onFailure(call: Call<InstalledDetailedResponse?>, t: Throwable) {
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

    private fun setUpAdapter(getErrorImagesDatumArrayList: ArrayList<IrisInstalledImagesDtl>) {
//        ArrayList<IrisInstalledImagesDtl> getErrorImagesDatumArrayListOne =
//                getErrorImagesDatumArrayList;
//        ArrayList<IrisInstalledImagesDtl> getErrorImagesDatumArrayListTwo =
//                getErrorImagesDatumArrayList;
        val getErrorImagesDatumArrayListOne = ArrayList<IrisInstalledImagesDtl>()
        val getErrorImagesDatumArrayListTwo = ArrayList<IrisInstalledImagesDtl>()

        var aa = 0
        var aass = 0

        //        getErrorImagesDatumArrayListOne.clear();
//        getErrorImagesDatumArrayListTwo.clear();
        for (i in getErrorImagesDatumArrayList.indices) {
            if (getErrorImagesDatumArrayList[i].getPhotoTypeId() <= 5) {
                getErrorImagesDatumArrayListOne.add(getErrorImagesDatumArrayList[i])
                aa = aa + 1
            } else {
                getErrorImagesDatumArrayListTwo.add(getErrorImagesDatumArrayList[i])
                aass = aass + 1
            }
        }
        // adapter = new ViewImagesListingAdapter(mActivity, getErrorImagesDatumArrayList, onItemViewClickListener);
        adapter = ViewImageInstalledDetailsAdapterIris(
            mActivity,
            getErrorImagesDatumArrayListOne,
            true,
            aa
        )
        adapterSec = ViewImageInstalledDetailsAdapterIris(
            mActivity,
            getErrorImagesDatumArrayListTwo,
            false,
            aass
        )
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

    override fun makeToast(string: String) {
        if (TextUtils.isEmpty(string)) return
        Toast.makeText(mActivity, string, Toast.LENGTH_SHORT).show()
    }
}


