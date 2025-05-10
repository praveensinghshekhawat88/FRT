package com.callmangement.ui.iris_derivery_installation

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
import com.callmangement.databinding.ActivityIrisDeliveryInstallationBinding
import com.callmangement.network.APIService
import com.callmangement.network.RetrofitInstance
import com.callmangement.ui.closed_guard_delivery.CloseGuardDeliveryListActivity
import com.callmangement.ui.home.MainActivity
import com.callmangement.ui.ins_weighing_scale.model.district.ModelDistrictList_w
import com.callmangement.ui.ins_weighing_scale.model.district.ModelDistrict_w
import com.callmangement.ui.iris_derivery_installation.Model.IrisDashboardResponse
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

class IrisDeliveryInstallationDashboard : CustomActivity() {
    private var binding: ActivityIrisDeliveryInstallationBinding? = null
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
    private val districtIdd = "0"
    private var districtNameEng: String? = ""
    private var dis: String? = null
    private var checkFilter = 0
    private var fromDate = ""
    private var toDate = ""
    private var swipeRefreshLayout: SwipeRefreshLayout? = null
    private var Delivered = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIrisDeliveryInstallationBinding.inflate(
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
        binding!!.actionBar.textToolbarTitle.text = "Iris Dashboard"
        //preference = PrefManager.getInstance(mActivity);
        //   prefManager = new PrefManager(mContext);
        setUpData()
        setClickListener()
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
            binding!!.cvIrisReplace.visibility = View.GONE

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
            binding!!.cvIrisReplace.visibility = View.GONE

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

        val USER_NAME = prefManager!!.useR_NAME
        val USER_EMAIL = prefManager!!.useR_EMAIL
        val USER_Mobile = prefManager!!.useR_Mobile
        val USER_DISTRICT = prefManager!!.useR_District
        Log.d("USER_NAME", " $USER_NAME")
        Log.d("USER_Email", " $USER_EMAIL")
        Log.d("USER_Dis", " $USER_DISTRICT")
        //    binding.actionBar.textToolbarTitle.setText(getResources().getString(R.string.dashboard));
        binding!!.actionBar.textToolbarTitle.text = "Iris Dashboard"
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

        // Log.d("useriduserid",""+prefManager.getUseR_TYPE_ID());
        // binding.textDestrict.setText(USER_DISTRICT);
        // getInstallationCntApi();
        setUpDateRangeSpinner()
    }

    private fun setClickListener() {
        binding!!.cvInstalled.setOnClickListener {
            val vibrator = getSystemService(VIBRATOR_SERVICE) as Vibrator
            // Check if the device supports vibration
            if (vibrator.hasVibrator()) {
                // Vibrate for 500 milliseconds (0.5 seconds)
                vibrator.vibrate(100)
                val selectedDate = binding!!.spinner.selectedItemPosition.toString()
                val selectedDis = binding!!.spinnerDistrict.selectedItemPosition.toString()
                val i = Intent(mContext, IrisInstalledListActivity::class.java)
                i.putExtra("key_districtId", districtId)
                i.putExtra("key_fromDate", fromDate)
                i.putExtra("key_toDate", toDate)
                i.putExtra("key_selectedDate", selectedDate)
                i.putExtra("key_selectedDis", selectedDis)
                startActivity(i)
            }
        }

        binding!!.lytClosedGuardDeliveryList.setOnClickListener {
            val vibrator = getSystemService(VIBRATOR_SERVICE) as Vibrator
            // Check if the device supports vibration
            if (vibrator.hasVibrator()) {
                // Vibrate for 500 milliseconds (0.5 seconds)
                vibrator.vibrate(100)
                val selectedDate = binding!!.spinner.selectedItemPosition.toString()
                val selectedDis = binding!!.spinnerDistrict.selectedItemPosition.toString()
                val i = Intent(mContext, CloseGuardDeliveryListActivity::class.java)
                i.putExtra("key_districtId", districtId)
                i.putExtra("key_fromDate", fromDate)
                i.putExtra("key_toDate", toDate)
                i.putExtra("key_selectedDate", selectedDate)
                i.putExtra("key_selectedDis", selectedDis)
                startActivity(i)
            }
        }

        binding!!.lytIrisInstallationPending.setOnClickListener {
            val vibrator = getSystemService(VIBRATOR_SERVICE) as Vibrator
            // Check if the device supports vibration
            if (vibrator.hasVibrator()) {
                // Vibrate for 500 milliseconds (0.5 seconds)
                vibrator.vibrate(100)
                val selectedDate = binding!!.spinner.selectedItemPosition.toString()
                val selectedDis = binding!!.spinnerDistrict.selectedItemPosition.toString()

                val i = Intent(mContext, InstallationPendingListActivity::class.java)
                i.putExtra("key_districtId", districtId)
                i.putExtra("key_districtName", districtNameEng)
                i.putExtra("key_fromDate", fromDate)
                i.putExtra("key_toDate", toDate)
                i.putExtra("key_selectedDate", selectedDate)
                i.putExtra("key_selectedDis", selectedDis)

                startActivity(i)
            }
        }


        if (prefManager!!.useR_TYPE_ID == "1") {
        } else if (prefManager!!.useR_TYPE_ID == "2") {
        } else if (prefManager!!.useR_TYPE_ID == "4") {
            binding!!.cvInstallationPending.setOnClickListener {
                val vibrator =
                    getSystemService(VIBRATOR_SERVICE) as Vibrator
                vibrator.vibrate(100)
                if (Delivered == "0") {
                    showAlertDialogWithSingleButton(
                        mActivity,
                        resources.getString(R.string.noinstpending)
                    )
                    // Check if the device supports vibration
                } else {
                    val selectedDate = binding!!.spinner.selectedItemPosition.toString()
                    val selectedDis = binding!!.spinnerDistrict.selectedItemPosition.toString()
                    //   Intent i = new Intent(mContext, InstallationPendingList.class);
                    val i = Intent(mContext, IrisNewDeliveryActivity::class.java)
                    i.putExtra("key_districtId", districtId)
                    i.putExtra("key_fromDate", fromDate)
                    i.putExtra("key_toDate", toDate)
                    i.putExtra("key_selectedDate", selectedDate)
                    i.putExtra("key_selectedDis", selectedDis)
                    startActivityForResult(i, 222)
                    Log.d("selectedDis", "selectedDis$selectedDis")
                }
            } // Check if the device supports vibration
        }

        /*binding.logout.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {

                logoutAlartDialoge();

            }
        });*/
        binding!!.cvIrisReplace.setOnClickListener {
            val vibrator = getSystemService(VIBRATOR_SERVICE) as Vibrator
            // Check if the device supports vibration
            if (vibrator.hasVibrator()) {
                // Vibrate for 500 milliseconds (0.5 seconds)
                vibrator.vibrate(100)
                val selectedDate = binding!!.spinner.selectedItemPosition.toString()
                val selectedDis = binding!!.spinnerDistrict.selectedItemPosition.toString()

                Log.d("selectedDate", selectedDate)

                val i = Intent(mContext, IrisReplaceListActivity::class.java)
                i.putExtra("key_districtId", districtId)
                i.putExtra("key_fromDate", fromDate)
                i.putExtra("key_toDate", toDate)
                i.putExtra("key_selectedDate", selectedDate)
                i.putExtra("key_selectedDis", selectedDis)

                startActivity(i)
            }
        }



        binding!!.actionBar.ivBack.setOnClickListener {
            val i = Intent(mContext, MainActivity::class.java)
            startActivity(i)
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
                        binding!!.textFromDate.text!!.clear()
                        binding!!.textToDate.text!!.clear()
                        binding!!.layoutDateRange.visibility = View.GONE

                        getInstallationCntApi(districtId, fromDate, toDate)

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
            getInstallationCntApi(
                districtId,
                fromDate,
                toDate
            )
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
        getInstallationCntApi(districtId, fromDate, toDate)
    }

    private fun getInstallationCntApi(districtId: String?, fromDate: String, toDate: String) {
        //Progress Bar while connection establishes

        if (isNetworkAvailable(mActivity!!)) {
            hideKeyboard(mActivity)
            //  Utils.showCustomProgressDialogCommonForAll(mActivity, getResources().getString(R.string.please_wait));
            showProgress()
            val USER_Id = prefManager!!.useR_Id
            Log.d("USER_ID", " $USER_Id")
            Log.d("districtId", " $districtId")
            val service = RetrofitInstance.getRetrofitInstance().create(
                APIService::class.java
            )
            val call =
                service.getIrisDashboardData(USER_Id, districtId, "0", "0", fromDate, toDate, "0")

            //   APIInterface apiInterface = APIClient.getRetrofitClientWithoutHeaders(mActivity!!!!,Utils.Baseurl).create(APIInterface.class);
            //   Call<CountRoot> call = apiInterface.appWeightCountApi(USER_Id,"" ,districtId,"", "0",fromDate,toDate);
            call.enqueue(object : Callback<IrisDashboardResponse?> {
                override fun onResponse(
                    call: Call<IrisDashboardResponse?>,
                    response: Response<IrisDashboardResponse?>
                ) {
                    // Utils.hideCustomProgressDialogCommonForAll();
                    hideProgress()
                    if (response.isSuccessful) {
                        if (response.code() == 200) {
                            if (response.body() != null) {
                                if (response.body()!!.status == "200") {
                                    val countRoot = response.body()
                                    val countDatum = countRoot!!.data

                                    // makeToast(String.valueOf(response.body().getMessage()));
                                    if (countDatum != null && !countDatum.isEmpty()) {
                                        // Perform operations with the data in countDatum
                                        for (datum in countDatum) {
                                            // Access individual data items in the countDatum ArrayList
                                            // Example: Accessing a property of CountDatum
                                            val total = datum.totalIRISDispatched.toString()
                                            Delivered = datum.totalIRISDeliveryPending.toString()
                                            val Installed =
                                                datum.totalIRISDeliverdInstalled.toString()

                                            binding!!.tvTotalcount.text = total
                                            binding!!.tvDelivered.text = Delivered
                                            binding!!.tvInstalled.text = Installed
                                            // Use 'dataValue' or perform operations with other properties
                                        }
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

                override fun onFailure(call: Call<IrisDashboardResponse?>, error: Throwable) {
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
            hideKeyboard(mActivity)
            //   Utils.showCustomProgressDialogCommonForAll(mActivity, getResources().getString(R.string.please_wait));
            val USER_Id = prefManager!!.useR_Id

            //  APIInterface apiInterface = APIClient.getRetrofitClientWithoutHeaders(mActivity!!!!,Utils.Baseurl).create(APIInterface.class);
            //  Call<ModelDistrict_w> call = apiInterface.apiGetDistictList();
            val service = RetrofitInstance.getRetrofitInstance().create(
                APIService::class.java
            )
            val call = service.apiGetDistictList_w()

            call.enqueue(object : Callback<ModelDistrict_w?> {
                override fun onResponse(
                    call: Call<ModelDistrict_w?>,
                    response: Response<ModelDistrict_w?>
                ) {
                    //            hideProgress();
                    if (response.isSuccessful) {
                        if (response.code() == 200) {
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
                                        binding!!.spinnerDistrict.adapter = dataAdapter

                                        /* if(d2!=null)
                                        {
                                            spinnerDistrict.setSelection(Integer.parseInt(d2));
                                        }*/

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
        /* //Uncomment the below code to Set the message and title from the strings.xml file
        builder.setMessage(R.string.dialog_message) .setTitle(R.string.dialog_title);
        //Setting message manually and performing action on button click
        builder.setMessage("Do you want to close this application ?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                          finish();
                        finishAffinity();
                        // Toast.makeText(getApplicationContext(),"you choose yes action for alertbox",
                                //Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //  Action for 'NO' Button
                        dialog.cancel();
                        //Toast.makeText(getApplicationContext(),"you choose no action for alertbox",
                             //   Toast.LENGTH_SHORT).show();
                    }
                });
        //Creating dialog box
        AlertDialog alert = builder.create();
        //Setting the title manually
        alert.setTitle("Exit");
        alert.show();
*/
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
}
