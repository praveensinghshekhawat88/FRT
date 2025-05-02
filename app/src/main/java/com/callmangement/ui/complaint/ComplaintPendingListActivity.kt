package com.callmangement.ui.complaint

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.DatePicker
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.callmangement.Network.APIService
import com.callmangement.Network.RetrofitInstance
import com.callmangement.R
import com.callmangement.adapter.ComplaintPendingListActivityAdapter
import com.callmangement.custom.CustomActivity
import com.callmangement.databinding.ActivityComplaintPendingListBinding
import com.callmangement.model.complaints.ModelComplaint
import com.callmangement.model.complaints.ModelComplaintList
import com.callmangement.model.district.ModelDistrictList
import com.callmangement.model.tehsil.ModelTehsilList
import com.callmangement.utils.PrefManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Collections
import java.util.Locale
import java.util.Objects

class ComplaintPendingListActivity : CustomActivity() {
    private var binding: ActivityComplaintPendingListBinding? = null
    private val REQUEST_CODE = 1
    private var adapter: ComplaintPendingListActivityAdapter? = null
    private var prefManager: PrefManager? = null
    private val tehsilList: MutableList<ModelTehsilList?> = ArrayList()
    private var tehsil_List_main: List<ModelTehsilList?>? = ArrayList()
    private var districtList: List<ModelDistrictList>? = ArrayList()
    private var checkTehsil = 0
    private var checkDistrict = 0
    private var tehsilNameEng = ""
    private var districtNameEng = ""
    private val modelComplaintList: MutableList<ModelComplaintList> = ArrayList()
    private var viewModel: ComplaintViewModel? = null
    private var filterType: String? = ""
    private val complainStatusId = "2"
    private var districtId: String? = "0"
    private val myFormat = "yyyy-MM-dd"
    private var checkFilter = 0

    private var currentPage = 1
    private val isPagination = "1"
    private val pageSize = "200"
    private var isLoading = false
    private var isLastPage = false
    var TotalItems: Int = 0
    var fpscodee: String? = null


    private val myCalendarFromDate: Calendar = Calendar.getInstance()
    private val myCalendarToDate: Calendar = Calendar.getInstance()
    private var fromDate = ""
    private var toDate = ""
    private val spinnerList: MutableList<String> = ArrayList()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_complaint_pending_list)
        binding!!.actionBar.ivBack.visibility = View.VISIBLE
        binding!!.actionBar.ivThreeDot.visibility = View.GONE
        binding!!.actionBar.layoutLanguage.visibility = View.GONE
        binding!!.actionBar.buttonPDF.visibility = View.GONE
        binding!!.actionBar.textToolbarTitle.text = resources.getString(R.string.complaint_pending)
        viewModel = ViewModelProviders.of(this).get(
            ComplaintViewModel::class.java
        )

        districtId = intent.getStringExtra("districtId")
        filterType = intent.getStringExtra("filter_type")
        tehsil_List_main = intent.getSerializableExtra("tehsil_list") as List<ModelTehsilList?>?
        districtList = intent.getSerializableExtra("district_list") as List<ModelDistrictList>?
        prefManager = PrefManager(mContext)
        binding!!.textNoComplaint.visibility = View.GONE
        tehsilNameEng = "--" + resources.getString(R.string.tehsil) + "--"
        districtNameEng = "--" + resources.getString(R.string.district) + "--"

        initView()
        fetchDataByMainType()
        DateRangeSpinner()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun initView() {
        tehsilList.addAll(tehsil_List_main!!)

        if (prefManager!!.useR_TYPE_ID == "4" && prefManager!!.useR_TYPE.equals(
                "ServiceEngineer",
                ignoreCase = true
            )
        ) { // for service engineer
            binding!!.rlTehsil.visibility = View.VISIBLE
            binding!!.rlDistrict.visibility = View.GONE
            if (tehsilList.size > 0) {
                Collections.reverse(tehsilList)
                val l = ModelTehsilList()
                l.tehsilId = (-1).toString()
                l.tehsilNameEng = "--" + resources.getString(R.string.tehsil) + "--"
                tehsilList.add(l)
                Collections.reverse(tehsilList)

                val dataAdapter = ArrayAdapter(mContext, R.layout.spinner_item, tehsilList)
                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                binding!!.spinnerTehsil.adapter = dataAdapter
            }
        } else if (prefManager!!.useR_TYPE_ID.equals(
                "6",
                ignoreCase = true
            ) && prefManager!!.useR_TYPE.equals("DSO", ignoreCase = true)
        ) { // for dso
            binding!!.rlTehsil.visibility = View.VISIBLE
            binding!!.rlDistrict.visibility = View.GONE
            if (tehsilList.size != 0) {
                Collections.reverse(tehsilList)
                val l = ModelTehsilList()
                l.tehsilId = (-1).toString()
                l.tehsilNameEng = "--" + resources.getString(R.string.tehsil) + "--"
                tehsilList.add(l)
                Collections.reverse(tehsilList)

                val dataAdapter = ArrayAdapter(mContext, R.layout.spinner_item, tehsilList)
                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                binding!!.spinnerTehsil.adapter = dataAdapter
            }
        } else if (prefManager!!.useR_TYPE_ID == "2" && prefManager!!.useR_TYPE.equals(
                "Manager",
                ignoreCase = true
            )
        ) { // for manager
            binding!!.rlTehsil.visibility = View.GONE
            binding!!.rlDistrict.visibility = View.VISIBLE
            updateTehsilByDistrictId(districtId!!)
            if (districtList != null && districtList!!.size > 0) {
                val dataAdapter = ArrayAdapter(
                    mContext, R.layout.spinner_item,
                    districtList!!
                )
                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                binding!!.spinnerDistrict.adapter = dataAdapter

                if (!districtId.equals("0", ignoreCase = true)) {
                    for (i in districtList!!.indices) {
                        if (districtList!![i].districtId == districtId) {
                            binding!!.spinnerDistrict.setSelection(i)
                        }
                    }
                }
            }
        } else if (prefManager!!.useR_TYPE_ID == "5" && prefManager!!.useR_TYPE.equals(
                "ServiceCentre",
                ignoreCase = true
            )
        ) { // for service center
            binding!!.rlTehsil.visibility = View.GONE
            binding!!.rlDistrict.visibility = View.VISIBLE
            updateTehsilByDistrictId(districtId!!)
            if (districtList != null && districtList!!.size > 0) {
                val dataAdapter = ArrayAdapter(
                    mContext, R.layout.spinner_item,
                    districtList!!
                )
                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                binding!!.spinnerDistrict.adapter = dataAdapter

                if (!districtId.equals("0", ignoreCase = true)) {
                    for (i in districtList!!.indices) {
                        if (districtList!![i].districtId == districtId) {
                            binding!!.spinnerDistrict.setSelection(i)
                        }
                    }
                }
            }
        } else if (prefManager!!.useR_TYPE_ID == "1" && prefManager!!.useR_TYPE.equals(
                "Admin",
                ignoreCase = true
            )
        ) { // for Admin
            binding!!.rlTehsil.visibility = View.GONE
            binding!!.rlDistrict.visibility = View.VISIBLE
            updateTehsilByDistrictId(districtId!!)
            if (districtList != null && districtList!!.size > 0) {
                val dataAdapter = ArrayAdapter(
                    mContext, R.layout.spinner_item,
                    districtList!!
                )
                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                binding!!.spinnerDistrict.adapter = dataAdapter

                if (!districtId.equals("0", ignoreCase = true)) {
                    for (i in districtList!!.indices) {
                        if (districtList!![i].districtId == districtId) {
                            binding!!.spinnerDistrict.setSelection(i)
                        }
                    }
                }
            }
        }

        adapter = ComplaintPendingListActivityAdapter(this@ComplaintPendingListActivity)
        adapter!!.notifyDataSetChanged()
        binding!!.rvComplaintPending.setHasFixedSize(true)
        binding!!.rvComplaintPending.layoutManager = LinearLayoutManager(
            this@ComplaintPendingListActivity,
            LinearLayoutManager.VERTICAL,
            false
        )
        binding!!.rvComplaintPending.adapter = adapter



        binding!!.rvComplaintPending.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager?
                val visibleItemCount = layoutManager!!.childCount
                val totalItemCount = layoutManager.itemCount
                val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

                if (!isLoading && !isLastPage && (visibleItemCount + firstVisibleItemPosition) >= totalItemCount && firstVisibleItemPosition >= 0) {
                    isLoading = true
                    currentPage++
                    Handler(Looper.getMainLooper()).post { fetchComplaintList() }
                }
            }
        })
















        binding!!.spinnerTehsil.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    adapterView: AdapterView<*>?,
                    view: View,
                    i: Int,
                    l: Long
                ) {
                    if (++checkTehsil > 1) {
                        tehsilNameEng = tehsilList[i]!!.tehsilNameEng
                        setDataIntoAdapterByTehsil(tehsilNameEng)
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
                    checkTehsil = 0
                    if (++checkDistrict > 1) {
                        districtNameEng = districtList!![i].districtNameEng
                        districtId = districtList!![i].districtId
                        // updateTehsilByDistrict(districtNameEng);
                    }
                }

                override fun onNothingSelected(adapterView: AdapterView<*>?) {
                }
            }


        /*  binding.inputSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                adapter.getFilter().filter(charSequence);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });*/
        binding!!.actionBar.ivBack.setOnClickListener { view: View? -> onBackPressed() }


        binding!!.buttonSearch.setOnClickListener { view ->
            currentPage = 1
            fpscodee = binding!!.inputFPS.text.toString()
            vibrateDevice(view.context)
            fetchComplaintList()
        }
    }


    private val isLoadingprogress: Unit
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

    @SuppressLint("SetTextI18n")
    private fun setDataIntoAdapter(modelComplaintList: List<ModelComplaintList>?) {
        if (modelComplaintList != null && modelComplaintList.size > 0) {
            // binding.textTotalComplaint.setText(""+ modelComplaintList.size());
            binding!!.textTotalComplaint.text = "" + TotalItems
            binding!!.rvComplaintPending.visibility = View.VISIBLE
            binding!!.textNoComplaint.visibility = View.GONE
            adapter!!.setData(modelComplaintList)
        } else {
            binding!!.textTotalComplaint.text = "0"
            binding!!.rvComplaintPending.visibility = View.GONE
            binding!!.textNoComplaint.visibility = View.VISIBLE
        }
        isLoading = false
    }

    @SuppressLint("SetTextI18n")
    private fun setDataIntoAdapterByTehsil(tehsilNameEng: String) {
        val complaintList: MutableList<ModelComplaintList> = ArrayList()
        if (modelComplaintList != null && modelComplaintList.size > 0) {
            for (i in modelComplaintList.indices) {
                if (tehsilNameEng.equals(
                        "--" + resources.getString(R.string.tehsil) + "--",
                        ignoreCase = true
                    )
                ) {
                    if (districtNameEng.equals(
                            "--" + resources.getString(R.string.district) + "--",
                            ignoreCase = true
                        )
                    ) {
                        complaintList.add(modelComplaintList[i])
                    } else {
                        if (modelComplaintList[i].district != null) {
                            if (modelComplaintList[i].district.equals(
                                    districtNameEng,
                                    ignoreCase = true
                                )
                            ) {
                                complaintList.add(modelComplaintList[i])
                            }
                        }
                    }
                } else {
                    if (modelComplaintList[i].tehsil != null) {
                        if (modelComplaintList[i].tehsil.equals(tehsilNameEng, ignoreCase = true)) {
                            complaintList.add(modelComplaintList[i])
                        }
                    }
                }
            }
        }

        if (complaintList.size > 0) {
            binding!!.textTotalComplaint.text = "" + complaintList.size
            binding!!.rvComplaintPending.visibility = View.VISIBLE
            binding!!.textNoComplaint.visibility = View.GONE
            adapter!!.setData(complaintList)
        } else {
            binding!!.textTotalComplaint.text = "0"
            binding!!.rvComplaintPending.visibility = View.GONE
            binding!!.textNoComplaint.visibility = View.VISIBLE
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setDataIntoAdapterByDistrict(districtNameEng: String) {
        val complaintList: MutableList<ModelComplaintList> = ArrayList()
        if (modelComplaintList != null && modelComplaintList.size > 0) {
            for (i in modelComplaintList.indices) {
                if (districtNameEng.equals(
                        "--" + resources.getString(R.string.district) + "--",
                        ignoreCase = true
                    )
                ) {
                    complaintList.add(modelComplaintList[i])
                } else {
                    if (modelComplaintList[i].district != null) {
                        if (modelComplaintList[i].district.equals(
                                districtNameEng,
                                ignoreCase = true
                            )
                        ) {
                            complaintList.add(modelComplaintList[i])
                        }
                    }
                }
            }
        }

        if (complaintList.size > 0) {
            binding!!.textTotalComplaint.text = "" + complaintList.size
            binding!!.rvComplaintPending.visibility = View.VISIBLE
            binding!!.textNoComplaint.visibility = View.GONE
            adapter!!.setData(complaintList)
        } else {
            binding!!.textTotalComplaint.text = "0"
            binding!!.rvComplaintPending.visibility = View.GONE
            binding!!.textNoComplaint.visibility = View.VISIBLE
        }
    }

    private fun updateTehsilByDistrict(district: String) {
        tehsilList.clear()
        if (tehsil_List_main != null && tehsil_List_main!!.size > 0) {
            for (i in tehsil_List_main!!.indices) {
                if (district.equals(
                        "--" + resources.getString(R.string.district) + "--",
                        ignoreCase = true
                    )
                ) {
                    tehsilList.add(tehsil_List_main!![i])
                } else {
                    if (tehsil_List_main!![i]!!.districtNameEng != null) {
                        if (tehsil_List_main!![i]!!
                                .districtNameEng.equals(district, ignoreCase = true)
                        ) {
                            tehsilList.add(tehsil_List_main!![i])
                        }
                    }
                }
            }
        }
        if (tehsilList.size > 0) {
            Collections.reverse(tehsilList)
            val l = ModelTehsilList()
            l.tehsilId = (-1).toString()
            l.tehsilNameEng = "--" + resources.getString(R.string.tehsil) + "--"
            tehsilList.add(l)
            Collections.reverse(tehsilList)

            val dataAdapter = ArrayAdapter(mContext, R.layout.spinner_item, tehsilList)
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding!!.spinnerTehsil.adapter = dataAdapter
        }
    }

    private fun updateTehsilByDistrictId(districtId: String) {
        tehsilList.clear()
        if (tehsil_List_main != null && tehsil_List_main!!.size > 0) {
            for (i in tehsil_List_main!!.indices) {
                if (districtId.equals("0", ignoreCase = true)) {
                    tehsilList.add(tehsil_List_main!![i])
                } else {
                    if (tehsil_List_main!![i]!!.fk_DistrictId != null) {
                        if (tehsil_List_main!![i]!!
                                .fk_DistrictId.equals(districtId, ignoreCase = true)
                        ) {
                            tehsilList.add(tehsil_List_main!![i])
                        }
                    }
                }
            }
        }

        if (tehsilList.size > 0) {
            Collections.reverse(tehsilList)
            val l = ModelTehsilList()
            l.tehsilId = (-1).toString()
            l.tehsilNameEng = "--" + resources.getString(R.string.tehsil) + "--"
            tehsilList.add(l)
            Collections.reverse(tehsilList)

            val dataAdapter = ArrayAdapter(mContext, R.layout.spinner_item, tehsilList)
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding!!.spinnerTehsil.adapter = dataAdapter
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                fetchComplaintList()
            }
        }
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
    }

    private fun vibrateDevice(context: Context) {
        val vibrator = context.getSystemService(VIBRATOR_SERVICE) as Vibrator
        if (vibrator != null && vibrator.hasVibrator()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(
                    VibrationEffect.createOneShot(
                        100,
                        VibrationEffect.DEFAULT_AMPLITUDE
                    )
                )
            } else {
                vibrator.vibrate(100)
            }
        }
    }


    private fun fetchDataByMainType() {
        if (!filterType.equals(
                "--" + resources.getString(R.string.select_filter) + "--",
                ignoreCase = true
            )
        ) {
            if (filterType.equals(resources.getString(R.string.today), ignoreCase = true)) {
                filterType = resources.getString(R.string.today)
                binding!!.spinner.setSelection(1)

                val calendar = Calendar.getInstance()
                val today = calendar.time
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                val todayDate = sdf.format(today)

                fromDate = todayDate
                toDate = todayDate
            } else if (filterType.equals(
                    resources.getString(R.string.yesterday),
                    ignoreCase = true
                )
            ) {
                filterType = resources.getString(R.string.yesterday)
                binding!!.spinner.setSelection(2)

                val calendar = Calendar.getInstance()
                calendar.add(Calendar.DAY_OF_YEAR, -1)
                val yesterday = calendar.time
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                val yesterdayDate = sdf.format(yesterday)

                fromDate = yesterdayDate
                toDate = yesterdayDate
            } else if (filterType.equals(
                    resources.getString(R.string.current_month),
                    ignoreCase = true
                )
            ) {
                filterType = resources.getString(R.string.current_month)
                binding!!.spinner.setSelection(3)

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
                fromDate = date1
                toDate = date2
            } else if (filterType.equals(
                    resources.getString(R.string.previous_month),
                    ignoreCase = true
                )
            ) {
                filterType = resources.getString(R.string.previous_month)
                binding!!.spinner.setSelection(4)

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
                fromDate = date1
                toDate = date2
            } else if (filterType.equals(
                    resources.getString(R.string.custom_filter),
                    ignoreCase = true
                )
            ) {
                filterType = resources.getString(R.string.custom_filter)
                binding!!.spinner.setSelection(5)

                fromDate = prefManager!!.froM_DATE
                toDate = prefManager!!.tO_DATE
                binding!!.layoutDateRange.visibility = View.VISIBLE
                binding!!.textFromDate.setText(fromDate)
                binding!!.textToDate.setText(toDate)
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                try {
                    myCalendarFromDate.time = sdf.parse(fromDate)
                    myCalendarToDate.time = sdf.parse(toDate)
                } catch (e: ParseException) {
                    e.printStackTrace()
                }
            }
            fetchComplaintList()
        } else {
            filterType = "--" + resources.getString(R.string.select_filter) + "--"
            fromDate = ""
            toDate = ""
            fetchComplaintList()
        }
    }


    private fun DateRangeSpinner() {
        spinnerList.add("--" + resources.getString(R.string.select_filter) + "--")
        spinnerList.add(resources.getString(R.string.today))
        spinnerList.add(resources.getString(R.string.yesterday))
        spinnerList.add(resources.getString(R.string.current_month))
        spinnerList.add(resources.getString(R.string.previous_month))
        spinnerList.add(resources.getString(R.string.custom_filter))
        val dataAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, spinnerList)
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding!!.spinner.adapter = dataAdapter

        binding!!.spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>, view: View, i: Int, l: Long) {
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
                            val sdf = SimpleDateFormat(myFormat, Locale.US)
                            val todayDate = sdf.format(today)
                            fromDate = todayDate
                            toDate = todayDate
                        } else if (item.equals(
                                resources.getString(R.string.yesterday),
                                ignoreCase = true
                            )
                        ) {
                            binding!!.layoutDateRange.visibility = View.GONE
                            val calendar = Calendar.getInstance()
                            calendar.add(Calendar.DAY_OF_YEAR, -1)
                            val yesterday = calendar.time
                            val sdf = SimpleDateFormat(myFormat, Locale.US)
                            val yesterdayDate = sdf.format(yesterday)
                            fromDate = yesterdayDate
                            toDate = yesterdayDate
                        } else if (item.equals(
                                resources.getString(R.string.current_month),
                                ignoreCase = true
                            )
                        ) {
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
                            fromDate = date1
                            toDate = date2
                        } else if (item.equals(
                                resources.getString(R.string.previous_month),
                                ignoreCase = true
                            )
                        ) {
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
                            fromDate = date1
                            toDate = date2
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
                    }
                }
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {
            }
        }


        binding!!.textFromDate.setOnClickListener {
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
            val minDateInMillis = myCalendarToDate.timeInMillis
            datePickerDialog.datePicker.maxDate = minDateInMillis
            datePickerDialog.show()
        }


        binding!!.textToDate.setOnClickListener {
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
            val minDateInMillis = myCalendarFromDate.timeInMillis
            datePickerDialog.datePicker.minDate = minDateInMillis
            datePickerDialog.show()
        }
    }


    private fun fetchComplaintList() {
        showProgress(resources.getString(R.string.please_wait))

        Log.d(
            "Parameter--",
            ("UserId: " + prefManager!!.useR_Id + " DistrictId: " + districtId + " StatusId: " + complainStatusId
                    + " FPS: " + fpscodee + " From: " + fromDate + " To: " + toDate + " Pagination: " + isPagination
                    + " Page: " + currentPage + " PageSize: " + pageSize)
        )

        val service = RetrofitInstance.getRetrofitInstance().create(
            APIService::class.java
        )
        val call = service.getComplaintListDistStatusDateWiseNew(
            prefManager!!.useR_Id, districtId, complainStatusId, fromDate, toDate,
            isPagination, currentPage.toString(), pageSize, fpscodee
        )

        call.enqueue(object : Callback<ModelComplaint?> {
            override fun onResponse(
                call: Call<ModelComplaint?>,
                response: Response<ModelComplaint?>
            ) {
                hideProgress()
                if (response.isSuccessful) {
                    val model = response.body()
                    if (model != null && model.status == "200") {
                        val totalPage = model.getTotalPages()
                        val CurrentPage = model.getCurrentPage()
                        TotalItems = model.getTotalItems()

                        Log.d("CheckNow--", "$totalPage $CurrentPage $TotalItems")

                        if (currentPage == 1) {
                            // Fresh search: Clear and set new data
                            modelComplaintList.clear()
                            modelComplaintList.addAll(model.complaint_List)
                            setDataIntoAdapter(modelComplaintList)
                        } else {
                            // Scrolling: Add more data
                            adapter!!.addData(model.complaint_List)
                        }

                        if (currentPage == totalPage) {
                            isLastPage = true
                        }

                        isLoading = false
                    } else {
                        modelComplaintList.clear()
                        setDataIntoAdapter(modelComplaintList)
                        isLoading = false
                        Toast.makeText(
                            mContext,
                            if (model != null) model.message else "No data",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    hideProgress()
                    isLoading = false
                    Toast.makeText(
                        mContext,
                        mContext.resources.getString(R.string.error),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<ModelComplaint?>, t: Throwable) {
                hideProgress()
                isLoading = false
                Toast.makeText(
                    mContext,
                    mContext.resources.getString(R.string.error_message),
                    Toast.LENGTH_SHORT
                ).show()
                Log.d("error----", "is" + t.message)
            }
        })
    }
}