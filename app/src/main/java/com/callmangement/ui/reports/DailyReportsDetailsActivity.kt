package com.callmangement.ui.reports

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.callmangement.R
import com.callmangement.adapter.DailyReportsDetailsActivityAdapter
import com.callmangement.custom.CustomActivity
import com.callmangement.databinding.ActivityDailyReportsDetailsBinding
import com.callmangement.model.complaints.ModelComplaintList
import com.callmangement.utils.EqualSpacingItemDecoration
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class DailyReportsDetailsActivity : CustomActivity() {
    private var binding: ActivityDailyReportsDetailsBinding? = null
    private var adapter: DailyReportsDetailsActivityAdapter? = null
    private var list: List<ModelComplaintList>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_daily_reports_details)
        binding!!.actionBar.ivBack.visibility = View.VISIBLE
        binding!!.actionBar.ivThreeDot.visibility = View.GONE
        binding!!.actionBar.layoutLanguage.visibility = View.GONE
        binding!!.actionBar.buttonPDF.visibility = View.GONE
        binding!!.actionBar.textToolbarTitle.text = resources.getString(R.string.reports_details)
        initView()
    }

    private fun initView() {
        binding!!.actionBar.ivBack.setOnClickListener { view: View? -> onBackPressed() }
        setComplaintList()
    }

    @SuppressLint("SetTextI18n")
    private fun setComplaintList() {
        binding!!.rvComplaints.layoutManager =
            LinearLayoutManager(
                this@DailyReportsDetailsActivity,
                LinearLayoutManager.VERTICAL,
                false
            )
        binding!!.rvComplaints.addItemDecoration(
            EqualSpacingItemDecoration(
                30,
                EqualSpacingItemDecoration.VERTICAL
            )
        )

        var complainDataStr: String? = ""
        if (intent.getStringExtra("complain_data") != null) complainDataStr =
            intent.getStringExtra("complain_data")
        if (complainDataStr != "" && complainDataStr != "[]") {
            binding!!.rvComplaints.visibility = View.VISIBLE
            binding!!.textNoComplaint.visibility = View.GONE
            val gson = Gson()
            list = gson.fromJson(
                complainDataStr,
                object : TypeToken<List<ModelComplaintList?>?>() {}.type
            )
            adapter = DailyReportsDetailsActivityAdapter(this@DailyReportsDetailsActivity, list)
            binding!!.rvComplaints.adapter = adapter
            binding!!.textTotalComplaint.text = "" + list!!.size
        } else {
            binding!!.rvComplaints.visibility = View.GONE
            binding!!.bottomLayout.visibility = View.GONE
            binding!!.textNoComplaint.visibility = View.VISIBLE
            val gson = Gson()
            list = gson.fromJson(
                complainDataStr,
                object : TypeToken<List<ModelComplaintList?>?>() {}.type
            )
            adapter = DailyReportsDetailsActivityAdapter(this@DailyReportsDetailsActivity, list)
            binding!!.rvComplaints.adapter = adapter
        }

        binding!!.inputSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
            }

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                adapter!!.filter.filter(charSequence)
            }

            override fun afterTextChanged(editable: Editable) {
            }
        })
    }
}