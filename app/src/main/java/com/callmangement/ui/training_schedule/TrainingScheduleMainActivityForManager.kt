package com.callmangement.ui.training_schedule

import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import com.callmangement.R
import com.callmangement.custom.CustomActivity
import com.callmangement.databinding.ActivityTrainingScheduleMainForManagerBinding

class TrainingScheduleMainActivityForManager : CustomActivity(), View.OnClickListener {
    private var binding: ActivityTrainingScheduleMainForManagerBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(
            this,
            R.layout.activity_training_schedule_main_for_manager
        )
        initView()
    }

    private fun initView() {
        onClickListener()
        setUpData()
    }

    private fun setUpData() {
        binding!!.actionBar.ivBack.visibility = View.VISIBLE
        binding!!.actionBar.ivThreeDot.visibility = View.GONE
        binding!!.actionBar.layoutLanguage.visibility = View.GONE
        binding!!.actionBar.textToolbarTitle.text = resources.getString(R.string.training_schedule)
    }

    private fun onClickListener() {
        binding!!.actionBar.ivBack.setOnClickListener(this)
        binding!!.buttonCreateTrainingSchedule.setOnClickListener(this)
        binding!!.buttonEditRemoveTrainingSchedule.setOnClickListener(this)
    }

    override fun onClick(view: View) {
        val id = view.id
        if (id == R.id.buttonCreateTrainingSchedule) {
            startActivity(CreateTrainingScheduleActivity::class.java)
        } else if (id == R.id.buttonEditRemoveTrainingSchedule) {
            startActivity(TrainingScheduleListActivity::class.java)
        } else if (id == R.id.iv_back) {
            onBackPressed()
        }
    }
}