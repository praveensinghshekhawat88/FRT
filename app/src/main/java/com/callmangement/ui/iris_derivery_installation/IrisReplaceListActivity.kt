package com.callmangement.ui.iris_derivery_installation

import android.app.Activity
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.DatePicker
import android.widget.Spinner
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.callmangement.R
import com.callmangement.custom.CustomActivity
import com.callmangement.databinding.ActivityIrisReplacedListBinding
import com.callmangement.network.APIService
import com.callmangement.network.RetrofitInstance
import com.callmangement.ui.ins_weighing_scale.model.district.ModelDistrictList_w
import com.callmangement.ui.ins_weighing_scale.model.district.ModelDistrict_w
import com.callmangement.ui.iris_derivery_installation.Model.IrisDeliveryListResponse
import com.callmangement.ui.iris_derivery_installation.adapter.IrisReplacedAdapter
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

class IrisReplaceListActivity : CustomActivity() {
    private var binding: ActivityIrisReplacedListBinding? = null
    private val spinnerList: MutableList<String> = ArrayList()
    private var mActivity: Activity? = null
    private var prefManager: PrefManager? = null
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
    private var fpscodee: String? = null
    private var serialno: String? = null
    private var value_selectedDay: String? = null
    private var value_selectedDis: String? = null
    private var irisInsDataArrayList: ArrayList<IrisDeliveryListResponse.Datum?>? = ArrayList()
    private val irisInsDataArrayListBlock = ArrayList<IrisDeliveryListResponse.Datum?>()
    private var irisInstalledAdapter: IrisReplacedAdapter? = null
    private val block_List: MutableList<String?> = ArrayList()
    private var installedRoot = IrisDeliveryListResponse()

    private var isLoading = false
    private var allPagesLoaded = false
    private var currentPage = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE) //will hide the title
        // getSupportActionBar().hide(); // hide the title bar
        binding = ActivityIrisReplacedListBinding.inflate(
            layoutInflater
        )
        setContentView(binding!!.root)
        init()
    }

    private fun init() {
        mActivity = this
        mContext = this
        prefManager = PrefManager(mContext!!)
        binding!!.actionBar.ivBack.visibility = View.VISIBLE
        //     binding.actionBar.textToolbarTitle.setText(getResources().getString(R.string.installed_list));
        binding!!.actionBar.textToolbarTitle.text = "Iris Installed List"
        setUpData()
        setClickListener()
        //  setClickListener();
    }

    private fun setUpData() {
        if (prefManager!!.useR_TYPE_ID == "1" && prefManager!!.useR_TYPE
                .equals("Admin", ignoreCase = true)
        ) {
            binding!!.rlDistrict.visibility = View.VISIBLE
            binding!!.seDistrict.visibility = View.GONE
            binding!!.spacer.visibility = View.VISIBLE
            val sp = getSharedPreferences("your_prefs", MODE_PRIVATE)
            val editor = sp.edit()
            editor.remove("districtId_key")
            editor.apply()
        } else if (prefManager!!.useR_TYPE_ID == "2" && prefManager!!.useR_TYPE
                .equals("Manager", ignoreCase = true)
        ) {
            binding!!.rlDistrict.visibility = View.VISIBLE
            binding!!.spacer.visibility = View.VISIBLE
            binding!!.seDistrict.visibility = View.GONE
            val sp = getSharedPreferences("your_prefs", MODE_PRIVATE)
            val editor = sp.edit()
            editor.remove("districtId_key")
            editor.apply()
        } else if (prefManager!!.useR_TYPE_ID == "4" && prefManager!!.useR_TYPE
                .equals("ServiceEngineer", ignoreCase = true)
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

        //   getIrisWeighInstallation(districtId,fromDate,toDate,fpscodee);
        binding!!.rvDelivered.layoutManager = LinearLayoutManager(mContext)
        //            mRecyclerView.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL));
        irisInstalledAdapter = IrisReplacedAdapter(irisInsDataArrayListBlock, mContext!!)
        binding!!.rvDelivered.adapter = irisInstalledAdapter

        binding!!.rvDelivered.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val layoutManager = recyclerView.layoutManager as LinearLayoutManager?
                val visibleItemCount = layoutManager!!.childCount
                val totalItemCount = layoutManager.itemCount
                val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

                if (!isLoading && !allPagesLoaded && (visibleItemCount + firstVisibleItemPosition) >= totalItemCount && firstVisibleItemPosition >= 0) {
                    isLoading = true
                    getIrisDeliveryInstallationLForReplacement(
                        districtId,
                        fromDate,
                        toDate,
                        fpscodee,
                        serialno
                    )
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
            Log.d("value_selectedDay", "--$value_selectedDay")

            //   binding.spinner.setSelection(2);
            districtId = value_districtId
            fromDate = value_fromDate
            toDate = value_toDate
            getIrisDeliveryInstallationLForReplacement(
                districtId,
                fromDate,
                toDate,
                fpscodee,
                serialno
            )
        } else {
            //  binding.spinner.setSelection(1);
            getIrisDeliveryInstallationLForReplacement(
                districtId,
                fromDate,
                toDate,
                fpscodee,
                serialno
            )
        }
        // Log.d("useriduserid",""+prefManager.getUseR_TYPE_ID());
        // binding.textDestrict.setText(USER_DISTRICT);
        // getInstallationCntApi();
        setUpDateRangeSpinner()
    }

    private fun setClickListener() {
        binding!!.actionBar.ivBack.setOnClickListener { //Intent i = new Intent(IrisInstalledListActivity.this, WeighingScaleDashboard.class);
            //startActivity(i);
            onBackPressed()
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

        binding!!.spinnerBlock.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    adapterView: AdapterView<*>?,
                    view: View,
                    i: Int,
                    l: Long
                ) {
                    filterBlock(i)
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
            serialno = binding!!.inputserialno.text.toString()

            currentPage = 1
            irisInsDataArrayListBlock.clear()
            getIrisDeliveryInstallationLForReplacement(
                districtId,
                fromDate,
                toDate,
                fpscodee,
                serialno
            )
        }
    }

    private fun getIrisDeliveryInstallationLForReplacement(
        districtId: String?, fromDate: String?, toDate: String?, fpscodee: String?,
        serialno: String?
    ) {
        if (isNetworkAvailable(mActivity!!)) {
            hideKeyboard(mActivity)
            showProgress()
            val USER_Id = prefManager!!.useR_Id
            Log.d("USER_ID", "" + USER_Id)
            val apiInterface = RetrofitInstance.getRetrofitInstance().create(
                APIService::class.java
            )
            //  Call<WeightInsRoot> call = apiInterface.apiIrisWeighInstallation(USER_Id,"",districtId,"","0",fromDate,toDate);
            val call = apiInterface.IrisDeliveryInstallationLForReplacement(
                USER_Id, fpscodee, districtId,
                "", "0", fromDate, toDate, "", serialno, "", "", "1", "" + currentPage, "100"
            )
            call.enqueue(object : Callback<IrisDeliveryListResponse?> {
                override fun onResponse(
                    call: Call<IrisDeliveryListResponse?>,
                    response: Response<IrisDeliveryListResponse?>
                ) {
                    hideProgress()
                    if (response.isSuccessful) {
                        if (response.code() == 200) {
                            if (response.body() != null) {
                                binding!!.txtNoRecord.visibility = View.GONE
                                if (response.body()!!.status == "200") {
                                    installedRoot = response.body()!!
                                    val message = installedRoot.message

                                    allPagesLoaded =
                                        if (installedRoot.currentPage == installedRoot.totalPages) true
                                        else false


                                    //  Toast.makeText(InstallationPendingList.this, message, Toast.LENGTH_SHORT).show();
                                    irisInsDataArrayList!!.clear()
                                    //    irisInsDataArrayListBlock.clear();
                                    irisInsDataArrayList = installedRoot.data
                                    //    Collections.reverse(irisInsDataArrayList);
                                    irisInsDataArrayListBlock.addAll(irisInsDataArrayList!!)
                                    Log.d(
                                        "irisInsDataArrayList",
                                        "Size...." + irisInsDataArrayList!!.size
                                    )
                                    irisInstalledAdapter!!.notifyDataSetChanged()

                                    binding!!.rvDelivered.visibility = View.VISIBLE

                                    //    binding.rlBlock.setVisibility(View.VISIBLE);

                                    // Use 'dataValue' or perform operations with other properties
                                    block_List.clear()
                                    for (i in irisInsDataArrayList!!.indices) {
                                        if (!block_List.contains(irisInsDataArrayList!![i]!!.blockName)) {
                                            block_List.add(irisInsDataArrayList!![i]!!.blockName)
                                        }
                                    }
                                    block_List.add("Select Block")
                                    Collections.reverse(block_List)

                                    val dataAdapter = ArrayAdapter(
                                        mActivity!!,
                                        android.R.layout.simple_spinner_item, block_List
                                    )
                                    dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                                    binding!!.spinnerBlock.adapter = dataAdapter

                                    //  binding.spinnerDistrict.setSelection(Integer.parseInt(value_selectedDis));
                                    currentPage++


                                    binding!!.txtTotalPages.text =
                                        "Total Pages: " + installedRoot.totalPages
                                    binding!!.txtTotalRecord.text =
                                        "Total Records: " + installedRoot.totalItems
                                    binding!!.txtPageNumber.text =
                                        "Page Number: " + installedRoot.currentPage
                                } else {
                                    binding!!.txtNoRecord.visibility = View.VISIBLE
                                    binding!!.rvDelivered.visibility = View.GONE
                                    binding!!.rlBlock.visibility = View.GONE

                                    binding!!.txtTotalPages.text = "Total Pages: 0"
                                    binding!!.txtTotalRecord.text = "Total Records: 0"
                                    binding!!.txtPageNumber.text = "Page Number: 0"
                                }
                            } else {
                                binding!!.txtNoRecord.visibility = View.VISIBLE
                                binding!!.rvDelivered.visibility = View.GONE
                                binding!!.rlBlock.visibility = View.GONE
                                makeToast(response.body()!!.message.toString())
                                binding!!.txtTotalPages.text = "Total Pages: 0"
                                binding!!.txtTotalRecord.text = "Total Records: 0"
                                binding!!.txtPageNumber.text = "Page Number: 0"
                            }
                        } else {
                            makeToast(resources.getString(R.string.error))
                        }
                    } else {
                        makeToast(resources.getString(R.string.error))
                    }
                    isLoading = false
                }

                override fun onFailure(call: Call<IrisDeliveryListResponse?>, error: Throwable) {
                    hideProgress()
                    makeToast(resources.getString(R.string.error))
                    call.cancel()
                    isLoading = false
                }
            })
        } else {
            isLoading = false
            makeToast(resources.getString(R.string.no_internet_connection))
        }
    }

    private fun filterBlock(position: Int) {
        irisInsDataArrayListBlock.clear()
        if (position > 0) {
            for (i in irisInsDataArrayList!!.indices) {
                if (irisInsDataArrayList!![i]!!.blockName == block_List[position]) {
                    irisInsDataArrayListBlock.add(irisInsDataArrayList!![i])
                }
            }
            irisInstalledAdapter!!.notifyDataSetChanged()
        } else {
            irisInsDataArrayListBlock.addAll(irisInsDataArrayList!!)
            irisInstalledAdapter!!.notifyDataSetChanged()
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
                                            mActivity!!,
                                            android.R.layout.simple_spinner_item, district_List!!
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
        val dataAdapter = ArrayAdapter(
            this, android.R.layout.simple_spinner_item,
            spinnerList
        )
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
        super.onBackPressed()
        //    Intent i = new Intent(IrisInstalledListActivity.this, WeighingScaleDashboard.class);
        //    startActivity(i);
    }

    override fun makeToast(string: String?) {
        if (TextUtils.isEmpty(string)) return
        Toast.makeText(mActivity, string, Toast.LENGTH_SHORT).show()
    }
}
