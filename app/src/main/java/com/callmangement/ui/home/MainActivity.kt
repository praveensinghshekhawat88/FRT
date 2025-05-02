package com.callmangement.ui.home

import android.Manifest
import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.CompoundButton
import android.widget.DatePicker
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.PopupMenu
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
import com.callmangement.R
import com.callmangement.custom.CustomActivity
import com.callmangement.databinding.ActivityMainBinding
import com.callmangement.firebase.FirebaseUtils.unregisterTopic
import com.callmangement.model.complaints.ModelComplaintsCount
import com.callmangement.model.complaints.ModelComplaintsCountData
import com.callmangement.model.district.ModelDistrict
import com.callmangement.model.district.ModelDistrictList
import com.callmangement.model.login.ModelLogin
import com.callmangement.model.logout.ModelLogout
import com.callmangement.model.tehsil.ModelTehsil
import com.callmangement.model.tehsil.ModelTehsilList
import com.callmangement.network.APIService
import com.callmangement.network.RetrofitInstance.retrofitInstance
import com.callmangement.support.charting.animation.Easing
import com.callmangement.support.charting.data.Entry
import com.callmangement.support.charting.data.PieData
import com.callmangement.support.charting.data.PieDataSet
import com.callmangement.support.charting.data.PieEntry
import com.callmangement.support.charting.formatter.PercentFormatter
import com.callmangement.support.charting.highlight.Highlight
import com.callmangement.support.charting.listener.OnChartValueSelectedListener
import com.callmangement.support.charting.utils.MPPointF
import com.callmangement.support.permissions.PermissionHandler
import com.callmangement.support.permissions.Permissions
import com.callmangement.tracking_service.GpsUtils
import com.callmangement.ui.attendance.AttendanceDetailsActivity
import com.callmangement.ui.biometric_delivery.BiometricDeliveryDashboardActivity
import com.callmangement.ui.closed_guard_delivery.ClosedGuardDeliveryActivity
import com.callmangement.ui.complaint.ChallanUploadComplaintPendingListActivity
import com.callmangement.ui.complaint.ComplaintPendingListActivity
import com.callmangement.ui.complaint.ComplaintResolveListActivity
import com.callmangement.ui.complaint.ComplaintViewModel
import com.callmangement.ui.complaint.EditChallanComplaintListActivity
import com.callmangement.ui.complaint.SendToSECenterListActivity
import com.callmangement.ui.complaint.TotalComplaintListActivity
import com.callmangement.ui.complaints_fps_wise.LastComplaintFPSListActivity
import com.callmangement.ui.distributor.activity.DistributorPosReportForSEActivity
import com.callmangement.ui.distributor.activity.PosDistributionActivity
import com.callmangement.ui.errors.activity.ErrorPosActivity
import com.callmangement.ui.ins_weighing_scale.activity.WeighingScaleDashboard
import com.callmangement.ui.inventory.InventoryActivity
import com.callmangement.ui.iris_derivery_installation.IrisDeliveryInstallationDashboard
import com.callmangement.ui.login.LoginActivity
import com.callmangement.ui.pos_help.activity.FAQListActivity
import com.callmangement.ui.pos_issue.activity.PosIssueActivity
import com.callmangement.ui.reports.ReportsActivity
import com.callmangement.ui.reset_device.ResetDeviceActivity
import com.callmangement.ui.training_schedule.TrainingScheduleListActivity
import com.callmangement.ui.training_schedule.TrainingScheduleListForSEActivity
import com.callmangement.utils.PrefManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Collections
import java.util.Locale
import java.util.Objects

class MainActivity : CustomActivity(), OnChartValueSelectedListener, View.OnClickListener,
    OnRefreshListener {
    private var binding: ActivityMainBinding? = null
    private var prefManager: PrefManager? = null
    private var myLocale: Locale? = null
    private val myCalendarFromDate: Calendar = Calendar.getInstance()
    private val myCalendarToDate: Calendar = Calendar.getInstance()
    private val spinnerList: MutableList<String> = ArrayList()
    private val myFormat = "yyyy-MM-dd"
    private var checkFilter = 0
    private var checkDistrict = 0
    private var districtNameEng: String? = ""
    private var filterType = ""
    private var viewModel: ComplaintViewModel? = null
    private var Tehsil_List: List<ModelTehsilList?>? = ArrayList()
    private var district_List: MutableList<ModelDistrictList>? = ArrayList()
    private var entries = ArrayList<PieEntry>()
    val MATERIAL_COLORS: IntArray = intArrayOf(rgb("#f2726f"), rgb("#29c3be"), rgb("#5d62b5"))
    private var districtId: String? = "0"
    private val IsSE_PunchIN = "false"
    private var backgroundLocationPermissionApproved = 0
    private var isGPS = false
    private var checkPermissionTypeStr = "initial"
    var globalId: Int = -1
    var globalEntryValue: Entry? = null
    lateinit var permissions: Array<String>

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding!!.actionBar.ivBack.visibility = View.GONE
        binding!!.actionBar.ivThreeDot.visibility = View.VISIBLE
        binding!!.actionBar.layoutLanguage.visibility = View.VISIBLE
        binding!!.actionBar.buttonPDF.visibility = View.GONE
        binding!!.actionBar.textToolbarTitle.text = resources.getString(R.string.app_name)
        viewModel = ViewModelProviders.of(this).get(
            ComplaintViewModel::class.java
        )
        prefManager = PrefManager(mContext!!)
        binding!!.textUsername.text = prefManager!!.uSER_NAME
        binding!!.textEmail.text = prefManager!!.uSER_EMAIL
        districtNameEng = "--" + resources.getString(R.string.district) + "--"
        initView()
    }

    private fun initView() {
        checkPermission()
        setUpClickListener()
        manageLayout()
        districtList()
        setUpData()
        clickListener()
        fetchDataByFilterType()
    }

    private fun setUpData() {
        filterType = "--" + resources.getString(R.string.select_filter) + "--"
        spinnerList.add("--" + resources.getString(R.string.select_filter) + "--")
        spinnerList.add(resources.getString(R.string.today))
        spinnerList.add(resources.getString(R.string.yesterday))
        spinnerList.add(resources.getString(R.string.current_month))
        spinnerList.add(resources.getString(R.string.previous_month))
        spinnerList.add(resources.getString(R.string.custom_filter))
        val dataAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, spinnerList)
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding!!.spinner.adapter = dataAdapter



        if (prefManager!!.uSER_TYPE_ID == "1" || prefManager!!.uSER_TYPE_ID == "2") {
            binding!!.spinner.setSelection(3)
            val selectedItemPosition = binding!!.spinner.selectedItemPosition
            if (selectedItemPosition == 3) {
                filterType = resources.getString(R.string.current_month)
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

                fetchComplaintListByCurrentMonthAndPreviousMonth(districtId, date1, date2)
            } else {
            }
        } else {
        }
    }

    @SuppressLint("SetTextI18n")
    private fun manageLayout() {
        if (prefManager!!.uSER_Change_Language != "") {
            if (prefManager!!.uSER_Change_Language == "en") {
                binding!!.actionBar.ivEngToHindi.visibility = View.VISIBLE
                binding!!.actionBar.ivHindiToEng.visibility = View.GONE
                prefManager!!.uSER_Change_Language = "en"
            } else {
                binding!!.actionBar.ivEngToHindi.visibility = View.GONE
                binding!!.actionBar.ivHindiToEng.visibility = View.VISIBLE
                prefManager!!.uSER_Change_Language = "hi"
            }
        } else {
            binding!!.actionBar.ivEngToHindi.visibility = View.VISIBLE
            binding!!.actionBar.ivHindiToEng.visibility = View.GONE
        }

        if (prefManager!!.uSER_TYPE_ID == "1" && prefManager!!.uSER_TYPE.equals(
                "Admin",
                ignoreCase = true
            )
        ) {
            binding!!.buttonInventorySE.visibility = View.GONE
            binding!!.buttonPosDistributionReportForSE.visibility = View.GONE
            binding!!.llTrainingScheduleSE.visibility = View.GONE
            binding!!.buttonAttendanceBlank.visibility = View.GONE
            binding!!.buttonAttendance.visibility = View.VISIBLE
            binding!!.llResetDeviceAndFpsDetails.visibility = View.VISIBLE
            binding!!.llInventoryAndPosDistributionReport.visibility = View.VISIBLE
            binding!!.llPosIssueAndTrainingScheduleForManager.visibility = View.GONE
            binding!!.llPosErrorShow.visibility = View.VISIBLE
            binding!!.space.visibility = View.GONE
            binding!!.llInstallationWeigingScale.visibility = View.VISIBLE
            binding!!.lltIrisDeliveryInstallation.visibility = View.VISIBLE
            binding!!.lltBiometricDeliveryDashboard.visibility = View.VISIBLE
            binding!!.lltClosedGuardDelivery.visibility = View.GONE

            //  binding.rstSpace.setVisibility(View.GONE);
            //  binding.posSpace.setVisibility(View.GONE);
        } else if (prefManager!!.uSER_TYPE_ID == "2" && prefManager!!.uSER_TYPE.equals(
                "Manager",
                ignoreCase = true
            )
        ) {
            binding!!.buttonInventorySE.visibility = View.GONE
            binding!!.buttonPosDistributionReportForSE.visibility = View.GONE
            binding!!.llTrainingScheduleSE.visibility = View.GONE
            binding!!.buttonAttendanceBlank.visibility = View.GONE
            binding!!.buttonAttendance.visibility = View.VISIBLE
            binding!!.llResetDeviceAndFpsDetails.visibility = View.VISIBLE
            binding!!.llInventoryAndPosDistributionReport.visibility = View.VISIBLE
            binding!!.llPosIssueAndTrainingScheduleForManager.visibility = View.GONE
            binding!!.llPosErrorShow.visibility = View.VISIBLE
            binding!!.space.visibility = View.GONE
            binding!!.llInstallationWeigingScale.visibility = View.VISIBLE
            binding!!.lltIrisDeliveryInstallation.visibility = View.VISIBLE
            binding!!.lltBiometricDeliveryDashboard.visibility = View.VISIBLE
            binding!!.lltClosedGuardDelivery.visibility = View.GONE


            //  binding.rstSpace.setVisibility(View.GONE);
            //  binding.posSpace.setVisibility(View.GONE);
        } else if (prefManager!!.uSER_TYPE_ID == "4" && prefManager!!.uSER_TYPE.equals(
                "ServiceEngineer",
                ignoreCase = true
            )
        ) {
            binding!!.buttonInventorySE.visibility = View.VISIBLE
            binding!!.rstSpace.visibility = View.GONE
            binding!!.buttonPosDistributionReportForSE.visibility = View.VISIBLE
            binding!!.llTrainingScheduleSE.visibility = View.GONE
            binding!!.buttonAttendanceBlank.visibility = View.GONE
            binding!!.buttonAttendance.visibility = View.GONE
            binding!!.llResetDeviceAndFpsDetails.visibility = View.GONE
            binding!!.llInventoryAndPosDistributionReport.visibility = View.GONE
            binding!!.llPosIssueAndTrainingScheduleForManager.visibility = View.GONE
            binding!!.space.visibility = View.VISIBLE
            binding!!.llPosErrorShow.visibility = View.VISIBLE
            binding!!.llInstallationWeigingScale.visibility = View.VISIBLE
            binding!!.lltIrisDeliveryInstallation.visibility = View.VISIBLE
            binding!!.lltBiometricDeliveryDashboard.visibility = View.VISIBLE
            binding!!.lltClosedGuardDelivery.visibility = View.VISIBLE
            binding!!.posSpace.visibility = View.GONE
            binding!!.hlpSpace.visibility = View.GONE
        } else if (prefManager!!.uSER_TYPE_ID == "5" && prefManager!!.uSER_TYPE.equals(
                "ServiceCentre",
                ignoreCase = true
            )
        ) {
            binding!!.buttonInventorySE.visibility = View.VISIBLE
            binding!!.rstSpace.visibility = View.GONE

            binding!!.buttonPosDistributionReportForSE.visibility = View.GONE
            binding!!.llTrainingScheduleSE.visibility = View.GONE
            binding!!.buttonAttendanceBlank.visibility = View.GONE
            binding!!.buttonAttendance.visibility = View.GONE
            binding!!.llResetDeviceAndFpsDetails.visibility = View.GONE
            binding!!.llInventoryAndPosDistributionReport.visibility = View.GONE
            binding!!.llPosIssueAndTrainingScheduleForManager.visibility = View.GONE
            binding!!.llPosErrorShow.visibility = View.GONE
            binding!!.llInstallationWeigingScale.visibility = View.GONE
            binding!!.lltIrisDeliveryInstallation.visibility = View.GONE
            binding!!.lltBiometricDeliveryDashboard.visibility = View.GONE
            binding!!.lltClosedGuardDelivery.visibility = View.GONE
            binding!!.posSpace.visibility = View.GONE
            binding!!.hlpSpace.visibility = View.GONE
        } else if (prefManager!!.uSER_TYPE_ID.equals(
                "6",
                ignoreCase = true
            ) && prefManager!!.uSER_TYPE.equals("DSO", ignoreCase = true)
        ) {
            binding!!.buttonInventorySE.visibility = View.GONE
            binding!!.buttonPosDistributionReportForSE.visibility = View.GONE
            binding!!.llTrainingScheduleSE.visibility = View.GONE
            binding!!.buttonAttendanceBlank.visibility = View.GONE
            binding!!.buttonAttendance.visibility = View.GONE
            binding!!.llResetDeviceAndFpsDetails.visibility = View.GONE
            binding!!.llInventoryAndPosDistributionReport.visibility = View.GONE
            binding!!.llPosIssueAndTrainingScheduleForManager.visibility = View.GONE
            binding!!.llPosErrorShow.visibility = View.GONE
            binding!!.llInstallationWeigingScale.visibility = View.GONE
            binding!!.lltIrisDeliveryInstallation.visibility = View.GONE
            binding!!.lltBiometricDeliveryDashboard.visibility = View.GONE
            binding!!.lltClosedGuardDelivery.visibility = View.GONE
            //    binding.rstSpace.setVisibility(View.GONE);
            binding!!.posSpace.visibility = View.GONE
            binding!!.hlpSpace.visibility = View.GONE
        }

        if (prefManager!!.uSER_TYPE_ID == "4" && prefManager!!.uSER_TYPE.equals(
                "ServiceEngineer",
                ignoreCase = true
            )
        ) {
            binding!!.rlDistrict.visibility = View.GONE
            binding!!.spacer.visibility = View.GONE
            binding!!.textDestrict.visibility = View.VISIBLE
            binding!!.textDestrict.text =
                resources.getString(R.string.district) + " : " + prefManager!!.uSER_District
            districtId = prefManager!!.uSER_DistrictId
            tehsilList(prefManager!!.uSER_DistrictId)
        } else if (prefManager!!.uSER_TYPE_ID.equals(
                "6",
                ignoreCase = true
            ) && prefManager!!.uSER_TYPE.equals("DSO", ignoreCase = true)
        ) {
            binding!!.rlDistrict.visibility = View.GONE
            binding!!.spacer.visibility = View.GONE
            binding!!.textDestrict.visibility = View.VISIBLE
            binding!!.textDestrict.text =
                resources.getString(R.string.district) + " : " + prefManager!!.uSER_District

            districtId = prefManager!!.uSER_DistrictId
            tehsilList(prefManager!!.uSER_DistrictId)
        } else {
            binding!!.textDestrict.visibility = View.GONE
            binding!!.rlDistrict.visibility = View.VISIBLE
            binding!!.spacer.visibility = View.VISIBLE
            tehsilList("0")
        }
    }

    private fun clickListener() {
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
                            filterType = resources.getString(R.string.today)
                            binding!!.layoutDateRange.visibility = View.GONE

                            val calendar = Calendar.getInstance()
                            val today = calendar.time
                            val sdf = SimpleDateFormat(myFormat, Locale.US)
                            val todayDate = sdf.format(today)
                            fetchComplaintListBySelect(districtId, todayDate)
                        } else if (item.equals(
                                resources.getString(R.string.yesterday),
                                ignoreCase = true
                            )
                        ) {
                            filterType = resources.getString(R.string.yesterday)
                            binding!!.layoutDateRange.visibility = View.GONE
                            val calendar = Calendar.getInstance()
                            calendar.add(Calendar.DAY_OF_YEAR, -1)
                            val yesterday = calendar.time
                            val sdf = SimpleDateFormat(myFormat, Locale.US)
                            val yesterdayDate = sdf.format(yesterday)
                            fetchComplaintListBySelect(districtId, yesterdayDate)
                        } else if (item.equals(
                                resources.getString(R.string.current_month),
                                ignoreCase = true
                            )
                        ) {
                            filterType = resources.getString(R.string.current_month)
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

                            fetchComplaintListByCurrentMonthAndPreviousMonth(
                                districtId,
                                date1,
                                date2
                            )
                        } else if (item.equals(
                                resources.getString(R.string.previous_month),
                                ignoreCase = true
                            )
                        ) {
                            filterType = resources.getString(R.string.previous_month)
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

                            fetchComplaintListByCurrentMonthAndPreviousMonth(
                                districtId,
                                date1,
                                date2
                            )
                        } else if (item.equals(
                                resources.getString(R.string.custom_filter),
                                ignoreCase = true
                            )
                        ) {
                            filterType = resources.getString(R.string.custom_filter)
                            binding!!.layoutDateRange.visibility = View.VISIBLE
                        }
                    } else {
                        filterType = "--" + resources.getString(R.string.select_filter) + "--"
                        Objects.requireNonNull(binding!!.textFromDate.text)!!.clear()
                        Objects.requireNonNull(binding!!.textToDate.text)!!.clear()
                        binding!!.layoutDateRange.visibility = View.GONE
                        fetchComplaintListBySelect(districtId, "")
                    }
                }
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {
            }
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
                        districtId = district_List!![i]!!.districtId
                        fetchDataByFilterType()
                    }
                }

                override fun onNothingSelected(adapterView: AdapterView<*>?) {
                }
            }

        val dateFromDate =
            OnDateSetListener { view: DatePicker?, year: Int, monthOfYear: Int, dayOfMonth: Int ->
                myCalendarFromDate[Calendar.YEAR] = year
                myCalendarFromDate[Calendar.MONTH] = monthOfYear
                myCalendarFromDate[Calendar.DAY_OF_MONTH] = dayOfMonth
                updateLabelFromDate()
            }

        val dateToDate =
            OnDateSetListener { view: DatePicker?, year: Int, monthOfYear: Int, dayOfMonth: Int ->
                myCalendarToDate[Calendar.YEAR] = year
                myCalendarToDate[Calendar.MONTH] = monthOfYear
                myCalendarToDate[Calendar.DAY_OF_MONTH] = dayOfMonth
                updateLabelToDate()
            }

        binding!!.textFromDate.setOnClickListener { view: View? ->
            val datePickerDialog = DatePickerDialog(
                mContext!!,
                dateFromDate,
                myCalendarFromDate[Calendar.YEAR],
                myCalendarFromDate[Calendar.MONTH],
                myCalendarFromDate[Calendar.DAY_OF_MONTH]
            )
            datePickerDialog.datePicker.maxDate = System.currentTimeMillis() - 1000
            datePickerDialog.show()
        }

        binding!!.textToDate.setOnClickListener { view: View? ->
            val datePickerDialog = DatePickerDialog(
                mContext!!,
                dateToDate,
                myCalendarToDate[Calendar.YEAR],
                myCalendarToDate[Calendar.MONTH],
                myCalendarToDate[Calendar.DAY_OF_MONTH]
            )
            datePickerDialog.datePicker.maxDate = System.currentTimeMillis() - 1000
            datePickerDialog.show()
        }

        binding!!.actionBar.ivEngToHindi.setOnClickListener { view: View? ->
            val builder = AlertDialog.Builder(
                mContext!!
            )
            builder.setMessage(resources.getString(R.string.eng_to_hi_message))
                .setCancelable(false)
                .setPositiveButton(resources.getString(R.string.ok)) { dialog: DialogInterface, id: Int ->
                    dialog.cancel()
                    binding!!.actionBar.ivEngToHindi.visibility = View.VISIBLE
                    binding!!.actionBar.ivHindiToEng.visibility = View.GONE
                    prefManager!!.uSER_Change_Language = "hi"
                    setLocaleUpdate("hi")
                }
                .setNegativeButton(resources.getString(R.string.cancel)) { dialog: DialogInterface, id: Int -> dialog.cancel() }
            val alert = builder.create()
            alert.setTitle(resources.getString(R.string.alert))
            alert.show()
        }

        binding!!.actionBar.ivHindiToEng.setOnClickListener { view: View? ->
            val builder = AlertDialog.Builder(
                mContext!!
            )
            builder.setMessage(resources.getString(R.string.hi_to_eng_message))
                .setCancelable(false)
                .setPositiveButton(resources.getString(R.string.ok)) { dialog: DialogInterface, id: Int ->
                    dialog.cancel()
                    binding!!.actionBar.ivEngToHindi.visibility = View.GONE
                    binding!!.actionBar.ivHindiToEng.visibility = View.VISIBLE
                    prefManager!!.uSER_Change_Language = "en"
                    setLocaleUpdate("en")
                }
                .setNegativeButton(resources.getString(R.string.cancel)) { dialog: DialogInterface, id: Int -> dialog.cancel() }
            val alert = builder.create()
            alert.setTitle(resources.getString(R.string.alert))
            alert.show()
        }

        binding!!.actionBar.ivThreeDot.setOnClickListener { view: View? ->
            val popupMenu = PopupMenu(this@MainActivity, binding!!.actionBar.ivThreeDot)
            popupMenu.menuInflater.inflate(R.menu.menu, popupMenu.menu)
            popupMenu.setOnMenuItemClickListener { menuItem: MenuItem? ->
                val builder = AlertDialog.Builder(
                    mContext!!
                )
                builder.setMessage(resources.getString(R.string.do_you_want_to_logout_from_this_app))
                    .setCancelable(false)
                    .setPositiveButton(resources.getString(R.string.logout)) { dialog: DialogInterface, id: Int ->
                        dialog.cancel()
                        logout()
                    }
                    .setNegativeButton(resources.getString(R.string.cancel)) { dialog: DialogInterface, id: Int -> dialog.cancel() }
                val alert = builder.create()
                alert.setTitle(resources.getString(R.string.alert))
                alert.show()
                true
            }
            popupMenu.show()
        }

        binding!!.switchView.setOnCheckedChangeListener { compoundButton: CompoundButton?, b: Boolean ->
            if (b) {
                binding!!.layoutPieChart.visibility = View.GONE
                binding!!.layoutNormalView.visibility = View.VISIBLE
            } else {
                binding!!.layoutPieChart.visibility = View.VISIBLE
                binding!!.layoutNormalView.visibility = View.GONE
            }
        }
    }

    private fun setUpClickListener() {
        binding!!.buttonComplaintTotal.setOnClickListener(this)
        binding!!.buttonComplaintPending.setOnClickListener(this)
        binding!!.buttonUploadChallan.setOnClickListener(this)
        binding!!.buttonEditChallan.setOnClickListener(this)
        binding!!.buttonComplaintResolved.setOnClickListener(this)
        binding!!.buttonReports.setOnClickListener(this)
        binding!!.buttonSendToSeCenter.setOnClickListener(this)
        binding!!.buttonAttendance.setOnClickListener(this)
        binding!!.buttonTrainingScheduleManager.setOnClickListener(this)
        binding!!.buttonTrainingScheduleSE.setOnClickListener(this)
        binding!!.buttonInventory.setOnClickListener(this)
        binding!!.buttonInventorySE.setOnClickListener(this)
        binding!!.buttonFPSDetails.setOnClickListener(this)
        binding!!.buttonResetDevice.setOnClickListener(this)
        binding!!.swipeRefreshLayout.setOnRefreshListener(this)
        binding!!.buttonPosDistributionReportForSE.setOnClickListener(this)
        binding!!.buttonPosDistribution.setOnClickListener(this)
        binding!!.buttonPosIssue.setOnClickListener(this)
        binding!!.buttonPosHelpSE.setOnClickListener(this)
        binding!!.llPosErrorShow.setOnClickListener(this)
        binding!!.llInstallationWeigingScale.setOnClickListener(this)
        binding!!.lltIrisDeliveryInstallation.setOnClickListener(this)
        binding!!.lltBiometricDeliveryDashboard.setOnClickListener(this)
        binding!!.lltClosedGuardDelivery.setOnClickListener(this)
        binding!!.buttonEhr.setOnClickListener(this)
    }

    private fun fetchDataByFilterType() {
        if (!filterType.equals(
                "--" + resources.getString(R.string.select_filter) + "--",
                ignoreCase = true
            )
        ) {
            Objects.requireNonNull(binding!!.textFromDate.text)!!.clear()
            Objects.requireNonNull(binding!!.textToDate.text)!!.clear()
            if (filterType.equals(resources.getString(R.string.today), ignoreCase = true)) {
                filterType = resources.getString(R.string.today)
                binding!!.layoutDateRange.visibility = View.GONE
                val calendar = Calendar.getInstance()
                val today = calendar.time
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                val todayDate = sdf.format(today)
                fetchComplaintListBySelect(districtId, todayDate)
            } else if (filterType.equals(
                    resources.getString(R.string.yesterday),
                    ignoreCase = true
                )
            ) {
                filterType = resources.getString(R.string.yesterday)
                binding!!.layoutDateRange.visibility = View.GONE
                val calendar = Calendar.getInstance()
                calendar.add(Calendar.DAY_OF_YEAR, -1)
                val yesterday = calendar.time
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                val yesterdayDate = sdf.format(yesterday)
                fetchComplaintListBySelect(districtId, yesterdayDate)
            } else if (filterType.equals(
                    resources.getString(R.string.current_month),
                    ignoreCase = true
                )
            ) {
                filterType = resources.getString(R.string.current_month)
                binding!!.layoutDateRange.visibility = View.GONE
                val sdf = SimpleDateFormat(myFormat, Locale.US)

                val calendar = Calendar.getInstance()
                calendar[Calendar.DAY_OF_MONTH] = calendar.getActualMinimum(Calendar.DAY_OF_MONTH)
                calendar[Calendar.HOUR_OF_DAY] = 0
                calendar[Calendar.MINUTE] = 0
                calendar[Calendar.SECOND] = 0
                calendar[Calendar.MILLISECOND] = 0
                val firstDateOfCurrentMonth = calendar.time
                val date1 = sdf.format(firstDateOfCurrentMonth)

                val calendar1 = Calendar.getInstance()
                val currentDateOfCurrentMonth = calendar1.time
                val date2 = sdf.format(currentDateOfCurrentMonth)

                fetchComplaintListByCurrentMonthAndPreviousMonth(districtId, date1, date2)
            } else if (filterType.equals(
                    resources.getString(R.string.previous_month),
                    ignoreCase = true
                )
            ) {
                filterType = resources.getString(R.string.previous_month)
                binding!!.layoutDateRange.visibility = View.GONE
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                val aCalendar = Calendar.getInstance()
                aCalendar.add(Calendar.MONTH, -1)
                aCalendar[Calendar.DATE] = 1
                val firstDateOfPreviousMonth = aCalendar.time
                val date1 = sdf.format(firstDateOfPreviousMonth)

                aCalendar[Calendar.DATE] = aCalendar.getActualMaximum(Calendar.DAY_OF_MONTH)
                val lastDateOfPreviousMonth = aCalendar.time
                val date2 = sdf.format(lastDateOfPreviousMonth)

                fetchComplaintListByCurrentMonthAndPreviousMonth(districtId, date1, date2)
            } else if (filterType.equals(
                    resources.getString(R.string.custom_filter),
                    ignoreCase = true
                )
            ) {
                filterType = resources.getString(R.string.custom_filter)
                binding!!.layoutDateRange.visibility = View.VISIBLE
            }
        } else {
            filterType = "--" + resources.getString(R.string.select_filter) + "--"
            Objects.requireNonNull(binding!!.textFromDate.text)!!.clear()
            Objects.requireNonNull(binding!!.textToDate.text)!!.clear()
            binding!!.layoutDateRange.visibility = View.GONE
            fetchComplaintList(districtId)
        }
    }

    private fun pieChart(model: ModelComplaintsCountData) {
        binding!!.pieChart.setUsePercentValues(true)
        binding!!.pieChart.description.isEnabled = false
        binding!!.pieChart.setExtraOffsets(10f, 0f, 10f, 10f)

        binding!!.pieChart.dragDecelerationFrictionCoef = 0.95f

        binding!!.pieChart.centerText = """
            ${resources.getString(R.string.total)}
            ${model.total}
            """.trimIndent()
        binding!!.pieChart.setCenterTextSize(14f)
        binding!!.pieChart.setCenterTextColor(Color.BLACK)

        binding!!.pieChart.isDrawHoleEnabled = true
        binding!!.pieChart.setHoleColor(Color.WHITE)

        binding!!.pieChart.setTransparentCircleColor(Color.WHITE)
        binding!!.pieChart.setTransparentCircleAlpha(110)

        binding!!.pieChart.holeRadius = 58f
        binding!!.pieChart.transparentCircleRadius = 61f

        binding!!.pieChart.setDrawCenterText(true)

        binding!!.pieChart.rotationAngle = 0f
        // enable rotation of the chart by touch
        binding!!.pieChart.isRotationEnabled = true
        binding!!.pieChart.isHighlightPerTapEnabled = true

        binding!!.pieChart.setOnChartValueSelectedListener(this)
        binding!!.pieChart.animateY(1400, Easing.EaseInOutQuad)

        binding!!.pieChart.legend.isEnabled = false

        //        Legend l = binding.pieChart.getLegend();
//        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
//        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
//        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
//        l.setDrawInside(false);
//        l.setXEntrySpace(7f);
//        l.setYEntrySpace(0f);
//        l.setYOffset(7f);
//        l.setTextSize(12f);

        // entry label styling
//        binding.pieChart.setEntryLabelColor(Color.BLACK);
//        binding.pieChart.setEntryLabelTypeface(tfRegular);
//        binding.pieChart.setEntryLabelTextSize(12f);
        setData(model)
    }

    @SuppressLint("SetTextI18n", "DefaultLocale")
    private fun setData(model: ModelComplaintsCountData) {
        entries = ArrayList()
        var sumPendingComp = 0f
        var sumResolvedComp = 0f
        var sumComplaintOnServiceCenter = 0f

        if (model.notResolve != null) {
            sumPendingComp = model.notResolve!!.toFloat()
        }
        if (model.resolved != null) {
            sumResolvedComp = model.resolved!!.toFloat()
        }


        if (model.sendToSECenter != null) {
            sumComplaintOnServiceCenter = model.sendToSECenter!!.toFloat()
        }

        if (model.total != null) {
            val sumPendingCompPercent = (sumPendingComp * 100) / model.total!!
            val sumResolvedCompPercent = (sumResolvedComp * 100) / model.total!!
            val sumComplaintOnServiceCenterCompPercent =
                (sumComplaintOnServiceCenter * 100) / model.total!!

            entries.add(PieEntry(sumPendingComp))
            entries.add(PieEntry(sumResolvedComp))
            entries.add(PieEntry(sumComplaintOnServiceCenter))

            binding!!.textPendingComplaints.text =
                "(" + sumPendingComp.toInt() + ") = " + String.format(
                    "%.1f",
                    sumPendingCompPercent
                ) + " %"
            binding!!.textResolveComplaints.text =
                "(" + sumResolvedComp.toInt() + ") = " + String.format(
                    "%.1f",
                    sumResolvedCompPercent
                ) + " %"
            binding!!.textComplaintOnServiceCenter.text =
                "(" + sumComplaintOnServiceCenter.toInt() + ") = " + String.format(
                    "%.1f",
                    sumComplaintOnServiceCenterCompPercent
                ) + " %"
        }

        val dataSet = PieDataSet(entries, "")

        dataSet.setDrawIcons(false)

        dataSet.sliceSpace = 3f
        dataSet.iconsOffset = MPPointF(0f, 40f)
        dataSet.selectionShift = 5f

        dataSet.setColors(*MATERIAL_COLORS)

        //dataSet.setSelectionShift(0f);
        dataSet.valueLinePart1OffsetPercentage = 80f
        dataSet.valueLinePart1Length = 0.2f
        dataSet.valueLinePart2Length = 0.7f

        //        dataSet.setXValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
        dataSet.yValuePosition = PieDataSet.ValuePosition.OUTSIDE_SLICE

        val data = PieData(dataSet)
        data.setValueFormatter(PercentFormatter(binding!!.pieChart))
        data.setValueTextSize(15f)
        data.setValueTextColor(Color.BLACK)

        binding!!.pieChart.data = data
        binding!!.pieChart.highlightValues(null)
        binding!!.pieChart.invalidate()
    }

    private fun fetchComplaintList(districtId: String?) {
        val fromDate =
            Objects.requireNonNull(binding!!.textFromDate.text).toString().trim { it <= ' ' }
        val toDate = Objects.requireNonNull(binding!!.textToDate.text).toString().trim { it <= ' ' }
        //isLoading();
        viewModel!!.getComplaintsCount(
            prefManager!!.uSER_Id.toString(),
            districtId,
            fromDate,
            toDate
        )!!.observe(
            this
        ) { modelComplaintsCount: ModelComplaintsCount? ->
            //isLoading();
            if (modelComplaintsCount!!.status == "200") {
                val modelComplaintsCountData = modelComplaintsCount.complaints_Count
                pieChart(modelComplaintsCountData)
                binding!!.textCountTotal.text = modelComplaintsCountData.total.toString()
                binding!!.textCountTotalPending.text =
                    modelComplaintsCountData.notResolve.toString()
                binding!!.textCountTotalResolved.text = modelComplaintsCountData.resolved.toString()
                binding!!.textCountSenToSECenter.text =
                    modelComplaintsCountData.sendToSECenter.toString()
                binding!!.textUploadPendingComplaintChallam.text =
                    modelComplaintsCountData.uploadPendingChallan.toString()
            }
        }
    }

    private fun fetchComplaintListBySelect(districtId: String?, date: String) {
        showProgress()
        val service = retrofitInstance!!.create(APIService::class.java)
        val call = service.getComplaintsCountDateDistrictIdWise(
            prefManager!!.uSER_Id, districtId, date, date
        )
        call!!.enqueue(object : Callback<ModelComplaintsCount?> {
            override fun onResponse(
                call: Call<ModelComplaintsCount?>,
                response: Response<ModelComplaintsCount?>
            ) {
                hideProgress()
                if (response.isSuccessful) {
                    val model = response.body()

                    if (Objects.requireNonNull(model)!!.status == "200") {
                        val modelComplaintsCountData = model!!.complaints_Count
                        pieChart(modelComplaintsCountData)
                        binding!!.textCountTotal.text = modelComplaintsCountData.total.toString()
                        binding!!.textCountTotalPending.text =
                            modelComplaintsCountData.notResolve.toString()
                        binding!!.textCountTotalResolved.text =
                            modelComplaintsCountData.resolved.toString()
                        binding!!.textCountSenToSECenter.text =
                            modelComplaintsCountData.sendToSECenter.toString()
                        binding!!.textUploadPendingComplaintChallam.text =
                            modelComplaintsCountData.uploadPendingChallan.toString()
                    } else {
                        Toast.makeText(mContext, model!!.message, Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(
                        mContext,
                        resources.getString(R.string.error),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<ModelComplaintsCount?>, t: Throwable) {
                hideProgress()
                Toast.makeText(
                    mContext,
                    resources.getString(R.string.error_message),
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    private fun fetchComplaintListByCurrentMonthAndPreviousMonth(
        districtId: String?,
        date1: String,
        date2: String
    ) {
        showProgress()
        val service = retrofitInstance!!.create(APIService::class.java)
        val call = service.getComplaintsCountDateDistrictIdWise(
            prefManager!!.uSER_Id, districtId, date1, date2
        )
        call!!.enqueue(object : Callback<ModelComplaintsCount?> {
            override fun onResponse(
                call: Call<ModelComplaintsCount?>,
                response: Response<ModelComplaintsCount?>
            ) {
                hideProgress()
                if (response.isSuccessful) {
                    val model = response.body()
                    if (Objects.requireNonNull(model)!!.status == "200") {
                        val modelComplaintsCountData = model!!.complaints_Count
                        pieChart(modelComplaintsCountData)
                        binding!!.textCountTotal.text = modelComplaintsCountData.total.toString()
                        binding!!.textCountTotalPending.text =
                            modelComplaintsCountData.notResolve.toString()
                        binding!!.textCountTotalResolved.text =
                            modelComplaintsCountData.resolved.toString()
                        binding!!.textCountSenToSECenter.text =
                            modelComplaintsCountData.sendToSECenter.toString()
                        binding!!.textUploadPendingComplaintChallam.text =
                            modelComplaintsCountData.uploadPendingChallan.toString()
                    } else {
                        Toast.makeText(mContext, model!!.message, Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(
                        mContext,
                        resources.getString(R.string.error),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<ModelComplaintsCount?>, t: Throwable) {
                hideProgress()
                Toast.makeText(
                    mContext,
                    resources.getString(R.string.error_message),
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    private fun fetchComplaintListByDateRange(districtId: String?) {
        val fromDate =
            Objects.requireNonNull(binding!!.textFromDate.text).toString().trim { it <= ' ' }
        val toDate = Objects.requireNonNull(binding!!.textToDate.text).toString().trim { it <= ' ' }
        showProgress()
        val service = retrofitInstance!!.create(APIService::class.java)
        val call = service.getComplaintsCountDateDistrictIdWise(
            prefManager!!.uSER_Id, districtId, fromDate, toDate
        )
        call!!.enqueue(object : Callback<ModelComplaintsCount?> {
            override fun onResponse(
                call: Call<ModelComplaintsCount?>,
                response: Response<ModelComplaintsCount?>
            ) {
                hideProgress()
                if (response.isSuccessful) {
                    val model = response.body()
                    if (Objects.requireNonNull(model)!!.status == "200") {
                        val modelComplaintsCountData = model!!.complaints_Count
                        pieChart(modelComplaintsCountData)
                        binding!!.textCountTotal.text = modelComplaintsCountData.total.toString()
                        binding!!.textCountTotalPending.text =
                            modelComplaintsCountData.notResolve.toString()
                        binding!!.textCountTotalResolved.text =
                            modelComplaintsCountData.resolved.toString()
                        binding!!.textCountSenToSECenter.text =
                            modelComplaintsCountData.sendToSECenter.toString()
                        binding!!.textUploadPendingComplaintChallam.text =
                            modelComplaintsCountData.uploadPendingChallan.toString()
                    } else {
                        Toast.makeText(mContext, model!!.message, Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(
                        mContext,
                        resources.getString(R.string.error),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<ModelComplaintsCount?>, t: Throwable) {
                hideProgress()
                Toast.makeText(
                    mContext,
                    resources.getString(R.string.error_message),
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    private fun tehsilList(user_id: String?) {
        viewModel!!.getTehsil(user_id)!!.observe(this) { modelTehsil: ModelTehsil? ->
            if (modelTehsil!!.status == "200") {
                Tehsil_List = modelTehsil.tehsil_List
            }
        }
    }

    private fun districtList() {
        viewModel!!.district!!.observe(this) { modelDistrict: ModelDistrict? ->
            if (modelDistrict!!.status == "200") {
                district_List = modelDistrict.district_List
                if (district_List != null && district_List!!.size > 0) {
                    Collections.reverse(district_List)
                    val l = ModelDistrictList()
                    l.districtId = (-1).toString()
                    l.districtNameEng = "--" + resources.getString(R.string.district) + "--"
                    district_List!!.add(l)
                    Collections.reverse(district_List)

                    val dataAdapter = ArrayAdapter(
                        mContext!!, android.R.layout.simple_spinner_item, district_List!!
                    )
                    dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    binding!!.spinnerDistrict.adapter = dataAdapter
                }
            }
        }
    }

    private fun checkLoginStatus() {
        val service = retrofitInstance!!.create(APIService::class.java)
        val call = service.login(
            prefManager!!.uSER_EMAIL,
            prefManager!!.uSER_PASSWORD,
            prefManager!!.dEVICE_ID,
            prefManager!!.fIREBASE_DEVICE_TOKEN
        )
        call!!.enqueue(object : Callback<ModelLogin?> {
            override fun onResponse(call: Call<ModelLogin?>, response: Response<ModelLogin?>) {
                if (response.isSuccessful) {
                    if (response.code() == 200) {
                        val model = response.body()
                        if (Objects.requireNonNull(model)!!.status != "200") {
                            logout()
                        }
                    } else {
                        makeToast(resources.getString(R.string.error))
                    }
                } else {
                    makeToast(resources.getString(R.string.error))
                }
            }

            override fun onFailure(call: Call<ModelLogin?>, t: Throwable) {
                makeToast(resources.getString(R.string.error_message))
            }
        })
    }

    private fun logout() {
        showProgress(resources.getString(R.string.logout))
        val service = retrofitInstance!!.create(APIService::class.java)
        val call = service.logOutApp(
            prefManager!!.uSER_Id, prefManager!!.uSER_EMAIL
        )
        call!!.enqueue(object : Callback<ModelLogout?> {
            override fun onResponse(call: Call<ModelLogout?>, response: Response<ModelLogout?>) {
                hideProgress()
                if (response.isSuccessful) {
                    if (response.code() == 200) {
                        val model = response.body()
                        if (Objects.requireNonNull(model)!!.status == "200") {
                            unregisterTopic("all")
                            prefManager!!.clear()
                            prefManager!!.uSER_PunchIn = IsSE_PunchIN
                            prefManager!!.setUser_Id("0")
                            prefManager!!.uSER_DistrictId = "0"
                            @SuppressLint("HardwareIds") val android_id = Settings.Secure.getString(
                                mContext!!.contentResolver, Settings.Secure.ANDROID_ID
                            )
                            prefManager!!.dEVICE_ID = android_id
                            startActivity(
                                Intent(mContext, LoginActivity::class.java).addFlags(
                                    Intent.FLAG_ACTIVITY_NEW_TASK
                                )
                            )
                            finish()
                        } else {
                            makeToast(model!!.message)
                        }
                    } else {
                        makeToast(resources.getString(R.string.error))
                    }
                } else {
                    makeToast(resources.getString(R.string.error))
                }
            }

            override fun onFailure(call: Call<ModelLogout?>, t: Throwable) {
                hideProgress()
                makeToast(resources.getString(R.string.error_message))
            }
        })
    }

    private val isLoading: Unit
        get() {
            viewModel!!.isLoading!!.observe(this) { aBoolean: Boolean? ->
                if (aBoolean!!) {
                    showProgress(resources.getString(R.string.please_wait))
                } else {
                    hideProgress()
                }
            }
        }

    private fun updateLabelFromDate() {
        val myFormat = "yyyy-MM-dd" //In which you need put here
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        binding!!.textFromDate.setText(sdf.format(myCalendarFromDate.time))
        Objects.requireNonNull(binding!!.textToDate.text)!!.clear()
        prefManager!!.fROM_DATE =
            Objects.requireNonNull(binding!!.textFromDate.text).toString().trim { it <= ' ' }
    }

    private fun updateLabelToDate() {
        val myFormat = "yyyy-MM-dd" //In which you need put here
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        binding!!.textToDate.setText(sdf.format(myCalendarToDate.time))
        prefManager!!.tO_DATE =
            Objects.requireNonNull(binding!!.textToDate.text).toString().trim { it <= ' ' }
        fetchComplaintListByDateRange(districtId)
    }

    fun setLocale(lang: String?) {
        myLocale = Locale(lang)
        val res = resources
        val dm = res.displayMetrics
        val conf = res.configuration
        conf.locale = myLocale
        res.updateConfiguration(conf, dm)
    }

    fun setLocaleUpdate(lang: String?) {
        prefManager!!.uSER_Change_Language = lang
        myLocale = Locale(lang)
        val res = resources
        val dm = res.displayMetrics
        val conf = res.configuration
        conf.locale = myLocale
        //    conf.setLocale(myLocale);
        res.updateConfiguration(conf, dm)
        startActivity(MainActivity::class.java)
    }

    fun onClickComplaintPending() {
        startActivity(
            Intent(mContext, ComplaintPendingListActivity::class.java)
                .putExtra("districtId", districtId)
                .putExtra("filter_type", filterType)
                .putExtra("tehsil_list", Tehsil_List as Serializable?)
                .putExtra("district_list", district_List as Serializable?)
        )
    }

    fun onClickChallanUploadComplaintPending() {
        startActivity(
            Intent(mContext, ChallanUploadComplaintPendingListActivity::class.java)
                .putExtra("districtId", districtId)
                .putExtra("filter_type", filterType)
                .putExtra("tehsil_list", Tehsil_List as Serializable?)
                .putExtra("district_list", district_List as Serializable?)
        )
    }

    fun onClickEditChallanComplaintList() {
        startActivity(
            Intent(mContext, EditChallanComplaintListActivity::class.java)
                .putExtra("districtId", districtId)
                .putExtra("filter_type", filterType)
                .putExtra("tehsil_list", Tehsil_List as Serializable?)
                .putExtra("district_list", district_List as Serializable?)
        )
    }


    fun onClickComplaintResolve() {
        startActivity(
            Intent(mContext, ComplaintResolveListActivity::class.java)
                .putExtra("districtId", districtId)
                .putExtra("filter_type", filterType)
                .putExtra("tehsil_list", Tehsil_List as Serializable?)
                .putExtra("district_list", district_List as Serializable?)
        )
    }

    fun onClickTotalComplaint() {
        startActivity(
            Intent(mContext, TotalComplaintListActivity::class.java)
                .putExtra("districtId", districtId)
                .putExtra("filter_type", filterType)
                .putExtra("tehsil_list", Tehsil_List as Serializable?)
                .putExtra("district_list", district_List as Serializable?)
        )
    }

    fun onClickSendToSECenter() {
        startActivity(
            Intent(mContext, SendToSECenterListActivity::class.java)
                .putExtra("districtId", districtId)
                .putExtra("filter_type", filterType)
                .putExtra("tehsil_list", Tehsil_List as Serializable?)
                .putExtra("district_list", district_List as Serializable?)
        )
    }

    private fun checkPermission() {
        // String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION
        //        , Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};


        permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.READ_MEDIA_IMAGES,
                Manifest.permission.READ_MEDIA_VIDEO,
                Manifest.permission.CAMERA
            )


            //    Log.d("checkggg", "b");
        } else {
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA
            )

            //  Log.d("checkggg", "0");
        }


        val rationale = "Please provide location permission so that you can ..."
        val options = Permissions.Options()
            .setRationaleDialogTitle("Info")
            .setSettingsDialogTitle("Warning")
        Permissions.check(mContext, permissions, rationale, options, object : PermissionHandler() {
            override fun onGranted() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    backgroundLocationPermissionApproved = ActivityCompat.checkSelfPermission(
                        mContext!!, Manifest.permission.ACCESS_BACKGROUND_LOCATION
                    )
                    if (backgroundLocationPermissionApproved != 0) Handler(Looper.getMainLooper()).postDelayed(
                        { collectLocationPermissionDataDialog() }, 500
                    )
                    else {
                        if (!gpsStatus) turnOnGps()
                        else {
                            if (checkPermissionTypeStr.equals(
                                    "next_process",
                                    ignoreCase = true
                                )
                            ) onClickWithPermissionCheck()
                            else if (checkPermissionTypeStr.equals(
                                    "pie",
                                    ignoreCase = true
                                )
                            ) onValueSelectPieWithPermission()
                        }
                    }
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    backgroundLocationPermissionApproved = ActivityCompat.checkSelfPermission(
                        mContext!!, Manifest.permission.ACCESS_BACKGROUND_LOCATION
                    )
                    if (backgroundLocationPermissionApproved != 0) Handler(Looper.getMainLooper()).postDelayed(
                        { collectLocationPermissionDataDialog() }, 500
                    )
                    else {
                        if (!gpsStatus) turnOnGps()
                        else {
                            if (checkPermissionTypeStr.equals(
                                    "next_process",
                                    ignoreCase = true
                                )
                            ) onClickWithPermissionCheck()
                            else if (checkPermissionTypeStr.equals(
                                    "pie",
                                    ignoreCase = true
                                )
                            ) onValueSelectPieWithPermission()
                        }
                    }
                } else {
                    if (!gpsStatus) turnOnGps()
                    else {
                        if (checkPermissionTypeStr.equals(
                                "next_process",
                                ignoreCase = true
                            )
                        ) onClickWithPermissionCheck()
                        else if (checkPermissionTypeStr.equals(
                                "pie",
                                ignoreCase = true
                            )
                        ) onValueSelectPieWithPermission()
                    }
                }
            }

            override fun onDenied(context: Context, deniedPermissions: ArrayList<String>) {
                Toast.makeText(mContext, "Permission denied", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun collectLocationPermissionDataDialog() {
        val builder = AlertDialog.Builder(
            mContext!!
        )
        builder.setMessage(resources.getString(R.string.collect_location_permission_alert))
            .setCancelable(false)
            .setPositiveButton(resources.getString(R.string.ok)) { dialog: DialogInterface, id: Int ->
                checkBackgroundLocationPermission()
                dialog.dismiss()
            }
        val alert = builder.create()
        alert.setTitle(resources.getString(R.string.alert))
        alert.show()
    }

    private fun checkBackgroundLocationPermission() {
        val permissions = arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
        val rationale = "Please provide Access Background Location"
        val options = Permissions.Options()
            .setRationaleDialogTitle("Info")
            .setSettingsDialogTitle("Warning")
        Permissions.check(mContext, permissions, rationale, options, object : PermissionHandler() {
            override fun onGranted() {
                if (!gpsStatus) turnOnGps()
                else {
                    if (checkPermissionTypeStr.equals(
                            "next_process",
                            ignoreCase = true
                        )
                    ) onClickWithPermissionCheck()
                    else if (checkPermissionTypeStr.equals(
                            "pie",
                            ignoreCase = true
                        )
                    ) onValueSelectPieWithPermission()
                }
            }

            override fun onDenied(context: Context, deniedPermissions: ArrayList<String>) {
                Toast.makeText(mContext, "Permission denied", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun turnOnGps() {
        GpsUtils(mContext!!).turnGPSOn { isGPSEnable: Boolean ->
            isGPS = isGPSEnable
        }
    }

    private val gpsStatus: Boolean
        get() {
            val manager = getSystemService(LOCATION_SERVICE) as LocationManager
            return manager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        }

    private fun onClickWithPermissionCheck() {
        if (globalId == R.id.buttonComplaintTotal) {
            onClickTotalComplaint()
        } else if (globalId == R.id.buttonComplaintPending) {
            onClickComplaintPending()
        } else if (globalId == R.id.buttonUploadChallan) {
            //   onClickComplaintPending();
            onClickChallanUploadComplaintPending()
        } else if (globalId == R.id.buttonEditChallan) {
            onClickEditChallanComplaintList()
        } else if (globalId == R.id.buttonComplaintResolved) {
            onClickComplaintResolve()
        } else if (globalId == R.id.buttonReports) {
            startActivity(ReportsActivity::class.java)
        } else if (globalId == R.id.buttonSendToSeCenter) {
            onClickSendToSECenter()
        } else if (globalId == R.id.buttonAttendance) {
            startActivity(AttendanceDetailsActivity::class.java)
        } else if (globalId == R.id.buttonTrainingScheduleManager) {
            startActivity(TrainingScheduleListActivity::class.java)
        } else if (globalId == R.id.buttonTrainingScheduleSE) {
            startActivity(TrainingScheduleListForSEActivity::class.java)
        } else if (globalId == R.id.buttonInventory) {
            startActivity(InventoryActivity::class.java)
        } else if (globalId == R.id.buttonInventorySE) {
            startActivity(InventoryActivity::class.java)
        } else if (globalId == R.id.buttonFPSDetails) {
            startActivity(LastComplaintFPSListActivity::class.java)
        } else if (globalId == R.id.buttonResetDevice) {
            startActivity(ResetDeviceActivity::class.java)
        } else if (globalId == R.id.buttonPosDistributionReportForSE) {
            startActivity(DistributorPosReportForSEActivity::class.java)
        } else if (globalId == R.id.buttonPosDistribution) {
            startActivity(PosDistributionActivity::class.java)
        } else if (globalId == R.id.buttonPosIssue) {
            startActivity(PosIssueActivity::class.java)
        } else if (globalId == R.id.buttonPosHelpSE) {
            startActivity(FAQListActivity::class.java)
        } else if (globalId == R.id.ll_pos_error_show) {
            //  startActivity(ErrorPosActivity.class);
            startActivity(
                Intent(mContext, ErrorPosActivity::class.java)
                    .putExtra("districtId", districtId)
            )
        } else if (globalId == R.id.ll_installation_weiging_scale) {
            startActivity(WeighingScaleDashboard::class.java)
        } else if (globalId == R.id.lltIrisDeliveryInstallation) {
            startActivity(IrisDeliveryInstallationDashboard::class.java)
        } else if (globalId == R.id.lltClosedGuardDelivery) {
            startActivity(ClosedGuardDeliveryActivity::class.java)
        } else if (globalId == R.id.lltBiometricDeliveryDashboard) {
            startActivity(
                Intent(mContext, BiometricDeliveryDashboardActivity::class.java)
                    .putExtra("districtId", districtId)
            )

            ////  startActivity(BiometricDeliveryDashboardActivity.class);
        } else if (globalId == R.id.buttonEhr) {
            startActivity(com.callmangement.EHR.ehrActivities.MainActivity::class.java)
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 102) {
            isGPS = gpsStatus
            if (isGPS && checkPermissionTypeStr.equals(
                    "next_process",
                    ignoreCase = true
                )
            ) onClickWithPermissionCheck()
            else if (isGPS && checkPermissionTypeStr.equals(
                    "pie",
                    ignoreCase = true
                )
            ) onValueSelectPieWithPermission()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val builder = AlertDialog.Builder(
            mContext!!
        )
        builder.setMessage(resources.getString(R.string.do_you_want_to_exit_from_this_app))
            .setCancelable(false)
            .setPositiveButton(resources.getString(R.string.ok)) { dialog: DialogInterface, id: Int ->
                dialog.cancel()
                finishAffinity()
            }
            .setNegativeButton(resources.getString(R.string.cancel)) { dialog: DialogInterface, id: Int -> dialog.cancel() }
        val alert = builder.create()
        alert.setTitle(resources.getString(R.string.alert))
        alert.show()
    }

    override fun onStart() {
        val locale = prefManager!!.uSER_Change_Language
        if (locale != "") {
            if (locale == "hi") {
                setLocale("hi")
            } else {
                setLocale("en")
            }
        } else {
            prefManager!!.uSER_Change_Language = "en"
        }
        super.onStart()
    }

    override fun onResume() {
        super.onResume()
        checkLoginStatus()
    }

    private fun onValueSelectPieWithPermission() {
        if (entries.indexOf(globalEntryValue) == 0) {
            onClickComplaintPending()
        } else if (entries.indexOf(globalEntryValue) == 1) {
            onClickComplaintResolve()
        } else if (entries.indexOf(globalEntryValue) == 2) {
            onClickSendToSECenter()
        }
    }

    override fun onValueSelected(e: Entry, h: Highlight) {
        checkPermissionTypeStr = "pie"
        globalEntryValue = e
        checkPermission()
    }

    override fun onNothingSelected() {
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onClick(view: View) {
        globalId = view.id
        checkPermissionTypeStr = "next_process"
        checkPermission()
    }

    override fun onRefresh() {
        fetchDataByFilterType()
        binding!!.swipeRefreshLayout.isRefreshing = false
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    companion object {
        fun newIntent(context: Context?, title: String?): Intent {
            val starter = Intent(context, MainActivity::class.java)
            starter.putExtra("param", title)
            return starter
        }

        fun rgb(hex: String): Int {
            val color = hex.replace("#", "").toLong(16).toInt()
            val r = (color shr 16) and 0xFF
            val g = (color shr 8) and 0xFF
            val b = (color shr 0) and 0xFF
            return Color.rgb(r, g, b)
        }
    }
}