package com.callmangement.ehr.ehrActivities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.recyclerview.widget.GridLayoutManager
import com.callmangement.R
import com.callmangement.databinding.ActivityViewCampDetailsBinding
import com.callmangement.ehr.adapter.CampUploadedImagesListingAdapter
import com.callmangement.ehr.models.CampDetailsInfo
import com.callmangement.ehr.models.CampDocInfo
import com.callmangement.ehr.support.EqualSpacingItemDecoration
import com.callmangement.utils.PrefManager

class ViewCampDetailsActivity : BaseActivity() {
    private var mActivity: Activity? = null
    private var binding: ActivityViewCampDetailsBinding? = null
    private var preference: PrefManager? = null
    private var campDocInfoArrayList: ArrayList<CampDocInfo>? = null
    private var adapter: CampUploadedImagesListingAdapter? = null
    private var campDetailsInfo: CampDetailsInfo? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        binding = ActivityViewCampDetailsBinding.inflate(
            layoutInflater
        )
        setContentView(binding!!.root)
        init()
    }

    private fun init() {
        mActivity = this
        preference = PrefManager(mActivity!!)

        val bundle = intent.extras
        campDocInfoArrayList = bundle!!.getSerializable("mylist") as ArrayList<CampDocInfo>?
        campDetailsInfo = bundle.getSerializable("campDetails") as CampDetailsInfo?

        binding!!.actionBar.ivBack.visibility = View.VISIBLE
        binding!!.actionBar.ivThreeDot.visibility = View.GONE
        binding!!.actionBar.layoutLanguage.visibility = View.GONE
        binding!!.actionBar.buttonPDF.visibility = View.GONE
        binding!!.actionBar.textToolbarTitle.text = resources.getString(R.string.view_camp_details)

        setUpData()
        setClickListener()
    }

    private fun setUpData() {
        if (campDocInfoArrayList != null && campDocInfoArrayList!!.size > 0) {
            binding!!.txtViewUploadedImages.visibility = View.VISIBLE
            binding!!.rvAllCamps.visibility = View.VISIBLE
        } else {
            binding!!.txtViewUploadedImages.visibility = View.GONE
            binding!!.txtViewUploadedImages.visibility = View.GONE
        }

        adapter = CampUploadedImagesListingAdapter(
            mActivity!!,
            campDocInfoArrayList,
            onItemViewClickListener
        )
        binding!!.rvAllCamps.setHasFixedSize(true)
        val layoutManager = GridLayoutManager(mActivity, 3)
        binding!!.rvAllCamps.layoutManager = layoutManager
        binding!!.rvAllCamps.addItemDecoration(
            EqualSpacingItemDecoration(
                30,
                EqualSpacingItemDecoration.VERTICAL
            )
        )
        binding!!.rvAllCamps.adapter = adapter

        if (campDetailsInfo != null && campDetailsInfo!!.startDate != null) binding!!.inputStartDateTime.text =
            campDetailsInfo!!.startDate

        if (campDetailsInfo != null && campDetailsInfo!!.endDate != null) binding!!.inputEndDateTime.text =
            campDetailsInfo!!.endDate

        if (campDetailsInfo != null && campDetailsInfo!!.organizeDate != null) binding!!.inputOrganiseDateTime.text =
            campDetailsInfo!!.organizeDate

        if (campDetailsInfo != null && campDetailsInfo!!.districtNameEng != null) binding!!.inputDistrict.text =
            campDetailsInfo!!.districtNameEng

        if (campDetailsInfo != null && campDetailsInfo!!.blockName != null) binding!!.inputBlock.text =
            campDetailsInfo!!.blockName.toString()

        if (campDetailsInfo != null && campDetailsInfo!!.address != null) binding!!.inputPlace.text =
            campDetailsInfo!!.address

        if (campDetailsInfo != null && campDetailsInfo!!.description != null) binding!!.inputDescription.text =
            campDetailsInfo!!.description
    }

    var onItemViewClickListener: CampUploadedImagesListingAdapter.OnItemViewClickListener =
        object : CampUploadedImagesListingAdapter.OnItemViewClickListener {
            override fun onItemClick(campDocInfo: CampDocInfo?, position: Int) {
                startActivity(
                    Intent(mActivity, ZoomInZoomOutActivity::class.java).putExtra(
                        "image",
                        campDocInfo!!.documentPath
                    )
                )
            }
        }

    private fun setClickListener() {
        binding!!.actionBar.ivBack.setOnClickListener { view: View? -> onBackPressed() }
    }
}