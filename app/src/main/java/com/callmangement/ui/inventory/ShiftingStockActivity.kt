package com.callmangement.ui.inventory

import android.content.DialogInterface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.callmangement.R
import com.callmangement.custom.CustomActivity
import com.callmangement.databinding.ActivityShiftingStockBinding
import com.callmangement.model.ModelResponse
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
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Collections
import java.util.Locale
import java.util.Objects

class ShiftingStockActivity : CustomActivity(), View.OnClickListener {
    private var binding: ActivityShiftingStockBinding? = null
    private var viewModel: ComplaintViewModel? = null
    private var inventoryViewModel: InventoryViewModel? = null
    private var district_List: MutableList<ModelDistrictList?>? = ArrayList()
    private var checkDistrict = 0
    private var checkSEUsers = 0
    private var districtNameEng: String? = ""
    private var districtId: String? = "0"
    private var seUserName: String? = ""
    private var seUserId: String? = "0"
    private var prefManager: PrefManager? = null
    private var modelPartsList: MutableList<ModelPartsList?> = ArrayList()
    private var modelSEUsersList: MutableList<ModelSEUsersList?>? = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShiftingStockBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        initView()
    }

    private fun initView() {
        binding!!.actionBar.ivThreeDot.visibility = View.GONE
        binding!!.actionBar.ivBack.visibility = View.VISIBLE
        binding!!.actionBar.layoutLanguage.visibility = View.GONE
        binding!!.actionBar.buttonPDF.visibility = View.GONE
        binding!!.actionBar.textToolbarTitle.text =
            resources.getString(R.string.shifting_se_stock_to_main_stock)
        prefManager = PrefManager(mContext!!)
        viewModel = ViewModelProviders.of(this).get(
            ComplaintViewModel::class.java
        )
        inventoryViewModel = ViewModelProviders.of(this).get(
            InventoryViewModel::class.java
        )

        districtNameEng = "--" + resources.getString(R.string.district) + "--"
        seUserName = "--" + resources.getString(R.string.username) + "--"

        setUpClickListener()
        setUpData()
        setUpUserNameSpinner()
        districtList()
        //        getSEAvailableStockList(seUserId);
    }

    private fun setUpClickListener() {
        binding!!.inputSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
            }

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                if (!charSequence.toString().isEmpty()) {
                    val filterList: MutableList<ModelPartsList> = ArrayList()
                    if (modelPartsList.size > 0) {
                        for (model in modelPartsList) {
                            if (model!!.itemName!!.lowercase(Locale.getDefault())
                                    .contains(charSequence.toString())
                            ) filterList.add(model)
                        }
                    }
                    setPartsListAdapter(filterList)
                } else setPartsListAdapter(modelPartsList)
            }

            override fun afterTextChanged(editable: Editable) {
            }
        })

        binding!!.buttonShifting.setOnClickListener(this)
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
                    if (modelDistrict?.status == "200") {
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
                            if (modelSEUsers!!.status == "200") {
                                modelSEUsersList = modelSEUsers!!.sEUsersList
                                if (modelSEUsersList != null && modelSEUsersList!!.size > 0) {
                                    Collections.reverse(modelSEUsersList)
                                    val l = ModelSEUsersList()
                                    l.userId = (-1).toString()
                                    l.userName =
                                        "--" + resources.getString(R.string.username) + "--"
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
            } else {
                makeToast(resources.getString(R.string.no_internet_connection))
            }
        }

    private fun getSEAvailableStockList(seUserId: String?) {
        if (isNetworkAvailable(mContext!!)) {
            modelPartsList.clear()
            showProgress()
            val service = RetrofitInstance.getRetrofitInstance().create(
                APIService::class.java
            )
            val call = service.getAllSE_AvlStockPartsList(seUserId, "0")
            call.enqueue(object : Callback<ModelParts?> {
                override fun onResponse(call: Call<ModelParts?>, response: Response<ModelParts?>) {
                    hideProgress()
                    if (response.isSuccessful) {
                        if (response.code() == 200) {
                            val modelParts = response.body()
                            if (modelParts != null) {
                                modelPartsList = modelParts.parts
                            }
                            if (modelParts!!.status == "200") {
                                setPartsListAdapter(modelPartsList)
                            } else {
                                binding!!.partsListLay.removeAllViews()
                                binding!!.partsListLay.visibility = View.GONE
                                binding!!.textNoDataFound.visibility = View.VISIBLE
                            }
                        } else {
                            makeToast(resources.getString(R.string.error))
                        }
                    } else {
                        makeToast(resources.getString(R.string.error))
                    }
                }

                override fun onFailure(call: Call<ModelParts?>, t: Throwable) {
                    hideProgress()
                    makeToast(resources.getString(R.string.error_message))
                }
            })
        } else {
            makeToast(resources.getString(R.string.no_internet_connection))
        }
    }

    private fun setPartsListAdapter(modelPartsList: List<ModelPartsList?>) {
        binding!!.partsListLay.removeAllViews()
        if (modelPartsList.size > 0) {
            binding!!.textNoDataFound.visibility = View.GONE
            binding!!.partsListLay.visibility = View.VISIBLE
            for (i in modelPartsList.indices) {
                val inflater = getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
                val itemView = inflater.inflate(R.layout.item_create_challan_dispatch, null, true)

                val textItemName = itemView.findViewById<TextView>(R.id.tv_item_name)
                val inputProductAvailableQty =
                    itemView.findViewById<TextView>(R.id.inputProductAvailableQty)
                val inputProductCount = itemView.findViewById<EditText>(R.id.inputProductCount)
                val ivMinus = itemView.findViewById<ImageView>(R.id.iv_minus)
                val ivPlus = itemView.findViewById<ImageView>(R.id.iv_plus)
                val ivCheckbox = itemView.findViewById<ImageView>(R.id.iv_checkbox)

                inputProductAvailableQty.text = modelPartsList[i]!!.item_Qty
                inputProductCount.setText(modelPartsList[i]!!.getQuantity())
                textItemName.text = modelPartsList[i]!!.itemName

                if (modelPartsList[i]!!.isSelectFlag) {
                    ivCheckbox.setBackgroundResource(R.drawable.ic_check_box)
                } else {
                    ivCheckbox.setBackgroundResource(R.drawable.ic_uncheck_box)
                }

                ivCheckbox.tag = 1000 + i
                ivMinus.tag = 2000 + i
                ivPlus.tag = 3000 + i
                inputProductCount.tag = 4000 + i

                ivCheckbox.setOnClickListener { view: View? ->
                    val tag = ivCheckbox.tag as Int - 1000
                    if (modelPartsList[tag]!!.isSelectFlag) {
                        modelPartsList[tag]!!.isSelectFlag = false
                        ivCheckbox.setBackgroundResource(R.drawable.ic_uncheck_box)
                    } else {
                        modelPartsList[tag]!!.isSelectFlag = true
                        ivCheckbox.setBackgroundResource(R.drawable.ic_check_box)
                    }
                }

                ivMinus.setOnClickListener { view: View? ->
                    val tag = ivMinus.tag as Int - 2000
                    val quantity =
                        inputProductCount.text.toString().trim { it <= ' ' }
                    if (!quantity.isEmpty() && quantity != "0") {
                        val intQuantity = quantity.toInt()
                        val updatedQuantity = intQuantity - 1
                        modelPartsList[tag]!!.setQuantity(updatedQuantity.toString())
                        inputProductCount.setText(modelPartsList[tag]!!.getQuantity())
                    }
                    inputProductCount.setSelection(inputProductCount.text.length)
                }

                ivPlus.setOnClickListener { view: View? ->
                    val tag = ivPlus.tag as Int - 3000
                    val quantity =
                        inputProductCount.text.toString().trim { it <= ' ' }
                    val availableQty =
                        inputProductAvailableQty.text.toString().trim { it <= ' ' }.toInt()
                    if (!quantity.isEmpty()) {
                        val intQuantity = quantity.toInt()
                        val updatedQuantity = intQuantity + 1
                        if (updatedQuantity > availableQty) {
                            makeToast(resources.getString(R.string.message_dispatch_quantity))
                        } else {
                            modelPartsList[tag]!!.setQuantity(updatedQuantity.toString())
                            inputProductCount.setText(modelPartsList[tag]!!.getQuantity())
                        }
                        inputProductCount.setSelection(inputProductCount.text.length)
                    }
                }

                inputProductCount.addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(
                        charSequence: CharSequence,
                        i: Int,
                        i1: Int,
                        i2: Int
                    ) {
                    }

                    override fun onTextChanged(
                        charSequence: CharSequence,
                        i: Int,
                        i1: Int,
                        i2: Int
                    ) {
                        val tag = inputProductCount.tag as Int - 4000
                        if (charSequence.toString() != "") {
                            val quantity = inputProductCount.text.toString().trim { it <= ' ' }
                            val availableQty =
                                inputProductAvailableQty.text.toString().trim { it <= ' ' }.toInt()
                            val intQuantity = quantity.toInt()
                            if (intQuantity > availableQty) {
                                makeToast(resources.getString(R.string.message_dispatch_quantity))
                                inputProductCount.setText(modelPartsList[tag]!!.getQuantity())
                            } else {
                                modelPartsList[tag]!!.setQuantity(quantity)
                            }
                        } else {
                            modelPartsList[tag]!!.setQuantity("0")
                        }
                        inputProductCount.setSelection(inputProductCount.text.length)
                    }

                    override fun afterTextChanged(editable: Editable) {
                    }
                })
                binding!!.partsListLay.addView(itemView)
            }
        } else {
            binding!!.partsListLay.visibility = View.GONE
            binding!!.textNoDataFound.visibility = View.VISIBLE
            hideProgress()
        }
    }

    private fun shiftingStock(dispatchList: List<ModelPartsList>) {
        if (isNetworkAvailable(mContext!!)) {
            if (districtNameEng.equals(
                    "--" + resources.getString(R.string.district) + "--",
                    ignoreCase = true
                )
            ) {
                makeToast(resources.getString(R.string.please_select_district))
            } else if (seUserName.equals(
                    "--" + resources.getString(R.string.username) + "--",
                    ignoreCase = true
                )
            ) {
                makeToast(resources.getString(R.string.please_select_username))
            } else {
                val builder = AlertDialog.Builder(
                    mContext!!
                )
                builder.setMessage(resources.getString(R.string.are_you_sure_you_want_to_shifting_se_stock_to_main_stock))
                    .setCancelable(false)
                    .setPositiveButton(resources.getString(R.string.yes)) { dlg: DialogInterface, ids: Int ->
                        dlg.dismiss()
                        createDispatchJSONArray(dispatchList)
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

    private fun createDispatchJSONArray(dispatchList: List<ModelPartsList>) {
        try {
            val jsonArray = JSONArray()
            for (model in dispatchList) {
                val jsonObject = JSONObject()
                jsonObject.put("ItemId", model.itemId)
                jsonObject.put("Item_Qty", model.getQuantity())
                jsonArray.put(jsonObject)
            }
            saveReturnPartsDetails(jsonArray)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    private fun saveReturnPartsDetails(jsonArray: JSONArray) {
        if (isNetworkAvailable(mContext!!)) {
            val service = RetrofitInstance.getRetrofitInstance().create(
                APIService::class.java
            )
            val call = service.saveReturnPartsFromUserSide(
                prefManager!!.useR_Id, seUserId, jsonArray.toString()
            )
            showProgress()
            call.enqueue(object : Callback<ModelResponse?> {
                override fun onResponse(
                    call: Call<ModelResponse?>,
                    response: Response<ModelResponse?>
                ) {
                    hideProgress()
                    if (response.isSuccessful) {
                        if (response.code() == 200) {
                            val model = response.body()
                            if (model!!.status == "200") {
                                onBackPressed()
                            } else {
                                makeToast(model!!.message)
                            }
                        }
                    } else {
                        makeToast(resources.getString(R.string.error))
                    }
                }

                override fun onFailure(call: Call<ModelResponse?>, t: Throwable) {
                    hideProgress()
                    makeToast(resources.getString(R.string.error_message))
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
        if (id == R.id.buttonShifting) {
            val dispatchList: MutableList<ModelPartsList> = ArrayList()
            for (model in modelPartsList) {
                if (model!!.isSelectFlag) dispatchList.add(model)
            }
            if (dispatchList.size > 0) {
                if (getSelectItemHaveValue(dispatchList)) {
                    shiftingStock(dispatchList)
                } else makeToast(resources.getString(R.string.selected_item_qty))
            } else makeToast(resources.getString(R.string.selected_item_qty))
        } else if (id == R.id.iv_back) {
            onBackPressed()
        }
    }
}