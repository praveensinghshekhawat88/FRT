package com.callmangement.ui.training_schedule;

import androidx.databinding.DataBindingUtil;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;

import com.callmangement.R;
import com.callmangement.custom.CustomActivity;
import com.callmangement.databinding.ActivityTrainingScheduleMainForManagerBinding;

public class TrainingScheduleMainActivityForManager extends CustomActivity implements View.OnClickListener {
    private ActivityTrainingScheduleMainForManagerBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_training_schedule_main_for_manager);
        initView();
    }

    private void initView() {
        onClickListener();
        setUpData();
    }

    private void setUpData(){
        binding.actionBar.ivBack.setVisibility(View.VISIBLE);
        binding.actionBar.ivThreeDot.setVisibility(View.GONE);
        binding.actionBar.layoutLanguage.setVisibility(View.GONE);
        binding.actionBar.textToolbarTitle.setText(getResources().getString(R.string.training_schedule));
    }

    private void onClickListener(){
        binding.actionBar.ivBack.setOnClickListener(this);
        binding.buttonCreateTrainingSchedule.setOnClickListener(this);
        binding.buttonEditRemoveTrainingSchedule.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.buttonCreateTrainingSchedule) {
            startActivity(CreateTrainingScheduleActivity.class);
        } else if (id == R.id.buttonEditRemoveTrainingSchedule) {
            startActivity(TrainingScheduleListActivity.class);
        } else if (id == R.id.iv_back) {
            onBackPressed();
        }
    }
}