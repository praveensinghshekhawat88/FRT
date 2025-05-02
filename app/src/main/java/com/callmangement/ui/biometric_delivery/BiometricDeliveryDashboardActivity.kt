package com.callmangement.ui.biometric_delivery

import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Vibrator
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.DatePicker
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.callmangement.R
import com.callmangement.custom.CustomActivity
import com.callmangement.databinding.ActivityBiometricDeliveryDashboardBinding
import com.callmangement.network.APIService
import com.callmangement.network.RetrofitInstance.retrofitInstance
import com.callmangement.ui.biometric_delivery.model.BiometricDashboardResponse
import com.callmangement.ui.home.MainActivity
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
import java.util.Locale
import java.util.Objects

class BiometricDeliveryDashboardActivity : CustomActivity(), View.OnClickListener {
    private var binding: ActivityBiometricDeliveryDashboardBinding? = null
    private val spinnerList: MutableList<String> = ArrayList()
    private val myFormat = "yyyy-MM-dd"
    private val myCalendarFromDate: Calendar = Calendar.getInstance()
    private val myCalendarToDate: Calendar = Calendar.getInstance()
    private val cvNewDelivery: CardView? = null
    private val cvCompleteDelivery: CardView? = null
    private val cvAbout: CardView? = null
    private var mActivity: Activity? = null
    private var builder: AlertDialog.Builder? = null
    private var prefManager: PrefManager? = null

    //   PrefManager preference;
    private var district_List: MutableList<ModelDistrictList_w?>? = ArrayList()
    private var checkDistrict = 0
    private var districtId: String? = "0"
    private val districtIdd: String? = "0"
    private var districtNameEng = ""
    private var dis: String? = null
    private var checkFilter = 0
    private var fromDate = ""
    private var toDate = ""
    private var swipeRefreshLayout: SwipeRefreshLayout? = null
    private var Pending = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBiometricDeliveryDashboardBinding.inflate(
            layoutInflater
        )
        setContentView(binding!!.root)
        init()
    }

    private fun init() {
        mActivity = this
        mContext = this
        prefManager = PrefManager(mContext!!)
        swipeRefreshLayout = findViewById(R.id.refreshLayoutt)
        binding!!.actionBar.ivBack.visibility = View.VISIBLE
        binding!!.actionBar.ivThreeDot.visibility = View.GONE
        binding!!.actionBar.layoutLanguage.visibility = View.GONE
        binding!!.actionBar.textToolbarTitle.text = "Biometric Delivery Dashboard"
        //preference = PrefManager.getInstance(mActivity);
        //   prefManager = new PrefManager(mContext);
        setUpData()
        setClickListener()
    }

    private fun setUpData() {
        if (prefManager!!.uSER_TYPE_ID == "1" && prefManager!!.uSER_TYPE.equals(
                "Admin",
                ignoreCase = true
            )
        ) {
            binding!!.rlDistrict.visibility = View.VISIBLE
            binding!!.seDistrict.visibility = View.GONE
            binding!!.spacer.visibility = View.VISIBLE
            binding!!.cvIrisReplace.visibility = View.GONE

            val sp = getSharedPreferences("your_prefs", MODE_PRIVATE)
            val editor = sp.edit()
            editor.remove("districtId_key")
            editor.apply()
        } else if (prefManager!!.uSER_TYPE_ID == "2" && prefManager!!.uSER_TYPE.equals(
                "Manager",
                ignoreCase = true
            )
        ) {
            binding!!.rlDistrict.visibility = View.VISIBLE
            binding!!.spacer.visibility = View.VISIBLE
            binding!!.seDistrict.visibility = View.GONE
            binding!!.cvIrisReplace.visibility = View.GONE

            val sp = getSharedPreferences("your_prefs", MODE_PRIVATE)
            val editor = sp.edit()
            editor.remove("districtId_key")
            editor.apply()
        } else if (prefManager!!.uSER_TYPE_ID == "4" && prefManager!!.uSER_TYPE.equals(
                "ServiceEngineer",
                ignoreCase = true
            )
        ) {
            binding!!.rlDistrict.visibility = View.GONE
            binding!!.spacer.visibility = View.VISIBLE
            binding!!.seDistrict.visibility = View.VISIBLE
            //    binding.cvIrisReplace.setVisibility(View.GONE);
            binding!!.cvIrisReplace.visibility = View.VISIBLE

            districtId = intent.getStringExtra("districtId")
            if (districtIdd != null && !districtIdd.isEmpty()) {
                try {
                    val sp = getSharedPreferences("your_prefs", MODE_PRIVATE)
                    val editor = sp.edit()
                    editor.putInt("districtId_key", districtId!!.toInt())
                    editor.commit()
                } catch (e: NumberFormatException) {
                    e.printStackTrace()
                }
            } else {
            }
        }

        binding!!.cvInstalled.visibility = View.GONE
        binding!!.cvIrisReplace.visibility = View.GONE


        val USER_NAME = prefManager!!.uSER_NAME
        val USER_EMAIL = prefManager!!.uSER_EMAIL
        val USER_Mobile = prefManager!!.uSER_Mobile
        val USER_DISTRICT = prefManager!!.uSER_District
        Log.d("USER_NAME", " $USER_NAME")
        Log.d("USER_Email", " $USER_EMAIL")
        Log.d("USER_Dis", " $USER_DISTRICT")

        binding!!.textUsername.text = USER_NAME
        binding!!.textEmail.text = USER_EMAIL
        binding!!.textmbl.text = USER_Mobile
        //29-01-2024
        //  districtId = getIntent().getStringExtra("districtId");
        binding!!.seDistrict.text = USER_DISTRICT
        builder = AlertDialog.Builder(this)
        districtList()
        districtNameEng = "--" + resources.getString(R.string.district) + "--"
        val calendar = Calendar.getInstance()
        val today = calendar.time
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        val todayDate = sdf.format(today)
        fromDate = todayDate
        toDate = todayDate
        getInstallationCntApi(districtId, fromDate, toDate)
        swipeRefreshLayout!!.setOnRefreshListener {
            swipeRefreshLayout!!.isRefreshing = false
            getInstallationCntApi(districtId, fromDate, toDate)
        }

        // Log.d("useriduserid",""+prefManager.getUSER_TYPE_ID());
        // binding.textDestrict.setText(USER_DISTRICT);
        // getInstallationCntApi();
        setUpDateRangeSpinner()
    }

    private fun setClickListener() {
//        binding.cvInstalled.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
//                // Check if the device supports vibration
//                if (vibrator.hasVibrator()) {
//                    // Vibrate for 500 milliseconds (0.5 seconds)
//                    vibrator.vibrate(100);
//                    String selectedDate = String.valueOf(binding.spinner.getSelectedItemPosition());
//                    String selectedDis = String.valueOf(binding.spinnerDistrict.getSelectedItemPosition());
//                    Intent i = new Intent(mContext, IrisInstalledListActivity.class);
//                    i.putExtra("key_districtId", districtId);
//                    i.putExtra("key_fromDate", fromDate);
//                    i.putExtra("key_toDate", toDate);
//                    i.putExtra("key_selectedDate", selectedDate);
//                    i.putExtra("key_selectedDis", selectedDis);
//                    startActivity(i);
//                }
//            }
//        });


//        binding.lytTotalL0Machine.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
//                // Check if the device supports vibration
//                if (vibrator.hasVibrator()) {
//                    // Vibrate for 500 milliseconds (0.5 seconds)
//                    vibrator.vibrate(100);
//                    String selectedDate = String.valueOf(binding.spinner.getSelectedItemPosition());
//                    String selectedDis = String.valueOf(binding.spinnerDistrict.getSelectedItemPosition());
//
//                    Intent i = new Intent(mContext, InstallationPendingListActivity.class);
//                    i.putExtra("key_districtId", districtId);
//                    i.putExtra("key_districtName", districtNameEng);
//                    i.putExtra("key_fromDate", fromDate);
//                    i.putExtra("key_toDate", toDate);
//                    i.putExtra("key_selectedDate", selectedDate);
//                    i.putExtra("key_selectedDis", selectedDis);
//                    i.putExtra("FilterTypeId", "0");
//
//                    startActivity(i);
//                }
//            }
//        });


        if ((prefManager!!.uSER_TYPE_ID == "1")) {
        } else if ((prefManager!!.uSER_TYPE_ID == "2")) {
        } else if ((prefManager!!.uSER_TYPE_ID == "4")) {
            binding!!.lytDistribute.setOnClickListener(View.OnClickListener {
                val vibrator = getSystemService(VIBRATOR_SERVICE) as Vibrator
                vibrator.vibrate(100)
                if ((Pending == "0")) {
                    showAlertDialogWithSingleButton(
                        (mActivity)!!,
                        resources.getString(R.string.noinstpending)
                    )
                    // Check if the device supports vibration
                } else {
                    val selectedDate = binding!!.spinner.selectedItemPosition.toString()
                    val selectedDis = binding!!.spinnerDistrict.selectedItemPosition.toString()

                    val i = Intent(mContext, BiometricDeliveryActivity::class.java)
                    i.putExtra("key_districtId", districtId)
                    i.putExtra("key_fromDate", fromDate)
                    i.putExtra("key_toDate", toDate)
                    i.putExtra("key_selectedDate", selectedDate)
                    i.putExtra("key_selectedDis", selectedDis)
                    startActivityForResult(i, 222)
                    Log.d("selectedDis", "selectedDis$selectedDis")
                }
            } // Check if the device supports vibration
            )
        }

        binding!!.lytTotalL0Machine.setOnClickListener(this)
        binding!!.lytTotalMapped.setOnClickListener(this)
        binding!!.lytTotalDistributed.setOnClickListener(this)
        binding!!.cvInstallationPending.setOnClickListener(this)

        /*binding.logout.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {

                logoutAlartDialoge();

            }
        });*/

//        binding.cvIrisReplace.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
//                // Check if the device supports vibration
//                if (vibrator.hasVibrator()) {
//                    // Vibrate for 500 milliseconds (0.5 seconds)
//                    vibrator.vibrate(100);
//                    String selectedDate = String.valueOf(binding.spinner.getSelectedItemPosition());
//                    String selectedDis = String.valueOf(binding.spinnerDistrict.getSelectedItemPosition());
//
//                    Log.d("selectedDate", selectedDate);
//
//                    Intent i = new Intent(mContext, IrisReplaceListActivity.class);
//                    i.putExtra("key_districtId", districtId);
//                    i.putExtra("key_fromDate", fromDate);
//                    i.putExtra("key_toDate", toDate);
//                    i.putExtra("key_selectedDate", selectedDate);
//                    i.putExtra("key_selectedDis", selectedDis);
//                    startActivity(i);
//                }
//            }
//        });
        binding!!.lytSummary.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                val vibrator = getSystemService(VIBRATOR_SERVICE) as Vibrator
                // Check if the device supports vibration
                if (vibrator.hasVibrator()) {
                    // Vibrate for 500 milliseconds (0.5 seconds)
                    vibrator.vibrate(100)
                    val selectedDate = binding!!.spinner.selectedItemPosition.toString()
                    val selectedDis = binding!!.spinnerDistrict.selectedItemPosition.toString()

                    Log.d("selectedDate", selectedDate)

                    val i = Intent(mContext, SensorSummaryActivity::class.java)
                    i.putExtra("key_districtId", districtId)
                    i.putExtra("key_fromDate", fromDate)
                    i.putExtra("key_toDate", toDate)
                    i.putExtra("key_selectedDate", selectedDate)
                    i.putExtra("key_selectedDis", selectedDis)

                    startActivity(i)
                }
            }
        })


        //        binding.lytTotalDistributed.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
//                // Check if the device supports vibration
//                if (vibrator.hasVibrator()) {
//                    // Vibrate for 500 milliseconds (0.5 seconds)
//                    vibrator.vibrate(100);
//                    String selectedDate = String.valueOf(binding.spinner.getSelectedItemPosition());
//                    String selectedDis = String.valueOf(binding.spinnerDistrict.getSelectedItemPosition());
//
//                    Log.d("selectedDate", selectedDate);
//
//                    Intent i = new Intent(mContext, BiometricDeliveryListActivity.class);
//                    i.putExtra("key_districtId", districtId);
//                    i.putExtra("key_fromDate", fromDate);
//                    i.putExtra("key_toDate", toDate);
//                    i.putExtra("key_selectedDate", selectedDate);
//                    i.putExtra("key_selectedDis", selectedDis);
//
//                    startActivity(i);
//                }
//            }
//        });
        binding!!.actionBar.ivBack.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                val i = Intent(mContext, MainActivity::class.java)
                startActivity(i)
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
                        dis = district_List!![i]!!.districtNameEng
                        Log.d("dfgfd", " $dis")
                        districtId = district_List!![i]!!.districtId
                        Log.d("fggfgh", " $districtId")
                        val editor = getSharedPreferences("MyPrefs", MODE_PRIVATE).edit()
                        editor.putString("dis", dis)
                        editor.putString("districtId", districtId)
                        editor.apply()

                        getInstallationCntApi(districtId, fromDate, toDate)

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
                        Objects.requireNonNull(binding!!.textFromDate.text)!!.clear()
                        Objects.requireNonNull(binding!!.textToDate.text)!!.clear()
                        if (item.equals(resources.getString(R.string.today), ignoreCase = true)) {
                            binding!!.layoutDateRange.visibility = View.GONE
                            val calendar = Calendar.getInstance()
                            val today = calendar.time
                            val sdf = SimpleDateFormat(myFormat, Locale.US)
                            val todayDate = sdf.format(today)
                            fromDate = todayDate
                            toDate = todayDate

                            getInstallationCntApi(districtId, fromDate, toDate)

                            // getPosError(expenseStatusId,districtId,fromDate,toDate,fpscodee);
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
                            getInstallationCntApi(districtId, fromDate, toDate)

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

                            getInstallationCntApi(districtId, fromDate, toDate)

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

                            getInstallationCntApi(districtId, fromDate, toDate)

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
                        Objects.requireNonNull(binding!!.textFromDate.text)!!.clear()
                        Objects.requireNonNull(binding!!.textToDate.text)!!.clear()
                        binding!!.layoutDateRange.visibility = View.GONE

                        getInstallationCntApi(districtId, fromDate, toDate)

                        //     getPosError(expenseStatusId,districtId,fromDate,toDate,fpscodee);
                    }
                }
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {
            }
        }

        binding!!.textFromDate.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                val dateFromDate =
                    OnDateSetListener { view1: DatePicker?, year: Int, monthOfYear: Int, dayOfMonth: Int ->
                        myCalendarFromDate[Calendar.YEAR] = year
                        myCalendarFromDate[Calendar.MONTH] = monthOfYear
                        myCalendarFromDate[Calendar.DAY_OF_MONTH] = dayOfMonth
                        updateLabelFromDate()
                    }
                val datePickerDialog = DatePickerDialog(
                    (mContext)!!,
                    dateFromDate,
                    myCalendarFromDate[Calendar.YEAR],
                    myCalendarFromDate[Calendar.MONTH],
                    myCalendarFromDate[Calendar.DAY_OF_MONTH]
                )
                datePickerDialog.datePicker.maxDate = System.currentTimeMillis() - 1000
                val minDateInMillis = myCalendarToDate.timeInMillis
                datePickerDialog.datePicker.maxDate = minDateInMillis
                datePickerDialog.show()
            }
        })

        binding!!.textToDate.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                val dateToDate =
                    OnDateSetListener { view1: DatePicker?, year: Int, monthOfYear: Int, dayOfMonth: Int ->
                        myCalendarToDate[Calendar.YEAR] = year
                        myCalendarToDate[Calendar.MONTH] = monthOfYear
                        myCalendarToDate[Calendar.DAY_OF_MONTH] = dayOfMonth
                        updateLabelToDate()
                    }
                val datePickerDialog = DatePickerDialog(
                    (mContext)!!,
                    dateToDate,
                    myCalendarToDate[Calendar.YEAR],
                    myCalendarToDate[Calendar.MONTH],
                    myCalendarToDate[Calendar.DAY_OF_MONTH]
                )
                datePickerDialog.datePicker.maxDate = System.currentTimeMillis() - 1000
                val minDateInMillis = myCalendarFromDate.timeInMillis
                datePickerDialog.datePicker.minDate = minDateInMillis
                datePickerDialog.show()
            }
        })

        binding!!.btnsearch.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                getInstallationCntApi(districtId, fromDate, toDate)
            }
        })
    }

    private fun updateLabelFromDate() {
        val myFormat = "yyyy-MM-dd"
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        binding!!.textFromDate.setText(sdf.format(myCalendarFromDate.time))
        Objects.requireNonNull(binding!!.textToDate.text)!!.clear()
        fromDate = Objects.requireNonNull(binding!!.textFromDate.text).toString().trim { it <= ' ' }
    }

    private fun updateLabelToDate() {
        val myFormat = "yyyy-MM-dd"
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        binding!!.textToDate.setText(sdf.format(myCalendarToDate.time))
        toDate = Objects.requireNonNull(binding!!.textToDate.text).toString().trim { it <= ' ' }
        //    getPosError(expenseStatusId,districtId,fromDate,toDate,fpscodee);
        getInstallationCntApi(districtId, fromDate, toDate)
    }

    private fun getInstallationCntApi(districtId: String?, fromDate: String, toDate: String) {
        //Progress Bar while connection establishes

        if (isNetworkAvailable(mActivity!!)) {
            hideKeyboard(mActivity!!)
            //  Utils.showCustomProgressDialogCommonForAll(mActivity, getResources().getString(R.string.please_wait));
            showProgress()
            val USER_Id = prefManager!!.uSER_Id
            Log.d("USER_ID", " $USER_Id")
            Log.d("districtId", " $districtId")
            val service = retrofitInstance!!.create(
                APIService::class.java
            )
            val call = service.getBiometricSensorDashboardData(
                USER_Id,
                districtId,
                "",
                fromDate,
                toDate,
                "0"
            )
            call!!.enqueue(object : Callback<BiometricDashboardResponse?> {
                override fun onResponse(
                    call: Call<BiometricDashboardResponse?>,
                    response: Response<BiometricDashboardResponse?>
                ) {
                    // Utils.hideCustomProgressDialogCommonForAll();
                    hideProgress()
                    if (response.isSuccessful) {
                        if (response.code() == 200) {
                            if (response.body() != null) {
                                if (Objects.requireNonNull(response.body())!!.status == "200") {
                                    val countRoot = response.body()
                                    val countDatum = countRoot!!.data

                                    // makeToast(String.valueOf(response.body().getMessage()));
                                    if (countDatum != null) {
                                        // Perform operations with the data in countDatum

                                        val total =
                                            countDatum.total_Distributed_BiometricSensor
                                        Pending = countDatum.total_Pending!!

                                        //    String Installed = String.valueOf(datum.getTotalIRISDeliverdInstalled());
                                        binding!!.tvTotalcount.text = total
                                        binding!!.txtPending.text = Pending
                                        binding!!.txttotalL0Machine.text =
                                            countDatum.total_L0_Machine
                                        binding!!.txtTotalMapped.text =
                                            countDatum.total_Mapped_BiometricSensor
                                        //    binding.tvInstalled.setText(Installed);
                                        // Use 'dataValue' or perform operations with other properties
                                    } else {
                                        makeToast(response.body()!!.message.toString())
                                        // Handle the case when countDatum is empty or null
                                    }
                                } else {
                                    // Handle the case when the response status is not 200 or the response body is null
                                }
                            } else {
                                makeToast(response.body()!!.message.toString())
                            }
                        } else {
                            val msg = "HTTP Error: " + response.code()
                            Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        val msg = "HTTP Error: " + response.code()
                        Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<BiometricDashboardResponse?>, error: Throwable) {
                    //  Utils.hideCustomProgressDialogCommonForAll();
                    hideProgress()
                    makeToast(resources.getString(R.string.error))
                    call.cancel()
                }
            })
        } else {
            makeToast(resources.getString(R.string.no_internet_connection))
        }
    }

    private fun districtList() {
        if (isNetworkAvailable(mActivity!!)) {
            hideKeyboard(mActivity!!)
            //   Utils.showCustomProgressDialogCommonForAll(mActivity, getResources().getString(R.string.please_wait));
            val USER_Id = prefManager!!.uSER_Id
            val service = retrofitInstance!!.create(
                APIService::class.java
            )
            val call = service.apiGetDistictList_w()
            call!!.enqueue(object : Callback<ModelDistrict_w?> {
                override fun onResponse(
                    call: Call<ModelDistrict_w?>,
                    response: Response<ModelDistrict_w?>
                ) {
                    //            hideProgress();
                    if (response.isSuccessful) {
                        if (response.code() == 200) {
                            if (response.body() != null) {
                                if (Objects.requireNonNull(response.body())!!.status == "200") {
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
                                            mActivity!!,
                                            android.R.layout.simple_spinner_item,
                                            district_List!!
                                        )
                                        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                                        binding!!.spinnerDistrict.adapter = dataAdapter


                                        /*  if(d2!=null && !d2.isEmpty())
                                        {
                                            Log.d("ghjh",""+d2);
                                   int userId = Integer.parseInt(d2); // replace with the user ID you want to select
                                            for (int i = 0; i < dataAdapter.getCount(); i++) {
                                                ModelDistrictList_w user = dataAdapter.getItem(i);
                                                if (String.valueOf(user.getDistrictId()).equals(String.valueOf(userId))) {
                                                    spinnerDistrict.setSelection(i);
                                                    break;
                                                }
                                            }
                                        }*/
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

    override fun onBackPressed() {
        super.onBackPressed()
        val i = Intent(mContext, MainActivity::class.java)
        startActivity(i)
    }

    override fun makeToast(string: String?) {
        if (TextUtils.isEmpty(string)) return
        Toast.makeText(mActivity, string, Toast.LENGTH_SHORT).show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 222) {
            getInstallationCntApi(districtId, fromDate, toDate)
        }
    }

    override fun onClick(v: View) {
        if (v.id == R.id.lytTotalL0Machine) {
            val vibrator = getSystemService(VIBRATOR_SERVICE) as Vibrator
            // Check if the device supports vibration
            if (vibrator.hasVibrator()) {
                // Vibrate for 500 milliseconds (0.5 seconds)
                vibrator.vibrate(100)
                val selectedDate = binding!!.spinner.selectedItemPosition.toString()
                val selectedDis = binding!!.spinnerDistrict.selectedItemPosition.toString()

                val i = Intent(mContext, BiometricDeliveryListActivity::class.java)
                i.putExtra("key_districtId", districtId)
                i.putExtra("key_districtName", districtNameEng)
                i.putExtra("key_fromDate", fromDate)
                i.putExtra("key_toDate", toDate)
                i.putExtra("key_selectedDate", selectedDate)
                i.putExtra("key_selectedDis", selectedDis)
                i.putExtra("FILTER_TYPE_ID", "0")

                startActivity(i)
            }
        } else if (v.id == R.id.lytTotalMapped) {
            val vibrator = getSystemService(VIBRATOR_SERVICE) as Vibrator
            // Check if the device supports vibration
            if (vibrator.hasVibrator()) {
                // Vibrate for 500 milliseconds (0.5 seconds)
                vibrator.vibrate(100)
                val selectedDate = binding!!.spinner.selectedItemPosition.toString()
                val selectedDis = binding!!.spinnerDistrict.selectedItemPosition.toString()

                val i = Intent(mContext, BiometricDeliveryListActivity::class.java)
                i.putExtra("key_districtId", districtId)
                i.putExtra("key_districtName", districtNameEng)
                i.putExtra("key_fromDate", fromDate)
                i.putExtra("key_toDate", toDate)
                i.putExtra("key_selectedDate", selectedDate)
                i.putExtra("key_selectedDis", selectedDis)
                i.putExtra("FILTER_TYPE_ID", "1")

                startActivity(i)
            }
        } else if (v.id == R.id.lytTotalDistributed) {
            val vibrator = getSystemService(VIBRATOR_SERVICE) as Vibrator
            // Check if the device supports vibration
            if (vibrator.hasVibrator()) {
                // Vibrate for 500 milliseconds (0.5 seconds)
                vibrator.vibrate(100)
                val selectedDate = binding!!.spinner.selectedItemPosition.toString()
                val selectedDis = binding!!.spinnerDistrict.selectedItemPosition.toString()

                val i = Intent(mContext, BiometricDeliveryListActivity::class.java)
                i.putExtra("key_districtId", districtId)
                i.putExtra("key_districtName", districtNameEng)
                i.putExtra("key_fromDate", fromDate)
                i.putExtra("key_toDate", toDate)
                i.putExtra("key_selectedDate", selectedDate)
                i.putExtra("key_selectedDis", selectedDis)
                i.putExtra("FILTER_TYPE_ID", "2")

                startActivity(i)
            }
        } else if (v.id == R.id.cvInstallationPending) {
            val vibrator = getSystemService(VIBRATOR_SERVICE) as Vibrator
            // Check if the device supports vibration
            if (vibrator.hasVibrator()) {
                // Vibrate for 500 milliseconds (0.5 seconds)
                vibrator.vibrate(100)
                val selectedDate = binding!!.spinner.selectedItemPosition.toString()
                val selectedDis = binding!!.spinnerDistrict.selectedItemPosition.toString()

                val i = Intent(mContext, BiometricDeliveryListActivity::class.java)
                i.putExtra("key_districtId", districtId)
                i.putExtra("key_districtName", districtNameEng)
                i.putExtra("key_fromDate", fromDate)
                i.putExtra("key_toDate", toDate)
                i.putExtra("key_selectedDate", selectedDate)
                i.putExtra("key_selectedDis", selectedDis)
                i.putExtra("FILTER_TYPE_ID", "3")

                startActivity(i)
            }
        }
    }
}
