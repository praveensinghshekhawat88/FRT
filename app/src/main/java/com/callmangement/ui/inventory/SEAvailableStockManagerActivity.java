package com.callmangement.ui.inventory;

import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.callmangement.network.APIService;
import com.callmangement.network.RetrofitInstance;
import com.callmangement.R;
import com.callmangement.adapter.SEAvailableStockManagerActivityAdapter;
import com.callmangement.custom.CustomActivity;
import com.callmangement.databinding.ActivitySeavailableStockManagerBinding;
import com.callmangement.model.district.ModelDistrictList;
import com.callmangement.model.inventrory.ModelPartsList;
import com.callmangement.model.inventrory.ModelSEUsers;
import com.callmangement.model.inventrory.ModelSEUsersList;
import com.callmangement.ui.complaint.ComplaintViewModel;
import com.callmangement.utils.Constants;
import com.callmangement.utils.PrefManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SEAvailableStockManagerActivity extends CustomActivity implements View.OnClickListener {
    private ActivitySeavailableStockManagerBinding binding;
    private SEAvailableStockManagerActivityAdapter adapter;
    private PrefManager prefManager;
    private ComplaintViewModel viewModel;
    private InventoryViewModel inventoryViewModel;
    private List<ModelDistrictList> district_List = new ArrayList<>();
    private int checkDistrict = 0;
    private int checkSEUsers = 0;
    private String districtNameEng = "";
    private String districtId = "0";
    private String seUserName = "";
    private String seUserId = "0";
    private List<ModelSEUsersList> modelSEUsersList = new ArrayList<>();
    private List<ModelPartsList> modelPartsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySeavailableStockManagerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.actionBar.ivBack.setVisibility(View.VISIBLE);
        binding.actionBar.ivThreeDot.setVisibility(View.GONE);
        binding.actionBar.layoutLanguage.setVisibility(View.GONE);
        binding.actionBar.textToolbarTitle.setText(getResources().getString(R.string.se_available_stock));
        prefManager = new PrefManager(mContext);
        viewModel = ViewModelProviders.of(this).get(ComplaintViewModel.class);
        inventoryViewModel = ViewModelProviders.of(this).get(InventoryViewModel.class);

        districtNameEng = "--" + getResources().getString(R.string.district) + "--";
        seUserName = "--" + getResources().getString(R.string.username) + "--";

        initView();
    }

    private void initView() {
        setUpOnClickListener();
        setUpData();
        setUpUserNameSpinner();
        districtList();
        getSEAvailableStockList(seUserId);
    }

    private void setUpOnClickListener(){
        binding.inputSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//                adapter.getFilter().filter(charSequence);
                if (!charSequence.toString().isEmpty()){
                    List<ModelPartsList> filterList = new ArrayList<>();
                    if (modelPartsList.size() > 0){
                        for (ModelPartsList model : modelPartsList){
                            if (model.getItemName().toLowerCase().contains(charSequence.toString()))
                                filterList.add(model);
                        }
                    }
                    setUpSEAvailableStockListAdapter(filterList);
                }else setUpSEAvailableStockListAdapter(modelPartsList);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        binding.actionBar.ivBack.setOnClickListener(this);
    }

    private void setUpData() {
        binding.spinnerDistrict.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (++checkDistrict > 1) {
                    districtNameEng = district_List.get(i).getDistrictNameEng();
                    districtId = district_List.get(i).getDistrictId();
                    if (!districtNameEng.equalsIgnoreCase("--" + getResources().getString(R.string.district) + "--")) {
                        getSEUsersList();
                    } else {
                        setUpUserNameSpinner();
                        seUserId = "0";
                        binding.spinnerServiceEngineer.setSelection(0);
                        getSEAvailableStockList(seUserId);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        binding.spinnerServiceEngineer.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (++checkSEUsers > 1) {
                    seUserName = modelSEUsersList.get(i).getUserName();
                    seUserId = modelSEUsersList.get(i).getUserId();
                    if (!seUserName.equalsIgnoreCase("--" + getResources().getString(R.string.username) + "--")) {
                        getSEAvailableStockList(seUserId);
                    } else {
                        binding.rvSeAvailableStockManager.setVisibility(View.GONE);
                        binding.textNoStockFound.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void districtList() {
        if (Constants.isNetworkAvailable(mContext)) {
            isLoading();
            viewModel.getDistrict().observe(this, modelDistrict -> {
                isLoading();
                if (modelDistrict.getStatus().equals("200")) {
                    district_List = modelDistrict.getDistrict_List();
                    if (district_List != null && district_List.size() > 0) {
                        Collections.reverse(district_List);
                        ModelDistrictList l = new ModelDistrictList();
                        l.setDistrictId(String.valueOf(-1));
                        l.setDistrictNameEng("--" + getResources().getString(R.string.district) + "--");
                        district_List.add(l);
                        Collections.reverse(district_List);

                        ArrayAdapter<ModelDistrictList> dataAdapter = new ArrayAdapter<>(mContext, android.R.layout.simple_spinner_item, district_List);
                        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        binding.spinnerDistrict.setAdapter(dataAdapter);
                    }
                }
            });
        } else {
            makeToast(getResources().getString(R.string.no_internet_connection));
        }
    }

    private void setUpUserNameSpinner() {
        modelSEUsersList.clear();
        Collections.reverse(modelSEUsersList);
        ModelSEUsersList l = new ModelSEUsersList();
        l.setUserId(String.valueOf(-1));
        l.setUserName("--" + getResources().getString(R.string.username) + "--");
        modelSEUsersList.add(l);
        Collections.reverse(modelSEUsersList);

        ArrayAdapter<ModelSEUsersList> dataAdapter = new ArrayAdapter<>(mContext, android.R.layout.simple_spinner_item, modelSEUsersList);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerServiceEngineer.setAdapter(dataAdapter);
    }

    private void getSEUsersList() {
        if (Constants.isNetworkAvailable(mContext)) {
            APIService service = RetrofitInstance.getRetrofitInstance().create(APIService.class);
            Call<ModelSEUsers> call = service.getSEUserList(districtId);
            call.enqueue(new Callback<ModelSEUsers>() {
                @Override
                public void onResponse(Call<ModelSEUsers> call, Response<ModelSEUsers> response) {
                    if (response.isSuccessful()) {
                        ModelSEUsers modelSEUsers = response.body();
                        if (Objects.requireNonNull(modelSEUsers).getStatus().equals("200")) {
                            modelSEUsersList = modelSEUsers.getSEUsersList();
                            if (modelSEUsersList != null && modelSEUsersList.size() > 0) {
                                Collections.reverse(modelSEUsersList);
                                ModelSEUsersList l = new ModelSEUsersList();
                                l.setUserId(String.valueOf(-1));
                                l.setUserName("--" + getResources().getString(R.string.username) + "--");
                                modelSEUsersList.add(l);
                                Collections.reverse(modelSEUsersList);

                                ArrayAdapter<ModelSEUsersList> dataAdapter = new ArrayAdapter<>(mContext, android.R.layout.simple_spinner_item, modelSEUsersList);
                                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                binding.spinnerServiceEngineer.setAdapter(dataAdapter);
                            }
                        } else {
                            makeToast(modelSEUsers.getMessage());
                        }
                    } else {
                        makeToast(getResources().getString(R.string.error));
                    }
                }

                @Override
                public void onFailure(Call<ModelSEUsers> call, Throwable t) {
                    makeToast(getResources().getString(R.string.error_message));
                }
            });
        }else {
            makeToast(getResources().getString(R.string.no_internet_connection));
        }
    }

    private void getSEAvailableStockList(String seUserId){
        if (Constants.isNetworkAvailable(mContext)) {
            modelPartsList.clear();
            isLoadingInventory();
            inventoryViewModel.getSEAvailableStockListForManager(seUserId, "0").observe(this, modelParts -> {
                isLoadingInventory();
                if (modelParts.getStatus().equals("200")) {
                    modelPartsList = modelParts.getParts();
                    if (modelPartsList.size() > 0) {
                        setUpSEAvailableStockListAdapter(modelPartsList);
                    } else {
                        binding.rvSeAvailableStockManager.setVisibility(View.GONE);
                        binding.textNoStockFound.setVisibility(View.VISIBLE);
                    }
                }
            });
        } else {
            makeToast(getResources().getString(R.string.no_internet_connection));
        }
    }

    private void setUpSEAvailableStockListAdapter(List<ModelPartsList> modelPartsList){
        if (modelPartsList.size() > 0) {
            int quantity = 0;
            for (ModelPartsList model : modelPartsList){
                quantity = quantity + Integer.parseInt(model.getItem_Qty());
            }
            binding.textTotalItemQuantity.setText(String.valueOf(quantity));
            binding.rvSeAvailableStockManager.setVisibility(View.VISIBLE);
            binding.textNoStockFound.setVisibility(View.GONE);
            adapter = new SEAvailableStockManagerActivityAdapter(mContext, modelPartsList);
            binding.rvSeAvailableStockManager.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
            binding.rvSeAvailableStockManager.setAdapter(adapter);
        } else {
            binding.textTotalItemQuantity.setText("0");
            binding.rvSeAvailableStockManager.setVisibility(View.GONE);
            binding.textNoStockFound.setVisibility(View.VISIBLE);
        }
    }

    private void isLoading() {
        viewModel.isLoading().observe(this, aBoolean -> {
            if (aBoolean) {
                showProgress(getResources().getString(R.string.please_wait));
            } else {
                hideProgress();
            }
        });
    }

    private void isLoadingInventory() {
        inventoryViewModel.getIsLoading().observe(this, aBoolean -> {
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