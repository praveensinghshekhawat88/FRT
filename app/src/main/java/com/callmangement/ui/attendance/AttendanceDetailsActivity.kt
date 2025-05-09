package com.callmangement.ui.attendance

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.DatePicker
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.callmangement.R
import com.callmangement.adapter.AttendanceDetailsActivityAdapter
import com.callmangement.custom.CustomActivity
import com.callmangement.databinding.ActivityAttendanceDetailsBinding
import com.callmangement.model.attendance.ModelAttendanceList
import com.callmangement.model.district.ModelDistrict
import com.callmangement.model.district.ModelDistrictList
import com.callmangement.ui.complaint.ComplaintViewModel
import com.callmangement.utils.EqualSpacingItemDecoration
import com.callmangement.utils.PrefManager
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Collections
import java.util.Locale
import java.util.Objects

class AttendanceDetailsActivity : CustomActivity() {

    var binding: ActivityAttendanceDetailsBinding? = null
    
    private val myCalendarFromDate: Calendar = Calendar.getInstance()
    private val myCalendarToDate: Calendar = Calendar.getInstance()
    private val myFormat = "yyyy-MM-dd"
    private var viewModel: ComplaintViewModel? = null
    private var attendanceViewModel: AttendanceViewModel? = null
    private var adapter: AttendanceDetailsActivityAdapter? = null
    private var prefManager: PrefManager? = null
    private var districtId = "0"
    private var checkDistrict = 0
    private var districtNameEng = ""
    private var district_List: MutableList<ModelDistrictList?>? = ArrayList()
    private var fromDate = ""
    private var toDate = ""
    private var userId = "0"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_attendance_details)
        mContext = this
        prefManager = PrefManager(mContext!!)
        binding!!.actionBar.ivBack.visibility = View.VISIBLE
        binding!!.actionBar.ivThreeDot.visibility = View.GONE
        binding!!.actionBar.layoutLanguage.visibility = View.GONE
        binding!!.actionBar.buttonPDF.visibility = View.GONE
        binding!!.actionBar.textToolbarTitle.text = resources.getString(R.string.frt_punch_in)
        viewModel = ViewModelProviders.of(this).get(
            ComplaintViewModel::class.java
        )
        attendanceViewModel = ViewModelProviders.of(this).get(
            AttendanceViewModel::class.java
        )
        districtNameEng = prefManager!!.useR_District!!

        if (prefManager!!.useR_TYPE_ID == "4" && prefManager!!.useR_TYPE.equals(
                "ServiceEngineer",
                ignoreCase = true
            )
        ) { // for service engineer
            districtId = prefManager!!.useR_DistrictId!!
            userId = prefManager!!.useR_Id
            binding!!.rlDistrict.visibility = View.GONE
        } else if (prefManager!!.useR_TYPE_ID.equals(
                "6",
                ignoreCase = true
            ) && prefManager!!.useR_TYPE.equals("DSO", ignoreCase = true)
        ) { // for dso
            districtId = prefManager!!.useR_DistrictId!!
            userId = prefManager!!.useR_Id
            binding!!.rlDistrict.visibility = View.GONE
        } else {
            userId = "0"
            districtList()
            binding!!.rlDistrict.visibility = View.VISIBLE
        }
        initView()
        fetchDataFromCurrentMonth()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun initView() {
        adapter = AttendanceDetailsActivityAdapter(mContext!!)
        adapter!!.notifyDataSetChanged()
        binding!!.rvMarkAttendance.layoutManager =
            LinearLayoutManager(mContext!!, LinearLayoutManager.VERTICAL, false)
        binding!!.rvMarkAttendance.addItemDecoration(
            EqualSpacingItemDecoration(
                30,
                EqualSpacingItemDecoration.VERTICAL
            )
        )
        binding!!.rvMarkAttendance.adapter = adapter

        binding!!.spinnerDistrict.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    adapterView: AdapterView<*>?,
                    view: View,
                    i: Int,
                    l: Long
                ) {
                    if (++checkDistrict > 1) {
                        districtNameEng = district_List!![i]!!.districtNameEng!!
                        districtId = district_List!![i]!!.districtId!!
                        if (districtNameEng.equals(
                                "--" + resources.getString(R.string.district) + "--",
                                ignoreCase = true
                            )
                        ) {
                            districtId = "0"
                        }
                        fetchAttendanceList(districtId)
                    }
                }

                override fun onNothingSelected(adapterView: AdapterView<*>?) {
                }
            }

        val dateFromDate =
            OnDateSetListener { view: DatePicker?, year: Int, monthOfYear: Int, dayOfMonth: Int ->
                myCalendarFromDate[Calendar.YEAR] =
                    year
                myCalendarFromDate[Calendar.MONTH] = monthOfYear
                myCalendarFromDate[Calendar.DAY_OF_MONTH] = dayOfMonth
                updateLabelFromDate()
            }

        val dateToDate =
            OnDateSetListener { view: DatePicker?, year: Int, monthOfYear: Int, dayOfMonth: Int ->
                myCalendarToDate[Calendar.YEAR] =
                    year
                myCalendarToDate[Calendar.MONTH] = monthOfYear
                myCalendarToDate[Calendar.DAY_OF_MONTH] = dayOfMonth
                updateLabelToDate()
            }

        binding!!.textFromDate.setOnClickListener { view: View? ->
            val datePickerDialog = DatePickerDialog(
                mContext!!, dateFromDate,
                myCalendarFromDate[Calendar.YEAR],
                myCalendarFromDate[Calendar.MONTH],
                myCalendarFromDate[Calendar.DAY_OF_MONTH]
            )
            datePickerDialog.datePicker.maxDate = System.currentTimeMillis() - 1000
            datePickerDialog.show()
        }

        binding!!.textToDate.setOnClickListener { view: View? ->
            val datePickerDialog = DatePickerDialog(
                mContext!!,
                dateToDate,
                myCalendarToDate[Calendar.YEAR],
                myCalendarToDate[Calendar.MONTH],
                myCalendarToDate[Calendar.DAY_OF_MONTH]
            )
            datePickerDialog.datePicker.maxDate = System.currentTimeMillis() - 1000
            datePickerDialog.show()
        }

        binding!!.actionBar.ivBack.setOnClickListener { view: View? -> onBackPressed() }
    }

    private fun updateLabelFromDate() {
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        binding!!.textFromDate.setText(sdf.format(myCalendarFromDate.time))
        fromDate = Objects.requireNonNull(binding!!.textFromDate.text).toString().trim { it <= ' ' }
        binding!!.textToDate.text!!.clear()
    }

    private fun updateLabelToDate() {
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        binding!!.textToDate.setText(sdf.format(myCalendarToDate.time))
        fromDate = Objects.requireNonNull(binding!!.textFromDate.text).toString().trim { it <= ' ' }
        toDate = Objects.requireNonNull(binding!!.textToDate.text).toString().trim { it <= ' ' }
        fetchAttendanceList(districtId)
    }

    private fun fetchDataFromCurrentMonth() {
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        val calendar = Calendar.getInstance()
        calendar[Calendar.DAY_OF_MONTH] =
            calendar.getActualMinimum(Calendar.DAY_OF_MONTH)
        calendar[Calendar.HOUR_OF_DAY] = 0
        calendar[Calendar.MINUTE] = 0
        calendar[Calendar.SECOND] = 0
        calendar[Calendar.MILLISECOND] = 0
        val firstDateOfCurrentMonth = calendar.time
        val calendarToday = Calendar.getInstance()
        val today = calendarToday.time
        val firstDateOfCurrentMonthInAPI = sdf.format(firstDateOfCurrentMonth)
        val todayDateInAPI = sdf.format(today)
        fromDate = firstDateOfCurrentMonthInAPI
        toDate = todayDateInAPI
        fetchAttendanceList(districtId)
    }

    private fun fetchAttendanceList(districtId: String) {
        isLoading
        attendanceViewModel!!.getAttendanceList(userId, districtId, fromDate, toDate).observe(
            this
        ) { modelAttendanceList: ModelAttendanceList? ->
            isLoading
            val modelAttendanceData = modelAttendanceList!!.data
            if (modelAttendanceData != null && modelAttendanceData.size > 0) {
                binding!!.rvMarkAttendance.visibility = View.VISIBLE
                binding!!.textNoAttendance.visibility = View.GONE
                adapter!!.setData(modelAttendanceData)
            } else {
                binding!!.rvMarkAttendance.visibility = View.GONE
                binding!!.textNoAttendance.visibility = View.VISIBLE
            }
        }
    }

    private fun districtList() {
        viewModel!!.district.observe(
            this
        ) { modelDistrict: ModelDistrict? ->
            if (modelDistrict!!.status == "200") {
                district_List = modelDistrict.district_List
                if (district_List != null && district_List!!.size > 0) {
                    district_List!!.reverse()
                    val l = ModelDistrictList()
                    l.districtId = (-1).toString()
                    l.districtNameEng = "--" + resources.getString(R.string.district) + "--"
                    district_List!!.add(l)
                    district_List!!.reverse()
                    val dataAdapter =
                        ArrayAdapter(
                            mContext!!, android.R.layout.simple_spinner_item,
                            district_List!!
                        )
                    dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    binding!!.spinnerDistrict.adapter = dataAdapter
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
}