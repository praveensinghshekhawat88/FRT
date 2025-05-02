package com.callmangement.EHR.ehrActivities;

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
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.callmangement.EHR.models.FeedbackbyDCListDatum;
import com.callmangement.Network.APIService;
import com.callmangement.Network.RetrofitInstance;
import com.callmangement.R;
import com.callmangement.custom.CustomActivity;
import com.callmangement.databinding.ActivityFeedbackListdetailedBinding;
import com.callmangement.databinding.ActivityInstalledListdetailedBinding;
import com.callmangement.imagepicker.ui.imagepicker.ImagePicker;
import com.callmangement.ui.ins_weighing_scale.adapter.ViewImageInstalledAdapterIris;
import com.callmangement.ui.ins_weighing_scale.model.Installed.InstalledDatum;
import com.callmangement.ui.ins_weighing_scale.model.InstalledDetailed.InstalledDetailedData;
import com.callmangement.ui.ins_weighing_scale.model.InstalledDetailed.InstalledDetailedRoot;
import com.callmangement.ui.ins_weighing_scale.model.InstalledDetailed.InstalledImagesDetail;
import com.callmangement.utils.Constants;
import com.callmangement.utils.EqualSpacingItemDecoration;
import com.callmangement.utils.PrefManager;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;


import java.util.ArrayList;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FeedBackListDetail extends FragmentActivity implements View.OnClickListener, OnMapReadyCallback {
    private ActivityFeedbackListdetailedBinding binding;
    //private ModelExpensesList model;
    private FeedbackbyDCListDatum model;
    PrefManager preference;
    private final ArrayList<String> playerNames = new ArrayList<String>();
    String selectedValue, FPS_CODE, Input_Device_Code, Input_Remark;
    ArrayList<String> stringArrayListHavingAllFilePath = new ArrayList<>();
    public final int REQUEST_PICK_IMAGE_ONE = 1111;
    public final int REQUEST_PICK_IMAGE_TWO = 1112;
    public final int REQUEST_PICK_IMAGE_THREE = 1113;
    private ViewImageInstalledAdapterIris adapter;
    private ViewImageInstalledAdapterIris adapterSec;
    Activity mActivity;
    Context mContext;

    private GoogleMap mMap;
    private double latitude ;  // Replace with your latitude
    private double longitude ; // Replace with your longitude


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //will hide the title
        //    getSupportActionBar().hide(); // hide the title bar
        binding = ActivityFeedbackListdetailedBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        initView();
    }

    private void initView() {
        mActivity = this;
        mContext = this;
        preference = new PrefManager(mContext);
        binding.actionBar.ivBack.setVisibility(View.VISIBLE);
        binding.actionBar.textToolbarTitle.setText(getResources().getString(R.string.feedbacklist_detail));


        getIntentData();
        setUpOnClickListener();
        setUpData();
        //  GetErrorImages();
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
        model = (FeedbackbyDCListDatum) getIntent().getSerializableExtra("paramm");
    }

    private void setUpData() {
        binding.inputFpsCode.setText(model.getFpscode());
        binding.inputdis.setText(model.getDistrictName());
        binding.inputDealerName.setText(model.getFeedBackBy());
        binding.inputday.setText(model.getUpdatedOn().getDayOfWeek());

        String remark = String.valueOf(model.getRemarks());
        if (remark.equals(null) || remark.isEmpty()) {
            binding.inputRemark.setText(" ");

        } else {
            binding.inputRemark.setText(remark);

        }

        binding.inputfeedbackid.setText(String.valueOf(model.getFeedBackId()));
        String date = model.getUpdatedOn().getDayOfMonth() + "-" + model.getUpdatedOn().getMonthValue() + "-" + model.getUpdatedOn().getYear();
        binding.inputdelivereddate.setText(date);

        //  String time = model.getUpdatedOn().getHour()+":"+model.getUpdatedOn().getMinute()+":"+model.getUpdatedOn().getSecond();
        binding.inputtime.setText(model.getLogInTime());

        binding.inputouttime.setText(model.getLogOutTime());


        binding.inputaddress.setText(model.getLocationAddress());

        //   ArrayList<String> myvalue = new ArrayList<String>();
        //   myvalue.add(model.getErrorType().toString());
       /* ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(ErrorposDetail.this, simple_spinner_item, myvalue);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
        binding.spinner.setAdapter(spinnerArrayAdapter);*/
        //   binding.spinner.setText(model.getErrorType());
        //  Log.d("getDealerMobileNo", "jfdl" + model.getDealerMobileNo());
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

        String shopimg = model.getSelfyWithFPSShopImage();


        if (shopimg == null) {
            binding.textNoDataFound.setText("No Image");
        }
        Glide.with(mActivity)
                .load(shopimg)
                .placeholder(R.drawable.image_not_fount)
                .into(binding.ivPartsImage1);
        binding.textNoDataFound.setText("Dealer's Selfi");

        binding.ivPartsImage1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(mActivity, ZoomInZoomOutActivity.class).putExtra("image", shopimg));

            }
        });


        String sigimg = model.getDealerSignatureImage();
        latitude = Double.parseDouble(model.getLatitude());
        longitude  = Double.parseDouble(model.getLongitude());




        if (sigimg == null) {
            binding.textNoDataFoundSec.setText("No Image");
        }
        Glide.with(mActivity)
                .load(sigimg)
                .placeholder(R.drawable.image_not_fount)
                .into(binding.ivPartsImage2);
        binding.textNoDataFoundSec.setText("Dealer Signature");


        binding.ivPartsImage2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(mActivity, ZoomInZoomOutActivity.class).putExtra("image", sigimg));
            }
        });


        String dcvstimg = model.getDcVisitingChallan();


        if (dcvstimg == null) {
            binding.textNoDataFound3.setText("No Image");
        }
        Glide.with(mActivity)
                .load(dcvstimg)
                .placeholder(R.drawable.image_not_fount)
                .into(binding.ivPartsImage3);
        binding.textNoDataFound3.setText("Challan Image");

        binding.ivPartsImage3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(mActivity, ZoomInZoomOutActivity.class).putExtra("image", dcvstimg));
            }
        });


    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.iv_back) {
            onBackPressed();
        }//else if (id == R.id.your_button){
        // startActivity(new Intent(mContext, ZoomInZoomOutActivity.class).putExtra("image", Constants.API_BASE_URL+""+model.getFilePath()));
        // }
    }

    public void makeToast(String string) {
        if (TextUtils.isEmpty(string)) return;
        Toast.makeText(mActivity, string, Toast.LENGTH_SHORT).show();
    }



    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {

        mMap = googleMap;
        LatLng location = new LatLng(latitude, longitude);
        mMap.addMarker(new MarkerOptions().position(location).title(model.getLocationAddress()));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15));




    }
}

