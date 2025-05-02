package com.callmangement.ui.pos_issue.activity;

import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.view.View;

import com.callmangement.R;
import com.callmangement.custom.CustomActivity;
import com.callmangement.databinding.ActivityViewIssuesBinding;
import com.callmangement.ui.pos_issue.adapter.ViewIssuesActivityAdapter;

public class ViewIssuesActivity extends CustomActivity implements View.OnClickListener {
    private ActivityViewIssuesBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityViewIssuesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initView();
    }

    private void initView(){
        binding.actionBar.ivBack.setVisibility(View.VISIBLE);
        binding.actionBar.ivThreeDot.setVisibility(View.GONE);
        binding.actionBar.layoutLanguage.setVisibility(View.GONE);
        binding.actionBar.textToolbarTitle.setText(getResources().getString(R.string.view_issue));
        setUpOnClickListener();
        setUpAdapter();
    }

    private void setUpOnClickListener(){
        binding.actionBar.ivBack.setOnClickListener(this);
    }

    private void setUpAdapter(){
        binding.rvIssueList.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        binding.rvIssueList.setAdapter(new ViewIssuesActivityAdapter(mContext));
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.iv_back){
            onBackPressed();
        }
    }
}