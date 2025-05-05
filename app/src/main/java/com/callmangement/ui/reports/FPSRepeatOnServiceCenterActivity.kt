package com.callmangement.ui.reports

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.DatePicker
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.callmangement.network.APIService
import com.callmangement.network.RetrofitInstance
import com.callmangement.R
import com.callmangement.adapter.FPSRepeatOnServiceCenterActivityAdapter
import com.callmangement.custom.CustomActivity
import com.callmangement.databinding.ActivityFpsrepeatonServiceCenterBinding
import com.callmangement.model.district.ModelDistrict
import com.callmangement.model.district.ModelDistrictList
import com.callmangement.model.fps_repeat_on_service_center.ModelRepeatFpsComplaints
import com.callmangement.model.fps_repeat_on_service_center.ModelRepeatFpsComplaintsList
import com.callmangement.ui.complaint.ComplaintViewModel
import com.callmangement.utils.Constants
import com.callmangement.utils.PrefManager
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.Objects

class FPSRepeatOnServiceCenterActivity : CustomActivity(), View.OnClickListener {
    private var binding: ActivityFpsrepeatonServiceCenterBinding? = null
    private var viewModel: ComplaintViewModel? = null
    private val myCalendarFromDate: Calendar = Calendar.getInstance()
    private val myCalendarToDate: Calendar = Calendar.getInstance()
    private val spinnerList: MutableList<String> = ArrayList()
    private val myFormat = "yyyy-MM-dd"
    private var checkFilter = 0
    private var checkDistrict = 0
    private var districtId = "0"
    private var district_List: MutableList<ModelDistrictList?>? = ArrayList()
    private var prefManager: PrefManager? = null
    private var fromDate = ""
    private var toDate = ""
    private var list: List<ModelRepeatFpsComplaintsList> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFpsrepeatonServiceCenterBinding.inflate(
            layoutInflater
        )
        setContentView(binding!!.root)
        initView()
    }

    private fun initView() {
        binding!!.actionBar.ivBack.visibility = View.VISIBLE
        binding!!.actionBar.ivThreeDot.visibility = View.GONE
        binding!!.actionBar.layoutLanguage.visibility = View.GONE
        binding!!.actionBar.buttonPDF.visibility = View.GONE
        binding!!.actionBar.textToolbarTitle.text =
            resources.getString(R.string.count_on_service_center)
        viewModel = ViewModelProviders.of(this).get(
            ComplaintViewModel::class.java
        )
        prefManager = PrefManager(mContext)
        districtId = prefManager!!.useR_DistrictId!!
        setUpOnClickListener()
        manageLayout()
        setUpData()
        districtList()
        fetchData(districtId, fromDate, toDate)
    }

    private fun manageLayout() {
        if (prefManager!!.useR_TYPE_ID == "4" && prefManager!!.useR_TYPE.equals(
                "ServiceEngineer",
                ignoreCase = true
            )
        ) {
            binding!!.rlDistrict.visibility = View.GONE
            binding!!.spacer.visibility = View.GONE
            districtId = prefManager!!.useR_DistrictId!!
        } else if (prefManager!!.useR_TYPE_ID.equals(
                "6",
                ignoreCase = true
            ) && prefManager!!.useR_TYPE.equals("DSO", ignoreCase = true)
        ) {
            binding!!.rlDistrict.visibility = View.GONE
            binding!!.spacer.visibility = View.GONE
            districtId = prefManager!!.useR_DistrictId!!
        } else {
            binding!!.rlDistrict.visibility = View.VISIBLE
            binding!!.spacer.visibility = View.VISIBLE
        }
    }

    private fun setUpData() {
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

    private fun setUpOnClickListener() {
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

                            fetchData(districtId, fromDate, toDate)
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
                            fetchData(districtId, fromDate, toDate)
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

                            fetchData(districtId, fromDate, toDate)
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

                            fetchData(districtId, fromDate, toDate)
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
                        fetchData(districtId, fromDate, toDate)
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
                        districtId = district_List!![i]!!.districtId
                        fetchData(districtId, fromDate, toDate)
                    }
                }

                override fun onNothingSelected(adapterView: AdapterView<*>?) {
                }
            }

        binding!!.inputFpsCode.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
            }

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                if (!charSequence.toString().isEmpty()) {
                    val filterList: MutableList<ModelRepeatFpsComplaintsList> = ArrayList()
                    if (list.size > 0) {
                        for (model in list) {
                            if (model.fpscode.contains(charSequence.toString())) filterList.add(
                                model
                            )
                        }
                    }
                    setUpAdapter(filterList)
                } else setUpAdapter(list)
            }

            override fun afterTextChanged(editable: Editable) {
            }
        })

        binding!!.actionBar.ivBack.setOnClickListener(this)
        binding!!.textFromDate.setOnClickListener(this)
        binding!!.textToDate.setOnClickListener(this)
        //        binding.actionBar.buttonPDF.setOnClickListener(this);
    }

    private fun updateLabelFromDate() {
        val myFormat = "yyyy-MM-dd"
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        binding!!.textFromDate.setText(sdf.format(myCalendarFromDate.time))
        binding!!.textToDate.text!!.clear()
        fromDate = Objects.requireNonNull(binding!!.textFromDate.text).toString().trim { it <= ' ' }
    }

    private fun updateLabelToDate() {
        val myFormat = "yyyy-MM-dd"
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        binding!!.textToDate.setText(sdf.format(myCalendarToDate.time))
        toDate = Objects.requireNonNull(binding!!.textToDate.text).toString().trim { it <= ' ' }
        fetchData(districtId, fromDate, toDate)
    }

    private fun districtList() {
        viewModel!!.district.observe(
            this
        ) { modelDistrict: ModelDistrict? ->
            if (modelDistrict!!.status == "200") {
                district_List = modelDistrict.district_List
                if (district_List != null && district_List!!.size > 0) {
                    district_List?.reverse()
                    val l = ModelDistrictList()
                    l.districtId = (-1).toString()
                    l.districtNameEng = "--" + resources.getString(R.string.district) + "--"
                    district_List!!.add(l)
                    district_List?.reverse()
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

    private fun fetchData(districtId: String, fromDate: String, toDate: String) {
        if (Constants.isNetworkAvailable(mContext)) {
            showProgress()
            val service = RetrofitInstance.getRetrofitInstance().create(
                APIService::class.java
            )
            val call = service.getFpsRepeatOnServiceCenter(
                prefManager!!.useR_Id,
                fromDate,
                toDate,
                districtId,
                ""
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
                                        ModelRepeatFpsComplaints::class.java
                                    ) as ModelRepeatFpsComplaints
                                    if (modelResponse != null) {
                                        if (modelResponse.status == "200") {
                                            if (modelResponse.parts.size > 0) {
                                                list = modelResponse.parts
                                                //                                                Constants.modelRepeatFpsComplaintsList = list;
                                                setUpAdapter(list)
                                            } else {
                                                binding!!.rvFpsRepeat.visibility = View.GONE
                                                binding!!.tvNoDataFound.visibility = View.VISIBLE
                                            }
                                        } else {
                                            binding!!.rvFpsRepeat.visibility = View.GONE
                                            binding!!.tvNoDataFound.visibility = View.VISIBLE
                                        }
                                    } else {
                                        binding!!.rvFpsRepeat.visibility = View.GONE
                                        binding!!.tvNoDataFound.visibility = View.VISIBLE
                                    }
                                } else {
                                    binding!!.rvFpsRepeat.visibility = View.GONE
                                    binding!!.tvNoDataFound.visibility = View.VISIBLE
                                }
                            } else {
                                binding!!.rvFpsRepeat.visibility = View.GONE
                                binding!!.tvNoDataFound.visibility = View.VISIBLE
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                            binding!!.rvFpsRepeat.visibility = View.GONE
                            binding!!.tvNoDataFound.visibility = View.VISIBLE
                        }
                    } else {
                        makeToast(resources.getString(R.string.error))
                        binding!!.rvFpsRepeat.visibility = View.GONE
                        binding!!.tvNoDataFound.visibility = View.VISIBLE
                    }
                }

                override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                    hideProgress()
                    makeToast(resources.getString(R.string.error_message))
                    binding!!.rvFpsRepeat.visibility = View.GONE
                    binding!!.tvNoDataFound.visibility = View.VISIBLE
                }
            })
        } else {
            makeToast(resources.getString(R.string.no_internet_connection))
        }
    }

    private fun setUpAdapter(list: List<ModelRepeatFpsComplaintsList>) {
        if (list.size > 0) {
            binding!!.rvFpsRepeat.visibility = View.VISIBLE
            binding!!.tvNoDataFound.visibility = View.GONE
            binding!!.rvFpsRepeat.layoutManager =
                LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false)
            binding!!.rvFpsRepeat.adapter = FPSRepeatOnServiceCenterActivityAdapter(mContext, list)
            binding!!.textTotalCount.text = list.size.toString()
        } else {
            binding!!.rvFpsRepeat.visibility = View.GONE
            binding!!.tvNoDataFound.visibility = View.VISIBLE
            binding!!.textTotalCount.text = "0"
        }
    }

    fun fpsRepeatOnServiceCenterView(model: ModelRepeatFpsComplaintsList?) {
        startActivity(
            Intent(mContext, FPSRepeatOnServiceCenterDetailActivity::class.java).putExtra(
                "param",
                model
            )
        )
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
                mContext, dateFromDate,
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
                mContext, dateToDate,
                myCalendarToDate[Calendar.YEAR],
                myCalendarToDate[Calendar.MONTH],
                myCalendarToDate[Calendar.DAY_OF_MONTH]
            )
            datePickerDialog.datePicker.maxDate = System.currentTimeMillis() - 1000
            datePickerDialog.show()
        } /*else if (id == R.id.buttonPDF){
            if (Constants.modelRepeatFpsComplaintsList != null && Constants.modelRepeatFpsComplaintsList.size() > 0){
                startActivity(new Intent(mContext, FPSRepeatOnServiceCenterPDFActivity.class));
                finish();
            }
        }*/
    }
}