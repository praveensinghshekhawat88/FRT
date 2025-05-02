package com.callmangement.ui.pos_help.activity;

import android.os.Bundle;
import android.view.View;
import android.view.animation.TranslateAnimation;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.callmangement.R;
import com.callmangement.custom.CustomActivity;
import com.callmangement.databinding.ActivityFaqListBinding;
import com.callmangement.ui.pos_help.adapter.FAQListAdapter;
import com.callmangement.ui.pos_help.model.ModelFAQList;

import java.util.ArrayList;
import java.util.List;

public class FAQListActivity extends CustomActivity implements View.OnClickListener {
    private ActivityFaqListBinding binding;
    private final List<ModelFAQList> modelFAQList = new ArrayList<>();
    boolean isUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFaqListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initView();
    }

    private void initView(){
        setUpActionBar();
        setUpOnClickListener();
        setUpAdapter();
    }

    private void setUpActionBar(){
        binding.actionBar.ivBack.setVisibility(View.VISIBLE);
        binding.actionBar.ivThreeDot.setVisibility(View.GONE);
        binding.actionBar.layoutLanguage.setVisibility(View.GONE);
        binding.actionBar.textToolbarTitle.setText(getResources().getString(R.string.faq_list));
    }

    private void setUpOnClickListener(){
        binding.actionBar.ivBack.setOnClickListener(this);
    }

    private void setUpAdapter(){
        ModelFAQList model1 = new ModelFAQList();
        model1.setId("1");
        model1.setQuestion("RD issue.");
        modelFAQList.add(model1);

        ModelFAQList model2 = new ModelFAQList();
        model2.setId("2");
        model2.setQuestion("Fingerprint not working.");
        modelFAQList.add(model2);

        ModelFAQList model3 = new ModelFAQList();
        model3.setId("3");
        model3.setQuestion("Fingerprint capturing failed.");
        modelFAQList.add(model3);

        binding.rvFaqList.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        binding.rvFaqList.setAdapter(new FAQListAdapter(mContext, modelFAQList));
    }

    // slide the view from below itself to the current position
    public void slideUp(View view){
        view.setVisibility(View.VISIBLE);
        TranslateAnimation animate = new TranslateAnimation(
                0,                 // fromXDelta
                0,                 // toXDelta
                view.getHeight(),  // fromYDelta
                0);                // toYDelta
        animate.setDuration(500);
        animate.setFillAfter(true);
        view.startAnimation(animate);
    }

    // slide the view from its current position to below itself
    public void slideDown(View view){
        TranslateAnimation animate = new TranslateAnimation(
                0,                 // fromXDelta
                0,                 // toXDelta
                0,                 // fromYDelta
                view.getHeight()); // toYDelta
        animate.setDuration(500);
        animate.setFillAfter(true);
        view.startAnimation(animate);
    }

    public void onSlideViewButtonClick(View view) {
        if (isUp) {
            slideDown(view);
        } else {
            slideUp(view);
        }
        isUp = !isUp;
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.iv_back){
            onBackPressed();
        }
    }
}