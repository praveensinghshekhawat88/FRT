package com.callmangement.ui.reports

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.callmangement.R
import com.callmangement.adapter.MonthlyReportsActivityAdapter
import com.callmangement.custom.CustomActivity
import com.callmangement.databinding.ActivityMonthlyReportsBinding
import com.callmangement.model.complaints.ModelComplaint
import com.callmangement.model.complaints.ModelComplaintList
import com.callmangement.model.district.ModelDistrict
import com.callmangement.model.district.ModelDistrictList
import com.callmangement.model.reports.MonthDateModel
import com.callmangement.model.reports.MonthReportModel
import com.callmangement.model.reports.Monthly_Reports_Info
import com.callmangement.report_pdf.ReportPdfActivity
import com.callmangement.support.rackmonthpicker.RackMonthPicker
import com.callmangement.ui.complaint.ComplaintViewModel
import com.callmangement.utils.Constants
import com.callmangement.utils.EqualSpacingItemDecoration
import com.callmangement.utils.PrefManager
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Collections
import java.util.Date
import java.util.Locale

class MonthlyReportsActivity : CustomActivity() {
    private var binding: ActivityMonthlyReportsBinding? = null
    private var complaintList: List<ModelComplaintList>? = null
    private var prefManager: PrefManager? = null
    private var adapter: MonthlyReportsActivityAdapter? = null
    private val myFormat = "yyyy-MM-dd"
    private var viewModel: ComplaintViewModel? = null
    private val objectList: MutableList<Any> = ArrayList()
    private val complainStatusId = "0"
    private var districtId = "0"
    private var checkDistrict = 0
    private var districtNameEng = ""
    private var district_List: MutableList<ModelDistrictList?>? = ArrayList()
    private var fromDate: String? = null
    private var toDate: String? = null
    private var isLoading = false
    private var allPagesLoaded = false
    private var currentPage = 1
    private val PAGE_SIZE = 50

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_monthly_reports)
        prefManager = PrefManager(mContext)
        binding!!.actionBar.ivBack.visibility = View.VISIBLE
        binding!!.actionBar.ivThreeDot.visibility = View.GONE
        binding!!.actionBar.layoutLanguage.visibility = View.GONE
        binding!!.actionBar.buttonPDF.visibility = View.VISIBLE
        binding!!.actionBar.buttonEXCEL.visibility = View.GONE

        binding!!.actionBar.textToolbarTitle.text = resources.getString(R.string.monthly_reports)
        viewModel = ViewModelProviders.of(this).get(
            ComplaintViewModel::class.java
        )
        districtNameEng = prefManager!!.useR_District!!
        if (prefManager!!.useR_TYPE_ID == "4" && prefManager!!.useR_TYPE.equals(
                "ServiceEngineer",
                ignoreCase = true
            )
        ) { // for service engineer
            districtId = prefManager!!.useR_DistrictId!!
            binding!!.rlDistrict.visibility = View.GONE
        } else if (prefManager!!.useR_TYPE_ID.equals(
                "6",
                ignoreCase = true
            ) && prefManager!!.useR_TYPE.equals("DSO", ignoreCase = true)
        ) { // for dso
            districtId = prefManager!!.useR_DistrictId!!
            binding!!.rlDistrict.visibility = View.GONE
        } else {
            districtList()
            binding!!.rlDistrict.visibility = View.VISIBLE
        }
        initView()
        setComplaintsListObserver()
    }

    @SuppressLint("SetTextI18n", "NotifyDataSetChanged")
    private fun initView() {
        val c = Calendar.getInstance()
        val year = c[Calendar.YEAR]
        val month = c[Calendar.MONTH]
        @SuppressLint("SimpleDateFormat") val monthStr = SimpleDateFormat("MMM").format(c.time)

        binding!!.textSelectYearMonth.setText("$monthStr, $year")

        adapter = MonthlyReportsActivityAdapter(this@MonthlyReportsActivity)
        binding!!.rvMonthlyReports.setHasFixedSize(true)
        binding!!.rvMonthlyReports.layoutManager =
            LinearLayoutManager(this@MonthlyReportsActivity, LinearLayoutManager.VERTICAL, false)
        binding!!.rvMonthlyReports.addItemDecoration(
            EqualSpacingItemDecoration(
                30,
                EqualSpacingItemDecoration.VERTICAL
            )
        )
        binding!!.rvMonthlyReports.adapter = adapter
        adapter!!.notifyDataSetChanged()

        binding!!.rvMonthlyReports.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val layoutManager = recyclerView.layoutManager as LinearLayoutManager?
                val visibleItemCount = layoutManager!!.childCount
                val totalItemCount = layoutManager.itemCount
                val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

                if (!isLoading && !allPagesLoaded && (visibleItemCount + firstVisibleItemPosition) >= totalItemCount && firstVisibleItemPosition >= 0) {
                    isLoading = true
                    fetchData()
                }
            }
        })

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
                        if (districtNameEng.equals(
                                "--" + resources.getString(R.string.district) + "--",
                                ignoreCase = true
                            )
                        ) {
                            //    fetchComplaintListByDistrictWise("0");
                            districtId = "0"
                            currentPage = 1
                            objectList.clear()
                            adapter!!.notifyDataSetChanged()
                            allPagesLoaded = false
                            fetchData()
                        } else {
                            //    fetchComplaintListByDistrictWise(districtId);
                            currentPage = 1
                            objectList.clear()
                            adapter!!.notifyDataSetChanged()
                            allPagesLoaded = false
                            fetchData()
                        }
                    }
                }

                override fun onNothingSelected(adapterView: AdapterView<*>?) {
                }
            }

        binding!!.textSelectYearMonth.setOnClickListener { view: View? ->
            RackMonthPicker(mContext)
                .setLocale(Locale.ENGLISH)
                .setPositiveButton { month1: Int, startDate: Int, endDate: Int, year1: Int, monthLabel: String? ->
                    fromDate =
                        "$year1-$month1-$startDate"
                    toDate = "$year1-$month1-$endDate"
                    binding!!.textSelectYearMonth.setText(monthLabel)
                    //    fetchComplaintListMonthAndYearWise(fromDate, toDate);
                    currentPage = 1
                    objectList.clear()
                    adapter!!.notifyDataSetChanged()
                    allPagesLoaded = false
                    fetchData()
                }
                .setNegativeButton { obj: AlertDialog? -> obj!!.dismiss() }
                .show()
        }

        binding!!.actionBar.buttonPDF.setOnClickListener { view: View? ->
            if (objectList != null && objectList.size > 0) {
                Constants.listMonthReport = getFormattedList(objectList)
                val intent = Intent(mContext, ReportPdfActivity::class.java)
                intent.putExtra("from_where", "monthly")
                intent.putExtra("title", "MONTHLY CALL LOGGED SUMMARY")
                intent.putExtra(
                    "district",
                    if (districtNameEng == "") "All District" else districtNameEng
                )
                intent.putExtra("name", prefManager!!.useR_NAME)
                intent.putExtra("email", prefManager!!.useR_EMAIL)
                startActivity(intent)
            } else Toast.makeText(
                mContext,
                resources.getString(R.string.no_record_found_to_export_pdf),
                Toast.LENGTH_SHORT
            ).show()
        }

        binding!!.actionBar.ivBack.setOnClickListener { view: View? -> onBackPressed() }
    }


    private fun setComplaintsListObserver() {
        val sdf_in_api = SimpleDateFormat(myFormat, Locale.US)

        val calendar = Calendar.getInstance()
        calendar[Calendar.DAY_OF_MONTH] = calendar.getActualMinimum(Calendar.DAY_OF_MONTH)
        calendar[Calendar.HOUR_OF_DAY] = 0
        calendar[Calendar.MINUTE] = 0
        calendar[Calendar.SECOND] = 0
        calendar[Calendar.MILLISECOND] = 0
        val firstDateOfCurrentMonth = calendar.time

        //String date1 = sdf.format(firstDateOfCurrentMonth);
        val calendarToday = Calendar.getInstance()
        val today = calendarToday.time

        //String todayDate = sdf.format(today);
        val firstDateOfCurrentMonthInAPI = sdf_in_api.format(firstDateOfCurrentMonth)
        val todayDateInAPI = sdf_in_api.format(today)

        fromDate = firstDateOfCurrentMonthInAPI
        toDate = todayDateInAPI

        //isLoading();
        showProgress(resources.getString(R.string.please_wait))
        viewModel!!.getComplaintsDistStatusDateWise(
            prefManager!!.useR_Id.toString(), districtId, complainStatusId, fromDate, toDate,
            "1", "" + currentPage, "" + PAGE_SIZE, ""
        ).observe(
            this
        ) { modelComplaint: ModelComplaint? ->
            //     isLoading();
            hideProgress()
            adapter!!.showLoader(false)
            Log.d("status", "status---" + modelComplaint!!.status)

            if (modelComplaint!!.status == "200") {
                allPagesLoaded =
                    if (modelComplaint.getCurrentPage() == modelComplaint.getTotalPages()) true
                    else false

                complaintList = modelComplaint.complaint_List
                //                List<ModelComplaintList> sortedList = new ArrayList<>(complaintList);
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                    Collections.sort(sortedList, Comparator.comparing(ModelComplaintList::getComplainRegDateStr));
//                }
                //        objectList = new ArrayList<>(sortedList);
                (complaintList as MutableList<ModelComplaintList>?)?.let { objectList.addAll(it) }
                Log.d("objectList", "objectList---" + objectList.size)
                if (objectList.size > 0) {
                    binding!!.rvMonthlyReports.visibility = View.VISIBLE
                    binding!!.textNoComplaint.visibility = View.GONE
                    adapter!!.setData(getFormattedList(objectList))
                } else {
                    binding!!.rvMonthlyReports.visibility = View.GONE
                    binding!!.textNoComplaint.visibility = View.VISIBLE
                }

                currentPage++
            } else if (modelComplaint.status == "201") {
                binding!!.rvMonthlyReports.visibility = View.GONE
                binding!!.textNoComplaint.visibility = View.VISIBLE
            }
            isLoading = false
        }
    }

    private fun fetchData() {
        if (currentPage == 1) showProgress(resources.getString(R.string.please_wait))
        else adapter!!.showLoader(true)

        viewModel!!.getComplaintsDistStatusDateWise(
            prefManager!!.useR_Id.toString(),
            districtId,
            complainStatusId,
            fromDate,
            toDate,
            "1",
            "" + currentPage,
            "" + PAGE_SIZE,
            ""
        )
    }


    private fun getFormattedList(objectList: List<Any>): List<MonthReportModel> {
        val monthReportList: MutableList<MonthReportModel> = ArrayList()
        val formattedObjectList: MutableList<Any> = ArrayList()
        if (objectList.size > 0) {
            val date = (objectList[0] as ModelComplaintList).complainRegDateStr
            if (objectList.size > 1) formattedObjectList.add(MonthDateModel(date))
            formattedObjectList.add(objectList[0])
            for (i in 1 until objectList.size) {
                val date1 = (objectList[i] as ModelComplaintList).complainRegDateStr
                val date2 = (objectList[i - 1] as ModelComplaintList).complainRegDateStr
                if (date1 != date2) {
                    formattedObjectList.add(MonthDateModel((objectList[i] as ModelComplaintList).complainRegDateStr))
                    formattedObjectList.add(objectList[i])
                } else formattedObjectList.add(objectList[i])
            }
        }
        try {
            if (formattedObjectList.size > 0) {
                var innerDateWiseList: MutableList<ModelComplaintList?>? = null
                var date: String? = ""
                if (formattedObjectList.size > 1) {
                    for (j in formattedObjectList.indices) {
                        val monthReportModel = MonthReportModel()
                        if (formattedObjectList[j] is MonthDateModel) {
                            if (innerDateWiseList != null) {
                                monthReportModel.date = date
                                monthReportModel.list = innerDateWiseList
                                monthReportList.add(monthReportModel)
                            }
                            date = (formattedObjectList[j] as MonthDateModel).date
                            innerDateWiseList = ArrayList()
                        } else {
                            innerDateWiseList?.add(formattedObjectList[j] as ModelComplaintList)
                        }
                    }
                    if (innerDateWiseList != null) {
                        val monthReportModel = MonthReportModel()
                        monthReportModel.date = date
                        monthReportModel.list = innerDateWiseList
                        monthReportList.add(monthReportModel)
                    }
                } else {
                    val monthReportModel = MonthReportModel()
                    monthReportModel.date =
                        (formattedObjectList[0] as ModelComplaintList).complainRegDateStr
                    innerDateWiseList = ArrayList()
                    innerDateWiseList.add(formattedObjectList[0] as ModelComplaintList)
                    monthReportModel.list = innerDateWiseList
                    monthReportList.add(monthReportModel)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return monthReportList
    }

    private fun districtList() {
        viewModel!!.district.observe(
            this
        ) { modelDistrict: ModelDistrict? ->
            if (modelDistrict!!.status == "200") {
                district_List = modelDistrict.district_List

                if (district_List != null && district_List!!.size > 0) {
                    Collections.reverse(district_List)
                    val l = ModelDistrictList()
                    l.districtId = (-1).toString()
                    l.districtNameEng = "--" + resources.getString(R.string.district) + "--"
                    district_List!!.add(l)
                    Collections.reverse(district_List)

                    val dataAdapter =
                        ArrayAdapter(
                            mContext, android.R.layout.simple_spinner_item,
                            district_List!!
                        )
                    dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    binding!!.spinnerDistrict.adapter = dataAdapter
                }
            }
        }
    }

    private fun isLoading() {
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
            return o1.date.compareTo(o2.date)
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
            cal1.time = date1

            val cal2 = Calendar.getInstance()
            cal2.time = date2

            while (!cal1.after(cal2)) {
                val newDate = sdf.format(cal1.time)
                dates.add(newDate)
                cal1.add(Calendar.DATE, 1)
            }
            return dates
        }
    }
}