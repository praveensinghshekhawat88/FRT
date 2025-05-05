package com.callmangement.ehr.ehrActivities

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
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.callmangement.BuildConfig
import com.callmangement.R
import com.callmangement.custom.CustomActivity
import com.callmangement.databinding.ActivityFeedbackformListBinding
import com.callmangement.ehr.adapter.FeedbackListAdapter
import com.callmangement.ehr.api.APIClient.getRetrofitClientWithoutHeaders
import com.callmangement.ehr.api.APIInterface
import com.callmangement.ehr.models.FeedbackbyDCListDatum
import com.callmangement.ehr.models.FeedbackbyDCListRoot
import com.callmangement.ehr.models.ModelSEUser
import com.callmangement.ehr.models.ModelSEUserList
import com.callmangement.ehr.support.Utils
import com.callmangement.ehr.support.Utils.hideCustomProgressDialogCommonForAll
import com.callmangement.ehr.support.Utils.hideKeyboard
import com.callmangement.ehr.support.Utils.showCustomProgressDialogCommonForAll
import com.callmangement.network.APIService
import com.callmangement.network.RetrofitInstance
import com.callmangement.ui.ins_weighing_scale.model.district.ModelDistrictList_w
import com.callmangement.ui.ins_weighing_scale.model.district.ModelDistrict_w
import com.callmangement.utils.Constants
import com.callmangement.utils.PrefManager
import com.google.common.reflect.TypeToken
import com.google.gson.Gson
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
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Collections
import java.util.Date
import java.util.Locale
import java.util.Objects


class FeedbackFormListActivity : BaseActivity() {

    private var binding: ActivityFeedbackformListBinding? = null
    private val spinnerList: MutableList<String> = ArrayList()
    private var mActivity: Activity? = null
    private var prefManager: PrefManager? = null
    private var mRecyclerView: RecyclerView? = null
    private var mContext: Context? = null
    private var spinnerDistrict: Spinner? = null
    private var spinnerSEUsers: Spinner? = null
    private var SEUserNameEng: String? = ""
    private var checkDistrict = 0
    private var districtNameEng = ""
    private var districtId: String? = "0"
    private var SEUserId: String? = "0"
    private var USER_Id: String? = null
    private val myFormat = "yyyy-MM-dd"
    private var fromDate: String? = ""
    private var toDate: String? = ""
    private val myCalendarFromDate: Calendar = Calendar.getInstance()
    private val myCalendarToDate: Calendar = Calendar.getInstance()
    private var district_List: MutableList<ModelDistrictList_w?>? = ArrayList()
    private var SEUser_list: MutableList<ModelSEUserList?> = ArrayList()
    private var dis: String? = null
    private var checkFilter = 0
    private var swipeRefreshLayout: SwipeRefreshLayout? = null
    private var fpscodee: String? = null
    private val selecteduserid: String? = null
    private var value_selectedDay: String? = null
    private var value_selectedDis: String? = null
    private var vibrator: Vibrator? = null
    private var sharedPreferences: SharedPreferences? = null
    private var feedbackbyDCListDatumArrayList: ArrayList<FeedbackbyDCListDatum>? = null
    private val originalFileName = "Demo.xlsx" // Original file name
    private val uniqueFileName = generateUniqueFileName(originalFileName)
    private val filePathh = File("/storage/emulated/0/Download/$uniqueFileName")
    private var checkSEUser = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE) //will hide the title
        // getSupportActionBar().hide(); // hide the title bar
        binding = ActivityFeedbackformListBinding.inflate(
            layoutInflater
        )
        setContentView(binding!!.root)
        init()
    }

    private fun init() {
        mActivity = this
        mContext = this
        clearSharePreference()

        prefManager = PrefManager(mContext!!)
        vibrator = getSystemService(VIBRATOR_SERVICE) as Vibrator

        swipeRefreshLayout = findViewById(R.id.refresh_list)
        binding!!.actionBar.ivBack.visibility = View.VISIBLE
        mRecyclerView = findViewById(R.id.rv_delivered)
        binding!!.actionBar.textToolbarTitle.text = resources.getString(R.string.feedback_form_list)
        USER_Id = prefManager!!.useR_Id

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
            binding!!.rlTehsil.visibility = View.VISIBLE

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
            binding!!.rlTehsil.visibility = View.VISIBLE

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
            binding!!.rlTehsil.visibility = View.GONE

            districtId = intent.getStringExtra("districtId")
            SEUserId = USER_Id

            if (districtId != null && districtId!!.isNotEmpty()) {
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
        spinnerSEUsers = findViewById(R.id.spinnerSEUsers)

        districtNameEng = "--" + resources.getString(R.string.district) + "--"
        SEUserNameEng = "--" + resources.getString(R.string.se_user) + "--"


        districtList()

        SEUser_list.reverse()
        val l = ModelSEUserList()
        l.userId = (-1).toString()
        l.userName = "--" + resources.getString(R.string.se_user) + "--"
        SEUser_list.add(l)
        SEUser_list.reverse()
        val dataAdapter =
            ArrayAdapter(mActivity!!, android.R.layout.simple_spinner_item, SEUser_list)
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerSEUsers!!.setAdapter(dataAdapter)

        val calendar = Calendar.getInstance()
        val today = calendar.time
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        val todayDate = sdf.format(today)
        fromDate = todayDate
        toDate = todayDate
        //   getIrisWeighInstallation(districtId,fromDate,toDate,fpscodee);
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
            clearSharePreference()
            val i = Intent(
                mContext,
                MainActivity::class.java
            )
            startActivity(i)
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


                    if (districtNameEng.equals(
                            "--" + resources.getString(R.string.district) + "--",
                            ignoreCase = true
                        )
                    ) {
                        SEUser_list.clear()
                        Collections.reverse(SEUser_list)
                        val list = ModelSEUserList()
                        list.userId = (-1).toString()
                        list.userName = "--" + resources.getString(R.string.se_user) + "--"
                        SEUser_list.add(list)
                        Collections.reverse(SEUser_list)
                        val dataAdapter = ArrayAdapter(
                            mActivity!!, android.R.layout.simple_spinner_item, SEUser_list
                        )
                        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        spinnerSEUsers!!.adapter = dataAdapter
                    } else {
                        SEUsersList(districtId)
                    }
                }
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {
            }
        }


        spinnerSEUsers!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>?, view: View, i: Int, l: Long) {
                if (++checkSEUser > 1) {
                    SEUserNameEng = SEUser_list[i]!!.userName
                    SEUserId = SEUser_list[i]!!.userId
                    if (SEUserId == "-1") {
                    } else {
                        val editor = getSharedPreferences("MyPrefsMyPrefs", MODE_PRIVATE).edit()
                        editor.putString("UserId", SEUserId)
                        editor.apply()
                        Log.d("ghgfh", "fghgh$SEUserNameEng$SEUserId")
                    }
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
            /*  SharedPreferences sh2 = getSharedPreferences("MyPrefsMyPrefs", MODE_PRIVATE);
                               selecteduserid = sh2.getString("UserId", "");
                               Log.d("selecteduserid", "" + selecteduserid);*/
            getIrisWeighInstallation(districtId, fromDate, toDate, fpscodee)
        }


        binding!!.mfExcel.setOnClickListener {
            vibrator!!.vibrate(100)
            if (feedbackbyDCListDatumArrayList != null && feedbackbyDCListDatumArrayList!!.size > 0) {
                //           showProgress();

                ExcelformTable()
            } else {
                Toast.makeText(mActivity, "No Data Found", Toast.LENGTH_SHORT).show()
            }
            //  ExcelformTable();
        }
    }

    private fun getIrisWeighInstallation(
        districtId: String?,
        fromDate: String?,
        toDate: String?,
        fpscodee: String?
    ) {
        if (Constants.isNetworkAvailable(mActivity!!)) {
            CustomActivity.hideKeyboard(mActivity)
            binding!!.progressBar.visibility = View.VISIBLE

            val USER_Id = prefManager!!.useR_Id


            val apiInterface = RetrofitInstance.getRetrofitInstance().create(
                APIService::class.java
            )

            //  Call<WeightInsRoot> call = apiInterface.apiIrisWeighInstallation(USER_Id,"",districtId,"","0",fromDate,toDate);
            val call = apiInterface.getVisitFeedbackbyDCList(
                USER_Id,
                "0",
                fpscodee,
                districtId,
                fromDate,
                toDate,
                SEUserId
            )
            Log.d("fromDate_toDate", "--$fromDate $toDate")
            Log.d(
                "responseis ",
                "$USER_Id  0$fpscodee  $districtId $fromDate$toDate $SEUserId"
            )

            call.enqueue(object : Callback<FeedbackbyDCListRoot?> {
                override fun onResponse(
                    call: Call<FeedbackbyDCListRoot?>,
                    response: Response<FeedbackbyDCListRoot?>
                ) {
                    binding!!.progressBar.visibility = View.GONE


                    if (response.isSuccessful) {
                        if (response.code() == 200) {
                            if (response.body() != null) {
                                binding!!.txtNoRecord.visibility = View.GONE
                                if (response.body()!!.status == "200") {
                                    val installedRoot = response.body()
                                    val message =
                                        installedRoot!!.message

                                    //  Toast.makeText(InstallationPendingList.this, message, Toast.LENGTH_SHORT).show();
                                    feedbackbyDCListDatumArrayList = installedRoot.data

                                    Log.d(
                                        "weighInsDataArrayList",
                                        "Size...." + feedbackbyDCListDatumArrayList!!.size
                                    )
                                    mRecyclerView!!.layoutManager =
                                        LinearLayoutManager(mContext)
                                    mRecyclerView!!.addItemDecoration(
                                        DividerItemDecoration(
                                            mContext,
                                            DividerItemDecoration.VERTICAL
                                        )
                                    )
                                    mRecyclerView!!.visibility = View.VISIBLE
                                    val feedbackListAdapter = FeedbackListAdapter(
                                        feedbackbyDCListDatumArrayList!!, mContext!!
                                    )
                                    mRecyclerView!!.adapter = feedbackListAdapter


                                    val sharedPreferences =
                                        getSharedPreferences("pm_shared preferences", MODE_PRIVATE)

                                    // creating a variable for editor to
                                    // store data in shared preferences.
                                    val editor = sharedPreferences.edit()

                                    // creating a new variable for gson.
                                    val gson = Gson()

                                    // getting data from gson and storing it in a string.
                                    val json = gson.toJson(feedbackbyDCListDatumArrayList)

                                    // below line is to save data in shared
                                    // prefs in the form of string.
                                    editor.putString("pm_courses", json)

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

                override fun onFailure(call: Call<FeedbackbyDCListRoot?>, error: Throwable) {
                    // hideProgress();
                    binding!!.progressBar.visibility = View.GONE


                    makeToast(resources.getString(R.string.error))

                    call.cancel()
                }
            })
        } else {
            makeToast(resources.getString(R.string.no_internet_connection))
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


    private fun SEUsersList(districtId: String?) {
        if (Utils.isNetworkAvailable(mActivity!!)) {
            hideKeyboard(mActivity!!)
            binding!!.progressBar.visibility = View.INVISIBLE
            showCustomProgressDialogCommonForAll(
                mActivity!!,
                resources.getString(R.string.please_wait)
            )
            val apiInterface = getRetrofitClientWithoutHeaders(
                mActivity!!,
                BaseUrl()!!
            ).create(
                APIInterface::class.java
            )
            val paramStr = SEUserListByDistictQueryParams()
            val splitArray =
                paramStr!!.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val paramData: MutableMap<String?, String?> = HashMap()
            paramData[splitArray[0]] = districtId
            val call = apiInterface.callSEUserListApiByDistict(SEUserListByDistict(), paramData)
            call!!.enqueue(object : Callback<ModelSEUser?> {
                override fun onResponse(
                    call: Call<ModelSEUser?>,
                    response: Response<ModelSEUser?>
                ) {
                    hideCustomProgressDialogCommonForAll()
                    binding!!.progressBar.visibility = View.GONE

                    if (response.isSuccessful) {
                        if (response.code() == 200) {
                            if (response.body() != null) {
                                if (response.body()!!.status == "200") {
                                    SEUser_list = response.body()!!.sEUsersList
                                    if (SEUser_list != null && SEUser_list.size > 0) {
                                        Collections.reverse(SEUser_list)
                                        val l = ModelSEUserList()
                                        l.userId = (-1).toString()
                                        l.userName =
                                            "--" + resources.getString(R.string.se_user) + "--"
                                        SEUser_list.add(l)
                                        Collections.reverse(SEUser_list)
                                        val dataAdapter = ArrayAdapter(
                                            mActivity!!,
                                            android.R.layout.simple_spinner_item,
                                            SEUser_list
                                        )
                                        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                                        spinnerSEUsers!!.adapter = dataAdapter
                                        Log.d("userid", "" + selecteduserid)
                                        if (selecteduserid != null && !selecteduserid.isEmpty()) {
                                            Log.d("ghjh", "" + selecteduserid)
                                            val userId =
                                                selecteduserid.toInt() // replace with the user ID you want to select
                                            for (i in 0 until dataAdapter.count) {
                                                val user = dataAdapter.getItem(i)
                                                if (user!!.userId.toString() == userId.toString()) {
                                                    spinnerSEUsers!!.setSelection(i)
                                                    break
                                                }
                                            }
                                        }
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

                override fun onFailure(call: Call<ModelSEUser?>, error: Throwable) {
                    hideCustomProgressDialogCommonForAll()
                    binding!!.progressBar.visibility = View.GONE

                    makeToast(resources.getString(R.string.error))

                    call.cancel()
                }
            })
        } else {
            makeToast(resources.getString(R.string.no_internet_connection))
        }
    }

    private fun districtList() {
        if (Constants.isNetworkAvailable(mActivity!!)) {
            CustomActivity.hideKeyboard(mActivity)
            // hideProgress();
            binding!!.progressBar.visibility = View.VISIBLE

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
                    //  hideProgress();
                    binding!!.progressBar.visibility = View.GONE
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


                                        // nowbygagan
                                        //    binding.spinnerDistrict.setSelection(Integer.parseInt(value_selectedDis));

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
                    //  hideProgress();
                    binding!!.progressBar.visibility = View.GONE


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
        binding!!.spinner.setSelection(3)
        val selectedItemPosition = binding!!.spinner.selectedItemPosition
        if (selectedItemPosition == 3) {
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
            getIrisWeighInstallation(districtId, fromDate, toDate, fpscodee)
        } else {
        }


        //NowbyGagan
        //  binding.spinner.setSelection(Integer.parseInt(value_selectedDay));
    }


    private fun ExcelformTable() {
        mActivity = this
        prefManager = PrefManager(mActivity!!)

        sharedPreferences = getSharedPreferences("pm_shared preferences", MODE_PRIVATE)
        // creating a variable for gson.
        val gson = Gson()

        // below line is to get to string present from our
        // shared prefs if not present setting it as null.
        val json = sharedPreferences!!.getString("pm_courses", null)

        // below line is to get the type of our array list.
        val type = object : TypeToken<ArrayList<FeedbackbyDCListDatum?>?>() {
        }.type

        // in below line we are getting data from gson
        // and saving it to our array list
        feedbackbyDCListDatumArrayList = gson.fromJson(json, type)

        // checking below if the array list is empty or not
        if (feedbackbyDCListDatumArrayList == null) {
            // if the array list is empty
            // creating a new array list.
            feedbackbyDCListDatumArrayList = ArrayList()
            Log.d("nbb", "" + feedbackbyDCListDatumArrayList)
        }
        Log.d("gfhvbb", "" + feedbackbyDCListDatumArrayList)

        val originalFormat = "yyyy-MM-dd"
        val desiredFormat = "dd-MM-yyyy"
        // mycomment
        val corStartDate = convertDateFormat(
            fromDate!!, originalFormat, desiredFormat
        )
        val corEndDate = convertDateFormat(toDate!!, originalFormat, desiredFormat)

        //  Log.d("formateddate", "" + corStartDate);
        val workbook = HSSFWorkbook()
        val firstSheet = workbook.createSheet("Record $corStartDate - $corEndDate")

        //  HSSFSheet firstSheet = workbook.createSheet("Record " );

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
        firstSheet.setColumnWidth(1, 4000)
        //   cellB.setCellStyle(cellStyle);
        val cellC = rowA.createCell(2)
        firstSheet.setColumnWidth(2, 4000)
        val richText2: RichTextString = HSSFRichTextString("FeedBack\nID")
        richText2.applyFont(boldFont)
        cellC.setCellValue(richText2)
        //  cellC.setCellValue(new HSSFRichTextString("Punch\nDate"));
        val cellD = rowA.createCell(3)
        firstSheet.setColumnWidth(3, 9000)
        val richText3: RichTextString = HSSFRichTextString("Service  \nEngineer")
        richText3.applyFont(boldFont)
        cellD.setCellValue(richText3)
        val cellE = rowA.createCell(4)
        firstSheet.setColumnWidth(4, 4000)
        val richText4: RichTextString = HSSFRichTextString("Date")
        richText4.applyFont(boldFont)
        cellE.setCellValue(richText4)

        // cellD.setCellValue(new HSSFRichTextString("Punch\nDay"));

        //  cellE.setCellValue(new HSSFRichTextString("PunchIn\nTime"));
        val cellF = rowA.createCell(5)
        firstSheet.setColumnWidth(5, 4000)
        val richText5: RichTextString = HSSFRichTextString("LogInTime")
        richText5.applyFont(boldFont)
        cellF.setCellValue(richText5)
        // cellF.setCellValue(new HSSFRichTextString("Punch\nOut\nTime"));
        //    cellF.setCellStyle(cellStyle);
        val cellG = rowA.createCell(6)
        firstSheet.setColumnWidth(6, 4000)
        val richText6: RichTextString = HSSFRichTextString("LogOutTime")
        richText6.applyFont(boldFont)
        cellG.setCellValue(richText6)
        //  cellG.setCellValue(new HSSFRichTextString("AddressIn"));
        //    cellG.setCellStyle(cellStyle);
        val cellH = rowA.createCell(7)
        firstSheet.setColumnWidth(7, 20000)
        //cellH.setCellValue(new HSSFRichTextString("AddressOut"));
        val richText7: RichTextString = HSSFRichTextString("Remark")
        richText7.applyFont(boldFont)
        cellH.setCellValue(richText7)


        val cellI = rowA.createCell(8)
        firstSheet.setColumnWidth(8, 20000)
        //cellH.setCellValue(new HSSFRichTextString("AddressOut"));
        val richText8: RichTextString = HSSFRichTextString("Address")
        richText8.applyFont(boldFont)
        cellI.setCellValue(richText8)


        Log.d("mylist", " -------------- $feedbackbyDCListDatumArrayList")
        if (feedbackbyDCListDatumArrayList != null && feedbackbyDCListDatumArrayList!!.size > 0) {
            for (i in feedbackbyDCListDatumArrayList!!.indices) {
                val detailsInfo = feedbackbyDCListDatumArrayList!![i]
                val districtName = detailsInfo.districtName.toString()
                val fps = detailsInfo.fpscode.toString()
                val feedbackid = detailsInfo.feedBackId.toString()
                val feedbackby = detailsInfo.feedBackBy.toString()

                val date =
                    detailsInfo.updatedOn.dayOfMonth.toString() + "-" + detailsInfo.updatedOn.monthValue + "-" + detailsInfo.updatedOn.year

                val Date = date.toString()
                val `in` = detailsInfo.logInTime.toString()
                val out = detailsInfo.logOutTime.toString()
                val remark = detailsInfo.remarks.toString()
                val address = detailsInfo.locationAddress.toString()
                val dataRow: Row = firstSheet.createRow(i + 1) // Start from row 1 for data
                // Column 1: District Name
                dataRow.createCell(0).setCellValue(districtName)
                // Column 2: Attendance Value
                dataRow.createCell(1).setCellValue(fps)
                dataRow.createCell(2).setCellValue(feedbackid)
                dataRow.createCell(3).setCellValue(feedbackby)
                dataRow.createCell(4).setCellValue(Date)
                dataRow.createCell(5).setCellValue(`in`)
                dataRow.createCell(6).setCellValue(out)
                dataRow.createCell(7).setCellValue(remark)
                dataRow.createCell(8).setCellValue(address)
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
            Toast.makeText(
                mContext,
                "Excel Sheet Download ",
                Toast.LENGTH_SHORT
            ).show()
        }


        /*  long timeMillis = System.currentTimeMillis();

        // Generate a random number.
        Random random = new Random();
        int randomNumber = random.nextInt(100000);

        // Combine the current date and time with the random number to generate a unique string.
        String fileName = String.format("excel_%d_%d", timeMillis, randomNumber);
        Log.d("fkddv", "fh" + fileName);
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");


        try {
            startActivityForResult(intent, 1); // Use startActivityForResult to get the selected file's URI
        } catch (ActivityNotFoundException e) {
        }*/


        // i replace top code with this
        try {
            val fileUri = FileProvider.getUriForFile(
                mContext!!,
                BuildConfig.APPLICATION_ID + ".provider",  // Replace with your app's provider authority
                filePathh
            )

            val openIntent = Intent(Intent.ACTION_VIEW)
            openIntent.setDataAndType(fileUri, "application/vnd.ms-excel")
            openIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            startActivity(openIntent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(
                mContext,
                "No app found to open Excel files.",
                Toast.LENGTH_SHORT
            ).show()
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


    /*  private  String generateUniqueFileName(String originalFileName) {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String fileExtension = getFileExtension(originalFileName);
        return "excel_" + timeStamp + (fileExtension.isEmpty() ? "" : "." + fileExtension);
    }

    private  String getFileExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf(".");
        if (dotIndex != -1 && dotIndex < fileName.length() - 1) {
            return fileName.substring(dotIndex + 1).toLowerCase();
        }
        return ""; // Return an empty string if no extension is found
    }*/
    fun clearSharePreference() {
        // super.onBackPressed();
        val sharedPreferences = getSharedPreferences("pm_shared preferences", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        // Clearing the value associated with the "camp" key
        editor.remove("pm_courses")
        // Applying the changes to save the updated SharedPreferences
        editor.apply()
    }


    override fun onBackPressed() {
        //  super.onBackPressed();
        clearSharePreference()

        val i = Intent(mContext, MainActivity::class.java)
        startActivity(i)
    }

    fun makeToast(string: String?) {
        if (TextUtils.isEmpty(string)) return
        Toast.makeText(mActivity, string, Toast.LENGTH_SHORT).show()
    }

    companion object {
        private fun convertDateFormat(
            dateString: String,
            originalFormat: String,
            desiredFormat: String
        ): String {
            val originalDateFormat = SimpleDateFormat(originalFormat, Locale.US)
            val desiredDateFormat = SimpleDateFormat(desiredFormat, Locale.US)

            try {
                // Parse the original date string into a Date object
                val date = originalDateFormat.parse(dateString)

                // Format the Date object into the desired format
                return desiredDateFormat.format(date)
            } catch (e: ParseException) {
                e.printStackTrace()
                return "" // Return an empty string if there's an error in parsing or formatting
            }
        }
    }
}
