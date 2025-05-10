package com.callmangement.ui.reports

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.callmangement.R
import com.callmangement.adapter.SLAReportsDetailsActivityAdapter
import com.callmangement.custom.CustomActivity
import com.callmangement.databinding.ActivitySlaReportsDetailsBinding
import com.callmangement.model.reports.ModelSLAReportDetails
import com.callmangement.model.reports.ModelSLAReportDetailsList
import com.callmangement.ui.complaint.ComplaintViewModel
import com.callmangement.utils.EqualSpacingItemDecoration
import com.callmangement.utils.PrefManager
import java.util.Calendar

class SLAReportsDetailsActivity : CustomActivity() {

    
    var binding: ActivitySlaReportsDetailsBinding? = null
    private var adapter: SLAReportsDetailsActivityAdapter? = null
    private var prefManager: PrefManager? = null
    private var complaintList: MutableList<ModelSLAReportDetailsList>? = ArrayList()
    private val myFormat = "yyyy-MM-dd"
    private val myCalendarFromDate: Calendar = Calendar.getInstance()
    private val myCalendarToDate: Calendar = Calendar.getInstance()
    private var viewModel: ComplaintViewModel? = null
    private var districtId = 0
    private var intervalDays = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sla_reports_details)

        mContext = this
        prefManager = PrefManager(mContext!!)
        binding!!.actionBar.ivBack.visibility = View.VISIBLE
        binding!!.actionBar.ivThreeDot.visibility = View.GONE
        binding!!.actionBar.layoutLanguage.visibility = View.GONE
        binding!!.actionBar.buttonPDF.visibility = View.GONE
        binding!!.actionBar.textToolbarTitle.text = resources.getString(R.string.reports_details)
        viewModel = ViewModelProviders.of(this).get(
            ComplaintViewModel::class.java
        )
        districtId = intent.getIntExtra("districtId", 0)
        intervalDays = intent.getIntExtra("intervalDays", 0)
        initView()
        val fromDate = ""
        val toDate = ""
        fetchSLAReportDetails(fromDate, toDate, intervalDays, "" + districtId)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun initView() {
        adapter = SLAReportsDetailsActivityAdapter(this@SLAReportsDetailsActivity)
        adapter!!.notifyDataSetChanged()
        binding!!.rvComplaints.setHasFixedSize(true)
        binding!!.rvComplaints.layoutManager =
            LinearLayoutManager(this@SLAReportsDetailsActivity, LinearLayoutManager.VERTICAL, false)
        binding!!.rvComplaints.addItemDecoration(
            EqualSpacingItemDecoration(
                30,
                EqualSpacingItemDecoration.VERTICAL
            )
        )
        binding!!.rvComplaints.adapter = adapter
        binding!!.inputSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
            }

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                adapter!!.filter.filter(charSequence)
            }

            override fun afterTextChanged(editable: Editable) {
            }
        })

        binding!!.actionBar.ivBack.setOnClickListener { view: View? -> onBackPressed() }
    }

    @SuppressLint("SetTextI18n")
    private fun fetchSLAReportDetails(
        fromDate: String,
        toDate: String,
        day: Int,
        districtId: String
    ) {
        try {
            complaintList!!.clear()
            isLoading
            viewModel!!.getSLAReportDetails(
                prefManager!!.useR_Id.toString(),
                fromDate,
                toDate,
                day,
                districtId
            ).observe(
                this
            ) { modelComplaint: ModelSLAReportDetails? ->
                isLoading
                if (modelComplaint!!.status == "200") {
                    complaintList = modelComplaint.sla_reports_infos
                    if (complaintList != null && complaintList!!.size > 0) {
                        binding!!.rvComplaints.visibility = View.VISIBLE
                        binding!!.textNoComplaint.visibility = View.GONE
                        binding!!.textTotalComplaint.text = "" + complaintList!!.size
                        adapter!!.setData(complaintList!!)
                    } else {
                        binding!!.rvComplaints.visibility = View.GONE
                        binding!!.bottomLayout.visibility = View.GONE
                        binding!!.textNoComplaint.visibility = View.VISIBLE
                    }
                }
            }
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
}