package com.callmangement.ui.ins_weighing_scale.activity

import android.app.Activity
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Environment
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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.callmangement.R
import com.callmangement.custom.CustomActivity
import com.callmangement.databinding.ActivityInstalledlistBinding
import com.callmangement.network.APIService
import com.callmangement.network.RetrofitInstance
import com.callmangement.ui.ins_weighing_scale.adapter.InstalledAdapter
import com.callmangement.ui.ins_weighing_scale.model.Installed.InstalledDatum
import com.callmangement.ui.ins_weighing_scale.model.Installed.InstalledRoot
import com.callmangement.ui.ins_weighing_scale.model.district.ModelDistrictList_w
import com.callmangement.ui.ins_weighing_scale.model.district.ModelDistrict_w
import com.callmangement.utils.Constants.isNetworkAvailable
import com.callmangement.utils.PrefManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.apache.poi.hssf.usermodel.HSSFRichTextString
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.ss.usermodel.Font
import org.apache.poi.ss.usermodel.RichTextString
import org.apache.poi.ss.usermodel.Row
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Collections
import java.util.Date
import java.util.Locale
import java.util.Objects
import java.util.Random

class InstalledList : CustomActivity() {
    private var binding: ActivityInstalledlistBinding? = null
    private val spinnerList: MutableList<String> = ArrayList()
    var mActivity: Activity? = null
    var prefManager: PrefManager? = null
    var mRecyclerView: RecyclerView? = null
    private var spinnerDistrict: Spinner? = null
    private var checkDistrict = 0
    private var districtNameEng: String? = ""
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
    var swipeRefreshLayout: SwipeRefreshLayout? = null
    var fpscodee: String? = null
    var value_selectedDay: String? = null
    var value_selectedDis: String? = null
    private var vibrator: Vibrator? = null
    var sharedPreferences: SharedPreferences? = null
    var irisInsDataArrayList: ArrayList<InstalledDatum>? = null
    var originalFileName: String = "Demo.xlsx" // Original file name
    var uniqueFileName: String = generateUniqueFileName(originalFileName)
    private val filePathh = File("/storage/emulated/0/Download/$uniqueFileName")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE) //will hide the title
        // getSupportActionBar().hide(); // hide the title bar
        binding = ActivityInstalledlistBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        init()
    }

    private fun init() {
        mActivity = this
        mContext = this
        prefManager = PrefManager(mContext!!)
        swipeRefreshLayout = findViewById(R.id.refresh_list)
        binding!!.actionBar.ivBack.visibility = View.VISIBLE
        binding!!.actionBar.buttonPDF.visibility = View.VISIBLE
        binding!!.actionBar.buttonPDF.setImageDrawable(getDrawable(R.drawable.excel))
        mRecyclerView = findViewById(R.id.rv_delivered)
        vibrator = getSystemService(VIBRATOR_SERVICE) as Vibrator

        binding!!.actionBar.textToolbarTitle.text = resources.getString(R.string.installed_list)
        setUpData()
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
        //  getIrisWeighInstallation();
        //  districtId = getIntent().getStringExtra("districtId");
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
        // getIrisWeighInstallation(districtId,fromDate,toDate,fpscodee);
        swipeRefreshLayout!!.setOnRefreshListener {
            swipeRefreshLayout!!.isRefreshing = false
            getIrisWeighInstallation(districtId, fromDate, toDate, fpscodee)
        }


        val intent = intent

        // Get the value from the intent using the key
        if (intent != null) {
            val value_districtId = intent.getStringExtra("key_districtId")
            val value_fromDate = intent.getStringExtra("key_fromDate")
            val value_toDate = intent.getStringExtra("key_toDate")
            value_selectedDay = intent.getStringExtra("key_selectedDate")
            value_selectedDis = intent.getStringExtra("key_selectedDis")

            //    Log.d("value_selectedDay", value_selectedDay);
            //   binding.spinner.setSelection(2);
            districtId = value_districtId
            fromDate = value_fromDate
            toDate = value_toDate
            getIrisWeighInstallation(districtId, fromDate, toDate, fpscodee)
        } else {
            //  binding.spinner.setSelection(1);

            getIrisWeighInstallation(districtId, fromDate, toDate, fpscodee)
        }
        Log.d("----ApiValue-----", "$districtId $fromDate $toDate $fpscodee")


        // Log.d("useriduserid",""+prefManager.getUseR_TYPE_ID());


        // binding.textDestrict.setText(USER_DISTRICT);
        // getInstallationCntApi();
        setUpDateRangeSpinner()
    }


    private fun setClickListener() {
        binding!!.actionBar.ivBack.setOnClickListener {
            val i = Intent(this@InstalledList, WeighingScaleDashboard::class.java)
            startActivity(i)
        }

        binding!!.actionBar.buttonPDF.setOnClickListener {
            vibrator!!.vibrate(100)
            ExcelformTable()
        }




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
                            //    getPosError(expenseStatusId,districtId,fromDate,toDate,fpscodee);
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
            fpscodee = binding!!.inputfps.text.toString()
            getIrisWeighInstallation(districtId, fromDate, toDate, fpscodee)
        }
    }

    private fun getIrisWeighInstallation(
        districtId: String?,
        fromDate: String?,
        toDate: String?,
        fpscodee: String?
    ) {
        if (isNetworkAvailable(mActivity!!)) {
            hideKeyboard(mActivity)
            showProgress()
            val USER_Id = prefManager!!.useR_Id
            Log.d("USER_ID", USER_Id)
            val apiInterface = RetrofitInstance.getRetrofitInstance().create(
                APIService::class.java
            )
            //  Call<WeightInsRoot> call = apiInterface.apiIrisWeighInstallation(USER_Id,"",districtId,"","0",fromDate,toDate);
            val call = apiInterface.IrisWeighInstallation(
                USER_Id,
                fpscodee,
                districtId,
                "",
                "0",
                fromDate,
                toDate,
                ""
            )

            call.enqueue(object : Callback<InstalledRoot?> {
                override fun onResponse(
                    call: Call<InstalledRoot?>,
                    response: Response<InstalledRoot?>
                ) {
                    hideProgress()
                    if (response.isSuccessful) {
                        if (response.code() == 200) {
                            if (response.body() != null) {
                                binding!!.txtNoRecord.visibility = View.GONE
                                if (response.body()!!.status == "200") {
                                    val installedRoot = response.body()
                                    val message =
                                        installedRoot!!.message
                                    //  Toast.makeText(InstallationPendingList.this, message, Toast.LENGTH_SHORT).show();
                                    irisInsDataArrayList = installedRoot.data
                                    Log.d(
                                        "weighInsDataArrayList",
                                        "Size...." + irisInsDataArrayList!!.size
                                    )
                                    mRecyclerView!!.layoutManager =
                                        LinearLayoutManager(this@InstalledList)
                                    mRecyclerView!!.addItemDecoration(
                                        DividerItemDecoration(
                                            this@InstalledList,
                                            DividerItemDecoration.VERTICAL
                                        )
                                    )
                                    mRecyclerView!!.visibility = View.VISIBLE
                                    val deliveredWgtInsAdapter = InstalledAdapter(
                                        irisInsDataArrayList!!, mContext!!
                                    )
                                    mRecyclerView!!.adapter = deliveredWgtInsAdapter


                                    val sharedPreferences =
                                        getSharedPreferences("shared preferences", MODE_PRIVATE)

                                    // creating a variable for editor to
                                    // store data in shared preferences.
                                    val editor = sharedPreferences.edit()

                                    // creating a new variable for gson.
                                    val gson = Gson()

                                    // getting data from gson and storing it in a string.
                                    val json = gson.toJson(response.body()!!.data)

                                    // below line is to save data in shared
                                    // prefs in the form of string.
                                    editor.putString("IL_courses", json)

                                    // below line is to apply changes
                                    // and save data in shared prefs.
                                    editor.apply()


                                    // Use 'dataValue' or perform operations with other properties
                                } else {
                                    binding!!.txtNoRecord.visibility = View.VISIBLE
                                    mRecyclerView!!.visibility = View.GONE
                                }
                            } else {
                                binding!!.txtNoRecord.visibility = View.VISIBLE
                                mRecyclerView!!.visibility = View.GONE
                                makeToast(response.body()!!.message.toString())
                            }
                        } else {
                            makeToast(resources.getString(R.string.error))
                        }
                    } else {
                        makeToast(resources.getString(R.string.error))
                    }
                }

                override fun onFailure(call: Call<InstalledRoot?>, error: Throwable) {
                    hideProgress()
                    makeToast(resources.getString(R.string.error))
                    call.cancel()
                }
            })
        } else {
            hideProgress()
            makeToast(resources.getString(R.string.no_internet_connection))
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
        if (isNetworkAvailable(mActivity!!)) {
            hideKeyboard(mActivity)
            hideProgress()
            //   Utils.showCustomProgressDialogCommonForAll(mActivity, getResources().getString(R.string.please_wait));
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


                                        /* if(d2!=null)
                                        {
                                            spinnerDistrict.setSelection(Integer.parseInt(d2));

                                        }*/


                                        /*  if(d2!=null && !d2.isEmpty())
                                        {
                                            Log.d("ghjh",""+d2);

                                   int userId = Integer.parseInt(d2); // replace with the user ID you want to select

                                            for (int i = 0; i < dataAdapter.getCount(); i++) {
                                                ModelDistrictList user = dataAdapter.getItem(i);
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
        binding!!.spinner.setSelection(value_selectedDay!!.toInt())

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
        //  super.onBackPressed();
        val i = Intent(this@InstalledList, WeighingScaleDashboard::class.java)
        startActivity(i)
    }

    override fun makeToast(string: String?) {
        if (TextUtils.isEmpty(string)) return
        Toast.makeText(mActivity, string, Toast.LENGTH_SHORT).show()
    }


    private fun ExcelformTable() {
        mActivity = this
        //  prefManager = PrefManager.getInstance(mActivity);
        sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE)
        // creating a variable for gson.
        val gson = Gson()

        // below line is to get to string present from our
        // shared prefs if not present setting it as null.
        val json = sharedPreferences!!.getString("IL_courses", null)

        // below line is to get the type of our array list.
        val type = object : TypeToken<ArrayList<InstalledDatum?>?>() {}.type

        // in below line we are getting data from gson
        // and saving it to our array list
        irisInsDataArrayList = gson.fromJson(json, type)

        // checking below if the array list is empty or not
        if (irisInsDataArrayList == null) {
            // if the array list is empty
            // creating a new array list.
            irisInsDataArrayList = ArrayList()
            Log.d("nbb", "" + irisInsDataArrayList)
        }
        Log.d("gfhvbb", "" + irisInsDataArrayList)
        val originalFormat = "yyyy-MM-dd"
        val desiredFormat = "dd-MM-yyyy"
        //    String corStartDate = convertDateFormat(fromDate, originalFormat, desiredFormat);
        //   String corEndDate = convertDateFormat(toDate, originalFormat, desiredFormat);
        val corStartDate = fromDate
        val corEndDate = toDate
        Log.d("formateddate", corStartDate!!)

        val workbook = HSSFWorkbook()
        val firstSheet = workbook.createSheet("Installation Pending $corStartDate - $corEndDate")

        // Create cell style for center alignment
        val rowA = firstSheet.createRow(0)
        rowA.heightInPoints = 20f // Set row height in points
        val cellA = rowA.createCell(0)
        /*  cellA.setCellValue(new HSSFRichTextString("District\n name"));
        // Create a bold font
        HSSFFont boldFont = workbook.createFont();
        boldFont.setBold(true);

        // Apply the bold font to a new cell style
        HSSFCellStyle boldCellStyle = workbook.createCellStyle();
        boldCellStyle.setFont(boldFont);

        cellA.setCellStyle(boldCellStyle);

        firstSheet.setColumnWidth(0, 3000);
*/
        val boldFont: Font = workbook.createFont()
        boldFont.bold = true
        // Create a RichTextString with bold formatting
        val richText: RichTextString = HSSFRichTextString("District\nName")
        richText.applyFont(boldFont)
        // Set the RichTextString to the cell
        cellA.setCellValue(richText)
        firstSheet.setColumnWidth(0, 4000)
        val cellB = rowA.createCell(1)
        val richText1: RichTextString = HSSFRichTextString("FPS\nCode")

        richText1.applyFont(boldFont)
        // Set the RichTextString to the cell
        cellB.setCellValue(richText1)
        firstSheet.setColumnWidth(1, 2000)
        //   cellB.setCellStyle(cellStyle);
        val cellC = rowA.createCell(2)
        firstSheet.setColumnWidth(2, 11000)
        val richText2: RichTextString = HSSFRichTextString("Dealer\nName")
        richText2.applyFont(boldFont)
        cellC.setCellValue(richText2)

        //  cellC.setCellValue(new HSSFRichTextString("Punch\nDate"));
        val cellD = rowA.createCell(3)
        firstSheet.setColumnWidth(3, 4000)

        val richText3: RichTextString = HSSFRichTextString("DealerMobileNo")
        richText3.applyFont(boldFont)
        cellD.setCellValue(richText3)
        // cellD.setCellValue(new HSSFRichTextString("Punch\nDay"));
        val cellE = rowA.createCell(4)
        firstSheet.setColumnWidth(4, 4000)
        val richText4: RichTextString = HSSFRichTextString("W_Model")
        richText4.applyFont(boldFont)
        cellE.setCellValue(richText4)
        //  cellE.setCellValue(new HSSFRichTextString("PunchIn\nTime"));
        val cellF = rowA.createCell(5)
        firstSheet.setColumnWidth(5, 4000)
        val richText5: RichTextString = HSSFRichTextString("W_SerialNo")
        richText5.applyFont(boldFont)
        cellF.setCellValue(richText5)
        // cellF.setCellValue(new HSSFRichTextString("Punch\nOut\nTime"));
        //    cellF.setCellStyle(cellStyle);
        val cellG = rowA.createCell(6)
        firstSheet.setColumnWidth(6, 4000)
        val richText6: RichTextString = HSSFRichTextString("TicketNo")
        richText6.applyFont(boldFont)
        cellG.setCellValue(richText6)
        //  cellG.setCellValue(new HSSFRichTextString("AddressIn"));
        //    cellG.setCellStyle(cellStyle);
        val cellH = rowA.createCell(7)
        firstSheet.setColumnWidth(7, 5000)
        //cellH.setCellValue(new HSSFRichTextString("AddressOut"));
        val richText7: RichTextString = HSSFRichTextString("Delivered\nDate")
        richText7.applyFont(boldFont)
        cellH.setCellValue(richText7)



        Log.d("mylist", " -------------- $irisInsDataArrayList")
        if (irisInsDataArrayList != null && irisInsDataArrayList!!.size > 0) {
            for (i in irisInsDataArrayList!!.indices) {
                val detailsInfo = irisInsDataArrayList!![i]
                val districtName = detailsInfo.districtName.toString()
                val dName = detailsInfo.dealerName.toString()
                val fps = detailsInfo.fpscode.toString()
                val modelName = detailsInfo.weighingScaleModelName.toString()
                val serialNo = detailsInfo.weighingScaleSerialNo.toString()
                val date = detailsInfo.winghingScaleDeliveredOnStr.toString()
                val mobileNo = detailsInfo.dealerMobileNo.toString()
                val ticketNo = detailsInfo.ticketNo.toString()
                val dataRow: Row = firstSheet.createRow(i + 1) // Start from row 1 for data
                // Column 1: District Name
                dataRow.createCell(0).setCellValue(districtName)
                // Column 2: Attendance Value
                dataRow.createCell(1).setCellValue(fps)
                dataRow.createCell(2).setCellValue(dName)
                dataRow.createCell(3).setCellValue(mobileNo)
                dataRow.createCell(4).setCellValue(modelName)
                dataRow.createCell(5).setCellValue(serialNo)
                dataRow.createCell(6).setCellValue(ticketNo)
                dataRow.createCell(7).setCellValue(date)
            }
        }


        var fos: FileOutputStream? = null
        try {
            val str_path = Environment.getExternalStorageDirectory().toString()
            val file = File(str_path, getString(R.string.app_name) + ".xls")
            fos = FileOutputStream(filePathh)
            workbook.write(fos)
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            if (fos != null) {
                try {
                    fos.flush()
                    fos.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
            Toast.makeText(this@InstalledList, "Excel Sheet Download ", Toast.LENGTH_SHORT).show()
        }

        val timeMillis = System.currentTimeMillis()

        // Generate a random number.
        val random = Random()
        val randomNumber = random.nextInt(100000)

        // Combine the current date and time with the random number to generate a unique string.
        val fileName = String.format("excel_%d_%d", timeMillis, randomNumber)
        Log.d("fkddv", "fh$fileName")
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.setType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet") // Filter for Excel files

        try {
            startActivityForResult(
                intent,
                1
            ) // Use startActivityForResult to get the selected file's URI
        } catch (e: ActivityNotFoundException) {
            // Handle the case where no app capable of handling this intent is installed
        }
    }

    private fun generateUniqueFileName(originalFileName: String): String {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val fileExtension = getFileExtension(originalFileName)
        return "excel_$timeStamp.$fileExtension"
    }

    private fun getFileExtension(fileName: String): String {
        val dotIndex = fileName.lastIndexOf(".")
        if (dotIndex != -1 && dotIndex < fileName.length - 1) {
            return fileName.substring(dotIndex + 1).lowercase(Locale.getDefault())
        }
        return ""
    }

    fun clearSharePreference() {
        // super.onBackPressed();
        val sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        // Clearing the value associated with the "camp" key
        editor.remove("IL_courses")
        // Applying the changes to save the updated SharedPreferences
        editor.apply()
    }
}
