package com.callmangement.ui.attendance

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import com.callmangement.R
import com.callmangement.custom.CustomActivity
import com.callmangement.databinding.ActivityServiceEngineerListBinding
import com.callmangement.ui.complaint.ComplaintViewModel

class ServiceEngineerListActivity : CustomActivity() {
    private var binding: ActivityServiceEngineerListBinding? = null
    private var attendanceViewModel: AttendanceViewModel? = null
    private var viewModel: ComplaintViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_service_engineer_list)
        viewModel = ViewModelProviders.of(this).get(
            ComplaintViewModel::class.java
        )
        attendanceViewModel = ViewModelProviders.of(this).get(
            AttendanceViewModel::class.java
        )
        initView()
    }

    private fun initView() {
    }
}