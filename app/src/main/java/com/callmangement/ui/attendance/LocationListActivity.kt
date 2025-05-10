package com.callmangement.ui.attendance

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.callmangement.R
import com.callmangement.adapter.LocationListActivityAdapter
import com.callmangement.custom.CustomActivity
import com.callmangement.databinding.ActivityLocationListBinding
import com.callmangement.model.attendance.ModelAddLocationList
import com.callmangement.model.attendance.ModelAttendanceData
import com.callmangement.utils.EqualSpacingItemDecoration
import com.callmangement.utils.PrefManager

class LocationListActivity : CustomActivity() {
    
    var binding: ActivityLocationListBinding? = null
    private var adapter: LocationListActivityAdapter? = null
    private var model: ModelAttendanceData? = null
    private var attendanceViewModel: AttendanceViewModel? = null
    private var prefManager: PrefManager? = null
    private val userId = "0"
    private val districtId = "0"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_location_list)
        mContext = this
        prefManager = PrefManager(mContext!!)
        binding!!.actionBar.ivBack.visibility = View.VISIBLE
        binding!!.actionBar.ivThreeDot.visibility = View.GONE
        binding!!.actionBar.layoutLanguage.visibility = View.GONE
        binding!!.actionBar.buttonPDF.visibility = View.GONE
        binding!!.actionBar.textToolbarTitle.text = resources.getString(R.string.location)
        model = intent.getSerializableExtra("param") as ModelAttendanceData?
        attendanceViewModel = ViewModelProviders.of(this).get(
            AttendanceViewModel::class.java
        )

        //        if (prefManager.getUSER_TYPE_ID().equals("4") && prefManager.getUSER_TYPE().equalsIgnoreCase("ServiceEngineer")) { // for service engineer
//            userId = prefManager.getUSER_Id();
//            districtId = prefManager.getUSER_DistrictId();
//        } else if (prefManager.getUSER_TYPE_ID().equalsIgnoreCase("6") && prefManager.getUSER_TYPE().equalsIgnoreCase("DSO")){ // for dso
//            userId = prefManager.getUSER_Id();
//            districtId = prefManager.getUSER_DistrictId();
//        } else {
//            userId = String.valueOf(model.getUser_Id());
//            districtId = String.valueOf(model.getDistrict_Id());
//        }
        initView()
        fetchLocationData()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun initView() {
        adapter = LocationListActivityAdapter(mContext!!, model!!)
        adapter!!.notifyDataSetChanged()
        binding!!.rvLocation.layoutManager =
            LinearLayoutManager(mContext!!, LinearLayoutManager.VERTICAL, false)
        binding!!.rvLocation.addItemDecoration(
            EqualSpacingItemDecoration(
                30,
                EqualSpacingItemDecoration.VERTICAL
            )
        )
        binding!!.rvLocation.adapter = adapter

        binding!!.actionBar.ivBack.setOnClickListener { view: View? -> onBackPressed() }
    }

    private fun fetchLocationData() {
        isLoading()
        attendanceViewModel!!.getLocationList(
            model!!.user_Id.toString(),
            model!!.district_Id.toString(),
            model!!.punch_In_Date!!
        ).observe(
            this
        ) { modelAddLocationList: ModelAddLocationList? ->
            isLoading()
            val modelAddLocationData = modelAddLocationList!!.location_list
            if (modelAddLocationData != null && modelAddLocationData.size > 0) {
                binding!!.rvLocation.visibility = View.VISIBLE
                binding!!.textNoLocation.visibility = View.GONE
                adapter!!.setData(modelAddLocationData)
            } else {
                binding!!.rvLocation.visibility = View.GONE
                binding!!.textNoLocation.visibility = View.VISIBLE
            }
        }
    }

    private fun isLoading() {
            attendanceViewModel!!.isLoading.observe(this) { aBoolean ->
                if (aBoolean) {
                    showProgress(resources.getString(R.string.please_wait))
                } else {
                    hideProgress()
                }
            }
        }
}