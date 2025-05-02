package com.callmangement.EHR.ehrActivities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import com.callmangement.R;
import androidx.recyclerview.widget.GridLayoutManager;

import com.callmangement.EHR.adapter.CampUploadedImagesListingAdapter;
import com.callmangement.databinding.ActivityViewCampDetailsBinding;
import com.callmangement.EHR.models.CampDetailsInfo;
import com.callmangement.EHR.models.CampDocInfo;
import com.callmangement.EHR.support.EqualSpacingItemDecoration;
import com.callmangement.EHR.support.Preference;
import com.callmangement.utils.PrefManager;

import java.util.ArrayList;

public class ViewCampDetailsActivity extends BaseActivity {

    Activity mActivity;
    private ActivityViewCampDetailsBinding binding;

    PrefManager preference;
    ArrayList<CampDocInfo> campDocInfoArrayList = null;
    private CampUploadedImagesListingAdapter adapter;

    CampDetailsInfo campDetailsInfo = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        binding = ActivityViewCampDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        init();
    }

    private void init() {
        mActivity = this;
        preference =  new PrefManager(mActivity);

        Bundle bundle = getIntent().getExtras();
        campDocInfoArrayList = (ArrayList<CampDocInfo>) bundle.getSerializable("mylist");
        campDetailsInfo = (CampDetailsInfo) bundle.getSerializable("campDetails");

        binding.actionBar.ivBack.setVisibility(View.VISIBLE);
        binding.actionBar.ivThreeDot.setVisibility(View.GONE);
        binding.actionBar.layoutLanguage.setVisibility(View.GONE);
        binding.actionBar.buttonPDF.setVisibility(View.GONE);
        binding.actionBar.textToolbarTitle.setText(getResources().getString(R.string.view_camp_details));

        setUpData();
        setClickListener();
    }

    private void setUpData() {

        if (campDocInfoArrayList != null && campDocInfoArrayList.size() > 0) {
            binding.txtViewUploadedImages.setVisibility(View.VISIBLE);
            binding.rvAllCamps.setVisibility(View.VISIBLE);
        } else {
            binding.txtViewUploadedImages.setVisibility(View.GONE);
            binding.txtViewUploadedImages.setVisibility(View.GONE);
        }

        adapter = new CampUploadedImagesListingAdapter(mActivity, campDocInfoArrayList, onItemViewClickListener);
        binding.rvAllCamps.setHasFixedSize(true);
        GridLayoutManager layoutManager = new GridLayoutManager(mActivity, 3);
        binding.rvAllCamps.setLayoutManager(layoutManager);
        binding.rvAllCamps.addItemDecoration(new EqualSpacingItemDecoration(30, EqualSpacingItemDecoration.VERTICAL));
        binding.rvAllCamps.setAdapter(adapter);

        if (campDetailsInfo != null && campDetailsInfo.getStartDate() != null)
            binding.inputStartDateTime.setText(campDetailsInfo.getStartDate());

        if (campDetailsInfo != null && campDetailsInfo.getEndDate() != null)
            binding.inputEndDateTime.setText(campDetailsInfo.getEndDate());

        if (campDetailsInfo != null && campDetailsInfo.getOrganizeDate() != null)
            binding.inputOrganiseDateTime.setText(campDetailsInfo.getOrganizeDate());

        if (campDetailsInfo != null && campDetailsInfo.getDistrictNameEng() != null)
            binding.inputDistrict.setText(campDetailsInfo.getDistrictNameEng());

        if (campDetailsInfo != null && campDetailsInfo.getBlockName() != null)
            binding.inputBlock.setText(campDetailsInfo.getBlockName().toString());

        if (campDetailsInfo != null && campDetailsInfo.getAddress() != null)
            binding.inputPlace.setText(campDetailsInfo.getAddress());

        if (campDetailsInfo != null && campDetailsInfo.getDescription() != null)
            binding.inputDescription.setText(campDetailsInfo.getDescription());

    }

    CampUploadedImagesListingAdapter.OnItemViewClickListener onItemViewClickListener = new CampUploadedImagesListingAdapter.OnItemViewClickListener() {
        @Override
        public void onItemClick(CampDocInfo campDocInfo, int position) {
            startActivity(new Intent(mActivity, ZoomInZoomOutActivity.class).putExtra("image", campDocInfo.getDocumentPath()));
        }
    };

    private void setClickListener() {
        binding.actionBar.ivBack.setOnClickListener(view -> onBackPressed());
    }

}