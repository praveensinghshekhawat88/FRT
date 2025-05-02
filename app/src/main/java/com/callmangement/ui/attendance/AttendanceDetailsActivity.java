package com.callmangement.ui.attendance;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import com.callmangement.R;
import com.callmangement.adapter.AttendanceDetailsActivityAdapter;
import com.callmangement.custom.CustomActivity;
import com.callmangement.databinding.ActivityAttendanceDetailsBinding;
import com.callmangement.model.attendance.ModelAttendanceData;
import com.callmangement.model.district.ModelDistrictList;
import com.callmangement.ui.complaint.ComplaintViewModel;
import com.callmangement.utils.EqualSpacingItemDecoration;
import com.callmangement.utils.PrefManager;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class AttendanceDetailsActivity extends CustomActivity {
    ActivityAttendanceDetailsBinding binding;
    private final Calendar myCalendarFromDate = Calendar.getInstance();
    private final Calendar myCalendarToDate = Calendar.getInstance();
    private final String myFormat = "yyyy-MM-dd";
    private ComplaintViewModel viewModel;
    private AttendanceViewModel attendanceViewModel;
    private AttendanceDetailsActivityAdapter adapter;
    private PrefManager prefManager;
    private String districtId = "0";
    private int checkDistrict = 0;
    private String districtNameEng = "";
    private List<ModelDistrictList> district_List = new ArrayList<>();
    private String fromDate = "";
    private String toDate = "";
    private String userId = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_attendance_details);
        prefManager = new PrefManager(mContext);
        binding.actionBar.ivBack.setVisibility(View.VISIBLE);
        binding.actionBar.ivThreeDot.setVisibility(View.GONE);
        binding.actionBar.layoutLanguage.setVisibility(View.GONE);
        binding.actionBar.buttonPDF.setVisibility(View.GONE);
        binding.actionBar.textToolbarTitle.setText(getResources().getString(R.string.frt_punch_in));
        viewModel = ViewModelProviders.of(this).get(ComplaintViewModel.class);
        attendanceViewModel = ViewModelProviders.of(this).get(AttendanceViewModel.class);
        districtNameEng = prefManager.getUSER_District();

        if (prefManager.getUSER_TYPE_ID().equals("4") && prefManager.getUSER_TYPE().equalsIgnoreCase("ServiceEngineer")) { // for service engineer
            districtId = prefManager.getUSER_DistrictId();
            userId = prefManager.getUSER_Id();
            binding.rlDistrict.setVisibility(View.GONE);
        } else if (prefManager.getUSER_TYPE_ID().equalsIgnoreCase("6") && prefManager.getUSER_TYPE().equalsIgnoreCase("DSO")){ // for dso
            districtId = prefManager.getUSER_DistrictId();
            userId = prefManager.getUSER_Id();
            binding.rlDistrict.setVisibility(View.GONE);
        } else {
            userId = "0";
            districtList();
            binding.rlDistrict.setVisibility(View.VISIBLE);
        }
        initView();
        fetchDataFromCurrentMonth();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void initView() {
        adapter = new AttendanceDetailsActivityAdapter(mContext);
        adapter.notifyDataSetChanged();
        binding.rvMarkAttendance.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        binding.rvMarkAttendance.addItemDecoration(new EqualSpacingItemDecoration(30, EqualSpacingItemDecoration.VERTICAL));
        binding.rvMarkAttendance.setAdapter(adapter);

        binding.spinnerDistrict.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(++checkDistrict > 1) {
                    districtNameEng = district_List.get(i).getDistrictNameEng();
                    districtId = district_List.get(i).getDistrictId();
                    if (districtNameEng.equalsIgnoreCase("--"+getResources().getString(R.string.district)+"--")) {
                        districtId = "0";
                    }
                    fetchAttendanceList(districtId);
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
            DatePickerDialog datePickerDialog = new  DatePickerDialog(mContext, dateFromDate, myCalendarFromDate
                    .get(Calendar.YEAR), myCalendarFromDate.get(Calendar.MONTH),
                    myCalendarFromDate.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis() - 1000);
            datePickerDialog.show();
        });

        binding.textToDate.setOnClickListener(view -> {
            DatePickerDialog datePickerDialog = new  DatePickerDialog(mContext, dateToDate, myCalendarToDate
                    .get(Calendar.YEAR), myCalendarToDate.get(Calendar.MONTH),
                    myCalendarToDate.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis() - 1000);
            datePickerDialog.show();
        });

        binding.actionBar.ivBack.setOnClickListener(view -> onBackPressed());

    }

    private void updateLabelFromDate() {
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        binding.textFromDate.setText(sdf.format(myCalendarFromDate.getTime()));
        fromDate = Objects.requireNonNull(binding.textFromDate.getText()).toString().trim();
        Objects.requireNonNull(binding.textToDate.getText()).clear();
    }

    private void updateLabelToDate() {
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        binding.textToDate.setText(sdf.format(myCalendarToDate.getTime()));
        fromDate = Objects.requireNonNull(binding.textFromDate.getText()).toString().trim();
        toDate = Objects.requireNonNull(binding.textToDate.getText()).toString().trim();
        fetchAttendanceList(districtId);
    }

    private void fetchDataFromCurrentMonth(){
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        Date firstDateOfCurrentMonth = calendar.getTime();
        Calendar calendarToday = Calendar.getInstance();
        Date today = calendarToday.getTime();
        String firstDateOfCurrentMonthInAPI = sdf.format(firstDateOfCurrentMonth);
        String todayDateInAPI = sdf.format(today);
        fromDate = firstDateOfCurrentMonthInAPI;
        toDate = todayDateInAPI;
        fetchAttendanceList(districtId);
    }

    private void fetchAttendanceList(String districtId) {
        isLoading();
        attendanceViewModel.getAttendanceList(userId,districtId,fromDate, toDate).observe(this, modelAttendanceList -> {
            isLoading();
            List<ModelAttendanceData> modelAttendanceData = modelAttendanceList.getData();
            if (modelAttendanceData != null && modelAttendanceData.size() > 0) {
                binding.rvMarkAttendance.setVisibility(View.VISIBLE);
                binding.textNoAttendance.setVisibility(View.GONE);
                adapter.setData(modelAttendanceData);
            } else {
                binding.rvMarkAttendance.setVisibility(View.GONE);
                binding.textNoAttendance.setVisibility(View.VISIBLE);
            }
        });
    }

    private void districtList(){
        viewModel.getDistrict().observe(this, modelDistrict -> {
            if (modelDistrict.getStatus().equals("200")){
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

    private void isLoading() {
        viewModel.getIsLoading().observe(this, aBoolean -> {
            if (aBoolean) {
                showProgress(getResources().getString(R.string.please_wait));
            } else {
                hideProgress();
            }
        });
    }

}