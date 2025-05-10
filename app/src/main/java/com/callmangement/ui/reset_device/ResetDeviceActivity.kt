package com.callmangement.ui.reset_device

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import androidx.lifecycle.ViewModelProviders
import com.callmangement.network.APIService
import com.callmangement.network.RetrofitInstance
import com.callmangement.R
import com.callmangement.custom.CustomActivity
import com.callmangement.databinding.ActivityResetDeviceBinding
import com.callmangement.model.district.ModelDistrict
import com.callmangement.model.district.ModelDistrictList
import com.callmangement.model.inventrory.ModelSEUsers
import com.callmangement.model.inventrory.ModelSEUsersList
import com.callmangement.model.reset_device.ModelResetDevice
import com.callmangement.ui.complaint.ComplaintViewModel
import com.callmangement.utils.Constants
import com.callmangement.utils.PrefManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Collections

class ResetDeviceActivity : CustomActivity(), View.OnClickListener {

    var binding: ActivityResetDeviceBinding? = null
    private var prefManager: PrefManager? = null
    private var viewModel: ComplaintViewModel? = null
    private var district_List: MutableList<ModelDistrictList?>? = ArrayList()
    private var modelSEUsersList: MutableList<ModelSEUsersList?>? = ArrayList()
    private var checkDistrict = 0
    private var checkSEUsers = 0
    private var districtNameEng = ""
    private var districtId = "0"
    private var seUserName = ""
    private var seUserId = "0"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResetDeviceBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        initView()
    }

    private fun initView() {

        mContext = this
        binding!!.actionBar.ivThreeDot.visibility = View.GONE
        binding!!.actionBar.ivBack.visibility = View.VISIBLE
        binding!!.actionBar.layoutLanguage.visibility = View.GONE
        binding!!.actionBar.buttonPDF.visibility = View.GONE
        binding!!.actionBar.textToolbarTitle.text = resources.getString(R.string.reset_device)
        prefManager = PrefManager(mContext!!)
        viewModel = ViewModelProviders.of(this).get(
            ComplaintViewModel::class.java
        )

        setUpClickListener()
        setUpData()
        setUpUserNameSpinner()
        districtList()
    }

    private fun setUpClickListener() {
        binding!!.actionBar.ivBack.setOnClickListener(this)
        binding!!.buttonResetDevice.setOnClickListener(this)
    }

    private fun setUpData() {
        binding!!.spinnerDistrict.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    adapterView: AdapterView<*>?,
                    view: View,
                    i: Int,
                    l: Long
                ) {
                    if (++checkDistrict > 1) {
                        districtNameEng = district_List!![i]!!.districtNameEng!!
                        districtId = district_List!![i]!!.districtId!!
                        if (!districtNameEng.equals(
                                "--" + resources.getString(R.string.district) + "--",
                                ignoreCase = true
                            )
                        ) {
                            sEUsersList()
                        } else {
                            districtId = "0"
                        }
                    }
                }

                override fun onNothingSelected(adapterView: AdapterView<*>?) {
                }
            }

        binding!!.spinnerServiceEngineer.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    adapterView: AdapterView<*>?,
                    view: View,
                    i: Int,
                    l: Long
                ) {
                    if (++checkSEUsers > 1) {
                        seUserName = modelSEUsersList!![i]!!.userName!!
                        seUserId = modelSEUsersList!![i]!!.userId!!
                        if (seUserName.equals(
                                "--" + resources.getString(R.string.username) + "--",
                                ignoreCase = true
                            )
                        ) {
                            seUserId = "0"
                        }
                    }
                }

                override fun onNothingSelected(adapterView: AdapterView<*>?) {
                }
            }
    }

    private fun districtList() {
        isLoading
        viewModel!!.district.observe(
            this
        ) { modelDistrict: ModelDistrict? ->
            isLoading
            if (modelDistrict!!.status == "200") {
                district_List = modelDistrict.district_List
                if (district_List != null && district_List!!.size > 0) {
                    district_List!!.reverse()
                    val l = ModelDistrictList()
                    l.districtId = (-1).toString()
                    l.districtNameEng = "--" + resources.getString(R.string.district) + "--"
                    district_List!!.add(l)
                    district_List!!.reverse()

                    val dataAdapter =
                        ArrayAdapter(
                            mContext!!, android.R.layout.simple_spinner_item,
                            district_List!!
                        )
                    dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    binding!!.spinnerDistrict.adapter = dataAdapter
                }
            }
        }
    }

    private fun setUpUserNameSpinner() {
        Collections.reverse(modelSEUsersList)
        val l = ModelSEUsersList()
        l.userId = (-1).toString()
        l.userName = "--" + resources.getString(R.string.username) + "--"
        modelSEUsersList!!.add(l)
        Collections.reverse(modelSEUsersList)

        val dataAdapter =
            ArrayAdapter(mContext!!, android.R.layout.simple_spinner_item, modelSEUsersList!!)
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding!!.spinnerServiceEngineer.adapter = dataAdapter
    }

    private fun sEUsersList(){
            val service = RetrofitInstance.getRetrofitInstance().create(
                APIService::class.java
            )
            val call = service.getSEUserList(districtId)
            call.enqueue(object : Callback<ModelSEUsers?> {
                override fun onResponse(
                    call: Call<ModelSEUsers?>,
                    response: Response<ModelSEUsers?>
                ) {
                    if (response.isSuccessful) {
                        val modelSEUsers = response.body()
                        if (modelSEUsers!!.status == "200") {
                            modelSEUsersList = modelSEUsers.sEUsersList
                            if (modelSEUsersList != null && modelSEUsersList!!.size > 0) {
                                modelSEUsersList!!.reverse()
                                val l = ModelSEUsersList()
                                l.userId = (-1).toString()
                                l.userName = "--" + resources.getString(R.string.username) + "--"
                                modelSEUsersList!!.add(l)
                                modelSEUsersList!!.reverse()

                                val dataAdapter = ArrayAdapter(
                                    mContext!!, android.R.layout.simple_spinner_item,
                                    modelSEUsersList!!
                                )
                                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                                binding!!.spinnerServiceEngineer.adapter = dataAdapter
                            }
                        } else {
                            makeToast(modelSEUsers!!.message)
                        }
                    } else {
                        makeToast(resources.getString(R.string.error))
                    }
                }

                override fun onFailure(call: Call<ModelSEUsers?>, t: Throwable) {
                    makeToast(resources.getString(R.string.error_message))
                }
            })
        }

    private fun resetDevice() {
        if (Constants.isNetworkAvailable(mContext!!)) {
            val service = RetrofitInstance.getRetrofitInstance().create(
                APIService::class.java
            )
            val call = service.resetDeviceIdTokenID(seUserId, districtId)
            showProgress()
            call.enqueue(object : Callback<ModelResetDevice?> {
                override fun onResponse(
                    call: Call<ModelResetDevice?>,
                    response: Response<ModelResetDevice?>
                ) {
                    hideProgress()
                    if (response.isSuccessful) {
                        val model = response.body()
                        if (model!!.status == "200") {
                            dialogResetDeviceSuccess()
                            binding!!.spinnerDistrict.setSelection(0)
                            binding!!.spinnerServiceEngineer.setSelection(0)
                        } else {
                            makeToast(model!!.message)
                        }
                    } else {
                        makeToast(resources.getString(R.string.error))
                    }
                }

                override fun onFailure(call: Call<ModelResetDevice?>, t: Throwable) {
                    hideProgress()
                    makeToast(resources.getString(R.string.error_message))
                }
            })
        } else {
            makeToast(resources.getString(R.string.no_internet_connection))
        }
    }

    @SuppressLint("SetTextI18n")
    private fun alertDialogResetDevice() {
        try {
            val dialog = Dialog(mContext!!)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setContentView(R.layout.dialog_reset_device_confirmation)
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.setCanceledOnTouchOutside(false)

            val buttonNo = dialog.findViewById<Button>(R.id.buttonNo)
            val buttonYes = dialog.findViewById<Button>(R.id.buttonYes)
            buttonNo.setOnClickListener { view: View? ->
                dialog.dismiss()
            }
            buttonYes.setOnClickListener { view: View? ->
                resetDevice()
                dialog.dismiss()
            }
            dialog.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun dialogResetDeviceSuccess() {
        try {
            val dialog = Dialog(mContext!!)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setContentView(R.layout.dialog_reset_device_message)
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.setCanceledOnTouchOutside(false)

            val buttonOk = dialog.findViewById<Button>(R.id.buttonOk)
            buttonOk.setOnClickListener { view: View? ->
                dialog.dismiss()
            }
            dialog.show()
        } catch (e: Exception) {
            e.printStackTrace()
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

    override fun onClick(view: View) {
        val id = view.id
        if (id == R.id.iv_back) {
            onBackPressed()
        } else if (id == R.id.buttonResetDevice) {
            alertDialogResetDevice()
        }
    }
}