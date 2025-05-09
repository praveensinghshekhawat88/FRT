package com.callmangement.ui.reports

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.bumptech.glide.Glide
import com.callmangement.R
import com.callmangement.custom.CustomActivity
import com.callmangement.databinding.ActivityRepeatFpsComplaintDetailBinding
import com.callmangement.model.fps_repeat_on_service_center.ModelRepeatFpsComplaintsList
import com.callmangement.ui.home.ZoomInZoomOutActivity
import com.callmangement.utils.Constants

class RepeatFpsComplaintDetailActivity : CustomActivity(), View.OnClickListener {

    
    private var binding: ActivityRepeatFpsComplaintDetailBinding? = null
    private var model: ModelRepeatFpsComplaintsList? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRepeatFpsComplaintDetailBinding.inflate(
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
        binding!!.actionBar.textToolbarTitle.text = resources.getString(R.string.complaint_details)
        intentData
        setUpOnClickListener()
    }

    private val intentData: Unit
        get() {
            model = intent.getSerializableExtra("param") as ModelRepeatFpsComplaintsList?
            binding!!.data = model
            binding!!.chkBoxIsPhysicalDamage.isChecked =
                model!!.isPhysicalDamage != null && model!!.isPhysicalDamage
            if (!model!!.imagePath.isEmpty()) {
                binding!!.seImage.visibility = View.VISIBLE
                Glide.with(mContext!!)
                    .load(Constants.API_BASE_URL + model!!.imagePath)
                    .placeholder(R.drawable.image_not_fount)
                    .into(binding!!.seImage)
            } else {
                binding!!.seImage.visibility = View.GONE
            }
        }

    private fun setUpOnClickListener() {
        binding!!.actionBar.ivBack.setOnClickListener(this)
        binding!!.seImage.setOnClickListener(this)
    }

    override fun onClick(view: View) {
        val id = view.id
        if (id == R.id.se_image) {
            startActivity(
                Intent(mContext!!, ZoomInZoomOutActivity::class.java).putExtra(
                    "image",
                    model!!.imagePath
                )
            )
        } else if (id == R.id.iv_back) {
            onBackPressed()
        }
    }
}