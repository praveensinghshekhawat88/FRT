package com.callmangement.ui.closed_guard_delivery;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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

import com.bumptech.glide.Glide;
import com.callmangement.network.APIService;
import com.callmangement.network.RetrofitInstance;
import com.callmangement.R;
import com.callmangement.custom.CustomActivity;
import com.callmangement.databinding.ActivityClosedGuardDetailBinding;
import com.callmangement.imagepicker.ui.imagepicker.ImagePicker;
import com.callmangement.ui.closed_guard_delivery.model.ClosedGuardDeliveryListResponse;
import com.callmangement.ui.home.ZoomInZoomOutActivity;
import com.callmangement.ui.iris_derivery_installation.Model.InstalledDetailedResponse;
import com.callmangement.ui.iris_derivery_installation.Model.InstalledDtlDataIris;
import com.callmangement.ui.iris_derivery_installation.Model.IrisInstalledImagesDtl;
import com.callmangement.ui.iris_derivery_installation.adapter.ViewImageInstalledDetailsAdapterIris;
import com.callmangement.utils.Constants;
import com.callmangement.utils.EqualSpacingItemDecoration;
import com.callmangement.utils.PrefManager;

import java.util.ArrayList;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ClosedGuardDetailActivity extends CustomActivity implements View.OnClickListener {
    private ActivityClosedGuardDetailBinding binding;
    //private ModelExpensesList model;
    private ClosedGuardDeliveryListResponse.Datum model;
    PrefManager preference;
    private final ArrayList<String> playerNames = new ArrayList<String>();
    String selectedValue, FPS_CODE, Input_Device_Code, Input_Remark;
    ArrayList<String> stringArrayListHavingAllFilePath = new ArrayList<>();
    public final int REQUEST_PICK_IMAGE_ONE = 1111;
    public final int REQUEST_PICK_IMAGE_TWO = 1112;
    public final int REQUEST_PICK_IMAGE_THREE = 1113;
    private ViewImageInstalledDetailsAdapterIris adapter;
    private ViewImageInstalledDetailsAdapterIris adapterSec;
    Activity mActivity;
    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //will hide the title
        //    getSupportActionBar().hide(); // hide the title bar
        binding = ActivityClosedGuardDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initView();
    }

    private void initView() {
        mActivity = this;
        mContext = this;
        preference = new PrefManager(mContext);
        binding.actionBar.ivBack.setVisibility(View.VISIBLE);
        binding.actionBar.textToolbarTitle.setText(getResources().getString(R.string.closed_guard_detail));
        getIntentData();
        setUpOnClickListener();
        setUpData();
    //    GetErrorImages();
    }

    private void setUpOnClickListener() {
        binding.actionBar.ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        //  binding.ivChallanImage.setOnClickListener(this);
    }

    private void getIntentData() {
        model = (ClosedGuardDeliveryListResponse.Datum) getIntent().getSerializableExtra("param");
    }

    private void setUpData() {

//        binding.inputFpsCode.setText(model.getFpscode());
//        binding.inputBlockName.setText(model.getBlockName());
//        binding.inputDealerName.setText(model.getDealerName());
//        binding.inputMobileno.setText(model.getDealerMobileNo());
//        binding.inputWsSerialno.setText(model.getWeighingScaleSerialNo());
//        binding.intputWsModel.setText(model.getWeighingScaleModelName());
//        binding.inputtickretno.setText(model.getTicketNo());
//        binding.inputdelivereddate.setText(model.getWinghingScaleDeliveredOnStr());
//        binding.inputinstalleddate.setText(model.getInstallationOnStr());
//        binding.inputstatus.setText(model.getLast_TicketStatus());
//        binding.inputSerialno.setText(model.getIrisScannerSerialNo());
//        binding.intputIrisModel.setText(model.getIrisDeviceModel());
//        binding.inputaddress.setText(model.getShopAddress());

        binding.inputFpsCode.setText(model.getFpscode());
        binding.inputBlockName.setText(model.getBlockName());
        binding.inputDealerName.setText(model.getDealerName());
        binding.inputMobileno.setText(model.getDealerMobileNo());
        //    binding.inputWsSerialno.setText(model.getWeighingScaleSerialNo());
        //     binding.intputWsModel.setText(model.getWeighingScaleModelName());
   //     binding.inputtickretno.setText(model.getTicketNo());
        binding.inputdelivereddate.setText(model.getCloseGuardDeliverdOnStr());
        //    binding.inputinstalleddate.setText(model.getInstallationOnStr());
   //     binding.inputstatus.setText(model.getDeliverdStatus());
        binding.inputSerialno.setText(model.getSerialNo());
        binding.intputIrisModel.setText(model.getDeviceModelName());
        binding.inputaddress.setText(model.getCloseGuardDeliveryAddress());

        Glide.with(mContext)
                .load(model.getCgphotoPath())
                .placeholder(R.drawable.image_not_fount)
                .into(binding.imgDealer);

        Glide.with(mContext)
                .load(model.getCgsignaturePath())
                .placeholder(R.drawable.image_not_fount)
                .into(binding.imgSign);

        binding.imgDealer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mContext, ZoomInZoomOutActivity.class).putExtra("image", model.getCgphotoPath()));
            }
        });

        binding.imgSign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mContext, ZoomInZoomOutActivity.class).putExtra("image", model.getCgsignaturePath()));
            }
        });


        //   ArrayList<String> myvalue = new ArrayList<String>();
        //   myvalue.add(model.getErrorType().toString());

        //   binding.spinner.setText(model.getErrorType());
        Log.d("getDealerMobileNo", "jfdl" + model.getDealerMobileNo());
        //    binding.inputRemark.setText(model.getRemark());
        //  binding.inputRemark.setText(model.getRemark());
        //    binding.inputDeviceCode.setText(String.valueOf(model.getDeviceCode()));
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

    private void GetErrorImages() {
        if (Constants.isNetworkAvailable(mActivity)) {
            hideProgress();
            APIService apiInterface = RetrofitInstance.getRetrofitInstance().create(APIService.class);
            String USER_Id = preference.getUSER_Id();
            String fpscode = model.getFpscode();
            String disid = String.valueOf(model.getDistrictId());
        //   String modelTicketNo = model.getTicketNo();
            String modelTicketNo = "model.getTicketNo()";
            String deliveryid = String.valueOf(model.getDeliveryId());
            Log.d("USER_ID","--" +preference.getUSER_Id());
            Log.d("fpscode","--" +fpscode);
            Log.d("disid","--" +disid);
    //        Log.d("modelTicketNo","--"+ modelTicketNo);
            Log.d("deliveryid", "--"+deliveryid);
            // Log.d("district_Id",""+apiInterface
            Call<InstalledDetailedResponse> call = apiInterface.IrisDeliveryInstallationDtl(USER_Id, fpscode, disid, modelTicketNo, deliveryid);
            call.enqueue(new Callback<InstalledDetailedResponse>() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onResponse(@NonNull Call<InstalledDetailedResponse> call, @NonNull Response<InstalledDetailedResponse> response) {
                    hideProgress();
                    if (response.isSuccessful()) {
                        try {
                            if (response.code() == 200) {
                                if (response.body() != null) {
                                    if (Objects.requireNonNull(response.body()).getStatus().equals("200")) {
                                        InstalledDetailedResponse getErrorImagesRoot = response.body();
                                        InstalledDtlDataIris weighingDeliveryData = getErrorImagesRoot.getData();
                                        Log.d("getErrorTypesRoot..", "getErrorTypesRoot.." + getErrorImagesRoot);
                                        ArrayList<IrisInstalledImagesDtl> getErrorImagesDatumArrayList =
                                                weighingDeliveryData.getImagesDetail();
                                        if (getErrorImagesDatumArrayList.size() > 0) {
                                            //            binding.tvUploadedimage.setVisibility(View.VISIBLE);
                                            //            binding.rvViewimages.setVisibility(View.VISIBLE);
                                            setUpAdapter(getErrorImagesDatumArrayList);
                                        } else {
                                            //            binding.tvUploadedimage.setVisibility(View.VISIBLE);
                                            binding.textNoDataFound.setVisibility(View.GONE);
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
                                String msg = "HTTP Error: " + response.code();
                                showAlertDialogWithSingleButton(mActivity, msg);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            binding.rvViewimages.setVisibility(View.GONE);
                            binding.textNoDataFound.setVisibility(View.VISIBLE);
                        }
                    } else {
                        //  makeToast(getResources().getString(R.string.error));
                        String msg = "HTTP Error: " + response.code();
                        showAlertDialogWithSingleButton(mActivity, msg);
                        binding.rvViewimages.setVisibility(View.GONE);
                        binding.textNoDataFound.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onFailure(@NonNull Call<InstalledDetailedResponse> call, @NonNull Throwable t) {
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

    private void setUpAdapter(ArrayList<IrisInstalledImagesDtl> getErrorImagesDatumArrayList) {
//        ArrayList<IrisInstalledImagesDtl> getErrorImagesDatumArrayListOne =
//                getErrorImagesDatumArrayList;
//        ArrayList<IrisInstalledImagesDtl> getErrorImagesDatumArrayListTwo =
//                getErrorImagesDatumArrayList;
        ArrayList<IrisInstalledImagesDtl> getErrorImagesDatumArrayListOne = new ArrayList<>();
        ArrayList<IrisInstalledImagesDtl> getErrorImagesDatumArrayListTwo = new ArrayList<>();

        Integer aa = 0;
        Integer aass = 0;
//        getErrorImagesDatumArrayListOne.clear();
//        getErrorImagesDatumArrayListTwo.clear();

        for (int i = 0; i < getErrorImagesDatumArrayList.size(); i++) {
            if (getErrorImagesDatumArrayList.get(i).getPhotoTypeId() <= 5) {
                getErrorImagesDatumArrayListOne.add(getErrorImagesDatumArrayList.get(i));
                aa = aa + 1;
            } else {
                getErrorImagesDatumArrayListTwo.add(getErrorImagesDatumArrayList.get(i));
                aass = aass + 1;
            }
        }
        // adapter = new ViewImagesListingAdapter(mActivity, getErrorImagesDatumArrayList, onItemViewClickListener);
        adapter = new ViewImageInstalledDetailsAdapterIris(mActivity, getErrorImagesDatumArrayListOne, true, aa);
        adapterSec = new ViewImageInstalledDetailsAdapterIris(mActivity, getErrorImagesDatumArrayListTwo, false, aass);
        binding.rvViewimages.setHasFixedSize(true);
        GridLayoutManager layoutManager = new GridLayoutManager(mActivity, 3) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        binding.rvViewimages.setLayoutManager(layoutManager);
        binding.rvViewimages.addItemDecoration(new EqualSpacingItemDecoration(30, EqualSpacingItemDecoration.VERTICAL));
        binding.rvViewimages.setAdapter(adapter);

        binding.rvViewimagesSec.setHasFixedSize(true);
        GridLayoutManager layoutManagerSec = new GridLayoutManager(mActivity, 3) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        binding.rvViewimagesSec.setLayoutManager(layoutManagerSec);
        binding.rvViewimagesSec.addItemDecoration(new EqualSpacingItemDecoration(30, EqualSpacingItemDecoration.VERTICAL));
        binding.rvViewimagesSec.setAdapter(adapterSec);
    }

    /*  ViewImagesListingAdapter.OnItemViewClickListener onItemViewClickListener = new ViewImagesListingAdapter.OnItemViewClickListener() {
            @Override
            public void onItemClick(weighingDeliveryImagesDetail campDocInfo, int position) {
                startActivity(new Intent(mActivity, ZoomInZoomOutActivity.class).putExtra("image", Utils.Baseurl +campDocInfo.getImagePath()));
               // Log.d("API_BASE_URL","API_BASE_URL "+Constants.API_BASE_URL +campDocInfo.getImagePath());
            }
        };
    */

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.iv_back) {
            onBackPressed();
        }// else if (id == R.id.ivChallanImage){
        //    startActivity(new Intent(mContext, ZoomInZoomOutActivity.class).putExtra("image", Constants.API_BASE_URL+""+model.getFilePath()));
        //    }
    }

    public void makeToast(String string) {
        if (TextUtils.isEmpty(string)) return;
        Toast.makeText(mActivity, string, Toast.LENGTH_SHORT).show();
    }
}


