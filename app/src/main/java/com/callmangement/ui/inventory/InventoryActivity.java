package com.callmangement.ui.inventory;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.callmangement.R;
import com.callmangement.custom.CustomActivity;
import com.callmangement.databinding.ActivityInventoryBinding;
import com.callmangement.ui.home.MainActivity;
import com.callmangement.utils.Constants;
import com.callmangement.utils.PrefManager;

public class InventoryActivity extends CustomActivity implements View.OnClickListener {
    private ActivityInventoryBinding binding;
    private PrefManager prefManager;
    private final String fromWhere = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityInventoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.actionBar.ivBack.setVisibility(View.VISIBLE);
        binding.actionBar.ivThreeDot.setVisibility(View.GONE);
        binding.actionBar.layoutLanguage.setVisibility(View.GONE);
        binding.actionBar.textToolbarTitle.setText(getResources().getString(R.string.inventory));
        prefManager = new PrefManager(mContext);
        initView();
    }

    private void initView() {
        if (prefManager.getUseR_TYPE_ID().equals("4") && prefManager.getUseR_TYPE().equalsIgnoreCase("ServiceEngineer")){
            binding.buttonCreateNewChallanforDispatch.setVisibility(View.GONE);
            binding.buttonCurrentStockManager.setVisibility(View.GONE);
            binding.buttonSEAvailableStockManager.setVisibility(View.GONE);
            binding.buttonAddStockManager.setVisibility(View.GONE);
            binding.buttonCreateDispatchStock.setVisibility(View.GONE);
            binding.buttonShiftingSEStockInMainStock.setVisibility(View.GONE);
            binding.buttonAvailableStockForSE.setVisibility(View.VISIBLE);
            binding.buttonReceiveMaterial.setVisibility(View.VISIBLE);

        } else if (prefManager.getUseR_TYPE_ID().equals("5") && prefManager.getUseR_TYPE().equalsIgnoreCase("ServiceCentre")) {
            binding.buttonCreateNewChallanforDispatch.setVisibility(View.GONE);
            binding.buttonCurrentStockManager.setVisibility(View.GONE);
            binding.buttonSEAvailableStockManager.setVisibility(View.GONE);
            binding.buttonAddStockManager.setVisibility(View.GONE);
            binding.buttonCreateDispatchStock.setVisibility(View.GONE);
            binding.buttonShiftingSEStockInMainStock.setVisibility(View.GONE);
            binding.buttonAvailableStockForSE.setVisibility(View.VISIBLE);
            binding.buttonReceiveMaterial.setVisibility(View.VISIBLE);

        } else {
            binding.buttonCreateNewChallanforDispatch.setVisibility(View.VISIBLE);
            binding.buttonCurrentStockManager.setVisibility(View.VISIBLE);
            binding.buttonSEAvailableStockManager.setVisibility(View.VISIBLE);
            binding.buttonAddStockManager.setVisibility(View.VISIBLE);
            binding.buttonCreateDispatchStock.setVisibility(View.VISIBLE);
            binding.buttonShiftingSEStockInMainStock.setVisibility(View.VISIBLE);
            binding.buttonAvailableStockForSE.setVisibility(View.GONE);
            binding.buttonReceiveMaterial.setVisibility(View.GONE);
        }
        setUpOnclickListener();
    }

    private void setUpOnclickListener(){
        binding.actionBar.ivBack.setOnClickListener(this);
        binding.buttonCurrentStockManager.setOnClickListener(this);
        binding.buttonReceiveMaterial.setOnClickListener(this);
        binding.buttonCreateNewChallanforDispatch.setOnClickListener(this);
        binding.buttonSEAvailableStockManager.setOnClickListener(this);
        binding.buttonAvailableStockForSE.setOnClickListener(this);
        binding.buttonAddStockManager.setOnClickListener(this);
        binding.buttonCreateDispatchStock.setOnClickListener(this);
        binding.buttonShiftingSEStockInMainStock.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.buttonCurrentStockManager){
            startActivity(TotalStockListForManagerActivity.class);
        } else if (id == R.id.buttonReceiveMaterial){
            startActivity(ReceiveMaterialListActivity.class);
        } else if (id == R.id.buttonCreateNewChallanforDispatch){
            startActivity(DispatchChallanListActivity.class);
        } else if (id == R.id.buttonSEAvailableStockManager){
            startActivity(SEAvailableStockManagerActivity.class);
        } else if (id == R.id.buttonAvailableStockForSE){
            startActivity(AvailableStockListForSEActivity.class);
        } else if (id == R.id.buttonAddStockManager){
            startActivity(AddStockActivity.class);
        } else if (id == R.id.buttonCreateDispatchStock){
            startActivity(CreateNewChallanforDispatchActivity.class);
        } else if (id == R.id.buttonShiftingSEStockInMainStock){
            startActivity(ShiftingStockActivity.class);
        }else if (id == R.id.iv_back){
            onBackPressed();
        }
    }

}