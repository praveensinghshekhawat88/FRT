package com.callmangement.ui.distributor.activity

import android.os.Bundle
import android.view.View
import com.callmangement.R
import com.callmangement.custom.CustomActivity
import com.callmangement.databinding.ActivityPosDistributionBinding
import com.callmangement.ui.distributor.activity.DistributedPosListActivity
import com.callmangement.utils.PrefManager

class PosDistributionActivity : CustomActivity(), View.OnClickListener {
    private var binding: ActivityPosDistributionBinding? = null
    private var prefManager: PrefManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPosDistributionBinding.inflate(
            layoutInflater
        )
        setContentView(binding!!.root)
        binding!!.actionBar.ivBack.visibility = View.VISIBLE
        binding!!.actionBar.ivThreeDot.visibility = View.GONE
        binding!!.actionBar.layoutLanguage.visibility = View.GONE
        binding!!.actionBar.textToolbarTitle.text = resources.getString(R.string.pos_distribution)
        prefManager = PrefManager(mContext)
        initView()
    }

    private fun initView() {
        setUpOnClickListener()
    }

    private fun setUpOnClickListener() {
        binding!!.actionBar.ivBack.setOnClickListener(this)
        binding!!.buttonPosDistributionReport.setOnClickListener(this)
        binding!!.buttonDistributedPosList.setOnClickListener(this)
    }

    override fun onClick(view: View) {
        val id = view.id
        if (id == R.id.iv_back) {
            onBackPressed()
        } else if (id == R.id.buttonPosDistributionReport) {
            startActivity(PosDistributionReportActivity::class.java)
        } else if (id == R.id.buttonDistributedPosList) {
            startActivity(DistributedPosListActivity::class.java)
        }
    }
}