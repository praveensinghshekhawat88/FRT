package com.callmangement.ehr.ehrActivities

import android.Manifest.permission
import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.Activity
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.content.ActivityNotFoundException
import android.content.BroadcastReceiver
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Vibrator
import android.text.TextUtils
import android.util.Log
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
import androidx.core.content.FileProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.callmangement.BuildConfig
import com.callmangement.ehr.adapter.AttendanceListingAdapter
import com.callmangement.ehr.api.APIClient
import com.callmangement.ehr.api.APIInterface
import com.callmangement.ehr.models.AttStatusDatum
import com.callmangement.ehr.models.AttStatusRoot
import com.callmangement.ehr.models.AttendanceDetailsInfo
import com.callmangement.ehr.models.DashbordRoot
import com.callmangement.ehr.models.ModelAttendanceListing
import com.callmangement.ehr.models.ModelDistrict
import com.callmangement.ehr.models.ModelDistrictList
import com.callmangement.ehr.models.ModelSEUser
import com.callmangement.ehr.models.ModelSEUserList
import com.callmangement.ehr.support.EqualSpacingItemDecoration
import com.callmangement.ehr.support.OnSingleClickListener
import com.callmangement.ehr.support.Utils.hideCustomProgressDialogCommonForAll
import com.callmangement.ehr.support.Utils.hideKeyboard
import com.callmangement.ehr.support.Utils.isNetworkAvailable
import com.callmangement.ehr.support.Utils.showCustomProgressDialogCommonForAll
import com.callmangement.R
import com.callmangement.databinding.ActivityAttendanceListingBinding
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
import java.text.DecimalFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Collections
import java.util.Date
import java.util.Locale
import java.util.Objects
import java.util.concurrent.TimeUnit

class AttendanceListingActivity : BaseActivity() {
    var targetPdf: String = "Camp Daily Work Report.pdf"
    private var mActivity: Activity? = null
    private var mContext: Context? = null
    private var binding: ActivityAttendanceListingBinding? = null
    var preference: PrefManager? = null
    private var dis: String? = null
    private var attendanceDetailsInfoList: MutableList<AttendanceDetailsInfo>? = ArrayList()
    private var adapter: AttendanceListingAdapter? = null
    val LAUNCH_MARK_ATTENDANCE_ACTIVITY: Int = 333
    private var permissionsToRequest: ArrayList<String>? = null
    private val permissionsRejected = ArrayList<String>()
    private val permissions = ArrayList<String>()
    private var spinnerDistrict: Spinner? = null
    private val spinner: Spinner? = null
    private val se_date_spinner: Spinner? = null
    private var spinnerSEUsers: Spinner? = null
    private val inputStartDate: EditText? = null
    private val inputEndDate: EditText? = null
    private var startDate: String? = ""
    private var endDate: String? = ""
    private var SDate = ""
    private var EDate = ""
    private var districtId: String? = "0"
    private var checkDistrict = 0
    private var districtNameEng: String? = ""
    private var checkSEUser = 0
    private var SEUserNameEng: String? = ""
    private var SEUserId: String? = "0"
    private val s1: String? = null
    private var d1: String? = null
    private var d2: String? = null
    private var selecteduserid: String? = null
    private var district_List: MutableList<ModelDistrictList?> = ArrayList()
    private var SEUser_list: MutableList<ModelSEUserList?> = ArrayList()
    private val myCalendarToDate: Calendar = Calendar.getInstance()
    private val myCalendarFromDate: Calendar = Calendar.getInstance()
    private var checkFilter = 0
    var layoutDateRange: LinearLayoutCompat? = null
    private val myFormat = "yyyy-MM-dd"
    var USER_Id: String? = null
    var totelPrestAtt: Int = 0
    var outQty: String? = null
    var otQty: String? = null
    private var vibrator: Vibrator? = null
    var daysDifference: Long = 0
    var todayDate: String? = null
    var sharedPreferences: SharedPreferences? = null
    var originalFileName: String = "Demo.xlsx" // Original file name
    var uniqueFileName: String = generateUniqueFileName(originalFileName)

    //  File filePathh = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), uniqueFileName);
    private val filePathh = File("/storage/emulated/0/Download/$uniqueFileName")

    //  private File filePathh = new File("/storage/emulated/0/Download" + "/Demo.xlsx");
    //  ArrayList<AttStatusDatum>  attStatusData;
    private var attStatusData: MutableList<AttStatusDatum> = ArrayList()

    private var Month = ""
    private var Year = ""
    var curMonth: String = ""
    var swipeRefreshLayout: SwipeRefreshLayout? = null

    var date_selected_year: Int = 0
    var date_selected_month: Int = 0
    var date_selected_day: Int = 0

    var currentPage: Int = 1
    var pageSize: Int = 200
    var pagination: String = 1.toString()

    private var isLoading = false
    private var isLastPage = false
    var totalPages: Int = 0 // Adjust accordingly based on response
    private var isExcelRequest = false
    private var isPdfRequest = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        binding = ActivityAttendanceListingBinding.inflate(
            layoutInflater
        )
        setContentView(binding!!.root)

        mActivity = this
        mContext = this

        LocalBroadcastManager.getInstance(mContext!!).registerReceiver(
            mMessageReceiver,
            IntentFilter("custom-message")
        )
        init()
    }

    private fun init() {
        clearSharePreference()
        preference = PrefManager(mActivity!!)
        permissions.add(permission.ACCESS_FINE_LOCATION)
        permissions.add(permission.ACCESS_COARSE_LOCATION)
        vibrator = getSystemService(VIBRATOR_SERVICE) as Vibrator
        binding!!.actionBar.ivBack.visibility = View.VISIBLE
        binding!!.actionBar.ivThreeDot.visibility = View.GONE
        binding!!.actionBar.layoutLanguage.visibility = View.GONE
        binding!!.actionBar.buttonPDF.visibility = View.GONE
        binding!!.actionBar.textToolbarTitle.text = resources.getString(R.string.manage_attendance)
        date_selected_year = intent.getIntExtra("YEAR", 0)
        date_selected_month = intent.getIntExtra("MONTH", 0)
        date_selected_day = intent.getIntExtra("DATE", 0)

        setNormalPicker()

        //   filterDialog();
        SEdatesppiner()

        spinnerStatus

        attendanceDetailsInfoList = ArrayList()
        attStatusData = ArrayList()

        /*  adapter = new AttendanceListingAdapter(mActivity, attendanceDetailsInfoList, attStatusData, onItemViewClickListener, preference.getUseR_TYPE_ID());
        binding.rvAllAttendance.setHasFixedSize(true);
        binding.rvAllAttendance.setLayoutManager(new LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false));
        binding.rvAllAttendance.addItemDecoration(new EqualSpacingItemDecoration(0, EqualSpacingItemDecoration.VERTICAL));
        binding.rvAllAttendance.setAdapter(adapter);*/
        // adapter.refreshItem(attendanceDetailsInfoList);
        setClickListener()
        binding!!.linCreateItem.visibility = View.GONE
        binding!!.actionBar.buttonFilter.visibility = View.GONE
        binding!!.buttonprint.visibility = View.GONE


        binding!!.buttonprint.setOnClickListener {
            vibrator!!.vibrate(100)
            //  Pdfform();
            clearSharePreference()
            pagination = 0.toString()
            currentPage = 0
            pageSize = 0
            isPdfRequest = true

            //  getAttendanceList(USER_Id, SDate, EDate);
            getAttendanceList(USER_Id, startDate, endDate)

            // Delay execution to ensure data is loaded
            /*   new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Pdfform();
                        }
                    }, 8000);
    
    */
        }
        binding!!.buttonexcel.visibility = View.GONE
        binding!!.buttonexcel.setOnClickListener {
            vibrator!!.vibrate(100)
            clearSharePreference()
            pagination = 0.toString()
            currentPage = 0
            pageSize = 0
            // getAttendanceList(USER_Id, SDate, EDate);
            isExcelRequest = true

            sharedPreferences =
                getSharedPreferences("shared preferences", MODE_PRIVATE)
            // creating a variable for gson.
            val gson = Gson()

            // below line is to get to string present from our
            // shared prefs if not present setting it as null.
            val fetchUserid = sharedPreferences!!.getString("fetchUserid", null)
            val fetchSDate = sharedPreferences!!.getString("fetchSDate", null)
            val fetchEDate = sharedPreferences!!.getString("fetchEDate", null)


            getAttendanceList(fetchUserid, fetchSDate, fetchEDate)
            Log.d("StartDate", "M---------$SDate  $EDate")


            Log.d("startdate", "M---------$startDate  $endDate")
            /* // Delay execution to ensure data is loaded
                               new Handler().postDelayed(new Runnable() {
                                   @Override
                                   public void run() {
                                       ExcelformTable();
                                   }
                               }, 8000);
               */
        }
        /* binding.totelAttandance.setVisibility(View.GONE);
 binding.inAttandance.setVisibility(View.GONE);
 binding.outAttandance.setVisibility(View.GONE);*/
        //   Log.d("jgggkgjll", "hkjkbb" + attendanceDetailsInfoList);
        val USER_TYPE = preference!!.useR_TYPE
        val USER_TYPE_ID = preference!!.useR_TYPE_ID
        if (USER_TYPE_ID == "1" && USER_TYPE.equals("Admin", ignoreCase = true)) {
            binding!!.linCreateItem.visibility = View.GONE
            binding!!.buttonprint.visibility = View.VISIBLE
            binding!!.buttonexcel.visibility = View.VISIBLE
            binding!!.actionBar.buttonFilter.visibility = View.GONE
            binding!!.llDisUser.visibility = View.VISIBLE
            binding!!.llAttendanceCount.visibility = View.GONE
            binding!!.filter.visibility = View.VISIBLE
            binding!!.SEsearch.visibility = View.GONE

            //  binding.llAttendanceCount.setVisibility(View.VISIBLE);
        } else if (USER_TYPE_ID == "2" && USER_TYPE.equals(
                "Manager",
                ignoreCase = true
            ) || USER_TYPE_ID == "16" && USER_TYPE.equals("HRManager", ignoreCase = true)
        ) {
            binding!!.linCreateItem.visibility = View.GONE
            binding!!.buttonprint.visibility = View.VISIBLE
            binding!!.buttonexcel.visibility = View.VISIBLE
            binding!!.actionBar.buttonFilter.visibility = View.GONE
            binding!!.llDisUser.visibility = View.VISIBLE
            binding!!.llAttendanceCount.visibility = View.GONE
            binding!!.filter.visibility = View.VISIBLE
            binding!!.SEsearch.visibility = View.GONE

            // binding.llAttendanceCount.setVisibility(View.VISIBLE);
        } else if (USER_TYPE_ID == "4" && USER_TYPE.equals("ServiceEngineer", ignoreCase = true)
            || USER_TYPE_ID == "3" && USER_TYPE.equals("Support", ignoreCase = true)
        ) {
            binding!!.linCreateItem.visibility = View.GONE
            binding!!.actionBar.buttonFilter.visibility = View.GONE
            binding!!.rlSedateSpinner.visibility = View.VISIBLE
            binding!!.llDisUser.visibility = View.GONE
            binding!!.llAttendanceCount.visibility = View.VISIBLE
            binding!!.filter.visibility = View.GONE
            binding!!.SEsearch.visibility = View.VISIBLE
            binding!!.buttonprint.visibility = View.VISIBLE
            binding!!.buttonexcel.visibility = View.VISIBLE
        }
        USER_Id = preference!!.useR_Id
        //   Log.d("userid", "" + USER_Id);
        if (USER_TYPE_ID == "1" && USER_TYPE.equals("Admin", ignoreCase = true)) {
            /* Calendar c = Calendar.getInstance();
            String year = String.valueOf(c.get(Calendar.YEAR));
            curMonth = String.valueOf(c.get(Calendar.MONTH)+1);
            Month = curMonth;
            Year = year;
            SEUserId = USER_Id;

            dashboardApi(  USER_Id,  Month ,  Year,SEUserId);*/
            //  SDate =Year+"-"+Month+"-"+"1";

            SEUserId = USER_Id

            val currentMonth = Calendar.getInstance()[Calendar.MONTH] + 1
            val calendar = Calendar.getInstance()
            val today = calendar.time
            val sdf = SimpleDateFormat(myFormat, Locale.US)
            val CurrentDay = sdf.format(today)
            //   Log.d("CurrentDay", " " + CurrentDay);
            SDate = CurrentDay

            EDate = CurrentDay
            currentPage = 1

            getAttendanceList(USER_Id, SDate, EDate)


            filterDialog()
        } else if (USER_TYPE_ID == "2" && USER_TYPE.equals(
                "Manager",
                ignoreCase = true
            ) || USER_TYPE_ID == "16" && USER_TYPE.equals("HRManager", ignoreCase = true)
        ) {
            filterDialog()


            /*
            Calendar c = Calendar.getInstance();
            String year = String.valueOf(c.get(Calendar.YEAR));
            curMonth = String.valueOf(c.get(Calendar.MONTH)+1);
            Month = curMonth;
            Year = year;
            SEUserId = USER_Id;

            dashboardApi(  USER_Id,  Month ,  Year,SEUserId);



            SDate =Year+"-"+Month+"-"+"1";

            Integer currentMonth =   Calendar.getInstance().get(Calendar.MONTH)+1;
            Calendar calendar = Calendar.getInstance();
            Date today = calendar.getTime();
            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
            String   CurrentDay = sdf.format(today);
            Log.d("CurrentDay"," "+CurrentDay);
            EDate =CurrentDay;

            getAttendanceList(USER_Id, SDate, EDate);*/


            //  SDate =Year+"-"+Month+"-"+"1";
            SEUserId = USER_Id

            val currentMonth = Calendar.getInstance()[Calendar.MONTH] + 1
            val calendar = Calendar.getInstance()
            val today = calendar.time
            val sdf = SimpleDateFormat(myFormat, Locale.US)
            val CurrentDay = sdf.format(today)
            //   Log.d("CurrentDay", " " + CurrentDay);
            SDate = CurrentDay
            EDate = CurrentDay
            currentPage = 1
            pagination = 1.toString()


            getAttendanceList(SEUserId, SDate, EDate)
        } else if (USER_TYPE_ID == "4" && USER_TYPE.equals("ServiceEngineer", ignoreCase = true)
            || USER_TYPE_ID == "3" && USER_TYPE.equals("Support", ignoreCase = true)
        ) {
            val c = Calendar.getInstance()
            //    String year = String.valueOf(c.get(Calendar.YEAR));
            //    curMonth = String.valueOf(c.get(Calendar.MONTH) + 1);
            curMonth = (date_selected_month + 1).toString()
            Month = curMonth
            //    Year = year;
            Year = date_selected_year.toString()
            SEUserId = USER_Id

            dashboardApi(USER_Id, Month, Year, SEUserId)


            SDate = "$Year-$Month-1"

            val currentMonth = Calendar.getInstance()[Calendar.MONTH] + 1
            val calendar = Calendar.getInstance()
            val today = calendar.time
            val sdf = SimpleDateFormat(myFormat, Locale.US)
            val CurrentDay = sdf.format(today)
            //  Log.d("CurrentDay", " " + CurrentDay);
            EDate = CurrentDay

            if (Month == currentMonth.toString()) {
                EDate = CurrentDay

                //    Log.d("jkkhjk", "" + EDate);
            } else {
                // Example month and year

                val month = Month.toInt() // June (months are 0-based, so 5 represents June)
                val year = Year.toInt()

                // Get the last day of the month
                val lastDay = getLastDayOfMonth(year, month)
                EDate = "$Year-$Month-$lastDay"

                //  Log.d("itiiuiy", "" + EDate);
                //  Log.d("MyMonth", "" + Month);
                //  Log.d("lastDay", "" + lastDay);
            }
            currentPage = 1
            pagination = 1.toString()


            //starting#
            Log.d("checkDate", "$SDate $EDate")
            startDate = SDate
            endDate = EDate

            getAttendanceList(USER_Id, SDate, EDate)

            val cal = Calendar.getInstance()
            val month_date = SimpleDateFormat("MMMM")
            val monthnum = date_selected_month
            cal[Calendar.MONTH] = monthnum
            val month_name = month_date.format(cal.time)

            // Log.e("", "" + month_name);
            val ed_monthYear = "--$month_name  $date_selected_year--"
            binding!!.dateSelt.setText(ed_monthYear)
        }

        Log.d("attStatusData..", "recyyyyyyy$attStatusData")
        Log.d("attStatusData..", "rec$attendanceDetailsInfoList")
        adapter = AttendanceListingAdapter(
            mActivity!!,
            attendanceDetailsInfoList,
            attStatusData,
            onItemViewClickListener,
            preference!!.useR_TYPE_ID!!
        )

        binding!!.rvAllAttendance.setHasFixedSize(true)
        binding!!.rvAllAttendance.layoutManager =
            LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false)
        binding!!.rvAllAttendance.addItemDecoration(
            EqualSpacingItemDecoration(
                0,
                EqualSpacingItemDecoration.VERTICAL
            )
        )
        binding!!.rvAllAttendance.adapter = adapter

        binding!!.rvAllAttendance.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val layoutManager = recyclerView.layoutManager as LinearLayoutManager?
                if (layoutManager != null) {
                    val visibleItemCount = layoutManager.childCount
                    val totalItemCount = layoutManager.itemCount
                    val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

                    // Log the values to verify scroll position and total item count
                    Log.d("ScrollListener", "VisibleItemCount: $visibleItemCount")
                    Log.d("ScrollListener", "TotalItemCount: $totalItemCount")
                    Log.d(
                        "ScrollListener",
                        "FirstVisibleItemPosition: $firstVisibleItemPosition"
                    )


                    Log.d("loggggggg", " AA    $currentPage")
                    Log.d("loggggggg", " VV    " + !isLoading + " ----" + !isLastPage)
                    Log.d("check--", " VV    " + !isLoading + " ----" + !isLastPage)
                    // Check if more data needs to be loaded
                    if (!isLoading && !isLastPage) {
                        Log.d("loggggggg", "BB     $currentPage")
                        // Load more data when 5 items are left to reach the end
                        if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount - 5) {
                            Log.d("ScrollListener", "End of list reached. Loading more data...")
                            isLoading = true // Set loading flag to true
                            Log.d("loggggggg", "CC     $currentPage")
                            currentPage++ // Increment currentPage
                            Log.d("ScrollListener", "Loading page: $currentPage")
                            // getAttendanceList(USER_Id, SDate, EDate);
                            Log.d("sDateSDate", "" + SDate)
                            Log.d("sDateSDate--", "" + startDate)


                            getAttendanceList(SEUserId, startDate, endDate)

                            // Fetch more data
                        }
                    }
                }
            }
        })


        //   getSpinnerStatus();
    }


    fun clearSharePreference() {
        val sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        // Clearing the value associated with the "camp" key
        editor.remove("courses")
        // Applying the changes to save the updated SharedPreferences
        editor.apply()
    }

    var mMessageReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(mContext: Context, intent: Intent) {
            // Get extra data included in the Intent
            outQty = intent.getStringExtra("quantity")
            otQty = outQty


            //   Log.d("otqty", "" + otQty);
            //    binding.totelAttandance.setText("Total Attn.\n "+totelPrestAtt+"/"+daysDifference);

            //   binding.inAttandance.setText("In Attn.\n"+totelPrestAtt+"/"+totelPrestAtt);

            //     binding.outAttandance.setText("Out Attn.\n"+ otQty+"/"+totelPrestAtt);
        }
    }

    fun attendencecount() {
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        // Parse the date strings into Date objects
        try {
            val startDatee = sdf.parse(startDate)
            val endDatee = sdf.parse(endDate)
            // Get the time difference in milliseconds
            val timeDifferenceMillis = endDatee!!.time - startDatee!!.time
            // Convert milliseconds to days
            daysDifference = TimeUnit.DAYS.convert(timeDifferenceMillis, TimeUnit.MILLISECONDS)
            daysDifference += 1
            //Log.d("time_diff", "" + daysDifference);
            //  Log.d("sjdbsdFB", "" + totelPrestAtt + daysDifference + otQty);
        } catch (e: ParseException) {
            e.printStackTrace()
        }
    }

    var onItemViewClickListener: AttendanceListingAdapter.OnItemViewClickListener =
        AttendanceListingAdapter.OnItemViewClickListener { attendanceDetailsInfo, position -> }


    private fun getAttendanceList(USER_Id: String?, SDate: String?, EDate: String?) {
        if (isNetworkAvailable(mActivity!!)) {
            hideKeyboard(mActivity!!)
            //   Utils.showCustomProgressDialogCommonForAll(mActivity, getResources().getString(R.string.please_wait));
            binding!!.progressBar.visibility = View.VISIBLE

            val apiInterface =
                APIClient.getRetrofitClientWithoutHeaders(mActivity!!, BaseUrl()!!).create(
                    APIInterface::class.java
                )
            val paramStr = GetAttendanceListQueryParams()
            val splitArray =
                paramStr!!.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val paramData: MutableMap<String?, String?> = HashMap()
            paramData[splitArray[0]] = USER_Id
            paramData[splitArray[1]] = districtId
            paramData[splitArray[2]] = SDate
            paramData[splitArray[3]] = EDate
            //  paramData.put(splitArray[4], pagination);
            if (splitArray.size > 4) {
                paramData[splitArray[4]] = pagination
            } else {
                Log.e(
                    "ArrayIndexError",
                    "splitArray has only " + splitArray.size + " elements. paramStr: " + paramStr
                )
            }
            if (splitArray.size > 5) {
                paramData[splitArray[5]] = currentPage.toString()
            } else {
                Log.e(
                    "ArrayIndexError",
                    "splitArray has only " + splitArray.size + " elements. paramStr: " + paramStr
                )
            }
            if (splitArray.size > 6) {
                paramData[splitArray[6]] = pageSize.toString()
            } else {
                Log.e(
                    "ArrayIndexError",
                    "splitArray has only " + splitArray.size + " elements. paramStr: " + paramStr
                )
            }

            val call = apiInterface.callAttendanceListApi(GetAttendanceList(), paramData)
            Log.d("-response-", "  " + GetAttendanceList() + paramData)
            Log.d(
                "Attendance_Pra",
                " ----$USER_Id   $districtId   $SDate   $EDate   $pagination   $currentPage   $pageSize"
            )

            isLoading = true
            call!!.enqueue(object : Callback<ModelAttendanceListing?> {
                override fun onResponse(
                    call: Call<ModelAttendanceListing?>,
                    response: Response<ModelAttendanceListing?>
                ) {
                    binding!!.progressBar.visibility = View.GONE
                    isLoading = false

                    if (response.isSuccessful && response.body() != null) {
                        if (response.body()!!.status == "200") {
                            val newAttendanceList = response.body()!!.data

                            totalPages = response.body()!!.totalPages
                            Log.d("totalPages", "" + totalPages)

                            //  currentPage = response.body().getCurrentPage(); // Use the currentPage from the response
                            if (newAttendanceList != null && !newAttendanceList.isEmpty()) {
                                binding!!.textNoCampSchedule.visibility = View.GONE
                                binding!!.rvAllAttendance.visibility = View.VISIBLE
                                Log.d("USER_Id", "     $USER_Id")




                                if (currentPage == 1) {
                                    // First Page: Set New Data
                                    adapter!!.setData(newAttendanceList, attStatusData)
                                    Log.d("loggggggg", "     $currentPage")
                                } else {
                                    // Next Page: Append Data
                                    Log.d("loggggggg", "     $currentPage")
                                    adapter!!.addData(newAttendanceList, attStatusData)
                                }

                                // Check if it's the last page
                                if (currentPage == totalPages) {
                                    isLastPage = true
                                }
                            } else {
                                if (currentPage == 1) {
                                    binding!!.textNoCampSchedule.visibility = View.VISIBLE
                                    binding!!.rvAllAttendance.visibility = View.GONE
                                }
                                isLastPage = true // No more pages to load
                            }


                            val sharedPreferences =
                                getSharedPreferences("shared preferences", MODE_PRIVATE)


                            val editor = sharedPreferences.edit()

                            val gson = Gson()

                            val json = gson.toJson(response.body()!!.data)


                            editor.putString("courses", json)
                            editor.putString("fetchUserid", USER_Id)
                            editor.putString("fetchSDate", SDate)
                            editor.putString("fetchEDate", EDate)


                            editor.apply()


                            if (isExcelRequest) {
                                ExcelformTable()
                                isExcelRequest = false // Reset flag after generating Excel
                            } else {
                            }



                            if (isPdfRequest) {
                                Pdfform()

                                isPdfRequest = false // Reset flag after generating Excel
                            } else {
                            }
                        } else {
                            makeToast(response.body()!!.message)
                        }
                    } else {
                        makeToast(resources.getString(R.string.error))
                    }
                }

                override fun onFailure(call: Call<ModelAttendanceListing?>, error: Throwable) {
                    binding!!.progressBar.visibility = View.GONE
                    isLoading = false
                    makeToast(resources.getString(R.string.error))
                    Log.e("API Error", error.message, error)
                    call.cancel()
                }
            })
        } else {
            makeToast(resources.getString(R.string.no_internet_connection))
        }
    }


    private val spinnerStatus: Unit
        get() {
            if (isNetworkAvailable(mActivity!!)) {
                val apiInterface =
                    APIClient.getRetrofitClientWithoutHeaders(mActivity!!, BaseUrl()!!).create(
                        APIInterface::class.java
                    )
                val call = apiInterface.GetAttStatus(USER_Id)
                call!!.enqueue(object : Callback<AttStatusRoot?> {
                    override fun onResponse(
                        call: Call<AttStatusRoot?>,
                        response: Response<AttStatusRoot?>
                    ) {
                        hideCustomProgressDialogCommonForAll()
                        Log.d("USER_Id..", "USER_Id..$USER_Id")

                        if (response.isSuccessful) {
                            if (response.code() == 200) {
                                if (response.body() != null) {
                                    if (response.body()!!.status == "200") {
                                        hideCustomProgressDialogCommonForAll()

                                        val getErrorTypesRoot = response.body()
                                        Log.d(
                                            "getErrorTypesRoot..",
                                            "getErrorTypesRoot..$getErrorTypesRoot"
                                        )

                                        Log.d(
                                            "aaaaaaaaaaaa",
                                            "${getErrorTypesRoot!!.data}"
                                        )

                                        attStatusData =
                                            getErrorTypesRoot.data

                                        Log.d(
                                            "attStatusData..",
                                            "attStatusData..$attStatusData"
                                        )

                                        Log.d(
                                            "bbbbbbbbbbb",
                                            "$attStatusData"
                                        )

                                        for (i in attStatusData.indices) {
                                            //  playerNames.add(attStatusData.get(i).getAttendanceStatus().toString());
                                            val attendancestatus =
                                                attStatusData[i].attendanceStatus.toString()

                                            Log.d(
                                                "attStatusData..",
                                                "attStatusData..$attendancestatus"
                                            )
                                        }
                                    } else {
                                        makeToast(response.body()!!.message.toString())
                                    }
                                } else {
                                    makeToast(mActivity!!.resources.getString(R.string.error))
                                }
                            } else {
                                makeToast(mActivity!!.resources.getString(R.string.error))
                            }
                        } else {
                            makeToast(mActivity!!.resources.getString(R.string.error))
                        }
                    }

                    override fun onFailure(call: Call<AttStatusRoot?>, error: Throwable) {
                        hideCustomProgressDialogCommonForAll()
                        hideCustomProgressDialogCommonForAll()

                        makeToast(mActivity!!.resources.getString(R.string.error))

                        call.cancel()
                    }
                })
            } else {
                makeToast(mActivity!!.resources.getString(R.string.no_internet_connection))
            }
        }


    fun makeToast(string: String?) {
        if (TextUtils.isEmpty(string)) return
        Toast.makeText(mActivity, string, Toast.LENGTH_SHORT).show()
    }

    private fun setClickListener() {
        binding!!.actionBar.ivBack.setOnClickListener { //    Intent intent = new Intent(mActivity, MainActivity.class);
            //    startActivity(intent);
            finish()
        }


        binding!!.actionBar.buttonFilter.setOnClickListener(object : OnSingleClickListener() {
            override fun onSingleClick(v: View?) {
                hideCustomProgressDialogCommonForAll()
                val sh = getSharedPreferences("MyPrefs", MODE_PRIVATE)
                d1 = sh.getString("dis", "")
                d2 = sh.getString("districtId", "")
                val sh2 = getSharedPreferences("MyPrefsMyPrefs", MODE_PRIVATE)
                selecteduserid = sh2.getString("UserId", "")
                Log.d("d2IDDD", "" + d2)
                Log.d("selecteduserid", "" + selecteduserid)



                filterDialog()
            }
        })
        binding!!.buttonMarkAttendance.setOnClickListener { view: View? ->
            permissionsToRequest = findUnAskedPermissions(permissions)
            //get the permissions we have asked for before but are not granted..
            //we will store this in a global list to access later.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (permissionsToRequest!!.size > 0) {
                    Log.d("checkcheck", "1")
                    requestPermissions(
                        permissionsToRequest!!.toTypedArray<String>(),
                        ALL_PERMISSIONS_RESULT
                    )
                } else {
                    val intent = Intent(mActivity, MarkAttendanceActivity::class.java)
                    startActivityForResult(intent, LAUNCH_MARK_ATTENDANCE_ACTIVITY)
                    Log.d("checkcheck", "2")
                }
            } else {
                val intent = Intent(mActivity, MarkAttendanceActivity::class.java)
                startActivityForResult(intent, LAUNCH_MARK_ATTENDANCE_ACTIVITY)
                Log.d("checkcheck", "3")
            }
        }


        binding!!.btnsearch.setOnClickListener { //String fps = binding.inputSearch.getText().toString();
            //  fpscodee = binding.inputSearch.getText().toString();
            //. getPosError(expenseStatusId,districtId,fromDate,toDate,fpscodee);


            //   filterDialog();


            USER_Id = SEUserId
            Log.d("USER_IdUSER_Id", "ioio$USER_Id")

            Log.d("districtList", "$districtNameEng  $SEUserNameEng")

            //   Log.d("startDate_endDate",""+startDate + "  "+ endDate);
            Log.d("startDate_endDate", "$Month  $Year")

            dashboardApi(USER_Id, Month, Year, SEUserId)

            hideCustomProgressDialogCommonForAll()
            val sh = getSharedPreferences("MyPrefs", MODE_PRIVATE)
            d1 = sh.getString("dis", "")
            d2 = sh.getString("districtId", "")
            val sh2 = getSharedPreferences("MyPrefsMyPrefs", MODE_PRIVATE)
            selecteduserid = sh2.getString("UserId", "")
            Log.d("d2IDDD", "" + d2)
            Log.d("selecteduserid", "" + selecteduserid)


            SDate = "$Year-$Month-1"

            val currentMonth = Calendar.getInstance()[Calendar.MONTH] + 1
            val calendar = Calendar.getInstance()
            val today = calendar.time
            val sdf = SimpleDateFormat(myFormat, Locale.US)
            val CurrentDay = sdf.format(today)
            Log.d("CurrentDay", " $CurrentDay")


            //  curMonth = String.valueOf(c.get(Calendar.MONTH)+1);
            Log.d("bmvbkhkuk", "" + USER_Id)
            Log.d("ytuyu", "" + Month)
            Log.d("jyjjh", "" + currentMonth)


            if (Month == currentMonth.toString()) {
                EDate = CurrentDay
                Log.d("jkkhjk", "" + EDate)
            } else {
                // Example month and year

                val month = Month.toInt() // June (months are 0-based, so 5 represents June)
                val year = Year.toInt()

                // Get the last day of the month
                val lastDay = getLastDayOfMonth(year, month)
                EDate = "$Year-$Month-$lastDay"
                Log.d("itiiuiy", "" + EDate)
                Log.d("MyMonth", "" + Month)
                Log.d("lastDay", "" + lastDay)
            }

            Log.d("jfddkjv", "" + EDate)
            currentPage = 1
            pageSize = 10
            isLoading = false

            startDate = SDate
            endDate = EDate

            Log.d("startDatestartDate", "" + startDate)
            // Reset to first page
            isLastPage = false
            getAttendanceList(USER_Id, SDate, EDate)
        }
    }


    private fun filterDialog() {
        try {
            spinnerDistrict = findViewById(R.id.spinnerDistrict)
            spinnerSEUsers = findViewById(R.id.spinnerSEUsers)
            layoutDateRange = findViewById(R.id.layout_date_range)

            val inputTrainingNumber = findViewById<EditText>(R.id.inputTrainingNumber)
            val buttonTrainingFilter = findViewById<Button>(R.id.buttonTrainingFilter)
            Log.d("jgjgk", "" + d1)
            Log.d("kjkkgk", "" + d2)
            districtNameEng = "--" + resources.getString(R.string.district) + "--"
            SEUserNameEng = "--" + resources.getString(R.string.se_user) + "--"
            districtList()
            Collections.reverse(SEUser_list)
            val l = ModelSEUserList()
            l.userId = (-1).toString()
            l.userName = "--" + resources.getString(R.string.se_user) + "--"
            SEUser_list.add(l)
            Collections.reverse(SEUser_list)
            val dataAdapter = ArrayAdapter(
                mActivity!!, android.R.layout.simple_spinner_item, SEUser_list
            )
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerSEUsers!!.setAdapter(dataAdapter)


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
                        dis = district_List[i]!!.districtNameEng
                        Log.d("dfgfd", " $dis")
                        districtId = district_List[i]!!.districtId
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
                            spinnerSEUsers!!.setAdapter(dataAdapter)
                        } else {
                            SEUsersList(districtId)
                        }
                    }
                }

                override fun onNothingSelected(adapterView: AdapterView<*>?) {
                }
            })

            spinnerSEUsers!!.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    adapterView: AdapterView<*>?,
                    view: View,
                    i: Int,
                    l: Long
                ) {
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
            })


            binding!!.buttonTrainingFilter.setOnClickListener { view: View? ->
                val UserIdd = SEUserId!!.toInt()
                Log.d("userid", "" + UserIdd)


                //   districtId = district_List.get(i).getDistrictId();
                Log.d("districtList", "$districtNameEng  $SEUserNameEng")
                //   startDate = inputStartDate.getText().toString().trim();
                //  endDate = inputEndDate.getText().toString().trim();
                Log.d("startDate_endDate", "$startDate  $endDate")
                layoutDateRange!!.getVisibility()
                if (layoutDateRange!!.getVisibility() == View.VISIBLE && startDate!!.isEmpty()) {
                    Toast.makeText(mActivity, "Select Start date", Toast.LENGTH_SHORT).show()
                } else if (layoutDateRange!!.getVisibility() == View.VISIBLE && endDate!!.isEmpty()) {
                    Toast.makeText(mActivity, "Select End date", Toast.LENGTH_SHORT).show()
                } else {
                    isLoading = false // Start loading
                    currentPage = 1
                    pagination = 1.toString()
                    pageSize = 10
                    // Reset to first page
                    isLastPage = false
                    getAttendanceList(SEUserId, startDate, endDate)
                }
            }


            /*    binding. btnsearch.setOnClickListener(view -> {

            int UserIdd = Integer.parseInt(SEUserId);
            Log.d("userid",""  +UserIdd);

            Log.d("districtList",""+districtNameEng + "  "+ SEUserNameEng);

            Log.d("startDate_endDate",""+startDate + "  "+ endDate);

            dashboardApi(  USER_Id,  Month ,  Year,SEUserId);




        });*/
            // dialog.show();
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    private fun SEdatesppiner() {
        //SE Date Spinner


        val SEspinnerList: MutableList<String> = ArrayList()
        SEspinnerList.add("--" + resources.getString(R.string.select_filter) + "--")
        SEspinnerList.add(resources.getString(R.string.today))
        SEspinnerList.add(resources.getString(R.string.yesterday))
        SEspinnerList.add(resources.getString(R.string.current_month))
        SEspinnerList.add(resources.getString(R.string.previous_month))
        SEspinnerList.add(resources.getString(R.string.custom_filter))
        val SEdataAdapterr = ArrayAdapter(this, android.R.layout.simple_spinner_item, SEspinnerList)
        SEdataAdapterr.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding!!.seDateSpinner.adapter = SEdataAdapterr
        binding!!.seDateSpinner.setSelection(1)

        //  String SEselectedString = (String)  binding. seDateSpinner.getSelectedItem();
        val SEselectedItemPosition = binding!!.seDateSpinner.selectedItemPosition
        if (SEselectedItemPosition == 1) {
            binding!!.layoutDateRange.visibility = View.GONE
            val calendar = Calendar.getInstance()
            val today = calendar.time
            val sdf = SimpleDateFormat(myFormat, Locale.US)
            todayDate = sdf.format(today)
            startDate = todayDate
            endDate = todayDate
        } else {
        }

        binding!!.seDateSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
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
                            /*   Objects.requireNonNull(inputStartDate.getText()).clear();
                            Objects.requireNonNull(inputEndDate.getText()).clear();*/
                            binding!!.textFromDate.text!!.clear()
                            binding!!.textToDate.text!!.clear()
                            if (item.equals(
                                    resources.getString(R.string.today),
                                    ignoreCase = true
                                )
                            ) {
                                binding!!.layoutDateRange.visibility = View.GONE
                                val calendar = Calendar.getInstance()
                                val today = calendar.time
                                val sdf = SimpleDateFormat(myFormat, Locale.US)
                                todayDate = sdf.format(today)
                                startDate = todayDate
                                endDate = todayDate
                                //  getAttendanceList(USER_Id,startDate,endDate);
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
                                startDate = yesterdayDate
                                endDate = yesterdayDate
                                // getReportList(districtId,startDate,endDate);
                                //  getAttendanceList(USER_Id,startDate,endDate);
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
                                startDate = date1
                                endDate = date2
                                //   getAttendanceList(USER_Id,startDate,endDate);
                                //   getReportList(districtId,startDate,endDate);
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
                                binding!!.layoutDateRange.visibility = View.VISIBLE
                            }
                        } else {
                            startDate = ""
                            endDate = ""

                            //   Objects.requireNonNull(inputStartDate.getText()).clear();
                            //  Objects.requireNonNull(inputEndDate.getText()).clear();
                            binding!!.textFromDate.text!!.clear()
                            binding!!.textToDate.text!!.clear()
                            binding!!.layoutDateRange.visibility = View.GONE
                            Log.d("yoyo", "yoyo")
                            // getAttendanceList(USER_Id,startDate,endDate);
                            // getReportList(districtId,startDate,endDate);
                        }
                    }
                }

                override fun onNothingSelected(adapterView: AdapterView<*>?) {
                }
            }


        /*from date*/
        val dateFromDate =
            OnDateSetListener { view: DatePicker?, year: Int, monthOfYear: Int, dayOfMonth: Int ->
                myCalendarFromDate[Calendar.YEAR] =
                    year
                myCalendarFromDate[Calendar.MONTH] = monthOfYear
                myCalendarFromDate[Calendar.DAY_OF_MONTH] = dayOfMonth
                updateLabelFromDateSE()
            }
        val date = Date() // to get the date
        val df = SimpleDateFormat("yyyy-MM-dd") // getting date in this format
        val formattedDate = df.format(date.time)
        binding!!.textFromDate.setText(formattedDate)
        binding!!.textFromDate.setOnClickListener { view: View? ->
            val datePickerDialog = DatePickerDialog(
                mActivity!!, R.style.DialogThemeTwo, dateFromDate,
                myCalendarFromDate[Calendar.YEAR],
                myCalendarFromDate[Calendar.MONTH],
                myCalendarFromDate[Calendar.DAY_OF_MONTH]
            )
            datePickerDialog.datePicker.maxDate = System.currentTimeMillis()
            val minDateInMillis = myCalendarToDate.timeInMillis
            datePickerDialog.datePicker.maxDate = minDateInMillis
            datePickerDialog.show()
        }

        /*from date*/

        /*to date*/
        val dateToDate =
            OnDateSetListener { view: DatePicker?, year: Int, monthOfYear: Int, dayOfMonth: Int ->
                myCalendarToDate[Calendar.YEAR] =
                    year
                myCalendarToDate[Calendar.MONTH] = monthOfYear
                myCalendarToDate[Calendar.DAY_OF_MONTH] = dayOfMonth
                updateLabelToDateSE()
            }

        binding!!.textToDate.setText(formattedDate)
        binding!!.textToDate.setOnClickListener { view: View? ->
            val datePickerDialog = DatePickerDialog(
                mActivity!!,
                R.style.DialogThemeTwo,
                dateToDate,
                myCalendarToDate[Calendar.YEAR],
                myCalendarToDate[Calendar.MONTH],
                myCalendarToDate[Calendar.DAY_OF_MONTH]
            )
            datePickerDialog.datePicker.maxDate = System.currentTimeMillis()
            val minDateInMillis = myCalendarFromDate.timeInMillis
            datePickerDialog.datePicker.minDate = minDateInMillis
            /*if (myCalendarToDate.before(myCalendarFromDate)) {
        // "To" date is smaller than "from" date, show an error message or take appropriate action
        Toast.makeText(mActivity, "Invalid date selection", Toast.LENGTH_SHORT).show();
        // Reset the "to" date to the previous valid selection
    }*/
            datePickerDialog.show()
        }


        /*to date*/


        //end SE date spinner
    }


    override fun onBackPressed() {
        clearSharePreference()

        //        Intent intent = new Intent(mActivity, MainActivity.class);
//        startActivity(intent);
        super.onBackPressed()
    }

    @SuppressLint("SetTextI18n")
    private fun updateLabelFromDate() {
        // SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        val sdf = SimpleDateFormat("dd-MM-yyyy", Locale.US)
        inputStartDate!!.setText(sdf.format(myCalendarFromDate.time))
        Log.d("yoyoyo", "hohoho$sdf")

        //  Objects.requireNonNull(binding.textToDate.getText()).clear();
        //  fromDate = Objects.requireNonNull(binding.textFromDate.getText()).toString().trim();
        //   startDate = inputStartDate.getText().toString().trim();
        val calstartDate = inputStartDate.text.toString().trim { it <= ' ' }
        val originalFormat = "dd-MM-yyyy"
        val desiredFormat = "yyyy-MM-dd"

        startDate = convertDateFormat(calstartDate, originalFormat, desiredFormat)
        Log.d("formateddate", "" + startDate)


        // inputEndDate.getText().clear();
    }

    @SuppressLint("SetTextI18n")
    private fun updateLabelToDate() {
        val myFormat = "dd-MM-yyy"
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        inputEndDate!!.setText(sdf.format(myCalendarToDate.time))
        //  endDate = inputEndDate.getText().toString().trim();
        val calendDate = inputEndDate.text.toString().trim { it <= ' ' }
        val originalFormat = "dd-MM-yyyy"
        val desiredFormat = "yyyy-MM-dd"

        endDate = convertDateFormat(calendDate, originalFormat, desiredFormat)
        Log.d("formateddate", "" + startDate)
    }


    @SuppressLint("SetTextI18n")
    private fun updateLabelFromDateSE() {
        // SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        val sdf = SimpleDateFormat("dd-MM-yyyy", Locale.US)
        binding!!.textFromDate.setText(sdf.format(myCalendarFromDate.time))
        Log.d("yoyoyo", "hohoho$sdf")

        //  Objects.requireNonNull(binding.textToDate.getText()).clear();
        //  fromDate = Objects.requireNonNull(binding.textFromDate.getText()).toString().trim();
        //   startDate = inputStartDate.getText().toString().trim();
        val calstartDate = binding!!.textFromDate.text.toString().trim { it <= ' ' }
        val originalFormat = "dd-MM-yyyy"
        val desiredFormat = "yyyy-MM-dd"

        startDate = convertDateFormat(calstartDate, originalFormat, desiredFormat)
        Log.d("formateddate", "" + startDate)


        // inputEndDate.getText().clear();
    }

    @SuppressLint("SetTextI18n")
    private fun updateLabelToDateSE() {
        val myFormat = "dd-MM-yyy"
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        binding!!.textToDate.setText(sdf.format(myCalendarToDate.time))
        //  endDate = inputEndDate.getText().toString().trim();
        val calendDate = binding!!.textToDate.text.toString().trim { it <= ' ' }
        val originalFormat = "dd-MM-yyyy"
        val desiredFormat = "yyyy-MM-dd"

        endDate = convertDateFormat(calendDate, originalFormat, desiredFormat)
        Log.d("formateddate", "" + startDate)
    }


    private fun SEUsersList(districtId: String?) {
        if (isNetworkAvailable(mActivity!!)) {
            hideKeyboard(mActivity!!)
            binding!!.progressBar.visibility = View.INVISIBLE
            showCustomProgressDialogCommonForAll(
                mActivity!!,
                resources.getString(R.string.please_wait)
            )
            val apiInterface =
                APIClient.getRetrofitClientWithoutHeaders(mActivity!!, BaseUrl()!!).create(
                    APIInterface::class.java
                )
            val paramStr = SEUserListByDistictQueryParams()
            val splitArray =
                paramStr!!.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val paramData: MutableMap<String?, String?>? = HashMap()
            paramData!!.set(splitArray[0], districtId)
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
                                if (Objects.requireNonNull<ModelSEUser?>(response.body()).status == "200") {
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
                                        if (selecteduserid != null && !selecteduserid!!.isEmpty()) {
                                            Log.d("ghjh", "" + selecteduserid)
                                            val userId =
                                                selecteduserid!!.toInt() // replace with the user ID you want to select
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
        if (isNetworkAvailable(mActivity!!)) {
            hideKeyboard(mActivity!!)

            binding!!.progressBar.visibility = View.VISIBLE

            //   Utils.showCustomProgressDialogCommonForAll(mActivity, getResources().getString(R.string.please_wait));
            val apiInterface =
                APIClient.getRetrofitClientWithoutHeaders(mActivity!!, BaseUrl()!!).create(
                    APIInterface::class.java
                )
            val call = apiInterface.callDistrictListApi(DistictList())
            call!!.enqueue(object : Callback<ModelDistrict?> {
                override fun onResponse(
                    call: Call<ModelDistrict?>,
                    response: Response<ModelDistrict?>
                ) {
                    hideCustomProgressDialogCommonForAll()
                    binding!!.progressBar.visibility = View.GONE
                    if (response.isSuccessful) {
                        if (response.code() == 200) {
                            if (response.body() != null) {
                                if (Objects.requireNonNull<ModelDistrict?>(response.body()).status == "200") {
                                    district_List = response.body()!!.district_List
                                    if (district_List != null && district_List.size > 0) {
                                        Collections.reverse(district_List)
                                        val l = ModelDistrictList()
                                        l.districtId = (-1).toString()
                                        l.districtNameEng =
                                            "--" + resources.getString(R.string.district) + "--"
                                        district_List.add(l)
                                        Collections.reverse(district_List)
                                        val dataAdapter = ArrayAdapter(
                                            mActivity!!,
                                            android.R.layout.simple_spinner_item,
                                            district_List
                                        )
                                        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                                        spinnerDistrict!!.adapter = dataAdapter
                                        //   spinnerDistrict.setSelection(1);
                                        /*if(d2!=null)
                                        {
                                            spinnerDistrict.setSelection(Integer.parseInt(d2));
                                        }*/
                                        if (d2 != null && !d2!!.isEmpty()) {
                                            Log.d("ghjh", "" + d2)
                                            val userId =
                                                d2!!.toInt() // replace with the user ID you want to select
                                            for (i in 0 until dataAdapter.count) {
                                                val user = dataAdapter.getItem(i)
                                                if (user!!.districtId.toString() == userId.toString()) {
                                                    spinnerDistrict!!.setSelection(i)
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

                override fun onFailure(call: Call<ModelDistrict?>, error: Throwable) {
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


    private fun findUnAskedPermissions(wanted: ArrayList<String>): ArrayList<String> {
        val result = ArrayList<String>()
        for (perm in wanted) {
            if (!hasPermission(perm)) {
                result.add(perm)
            }
        }
        return result
    }

    private fun hasPermission(permission: String): Boolean {
        if (canMakeSmores()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return (checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED)
            }
        }
        return true
    }

    private fun canMakeSmores(): Boolean {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1)
    }

    @TargetApi(Build.VERSION_CODES.M)
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            ALL_PERMISSIONS_RESULT -> {
                for (perms in permissionsToRequest!!) {
                    if (!hasPermission(perms)) {
                        permissionsRejected.add(perms)
                    }
                }
                if (permissionsRejected.size > 0) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (shouldShowRequestPermissionRationale(permissionsRejected[0])) {
                            showMessageOKCancel(
                                "These permissions are mandatory for the application. Please allow access."
                            ) { dialog, which ->
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    requestPermissions(
                                        permissionsRejected.toTypedArray<String>(),
                                        ALL_PERMISSIONS_RESULT
                                    )
                                }
                            }

                            return
                        }
                    }
                } else {
                    val intent = Intent(mActivity, MarkAttendanceActivity::class.java)
                    startActivityForResult(intent, LAUNCH_MARK_ATTENDANCE_ACTIVITY)
                }
            }
        }
    }

    private fun showMessageOKCancel(message: String, okListener: DialogInterface.OnClickListener) {
        AlertDialog.Builder(mActivity!!)
            .setMessage(message)
            .setPositiveButton("OK", okListener)
            .setNegativeButton("Cancel", null)
            .create()
            .show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == LAUNCH_MARK_ATTENDANCE_ACTIVITY) {
            if (resultCode == RESULT_OK) {
                attendanceDetailsInfoList = ArrayList()
                attStatusData = ArrayList()

                Log.d("cccccc", ">>>>")
                adapter!!.setData(attendanceDetailsInfoList!!, attStatusData)

                val USER_TYPE = preference!!.useR_TYPE
                val USER_TYPE_ID = preference!!.useR_TYPE_ID
                val USER_Id = preference!!.useR_Id
                if (USER_TYPE_ID == "4" && USER_TYPE.equals("ServiceEngineer", ignoreCase = true)
                    || USER_TYPE_ID == "3" && USER_TYPE.equals("Support", ignoreCase = true)
                ) {
                    //  getAttendanceList(USER_Id, "", "");
                    isLoading = false

                    currentPage = 1
                    getAttendanceList(USER_Id, SDate, EDate)
                }
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
                    val playStoreIntent = Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("market://details?id=cn.wps.moffice_eng")
                    )
                    startActivity(playStoreIntent)
                    // Handle the case where WPS Office or a compatible app is not installed
                }
            }
        }
    }


    private fun Pdfform() {
        val originalFormat = "yyyy-MM-dd"
        val desiredFormat = "dd/MM/yyyy"
        val corStartDate = convertDateFormat(
            startDate!!, originalFormat, desiredFormat
        )
        val corEndDate = convertDateFormat(
            endDate!!, originalFormat, desiredFormat
        )
        Log.d("formateddate", "" + corStartDate)
        val intent = Intent(mActivity, Excelfortable::class.java)
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
        val json = sharedPreferences!!.getString("courses", null)
        if (json == null) {
            Log.e("ExcelGeneration", "No data found in SharedPreferences.")
            Toast.makeText(this, "No data available to generate Excel", Toast.LENGTH_SHORT).show()
            return
        }

        // below line is to get the type of our array list.
        val type = object : TypeToken<ArrayList<AttendanceDetailsInfo?>?>() {
        }.type

        // in below line we are getting data from gson
        // and saving it to our array list
        attendanceDetailsInfoList = gson.fromJson(json, type)


        // checking below if the array list is empty or not
        /* if (attendanceDetailsInfoList == null) {
            // if the array list is empty
            // creating a new array list.
            attendanceDetailsInfoList = new ArrayList<>();
            Log.d("nbb", "" + attendanceDetailsInfoList);
        }*/
        if (attendanceDetailsInfoList == null || attendanceDetailsInfoList!!.isEmpty()) {
            attendanceDetailsInfoList = ArrayList()

            Log.e("ExcelGeneration", "Attendance list is empty.")
            Toast.makeText(this, "No attendance data found", Toast.LENGTH_SHORT).show()
            return
        }

        Log.d("gfhvbb", "" + attendanceDetailsInfoList)
        val originalFormat = "yyyy-MM-dd"
        val desiredFormat = "dd-MM-yyyy"
        val corStartDate = convertDateFormat(
            startDate!!, originalFormat, desiredFormat
        )
        val corEndDate = convertDateFormat(
            endDate!!, originalFormat, desiredFormat
        )
        Log.d("formateddate", "" + corStartDate)

        val workbook = HSSFWorkbook()
        val firstSheet = workbook.createSheet("Record $corStartDate - $corEndDate")

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
        val richText1: RichTextString = HSSFRichTextString("User\nName")
        richText1.applyFont(boldFont)
        // Set the RichTextString to the cell
        cellB.setCellValue(richText1)
        firstSheet.setColumnWidth(1, 4000)
        //   cellB.setCellStyle(cellStyle);
        val cellC = rowA.createCell(2)
        firstSheet.setColumnWidth(2, 4000)
        val richText2: RichTextString = HSSFRichTextString("Punch\nDate")
        richText2.applyFont(boldFont)
        cellC.setCellValue(richText2)
        //  cellC.setCellValue(new HSSFRichTextString("Punch\nDate"));
        val cellD = rowA.createCell(3)
        firstSheet.setColumnWidth(3, 4000)
        val richText3: RichTextString = HSSFRichTextString("Punch\nDay")
        richText3.applyFont(boldFont)
        cellD.setCellValue(richText3)
        // cellD.setCellValue(new HSSFRichTextString("Punch\nDay"));
        val cellE = rowA.createCell(4)
        firstSheet.setColumnWidth(4, 4000)
        val richText4: RichTextString = HSSFRichTextString("PunchInTime")
        richText4.applyFont(boldFont)
        cellE.setCellValue(richText4)
        //  cellE.setCellValue(new HSSFRichTextString("PunchIn\nTime"));
        val cellF = rowA.createCell(5)
        firstSheet.setColumnWidth(5, 4000)
        val richText5: RichTextString = HSSFRichTextString("PunchOutTime")
        richText5.applyFont(boldFont)
        cellF.setCellValue(richText5)
        // cellF.setCellValue(new HSSFRichTextString("Punch\nOut\nTime"));
        //    cellF.setCellStyle(cellStyle);
        val cellG = rowA.createCell(6)
        firstSheet.setColumnWidth(6, 20000)
        val richText6: RichTextString = HSSFRichTextString("AddressIn")
        richText6.applyFont(boldFont)
        cellG.setCellValue(richText6)
        //  cellG.setCellValue(new HSSFRichTextString("AddressIn"));
        //    cellG.setCellStyle(cellStyle);
        val cellH = rowA.createCell(7)
        firstSheet.setColumnWidth(7, 20000)
        //cellH.setCellValue(new HSSFRichTextString("AddressOut"));
        val richText7: RichTextString = HSSFRichTextString("AddressOut")
        richText7.applyFont(boldFont)
        cellH.setCellValue(richText7)


        Log.d("mylist", " -------------- $attendanceDetailsInfoList")
        if (attendanceDetailsInfoList != null && attendanceDetailsInfoList!!.size > 0) {
            for (i in attendanceDetailsInfoList!!.indices) {
                val detailsInfo = attendanceDetailsInfoList!![i]
                val districtName = detailsInfo.districtName.toString()
                val attendanceValue = detailsInfo.username.toString()
                val date = detailsInfo.punchInDate.toString()
                val day = detailsInfo.dayName.toString()
                val intime = detailsInfo.punchInTime.toString()
                val outtime = detailsInfo.punchOutTime.toString()
                val inadd = detailsInfo.address_In.toString()
                val outadd = detailsInfo.address_Out.toString()
                val dataRow: Row = firstSheet.createRow(i + 1) // Start from row 1 for data
                // Column 1: District Name
                dataRow.createCell(0).setCellValue(districtName)
                // Column 2: Attendance Value
                dataRow.createCell(1).setCellValue(attendanceValue)
                dataRow.createCell(2).setCellValue(date)
                dataRow.createCell(3).setCellValue(day)
                dataRow.createCell(4).setCellValue(intime)
                dataRow.createCell(5).setCellValue(outtime)
                dataRow.createCell(6).setCellValue(inadd)
                dataRow.createCell(7).setCellValue(outadd)
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
                this@AttendanceListingActivity,
                "Excel Sheet Download ",
                Toast.LENGTH_SHORT
            ).show()
        }



        try {
            val fileUri = FileProvider.getUriForFile(
                this@AttendanceListingActivity,
                BuildConfig.APPLICATION_ID + ".provider",  // Replace with your app's provider authority
                filePathh
            )

            val openIntent = Intent(Intent.ACTION_VIEW)
            openIntent.setDataAndType(fileUri, "application/vnd.ms-excel")
            openIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            startActivity(openIntent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(
                this@AttendanceListingActivity,
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

    private fun dashboardApi(USER_Id: String?, Month: String, Year: String, SEUserId: String?) {
//Progress Bar while connection establishes
        var USER_Id = USER_Id
        if (isNetworkAvailable(mActivity!!)) {
            hideKeyboard(mActivity!!)
            //  Utils.showCustomProgressDialogCommonForAll(mActivity, getResources().getString(R.string.please_wait));
            binding!!.progressBar.visibility = View.VISIBLE
            USER_Id = preference!!.useR_Id

            Log.d("dashboardAPI", " $USER_Id   $Month   $Year   $SEUserId")

            val apiInterface =
                APIClient.getRetrofitClientWithoutHeaders(mActivity!!, BaseUrl()!!).create(
                    APIInterface::class.java
                )
            val call = apiInterface.dashboardApi(USER_Id, Month, Year, SEUserId)
            call!!.enqueue(object : Callback<DashbordRoot?> {
                override fun onResponse(
                    call: Call<DashbordRoot?>,
                    response: Response<DashbordRoot?>
                ) {
                    hideCustomProgressDialogCommonForAll()
                    binding!!.progressBar.visibility = View.GONE


                    if (response.isSuccessful) {
                        if (response.code() == 200) {
                            if (response.body() != null) {
                                if (response.body()!!.status == "200") {
                                    val dashbordRoot = response.body()
                                    val dashboardUserDetails =
                                        dashbordRoot!!.user_details
                                    val CreatedCampCount = dashboardUserDetails.createdCampCount
                                    val OrganizedCampCount = dashboardUserDetails.organizedCampCount
                                    val AttendanceCount = dashboardUserDetails.attendanceCount
                                    val presentCount = dashboardUserDetails.persent.toString()
                                    val abstCount = dashboardUserDetails.absent.toString()
                                    val halfdayCount = dashboardUserDetails.halfDay.toString()
                                    val leaveCount = dashboardUserDetails.leave.toString()
                                    val tourCount = dashboardUserDetails.tour.toString()
                                    // binding.totelAttandance.setText(CreatedCampCount);
                                    // binding.preAttandance.setText(OrganizedCampCount);
                                    binding!!.totelAttandance.text = "Total Attn.\n$AttendanceCount"
                                    binding!!.preAttandance.text = "Present.\n$presentCount"
                                    binding!!.aabAttandance.text = "Absent.\n$abstCount"
                                    binding!!.halfday.text = "HalfDay.\n$halfdayCount"
                                    binding!!.leave.text = "Leave.\n$leaveCount"
                                    binding!!.tour.text = "Tour.\n$tourCount"


                                    Log.d(
                                        "ghmn",
                                        "asmbnmldjfjas$CreatedCampCount  $OrganizedCampCount $AttendanceCount"
                                    )
                                } else {
                                    makeToast(response.body()!!.message.toString())
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

                override fun onFailure(call: Call<DashbordRoot?>, error: Throwable) {
                    hideCustomProgressDialogCommonForAll()
                    binding!!.progressBar.visibility = View.GONE


                    makeToast(resources.getString(R.string.error))

                    call.cancel()
                }
            })
        } else {
            makeToast(resources.getString(R.string.no_internet_connection))
            hideCustomProgressDialogCommonForAll()
            binding!!.progressBar.visibility = View.GONE
        }
    }


    private fun setNormalPicker() {
        //  setContentView(R.layout.activity_main);
        val today = Calendar.getInstance()
        findViewById<View>(R.id.date_selt).setOnClickListener {
            val minDate = "1609439400000".toLong()
            val c = Calendar.getInstance()
            val maxDate = c.timeInMillis
            DatePickerDialog(mContext!!, binding!!.dateSelt, minDate, maxDate)
        }
    }


    private fun DatePickerDialog(con: Context, v: View, mindate: Long, maxDate: Long) {
        //    DatePickerDialog dpd = new DatePickerDialog(con, new DatePickerDialog.OnDateSetListener() {

        val dpd: DatePickerDialog = object : DatePickerDialog(
            con,
            THEME_HOLO_LIGHT,
            OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                date_selected_year = year
                date_selected_month = monthOfYear
                date_selected_day = dayOfMonth
                // TODO Auto-generated method stub
                val formatter = DecimalFormat("00")
                val date = formatter.format(dayOfMonth.toLong())
                val month = formatter.format((monthOfYear + 1).toLong())
                Month = month
                Year = year.toString()
                val cal = Calendar.getInstance()
                val month_date = SimpleDateFormat("MMMM")
                val monthnum = Month.toInt() - 1
                cal[Calendar.MONTH] = monthnum
                val month_name = month_date.format(cal.time)

                //   Log.e(">>>>>", "" + monthnum);
                val ed_monthYear = "--$month_name  $Year--"

                if (v is Button) {
                    //                    btn.setText(date + "/" + month + "/" + year);
                    //                    btn.setTag(date + "/" + month + "/" + year);
                    v.text = ed_monthYear
                } else if (v is EditText) {
                    //                    txt.setText(date + "/" + month + "/" + year);
                    //                    txt.setTag(date + "/" + month + "/" + year);
                    v.setText(ed_monthYear)
                }
                //    binding.buttonTrainingFilter.performClick();
            }, date_selected_year, date_selected_month, date_selected_day
        ) {
            override fun onCreate(savedInstanceState: Bundle?) {
                super.onCreate(savedInstanceState)
                datePicker.findViewById<View>(
                    resources.getIdentifier(
                        "day",
                        "id",
                        "android"
                    )
                ).visibility =
                    View.GONE
            }
        }
        if (mindate > 0) dpd.datePicker.minDate = mindate
        if (maxDate > 0) dpd.datePicker.maxDate = maxDate

        //        dpd.getDatePicker().setMaxDate(calendar.getTimeInMillis());
        dpd.show()
        //   dpd.findViewById(getResources().getIdentifier("day","id","android")).setVisibility(View.GONE);
    }

    companion object {
        private const val ALL_PERMISSIONS_RESULT = 101
        fun getLastDayOfMonth(year: Int, month: Int): Int {
            val calendar = Calendar.getInstance()
            calendar[Calendar.YEAR] = year
            calendar[Calendar.MONTH] = month - 1
            // Set the date to the first day of the month
            calendar[Calendar.DAY_OF_MONTH] = 1

            // Get the last day of the month
            val lastDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)


            return lastDay
        }


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

