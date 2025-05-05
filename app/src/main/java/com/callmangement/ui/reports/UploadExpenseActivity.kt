package com.callmangement.ui.reports

import android.Manifest
import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.callmangement.network.APIService
import com.callmangement.network.MultipartRequester
import com.callmangement.network.RetrofitInstance
import com.callmangement.R
import com.callmangement.custom.CustomActivity
import com.callmangement.databinding.ActivityUploadExpenseBinding
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
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.IOException
import java.util.Objects

class UploadExpenseActivity : CustomActivity(), View.OnClickListener {
    private var binding: ActivityUploadExpenseBinding? = null
    private var prefManager: PrefManager? = null
    private var challanImageStoragePath = ""
    private var permissionGranted = false
    private val REQUEST_PICK_CHALLAN_IMAGES: Int = 1113

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUploadExpenseBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        initView()
    }

    private fun initView() {
        prefManager = PrefManager(mContext)
        binding!!.actionBar.ivBack.visibility = View.VISIBLE
        binding!!.actionBar.ivThreeDot.visibility = View.GONE
        binding!!.actionBar.layoutLanguage.visibility = View.GONE
        binding!!.actionBar.textToolbarTitle.text = resources.getString(R.string.upload_expense)
        setUpOnClickListener()
        checkPermission()
        setUpData()
    }

    private fun setUpOnClickListener() {
        binding!!.actionBar.ivBack.setOnClickListener(this)
        binding!!.ivChallanImage.setOnClickListener(this)
        binding!!.buttonSave.setOnClickListener(this)
    }

    private fun setUpData() {
        binding!!.inputName.setText(prefManager!!.useR_NAME)
        binding!!.inputDistrict.setText(prefManager!!.useR_District)
    }

    private fun saveExpense() {
        val docketno =
            Objects.requireNonNull(binding!!.inputDocketno.text).toString().trim { it <= ' ' }
        val couriername =
            Objects.requireNonNull(binding!!.inputcouriername.text).toString().trim { it <= ' ' }
        val remark =
            Objects.requireNonNull(binding!!.inputRemark.text).toString().trim { it <= ' ' }
        val expenseAmount =
            Objects.requireNonNull(binding!!.inputTotalExpenseAmount.text).toString()
                .trim { it <= ' ' }
        if (Constants.isNetworkAvailable(mContext)) {
            if (remark.isEmpty()) {
                makeToast(resources.getString(R.string.please_input_remark))
            } else if (expenseAmount.isEmpty()) {
                makeToast(resources.getString(R.string.please_input_expense_amount))
            } else if (challanImageStoragePath.isEmpty()) {
                makeToast(resources.getString(R.string.please_select_challan_image))
            } else if (docketno.isEmpty()) {
                makeToast(resources.getString(R.string.please_select_docket_no_))
            } else if (couriername.isEmpty()) {
                makeToast(resources.getString(R.string.please_select_courier_name))
            } else {
                showProgress()
                val service = RetrofitInstance.getRetrofitInstance().create(
                    APIService::class.java
                )
                val call = service.saveExpenses(
                    MultipartRequester.fromString(prefManager!!.useR_Id),
                    MultipartRequester.fromString(expenseAmount),
                    MultipartRequester.fromString(remark),
                    MultipartRequester.fromString(docketno),
                    MultipartRequester.fromString(couriername),
                    MultipartRequester.fromFile("ExChallanCopy", challanImageStoragePath)
                )
                call.enqueue(object : Callback<ResponseBody?> {
                    @SuppressLint("UseCompatLoadingForDrawables")
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
                                            Toast.makeText(mContext, message, Toast.LENGTH_SHORT)
                                                .show()
                                            binding!!.inputRemark.text!!.clear()
                                            binding!!.inputTotalExpenseAmount.text!!.clear()
                                            binding!!.inputDocketno.text!!.clear()
                                            binding!!.inputcouriername.text!!.clear()
                                            binding!!.ivChallanImage.setImageBitmap(null)
                                            binding!!.ivChallanImage.setImageDrawable(
                                                resources.getDrawable(
                                                    R.drawable.ic_baseline_image
                                                )
                                            )
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
            }
        } else makeToast(resources.getString(R.string.no_internet_connection))
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

    private fun selectImage() {
        try {
            val items = arrayOf<CharSequence>(
                resources.getString(R.string.imagepicker_str_take_photo),
                resources.getString(R.string.imagepicker_str_choose_from_gallery),
                resources.getString(R.string.imagepicker_str_cancel)
            )
            val title = TextView(mContext)
            title.text = resources.getString(R.string.imagepicker_str_select_challan_image)
            title.setBackgroundColor(resources.getColor(R.color.colorActionBar))
            title.setPadding(15, 25, 15, 25)
            title.gravity = Gravity.CENTER
            title.setTextColor(Color.WHITE)
            title.textSize = 22f

            val builder = AlertDialog.Builder(mContext)
            builder.setCustomTitle(title)
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
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_PICK_CHALLAN_IMAGES && resultCode == RESULT_OK && data != null) {
            val images = data.getParcelableArrayListExtra<Image>(Config.EXTRA_IMAGES)
            if (images != null && images.size > 0) {
                val image = images[0]
                challanImageStoragePath = image.path
                if (challanImageStoragePath.contains("file:/")) {
                    challanImageStoragePath = challanImageStoragePath.replace("file:/", "")
                }
                challanImageStoragePath = CompressImage.compress(
                    challanImageStoragePath,
                    this
                )
                val imgFile = File(challanImageStoragePath)
                val myBitmap = BitmapFactory.decodeFile(imgFile.absolutePath)
                try {
                    binding!!.ivChallanImage.setImageBitmap(
                        ImageUtilsForRotate.ensurePortrait(
                            challanImageStoragePath
                        )
                    )
                } catch (e: IOException) {
                    binding!!.ivChallanImage.setImageBitmap(myBitmap)
                    e.printStackTrace()
                } catch (e: NullPointerException) {
                    e.printStackTrace()
                }
            }
        }
    }

    override fun onClick(view: View) {
        val id = view.id
        if (id == R.id.iv_back) {
            onBackPressed()
        } else if (id == R.id.ivChallanImage) {
            if (permissionGranted) {
                selectImage()
            } else {
                makeToast(resources.getString(R.string.please_allow_all_permission))
            }
        } else if (id == R.id.buttonSave) {
            saveExpense()
        }
    }
}