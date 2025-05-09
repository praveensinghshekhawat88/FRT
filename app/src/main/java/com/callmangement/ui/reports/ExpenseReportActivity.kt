package com.callmangement.ui.reports

import android.content.Context
import android.os.Bundle
import android.view.View
import com.callmangement.R
import com.callmangement.custom.CustomActivity
import com.callmangement.databinding.ActivityExpenseReportBinding
import com.callmangement.utils.PrefManager

class ExpenseReportActivity : CustomActivity(), View.OnClickListener {

    
    private var binding: ActivityExpenseReportBinding? = null
    private var prefManager: PrefManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExpenseReportBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        initView()
    }

    private fun initView() {
        mContext = this
        prefManager = PrefManager(mContext!!)
        binding!!.actionBar.ivBack.visibility = View.VISIBLE
        binding!!.actionBar.ivThreeDot.visibility = View.GONE
        binding!!.actionBar.layoutLanguage.visibility = View.GONE
        binding!!.actionBar.textToolbarTitle.text = resources.getString(R.string.expense_report)
        setUpOnclickListener()
    }

    private fun setUpOnclickListener() {
        binding!!.buttonUploadExpense.setOnClickListener(this)
        binding!!.buttonViewExpense.setOnClickListener(this)
        binding!!.actionBar.ivBack.setOnClickListener(this)
    }

    override fun onClick(view: View) {
        val id = view.id
        if (id == R.id.buttonUploadExpense) {
            startActivity(UploadExpenseActivity::class.java)
        } else if (id == R.id.buttonViewExpense) {
            startActivity(ViewExpenseActivity::class.java)
        } else if (id == R.id.iv_back) {
            onBackPressed()
        }
    }
}