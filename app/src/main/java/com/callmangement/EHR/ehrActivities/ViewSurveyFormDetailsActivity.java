package com.callmangement.EHR.ehrActivities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import com.callmangement.R;
import androidx.recyclerview.widget.GridLayoutManager;

import com.callmangement.EHR.adapter.SurveyFormUploadedImagesListingAdapter;
import com.callmangement.databinding.ActivityViewSurveyFormDetailsBinding;
import com.callmangement.EHR.models.SurveyFormDetailsInfo;
import com.callmangement.EHR.models.SurveyFormDocInfo;
import com.callmangement.EHR.support.EqualSpacingItemDecoration;
import com.callmangement.EHR.support.Preference;
import com.callmangement.utils.PrefManager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ViewSurveyFormDetailsActivity extends BaseActivity {

    Activity mActivity;
    private ActivityViewSurveyFormDetailsBinding binding;

    PrefManager preference;
    ArrayList<SurveyFormDocInfo> surveyFormDocInfoArrayList = null;
    private SurveyFormUploadedImagesListingAdapter adapter;

    SurveyFormDetailsInfo surveyFormDetailsInfo = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        binding = ActivityViewSurveyFormDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        init();
    }

    private void init() {
        mActivity = this;
        preference =  new PrefManager(mActivity);

        Bundle bundle = getIntent().getExtras();
        surveyFormDocInfoArrayList = (ArrayList<SurveyFormDocInfo>) bundle.getSerializable("mylist");
        surveyFormDetailsInfo = (SurveyFormDetailsInfo) bundle.getSerializable("surveyFormDetails");

        binding.actionBar.ivBack.setVisibility(View.VISIBLE);
        binding.actionBar.ivThreeDot.setVisibility(View.GONE);
        binding.actionBar.layoutLanguage.setVisibility(View.GONE);
        binding.actionBar.buttonPDF.setVisibility(View.GONE);
        binding.actionBar.textToolbarTitle.setText(getResources().getString(R.string.view_survey_form_details));

        setUpData();
        setClickListener();
    }

    private void setUpData() {

        if (surveyFormDocInfoArrayList != null && surveyFormDocInfoArrayList.size() > 0) {
            binding.txtViewUploadedImages.setVisibility(View.VISIBLE);
            binding.rvAllSurveyForms.setVisibility(View.VISIBLE);
        } else {
            binding.txtViewUploadedImages.setVisibility(View.GONE);
            binding.txtViewUploadedImages.setVisibility(View.GONE);
        }

        adapter = new SurveyFormUploadedImagesListingAdapter(mActivity, surveyFormDocInfoArrayList, onItemViewClickListener);
        binding.rvAllSurveyForms.setHasFixedSize(true);
        GridLayoutManager layoutManager = new GridLayoutManager(mActivity, 3);
        binding.rvAllSurveyForms.setLayoutManager(layoutManager);
        binding.rvAllSurveyForms.addItemDecoration(new EqualSpacingItemDecoration(30, EqualSpacingItemDecoration.VERTICAL));
        binding.rvAllSurveyForms.setAdapter(adapter);

        if (surveyFormDetailsInfo.getCustomerName() != null)
            binding.customerName.setText(surveyFormDetailsInfo.getCustomerName());

        if (surveyFormDetailsInfo.getBill_ChallanNo() != null)
            binding.billChallanNo.setText(surveyFormDetailsInfo.getBill_ChallanNo());

        if (surveyFormDetailsInfo.getAddress() != null)
            binding.customerAddress.setText(surveyFormDetailsInfo.getAddress());

        if (surveyFormDetailsInfo.getBillRemark() != null)
            binding.billRemarks.setText(surveyFormDetailsInfo.getBillRemark());

        if (surveyFormDetailsInfo.getPointOfContact() != null)
            binding.pointOfContact.setText(surveyFormDetailsInfo.getPointOfContact());

        if (surveyFormDetailsInfo.getMobileNumber() != null)
            binding.mobileNumber.setText(surveyFormDetailsInfo.getMobileNumber());

        if (surveyFormDetailsInfo.getInstallationDateStr() != null) {
            SimpleDateFormat input = new SimpleDateFormat("dd-MM-yyyy");
            SimpleDateFormat output = new SimpleDateFormat("yyyy-MM-dd");
            try {
                Date oneWayTripDate = input.parse(surveyFormDetailsInfo.getInstallationDateStr());                 // parse input
                binding.dateOfInstallation.setText(output.format(oneWayTripDate));    // format output
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        if (surveyFormDetailsInfo.getTypeOfCall() != null)
            binding.typeOfCall.setText(surveyFormDetailsInfo.getTypeOfCall());

        if (surveyFormDetailsInfo.getItemDetail() != null)
            binding.itemDetails.setText(surveyFormDetailsInfo.getItemDetail());

        if (surveyFormDetailsInfo.getPurchaseOrderDtl() != null)
            binding.purchaseOrderDetails.setText(surveyFormDetailsInfo.getPurchaseOrderDtl());

        if (surveyFormDetailsInfo.getInstalledMachineSpecification() != null)
            binding.specificationOfMachineInstalled.setText(surveyFormDetailsInfo.getInstalledMachineSpecification());

        if (surveyFormDetailsInfo.getAccessesory() != null)
            binding.anyAccessory.setText(surveyFormDetailsInfo.getAccessesory());

        if (surveyFormDetailsInfo.getInstallationDone() != null)
            binding.installationDone.setText(surveyFormDetailsInfo.getInstallationDone());

        if (surveyFormDetailsInfo.getCustomer_Remark() != null)
            binding.customerRemarks.setText(surveyFormDetailsInfo.getCustomer_Remark());

        if (surveyFormDetailsInfo.getEngineerName() != null)
            binding.engineerName.setText(surveyFormDetailsInfo.getEngineerName());

    }

    SurveyFormUploadedImagesListingAdapter.OnItemViewClickListener onItemViewClickListener = new SurveyFormUploadedImagesListingAdapter.OnItemViewClickListener() {
        @Override
        public void onItemClick(SurveyFormDocInfo surveyFormDocInfo, int position) {
            startActivity(new Intent(mActivity, ZoomInZoomOutActivity.class).putExtra("image", surveyFormDocInfo.getDocumentPath()));
        }
    };

    private void setClickListener() {
        binding.actionBar.ivBack.setOnClickListener(view -> onBackPressed());
    }

}