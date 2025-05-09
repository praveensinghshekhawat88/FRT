package com.callmangement.ui.reports

import android.content.Context
import android.os.Bundle
import android.view.View
import com.callmangement.R
import com.callmangement.custom.CustomActivity
import com.callmangement.databinding.ActivityReportsBinding
import com.callmangement.ui.reports.GraphActivity
import com.callmangement.ui.reports.MonthlyReportsActivity
import com.callmangement.utils.PrefManager

class ReportsActivity : CustomActivity(), View.OnClickListener {

    
    private var binding: ActivityReportsBinding? = null
    private var prefManager: PrefManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReportsBinding.inflate(layoutInflater)
        setContentView(binding!!.root)


        mContext = this
        binding!!.actionBar.ivBack.visibility = View.VISIBLE
        binding!!.actionBar.ivThreeDot.visibility = View.GONE
        binding!!.actionBar.layoutLanguage.visibility = View.GONE
        binding!!.actionBar.textToolbarTitle.text = resources.getString(R.string.reports)
        prefManager = PrefManager(mContext!!)
        initView()
    }

    private fun initView() {
        if (prefManager!!.useR_TYPE_ID == "1" && prefManager!!.useR_TYPE.equals(
                "Admin",
                ignoreCase = true
            )
        ) {
            binding!!.buttonExpenseReport.visibility = View.GONE
            binding!!.buttonSEExpenseReport.visibility = View.VISIBLE
        } else if (prefManager!!.useR_TYPE_ID == "2" && prefManager!!.useR_TYPE.equals(
                "Manager",
                ignoreCase = true
            )
        ) {
            binding!!.buttonExpenseReport.visibility = View.GONE
            binding!!.buttonSEExpenseReport.visibility = View.VISIBLE
        } else if (prefManager!!.useR_TYPE_ID == "4" && prefManager!!.useR_TYPE.equals(
                "ServiceEngineer",
                ignoreCase = true
            )
        ) {
            binding!!.buttonExpenseReport.visibility = View.VISIBLE
            binding!!.buttonSEExpenseReport.visibility = View.GONE
        } else {
            binding!!.buttonExpenseReport.visibility = View.GONE
            binding!!.buttonSEExpenseReport.visibility = View.GONE
        }

        setUpOnclickListener()
    }

    private fun setUpOnclickListener() {
        binding!!.buttonDailyReports.setOnClickListener(this)
        binding!!.buttonMonthlyReports.setOnClickListener(this)
        binding!!.buttonSLAReports.setOnClickListener(this)
        binding!!.buttonGraph.setOnClickListener(this)
        binding!!.buttonFpsRepeatOnServiceCenter.setOnClickListener(this)
        binding!!.buttonExpenseReport.setOnClickListener(this)
        binding!!.buttonSEExpenseReport.setOnClickListener(this)
        binding!!.actionBar.ivBack.setOnClickListener(this)
    }

    override fun onClick(view: View) {
        val id = view.id
        if (id == R.id.buttonDailyReports) {
            startActivity(DailyReportsActivity::class.java)
        } else if (id == R.id.buttonMonthlyReports) {
            startActivity(MonthlyReportsActivity::class.java)
        } else if (id == R.id.buttonSLAReports) {
            startActivity(SLAReportsActivity::class.java)
        } else if (id == R.id.buttonGraph) {
            startActivity(GraphActivity::class.java)
        } else if (id == R.id.buttonFpsRepeatOnServiceCenter) {
            startActivity(FPSRepeatOnServiceCenterActivity::class.java)
        } else if (id == R.id.buttonExpenseReport) {
            startActivity(ExpenseReportActivity::class.java)
        } else if (id == R.id.buttonSEExpenseReport) {
            startActivity(ViewExpenseActivity::class.java)
        } else if (id == R.id.iv_back) {
            onBackPressed()
        }
    }
}