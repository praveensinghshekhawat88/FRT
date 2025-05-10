package com.callmangement.ui.inventory

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.callmangement.R
import com.callmangement.adapter.DialogChallanForDispatchAdapter
import com.callmangement.custom.CustomActivity
import com.callmangement.databinding.ActivityPickImageAndCourierDetailForDispatchStockBinding
import com.callmangement.imagepicker.model.Config
import com.callmangement.imagepicker.model.Image
import com.callmangement.imagepicker.ui.imagepicker.ImagePicker
import com.callmangement.model.inventrory.ModelPartsList
import com.callmangement.model.inventrory.ModelSavePartsDispatchDetails
import com.callmangement.network.APIService
import com.callmangement.network.MultipartRequester
import com.callmangement.network.RetrofitInstance
import com.callmangement.report_pdf.ChallanPDFActivity
import com.callmangement.support.ImageUtilsForRotate.ensurePortrait
import com.callmangement.support.dexter.Dexter
import com.callmangement.support.dexter.MultiplePermissionsReport
import com.callmangement.support.dexter.PermissionToken
import com.callmangement.support.dexter.listener.PermissionRequest
import com.callmangement.support.dexter.listener.multi.MultiplePermissionsListener
import com.callmangement.utils.CompressImage.Companion.compress
import com.callmangement.utils.Constants
import com.callmangement.utils.Constants.isNetworkAvailable
import com.callmangement.utils.PrefManager
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody.Part.Companion.createFormData
import okhttp3.RequestBody
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.IOException
import java.util.Objects

class PickImageAndCourierDetailForDispatchStockActivity : CustomActivity(), View.OnClickListener {
    private var binding: ActivityPickImageAndCourierDetailForDispatchStockBinding? = null
    private var inventoryViewModel: InventoryViewModel? = null
    private var dispatchList: List<ModelPartsList>? = ArrayList()
    private var prefManager: PrefManager? = null
    private var challanImageStoragePath = ""
    private var partsImageStoragePath1 = ""
    private var partsImageStoragePath2 = ""
    private var permissionGranted = false
    val REQUEST_PICK_CHALLAN_IMAGES: Int = 1113
    val REQUEST_PICK_PARTS_IMAGES1: Int = 1114
    val REQUEST_PICK_PARTS_IMAGES2: Int = 1115
    private var districtNameEng: String? = ""
    private var seUserName: String? = ""
    private var userTypeName: String? = ""
    private var dispatchId: String? = "0"
    private val invoiceId = "0"
    private var seUserId: String? = "0"
    private var districtId: String? = "0"
    private val stockStatusId = "3"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPickImageAndCourierDetailForDispatchStockBinding.inflate(
            layoutInflater
        )
        setContentView(binding!!.root)
        initView()
    }

    private fun initView() {
        binding!!.actionBar.ivThreeDot.visibility = View.GONE
        binding!!.actionBar.ivBack.visibility = View.VISIBLE
        binding!!.actionBar.layoutLanguage.visibility = View.GONE
        binding!!.actionBar.buttonPDF.visibility = View.GONE
        binding!!.actionBar.textToolbarTitle.text =
            resources.getString(R.string.create_dispatch_stock)
        prefManager = PrefManager(mContext!!)
        inventoryViewModel = ViewModelProviders.of(this).get(
            InventoryViewModel::class.java
        )
        checkPermission()
        setUpClickListener()
        intentData
    }

    private val intentData: Unit
        get() {
            dispatchList =
                intent.getSerializableExtra("dispatchList") as List<ModelPartsList>?
            districtNameEng = intent.getStringExtra("districtNameEng")
            seUserName = intent.getStringExtra("seUserName")
            dispatchId = intent.getStringExtra("dispatchId")
            seUserId = intent.getStringExtra("seUserId")
            districtId = intent.getStringExtra("districtId")
            userTypeName = intent.getStringExtra("userTypeName")
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
        binding!!.buttonCreateChallan.setOnClickListener(this)
        binding!!.ivChallanImage.setOnClickListener(this)
        binding!!.ivPartsImage1.setOnClickListener(this)
        binding!!.ivPartsImage2.setOnClickListener(this)
        binding!!.actionBar.ivBack.setOnClickListener(this)
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

            val builder = AlertDialog.Builder(
                mContext!!
            )
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

    private fun selectPartsImage1() {
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
                        .start(REQUEST_PICK_PARTS_IMAGES1)
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
                        .start(REQUEST_PICK_PARTS_IMAGES1)
                } else if (items[item] == resources.getString(R.string.imagepicker_str_cancel)) {
                    dialog.dismiss()
                }
            }
            builder.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun selectPartsImage2() {
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
                        .start(REQUEST_PICK_PARTS_IMAGES2)
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
                        .start(REQUEST_PICK_PARTS_IMAGES2)
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
                    binding!!.ivChallanImage.setImageBitmap(ensurePortrait(challanImageStoragePath))
                } catch (e: IOException) {
                    binding!!.ivChallanImage.setImageBitmap(myBitmap)
                    e.printStackTrace()
                } catch (e: NullPointerException) {
                    e.printStackTrace()
                }
            }
        } else if (requestCode == REQUEST_PICK_PARTS_IMAGES1 && resultCode == RESULT_OK && data != null) {
            val images = data.getParcelableArrayListExtra<Image>(Config.EXTRA_IMAGES)
            if (images != null && images.size > 0) {
                val image = images[0]
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
                } catch (e: IOException) {
                    binding!!.ivPartsImage1.setImageBitmap(myBitmap)
                    e.printStackTrace()
                } catch (e: NullPointerException) {
                    e.printStackTrace()
                }
            }
        } else if (requestCode == REQUEST_PICK_PARTS_IMAGES2 && resultCode == RESULT_OK && data != null) {
            val images = data.getParcelableArrayListExtra<Image>(Config.EXTRA_IMAGES)
            if (images != null && images.size > 0) {
                val image = images[0]
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
                } catch (e: IOException) {
                    binding!!.ivPartsImage2.setImageBitmap(myBitmap)
                    e.printStackTrace()
                } catch (e: NullPointerException) {
                    e.printStackTrace()
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun createChallan(dispatchList: List<ModelPartsList>) {
        if (isNetworkAvailable(mContext!!)) {
            if (!userTypeName.equals("ServiceCentre", ignoreCase = true)) {
                if (partsImageStoragePath1.isEmpty()) {
                    makeToast(resources.getString(R.string.please_select_parts_image1))
                } else if (partsImageStoragePath2.isEmpty()) {
                    makeToast(resources.getString(R.string.please_select_parts_image2))
                } else {
                    val builder = AlertDialog.Builder(
                        mContext!!
                    )
                    builder.setMessage(resources.getString(R.string.are_you_sure_you_want_to_create_challan))
                        .setCancelable(false)
                        .setPositiveButton(resources.getString(R.string.yes)) { dlg: DialogInterface, ids: Int ->
                            dlg.dismiss()
                            dialogDispatchChallan(dispatchList)
                        }
                        .setNegativeButton(
                            resources.getString(R.string.no)
                        ) { dlg: DialogInterface, ids: Int -> dlg.dismiss() }
                    val alert = builder.create()
                    alert.setTitle(resources.getString(R.string.alert))
                    alert.show()
                }
            } else {
                val builder = AlertDialog.Builder(
                    mContext!!
                )
                builder.setMessage(resources.getString(R.string.are_you_sure_you_want_to_create_challan))
                    .setCancelable(false)
                    .setPositiveButton(resources.getString(R.string.yes)) { dlg: DialogInterface, ids: Int ->
                        dlg.dismiss()
                        dialogDispatchChallan(dispatchList)
                    }
                    .setNegativeButton(
                        resources.getString(R.string.no)
                    ) { dlg: DialogInterface, ids: Int -> dlg.dismiss() }
                val alert = builder.create()
                alert.setTitle(resources.getString(R.string.alert))
                alert.show()
            }
        } else makeToast(resources.getString(R.string.no_internet_connection))
    }

    @SuppressLint("SetTextI18n")
    private fun dialogDispatchChallan(partsList: List<ModelPartsList>) {
        try {
            val dialog = Dialog(mContext!!)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setContentView(R.layout.dialog_challan_for_dispatch)
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.setCanceledOnTouchOutside(false)

            //            TextView invoice_id = dialog.findViewById(R.id.textInvoiceNumber);
            val dispatchFrom = dialog.findViewById<TextView>(R.id.textDispatchFrom)
            val textDispatcherEmail = dialog.findViewById<TextView>(R.id.textDispatcherEmail)
            val dispatchTo = dialog.findViewById<TextView>(R.id.textDispatchTo)
            val username = dialog.findViewById<TextView>(R.id.textUsername)
            val rvDispatchChallan = dialog.findViewById<RecyclerView>(R.id.rv_dispatch_challan)
            val buttonCancel = dialog.findViewById<Button>(R.id.buttonCancel)
            val buttonDispatch = dialog.findViewById<Button>(R.id.buttonDispatch)

            //            invoice_id.setText(invoiceId);
            dispatchFrom.text = prefManager!!.useR_NAME
            textDispatcherEmail.text = prefManager!!.useR_EMAIL
            dispatchTo.text = districtNameEng
            username.text = seUserName

            rvDispatchChallan.layoutManager =
                LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false)
            rvDispatchChallan.adapter = DialogChallanForDispatchAdapter(mContext!!, partsList)

            buttonCancel.setOnClickListener { view: View? -> dialog.dismiss() }

            buttonDispatch.setOnClickListener { view: View? ->
                val builder = AlertDialog.Builder(
                    mContext!!
                )
                builder.setMessage(resources.getString(R.string.are_you_sure_you_want_to_dispatch_this_invoice))
                    .setCancelable(false)
                    .setPositiveButton(resources.getString(R.string.yes)) { dlg: DialogInterface, id: Int ->
                        dialog.dismiss()
                        dlg.dismiss()
                        createDispatchJSONArray(partsList)
                    }
                    .setNegativeButton(
                        resources.getString(R.string.no)
                    ) { dlg: DialogInterface, id: Int -> dlg.dismiss() }
                val alert = builder.create()
                alert.setTitle(resources.getString(R.string.alert))
                alert.show()
            }

            dialog.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun createDispatchJSONArray(dispatchList: List<ModelPartsList>) {
        try {
            val jsonArray = JSONArray()
            for (model in dispatchList) {
                val jsonObject = JSONObject()
                jsonObject.put("DispatchId", dispatchId)
                jsonObject.put("InvoiceId", invoiceId)
                jsonObject.put("ItemId", model.itemId)
                jsonObject.put("Item_Qty", model.getQuantity())
                jsonArray.put(jsonObject)
            }
            savePartsDispatchDetails(dispatchList, jsonArray)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    private fun savePartsDispatchDetails(dispatchList: List<ModelPartsList>, jsonArray: JSONArray) {
        val attachment: RequestBody
        val attachmentPartsImage1: RequestBody
        val attachmentPartsImage2: RequestBody

        var fileName = ""
        var fileNamePartsImage1 = ""
        var fileNamePartsImage2 = ""

        if (challanImageStoragePath != "") {
            fileName = File(challanImageStoragePath).name
            attachment = RequestBody.create(
                "multipart/form-data".toMediaTypeOrNull(),
                File(challanImageStoragePath)
            )
        } else {
            fileName = ""
            attachment = RequestBody.create("text/plain".toMediaTypeOrNull(), "")
        }

        if (partsImageStoragePath1 != "") {
            fileNamePartsImage1 = File(partsImageStoragePath1).name
            attachmentPartsImage1 =
                RequestBody.create("multipart/form-data".toMediaTypeOrNull(), File(partsImageStoragePath1))
        } else {
            fileNamePartsImage1 = ""
            attachmentPartsImage1 = RequestBody.create("text/plain".toMediaTypeOrNull(), "")
        }

        if (partsImageStoragePath2 != "") {
            fileNamePartsImage2 = File(partsImageStoragePath2).name
            attachmentPartsImage2 =
                RequestBody.create("multipart/form-data".toMediaTypeOrNull(), File(partsImageStoragePath2))
        } else {
            fileNamePartsImage2 = ""
            attachmentPartsImage2 = RequestBody.create("text/plain".toMediaTypeOrNull(), "")
        }

        val courierTrackingNumber = Objects.requireNonNull(
            binding!!.inputCourierTrackingNo.text
        ).toString().trim { it <= ' ' }
        val courierName =
            Objects.requireNonNull(binding!!.inputCourierName.text).toString().trim { it <= ' ' }

        val service = RetrofitInstance.getRetrofitInstance().create(
            APIService::class.java
        )
        val call = service.savePartsDispatchDetails(
            MultipartRequester.fromString(invoiceId),
            MultipartRequester.fromString(prefManager!!.useR_Id),
            MultipartRequester.fromString(seUserId),
            MultipartRequester.fromString(districtId),
            MultipartRequester.fromString(""),
            MultipartRequester.fromString(""),
            MultipartRequester.fromString(stockStatusId),
            MultipartRequester.fromString(courierName),
            MultipartRequester.fromString(courierTrackingNumber),
            MultipartRequester.fromString(jsonArray.toString()),
            createFormData("DispChalImage", fileName, attachment),
            createFormData(
                "PartsImage_1",
                fileNamePartsImage1,
                attachmentPartsImage1
            ),
            createFormData(
                "PartsImage_2",
                fileNamePartsImage2,
                attachmentPartsImage2
            )
        )
        showProgress()
        call.enqueue(object : Callback<ModelSavePartsDispatchDetails?> {
            override fun onResponse(
                call: Call<ModelSavePartsDispatchDetails?>,
                response: Response<ModelSavePartsDispatchDetails?>
            ) {
                hideProgress()
                if (response.isSuccessful) {
                    if (response.code() == 200) {
                        val model = response.body()
                        if (model!!.status == "200") {
                            submitDispatchParts(
                                dispatchList,
                                model.modelPartsDispatchInvoiceList.invoiceId,
                                courierName,
                                courierTrackingNumber
                            )
                        } else {
                            makeToast(model.message)
                        }
                    }
                } else {
                    makeToast(resources.getString(R.string.error))
                }
            }

            override fun onFailure(call: Call<ModelSavePartsDispatchDetails?>, t: Throwable) {
                hideProgress()
                makeToast(resources.getString(R.string.error_message))
            }
        })
    }

    private fun submitDispatchParts(
        partsList: List<ModelPartsList>,
        invoiceId: String?,
        courierName: String,
        courierTrackingNo: String
    ) {
        if (isNetworkAvailable(mContext!!)) {
            isLoading
            inventoryViewModel!!.submitDispatchParts(prefManager!!.useR_Id, invoiceId, "").observe(
                this,
                Observer<ModelSavePartsDispatchDetails?> { modelSavePartsDispatchDetails: ModelSavePartsDispatchDetails? ->
                    isLoading
                    if (modelSavePartsDispatchDetails!!.status == "200") {
                        Constants.modelPartsList = partsList
                        startActivity(
                            Intent(mContext, ChallanPDFActivity::class.java)
                                .putExtra("invoiceId", invoiceId)
                                .putExtra("dispatchFrom", prefManager!!.useR_NAME)
                                .putExtra("email", prefManager!!.useR_EMAIL)
                                .putExtra("dispatchTo", districtNameEng)
                                .putExtra("username", seUserName)
                                .putExtra(
                                    "datetime",
                                    modelSavePartsDispatchDetails.modelPartsDispatchInvoiceList.dispatchDateStr
                                )
                                .putExtra("courierName", courierName)
                                .putExtra("courierTrackingNo", courierTrackingNo)
                                .putExtra(Constants.fromWhere, "CreateNewChallanDispatchActivity")
                        )
                        finish()
                    }
                })
        } else {
            makeToast(resources.getString(R.string.no_internet_connection))
        }
    }

    private val isLoading: Unit
        get() {
            inventoryViewModel!!.isLoading.observe(
                this
            ) { aBoolean: Boolean ->
                if (aBoolean) {
                    showProgress(resources.getString(R.string.please_wait))
                } else {
                    hideProgress()
                }
            }
        }

    private fun getSelectItemHaveValue(dispatchList: List<ModelPartsList>): Boolean {
        var flag = false
        for (model in dispatchList) {
            if (model.getQuantity()!!.toInt() > 0) flag = true
            else {
                flag = false
                break
            }
        }
        return flag
    }

    override fun onClick(view: View) {
        val id = view.id
        if (id == R.id.buttonCreateChallan) {
            createChallan(dispatchList!!)
        } else if (id == R.id.ivChallanImage) {
            if (permissionGranted) {
                selectImage()
            } else {
                makeToast(resources.getString(R.string.please_allow_all_permission))
            }
        } else if (id == R.id.ivPartsImage1) {
            if (permissionGranted) {
                selectPartsImage1()
            } else {
                makeToast(resources.getString(R.string.please_allow_all_permission))
            }
        } else if (id == R.id.ivPartsImage2) {
            if (permissionGranted) {
                selectPartsImage2()
            } else {
                makeToast(resources.getString(R.string.please_allow_all_permission))
            }
        } else if (id == R.id.iv_back) {
            onBackPressed()
        }
    }
}