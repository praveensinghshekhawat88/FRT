package com.callmangement.ehr.ehrActivities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.recyclerview.widget.GridLayoutManager
import com.callmangement.R
import com.callmangement.databinding.ActivityViewSurveyFormDetailsBinding
import com.callmangement.ehr.adapter.SurveyFormUploadedImagesListingAdapter
import com.callmangement.ehr.models.SurveyFormDetailsInfo
import com.callmangement.ehr.models.SurveyFormDocInfo
import com.callmangement.ehr.support.EqualSpacingItemDecoration
import com.callmangement.utils.PrefManager
import java.text.ParseException
import java.text.SimpleDateFormat

class ViewSurveyFormDetailsActivity : BaseActivity() {
    private var mActivity: Activity? = null
    private var binding: ActivityViewSurveyFormDetailsBinding? = null
    private var preference: PrefManager? = null
    private var surveyFormDocInfoArrayList: ArrayList<SurveyFormDocInfo>? = null
    private var adapter: SurveyFormUploadedImagesListingAdapter? = null
    private var surveyFormDetailsInfo: SurveyFormDetailsInfo? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        binding = ActivityViewSurveyFormDetailsBinding.inflate(
            layoutInflater
        )
        setContentView(binding!!.root)
        init()
    }

    private fun init() {
        mActivity = this
        preference = PrefManager(mActivity!!)

        val bundle = intent.extras
        surveyFormDocInfoArrayList =
            bundle!!.getSerializable("mylist") as ArrayList<SurveyFormDocInfo>?
        surveyFormDetailsInfo =
            bundle.getSerializable("surveyFormDetails") as SurveyFormDetailsInfo?

        binding!!.actionBar.ivBack.visibility = View.VISIBLE
        binding!!.actionBar.ivThreeDot.visibility = View.GONE
        binding!!.actionBar.layoutLanguage.visibility = View.GONE
        binding!!.actionBar.buttonPDF.visibility = View.GONE
        binding!!.actionBar.textToolbarTitle.text =
            resources.getString(R.string.view_survey_form_details)

        setUpData()
        setClickListener()
    }

    private fun setUpData() {
        if (surveyFormDocInfoArrayList != null && surveyFormDocInfoArrayList!!.size > 0) {
            binding!!.txtViewUploadedImages.visibility = View.VISIBLE
            binding!!.rvAllSurveyForms.visibility = View.VISIBLE
        } else {
            binding!!.txtViewUploadedImages.visibility = View.GONE
            binding!!.txtViewUploadedImages.visibility = View.GONE
        }

        adapter = SurveyFormUploadedImagesListingAdapter(
            mActivity!!,
            surveyFormDocInfoArrayList,
            onItemViewClickListener
        )
        binding!!.rvAllSurveyForms.setHasFixedSize(true)
        val layoutManager = GridLayoutManager(mActivity, 3)
        binding!!.rvAllSurveyForms.layoutManager = layoutManager
        binding!!.rvAllSurveyForms.addItemDecoration(
            EqualSpacingItemDecoration(
                30,
                EqualSpacingItemDecoration.VERTICAL
            )
        )
        binding!!.rvAllSurveyForms.adapter = adapter

        if (surveyFormDetailsInfo!!.customerName != null) binding!!.customerName.text =
            surveyFormDetailsInfo!!.customerName

        if (surveyFormDetailsInfo!!.bill_ChallanNo != null) binding!!.billChallanNo.text =
            surveyFormDetailsInfo!!.bill_ChallanNo

        if (surveyFormDetailsInfo!!.address != null) binding!!.customerAddress.text =
            surveyFormDetailsInfo!!.address

        if (surveyFormDetailsInfo!!.billRemark != null) binding!!.billRemarks.text =
            surveyFormDetailsInfo!!.billRemark

        if (surveyFormDetailsInfo!!.pointOfContact != null) binding!!.pointOfContact.text =
            surveyFormDetailsInfo!!.pointOfContact

        if (surveyFormDetailsInfo!!.mobileNumber != null) binding!!.mobileNumber.text =
            surveyFormDetailsInfo!!.mobileNumber

        if (surveyFormDetailsInfo!!.installationDateStr != null) {
            val input = SimpleDateFormat("dd-MM-yyyy")
            val output = SimpleDateFormat("yyyy-MM-dd")
            try {
                val oneWayTripDate =
                    input.parse(surveyFormDetailsInfo!!.installationDateStr) // parse input
                binding!!.dateOfInstallation.text = output.format(oneWayTripDate) // format output
            } catch (e: ParseException) {
                e.printStackTrace()
            }
        }

        if (surveyFormDetailsInfo!!.typeOfCall != null) binding!!.typeOfCall.text =
            surveyFormDetailsInfo!!.typeOfCall

        if (surveyFormDetailsInfo!!.itemDetail != null) binding!!.itemDetails.text =
            surveyFormDetailsInfo!!.itemDetail

        if (surveyFormDetailsInfo!!.purchaseOrderDtl != null) binding!!.purchaseOrderDetails.text =
            surveyFormDetailsInfo!!.purchaseOrderDtl

        if (surveyFormDetailsInfo!!.installedMachineSpecification != null) binding!!.specificationOfMachineInstalled.text =
            surveyFormDetailsInfo!!.installedMachineSpecification

        if (surveyFormDetailsInfo!!.accessesory != null) binding!!.anyAccessory.text =
            surveyFormDetailsInfo!!.accessesory

        if (surveyFormDetailsInfo!!.installationDone != null) binding!!.installationDone.text =
            surveyFormDetailsInfo!!.installationDone

        if (surveyFormDetailsInfo!!.customer_Remark != null) binding!!.customerRemarks.text =
            surveyFormDetailsInfo!!.customer_Remark

        if (surveyFormDetailsInfo!!.engineerName != null) binding!!.engineerName.text =
            surveyFormDetailsInfo!!.engineerName
    }

    var onItemViewClickListener: SurveyFormUploadedImagesListingAdapter.OnItemViewClickListener =
        object : SurveyFormUploadedImagesListingAdapter.OnItemViewClickListener {
            override fun onItemClick(surveyFormDocInfo: SurveyFormDocInfo?, position: Int) {
                startActivity(
                    Intent(mActivity, ZoomInZoomOutActivity::class.java).putExtra(
                        "image",
                        surveyFormDocInfo!!.documentPath
                    )
                )
            }
        }

    private fun setClickListener() {
        binding!!.actionBar.ivBack.setOnClickListener { view: View? -> onBackPressed() }
    }
}