package com.callmangement.ui.biometric_delivery

import android.app.Activity
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.content.Context
import android.os.Bundle
import android.os.Vibrator
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.DatePicker
import android.widget.Spinner
import android.widget.Toast
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.callmangement.network.APIService
import com.callmangement.network.RetrofitInstance
import com.callmangement.R
import com.callmangement.custom.CustomActivity
import com.callmangement.databinding.ActivityBiometricDeliveryListBinding
import com.callmangement.ui.biometric_delivery.adapter.SensorDistributionListAdapter
import com.callmangement.ui.biometric_delivery.model.SensorDistributionDetailsListResp
import com.callmangement.ui.ins_weighing_scale.model.district.ModelDistrictList_w
import com.callmangement.ui.ins_weighing_scale.model.district.ModelDistrict_w
import com.callmangement.utils.Constants
import com.callmangement.utils.PrefManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Collections
import java.util.Locale
import java.util.Objects

class BiometricDeliveryListActivity : CustomActivity() {

    
    private var binding: ActivityBiometricDeliveryListBinding? = null
    private val spinnerList: MutableList<String> = ArrayList()
    private var mActivity: Activity? = null
    private var prefManager: PrefManager? = null
    private var spinnerDistrict: Spinner? = null
    private var checkDistrict = 0
    private var districtNameEng = ""
    private var districtId: String? = "0"
    private val districtIdd = "0"
    private val myFormat = "yyyy-MM-dd"
    private var fromDate: String? = ""
    private var toDate: String? = ""
    private val myCalendarFromDate: Calendar = Calendar.getInstance()
    private val myCalendarToDate: Calendar = Calendar.getInstance()
    private var district_List: MutableList<ModelDistrictList_w?>? = ArrayList()
    private var dis: String? = null
    private var checkFilter = 0
    private var fpscodee: String? = null
    private var value_selectedDay: String? = null
    private var value_selectedDis: String? = null
    private var FILTER_TYPE_ID: String? = ""
    private var vibrator: Vibrator? = null
    var sensorDistributionList: ArrayList<SensorDistributionDetailsListResp.Data> = ArrayList()
    private var sensorDistributionListAdapter: SensorDistributionListAdapter? = null
    private var isLoading = false
    private var allPagesLoaded = false
    private var currentPage = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE) //will hide the title
        // getSupportActionBar().hide(); // hide the title bar
        binding = ActivityBiometricDeliveryListBinding.inflate(
            layoutInflater
        )
        setContentView(binding!!.root)
        init()
    }

    private fun init() {
        //   clearSharePreference();
        mActivity = this
        mContext = this
        prefManager = PrefManager(mContext!!)
        binding!!.actionBar.ivBack.visibility = View.VISIBLE
        //    binding.actionBar.buttonPDF.setVisibility(View.VISIBLE);
        //  binding.actionBar.buttonPDF.setImageDrawable(getDrawable(R.drawable.excel));
        vibrator = getSystemService(VIBRATOR_SERVICE) as Vibrator
        binding!!.actionBar.textToolbarTitle.text =
            resources.getString(R.string.biometric_delivery_list)

        setUpData()
        setBinding()
        setClickListener()

        //  setClickListener();
    }

    private fun setUpData() {
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
        }

        val USER_NAME = prefManager!!.useR_NAME
        val USER_EMAIL = prefManager!!.useR_EMAIL
        val USER_Mobile = prefManager!!.useR_Mobile
        val USER_DISTRICT = prefManager!!.useR_District
        Log.d("USER_NAME", " $USER_NAME")

        binding!!.seDistrict.text = USER_DISTRICT
        spinnerDistrict = findViewById(R.id.spinnerDistrict)
        districtList()
        districtNameEng = "--" + resources.getString(R.string.district) + "--"

        val calendar = Calendar.getInstance()
        val today = calendar.time
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        val todayDate = sdf.format(today)
        fromDate = todayDate
        toDate = todayDate

        binding!!.rvDelivered.layoutManager = LinearLayoutManager(mContext!!)
        binding!!.rvDelivered.addItemDecoration(
            DividerItemDecoration(
                mContext!!,
                DividerItemDecoration.VERTICAL
            )
        )
        sensorDistributionListAdapter = SensorDistributionListAdapter(sensorDistributionList)
        binding!!.rvDelivered.adapter = sensorDistributionListAdapter

        binding!!.rvDelivered.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val layoutManager = recyclerView.layoutManager as LinearLayoutManager?
                val visibleItemCount = layoutManager!!.childCount
                val totalItemCount = layoutManager.itemCount
                val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

                if (!isLoading && !allPagesLoaded && (visibleItemCount + firstVisibleItemPosition) >= totalItemCount && firstVisibleItemPosition >= 0) {
                    isLoading = true
                    getSensorDistributionList(districtId, fromDate, toDate)
                    //    currentPage++;
                }
            }
        })


        val intent = intent
        // Get the value from the intent using the key
        if (intent != null) {
            val value_districtId = intent.getStringExtra("key_districtId")
            val value_fromDate = intent.getStringExtra("key_fromDate")
            val value_toDate = intent.getStringExtra("key_toDate")

            value_selectedDay = intent.getStringExtra("key_selectedDate")
            value_selectedDis = intent.getStringExtra("key_selectedDis")
            FILTER_TYPE_ID = intent.getStringExtra("FILTER_TYPE_ID")
            Log.d("value_selectedDay", value_selectedDay!!)

            districtId = value_districtId
            fromDate = value_fromDate
            toDate = value_toDate
            getSensorDistributionList(districtId, fromDate, toDate)
        } else {
            binding!!.spinner.setSelection(1)
            getSensorDistributionList(districtId, fromDate, toDate)
        }

        setUpDateRangeSpinner()
    }

    private fun setBinding() {
    }

    private fun setClickListener() {
        binding!!.actionBar.ivBack.setOnClickListener { onBackPressed() }

        //  binding.ivChallanImage.setOnClickListener(this);
        spinnerDistrict!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>?, view: View, i: Int, l: Long) {
                if (++checkDistrict > 1) {
                    districtNameEng = district_List!![i]!!.districtNameEng
                    dis = district_List!![i]!!.districtNameEng
                    Log.d("dfgfd", " $dis")
                    districtId = district_List!![i]!!.districtId
                    Log.d("fggfgh", " $districtId")
                    val editor = getSharedPreferences("MyPrefs", MODE_PRIVATE).edit()
                    editor.putString("dis", dis)
                    editor.putString("districtId", districtId)
                    editor.apply()

                    /*  if (districtNameEng.equalsIgnoreCase("--" + getResources().getString(R.string.district) + "--")) {
                                    SEUser_list.clear();
                                    Collections.reverse(SEUser_list);
                                    ModelSEUserList list = new ModelSEUserList();
                                    list.setUserId(valueOf(-1));
                                    list.setUserName("--" + getResources().getString(R.string.se_user) + "--");
                                    SEUser_list.add(list);
                                    Collections.reverse(SEUser_list);
                                    ArrayAdapter<ModelSEUserList> dataAdapter = new ArrayAdapter<>(mActivity, android.R.layout.simple_spinner_item, SEUser_list);
                                    dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                    spinnerSEUsers.setAdapter(dataAdapter);
                                } else {
                                    SEUsersList(districtId);
                                }*/
                }
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {
            }
        }

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
                            val sdf = SimpleDateFormat(myFormat, Locale.US)
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

                            //      getPosError(expenseStatusId,districtId,fromDate,toDate,fpscodee);
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
                mContext!!, dateFromDate,
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
                mContext!!, dateToDate,
                myCalendarToDate[Calendar.YEAR],
                myCalendarToDate[Calendar.MONTH],
                myCalendarToDate[Calendar.DAY_OF_MONTH]
            )
            datePickerDialog.datePicker.maxDate = System.currentTimeMillis() - 1000
            val minDateInMillis = myCalendarFromDate.timeInMillis
            datePickerDialog.datePicker.minDate = minDateInMillis
            datePickerDialog.show()
        }

        binding!!.btnsearch.setOnClickListener {
            currentPage = 1
            allPagesLoaded = false
            sensorDistributionList.clear()
            sensorDistributionListAdapter!!.notifyDataSetChanged()
            getSensorDistributionList(districtId, fromDate, toDate)
        }
    }

    private fun getSensorDistributionList(districtId: String?, fromDate: String?, toDate: String?) {
        if (Constants.isNetworkAvailable(mActivity!!)) {
            hideKeyboard(mActivity)
            showProgress()

            fpscodee = binding!!.inputfps.text.toString()

            var seUserId: String? = "0"
            val USER_Id = prefManager!!.useR_Id
            Log.d("USER_ID", " $USER_Id")
            Log.d("myDistrictId", " $districtId")
            Log.d("myfromDate", " $fromDate")
            Log.d("mytoDate", " $toDate")

            if (prefManager!!.useR_TYPE_ID == "4" && prefManager!!.useR_TYPE
                    .equals("ServiceEngineer", ignoreCase = true)
            ) {
                seUserId = USER_Id
            }

            val apiInterface = RetrofitInstance.getRetrofitInstance().create(
                APIService::class.java
            )
            val call = apiInterface.getSensorDistributionDetailsList(
                USER_Id, fpscodee, districtId,
                "", "", FILTER_TYPE_ID, "1", "" + currentPage, "50", seUserId, fromDate, toDate
            )

            call.enqueue(object : Callback<SensorDistributionDetailsListResp?> {
                override fun onResponse(
                    call: Call<SensorDistributionDetailsListResp?>,
                    response: Response<SensorDistributionDetailsListResp?>
                ) {
                    hideProgress()

                    if (response.isSuccessful) {
                        if (response.code() == 200) {
                            if (response.body() != null) {
                                binding!!.txtNoRecord.visibility = View.GONE


                                if (response.body()!!.status == "200") {
                                    //                    binding.actionBar.buttonPDF.setVisibility(View.VISIBLE);

                                    val sensorSummaryResponse = response.body()
                                    val message = sensorSummaryResponse!!.message

                                    allPagesLoaded =
                                        if (sensorSummaryResponse.currentPage == sensorSummaryResponse.totalPages) true
                                        else false

                                    //     sensorDistributionList = sensorSummaryResponse.getData();
                                    sensorDistributionList.addAll(sensorSummaryResponse.data!!)
                                    sensorDistributionListAdapter!!.notifyDataSetChanged()
                                    Log.d(
                                        "sensorDistributionList",
                                        "Size...." + sensorDistributionList.size
                                    )

                                    //                                    binding.rvDelivered.setLayoutManager(new LinearLayoutManager(mContext!!));
//                                    binding.rvDelivered.addItemDecoration(new DividerItemDecoration(mContext!!, DividerItemDecoration.VERTICAL));
//                                    binding.rvDelivered.setVisibility(View.VISIBLE);
//                                    SensorDistributionListAdapter sensorDistributionListAdapter = new SensorDistributionListAdapter(sensorDistributionList);
//                                    binding.rvDelivered.setAdapter(sensorDistributionListAdapter);
                                    binding!!.rvDelivered.visibility = View.VISIBLE

                                    currentPage++
                                } else {
                                    binding!!.txtNoRecord.visibility = View.VISIBLE
                                    binding!!.rvDelivered.visibility = View.GONE
                                    showAlertDialogWithSingleButton(
                                        mActivity, response.body()!!
                                            .message
                                    )
                                    binding!!.actionBar.buttonPDF.visibility = View.GONE
                                }
                            } else {
                                binding!!.txtNoRecord.visibility = View.VISIBLE
                                binding!!.rvDelivered.visibility = View.GONE
                                binding!!.actionBar.buttonPDF.visibility = View.GONE
                                showAlertDialogWithSingleButton(
                                    mActivity, response.body()!!
                                        .message
                                )
                            }
                        } else {
                            val msg = "HTTP Error: " + response.code()
                            showAlertDialogWithSingleButton(mActivity, msg)
                            binding!!.actionBar.buttonPDF.visibility = View.GONE
                        }
                    } else {
                        val msg = "HTTP Error: " + response.code()
                        showAlertDialogWithSingleButton(mActivity, msg)
                        binding!!.actionBar.buttonPDF.visibility = View.GONE
                    }
                    isLoading = false
                }

                override fun onFailure(
                    call: Call<SensorDistributionDetailsListResp?>,
                    error: Throwable
                ) {
                    hideProgress()
                    showAlertDialogWithSingleButton(mActivity, error.message)
                    binding!!.actionBar.buttonPDF.visibility = View.GONE
                    call.cancel()
                    isLoading = false
                }
            })
        } else {
            makeToast(resources.getString(R.string.no_internet_connection))
            binding!!.actionBar.buttonPDF.visibility = View.GONE
            isLoading = false
        }
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
        if (Constants.isNetworkAvailable(mActivity!!)) {
            hideKeyboard(mActivity)
            hideProgress()
            val USER_Id = prefManager!!.useR_Id
            val apiInterface = RetrofitInstance.getRetrofitInstance().create(
                APIService::class.java
            )
            val call = apiInterface.apiGetDistictList_w()

            call.enqueue(object : Callback<ModelDistrict_w?> {
                override fun onResponse(
                    call: Call<ModelDistrict_w?>,
                    response: Response<ModelDistrict_w?>
                ) {
                    hideProgress()
                    if (response.isSuccessful) {
                        if (response.code() == 200) {
                            Log.d("mydataresponse", "" + response.code())
                            if (response.body() != null) {
                                if (response.body()!!.status == "200") {
                                    district_List = response.body()!!.district_List
                                    if (district_List != null && district_List!!.size > 0) {
                                        Collections.reverse(district_List)
                                        val l = ModelDistrictList_w()
                                        l.districtId = (-1).toString()
                                        l.districtNameEng =
                                            "--" + resources.getString(R.string.district) + "--"
                                        district_List!!.add(l)
                                        Collections.reverse(district_List)
                                        val dataAdapter = ArrayAdapter(
                                            mActivity!!, android.R.layout.simple_spinner_item,
                                            district_List!!
                                        )
                                        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                                        spinnerDistrict!!.adapter = dataAdapter
                                        binding!!.spinnerDistrict.setSelection(value_selectedDis!!.toInt())
                                    }
                                } else {
                                    makeToast(response.body()!!.message)
                                }
                            } else {
                                makeToast(resources.getString(R.string.error))
                            }
                        } else {
                            makeToast(resources.getString(R.string.error))
                        }
                    } else {
                        makeToast(resources.getString(R.string.error))
                    }
                }

                override fun onFailure(call: Call<ModelDistrict_w?>, error: Throwable) {
                    hideProgress()
                    makeToast(resources.getString(R.string.error))
                    call.cancel()
                }
            })
        } else {
            makeToast(resources.getString(R.string.no_internet_connection))
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
        binding!!.spinner.setSelection(value_selectedDay!!.toInt())

        //  binding.spinner.setSelection(1);
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

    override fun onBackPressed() {
        setResult(RESULT_OK)
        finish()
    }

    override fun makeToast(string: String?) {
        if (TextUtils.isEmpty(string)) return
        Toast.makeText(mActivity, string, Toast.LENGTH_SHORT).show()
    }
}
