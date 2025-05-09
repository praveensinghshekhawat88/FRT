package com.callmangement.ehr.ehrActivities

import android.Manifest.permission
import android.animation.LayoutTransition
import android.annotation.TargetApi
import android.app.Activity
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.content.ContentValues
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Vibrator
import android.text.TextUtils
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.callmangement.R
import com.callmangement.databinding.EhrActivityMainBinding
import com.callmangement.ehr.api.APIClient.getRetrofitClientWithoutHeaders
import com.callmangement.ehr.api.APIInterface
import com.callmangement.ehr.ehrActivities.AttendanceListingActivity
import com.callmangement.ehr.ehrActivities.CampsListingActivity
import com.callmangement.ehr.ehrActivities.FeedbackFormActivity
import com.callmangement.ehr.ehrActivities.FeedbackFormListActivity
import com.callmangement.ehr.models.DashbordRoot
import com.callmangement.ehr.models.ModelDistrict
import com.callmangement.ehr.models.ModelDistrictList
import com.callmangement.ehr.models.ModelSEUser
import com.callmangement.ehr.models.ModelSEUserList
import com.callmangement.ehr.support.Utils.hideCustomProgressDialogCommonForAll
import com.callmangement.ehr.support.Utils.hideKeyboard
import com.callmangement.ehr.support.Utils.isNetworkAvailable
import com.callmangement.ehr.support.Utils.showCustomProgressDialogCommonForAll
import com.callmangement.ui.home.MainActivity
import com.callmangement.utils.PrefManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Collections
import java.util.Objects

class MainActivity : BaseActivity() {

    private var mActivity: Activity? = null
    private var mContext: Context? = null
    private var binding: EhrActivityMainBinding? = null
    private var swipeRefreshLayout: SwipeRefreshLayout? = null
    private var preference: PrefManager? = null
    private var permissionsToRequest: ArrayList<String>? = null
    private val permissionsRejected = ArrayList<String>()
    private val permissions = ArrayList<String>()
    private val LAUNCH_MARK_ATTENDANCE_ACTIVITY: Int = 333
    private var USER_TYPE_ID: String? = null
    private var presentYear: String? = null
    private var presentMonth: String? = null
    private var _maxMonth: String? = null
    private var month_namee: String? = null
    private var _selectedyear: Int = 0
    private var Month = ""
    private var Year = ""
    private var dis: String? = null
    private var spinnerDistrict: Spinner? = null
    private val spinner: Spinner? = null
    private val se_date_spinner: Spinner? = null
    private var spinnerSEUsers: Spinner? = null
    private val inputStartDate: EditText? = null
    private var DateSelector: EditText? = null
    private val startDate = ""
    private val endDate = ""
    private var districtId: String? = "0"
    private var checkDistrict = 0
    private var districtNameEng: String? = ""
    private var checkSEUser = 0
    private var SEUserNameEng: String? = ""
    private var SEUserId: String? = "0"
    private val s1: String? = null
    private val d1: String? = null
    private val d2: String? = null
    private val selecteduserid: String? = null
    private var USER_Id: String? = null
    private var district_List: MutableList<ModelDistrictList?> = ArrayList()
    private var SEUser_list: MutableList<ModelSEUserList?> = ArrayList()
    private val myCalendarToDate: Calendar = Calendar.getInstance()
    private val myCalendarFromDate: Calendar = Calendar.getInstance()
    private val checkFilter = 0
    private var layoutDateRange: LinearLayoutCompat? = null
    private val myFormat = "yyyy-MM-dd"
    private var curMonth: String? = null
    private var temp: Boolean = false
    private var arrowleft: ImageView? = null
    private var arrowright: ImageView? = null
    private var date_selected_year: Int = 0
    private var date_selected_month: Int = 0
    private var date_selected_day: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        binding = EhrActivityMainBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        init()
    }

    private fun init() {
        mActivity = this
        mContext = this
        preference = PrefManager(mContext!!)
        permissions.add(permission.ACCESS_FINE_LOCATION)
        permissions.add(permission.ACCESS_COARSE_LOCATION)
        swipeRefreshLayout = findViewById(R.id.refreshLayout)
        binding!!.linHavingAttendanceDtl.visibility = View.GONE
        setNormalPicker()
        // filterDialog();
        USER_Id = preference!!.useR_Id
        binding!!.actionBar.ivBack.visibility = View.VISIBLE
        binding!!.actionBar.ivThreeDot.visibility = View.GONE
        binding!!.actionBar.layoutLanguage.visibility = View.VISIBLE
        binding!!.actionBar.buttonPDF.visibility = View.GONE
        binding!!.actionBar.textToolbarTitle.text = "EHR"
        binding!!.textDestrict.visibility = View.GONE
        try {
            binding!!.textUsername.text = preference!!.useR_NAME
            binding!!.textEmail.text = preference!!.useR_EMAIL
            val USER_TYPE = preference!!.useR_TYPE
            val USER_TYPE_ID = preference!!.useR_TYPE_ID
            if (USER_TYPE_ID == "4" && USER_TYPE.equals("ServiceEngineer", ignoreCase = true)) {
                binding!!.textDestrict.visibility = View.VISIBLE
                binding!!.textDestrict.text =
                    resources.getString(R.string.district) + " : " + preference!!.useR_District
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        binding!!.layout.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)

        setClickListener()
    }

    fun expand(view: View) {
        val visibility = if (binding!!.layout1.visibility == View.GONE) View.VISIBLE else View.GONE
        TransitionManager.beginDelayedTransition(binding!!.layout, AutoTransition())
        binding!!.layout1.visibility = visibility
    }

    private fun setClickListener() {
        /*  binding.leftarrow.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View view) {
                       Calendar c = Calendar.getInstance();
                       Month = String.valueOf(c.get(Calendar.MONTH)+1);
       
                   }
               });
       
             binding.leftarrow.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View view) {
                       Calendar c = Calendar.getInstance();
                       Month = String.valueOf(c.get(Calendar.MONTH)-1);
       
                   }
               });*/

        /*   binding.actionBar.ivThreeDot.setOnClickListener(view -> {
            PopupMenu popupMenu = new PopupMenu(MainActivity.this, binding.actionBar.ivThreeDot);
            popupMenu.getMenuInflater().inflate(R.menu.menu, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(menuItem -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
                builder.setMessage(getResources().getString(R.string.do_you_want_to_logout_from_this_app))
                        .setCancelable(false)
                        .setPositiveButton(getResources().getString(R.string.logout), (dialog, id) -> {
                            dialog.cancel();
                            logout();
                        })
                        .setNegativeButton(getResources().getString(R.string.cancel), (dialog, id) -> dialog.cancel());
                AlertDialog alert = builder.create();
                alert.setTitle(getResources().getString(R.string.alert));
                alert.show();
                return true;
            });
            popupMenu.show();
        });*/

        temp = false // Initial state of the visibility flag

        binding!!.buttonComplaintTotal.setOnClickListener {
            binding!!.linHavingAttendanceDtl.visibility = View.VISIBLE
            if (!temp) {
                binding!!.linHavingAttendanceDtl.visibility = View.VISIBLE
            } else {
                binding!!.linHavingAttendanceDtl.visibility = View.GONE
            }
            temp = !temp
        }
        binding!!.actionBar.ivBack.setOnClickListener {
            val i = Intent(
                this@MainActivity,
                MainActivity::class.java
            )
            startActivity(i)
        }

        binding!!.buttonOrganiseCamp.visibility = View.GONE
        binding!!.buttonManageSurvey.visibility = View.GONE
        binding!!.buttonManageAttendance.visibility = View.GONE
        binding!!.linHavingAttendanceAndCampCount.visibility = View.GONE
        val USER_TYPE = preference!!.useR_TYPE
        USER_TYPE_ID = preference!!.useR_TYPE_ID
        USER_Id = preference!!.useR_Id
        Log.d("usertypeid", " $USER_TYPE_ID")
        if (USER_TYPE_ID == "1" && USER_TYPE.equals("Admin", ignoreCase = true)) {
            binding!!.buttonOrganiseCamp.visibility = View.VISIBLE
            binding!!.buttonManageAttendance.visibility = View.VISIBLE

            binding!!.textManageCamps.text = "View Camp"
            binding!!.textManageAttendance.text = "View Attendance"
            binding!!.buttonMarkAttendance.visibility = View.GONE
            binding!!.llDisUser.visibility = View.VISIBLE
            binding!!.buttonFeedbackform.visibility = View.GONE
            binding!!.buttonFeedbackformlist.visibility = View.VISIBLE

            binding!!.linHavingAttendanceAndCampCount.visibility = View.VISIBLE
            val c = Calendar.getInstance()
            val year = c[Calendar.YEAR].toString()
            curMonth = (c[Calendar.MONTH] + 1).toString()
            Month = curMonth!!
            Year = year
            SEUserId = "5"

            filterDialog()
            dashboardApi(USER_Id, Month, Year, SEUserId)
        } else if (USER_TYPE_ID == "2" && USER_TYPE.equals(
                "Manager",
                ignoreCase = true
            ) || USER_TYPE_ID == "16" && USER_TYPE.equals("HRManager", ignoreCase = true)
        ) {
            binding!!.buttonOrganiseCamp.visibility = View.VISIBLE
            binding!!.buttonManageAttendance.visibility = View.VISIBLE
            binding!!.textManageCamps.text = "View Camp"
            binding!!.textManageAttendance.text = "View Attendance"
            binding!!.buttonMarkAttendance.visibility = View.GONE
            binding!!.llDisUser.visibility = View.VISIBLE
            binding!!.linHavingAttendanceAndCampCount.visibility = View.VISIBLE
            binding!!.buttonTrainingFilter.visibility = View.VISIBLE
            binding!!.buttonFeedbackform.visibility = View.GONE
            binding!!.buttonFeedbackformlist.visibility = View.VISIBLE
            val c = Calendar.getInstance()
            val year = c[Calendar.YEAR].toString()
            curMonth = (c[Calendar.MONTH] + 1).toString()
            Month = curMonth!!
            Year = year
            SEUserId = "5"
            filterDialog()
            dashboardApi(USER_Id, Month, Year, SEUserId)
        } else if (USER_TYPE_ID == "4" && USER_TYPE.equals("ServiceEngineer", ignoreCase = true)) {
            binding!!.buttonOrganiseCamp.visibility = View.VISIBLE
            binding!!.buttonManageAttendance.visibility = View.VISIBLE
            binding!!.textManageCamps.text = "Arrange Camp"
            binding!!.textManageAttendance.text = "View Attendance"
            binding!!.buttonManageSurvey.visibility = View.VISIBLE
            binding!!.textManageSurvey.text = "Manage Survey Form"
            binding!!.linHavingAttendanceAndCampCount.visibility = View.VISIBLE
            binding!!.buttonMarkAttendance.visibility = View.VISIBLE
            binding!!.llDisUser.visibility = View.GONE
            binding!!.buttonFeedbackform.visibility = View.VISIBLE
            binding!!.buttonFeedbackformlist.visibility = View.VISIBLE
            filterDialog()
            val c = Calendar.getInstance()
            val year = c[Calendar.YEAR].toString()
            curMonth = (c[Calendar.MONTH] + 1).toString()
            Month = curMonth!!
            Year = year
            SEUserId = USER_Id
            dashboardApi(USER_Id, Month, Year, SEUserId)
        } else if (USER_TYPE_ID == "3" && USER_TYPE.equals("Support", ignoreCase = true)) {
            // binding.buttonOrganiseCamp.setVisibility(View.VISIBLE);
            binding!!.buttonManageAttendance.visibility = View.VISIBLE
            //binding.textManageCamps.setText("Arrange Camp");
            binding!!.textManageAttendance.text = "View Attendance"
            binding!!.linHavingAttendanceAndCampCount.visibility = View.VISIBLE
            binding!!.buttonMarkAttendance.visibility = View.GONE
            binding!!.llDisUser.visibility = View.GONE
            binding!!.buttonFeedbackform.visibility = View.VISIBLE
            binding!!.buttonFeedbackformlist.visibility = View.VISIBLE
            filterDialog()
            val c = Calendar.getInstance()
            val year = c[Calendar.YEAR].toString()
            curMonth = (c[Calendar.MONTH] + 1).toString()
            Month = curMonth!!
            Year = year
            SEUserId = USER_Id
            dashboardApi(USER_Id, Month, Year, SEUserId)
        } else if (USER_TYPE_ID == "11" && USER_TYPE.equals("SurveyUser", ignoreCase = true)) {
            binding!!.buttonManageSurvey.visibility = View.VISIBLE
            binding!!.textManageSurvey.text = "Manage Survey Form"
            binding!!.buttonMarkAttendance.visibility = View.GONE
            binding!!.filter.visibility = View.GONE
        }
        val cal = Calendar.getInstance()
        val month_date = SimpleDateFormat("MMMM")
        val monthnum = Month.toInt() - 1
        cal[Calendar.MONTH] = monthnum
        val month_name = month_date.format(cal.time)
        //    Log.e("", month_name);
        val ed_monthYear = "--$month_name  $Year--"
        binding!!.dateSelt.setText(ed_monthYear)
        binding!!.buttonOrganiseCamp.setOnClickListener { view: View? ->
            val intent = Intent(mActivity, CampsListingActivity::class.java)
            startActivity(intent)
        }
        binding!!.buttonManageSurvey.setOnClickListener { view: View? ->
            val intent = Intent(
                mActivity,
                SurveyFormsListingActivity::class.java
            )
            startActivity(intent)
        }
        binding!!.buttonManageAttendance.setOnClickListener { view: View? ->
            val intent = Intent(mActivity, AttendanceListingActivity::class.java)
            //   Intent intent = new Intent(mActivity, AttendanceListingActivityTwo.class);
            intent.putExtra("YEAR", date_selected_year)
            intent.putExtra("MONTH", date_selected_month)
            intent.putExtra("DATE", date_selected_day)
            startActivity(intent)
        }
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

        binding!!.buttonFeedbackform.setOnClickListener {
            val vibrator = getSystemService(VIBRATOR_SERVICE) as Vibrator
            vibrator.vibrate(100)
            val i = Intent(this@MainActivity, FeedbackFormActivity::class.java)
            startActivity(i)
        } // Check if the device supports vibration

        binding!!.buttonFeedbackformlist.setOnClickListener {
            val vibrator = getSystemService(VIBRATOR_SERVICE) as Vibrator
            vibrator.vibrate(100)
            val i = Intent(this@MainActivity, FeedbackFormListActivity::class.java)
            i.putExtra("key_districtId", districtId)
            i.putExtra("key_fromDate", startDate)
            i.putExtra("key_toDate", endDate)
            i.putExtra("key_selectedDate", date_selected_day.toString())
            i.putExtra("key_selectedDis", preference!!.useR_District)
            startActivity(i)
        } // Check if the device supports vibration

        binding!!.buttonPMReport.visibility = View.GONE

        binding!!.buttonPMReport.setOnClickListener {
            val vibrator = getSystemService(VIBRATOR_SERVICE) as Vibrator
            vibrator.vibrate(100)
            val i = Intent(this@MainActivity, FeedbackReportActivity::class.java)
            i.putExtra("key_districtId", districtId)
            i.putExtra("key_fromDate", startDate)
            i.putExtra("key_toDate", endDate)
            i.putExtra("key_selectedDate", date_selected_day)
            i.putExtra("key_selectedDis", preference!!.useR_District)
            startActivity(i)
        } // Check if the device supports vibration

        swipeRefreshLayout!!.setOnRefreshListener {
            swipeRefreshLayout!!.isRefreshing = false
            /* Calendar c = Calendar.getInstance();
                               String year = String.valueOf(c.get(Calendar.YEAR));
                               curMonth = String.valueOf(c.get(Calendar.MONTH) + 1);
                               Month = curMonth;
                               Year = year;
                               SEUserId = USER_Id;
                               dashboardApi(USER_Id, Month, Year, SEUserId);*/
            filterNow()
        }
    }

    fun makeToast(string: String?) {
        if (TextUtils.isEmpty(string)) return
        Toast.makeText(mActivity, string, Toast.LENGTH_SHORT).show()
    }

    private fun dashboardApi(USER_Id: String?, Month: String, Year: String, SEUserId: String?) {
//Progress Bar while connection establishes
        var USER_Id = USER_Id
        if (isNetworkAvailable(mActivity!!)) {
            hideKeyboard(mActivity!!)
            //  Utils.showCustomProgressDialogCommonForAll(mActivity, getResources().getString(R.string.please_wait));
            binding!!.progressBar.visibility = View.VISIBLE
            USER_Id = preference!!.useR_Id
            Log.d("dashboardapi", " $USER_Id   $Month   $Year   $SEUserId")
            val apiInterface = getRetrofitClientWithoutHeaders(
                mActivity!!,
                BaseUrl()!!
            ).create(
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
                                    binding!!.textcreatedCampCount.text = CreatedCampCount
                                    binding!!.textCampsCount.text = OrganizedCampCount
                                    binding!!.textAttendanceCount.text = AttendanceCount
                                    binding!!.textAttendanceprst.text = presentCount
                                    binding!!.textAbsent.text = abstCount
                                    binding!!.texthalfday.text = halfdayCount
                                    binding!!.textLeave.text = leaveCount
                                    binding!!.textTour.text = tourCount

                                    Log.d(
                                        "ghmn",
                                        "asmbnmldjfjas$CreatedCampCount  $OrganizedCampCount $AttendanceCount"
                                    )
                                } else {
                                    val dialog = AlertDialog.Builder(this@MainActivity)
                                    dialog.setCancelable(false)
                                    // dialog.setTitle("Dialog on Android");
                                    dialog.setMessage("Month And Year Should Be Less Than Current Month And Year")
                                    dialog.setPositiveButton(
                                        "Ok"
                                    ) { dialog, id -> //Action for "Delete".
                                        dialog.dismiss()
                                    }

                                    val alert = dialog.create()
                                    alert.show()
                                    //    makeToast(String.valueOf(response.body().getMessage()));
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

    override fun onBackPressed() {
        val i = Intent(this@MainActivity, MainActivity::class.java)
        startActivity(i)
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

    private fun filterDialog() {
        try {
            spinnerDistrict = findViewById(R.id.spinnerDistrict)
            spinnerSEUsers = findViewById(R.id.spinnerSEUsers)

            val inputTrainingNumber = findViewById<EditText>(R.id.inputTrainingNumber)
            val buttonTrainingFilter = findViewById<Button>(R.id.buttonTrainingFilter)
            //  Log.d("jgjgk", d1);
            // Log.d("kjkkgk", d2);
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

            //  spinnerSEUsers.setSelection(1);
            spinnerDistrict!!.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    adapterView: AdapterView<*>,
                    view: View,
                    i: Int,
                    l: Long
                ) {
                    if (++checkDistrict > 1) {
                        val item = adapterView.getItemAtPosition(i).toString()


                        //  if (!item.equalsIgnoreCase("--" + getResources().getString(R.string.district) + "--") ){
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

                            val dialog = AlertDialog.Builder(this@MainActivity)
                            dialog.setCancelable(false)
                            // dialog.setTitle("Dialog on Android");
                            dialog.setMessage("Please Select User. Which User's Count Do you Want?")
                            dialog.setPositiveButton(
                                "Ok"
                            ) { dialog, id -> //Action for "Delete".
                                dialog.dismiss()
                            }


                            val alert = dialog.create()
                            alert.show()

                            binding!!.textcreatedCampCount.text = "---"
                            binding!!.textCampsCount.text = "---"
                            binding!!.textAttendanceCount.text = "---"
                            binding!!.textAttendanceprst.text = "---"
                            binding!!.textAbsent.text = "---"
                            binding!!.texthalfday.text = "---"
                            binding!!.textLeave.text = "---"
                            binding!!.textTour.text = "---"

                            //  spinnerSEUsers.setSelection(1);

                            /*   if (USER_TYPE_ID.equals("1") || USER_TYPE_ID.equals("2") || USER_TYPE_ID.equals("16")&&) {
                                        spinnerSEUsers.setSelection(1);

                                    }*/

                            //   spinnerSEUsers.setSelection(1);

                            // SEUsersList(districtId);

                            //Toast.makeText(MainActivity.this, "No possible", Toast.LENGTH_SHORT).show();
                        }
                        SEUsersList(districtId)

                        //   }
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
                filterNow()
            }


            //   dashboardApi(  USER_Id,  Month ,  Year,SEUserId);

            // dialog.show();
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun filterNow() {
        if (SEUserId == "-1") {
            //    Toast.makeText(mActivity, "Please Select User", Toast.LENGTH_SHORT).show();


            val dialog = AlertDialog.Builder(this@MainActivity)
            dialog.setCancelable(false)
            // dialog.setTitle("Dialog on Android");
            dialog.setMessage("Please Select User. Which User's Count Do you Want?")
            dialog.setPositiveButton(
                "Ok"
            ) { dialog, id -> //Action for "Delete".
                dialog.dismiss()
            }


            val alert = dialog.create()
            alert.show()
        } else {
            val UserIdd = SEUserId!!.toInt()
            Log.d("userid", "" + UserIdd)

            Log.d("districtList", "$districtNameEng  $SEUserNameEng")

            Log.d("startDate_endDate", "$startDate  $endDate")
            Log.d("RESUT--", "$USER_Id  $Month $Year  $SEUserId")

            dashboardApi(USER_Id, Month, Year, SEUserId)
        }
    }

    private fun SEUsersList(districtId: String?) {
        Log.d("idddddd", " $districtId")
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
                    if (response.isSuccessful) {
                        if (response.code() == 200) {
                            if (response.body() != null) {
                                if (response.body()!!.status == "200") {
                                    Log.d(
                                        "dataaaaaaa",
                                        " " + response.body()!!.sEUsersList.toString()
                                    )
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

                                        if (USER_TYPE_ID == "1" || USER_TYPE_ID == "2" || USER_TYPE_ID == "16") {
                                            spinnerSEUsers!!.setSelection(1)
                                        }
                                        //   spinnerSEUsers.setSelection(1);
                                        Log.d("userid", " $selecteduserid")
                                        if (selecteduserid != null && !selecteduserid.isEmpty()) {
                                            Log.d("ghjh", " $selecteduserid")
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
                                    } else {
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
                                    Log.d("disssssssss", response.body().toString())
                                    district_List = response.body()!!.district_List
                                    if (district_List != null && district_List.size > 0) {
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
                                        spinnerDistrict!!.setSelection(1)
                                        SEUsersList(district_List[spinnerDistrict!!.selectedItemPosition]!!.districtId)
                                        if (USER_TYPE_ID == "1" || USER_TYPE_ID == "2" || USER_TYPE_ID == "16") {
                                            spinnerDistrict!!.setSelection(1)
                                        }

                                        //   spinnerDistrict.setSelection(1);

                                        /*if(d2!=null)
                                        {
                                            spinnerDistrict.setSelection(Integer.parseInt(d2));
                                        }*/
                                        if (d2 != null && d2.isNotEmpty()) {
                                            Log.d("ghjh", d2)
                                            val userId =
                                                d2.toInt() // replace with the user ID you want to select
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
                    makeToast(resources.getString(R.string.error))
                    call.cancel()
                }
            })
        } else {
            makeToast(resources.getString(R.string.no_internet_connection))
        }
    }

    private fun setNormalPicker() {
        val calendar = Calendar.getInstance()
        date_selected_year = calendar[Calendar.YEAR]
        date_selected_month = calendar[Calendar.MONTH]
        date_selected_day = calendar[Calendar.DAY_OF_MONTH]
        //  setContentView(R.layout.activity_main);
        val today = Calendar.getInstance()
        findViewById<View>(R.id.date_selt).setOnClickListener {
            val minDate = "1609439400000".toLong()
            val c = Calendar.getInstance()
            val maxDate = c.timeInMillis
            DatePickerDialog(mContext!!, binding!!.dateSelt, minDate, maxDate)
            //        showYearMonthPicker();
        }
    }

    var calendar: Calendar = Calendar.getInstance()

    private fun changeMonth(delta: Int) {
        calendar.add(Calendar.MONTH, delta)
        val year = Year.toInt()
        val month = Month.toInt()
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
                Log.d(
                    ContentValues.TAG,
                    "selectedMonth : $monthOfYear selectedYear : $year"
                )
                // TODO Auto-generated method stub
                val formatter = DecimalFormat("00")
                val date = formatter.format(dayOfMonth.toLong())
                val month = formatter.format((monthOfYear + 1).toLong())
                Month = month
                Log.d("sdsknljf", "gh" + (year + 1))
                Year = year.toString()
                val cal = Calendar.getInstance()
                val month_date = SimpleDateFormat("MMMM")
                val monthnum = Month.toInt() - 1
                cal[Calendar.MONTH] = monthnum
                month_namee = month_date.format(cal.time)
                _selectedyear = year
                Log.e(">>>>>", "" + _selectedyear)
                Log.e(">>>>>", "" + monthnum)
                val ed_monthYear = "--$month_namee  $Year--"
                if (v is Button) {
                    //                    btn.setText(date + "/" + month + "/" + year);
                    //                    btn.setTag(date + "/" + month + "/" + year);
                    v.text = ed_monthYear
                } else if (v is EditText) {
                    //                    txt.setText(date + "/" + month + "/" + year);
                    //                    txt.setTag(date + "/" + month + "/" + year);
                    v.setText(ed_monthYear)
                }
                binding!!.buttonTrainingFilter.performClick()
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
    }
}
