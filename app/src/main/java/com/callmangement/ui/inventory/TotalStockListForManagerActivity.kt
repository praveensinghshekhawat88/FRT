package com.callmangement.ui.inventory

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.callmangement.R
import com.callmangement.adapter.TotalStockListForManagerActivityAdapter
import com.callmangement.custom.CustomActivity
import com.callmangement.databinding.ActivityTotalStockListForManagerBinding
import com.callmangement.model.inventrory.ModelParts
import com.callmangement.model.inventrory.ModelPartsList
import com.callmangement.utils.Constants.isNetworkAvailable
import com.callmangement.utils.PrefManager
import java.util.Locale

class TotalStockListForManagerActivity : CustomActivity(), View.OnClickListener {
    private var binding: ActivityTotalStockListForManagerBinding? = null
    private var adapter: TotalStockListForManagerActivityAdapter? = null
    private var viewModel: InventoryViewModel? = null
    private var modelPartsList: List<ModelPartsList?> = ArrayList()
    private var prefManager: PrefManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTotalStockListForManagerBinding.inflate(
            layoutInflater
        )
        setContentView(binding!!.root)
        binding!!.actionBar.ivBack.visibility = View.VISIBLE
        binding!!.actionBar.ivThreeDot.visibility = View.GONE
        binding!!.actionBar.layoutLanguage.visibility = View.GONE
        binding!!.actionBar.textToolbarTitle.text = resources.getString(R.string.current_stock)
        prefManager = PrefManager(mContext!!)
        viewModel = ViewModelProviders.of(this).get(
            InventoryViewModel::class.java
        )
        initView()
    }

    private fun initView() {
        setUpOnClickListener()
        partsCurrentStockList
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
                    setUpCurrentStockListAdapter(filterList)
                } else setUpCurrentStockListAdapter(modelPartsList)
            }

            override fun afterTextChanged(editable: Editable) {
            }
        })
        binding!!.actionBar.ivBack.setOnClickListener(this)
    }

    private val partsCurrentStockList: Unit
        get() {
            if (isNetworkAvailable(mContext!!)) {
                isLoading
                viewModel!!.getPartsCurrentStockList("0", "0")
                    .observe(this, object : Observer<ModelParts?> {
                        override fun onChanged(modelParts: ModelParts?) {
                            isLoading
                            if (modelParts!!.status == "200") {
                                modelPartsList = modelParts.parts
                                if (modelPartsList.size > 0) {
                                    setUpCurrentStockListAdapter(modelPartsList)
                                } else {
                                    binding!!.rvCurrentStock.visibility = View.GONE
                                    binding!!.textNoStockFound.visibility = View.VISIBLE
                                }
                            }
                        }
                    })
            } else {
                makeToast(resources.getString(R.string.no_internet_connection))
            }
        }

    private fun setUpCurrentStockListAdapter(modelPartsList: List<ModelPartsList?>) {
        if (modelPartsList.size > 0) {
            var quantity = 0
            for (model in modelPartsList) {
                quantity = quantity + model!!.item_Qty!!.toInt()
            }
            binding!!.textTotalItemQuantity.text = quantity.toString()
            binding!!.rvCurrentStock.visibility = View.VISIBLE
            binding!!.textNoStockFound.visibility = View.GONE
            binding!!.rvCurrentStock.layoutManager =
                LinearLayoutManager(
                    this@TotalStockListForManagerActivity,
                    LinearLayoutManager.VERTICAL,
                    false
                )
            adapter = TotalStockListForManagerActivityAdapter(mContext!!, modelPartsList)
            binding!!.rvCurrentStock.adapter = adapter
        } else {
            binding!!.textTotalItemQuantity.text = "0"
            binding!!.rvCurrentStock.visibility = View.GONE
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


    override fun onClick(view: View) {
        val id = view.id
        if (id == R.id.iv_back) {
            onBackPressed()
        }
    }
}