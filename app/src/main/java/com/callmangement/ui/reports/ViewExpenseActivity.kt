package com.callmangement.ui.reports

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.DatePicker
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.callmangement.network.APIService
import com.callmangement.network.RetrofitInstance
import com.callmangement.R
import com.callmangement.adapter.ViewExpenseActivityAdapter
import com.callmangement.custom.CustomActivity
import com.callmangement.databinding.ActivityViewExpenseBinding
import com.callmangement.model.district.ModelDistrict
import com.callmangement.model.district.ModelDistrictList
import com.callmangement.model.expense.ModelExpenseStatus
import com.callmangement.model.expense.ModelExpensesList
import com.callmangement.model.expense.ModelExpesne
import com.callmangement.ui.complaint.ComplaintViewModel
import com.callmangement.utils.Constants
import com.callmangement.utils.PrefManager
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Collections
import java.util.Locale

class ViewExpenseActivity : CustomActivity(), View.OnClickListener {

    
    private var binding: ActivityViewExpenseBinding? = null
    private var prefManager: PrefManager? = null
    private var expenseStatusId = "0"

    //    private List<ModelExpensesList> modelExpensesList = new ArrayList<>();
    private val modelExpenseStatusList: MutableList<ModelExpenseStatus> = ArrayList()
    private var viewModel: ComplaintViewModel? = null
    private val myCalendarFromDate: Calendar = Calendar.getInstance()
    private val myCalendarToDate: Calendar = Calendar.getInstance()
    private val spinnerList: MutableList<String> = ArrayList()
    private val myFormat = "yyyy-MM-dd"
    private var checkFilter = 0
    private var checkExpenseStatus = 0
    private var checkDistrict = 0
    private var districtId = "0"
    private var fromDate = ""
    private var toDate = ""
    private var district_List: MutableList<ModelDistrictList?>? = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewExpenseBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        initView()
    }

    private fun initView() {

        mContext = this
        prefManager = PrefManager(mContext!!)
        binding!!.actionBar.ivBack.visibility = View.VISIBLE
        binding!!.actionBar.ivThreeDot.visibility = View.GONE
        binding!!.actionBar.layoutLanguage.visibility = View.GONE
        if (prefManager!!.useR_TYPE_ID == "1" && prefManager!!.useR_TYPE.equals(
                "Admin",
                ignoreCase = true
            )
        ) {
            binding!!.actionBar.textToolbarTitle.text =
                resources.getString(R.string.se_expense_report)
        } else if (prefManager!!.useR_TYPE_ID == "2" && prefManager!!.useR_TYPE.equals(
                "Manager",
                ignoreCase = true
            )
        ) {
            binding!!.actionBar.textToolbarTitle.text =
                resources.getString(R.string.se_expense_report)
        } else if (prefManager!!.useR_TYPE_ID == "4" && prefManager!!.useR_TYPE.equals(
                "ServiceEngineer",
                ignoreCase = true
            )
        ) {
            binding!!.actionBar.textToolbarTitle.text = resources.getString(R.string.expense_report)
        }
        viewModel = ViewModelProviders.of(this).get(
            ComplaintViewModel::class.java
        )
        setUpOnclickListener()
        setUpLayout()
        districtList()
        setUpDateRangeSpinner()
        setUpExpenseStatusSpinner()
        //        getExpenseList(expenseStatusId,districtId,fromDate,toDate);
    }

    private fun setUpOnclickListener() {
        binding!!.spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
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
                        if (item.equals(resources.getString(R.string.today), ignoreCase = true)) {
                            binding!!.layoutDateRange.visibility = View.GONE
                            val calendar = Calendar.getInstance()
                            val today = calendar.time
                            val sdf =
                                SimpleDateFormat(myFormat, Locale.US)
                            val todayDate = sdf.format(today)
                            fromDate = todayDate
                            toDate = todayDate

                            getExpenseList(expenseStatusId, districtId, fromDate, toDate)
                        } else if (item.equals(
                                resources.getString(R.string.yesterday),
                                ignoreCase = true
                            )
                        ) {
                            binding!!.layoutDateRange.visibility = View.GONE
                            val calendar = Calendar.getInstance()
                            calendar.add(Calendar.DAY_OF_YEAR, -1)
                            val yesterday = calendar.time
                            val sdf =
                                SimpleDateFormat(myFormat, Locale.US)
                            val yesterdayDate = sdf.format(yesterday)
                            fromDate = yesterdayDate
                            toDate = yesterdayDate
                            getExpenseList(expenseStatusId, districtId, fromDate, toDate)
                        } else if (item.equals(
                                resources.getString(R.string.current_month),
                                ignoreCase = true
                            )
                        ) {
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

                            fromDate = date1
                            toDate = date2

                            getExpenseList(expenseStatusId, districtId, fromDate, toDate)
                        } else if (item.equals(
                                resources.getString(R.string.previous_month),
                                ignoreCase = true
                            )
                        ) {
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

                            fromDate = date1
                            toDate = date2

                            getExpenseList(expenseStatusId, districtId, fromDate, toDate)
                        } else if (item.equals(
                                resources.getString(R.string.custom_filter),
                                ignoreCase = true
                            )
                        ) {
                            binding!!.layoutDateRange.visibility = View.VISIBLE
                        }
                    } else {
                        fromDate = ""
                        toDate = ""
                        binding!!.textFromDate.text!!.clear()
                        binding!!.textToDate.text!!.clear()
                        binding!!.layoutDateRange.visibility = View.GONE
                        getExpenseList(expenseStatusId, districtId, fromDate, toDate)
                    }
                }
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {
            }
        }

        binding!!.spinnerDistrict.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    adapterView: AdapterView<*>?,
                    view: View,
                    i: Int,
                    l: Long
                ) {
                    if (++checkDistrict > 1) {
                        districtId = district_List!![i]!!.districtId!!
                        getExpenseList(expenseStatusId, districtId, fromDate, toDate)
                    }
                }

                override fun onNothingSelected(adapterView: AdapterView<*>?) {
                }
            }

        binding!!.spinnerExpenseStatus.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    adapterView: AdapterView<*>?,
                    view: View,
                    i: Int,
                    l: Long
                ) {
                    if (++checkExpenseStatus > 1) {
                        expenseStatusId = modelExpenseStatusList[i].id!!
                        getExpenseList(expenseStatusId, districtId, fromDate, toDate)
                    }
                }

                override fun onNothingSelected(adapterView: AdapterView<*>?) {
                }
            }

        binding!!.actionBar.ivBack.setOnClickListener(this)
        binding!!.textFromDate.setOnClickListener(this)
        binding!!.textToDate.setOnClickListener(this)
    }

    private fun setUpLayout() {
        if (prefManager!!.useR_TYPE_ID == "1" && prefManager!!.useR_TYPE.equals(
                "Admin",
                ignoreCase = true
            )
        ) {
            binding!!.rlDistrict.visibility = View.VISIBLE
            binding!!.spacer.visibility = View.VISIBLE
        } else if (prefManager!!.useR_TYPE_ID == "2" && prefManager!!.useR_TYPE.equals(
                "Manager",
                ignoreCase = true
            )
        ) {
            binding!!.rlDistrict.visibility = View.VISIBLE
            binding!!.spacer.visibility = View.VISIBLE
        } else if (prefManager!!.useR_TYPE_ID == "4" && prefManager!!.useR_TYPE.equals(
                "ServiceEngineer",
                ignoreCase = true
            )
        ) {
            binding!!.rlDistrict.visibility = View.GONE
            binding!!.spacer.visibility = View.GONE
            districtId = prefManager!!.useR_DistrictId!!
        }
    }

    private fun setUpDateRangeSpinner() {
        spinnerList.add("--" + resources.getString(R.string.select_filter) + "--")
        spinnerList.add(resources.getString(R.string.today))
        spinnerList.add(resources.getString(R.string.yesterday))
        spinnerList.add(resources.getString(R.string.current_month))
        spinnerList.add(resources.getString(R.string.previous_month))
        spinnerList.add(resources.getString(R.string.custom_filter))

        val dataAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, spinnerList)
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding!!.spinner.adapter = dataAdapter
    }

    private fun setUpExpenseStatusSpinner() {
        val model1 = ModelExpenseStatus()
        model1.id = "0"
        model1.expense_status = "--" + resources.getString(R.string.expense_status) + "--"
        modelExpenseStatusList.add(model1)

        val model2 = ModelExpenseStatus()
        model2.id = "1"
        model2.expense_status = resources.getString(R.string.pending)
        modelExpenseStatusList.add(model2)

        val model3 = ModelExpenseStatus()
        model3.id = "2"
        model3.expense_status = resources.getString(R.string.completed)
        modelExpenseStatusList.add(model3)

        val dataAdapter =
            ArrayAdapter(mContext!!, android.R.layout.simple_spinner_item, modelExpenseStatusList)
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding!!.spinnerExpenseStatus.adapter = dataAdapter
    }

    private fun updateLabelFromDate() {
        val myFormat = "yyyy-MM-dd"
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        binding!!.textFromDate.setText(sdf.format(myCalendarFromDate.time))
        binding!!.textToDate.text!!.clear()
        fromDate = binding!!.textFromDate.text!!.toString().trim { it <= ' ' }
    }

    private fun updateLabelToDate() {
        val myFormat = "yyyy-MM-dd"
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        binding!!.textToDate.setText(sdf.format(myCalendarToDate.time))
        toDate = binding!!.textToDate.text!!.toString().trim { it <= ' ' }
        getExpenseList(expenseStatusId, districtId, fromDate, toDate)
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

    private fun getExpenseList(
        expenseStatusId: String,
        districtId: String,
        fromDate: String,
        toDate: String
    ) {
        if (Constants.isNetworkAvailable(mContext!!)) {
            showProgress()
            val service = RetrofitInstance.getRetrofitInstance().create(
                APIService::class.java
            )
            val call = service.getExpensesList(
                prefManager!!.useR_Id,
                expenseStatusId,
                districtId,
                fromDate,
                toDate
            )
            call.enqueue(object : Callback<ResponseBody?> {
                @SuppressLint("SetTextI18n")
                override fun onResponse(
                    call: Call<ResponseBody?>,
                    response: Response<ResponseBody?>
                ) {
                    hideProgress()
                    if (response.isSuccessful) {
                        try {
                            if (response.code() == 200) {
                                if (response.body() != null) {
                                    val responseStr = response.body()!!.string()
                                    val modelResponse = getObject(
                                        responseStr,
                                        ModelExpesne::class.java
                                    ) as ModelExpesne
                                    if (modelResponse != null) {
                                        if (modelResponse.status == "200") {
                                            if (modelResponse.expensesList!!.size > 0) {
                                                binding!!.rvExpenses.visibility = View.VISIBLE
                                                binding!!.textNoDataFound.visibility = View.GONE
                                                val modelExpensesList = modelResponse.expensesList
                                                setUpAdapter(modelExpensesList)
                                            } else {
                                                binding!!.rvExpenses.visibility = View.GONE
                                                binding!!.textNoDataFound.visibility = View.VISIBLE
                                            }
                                        } else {
                                            binding!!.rvExpenses.visibility = View.GONE
                                            binding!!.textNoDataFound.visibility = View.VISIBLE
                                        }
                                    } else {
                                        binding!!.rvExpenses.visibility = View.GONE
                                        binding!!.textNoDataFound.visibility = View.VISIBLE
                                    }
                                } else {
                                    binding!!.rvExpenses.visibility = View.GONE
                                    binding!!.textNoDataFound.visibility = View.VISIBLE
                                }
                            } else {
                                binding!!.rvExpenses.visibility = View.GONE
                                binding!!.textNoDataFound.visibility = View.VISIBLE
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                            binding!!.rvExpenses.visibility = View.GONE
                            binding!!.textNoDataFound.visibility = View.VISIBLE
                        }
                    } else {
                        makeToast(resources.getString(R.string.error))
                        binding!!.rvExpenses.visibility = View.GONE
                        binding!!.textNoDataFound.visibility = View.VISIBLE
                    }
                }

                override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                    hideProgress()
                    makeToast(resources.getString(R.string.error_message))
                    binding!!.rvExpenses.visibility = View.GONE
                    binding!!.textNoDataFound.visibility = View.VISIBLE
                }
            })
        } else {
            makeToast(resources.getString(R.string.no_internet_connection))
        }
    }

    private fun setUpAdapter(modelExpensesList: List<ModelExpensesList>?) {
        binding!!.rvExpenses.layoutManager =
            LinearLayoutManager(mContext!!, LinearLayoutManager.VERTICAL, false)
        binding!!.rvExpenses.adapter = ViewExpenseActivityAdapter(mContext!!, modelExpensesList!!)
    }

    override fun onResume() {
        super.onResume()
        getExpenseList(expenseStatusId, districtId, fromDate, toDate)
    }

    override fun onClick(view: View) {
        val id = view.id
        if (id == R.id.iv_back) {
            onBackPressed()
        } else if (id == R.id.textFromDate) {
            val dateFromDate =
                OnDateSetListener { view1: DatePicker?, year: Int, monthOfYear: Int, dayOfMonth: Int ->
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
                OnDateSetListener { view1: DatePicker?, year: Int, monthOfYear: Int, dayOfMonth: Int ->
                    myCalendarToDate[Calendar.YEAR] =
                        year
                    myCalendarToDate[Calendar.MONTH] = monthOfYear
                    myCalendarToDate[Calendar.DAY_OF_MONTH] = dayOfMonth
                    updateLabelToDate()
                }
            val datePickerDialog = DatePickerDialog(
                mContext!!, dateToDate,
                myCalendarToDate[Calendar.YEAR],
                myCalendarToDate[Calendar.MONTH],
                myCalendarToDate[Calendar.DAY_OF_MONTH]
            )
            datePickerDialog.datePicker.maxDate = System.currentTimeMillis() - 1000
            datePickerDialog.show()
        }
    }
}