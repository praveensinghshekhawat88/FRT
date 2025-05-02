package com.callmangement.ui.pos_issue.activity;

import android.os.Bundle;
import android.view.View;

import com.callmangement.R;
import com.callmangement.custom.CustomActivity;
import com.callmangement.databinding.ActivityPosIssueBinding;

public class PosIssueActivity extends CustomActivity implements View.OnClickListener {
    private ActivityPosIssueBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPosIssueBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initView();
    }

    private void initView() {
        setUpActionBar();
        setUpOnClickListener();
    }

    private void setUpActionBar(){
        binding.actionBar.ivBack.setVisibility(View.VISIBLE);
        binding.actionBar.ivThreeDot.setVisibility(View.GONE);
        binding.actionBar.layoutLanguage.setVisibility(View.GONE);
        binding.actionBar.textToolbarTitle.setText(getResources().getString(R.string.pos_issue));
    }

    private void setUpOnClickListener(){
        binding.actionBar.ivBack.setOnClickListener(this);
        binding.buttonViewIssue.setOnClickListener(this);
        binding.buttonRegisterIssue.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.buttonViewIssue){
            startActivity(ViewIssuesActivity.class);
        } else if (id == R.id.buttonRegisterIssue){
            startActivity(RegisterIssueActivity.class);
        } else if (id == R.id.iv_back){
            onBackPressed();
        }
    }
}