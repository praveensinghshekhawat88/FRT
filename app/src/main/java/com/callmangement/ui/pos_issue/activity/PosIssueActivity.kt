package com.callmangement.ui.pos_issue.activity

import android.os.Bundle
import android.view.View
import com.callmangement.R
import com.callmangement.custom.CustomActivity
import com.callmangement.databinding.ActivityPosIssueBinding

class PosIssueActivity : CustomActivity(), View.OnClickListener {
    private var binding: ActivityPosIssueBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPosIssueBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        initView()
    }

    private fun initView() {
        setUpActionBar()
        setUpOnClickListener()
    }

    private fun setUpActionBar() {
        binding!!.actionBar.ivBack.visibility = View.VISIBLE
        binding!!.actionBar.ivThreeDot.visibility = View.GONE
        binding!!.actionBar.layoutLanguage.visibility = View.GONE
        binding!!.actionBar.textToolbarTitle.text = resources.getString(R.string.pos_issue)
    }

    private fun setUpOnClickListener() {
        binding!!.actionBar.ivBack.setOnClickListener(this)
        binding!!.buttonViewIssue.setOnClickListener(this)
        binding!!.buttonRegisterIssue.setOnClickListener(this)
    }

    override fun onClick(view: View) {
        val id = view.id
        if (id == R.id.buttonViewIssue) {
            startActivity(ViewIssuesActivity::class.java)
        } else if (id == R.id.buttonRegisterIssue) {
            startActivity(RegisterIssueActivity::class.java)
        } else if (id == R.id.iv_back) {
            onBackPressed()
        }
    }
}