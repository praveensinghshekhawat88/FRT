package com.callmangement.ehr.ehrActivities

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.text.TextUtils
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.callmangement.R
import com.callmangement.databinding.ActivityUploadSurveyFormReportBinding
import com.callmangement.ehr.api.APIClient.getRetrofitClientWithoutHeaders
import com.callmangement.ehr.api.APIInterface
import com.callmangement.ehr.api.Constants
import com.callmangement.ehr.api.MultipartRequester.fromString
import com.callmangement.ehr.imagepicker.model.Config
import com.callmangement.ehr.imagepicker.model.Image
import com.callmangement.ehr.imagepicker.ui.imagepicker.ImagePicker.Companion.with
import com.callmangement.ehr.models.ModelUploadSurveyFormReport
import com.callmangement.ehr.support.CompressImage.Companion.compress
import com.callmangement.ehr.support.OnSingleClickListener
import com.callmangement.ehr.support.Utils.hideCustomProgressDialogCommonForAll
import com.callmangement.ehr.support.Utils.hideKeyboard
import com.callmangement.ehr.support.Utils.isNetworkAvailable
import com.callmangement.ehr.support.Utils.showCustomProgressDialogCommonForAll
import com.callmangement.support.ImageUtilsForRotate
import com.callmangement.utils.PrefManager
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.MultipartBody.Part.Companion.createFormData
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.IOException

class UploadSurveyFormReportActivity : BaseActivity() {
    private var mActivity: Activity? = null
    private var binding: ActivityUploadSurveyFormReportBinding? = null
    private var preference: PrefManager? = null
    private var ServeyFormId = ""
    private var TicketNo = ""
    val REQUEST_PICK_IMAGE_ONE: Int = 1111
    val REQUEST_PICK_IMAGE_TWO: Int = 1112
    val REQUEST_PICK_IMAGE_THREE: Int = 1113
    private val stringArrayListHavingAllFilePath = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        binding = ActivityUploadSurveyFormReportBinding.inflate(
            layoutInflater
        )
        setContentView(binding!!.root)
        init()
    }

    private fun setUpData() {
    }

    private fun init() {
        mActivity = this
        preference = PrefManager(mActivity!!)

        val bundle = intent.extras
        ServeyFormId = bundle!!.getString("ServeyFormId", "")
        TicketNo = bundle.getString("TicketNo", "")

        binding!!.actionBar.ivBack.visibility = View.VISIBLE
        binding!!.actionBar.ivThreeDot.visibility = View.GONE
        binding!!.actionBar.layoutLanguage.visibility = View.GONE
        binding!!.actionBar.buttonPDF.visibility = View.GONE
        binding!!.actionBar.textToolbarTitle.text = resources.getString(R.string.upload_images)

        setUpData()
        setClickListener()
    }

    fun makeToast(string: String?) {
        if (TextUtils.isEmpty(string)) return
        Toast.makeText(mActivity, string, Toast.LENGTH_SHORT).show()
    }

    private fun setClickListener() {
        binding!!.actionBar.ivBack.setOnClickListener { view: View? -> onBackPressed() }

        binding!!.linChooseImages1.setOnClickListener(object : OnSingleClickListener() {
            override fun onSingleClick(v: View?) {
                selectImage(REQUEST_PICK_IMAGE_ONE)
            }
        })

        binding!!.linChooseImages2.setOnClickListener(object : OnSingleClickListener() {
            override fun onSingleClick(v: View?) {
                selectImage(REQUEST_PICK_IMAGE_TWO)
            }
        })

        binding!!.linChooseImages3.setOnClickListener(object : OnSingleClickListener() {
            override fun onSingleClick(v: View?) {
                selectImage(REQUEST_PICK_IMAGE_THREE)
            }
        })

        binding!!.buttonUploadImages.setOnClickListener { view: View? ->
            if (stringArrayListHavingAllFilePath != null && stringArrayListHavingAllFilePath.size > 0) {
                val USER_Id = preference!!.useR_Id
                uploadImages(USER_Id, ServeyFormId, TicketNo, stringArrayListHavingAllFilePath)
            } else {
                makeToast("Please choose at least one image.")
            }
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
                    with(mActivity!!)
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
                    with(mActivity!!)
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
                if (imageStoragePath!!.contains("file:/")) {
                    imageStoragePath = imageStoragePath.replace("file:/", "")
                }
                imageStoragePath = compress(
                    imageStoragePath, this
                )
                val imgFile = File(imageStoragePath)
                val myBitmap = BitmapFactory.decodeFile(imgFile.absolutePath)
                try {
                    binding!!.ivChallanImage.setImageBitmap(
                        ImageUtilsForRotate.ensurePortrait(
                            imageStoragePath
                        )
                    )
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
                if (imageStoragePath!!.contains("file:/")) {
                    imageStoragePath = imageStoragePath.replace("file:/", "")
                }
                imageStoragePath = compress(
                    imageStoragePath, this
                )
                val imgFile = File(imageStoragePath)
                val myBitmap = BitmapFactory.decodeFile(imgFile.absolutePath)

                try {
                    binding!!.ivPartsImage1.setImageBitmap(
                        ImageUtilsForRotate.ensurePortrait(
                            imageStoragePath
                        )
                    )
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
                if (imageStoragePath!!.contains("file:/")) {
                    imageStoragePath = imageStoragePath.replace("file:/", "")
                }
                imageStoragePath = compress(
                    imageStoragePath, this
                )
                val imgFile = File(imageStoragePath)
                val myBitmap = BitmapFactory.decodeFile(imgFile.absolutePath)
                try {
                    binding!!.ivPartsImage2.setImageBitmap(
                        ImageUtilsForRotate.ensurePortrait(
                            imageStoragePath
                        )
                    )
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

    private fun uploadImages(
        UserID: String,
        ServeyFormId: String,
        TicketNo: String,
        arrayHavingAllFilePath: ArrayList<String>
    ) {
        if (isNetworkAvailable(mActivity!!)) {
            hideKeyboard(mActivity!!)
            showCustomProgressDialogCommonForAll(
                mActivity!!,
                resources.getString(R.string.please_wait)
            )

            val apiInterface = getRetrofitClientWithoutHeaders(
                mActivity!!,
                BaseUrl()!!
            ).create(
                APIInterface::class.java
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

            val call = apiInterface.callUploadSurveyFormReportImagesApi(
                UploadDocumentOfSurveyFormReport(),
                fromString(UserID),
                fromString(ServeyFormId),
                fromString(TicketNo),
                campDocumentsParts
            )
            call!!.enqueue(object : Callback<ModelUploadSurveyFormReport?> {
                override fun onResponse(
                    call: Call<ModelUploadSurveyFormReport?>,
                    response: Response<ModelUploadSurveyFormReport?>
                ) {
                    hideCustomProgressDialogCommonForAll()

                    if (response.isSuccessful) {
                        if (response.code() == 200) {
                            if (response.body() != null) {
                                if (response.body()!!.status == "200") {
                                    makeToast(response.body()!!.message)
                                    finish()
                                } else {
                                    makeToast(response.body()!!.message)
                                }
                            } else {
                                makeToast(resources.getString(R.string.error))
                            }
                        } else {
                            makeToast(resources.getString(R.string.error))
                        }
                    } else {
                        makeToast(resources.getString(R.string.error))
                    }
                }

                override fun onFailure(call: Call<ModelUploadSurveyFormReport?>, error: Throwable) {
                    hideCustomProgressDialogCommonForAll()

                    makeToast(resources.getString(R.string.error))

                    call.cancel()
                }
            })
        } else {
            makeToast(resources.getString(R.string.no_internet_connection))
        }
    }
}