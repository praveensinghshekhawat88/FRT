package com.callmangement.ui.inventory

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.callmangement.R
import com.callmangement.adapter.AvailableStockListForSEActivityAdapter
import com.callmangement.custom.CustomActivity
import com.callmangement.databinding.ActivityAvailableStockListForSeactivityBinding
import com.callmangement.model.inventrory.ModelParts
import com.callmangement.model.inventrory.ModelPartsList
import com.callmangement.utils.Constants.isNetworkAvailable
import com.callmangement.utils.PrefManager
import java.util.Locale

class AvailableStockListForSEActivity : CustomActivity(), View.OnClickListener {
    private var binding: ActivityAvailableStockListForSeactivityBinding? = null
    private var adapter: AvailableStockListForSEActivityAdapter? = null
    private var modelPartsList: List<ModelPartsList?> = ArrayList()
    private var prefManager: PrefManager? = null
    private var viewModel: InventoryViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAvailableStockListForSeactivityBinding.inflate(
            layoutInflater
        )
        setContentView(binding!!.root)
        binding!!.actionBar.ivBack.visibility = View.VISIBLE
        binding!!.actionBar.ivThreeDot.visibility = View.GONE
        binding!!.actionBar.layoutLanguage.visibility = View.GONE
        binding!!.actionBar.textToolbarTitle.text = resources.getString(R.string.available_stock)
        prefManager = PrefManager(mContext!!)
        viewModel = ViewModelProviders.of(this).get(
            InventoryViewModel::class.java
        )
        initView()
    }

    private fun initView() {
        setUpOnClickListener()
        availableStockList
    }

    private fun setUpOnClickListener() {
        binding!!.inputSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
            }

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                if (!charSequence.toString().isEmpty()) {
                    val filterList: MutableList<ModelPartsList?> = ArrayList()
                    if (modelPartsList.size > 0) {
                        for (model in modelPartsList) {
                            if (model!!.itemName!!.lowercase(Locale.getDefault())
                                    .contains(charSequence.toString())
                            ) filterList.add(model)
                        }
                    }
                    setUpAvailableStockAdapter(filterList)
                } else setUpAvailableStockAdapter(modelPartsList)
            }

            override fun afterTextChanged(editable: Editable) {
            }
        })
        binding!!.actionBar.ivBack.setOnClickListener(this)
    }

    private val availableStockList: Unit
        get() {
            if (isNetworkAvailable(mContext!!)) {
                isLoading
                viewModel!!.getAvailableStockListForSE(prefManager!!.useR_Id, "0").observe(
                    this
                ) { modelParts: ModelParts? ->
                    isLoading
                    if (modelParts!!.status == "200") {
                        modelPartsList = modelParts.parts
                        if (modelPartsList.size > 0) {
                            setUpAvailableStockAdapter(modelPartsList)
                        } else {
                            binding!!.rvAvailableStock.visibility = View.GONE
                            binding!!.textNoStockFound.visibility = View.VISIBLE
                        }
                    }
                }
            } else {
                makeToast(resources.getString(R.string.no_internet_connection))
            }
        }

    private fun setUpAvailableStockAdapter(modelPartsList: List<ModelPartsList?>) {
        if (modelPartsList.size > 0) {
            binding!!.rvAvailableStock.visibility = View.VISIBLE
            binding!!.textNoStockFound.visibility = View.GONE
            var quantity = 0
            for (model in modelPartsList) {
                quantity = quantity + model!!.item_Qty!!.toInt()
            }
            binding!!.textTotalItemQuantity.text = quantity.toString()
            binding!!.rvAvailableStock.layoutManager =
                LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false)
            adapter = AvailableStockListForSEActivityAdapter(mContext!!, modelPartsList)
            binding!!.rvAvailableStock.adapter = adapter
        } else {
            binding!!.rvAvailableStock.visibility = View.GONE
            binding!!.textNoStockFound.visibility = View.VISIBLE
            binding!!.textTotalItemQuantity.text = "0"
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
        }
    }
}