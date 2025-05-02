package com.callmangement.ui.attendance

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.callmangement.R
import com.callmangement.adapter.DistrictListActivityAdapter
import com.callmangement.custom.CustomActivity
import com.callmangement.databinding.ActivityDistrictListBinding
import com.callmangement.model.district.ModelDistrict
import com.callmangement.model.district.ModelDistrictList
import com.callmangement.ui.complaint.ComplaintViewModel

class DistrictListActivity : CustomActivity() {
    private var binding: ActivityDistrictListBinding? = null
    private var adapter: DistrictListActivityAdapter? = null
    private var viewModel: ComplaintViewModel? = null
    private var district_List: MutableList<ModelDistrictList>? = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_district_list)
        viewModel = ViewModelProviders.of(this).get(
            ComplaintViewModel::class.java
        )
        initView()
        districtList()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun initView() {
        adapter = DistrictListActivityAdapter(mContext!!)
        adapter!!.notifyDataSetChanged()
        binding!!.rvDistrictList.layoutManager =
            LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false)
        binding!!.rvDistrictList.adapter = adapter
    }

    private fun districtList() {
        isLoading
        viewModel!!.district!!.observe(this) { modelDistrict: ModelDistrict? ->
            isLoading
            if (modelDistrict!!.status == "200") {
                district_List = modelDistrict!!.district_List
                adapter!!.setData(district_List)
            }
        }
    }

    private val isLoading: Unit
        get() {
            viewModel!!.isLoading!!.observe(this) { aBoolean: Boolean? ->
                if (aBoolean!!) {
                    showProgress(resources.getString(R.string.please_wait))
                } else {
                    hideProgress()
                }
            }
        }
}