package com.callmangement.ui.distributor.activity;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProviders;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import com.bumptech.glide.Glide;
import com.callmangement.Network.APIService;
import com.callmangement.Network.RetrofitInstance;
import com.callmangement.R;
import com.callmangement.custom.CustomActivity;
import com.callmangement.databinding.ActivityPosDistributionFormViewBinding;
import com.callmangement.model.district.ModelDistrictList;
import com.callmangement.model.pos_distribution_form.ModelEquipmentModel;
import com.callmangement.model.pos_distribution_form.ModelNewMachineMake;
import com.callmangement.model.pos_distribution_form.ModelOldMachineMake;
import com.callmangement.ui.complaint.ComplaintViewModel;
import com.callmangement.ui.distributor.model.PosDistributionDetail;
import com.callmangement.ui.distributor.model.PosDistributionFormDetailResponse;
import com.callmangement.ui.home.ZoomInZoomOutActivity;
import com.callmangement.utils.Constants;
import com.callmangement.utils.PrefManager;
import java.util.ArrayList;
import java.util.List;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PosDistributionFormViewActivity extends CustomActivity implements View.OnClickListener {
    private ActivityPosDistributionFormViewBinding binding;
    private PrefManager prefManager;
    private PosDistributionDetail model;
    private final List<ModelEquipmentModel> listEquipmentModel = new ArrayList<>();
    private final List<ModelOldMachineMake> listOldMachineMake = new ArrayList<>();
    private final List<ModelNewMachineMake> listNewMachineMake = new ArrayList<>();
    private ComplaintViewModel viewModel;
    private List<ModelDistrictList> district_List = new ArrayList<>();
    private String photoPath = "";
    private String formPhotoPath = "";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPosDistributionFormViewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.actionBar.ivBack.setVisibility(View.VISIBLE);
        binding.actionBar.ivThreeDot.setVisibility(View.GONE);
        binding.actionBar.layoutLanguage.setVisibility(View.GONE);
        binding.actionBar.buttonPDF.setVisibility(View.GONE);
        binding.actionBar.textToolbarTitle.setText(getResources().getString(R.string.pos_distribution_form));
        viewModel = ViewModelProviders.of(this).get(ComplaintViewModel.class);
        prefManager = new PrefManager(mContext);
        model = (PosDistributionDetail) getIntent().getSerializableExtra("param");
        initView();
    }

    private void initView() {

        setUpClickListener();
        setEquipmentModelSpinner();
        setNewMachineMakeSpinner();
        setOldMachineMakeSpinner();
        districtList();
        getPosDistributionDetail();
    }

    private void setUpClickListener(){
        binding.actionBar.ivBack.setOnClickListener(this);
        binding.rlPhoto.setOnClickListener(this);
        binding.rlUploadFormPhoto.setOnClickListener(this);
        binding.actionBar.buttonPDF.setOnClickListener(this);
    }

    private void setEquipmentModelSpinner() {
        ModelEquipmentModel model = new ModelEquipmentModel();
        model.setId("1");
        model.setName("Mobiocean TPS 900 (Android 10 OS)");
        listEquipmentModel.add(model);
        ArrayAdapter<ModelEquipmentModel> dataAdapter = new ArrayAdapter<>(mContext, android.R.layout.simple_spinner_item, listEquipmentModel);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerEquipmentModel.setAdapter(dataAdapter);
    }

    private void setOldMachineMakeSpinner() {
        ModelOldMachineMake model1 = new ModelOldMachineMake();
        model1.setId("1");
        model1.setName("VISIONTEK");
        listOldMachineMake.add(model1);
        ModelOldMachineMake model2 = new ModelOldMachineMake();
        model2.setId("2");
        model2.setName("ANALOGICS");
        listOldMachineMake.add(model2);
        ArrayAdapter<ModelOldMachineMake> dataAdapter = new ArrayAdapter<>(mContext, android.R.layout.simple_spinner_item, listOldMachineMake);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerOldMachineMake.setAdapter(dataAdapter);
    }

    private void setNewMachineMakeSpinner() {
        ModelNewMachineMake model1 = new ModelNewMachineMake();
        model1.setId("3");
        model1.setName("MOBIOCEAN");
        listNewMachineMake.add(model1);
        ArrayAdapter<ModelNewMachineMake> dataAdapter = new ArrayAdapter<>(mContext, android.R.layout.simple_spinner_item, listNewMachineMake);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerNewMachineMake.setAdapter(dataAdapter);
    }

    private void districtList() {
        if (Constants.isNetworkAvailable(mContext)) {
            isLoading();
            viewModel.getDistrict().observe(this, modelDistrict -> {
                isLoading();
                if (modelDistrict.status.equals("200")) {
                    district_List = modelDistrict.district_List;
                    if (district_List != null && district_List.size() > 0) {
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

    private void isLoading() {
        viewModel.getIsLoading().observe(this, aBoolean -> {
            if (aBoolean) {
                showProgress(getResources().getString(R.string.please_wait));
            } else {
                hideProgress();
            }
        });
    }

    private void getPosDistributionDetail(){
        if (Constants.isNetworkAvailable(mContext)) {
            showProgress();
            APIService service = RetrofitInstance.getRetrofitInstance().create(APIService.class);
            Call<ResponseBody> call = service.getPosDistributionDetailByTIDAPI( prefManager.getUSER_Id(), String.valueOf(model.getTranId()));
            call.enqueue(new Callback<ResponseBody>() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                    hideProgress();
                    if (response.isSuccessful()) {
                        try {
                            if (response.code() == 200) {
                                if (response.body() != null) {
                                    String responseStr = response.body().string();
                                    PosDistributionFormDetailResponse modelResponse = (PosDistributionFormDetailResponse) getObject(responseStr, PosDistributionFormDetailResponse.class);
                                    PosDistributionDetail posDistributionDetail = modelResponse.getPosDistributionDetail();
                                    if (modelResponse.getStatus().equals("200")) {
                                        if (posDistributionDetail != null){
                                            binding.inputDate.setText(posDistributionDetail.getTranDateStr());
                                            binding.inputFpsCode.setText(posDistributionDetail.getFpscode());
                                            binding.inputDealerName.setText(posDistributionDetail.getDealerName());
                                            binding.inputMobileNumber.setText(posDistributionDetail.getMobileNo());
                                            binding.inputTicketNumber.setText(posDistributionDetail.getTicketNo());
                                            binding.inputBlockName.setText(posDistributionDetail.getBlockName());
                                            binding.inputOldMachineSrNo.setText(posDistributionDetail.getOldMachineSerialNo());
                                            binding.inputFingerprintSrNo.setText(posDistributionDetail.getOldMachineBiometricSeriallNo());
                                            binding.inputNewMachineOrderNo.setText(String.valueOf(posDistributionDetail.getNewMachineOrderNo()));
                                            binding.inputNewMachineSrNo.setText(posDistributionDetail.getNewMachineSerialNo());
                                            binding.inputNewFingerprintSrNo.setText(posDistributionDetail.getNewMachineBiometricSeriallNo());
                                            binding.inputIMEIIMEI2.setText(posDistributionDetail.getNewMachineIMEI1()+" - "+posDistributionDetail.getNewMachineIMEI2());
                                            binding.spinnerDistrict.setEnabled(false);
                                            binding.spinnerEquipmentModel.setEnabled(false);
                                            binding.spinnerOldMachineMake.setEnabled(false);
                                            binding.spinnerNewMachineMake.setEnabled(false);
                                            photoPath = Constants.API_BASE_URL+posDistributionDetail.getUpPhotoPath();
                                            formPhotoPath = Constants.API_BASE_URL+posDistributionDetail.getUpFormPath();
                                            Glide.with(mContext)
                                                    .load(Constants.API_BASE_URL+posDistributionDetail.getUpPhotoPath())
                                                    .placeholder(R.drawable.image_not_fount)
                                                    .into(binding.ivPhoto);
                                            Glide.with(mContext)
                                                    .load(Constants.API_BASE_URL+posDistributionDetail.getUpFormPath())
                                                    .placeholder(R.drawable.image_not_fount)
                                                    .into(binding.ivUploadFormPhoto);
                                            if (posDistributionDetail.getWhetherOldMachProvdedForReplacmnt()){
                                                binding.chkYes.setChecked(true);
                                                binding.chkNo.setChecked(false);
                                            } else {
                                                binding.chkNo.setChecked(true);
                                                binding.chkYes.setChecked(false);
                                            }
                                            if (posDistributionDetail.getOldMachineWorkingStatus().equalsIgnoreCase("Running")){
                                                binding.chkRunning.setChecked(true);
                                                binding.chkDead.setChecked(false);
                                            } else if (posDistributionDetail.getOldMachineWorkingStatus().equalsIgnoreCase("Dead")){
                                                binding.chkRunning.setChecked(false);
                                                binding.chkDead.setChecked(true);
                                            }
                                            binding.chkCompleteWithSatisfactorily.setChecked(posDistributionDetail.getIsCompleteWithSatisfactorily());
                                            if (district_List.size() > 0){
                                                for (int i = 0; i < district_List.size(); i++){
                                                    if (posDistributionDetail.getDistrictId() == Integer.parseInt(district_List.get(i).getDistrictId())){
                                                        binding.spinnerDistrict.setSelection(i);
                                                        binding.spinnerDistrict.setEnabled(false);
                                                    }
                                                }
                                            }

                                            if (listEquipmentModel.size() > 0){
                                                for (int i = 0; i < listEquipmentModel.size(); i++){
                                                    if (posDistributionDetail.getEquipmentModelId() == Integer.parseInt(listEquipmentModel.get(i).getId())){
                                                        binding.spinnerEquipmentModel.setSelection(i);
                                                        binding.spinnerEquipmentModel.setEnabled(false);
                                                    }
                                                }
                                            }

                                            if (listOldMachineMake.size() > 0){
                                                for (int i = 0; i < listOldMachineMake.size(); i++){
                                                    if (posDistributionDetail.getOldMachineVenderid() == Integer.parseInt(listOldMachineMake.get(i).getId())){
                                                        binding.spinnerOldMachineMake.setSelection(i);
                                                        binding.spinnerOldMachineMake.setEnabled(false);
                                                    }
                                                }
                                            }
                                            if (listNewMachineMake.size() > 0){
                                                for (int i = 0; i < listNewMachineMake.size(); i++){
                                                    if (posDistributionDetail.getNewMachineVenderid() == Integer.parseInt(listNewMachineMake.get(i).getId())){
                                                        binding.spinnerNewMachineMake.setSelection(i);
                                                        binding.spinnerNewMachineMake.setEnabled(false);
                                                    }
                                                }
                                            }
                                        }
                                    } else {
                                        makeToast(modelResponse.getMessage());
                                    }
                                } else {
                                    makeToast(getResources().getString(R.string.error));
                                }
                            } else {
                                makeToast(getResources().getString(R.string.error));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        makeToast(getResources().getString(R.string.error));
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                    hideProgress();
                    makeToast(getResources().getString(R.string.error_message));
                }
            });
        } else {
            makeToast(getResources().getString(R.string.no_internet_connection));
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.iv_back) {
            onBackPressed();
        } else if (id == R.id.rl_photo) {
            startActivity(new Intent(mContext, ZoomInZoomOutActivity.class).putExtra("image", photoPath));
        } else if (id == R.id.rl_upload_form_photo) {
            startActivity(new Intent(mContext, ZoomInZoomOutActivity.class).putExtra("image", formPhotoPath));
        }
    }
}