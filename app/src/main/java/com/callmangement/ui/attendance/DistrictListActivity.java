package com.callmangement.ui.attendance;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.annotation.SuppressLint;
import android.os.Bundle;
import com.callmangement.R;
import com.callmangement.adapter.DistrictListActivityAdapter;
import com.callmangement.custom.CustomActivity;
import com.callmangement.databinding.ActivityDistrictListBinding;
import com.callmangement.model.district.ModelDistrictList;
import com.callmangement.ui.complaint.ComplaintViewModel;
import java.util.ArrayList;
import java.util.List;

public class DistrictListActivity extends CustomActivity {
    private ActivityDistrictListBinding binding;
    private DistrictListActivityAdapter adapter;
    private ComplaintViewModel viewModel;
    private List<ModelDistrictList> district_List = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_district_list);
        viewModel = ViewModelProviders.of(this).get(ComplaintViewModel.class);
        initView();
        districtList();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void initView() {
        adapter = new DistrictListActivityAdapter(mContext);
        adapter.notifyDataSetChanged();
        binding.rvDistrictList.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL,false));
        binding.rvDistrictList.setAdapter(adapter);
    }

    private void districtList(){
        isLoading();
        viewModel.getDistrict().observe(this, modelDistrict -> {
            isLoading();
            if (modelDistrict.getStatus().equals("200")){
                district_List = modelDistrict.getDistrict_List();
                adapter.setData(district_List);
            }
        });
    }

    private void isLoading() {
        viewModel.getIsLoading().observe(this, aBoolean -> {
            if (aBoolean) {
                showProgress(getResources().getString(R.string.please_wait));
            } else {
                hideProgress();
            }
        });
    }

}