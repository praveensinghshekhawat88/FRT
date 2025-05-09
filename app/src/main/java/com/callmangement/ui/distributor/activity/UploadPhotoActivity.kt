package com.callmangement.ui.distributor.activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.wifi.WifiManager
import android.os.Build
import android.os.Bundle
import android.text.format.Formatter
import android.view.Gravity
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.callmangement.network.APIService
import com.callmangement.network.RetrofitInstance
import com.callmangement.R
import com.callmangement.custom.CustomActivity
import com.callmangement.databinding.ActivityUploadPhotoBinding
import com.callmangement.imagepicker.model.Config
import com.callmangement.imagepicker.model.Image
import com.callmangement.imagepicker.ui.imagepicker.ImagePicker
import com.callmangement.support.ImageUtilsForRotate
import com.callmangement.support.dexter.Dexter
import com.callmangement.support.dexter.MultiplePermissionsReport
import com.callmangement.support.dexter.PermissionToken
import com.callmangement.support.dexter.listener.PermissionRequest
import com.callmangement.support.dexter.listener.multi.MultiplePermissionsListener
import com.callmangement.utils.CompressImage
import com.callmangement.utils.Constants
import com.callmangement.utils.PrefManager
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.IOException

class UploadPhotoActivity : CustomActivity(), View.OnClickListener {

    
    private lateinit var mActivity: Activity
    private var binding: ActivityUploadPhotoBinding? = null
    private var permissionGranted = false
    private var uploadImageFile: File? = null
    private var fpsCode: String? = ""
    private var tranId: String? = ""
    private var districtId: String? = ""
    private var flagType: String? = ""
    val REQUEST_PICK_PHOTO: Int = 1113
    val REQUEST_PICK_GALLERY_IMAGE: Int = 1114
    private var prefManager: PrefManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUploadPhotoBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        binding!!.actionBar.ivBack.visibility = View.VISIBLE
        binding!!.actionBar.ivThreeDot.visibility = View.GONE
        binding!!.actionBar.layoutLanguage.visibility = View.GONE
        binding!!.actionBar.buttonPDF.visibility = View.GONE
        binding!!.actionBar.textToolbarTitle.text = resources.getString(R.string.upload_photo)
        init()
    }

    private fun init() {

        mContext = this
        mActivity = this
        prefManager = PrefManager(mContext!!)
        setUpClickListener()
        checkPermission()
        intentData
        if (permissionGranted) {
            capturePhoto()
        } else {
            makeToast(resources.getString(R.string.please_allow_all_permission))
        }
    }

    private fun setUpClickListener() {
        binding!!.actionBar.ivBack.setOnClickListener(this)
        binding!!.tvUpload.setOnClickListener(this)
        binding!!.ivImage.setOnClickListener(this)
    }

    private val intentData: Unit
        get() {
            fpsCode = intent.getStringExtra("fps_code")
            tranId = intent.getStringExtra("tran_id")
            districtId = intent.getStringExtra("district_id")
            flagType = intent.getStringExtra("flagType")
        }

    private fun checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Dexter.withContext(this)
                .withPermissions(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.READ_MEDIA_IMAGES,
                    Manifest.permission.READ_MEDIA_VIDEO,
                    Manifest.permission.CAMERA,
                    Manifest.permission.ACCESS_WIFI_STATE
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
                    Manifest.permission.CAMERA,
                    Manifest.permission.ACCESS_WIFI_STATE
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

    private fun capturePhoto() {
        try {
            val items = arrayOf<CharSequence>(
                resources.getString(R.string.imagepicker_str_take_photo),
                resources.getString(R.string.imagepicker_str_choose_from_gallery),
                resources.getString(R.string.imagepicker_str_cancel)
            )
            val title = TextView(mContext!!)
            title.text = resources.getString(R.string.capture_photo)
            title.setBackgroundColor(resources.getColor(R.color.colorActionBar))
            title.setPadding(15, 25, 15, 25)
            title.gravity = Gravity.CENTER
            title.setTextColor(Color.WHITE)
            title.textSize = 22f
            val builder = AlertDialog.Builder(mContext!!)
            builder.setCustomTitle(title)
            builder.setCancelable(false)
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
                        .start(REQUEST_PICK_PHOTO)
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
                        .start(REQUEST_PICK_GALLERY_IMAGE)
                } else if (items[item] == resources.getString(R.string.imagepicker_str_cancel)) {
                    onBackPressed()
                    dialog.dismiss()
                }
            }
            builder.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_PICK_PHOTO && resultCode == RESULT_OK && data != null) {
            val images = data.getParcelableArrayListExtra<Image>(Config.EXTRA_IMAGES)
            if (images != null && images.size > 0) {
                val image = images[0]
                var photoStoragePath = image.path
                if (photoStoragePath.contains("file:/")) {
                    photoStoragePath = photoStoragePath.replace("file:/", "")
                }
                photoStoragePath = CompressImage.compress(
                    photoStoragePath,
                    this
                )
                uploadImageFile = File(photoStoragePath)
                val myBitmap = BitmapFactory.decodeFile(uploadImageFile!!.absolutePath)
                try {
                    binding!!.ivImage.setImageBitmap(
                        ImageUtilsForRotate.ensurePortrait(
                            photoStoragePath
                        )
                    )
                } catch (e: IOException) {
                    binding!!.ivImage.setImageBitmap(myBitmap)
                    e.printStackTrace()
                } catch (e: NullPointerException) {
                    e.printStackTrace()
                }
            }
        } else if (requestCode == REQUEST_PICK_GALLERY_IMAGE && resultCode == RESULT_OK && data != null) {
            val images = data.getParcelableArrayListExtra<Image>(Config.EXTRA_IMAGES)
            if (images != null && images.size > 0) {
                val image = images[0]
                var photoStoragePath = image.path
                if (photoStoragePath.contains("file:/")) {
                    photoStoragePath = photoStoragePath.replace("file:/", "")
                }
                photoStoragePath = CompressImage.compress(
                    photoStoragePath,
                    this
                )
                uploadImageFile = File(photoStoragePath)
                val myBitmap = BitmapFactory.decodeFile(uploadImageFile!!.absolutePath)
                try {
                    binding!!.ivImage.setImageBitmap(
                        ImageUtilsForRotate.ensurePortrait(
                            photoStoragePath
                        )
                    )
                } catch (e: IOException) {
                    binding!!.ivImage.setImageBitmap(myBitmap)
                    e.printStackTrace()
                } catch (e: NullPointerException) {
                    e.printStackTrace()
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun upload() {
        if (uploadImageFile == null) makeToast("Please select photo")
        else {
            if (Constants.isNetworkAvailable(mContext!!)) {
                showProgress()
                val service = RetrofitInstance.getRetrofitInstance().create(
                    APIService::class.java
                )

                val builder = MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("userId", prefManager!!.useR_Id)
                    .addFormDataPart("tranId", tranId!!)
                    .addFormDataPart("districtId", districtId!!)
                    .addFormDataPart("FPSCode", fpsCode!!)
                    .addFormDataPart("ipAddress", networkIp)
                    .addFormDataPart("flagType", flagType!!)

                val imageRequestBody = uploadImageFile!!
                    .asRequestBody("multipart/form-data".toMediaTypeOrNull())

                builder.addFormDataPart("dealerImage", uploadImageFile!!.name, imageRequestBody)
                val requestBody: RequestBody = builder.build()

                val call = service.uploadDistributerPhoto(requestBody)
                call.enqueue(object : Callback<ResponseBody?> {
                    @SuppressLint("SetTextI18n")
                    override fun onResponse(
                        call: Call<ResponseBody?>,
                        response: Response<ResponseBody?>
                    ) {
                        hideProgress()
                        if (response.isSuccessful) {
                            try {
                                if (response.code() == 200) {
                                    if (response.body() != null) {
                                        val responseStr = response.body()!!.string()
                                        //    Log.e("upload_response", responseStr);
                                        val jsonObject = JSONObject((responseStr))
                                        val status = jsonObject.optString("status")
                                        val message = jsonObject.optString("message")
                                        if (status == "200") {
//                                            makeToast(message);
                                            onBackPressed()
                                        } else makeToast(message)
                                    } else makeToast(resources.getString(R.string.error))
                                } else makeToast(resources.getString(R.string.error))
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        } else {
                            makeToast(resources.getString(R.string.error))
                        }
                    }

                    override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                        hideProgress()
                        makeToast(resources.getString(R.string.error_message))
                    }
                })
            }
        }
    }

    private val networkIp: String
        get() {
            val wm =
                applicationContext.getSystemService(WIFI_SERVICE) as WifiManager
            return Formatter.formatIpAddress(
                wm.connectionInfo.ipAddress
            )
        }

    override fun onClick(view: View) {
        val id = view.id
        if (id == R.id.iv_back) onBackPressed()
        else if (id == R.id.tv_upload) upload()
        else if (id == R.id.iv_image) if (permissionGranted) {
            capturePhoto()
        } else {
            makeToast(resources.getString(R.string.please_allow_all_permission))
        }
    }
}