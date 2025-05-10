package com.callmangement.ui.inventory

import android.os.Bundle
import android.view.View
import com.callmangement.R
import com.callmangement.custom.CustomActivity
import com.callmangement.databinding.ActivityInventoryBinding
import com.callmangement.utils.PrefManager

class InventoryActivity : CustomActivity(), View.OnClickListener {
    private var binding: ActivityInventoryBinding? = null
    private var prefManager: PrefManager? = null
    private val fromWhere = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInventoryBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        binding!!.actionBar.ivBack.visibility = View.VISIBLE
        binding!!.actionBar.ivThreeDot.visibility = View.GONE
        binding!!.actionBar.layoutLanguage.visibility = View.GONE
        binding!!.actionBar.textToolbarTitle.text = resources.getString(R.string.inventory)
        prefManager = PrefManager(mContext!!)
        initView()
    }

    private fun initView() {
        if (prefManager!!.useR_TYPE_ID == "4" && prefManager!!.useR_TYPE.equals(
                "ServiceEngineer",
                ignoreCase = true
            )
        ) {
            binding!!.buttonCreateNewChallanforDispatch.visibility = View.GONE
            binding!!.buttonCurrentStockManager.visibility = View.GONE
            binding!!.buttonSEAvailableStockManager.visibility = View.GONE
            binding!!.buttonAddStockManager.visibility = View.GONE
            binding!!.buttonCreateDispatchStock.visibility = View.GONE
            binding!!.buttonShiftingSEStockInMainStock.visibility = View.GONE
            binding!!.buttonAvailableStockForSE.visibility = View.VISIBLE
            binding!!.buttonReceiveMaterial.visibility = View.VISIBLE
        } else if (prefManager!!.useR_TYPE_ID == "5" && prefManager!!.useR_TYPE.equals(
                "ServiceCentre",
                ignoreCase = true
            )
        ) {
            binding!!.buttonCreateNewChallanforDispatch.visibility = View.GONE
            binding!!.buttonCurrentStockManager.visibility = View.GONE
            binding!!.buttonSEAvailableStockManager.visibility = View.GONE
            binding!!.buttonAddStockManager.visibility = View.GONE
            binding!!.buttonCreateDispatchStock.visibility = View.GONE
            binding!!.buttonShiftingSEStockInMainStock.visibility = View.GONE
            binding!!.buttonAvailableStockForSE.visibility = View.VISIBLE
            binding!!.buttonReceiveMaterial.visibility = View.VISIBLE
        } else {
            binding!!.buttonCreateNewChallanforDispatch.visibility = View.VISIBLE
            binding!!.buttonCurrentStockManager.visibility = View.VISIBLE
            binding!!.buttonSEAvailableStockManager.visibility = View.VISIBLE
            binding!!.buttonAddStockManager.visibility = View.VISIBLE
            binding!!.buttonCreateDispatchStock.visibility = View.VISIBLE
            binding!!.buttonShiftingSEStockInMainStock.visibility = View.VISIBLE
            binding!!.buttonAvailableStockForSE.visibility = View.GONE
            binding!!.buttonReceiveMaterial.visibility = View.GONE
        }
        setUpOnclickListener()
    }

    private fun setUpOnclickListener() {
        binding!!.actionBar.ivBack.setOnClickListener(this)
        binding!!.buttonCurrentStockManager.setOnClickListener(this)
        binding!!.buttonReceiveMaterial.setOnClickListener(this)
        binding!!.buttonCreateNewChallanforDispatch.setOnClickListener(this)
        binding!!.buttonSEAvailableStockManager.setOnClickListener(this)
        binding!!.buttonAvailableStockForSE.setOnClickListener(this)
        binding!!.buttonAddStockManager.setOnClickListener(this)
        binding!!.buttonCreateDispatchStock.setOnClickListener(this)
        binding!!.buttonShiftingSEStockInMainStock.setOnClickListener(this)
    }

    override fun onClick(view: View) {
        val id = view.id
        if (id == R.id.buttonCurrentStockManager) {
            startActivity(TotalStockListForManagerActivity::class.java)
        } else if (id == R.id.buttonReceiveMaterial) {
            startActivity(ReceiveMaterialListActivity::class.java)
        } else if (id == R.id.buttonCreateNewChallanforDispatch) {
            startActivity(DispatchChallanListActivity::class.java)
        } else if (id == R.id.buttonSEAvailableStockManager) {
            startActivity(SEAvailableStockManagerActivity::class.java)
        } else if (id == R.id.buttonAvailableStockForSE) {
            startActivity(AvailableStockListForSEActivity::class.java)
        } else if (id == R.id.buttonAddStockManager) {
            startActivity(AddStockActivity::class.java)
        } else if (id == R.id.buttonCreateDispatchStock) {
            startActivity(CreateNewChallanforDispatchActivity::class.java)
        } else if (id == R.id.buttonShiftingSEStockInMainStock) {
            startActivity(ShiftingStockActivity::class.java)
        } else if (id == R.id.iv_back) {
            onBackPressed()
        }
    }
}