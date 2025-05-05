package com.callmangement.ehr.ehrActivities

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Vibrator
import android.text.TextUtils
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.LinearLayoutManager
import com.callmangement.R
import com.callmangement.databinding.ActivityCampsListingBinding
import com.callmangement.ehr.adapter.CampListingAdapter
import com.callmangement.ehr.adapter.CampListingAdapter.OnItemCampListClickListener
import com.callmangement.ehr.api.APIClient.getRetrofitClientWithoutHeaders
import com.callmangement.ehr.api.APIInterface
import com.callmangement.ehr.api.Constants
import com.callmangement.ehr.models.CampDetailsInfo
import com.callmangement.ehr.models.CampDocInfo
import com.callmangement.ehr.models.DealersInfo
import com.callmangement.ehr.models.ModelAllDealersByBlock
import com.callmangement.ehr.models.ModelCampSchedule
import com.callmangement.ehr.models.ModelDeleteACamp
import com.callmangement.ehr.models.ModelDistrict
import com.callmangement.ehr.models.ModelDistrictList
import com.callmangement.ehr.models.ModelGetCampDocList
import com.callmangement.ehr.models.ModelOrganiseACamp
import com.callmangement.ehr.models.ModelTehsil
import com.callmangement.ehr.models.ModelTehsilList
import com.callmangement.ehr.support.EqualSpacingItemDecoration
import com.callmangement.ehr.support.OnSingleClickListener
import com.callmangement.ehr.support.Utils.hideCustomProgressDialogCommonForAll
import com.callmangement.ehr.support.Utils.hideKeyboard
import com.callmangement.ehr.support.Utils.isNetworkAvailable
import com.callmangement.ehr.support.Utils.showCustomProgressDialogCommonForAll
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
import java.io.Serializable
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Collections
import java.util.Date
import java.util.Locale
import java.util.Objects
import java.util.Random

class CampsListingActivity : BaseActivity() {
    var mActivity: Activity? = null
    private var binding: ActivityCampsListingBinding? = null

    var preference: PrefManager? = null

    private var campDetails_infoArrayList: List<CampDetailsInfo>? = ArrayList()
    private var adapter: CampListingAdapter? = null
    val LAUNCH_CAMP_CREATION_ACTIVITY: Int = 155
    private var spinnerDistrict: Spinner? = null
    private var spinnerTehsil: Spinner? = null
    private var inputStartDate: EditText? = null
    private var inputEndDate: EditText? = null
    private var startDate = ""
    private var endDate = ""
    private var trainingNumber = ""
    private var districtId: String? = "0"
    private var checkDistrict = 0
    private var districtNameEng: String? = ""
    private var checkTehsil = 0
    private var tehsilNameEng: String? = ""
    private var tehsilId: String? = "0"
    var d2: String? = null
    var tehsilvalue_2: String? = null
    private var district_List: MutableList<ModelDistrictList?> = ArrayList()
    private var tehsil_list: MutableList<ModelTehsilList?> = ArrayList()
    private val myCalendarToDate: Calendar = Calendar.getInstance()
    private val myCalendarFromDate: Calendar = Calendar.getInstance()
    var spinner: Spinner? = null
    private var checkFilter = 0
    var layoutDateRange: LinearLayoutCompat? = null
    private val myFormat = "yyyy-MM-dd"

    // private final String myFormat = "yyyy-MM-dd";
    private var vibrator: Vibrator? = null

    var sharedPreferences: SharedPreferences? = null


    var originalFileName: String = "Demo.xlsx" // Original file name
    var uniqueFileName: String = generateUniqueFileName(originalFileName)

    //File filePathh = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), uniqueFileName);
    private val filePathh = File("/storage/emulated/0/Download/$uniqueFileName")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        binding = ActivityCampsListingBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        init()
    }

    private fun init() {
        clearSharePreference()
        mActivity = this
        preference = PrefManager(mActivity!!)
        vibrator = getSystemService(VIBRATOR_SERVICE) as Vibrator
        val preferences = getSharedPreferences("MyPrefsMyPrefs", MODE_PRIVATE)
        val editor = preferences.edit()
        editor.remove("districtId")
        editor.remove("UserId")
        editor.apply()
        binding!!.actionBar.ivBack.visibility = View.VISIBLE
        binding!!.actionBar.ivThreeDot.visibility = View.GONE
        binding!!.actionBar.layoutLanguage.visibility = View.GONE
        binding!!.actionBar.buttonPDF.visibility = View.GONE
        binding!!.actionBar.textToolbarTitle.text = resources.getString(R.string.manage_camps)
        campDetails_infoArrayList = ArrayList()
        adapter = CampListingAdapter(
            mActivity!!,
            campDetails_infoArrayList,
            onItemCampListClickListener
        )
        binding!!.rvAllCamps.setHasFixedSize(true)
        binding!!.rvAllCamps.layoutManager =
            LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false)
        binding!!.rvAllCamps.addItemDecoration(
            EqualSpacingItemDecoration(
                30,
                EqualSpacingItemDecoration.VERTICAL
            )
        )
        binding!!.rvAllCamps.adapter = adapter

        binding!!.linCreateItem.visibility = View.GONE
        binding!!.actionBar.buttonFilter.visibility = View.GONE
        binding!!.buttonprint.visibility = View.GONE
        binding!!.buttonprint.setOnClickListener {
            vibrator!!.vibrate(100)
            try {
                Excelform()
            } catch (e: ParseException) {
                throw RuntimeException(e)
            }
        }

        binding!!.buttonexcel.visibility = View.GONE
        binding!!.buttonexcel.setOnClickListener {
            vibrator!!.vibrate(100)
            ExcelformTable()
        }

        val USER_TYPE = preference!!.useR_TYPE
        val USER_TYPE_ID = preference!!.useR_TYPE_ID
        if (USER_TYPE_ID == "1" && USER_TYPE.equals("Admin", ignoreCase = true)) {
            binding!!.linCreateItem.visibility = View.GONE
            binding!!.actionBar.buttonFilter.visibility = View.VISIBLE
            binding!!.buttonprint.visibility = View.VISIBLE
            binding!!.buttonexcel.visibility = View.VISIBLE
        } else if (USER_TYPE_ID == "2" && USER_TYPE.equals("Manager", ignoreCase = true)) {
            binding!!.linCreateItem.visibility = View.GONE
            binding!!.actionBar.buttonFilter.visibility = View.VISIBLE
            binding!!.buttonprint.visibility = View.VISIBLE
            binding!!.buttonexcel.visibility = View.VISIBLE
        } else if (USER_TYPE_ID == "4" && USER_TYPE.equals("ServiceEngineer", ignoreCase = true)) {
            binding!!.linCreateItem.visibility = View.VISIBLE
            binding!!.actionBar.buttonFilter.visibility = View.GONE
            binding!!.buttonprint.visibility = View.VISIBLE
            binding!!.buttonexcel.visibility = View.VISIBLE
        }

        setClickListener()

        var USER_DISTRICT_Id = preference!!.useR_DistrictId

        if (USER_TYPE_ID == "1" && USER_TYPE.equals("Admin", ignoreCase = true)) {
            USER_DISTRICT_Id = "0"
        } else if (USER_TYPE_ID == "2" && USER_TYPE.equals("Manager", ignoreCase = true)) {
            USER_DISTRICT_Id = "0"
        }
        getTraining(USER_DISTRICT_Id, "0", "", "", "", "0")
    }


    fun clearSharePreference() {
        val sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        // Clearing the value associated with the "camp" key
        editor.remove("coursescamp")
        // Applying the changes to save the updated SharedPreferences
        editor.apply()
    }


    private fun filterDialog() {
        try {
            val dialog = Dialog(mActivity!!)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setContentView(R.layout.dialog_filter_camp)
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.setCanceledOnTouchOutside(true)
            spinner = dialog.findViewById(R.id.spinner)
            layoutDateRange = dialog.findViewById(R.id.layout_date_range)
            spinnerDistrict = dialog.findViewById(R.id.spinnerDistrict)
            spinnerTehsil = dialog.findViewById(R.id.spinnerTehsil)
            inputStartDate = dialog.findViewById(R.id.inputStartDate)
            inputEndDate = dialog.findViewById(R.id.inputEndDate)
            val inputTrainingNumber = dialog.findViewById<EditText>(R.id.inputTrainingNumber)
            val buttonTrainingFilter = dialog.findViewById<Button>(R.id.buttonTrainingFilter)
            districtNameEng = "--" + resources.getString(R.string.district) + "--"
            tehsilNameEng = "--" + resources.getString(R.string.tehsil) + "--"
            districtList()
            Collections.reverse(tehsil_list)
            val l = ModelTehsilList()
            l.tehsilId = (-1).toString()
            l.tehsilNameEng = "--" + resources.getString(R.string.tehsil) + "--"
            tehsil_list.add(l)
            Collections.reverse(tehsil_list)
            val dataAdapter = ArrayAdapter(
                mActivity!!, android.R.layout.simple_spinner_item, tehsil_list
            )
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerTehsil!!.setAdapter(dataAdapter)
            val spinnerList: MutableList<String> = ArrayList()
            spinnerList.add("--" + resources.getString(R.string.select_filter) + "--")
            spinnerList.add(resources.getString(R.string.today))
            spinnerList.add(resources.getString(R.string.yesterday))
            spinnerList.add(resources.getString(R.string.current_month))
            spinnerList.add(resources.getString(R.string.previous_month))
            spinnerList.add(resources.getString(R.string.custom_filter))
            val dataAdapterr = ArrayAdapter(this, android.R.layout.simple_spinner_item, spinnerList)
            dataAdapterr.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner!!.setAdapter(dataAdapterr)
            spinner!!.setSelection(1)
            val selectedString = spinner!!.getSelectedItem() as String
            val selectedItemPosition = spinner!!.getSelectedItemPosition()
            if (selectedItemPosition == 1) {
                layoutDateRange!!.setVisibility(View.GONE)
                val calendar = Calendar.getInstance()
                val today = calendar.time
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                val todayDate = sdf.format(today)
                startDate = todayDate
                endDate = todayDate
            } else {
            }
            spinner!!.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
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
                            inputStartDate!!.getText().clear()
                            inputEndDate!!.getText().clear()
                            if (item.equals(
                                    resources.getString(R.string.today),
                                    ignoreCase = true
                                )
                            ) {
                                layoutDateRange!!.setVisibility(View.GONE)
                                val calendar = Calendar.getInstance()
                                val today = calendar.time
                                val sdf = SimpleDateFormat(myFormat, Locale.US)
                                val todayDate = sdf.format(today)
                                startDate = todayDate
                                endDate = todayDate
                                //  getAttendanceList(USER_Id,startDate,endDate);
                            } else if (item.equals(
                                    resources.getString(R.string.yesterday),
                                    ignoreCase = true
                                )
                            ) {
                                layoutDateRange!!.setVisibility(View.GONE)
                                val calendar = Calendar.getInstance()
                                calendar.add(Calendar.DAY_OF_YEAR, -1)
                                val yesterday = calendar.time
                                val sdf = SimpleDateFormat(myFormat, Locale.US)
                                val yesterdayDate = sdf.format(yesterday)
                                startDate = yesterdayDate
                                endDate = yesterdayDate


                                // getReportList(districtId,startDate,endDate);
                                //  getAttendanceList(USER_Id,startDate,endDate);
                            } else if (item.equals(
                                    resources.getString(R.string.current_month),
                                    ignoreCase = true
                                )
                            ) {
                                layoutDateRange!!.setVisibility(View.GONE)
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

                                startDate = date1
                                endDate = date2

                                //   getAttendanceList(USER_Id,startDate,endDate);
                                //   getReportList(districtId,startDate,endDate);
                            } else if (item.equals(
                                    resources.getString(R.string.previous_month),
                                    ignoreCase = true
                                )
                            ) {
                                layoutDateRange!!.setVisibility(View.GONE)
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
                                startDate = date1
                                endDate = date2

                                //  getAttendanceList(USER_Id,startDate,endDate);
                                //  getReportList(districtId,startDate,endDate);
                            } else if (item.equals(
                                    resources.getString(R.string.custom_filter),
                                    ignoreCase = true
                                )
                            ) {
                                startDate = ""
                                endDate = ""
                                layoutDateRange!!.setVisibility(View.VISIBLE)
                            }
                        } else {
                            startDate = ""
                            endDate = ""
                            Objects.requireNonNull(inputStartDate!!.getText()).clear()
                            Objects.requireNonNull(inputEndDate!!.getText()).clear()
                            layoutDateRange!!.setVisibility(View.GONE)

                            // getAttendanceList(USER_Id,startDate,endDate);

                            // getReportList(districtId,startDate,endDate);
                        }
                    }
                }

                override fun onNothingSelected(adapterView: AdapterView<*>?) {
                }
            })

            spinnerDistrict!!.setOnItemSelectedListener(object :
                AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    adapterView: AdapterView<*>?,
                    view: View,
                    i: Int,
                    l: Long
                ) {
                    if (++checkDistrict > 1) {
                        districtNameEng = district_List[i]!!.districtNameEng
                        districtId = district_List[i]!!.districtId
                        val editor = getSharedPreferences("MyPrefsMyPrefs", MODE_PRIVATE).edit()
                        editor.putString("districtId", districtId)
                        editor.apply()
                        if (districtNameEng.equals(
                                "--" + resources.getString(R.string.district) + "--",
                                ignoreCase = true
                            )
                        ) {
                            tehsil_list.clear()
                            Collections.reverse(tehsil_list)
                            val list = ModelTehsilList()
                            list.tehsilId = (-1).toString()
                            list.tehsilNameEng = "--" + resources.getString(R.string.tehsil) + "--"
                            tehsil_list.add(list)
                            Collections.reverse(tehsil_list)
                            val dataAdapter = ArrayAdapter(
                                mActivity!!, android.R.layout.simple_spinner_item, tehsil_list
                            )
                            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                            spinnerTehsil!!.setAdapter(dataAdapter)
                        } else {
                            tehsilList(districtId)
                        }
                    }
                }

                override fun onNothingSelected(adapterView: AdapterView<*>?) {
                }
            })
            spinnerTehsil!!.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    adapterView: AdapterView<*>?,
                    view: View,
                    i: Int,
                    l: Long
                ) {
                    if (++checkTehsil > 1) {
                        tehsilNameEng = tehsil_list[i]!!.tehsilNameEng
                        tehsilId = tehsil_list[i]!!.tehsilId

                        if (tehsilId == "-1") {
                        } else {
                            val editor = getSharedPreferences("MyPrefsMyPrefs", MODE_PRIVATE).edit()
                            editor.putString("UserId", tehsilId)
                            editor.apply()
                            //    Log.d("ghgfh", "fghgh"+tehsilNameEng +tehsilId);
                        }
                    }
                }

                override fun onNothingSelected(adapterView: AdapterView<*>?) {
                }
            })

            /*from date*/
            val dateFromDate =
                OnDateSetListener { view: DatePicker?, year: Int, monthOfYear: Int, dayOfMonth: Int ->
                    myCalendarFromDate[Calendar.YEAR] =
                        year
                    myCalendarFromDate[Calendar.MONTH] = monthOfYear
                    myCalendarFromDate[Calendar.DAY_OF_MONTH] = dayOfMonth
                    updateLabelFromDate()
                }
            inputStartDate!!.setOnClickListener(View.OnClickListener { view: View? ->
                val datePickerDialog = DatePickerDialog(
                    mActivity!!, R.style.DialogThemeTwo, dateFromDate,
                    myCalendarFromDate[Calendar.YEAR],
                    myCalendarFromDate[Calendar.MONTH],
                    myCalendarFromDate[Calendar.DAY_OF_MONTH]
                )
                //datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
                datePickerDialog.datePicker.maxDate = System.currentTimeMillis()
                val minDateInMillis = myCalendarToDate.timeInMillis
                datePickerDialog.datePicker.maxDate = minDateInMillis
                datePickerDialog.show()
            })

            /*from date*/

            /*to date*/
            val dateToDate =
                OnDateSetListener { view: DatePicker?, year: Int, monthOfYear: Int, dayOfMonth: Int ->
                    myCalendarToDate[Calendar.YEAR] =
                        year
                    myCalendarToDate[Calendar.MONTH] = monthOfYear
                    myCalendarToDate[Calendar.DAY_OF_MONTH] = dayOfMonth
                    updateLabelToDate()
                }
            inputEndDate!!.setOnClickListener(View.OnClickListener { view: View? ->
                val datePickerDialog = DatePickerDialog(
                    mActivity!!, R.style.DialogThemeTwo, dateToDate,
                    myCalendarToDate[Calendar.YEAR],
                    myCalendarToDate[Calendar.MONTH],
                    myCalendarToDate[Calendar.DAY_OF_MONTH]
                )
                datePickerDialog.datePicker.maxDate = System.currentTimeMillis()
                val minDateInMillis = myCalendarFromDate.timeInMillis
                datePickerDialog.datePicker.minDate = minDateInMillis
                //datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
                datePickerDialog.show()
            })

            /*to date*/
            buttonTrainingFilter.setOnClickListener { view: View? ->
                //  startDate = inputStartDate.getText().toString().trim();
                //  endDate = inputEndDate.getText().toString().trim();
                //  Log.d("startDate_endDate", startDate + endDate);
                trainingNumber = inputTrainingNumber.text.toString().trim { it <= ' ' }
                if (layoutDateRange!!.getVisibility() == View.VISIBLE && startDate.isEmpty()) {
                    Toast.makeText(mActivity, "Select Start date", Toast.LENGTH_SHORT).show()

                    // inputStartDate.setError("Error message");
                    // ((TextView)spinner.getSelectedView()).setError("Error message");
                } else if (layoutDateRange!!.getVisibility() == View.VISIBLE && endDate.isEmpty()) {
                    Toast.makeText(mActivity, "Select End date", Toast.LENGTH_SHORT).show()

                    //inputEndDate.setError("Error message");
                } else {
                    getTraining(districtId, tehsilId, trainingNumber, startDate, endDate, "0")
                    dialog.dismiss()
                }
            }

            dialog.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun updateLabelFromDate() {
        val myFormat = "dd-MM-yyyy"
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        inputStartDate!!.setText(sdf.format(myCalendarFromDate.time))
        //startDate = inputStartDate.getText().toString().trim();
        val calstartDate = inputStartDate!!.text.toString().trim { it <= ' ' }
        val originalFormat = "dd-MM-yyyy"
        val desiredFormat = "yyyy-MM-dd"
        startDate = convertDateFormat(calstartDate, originalFormat, desiredFormat)
        // Log.d("formateddate", startDate);
        // inputEndDate.getText().clear();
    }

    @SuppressLint("SetTextI18n")
    private fun updateLabelToDate() {
        val myFormat = "dd-MM-yyyy"
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        inputEndDate!!.setText(sdf.format(myCalendarToDate.time))
        //  endDate = inputEndDate.getText().toString().trim();
        val calendDate = inputEndDate!!.text.toString().trim { it <= ' ' }
        val originalFormat = "dd-MM-yyyy"
        val desiredFormat = "yyyy-MM-dd"
        endDate = convertDateFormat(calendDate, originalFormat, desiredFormat)


        //  Log.d("formateddate", startDate);
    }

    private fun tehsilList(districtId: String?) {
        if (isNetworkAvailable(mActivity!!)) {
            hideKeyboard(mActivity!!)
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
            val paramStr = TehsilListByDistictQueryParams()
            val splitArray =
                paramStr!!.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val paramData: MutableMap<String?, String?> = HashMap()
            paramData[splitArray[0]] = districtId
            val call = apiInterface.callTehsilListApiByDistict(TehsilListByDistict(), paramData)
            call!!.enqueue(object : Callback<ModelTehsil?> {
                override fun onResponse(
                    call: Call<ModelTehsil?>,
                    response: Response<ModelTehsil?>
                ) {
                    hideCustomProgressDialogCommonForAll()
                    if (response.isSuccessful) {
                        if (response.code() == 200) {
                            if (response.body() != null) {
                                if (Objects.requireNonNull<ModelTehsil?>(response.body()).status == "200") {
                                    tehsil_list = response.body()!!.tehsil_List
                                    if (tehsil_list != null && tehsil_list.size > 0) {
                                        Collections.reverse(tehsil_list)
                                        val l = ModelTehsilList()
                                        l.tehsilId = (-1).toString()
                                        l.tehsilNameEng =
                                            "--" + resources.getString(R.string.tehsil) + "--"
                                        tehsil_list.add(l)
                                        Collections.reverse(tehsil_list)
                                        val dataAdapter = ArrayAdapter(
                                            mActivity!!,
                                            android.R.layout.simple_spinner_item,
                                            tehsil_list
                                        )
                                        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                                        spinnerTehsil!!.adapter = dataAdapter
                                        if (tehsilvalue_2 != null && !tehsilvalue_2!!.isEmpty()) {
                                            //  Log.d("ghjh", tehsilvalue_2);
                                            val userId =
                                                tehsilvalue_2!!.toInt() // replace with the user ID you want to select
                                            for (i in 0 until dataAdapter.count) {
                                                val user = dataAdapter.getItem(i)
                                                if (user!!.tehsilId.toString() == userId.toString()) {
                                                    spinnerTehsil!!.setSelection(i)
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

                override fun onFailure(call: Call<ModelTehsil?>, error: Throwable) {
                    hideCustomProgressDialogCommonForAll()
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
            val call = apiInterface.callDistrictListApi(DistictList())
            call!!.enqueue(object : Callback<ModelDistrict?> {
                override fun onResponse(
                    call: Call<ModelDistrict?>,
                    response: Response<ModelDistrict?>
                ) {
                    hideCustomProgressDialogCommonForAll()

                    if (response.isSuccessful) {
                        if (response.code() == 200) {
                            if (response.body() != null) {
                                if (response.body()!!.status == "200") {
                                    district_List = response.body()!!.district_List
                                    if (district_List.size > 0) {
                                        district_List.reverse()
                                        val l = ModelDistrictList()
                                        l.districtId = (-1).toString()
                                        l.districtNameEng =
                                            "--" + resources.getString(R.string.district) + "--"
                                        district_List.add(l)
                                        district_List.reverse()
                                        val dataAdapter = ArrayAdapter(
                                            mActivity!!,
                                            android.R.layout.simple_spinner_item,
                                            district_List
                                        )
                                        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                                        spinnerDistrict!!.adapter = dataAdapter

                                        /*
                                    if(d2!=null)
                                        {
                                            spinnerDistrict.setSelection(Integer.parseInt(d2));

                                        }*/
                                        if (d2 != null && d2!!.isNotEmpty()) {
                                            //  Log.d("ghjh", d2);
                                            val userId =
                                                d2!!.toInt() // replace with the user ID you want to select
                                            for (i in 0 until dataAdapter.count) {
                                                val user = dataAdapter.getItem(i)
                                                if (user!!.districtId.toString() == userId.toString()) {
                                                    spinnerDistrict!!.setSelection(i)
                                                    break
                                                }
                                            }
                                        } else {
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

                override fun onFailure(call: Call<ModelDistrict?>, error: Throwable) {
                    hideCustomProgressDialogCommonForAll()

                    makeToast(resources.getString(R.string.error))

                    call.cancel()
                }
            })
        } else {
            makeToast(resources.getString(R.string.no_internet_connection))
        }
    }

    private var onItemCampListClickListener: OnItemCampListClickListener =
        object : OnItemCampListClickListener {
            override fun onItemClick(
                campDetailsInfo: CampDetailsInfo?,
                position: Int,
                action: CampListingAdapter.CampAction
            ) {
                when (action) {
                    CampListingAdapter.CampAction.ORGANISE_CAMP -> {
                        val builder = AlertDialog.Builder(
                            mActivity!!
                        )
                        builder.setMessage(resources.getString(R.string.organise_camp_now))
                            .setCancelable(false)
                            .setPositiveButton(resources.getString(R.string.yes)) { dialog: DialogInterface, id: Int ->
                                dialog.cancel()
                                val USER_Id = preference!!.useR_Id
                                organiseACamp(
                                    USER_Id,
                                    campDetailsInfo!!.trainingId,
                                    campDetailsInfo.trainingNo,
                                    Constants.CAMP_STATUS_ID_ORGANISED
                                )
                            }
                            .setNegativeButton(
                                resources.getString(R.string.cancel)
                            ) { dialog: DialogInterface, i: Int ->
                                dialog.cancel()
                            }
                        val alert = builder.create()
                        alert.setTitle(resources.getString(R.string.alert))
                        alert.show()
                    }

                    CampListingAdapter.CampAction.UPLOAD_REPORT -> {
                        val builder = AlertDialog.Builder(
                            mActivity!!
                        )
                        builder.setMessage(resources.getString(R.string.upload_daily_work_sheet_for_camp_now))
                            .setCancelable(false)
                            .setPositiveButton(resources.getString(R.string.yes)) { dialog: DialogInterface, id: Int ->
                                dialog.cancel()
                                val intent = Intent(mActivity, UploadCampDailyReportActivity::class.java)
                                val bundle = Bundle()
                                bundle.putString("TrainingNo", campDetailsInfo!!.trainingNo)
                                bundle.putString("TrainingId", campDetailsInfo.trainingId)
                                bundle.putString("FlagTypeId", "1")
                                //    Log.d("one", campDetailsInfo.getTrainingNo() +" "  +campDetailsInfo.getTrainingId());
                                intent.putExtras(bundle)
                                startActivity(intent)
                            }
                            .setNegativeButton(
                                resources.getString(R.string.cancel)
                            ) { dialog: DialogInterface, i: Int ->
                                dialog.cancel()
                            }
                        val alert = builder.create()
                        alert.setTitle(resources.getString(R.string.alert))
                        alert.show()
                    }

                    CampListingAdapter.CampAction.UPLOAD_CAMP_PHOTO -> {
                        val builder = AlertDialog.Builder(
                            mActivity!!
                        )
                        builder.setMessage(resources.getString(R.string.upload_camp_photo_for_camp_now))
                            .setCancelable(false)
                            .setPositiveButton(resources.getString(R.string.yes)) { dialog: DialogInterface, id: Int ->
                                dialog.cancel()
                                val intent = Intent(mActivity, UploadCampDailyReportActivity::class.java)
                                val bundle = Bundle()
                                bundle.putString("TrainingNo", campDetailsInfo!!.trainingNo)
                                bundle.putString("TrainingId", campDetailsInfo.trainingId)
                                bundle.putString("FlagTypeId", "3")
                                //     Log.d("two", campDetailsInfo.getTrainingNo() +" "  +campDetailsInfo.getTrainingId());
                                intent.putExtras(bundle)
                                startActivity(intent)
                            }
                            .setNegativeButton(
                                resources.getString(R.string.cancel)
                            ) { dialog: DialogInterface, i: Int ->
                                dialog.cancel()
                            }
                        val alert = builder.create()
                        alert.setTitle(resources.getString(R.string.alert))
                        alert.show()
                    }
                    CampListingAdapter.CampAction.DOWNLOAD -> {
                        val builder = AlertDialog.Builder(
                            mActivity!!
                        )
                        builder.setMessage(resources.getString(R.string.download_daily_work_sheet_for_camp_now))
                            .setCancelable(false)
                            .setPositiveButton(resources.getString(R.string.yes)) { dialog: DialogInterface, id: Int ->
                                dialog.cancel()
                                val USER_Id = preference!!.useR_Id
                                downloadDealersAndCreatePDF(
                                    USER_Id,
                                    campDetailsInfo!!.districtID,
                                    campDetailsInfo.tehsilID,
                                    campDetailsInfo.blockId,
                                    campDetailsInfo.trainingNo
                                )
                            }
                            .setNegativeButton(
                                resources.getString(R.string.cancel)
                            ) { dialog: DialogInterface, i: Int ->
                                dialog.cancel()
                            }
                        val alert = builder.create()
                        alert.setTitle(resources.getString(R.string.alert))
                        alert.show()
                    }

                    CampListingAdapter.CampAction.VIEW_DETAILS -> {
                        val USER_Id = preference!!.useR_Id
                        viewUploadedImages(USER_Id, campDetailsInfo!!)
                    }

                    CampListingAdapter.CampAction.COMPLETE_CAMP -> {
                        val builder = AlertDialog.Builder(
                            mActivity!!
                        )
                        builder.setMessage(resources.getString(R.string.complete_camp_now))
                            .setCancelable(false)
                            .setPositiveButton(resources.getString(R.string.yes)) { dialog: DialogInterface, id: Int ->
                                dialog.cancel()
                                val USER_Id = preference!!.useR_Id
                                completeACamp(
                                    USER_Id,
                                    campDetailsInfo!!.trainingId,
                                    campDetailsInfo.trainingNo,
                                    Constants.CAMP_STATUS_ID_COMPLETED
                                )
                            }
                            .setNegativeButton(
                                resources.getString(R.string.cancel)
                            ) { dialog: DialogInterface, i: Int ->
                                dialog.cancel()
                            }
                        val alert = builder.create()
                        alert.setTitle(resources.getString(R.string.alert))
                        alert.show()
                    }
                    CampListingAdapter.CampAction.DELETE_CAMP -> {
                        val builder = AlertDialog.Builder(
                            mActivity!!
                        )
                        builder.setMessage(resources.getString(R.string.delete_camp_now))
                            .setCancelable(false)
                            .setPositiveButton(resources.getString(R.string.yes)) { dialog: DialogInterface, id: Int ->
                                dialog.cancel()
                                val USER_Id = preference!!.useR_Id
                                deleteACamp(
                                    USER_Id,
                                    campDetailsInfo!!.trainingId,
                                    campDetailsInfo.trainingNo
                                )
                            }
                            .setNegativeButton(
                                resources.getString(R.string.cancel)
                            ) { dialog: DialogInterface, i: Int ->
                                dialog.cancel()
                            }
                        val alert = builder.create()
                        alert.setTitle(resources.getString(R.string.alert))
                        alert.show()
                    }
                }
            }
        }

    private fun downloadDealersAndCreatePDF(
        UserID: String,
        DistrictID: String?,
        TehsilID: String?,
        BlockId: String?,
        TrainingNo: String?
    ) {
        if (isNetworkAvailable(mActivity!!)) {
            hideKeyboard(mActivity!!)
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

            val paramStr = GetDealersByBlockQueryParams()
            val splitArray =
                paramStr!!.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val paramData: MutableMap<String?, String?> = HashMap()
            paramData[splitArray[0]] = UserID
            paramData[splitArray[1]] = DistrictID
            paramData[splitArray[2]] = TehsilID
            paramData[splitArray[3]] = BlockId

            val call = apiInterface.callGetDealersByBlockApi(GetDealersByBlock(), paramData)
            call!!.enqueue(object : Callback<ModelAllDealersByBlock?> {
                override fun onResponse(
                    call: Call<ModelAllDealersByBlock?>,
                    response: Response<ModelAllDealersByBlock?>
                ) {
                    hideCustomProgressDialogCommonForAll()

                    if (response.isSuccessful) {
                        if (response.code() == 200) {
                            if (response.body() != null) {
                                if (response.body()!!.status == "200") {
                                    if (response.body()!!.list != null && response.body()!!.list.size > 0) {
                                        val intent = Intent(
                                            mActivity,
                                            CampDailyWorkReportPdfActivity::class.java
                                        )
                                        val bundle = Bundle()
                                        bundle.putSerializable(
                                            "mylist",
                                            response.body()!!.list as Serializable
                                        )
                                        bundle.putString("TrainingNo", TrainingNo)
                                        intent.putExtras(bundle)
                                        startActivity(intent)
                                    } else {
                                        val list = ArrayList<DealersInfo>()

                                        val intent = Intent(
                                            mActivity,
                                            CampDailyWorkReportPdfActivity::class.java
                                        )
                                        val bundle = Bundle()
                                        bundle.putSerializable("mylist", list as Serializable)
                                        bundle.putString("TrainingNo", TrainingNo)
                                        intent.putExtras(bundle)
                                        startActivity(intent)
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

                override fun onFailure(call: Call<ModelAllDealersByBlock?>, error: Throwable) {
                    hideCustomProgressDialogCommonForAll()

                    makeToast(resources.getString(R.string.error))

                    call.cancel()
                }
            })
        } else {
            makeToast(resources.getString(R.string.no_internet_connection))
        }
    }

    private fun viewUploadedImages(UserID: String, campDetailsInfo: CampDetailsInfo) {
        if (isNetworkAvailable(mActivity!!)) {
            hideKeyboard(mActivity!!)
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

            val paramStr = GetCampDocListQueryParams()
            val splitArray =
                paramStr!!.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val paramData: MutableMap<String?, String?> = HashMap()
            paramData[splitArray[0]] = UserID
            paramData[splitArray[1]] = campDetailsInfo.trainingId

            val call = apiInterface.callGetCampDocListApi(GetCampDocList(), paramData)
            call!!.enqueue(object : Callback<ModelGetCampDocList?> {
                override fun onResponse(
                    call: Call<ModelGetCampDocList?>,
                    response: Response<ModelGetCampDocList?>
                ) {
                    hideCustomProgressDialogCommonForAll()

                    if (response.isSuccessful) {
                        if (response.code() == 200) {
                            if (response.body() != null) {
                                if (response.body()!!.status == "200") {
                                    val campDocInfoArrayList = ArrayList<CampDocInfo>()

                                    if (response.body()!!.list != null && response.body()!!.list.size > 0) {
                                        for (i in response.body()!!.list.indices) {
                                            val campDocInfo = response.body()!!.list[i]

                                            val imagePath = BaseUrl() + campDocInfo.documentPath
                                            campDocInfo.documentPath = imagePath

                                            campDocInfoArrayList.add(campDocInfo)
                                        }

                                        val intent = Intent(
                                            mActivity,
                                            ViewCampDetailsActivity::class.java
                                        )
                                        val bundle = Bundle()
                                        bundle.putSerializable(
                                            "mylist",
                                            campDocInfoArrayList as Serializable
                                        )
                                        bundle.putSerializable(
                                            "campDetails",
                                            campDetailsInfo as Serializable
                                        )
                                        intent.putExtras(bundle)
                                        startActivity(intent)
                                    } else {
                                        val intent = Intent(
                                            mActivity,
                                            ViewCampDetailsActivity::class.java
                                        )
                                        val bundle = Bundle()
                                        bundle.putSerializable(
                                            "mylist",
                                            campDocInfoArrayList as Serializable
                                        )
                                        bundle.putSerializable(
                                            "campDetails",
                                            campDetailsInfo as Serializable
                                        )
                                        intent.putExtras(bundle)
                                        startActivity(intent)
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

                override fun onFailure(call: Call<ModelGetCampDocList?>, error: Throwable) {
                    hideCustomProgressDialogCommonForAll()

                    makeToast(resources.getString(R.string.error))

                    call.cancel()
                }
            })
        } else {
            makeToast(resources.getString(R.string.no_internet_connection))
        }
    }

    private fun organiseACamp(
        UserID: String,
        TrainingId: String?,
        TrainingNo: String?,
        TStatusId: String
    ) {
        if (isNetworkAvailable(mActivity!!)) {
            hideKeyboard(mActivity!!)
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

            val paramStr = OrganiseACampQueryParams()
            val splitArray =
                paramStr!!.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val paramData: MutableMap<String?, String?> = HashMap()
            paramData[splitArray[0]] = UserID
            paramData[splitArray[1]] = TrainingId
            paramData[splitArray[2]] = TrainingNo
            paramData[splitArray[3]] = TStatusId
            val call = apiInterface.callOrganiseACampApi(OrganiseACamp(), paramData)
            call!!.enqueue(object : Callback<ModelOrganiseACamp?> {
                override fun onResponse(
                    call: Call<ModelOrganiseACamp?>,
                    response: Response<ModelOrganiseACamp?>
                ) {
                    hideCustomProgressDialogCommonForAll()

                    if (response.isSuccessful) {
                        if (response.code() == 200) {
                            if (response.body() != null) {
                                if (response.body()!!.status == "200") {
                                    makeToast(response.body()!!.message)

                                    campDetails_infoArrayList = ArrayList()
                                    adapter!!.setData(campDetails_infoArrayList)

                                    var USER_DISTRICT_Id = preference!!.useR_DistrictId

                                    val USER_TYPE = preference!!.useR_TYPE
                                    val USER_TYPE_ID = preference!!.useR_TYPE_ID
                                    if (USER_TYPE_ID == "1" && USER_TYPE.equals(
                                            "Admin",
                                            ignoreCase = true
                                        )
                                    ) {
                                        USER_DISTRICT_Id = "0"
                                    } else if (USER_TYPE_ID == "2" && USER_TYPE.equals(
                                            "Manager",
                                            ignoreCase = true
                                        )
                                    ) {
                                        USER_DISTRICT_Id = "0"
                                    }

                                    getTraining(USER_DISTRICT_Id, "0", "", "", "", "0")
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

                override fun onFailure(call: Call<ModelOrganiseACamp?>, error: Throwable) {
                    hideCustomProgressDialogCommonForAll()
                    makeToast(resources.getString(R.string.error))
                    call.cancel()
                }
            })
        } else {
            makeToast(resources.getString(R.string.no_internet_connection))
        }
    }

    private fun completeACamp(
        UserID: String,
        TrainingId: String?,
        TrainingNo: String?,
        TStatusId: String
    ) {
        if (isNetworkAvailable(mActivity!!)) {
            hideKeyboard(mActivity!!)
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

            val paramStr = OrganiseACampQueryParams()
            val splitArray =
                paramStr!!.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val paramData: MutableMap<String?, String?> = HashMap()
            paramData[splitArray[0]] = UserID
            paramData[splitArray[1]] = TrainingId
            paramData[splitArray[2]] = TrainingNo
            paramData[splitArray[3]] = TStatusId

            val call = apiInterface.callOrganiseACampApi(OrganiseACamp(), paramData)
            call!!.enqueue(object : Callback<ModelOrganiseACamp?> {
                override fun onResponse(
                    call: Call<ModelOrganiseACamp?>,
                    response: Response<ModelOrganiseACamp?>
                ) {
                    hideCustomProgressDialogCommonForAll()

                    if (response.isSuccessful) {
                        if (response.code() == 200) {
                            if (response.body() != null) {
                                if (response.body()!!.status == "200") {
                                    makeToast(response.body()!!.message)

                                    campDetails_infoArrayList = ArrayList()
                                    adapter!!.setData(campDetails_infoArrayList)

                                    var USER_DISTRICT_Id = preference!!.useR_DistrictId

                                    val USER_TYPE = preference!!.useR_TYPE
                                    val USER_TYPE_ID = preference!!.useR_TYPE_ID
                                    if (USER_TYPE_ID == "1" && USER_TYPE.equals(
                                            "Admin",
                                            ignoreCase = true
                                        )
                                    ) {
                                        USER_DISTRICT_Id = "0"
                                    } else if (USER_TYPE_ID == "2" && USER_TYPE.equals(
                                            "Manager",
                                            ignoreCase = true
                                        )
                                    ) {
                                        USER_DISTRICT_Id = "0"
                                    }

                                    getTraining(USER_DISTRICT_Id, "0", "", "", "", "0")
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

                override fun onFailure(call: Call<ModelOrganiseACamp?>, error: Throwable) {
                    hideCustomProgressDialogCommonForAll()

                    makeToast(resources.getString(R.string.error))

                    call.cancel()
                }
            })
        } else {
            makeToast(resources.getString(R.string.no_internet_connection))
        }
    }

    private fun deleteACamp(UserID: String, TrainingId: String?, TrainingNo: String?) {
        if (isNetworkAvailable(mActivity!!)) {
            hideKeyboard(mActivity!!)
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

            val paramStr = DeleteACampQueryParams()
            val splitArray =
                paramStr!!.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val paramData: MutableMap<String?, String?> = HashMap()
            paramData[splitArray[0]] = UserID
            paramData[splitArray[1]] = TrainingId
            paramData[splitArray[2]] = TrainingNo

            val call = apiInterface.callDeleteACampApi(DeleteACamp(), paramData)
            call!!.enqueue(object : Callback<ModelDeleteACamp?> {
                override fun onResponse(
                    call: Call<ModelDeleteACamp?>,
                    response: Response<ModelDeleteACamp?>
                ) {
                    hideCustomProgressDialogCommonForAll()

                    if (response.isSuccessful) {
                        if (response.code() == 200) {
                            if (response.body() != null) {
                                if (response.body()!!.status == "200") {
                                    makeToast(response.body()!!.message)

                                    campDetails_infoArrayList = ArrayList()
                                    adapter!!.setData(campDetails_infoArrayList)


                                    var USER_DISTRICT_Id = preference!!.useR_DistrictId

                                    val USER_TYPE = preference!!.useR_TYPE
                                    val USER_TYPE_ID = preference!!.useR_TYPE_ID
                                    if (USER_TYPE_ID == "1" && USER_TYPE.equals(
                                            "Admin",
                                            ignoreCase = true
                                        )
                                    ) {
                                        USER_DISTRICT_Id = "0"
                                    } else if (USER_TYPE_ID == "2" && USER_TYPE.equals(
                                            "Manager",
                                            ignoreCase = true
                                        )
                                    ) {
                                        USER_DISTRICT_Id = "0"
                                    }

                                    getTraining(USER_DISTRICT_Id, "0", "", "", "", "0")
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

                override fun onFailure(call: Call<ModelDeleteACamp?>, error: Throwable) {
                    hideCustomProgressDialogCommonForAll()

                    makeToast(resources.getString(R.string.error))

                    call.cancel()
                }
            })
        } else {
            makeToast(resources.getString(R.string.no_internet_connection))
        }
    }

    private fun getTraining(
        DistrictID: String?,
        TehsilID: String?,
        TrainingNo: String,
        StartedDate: String,
        EndDate: String,
        TStatusId: String
    ) {
        if (isNetworkAvailable(mActivity!!)) {
            hideKeyboard(mActivity!!)
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

            val USER_Id = preference!!.useR_Id
            val paramStr = GetCampListQueryParams()
            val splitArray =
                paramStr!!.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val paramData: MutableMap<String?, String?> = HashMap()
            paramData[splitArray[0]] = USER_Id
            paramData[splitArray[1]] = DistrictID
            paramData[splitArray[2]] = TehsilID
            paramData[splitArray[3]] = TrainingNo
            paramData[splitArray[4]] = StartedDate
            paramData[splitArray[5]] = EndDate
            paramData[splitArray[6]] = TStatusId
            val call = apiInterface.callCampListApi(GetCampList(), paramData)
            call!!.enqueue(object : Callback<ModelCampSchedule?> {
                override fun onResponse(
                    call: Call<ModelCampSchedule?>,
                    response: Response<ModelCampSchedule?>
                ) {
                    hideCustomProgressDialogCommonForAll()

                    if (response.isSuccessful) {
                        if (response.code() == 200) {
                            if (response.body() != null) {
                                if (response.body()!!.status == "200") {
                                    if (response.body()!!.list.size > 0) {
                                        binding!!.textNoCampSchedule.visibility = View.GONE
                                        binding!!.rvAllCamps.visibility = View.VISIBLE
                                        adapter!!.setData(response.body()!!.list)
                                    } else {
                                        binding!!.textNoCampSchedule.visibility = View.VISIBLE
                                        binding!!.rvAllCamps.visibility = View.GONE
                                    }
                                    val sharedPreferences =
                                        getSharedPreferences("shared preferences", MODE_PRIVATE)
                                    // creating a variable for editor to
                                    // store data in shared preferences.
                                    val editor = sharedPreferences.edit()
                                    // creating a new variable for gson.
                                    val gson = Gson()
                                    // getting data from gson and storing it in a string.
                                    val json = gson.toJson(response.body()!!.list)
                                    // below line is to save data in shared
                                    // prefs in the form of string.
                                    editor.putString("coursescamp", json)
                                    // below line is to apply changes
                                    // and save data in shared prefs.
                                    editor.apply()

                                    // after saving data we are displaying a toast message.
                                    //   Toast.makeText(CampsListingActivity.this, "Saved Camp Array List to Shared preferences. ", Toast.LENGTH_SHORT).show();
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

                override fun onFailure(call: Call<ModelCampSchedule?>, error: Throwable) {
                    hideCustomProgressDialogCommonForAll()

                    makeToast(resources.getString(R.string.error))

                    call.cancel()
                }
            })
        } else {
            makeToast(resources.getString(R.string.no_internet_connection))
        }
    }

    fun makeToast(string: String?) {
        if (TextUtils.isEmpty(string)) return
        Toast.makeText(mActivity, string, Toast.LENGTH_SHORT).show()
    }

    private fun setClickListener() {
        binding!!.actionBar.ivBack.setOnClickListener { /* SharedPreferences.Editor editor = getSharedPreferences("MyPrefsMyPrefs", MODE_PRIVATE).edit();
                                                                editor.putString("districtId", districtId);
                                                                editor.clear();
                                                                editor.commit();
    */
            /*   SharedPreferences preferences = getSharedPreferences("MyPrefsMyPrefs", Activity.MODE_PRIVATE);
                                                                          android.content.SharedPreferences.Editor editor = preferences.edit();
                                                                        editor.remove("districtId");
                                                                          editor.apply();*/

            val intent =
                Intent(mActivity, MainActivity::class.java)
            startActivity(intent)
        }
        binding!!.actionBar.buttonFilter.setOnClickListener(object : OnSingleClickListener() {
            override fun onSingleClick(v: View?) {
                hideCustomProgressDialogCommonForAll()

                val sh = getSharedPreferences("MyPrefsMyPrefs", MODE_PRIVATE)
                d2 = sh.getString("districtId", "")
                Log.d("kfhhf", d2!!)
                val sh2 = getSharedPreferences("MyPrefsMyPrefs", MODE_PRIVATE)
                tehsilvalue_2 = sh2.getString("UserId", "")
                Log.d("d2", d2!!)
                Log.d("selecteduserid", tehsilId!!)
                filterDialog()
            }
        })




        binding!!.actionBar.ivThreeDot.setOnClickListener { view: View? ->
            val popupMenu =
                PopupMenu(
                    this@CampsListingActivity,
                    binding!!.actionBar.ivThreeDot
                )
            popupMenu.menuInflater.inflate(R.menu.menu, popupMenu.menu)
            popupMenu.setOnMenuItemClickListener { menuItem: MenuItem? ->
                val builder = AlertDialog.Builder(
                    mActivity!!
                )
                builder.setMessage(resources.getString(R.string.do_you_want_to_logout_from_this_app))
                    .setCancelable(false)
                    .setPositiveButton(
                        resources.getString(R.string.logout)
                    ) { dialog: DialogInterface, id: Int ->
                        dialog.cancel()
                    }
                    .setNegativeButton(
                        resources.getString(R.string.cancel)
                    ) { dialog: DialogInterface, id: Int -> dialog.cancel() }
                val alert = builder.create()
                alert.setTitle(resources.getString(R.string.alert))
                alert.show()
                true
            }
            popupMenu.show()
        }

        binding!!.buttonCreateNewCamp.setOnClickListener { view: View? ->
            val intent = Intent(mActivity, CreateNewCampActivity::class.java)
            startActivityForResult(intent, LAUNCH_CAMP_CREATION_ACTIVITY)
        }
    }


    override fun onBackPressed() {
        super.onBackPressed()
        clearSharePreference()
        val intent = Intent(mActivity, MainActivity::class.java)
        startActivity(intent)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == LAUNCH_CAMP_CREATION_ACTIVITY) {
            if (resultCode == RESULT_OK) {
                campDetails_infoArrayList = ArrayList()
                adapter!!.setData(campDetails_infoArrayList)
                var USER_DISTRICT_Id = preference!!.useR_DistrictId
                val USER_TYPE = preference!!.useR_TYPE
                val USER_TYPE_ID = preference!!.useR_TYPE_ID
                if (USER_TYPE_ID == "1" && USER_TYPE.equals("Admin", ignoreCase = true)) {
                    USER_DISTRICT_Id = "0"
                } else if (USER_TYPE_ID == "2" && USER_TYPE.equals("Manager", ignoreCase = true)) {
                    USER_DISTRICT_Id = "0"
                }
                getTraining(USER_DISTRICT_Id, "0", "", "", "", "0")
            }
        } else if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            val fileUri = data.data
            if (fileUri != null) {
                val wpsIntent = Intent(Intent.ACTION_VIEW)
                wpsIntent.setDataAndType(
                    fileUri,
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
                )
                wpsIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

                try {
                    startActivity(wpsIntent)
                } catch (e: ActivityNotFoundException) {
                    // Handle the case where WPS Office or a compatible app is not installed
                    val playStoreIntent = Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("market://details?id=cn.wps.moffice_eng")
                    )
                    startActivity(playStoreIntent)
                }
            }
        }
    }

    @Throws(ParseException::class)
    private fun Excelform() {
        /*  String corFormat = "dd-MM-yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(corFormat, Locale.getDefault());
        String corStartDate = sdf.format(startDate);
        String corEndDate = sdf.format(endDate);
*/
        val originalFormat = "yyyy-MM-dd"
        val desiredFormat = "dd/MM/yyyy"
        val corStartDate = convertDateFormat(startDate, originalFormat, desiredFormat)
        val corEndDate = convertDateFormat(endDate, originalFormat, desiredFormat)
        Log.d("formateddate", corStartDate)
        val intent = Intent(mActivity, ExcelforCamp::class.java)
        intent.putExtra("startDateKey", corStartDate)
        intent.putExtra("endDateKey", corEndDate)
        startActivity(intent)
    }

    private fun ExcelformTable() {
        mActivity = this
        preference = PrefManager(mActivity!!)


        sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE)

        // creating a variable for gson.
        val gson = Gson()

        // below line is to get to string present from our
        // shared prefs if not present setting it as null.
        val json = sharedPreferences!!.getString("coursescamp", null)

        // below line is to get the type of our array list.
        val type = object : TypeToken<ArrayList<CampDetailsInfo?>?>() {}.type

        // in below line we are getting data from gson
        // and saving it to our array list
        campDetails_infoArrayList = gson.fromJson(json, type)

        // checking below if the array list is empty or not
        if (campDetails_infoArrayList == null) {
            // if the array list is empty
            // creating a new array list.
            campDetails_infoArrayList = ArrayList()
            Log.d("nbb", "" + campDetails_infoArrayList)
        }
        Log.d("gfhvbb", "" + campDetails_infoArrayList)
        val originalFormat = "yyyy-MM-dd"
        val desiredFormat = "dd-MM-yyyy"
        val corStartDate = convertDateFormat(startDate, originalFormat, desiredFormat)
        val corEndDate = convertDateFormat(endDate, originalFormat, desiredFormat)
        Log.d("formateddate", corStartDate)
        val workbook = HSSFWorkbook()
        val firstSheet = if (corStartDate.isEmpty() && corStartDate.isEmpty()) {
            workbook.createSheet("AllRecord")
        } else {
            workbook.createSheet("Record $corStartDate - $corEndDate")
        }
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
        val richText: RichTextString = HSSFRichTextString("DistrictName")
        richText.applyFont(boldFont)
        // Set the RichTextString to the cell
        cellA.setCellValue(richText)
        firstSheet.setColumnWidth(0, 4000)
        val cellB = rowA.createCell(1)
        val richText1: RichTextString = HSSFRichTextString("BlockName")
        richText1.applyFont(boldFont)
        // Set the RichTextString to the cell
        cellB.setCellValue(richText1)
        firstSheet.setColumnWidth(1, 4000)
        //   cellB.setCellStyle(cellStyle);
        val cellC = rowA.createCell(2)
        firstSheet.setColumnWidth(2, 4000)
        val richText2: RichTextString = HSSFRichTextString("StartDate")
        richText2.applyFont(boldFont)
        cellC.setCellValue(richText2)

        //  cellC.setCellValue(new HSSFRichTextString("Punch\nDate"));
        val cellD = rowA.createCell(3)
        firstSheet.setColumnWidth(3, 4000)
        val richText3: RichTextString = HSSFRichTextString("EndDate")
        richText3.applyFont(boldFont)
        cellD.setCellValue(richText3)

        // cellD.setCellValue(new HSSFRichTextString("Punch\nDay"));
        val cellE = rowA.createCell(4)
        firstSheet.setColumnWidth(4, 4000)
        val richText4: RichTextString = HSSFRichTextString("Status")
        richText4.applyFont(boldFont)
        cellE.setCellValue(richText4)


        //  cellE.setCellValue(new HSSFRichTextString("PunchIn\nTime"));
        val cellF = rowA.createCell(5)
        firstSheet.setColumnWidth(5, 20000)
        val richText5: RichTextString = HSSFRichTextString("Address")
        richText5.applyFont(boldFont)
        cellF.setCellValue(richText5)


        // cellF.setCellValue(new HSSFRichTextString("Punch\nOut\nTime"));
        //    cellF.setCellStyle(cellStyle);


        //  cellG.setCellValue(new HSSFRichTextString("AddressIn"));
        //    cellG.setCellStyle(cellStyle);
        Log.d("mylist", " -------------- $campDetails_infoArrayList")

        if (campDetails_infoArrayList != null && campDetails_infoArrayList!!.size > 0) {
            for (i in campDetails_infoArrayList!!.indices) {
                val detailsInfo = campDetails_infoArrayList!![i]

                val districtName = detailsInfo.districtNameEng.toString()
                val attendanceValue = detailsInfo.blockName.toString()
                val date = detailsInfo.startDate.toString()
                val day = detailsInfo.endDate.toString()
                val intime = detailsInfo.status.toString()
                val outtime = detailsInfo.address.toString()

                //  String inadd = String.valueOf(detailsInfo.getAddress_In());
                //  String outadd = String.valueOf(detailsInfo.getAddress_Out());
                val dataRow: Row = firstSheet.createRow(i + 1) // Start from row 1 for data

                // Column 1: District Name
                dataRow.createCell(0).setCellValue(districtName)

                // Column 2: Attendance Value
                dataRow.createCell(1).setCellValue(attendanceValue)
                dataRow.createCell(2).setCellValue(date)
                dataRow.createCell(3).setCellValue(day)
                dataRow.createCell(4).setCellValue(intime)
                dataRow.createCell(5).setCellValue(outtime)


                //  dataRow.createCell(6).setCellValue(inadd);
                //  dataRow.createCell(7).setCellValue(outadd);
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
            Toast.makeText(this@CampsListingActivity, "Excel Sheet Download ", Toast.LENGTH_SHORT)
                .show()
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
                return "" // Return an empty str
                // ing if there's an error in parsing or formatting
            }
        }
    }
}