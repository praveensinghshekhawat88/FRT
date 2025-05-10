package com.callmangement.ui.training_schedule

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.callmangement.R
import com.callmangement.custom.CustomActivity
import com.callmangement.databinding.ActivityTrainingScheduleDetailsBinding

class TrainingScheduleDetailsActivity : CustomActivity() {
    var binding: ActivityTrainingScheduleDetailsBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_training_schedule_details)
        initView()
    }

    private fun initView() {
    }
}