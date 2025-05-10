package com.callmangement.ui.inventory

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.callmangement.R
import com.callmangement.adapter.ReceiveMaterialListDetailsActivityAdapter
import com.callmangement.custom.CustomActivity
import com.callmangement.databinding.ActivityReceiveMaterialListDetailsBinding
import com.callmangement.imagepicker.model.Config
import com.callmangement.imagepicker.model.Image
import com.callmangement.imagepicker.ui.imagepicker.ImagePicker
import com.callmangement.model.inventrory.ModelPartsDispatchInvoiceList
import com.callmangement.network.APIService
import com.callmangement.network.MultipartRequester
import com.callmangement.network.RetrofitInstance
import com.callmangement.support.ImageUtilsForRotate.ensurePortrait
import com.callmangement.support.dexter.Dexter
import com.callmangement.support.dexter.MultiplePermissionsReport
import com.callmangement.support.dexter.PermissionToken
import com.callmangement.support.dexter.listener.PermissionRequest
import com.callmangement.support.dexter.listener.multi.MultiplePermissionsListener
import com.callmangement.ui.home.ZoomInZoomOutActivity
import com.callmangement.ui.model.inventory.receive_invoice_parts.PartsDispatchInvoice
import com.callmangement.ui.model.inventory.receive_invoice_parts.ReceiveInvoicePartsResponse
import com.callmangement.utils.CompressImage.Companion.compress
import com.callmangement.utils.Constants
import com.callmangement.utils.Constants.isNetworkAvailable
import com.callmangement.utils.PrefManager
import com.google.gson.Gson
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.IOException

class ReceiveMaterialListDetailsActivity : CustomActivity(), View.OnClickListener {
    private var binding: ActivityReceiveMaterialListDetailsBinding? = null
    private var prefManager: PrefManager? = null
    private var model: ModelPartsDispatchInvoiceList? = null
    private var mActivity: Activity? = null
    var partsList: List<PartsDispatchInvoice>? = null
    var invoiceId: String? = ""
    var dispatchId: String? = ""
    var userId: String = ""
    private var challanImageStoragePath = ""
    private var permissionGranted = false
    val REQUEST_PICK_CHALLAN_IMAGES: Int = 1113

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_receive_material_list_details)
        binding!!.actionBar.ivBack.visibility = View.VISIBLE
        binding!!.actionBar.ivThreeDot.visibility = View.GONE
        binding!!.actionBar.layoutLanguage.visibility = View.GONE
        binding!!.actionBar.textToolbarTitle.text = resources.getString(R.string.receive_material)
        prefManager = PrefManager(mContext!!)
        model = intent.getSerializableExtra("param") as ModelPartsDispatchInvoiceList?
        initView()
    }

    @SuppressLint("SetTextI18n")
    private fun initView() {
        mActivity = this

        if (model != null) {
            if (model!!.isReceived.equals("true", ignoreCase = true)) {
                binding!!.buttonReceive.visibility = View.GONE
                binding!!.ivPartsImage.visibility = View.GONE
                binding!!.ivPartsImageView.visibility = View.VISIBLE
                Glide.with(mContext!!)
                    .load(Constants.API_BASE_URL + model!!.receivedPartsImage)
                    .placeholder(R.drawable.image_not_fount)
                    .into(binding!!.ivPartsImageView)
                /*binding.inputRemark.setEnabled(false);
                binding.inputRemark.setText(""+model.getReceiverRemark());*/
            } else if (model!!.isReceived.equals("false", ignoreCase = true)) {
                binding!!.buttonReceive.visibility = View.VISIBLE
                binding!!.ivPartsImage.visibility = View.VISIBLE
                binding!!.ivPartsImageView.visibility = View.GONE
                //                binding.inputRemark.setEnabled(true);
            }
        }

        checkPermission()
        setUpClickListener()
        setUpData()
        setReceiveMaterialListAdapter()
    }

    private fun checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Dexter.withContext(this)
                .withPermissions(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.READ_MEDIA_IMAGES,
                    Manifest.permission.READ_MEDIA_VIDEO,
                    Manifest.permission.CAMERA
                ).withListener(object : MultiplePermissionsListener {
                    override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                        if (report.areAllPermissionsGranted()) {
                            permissionGranted = true
                        }
                    }

                    override fun onPermissionRationaleShouldBeShown(
                        permissions: List<PermissionRequest>,
                        token: PermissionToken
                    ) {
                        token.continuePermissionRequest()
                    }
                }).check()
        } else {
            Dexter.withContext(this)
                .withPermissions(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA
                ).withListener(object : MultiplePermissionsListener {
                    override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                        if (report.areAllPermissionsGranted()) {
                            permissionGranted = true
                        }
                    }

                    override fun onPermissionRationaleShouldBeShown(
                        permissions: List<PermissionRequest>,
                        token: PermissionToken
                    ) {
                        token.continuePermissionRequest()
                    }
                }).check()
        }
    }

    private fun setUpClickListener() {
        binding!!.actionBar.ivBack.setOnClickListener(this)
        binding!!.buttonReceive.setOnClickListener(this)
        binding!!.ivPartsImage.setOnClickListener(this)
        binding!!.ivPartsImageView.setOnClickListener(this)
    }

    @SuppressLint("SetTextI18n")
    private fun setUpData() {
        if (intent.getStringExtra("date") != null) {
            val separator =
                intent.getStringExtra("date")!!.split("-".toRegex()).dropLastWhile { it.isEmpty() }
                    .toTypedArray()
            val year = separator[0]
            val month = separator[1]
            val day = separator[2]
            binding!!.tvReceiveItemDate.text = "$day-$month-$year"
        }
    }

    private fun selectImage() {
        try {
            val items = arrayOf<CharSequence>(
                resources.getString(R.string.imagepicker_str_take_photo),
                resources.getString(R.string.imagepicker_str_choose_from_gallery),
                resources.getString(R.string.imagepicker_str_cancel)
            )
            val title = TextView(mContext)
            title.text = resources.getString(R.string.imagepicker_str_select_parts_image)
            title.setBackgroundColor(resources.getColor(R.color.colorActionBar))
            title.setPadding(15, 25, 15, 25)
            title.gravity = Gravity.CENTER
            title.setTextColor(Color.WHITE)
            title.textSize = 22f

            val builder = AlertDialog.Builder(
                mContext!!
            )
            builder.setCustomTitle(title)
            // builder.setTitle("Add Photo!");
            builder.setItems(
                items
            ) { dialog: DialogInterface, item: Int ->
                if (items[item] == resources.getString(R.string.imagepicker_str_take_photo)) {
                    ImagePicker.with(mContext)
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
                        .start(REQUEST_PICK_CHALLAN_IMAGES)
                } else if (items[item] == resources.getString(R.string.imagepicker_str_choose_from_gallery)) {
                    ImagePicker.with(mContext)
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
                        .start(REQUEST_PICK_CHALLAN_IMAGES)
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
        if (requestCode == REQUEST_PICK_CHALLAN_IMAGES && resultCode == RESULT_OK && data != null) {
            val images = data.getParcelableArrayListExtra<Image>(Config.EXTRA_IMAGES)
            if (images != null && images.size > 0) {
                val image = images[0]
                challanImageStoragePath = image.path
                if (challanImageStoragePath.contains("file:/")) {
                    challanImageStoragePath = challanImageStoragePath.replace("file:/", "")
                }
                challanImageStoragePath = compress(
                    challanImageStoragePath,
                    this
                )
                val imgFile = File(challanImageStoragePath)
                val myBitmap = BitmapFactory.decodeFile(imgFile.absolutePath)
                try {
                    binding!!.ivPartsImage.setImageBitmap(ensurePortrait(challanImageStoragePath))
                } catch (e: IOException) {
                    binding!!.ivPartsImage.setImageBitmap(myBitmap)
                    e.printStackTrace()
                } catch (e: NullPointerException) {
                    e.printStackTrace()
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun setReceiveMaterialListAdapter() {
        if (isNetworkAvailable(mActivity!!)) {
            showProgress()
            val service = RetrofitInstance.getRetrofitInstance().create(
                APIService::class.java
            )
            invoiceId = intent.getStringExtra("invoice_id")
            dispatchId = intent.getStringExtra("dispatch_id")
            userId = prefManager!!.useR_Id
            //   Log.e("params","invoice_id : "+invoiceId+" user_id : "+userId+" dispatch_id : "+dispatchId);
            val call = service.getReciveInvoiceParts(invoiceId, userId, dispatchId)
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
                                    val gson = Gson()
                                    val modelResponse = gson.fromJson(
                                        response.body()!!.string(),
                                        ReceiveInvoicePartsResponse::class.java
                                    )
                                    if (modelResponse != null) {
                                        if (modelResponse.status == "200") {
                                            if (modelResponse.partsDispatchInvoiceList!!.size > 0) {
                                                partsList = modelResponse.partsDispatchInvoiceList
                                                binding!!.topLay.visibility = View.VISIBLE
                                                binding!!.rvMaterialList.layoutManager =
                                                    LinearLayoutManager(
                                                        this@ReceiveMaterialListDetailsActivity,
                                                        LinearLayoutManager.VERTICAL,
                                                        false
                                                    )
                                                binding!!.rvMaterialList.adapter =
                                                    ReceiveMaterialListDetailsActivityAdapter(
                                                        mContext!!, partsList!!
                                                    )
                                            }
                                        }
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

    private fun saveReceiveItem() {
//        String remarkStr = binding.inputRemark.getText().toString().trim();
//        if (!remarkStr.isEmpty()) {
        val jsonArray = JSONArray()
        for (model in partsList!!) {
            try {
                var isReceivedValue = "0"
                if (model.quanity.toInt() > 0) isReceivedValue = "1"
                val jsonObject = JSONObject()
                jsonObject.put("DispatchId", model.dispatchId)
                jsonObject.put("InvoiceId", invoiceId)
                jsonObject.put("ItemId", model.itemId)
                jsonObject.put("Received_Item_Qty", model.quanity)
                jsonObject.put("IsReceived", isReceivedValue)
                jsonArray.put(jsonObject)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        if (isNetworkAvailable(mActivity!!)) {
            showProgress()
            //                RequestBody jsonArrBody = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), String.valueOf(jsonArray));
            val service = RetrofitInstance.getRetrofitInstance().create(
                APIService::class.java
            )
            val call = service.savePartsReceive(
                MultipartRequester.fromString(invoiceId),
                MultipartRequester.fromString(userId),
                MultipartRequester.fromString("true"),
                MultipartRequester.fromString(""),
                MultipartRequester.fromString(jsonArray.toString()),
                MultipartRequester.fromFile("RecPartsImage", challanImageStoragePath)
            )
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
                                    //        Log.e("param_save_receive_item",responseStr);
                                    val jsonObject = JSONObject(responseStr)
                                    val status = jsonObject.optString("status")
                                    val message = jsonObject.optString("message")
                                    if (status == "200") {
//                                            Toast.makeText(mActivity, "Save Successfully", Toast.LENGTH_SHORT).show();
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
        /*}else makeToast(getResources().getString(R.string.please_enter_remark));*/
    }

    override fun onClick(view: View) {
        val id = view.id
        if (id == R.id.iv_back) {
            onBackPressed()
        } else if (id == R.id.buttonReceive) {
            if (challanImageStoragePath.isEmpty()) {
                makeToast(resources.getString(R.string.please_select_parts_image))
            } else {
                val builder = AlertDialog.Builder(
                    mContext!!
                )
                builder.setMessage(resources.getString(R.string.are_you_sure_you_want_to_receive_this_invoice))
                    .setCancelable(false)
                    .setPositiveButton(resources.getString(R.string.yes)) { dlg: DialogInterface, ids: Int ->
                        dlg.dismiss()
                        saveReceiveItem()
                    }
                    .setNegativeButton(
                        resources.getString(R.string.no)
                    ) { dlg: DialogInterface, ids: Int -> dlg.dismiss() }
                val alert = builder.create()
                alert.setTitle(resources.getString(R.string.alert))
                alert.show()
            }
        } else if (id == R.id.ivPartsImage) {
            if (permissionGranted) {
                selectImage()
            } else {
                makeToast(resources.getString(R.string.please_allow_all_permission))
            }
        } else if (id == R.id.ivPartsImageView) {
            startActivity(
                Intent(mContext, ZoomInZoomOutActivity::class.java).putExtra(
                    "image",
                    Constants.API_BASE_URL + model!!.receivedPartsImage
                )
            )
        }
    }
}