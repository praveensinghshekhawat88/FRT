package com.callmangement.ui.training_schedule;

import androidx.databinding.DataBindingUtil;

import android.os.Bundle;

import com.callmangement.R;
import com.callmangement.custom.CustomActivity;
import com.callmangement.databinding.ActivityTrainingScheduleDetailsBinding;

public class TrainingScheduleDetailsActivity extends CustomActivity {
    ActivityTrainingScheduleDetailsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_training_schedule_details);
        initView();
    }

    private void initView() {

    }
}