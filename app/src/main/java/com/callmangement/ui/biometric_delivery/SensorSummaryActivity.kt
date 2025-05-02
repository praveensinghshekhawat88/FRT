package com.callmangement.ui.biometric_delivery

import android.app.Activity
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
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
import com.callmangement.R
import com.callmangement.custom.CustomActivity
import com.callmangement.databinding.ActivitySensorSummaryBinding
import com.callmangement.network.APIService
import com.callmangement.network.RetrofitInstance.retrofitInstance
import com.callmangement.ui.biometric_delivery.adapter.SensorSummaryAdapter
import com.callmangement.ui.biometric_delivery.model.SensorSummaryResponse
import com.callmangement.ui.ins_weighing_scale.model.district.ModelDistrictList_w
import com.callmangement.ui.ins_weighing_scale.model.district.ModelDistrict_w
import com.callmangement.utils.Constants.isNetworkAvailable
import com.callmangement.utils.PrefManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Collections
import java.util.Date
import java.util.Locale
import java.util.Objects

class SensorSummaryActivity() : CustomActivity() {
    private var binding: ActivitySensorSummaryBinding? = null
    private val spinnerList: MutableList<String> = ArrayList()
    private var mActivity: Activity? = null
    private var prefManager: PrefManager? = null
    private var spinnerDistrict: Spinner? = null
    private var checkDistrict: Int = 0
    private var districtNameEng: String = ""
    private var districtId: String? = "0"
    private val districtIdd: String? = "0"
    private val myFormat: String = "yyyy-MM-dd"
    private var fromDate: String? = ""
    private var toDate: String? = ""
    private val myCalendarFromDate: Calendar = Calendar.getInstance()
    private val myCalendarToDate: Calendar = Calendar.getInstance()
    private var district_List: MutableList<ModelDistrictList_w?>? = ArrayList()
    private var dis: String? = null
    private var checkFilter: Int = 0
    private var fpscodee: String? = null
    private var value_selectedDay: String? = null
    private var value_selectedDis: String? = null
    private var vibrator: Vibrator? = null
    var sensorSummaryList: ArrayList<SensorSummaryResponse.Data>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE) //will hide the title
        // getSupportActionBar().hide(); // hide the title bar
        binding = ActivitySensorSummaryBinding.inflate(getLayoutInflater())
        setContentView(binding!!.getRoot())
        init()
    }

    private fun init() {
        //   clearSharePreference();
        mActivity = this
        mContext = this
        prefManager = PrefManager(mContext!!)
        binding!!.actionBar.ivBack.setVisibility(View.VISIBLE)
        //    binding.actionBar.buttonPDF.setVisibility(View.VISIBLE);
        //  binding.actionBar.buttonPDF.setImageDrawable(getDrawable(R.drawable.excel));
        vibrator = getSystemService(VIBRATOR_SERVICE) as Vibrator?
        binding!!.actionBar.textToolbarTitle.setText(getResources().getString(R.string.summary))

        setUpData()
        setBinding()
        setClickListener()

        //  setClickListener();
    }

    private fun setUpData() {
        if ((prefManager!!.uSER_TYPE_ID == "1") && prefManager!!.uSER_TYPE.equals(
                "Admin",
                ignoreCase = true
            )
        ) {
            binding!!.rlDistrict.setVisibility(View.VISIBLE)
            binding!!.seDistrict.setVisibility(View.GONE)
            binding!!.spacer.setVisibility(View.VISIBLE)
            val sp: SharedPreferences = getSharedPreferences("your_prefs", MODE_PRIVATE)
            val editor: SharedPreferences.Editor = sp.edit()
            editor.remove("districtId_key")
            editor.apply()
        } else if ((prefManager!!.uSER_TYPE_ID == "2") && prefManager!!.uSER_TYPE.equals(
                "Manager",
                ignoreCase = true
            )
        ) {
            binding!!.rlDistrict.setVisibility(View.VISIBLE)
            binding!!.spacer.setVisibility(View.VISIBLE)
            binding!!.seDistrict.setVisibility(View.GONE)

            val sp: SharedPreferences = getSharedPreferences("your_prefs", MODE_PRIVATE)
            val editor: SharedPreferences.Editor = sp.edit()
            editor.remove("districtId_key")
            editor.apply()
        } else if ((prefManager!!.uSER_TYPE_ID == "4") && prefManager!!.uSER_TYPE.equals(
                "ServiceEngineer",
                ignoreCase = true
            )
        ) {
            binding!!.rlDistrict.setVisibility(View.GONE)
            binding!!.spacer.setVisibility(View.VISIBLE)
            binding!!.seDistrict.setVisibility(View.VISIBLE)
            districtId = getIntent().getStringExtra("districtId")
            if (districtIdd != null && !districtIdd.isEmpty()) {
                try {
                    val sp: SharedPreferences = getSharedPreferences("your_prefs", MODE_PRIVATE)
                    val editor: SharedPreferences.Editor = sp.edit()
                    editor.putInt("districtId_key", districtId!!.toInt())
                    editor.commit()
                } catch (e: NumberFormatException) {
                }
            } else {
            }
        }

        val USER_NAME: String? = prefManager!!.uSER_NAME
        val USER_EMAIL: String? = prefManager!!.uSER_EMAIL
        val USER_Mobile: String? = prefManager!!.uSER_Mobile
        val USER_DISTRICT: String? = prefManager!!.uSER_District
        Log.d("USER_NAME", " " + USER_NAME)

        binding!!.seDistrict.setText(USER_DISTRICT)
        spinnerDistrict = findViewById(R.id.spinnerDistrict)
        districtList()
        districtNameEng = "--" + getResources().getString(R.string.district) + "--"

        val calendar: Calendar = Calendar.getInstance()
        val today: Date = calendar.getTime()
        val sdf: SimpleDateFormat = SimpleDateFormat(myFormat, Locale.US)
        val todayDate: String = sdf.format(today)
        fromDate = todayDate
        toDate = todayDate

        val intent: Intent? = getIntent()
        // Get the value from the intent using the key
        if (intent != null) {
            val value_districtId: String? = intent.getStringExtra("key_districtId")
            val value_fromDate: String? = intent.getStringExtra("key_fromDate")
            val value_toDate: String? = intent.getStringExtra("key_toDate")

            value_selectedDay = intent.getStringExtra("key_selectedDate")
            value_selectedDis = intent.getStringExtra("key_selectedDis")
            Log.d("value_selectedDay", (value_selectedDay)!!)

            districtId = value_districtId
            fromDate = value_fromDate
            toDate = value_toDate
            getSensorSummary(districtId, fromDate, toDate, fpscodee)
        } else {
            binding!!.spinner.setSelection(1)
            getSensorSummary(districtId, fromDate, toDate, fpscodee)
        }

        setUpDateRangeSpinner()
    }

    private fun setBinding() {
    }

    private fun setClickListener() {
        binding!!.actionBar.ivBack.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                onBackPressed()
            }
        })

        //  binding.ivChallanImage.setOnClickListener(this);
        spinnerDistrict!!.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>?, view: View, i: Int, l: Long) {
                if (++checkDistrict > 1) {
                    districtNameEng = district_List!!.get(i)!!.getDistrictNameEng()
                    dis = district_List!!.get(i)!!.getDistrictNameEng()
                    Log.d("dfgfd", " " + dis)
                    districtId = district_List!!.get(i)!!.getDistrictId()
                    Log.d("fggfgh", " " + districtId)
                    val editor: SharedPreferences.Editor =
                        getSharedPreferences("MyPrefs", MODE_PRIVATE).edit()
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
        })

        binding!!.spinner.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>, view: View, i: Int, l: Long) {
                if (++checkFilter > 1) {
                    val item: String = adapterView.getItemAtPosition(i).toString()
                    if (!item.equals(
                            "--" + getResources().getString(R.string.select_filter) + "--",
                            ignoreCase = true
                        )
                    ) {
                        Objects.requireNonNull(binding!!.textFromDate.getText())!!.clear()
                        Objects.requireNonNull(binding!!.textToDate.getText())!!.clear()
                        if (item.equals(
                                getResources().getString(R.string.today),
                                ignoreCase = true
                            )
                        ) {
                            binding!!.layoutDateRange.setVisibility(View.GONE)
                            val calendar: Calendar = Calendar.getInstance()
                            val today: Date = calendar.getTime()
                            val sdf: SimpleDateFormat = SimpleDateFormat(myFormat, Locale.US)
                            val todayDate: String = sdf.format(today)
                            fromDate = todayDate
                            toDate = todayDate

                            //     getPosError(expenseStatusId,districtId,fromDate,toDate,fpscodee);
                        } else if (item.equals(
                                getResources().getString(R.string.yesterday),
                                ignoreCase = true
                            )
                        ) {
                            binding!!.layoutDateRange.setVisibility(View.GONE)
                            val calendar: Calendar = Calendar.getInstance()
                            calendar.add(Calendar.DAY_OF_YEAR, -1)
                            val yesterday: Date = calendar.getTime()
                            val sdf: SimpleDateFormat = SimpleDateFormat(myFormat, Locale.US)
                            val yesterdayDate: String = sdf.format(yesterday)
                            fromDate = yesterdayDate
                            toDate = yesterdayDate

                            //   getPosError(expenseStatusId,districtId,fromDate,toDate,fpscodee);
                        } else if (item.equals(
                                getResources().getString(R.string.current_month),
                                ignoreCase = true
                            )
                        ) {
                            binding!!.layoutDateRange.setVisibility(View.GONE)
                            val sdf: SimpleDateFormat = SimpleDateFormat(myFormat, Locale.US)

                            val calendar: Calendar = Calendar.getInstance()
                            calendar.set(
                                Calendar.DAY_OF_MONTH,
                                calendar.getActualMinimum(Calendar.DAY_OF_MONTH)
                            )
                            calendar.set(Calendar.HOUR_OF_DAY, 0)
                            calendar.set(Calendar.MINUTE, 0)
                            calendar.set(Calendar.SECOND, 0)
                            calendar.set(Calendar.MILLISECOND, 0)
                            val firstDateOfCurrentMonth: Date = calendar.getTime()
                            val date1: String = sdf.format(firstDateOfCurrentMonth)

                            val calendar1: Calendar = Calendar.getInstance()
                            val currentDateOfCurrentMonth: Date = calendar1.getTime()
                            val date2: String = sdf.format(currentDateOfCurrentMonth)
                            fromDate = date1
                            toDate = date2

                            //      getPosError(expenseStatusId,districtId,fromDate,toDate,fpscodee);
                        } else if (item.equals(
                                getResources().getString(R.string.previous_month),
                                ignoreCase = true
                            )
                        ) {
                            binding!!.layoutDateRange.setVisibility(View.GONE)
                            val sdf: SimpleDateFormat = SimpleDateFormat(myFormat, Locale.US)
                            val aCalendar: Calendar = Calendar.getInstance()
                            aCalendar.add(Calendar.MONTH, -1)
                            aCalendar.set(Calendar.DATE, 1)
                            val firstDateOfPreviousMonth: Date = aCalendar.getTime()
                            val date1: String = sdf.format(firstDateOfPreviousMonth)
                            aCalendar.set(
                                Calendar.DATE,
                                aCalendar.getActualMaximum(Calendar.DAY_OF_MONTH)
                            )
                            val lastDateOfPreviousMonth: Date = aCalendar.getTime()
                            val date2: String = sdf.format(lastDateOfPreviousMonth)
                            fromDate = date1
                            toDate = date2

                            //       getPosError(expenseStatusId,districtId,fromDate,toDate,fpscodee);
                        } else if (item.equals(
                                getResources().getString(R.string.custom_filter),
                                ignoreCase = true
                            )
                        ) {
                            binding!!.layoutDateRange.setVisibility(View.VISIBLE)
                        }
                    } else {
                        fromDate = ""
                        toDate = ""
                        Objects.requireNonNull(binding!!.textFromDate.getText())!!.clear()
                        Objects.requireNonNull(binding!!.textToDate.getText())!!.clear()
                        binding!!.layoutDateRange.setVisibility(View.GONE)
                        //     getPosError(expenseStatusId,districtId,fromDate,toDate,fpscodee);
                    }
                }
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {
            }
        })

        binding!!.textFromDate.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                val dateFromDate: OnDateSetListener =
                    OnDateSetListener({ view1: DatePicker?, year: Int, monthOfYear: Int, dayOfMonth: Int ->
                        myCalendarFromDate.set(
                            Calendar.YEAR, year
                        )
                        myCalendarFromDate.set(Calendar.MONTH, monthOfYear)
                        myCalendarFromDate.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                        updateLabelFromDate()
                    })
                val datePickerDialog: DatePickerDialog = DatePickerDialog(
                    (mContext)!!, dateFromDate, myCalendarFromDate
                        .get(Calendar.YEAR), myCalendarFromDate.get(Calendar.MONTH),
                    myCalendarFromDate.get(Calendar.DAY_OF_MONTH)
                )
                datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis() - 1000)
                val minDateInMillis: Long = myCalendarToDate.getTimeInMillis()
                datePickerDialog.getDatePicker().setMaxDate(minDateInMillis)
                datePickerDialog.show()
            }
        })

        binding!!.textToDate.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                val dateToDate: OnDateSetListener =
                    OnDateSetListener({ view1: DatePicker?, year: Int, monthOfYear: Int, dayOfMonth: Int ->
                        myCalendarToDate.set(
                            Calendar.YEAR, year
                        )
                        myCalendarToDate.set(Calendar.MONTH, monthOfYear)
                        myCalendarToDate.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                        updateLabelToDate()
                    })
                val datePickerDialog: DatePickerDialog = DatePickerDialog(
                    (mContext)!!, dateToDate, myCalendarToDate
                        .get(Calendar.YEAR), myCalendarToDate.get(Calendar.MONTH),
                    myCalendarToDate.get(Calendar.DAY_OF_MONTH)
                )
                datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis() - 1000)
                val minDateInMillis: Long = myCalendarFromDate.getTimeInMillis()
                datePickerDialog.getDatePicker().setMinDate(minDateInMillis)
                datePickerDialog.show()
            }
        })

        binding!!.btnsearch.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                fpscodee = binding!!.inputfps.getText().toString()
                getSensorSummary(districtId, fromDate, toDate, fpscodee)
            }
        })
    }

    private fun getSensorSummary(
        districtId: String?,
        fromDate: String?,
        toDate: String?,
        fpscodee: String?
    ) {
        if (isNetworkAvailable((mActivity)!!)) {
            hideKeyboard((mActivity)!!)
            showProgress()

            val USER_Id: String? = prefManager!!.uSER_Id
            Log.d("USER_ID", " " + USER_Id)
            Log.d("myDistrictId", " " + districtId)
            Log.d("myfromDate", " " + fromDate)
            Log.d("mytoDate", " " + toDate)

            val apiInterface: APIService = retrofitInstance!!.create(
                APIService::class.java
            )
            val call: Call<SensorSummaryResponse?>? = apiInterface.getSensorDisbnSummaryCnt(
                USER_Id,
                fpscodee,
                districtId,
                "0",
                fromDate,
                toDate
            )

            call!!.enqueue(object : Callback<SensorSummaryResponse?> {
                override fun onResponse(
                    call: Call<SensorSummaryResponse?>,
                    response: Response<SensorSummaryResponse?>
                ) {
                    hideProgress()

                    if (response.isSuccessful()) {
                        if (response.code() == 200) {
                            if (response.body() != null) {
                                binding!!.txtNoRecord.setVisibility(View.GONE)


                                if ((Objects.requireNonNull(response.body())
                                        !!.status == "200")
                                ) {
                                    //                    binding.actionBar.buttonPDF.setVisibility(View.VISIBLE);

                                    val sensorSummaryResponse: SensorSummaryResponse? =
                                        response.body()
                                    val message: String = sensorSummaryResponse!!.message!!

                                    sensorSummaryList = sensorSummaryResponse.data
                                    Log.d("sensorSummaryList", "Size...." + sensorSummaryList!!.size)

                                    binding!!.rvDelivered.setLayoutManager(
                                        LinearLayoutManager(
                                            mContext
                                        )
                                    )
                                    binding!!.rvDelivered.addItemDecoration(
                                        DividerItemDecoration(
                                            mContext,
                                            DividerItemDecoration.VERTICAL
                                        )
                                    )
                                    binding!!.rvDelivered.setVisibility(View.VISIBLE)
                                    val sensorSummaryAdapter: SensorSummaryAdapter =
                                        SensorSummaryAdapter(sensorSummaryList!!)
                                    binding!!.rvDelivered.setAdapter(sensorSummaryAdapter)
                                } else {
                                    binding!!.txtNoRecord.setVisibility(View.VISIBLE)
                                    binding!!.rvDelivered.setVisibility(View.GONE)
                                    showAlertDialogWithSingleButton(
                                        (mActivity)!!, response.body()!!.message
                                    )
                                    binding!!.actionBar.buttonPDF.setVisibility(View.GONE)
                                }
                            } else {
                                binding!!.txtNoRecord.setVisibility(View.VISIBLE)
                                binding!!.rvDelivered.setVisibility(View.GONE)
                                binding!!.actionBar.buttonPDF.setVisibility(View.GONE)
                                showAlertDialogWithSingleButton(
                                    (mActivity)!!, response.body()!!.message
                                )
                            }
                        } else {
                            val msg: String = "HTTP Error: " + response.code()
                            showAlertDialogWithSingleButton((mActivity)!!, msg)
                            binding!!.actionBar.buttonPDF.setVisibility(View.GONE)
                        }
                    } else {
                        val msg: String = "HTTP Error: " + response.code()
                        showAlertDialogWithSingleButton((mActivity)!!, msg)
                        binding!!.actionBar.buttonPDF.setVisibility(View.GONE)
                    }
                }

                override fun onFailure(call: Call<SensorSummaryResponse?>, error: Throwable) {
                    hideProgress()
                    showAlertDialogWithSingleButton((mActivity)!!, error.message)
                    binding!!.actionBar.buttonPDF.setVisibility(View.GONE)
                    call.cancel()
                }
            })
        } else {
            makeToast(getResources().getString(R.string.no_internet_connection))
            binding!!.actionBar.buttonPDF.setVisibility(View.GONE)
        }
    }

    private fun updateLabelFromDate() {
        val myFormat: String = "yyyy-MM-dd"
        val sdf: SimpleDateFormat = SimpleDateFormat(myFormat, Locale.US)
        binding!!.textFromDate.setText(sdf.format(myCalendarFromDate.getTime()))
        Objects.requireNonNull(binding!!.textToDate.getText())!!.clear()
        fromDate =
            Objects.requireNonNull(binding!!.textFromDate.getText()).toString().trim({ it <= ' ' })
    }

    private fun updateLabelToDate() {
        val myFormat: String = "yyyy-MM-dd"
        val sdf: SimpleDateFormat = SimpleDateFormat(myFormat, Locale.US)
        binding!!.textToDate.setText(sdf.format(myCalendarToDate.getTime()))
        toDate =
            Objects.requireNonNull(binding!!.textToDate.getText()).toString().trim({ it <= ' ' })
        //    getPosError(expenseStatusId,districtId,fromDate,toDate,fpscodee);
    }

    private fun districtList() {
        if (isNetworkAvailable((mActivity)!!)) {
            hideKeyboard((mActivity)!!)
            hideProgress()
            val USER_Id: String? = prefManager!!.uSER_Id
            val apiInterface: APIService = retrofitInstance!!.create(
                APIService::class.java
            )
            val call: Call<ModelDistrict_w?>? = apiInterface.apiGetDistictList_w()

            call!!.enqueue(object : Callback<ModelDistrict_w?> {
                override fun onResponse(
                    call: Call<ModelDistrict_w?>,
                    response: Response<ModelDistrict_w?>
                ) {
                    hideProgress()
                    if (response.isSuccessful()) {
                        if (response.code() == 200) {
                            Log.d("mydataresponse", "" + response.code())
                            if (response.body() != null) {
                                if ((Objects.requireNonNull(response.body())
                                        !!.getStatus() == "200")
                                ) {
                                    district_List = response.body()!!.getDistrict_List()
                                    if (district_List != null && district_List!!.size > 0) {
                                        Collections.reverse(district_List)
                                        val l: ModelDistrictList_w = ModelDistrictList_w()
                                        l.setDistrictId((-1).toString())
                                        l.setDistrictNameEng("--" + getResources().getString(R.string.district) + "--")
                                        district_List!!.add(l)
                                        Collections.reverse(district_List)
                                        val dataAdapter: ArrayAdapter<ModelDistrictList_w?> =
                                            ArrayAdapter(
                                                (mActivity)!!,
                                                android.R.layout.simple_spinner_item,
                                                district_List!!
                                            )
                                        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                                        spinnerDistrict!!.setAdapter(dataAdapter)
                                        binding!!.spinnerDistrict.setSelection(value_selectedDis!!.toInt())
                                    }
                                } else {
                                    makeToast(response.body()!!.getMessage())
                                }
                            } else {
                                makeToast(getResources().getString(R.string.error))
                            }
                        } else {
                            makeToast(getResources().getString(R.string.error))
                        }
                    } else {
                        makeToast(getResources().getString(R.string.error))
                    }
                }

                override fun onFailure(call: Call<ModelDistrict_w?>, error: Throwable) {
                    hideProgress()
                    makeToast(getResources().getString(R.string.error))
                    call.cancel()
                }
            })
        } else {
            makeToast(getResources().getString(R.string.no_internet_connection))
        }
    }

    private fun setUpDateRangeSpinner() {
        spinnerList.add("--" + getResources().getString(R.string.select_filter) + "--")
        spinnerList.add(getResources().getString(R.string.today))
        spinnerList.add(getResources().getString(R.string.yesterday))
        spinnerList.add(getResources().getString(R.string.current_month))
        spinnerList.add(getResources().getString(R.string.previous_month))
        spinnerList.add(getResources().getString(R.string.custom_filter))
        val dataAdapter: ArrayAdapter<String> =
            ArrayAdapter(this, android.R.layout.simple_spinner_item, spinnerList)
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding!!.spinner.setAdapter(dataAdapter)
        binding!!.spinner.setSelection(value_selectedDay!!.toInt())

        //  binding.spinner.setSelection(1);
        val selectedString: String = binding!!.spinner.getSelectedItem() as String
        val selectedItemPosition: Int = binding!!.spinner.getSelectedItemPosition()
        if (selectedItemPosition == 1) {
            val calendar: Calendar = Calendar.getInstance()
            val today: Date = calendar.getTime()
            val sdf: SimpleDateFormat = SimpleDateFormat(myFormat, Locale.US)
            val todayDate: String = sdf.format(today)
            fromDate = todayDate
            toDate = todayDate
        } else {
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        setResult(RESULT_OK)
        finish()
    }

    override fun makeToast(string: String?) {
        if (TextUtils.isEmpty(string)) return
        Toast.makeText(mActivity, string, Toast.LENGTH_SHORT).show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 444) {
            if (resultCode == RESULT_OK) {
                getSensorSummary(districtId, fromDate, toDate, fpscodee)
            } else {
            }
        }
    }
}
