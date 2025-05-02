package com.callmangement.ui.reset_device;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.callmangement.Network.APIService;
import com.callmangement.Network.RetrofitInstance;
import com.callmangement.R;
import com.callmangement.adapter.DialogChallanForDispatchAdapter;
import com.callmangement.custom.CustomActivity;
import com.callmangement.databinding.ActivityResetDeviceBinding;
import com.callmangement.model.district.ModelDistrictList;
import com.callmangement.model.inventrory.ModelPartsList;
import com.callmangement.model.inventrory.ModelSEUsers;
import com.callmangement.model.inventrory.ModelSEUsersList;
import com.callmangement.model.reset_device.ModelResetDevice;
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

public class ResetDeviceActivity extends CustomActivity implements View.OnClickListener {
    ActivityResetDeviceBinding binding;
    private PrefManager prefManager;
    private ComplaintViewModel viewModel;
    private List<ModelDistrictList> district_List = new ArrayList<>();
    private List<ModelSEUsersList> modelSEUsersList = new ArrayList<>();
    private int checkDistrict = 0;
    private int checkSEUsers = 0;
    private String districtNameEng = "";
    private String districtId = "0";
    private String seUserName = "";
    private String seUserId = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityResetDeviceBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initView();
    }

    private void initView() {
        binding.actionBar.ivThreeDot.setVisibility(View.GONE);
        binding.actionBar.ivBack.setVisibility(View.VISIBLE);
        binding.actionBar.layoutLanguage.setVisibility(View.GONE);
        binding.actionBar.buttonPDF.setVisibility(View.GONE);
        binding.actionBar.textToolbarTitle.setText(getResources().getString(R.string.reset_device));
        prefManager = new PrefManager(mContext);
        viewModel = ViewModelProviders.of(this).get(ComplaintViewModel.class);

        setUpClickListener();
        setUpData();
        setUpUserNameSpinner();
        districtList();
    }

    private void setUpClickListener() {
        binding.actionBar.ivBack.setOnClickListener(this);
        binding.buttonResetDevice.setOnClickListener(this);
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
                        districtId = "0";
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
                    if (seUserName.equalsIgnoreCase("--" + getResources().getString(R.string.username) + "--")) {
                        seUserId = "0";
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void districtList() {
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
    }

    private void resetDevice() {
        if (Constants.isNetworkAvailable(mContext)) {
            APIService service = RetrofitInstance.getRetrofitInstance().create(APIService.class);
            Call<ModelResetDevice> call = service.resetDeviceIdTokenID(seUserId, districtId);
            showProgress();
            call.enqueue(new Callback<ModelResetDevice>() {
                @Override
                public void onResponse(@NonNull Call<ModelResetDevice> call, @NonNull Response<ModelResetDevice> response) {
                    hideProgress();
                    if (response.isSuccessful()) {
                        ModelResetDevice model = response.body();
                        if (Objects.requireNonNull(model).getStatus().equals("200")) {
                            dialogResetDeviceSuccess();
                            binding.spinnerDistrict.setSelection(0);
                            binding.spinnerServiceEngineer.setSelection(0);
                        } else {
                            makeToast(model.getMessage());
                        }
                    } else {
                        makeToast(getResources().getString(R.string.error));
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ModelResetDevice> call, @NonNull Throwable t) {
                    hideProgress();
                    makeToast(getResources().getString(R.string.error_message));
                }
            });
        } else {
            makeToast(getResources().getString(R.string.no_internet_connection));
        }
    }

    @SuppressLint("SetTextI18n")
    private void alertDialogResetDevice() {
        try {
            final Dialog dialog = new Dialog(mContext);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.dialog_reset_device_confirmation);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.setCanceledOnTouchOutside(false);

            Button buttonNo = dialog.findViewById(R.id.buttonNo);
            Button buttonYes = dialog.findViewById(R.id.buttonYes);
            buttonNo.setOnClickListener(view -> {
                dialog.dismiss();
            });
            buttonYes.setOnClickListener(view -> {
                resetDevice();
                dialog.dismiss();
            });
            dialog.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("SetTextI18n")
    private void dialogResetDeviceSuccess() {
        try {
            final Dialog dialog = new Dialog(mContext);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.dialog_reset_device_message);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.setCanceledOnTouchOutside(false);

            Button buttonOk = dialog.findViewById(R.id.buttonOk);
            buttonOk.setOnClickListener(view -> {
                dialog.dismiss();
            });
            dialog.show();

        } catch (Exception e) {
            e.printStackTrace();
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
        if (id == R.id.iv_back) {
            onBackPressed();
        } else if (id == R.id.buttonResetDevice) {
            alertDialogResetDevice();
        }
    }
}