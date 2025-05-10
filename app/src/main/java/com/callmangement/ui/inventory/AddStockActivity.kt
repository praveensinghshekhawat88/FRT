package com.callmangement.ui.inventory

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.EditText
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.callmangement.R
import com.callmangement.adapter.DialogAddStockListAdapter
import com.callmangement.custom.CustomActivity
import com.callmangement.custom.SpinnerAdapter
import com.callmangement.databinding.ActivityAddStockBinding
import com.callmangement.model.inventrory.ModelAddStock
import com.callmangement.model.inventrory.ModelParts
import com.callmangement.model.inventrory.ModelPartsList
import com.callmangement.network.APIService
import com.callmangement.network.RetrofitInstance
import com.callmangement.report_pdf.AddStockPdfActivity
import com.callmangement.utils.Constants
import com.callmangement.utils.Constants.isNetworkAvailable
import com.callmangement.utils.PrefManager
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Collections

class AddStockActivity : CustomActivity(), View.OnClickListener {
    private var binding: ActivityAddStockBinding? = null
    private var inventoryViewModel: InventoryViewModel? = null
    private var prefManager: PrefManager? = null
    private val list: MutableList<ModelAddStock>? = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddStockBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        binding!!.actionBar.ivBack.visibility = View.VISIBLE
        binding!!.actionBar.ivThreeDot.visibility = View.GONE
        binding!!.actionBar.layoutLanguage.visibility = View.GONE
        binding!!.actionBar.textToolbarTitle.text = resources.getString(R.string.add_stock)
        inventoryViewModel = ViewModelProviders.of(this).get(
            InventoryViewModel::class.java
        )
        prefManager = PrefManager(mContext!!)
        initView()
    }

    private fun initView() {
        setUpOnClickListener()
        partsList
    }

    private fun setUpOnClickListener() {
        binding!!.actionBar.ivBack.setOnClickListener(this)
        binding!!.buttonAddLayout.setOnClickListener(this)
        binding!!.buttonAddStock.setOnClickListener(this)
    }

    private val partsList: Unit
        get() {
            inventoryViewModel!!.partsList.observe(
                this
            ) { modelParts: ModelParts? ->
                if (modelParts!!.status == "200") {
                    val modelProductsList =
                        modelParts.parts
                    if (modelProductsList != null && modelProductsList.size > 0) {
                        Collections.reverse(modelProductsList)
                        val l = ModelPartsList()
                        l.itemId = (-1).toString()
                        l.itemName = "--" + resources.getString(R.string.select_products) + "--"
                        modelProductsList.add(l)
                        Collections.reverse(modelProductsList)
                        Constants.modelProductLists = modelProductsList
                        setUpPartsListAdapter()
                    }
                }
            }
        }

    private fun setUpPartsListAdapter() {
        list!!.clear()
        list.add(ModelAddStock("", "", "", Constants.modelProductLists as List<ModelPartsList?>?))
        setUpPartsList()
    }

    private fun setUpPartsList() {
        binding!!.partsListLay.removeAllViews()

        for (i in list!!.indices) {
            val inflater = getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val itemView = inflater.inflate(R.layout.item_add_stock_activity, null, true)

            val topLay = itemView.findViewById<RelativeLayout>(R.id.rl_product)
            val spinnerItemName = itemView.findViewById<Spinner>(R.id.spinnerItemName)
            val tvSpinner = itemView.findViewById<TextView>(R.id.tv_spinner)
            val inputItemQuantity = itemView.findViewById<EditText>(R.id.inputItemQuantity)
            val buttonRemoveLayout = itemView.findViewById<ImageView>(R.id.buttonRemoveLayout)

            if (list.get(i).isFlag) spinnerItemName.visibility = View.GONE
            else spinnerItemName.visibility = View.VISIBLE

            tvSpinner.text = list[i].itemName
            list[i].relativeLayout = topLay
            spinnerItemName.adapter =
                SpinnerAdapter(mContext, list[i].spinnerItemList)
            spinnerItemName.setSelection(list[i].spinnerSelectedIndex)

            if (list[i].isItemSelectFlag) {
                inputItemQuantity.setText(list[i].qty)
                inputItemQuantity.isEnabled = true
            }

            currentFocus
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(inputItemQuantity.windowToken, 0)
            inputItemQuantity.requestFocus()

            spinnerItemName.tag = 1000 + i
            inputItemQuantity.tag = 2000 + i
            buttonRemoveLayout.tag = 3000 + i

            spinnerItemName.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View,
                    spinnerPosition: Int,
                    id: Long
                ) {
                    val tag = spinnerItemName.tag as Int - 1000
                    if (spinnerPosition != 0) {
                        list[tag].isItemSelectFlag = true
                        list[tag].spinnerSelectedIndex = spinnerPosition
                        list[tag].id =
                            list[tag].spinnerItemList?.get(spinnerPosition)!!.itemId!!
                        list[tag].itemName =
                            list[tag].spinnerItemList?.get(spinnerPosition)!!.itemName!!
                    } else {
                        list[tag].isItemSelectFlag = false
                        list[tag].spinnerSelectedIndex = spinnerPosition
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                }
            }

            inputItemQuantity.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                }

                override fun afterTextChanged(s: Editable) {
                    val tag = inputItemQuantity.tag as Int - 2000
                    if (s.toString() != "") {
                        list[tag]!!.qty = s.toString()
                        list[tag]!!.isItemSelectFlag = true
                    } else {
                        list[tag]!!.qty = s.toString()
                        if (list[tag]!!.qty == "") list[tag]!!.isItemSelectFlag = false
                    }
                }
            })

            buttonRemoveLayout.setOnClickListener { view: View? ->
                val tag = buttonRemoveLayout.tag as Int - 3000
                if (list.size > 1) {
                    val spinnerPosition = list[tag]!!.spinnerSelectedIndex
                    list[tag]!!.spinnerItemList!!.get(spinnerPosition)!!.isVisibleItemFlag = false
                    list.removeAt(tag)
                    setUpPartsList()
                }
            }
            binding!!.partsListLay.addView(itemView)
        }
    }

    private fun addPartsInStock() {
        if (isNetworkAvailable(mContext!!)) {
            val jsonArray = JSONArray()
            for (model in list!!) {
                try {
                    val jsonObject = JSONObject()
                    jsonObject.put("itemId", model.id)
                    jsonObject.put("item_Qty", model.qty)
                    jsonArray.put(jsonObject)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            if (jsonArray.length() > 0) {
                val jsonArrBody = RequestBody.create(
                    "application/json; charset=utf-8".toMediaTypeOrNull(),
                    jsonArray.toString()
                )
                val service = RetrofitInstance.getRetrofitInstance().create(
                    APIService::class.java
                )
                showProgress()
                val call =
                    service.updatePartsStock("0", prefManager!!.useR_Id, "1", "", jsonArrBody)
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
                                        val jsonObject = JSONObject(responseStr)
                                        val status = jsonObject.optString("status")
                                        val message = jsonObject.optString("message")
                                        if (status == "200") {
//                                            makeToast(getResources().getString(R.string.stock_added_successfully));
                                            Constants.modelAddStock = list
                                            startActivity(
                                                Intent(mContext, AddStockPdfActivity::class.java)
                                                    .putExtra("name", prefManager!!.useR_NAME)
                                                    .putExtra("email", prefManager!!.useR_EMAIL)
                                            )
                                            finish()
                                        } else makeToast(message)
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
            } else {
                makeToast(resources.getString(R.string.please_select_item_or_quantity))
            }
        } else {
            makeToast(resources.getString(R.string.no_internet_connection))
        }
    }

    private fun addItem() {
        if (list!!.size > 0) {
            val lastIndex = list.size - 1
            if (list[lastIndex].qty != "" && list[lastIndex].spinnerSelectedIndex != 0) {
                for (i in list.indices) {
                    val spinnerPosition = list[i].spinnerSelectedIndex
                    list[i].id = list[i].spinnerItemList?.get(spinnerPosition)?.itemId!!
                    list[i].itemName = list[i].spinnerItemList?.get(spinnerPosition)?.itemName!!
                    list[i].spinnerItemList?.get(spinnerPosition)!!.isVisibleItemFlag = true
                    list[i].isFlag = true
                }
                list.add(
                    ModelAddStock(
                        "",
                        "",
                        "",
                        Constants.modelProductLists as List<ModelPartsList?>?
                    )
                )
                setUpPartsList()
            } else makeToast(resources.getString(R.string.please_select_item_or_quantity))
        }
    }

    private fun dialogAddStockList(list: List<ModelAddStock>) {
        try {
            val dialog = Dialog(mContext!!)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setContentView(R.layout.dialog_add_stock_list)
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.setCanceledOnTouchOutside(false)

            val rvStockList = dialog.findViewById<RecyclerView>(R.id.rv_stock_list)
            val textCancel = dialog.findViewById<TextView>(R.id.textCancel)
            val textConfirm = dialog.findViewById<TextView>(R.id.textConfirm)

            rvStockList.layoutManager =
                LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false)
            rvStockList.adapter = DialogAddStockListAdapter(mContext!!, list)

            textCancel.setOnClickListener { view: View? -> dialog.dismiss() }

            textConfirm.setOnClickListener { view: View? ->
                dialog.dismiss()
                addPartsInStock()
            }

            dialog.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onClick(view: View) {
        val id = view.id
        if (id == R.id.iv_back) {
            onBackPressed()
        } else if (id == R.id.buttonAddLayout) {
            addItem()
        } else if (id == R.id.buttonAddStock) {
            if (list!!.size > 0) {
                val lastIndex = list.size - 1
                if (list[lastIndex].qty != "" && list[lastIndex].spinnerSelectedIndex != 0) {
                    dialogAddStockList(list)
                } else Toast.makeText(
                    mContext,
                    resources.getString(R.string.please_select_item_or_quantity),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}