package com.callmangement.ui.inventory;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.ViewModelProviders;
import android.content.Context;
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
import com.callmangement.databinding.ActivityShiftingStockBinding;
import com.callmangement.model.ModelResponse;
import com.callmangement.model.district.ModelDistrictList;
import com.callmangement.model.inventrory.ModelParts;
import com.callmangement.model.inventrory.ModelPartsList;
import com.callmangement.model.inventrory.ModelSEUsers;
import com.callmangement.model.inventrory.ModelSEUsersList;
import com.callmangement.ui.complaint.ComplaintViewModel;
import com.callmangement.utils.Constants;
import com.callmangement.utils.PrefManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShiftingStockActivity extends CustomActivity implements View.OnClickListener {
    private ActivityShiftingStockBinding binding;
    private ComplaintViewModel viewModel;
    private InventoryViewModel inventoryViewModel;
    private List<ModelDistrictList> district_List = new ArrayList<>();
    private int checkDistrict = 0;
    private int checkSEUsers = 0;
    private String districtNameEng = "";
    private String districtId = "0";
    private String seUserName = "";
    private String seUserId = "0";
    private PrefManager prefManager;
    private List<ModelPartsList> modelPartsList = new ArrayList<>();
    private List<ModelSEUsersList> modelSEUsersList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityShiftingStockBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initView();
    }

    private void initView() {
        binding.actionBar.ivThreeDot.setVisibility(View.GONE);
        binding.actionBar.ivBack.setVisibility(View.VISIBLE);
        binding.actionBar.layoutLanguage.setVisibility(View.GONE);
        binding.actionBar.buttonPDF.setVisibility(View.GONE);
        binding.actionBar.textToolbarTitle.setText(getResources().getString(R.string.shifting_se_stock_to_main_stock));
        prefManager = new PrefManager(mContext);
        viewModel = ViewModelProviders.of(this).get(ComplaintViewModel.class);
        inventoryViewModel = ViewModelProviders.of(this).get(InventoryViewModel.class);

        districtNameEng = "--" + getResources().getString(R.string.district) + "--";
        seUserName = "--" + getResources().getString(R.string.username) + "--";

        setUpClickListener();
        setUpData();
        setUpUserNameSpinner();
        districtList();
//        getSEAvailableStockList(seUserId);
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

        binding.buttonShifting.setOnClickListener(this);
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
                    }else {
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
        }else {
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

    private void getSEAvailableStockList(String seUserId){
        if (Constants.isNetworkAvailable(mContext)) {
            modelPartsList.clear();
            showProgress();
            APIService service = RetrofitInstance.getRetrofitInstance().create(APIService.class);
            Call<ModelParts> call = service.getAllSE_AvlStockPartsList(seUserId, "0");
            call.enqueue(new Callback<ModelParts>() {
                @Override
                public void onResponse(@NonNull Call<ModelParts> call, @NonNull Response<ModelParts> response) {
                    hideProgress();
                    if (response.isSuccessful()){
                        if (response.code() == 200){
                            ModelParts modelParts = response.body();
                            modelPartsList = Objects.requireNonNull(modelParts).getParts();
                            if (modelParts.getStatus().equals("200")) {
                                setPartsListAdapter(modelPartsList);
                            } else {
                                binding.partsListLay.removeAllViews();
                                binding.partsListLay.setVisibility(View.GONE);
                                binding.textNoDataFound.setVisibility(View.VISIBLE);
                            }
                        } else {
                            makeToast(getResources().getString(R.string.error));
                        }
                    } else {
                        makeToast(getResources().getString(R.string.error));
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ModelParts> call, @NonNull Throwable t) {
                    hideProgress();
                    makeToast(getResources().getString(R.string.error_message));
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
            binding.partsListLay.setVisibility(View.VISIBLE);
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
        } else {
            binding.partsListLay.setVisibility(View.GONE);
            binding.textNoDataFound.setVisibility(View.VISIBLE);
            hideProgress();
        }
    }

    private void shiftingStock(List<ModelPartsList> dispatchList){
        if (Constants.isNetworkAvailable(mContext)) {
            if (districtNameEng.equalsIgnoreCase("--" + getResources().getString(R.string.district) + "--")) {
                makeToast(getResources().getString(R.string.please_select_district));
            } else if (seUserName.equalsIgnoreCase("--" + getResources().getString(R.string.username) + "--")) {
                makeToast(getResources().getString(R.string.please_select_username));
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setMessage(getResources().getString(R.string.are_you_sure_you_want_to_shifting_se_stock_to_main_stock))
                        .setCancelable(false)
                        .setPositiveButton(getResources().getString(R.string.yes), (dlg, ids) -> {
                            dlg.dismiss();
                            createDispatchJSONArray(dispatchList);
                        })
                        .setNegativeButton(getResources().getString(R.string.no), (dlg, ids) -> dlg.dismiss());
                AlertDialog alert = builder.create();
                alert.setTitle(getResources().getString(R.string.alert));
                alert.show();
            }
        } else makeToast(getResources().getString(R.string.no_internet_connection));
    }

    private void createDispatchJSONArray(List<ModelPartsList> dispatchList) {
        try {
            JSONArray jsonArray = new JSONArray();
            for (ModelPartsList model : dispatchList) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("ItemId", model.getItemId());
                jsonObject.put("Item_Qty", model.getQuantity());
                jsonArray.put(jsonObject);
            }
            saveReturnPartsDetails(jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void saveReturnPartsDetails(JSONArray jsonArray) {
        if (Constants.isNetworkAvailable(mContext)) {
            APIService service = RetrofitInstance.getRetrofitInstance().create(APIService.class);
            Call<ModelResponse> call = service.saveReturnPartsFromUserSide(prefManager.getUSER_Id(), seUserId, String.valueOf(jsonArray));
            showProgress();
            call.enqueue(new Callback<ModelResponse>() {
                @Override
                public void onResponse(@NonNull Call<ModelResponse> call, @NonNull Response<ModelResponse> response) {
                    hideProgress();
                    if (response.isSuccessful()) {
                        if (response.code() == 200) {
                            ModelResponse model = response.body();
                            if (Objects.requireNonNull(model).getStatus().equals("200")) {
                                onBackPressed();
                            } else {
                                makeToast(model.getMessage());
                            }
                        }
                    } else {
                        makeToast(getResources().getString(R.string.error));
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ModelResponse> call, @NonNull Throwable t) {
                    hideProgress();
                    makeToast(getResources().getString(R.string.error_message));
                }
            });
        } else {
            makeToast(getResources().getString(R.string.no_internet_connection));
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
        if (id == R.id.buttonShifting) {
            List<ModelPartsList> dispatchList = new ArrayList<>();
            for (ModelPartsList model : modelPartsList) {
                if (model.isSelectFlag())
                    dispatchList.add(model);
            }
            if (dispatchList.size() > 0) {
                if (getSelectItemHaveValue(dispatchList)){
                    shiftingStock(dispatchList);
                } else
                    makeToast(getResources().getString(R.string.selected_item_qty));
            } else
                makeToast(getResources().getString(R.string.selected_item_qty));

        } else if (id == R.id.iv_back) {
            onBackPressed();
        }
    }
}