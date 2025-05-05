package com.callmangement.ui.home;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.callmangement.network.APIService;
import com.callmangement.network.RetrofitInstance;
import com.callmangement.R;
import com.callmangement.custom.CustomActivity;
import com.callmangement.databinding.ActivityMainBinding;
import com.callmangement.firebase.FirebaseUtils;
import com.callmangement.model.complaints.ModelComplaintsCount;
import com.callmangement.model.complaints.ModelComplaintsCountData;
import com.callmangement.model.district.ModelDistrictList;
import com.callmangement.model.login.ModelLogin;
import com.callmangement.model.logout.ModelLogout;
import com.callmangement.model.tehsil.ModelTehsilList;
import com.callmangement.tracking_service.GpsUtils;
import com.callmangement.ui.attendance.AttendanceDetailsActivity;
import com.callmangement.ui.closed_guard_delivery.ClosedGuardDeliveryActivity;
import com.callmangement.ui.complaint.ChallanUploadComplaintPendingListActivity;
import com.callmangement.ui.complaint.ComplaintPendingListActivity;
import com.callmangement.ui.complaint.ComplaintResolveListActivity;
import com.callmangement.ui.complaint.ComplaintViewModel;
import com.callmangement.ui.complaint.EditChallanComplaintListActivity;
import com.callmangement.ui.complaint.SendToSECenterListActivity;
import com.callmangement.ui.complaint.TotalComplaintListActivity;
import com.callmangement.ui.complaints_fps_wise.LastComplaintFPSListActivity;
import com.callmangement.ui.distributor.activity.DistributorPosReportForSEActivity;
import com.callmangement.ui.distributor.activity.PosDistributionActivity;
import com.callmangement.ui.errors.activity.ErrorPosActivity;
import com.callmangement.ui.biometric_delivery.BiometricDeliveryDashboardActivity;
import com.callmangement.ui.ins_weighing_scale.activity.WeighingScaleDashboard;
import com.callmangement.ui.inventory.InventoryActivity;
import com.callmangement.ui.iris_derivery_installation.IrisDeliveryInstallationDashboard;
import com.callmangement.ui.login.LoginActivity;
import com.callmangement.ui.pos_help.activity.FAQListActivity;
import com.callmangement.ui.pos_issue.activity.PosIssueActivity;
import com.callmangement.ui.reports.ReportsActivity;
import com.callmangement.ui.reset_device.ResetDeviceActivity;
import com.callmangement.ui.training_schedule.TrainingScheduleListActivity;
import com.callmangement.ui.training_schedule.TrainingScheduleListForSEActivity;
import com.callmangement.utils.PrefManager;
import com.callmangement.support.charting.animation.Easing;
import com.callmangement.support.charting.data.Entry;
import com.callmangement.support.charting.data.PieData;
import com.callmangement.support.charting.data.PieDataSet;
import com.callmangement.support.charting.data.PieEntry;
import com.callmangement.support.charting.formatter.PercentFormatter;
import com.callmangement.support.charting.highlight.Highlight;
import com.callmangement.support.charting.listener.OnChartValueSelectedListener;
import com.callmangement.support.charting.utils.MPPointF;
import com.callmangement.support.permissions.PermissionHandler;
import com.callmangement.support.permissions.Permissions;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends CustomActivity implements OnChartValueSelectedListener, View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {
    private ActivityMainBinding binding;
    private PrefManager prefManager;
    private Locale myLocale;
    private final Calendar myCalendarFromDate = Calendar.getInstance();
    private final Calendar myCalendarToDate = Calendar.getInstance();
    private final List<String> spinnerList = new ArrayList<>();
    private final String myFormat = "yyyy-MM-dd";
    private int checkFilter = 0;
    private int checkDistrict = 0;
    private String districtNameEng = "";
    private String filterType = "";
    private ComplaintViewModel viewModel;
    private List<ModelTehsilList> Tehsil_List = new ArrayList<>();
    private List<ModelDistrictList> district_List = new ArrayList<>();
    private ArrayList<PieEntry> entries = new ArrayList<>();
    public final int[] MATERIAL_COLORS = {rgb("#f2726f"), rgb("#29c3be"), rgb("#5d62b5")};
    private String districtId = "0";
    private final String IsSE_PunchIN = "false";
    private int backgroundLocationPermissionApproved;
    private boolean isGPS = false;
    private String checkPermissionTypeStr = "initial";
    int globalId = -1;
    Entry globalEntryValue;
    String[] permissions;

    public static Intent newIntent(Context context, String title) {
        Intent starter = new Intent(context, MainActivity.class);
        starter.putExtra("param", title);
        return starter;
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        binding.actionBar.ivBack.setVisibility(View.GONE);
        binding.actionBar.ivThreeDot.setVisibility(View.VISIBLE);
        binding.actionBar.layoutLanguage.setVisibility(View.VISIBLE);
        binding.actionBar.buttonPDF.setVisibility(View.GONE);
        binding.actionBar.textToolbarTitle.setText(getResources().getString(R.string.app_name));
        viewModel = ViewModelProviders.of(this).get(ComplaintViewModel.class);
        prefManager = new PrefManager(mContext);
        binding.textUsername.setText(prefManager.getUseR_NAME());
        binding.textEmail.setText(prefManager.getUseR_EMAIL());
        districtNameEng = "--" + getResources().getString(R.string.district) + "--";
        initView();

    }

    private void initView() {
        checkPermission();
        setUpClickListener();
        manageLayout();
        districtList();
        setUpData();
        clickListener();
        fetchDataByFilterType();
    }

    private void setUpData() {
        filterType = "--" + getResources().getString(R.string.select_filter) + "--";
        spinnerList.add("--" + getResources().getString(R.string.select_filter) + "--");
        spinnerList.add(getResources().getString(R.string.today));
        spinnerList.add(getResources().getString(R.string.yesterday));
        spinnerList.add(getResources().getString(R.string.current_month));
        spinnerList.add(getResources().getString(R.string.previous_month));
        spinnerList.add(getResources().getString(R.string.custom_filter));
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, spinnerList);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinner.setAdapter(dataAdapter);



        if (prefManager.getUseR_TYPE_ID().equals("1") || prefManager.getUseR_TYPE_ID().equals("2")) {
            binding. spinner.setSelection(3);
            int selectedItemPosition =binding. spinner.getSelectedItemPosition();
            if (selectedItemPosition==3)
            {
                filterType = getResources().getString(R.string.current_month);
                binding.layoutDateRange.setVisibility(View.GONE);
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
                calendar.set(Calendar.HOUR_OF_DAY, 0);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);
                Date firstDateOfCurrentMonth = calendar.getTime();
                String date1 = sdf.format(firstDateOfCurrentMonth);

                Calendar calendar1 = Calendar.getInstance();
                Date currentDateOfCurrentMonth = calendar1.getTime();
                String date2 = sdf.format(currentDateOfCurrentMonth);

                fetchComplaintListByCurrentMonthAndPreviousMonth(districtId, date1, date2);

            }
            else{

            }
        }
        else {

        }


    }

    @SuppressLint("SetTextI18n")
    private void manageLayout() {
        if (!prefManager.getUSER_Change_Language().equals("")) {
            if (prefManager.getUSER_Change_Language().equals("en")) {
                binding.actionBar.ivEngToHindi.setVisibility(View.VISIBLE);
                binding.actionBar.ivHindiToEng.setVisibility(View.GONE);
                prefManager.setUSER_Change_Language("en");
            } else {
                binding.actionBar.ivEngToHindi.setVisibility(View.GONE);
                binding.actionBar.ivHindiToEng.setVisibility(View.VISIBLE);
                prefManager.setUSER_Change_Language("hi");
            }
        } else {
            binding.actionBar.ivEngToHindi.setVisibility(View.VISIBLE);
            binding.actionBar.ivHindiToEng.setVisibility(View.GONE);
        }

        if (prefManager.getUseR_TYPE_ID().equals("1") && prefManager.getUseR_TYPE().equalsIgnoreCase("Admin")) {
            binding.buttonInventorySE.setVisibility(View.GONE);
            binding.buttonPosDistributionReportForSE.setVisibility(View.GONE);
            binding.llTrainingScheduleSE.setVisibility(View.GONE);
            binding.buttonAttendanceBlank.setVisibility(View.GONE);
            binding.buttonAttendance.setVisibility(View.VISIBLE);
            binding.llResetDeviceAndFpsDetails.setVisibility(View.VISIBLE);
            binding.llInventoryAndPosDistributionReport.setVisibility(View.VISIBLE);
            binding.llPosIssueAndTrainingScheduleForManager.setVisibility(View.GONE);
            binding.llPosErrorShow.setVisibility(View.VISIBLE);
            binding.space.setVisibility(View.GONE);
            binding.llInstallationWeigingScale.setVisibility(View.VISIBLE);
            binding.lltIrisDeliveryInstallation.setVisibility(View.VISIBLE);
            binding.lltBiometricDeliveryDashboard.setVisibility(View.VISIBLE);
            binding.lltClosedGuardDelivery.setVisibility(View.GONE);
          //  binding.rstSpace.setVisibility(View.GONE);
         //  binding.posSpace.setVisibility(View.GONE);

        } else if (prefManager.getUseR_TYPE_ID().equals("2") && prefManager.getUseR_TYPE().equalsIgnoreCase("Manager")) {
            binding.buttonInventorySE.setVisibility(View.GONE);
            binding.buttonPosDistributionReportForSE.setVisibility(View.GONE);
            binding.llTrainingScheduleSE.setVisibility(View.GONE);
            binding.buttonAttendanceBlank.setVisibility(View.GONE);
            binding.buttonAttendance.setVisibility(View.VISIBLE);
            binding.llResetDeviceAndFpsDetails.setVisibility(View.VISIBLE);
            binding.llInventoryAndPosDistributionReport.setVisibility(View.VISIBLE);
            binding.llPosIssueAndTrainingScheduleForManager.setVisibility(View.GONE);
            binding.llPosErrorShow.setVisibility(View.VISIBLE);
            binding.space.setVisibility(View.GONE);
            binding.llInstallationWeigingScale.setVisibility(View.VISIBLE);
            binding.lltIrisDeliveryInstallation.setVisibility(View.VISIBLE);
            binding.lltBiometricDeliveryDashboard.setVisibility(View.VISIBLE);
            binding.lltClosedGuardDelivery.setVisibility(View.GONE);
         //  binding.rstSpace.setVisibility(View.GONE);
        //  binding.posSpace.setVisibility(View.GONE);


        } else if (prefManager.getUseR_TYPE_ID().equals("4") && prefManager.getUseR_TYPE().equalsIgnoreCase("ServiceEngineer")) {
            binding.buttonInventorySE.setVisibility(View.VISIBLE);
            binding.rstSpace.setVisibility(View.GONE);
            binding.buttonPosDistributionReportForSE.setVisibility(View.VISIBLE);
            binding.llTrainingScheduleSE.setVisibility(View.GONE);
            binding.buttonAttendanceBlank.setVisibility(View.GONE);
            binding.buttonAttendance.setVisibility(View.GONE);
            binding.llResetDeviceAndFpsDetails.setVisibility(View.GONE);
            binding.llInventoryAndPosDistributionReport.setVisibility(View.GONE);
            binding.llPosIssueAndTrainingScheduleForManager.setVisibility(View.GONE);
            binding.space.setVisibility(View.VISIBLE);
            binding.llPosErrorShow.setVisibility(View.VISIBLE);
            binding.llInstallationWeigingScale.setVisibility(View.VISIBLE);
            binding.lltIrisDeliveryInstallation.setVisibility(View.VISIBLE);
            binding.lltBiometricDeliveryDashboard.setVisibility(View.VISIBLE);
            binding.lltClosedGuardDelivery.setVisibility(View.VISIBLE);
            binding.posSpace.setVisibility(View.GONE);
            binding.hlpSpace.setVisibility(View.GONE);


        } else if (prefManager.getUseR_TYPE_ID().equals("5") && prefManager.getUseR_TYPE().equalsIgnoreCase("ServiceCentre")) {
            binding.buttonInventorySE.setVisibility(View.VISIBLE);
            binding.rstSpace.setVisibility(View.GONE);

            binding.buttonPosDistributionReportForSE.setVisibility(View.GONE);
            binding.llTrainingScheduleSE.setVisibility(View.GONE);
            binding.buttonAttendanceBlank.setVisibility(View.GONE);
            binding.buttonAttendance.setVisibility(View.GONE);
            binding.llResetDeviceAndFpsDetails.setVisibility(View.GONE);
            binding.llInventoryAndPosDistributionReport.setVisibility(View.GONE);
            binding.llPosIssueAndTrainingScheduleForManager.setVisibility(View.GONE);
            binding.llPosErrorShow.setVisibility(View.GONE);
            binding.llInstallationWeigingScale.setVisibility(View.GONE);
            binding.lltIrisDeliveryInstallation.setVisibility(View.GONE);
            binding.lltBiometricDeliveryDashboard.setVisibility(View.GONE);
            binding.lltClosedGuardDelivery.setVisibility(View.GONE);
            binding.posSpace.setVisibility(View.GONE);
            binding.hlpSpace.setVisibility(View.GONE);



        } else if (prefManager.getUseR_TYPE_ID().equalsIgnoreCase("6") && prefManager.getUseR_TYPE().equalsIgnoreCase("DSO")) {
            binding.buttonInventorySE.setVisibility(View.GONE);
            binding.buttonPosDistributionReportForSE.setVisibility(View.GONE);
            binding.llTrainingScheduleSE.setVisibility(View.GONE);
            binding.buttonAttendanceBlank.setVisibility(View.GONE);
            binding.buttonAttendance.setVisibility(View.GONE);
            binding.llResetDeviceAndFpsDetails.setVisibility(View.GONE);
            binding.llInventoryAndPosDistributionReport.setVisibility(View.GONE);
            binding.llPosIssueAndTrainingScheduleForManager.setVisibility(View.GONE);
            binding.llPosErrorShow.setVisibility(View.GONE);
            binding.llInstallationWeigingScale.setVisibility(View.GONE);
            binding.lltIrisDeliveryInstallation.setVisibility(View.GONE);
            binding.lltBiometricDeliveryDashboard.setVisibility(View.GONE);
            binding.lltClosedGuardDelivery.setVisibility(View.GONE);
        //    binding.rstSpace.setVisibility(View.GONE);
            binding.posSpace.setVisibility(View.GONE);
            binding.hlpSpace.setVisibility(View.GONE);



        }

        if (prefManager.getUseR_TYPE_ID().equals("4") && prefManager.getUseR_TYPE().equalsIgnoreCase("ServiceEngineer")) {
            binding.rlDistrict.setVisibility(View.GONE);
            binding.spacer.setVisibility(View.GONE);
            binding.textDestrict.setVisibility(View.VISIBLE);
            binding.textDestrict.setText(getResources().getString(R.string.district) + " : " + prefManager.getUseR_District());
            districtId = prefManager.getUseR_DistrictId();
            tehsilList(prefManager.getUseR_DistrictId());

        } else if (prefManager.getUseR_TYPE_ID().equalsIgnoreCase("6") && prefManager.getUseR_TYPE().equalsIgnoreCase("DSO")) {
            binding.rlDistrict.setVisibility(View.GONE);
            binding.spacer.setVisibility(View.GONE);
            binding.textDestrict.setVisibility(View.VISIBLE);
            binding.textDestrict.setText(getResources().getString(R.string.district) + " : " + prefManager.getUseR_District());

            districtId = prefManager.getUseR_DistrictId();
            tehsilList(prefManager.getUseR_DistrictId());

        } else {
            binding.textDestrict.setVisibility(View.GONE);
            binding.rlDistrict.setVisibility(View.VISIBLE);
            binding.spacer.setVisibility(View.VISIBLE);
            tehsilList("0");
        }
    }

    private void clickListener() {
        binding.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (++checkFilter > 1) {
                    String item = adapterView.getItemAtPosition(i).toString();
                    if (!item.equalsIgnoreCase("--" + getResources().getString(R.string.select_filter) + "--")) {
                        Objects.requireNonNull(binding.textFromDate.getText()).clear();
                        Objects.requireNonNull(binding.textToDate.getText()).clear();
                        if (item.equalsIgnoreCase(getResources().getString(R.string.today))) {
                            filterType = getResources().getString(R.string.today);
                            binding.layoutDateRange.setVisibility(View.GONE);

                            Calendar calendar = Calendar.getInstance();
                            Date today = calendar.getTime();
                            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                            String todayDate = sdf.format(today);
                            fetchComplaintListBySelect(districtId, todayDate);

                        } else if (item.equalsIgnoreCase(getResources().getString(R.string.yesterday))) {
                            filterType = getResources().getString(R.string.yesterday);
                            binding.layoutDateRange.setVisibility(View.GONE);
                            Calendar calendar = Calendar.getInstance();
                            calendar.add(Calendar.DAY_OF_YEAR, -1);
                            Date yesterday = calendar.getTime();
                            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                            String yesterdayDate = sdf.format(yesterday);
                            fetchComplaintListBySelect(districtId, yesterdayDate);

                        } else if (item.equalsIgnoreCase(getResources().getString(R.string.current_month))) {
                            filterType = getResources().getString(R.string.current_month);
                            binding.layoutDateRange.setVisibility(View.GONE);
                            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

                            Calendar calendar = Calendar.getInstance();
                            calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
                            calendar.set(Calendar.HOUR_OF_DAY, 0);
                            calendar.set(Calendar.MINUTE, 0);
                            calendar.set(Calendar.SECOND, 0);
                            calendar.set(Calendar.MILLISECOND, 0);
                            Date firstDateOfCurrentMonth = calendar.getTime();
                            String date1 = sdf.format(firstDateOfCurrentMonth);

                            Calendar calendar1 = Calendar.getInstance();
                            Date currentDateOfCurrentMonth = calendar1.getTime();
                            String date2 = sdf.format(currentDateOfCurrentMonth);

                            fetchComplaintListByCurrentMonthAndPreviousMonth(districtId, date1, date2);

                        } else if (item.equalsIgnoreCase(getResources().getString(R.string.previous_month))) {
                            filterType = getResources().getString(R.string.previous_month);
                            binding.layoutDateRange.setVisibility(View.GONE);
                            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                            Calendar aCalendar = Calendar.getInstance();
                            aCalendar.add(Calendar.MONTH, -1);
                            aCalendar.set(Calendar.DATE, 1);
                            Date firstDateOfPreviousMonth = aCalendar.getTime();
                            String date1 = sdf.format(firstDateOfPreviousMonth);

                            aCalendar.set(Calendar.DATE, aCalendar.getActualMaximum(Calendar.DAY_OF_MONTH));
                            Date lastDateOfPreviousMonth = aCalendar.getTime();
                            String date2 = sdf.format(lastDateOfPreviousMonth);

                            fetchComplaintListByCurrentMonthAndPreviousMonth(districtId, date1, date2);


                        } else if (item.equalsIgnoreCase(getResources().getString(R.string.custom_filter))) {
                            filterType = getResources().getString(R.string.custom_filter);
                            binding.layoutDateRange.setVisibility(View.VISIBLE);
                        }
                    } else {
                        filterType = "--" + getResources().getString(R.string.select_filter) + "--";
                        Objects.requireNonNull(binding.textFromDate.getText()).clear();
                        Objects.requireNonNull(binding.textToDate.getText()).clear();
                        binding.layoutDateRange.setVisibility(View.GONE);
                        fetchComplaintListBySelect(districtId, "");
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        binding.spinnerDistrict.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (++checkDistrict > 1) {
                    districtNameEng = district_List.get(i).getDistrictNameEng();
                    districtId = district_List.get(i).getDistrictId();
                    fetchDataByFilterType();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        DatePickerDialog.OnDateSetListener dateFromDate = (view, year, monthOfYear, dayOfMonth) -> {
            myCalendarFromDate.set(Calendar.YEAR, year);
            myCalendarFromDate.set(Calendar.MONTH, monthOfYear);
            myCalendarFromDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabelFromDate();
        };

        DatePickerDialog.OnDateSetListener dateToDate = (view, year, monthOfYear, dayOfMonth) -> {
            myCalendarToDate.set(Calendar.YEAR, year);
            myCalendarToDate.set(Calendar.MONTH, monthOfYear);
            myCalendarToDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabelToDate();
        };

        binding.textFromDate.setOnClickListener(view -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(mContext, dateFromDate, myCalendarFromDate
                    .get(Calendar.YEAR), myCalendarFromDate.get(Calendar.MONTH),
                    myCalendarFromDate.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis() - 1000);
            datePickerDialog.show();
        });

        binding.textToDate.setOnClickListener(view -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(mContext, dateToDate, myCalendarToDate
                    .get(Calendar.YEAR), myCalendarToDate.get(Calendar.MONTH),
                    myCalendarToDate.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis() - 1000);
            datePickerDialog.show();
        });

        binding.actionBar.ivEngToHindi.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setMessage(getResources().getString(R.string.eng_to_hi_message))
                    .setCancelable(false)
                    .setPositiveButton(getResources().getString(R.string.ok), (dialog, id) -> {
                        dialog.cancel();
                        binding.actionBar.ivEngToHindi.setVisibility(View.VISIBLE);
                        binding.actionBar.ivHindiToEng.setVisibility(View.GONE);
                        prefManager.setUSER_Change_Language("hi");
                        setLocaleUpdate("hi");
                    })
                    .setNegativeButton(getResources().getString(R.string.cancel), (dialog, id) -> dialog.cancel());
            AlertDialog alert = builder.create();
            alert.setTitle(getResources().getString(R.string.alert));
            alert.show();
        });

        binding.actionBar.ivHindiToEng.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setMessage(getResources().getString(R.string.hi_to_eng_message))
                    .setCancelable(false)
                    .setPositiveButton(getResources().getString(R.string.ok), (dialog, id) -> {
                        dialog.cancel();
                        binding.actionBar.ivEngToHindi.setVisibility(View.GONE);
                        binding.actionBar.ivHindiToEng.setVisibility(View.VISIBLE);
                        prefManager.setUSER_Change_Language("en");
                        setLocaleUpdate("en");
                    })
                    .setNegativeButton(getResources().getString(R.string.cancel), (dialog, id) -> dialog.cancel());
            AlertDialog alert = builder.create();
            alert.setTitle(getResources().getString(R.string.alert));
            alert.show();
        });

        binding.actionBar.ivThreeDot.setOnClickListener(view -> {
            PopupMenu popupMenu = new PopupMenu(MainActivity.this, binding.actionBar.ivThreeDot);
            popupMenu.getMenuInflater().inflate(R.menu.menu, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(menuItem -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
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
        });

        binding.switchView.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                binding.layoutPieChart.setVisibility(View.GONE);
                binding.layoutNormalView.setVisibility(View.VISIBLE);
            } else {
                binding.layoutPieChart.setVisibility(View.VISIBLE);
                binding.layoutNormalView.setVisibility(View.GONE);
            }
        });
    }

    private void setUpClickListener() {
        binding.buttonComplaintTotal.setOnClickListener(this);
        binding.buttonComplaintPending.setOnClickListener(this);
        binding.buttonUploadChallan.setOnClickListener(this);
        binding.buttonEditChallan.setOnClickListener(this);
        binding.buttonComplaintResolved.setOnClickListener(this);
        binding.buttonReports.setOnClickListener(this);
        binding.buttonSendToSeCenter.setOnClickListener(this);
        binding.buttonAttendance.setOnClickListener(this);
        binding.buttonTrainingScheduleManager.setOnClickListener(this);
        binding.buttonTrainingScheduleSE.setOnClickListener(this);
        binding.buttonInventory.setOnClickListener(this);
        binding.buttonInventorySE.setOnClickListener(this);
        binding.buttonFPSDetails.setOnClickListener(this);
        binding.buttonResetDevice.setOnClickListener(this);
        binding.swipeRefreshLayout.setOnRefreshListener(this);
        binding.buttonPosDistributionReportForSE.setOnClickListener(this);
        binding.buttonPosDistribution.setOnClickListener(this);
        binding.buttonPosIssue.setOnClickListener(this);
        binding.buttonPosHelpSE.setOnClickListener(this);
        binding.llPosErrorShow.setOnClickListener(this);
        binding.llInstallationWeigingScale.setOnClickListener(this);
        binding.lltIrisDeliveryInstallation.setOnClickListener(this);
        binding.lltBiometricDeliveryDashboard.setOnClickListener(this);
        binding.lltClosedGuardDelivery.setOnClickListener(this);
        binding.buttonEhr.setOnClickListener(this);


    }

    private void fetchDataByFilterType() {
        if (!filterType.equalsIgnoreCase("--" + getResources().getString(R.string.select_filter) + "--")) {
            Objects.requireNonNull(binding.textFromDate.getText()).clear();
            Objects.requireNonNull(binding.textToDate.getText()).clear();
            if (filterType.equalsIgnoreCase(getResources().getString(R.string.today))) {
                filterType = getResources().getString(R.string.today);
                binding.layoutDateRange.setVisibility(View.GONE);
                Calendar calendar = Calendar.getInstance();
                Date today = calendar.getTime();
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                String todayDate = sdf.format(today);
                fetchComplaintListBySelect(districtId, todayDate);

            } else if (filterType.equalsIgnoreCase(getResources().getString(R.string.yesterday))) {
                filterType = getResources().getString(R.string.yesterday);
                binding.layoutDateRange.setVisibility(View.GONE);
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.DAY_OF_YEAR, -1);
                Date yesterday = calendar.getTime();
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                String yesterdayDate = sdf.format(yesterday);
                fetchComplaintListBySelect(districtId, yesterdayDate);

            } else if (filterType.equalsIgnoreCase(getResources().getString(R.string.current_month))) {
                filterType = getResources().getString(R.string.current_month);
                binding.layoutDateRange.setVisibility(View.GONE);
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
                calendar.set(Calendar.HOUR_OF_DAY, 0);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);
                Date firstDateOfCurrentMonth = calendar.getTime();
                String date1 = sdf.format(firstDateOfCurrentMonth);

                Calendar calendar1 = Calendar.getInstance();
                Date currentDateOfCurrentMonth = calendar1.getTime();
                String date2 = sdf.format(currentDateOfCurrentMonth);

                fetchComplaintListByCurrentMonthAndPreviousMonth(districtId, date1, date2);

            } else if (filterType.equalsIgnoreCase(getResources().getString(R.string.previous_month))) {
                filterType = getResources().getString(R.string.previous_month);
                binding.layoutDateRange.setVisibility(View.GONE);
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                Calendar aCalendar = Calendar.getInstance();
                aCalendar.add(Calendar.MONTH, -1);
                aCalendar.set(Calendar.DATE, 1);
                Date firstDateOfPreviousMonth = aCalendar.getTime();
                String date1 = sdf.format(firstDateOfPreviousMonth);

                aCalendar.set(Calendar.DATE, aCalendar.getActualMaximum(Calendar.DAY_OF_MONTH));
                Date lastDateOfPreviousMonth = aCalendar.getTime();
                String date2 = sdf.format(lastDateOfPreviousMonth);

                fetchComplaintListByCurrentMonthAndPreviousMonth(districtId, date1, date2);

            } else if (filterType.equalsIgnoreCase(getResources().getString(R.string.custom_filter))) {
                filterType = getResources().getString(R.string.custom_filter);
                binding.layoutDateRange.setVisibility(View.VISIBLE);
            }

        } else {
            filterType = "--" + getResources().getString(R.string.select_filter) + "--";
            Objects.requireNonNull(binding.textFromDate.getText()).clear();
            Objects.requireNonNull(binding.textToDate.getText()).clear();
            binding.layoutDateRange.setVisibility(View.GONE);
            fetchComplaintList(districtId);
        }
    }

    private void pieChart(ModelComplaintsCountData model) {
        binding.pieChart.setUsePercentValues(true);
        binding.pieChart.getDescription().setEnabled(false);
        binding.pieChart.setExtraOffsets(10, 0, 10, 10);

        binding.pieChart.setDragDecelerationFrictionCoef(0.95f);

        binding.pieChart.setCenterText(getResources().getString(R.string.total) + "\n" + model.getTotal());
        binding.pieChart.setCenterTextSize(14f);
        binding.pieChart.setCenterTextColor(Color.BLACK);

        binding.pieChart.setDrawHoleEnabled(true);
        binding.pieChart.setHoleColor(Color.WHITE);

        binding.pieChart.setTransparentCircleColor(Color.WHITE);
        binding.pieChart.setTransparentCircleAlpha(110);

        binding.pieChart.setHoleRadius(58f);
        binding.pieChart.setTransparentCircleRadius(61f);

        binding.pieChart.setDrawCenterText(true);

        binding.pieChart.setRotationAngle(0);
        // enable rotation of the chart by touch
        binding.pieChart.setRotationEnabled(true);
        binding.pieChart.setHighlightPerTapEnabled(true);

        binding.pieChart.setOnChartValueSelectedListener(this);
        binding.pieChart.animateY(1400, Easing.EaseInOutQuad);

        binding.pieChart.getLegend().setEnabled(false);

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

        setData(model);

    }

    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    private void setData(ModelComplaintsCountData model) {

        entries = new ArrayList<>();
        float sumPendingComp = 0;
        float sumResolvedComp = 0;
        float sumComplaintOnServiceCenter = 0;

        if (model.getNotResolve() != null) {
            sumPendingComp = model.getNotResolve();
        }
        if (model.getResolved() != null) {
            sumResolvedComp = model.getResolved();
        }


        if (model.getSendToSECenter() != null) {
            sumComplaintOnServiceCenter = model.getSendToSECenter();
        }

        if (model.getTotal() != null) {
            float sumPendingCompPercent = (sumPendingComp * 100) / model.getTotal();
            float sumResolvedCompPercent = (sumResolvedComp * 100) / model.getTotal();
            float sumComplaintOnServiceCenterCompPercent = (sumComplaintOnServiceCenter * 100) / model.getTotal();

            entries.add(new PieEntry(sumPendingComp));
            entries.add(new PieEntry(sumResolvedComp));
            entries.add(new PieEntry(sumComplaintOnServiceCenter));

            binding.textPendingComplaints.setText("(" + (int) sumPendingComp + ") = " + String.format("%.1f", sumPendingCompPercent) + " %");
            binding.textResolveComplaints.setText("(" + (int) sumResolvedComp + ") = " + String.format("%.1f", sumResolvedCompPercent) + " %");
            binding.textComplaintOnServiceCenter.setText("(" + (int) sumComplaintOnServiceCenter + ") = " + String.format("%.1f", sumComplaintOnServiceCenterCompPercent) + " %");
        }

        PieDataSet dataSet = new PieDataSet(entries, "");

        dataSet.setDrawIcons(false);

        dataSet.setSliceSpace(3f);
        dataSet.setIconsOffset(new MPPointF(0, 40));
        dataSet.setSelectionShift(5f);

        dataSet.setColors(MATERIAL_COLORS);

        //dataSet.setSelectionShift(0f);

        dataSet.setValueLinePart1OffsetPercentage(80.f);
        dataSet.setValueLinePart1Length(0.2f);
        dataSet.setValueLinePart2Length(0.7f);

//        dataSet.setXValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
        dataSet.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);

        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter(binding.pieChart));
        data.setValueTextSize(15f);
        data.setValueTextColor(Color.BLACK);

        binding.pieChart.setData(data);
        binding.pieChart.highlightValues(null);
        binding.pieChart.invalidate();

    }

    private void fetchComplaintList(String districtId) {
        String fromDate = Objects.requireNonNull(binding.textFromDate.getText()).toString().trim();
        String toDate = Objects.requireNonNull(binding.textToDate.getText()).toString().trim();
        //isLoading();
        viewModel.getComplaintsCount(String.valueOf(prefManager.getUseR_Id()), districtId, fromDate, toDate).observe(this, modelComplaintsCount -> {
            //isLoading();
            if (modelComplaintsCount.getStatus().equals("200")) {
                ModelComplaintsCountData modelComplaintsCountData = modelComplaintsCount.getComplaints_Count();
                pieChart(modelComplaintsCountData);
                binding.textCountTotal.setText(String.valueOf(modelComplaintsCountData.getTotal()));
                binding.textCountTotalPending.setText(String.valueOf(modelComplaintsCountData.getNotResolve()));
                binding.textCountTotalResolved.setText(String.valueOf(modelComplaintsCountData.getResolved()));
                binding.textCountSenToSECenter.setText(String.valueOf(modelComplaintsCountData.getSendToSECenter()));
                binding.textUploadPendingComplaintChallam.setText(String.valueOf(modelComplaintsCountData.getUploadPendingChallan()));

            }
        });
    }

    private void fetchComplaintListBySelect(String districtId, String date) {
        showProgress();
        APIService service = RetrofitInstance.getRetrofitInstance().create(APIService.class);
        Call<ModelComplaintsCount> call = service.getComplaintsCountDateDistrictIdWise(prefManager.getUseR_Id(), districtId, date, date);
        call.enqueue(new Callback<ModelComplaintsCount>() {
            @Override
            public void onResponse(@NonNull Call<ModelComplaintsCount> call, @NonNull Response<ModelComplaintsCount> response) {
                hideProgress();
                if (response.isSuccessful()) {
                    ModelComplaintsCount model = response.body();

                    if (Objects.requireNonNull(model).getStatus().equals("200")) {
                        ModelComplaintsCountData modelComplaintsCountData = model.getComplaints_Count();
                        pieChart(modelComplaintsCountData);
                        binding.textCountTotal.setText(String.valueOf(modelComplaintsCountData.getTotal()));
                        binding.textCountTotalPending.setText(String.valueOf(modelComplaintsCountData.getNotResolve()));
                        binding.textCountTotalResolved.setText(String.valueOf(modelComplaintsCountData.getResolved()));
                        binding.textCountSenToSECenter.setText(String.valueOf(modelComplaintsCountData.getSendToSECenter()));
                        binding.textUploadPendingComplaintChallam.setText(String.valueOf(modelComplaintsCountData.getUploadPendingChallan()));
                    } else {
                        Toast.makeText(mContext, model.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(mContext, getResources().getString(R.string.error), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ModelComplaintsCount> call, @NonNull Throwable t) {
                hideProgress();
                Toast.makeText(mContext, getResources().getString(R.string.error_message), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchComplaintListByCurrentMonthAndPreviousMonth(String districtId, String date1, String date2) {
        showProgress();
        APIService service = RetrofitInstance.getRetrofitInstance().create(APIService.class);
        Call<ModelComplaintsCount> call = service.getComplaintsCountDateDistrictIdWise(prefManager.getUseR_Id(), districtId, date1, date2);
        call.enqueue(new Callback<ModelComplaintsCount>() {
            @Override
            public void onResponse(@NonNull Call<ModelComplaintsCount> call, @NonNull Response<ModelComplaintsCount> response) {
                hideProgress();
                if (response.isSuccessful()) {
                    ModelComplaintsCount model = response.body();
                    if (Objects.requireNonNull(model).getStatus().equals("200")) {
                        ModelComplaintsCountData modelComplaintsCountData = model.getComplaints_Count();
                        pieChart(modelComplaintsCountData);
                        binding.textCountTotal.setText(String.valueOf(modelComplaintsCountData.getTotal()));
                        binding.textCountTotalPending.setText(String.valueOf(modelComplaintsCountData.getNotResolve()));
                        binding.textCountTotalResolved.setText(String.valueOf(modelComplaintsCountData.getResolved()));
                        binding.textCountSenToSECenter.setText(String.valueOf(modelComplaintsCountData.getSendToSECenter()));
                        binding.textUploadPendingComplaintChallam.setText(String.valueOf(modelComplaintsCountData.getUploadPendingChallan()));

                    } else {
                        Toast.makeText(mContext, model.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(mContext, getResources().getString(R.string.error), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ModelComplaintsCount> call, @NonNull Throwable t) {
                hideProgress();
                Toast.makeText(mContext, getResources().getString(R.string.error_message), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchComplaintListByDateRange(String districtId) {
        String fromDate = Objects.requireNonNull(binding.textFromDate.getText()).toString().trim();
        String toDate = Objects.requireNonNull(binding.textToDate.getText()).toString().trim();
        showProgress();
        APIService service = RetrofitInstance.getRetrofitInstance().create(APIService.class);
        Call<ModelComplaintsCount> call = service.getComplaintsCountDateDistrictIdWise(prefManager.getUseR_Id(), districtId, fromDate, toDate);
        call.enqueue(new Callback<ModelComplaintsCount>() {
            @Override
            public void onResponse(@NonNull Call<ModelComplaintsCount> call, @NonNull Response<ModelComplaintsCount> response) {
                hideProgress();
                if (response.isSuccessful()) {
                    ModelComplaintsCount model = response.body();
                    if (Objects.requireNonNull(model).getStatus().equals("200")) {
                        ModelComplaintsCountData modelComplaintsCountData = model.getComplaints_Count();
                        pieChart(modelComplaintsCountData);
                        binding.textCountTotal.setText(String.valueOf(modelComplaintsCountData.getTotal()));
                        binding.textCountTotalPending.setText(String.valueOf(modelComplaintsCountData.getNotResolve()));
                        binding.textCountTotalResolved.setText(String.valueOf(modelComplaintsCountData.getResolved()));
                        binding.textCountSenToSECenter.setText(String.valueOf(modelComplaintsCountData.getSendToSECenter()));
                        binding.textUploadPendingComplaintChallam.setText(String.valueOf(modelComplaintsCountData.getUploadPendingChallan()));

                    } else {
                        Toast.makeText(mContext, model.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(mContext, getResources().getString(R.string.error), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ModelComplaintsCount> call, @NonNull Throwable t) {
                hideProgress();
                Toast.makeText(mContext, getResources().getString(R.string.error_message), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void tehsilList(String user_id) {
        viewModel.getTehsil(user_id).observe(this, modelTehsil -> {
            if (modelTehsil.getStatus().equals("200")) {
                Tehsil_List = modelTehsil.getTehsil_List();
            }
        });
    }

    private void districtList() {
        viewModel.getDistrict().observe(this, modelDistrict -> {
            if (modelDistrict.getStatus().equals("200")) {
                district_List = modelDistrict.getDistrict_List();
                if (district_List != null && district_List.size() > 0) {
                    Collections.reverse(district_List);
                    ModelDistrictList l = new ModelDistrictList();
                    l.setDistrictId(String.valueOf(-1));
                    l.setDistrictNameEng("--" + getResources().getString(R.string.district) + "--");
                    district_List.add(l);
                    Collections.reverse(district_List);

                    ArrayAdapter<ModelDistrictList> dataAdapter = new ArrayAdapter<>(mContext, android.R.layout.simple_spinner_item, district_List);
                    dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    binding.spinnerDistrict.setAdapter(dataAdapter);
                }

            }
        });
    }

    private void checkLoginStatus() {
        APIService service = RetrofitInstance.getRetrofitInstance().create(APIService.class);
        Call<ModelLogin> call = service.login(prefManager.getUseR_EMAIL(), prefManager.getUseR_PASSWORD(), prefManager.getDevicE_ID(), prefManager.getFirebasE_DEVICE_TOKEN());
        call.enqueue(new Callback<ModelLogin>() {
            @Override
            public void onResponse(@NonNull Call<ModelLogin> call, @NonNull Response<ModelLogin> response) {
                if (response.isSuccessful()) {
                    if (response.code() == 200) {
                        ModelLogin model = response.body();
                        if (!Objects.requireNonNull(model).getStatus().equals("200")) {
                            logout();
                        }
                    } else {
                        makeToast(getResources().getString(R.string.error));
                    }
                } else {
                    makeToast(getResources().getString(R.string.error));
                }
            }

            @Override
            public void onFailure(@NonNull Call<ModelLogin> call, @NonNull Throwable t) {
                makeToast(getResources().getString(R.string.error_message));
            }
        });
    }

    private void logout() {
        showProgress(getResources().getString(R.string.logout));
        APIService service = RetrofitInstance.getRetrofitInstance().create(APIService.class);
        Call<ModelLogout> call = service.logOutApp(prefManager.getUseR_Id(), prefManager.getUseR_EMAIL());
        call.enqueue(new Callback<ModelLogout>() {
            @Override
            public void onResponse(@NonNull Call<ModelLogout> call, @NonNull Response<ModelLogout> response) {
                hideProgress();
                if (response.isSuccessful()) {
                    if (response.code() == 200) {
                        ModelLogout model = response.body();
                        if (Objects.requireNonNull(model).getStatus().equals("200")) {
                            FirebaseUtils.unregisterTopic("all");
                            prefManager.clear();
                            prefManager.setUseR_PunchIn(IsSE_PunchIN);
                            prefManager.setUser_Id("0");
                            prefManager.setUseR_DistrictId("0");
                            @SuppressLint("HardwareIds") String android_id = Settings.Secure.getString(mContext.getContentResolver(), Settings.Secure.ANDROID_ID);
                            prefManager.setDevicE_ID(android_id);
                            startActivity(new Intent(mContext, LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                            finish();
                        } else {
                            makeToast(model.getMessage());
                        }
                    } else {
                        makeToast(getResources().getString(R.string.error));
                    }
                } else {
                    makeToast(getResources().getString(R.string.error));
                }
            }

            @Override
            public void onFailure(@NonNull Call<ModelLogout> call, @NonNull Throwable t) {
                hideProgress();
                makeToast(getResources().getString(R.string.error_message));
            }
        });
    }

    private void isLoading() {
        viewModel.isLoading().observe(this, aBoolean -> {
            if (aBoolean) {
                showProgress(getResources().getString(R.string.please_wait));
            } else {
                hideProgress();
            }
        });
    }

    private void updateLabelFromDate() {
        String myFormat = "yyyy-MM-dd"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        binding.textFromDate.setText(sdf.format(myCalendarFromDate.getTime()));
        Objects.requireNonNull(binding.textToDate.getText()).clear();
        prefManager.setFroM_DATE(Objects.requireNonNull(binding.textFromDate.getText()).toString().trim());
    }

    private void updateLabelToDate() {
        String myFormat = "yyyy-MM-dd"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        binding.textToDate.setText(sdf.format(myCalendarToDate.getTime()));
        prefManager.setTO_DATE(Objects.requireNonNull(binding.textToDate.getText()).toString().trim());
        fetchComplaintListByDateRange(districtId);
    }

    public void setLocale(String lang) {
        myLocale = new Locale(lang);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
    }

    public void setLocaleUpdate(String lang) {
        prefManager.setUSER_Change_Language(lang);
        myLocale = new Locale(lang);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        //    conf.setLocale(myLocale);
        res.updateConfiguration(conf, dm);
        startActivity(MainActivity.class);
    }

    public void onClickComplaintPending() {
        startActivity(new Intent(mContext, ComplaintPendingListActivity.class)
                .putExtra("districtId", districtId)
                .putExtra("filter_type", filterType)
                .putExtra("tehsil_list", (Serializable) Tehsil_List)
                .putExtra("district_list", (Serializable) district_List));
    }

    public void onClickChallanUploadComplaintPending() {
        startActivity(new Intent(mContext, ChallanUploadComplaintPendingListActivity.class)
                .putExtra("districtId", districtId)
                .putExtra("filter_type", filterType)
                .putExtra("tehsil_list", (Serializable) Tehsil_List)
                .putExtra("district_list", (Serializable) district_List));
    }

    public void onClickEditChallanComplaintList() {
        startActivity(new Intent(mContext, EditChallanComplaintListActivity.class)
                .putExtra("districtId", districtId)
                .putExtra("filter_type", filterType)
                .putExtra("tehsil_list", (Serializable) Tehsil_List)
                .putExtra("district_list", (Serializable) district_List));
    }


    public void onClickComplaintResolve() {
        startActivity(new Intent(mContext, ComplaintResolveListActivity.class)
                .putExtra("districtId", districtId)
                .putExtra("filter_type", filterType)
                .putExtra("tehsil_list", (Serializable) Tehsil_List)
                .putExtra("district_list", (Serializable) district_List));
    }

    public void onClickTotalComplaint() {
        startActivity(new Intent(mContext, TotalComplaintListActivity.class)
                .putExtra("districtId", districtId)
                .putExtra("filter_type", filterType)
                .putExtra("tehsil_list", (Serializable) Tehsil_List)
                .putExtra("district_list", (Serializable) district_List));
    }

    public void onClickSendToSECenter() {
        startActivity(new Intent(mContext, SendToSECenterListActivity.class)
                .putExtra("districtId", districtId)
                .putExtra("filter_type", filterType)
                .putExtra("tehsil_list", (Serializable) Tehsil_List)
                .putExtra("district_list", (Serializable) district_List));
    }

    private void checkPermission() {
        // String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION
        //        , Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.READ_MEDIA_VIDEO, Manifest.permission.CAMERA};
            //    Log.d("checkggg", "b");


        } else {
            permissions =
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};

          //  Log.d("checkggg", "0");
        }


        String rationale = "Please provide location permission so that you can ...";
        Permissions.Options options = new Permissions.Options()
                .setRationaleDialogTitle("Info")
                .setSettingsDialogTitle("Warning");
        Permissions.check(mContext, permissions, rationale, options, new PermissionHandler() {
            @Override
            public void onGranted() {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    backgroundLocationPermissionApproved = ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_BACKGROUND_LOCATION);
                    if (backgroundLocationPermissionApproved != 0)
                        new Handler(Looper.getMainLooper()).postDelayed(() -> collectLocationPermissionDataDialog(), 500);
                    else {
                        if (!getGpsStatus())
                            turnOnGps();
                        else {
                            if (checkPermissionTypeStr.equalsIgnoreCase("next_process"))
                                onClickWithPermissionCheck();
                            else if (checkPermissionTypeStr.equalsIgnoreCase("pie"))
                                onValueSelectPieWithPermission();
                        }
                    }
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    backgroundLocationPermissionApproved = ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_BACKGROUND_LOCATION);
                    if (backgroundLocationPermissionApproved != 0)
                        new Handler(Looper.getMainLooper()).postDelayed(() -> collectLocationPermissionDataDialog(), 500);
                    else {
                        if (!getGpsStatus())
                            turnOnGps();
                        else {
                            if (checkPermissionTypeStr.equalsIgnoreCase("next_process"))
                                onClickWithPermissionCheck();
                            else if (checkPermissionTypeStr.equalsIgnoreCase("pie"))
                                onValueSelectPieWithPermission();
                        }
                    }
                } else {
                    if (!getGpsStatus())
                        turnOnGps();
                    else {
                        if (checkPermissionTypeStr.equalsIgnoreCase("next_process"))
                            onClickWithPermissionCheck();
                        else if (checkPermissionTypeStr.equalsIgnoreCase("pie"))
                            onValueSelectPieWithPermission();
                    }
                }

            }

            @Override
            public void onDenied(Context context, ArrayList<String> deniedPermissions) {
                Toast.makeText(mContext, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void collectLocationPermissionDataDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage(getResources().getString(R.string.collect_location_permission_alert))
                .setCancelable(false)
                .setPositiveButton(getResources().getString(R.string.ok), (dialog, id) -> {
                    checkBackgroundLocationPermission();
                    dialog.dismiss();
                });
        AlertDialog alert = builder.create();
        alert.setTitle(getResources().getString(R.string.alert));
        alert.show();
    }

    private void checkBackgroundLocationPermission() {
        String[] permissions = {Manifest.permission.ACCESS_BACKGROUND_LOCATION};
        String rationale = "Please provide Access Background Location";
        Permissions.Options options = new Permissions.Options()
                .setRationaleDialogTitle("Info")
                .setSettingsDialogTitle("Warning");
        Permissions.check(mContext, permissions, rationale, options, new PermissionHandler() {
            @Override
            public void onGranted() {
                if (!getGpsStatus())
                    turnOnGps();
                else {
                    if (checkPermissionTypeStr.equalsIgnoreCase("next_process"))
                        onClickWithPermissionCheck();
                    else if (checkPermissionTypeStr.equalsIgnoreCase("pie"))
                        onValueSelectPieWithPermission();
                }
            }

            @Override
            public void onDenied(Context context, ArrayList<String> deniedPermissions) {
                Toast.makeText(mContext, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void turnOnGps() {
        new GpsUtils(mContext).turnGPSOn(isGPSEnable -> {
            isGPS = isGPSEnable;
        });
    }

    private boolean getGpsStatus() {
        LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    private void onClickWithPermissionCheck() {
        if (globalId == R.id.buttonComplaintTotal) {
            onClickTotalComplaint();
        } else if (globalId == R.id.buttonComplaintPending) {
            onClickComplaintPending();
        } else if (globalId == R.id.buttonUploadChallan) {
            //   onClickComplaintPending();
            onClickChallanUploadComplaintPending();
        } else if (globalId == R.id.buttonEditChallan) {
            onClickEditChallanComplaintList();
        } else if (globalId == R.id.buttonComplaintResolved) {
            onClickComplaintResolve();
        } else if (globalId == R.id.buttonReports) {
            startActivity(ReportsActivity.class);
        } else if (globalId == R.id.buttonSendToSeCenter) {
            onClickSendToSECenter();
        } else if (globalId == R.id.buttonAttendance) {
            startActivity(AttendanceDetailsActivity.class);
        } else if (globalId == R.id.buttonTrainingScheduleManager) {
            startActivity(TrainingScheduleListActivity.class);
        } else if (globalId == R.id.buttonTrainingScheduleSE) {
            startActivity(TrainingScheduleListForSEActivity.class);
        } else if (globalId == R.id.buttonInventory) {
            startActivity(InventoryActivity.class);
        } else if (globalId == R.id.buttonInventorySE) {
            startActivity(InventoryActivity.class);
        } else if (globalId == R.id.buttonFPSDetails) {
            startActivity(LastComplaintFPSListActivity.class);
        } else if (globalId == R.id.buttonResetDevice) {
            startActivity(ResetDeviceActivity.class);
        } else if (globalId == R.id.buttonPosDistributionReportForSE) {
            startActivity(DistributorPosReportForSEActivity.class);
        } else if (globalId == R.id.buttonPosDistribution) {
            startActivity(PosDistributionActivity.class);
        } else if (globalId == R.id.buttonPosIssue) {
            startActivity(PosIssueActivity.class);
        } else if (globalId == R.id.buttonPosHelpSE) {
            startActivity(FAQListActivity.class);
        } else if (globalId == R.id.ll_pos_error_show) {
            //  startActivity(ErrorPosActivity.class);
            startActivity(new Intent(mContext, ErrorPosActivity.class)
                    .putExtra("districtId", districtId));

        } else if (globalId == R.id.ll_installation_weiging_scale) {

            startActivity(WeighingScaleDashboard.class);
        } else if (globalId == R.id.lltIrisDeliveryInstallation) {

            startActivity(IrisDeliveryInstallationDashboard.class);
        }else if (globalId == R.id.lltClosedGuardDelivery) {

            startActivity(ClosedGuardDeliveryActivity.class);
        }else if (globalId == R.id.lltBiometricDeliveryDashboard) {
            startActivity(new Intent(mContext, BiometricDeliveryDashboardActivity.class)
                    .putExtra("districtId", districtId));

          ////  startActivity(BiometricDeliveryDashboardActivity.class);
        } else if (globalId == R.id.buttonEhr) {


            startActivity(com.callmangement.ehr.ehrActivities.MainActivity.class);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 102) {
            isGPS = getGpsStatus();
            if (isGPS && checkPermissionTypeStr.equalsIgnoreCase("next_process"))
                onClickWithPermissionCheck();
            else if (isGPS && checkPermissionTypeStr.equalsIgnoreCase("pie"))
                onValueSelectPieWithPermission();
        }
    }

    @Override
    public void onBackPressed() {

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage(getResources().getString(R.string.do_you_want_to_exit_from_this_app))
                .setCancelable(false)
                .setPositiveButton(getResources().getString(R.string.ok), (dialog, id) -> {
                    dialog.cancel();
                    finishAffinity();
                })
                .setNegativeButton(getResources().getString(R.string.cancel), (dialog, id) -> dialog.cancel());
        AlertDialog alert = builder.create();
        alert.setTitle(getResources().getString(R.string.alert));
        alert.show();
    }

    @Override
    protected void onStart() {
        final String locale = prefManager.getUSER_Change_Language();
        if (!locale.equals("")) {
            if (locale.equals("hi")) {
                setLocale("hi");
            } else {
                setLocale("en");
            }
        } else {
            prefManager.setUSER_Change_Language("en");
        }
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkLoginStatus();
    }

    public static int rgb(String hex) {
        int color = (int) Long.parseLong(hex.replace("#", ""), 16);
        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = (color >> 0) & 0xFF;
        return Color.rgb(r, g, b);
    }

    private void onValueSelectPieWithPermission() {
        if (entries.indexOf(globalEntryValue) == 0) {
            onClickComplaintPending();
        } else if (entries.indexOf(globalEntryValue) == 1) {
            onClickComplaintResolve();
        } else if (entries.indexOf(globalEntryValue) == 2) {
            onClickSendToSECenter();
        }
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {
        checkPermissionTypeStr = "pie";
        globalEntryValue = e;
        checkPermission();
    }

    @Override
    public void onNothingSelected() {
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onClick(View view) {
        globalId = view.getId();
        checkPermissionTypeStr = "next_process";
        checkPermission();
    }

    @Override
    public void onRefresh() {
        fetchDataByFilterType();
        binding.swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}