package com.callmangement.ui.ins_weighing_scale.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.GridLayoutManager;

import com.callmangement.network.APIService;
import com.callmangement.network.MultipartRequester;
import com.callmangement.network.RetrofitInstance;
import com.callmangement.R;
import com.callmangement.custom.CustomActivity;
import com.callmangement.databinding.ActivityDeliverylistDetailsIrisBinding;
import com.callmangement.imagepicker.model.Config;
import com.callmangement.imagepicker.model.Image;
import com.callmangement.imagepicker.ui.imagepicker.ImagePicker;
import com.callmangement.model.weighingDeliveryDetail.weighingDelieryRoot;
import com.callmangement.model.weighingDeliveryDetail.weighingDeliveryData;
import com.callmangement.model.weighingDeliveryDetail.weighingDeliveryImagesDetail;
import com.callmangement.model.logout.ModelLogout;
import com.callmangement.support.ImageUtilsForRotate;
import com.callmangement.support.OnSingleClickListener;
import com.callmangement.ui.ins_weighing_scale.adapter.ViewImagesListingAdapterIris;
import com.callmangement.ui.ins_weighing_scale.model.DeliveredWeightInstal.WeighInsData;
import com.callmangement.ui.ins_weighing_scale.model.SaveInstall.SaveRoot;
import com.callmangement.utils.CompressImage;
import com.callmangement.utils.Constants;
import com.callmangement.utils.PrefManager;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ViewDetail extends CustomActivity implements View.OnClickListener {
    public final int REQUEST_PICK_IMAGE_ONE = 1111;
    public final int REQUEST_PICK_IMAGE_TWO = 1112;
    public final int REQUEST_PICK_IMAGE_THREE = 1113;

    private String partsImageStoragePath1 = "";
    private String partsImageStoragePath2 = "";
    private String partsImageStoragePath3 = "";
    PrefManager preference;
    String selectedValue, FPS_CODE, Input_Device_Code, Input_Remark;
    ArrayList<String> stringArrayListHavingAllFilePath = new ArrayList<>();
    Activity mActivity;
    Context mContext;
    private ActivityDeliverylistDetailsIrisBinding binding;
    //private ModelExpensesList model;
    private WeighInsData model;
    private final ArrayList<String> playerNames = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //will hide the title
        //    getSupportActionBar().hide(); // hide the title bar
        binding = ActivityDeliverylistDetailsIrisBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initView();
    }

    private void initView() {
        mActivity = this;
        mContext = this;
        preference = new PrefManager(mContext);
        binding.actionBar.ivBack.setVisibility(View.VISIBLE);
        binding.actionBar.textToolbarTitle.setText(getResources().getString(R.string.list_detail));

        getIntentData();
        setUpOnClickListener();
        setUpData();
        GetErrorImages();
    }

    private void setUpOnClickListener() {
        binding.actionBar.ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(RESULT_OK);

                finish();

            }
        });

        binding.buttonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String iris_inputserialno = String.valueOf(binding.inputWsSerialno.getText());
                String inputserialRemark = String.valueOf(binding.inputWsReamrk.getText());
                if(iris_inputserialno.trim().length() == 0){
                    String msg = getResources().getString(R.string.please_enter_serialno);
                    showAlertDialogWithSingleButton(mActivity, msg);
                    return;
                }

                if (inputserialRemark.trim().length() == 0) {
                    String msg = getResources().getString(R.string.please_enter_remark);
                    showAlertDialogWithSingleButton(mActivity, msg);
                    return;
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setMessage(getResources().getString(R.string.update_verify_content))
                        .setCancelable(false)
                        .setPositiveButton("OK", (dialog, id) -> {
                            dialog.cancel();
//                            finish();
                            UpdateDelivery();
                        })
                        .setNegativeButton("CANCEL", (dialog, id) -> dialog.cancel());
                AlertDialog alert = builder.create();
                alert.setTitle(getResources().getString(R.string.alert));
                alert.show();

            }
        });


        binding.buttonVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setMessage(getResources().getString(R.string.delivery_verify_content))
                        .setCancelable(false)
                        .setPositiveButton("OK", (dialog, id) -> {
                            dialog.cancel();
//                            finish();
                            verifyDelivery();
                        })
                        .setNegativeButton("CANCEL", (dialog, id) -> dialog.cancel());
                AlertDialog alert = builder.create();
                alert.setTitle(getResources().getString(R.string.alert));
                alert.show();

            }
        });
        //  binding.ivChallanImage.setOnClickListener(this);
    }
    private void UpdateDelivery() {
        if (Constants.isNetworkAvailable(mActivity)) {
            hideKeyboard(mActivity);
            showProgress();

            String USER_Id = preference.getUSER_Id();
            String deliveryid = String.valueOf(model.getDeliveryId());
            String iris_inputserialno = String.valueOf(binding.inputWsSerialno.getText());
            String inputserialRemark = String.valueOf(binding.inputWsReamrk.getText());
            APIService service = RetrofitInstance.getRetrofitInstance().create(APIService.class);



            RequestBody attachmentPartsImage1;
            RequestBody attachmentPartsImage2;
            RequestBody attachmentPartsImage3;
            String fileNamePartsImage1 = "";
            String fileNamePartsImage2 = "";
            String fileNamePartsImage3 = "";
            if (!partsImageStoragePath1.equals("")) {
                fileNamePartsImage1 = new File(partsImageStoragePath1).getName();
                attachmentPartsImage1 = RequestBody.create(MediaType.parse("multipart/form-data"), new File(partsImageStoragePath1));
            } else {
                fileNamePartsImage1 = "";
                attachmentPartsImage1 = RequestBody.create(MediaType.parse("text/plain"), "");
            }

            if (!partsImageStoragePath2.equals("")) {
                fileNamePartsImage2 = new File(partsImageStoragePath2).getName();
                attachmentPartsImage2 = RequestBody.create(MediaType.parse("multipart/form-data"), new File(partsImageStoragePath2));
            } else {
                fileNamePartsImage2 = "";
                attachmentPartsImage2 = RequestBody.create(MediaType.parse("text/plain"), "");
            }

            if (!partsImageStoragePath3.equals("")) {
                fileNamePartsImage3 = new File(partsImageStoragePath3).getName();
                attachmentPartsImage3 = RequestBody.create(MediaType.parse("multipart/form-data"), new File(partsImageStoragePath3));
            } else {
                fileNamePartsImage3 = "";
                attachmentPartsImage3 = RequestBody.create(MediaType.parse("text/plain"), "");
            }


            Call<SaveRoot> call = service.updateDelivery(
                    MultipartRequester.fromString(USER_Id),
                    MultipartRequester.fromString(deliveryid),
                    MultipartRequester.fromString(iris_inputserialno.trim()),
                    MultipartRequester.fromString(inputserialRemark.trim()),
                    MultipartBody.Part.createFormData("WeighingImage", fileNamePartsImage2, attachmentPartsImage2),
                    MultipartBody.Part.createFormData("DealerImage", fileNamePartsImage1, attachmentPartsImage1),
                    MultipartBody.Part.createFormData("ChallanImage", fileNamePartsImage3, attachmentPartsImage3));

            call.enqueue(new Callback<SaveRoot>() {
                @Override
                public void onResponse(@NonNull Call<SaveRoot> call, @NonNull Response<SaveRoot> response) {
                    hideProgress();

                    if (response.isSuccessful()) {
                        if (response.code() == 200) {
                            if (response.body() != null) {
                                Log.d("ResponseCode", "" + response.code());
                                if (Objects.requireNonNull(response.body()).getStatus().equals("200")) {
                                    SaveRoot saveRoot = response.body();
                                    makeToast(String.valueOf(response.body().getResponse().getMessage()));
                                    binding.inputWsReamrk.setText("");
                                    binding.ivPartsImage1.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_image));
                                    binding.ivPartsImage2.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_image));
                                    binding.ivPartsImage3.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_image));
                                    partsImageStoragePath1 = "";
                                    partsImageStoragePath2 = "";
                                    partsImageStoragePath3 = "";
                                    GetErrorImages();
                                } else {
                                    String massage = response.body().getResponse().getMessage();
                                    showAlertDialogWithSingleButton(mActivity, massage);
                                    // makeToast(String.valueOf(response.body().getResponse().getMessage()));
                                }
                            } else {
                                showAlertDialogWithSingleButton(mActivity, response.message());
                            }
                        } else {
                            String msg =  "HTTP Error: "+ response.code();
                            showAlertDialogWithSingleButton(mActivity, msg);
                        }
                    } else {
                        String msg =  "HTTP Error: "+ response.code();
                        showAlertDialogWithSingleButton(mActivity, msg);
                        Log.d("Toperror", String.valueOf(response));
                    }
                }

                @Override
                public void onFailure(@NonNull Call<SaveRoot> call, @NonNull Throwable error) {
                    // Utils.hideCustomProgressDialogCommonForAll();
                    hideProgress();
                    showAlertDialogWithSingleButton(mActivity, error.getMessage());


                    call.cancel();
                }
            });
        } else {
            showAlertDialogWithSingleButton(mActivity, getResources().getString(R.string.no_internet_connection));


        }
    }

    private void verifyDelivery() {
        PrefManager prefManager = new PrefManager(mContext);
        showProgress(getResources().getString(R.string.please_wait));
        APIService service = RetrofitInstance.getRetrofitInstance().create(APIService.class);
        Call<ModelLogout> call = service.verifyDelivery(prefManager.getUSER_Id(),String.valueOf(model.deliveryId),model.fpscode,model.weighingScaleSerialNo);
        call.enqueue(new Callback<ModelLogout>() {
            @Override
            public void onResponse(@NonNull Call<ModelLogout> call, @NonNull Response<ModelLogout> response) {
                hideProgress();
                if (response.isSuccessful()) {
                    if (response.code() == 200) {
                        ModelLogout model = response.body();
                        if (Objects.requireNonNull(model).status.equals("200")) {
                            Intent previousScreen = new Intent(getApplicationContext(), InstallationPendingList.class);
                            setResult(RESULT_OK);
                            finish();
                        } else {
                            makeToast(model.message);
                        }
                    } else {
                        makeToast(getResources().getString(R.string.error));
                    }
                } else {
                    makeToast(getResources().getString(R.string.error));
                }
            }

            @Override
            public void onFailure(@NonNull Call<ModelLogout> call, @NonNull Throwable t) {
                hideProgress();
                makeToast(getResources().getString(R.string.error_message));
            }
        });
    }

    private void getIntentData() {
        model = (WeighInsData) getIntent().getSerializableExtra("param");
    }

    private void setUpData() {
        binding.inputFpsCode.setText(model.getFpscode());
        binding.inputBlockName.setText(model.getBlockName());
        binding.inputDealerName.setText(model.getDealerName());
        binding.inputMobileno.setText(model.getDealerMobileNo());

//        binding.inputWsSerialno.setText(model.getWeighingScaleSerialNo());
        binding.intputWsModel.setText(model.getWeighingScaleModelName());
        binding.inputtickretno.setText(model.getTicketNo());
        binding.inputtransdate.setText(model.getWinghingScaleDeliveredOnStr());
        binding.inputstatus.setText(model.getLast_TicketStatus());
        binding.inputFpsCode.setText(model.getFpscode());

        if (preference.getUSER_TYPE_ID().equals("1") || preference.getUSER_TYPE_ID().equals("2")) {
            binding.inputWsSerialno.setEnabled(true);
            binding.updateImageAria.setVisibility(View.VISIBLE);
        }else {
            binding.inputWsSerialno.setEnabled(false);
            binding.updateImageAria.setVisibility(View.GONE);

        }
//        if(Objects.equals(getIntent().getStringExtra("isFrom"), "View")){
//            binding.buttonVerify.setVisibility(View.GONE);
//        }else {

//        }

        if(!model.isDeliveryVerify && Objects.equals(getIntent().getStringExtra("isFrom"), "Verify")){
            binding.buttonVerify.setVisibility(View.VISIBLE);
        }else {
            binding.buttonVerify.setVisibility(View.GONE);
        }
        binding.linChooseImages1.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {

                selectImage(REQUEST_PICK_IMAGE_ONE);


            }
        });

        binding.linChooseImages2.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                selectImage(REQUEST_PICK_IMAGE_TWO);
            }
        });

        binding.linChooseImages3.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                selectImage(REQUEST_PICK_IMAGE_THREE);
            }
        });

        //   ArrayList<String> myvalue = new ArrayList<String>();
        //   myvalue.add(model.getErrorType().toString());
       /* ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(ErrorposDetail.this, simple_spinner_item, myvalue);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
        binding.spinner.setAdapter(spinnerArrayAdapter);*/
        //   binding.spinner.setText(model.getErrorType());
        Log.d("getDealerMobileNo", "jfdl" + model.getDealerMobileNo());


    }


    private void selectImage(final Integer requestCode) {
        try {
            final CharSequence[] items = {getResources().getString(R.string.imagepicker_str_take_photo), getResources().getString(R.string.imagepicker_str_choose_from_gallery), getResources().getString(R.string.imagepicker_str_cancel)};
            TextView title = new TextView(mActivity);
            title.setText(getResources().getString(R.string.imagepicker_str_select_challan_image));
            title.setBackgroundColor(getResources().getColor(R.color.colorActionBar));
            title.setPadding(15, 25, 15, 25);
            title.setGravity(Gravity.CENTER);
            title.setTextColor(Color.WHITE);
            title.setTextSize(22);
            AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
            builder.setCustomTitle(title);
            // builder.setTitle("Add Photo!");
            builder.setItems(items, (dialog, item) -> {
                if (items[item].equals(getResources().getString(R.string.imagepicker_str_take_photo))) {
                    ImagePicker.with(mActivity)
                            .setToolbarColor("#FFBC45")
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
                            .start(requestCode);

                } else if (items[item].equals(getResources().getString(R.string.imagepicker_str_choose_from_gallery))) {
                    ImagePicker.with(mActivity)
                            .setToolbarColor("#FFBC45")
                            .setStatusBarColor("#000000")
                            .setToolbarTextColor("#FFFFFF")
                            .setToolbarIconColor("#FFFFFF")
                            .setProgressBarColor("#4CAF50")
                            .setBackgroundColor("#212121")
                            .setCameraOnly(false)
                            .setMultipleMode(false)
                            .setFolderMode(true)
                            .setShowCamera(false)
                            .setFolderTitle("Albums")
                            .setImageTitle("Galleries")
                            .setDoneTitle("Done")
                            .setMaxSize(1)
                            .setSavePath(Constants.saveImagePath)
                            .setSelectedImages(new ArrayList<>())
                            .start(requestCode);
                } else if (items[item].equals(getResources().getString(R.string.imagepicker_str_cancel))) {
                    dialog.dismiss();
                }
            });
            builder.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void GetErrorImages() {
        Log.d("USER_ID>>>>>>>>", preference.getUSER_Id());
        if (Constants.isNetworkAvailable(mActivity)) {
            hideProgress();
            APIService apiInterface = RetrofitInstance.getRetrofitInstance().create(APIService.class);
            String USER_Id = preference.getUSER_Id();
            String fpscode = model.getFpscode();
            String disid = String.valueOf(model.getDistrictId());
            String modelTicketNo = model.getTicketNo();



            Log.d("fpscode", fpscode);
            Log.d("disid", disid);
            Log.d("modelTicketNo", modelTicketNo);
            // Log.d("district_Id",""+apiInterface
            Call<weighingDelieryRoot> call = apiInterface.apiWeigDeliveryDetail(USER_Id, fpscode, disid, modelTicketNo);
            call.enqueue(new Callback<weighingDelieryRoot>() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onResponse(@NonNull Call<weighingDelieryRoot> call, @NonNull Response<weighingDelieryRoot> response) {
                    hideProgress();

                    if (response.isSuccessful()) {
                        try {
                            if (response.code() == 200) {
                                if (response.body() != null) {

                                    if (Objects.requireNonNull(response.body()).status.equals("200")) {
                                        weighingDelieryRoot getErrorImagesRoot = response.body();
                                        weighingDeliveryData weighingDeliveryData = getErrorImagesRoot.getData();
                                        Log.d("getErrorTypesRoot..", "getErrorTypesRoot.." + getErrorImagesRoot);
                                        binding.inputWsSerialno.setText(weighingDeliveryData.weighingScaleSerialNo);
                                        ArrayList<weighingDeliveryImagesDetail> getErrorImagesDatumArrayList =
                                                weighingDeliveryData.imagesDetail;

                                        if (!getErrorImagesDatumArrayList.isEmpty()) {
                                            binding.tvUploadedimage.setVisibility(View.VISIBLE);
                                            binding.rvViewimages.setVisibility(View.VISIBLE);
                                            setUpAdapter(getErrorImagesDatumArrayList);
                                        } else {
                                            binding.tvUploadedimage.setVisibility(View.GONE);
                                            binding.tvUploadedimage.setVisibility(View.GONE);
                                        }

                                    } else {
                                        binding.rvViewimages.setVisibility(View.GONE);
                                        binding.textNoDataFound.setVisibility(View.VISIBLE);
                                    }


                                } else {
                                    binding.rvViewimages.setVisibility(View.GONE);
                                    binding.textNoDataFound.setVisibility(View.VISIBLE);
                                }
                            } else {
                                binding.rvViewimages.setVisibility(View.GONE);
                                binding.textNoDataFound.setVisibility(View.VISIBLE);
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                            binding.rvViewimages.setVisibility(View.GONE);
                            binding.textNoDataFound.setVisibility(View.VISIBLE);
                        }
                    } else {
                        //  makeToast(getResources().getString(R.string.error));
                        binding.rvViewimages.setVisibility(View.GONE);
                        binding.textNoDataFound.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onFailure(@NonNull Call<weighingDelieryRoot> call, @NonNull Throwable t) {
                    hideProgress();

                    // makeToast(getResources().getString(R.string.error));
                    binding.rvViewimages.setVisibility(View.GONE);
                    //      binding.textNoDataFound.setVisibility(View.VISIBLE);
                }
            });
        } else {
            makeToast(getResources().getString(R.string.no_internet_connection));
        }
    }


    private void setUpAdapter(ArrayList<weighingDeliveryImagesDetail> getErrorImagesDatumArrayList) {
        // adapter = new ViewImagesListingAdapter(mActivity, getErrorImagesDatumArrayList, onItemViewClickListener);
        ViewImagesListingAdapterIris adapter = new ViewImagesListingAdapterIris(mActivity, getErrorImagesDatumArrayList);
        binding.rvViewimages.setHasFixedSize(true);
        GridLayoutManager layoutManager = new GridLayoutManager(mActivity, 3);
        binding.rvViewimages.setLayoutManager(layoutManager);
        binding.rvViewimages.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResult(RESULT_OK);
        finish();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.iv_back) {
            onBackPressed();
        }
    }

    public void makeToast(String string) {
        if (TextUtils.isEmpty(string)) return;
        Toast.makeText(mActivity, string, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_PICK_IMAGE_ONE && resultCode == RESULT_OK && data != null) {
            ArrayList<Image> images = data.getParcelableArrayListExtra(Config.EXTRA_IMAGES);
            if (images != null && !images.isEmpty()) {
                Image image = images.get(0);
                // String imageStoragePath = image.getPath();
                partsImageStoragePath1 = image.getPath();
                if (partsImageStoragePath1.contains("file:/")) {
                    partsImageStoragePath1 = partsImageStoragePath1.replace("file:/", "");
                }
                partsImageStoragePath1 = CompressImage.compress(partsImageStoragePath1, this);
                File imgFile = new File(partsImageStoragePath1);
                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                try {
                    binding.ivPartsImage1.setImageBitmap(ImageUtilsForRotate.ensurePortrait(partsImageStoragePath1));
                    stringArrayListHavingAllFilePath.add(partsImageStoragePath1);
                } catch (IOException e) {
                    binding.ivPartsImage1.setImageBitmap(myBitmap);
                    stringArrayListHavingAllFilePath.add(partsImageStoragePath1);

                    e.printStackTrace();
                }catch (NullPointerException e){
                    e.printStackTrace();
                }
            }
        } else if (requestCode == REQUEST_PICK_IMAGE_TWO && resultCode == RESULT_OK && data != null) {
            ArrayList<Image> images = data.getParcelableArrayListExtra(Config.EXTRA_IMAGES);
            if (images != null && !images.isEmpty()) {
                Image image = images.get(0);
                String imageStoragePath = image.getPath();
                partsImageStoragePath2 = image.getPath();
                if (partsImageStoragePath2.contains("file:/")) {
                    partsImageStoragePath2 = partsImageStoragePath2.replace("file:/", "");
                }
                partsImageStoragePath2 = CompressImage.compress(partsImageStoragePath2, this);
                File imgFile = new File(partsImageStoragePath2);
                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());

                try {
                    binding.ivPartsImage2.setImageBitmap(ImageUtilsForRotate.ensurePortrait(partsImageStoragePath2));
                    stringArrayListHavingAllFilePath.add(partsImageStoragePath2);
                } catch (IOException e) {
                    binding.ivPartsImage2.setImageBitmap(myBitmap);
                    stringArrayListHavingAllFilePath.add(partsImageStoragePath2);
                    e.printStackTrace();
                }catch (NullPointerException e){
                    e.printStackTrace();
                }
            }
        } else if (requestCode == REQUEST_PICK_IMAGE_THREE && resultCode == RESULT_OK && data != null) {
            ArrayList<Image> images = data.getParcelableArrayListExtra(Config.EXTRA_IMAGES);
            if (images != null && !images.isEmpty()) {
                Image image = images.get(0);
                //  String imageStoragePath = image.getPath();
                partsImageStoragePath3 = image.getPath();
                if (partsImageStoragePath3.contains("file:/")) {
                    partsImageStoragePath3 = partsImageStoragePath3.replace("file:/", "");
                }
                partsImageStoragePath3 = CompressImage.compress(partsImageStoragePath3, this);
                File imgFile = new File(partsImageStoragePath3);
                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());

                try {
                    binding.ivPartsImage3.setImageBitmap(ImageUtilsForRotate.ensurePortrait(partsImageStoragePath3));
                    stringArrayListHavingAllFilePath.add(partsImageStoragePath3);
                } catch (IOException e) {
                    binding.ivPartsImage3.setImageBitmap(myBitmap);
                    stringArrayListHavingAllFilePath.add(partsImageStoragePath3);
                    e.printStackTrace();
                }catch (NullPointerException e){
                    e.printStackTrace();
                }
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

}

