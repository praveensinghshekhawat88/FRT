package com.callmangement.ui.reports

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.callmangement.network.APIService
import com.callmangement.network.RetrofitInstance
import com.callmangement.R
import com.callmangement.adapter.FPSRepeatOnServiceCenterDetailActivityAdapter
import com.callmangement.custom.CustomActivity
import com.callmangement.databinding.ActivityFpsrepeatOnServiceCenterDetailBinding
import com.callmangement.model.fps_repeat_on_service_center.ModelRepeatFpsComplaints
import com.callmangement.model.fps_repeat_on_service_center.ModelRepeatFpsComplaintsList
import com.callmangement.utils.Constants
import com.callmangement.utils.PrefManager
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FPSRepeatOnServiceCenterDetailActivity : CustomActivity(), View.OnClickListener {

    
    private var binding: ActivityFpsrepeatOnServiceCenterDetailBinding? = null
    private var prefManager: PrefManager? = null
    private var model: ModelRepeatFpsComplaintsList? = null
    private var list: List<ModelRepeatFpsComplaintsList> = ArrayList()
    private var districtId = "0"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFpsrepeatOnServiceCenterDetailBinding.inflate(
            layoutInflater
        )
        setContentView(binding!!.root)
        initView()
    }

    private fun initView() {

        mContext = this
        binding!!.actionBar.ivBack.visibility = View.VISIBLE
        binding!!.actionBar.ivThreeDot.visibility = View.GONE
        binding!!.actionBar.layoutLanguage.visibility = View.GONE
        binding!!.actionBar.buttonPDF.visibility = View.GONE
        binding!!.actionBar.textToolbarTitle.text =
            resources.getString(R.string.count_on_service_center)
        prefManager = PrefManager(mContext!!)
        model = intent.getSerializableExtra("param") as ModelRepeatFpsComplaintsList?
        districtId = prefManager!!.useR_DistrictId!!
        setUpOnClickListener()
        fetchData()
    }

    private fun setUpOnClickListener() {
        binding!!.actionBar.ivBack.setOnClickListener(this)
        //        binding.actionBar.buttonPDF.setOnClickListener(this);
    }

    private fun fetchData() {
        if (Constants.isNetworkAvailable(mContext!!)) {
            showProgress()
            val service = RetrofitInstance.getRetrofitInstance().create(
                APIService::class.java
            )
            val call = service.RepaetFPSComplainsOnSCList(
                prefManager!!.useR_Id,
                "",
                "",
                districtId,
                model!!.fpscode
            )
            call.enqueue(object : Callback<ResponseBody?> {
                @SuppressLint("SetTextI18n")
                override fun onResponse(
                    call: Call<ResponseBody?>,
                    response: Response<ResponseBody?>
                ) {
                    hideProgress()
                    if (response.isSuccessful) {
                        try {
                            if (response.code() == 200) {
                                if (response.body() != null) {
                                    val responseStr = response.body()!!.string()
                                    val modelResponse = getObject(
                                        responseStr,
                                        ModelRepeatFpsComplaints::class.java
                                    ) as ModelRepeatFpsComplaints
                                    if (modelResponse != null) {
                                        if (modelResponse.status == "200") {
                                            if (modelResponse.parts.size > 0) {
                                                list = modelResponse.parts
                                                //                                                Constants.modelRepeatFpsComplaintsList = list;
                                                setUpAdapter(list)
                                            } else {
                                                binding!!.rvFpsRepeatDetail.visibility = View.GONE
                                                binding!!.tvNoDataFound.visibility = View.VISIBLE
                                            }
                                        } else {
                                            binding!!.rvFpsRepeatDetail.visibility = View.GONE
                                            binding!!.tvNoDataFound.visibility = View.VISIBLE
                                        }
                                    } else {
                                        binding!!.rvFpsRepeatDetail.visibility = View.GONE
                                        binding!!.tvNoDataFound.visibility = View.VISIBLE
                                    }
                                } else {
                                    binding!!.rvFpsRepeatDetail.visibility = View.GONE
                                    binding!!.tvNoDataFound.visibility = View.VISIBLE
                                }
                            } else {
                                binding!!.rvFpsRepeatDetail.visibility = View.GONE
                                binding!!.tvNoDataFound.visibility = View.VISIBLE
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                            binding!!.rvFpsRepeatDetail.visibility = View.GONE
                            binding!!.tvNoDataFound.visibility = View.VISIBLE
                        }
                    } else {
                        makeToast(resources.getString(R.string.error))
                        binding!!.rvFpsRepeatDetail.visibility = View.GONE
                        binding!!.tvNoDataFound.visibility = View.VISIBLE
                    }
                }

                override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                    hideProgress()
                    makeToast(resources.getString(R.string.error_message))
                    binding!!.rvFpsRepeatDetail.visibility = View.GONE
                    binding!!.tvNoDataFound.visibility = View.VISIBLE
                }
            })
        } else {
            makeToast(resources.getString(R.string.no_internet_connection))
        }
    }

    private fun setUpAdapter(list: List<ModelRepeatFpsComplaintsList>) {
        if (list.isNotEmpty()) {
            binding!!.rvFpsRepeatDetail.visibility = View.VISIBLE
            binding!!.tvNoDataFound.visibility = View.GONE
            binding!!.rvFpsRepeatDetail.layoutManager =
                LinearLayoutManager(mContext!!, LinearLayoutManager.VERTICAL, false)
            binding!!.rvFpsRepeatDetail.adapter =
                FPSRepeatOnServiceCenterDetailActivityAdapter(mContext!!, list)
            binding!!.textTotalCount.text = list.size.toString()
        } else {
            binding!!.rvFpsRepeatDetail.visibility = View.GONE
            binding!!.tvNoDataFound.visibility = View.VISIBLE
            binding!!.textTotalCount.text = "0"
        }
    }

    fun repeatFpsComplaintDetail(model: ModelRepeatFpsComplaintsList?) {
        val intent = Intent(mContext!!, RepeatFpsComplaintDetailActivity::class.java)
        intent.putExtra("param", model)
        startActivity(intent)
    }

    override fun onClick(view: View) {
        val id = view.id
        if (id == R.id.iv_back) {
            onBackPressed()
        } /*else if (id == R.id.buttonPDF){
            if (Constants.modelRepeatFpsComplaintsList != null && Constants.modelRepeatFpsComplaintsList.size() > 0){
                startActivity(new Intent(mContext!!, FPSRepeatOnServiceCenterPDFActivity.class));
                finish();
            }
        }*/
    }
}