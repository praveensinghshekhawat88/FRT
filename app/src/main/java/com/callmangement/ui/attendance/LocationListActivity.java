package com.callmangement.ui.attendance;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;

import com.callmangement.R;
import com.callmangement.adapter.LocationListActivityAdapter;
import com.callmangement.custom.CustomActivity;
import com.callmangement.databinding.ActivityLocationListBinding;
import com.callmangement.model.attendance.ModelAddLocationData;
import com.callmangement.model.attendance.ModelAttendanceData;
import com.callmangement.utils.EqualSpacingItemDecoration;
import com.callmangement.utils.PrefManager;

import java.util.List;

public class LocationListActivity extends CustomActivity {
    ActivityLocationListBinding binding;
    private LocationListActivityAdapter adapter;
    private ModelAttendanceData model;
    private AttendanceViewModel attendanceViewModel;
    private PrefManager prefManager;
    private final String userId = "0";
    private final String districtId = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_location_list);
        prefManager = new PrefManager(mContext);
        binding.actionBar.ivBack.setVisibility(View.VISIBLE);
        binding.actionBar.ivThreeDot.setVisibility(View.GONE);
        binding.actionBar.layoutLanguage.setVisibility(View.GONE);
        binding.actionBar.buttonPDF.setVisibility(View.GONE);
        binding.actionBar.textToolbarTitle.setText(getResources().getString(R.string.location));
        model = (ModelAttendanceData) getIntent().getSerializableExtra("param");
        attendanceViewModel = ViewModelProviders.of(this).get(AttendanceViewModel.class);

//        if (prefManager.getUSER_TYPE_ID().equals("4") && prefManager.getUSER_TYPE().equalsIgnoreCase("ServiceEngineer")) { // for service engineer
//            userId = prefManager.getUSER_Id();
//            districtId = prefManager.getUSER_DistrictId();
//        } else if (prefManager.getUSER_TYPE_ID().equalsIgnoreCase("6") && prefManager.getUSER_TYPE().equalsIgnoreCase("DSO")){ // for dso
//            userId = prefManager.getUSER_Id();
//            districtId = prefManager.getUSER_DistrictId();
//        } else {
//            userId = String.valueOf(model.getUser_Id());
//            districtId = String.valueOf(model.getDistrict_Id());
//        }

        initView();
        fetchLocationData();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void initView() {
        adapter = new LocationListActivityAdapter(mContext, model);
        adapter.notifyDataSetChanged();
        binding.rvLocation.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        binding.rvLocation.addItemDecoration(new EqualSpacingItemDecoration(30, EqualSpacingItemDecoration.VERTICAL));
        binding.rvLocation.setAdapter(adapter);

        binding.actionBar.ivBack.setOnClickListener(view -> onBackPressed());
    }

    private void fetchLocationData(){
        isLoading();
        attendanceViewModel.getLocationList(String.valueOf(model.getUser_Id()),String.valueOf(model.getDistrict_Id()), model.getPunch_In_Date()).observe(this, modelAddLocationList -> {
            isLoading();
            List<ModelAddLocationData> modelAddLocationData = modelAddLocationList.getLocation_list();
            if (modelAddLocationData != null && modelAddLocationData.size() > 0) {
                binding.rvLocation.setVisibility(View.VISIBLE);
                binding.textNoLocation.setVisibility(View.GONE);
                adapter.setData(modelAddLocationData);
            } else {
                binding.rvLocation.setVisibility(View.GONE);
                binding.textNoLocation.setVisibility(View.VISIBLE);
            }
        });
    }

    private void isLoading() {
        attendanceViewModel.getIsLoading().observe(this, aBoolean -> {
            if (aBoolean) {
                showProgress(getResources().getString(R.string.please_wait));
            } else {
                hideProgress();
            }
        });
    }
}