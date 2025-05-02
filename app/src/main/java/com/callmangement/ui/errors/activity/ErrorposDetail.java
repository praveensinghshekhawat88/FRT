package com.callmangement.ui.errors.activity;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.callmangement.Network.APIService;
import com.callmangement.Network.RetrofitInstance;
import com.callmangement.R;
import com.callmangement.custom.CustomActivity;
import com.callmangement.databinding.ActivityErrorrPosDetailsBinding;
import com.callmangement.imagepicker.model.Config;
import com.callmangement.imagepicker.model.Image;
import com.callmangement.imagepicker.ui.imagepicker.ImagePicker;
import com.callmangement.support.ImageUtilsForRotate;
import com.callmangement.ui.errors.adapter.RemarkAdapter;
import com.callmangement.ui.errors.adapter.ViewImagesListingAdapter;
import com.callmangement.ui.errors.model.GetErrorImagesDatum;
import com.callmangement.ui.errors.model.GetErrorImagesRoot;
import com.callmangement.ui.errors.model.GetPosDeviceErrorDatum;
import com.callmangement.ui.errors.model.GetRemarkDatum;
import com.callmangement.ui.errors.model.GetRemarkRoot;
import com.callmangement.ui.home.ZoomInZoomOutActivity;
import com.callmangement.utils.CompressImage;
import com.callmangement.utils.Constants;
import com.callmangement.utils.EqualSpacingItemDecoration;
import com.callmangement.utils.PrefManager;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ErrorposDetail extends CustomActivity implements View.OnClickListener {
    private ActivityErrorrPosDetailsBinding binding;
    //private ModelExpensesList model;
    private GetPosDeviceErrorDatum model;

    private PrefManager prefManager;
    private final ArrayList<String> playerNames = new ArrayList<String>();
    String selectedValue, FPS_CODE, Input_Device_Code, Input_Remark;
    ArrayList<String> stringArrayListHavingAllFilePath = new ArrayList<>();
    Activity mActivity;

    public final int REQUEST_PICK_IMAGE_ONE = 1111;
    public final int REQUEST_PICK_IMAGE_TWO = 1112;
    public final int REQUEST_PICK_IMAGE_THREE = 1113;
    private ViewImagesListingAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityErrorrPosDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initView();
    }

    private void initView() {
        mActivity = this;

        prefManager = new PrefManager(mContext);
        binding.actionBar.ivBack.setVisibility(View.VISIBLE);
        binding.actionBar.ivThreeDot.setVisibility(View.GONE);
        binding.actionBar.layoutLanguage.setVisibility(View.GONE);
        binding.actionBar.textToolbarTitle.setText(getResources().getString(R.string.error_detail));
        getIntentData();

        setUpOnClickListener();
        setUpData();
        GetErrorImages();
        GetRemark();
    }


    private void setUpOnClickListener() {
        binding.actionBar.ivBack.setOnClickListener(this);
        //  binding.ivChallanImage.setOnClickListener(this);
    }

    private void getIntentData() {
        model = (GetPosDeviceErrorDatum) getIntent().getSerializableExtra("param");
    }

    private void setUpData() {
        binding.inputDealerName.setText(model.getDealerName());
        binding.inputMobileno.setText(model.getDealerMobileNo());
        binding.inputDistrict.setText(model.getDistrictNameEng());

        ArrayList<String> myvalue = new ArrayList<String>();


        myvalue.add(model.getErrorType());


       /* ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(ErrorposDetail.this, simple_spinner_item, myvalue);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
        binding.spinner.setAdapter(spinnerArrayAdapter);*/


        binding.inputFpsCode.setText(model.getFpscode());
        binding.spinner.setText(model.getErrorType());
        //    Log.d("getDealerMobileNo","jfdl"+model.getDealerMobileNo());

        binding.inputRemark.setText(model.getRemark());
        //  binding.inputRemark.setText(model.getRemark());
        binding.inputDeviceCode.setText(String.valueOf(model.getDeviceCode()));
        //  binding.inputRemark.setText(model.getRemark());

      /*  if (model.getCompletedOnStr().isEmpty()){
            binding.expenseCompletedDateLay.setVisibility(View.GONE);
        } else {
            binding.expenseCompletedDateLay.setVisibility(View.VISIBLE);
            binding.inputExpenseCompletedDate.setText(model.getCompletedOnStr());
        }

        if (model.getExpenseStatusID() == 2) {
            binding.buttonComplete.setVisibility(View.GONE);
        }*/
/*
        Glide.with(mContext)
                .load(Constants.API_BASE_URL+""+model.getFilePath())
                .placeholder(R.drawable.image_not_fount)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(binding.ivChallanImage);*/

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
                            .start(requestCode);
                } else if (items[item].equals(getResources().getString(R.string.imagepicker_str_choose_from_gallery))) {
                    ImagePicker.with(mActivity)
                            .setToolbarColor("#212121")
                            .setStatusBarColor("#000000")
                            .setToolbarTextColor("#FFFFFF")
                            .setToolbarIconColor("#FFFFFF")
                            .setProgressBarColor("#4CAF50")
                            .setBackgroundColor("#212121")
                            .setCameraOnly(false)
                            .setMultipleMode(true)
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_PICK_IMAGE_ONE && resultCode == RESULT_OK && data != null) {
            ArrayList<Image> images = data.getParcelableArrayListExtra(Config.EXTRA_IMAGES);
            if (images != null && images.size() > 0) {
                Image image = images.get(0);
                String imageStoragePath = image.getPath();
                if (imageStoragePath.contains("file:/")) {
                    imageStoragePath = imageStoragePath.replace("file:/", "");
                }
                imageStoragePath = CompressImage.compress(imageStoragePath, this);
                File imgFile = new File(imageStoragePath);
                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                //  binding.ivChallanImage.setImageBitmap(myBitmap);
                try {
                    ImageUtilsForRotate.ensurePortrait(imageStoragePath);
                } catch (IOException | NullPointerException e) {
                    e.printStackTrace();
                }
                stringArrayListHavingAllFilePath.add(imageStoragePath);
            }
        } else if (requestCode == REQUEST_PICK_IMAGE_TWO && resultCode == RESULT_OK && data != null) {
            ArrayList<Image> images = data.getParcelableArrayListExtra(Config.EXTRA_IMAGES);
            if (images != null && images.size() > 0) {
                Image image = images.get(0);
                String imageStoragePath = image.getPath();
                if (imageStoragePath.contains("file:/")) {
                    imageStoragePath = imageStoragePath.replace("file:/", "");
                }
                imageStoragePath = CompressImage.compress(imageStoragePath, this);
                File imgFile = new File(imageStoragePath);
                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                try {
                    ImageUtilsForRotate.ensurePortrait(imageStoragePath);
                } catch (IOException | NullPointerException e) {
                    e.printStackTrace();
                }
                stringArrayListHavingAllFilePath.add(imageStoragePath);
            }
        } else if (requestCode == REQUEST_PICK_IMAGE_THREE && resultCode == RESULT_OK && data != null) {
            ArrayList<Image> images = data.getParcelableArrayListExtra(Config.EXTRA_IMAGES);
            if (images != null && images.size() > 0) {
                Image image = images.get(0);
                String imageStoragePath = image.getPath();
                if (imageStoragePath.contains("file:/")) {
                    imageStoragePath = imageStoragePath.replace("file:/", "");
                }
                imageStoragePath = CompressImage.compress(imageStoragePath, this);
                File imgFile = new File(imageStoragePath);
                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                //   binding.ivPartsImage2.setImageBitmap(myBitmap);
                try {
                    ImageUtilsForRotate.ensurePortrait(imageStoragePath);
                } catch (IOException | NullPointerException e) {
                    e.printStackTrace();
                }
                stringArrayListHavingAllFilePath.add(imageStoragePath);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    //private void GetErrorImages(String expenseStatusId, String districtId, String fromDate, String toDate){
    private void GetErrorImages() {
        if (Constants.isNetworkAvailable(mContext)) {
            showProgress();
            APIService service = RetrofitInstance.getRetrofitInstance().create(APIService.class);
            String errorid = String.valueOf(model.getErrorId());
            String errorRegNo = String.valueOf(model.errorRegNo);

            Call<GetErrorImagesRoot> call = service.GetErrorImages(errorid, prefManager.getUSER_Id(), errorRegNo);
            call.enqueue(new Callback<GetErrorImagesRoot>() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onResponse(@NonNull Call<GetErrorImagesRoot> call, @NonNull Response<GetErrorImagesRoot> response) {
                    hideProgress();
                    if (response.isSuccessful()) {
                        try {
                            if (response.code() == 200) {
                                if (response.body() != null) {

                                    if (Objects.requireNonNull(response.body()).getStatus().equals("200")) {
                                        GetErrorImagesRoot getErrorImagesRoot = response.body();
                                        //   Log.d("getErrorTypesRoot..","getErrorTypesRoot.."+getErrorImagesRoot);

                                        ArrayList<GetErrorImagesDatum> getErrorImagesDatumArrayList =
                                                getErrorImagesRoot.getData();


                                        if (getErrorImagesDatumArrayList.size() > 0) {
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
                        makeToast(getResources().getString(R.string.error));
                        binding.rvViewimages.setVisibility(View.GONE);
                        binding.textNoDataFound.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onFailure(@NonNull Call<GetErrorImagesRoot> call, @NonNull Throwable t) {
                    hideProgress();
                    makeToast(getResources().getString(R.string.error_message));
                    binding.rvViewimages.setVisibility(View.GONE);
                    //      binding.textNoDataFound.setVisibility(View.VISIBLE);
                }
            });
        } else {
            makeToast(getResources().getString(R.string.no_internet_connection));
        }
    }


    private void setUpAdapter(ArrayList<GetErrorImagesDatum> getErrorImagesDatumArrayList) {
        adapter = new ViewImagesListingAdapter(mActivity, getErrorImagesDatumArrayList, onItemViewClickListener);
        binding.rvViewimages.setHasFixedSize(true);
        GridLayoutManager layoutManager = new GridLayoutManager(mActivity, 3);
        binding.rvViewimages.setLayoutManager(layoutManager);
        binding.rvViewimages.addItemDecoration(new EqualSpacingItemDecoration(30, EqualSpacingItemDecoration.VERTICAL));
        binding.rvViewimages.setAdapter(adapter);


    }

    ViewImagesListingAdapter.OnItemViewClickListener onItemViewClickListener = new ViewImagesListingAdapter.OnItemViewClickListener() {
        @Override
        public void onItemClick(GetErrorImagesDatum campDocInfo, int position) {
            startActivity(new Intent(mActivity, ZoomInZoomOutActivity.class).putExtra("image", Constants.API_BASE_URL + campDocInfo.getImagePath()));
            //  Log.d("API_BASE_URL","API_BASE_URL "+Constants.API_BASE_URL +campDocInfo.getImagePath());
        }
    };


    private void GetRemark() {
        if (Constants.isNetworkAvailable(mContext)) {
            showProgress();
            APIService service = RetrofitInstance.getRetrofitInstance().create(APIService.class);

            String errorid = String.valueOf(model.getErrorId());
            String errorRegNo = String.valueOf(model.errorRegNo);
            Call<GetRemarkRoot> call = service.GetErrorRemarks(errorid, prefManager.getUSER_Id(), errorRegNo);
            call.enqueue(new Callback<GetRemarkRoot>() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onResponse(@NonNull Call<GetRemarkRoot> call, @NonNull Response<GetRemarkRoot> response) {
                    hideProgress();
                    if (response.isSuccessful()) {
                        try {
                            if (response.code() == 200) {
                                if (response.body() != null) {

                                    if (Objects.requireNonNull(response.body()).getStatus().equals("200")) {
                                        GetRemarkRoot getErrorImagesRoot = response.body();
                                        //  Log.d("getErrorTypesRoot..","getErrorTypesRoot.."+getErrorImagesRoot);

                                        ArrayList<GetRemarkDatum> getRemarkDatumArrayList =
                                                getErrorImagesRoot.getData();


                                        if (getRemarkDatumArrayList.size() > 0) {
                                            binding.tvUploadedimage.setVisibility(View.VISIBLE);
                                            binding.rvViewimages.setVisibility(View.VISIBLE);
                                            setUpremarkAdapter(getRemarkDatumArrayList);


                                        } else {
                                            binding.tvUploadedimage.setVisibility(View.GONE);
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
                        makeToast(getResources().getString(R.string.error));
                        binding.rvViewimages.setVisibility(View.GONE);
                        binding.textNoDataFound.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onFailure(@NonNull Call<GetRemarkRoot> call, @NonNull Throwable t) {
                    hideProgress();
                    makeToast(getResources().getString(R.string.error_message));
                    binding.rvViewimages.setVisibility(View.GONE);
                    //      binding.textNoDataFound.setVisibility(View.VISIBLE);
                }
            });
        } else {
            makeToast(getResources().getString(R.string.no_internet_connection));
        }
    }


    private void setUpremarkAdapter(ArrayList<GetRemarkDatum> getRemarkDatumArrayList) {
        binding.rvRemark.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        binding.rvRemark.setAdapter(new RemarkAdapter(mContext, getRemarkDatumArrayList));


    }


    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.iv_back) {
            onBackPressed();
        } else if (id == R.id.ivChallanImage) {
            //    startActivity(new Intent(mContext, ZoomInZoomOutActivity.class).putExtra("image", Constants.API_BASE_URL+""+model.getFilePath()));

        }
    }
}

