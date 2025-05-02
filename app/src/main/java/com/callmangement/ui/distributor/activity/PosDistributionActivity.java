package com.callmangement.ui.distributor.activity;

import android.os.Bundle;
import android.view.View;
import com.callmangement.R;
import com.callmangement.custom.CustomActivity;
import com.callmangement.databinding.ActivityPosDistributionBinding;
import com.callmangement.utils.PrefManager;

public class PosDistributionActivity extends CustomActivity implements View.OnClickListener {
    private ActivityPosDistributionBinding binding;
    private PrefManager prefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPosDistributionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.actionBar.ivBack.setVisibility(View.VISIBLE);
        binding.actionBar.ivThreeDot.setVisibility(View.GONE);
        binding.actionBar.layoutLanguage.setVisibility(View.GONE);
        binding.actionBar.textToolbarTitle.setText(getResources().getString(R.string.pos_distribution));
        prefManager = new PrefManager(mContext);
        initView();
    }

    private void initView() {
        setUpOnClickListener();
    }

    private void setUpOnClickListener(){
        binding.actionBar.ivBack.setOnClickListener(this);
        binding.buttonPosDistributionReport.setOnClickListener(this);
        binding.buttonDistributedPosList.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.iv_back){
            onBackPressed();
        } else if (id == R.id.buttonPosDistributionReport){
            startActivity(PosDistributionReportActivity.class);
        } else if (id == R.id.buttonDistributedPosList){
            startActivity(DistributedPosListActivity.class);
        }
    }
}