package com.callmangement.ui.inventory;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import com.callmangement.R;
import com.callmangement.adapter.TotalStockListForManagerActivityAdapter;
import com.callmangement.custom.CustomActivity;
import com.callmangement.databinding.ActivityTotalStockListForManagerBinding;
import com.callmangement.model.fps_repeat_on_service_center.ModelRepeatFpsComplaintsList;
import com.callmangement.model.inventrory.ModelParts;
import com.callmangement.model.inventrory.ModelPartsDispatchInvoiceList;
import com.callmangement.model.inventrory.ModelPartsList;
import com.callmangement.utils.Constants;
import com.callmangement.utils.PrefManager;

import java.util.ArrayList;
import java.util.List;

public class TotalStockListForManagerActivity extends CustomActivity implements View.OnClickListener {
    private ActivityTotalStockListForManagerBinding binding;
    private TotalStockListForManagerActivityAdapter adapter;
    private InventoryViewModel viewModel;
    private List<ModelPartsList> modelPartsList = new ArrayList<>();
    private PrefManager prefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTotalStockListForManagerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.actionBar.ivBack.setVisibility(View.VISIBLE);
        binding.actionBar.ivThreeDot.setVisibility(View.GONE);
        binding.actionBar.layoutLanguage.setVisibility(View.GONE);
        binding.actionBar.textToolbarTitle.setText(getResources().getString(R.string.current_stock));
        prefManager = new PrefManager(mContext);
        viewModel = ViewModelProviders.of(this).get(InventoryViewModel.class);
        initView();

    }

    private void initView() {
        setUpOnClickListener();
        getPartsCurrentStockList();
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
                    setUpCurrentStockListAdapter(filterList);
                }else setUpCurrentStockListAdapter(modelPartsList);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        binding.actionBar.ivBack.setOnClickListener(this);
    }

    private void getPartsCurrentStockList(){
        if (Constants.isNetworkAvailable(mContext)) {
            isLoading();
            viewModel.getPartsCurrentStockList("0", "0").observe(this, new Observer<ModelParts>() {
                @Override
                public void onChanged(ModelParts modelParts) {
                    isLoading();
                    if (modelParts.status.equals("200")) {
                        modelPartsList = modelParts.getParts();
                        if (modelPartsList.size() > 0) {
                            setUpCurrentStockListAdapter(modelPartsList);
                        } else {
                            binding.rvCurrentStock.setVisibility(View.GONE);
                            binding.textNoStockFound.setVisibility(View.VISIBLE);
                        }
                    }
                }
            });
        } else {
            makeToast(getResources().getString(R.string.no_internet_connection));
        }
    }

    private void setUpCurrentStockListAdapter(List<ModelPartsList> modelPartsList){
        if (modelPartsList.size() > 0) {
            int quantity = 0;
            for (ModelPartsList model : modelPartsList) {
                quantity = quantity + Integer.parseInt(model.getItem_Qty());
            }
            binding.textTotalItemQuantity.setText(String.valueOf(quantity));
            binding.rvCurrentStock.setVisibility(View.VISIBLE);
            binding.textNoStockFound.setVisibility(View.GONE);
            binding.rvCurrentStock.setLayoutManager(new LinearLayoutManager(TotalStockListForManagerActivity.this, LinearLayoutManager.VERTICAL, false));
            adapter = new TotalStockListForManagerActivityAdapter(mContext, modelPartsList);
            binding.rvCurrentStock.setAdapter(adapter);
        } else {
            binding.textTotalItemQuantity.setText("0");
            binding.rvCurrentStock.setVisibility(View.GONE);
            binding.textNoStockFound.setVisibility(View.VISIBLE);
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