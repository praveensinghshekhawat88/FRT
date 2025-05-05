package com.callmangement.ui.distributor.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.callmangement.network.APIService
import com.callmangement.network.RetrofitInstance
import com.callmangement.R
import com.callmangement.custom.CustomActivity
import com.callmangement.databinding.ActivityPosDistributionFormViewBinding
import com.callmangement.model.district.ModelDistrict
import com.callmangement.model.district.ModelDistrictList
import com.callmangement.model.pos_distribution_form.ModelEquipmentModel
import com.callmangement.model.pos_distribution_form.ModelNewMachineMake
import com.callmangement.model.pos_distribution_form.ModelOldMachineMake
import com.callmangement.ui.complaint.ComplaintViewModel
import com.callmangement.ui.distributor.model.PosDistributionDetail
import com.callmangement.ui.distributor.model.PosDistributionFormDetailResponse
import com.callmangement.ui.home.ZoomInZoomOutActivity
import com.callmangement.utils.Constants
import com.callmangement.utils.PrefManager
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PosDistributionFormViewActivity : CustomActivity(), View.OnClickListener {
    private var binding: ActivityPosDistributionFormViewBinding? = null
    private var prefManager: PrefManager? = null
    private var model: PosDistributionDetail? = null
    private val listEquipmentModel: MutableList<ModelEquipmentModel> = ArrayList()
    private val listOldMachineMake: MutableList<ModelOldMachineMake> = ArrayList()
    private val listNewMachineMake: MutableList<ModelNewMachineMake> = ArrayList()
    private var viewModel: ComplaintViewModel? = null
    private var district_List: List<ModelDistrictList>? = ArrayList()
    private var photoPath = ""
    private var formPhotoPath = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPosDistributionFormViewBinding.inflate(
            layoutInflater
        )
        setContentView(binding!!.root)
        binding!!.actionBar.ivBack.visibility = View.VISIBLE
        binding!!.actionBar.ivThreeDot.visibility = View.GONE
        binding!!.actionBar.layoutLanguage.visibility = View.GONE
        binding!!.actionBar.buttonPDF.visibility = View.GONE
        binding!!.actionBar.textToolbarTitle.text =
            resources.getString(R.string.pos_distribution_form)
        viewModel = ViewModelProviders.of(this).get(
            ComplaintViewModel::class.java
        )
        prefManager = PrefManager(mContext)
        model = intent.getSerializableExtra("param") as PosDistributionDetail?
        initView()
    }

    private fun initView() {
        setUpClickListener()
        setEquipmentModelSpinner()
        setNewMachineMakeSpinner()
        setOldMachineMakeSpinner()
        districtList()
        posDistributionDetail
    }

    private fun setUpClickListener() {
        binding!!.actionBar.ivBack.setOnClickListener(this)
        binding!!.rlPhoto.setOnClickListener(this)
        binding!!.rlUploadFormPhoto.setOnClickListener(this)
        binding!!.actionBar.buttonPDF.setOnClickListener(this)
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

    private val posDistributionDetail: Unit
        get() {
            if (Constants.isNetworkAvailable(mContext)) {
                showProgress()
                val service = RetrofitInstance.getRetrofitInstance().create(
                    APIService::class.java
                )
                val call = service.getPosDistributionDetailByTIDAPI(
                    prefManager!!.useR_Id, model!!.tranId.toString()
                )
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
                                        val modelResponse = getObject(
                                            responseStr,
                                            PosDistributionFormDetailResponse::class.java
                                        ) as PosDistributionFormDetailResponse
                                        val posDistributionDetail =
                                            modelResponse.posDistributionDetail
                                        if (modelResponse.status == "200") {
                                            if (posDistributionDetail != null) {
                                                binding!!.inputDate.setText(posDistributionDetail.tranDateStr)
                                                binding!!.inputFpsCode.setText(posDistributionDetail.fpscode)
                                                binding!!.inputDealerName.setText(
                                                    posDistributionDetail.dealerName
                                                )
                                                binding!!.inputMobileNumber.setText(
                                                    posDistributionDetail.mobileNo
                                                )
                                                binding!!.inputTicketNumber.setText(
                                                    posDistributionDetail.ticketNo
                                                )
                                                binding!!.inputBlockName.setText(
                                                    posDistributionDetail.blockName
                                                )
                                                binding!!.inputOldMachineSrNo.setText(
                                                    posDistributionDetail.oldMachineSerialNo
                                                )
                                                binding!!.inputFingerprintSrNo.setText(
                                                    posDistributionDetail.oldMachineBiometricSeriallNo
                                                )
                                                binding!!.inputNewMachineOrderNo.setText(
                                                    posDistributionDetail.newMachineOrderNo.toString()
                                                )
                                                binding!!.inputNewMachineSrNo.setText(
                                                    posDistributionDetail.newMachineSerialNo
                                                )
                                                binding!!.inputNewFingerprintSrNo.setText(
                                                    posDistributionDetail.newMachineBiometricSeriallNo
                                                )
                                                binding!!.inputIMEIIMEI2.setText(
                                                    posDistributionDetail.newMachineIMEI1 + " - " + posDistributionDetail.newMachineIMEI2
                                                )
                                                binding!!.spinnerDistrict.isEnabled = false
                                                binding!!.spinnerEquipmentModel.isEnabled = false
                                                binding!!.spinnerOldMachineMake.isEnabled = false
                                                binding!!.spinnerNewMachineMake.isEnabled = false
                                                photoPath =
                                                    Constants.API_BASE_URL + posDistributionDetail.upPhotoPath
                                                formPhotoPath =
                                                    Constants.API_BASE_URL + posDistributionDetail.upFormPath
                                                Glide.with(mContext)
                                                    .load(Constants.API_BASE_URL + posDistributionDetail.upPhotoPath)
                                                    .placeholder(R.drawable.image_not_fount)
                                                    .into(binding!!.ivPhoto)
                                                Glide.with(mContext)
                                                    .load(Constants.API_BASE_URL + posDistributionDetail.upFormPath)
                                                    .placeholder(R.drawable.image_not_fount)
                                                    .into(binding!!.ivUploadFormPhoto)
                                                if (posDistributionDetail.whetherOldMachProvdedForReplacmnt!!) {
                                                    binding!!.chkYes.isChecked = true
                                                    binding!!.chkNo.isChecked = false
                                                } else {
                                                    binding!!.chkNo.isChecked = true
                                                    binding!!.chkYes.isChecked = false
                                                }
                                                if (posDistributionDetail.oldMachineWorkingStatus.equals(
                                                        "Running",
                                                        ignoreCase = true
                                                    )
                                                ) {
                                                    binding!!.chkRunning.isChecked = true
                                                    binding!!.chkDead.isChecked = false
                                                } else if (posDistributionDetail.oldMachineWorkingStatus.equals(
                                                        "Dead",
                                                        ignoreCase = true
                                                    )
                                                ) {
                                                    binding!!.chkRunning.isChecked = false
                                                    binding!!.chkDead.isChecked = true
                                                }
                                                binding!!.chkCompleteWithSatisfactorily.isChecked =
                                                    posDistributionDetail.isCompleteWithSatisfactorily!!
                                                if (district_List!!.size > 0) {
                                                    for (i in district_List!!.indices) {
                                                        if (posDistributionDetail.districtId == district_List!![i].districtId.toInt()) {
                                                            binding!!.spinnerDistrict.setSelection(i)
                                                            binding!!.spinnerDistrict.isEnabled =
                                                                false
                                                        }
                                                    }
                                                }

                                                if (listEquipmentModel.size > 0) {
                                                    for (i in listEquipmentModel.indices) {
                                                        if (posDistributionDetail.equipmentModelId == listEquipmentModel[i].id.toInt()) {
                                                            binding!!.spinnerEquipmentModel.setSelection(
                                                                i
                                                            )
                                                            binding!!.spinnerEquipmentModel.isEnabled =
                                                                false
                                                        }
                                                    }
                                                }

                                                if (listOldMachineMake.size > 0) {
                                                    for (i in listOldMachineMake.indices) {
                                                        if (posDistributionDetail.oldMachineVenderid == listOldMachineMake[i].id.toInt()) {
                                                            binding!!.spinnerOldMachineMake.setSelection(
                                                                i
                                                            )
                                                            binding!!.spinnerOldMachineMake.isEnabled =
                                                                false
                                                        }
                                                    }
                                                }
                                                if (listNewMachineMake.size > 0) {
                                                    for (i in listNewMachineMake.indices) {
                                                        if (posDistributionDetail.newMachineVenderid == listNewMachineMake[i].id.toInt()) {
                                                            binding!!.spinnerNewMachineMake.setSelection(
                                                                i
                                                            )
                                                            binding!!.spinnerNewMachineMake.isEnabled =
                                                                false
                                                        }
                                                    }
                                                }
                                            }
                                        } else {
                                            makeToast(modelResponse.message)
                                        }
                                    } else {
                                        makeToast(resources.getString(R.string.error))
                                    }
                                } else {
                                    makeToast(resources.getString(R.string.error))
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
            } else {
                makeToast(resources.getString(R.string.no_internet_connection))
            }
        }

    override fun onClick(view: View) {
        val id = view.id
        if (id == R.id.iv_back) {
            onBackPressed()
        } else if (id == R.id.rl_photo) {
            startActivity(
                Intent(mContext, ZoomInZoomOutActivity::class.java).putExtra(
                    "image",
                    photoPath
                )
            )
        } else if (id == R.id.rl_upload_form_photo) {
            startActivity(
                Intent(mContext, ZoomInZoomOutActivity::class.java).putExtra(
                    "image",
                    formPhotoPath
                )
            )
        }
    }
}