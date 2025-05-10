package com.callmangement.ui.errors.activity

import android.R.layout
import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.DatePicker
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
import com.callmangement.R
import com.callmangement.custom.CustomActivity
import com.callmangement.databinding.ActivityErrorPosdistBinding
import com.callmangement.model.district.ModelDistrict
import com.callmangement.model.district.ModelDistrictList
import com.callmangement.model.expense.ModelExpenseStatus
import com.callmangement.network.APIService
import com.callmangement.network.RetrofitInstance
import com.callmangement.ui.complaint.ComplaintViewModel
import com.callmangement.ui.errors.adapter.ErrorPosAdapter
import com.callmangement.ui.errors.model.GetErrorTypesRoot
import com.callmangement.ui.errors.model.GetErrortypesDatum
import com.callmangement.ui.errors.model.GetPosDeviceErrorDatum
import com.callmangement.ui.errors.model.GetPosDeviceErrorsRoot
import com.callmangement.ui.home.MainActivity
import com.callmangement.utils.Constants.isNetworkAvailable
import com.callmangement.utils.EqualSpacingItemDecoration
import com.callmangement.utils.PrefManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Collections
import java.util.Locale
import java.util.Objects

class ErrorPosActivity : CustomActivity(), View.OnClickListener {
    private var binding: ActivityErrorPosdistBinding? = null
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
    private var districtId: String? = "0"
    private val districtIdd = "0"
    private var fromDate = ""
    private var toDate = ""
    var itt: String? = null
    private var district_List: MutableList<ModelDistrictList?>? = ArrayList()
    var swipeRefreshLayout: SwipeRefreshLayout? = null
    var adapter: ErrorPosAdapter? = null
    var getPosDeviceErrorDatumArrayList: ArrayList<GetPosDeviceErrorDatum>? = null
    var fpscodee: String? = null
    var ErrorTypeId: String? = null

    private val playerNames = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityErrorPosdistBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        initView()
    }

    private fun initView() {
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
                resources.getString(R.string.pos_distribution_errors)
            binding!!.tvAdd.visibility = View.GONE
        } else if (prefManager!!.useR_TYPE_ID == "2" && prefManager!!.useR_TYPE.equals(
                "Manager",
                ignoreCase = true
            )
        ) {
            binding!!.actionBar.textToolbarTitle.text =
                resources.getString(R.string.pos_distribution_errors)
            binding!!.tvAdd.visibility = View.GONE
        } else if (prefManager!!.useR_TYPE_ID == "4" && prefManager!!.useR_TYPE.equals(
                "ServiceEngineer",
                ignoreCase = true
            )
        ) {
            binding!!.actionBar.textToolbarTitle.text =
                resources.getString(R.string.pos_distribution_errors)
            binding!!.tvAdd.visibility = View.VISIBLE


            // Log.d("thfghfh"," "+districtIdd);
            //   districtId = String.valueOf(district_List.get(Integer.parseInt(districtIdd)));
            //  binding.spinnerDistrict.setSelection(Integer.parseInt(districtId));
        }
        viewModel = ViewModelProviders.of(this).get(
            ComplaintViewModel::class.java
        )
        fetchSpinnersata()
        setUpOnclickListener()
        setUpLayout()
        districtList()
        setUpDateRangeSpinner()
        setUpExpenseStatusSpinner()
        setupfilter()


        //      getExpenseList(expenseStatusId,districtId,fromDate,toDate);
        binding!!.btnsearch.setOnClickListener { //String fps = binding.inputSearch.getText().toString();
            fpscodee = binding!!.inputSearch.text.toString()
            getPosError(expenseStatusId, districtId, fromDate, toDate, fpscodee)
        }


        //     adapter = new ErrorPosAdapter(mContext, getPosDeviceErrorDatumArrayList,prefManager.getUseR_TYPE_ID());
        //     adapter.notifyDataSetChanged();
        //    binding.rvExpenses.setHasFixedSize(true);
        //    binding.rvExpenses.setLayoutManager(new LinearLayoutManager(ErrorPosActivity.this, LinearLayoutManager.VERTICAL, false));
        //   binding.rvExpenses.setAdapter(adapter);
    }


    private fun fetchSpinnersata() {
        if (isNetworkAvailable(mContext!!)) {
            showProgress()
            val service = RetrofitInstance.getRetrofitInstance().create(
                APIService::class.java
            )
            val call = service.GetErrorTypes(prefManager!!.useR_Id, "0")
            call.enqueue(object : Callback<GetErrorTypesRoot?> {
                override fun onResponse(
                    call: Call<GetErrorTypesRoot?>,
                    response: Response<GetErrorTypesRoot?>
                ) {
                    hideProgress()
                    if (response.isSuccessful) {
                        if (response.code() == 200) {
                            if (response.body() != null) {
                                if (response.body()!!.status == "200") {
                                    val getErrorTypesRoot = response.body()
                                    //   Log.d("getErrorTypesRoot..","getErrorTypesRoot.."+getErrorTypesRoot);
                                    val selecttype = GetErrortypesDatum("---SelectErrorType----", 0)
                                    val getErrortypesDatum =
                                        getErrorTypesRoot!!.data

                                    val getErrortypesDatum1 =
                                        ArrayList<GetErrortypesDatum>()
                                    getErrortypesDatum1.add(selecttype)


                                    //  playerNames.add("--" + getResources().getString(R.string.select_errortype) + "--");
                                    playerNames.add(selecttype.errorType)


                                    //   playerNames.add("--" + getResources().getString(R.string.select_errortype) + "--");
                                    for (i in getErrortypesDatum.indices) {
                                        playerNames.add(getErrortypesDatum[i].errorType)
                                        getErrortypesDatum1.add(getErrortypesDatum[i])
                                    }


                                    val spinnerArrayAdapter = ArrayAdapter(
                                        this@ErrorPosActivity,
                                        layout.simple_spinner_item,
                                        playerNames
                                    )
                                    spinnerArrayAdapter.setDropDownViewResource(layout.simple_spinner_dropdown_item)
                                    binding!!.spinnererrortype.adapter = spinnerArrayAdapter
                                    binding!!.spinnererrortype.onItemSelectedListener =
                                        object : AdapterView.OnItemSelectedListener {
                                            override fun onItemSelected(
                                                adapterView: AdapterView<*>?,
                                                view: View,
                                                i: Int,
                                                l: Long
                                            ) {
                                                ErrorTypeId =
                                                    getErrortypesDatum1[i].errorTypeId.toString()

                                                //  Log.d("modelExpensestatuslist", " " + ErrorTypeId);
                                            }

                                            override fun onNothingSelected(adapterView: AdapterView<*>?) {
                                            }
                                        }
                                    val msg = getErrorTypesRoot.message
                                    val status = getErrorTypesRoot.status

                                    /*  ArrayList<GetErrortypesDatum>  getErrortypesDatum =
                                            getErrorTypesRoot.getData();
                                    String msg =  getErrorTypesRoot.getMessage();
                                    String status =  getErrorTypesRoot.getStatus();

                                     JSONArray jsonArray = getErrortypesDatum
                                    String CreatedCampCount = getErrortypesDatum.get(Ar).;
                                    String OrganizedCampCount = dashboardUserDetails.getOrganizedCampCount();
                                    String AttendanceCount = dashboardUserDetails.getAttendanceCount();
                                    binding.textcreatedCampCount.setText(CreatedCampCount);
                                    binding.textCampsCount.setText(OrganizedCampCount);
                                    binding.textAttendanceCount.setText(AttendanceCount);*/
                                    //   Log.d("ghmn", "asmbnmldjfjas" + CreatedCampCount + "  " + OrganizedCampCount + " " + AttendanceCount);
                                } else {
                                    makeToast(response.body()!!.message.toString())
                                }
                            } else {
                                Log.i(
                                    "onEmptyResponse",
                                    "Returned empty response"
                                ) //Toast.makeText(getContext(),"Nothing returned",Toast.LENGTH_LONG).show();
                            }
                        } else {
                            makeToast(resources.getString(R.string.error))
                        }
                    } else {
                        makeToast(resources.getString(R.string.error))
                    }
                }

                override fun onFailure(call: Call<GetErrorTypesRoot?>, t: Throwable) {
                    hideProgress()
                    makeToast(resources.getString(R.string.error_message))
                }
            })
        } else {
            makeToast(resources.getString(R.string.no_internet_connection))
        }
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

                            //     getPosError(expenseStatusId,districtId,fromDate,toDate,fpscodee);
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

                            //   getPosError(expenseStatusId,districtId,fromDate,toDate,fpscodee);
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

                            //      getPosError(expenseStatusId,districtId,fromDate,toDate,fpscodee);
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

                            //       getPosError(expenseStatusId,districtId,fromDate,toDate,fpscodee);
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
                        //     getPosError(expenseStatusId,districtId,fromDate,toDate,fpscodee);
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


                        //     getPosError(expenseStatusId,districtId,fromDate,toDate,fpscodee);
                    }
                }

                override fun onNothingSelected(adapterView: AdapterView<*>?) {
                }
            }


        /* binding.inputFpsCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!charSequence.toString().isEmpty()){

                     List<PosDistributionDetail> filterList = new ArrayList<>();
                    if (userReportList.size() > 0){
                        for (PosDistributionDetail model : userReportList){
                            if (model.getFpscode().contains(charSequence.toString()))
                                filterList.add(model);
                        }
                    }
                    setAdapter(filterList);
                } else setAdapter(userReportList);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
*/
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
                        Log.d("modelExpensestatuslist", " $expenseStatusId")
                        //     getPosError(expenseStatusId,districtId,fromDate,toDate,fpscodee);
                    }
                }

                override fun onNothingSelected(adapterView: AdapterView<*>?) {
                }
            }

        binding!!.actionBar.ivBack.setOnClickListener(this)
        binding!!.textFromDate.setOnClickListener(this)
        binding!!.textToDate.setOnClickListener(this)
        binding!!.tvAdd.setOnClickListener(this)
    }


    private fun setupfilter() {
        adapter = ErrorPosAdapter(
            mContext!!, getPosDeviceErrorDatumArrayList!!,
            prefManager!!.useR_TYPE_ID!!
        )
        adapter!!.notifyDataSetChanged()
        binding!!.rvExpenses.setHasFixedSize(true)
        binding!!.rvExpenses.layoutManager =
            LinearLayoutManager(this@ErrorPosActivity, LinearLayoutManager.VERTICAL, false)
        binding!!.rvExpenses.addItemDecoration(
            EqualSpacingItemDecoration(
                30,
                EqualSpacingItemDecoration.VERTICAL
            )
        )
        binding!!.rvExpenses.adapter = adapter

        /*  binding.inputSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {



            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
              //  adapter.getFilter().filter(charSequence);
                fpscodee = charSequence.toString();
                getPosError(expenseStatusId,districtId,fromDate,toDate,fpscodee);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
*/
        binding!!.actionBar.ivBack.setOnClickListener { view: View? -> onBackPressed() }
    }


    private fun setUpLayout() {
        if (prefManager!!.useR_TYPE_ID == "1" && prefManager!!.useR_TYPE.equals(
                "Admin",
                ignoreCase = true
            )
        ) {
            binding!!.rlDistrict.visibility = View.VISIBLE
            binding!!.seDistrict.visibility = View.GONE
            binding!!.spacer.visibility = View.VISIBLE

            val sp = getSharedPreferences("your_prefs", MODE_PRIVATE)
            val editor = sp.edit()
            editor.remove("districtId_key")
            editor.apply()
        } else if (prefManager!!.useR_TYPE_ID == "2" && prefManager!!.useR_TYPE.equals(
                "Manager",
                ignoreCase = true
            )
        ) {
            binding!!.rlDistrict.visibility = View.VISIBLE
            binding!!.spacer.visibility = View.VISIBLE
            binding!!.seDistrict.visibility = View.GONE


            val sp = getSharedPreferences("your_prefs", MODE_PRIVATE)
            val editor = sp.edit()
            editor.remove("districtId_key")
            editor.apply()
        } else if (prefManager!!.useR_TYPE_ID == "4" && prefManager!!.useR_TYPE.equals(
                "ServiceEngineer",
                ignoreCase = true
            )
        ) {
            binding!!.rlDistrict.visibility = View.GONE
            binding!!.spacer.visibility = View.VISIBLE
            binding!!.seDistrict.visibility = View.VISIBLE
            districtId = intent.getStringExtra("districtId")
            if (districtIdd != null && !districtIdd.isEmpty()) {
                try {
                    val sp = getSharedPreferences("your_prefs", MODE_PRIVATE)
                    val editor = sp.edit()
                    editor.putInt("districtId_key", districtId!!.toInt())
                    editor.commit()
                } catch (e: NumberFormatException) {
                }
            } else {
            }


            // districtId = prefManager.getUseR_DistrictId();
        }

        val calendar = Calendar.getInstance()
        val today = calendar.time
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        val todayDate = sdf.format(today)
        fromDate = todayDate
        toDate = todayDate
        getPosError(expenseStatusId, districtId, fromDate, toDate, fpscodee)
        Log.d("useriduserid", prefManager!!.useR_TYPE_ID!!)




        swipeRefreshLayout = findViewById(R.id.refreshLayout)

        swipeRefreshLayout!!.setOnRefreshListener(OnRefreshListener {
            swipeRefreshLayout!!.setRefreshing(false)
            getPosError(expenseStatusId, districtId, fromDate, toDate, fpscodee)
        })
    }

    private fun setUpDateRangeSpinner() {
        spinnerList.add("--" + resources.getString(R.string.select_filter) + "--")
        spinnerList.add(resources.getString(R.string.today))
        spinnerList.add(resources.getString(R.string.yesterday))
        spinnerList.add(resources.getString(R.string.current_month))
        spinnerList.add(resources.getString(R.string.previous_month))
        spinnerList.add(resources.getString(R.string.custom_filter))
        val dataAdapter = ArrayAdapter(this, layout.simple_spinner_item, spinnerList)
        dataAdapter.setDropDownViewResource(layout.simple_spinner_dropdown_item)
        binding!!.spinner.adapter = dataAdapter
        binding!!.spinner.setSelection(1)
        val selectedString = binding!!.spinner.selectedItem as String
        val selectedItemPosition = binding!!.spinner.selectedItemPosition
        if (selectedItemPosition == 1) {
            val calendar = Calendar.getInstance()
            val today = calendar.time
            val sdf = SimpleDateFormat(myFormat, Locale.US)
            val todayDate = sdf.format(today)
            fromDate = todayDate
            toDate = todayDate
        } else {
        }
    }

    private fun setUpExpenseStatusSpinner() {
        val model1 = ModelExpenseStatus()
        model1.id = "0"
        model1.expense_status = "--" + resources.getString(R.string.errorstatus) + "--"
        modelExpenseStatusList.add(model1)
        val model2 = ModelExpenseStatus()
        model2.id = "1"
        model2.expense_status = resources.getString(R.string.pending)
        modelExpenseStatusList.add(model2)
        val model3 = ModelExpenseStatus()
        model3.id = "2"
        model3.expense_status = resources.getString(R.string.checking)
        modelExpenseStatusList.add(model3)
        val model4 = ModelExpenseStatus()
        model4.id = "3"
        model4.expense_status = resources.getString(R.string.resolved)
        modelExpenseStatusList.add(model4)
        val dataAdapter = ArrayAdapter(
            mContext!!, layout.simple_spinner_item, modelExpenseStatusList
        )
        dataAdapter.setDropDownViewResource(layout.simple_spinner_dropdown_item)
        binding!!.spinnerExpenseStatus.adapter = dataAdapter
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
        //    getPosError(expenseStatusId,districtId,fromDate,toDate,fpscodee);
    }

    private fun districtList() {
        viewModel!!.district.observe(
            this,
            Observer<ModelDistrict?> { modelDistrict: ModelDistrict? ->
                if (modelDistrict!!.status == "200") {
                    district_List = modelDistrict.district_List
                    if (district_List != null && district_List!!.size > 0) {
                        district_List!!.reverse()
                        val l = ModelDistrictList()
                        l.districtId = (-1).toString()
                        l.districtNameEng = "--" + resources.getString(R.string.district) + "--"
                        district_List!!.add(l)
                        district_List!!.reverse()
                        val dataAdapter = ArrayAdapter(
                            mContext!!, layout.simple_spinner_item,
                            district_List!!
                        )
                        dataAdapter.setDropDownViewResource(layout.simple_spinner_dropdown_item)
                        binding!!.spinnerDistrict.adapter = dataAdapter

                        val sp = getSharedPreferences("your_prefs", MODE_PRIVATE)
                        val myIntValue = sp.getInt("districtId_key", -1)
                        Log.d("districid", "" + myIntValue)

                        if (myIntValue != -1) {
                            val itt = district_List!![myIntValue].toString()
                            Log.d("district_List", " $itt")
                            if (itt != null) {
                                binding!!.seDistrict.text = itt
                            }
                        } else {
                        }


                        //  binding.spinnerDistrict.setSelection(Integer.parseInt(itt));
                        // binding.spinnerDistrict.setSelection(Integer.parseInt(itt));
                    }
                }
            })
    }

    private fun getPosError(
        expenseStatusId: String,
        districtId: String?,
        fromDate: String,
        toDate: String,
        fpscodee: String?
    ) {
        Log.d("fromdate", "  $fromDate$toDate")
        Log.d("districtId", "  $districtId")
        Log.d("expenseStatusId", "  $expenseStatusId")
        Log.d("ErrorTypeId", "  $ErrorTypeId")
        if (isNetworkAvailable(mContext!!)) {
            showProgress()
            val service = RetrofitInstance.getRetrofitInstance().create(
                APIService::class.java
            )
            val call = service.GetPOSDeviceErrors(
                "",
                prefManager!!.useR_Id,
                fpscodee,
                districtId,
                "",
                expenseStatusId,
                fromDate,
                toDate,
                ErrorTypeId
            )
            call.enqueue(object : Callback<GetPosDeviceErrorsRoot?> {
                @SuppressLint("SetTextI18n")
                override fun onResponse(
                    call: Call<GetPosDeviceErrorsRoot?>,
                    response: Response<GetPosDeviceErrorsRoot?>
                ) {
                    hideProgress()
                    if (response.isSuccessful) {
                        try {
                            if (response.code() == 200) {
                                if (response.body() != null) {
                                    if (response.body()!!.status == "200") {
                                        val getPosDeviceErrorsRoot = response.body()
                                        Log.d(
                                            "getErrorTypesRoot..",
                                            "getErrorTypesRoot..$getPosDeviceErrorsRoot"
                                        )
                                        getPosDeviceErrorDatumArrayList =
                                                //  ArrayList<GetPosDeviceErrorDatum>  getPosDeviceErrorDatumArrayList =
                                            getPosDeviceErrorsRoot!!.data
                                        if (getPosDeviceErrorDatumArrayList!!.size > 0) {
                                            binding!!.rvExpenses.visibility = View.VISIBLE
                                            binding!!.textNoDataFound.visibility = View.GONE
                                            //  List<ModelExpensesList> modelExpensesList = modelResponse.getExpensesList();
                                            setUpAdapter(getPosDeviceErrorDatumArrayList!!)
                                        } else {
                                            binding!!.rvExpenses.visibility = View.GONE
                                            binding!!.textNoDataFound.visibility = View.VISIBLE
                                        }


                                        /*  ArrayList<GetErrortypesDatum>  getErrortypesDatum =
                                            getErrorTypesRoot.getData();
                                    String msg =  getErrorTypesRoot.getMessage();
                                    String status =  getErrorTypesRoot.getStatus();

                                     JSONArray jsonArray = getErrortypesDatum

                                    String CreatedCampCount = getErrortypesDatum.get(Ar).;
                                    String OrganizedCampCount = dashboardUserDetails.getOrganizedCampCount();
                                    String AttendanceCount = dashboardUserDetails.getAttendanceCount();
                                    binding.textcreatedCampCount.setText(CreatedCampCount);
                                    binding.textCampsCount.setText(OrganizedCampCount);
                                    binding.textAttendanceCount.setText(AttendanceCount);*/
                                        //   Log.d("ghmn", "asmbnmldjfjas" + CreatedCampCount + "  " + OrganizedCampCount + " " + AttendanceCount);
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

                override fun onFailure(call: Call<GetPosDeviceErrorsRoot?>, t: Throwable) {
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

    private fun setUpAdapter(getPosDeviceErrorDatumArrayList: ArrayList<GetPosDeviceErrorDatum>) {
        binding!!.rvExpenses.layoutManager =
            LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false)
        binding!!.rvExpenses.adapter = ErrorPosAdapter(
            mContext!!, getPosDeviceErrorDatumArrayList,
            prefManager!!.useR_TYPE_ID!!
        )
    }

    override fun onResume() {
        super.onResume()
        //   getPosError(expenseStatusId,districtId,fromDate,toDate,fpscodee);
    }

    override fun onClick(view: View) {
        val id = view.id
        if (id == R.id.iv_back) {
            // onBackPressed();
            val i = Intent(this@ErrorPosActivity, MainActivity::class.java)
            startActivity(i)
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
                mContext!!, dateToDate, myCalendarToDate[Calendar.YEAR],
                myCalendarToDate[Calendar.MONTH],
                myCalendarToDate[Calendar.DAY_OF_MONTH]
            )
            datePickerDialog.datePicker.maxDate = System.currentTimeMillis() - 1000
            datePickerDialog.show()
        } else if (id == R.id.tv_add) {
            val i = Intent(this@ErrorPosActivity, AddnewErrorPosSE::class.java)
            startActivity(i)
        }
    }

    override fun onBackPressed() {
        //  super.onBackPressed();
        val i = Intent(this@ErrorPosActivity, MainActivity::class.java)
        startActivity(i)
    }
}