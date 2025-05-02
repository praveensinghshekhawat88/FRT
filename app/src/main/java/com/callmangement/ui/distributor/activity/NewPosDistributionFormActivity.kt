package com.callmangement.ui.distributor.activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.wifi.WifiManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.text.format.Formatter
import android.view.Gravity
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.CompoundButton
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.callmangement.Network.APIService
import com.callmangement.Network.MultipartRequester
import com.callmangement.Network.RetrofitInstance
import com.callmangement.R
import com.callmangement.custom.CustomActivity
import com.callmangement.databinding.ActivityNewPosDistributionFormBinding
import com.callmangement.imagepicker.model.Config
import com.callmangement.imagepicker.model.Image
import com.callmangement.imagepicker.ui.imagepicker.ImagePicker
import com.callmangement.model.district.ModelDistrict
import com.callmangement.model.district.ModelDistrictList
import com.callmangement.model.pos_distribution_form.ModelEquipmentModel
import com.callmangement.model.pos_distribution_form.ModelNewMachineDetailByFPSResponse
import com.callmangement.model.pos_distribution_form.ModelNewMachineMake
import com.callmangement.model.pos_distribution_form.ModelOldMachineDetailByFPSResponse
import com.callmangement.model.pos_distribution_form.ModelOldMachineMake
import com.callmangement.support.ImageUtilsForRotate
import com.callmangement.support.dexter.Dexter
import com.callmangement.support.dexter.MultiplePermissionsReport
import com.callmangement.support.dexter.PermissionToken
import com.callmangement.support.dexter.listener.PermissionRequest
import com.callmangement.support.dexter.listener.multi.MultiplePermissionsListener
import com.callmangement.ui.complaint.ComplaintViewModel
import com.callmangement.utils.CompressImage
import com.callmangement.utils.Constants
import com.callmangement.utils.DateTimeUtils
import com.callmangement.utils.PrefManager
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody.Part.Companion.createFormData
import okhttp3.RequestBody
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.IOException
import java.nio.charset.StandardCharsets
import java.util.Objects

class NewPosDistributionFormActivity : CustomActivity(), View.OnClickListener {
    private var binding: ActivityNewPosDistributionFormBinding? = null
    private var photoStoragePath = ""
    private var permissionGranted = false
    val REQUEST_PICK_PHOTO: Int = 1113
    private val listEquipmentModel: MutableList<ModelEquipmentModel> = ArrayList()
    private val listOldMachineMake: MutableList<ModelOldMachineMake> = ArrayList()
    private val listNewMachineMake: MutableList<ModelNewMachineMake> = ArrayList()
    private var viewModel: ComplaintViewModel? = null
    private val checkDistrict = 0
    private var districtNameEng = ""
    private var districtId = "0"
    private val tranId = "0"
    private var prefManager: PrefManager? = null
    private var district_List: List<ModelDistrictList>? = ArrayList()
    private val flagGetOldMachineDetail = false
    private var whetherOldMachineProvidedForReplacement = "1"
    private var equipmentModelName = ""
    private var equipmentModelId = "1"
    private var oldMachineMakeId = "1"
    private var newMachineMakeId = ""
    private var oldMachineCondition = ""
    private var completeWithSatisfactorily = "1"

    private var oldMachineSrNo = ""
    private var fingerPrintSrNo = ""

    var builder: AlertDialog.Builder? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewPosDistributionFormBinding.inflate(
            layoutInflater
        )
        setContentView(binding!!.root)
        binding!!.actionBar.ivBack.visibility = View.VISIBLE
        binding!!.actionBar.ivThreeDot.visibility = View.GONE
        binding!!.actionBar.layoutLanguage.visibility = View.GONE
        binding!!.actionBar.buttonPDF.visibility = View.GONE
        binding!!.actionBar.textToolbarTitle.text =
            resources.getString(R.string.pos_distribution_form)
        prefManager = PrefManager(mContext)
        viewModel = ViewModelProviders.of(this).get(
            ComplaintViewModel::class.java
        )
        initView()
    }

    private fun initView() {
        districtNameEng = "--" + resources.getString(R.string.district) + "--"
        binding!!.inputDealerName.isEnabled = false
        binding!!.inputMobileNumber.isEnabled = false
        binding!!.inputBlockName.isEnabled = false
        binding!!.inputNewMachineSrNo.isEnabled = false
        binding!!.inputNewFingerprintSrNo.isEnabled = false
        binding!!.inputIMEIIMEI2.isEnabled = false
        binding!!.inputAccessoriesProvided.isEnabled = false
        binding!!.inputDate.setText(DateTimeUtils.getCurrentDate())
        setUpOnClickListener()
        checkPermission()
        districtList()
        setEquipmentModelSpinner()
        setOldMachineMakeSpinner()
        setNewMachineMakeSpinner()
    }

    private fun setUpOnClickListener() {
        binding!!.spinnerDistrict.isEnabled = false
        binding!!.inputFpsCode.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
            }

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
            }

            override fun afterTextChanged(charSequence: Editable) {
                if (charSequence.toString().length > 0) {
                    Handler().postDelayed(
                        { getOldMachineDetailsByFPSAPI(charSequence.toString()) },
                        2500
                    )
                } else {
                    binding!!.inputDealerName.text!!.clear()
                    binding!!.inputMobileNumber.text!!.clear()
                    binding!!.inputBlockName.text!!.clear()
                    binding!!.inputOldMachineSrNo.text!!.clear()
                    binding!!.inputFingerprintSrNo.text!!.clear()
                }
            }
        })

        binding!!.inputNewMachineOrderNo.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
            }

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
            }

            override fun afterTextChanged(charSequence: Editable) {
                if (charSequence.toString().length > 0) {
                    Handler().postDelayed(
                        { getNewMachineDetailsByOrdNoAPI(charSequence.toString()) },
                        2000
                    )
                } else {
                    binding!!.inputNewMachineSrNo.text!!.clear()
                    binding!!.inputNewFingerprintSrNo.text!!.clear()
                    binding!!.inputIMEIIMEI2.text!!.clear()
                    binding!!.inputAccessoriesProvided.text!!.clear()
                }
            }
        })

        binding!!.inputOldMachineSrNo.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
            }

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                if (charSequence.toString().length > 0) {
                    if (charSequence.toString() == oldMachineSrNo) binding!!.inputFingerprintSrNo.setText(
                        fingerPrintSrNo
                    )
                    else binding!!.inputFingerprintSrNo.setText("")
                } else binding!!.inputFingerprintSrNo.setText("")
            }

            override fun afterTextChanged(charSequence: Editable) {
            }
        })

        binding!!.spinnerDistrict.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    adapterView: AdapterView<*>?,
                    view: View,
                    i: Int,
                    l: Long
                ) {
                    districtNameEng = district_List!![i].districtNameEng
                    districtId = district_List!![i].districtId
                    /*if (!districtId.equals("0") && binding.inputFpsCode.getText()).toString().trim().length() > 0)
                        getOldMachineDetailsByFPSAPI(binding.inputFpsCode.getText().toString());*/
                }

                override fun onNothingSelected(adapterView: AdapterView<*>?) {
                }
            }

        binding!!.spinnerEquipmentModel.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    adapterView: AdapterView<*>?,
                    view: View,
                    i: Int,
                    l: Long
                ) {
                    equipmentModelName = listEquipmentModel[i].name
                    equipmentModelId = listEquipmentModel[i].id
                }

                override fun onNothingSelected(adapterView: AdapterView<*>?) {
                }
            }

        binding!!.spinnerOldMachineMake.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    adapterView: AdapterView<*>?,
                    view: View,
                    i: Int,
                    l: Long
                ) {
                    oldMachineMakeId = listOldMachineMake[i].id
                }

                override fun onNothingSelected(adapterView: AdapterView<*>?) {
                }
            }
        binding!!.spinnerNewMachineMake.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    adapterView: AdapterView<*>?,
                    view: View,
                    i: Int,
                    l: Long
                ) {
                    newMachineMakeId = listNewMachineMake[i].id
                }

                override fun onNothingSelected(adapterView: AdapterView<*>?) {
                }
            }


        binding!!.chkYes.setOnCheckedChangeListener { compoundButton: CompoundButton?, checked: Boolean ->
            if (checked) {
                whetherOldMachineProvidedForReplacement = "1"
                binding!!.chkNo.isChecked = false
            } else {
                whetherOldMachineProvidedForReplacement = "0"
            }
        }

        binding!!.chkNo.setOnCheckedChangeListener { compoundButton: CompoundButton?, checked: Boolean ->
            if (checked) {
                whetherOldMachineProvidedForReplacement = "0"
                binding!!.chkYes.isChecked = false
            } else {
                whetherOldMachineProvidedForReplacement = "1"
                binding!!.chkYes.isChecked = true
            }
        }


        binding!!.chkCompleteWithSatisfactorily.setOnCheckedChangeListener { compoundButton: CompoundButton?, checked: Boolean ->
            completeWithSatisfactorily = if (checked) {
                "1"
            } else {
                "0"
            }
        }

        binding!!.chkRunning.setOnCheckedChangeListener { compoundButton: CompoundButton?, checked: Boolean ->
            if (checked) {
                oldMachineCondition = "Running"
                binding!!.chkDead.isChecked = false
            } /*else {
                oldMachineCondition = "";
            }*/
        }

        binding!!.chkDead.setOnCheckedChangeListener { compoundButton: CompoundButton?, checked: Boolean ->
            if (checked) {
                oldMachineCondition = "Dead"
                binding!!.chkRunning.isChecked = false
            } /*else {
                oldMachineCondition = "";
            }*/
        }

        binding!!.buttonSubmit.setOnClickListener(this)
        binding!!.ivPhoto.setOnClickListener(this)
        binding!!.actionBar.ivBack.setOnClickListener(this)
    }

    private fun setEquipmentModelSpinner() {
        val model = ModelEquipmentModel()
        model.id = "1"
        model.name = "Mobiocean TPS 900 (Android 10 OS)"
        listEquipmentModel.add(model)
        val dataAdapter =
            ArrayAdapter(mContext, android.R.layout.simple_spinner_item, listEquipmentModel)
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding!!.spinnerEquipmentModel.adapter = dataAdapter
        binding!!.spinnerEquipmentModel.setSelection(0)
    }

    private fun setOldMachineMakeSpinner() {
        val model1 = ModelOldMachineMake()
        model1.id = "1"
        model1.name = "VISIONTEK"
        listOldMachineMake.add(model1)
        val model2 = ModelOldMachineMake()
        model2.id = "2"
        model2.name = "ANALOGICS"
        listOldMachineMake.add(model2)
        val dataAdapter =
            ArrayAdapter(mContext, android.R.layout.simple_spinner_item, listOldMachineMake)
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding!!.spinnerOldMachineMake.adapter = dataAdapter
        binding!!.spinnerOldMachineMake.setSelection(0)
    }

    private fun setNewMachineMakeSpinner() {
        val model1 = ModelNewMachineMake()
        model1.id = "3"
        model1.name = "MOBIOCEAN"
        listNewMachineMake.add(model1)
        val dataAdapter =
            ArrayAdapter(mContext, android.R.layout.simple_spinner_item, listNewMachineMake)
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding!!.spinnerNewMachineMake.adapter = dataAdapter
        binding!!.spinnerNewMachineMake.setSelection(0)
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

    private fun districtList() {
        if (Constants.isNetworkAvailable(mContext)) {
            isLoading
            viewModel!!.district.observe(
                this
            ) { modelDistrict: ModelDistrict? ->
                isLoading
                if (modelDistrict!!.status == "200") {
                    district_List = modelDistrict.district_List
                    if (district_List != null && district_List!!.size > 0) {
                        /*Collections.reverse(district_List);
                    ModelDistrictList_w l = new ModelDistrictList_w();
                    l.setDistrictId(String.valueOf(-1));
                    l.setDistrictNameEng("--" + getResources().getString(R.string.district) + "--");
                    district_List.add(l);
                    Collections.reverse(district_List);*/
                        val dataAdapter = ArrayAdapter(
                            mContext, android.R.layout.simple_spinner_item,
                            district_List!!
                        )
                        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        binding!!.spinnerDistrict.adapter = dataAdapter
                    }
                }
            }
        } else {
            makeToast(resources.getString(R.string.no_internet_connection))
        }
    }

    private val isLoading: Unit
        get() {
            viewModel!!.isLoading.observe(
                this
            ) { aBoolean: Boolean ->
                if (aBoolean) {
                    showProgress(resources.getString(R.string.please_wait))
                } else {
                    hideProgress()
                }
            }
        }

    private fun selectPhoto() {
        try {
            val items = arrayOf<CharSequence>(
                resources.getString(R.string.imagepicker_str_take_photo),
                resources.getString(R.string.imagepicker_str_cancel)
            )
            val title = TextView(mContext)
            title.text = resources.getString(R.string.capture_photo)
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
                        .start(REQUEST_PICK_PHOTO)
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
        if (requestCode == REQUEST_PICK_PHOTO && resultCode == RESULT_OK && data != null) {
            val images = data.getParcelableArrayListExtra<Image>(Config.EXTRA_IMAGES)
            if (images != null && images.size > 0) {
                val image = images[0]
                photoStoragePath = image.path
                if (photoStoragePath.contains("file:/")) {
                    photoStoragePath = photoStoragePath.replace("file:/", "")
                }
                photoStoragePath = CompressImage.compress(
                    photoStoragePath,
                    this
                )
                val imgFile = File(photoStoragePath)
                val myBitmap = BitmapFactory.decodeFile(imgFile.absolutePath)

                try {
                    binding!!.ivPhoto.setImageBitmap(
                        ImageUtilsForRotate.ensurePortrait(
                            photoStoragePath
                        )
                    )
                } catch (e: IOException) {
                    binding!!.ivPhoto.setImageBitmap(myBitmap)
                    e.printStackTrace()
                } catch (e: NullPointerException) {
                    e.printStackTrace()
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun getOldMachineDetailsByFPSAPI(fpsCode: String) {
        if (Constants.isNetworkAvailable(mContext)) {
            /*if (districtId.equals("0")) {
                if (!flagGetOldMachineDetail) {
                    Toast.makeText(mContext, getResources().getString(R.string.please_select_district), Toast.LENGTH_SHORT).show();
                    flagGetOldMachineDetail = true;
                }
                return;
            }*/
            val service = RetrofitInstance.getRetrofitInstance().create(
                APIService::class.java
            )
            val call =
                service.GetOldMachineDetailsByFPSAPI(districtId, prefManager!!.useR_Id, fpsCode)
            call.enqueue(object : Callback<ResponseBody?> {
                override fun onResponse(
                    call: Call<ResponseBody?>,
                    response: Response<ResponseBody?>
                ) {
                    if (response.isSuccessful) {
                        try {
                            if (response.code() == 200) {
                                if (response.body() != null) {
                                    val responseStr = response.body()!!.string()
                                    //    Log.e("response", responseStr);
                                    val jsonObject = JSONObject((responseStr))
                                    val status = jsonObject.optString("status")
                                    val modelResponse = getObject(
                                        responseStr,
                                        ModelOldMachineDetailByFPSResponse::class.java
                                    ) as ModelOldMachineDetailByFPSResponse
                                    if (status == "200") {
                                        if (modelResponse != null) {
                                            binding!!.inputDealerName.isEnabled = false
                                            binding!!.inputMobileNumber.isEnabled = false
                                            binding!!.inputBlockName.isEnabled = false
                                            //                                            binding.inputOldMachineSrNo.setEnabled(false);
                                            binding!!.inputFingerprintSrNo.isEnabled = false
                                            binding!!.inputTicketNumber.isEnabled = false

                                            oldMachineSrNo =
                                                modelResponse.oldMachineData.oldMachineSerialNo
                                            fingerPrintSrNo =
                                                modelResponse.oldMachineData.oldMachineBiometricSeriallNo

                                            binding!!.inputDealerName.setText(modelResponse.oldMachineData.dealerName)
                                            binding!!.inputMobileNumber.setText(modelResponse.oldMachineData.mobileNo)
                                            binding!!.inputBlockName.setText(modelResponse.oldMachineData.blockName)
                                            binding!!.inputOldMachineSrNo.setText(oldMachineSrNo)
                                            binding!!.inputFingerprintSrNo.setText(fingerPrintSrNo)
                                            binding!!.inputTicketNumber.setText(modelResponse.oldMachineData.ticketNo)

                                            districtId =
                                                modelResponse.oldMachineData.districtId.toString()
                                            binding!!.spinnerDistrict.setSelection(
                                                getSelectedDistrictSpinnerIndex(modelResponse.oldMachineData.districtId.toString())
                                            )
                                        }
                                    } else {
                                        dialogMessage(modelResponse.message)
                                        binding!!.inputDealerName.setText("")
                                        binding!!.inputMobileNumber.setText("")
                                        binding!!.inputBlockName.setText("")
                                        binding!!.inputOldMachineSrNo.setText("")
                                        binding!!.inputFingerprintSrNo.setText("")
                                        binding!!.inputTicketNumber.setText("")
                                        binding!!.spinnerDistrict.setSelection(-1)
                                    }
                                }
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    } else {
//                        makeToast(getResources().getString(R.string.error));
                    }
                }

                override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                    makeToast(resources.getString(R.string.error_message))
                }
            })
        } else {
            makeToast(resources.getString(R.string.no_internet_connection))
        }
    }

    private fun getSelectedDistrictSpinnerIndex(districtId: String): Int {
        var index = 0
        try {
            if (district_List != null && district_List!!.size > 0) {
                for (i in district_List!!.indices) {
                    if (districtId == district_List!![i].districtId) {
                        index = i
                        break
                    }
                }
            }
            return index
        } catch (e: Exception) {
            e.printStackTrace()
            return index
        }
    }

    private fun getNewMachineDetailsByOrdNoAPI(newMachineOrderNo: String) {
        if (Constants.isNetworkAvailable(mContext)) {
            val service = RetrofitInstance.getRetrofitInstance().create(
                APIService::class.java
            )
            val call = service.GetNewMachineDetailsByOrdNoAPI( /*"0"*/districtId,
                prefManager!!.useR_Id,
                newMachineOrderNo
            )
            call.enqueue(object : Callback<ResponseBody?> {
                @SuppressLint("SetTextI18n")
                override fun onResponse(
                    call: Call<ResponseBody?>,
                    response: Response<ResponseBody?>
                ) {
                    if (response.isSuccessful) {
                        try {
                            if (response.code() == 200) {
                                if (response.body() != null) {
                                    val responseStr = response.body()!!.string()
                                    //    Log.e("response", responseStr);
                                    val jsonObject = JSONObject((responseStr))
                                    val status = jsonObject.optString("status")
                                    val modelResponse = getObject(
                                        responseStr,
                                        ModelNewMachineDetailByFPSResponse::class.java
                                    ) as ModelNewMachineDetailByFPSResponse
                                    if (status == "200") {
                                        if (modelResponse != null) {
                                            binding!!.inputNewMachineSrNo.isEnabled = false
                                            binding!!.inputNewFingerprintSrNo.isEnabled = false
                                            binding!!.inputIMEIIMEI2.isEnabled = false
                                            binding!!.inputAccessoriesProvided.isEnabled = false

                                            binding!!.inputNewMachineSrNo.setText(modelResponse.newMachineData.newMachineSerialNo)
                                            binding!!.inputNewFingerprintSrNo.setText(modelResponse.newMachineData.newMachineBiometricSeriallNo)
                                            binding!!.inputIMEIIMEI2.setText(modelResponse.newMachineData.newMachineIMEI1 + " - " + modelResponse.newMachineData.newMachineIMEI2)
                                            binding!!.inputAccessoriesProvided.setText(modelResponse.newMachineData.accessoriesProvided)
                                        }
                                    } else {
                                        dialogMessage(modelResponse.message)
                                        binding!!.inputNewMachineSrNo.setText("")
                                        binding!!.inputNewFingerprintSrNo.setText("")
                                        binding!!.inputIMEIIMEI2.setText("")
                                        binding!!.inputAccessoriesProvided.setText("")
                                    }
                                }
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    } else {
//                        makeToast(getResources().getString(R.string.error));
                    }
                }

                override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                    makeToast(resources.getString(R.string.error_message))
                }
            })
        } else {
            makeToast(resources.getString(R.string.no_internet_connection))
        }
    }

    private fun submit() {
        if (Constants.isNetworkAvailable(mContext)) {
            val tranDate =
                binding!!.inputDate.text!!.toString().trim { it <= ' ' }
            val fpsCode =
                binding!!.inputFpsCode.text.toString().trim { it <= ' ' }
            val newMachineOrderNo =
                binding!!.inputNewMachineOrderNo.text.toString()
                    .trim { it <= ' ' }
            val dealerName =
                binding!!.inputDealerName.text.toString().trim { it <= ' ' }
            val mobileNumber = binding!!.inputMobileNumber.text.toString()
                .trim { it <= ' ' }
            val ticketNumber = binding!!.inputTicketNumber.text.toString()
                .trim { it <= ' ' }
            val blockName =
                binding!!.inputBlockName.text.toString().trim { it <= ' ' }
            val oldMachineSrNo =
                binding!!.inputOldMachineSrNo.text.toString()
                    .trim { it <= ' ' }
            val oldFingerprintSrNo =
                binding!!.inputFingerprintSrNo.text.toString()
                    .trim { it <= ' ' }
            val newMachineSrNo =
                binding!!.inputNewMachineSrNo.text.toString()
                    .trim { it <= ' ' }
            val newFingerprintSrNo =
                binding!!.inputNewFingerprintSrNo.text.toString()
                    .trim { it <= ' ' }
            val imei1_imei2 =
                binding!!.inputIMEIIMEI2.text.toString().trim { it <= ' ' }
            val accessoriesProvided =
                binding!!.inputAccessoriesProvided.text.toString()
                    .trim { it <= ' ' }
            val remark =
                "Receive in new condition with all accessories and satisfactory training given."

            if (fpsCode.isEmpty()) {
                makeToast(resources.getString(R.string.please_input_fps_code))
            } else if (newMachineOrderNo.isEmpty()) {
                makeToast(resources.getString(R.string.please_input_new_machine_order_number))
            } else if (oldMachineCondition.isEmpty()) {
                makeToast(resources.getString(R.string.please_select_old_machine_condition))
            } else if (photoStoragePath == "") {
                val builder = AlertDialog.Builder(mContext)
                builder.setMessage(resources.getString(R.string.submit_form_confirmation_message_without_image))
                    .setCancelable(false)
                    .setPositiveButton(resources.getString(R.string.yes)) { dialog: DialogInterface, ids: Int ->
                        callSaveFormApi(
                            newMachineOrderNo,
                            fpsCode,
                            ticketNumber,
                            tranDate,
                            oldFingerprintSrNo,
                            dealerName,
                            mobileNumber,
                            blockName
                        )
                        dialog.cancel()
                    }
                    .setNegativeButton(
                        resources.getString(R.string.no)
                    ) { dialog: DialogInterface, ids: Int -> dialog.cancel() }
                val alert = builder.create()
                alert.setTitle(resources.getString(R.string.alert))
                alert.show()
            } else {
                val builder = AlertDialog.Builder(mContext)
                builder.setMessage(resources.getString(R.string.submit_form_confirmation_message))
                    .setCancelable(false)
                    .setPositiveButton(resources.getString(R.string.yes)) { dialog: DialogInterface, ids: Int ->
                        callSaveFormApi(
                            newMachineOrderNo,
                            fpsCode,
                            ticketNumber,
                            tranDate,
                            oldFingerprintSrNo,
                            dealerName,
                            mobileNumber,
                            blockName
                        )
                        dialog.cancel()
                    }
                    .setNegativeButton(
                        resources.getString(R.string.no)
                    ) { dialog: DialogInterface, ids: Int -> dialog.cancel() }
                val alert = builder.create()
                alert.setTitle(resources.getString(R.string.alert))
                alert.show()
            }
        } else {
            makeToast(resources.getString(R.string.no_internet_connection))
        }
    }

    private fun callSaveFormApi(
        newMachineOrderNo: String,
        fpsCode: String,
        ticketNumber: String,
        tranDate: String,
        oldFingerprintSrNo: String,
        dealerName: String,
        mobileNumber: String,
        blockName: String
    ) {
        showProgress()
        val service = RetrofitInstance.getRetrofitInstance().create(
            APIService::class.java
        )

        val attachment: RequestBody
        var fileName = ""
        if (photoStoragePath != "") {
            fileName = File(photoStoragePath).name
            attachment =
                RequestBody.create("multipart/form-data".toMediaTypeOrNull(), File(photoStoragePath))
        } else {
            fileName = ""
            attachment = RequestBody.create("text/plain".toMediaTypeOrNull(), "")
        }

        val call = service.saveNewPosDistributionAPI(
            MultipartRequester.fromString(prefManager!!.useR_Id),
            MultipartRequester.fromString(tranId),
            MultipartRequester.fromString(districtId),
            MultipartRequester.fromString(equipmentModelId),
            MultipartRequester.fromString(oldMachineMakeId),
            MultipartRequester.fromString(newMachineMakeId),
            MultipartRequester.fromString(newMachineOrderNo),
            MultipartRequester.fromString(fpsCode),
            MultipartRequester.fromString(ticketNumber),
            MultipartRequester.fromString(tranDate),
            MultipartRequester.fromString(oldMachineSrNo),
            MultipartRequester.fromString(oldFingerprintSrNo),
            MultipartRequester.fromString(oldMachineCondition),
            MultipartRequester.fromString(""),
            MultipartRequester.fromString(""),
            MultipartRequester.fromString(convertStringToUTF8(dealerName)),
            MultipartRequester.fromString(mobileNumber),
            MultipartRequester.fromString(convertStringToUTF8(blockName)),
            MultipartRequester.fromString(networkIp),
            MultipartRequester.fromString(whetherOldMachineProvidedForReplacement),
            MultipartRequester.fromString(completeWithSatisfactorily),
            createFormData("dealerImage", fileName, attachment)
        )
        call.enqueue(object : Callback<ResponseBody?> {
            @SuppressLint("SetTextI18n")
            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                hideProgress()
                if (response.isSuccessful) {
                    try {
                        if (response.code() == 200) {
                            if (response.body() != null) {
                                val responseStr = response.body()!!.string()
                                //   Log.e("response", responseStr);
                                val jsonObject = JSONObject((responseStr))
                                val status = jsonObject.optString("status")
                                val modelResponse = getObject(
                                    responseStr,
                                    ModelNewMachineDetailByFPSResponse::class.java
                                ) as ModelNewMachineDetailByFPSResponse
                                if (status == "200") {
                                    if (modelResponse != null) {
//                                        makeToast(modelResponse.getMessage());
                                        onBackPressed()
                                    }
                                } else {
                                    makeToast(modelResponse.message)
                                }
                            }
                        }
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

    private val networkIp: String
        get() {
            val wm =
                applicationContext.getSystemService(WIFI_SERVICE) as WifiManager
            return Formatter.formatIpAddress(
                wm.connectionInfo.ipAddress
            )
        }

    private fun dialogMessage(message: String) {
        if (builder == null) {
            builder = AlertDialog.Builder(mContext)
            builder!!.setMessage(message)
                .setCancelable(false)
                .setPositiveButton(resources.getString(R.string.ok)) { dialog: DialogInterface, ids: Int ->
                    builder = null
                    dialog.cancel()
                }
            /*.setNegativeButton(getResources().getString(R.string.cancel), (dialog, ids) -> {
    builder = null;
    dialog.cancel();
});*/
            val alert = builder!!.create()
            alert.setTitle(resources.getString(R.string.alert))
            alert.show()
        }
    }

    private fun convertStringToUTF8(s: String): String {
        val out =
            String(s.toByteArray(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1)
        return out
    }

    override fun onClick(view: View) {
        val id = view.id
        if (id == R.id.buttonSubmit) {
            submit()
        } else if (id == R.id.ivPhoto) {
            if (permissionGranted) {
                selectPhoto()
            } else {
                makeToast(resources.getString(R.string.please_allow_all_permission))
            }
        } else if (id == R.id.iv_back) {
            onBackPressed()
        }
    }
}