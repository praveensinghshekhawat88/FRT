package com.callmangement.ui.reports;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.bumptech.glide.Glide;
import com.callmangement.R;
import com.callmangement.custom.CustomActivity;
import com.callmangement.databinding.ActivityRepeatFpsComplaintDetailBinding;
import com.callmangement.model.fps_repeat_on_service_center.ModelRepeatFpsComplaintsList;
import com.callmangement.ui.home.ZoomInZoomOutActivity;
import com.callmangement.utils.Constants;

public class RepeatFpsComplaintDetailActivity extends CustomActivity implements View.OnClickListener {
    private ActivityRepeatFpsComplaintDetailBinding binding;
    private ModelRepeatFpsComplaintsList model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRepeatFpsComplaintDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initView();
    }

    private void initView() {
        binding.actionBar.ivBack.setVisibility(View.VISIBLE);
        binding.actionBar.ivThreeDot.setVisibility(View.GONE);
        binding.actionBar.layoutLanguage.setVisibility(View.GONE);
        binding.actionBar.buttonPDF.setVisibility(View.GONE);
        binding.actionBar.textToolbarTitle.setText(getResources().getString(R.string.complaint_details));
        getIntentData();
        setUpOnClickListener();
    }

    private void getIntentData() {
        model = (ModelRepeatFpsComplaintsList) getIntent().getSerializableExtra("param");
        binding.setData(model);
        binding.chkBoxIsPhysicalDamage.setChecked(model.getIsPhysicalDamage() != null && model.getIsPhysicalDamage());
        if (!model.getImagePath().isEmpty()) {
            binding.seImage.setVisibility(View.VISIBLE);
            Glide.with(mContext)
                    .load(Constants.API_BASE_URL+model.getImagePath())
                    .placeholder(R.drawable.image_not_fount)
                    .into(binding.seImage);
        } else {
            binding.seImage.setVisibility(View.GONE);
        }
    }

    private void setUpOnClickListener(){
        binding.actionBar.ivBack.setOnClickListener(this);
        binding.seImage.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.se_image) {
            startActivity(new Intent(mContext, ZoomInZoomOutActivity.class).putExtra("image", model.getImagePath()));
        } else if (id == R.id.iv_back) {
            onBackPressed();
        }
    }
}