package com.callmangement.ui.inventory;

import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import com.callmangement.R;
import com.callmangement.adapter.AvailableStockListForSEActivityAdapter;
import com.callmangement.custom.CustomActivity;
import com.callmangement.databinding.ActivityAvailableStockListForSeactivityBinding;
import com.callmangement.model.inventrory.ModelPartsList;
import com.callmangement.utils.Constants;
import com.callmangement.utils.PrefManager;

import java.util.ArrayList;
import java.util.List;

public class AvailableStockListForSEActivity extends CustomActivity implements View.OnClickListener {
    private ActivityAvailableStockListForSeactivityBinding binding;
    private AvailableStockListForSEActivityAdapter adapter;
    private List<ModelPartsList> modelPartsList = new ArrayList<>();
    private PrefManager prefManager;
    private InventoryViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAvailableStockListForSeactivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.actionBar.ivBack.setVisibility(View.VISIBLE);
        binding.actionBar.ivThreeDot.setVisibility(View.GONE);
        binding.actionBar.layoutLanguage.setVisibility(View.GONE);
        binding.actionBar.textToolbarTitle.setText(getResources().getString(R.string.available_stock));
        prefManager = new PrefManager(mContext);
        viewModel = ViewModelProviders.of(this).get(InventoryViewModel.class);
        initView();
    }

    private void initView() {
        setUpOnClickListener();
        getAvailableStockList();
    }

    private void setUpOnClickListener(){
        binding.inputSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!charSequence.toString().isEmpty()){
                    List<ModelPartsList> filterList = new ArrayList<>();
                    if (modelPartsList.size() > 0){
                        for (ModelPartsList model : modelPartsList){
                            if (model.getItemName().toLowerCase().contains(charSequence.toString()))
                                filterList.add(model);
                        }
                    }
                    setUpAvailableStockAdapter(filterList);
                } else setUpAvailableStockAdapter(modelPartsList);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        binding.actionBar.ivBack.setOnClickListener(this);
    }

    private void getAvailableStockList(){
        if (Constants.isNetworkAvailable(mContext)) {
            isLoading();
            viewModel.getAvailableStockListForSE(prefManager.getUSER_Id(),"0").observe(this, modelParts -> {
                isLoading();
                if (modelParts.status.equals("200")){
                    modelPartsList = modelParts.getParts();
                    if (modelPartsList.size() > 0) {
                        setUpAvailableStockAdapter(modelPartsList);
                    } else {
                        binding.rvAvailableStock.setVisibility(View.GONE);
                        binding.textNoStockFound.setVisibility(View.VISIBLE);
                    }
                }
            });
        } else {
            makeToast(getResources().getString(R.string.no_internet_connection));
        }
    }

    private void setUpAvailableStockAdapter(List<ModelPartsList> modelPartsList) {
        if (modelPartsList.size() > 0) {
            binding.rvAvailableStock.setVisibility(View.VISIBLE);
            binding.textNoStockFound.setVisibility(View.GONE);
            int quantity = 0;
            for (ModelPartsList model : modelPartsList){
                quantity = quantity + Integer.parseInt(model.getItem_Qty());
            }
            binding.textTotalItemQuantity.setText(String.valueOf(quantity));
            binding.rvAvailableStock.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
            adapter = new AvailableStockListForSEActivityAdapter(mContext, modelPartsList);
            binding.rvAvailableStock.setAdapter(adapter);
        } else {
            binding.rvAvailableStock.setVisibility(View.GONE);
            binding.textNoStockFound.setVisibility(View.VISIBLE);
            binding.textTotalItemQuantity.setText("0");
        }
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

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.iv_back){
            onBackPressed();
        }
    }
}