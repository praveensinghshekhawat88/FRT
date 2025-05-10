package com.callmangement.ui.pos_issue.activity

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.callmangement.R
import com.callmangement.custom.CustomActivity
import com.callmangement.databinding.ActivityViewIssuesBinding
import com.callmangement.ui.pos_issue.adapter.ViewIssuesActivityAdapter

class ViewIssuesActivity : CustomActivity(), View.OnClickListener {
    private var binding: ActivityViewIssuesBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewIssuesBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        initView()
    }

    private fun initView() {
        binding!!.actionBar.ivBack.visibility = View.VISIBLE
        binding!!.actionBar.ivThreeDot.visibility = View.GONE
        binding!!.actionBar.layoutLanguage.visibility = View.GONE
        binding!!.actionBar.textToolbarTitle.text = resources.getString(R.string.view_issue)
        setUpOnClickListener()
        setUpAdapter()
    }

    private fun setUpOnClickListener() {
        binding!!.actionBar.ivBack.setOnClickListener(this)
    }

    private fun setUpAdapter() {
        binding!!.rvIssueList.layoutManager =
            LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false)
        binding!!.rvIssueList.adapter = ViewIssuesActivityAdapter(mContext!!)
    }

    override fun onClick(view: View) {
        val id = view.id
        if (id == R.id.iv_back) {
            onBackPressed()
        }
    }
}