package com.callmangement.ui.inventory;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProviders;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.callmangement.Network.APIService;
import com.callmangement.Network.RetrofitInstance;
import com.callmangement.R;
import com.callmangement.custom.CustomActivity;
import com.callmangement.databinding.ActivityCreateNewChallanforDispatchBinding;
import com.callmangement.model.district.ModelDistrictList;
import com.callmangement.model.inventrory.ModelPartsList;
import com.callmangement.model.inventrory.ModelSEUsers;
import com.callmangement.model.inventrory.ModelSEUsersList;
import com.callmangement.ui.complaint.ComplaintViewModel;
import com.callmangement.utils.Constants;
import com.callmangement.utils.PrefManager;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateNewChallanforDispatchActivity extends CustomActivity implements View.OnClickListener {
    Activity mActivity;
    ActivityCreateNewChallanforDispatchBinding binding;
    private ComplaintViewModel viewModel;
    private InventoryViewModel inventoryViewModel;
    private List<ModelDistrictList> district_List = new ArrayList<>();
    private final String dispatchId = "0";
    private int checkDistrict = 0;
    private int checkSEUsers = 0;
    private String districtNameEng = "";
    private String districtId = "0";
    private String seUserName = "";
    private String userTypeName = "";
    private String seUserId = "0";
    private String seMobileNumber = "";
    private PrefManager prefManager;
    private List<ModelPartsList> modelPartsList = new ArrayList<>();
    private List<ModelSEUsersList> modelSEUsersList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCreateNewChallanforDispatchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        init();
    }

    private void init() {
        mActivity = this;
        binding.actionBar.ivThreeDot.setVisibility(View.GONE);
        binding.actionBar.ivBack.setVisibility(View.VISIBLE);
        binding.actionBar.layoutLanguage.setVisibility(View.GONE);
        binding.actionBar.buttonPDF.setVisibility(View.GONE);
        binding.actionBar.textToolbarTitle.setText(getResources().getString(R.string.create_dispatch_stock));
        prefManager = new PrefManager(mContext);
        viewModel = ViewModelProviders.of(this).get(ComplaintViewModel.class);
        inventoryViewModel = ViewModelProviders.of(this).get(InventoryViewModel.class);

        districtNameEng = "--" + getResources().getString(R.string.district) + "--";
        seUserName = "--" + getResources().getString(R.string.username) + "--";

        setUpClickListener();
        setUpData();
        setUpUserNameSpinner();
        districtList();
        getAvailableStockPartsList();
    }

    private void setUpClickListener() {
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
                    setPartsListAdapter(filterList);
                } else setPartsListAdapter(modelPartsList);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        binding.buttonNext.setOnClickListener(this);
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
                    seMobileNumber = modelSEUsersList.get(i).getMobileNo();
                    userTypeName = modelSEUsersList.get(i).getUserTypeName();
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
                public void onResponse(@NonNull Call<ModelSEUsers> call, @NonNull Response<ModelSEUsers> response) {
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
                public void onFailure(@NonNull Call<ModelSEUsers> call, @NonNull Throwable t) {
                    makeToast(getResources().getString(R.string.error_message));
                }
            });
        }else {
            makeToast(getResources().getString(R.string.no_internet_connection));
        }
    }

    private void getAvailableStockPartsList() {
        if (Constants.isNetworkAvailable(mContext)) {
            modelPartsList.clear();
            isLoading();
            inventoryViewModel.getAvailableStockPartsList("0", "0").observe(this, modelParts -> {
                isLoading();
                if (modelParts.getStatus().equals("200")) {
                    modelPartsList = modelParts.getParts();
                    setPartsListAdapter(modelPartsList);
                }
            });
        } else {
            makeToast(getResources().getString(R.string.no_internet_connection));
        }
    }

    private void setPartsListAdapter(List<ModelPartsList> modelPartsList) {
        binding.partsListLay.removeAllViews();
        if (modelPartsList.size() > 0) {
            binding.textNoDataFound.setVisibility(View.GONE);
            for (int i = 0; i < modelPartsList.size(); i++) {
                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View itemView = inflater.inflate(R.layout.item_create_challan_dispatch, null, true);

                TextView textItemName = itemView.findViewById(R.id.tv_item_name);
                TextView inputProductAvailableQty = itemView.findViewById(R.id.inputProductAvailableQty);
                EditText inputProductCount = itemView.findViewById(R.id.inputProductCount);
                ImageView ivMinus = itemView.findViewById(R.id.iv_minus);
                ImageView ivPlus = itemView.findViewById(R.id.iv_plus);
                ImageView ivCheckbox = itemView.findViewById(R.id.iv_checkbox);

                inputProductAvailableQty.setText(modelPartsList.get(i).getItem_Qty());
                inputProductCount.setText(modelPartsList.get(i).getQuantity());
                textItemName.setText(modelPartsList.get(i).getItemName());

                if (modelPartsList.get(i).isSelectFlag()) {
                    ivCheckbox.setBackgroundResource(R.drawable.ic_check_box);
                } else {
                    ivCheckbox.setBackgroundResource(R.drawable.ic_uncheck_box);
                }

                ivCheckbox.setTag(1000 + i);
                ivMinus.setTag(2000 + i);
                ivPlus.setTag(3000 + i);
                inputProductCount.setTag(4000 + i);

                ivCheckbox.setOnClickListener(view -> {
                    final int tag = (Integer) ivCheckbox.getTag() - 1000;
                    if (modelPartsList.get(tag).isSelectFlag()) {
                        modelPartsList.get(tag).setSelectFlag(false);
                        ivCheckbox.setBackgroundResource(R.drawable.ic_uncheck_box);
                    } else {
                        modelPartsList.get(tag).setSelectFlag(true);
                        ivCheckbox.setBackgroundResource(R.drawable.ic_check_box);
                    }
                });

                ivMinus.setOnClickListener(view -> {
                    final int tag = (Integer) ivMinus.getTag() - 2000;
                    String quantity = inputProductCount.getText().toString().trim();
                    if (!quantity.isEmpty() && !quantity.equals("0")) {
                        int intQuantity = Integer.parseInt(quantity);
                        int updatedQuantity = intQuantity - 1;
                        modelPartsList.get(tag).setQuantity(String.valueOf(updatedQuantity));
                        inputProductCount.setText(modelPartsList.get(tag).getQuantity());
                    }
                    inputProductCount.setSelection(inputProductCount.getText().length());
                });

                ivPlus.setOnClickListener(view -> {
                    final int tag = (Integer) ivPlus.getTag() - 3000;
                    String quantity = inputProductCount.getText().toString().trim();
                    int availableQty = Integer.parseInt(inputProductAvailableQty.getText().toString().trim());
                    if (!quantity.isEmpty()) {
                        int intQuantity = Integer.parseInt(quantity);
                        int updatedQuantity = intQuantity + 1;
                        if (updatedQuantity > availableQty) {
                            makeToast(getResources().getString(R.string.message_dispatch_quantity));
                        } else {
                            modelPartsList.get(tag).setQuantity(String.valueOf(updatedQuantity));
                            inputProductCount.setText(modelPartsList.get(tag).getQuantity());
                        }
                        inputProductCount.setSelection(inputProductCount.getText().length());
                    }
                });

                inputProductCount.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }
                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        final int tag = (Integer) inputProductCount.getTag() - 4000;
                        if (!charSequence.toString().equals("")) {
                            String quantity = inputProductCount.getText().toString().trim();
                            int availableQty = Integer.parseInt(inputProductAvailableQty.getText().toString().trim());
                            int intQuantity = Integer.parseInt(quantity);
                            if (intQuantity > availableQty) {
                                makeToast(getResources().getString(R.string.message_dispatch_quantity));
                                inputProductCount.setText(modelPartsList.get(tag).getQuantity());
                            } else {
                                modelPartsList.get(tag).setQuantity(quantity);
                            }
                        } else {
                            modelPartsList.get(tag).setQuantity("0");
                        }
                        inputProductCount.setSelection(inputProductCount.getText().length());
                    }
                    @Override
                    public void afterTextChanged(Editable editable) {

                    }
                });
                binding.partsListLay.addView(itemView);
            }
        }else{
            binding.textNoDataFound.setVisibility(View.VISIBLE);
            hideProgress();
        }
    }

    private void isLoading() {
        inventoryViewModel.getIsLoading().observe(this, aBoolean -> {
            if (aBoolean) {
                showProgress(getResources().getString(R.string.please_wait));
            } else {
                hideProgress();
            }
        });
    }

    private boolean getSelectItemHaveValue(List<ModelPartsList> dispatchList) {
        boolean flag = false;
        for (ModelPartsList model : dispatchList) {
            if (Integer.parseInt(model.getQuantity()) > 0)
                flag = true;
            else {
                flag = false;
                break;
            }
        }
        return flag;
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.buttonNext) {
            List<ModelPartsList> dispatchList = new ArrayList<>();
            for (ModelPartsList model : modelPartsList) {
                if (model.isSelectFlag())
                    dispatchList.add(model);
            }
            if (dispatchList.size() > 0) {
                if (getSelectItemHaveValue(dispatchList)){
                    if (districtNameEng.equalsIgnoreCase("--" + getResources().getString(R.string.district) + "--")) {
                        makeToast(getResources().getString(R.string.please_select_district));
                    } else if (seUserName.equalsIgnoreCase("--" + getResources().getString(R.string.username) + "--")) {
                        makeToast(getResources().getString(R.string.please_select_username));
                    } else {
                        Intent intent = new Intent(mContext, PickImageAndCourierDetailForDispatchStockActivity.class);
                        intent.putExtra("dispatchList", (Serializable) dispatchList);
                        intent.putExtra("districtNameEng", districtNameEng);
                        intent.putExtra("seUserName", seUserName);
                        intent.putExtra("dispatchId", dispatchId);
                        intent.putExtra("seUserId", seUserId);
                        intent.putExtra("districtId", districtId);
                        intent.putExtra("userTypeName", userTypeName);
                        startActivity(intent);
                    }
                } else
                    makeToast(getResources().getString(R.string.selected_item_qty));
            } else
                makeToast(getResources().getString(R.string.selected_item_qty));

        } else if (id == R.id.iv_back) {
            onBackPressed();
        }
    }
}