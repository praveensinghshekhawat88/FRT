package com.callmangement.ui.inventory

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.DatePicker
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.callmangement.R
import com.callmangement.adapter.ReceiveMaterialListActivityAdapter
import com.callmangement.custom.CustomActivity
import com.callmangement.databinding.ActivityReceiveMaterialListBinding
import com.callmangement.model.inventrory.ModelDispatchInvoice
import com.callmangement.model.inventrory.ModelPartsDispatchInvoiceList
import com.callmangement.network.APIService
import com.callmangement.network.RetrofitInstance
import com.callmangement.utils.Constants.isNetworkAvailable
import com.callmangement.utils.PrefManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Collections
import java.util.Locale
import java.util.Objects

class ReceiveMaterialListActivity : CustomActivity(), View.OnClickListener {
    private var binding: ActivityReceiveMaterialListBinding? = null
    private var prefManager: PrefManager? = null
    private var mActivity: Activity? = null
    private val myCalendarFromDate: Calendar = Calendar.getInstance()
    private val myCalendarToDate: Calendar = Calendar.getInstance()
    private val spinnerList: MutableList<String> = ArrayList()
    private var filterType = ""
    private var checkFilter = 0
    private val myFormat = "yyyy-MM-dd"
    private var fromDate = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReceiveMaterialListBinding.inflate(
            layoutInflater
        )
        setContentView(binding!!.root)
        binding!!.actionBar.ivBack.visibility = View.VISIBLE
        binding!!.actionBar.ivThreeDot.visibility = View.GONE
        binding!!.actionBar.layoutLanguage.visibility = View.GONE
        binding!!.actionBar.textToolbarTitle.text = resources.getString(R.string.receive_material)
        prefManager = PrefManager(mContext!!)
        initView()
    }

    private fun initView() {
        mActivity = this
        setUpClickListener()
        setUpSpinnerDataList()
    }

    private fun setUpClickListener() {
        binding!!.spinnerFilter.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    adapterView: AdapterView<*>,
                    view: View,
                    i: Int,
                    l: Long
                ) {
                    if (++checkFilter > 1) {
                        val item = adapterView.getItemAtPosition(i).toString()
                        if (!item.equals(
                                "--" + resources.getString(R.string.select_filter) + "--",
                                ignoreCase = true
                            )
                        ) {
                            binding!!.textFromDate.text!!.clear()
                            binding!!.textToDate.text!!.clear()
                            if (item.equals(
                                    resources.getString(R.string.today),
                                    ignoreCase = true
                                )
                            ) {
                                filterType = resources.getString(R.string.today)
                                binding!!.layoutDateRange.visibility = View.GONE
                                val calendar = Calendar.getInstance()
                                val today = calendar.time
                                val sdf =
                                    SimpleDateFormat(myFormat, Locale.US)
                                val todayDate = sdf.format(today)
                                getPartsReciverInvoices(todayDate, todayDate)
                            } else if (item.equals(
                                    resources.getString(R.string.yesterday),
                                    ignoreCase = true
                                )
                            ) {
                                filterType = resources.getString(R.string.yesterday)
                                binding!!.layoutDateRange.visibility = View.GONE
                                val calendar = Calendar.getInstance()
                                calendar.add(Calendar.DAY_OF_YEAR, -1)
                                val yesterday = calendar.time
                                val sdf =
                                    SimpleDateFormat(myFormat, Locale.US)
                                val yesterdayDate = sdf.format(yesterday)
                                getPartsReciverInvoices(yesterdayDate, yesterdayDate)
                            } else if (item.equals(
                                    resources.getString(R.string.current_month),
                                    ignoreCase = true
                                )
                            ) {
                                filterType = resources.getString(R.string.current_month)
                                binding!!.layoutDateRange.visibility = View.GONE
                                val sdf =
                                    SimpleDateFormat(myFormat, Locale.US)

                                val calendar = Calendar.getInstance()
                                calendar[Calendar.DAY_OF_MONTH] =
                                    calendar.getActualMinimum(Calendar.DAY_OF_MONTH)
                                calendar[Calendar.HOUR_OF_DAY] = 0
                                calendar[Calendar.MINUTE] = 0
                                calendar[Calendar.SECOND] = 0
                                calendar[Calendar.MILLISECOND] = 0
                                val firstDateOfCurrentMonth = calendar.time
                                val date1 = sdf.format(firstDateOfCurrentMonth)

                                val calendar1 = Calendar.getInstance()
                                val currentDateOfCurrentMonth = calendar1.time
                                val date2 = sdf.format(currentDateOfCurrentMonth)
                                getPartsReciverInvoices(date1, date2)
                            } else if (item.equals(
                                    resources.getString(R.string.previous_month),
                                    ignoreCase = true
                                )
                            ) {
                                filterType = resources.getString(R.string.previous_month)
                                binding!!.layoutDateRange.visibility = View.GONE
                                val sdf =
                                    SimpleDateFormat(myFormat, Locale.US)
                                val aCalendar = Calendar.getInstance()
                                aCalendar.add(Calendar.MONTH, -1)
                                aCalendar[Calendar.DATE] = 1
                                val firstDateOfPreviousMonth = aCalendar.time
                                val date1 = sdf.format(firstDateOfPreviousMonth)

                                aCalendar[Calendar.DATE] =
                                    aCalendar.getActualMaximum(Calendar.DAY_OF_MONTH)
                                val lastDateOfPreviousMonth = aCalendar.time
                                val date2 = sdf.format(lastDateOfPreviousMonth)
                                getPartsReciverInvoices(date1, date2)
                            } else if (item.equals(
                                    resources.getString(R.string.custom_filter),
                                    ignoreCase = true
                                )
                            ) {
                                filterType = resources.getString(R.string.custom_filter)
                                binding!!.layoutDateRange.visibility = View.VISIBLE
                            }
                        } else {
                            filterType = "--" + resources.getString(R.string.select_filter) + "--"
                            binding!!.textFromDate.text!!.clear()
                            binding!!.textToDate.text!!.clear()
                            binding!!.layoutDateRange.visibility = View.GONE
                            getPartsReciverInvoices("", "")
                        }
                    }
                }

                override fun onNothingSelected(adapterView: AdapterView<*>?) {
                }
            }

        binding!!.textFromDate.setOnClickListener(this)
        binding!!.textToDate.setOnClickListener(this)
        binding!!.actionBar.ivBack.setOnClickListener(this)
    }

    private fun updateLabelFromDate() {
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        binding!!.textFromDate.setText(sdf.format(myCalendarFromDate.time))
        binding!!.textToDate.text!!.clear()
        fromDate = Objects.requireNonNull(binding!!.textFromDate.text).toString().trim { it <= ' ' }
    }

    private fun updateLabelToDate() {
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        binding!!.textToDate.setText(sdf.format(myCalendarToDate.time))
        val toDate = Objects.requireNonNull(binding!!.textToDate.text).toString().trim { it <= ' ' }
        getPartsReciverInvoices(fromDate, toDate)
    }

    private fun setUpSpinnerDataList() {
//        filterType = "--" + getResources().getString(R.string.select_filter) + "--";
        filterType = resources.getString(R.string.today)
        spinnerList.add("--" + resources.getString(R.string.select_filter) + "--")
        spinnerList.add(resources.getString(R.string.today))
        spinnerList.add(resources.getString(R.string.yesterday))
        spinnerList.add(resources.getString(R.string.current_month))
        spinnerList.add(resources.getString(R.string.previous_month))
        spinnerList.add(resources.getString(R.string.custom_filter))

        val dataAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, spinnerList)
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding!!.spinnerFilter.adapter = dataAdapter
        binding!!.spinnerFilter.setSelection(1)
    }

    private fun fetchDataByFilterType() {
        if (!filterType.equals(
                "--" + resources.getString(R.string.select_filter) + "--",
                ignoreCase = true
            )
        ) {
            binding!!.textFromDate.text!!.clear()
            binding!!.textToDate.text!!.clear()
            if (filterType.equals(resources.getString(R.string.today), ignoreCase = true)) {
                filterType = resources.getString(R.string.today)
                binding!!.layoutDateRange.visibility = View.GONE
                val calendar = Calendar.getInstance()
                val today = calendar.time
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                val todayDate = sdf.format(today)
                getPartsReciverInvoices(todayDate, todayDate)
            } else if (filterType.equals(
                    resources.getString(R.string.yesterday),
                    ignoreCase = true
                )
            ) {
                filterType = resources.getString(R.string.yesterday)
                binding!!.layoutDateRange.visibility = View.GONE
                val calendar = Calendar.getInstance()
                calendar.add(Calendar.DAY_OF_YEAR, -1)
                val yesterday = calendar.time
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                val yesterdayDate = sdf.format(yesterday)
                getPartsReciverInvoices(yesterdayDate, yesterdayDate)
            } else if (filterType.equals(
                    resources.getString(R.string.current_month),
                    ignoreCase = true
                )
            ) {
                filterType = resources.getString(R.string.current_month)
                binding!!.layoutDateRange.visibility = View.GONE
                val sdf = SimpleDateFormat(myFormat, Locale.US)

                val calendar = Calendar.getInstance()
                calendar[Calendar.DAY_OF_MONTH] =
                    calendar.getActualMinimum(Calendar.DAY_OF_MONTH)
                calendar[Calendar.HOUR_OF_DAY] = 0
                calendar[Calendar.MINUTE] = 0
                calendar[Calendar.SECOND] = 0
                calendar[Calendar.MILLISECOND] = 0
                val firstDateOfCurrentMonth = calendar.time
                val date1 = sdf.format(firstDateOfCurrentMonth)

                val calendar1 = Calendar.getInstance()
                val currentDateOfCurrentMonth = calendar1.time
                val date2 = sdf.format(currentDateOfCurrentMonth)
                getPartsReciverInvoices(date1, date2)
            } else if (filterType.equals(
                    resources.getString(R.string.previous_month),
                    ignoreCase = true
                )
            ) {
                filterType = resources.getString(R.string.previous_month)
                binding!!.layoutDateRange.visibility = View.GONE
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                val aCalendar = Calendar.getInstance()
                aCalendar.add(Calendar.MONTH, -1)
                aCalendar[Calendar.DATE] = 1
                val firstDateOfPreviousMonth = aCalendar.time
                val date1 = sdf.format(firstDateOfPreviousMonth)

                aCalendar[Calendar.DATE] =
                    aCalendar.getActualMaximum(Calendar.DAY_OF_MONTH)
                val lastDateOfPreviousMonth = aCalendar.time
                val date2 = sdf.format(lastDateOfPreviousMonth)
                getPartsReciverInvoices(date1, date2)
            } else if (filterType.equals(
                    resources.getString(R.string.custom_filter),
                    ignoreCase = true
                )
            ) {
                filterType = resources.getString(R.string.custom_filter)
                binding!!.layoutDateRange.visibility = View.VISIBLE
            }
        } else {
            filterType = "--" + resources.getString(R.string.select_filter) + "--"
            binding!!.textFromDate.text!!.clear()
            binding!!.textToDate.text!!.clear()
            binding!!.layoutDateRange.visibility = View.GONE
            getPartsReciverInvoices("", "")
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun getPartsReciverInvoices(date1: String, date2: String) {
        if (isNetworkAvailable(mActivity!!)) {
            showProgress()
            val service = RetrofitInstance.getRetrofitInstance().create(
                APIService::class.java
            )
            val call =
                service.getPartsReciverInvoices("0", prefManager!!.useR_Id, "0", date1, date2)
            call.enqueue(object : Callback<ModelDispatchInvoice?> {
                override fun onResponse(
                    call: Call<ModelDispatchInvoice?>,
                    response: Response<ModelDispatchInvoice?>
                ) {
                    hideProgress()
                    if (response.isSuccessful) {
                        val model = response.body()
                        if (model!!.status == "200") {
                            hideProgress()
                            if (model!!.partsDispatchInvoiceList.size > 0) {
                                binding!!.rvReceiveMaterial.visibility = View.VISIBLE
                                binding!!.textNoRecordFound.visibility = View.GONE
                                val partsDispatchInvoiceList: List<ModelPartsDispatchInvoiceList?> =
                                    model.partsDispatchInvoiceList
                                Collections.reverse(partsDispatchInvoiceList)
                                binding!!.rvReceiveMaterial.layoutManager = LinearLayoutManager(
                                    mContext,
                                    LinearLayoutManager.VERTICAL,
                                    false
                                )
                                binding!!.rvReceiveMaterial.adapter =
                                    ReceiveMaterialListActivityAdapter(
                                        mContext!!, partsDispatchInvoiceList
                                    )
                            } else {
                                binding!!.rvReceiveMaterial.visibility = View.GONE
                                binding!!.textNoRecordFound.visibility = View.VISIBLE
                            }
                        } else {
                            binding!!.rvReceiveMaterial.visibility = View.GONE
                            binding!!.textNoRecordFound.visibility = View.VISIBLE
                        }
                    } else {
                        Toast.makeText(
                            mActivity,
                            resources.getString(R.string.error),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<ModelDispatchInvoice?>, t: Throwable) {
                    hideProgress()
                    Toast.makeText(
                        mActivity,
                        resources.getString(R.string.error_message),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
        } else {
            Toast.makeText(
                mActivity,
                resources.getString(R.string.no_internet_connection),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onResume() {
        super.onResume()
        //        binding.spinnerFilter.setSelection(1);
//        Calendar calendar = Calendar.getInstance();
//        Date today = calendar.getTime();
//        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
//        String todayDate = sdf.format(today);
//        getPartsReciverInvoices(todayDate,todayDate);
        fetchDataByFilterType()
    }

    override fun onClick(view: View) {
        val id = view.id
        if (id == R.id.iv_back) {
            onBackPressed()
        } else if (id == R.id.textFromDate) {
            val dateFromDate =
                OnDateSetListener { viewFromData: DatePicker?, year: Int, monthOfYear: Int, dayOfMonth: Int ->
                    myCalendarFromDate[Calendar.YEAR] =
                        year
                    myCalendarFromDate[Calendar.MONTH] = monthOfYear
                    myCalendarFromDate[Calendar.DAY_OF_MONTH] = dayOfMonth
                    updateLabelFromDate()
                }
            val datePickerDialog = DatePickerDialog(
                mContext!!, dateFromDate,
                myCalendarFromDate[Calendar.YEAR],
                myCalendarFromDate[Calendar.MONTH],
                myCalendarFromDate[Calendar.DAY_OF_MONTH]
            )
            datePickerDialog.datePicker.maxDate = System.currentTimeMillis() - 1000
            datePickerDialog.show()
        } else if (id == R.id.textToDate) {
            val dateToDate =
                OnDateSetListener { viewToDate: DatePicker?, year: Int, monthOfYear: Int, dayOfMonth: Int ->
                    myCalendarToDate[Calendar.YEAR] =
                        year
                    myCalendarToDate[Calendar.MONTH] = monthOfYear
                    myCalendarToDate[Calendar.DAY_OF_MONTH] = dayOfMonth
                    updateLabelToDate()
                }
            val datePickerDialog = DatePickerDialog(
                mContext!!, dateToDate, myCalendarToDate[Calendar.YEAR],
                myCalendarToDate[Calendar.MONTH],
                myCalendarToDate[Calendar.DAY_OF_MONTH]
            )
            datePickerDialog.datePicker.maxDate = System.currentTimeMillis() - 1000
            datePickerDialog.show()
        }
    }
}