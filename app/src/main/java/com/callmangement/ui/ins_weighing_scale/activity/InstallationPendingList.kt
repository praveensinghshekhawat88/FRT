package com.callmangement.ui.ins_weighing_scale.activity

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
import androidx.recyclerview.widget.RecyclerView
import com.callmangement.R
import com.callmangement.custom.CustomActivity
import com.callmangement.databinding.ActivityDeliverylistBinding
import com.callmangement.network.APIService
import com.callmangement.network.RetrofitInstance
import com.callmangement.ui.ins_weighing_scale.adapter.DeliveredWgtInsAdapter
import com.callmangement.ui.ins_weighing_scale.model.DeliveredWeightInstal.WeighInsData
import com.callmangement.ui.ins_weighing_scale.model.DeliveredWeightInstal.WeightInsRoot
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

class InstallationPendingList : CustomActivity() {
    private var binding: ActivityDeliverylistBinding? = null
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
    var fpscodee: String? = null
    var value_selectedDay: String? = null
    var value_selectedDis: String? = null
    private var vibrator: Vibrator? = null
    var sharedPreferences: SharedPreferences? = null
    var weighInsDataArrayList: ArrayList<WeighInsData>? = null
    var originalFileName: String = "Demo.xlsx" // Original file name

    // String uniqueFileName = generateUniqueFileName(originalFileName);
    // private final File filePathh = new File("/storage/emulated/0/Download/" + uniqueFileName);
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE) //will hide the title
        // getSupportActionBar().hide(); // hide the title bar
        binding = ActivityDeliverylistBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        init()
    }

    private fun init() {
        //   clearSharePreference();
        mActivity = this
        mContext = this
        prefManager = PrefManager(mContext!!)
        binding!!.actionBar.ivBack.visibility = View.VISIBLE
        binding!!.actionBar.buttonPDF.visibility = View.VISIBLE
        //  binding.actionBar.buttonPDF.setImageDrawable(getDrawable(R.drawable.excel));
        mRecyclerView = findViewById(R.id.rv_delivered)
        vibrator = getSystemService(VIBRATOR_SERVICE) as Vibrator
        binding!!.actionBar.textToolbarTitle.text =
            resources.getString(R.string.installation_pending_list)

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
        //  getIrisWeighInstallation();
        // districtId = getIntent().getStringExtra("districtId");
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

        //  getIrisWeighInstallation(districtId,fromDate,toDate,fpscodee);
        val intent = intent

        // Get the value from the intent using the key
        if (intent != null) {
            val value_districtId = intent.getStringExtra("key_districtId")
            val value_fromDate = intent.getStringExtra("key_fromDate")
            val value_toDate = intent.getStringExtra("key_toDate")

            value_selectedDay = intent.getStringExtra("key_selectedDate")
            value_selectedDis = intent.getStringExtra("key_selectedDis")
            Log.d("value_selectedDay", value_selectedDay!!)


            districtId = value_districtId
            fromDate = value_fromDate
            toDate = value_toDate
            getIrisWeighInstallation(districtId, fromDate, toDate, fpscodee)
        } else {
            binding!!.spinner.setSelection(1)

            getIrisWeighInstallation(districtId, fromDate, toDate, fpscodee)
        }


        // Log.d("useriduserid",""+prefManager.getUseR_TYPE_ID());


        // binding.textDestrict.setText(USER_DISTRICT);
        // getInstallationCntApi();
        setUpDateRangeSpinner()
    }


    private fun setBinding() {
    }


    private fun setClickListener() {
        binding!!.actionBar.ivBack.setOnClickListener { onBackPressed() }


        /* binding.actionBar.buttonPDF.setVisibility(View.GONE);
 binding.actionBar.buttonPDF.setOnClickListener(new View.OnClickListener() {
     @Override
     public void onClick(View view) {
         vibrator.vibrate(100);
         ExcelformTable();
     }
 });*/

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


            //  showProgress(getResources().getString(R.string.please_wait));
            val USER_Id = prefManager!!.useR_Id
            Log.d("USER_ID", " $USER_Id")
            Log.d("myDistrictId", " $districtId")
            Log.d("myfromDate", " $fromDate")
            Log.d("mytoDate", " $toDate")


            val apiInterface = RetrofitInstance.getRetrofitInstance().create(
                APIService::class.java
            )

            //  Call<WeightInsRoot> call = apiInterface.apiIrisWeighInstallation(USER_Id,"",districtId,"","0",fromDate,toDate);
            val call = apiInterface.apiIrisWeighInstallation(
                USER_Id,
                fpscodee,
                districtId,
                "",
                "1",
                fromDate,
                toDate
            )

            call.enqueue(object : Callback<WeightInsRoot?> {
                override fun onResponse(
                    call: Call<WeightInsRoot?>,
                    response: Response<WeightInsRoot?>
                ) {
                    hideProgress()

                    if (response.isSuccessful) {
                        if (response.code() == 200) {
                            if (response.body() != null) {
                                binding!!.txtNoRecord.visibility = View.GONE


                                if (response.body()!!.status == "200") {
                                    binding!!.actionBar.buttonPDF.visibility = View.VISIBLE

                                    // binding.buttonexcel.setVisibility(View.VISIBLE);
                                    val weightInsRoot = response.body()
                                    val message =
                                        weightInsRoot!!.message

                                    //  Toast.makeText(InstallationPendingList.this, message, Toast.LENGTH_SHORT).show();

                                    //     ArrayList<WeighInsData> weighInsDataArrayList = weightInsRoot.getData();
                                    weighInsDataArrayList = weightInsRoot.data
                                    Log.d(
                                        "weighInsDataArrayList",
                                        "Size...." + weighInsDataArrayList!!.size
                                    )

                                    mRecyclerView!!.layoutManager =
                                        LinearLayoutManager(this@InstallationPendingList)
                                    mRecyclerView!!.addItemDecoration(
                                        DividerItemDecoration(
                                            this@InstallationPendingList,
                                            DividerItemDecoration.VERTICAL
                                        )
                                    )
                                    mRecyclerView!!.visibility = View.VISIBLE
                                    val deliveredWgtInsAdapter = DeliveredWgtInsAdapter(
                                        weighInsDataArrayList!!,
                                        mContext,
                                        mActivity,
                                        prefManager!!.useR_TYPE_ID!!
                                    )
                                    mRecyclerView!!.adapter = deliveredWgtInsAdapter


                                    /*    SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);

                                    // creating a variable for editor to
                                    // store data in shared preferences.
                                    SharedPreferences.Editor editor = sharedPreferences.edit();

                                    // creating a new variable for gson.
                                    Gson gson = new Gson();

                                    // getting data from gson and storing it in a string.
                                    String json = gson.toJson(  response.body().getData());

                                    // below line is to save data in shared
                                    // prefs in the form of string.
                                    editor.putString("courses", json);

                                    // below line is to apply changes
                                    // and save data in shared prefs.
                                    editor.apply();*/


                                    // Use 'dataValue' or perform operations with other properties
                                } else {
                                    binding!!.txtNoRecord.visibility = View.VISIBLE
                                    mRecyclerView!!.visibility = View.GONE
                                    showAlertDialogWithSingleButton(
                                        mActivity,
                                        response.body()!!.message
                                    )
                                    binding!!.actionBar.buttonPDF.visibility = View.GONE
                                }
                            } else {
                                binding!!.txtNoRecord.visibility = View.VISIBLE
                                mRecyclerView!!.visibility = View.GONE
                                binding!!.actionBar.buttonPDF.visibility = View.GONE


                                showAlertDialogWithSingleButton(
                                    mActivity,
                                    response.body()!!.message
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
                }

                override fun onFailure(call: Call<WeightInsRoot?>, error: Throwable) {
                    hideProgress()

                    showAlertDialogWithSingleButton(mActivity, error.message)
                    binding!!.actionBar.buttonPDF.visibility = View.GONE

                    call.cancel()
                }
            })
        } else {
            makeToast(resources.getString(R.string.no_internet_connection))
            binding!!.actionBar.buttonPDF.visibility = View.GONE
        }
    }

    private fun updateLabelFromDate() {
        val myFormat = "yyyy-MM-dd"
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        binding!!.textFromDate.setText(sdf.format(myCalendarFromDate.time))
        binding!!.textToDate.text!!.clear()
        fromDate = binding!!.textFromDate.text.toString().trim { it <= ' ' }
    }

    private fun updateLabelToDate() {
        val myFormat = "yyyy-MM-dd"
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        binding!!.textToDate.setText(sdf.format(myCalendarToDate.time))
        toDate = binding!!.textToDate.text.toString().trim { it <= ' ' }
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 444) {
            if (resultCode == RESULT_OK) {
                getIrisWeighInstallation(districtId, fromDate, toDate, fpscodee)
            } else {
            }
        }
    } //excel
    /*    private void ExcelformTable() {

        mActivity = this;
        //  prefManager = PrefManager.getInstance(mActivity);
        sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        // creating a variable for gson.
        Gson gson = new Gson();

        // below line is to get to string present from our
        // shared prefs if not present setting it as null.
        String json = sharedPreferences.getString("courses", null);

        // below line is to get the type of our array list.
        Type type = new TypeToken<ArrayList<WeighInsData>>() {}.getType();

        // in below line we are getting data from gson
        // and saving it to our array list
        weighInsDataArrayList = gson.fromJson(json, type);

        // checking below if the array list is empty or not
        if (weighInsDataArrayList == null) {
            // if the array list is empty
            // creating a new array list.
            weighInsDataArrayList = new ArrayList<>();
            Log.d("nbb",""+weighInsDataArrayList);

        }
        Log.d("gfhvbb",""+weighInsDataArrayList);

        String originalFormat = "yyyy-MM-dd";
        String desiredFormat = "dd-MM-yyyy";
        //    String corStartDate = convertDateFormat(fromDate, originalFormat, desiredFormat);
        //   String corEndDate = convertDateFormat(toDate, originalFormat, desiredFormat);
        String corStartDate = fromDate;
        String corEndDate = toDate;
        Log.d("formateddate", corStartDate);

        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet firstSheet = workbook.createSheet("Installation Pending "+corStartDate+" - "+corEndDate);

        // Create cell style for center alignment

        HSSFRow rowA = firstSheet.createRow(0);
        rowA.setHeightInPoints(20); // Set row height in points
        HSSFCell cellA = rowA.createCell(0);
      */
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
    /*
        Font boldFont = workbook.createFont();
        boldFont.setBold(true);
        // Create a RichTextString with bold formatting
        RichTextString richText = new HSSFRichTextString("District\nName");
        richText.applyFont(boldFont);
        // Set the RichTextString to the cell
        cellA.setCellValue(richText);
        firstSheet.setColumnWidth(0, 4000);
        HSSFCell cellB = rowA.createCell(1);
        RichTextString richText1 = new HSSFRichTextString("FPS\nCode");

        richText1.applyFont(boldFont);
        // Set the RichTextString to the cell
        cellB.setCellValue(richText1);
        firstSheet.setColumnWidth(1, 2000);
        //   cellB.setCellStyle(cellStyle);
        HSSFCell cellC = rowA.createCell(2);
        firstSheet.setColumnWidth(2, 11000);
        RichTextString richText2 = new HSSFRichTextString("Dealer\nName");
        richText2.applyFont(boldFont);
        cellC.setCellValue(richText2);

        //  cellC.setCellValue(new HSSFRichTextString("Punch\nDate"));

        HSSFCell cellD = rowA.createCell(3);
        firstSheet.setColumnWidth(3, 4000);

        RichTextString richText3 = new HSSFRichTextString("DealerMobileNo");
        richText3.applyFont(boldFont);
        cellD.setCellValue(richText3);
        // cellD.setCellValue(new HSSFRichTextString("Punch\nDay"));
        HSSFCell cellE = rowA.createCell(4);
        firstSheet.setColumnWidth(4, 4000);
        RichTextString richText4 = new HSSFRichTextString("W_Model");
        richText4.applyFont(boldFont);
        cellE.setCellValue(richText4);
        //  cellE.setCellValue(new HSSFRichTextString("PunchIn\nTime"));
        HSSFCell cellF = rowA.createCell(5);
        firstSheet.setColumnWidth(5, 4000);
        RichTextString richText5 = new HSSFRichTextString("W_SerialNo");
        richText5.applyFont(boldFont);
        cellF.setCellValue(richText5);
        // cellF.setCellValue(new HSSFRichTextString("Punch\nOut\nTime"));
        //    cellF.setCellStyle(cellStyle);
        HSSFCell cellG = rowA.createCell(6);
        firstSheet.setColumnWidth(6, 4000);
        RichTextString richText6 = new HSSFRichTextString("TicketNo");
        richText6.applyFont(boldFont);
        cellG.setCellValue(richText6);
        //  cellG.setCellValue(new HSSFRichTextString("AddressIn"));
        //    cellG.setCellStyle(cellStyle);
        HSSFCell cellH = rowA.createCell(7);
        firstSheet.setColumnWidth(7, 5000);
        //cellH.setCellValue(new HSSFRichTextString("AddressOut"));
        RichTextString richText7 = new HSSFRichTextString("Delivered\nDate");
        richText7.applyFont(boldFont);
        cellH.setCellValue(richText7);



        Log.d("mylist"," -------------- "+weighInsDataArrayList);
        if(weighInsDataArrayList != null && weighInsDataArrayList.size() > 0) {
            for (int i = 0; i < weighInsDataArrayList.size(); i++) {
                WeighInsData detailsInfo = weighInsDataArrayList.get(i);
                String districtName = String.valueOf(detailsInfo.getDistrictName());
                String dName = String.valueOf(detailsInfo.getDealerName());
                String fps = String.valueOf(detailsInfo.getFpscode());
                String modelName = String.valueOf(detailsInfo.getWeighingScaleModelName());
                String serialNo = String.valueOf(detailsInfo.getWeighingScaleSerialNo());
                String date = String.valueOf(detailsInfo.getWinghingScaleDeliveredOnStr());
                String mobileNo = String.valueOf(detailsInfo.getDealerMobileNo());
                String ticketNo = String.valueOf(detailsInfo.getTicketNo());
                Row dataRow = firstSheet.createRow(i + 1); // Start from row 1 for data
                // Column 1: District Name
                dataRow.createCell(0).setCellValue(districtName);
                // Column 2: Attendance Value
                dataRow.createCell(1).setCellValue(fps);
                dataRow.createCell(2).setCellValue(dName);
                dataRow.createCell(3).setCellValue(mobileNo);
                dataRow.createCell(4).setCellValue(modelName);
                dataRow.createCell(5).setCellValue(serialNo);
                dataRow.createCell(6).setCellValue(ticketNo);
                dataRow.createCell(7).setCellValue(date);
            }
        }



        FileOutputStream fos = null;
        try {
            String str_path = Environment.getExternalStorageDirectory().toString();
            File file ;
            file = new File(str_path, getString(R.string.app_name) + ".xls");
            fos = new FileOutputStream(filePathh);
            workbook.write(fos);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.flush();
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            Toast.makeText(InstallationPendingList.this, "Excel Sheet Download ", Toast.LENGTH_SHORT).show();
        }

        long timeMillis = System.currentTimeMillis();

        // Generate a random number.
        Random random = new Random();
        int randomNumber = random.nextInt(100000);

        // Combine the current date and time with the random number to generate a unique string.
        String fileName = String.format("excel_%d_%d", timeMillis, randomNumber);
        Log.d("fkddv","fh"+fileName);
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"); // Filter for Excel files

        try {
            startActivityForResult(intent, 1); // Use startActivityForResult to get the selected file's URI
        } catch (ActivityNotFoundException e) {
            // Handle the case where no app capable of handling this intent is installed
        }


    }

    private String generateUniqueFileName(String originalFileName) {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String fileExtension = getFileExtension(originalFileName);
        return "excel_" + timeStamp + "." + fileExtension;
    }

    private String getFileExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf(".");
        if (dotIndex != -1 && dotIndex < fileName.length() - 1) {
            return fileName.substring(dotIndex + 1).toLowerCase();
        }
        return "";
    }

    public void clearSharePreference(){
        // super.onBackPressed();
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
// Clearing the value associated with the "camp" key
        editor.remove("courses");
// Applying the changes to save the updated SharedPreferences
        editor.apply();
    }*/
}
