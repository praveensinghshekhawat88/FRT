package com.callmangement.ui.distributor.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.Formatter;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.ViewModelProviders;

import com.callmangement.Network.APIService;
import com.callmangement.Network.MultipartRequester;
import com.callmangement.Network.RetrofitInstance;
import com.callmangement.R;
import com.callmangement.custom.CustomActivity;
import com.callmangement.databinding.ActivityNewPosDistributionFormBinding;
import com.callmangement.imagepicker.model.Config;
import com.callmangement.imagepicker.model.Image;
import com.callmangement.imagepicker.ui.imagepicker.ImagePicker;
import com.callmangement.model.district.ModelDistrictList;
import com.callmangement.model.pos_distribution_form.ModelEquipmentModel;
import com.callmangement.model.pos_distribution_form.ModelNewMachineDetailByFPSResponse;
import com.callmangement.model.pos_distribution_form.ModelNewMachineMake;
import com.callmangement.model.pos_distribution_form.ModelOldMachineDetailByFPSResponse;
import com.callmangement.model.pos_distribution_form.ModelOldMachineMake;
import com.callmangement.support.ImageUtilsForRotate;
import com.callmangement.ui.complaint.ComplaintViewModel;
import com.callmangement.utils.CompressImage;
import com.callmangement.utils.Constants;
import com.callmangement.utils.DateTimeUtils;
import com.callmangement.utils.PrefManager;
import com.callmangement.support.dexter.Dexter;
import com.callmangement.support.dexter.MultiplePermissionsReport;
import com.callmangement.support.dexter.PermissionToken;
import com.callmangement.support.dexter.listener.PermissionRequest;
import com.callmangement.support.dexter.listener.multi.MultiplePermissionsListener;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewPosDistributionFormActivity extends CustomActivity implements View.OnClickListener {
    private ActivityNewPosDistributionFormBinding binding;
    private String photoStoragePath = "";
    private boolean permissionGranted;
    public final int REQUEST_PICK_PHOTO = 1113;
    private final List<ModelEquipmentModel> listEquipmentModel = new ArrayList<>();
    private final List<ModelOldMachineMake> listOldMachineMake = new ArrayList<>();
    private final List<ModelNewMachineMake> listNewMachineMake = new ArrayList<>();
    private ComplaintViewModel viewModel;
    private final int checkDistrict = 0;
    private String districtNameEng = "";
    private String districtId = "0";
    private final String tranId = "0";
    private PrefManager prefManager;
    private List<ModelDistrictList> district_List = new ArrayList<>();
    private final boolean flagGetOldMachineDetail = false;
    private String whetherOldMachineProvidedForReplacement = "1";
    private String equipmentModelName = "";
    private String equipmentModelId = "1";
    private String oldMachineMakeId = "1";
    private String newMachineMakeId = "";
    private String oldMachineCondition = "";
    private String completeWithSatisfactorily = "1";

    private String oldMachineSrNo = "";
    private String fingerPrintSrNo = "";

    AlertDialog.Builder builder = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNewPosDistributionFormBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.actionBar.ivBack.setVisibility(View.VISIBLE);
        binding.actionBar.ivThreeDot.setVisibility(View.GONE);
        binding.actionBar.layoutLanguage.setVisibility(View.GONE);
        binding.actionBar.buttonPDF.setVisibility(View.GONE);
        binding.actionBar.textToolbarTitle.setText(getResources().getString(R.string.pos_distribution_form));
        prefManager = new PrefManager(mContext);
        viewModel = ViewModelProviders.of(this).get(ComplaintViewModel.class);
        initView();
    }

    private void initView() {
        districtNameEng = "--" + getResources().getString(R.string.district) + "--";
        binding.inputDealerName.setEnabled(false);
        binding.inputMobileNumber.setEnabled(false);
        binding.inputBlockName.setEnabled(false);
        binding.inputNewMachineSrNo.setEnabled(false);
        binding.inputNewFingerprintSrNo.setEnabled(false);
        binding.inputIMEIIMEI2.setEnabled(false);
        binding.inputAccessoriesProvided.setEnabled(false);
        binding.inputDate.setText(DateTimeUtils.getCurrentDate());
        setUpOnClickListener();
        checkPermission();
        districtList();
        setEquipmentModelSpinner();
        setOldMachineMakeSpinner();
        setNewMachineMakeSpinner();
    }

    private void setUpOnClickListener() {
        binding.spinnerDistrict.setEnabled(false);
        binding.inputFpsCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable charSequence) {
                if (charSequence.toString().length() > 0) {
                    new Handler().postDelayed(() -> getOldMachineDetailsByFPSAPI(charSequence.toString()), 2500);
                } else {
                    Objects.requireNonNull(binding.inputDealerName.getText()).clear();
                    Objects.requireNonNull(binding.inputMobileNumber.getText()).clear();
                    Objects.requireNonNull(binding.inputBlockName.getText()).clear();
                    Objects.requireNonNull(binding.inputOldMachineSrNo.getText()).clear();
                    Objects.requireNonNull(binding.inputFingerprintSrNo.getText()).clear();
                }
            }
        });

        binding.inputNewMachineOrderNo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable charSequence) {
                if (charSequence.toString().length() > 0) {
                    new Handler().postDelayed(() -> getNewMachineDetailsByOrdNoAPI(charSequence.toString()), 2000);
                } else {
                    Objects.requireNonNull(binding.inputNewMachineSrNo.getText()).clear();
                    Objects.requireNonNull(binding.inputNewFingerprintSrNo.getText()).clear();
                    Objects.requireNonNull(binding.inputIMEIIMEI2.getText()).clear();
                    Objects.requireNonNull(binding.inputAccessoriesProvided.getText()).clear();
                }
            }
        });

        binding.inputOldMachineSrNo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().length() > 0) {
                    if (charSequence.toString().equals(oldMachineSrNo))
                        binding.inputFingerprintSrNo.setText(fingerPrintSrNo);
                    else binding.inputFingerprintSrNo.setText("");
                } else binding.inputFingerprintSrNo.setText("");
            }

            @Override
            public void afterTextChanged(Editable charSequence) {

            }
        });

        binding.spinnerDistrict.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                districtNameEng = district_List.get(i).districtNameEng;
                districtId = district_List.get(i).getDistrictId();
                /*if (!districtId.equals("0") && Objects.requireNonNull(binding.inputFpsCode.getText()).toString().trim().length() > 0)
                    getOldMachineDetailsByFPSAPI(binding.inputFpsCode.getText().toString());*/
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        binding.spinnerEquipmentModel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                equipmentModelName = listEquipmentModel.get(i).getName();
                equipmentModelId = listEquipmentModel.get(i).getId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        binding.spinnerOldMachineMake.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                oldMachineMakeId = listOldMachineMake.get(i).getId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        binding.spinnerNewMachineMake.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                newMachineMakeId = listNewMachineMake.get(i).getId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        binding.chkYes.setOnCheckedChangeListener((compoundButton, checked) -> {
            if (checked) {
                whetherOldMachineProvidedForReplacement = "1";
                binding.chkNo.setChecked(false);
            } else {
                whetherOldMachineProvidedForReplacement = "0";
            }
        });

        binding.chkNo.setOnCheckedChangeListener((compoundButton, checked) -> {
            if (checked) {
                whetherOldMachineProvidedForReplacement = "0";
                binding.chkYes.setChecked(false);
            } else {
                whetherOldMachineProvidedForReplacement = "1";
                binding.chkYes.setChecked(true);
            }
        });


        binding.chkCompleteWithSatisfactorily.setOnCheckedChangeListener((compoundButton, checked) -> {
            if (checked) {
                completeWithSatisfactorily = "1";
            } else {
                completeWithSatisfactorily = "0";
            }
        });

        binding.chkRunning.setOnCheckedChangeListener((compoundButton, checked) -> {
            if (checked) {
                oldMachineCondition = "Running";
                binding.chkDead.setChecked(false);
            } /*else {
                oldMachineCondition = "";
            }*/
        });

        binding.chkDead.setOnCheckedChangeListener((compoundButton, checked) -> {
            if (checked) {
                oldMachineCondition = "Dead";
                binding.chkRunning.setChecked(false);
            } /*else {
                oldMachineCondition = "";
            }*/
        });

        binding.buttonSubmit.setOnClickListener(this);
        binding.ivPhoto.setOnClickListener(this);
        binding.actionBar.ivBack.setOnClickListener(this);
    }

    private void setEquipmentModelSpinner() {
        ModelEquipmentModel model = new ModelEquipmentModel();
        model.setId("1");
        model.setName("Mobiocean TPS 900 (Android 10 OS)");
        listEquipmentModel.add(model);
        ArrayAdapter<ModelEquipmentModel> dataAdapter = new ArrayAdapter<>(mContext, android.R.layout.simple_spinner_item, listEquipmentModel);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerEquipmentModel.setAdapter(dataAdapter);
        binding.spinnerEquipmentModel.setSelection(0);
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
        binding.spinnerOldMachineMake.setSelection(0);
    }

    private void setNewMachineMakeSpinner() {
        ModelNewMachineMake model1 = new ModelNewMachineMake();
        model1.setId("3");
        model1.setName("MOBIOCEAN");
        listNewMachineMake.add(model1);
        ArrayAdapter<ModelNewMachineMake> dataAdapter = new ArrayAdapter<>(mContext, android.R.layout.simple_spinner_item, listNewMachineMake);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerNewMachineMake.setAdapter(dataAdapter);
        binding.spinnerNewMachineMake.setSelection(0);
    }

    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Dexter.withContext(this)
                    .withPermissions(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.READ_MEDIA_IMAGES,
                            Manifest.permission.READ_MEDIA_VIDEO,
                            Manifest.permission.CAMERA,
                            Manifest.permission.ACCESS_WIFI_STATE
                    ).withListener(new MultiplePermissionsListener() {
                        @Override
                        public void onPermissionsChecked(MultiplePermissionsReport report) {
                            if (report.areAllPermissionsGranted()) {
                                permissionGranted = true;
                            }
                        }

                        @Override
                        public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                            token.continuePermissionRequest();
                        }
                    }).check();
        } else {


            Dexter.withContext(this)
                    .withPermissions(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.CAMERA,
                            Manifest.permission.ACCESS_WIFI_STATE
                    ).withListener(new MultiplePermissionsListener() {
                        @Override
                        public void onPermissionsChecked(MultiplePermissionsReport report) {
                            if (report.areAllPermissionsGranted()) {
                                permissionGranted = true;
                            }
                        }

                        @Override
                        public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                            token.continuePermissionRequest();
                        }
                    }).check();
        }
    }

    private void districtList() {
        if (Constants.isNetworkAvailable(mContext)) {
            isLoading();
            viewModel.getDistrict().observe(this, modelDistrict -> {
                isLoading();
                if (modelDistrict.status.equals("200")) {
                    district_List = modelDistrict.district_List;
                    if (district_List != null && district_List.size() > 0) {
                        /*Collections.reverse(district_List);
                        ModelDistrictList_w l = new ModelDistrictList_w();
                        l.setDistrictId(String.valueOf(-1));
                        l.setDistrictNameEng("--" + getResources().getString(R.string.district) + "--");
                        district_List.add(l);
                        Collections.reverse(district_List);*/
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

    private void selectPhoto() {
        try {
            final CharSequence[] items = {getResources().getString(R.string.imagepicker_str_take_photo), getResources().getString(R.string.imagepicker_str_cancel)};
            TextView title = new TextView(mContext);
            title.setText(getResources().getString(R.string.capture_photo));
            title.setBackgroundColor(getResources().getColor(R.color.colorActionBar));
            title.setPadding(15, 25, 15, 25);
            title.setGravity(Gravity.CENTER);
            title.setTextColor(Color.WHITE);
            title.setTextSize(22);

            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setCustomTitle(title);
            builder.setItems(items, (dialog, item) -> {
                if (items[item].equals(getResources().getString(R.string.imagepicker_str_take_photo))) {
                    ImagePicker.with(mContext)
                            .setToolbarColor("#212121")
                            .setStatusBarColor("#000000")
                            .setToolbarTextColor("#FFFFFF")
                            .setToolbarIconColor("#FFFFFF")
                            .setProgressBarColor("#4CAF50")
                            .setBackgroundColor("#212121")
                            .setCameraOnly(true)
                            .setMultipleMode(true)
                            .setFolderMode(true)
                            .setShowCamera(true)
                            .setFolderTitle("Albums")
                            .setImageTitle("Galleries")
                            .setDoneTitle("Done")
                            .setMaxSize(1)
                            .setSavePath(Constants.saveImagePath)
                            .setSelectedImages(new ArrayList<>())
                            .start(REQUEST_PICK_PHOTO);

                } else if (items[item].equals(getResources().getString(R.string.imagepicker_str_cancel))) {
                    dialog.dismiss();
                }
            });
            builder.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_PICK_PHOTO && resultCode == RESULT_OK && data != null) {
            ArrayList<Image> images = data.getParcelableArrayListExtra(Config.EXTRA_IMAGES);
            if (images != null && images.size() > 0) {
                Image image = images.get(0);
                photoStoragePath = image.getPath();
                if (photoStoragePath.contains("file:/")) {
                    photoStoragePath = photoStoragePath.replace("file:/", "");
                }
                photoStoragePath = CompressImage.compress(photoStoragePath, this);
                File imgFile = new File(photoStoragePath);
                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());

                try {
                    binding.ivPhoto.setImageBitmap(ImageUtilsForRotate.ensurePortrait(photoStoragePath));
                } catch (IOException e) {
                    binding.ivPhoto.setImageBitmap(myBitmap);
                    e.printStackTrace();
                }catch (NullPointerException e){
                    e.printStackTrace();
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void getOldMachineDetailsByFPSAPI(String fpsCode) {
        if (Constants.isNetworkAvailable(mContext)) {
            /*if (districtId.equals("0")) {
                if (!flagGetOldMachineDetail) {
                    Toast.makeText(mContext, getResources().getString(R.string.please_select_district), Toast.LENGTH_SHORT).show();
                    flagGetOldMachineDetail = true;
                }
                return;
            }*/
            APIService service = RetrofitInstance.getRetrofitInstance().create(APIService.class);
            Call<ResponseBody> call = service.GetOldMachineDetailsByFPSAPI(districtId, prefManager.getUSER_Id(), fpsCode);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        try {
                            if (response.code() == 200) {
                                if (response.body() != null) {
                                    String responseStr = response.body().string();
                                //    Log.e("response", responseStr);
                                    JSONObject jsonObject = new JSONObject((responseStr));
                                    String status = jsonObject.optString("status");
                                    ModelOldMachineDetailByFPSResponse modelResponse = (ModelOldMachineDetailByFPSResponse) getObject(responseStr, ModelOldMachineDetailByFPSResponse.class);
                                    if (status.equals("200")) {
                                        if (modelResponse != null) {
                                            binding.inputDealerName.setEnabled(false);
                                            binding.inputMobileNumber.setEnabled(false);
                                            binding.inputBlockName.setEnabled(false);
//                                            binding.inputOldMachineSrNo.setEnabled(false);
                                            binding.inputFingerprintSrNo.setEnabled(false);
                                            binding.inputTicketNumber.setEnabled(false);

                                            oldMachineSrNo = modelResponse.oldMachineData.getOldMachineSerialNo();
                                            fingerPrintSrNo = modelResponse.oldMachineData.getOldMachineBiometricSeriallNo();

                                            binding.inputDealerName.setText(modelResponse.oldMachineData.getDealerName());
                                            binding.inputMobileNumber.setText(modelResponse.oldMachineData.getMobileNo());
                                            binding.inputBlockName.setText(modelResponse.oldMachineData.getBlockName());
                                            binding.inputOldMachineSrNo.setText(oldMachineSrNo);
                                            binding.inputFingerprintSrNo.setText(fingerPrintSrNo);
                                            binding.inputTicketNumber.setText(modelResponse.oldMachineData.getTicketNo());

                                            districtId = modelResponse.oldMachineData.getDistrictId().toString();
                                            binding.spinnerDistrict.setSelection(getSelectedDistrictSpinnerIndex(modelResponse.oldMachineData.getDistrictId().toString()));
                                        }
                                    } else {
                                        dialogMessage(modelResponse.getMessage());
                                        binding.inputDealerName.setText("");
                                        binding.inputMobileNumber.setText("");
                                        binding.inputBlockName.setText("");
                                        binding.inputOldMachineSrNo.setText("");
                                        binding.inputFingerprintSrNo.setText("");
                                        binding.inputTicketNumber.setText("");
                                        binding.spinnerDistrict.setSelection(-1);
                                    }
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
//                        makeToast(getResources().getString(R.string.error));
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                    makeToast(getResources().getString(R.string.error_message));
                }
            });
        } else {
            makeToast(getResources().getString(R.string.no_internet_connection));
        }
    }

    private int getSelectedDistrictSpinnerIndex(String districtId) {
        int index = 0;
        try {
            if (district_List != null && district_List.size() > 0) {
                for (int i = 0; i < district_List.size(); i++) {
                    if (districtId.equals(district_List.get(i).getDistrictId())) {
                        index = i;
                        break;
                    }
                }
            }
            return index;
        } catch (Exception e) {
            e.printStackTrace();
            return index;
        }
    }

    private void getNewMachineDetailsByOrdNoAPI(String newMachineOrderNo) {
        if (Constants.isNetworkAvailable(mContext)) {
            APIService service = RetrofitInstance.getRetrofitInstance().create(APIService.class);
            Call<ResponseBody> call = service.GetNewMachineDetailsByOrdNoAPI(/*"0"*/districtId, prefManager.getUSER_Id(), newMachineOrderNo);
            call.enqueue(new Callback<ResponseBody>() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        try {
                            if (response.code() == 200) {
                                if (response.body() != null) {
                                    String responseStr = response.body().string();
                                //    Log.e("response", responseStr);
                                    JSONObject jsonObject = new JSONObject((responseStr));
                                    String status = jsonObject.optString("status");
                                    ModelNewMachineDetailByFPSResponse modelResponse = (ModelNewMachineDetailByFPSResponse) getObject(responseStr, ModelNewMachineDetailByFPSResponse.class);
                                    if (status.equals("200")) {
                                        if (modelResponse != null) {
                                            binding.inputNewMachineSrNo.setEnabled(false);
                                            binding.inputNewFingerprintSrNo.setEnabled(false);
                                            binding.inputIMEIIMEI2.setEnabled(false);
                                            binding.inputAccessoriesProvided.setEnabled(false);

                                            binding.inputNewMachineSrNo.setText(modelResponse.newMachineData.newMachineSerialNo);
                                            binding.inputNewFingerprintSrNo.setText(modelResponse.newMachineData.newMachineBiometricSeriallNo);
                                            binding.inputIMEIIMEI2.setText(modelResponse.newMachineData.newMachineIMEI1 + " - " + modelResponse.newMachineData.newMachineIMEI2);
                                            binding.inputAccessoriesProvided.setText(modelResponse.newMachineData.getAccessoriesProvided());
                                        }
                                    } else {
                                        dialogMessage(modelResponse.message);
                                        binding.inputNewMachineSrNo.setText("");
                                        binding.inputNewFingerprintSrNo.setText("");
                                        binding.inputIMEIIMEI2.setText("");
                                        binding.inputAccessoriesProvided.setText("");
                                    }
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
//                        makeToast(getResources().getString(R.string.error));
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                    makeToast(getResources().getString(R.string.error_message));
                }
            });
        } else {
            makeToast(getResources().getString(R.string.no_internet_connection));
        }
    }

    private void submit() {
        if (Constants.isNetworkAvailable(mContext)) {
            String tranDate = Objects.requireNonNull(binding.inputDate.getText()).toString().trim();
            String fpsCode = Objects.requireNonNull(binding.inputFpsCode.getText()).toString().trim();
            String newMachineOrderNo = Objects.requireNonNull(binding.inputNewMachineOrderNo.getText()).toString().trim();
            String dealerName = Objects.requireNonNull(binding.inputDealerName.getText()).toString().trim();
            String mobileNumber = Objects.requireNonNull(binding.inputMobileNumber.getText()).toString().trim();
            String ticketNumber = Objects.requireNonNull(binding.inputTicketNumber.getText()).toString().trim();
            String blockName = Objects.requireNonNull(binding.inputBlockName.getText()).toString().trim();
            String oldMachineSrNo = Objects.requireNonNull(binding.inputOldMachineSrNo.getText()).toString().trim();
            String oldFingerprintSrNo = Objects.requireNonNull(binding.inputFingerprintSrNo.getText()).toString().trim();
            String newMachineSrNo = Objects.requireNonNull(binding.inputNewMachineSrNo.getText()).toString().trim();
            String newFingerprintSrNo = Objects.requireNonNull(binding.inputNewFingerprintSrNo.getText()).toString().trim();
            String imei1_imei2 = Objects.requireNonNull(binding.inputIMEIIMEI2.getText()).toString().trim();
            String accessoriesProvided = Objects.requireNonNull(binding.inputAccessoriesProvided.getText()).toString().trim();
            String remark = "Receive in new condition with all accessories and satisfactory training given.";

            if (fpsCode.isEmpty()) {
                makeToast(getResources().getString(R.string.please_input_fps_code));
            } else if (newMachineOrderNo.isEmpty()) {
                makeToast(getResources().getString(R.string.please_input_new_machine_order_number));
            } else if (oldMachineCondition.isEmpty()) {
                makeToast(getResources().getString(R.string.please_select_old_machine_condition));
            } else if (photoStoragePath.equals("")) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setMessage(getResources().getString(R.string.submit_form_confirmation_message_without_image))
                        .setCancelable(false)
                        .setPositiveButton(getResources().getString(R.string.yes), (dialog, ids) -> {
                            callSaveFormApi(newMachineOrderNo, fpsCode, ticketNumber, tranDate, oldFingerprintSrNo, dealerName, mobileNumber, blockName);
                            dialog.cancel();
                        })
                        .setNegativeButton(getResources().getString(R.string.no), (dialog, ids) -> dialog.cancel());
                AlertDialog alert = builder.create();
                alert.setTitle(getResources().getString(R.string.alert));
                alert.show();
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setMessage(getResources().getString(R.string.submit_form_confirmation_message))
                        .setCancelable(false)
                        .setPositiveButton(getResources().getString(R.string.yes), (dialog, ids) -> {
                            callSaveFormApi(newMachineOrderNo, fpsCode, ticketNumber, tranDate, oldFingerprintSrNo, dealerName, mobileNumber, blockName);
                            dialog.cancel();
                        })
                        .setNegativeButton(getResources().getString(R.string.no), (dialog, ids) -> dialog.cancel());
                AlertDialog alert = builder.create();
                alert.setTitle(getResources().getString(R.string.alert));
                alert.show();
            }
        } else {
            makeToast(getResources().getString(R.string.no_internet_connection));
        }
    }

    private void callSaveFormApi(String newMachineOrderNo, String fpsCode, String ticketNumber, String tranDate, String oldFingerprintSrNo, String dealerName, String mobileNumber, String blockName) {
        showProgress();
        APIService service = RetrofitInstance.getRetrofitInstance().create(APIService.class);

        RequestBody attachment;
        String fileName = "";
        if (!photoStoragePath.equals("")) {
            fileName = new File(photoStoragePath).getName();
            attachment = RequestBody.create(MediaType.parse("multipart/form-data"), new File(photoStoragePath));
        } else {
            fileName = "";
            attachment = RequestBody.create(MediaType.parse("text/plain"), "");
        }

        Call<ResponseBody> call = service.saveNewPosDistributionAPI(
                MultipartRequester.fromString(prefManager.getUSER_Id()),
                MultipartRequester.fromString(tranId),
                MultipartRequester.fromString(districtId),
                MultipartRequester.fromString(equipmentModelId),
                MultipartRequester.fromString(oldMachineMakeId),
                MultipartRequester.fromString(newMachineMakeId),
                MultipartRequester.fromString(newMachineOrderNo),
                MultipartRequester.fromString(fpsCode),
                MultipartRequester.fromString(ticketNumber),
                MultipartRequester.fromString(tranDate),
                MultipartRequester.fromString(oldMachineSrNo),
                MultipartRequester.fromString(oldFingerprintSrNo),
                MultipartRequester.fromString(oldMachineCondition),
                MultipartRequester.fromString(""),
                MultipartRequester.fromString(""),
                MultipartRequester.fromString(convertStringToUTF8(dealerName)),
                MultipartRequester.fromString(mobileNumber),
                MultipartRequester.fromString(convertStringToUTF8(blockName)),
                MultipartRequester.fromString(getNetworkIp()),
                MultipartRequester.fromString(whetherOldMachineProvidedForReplacement),
                MultipartRequester.fromString(completeWithSatisfactorily),
                MultipartBody.Part.createFormData("dealerImage", fileName, attachment));
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
                             //   Log.e("response", responseStr);
                                JSONObject jsonObject = new JSONObject((responseStr));
                                String status = jsonObject.optString("status");
                                ModelNewMachineDetailByFPSResponse modelResponse = (ModelNewMachineDetailByFPSResponse) getObject(responseStr, ModelNewMachineDetailByFPSResponse.class);
                                if (status.equals("200")) {
                                    if (modelResponse != null) {
//                                        makeToast(modelResponse.getMessage());
                                        onBackPressed();
                                    }
                                } else {
                                    makeToast(modelResponse.message);
                                }
                            }
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
    }

    private String getNetworkIp() {
        WifiManager wm = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        return Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
    }

    private void dialogMessage(String message) {
        if (builder == null) {
            builder = new AlertDialog.Builder(mContext);
            builder.setMessage(message)
                    .setCancelable(false)
                    .setPositiveButton(getResources().getString(R.string.ok), (dialog, ids) -> {
                        builder = null;
                        dialog.cancel();
                    });
                    /*.setNegativeButton(getResources().getString(R.string.cancel), (dialog, ids) -> {
                        builder = null;
                        dialog.cancel();
                    });*/
            AlertDialog alert = builder.create();
            alert.setTitle(getResources().getString(R.string.alert));
            alert.show();
        }
    }

    private String convertStringToUTF8(String s) {
        String out;
        out = new String(s.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1);
        return out;
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.buttonSubmit) {
            submit();
        } else if (id == R.id.ivPhoto) {
            if (permissionGranted) {
                selectPhoto();
            } else {
                makeToast(getResources().getString(R.string.please_allow_all_permission));
            }
        } else if (id == R.id.iv_back) {
            onBackPressed();
        }
    }

}