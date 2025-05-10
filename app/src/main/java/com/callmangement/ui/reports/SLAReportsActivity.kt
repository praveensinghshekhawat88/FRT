package com.callmangement.ui.reports

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.callmangement.R
import com.callmangement.adapter.SLAReportsActivityAdapter
import com.callmangement.custom.CustomActivity
import com.callmangement.databinding.ActivitySlaReportsBinding
import com.callmangement.model.reports.Days_Reports_Info
import com.callmangement.model.reports.ModelSLAReport
import com.callmangement.model.reports.Monthly_Reports_Info
import com.callmangement.model.reports.SLA_Reports_Info
import com.callmangement.report_pdf.SLAReportPdfActivity
import com.callmangement.ui.complaint.ComplaintViewModel
import com.callmangement.utils.EqualSpacingItemDecoration
import com.callmangement.utils.PrefManager
import java.io.Serializable
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.Objects

class SLAReportsActivity : CustomActivity() {

    
    private var binding: ActivitySlaReportsBinding? = null
    private var sla_reports_infoArrayList: MutableList<SLA_Reports_Info>? = ArrayList()
    private var prefManager: PrefManager? = null
    private var adapter: SLAReportsActivityAdapter? = null
    private val myFormat = "yyyy-MM-dd" //In which you need put here
    private var viewModel: ComplaintViewModel? = null

    private val daysList: MutableList<Days_Reports_Info> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sla_reports)

        mContext = this
        prefManager = PrefManager(mContext!!)
        binding!!.actionBar.ivBack.visibility = View.VISIBLE
        binding!!.actionBar.ivThreeDot.visibility = View.GONE
        binding!!.actionBar.layoutLanguage.visibility = View.GONE
        binding!!.actionBar.buttonPDF.visibility = View.VISIBLE
        binding!!.actionBar.textToolbarTitle.text = resources.getString(R.string.sla_reports)
        viewModel = ViewModelProviders.of(this).get(
            ComplaintViewModel::class.java
        )
        initView()

        val day = 3
        val fromDate = ""
        val toDate = ""
        var districtId = "0"

        if (prefManager!!.useR_TYPE_ID == "4" && prefManager!!.useR_TYPE.equals(
                "ServiceEngineer",
                ignoreCase = true
            )
        ) {
            districtId = prefManager!!.useR_DistrictId!!
        } else if (prefManager!!.useR_TYPE_ID.equals(
                "6",
                ignoreCase = true
            ) && prefManager!!.useR_TYPE.equals("DSO", ignoreCase = true)
        ) {
            districtId = prefManager!!.useR_DistrictId!!
        }

        fetchSLAReportList(fromDate, toDate, day, districtId)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun initView() {
        adapter = SLAReportsActivityAdapter(this@SLAReportsActivity)
        adapter!!.notifyDataSetChanged()
        binding!!.rvSlaReports.setHasFixedSize(true)
        binding!!.rvSlaReports.layoutManager =
            LinearLayoutManager(this@SLAReportsActivity, LinearLayoutManager.VERTICAL, false)
        binding!!.rvSlaReports.addItemDecoration(
            EqualSpacingItemDecoration(
                30,
                EqualSpacingItemDecoration.VERTICAL
            )
        )
        binding!!.rvSlaReports.adapter = adapter

        binding!!.actionBar.buttonPDF.setOnClickListener { view: View? ->
            if (sla_reports_infoArrayList != null && sla_reports_infoArrayList!!.size > 0) {
                startActivity(
                    Intent(mContext!!, SLAReportPdfActivity::class.java)
                        .putExtra("param", sla_reports_infoArrayList as Serializable?)
                        .putExtra("ResolveInDays", adapter!!.days)
                        .putExtra("title", "SLA SUMMARY REPORT")
                        .putExtra("district", prefManager!!.useR_District)
                        .putExtra("name", prefManager!!.useR_NAME)
                        .putExtra("email", prefManager!!.useR_EMAIL)
                )
            } else {
                Toast.makeText(
                    mContext!!,
                    resources.getString(R.string.no_record_found_to_export_pdf),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        for (i in 3..30) {
            val days_reports_info = Days_Reports_Info()
            days_reports_info.day = "$i Days"
            days_reports_info.days_count = i
            daysList.add(days_reports_info)
        }

        if (daysList.size > 0) {
            val dataAdapter = ArrayAdapter(mContext!!, R.layout.spinner_item, daysList)
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding!!.spinnerDays.adapter = dataAdapter
        }

        binding!!.spinnerDays.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>?, view: View, i: Int, l: Long) {
                val day = daysList[i].days_count
                val fromDate = ""
                val toDate = ""
                var districtId = "0"
                if (prefManager!!.useR_TYPE_ID == "4" && prefManager!!.useR_TYPE.equals(
                        "ServiceEngineer",
                        ignoreCase = true
                    )
                ) {
                    districtId = prefManager!!.useR_DistrictId!!
                } else if (prefManager!!.useR_TYPE_ID.equals(
                        "6",
                        ignoreCase = true
                    ) && prefManager!!.useR_TYPE.equals("DSO", ignoreCase = true)
                ) {
                    districtId = prefManager!!.useR_DistrictId!!
                }
                fetchSLAReportDayWise(fromDate, toDate, day, districtId)
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {
            }
        }

        binding!!.actionBar.ivBack.setOnClickListener { view: View? -> onBackPressed() }
    }

    private fun fetchSLAReportList(
        fromDate: String,
        toDate: String,
        ResolveInDays: Int,
        districtId: String
    ) {
        sla_reports_infoArrayList!!.clear()
        isLoading
        viewModel!!.getSLAReportList(
            prefManager!!.useR_Id.toString(),
            fromDate,
            toDate,
            ResolveInDays,
            districtId
        ).observe(
            this
        ) { modelSLAReport: ModelSLAReport? ->
            isLoading
            if (modelSLAReport!!.status == "200") {
                sla_reports_infoArrayList = modelSLAReport.sla_reports_infos
                if (sla_reports_infoArrayList!!.size != 0) {
                    binding!!.rvSlaReports.visibility = View.VISIBLE
                    binding!!.textNoComplaint.visibility = View.GONE
                    adapter!!.days = ResolveInDays
                    adapter!!.setData(sla_reports_infoArrayList)
                } else {
                    binding!!.rvSlaReports.visibility = View.GONE
                    binding!!.textNoComplaint.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun fetchSLAReportDayWise(
        fromDate: String,
        toDate: String,
        ResolveInDays: Int,
        districtId: String
    ) {
        sla_reports_infoArrayList!!.clear()
        isLoading
        viewModel!!.getSLAReportList(
            prefManager!!.useR_Id.toString(),
            fromDate,
            toDate,
            ResolveInDays,
            districtId
        ).observe(
            this
        ) { modelSLAReport: ModelSLAReport? ->
            isLoading
            if (modelSLAReport!!.status == "200") {
                sla_reports_infoArrayList = modelSLAReport.sla_reports_infos
                if (sla_reports_infoArrayList!!.size != 0) {
                    binding!!.rvSlaReports.visibility = View.VISIBLE
                    binding!!.textNoComplaint.visibility = View.GONE
                    adapter!!.days = ResolveInDays
                    adapter!!.setData(sla_reports_infoArrayList)
                } else {
                    binding!!.rvSlaReports.visibility = View.GONE
                    binding!!.textNoComplaint.visibility = View.VISIBLE
                }
            }
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

    inner class CustomComparator : Comparator<Monthly_Reports_Info> {
        override fun compare(o1: Monthly_Reports_Info, o2: Monthly_Reports_Info): Int {
            return o1.date!!.compareTo(o2.date!!)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }

    companion object {
        private fun getDates(dateString1: String, dateString2: String): List<String> {
            val sdf = SimpleDateFormat("dd-MM-yyyy", Locale.US)

            val dates = ArrayList<String>()
            @SuppressLint("SimpleDateFormat") val df1: DateFormat = SimpleDateFormat("yyyy-MM-dd")

            var date1: Date? = null
            var date2: Date? = null

            try {
                date1 = df1.parse(dateString1)
                date2 = df1.parse(dateString2)
            } catch (e: ParseException) {
                e.printStackTrace()
            }

            val cal1 = Calendar.getInstance()
            cal1.time = Objects.requireNonNull(date1)


            val cal2 = Calendar.getInstance()
            cal2.time = Objects.requireNonNull(date2)

            while (!cal1.after(cal2)) {
                val newDate = sdf.format(cal1.time)
                dates.add(newDate)
                cal1.add(Calendar.DATE, 1)
            }
            return dates
        }
    }
}