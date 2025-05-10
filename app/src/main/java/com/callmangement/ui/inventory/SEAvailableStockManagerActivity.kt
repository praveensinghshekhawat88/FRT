package com.callmangement.ui.inventory

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.callmangement.R
import com.callmangement.adapter.SEAvailableStockManagerActivityAdapter
import com.callmangement.custom.CustomActivity
import com.callmangement.databinding.ActivitySeavailableStockManagerBinding
import com.callmangement.model.district.ModelDistrict
import com.callmangement.model.district.ModelDistrictList
import com.callmangement.model.inventrory.ModelParts
import com.callmangement.model.inventrory.ModelPartsList
import com.callmangement.model.inventrory.ModelSEUsers
import com.callmangement.model.inventrory.ModelSEUsersList
import com.callmangement.network.APIService
import com.callmangement.network.RetrofitInstance
import com.callmangement.ui.complaint.ComplaintViewModel
import com.callmangement.utils.Constants.isNetworkAvailable
import com.callmangement.utils.PrefManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Collections
import java.util.Locale
import java.util.Objects

class SEAvailableStockManagerActivity : CustomActivity(), View.OnClickListener {
    private var binding: ActivitySeavailableStockManagerBinding? = null
    private var adapter: SEAvailableStockManagerActivityAdapter? = null
    private var prefManager: PrefManager? = null
    private var viewModel: ComplaintViewModel? = null
    private var inventoryViewModel: InventoryViewModel? = null
    private var district_List: MutableList<ModelDistrictList?>? = ArrayList()
    private var checkDistrict = 0
    private var checkSEUsers = 0
    private var districtNameEng: String? = ""
    private var districtId: String? = "0"
    private var seUserName: String? = ""
    private var seUserId: String? = "0"
    private var modelSEUsersList: MutableList<ModelSEUsersList?>? = ArrayList()
    private var modelPartsList: MutableList<ModelPartsList?> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySeavailableStockManagerBinding.inflate(
            layoutInflater
        )
        setContentView(binding!!.root)
        binding!!.actionBar.ivBack.visibility = View.VISIBLE
        binding!!.actionBar.ivThreeDot.visibility = View.GONE
        binding!!.actionBar.layoutLanguage.visibility = View.GONE
        binding!!.actionBar.textToolbarTitle.text = resources.getString(R.string.se_available_stock)
        prefManager = PrefManager(mContext!!)
        viewModel = ViewModelProviders.of(this).get(
            ComplaintViewModel::class.java
        )
        inventoryViewModel = ViewModelProviders.of(this).get(
            InventoryViewModel::class.java
        )

        districtNameEng = "--" + resources.getString(R.string.district) + "--"
        seUserName = "--" + resources.getString(R.string.username) + "--"

        initView()
    }

    private fun initView() {
        setUpOnClickListener()
        setUpData()
        setUpUserNameSpinner()
        districtList()
        getSEAvailableStockList(seUserId)
    }

    private fun setUpOnClickListener() {
        binding!!.inputSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
            }

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
//                adapter.getFilter().filter(charSequence);
                if (!charSequence.toString().isEmpty()) {
                    val filterList: MutableList<ModelPartsList?> = ArrayList()
                    if (modelPartsList.size > 0) {
                        for (model in modelPartsList) {
                            if (model!!.itemName!!.lowercase(Locale.getDefault())
                                    .contains(charSequence.toString())
                            ) filterList.add(model)
                        }
                    }
                    setUpSEAvailableStockListAdapter(filterList)
                } else setUpSEAvailableStockListAdapter(modelPartsList)
            }

            override fun afterTextChanged(editable: Editable) {
            }
        })
        binding!!.actionBar.ivBack.setOnClickListener(this)
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
                        districtNameEng = district_List!![i]!!.districtNameEng
                        districtId = district_List!![i]!!.districtId
                        if (!districtNameEng.equals(
                                "--" + resources.getString(R.string.district) + "--",
                                ignoreCase = true
                            )
                        ) {
                            sEUsersList
                        } else {
                            setUpUserNameSpinner()
                            seUserId = "0"
                            binding!!.spinnerServiceEngineer.setSelection(0)
                            getSEAvailableStockList(seUserId)
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
                        seUserName = modelSEUsersList!![i]!!.userName
                        seUserId = modelSEUsersList!![i]!!.userId
                        if (!seUserName.equals(
                                "--" + resources.getString(R.string.username) + "--",
                                ignoreCase = true
                            )
                        ) {
                            getSEAvailableStockList(seUserId)
                        } else {
                            binding!!.rvSeAvailableStockManager.visibility = View.GONE
                            binding!!.textNoStockFound.visibility = View.VISIBLE
                        }
                    }
                }

                override fun onNothingSelected(adapterView: AdapterView<*>?) {
                }
            }
    }

    private fun districtList() {
        if (isNetworkAvailable(mContext!!)) {
            isLoading
            viewModel!!.district.observe(
                this,
                Observer<ModelDistrict?> { modelDistrict: ModelDistrict? ->
                    isLoading
                    if (modelDistrict!!.status == "200") {
                        district_List = modelDistrict.district_List
                        if (district_List != null && district_List!!.size > 0) {
                            Collections.reverse(district_List)
                            val l = ModelDistrictList()
                            l.districtId = (-1).toString()
                            l.districtNameEng = "--" + resources.getString(R.string.district) + "--"
                            district_List!!.add(l)
                            Collections.reverse(district_List)

                            val dataAdapter = ArrayAdapter(
                                mContext!!, android.R.layout.simple_spinner_item,
                                district_List!!
                            )
                            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                            binding!!.spinnerDistrict.adapter = dataAdapter
                        }
                    }
                })
        } else {
            makeToast(resources.getString(R.string.no_internet_connection))
        }
    }

    private fun setUpUserNameSpinner() {
        modelSEUsersList!!.clear()
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

    private val sEUsersList: Unit
        get() {
            if (isNetworkAvailable(mContext!!)) {
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
                            if (modelSEUsers?.status == "200") {
                                modelSEUsersList = modelSEUsers.sEUsersList
                                if (modelSEUsersList != null && modelSEUsersList!!.size > 0) {
                                    modelSEUsersList?.reverse()
                                    val l = ModelSEUsersList()
                                    l.userId = (-1).toString()
                                    l.userName =
                                        "--" + resources.getString(R.string.username) + "--"
                                    modelSEUsersList!!.add(l)
                                    modelSEUsersList?.reverse()

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
            } else {
                makeToast(resources.getString(R.string.no_internet_connection))
            }
        }

    private fun getSEAvailableStockList(seUserId: String?) {
        if (isNetworkAvailable(mContext!!)) {
            modelPartsList.clear()
            isLoadingInventory
            inventoryViewModel!!.getSEAvailableStockListForManager(seUserId, "0").observe(
                this,
                Observer<ModelParts?> { modelParts: ModelParts? ->
                    isLoadingInventory
                    if (modelParts!!.status == "200") {
                        modelPartsList = modelParts.parts
                        if (modelPartsList.size > 0) {
                            setUpSEAvailableStockListAdapter(modelPartsList)
                        } else {
                            binding!!.rvSeAvailableStockManager.visibility = View.GONE
                            binding!!.textNoStockFound.visibility = View.VISIBLE
                        }
                    }
                })
        } else {
            makeToast(resources.getString(R.string.no_internet_connection))
        }
    }

    private fun setUpSEAvailableStockListAdapter(modelPartsList: List<ModelPartsList?>) {
        if (modelPartsList.size > 0) {
            var quantity = 0
            for (model in modelPartsList) {
                if (model != null) {
                    quantity = quantity + model.item_Qty!!.toInt()
                }
            }
            binding!!.textTotalItemQuantity.text = quantity.toString()
            binding!!.rvSeAvailableStockManager.visibility = View.VISIBLE
            binding!!.textNoStockFound.visibility = View.GONE
            adapter = SEAvailableStockManagerActivityAdapter(mContext!!, modelPartsList)
            binding!!.rvSeAvailableStockManager.layoutManager =
                LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false)
            binding!!.rvSeAvailableStockManager.adapter = adapter
        } else {
            binding!!.textTotalItemQuantity.text = "0"
            binding!!.rvSeAvailableStockManager.visibility = View.GONE
            binding!!.textNoStockFound.visibility = View.VISIBLE
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

    private val isLoadingInventory: Unit
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

    override fun onClick(view: View) {
        val id = view.id
        if (id == R.id.iv_back) {
            onBackPressed()
        }
    }
}