package com.callmangement.ui.inventory

import android.app.Dialog
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
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.callmangement.R
import com.callmangement.adapter.DialogRequestStockListAdapter
import com.callmangement.custom.CustomActivity
import com.callmangement.custom.SpinnerAdapter
import com.callmangement.databinding.ActivityRequestStockBinding
import com.callmangement.model.inventrory.ModelParts
import com.callmangement.model.inventrory.ModelPartsList
import com.callmangement.model.inventrory.ModelRequestStock
import com.callmangement.utils.Constants
import com.callmangement.utils.Constants.isNetworkAvailable
import com.callmangement.utils.PrefManager
import org.json.JSONArray
import org.json.JSONObject
import java.util.Collections

class RequestStockActivity : CustomActivity(), View.OnClickListener {
    private var binding: ActivityRequestStockBinding? = null
    private var inventoryViewModel: InventoryViewModel? = null
    private var prefManager: PrefManager? = null
    private val list: MutableList<ModelRequestStock> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRequestStockBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        binding!!.actionBar.ivBack.visibility = View.VISIBLE
        binding!!.actionBar.ivThreeDot.visibility = View.GONE
        binding!!.actionBar.layoutLanguage.visibility = View.GONE
        binding!!.actionBar.textToolbarTitle.text = resources.getString(R.string.request_stock)
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
        binding!!.buttonRequestStock.setOnClickListener(this)
    }

    private val partsList: Unit
        get() {
            inventoryViewModel!!.partsList.observe(
                this,
                Observer<ModelParts?> { modelParts: ModelParts? ->
                    if (modelParts!!.status == "200") {
                        val modelProductsList = modelParts.parts
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
                })
        }

    private fun setUpPartsListAdapter() {
        list.clear()
        list.add(
            ModelRequestStock(
                "",
                "",
                "",
                Constants.modelProductLists as List<ModelPartsList>
            )
        )
        setUpPartsList()
    }

    private fun setUpPartsList() {
        binding!!.partsListLay.removeAllViews()

        for (i in list.indices) {
            val inflater = getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val itemView = inflater.inflate(R.layout.item_add_stock_activity, null, true)
            val topLay = itemView.findViewById<RelativeLayout>(R.id.rl_product)
            val spinnerItemName = itemView.findViewById<Spinner>(R.id.spinnerItemName)
            val tvSpinner = itemView.findViewById<TextView>(R.id.tv_spinner)
            val inputItemQuantity = itemView.findViewById<EditText>(R.id.inputItemQuantity)
            val buttonRemoveLayout = itemView.findViewById<ImageView>(R.id.buttonRemoveLayout)
            if (list[i].isFlag) spinnerItemName.visibility = View.GONE
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
                            list[tag].spinnerItemList[spinnerPosition].itemId!!
                        list[tag].itemName =
                            list[tag].spinnerItemList[spinnerPosition].itemName!!
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
                        list[tag].qty = s.toString()
                        list[tag].isItemSelectFlag = true
                    } else {
                        list[tag].qty = s.toString()
                        if (list[tag].qty == "") list[tag].isItemSelectFlag = false
                    }
                }
            })

            buttonRemoveLayout.setOnClickListener { view: View? ->
                val tag = buttonRemoveLayout.tag as Int - 3000
                if (list.size > 1) {
                    val spinnerPosition = list[tag].spinnerSelectedIndex
                    list[tag].spinnerItemList[spinnerPosition].isVisibleItemFlag = false
                    list.removeAt(tag)
                    setUpPartsList()
                }
            }

            binding!!.partsListLay.addView(itemView)
        }
    }

    private fun requestStock() {
        if (isNetworkAvailable(mContext!!)) {
            val jsonArray = JSONArray()
            for (model in list) {
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
                //    Log.e("jsonArray", String.valueOf(jsonArray));
            } else {
                makeToast(resources.getString(R.string.please_select_item_or_quantity))
            }
        } else {
            makeToast(resources.getString(R.string.no_internet_connection))
        }
    }

    private fun addItem() {
        val lastIndex = list.size - 1
        if (list[lastIndex].qty != "" && list[lastIndex].spinnerSelectedIndex != 0) {
            for (i in list.indices) {
                val spinnerPosition = list[i].spinnerSelectedIndex
                list[i].id = list[i].spinnerItemList[spinnerPosition].itemId!!
                list[i].itemName = list[i].spinnerItemList[spinnerPosition].itemName!!
                list[i].spinnerItemList[spinnerPosition].isVisibleItemFlag = true
                list[i].isFlag = true
            }
            list.add(
                ModelRequestStock(
                    "",
                    "",
                    "",
                    Constants.modelProductLists as List<ModelPartsList>
                )
            )
            setUpPartsList()
        } else Toast.makeText(
            mContext,
            resources.getString(R.string.please_select_item_or_quantity),
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun dialogConfirmationRequestStockList(list: List<ModelRequestStock>) {
        try {
            val dialog = Dialog(mContext!!)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setContentView(R.layout.dialog_request_stock_list)
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.setCanceledOnTouchOutside(false)

            val rvStockList = dialog.findViewById<RecyclerView>(R.id.rv_stock_list)
            val textCancel = dialog.findViewById<TextView>(R.id.textCancel)
            val textConfirm = dialog.findViewById<TextView>(R.id.textConfirm)

            rvStockList.layoutManager =
                LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false)
            rvStockList.adapter = DialogRequestStockListAdapter(mContext!!, list)

            textCancel.setOnClickListener { view: View? -> dialog.dismiss() }

            textConfirm.setOnClickListener { view: View? ->
                dialog.dismiss()
                requestStock()
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
        } else if (id == R.id.buttonRequestStock) {
            val lastIndex = list.size - 1
            if (list[lastIndex].qty != "" && list[lastIndex].spinnerSelectedIndex != 0) {
                dialogConfirmationRequestStockList(list)
            } else Toast.makeText(
                mContext,
                resources.getString(R.string.please_select_item_or_quantity),
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}