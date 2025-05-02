package com.callmangement.ui.attendance;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import android.os.Bundle;
import com.callmangement.R;
import com.callmangement.custom.CustomActivity;
import com.callmangement.databinding.ActivityServiceEngineerListBinding;
import com.callmangement.ui.complaint.ComplaintViewModel;

public class ServiceEngineerListActivity extends CustomActivity {
    private ActivityServiceEngineerListBinding binding;
    private AttendanceViewModel attendanceViewModel;
    private ComplaintViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_service_engineer_list);
        viewModel = ViewModelProviders.of(this).get(ComplaintViewModel.class);
        attendanceViewModel = ViewModelProviders.of(this).get(AttendanceViewModel.class);
        initView();

    }

    private void initView() {

    }

}