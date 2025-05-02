package com.callmangement.ui.complaint;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.documentfile.provider.DocumentFile;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.callmangement.Network.APIService;
import com.callmangement.Network.RetrofitInstance;
import com.callmangement.R;
import com.callmangement.custom.CustomActivity;
import com.callmangement.custom_spinner_with_checkbox.MutliSelectDistributionPartsAdapter;
import com.callmangement.databinding.ActivityComplaintDetailBinding;
import com.callmangement.databinding.CustomMultiSelectBinding;
import com.callmangement.imagepicker.model.Config;
import com.callmangement.imagepicker.model.Image;
import com.callmangement.imagepicker.ui.imagepicker.ImagePicker;
import com.callmangement.model.ModelResponse;
import com.callmangement.model.complaints.ModelComplaintList;
import com.callmangement.model.inventrory.ModelPartsList;
import com.callmangement.support.ImageUtilsForRotate;
import com.callmangement.ui.home.ZoomInZoomOutActivity;
import com.callmangement.ui.inventory.InventoryViewModel;
import com.callmangement.utils.CompressImage;
import com.callmangement.utils.Constants;
import com.callmangement.utils.FileUtils;
import com.callmangement.utils.PrefManager;
import com.callmangement.support.dexter.Dexter;
import com.callmangement.support.dexter.MultiplePermissionsReport;
import com.callmangement.support.dexter.PermissionToken;
import com.callmangement.support.dexter.listener.PermissionRequest;
import com.callmangement.support.dexter.listener.multi.MultiplePermissionsListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ComplaintDetailActivity extends CustomActivity implements View.OnClickListener {
    public final int REQUEST_PICK_IMAGES_CUSTOM_AAD_COMPLAINT = 1113;
    public final int REQUEST_PICK_IMAGES_DSO_LETTER = 1114;
    private static final int REQUEST_READ_STORAGE = 100;
    private final Calendar myCalendarResolvedDate = Calendar.getInstance();
    String TAG = "Cancel";
    List<String> alreadySelectedParts = new ArrayList<>();
    String[] permissions;
    private ActivityComplaintDetailBinding binding;
    private ModelComplaintList model;
    private PrefManager prefManager;
    private ComplaintViewModel viewModel;
    private InventoryViewModel inventoryViewModel;
    private String remark = "";
    private String replacePart = "";
    private String courierDetail = "";
    private String challanNumber = "";
    private String resolvedDate = "";
    private String param;
    private String imageStoragePath = "";
    private String dsoletterStoragePath = "";
//    private String dsoletterImageStoragePath = "";

    private String DSO_LETTER_TYPE = "";
    private boolean permissionGranted;
    private List<ModelPartsList> listParts = new ArrayList<>();
    private String replacePartsIds = "";
    private DatePickerDialog.OnDateSetListener dateResolved;
    private Dialog dialogReplaceParts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_complaint_detail);
        binding.actionBar.ivBack.setVisibility(View.VISIBLE);
        binding.actionBar.ivThreeDot.setVisibility(View.GONE);
        binding.actionBar.layoutLanguage.setVisibility(View.GONE);
        binding.actionBar.buttonPDF.setVisibility(View.GONE);
        binding.actionBar.textToolbarTitle.setText(getResources().getString(R.string.call_detail_));
        prefManager = new PrefManager(mContext);
        model = (ModelComplaintList) getIntent().getSerializableExtra("param");
        param = getIntent().getStringExtra("param2");
        binding.setData(model);
        viewModel = ViewModelProviders.of(this).get(ComplaintViewModel.class);
        inventoryViewModel = ViewModelProviders.of(this).get(InventoryViewModel.class);
        initView();
    }

    private void initView() {
        checkPermission();
        setUpOnClickListener();
        setUpData();
        getSE_AvlStockPartsList();

        dialogReplaceParts = new Dialog(this);
    }

    private void setUpOnClickListener() {
        binding.seImage.setOnClickListener(this);
        binding.inputResolvedDate.setOnClickListener(this);
        binding.buttonResolved.setOnClickListener(this);
        binding.inputImage.setOnClickListener(this);
        binding.inputDSOLetter.setOnClickListener(this);
        binding.inputReplacePart.setOnClickListener(this);
        binding.actionBar.ivBack.setOnClickListener(this);
        binding.buttonSliderSendToSE.setOnSlideCompleteListener(slideToActView -> onClickSendToSEComplaint());
        binding.buttonAcceptComplaint.setOnClickListener(this);


        binding.chkBoxIsPhysicalDamage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (binding.chkBoxIsPhysicalDamage.isChecked()) {
                    binding.inputDSOLetterHint.setVisibility(View.VISIBLE);
                    binding.inputDSOLetter.setVisibility(View.VISIBLE);
                } else {
                    binding.inputDSOLetterHint.setVisibility(View.GONE);
                    binding.inputDSOLetter.setVisibility(View.GONE);
                }
            }
        });

    }

    private void setUpData() {
        dateResolved = (view1, year, monthOfYear, dayOfMonth) -> {
            myCalendarResolvedDate.set(Calendar.YEAR, year);
            myCalendarResolvedDate.set(Calendar.MONTH, monthOfYear);
            myCalendarResolvedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            timePicker();
        };

        if (prefManager.getUSER_TYPE_ID().equalsIgnoreCase("4") && prefManager.getUSER_TYPE().equalsIgnoreCase("ServiceEngineer")) {
            if (param.equalsIgnoreCase("pending")) {
                binding.chkBoxIsPhysicalDamage.setVisibility(View.VISIBLE);
                binding.inputRemark.setClickable(true);
                binding.inputReplacePart.setEnabled(true);
                binding.inputCourierDetail.setEnabled(true);
                binding.inputChallanNumber.setEnabled(true);
                binding.inputResolvedDate.setEnabled(true);
                binding.inputImageHint.setVisibility(View.VISIBLE);
                binding.inputImage.setVisibility(View.VISIBLE);
                binding.inputDSOLetterHint.setVisibility(View.GONE);
                binding.inputDSOLetter.setVisibility(View.GONE);
                binding.seImage.setVisibility(View.GONE);

              binding.chkBoxIsPhysicalDamage.setChecked(model.getPhysicalDamage() != null && model.getPhysicalDamage());

                       Log.d("testedg", "my---"+model.getPhysicalDamage());


                if (binding.chkBoxIsPhysicalDamage.isChecked()) {
                    binding.inputDSOLetterHint.setVisibility(View.VISIBLE);
                    binding.inputDSOLetter.setVisibility(View.VISIBLE);
                    //gagan
                    binding.chkBoxIsPhysicalDamage.setClickable(false);

                } else {
                    binding.inputDSOLetterHint.setVisibility(View.GONE);
                    binding.inputDSOLetter.setVisibility(View.GONE);

                    //gagan
                    binding.chkBoxIsPhysicalDamage.setClickable(true);

                }


                if (model.getComplainStatusId() != null) {
                    if (model.getComplainStatusId().equals("5")) {
                        binding.chkBoxIsPhysicalDamage.setClickable(false);
                        binding.inputRemark.setEnabled(false);
                        binding.inputReplacePart.setEnabled(false);
                        binding.inputCourierDetail.setEnabled(false);
                        binding.inputChallanNumber.setEnabled(false);
                        binding.inputResolvedDate.setEnabled(false);
                        binding.inputImageHint.setVisibility(View.GONE);
                        binding.inputImage.setVisibility(View.GONE);
                        binding.inputDSOLetterHint.setVisibility(View.GONE);
                        binding.inputDSOLetter.setVisibility(View.GONE);
                        binding.seImage.setVisibility(View.VISIBLE);
                        binding.buttonAcceptComplaint.setVisibility(View.VISIBLE);
                        binding.buttonResolved.setVisibility(View.GONE);
                        binding.buttonSliderSendToSE.setVisibility(View.GONE);

                        if (model.getImagePath() != null && !model.getImagePath().isEmpty() && !model.getImagePath().equalsIgnoreCase("null")) {
                            binding.seImage.setVisibility(View.VISIBLE);
                            Glide.with(mContext)
                                    .load(model.getImagePath())
                                    .placeholder(R.drawable.image_not_fount)
                                    .into(binding.seImage);
                        } else {
                            binding.seImage.setVisibility(View.GONE);
                        }

                    } else {
                        binding.buttonAcceptComplaint.setVisibility(View.GONE);
                        binding.buttonResolved.setVisibility(View.VISIBLE);
                        binding.buttonSliderSendToSE.setVisibility(View.VISIBLE);
                    }
                } else {
                    binding.buttonAcceptComplaint.setVisibility(View.GONE);
                    binding.buttonResolved.setVisibility(View.VISIBLE);
                    binding.buttonSliderSendToSE.setVisibility(View.VISIBLE);
                }

                if (model.getSeremark() != null && !model.getSeremark().equalsIgnoreCase("null")) {
                    binding.inputRemark.setText(model.getSeremark());
                } else {
                    binding.inputRemark.setText("");
                }

                if (model.getIsSentToServiceCentre().equals("true")) {
                    if (model.getReplacedPartsDetail() != null && !model.getReplacedPartsDetail().equalsIgnoreCase("null")) {
                        binding.layoutReplacePartsByServiceCenter.setVisibility(View.VISIBLE);
                        binding.inputReplacePartByServiceCenter.setText(model.getReplacedPartsDetail());
                    } else {
                        binding.layoutReplacePartsByServiceCenter.setVisibility(View.GONE);
                    }
                } else {
                    binding.layoutReplacePartsByServiceCenter.setVisibility(View.GONE);
                }

                if (model.getCourierServicesDetail() != null && !model.getCourierServicesDetail().equalsIgnoreCase("null")) {
                    binding.inputCourierDetail.setText(model.getCourierServicesDetail());
                } else {
                    binding.inputCourierDetail.setText("");
                }

                if (model.getSermarkDateStr() != null && !model.getSermarkDateStr().equalsIgnoreCase("null")) {
                    binding.inputResolvedDate.setText(model.getSermarkDateStr());
                } else {
                    binding.inputResolvedDate.setText("");
                }

                if (model.getChallanNo() != null && !model.getChallanNo().equalsIgnoreCase("null")) {
                    binding.inputChallanNumber.setText(model.getChallanNo());
                } else {
                    binding.inputChallanNumber.setText("");
                }

            } else if (param.equalsIgnoreCase("resolved")) {
                binding.chkBoxIsPhysicalDamage.setClickable(false);
                binding.inputRemark.setEnabled(false);
                binding.inputReplacePart.setEnabled(false);
                binding.inputCourierDetail.setEnabled(false);
                binding.inputChallanNumber.setEnabled(false);
                binding.inputResolvedDate.setEnabled(false);
                binding.inputImageHint.setVisibility(View.GONE);
                binding.inputImage.setVisibility(View.GONE);
                binding.inputDSOLetterHint.setVisibility(View.GONE);
                binding.inputDSOLetter.setVisibility(View.GONE);
                binding.seImage.setVisibility(View.VISIBLE);
                binding.buttonResolved.setVisibility(View.GONE);
                binding.buttonSliderSendToSE.setVisibility(View.GONE);
                binding.buttonAcceptComplaint.setVisibility(View.GONE);

                binding.chkBoxIsPhysicalDamage.setChecked(model.getPhysicalDamage() != null && model.getPhysicalDamage());

                if (model.getSeremark() != null && !model.getSeremark().equalsIgnoreCase("null")) {
                    binding.inputRemark.setText(model.getSeremark());
                } else {
                    binding.inputRemark.setText("");
                }

                if (model.getReplacedPartsDetail() != null && !model.getReplacedPartsDetail().equalsIgnoreCase("null")) {
                    binding.layoutReplacePartsByServiceCenter.setVisibility(View.GONE);
                    binding.inputReplacePart.setText(model.getReplacedPartsDetail());
                } else {
                    binding.layoutReplacePartsByServiceCenter.setVisibility(View.GONE);
                    binding.inputReplacePart.setText("");
                }

                if (model.getCourierServicesDetail() != null && !model.getCourierServicesDetail().equalsIgnoreCase("null")) {
                    binding.inputCourierDetail.setText(model.getCourierServicesDetail());
                } else {
                    binding.inputCourierDetail.setText("");
                }

                if (model.getSermarkDateStr() != null && !model.getSermarkDateStr().equalsIgnoreCase("null")) {
                    binding.inputResolvedDate.setText(model.getSermarkDateStr());
                } else {
                    binding.inputResolvedDate.setText("");
                }
                if (model.getChallanNo() != null && !model.getChallanNo().equalsIgnoreCase("null")) {
                    binding.inputChallanNumber.setText(model.getChallanNo());
                } else {
                    binding.inputChallanNumber.setText("");
                }

                if (model.getImagePath() != null && !model.getImagePath().isEmpty() && !model.getImagePath().equalsIgnoreCase("null")) {
                    binding.seImage.setVisibility(View.VISIBLE);
                    Glide.with(mContext)
                            .load(model.getImagePath())
                            .placeholder(R.drawable.image_not_fount)
                            .into(binding.seImage);
                } else {
                    binding.seImage.setVisibility(View.GONE);
                }

            } else if (param.equalsIgnoreCase("service_center")) {
                binding.chkBoxIsPhysicalDamage.setClickable(false);
                binding.inputRemark.setEnabled(false);
                binding.inputReplacePart.setEnabled(false);
                binding.inputCourierDetail.setEnabled(false);
                binding.inputChallanNumber.setEnabled(false);
                binding.inputResolvedDate.setEnabled(false);
                binding.inputImageHint.setVisibility(View.GONE);
                binding.inputImage.setVisibility(View.GONE);
                binding.inputDSOLetterHint.setVisibility(View.GONE);
                binding.inputDSOLetter.setVisibility(View.GONE);
                binding.seImage.setVisibility(View.VISIBLE);
                binding.buttonResolved.setVisibility(View.GONE);
                binding.buttonSliderSendToSE.setVisibility(View.GONE);
                binding.buttonAcceptComplaint.setVisibility(View.GONE);

                binding.chkBoxIsPhysicalDamage.setChecked(model.getPhysicalDamage() != null && model.getPhysicalDamage());

                if (model.getSeremark() != null && !model.getSeremark().equalsIgnoreCase("null")) {
                    binding.inputRemark.setText(model.getSeremark());
                } else {
                    binding.inputRemark.setText("");
                }

                if (model.getReplacedPartsDetail() != null && !model.getReplacedPartsDetail().equalsIgnoreCase("null")) {
                    binding.layoutReplacePartsByServiceCenter.setVisibility(View.GONE);
                    binding.inputReplacePart.setText(model.getReplacedPartsDetail());
                } else {
                    binding.layoutReplacePartsByServiceCenter.setVisibility(View.GONE);
                    binding.inputReplacePart.setText("");
                }

                if (model.getCourierServicesDetail() != null && !model.getCourierServicesDetail().equalsIgnoreCase("null")) {
                    binding.inputCourierDetail.setText(model.getCourierServicesDetail());
                } else {
                    binding.inputCourierDetail.setText("");
                }

                if (model.getSermarkDateStr() != null && !model.getSermarkDateStr().equalsIgnoreCase("null")) {
                    binding.inputResolvedDate.setText(model.getSermarkDateStr());
                } else {
                    binding.inputResolvedDate.setText("");
                }

                if (model.getChallanNo() != null && !model.getChallanNo().equalsIgnoreCase("null")) {
                    binding.inputChallanNumber.setText(model.getChallanNo());
                } else {
                    binding.inputChallanNumber.setText("");
                }

                if (model.getImagePath() != null && !model.getImagePath().isEmpty() && !model.getImagePath().equalsIgnoreCase("null")) {
                    binding.seImage.setVisibility(View.VISIBLE);
                    Glide.with(mContext)
                            .load(model.getImagePath())
                            .placeholder(R.drawable.image_not_fount)
                            .into(binding.seImage);
                } else {
                    binding.seImage.setVisibility(View.GONE);
                }
            }

        }
        else if (prefManager.getUSER_TYPE_ID().equalsIgnoreCase("6") && prefManager.getUSER_TYPE().equalsIgnoreCase("DSO")) {

            if (param.equalsIgnoreCase("pending")) {
                binding.chkBoxIsPhysicalDamage.setClickable(false);
                binding.inputRemark.setEnabled(false);
                binding.inputReplacePart.setEnabled(false);
                binding.inputCourierDetail.setEnabled(false);
                binding.inputChallanNumber.setEnabled(false);
                binding.inputResolvedDate.setEnabled(false);
                binding.inputImageHint.setVisibility(View.GONE);
                binding.inputImage.setVisibility(View.GONE);
                binding.inputDSOLetterHint.setVisibility(View.GONE);
                binding.inputDSOLetter.setVisibility(View.GONE);
                binding.seImage.setVisibility(View.GONE);
                binding.buttonResolved.setVisibility(View.GONE);
                binding.buttonSliderSendToSE.setVisibility(View.GONE);
                binding.buttonAcceptComplaint.setVisibility(View.GONE);

                binding.chkBoxIsPhysicalDamage.setChecked(model.getPhysicalDamage() != null && model.getPhysicalDamage());

                if (model.getSeremark() != null && !model.getSeremark().equalsIgnoreCase("null")) {
                    binding.inputRemark.setText(model.getSeremark());
                } else {
                    binding.inputRemark.setText("");
                }

                if (model.getReplacedPartsDetail() != null && !model.getReplacedPartsDetail().equalsIgnoreCase("null")) {
                    binding.layoutReplacePartsByServiceCenter.setVisibility(View.GONE);
                    binding.inputReplacePart.setText(model.getReplacedPartsDetail());
                } else {
                    binding.layoutReplacePartsByServiceCenter.setVisibility(View.GONE);
                    binding.inputReplacePart.setText("");
                }

                if (model.getCourierServicesDetail() != null && !model.getCourierServicesDetail().equalsIgnoreCase("null")) {
                    binding.inputCourierDetail.setText(model.getCourierServicesDetail());
                } else {
                    binding.inputCourierDetail.setText("");
                }

                if (model.getSermarkDateStr() != null && !model.getSermarkDateStr().equalsIgnoreCase("null")) {
                    binding.inputResolvedDate.setText(model.getSermarkDateStr());
                } else {
                    binding.inputResolvedDate.setText("");
                }

                if (model.getChallanNo() != null && !model.getChallanNo().equalsIgnoreCase("null")) {
                    binding.inputChallanNumber.setText(model.getChallanNo());
                } else {
                    binding.inputChallanNumber.setText("");
                }

                if (model.getImagePath() != null && !model.getImagePath().isEmpty() && !model.getImagePath().equalsIgnoreCase("null")) {
                    binding.seImage.setVisibility(View.VISIBLE);
                    Glide.with(mContext)
                            .load(model.getImagePath())
                            .placeholder(R.drawable.image_not_fount)
                            .into(binding.seImage);
                } else {
                    binding.seImage.setVisibility(View.GONE);
                }

            } else if (param.equalsIgnoreCase("resolved")) {
                binding.chkBoxIsPhysicalDamage.setClickable(false);
                binding.inputRemark.setEnabled(false);
                binding.inputReplacePart.setEnabled(false);
                binding.inputCourierDetail.setEnabled(false);
                binding.inputChallanNumber.setEnabled(false);
                binding.inputResolvedDate.setEnabled(false);
                binding.inputImageHint.setVisibility(View.GONE);
                binding.inputImage.setVisibility(View.GONE);
                binding.inputDSOLetterHint.setVisibility(View.GONE);
                binding.inputDSOLetter.setVisibility(View.GONE);
                binding.seImage.setVisibility(View.VISIBLE);
                binding.buttonResolved.setVisibility(View.GONE);
                binding.buttonSliderSendToSE.setVisibility(View.GONE);
                binding.buttonAcceptComplaint.setVisibility(View.GONE);

                binding.chkBoxIsPhysicalDamage.setChecked(model.getPhysicalDamage() != null && model.getPhysicalDamage());

                if (model.getSeremark() != null && !model.getSeremark().equalsIgnoreCase("null")) {
                    binding.inputRemark.setText(model.getSeremark());
                } else {
                    binding.inputRemark.setText("");
                }

                if (model.getReplacedPartsDetail() != null && !model.getReplacedPartsDetail().equalsIgnoreCase("null")) {
                    binding.layoutReplacePartsByServiceCenter.setVisibility(View.GONE);
                    binding.inputReplacePart.setText(model.getReplacedPartsDetail());
                } else {
                    binding.layoutReplacePartsByServiceCenter.setVisibility(View.GONE);
                    binding.inputReplacePart.setText("");
                }

                if (model.getCourierServicesDetail() != null && !model.getCourierServicesDetail().equalsIgnoreCase("null")) {
                    binding.inputCourierDetail.setText(model.getCourierServicesDetail());
                } else {
                    binding.inputCourierDetail.setText("");
                }

                if (model.getSermarkDateStr() != null && !model.getSermarkDateStr().equalsIgnoreCase("null")) {
                    binding.inputResolvedDate.setText(model.getSermarkDateStr());
                } else {
                    binding.inputResolvedDate.setText("");
                }

                if (model.getChallanNo() != null && !model.getChallanNo().equalsIgnoreCase("null")) {
                    binding.inputChallanNumber.setText(model.getChallanNo());
                } else {
                    binding.inputChallanNumber.setText("");
                }

                if (model.getImagePath() != null && !model.getImagePath().isEmpty() && !model.getImagePath().equalsIgnoreCase("null")) {
                    binding.seImage.setVisibility(View.VISIBLE);
                    Glide.with(mContext)
                            .load(model.getImagePath())
                            .placeholder(R.drawable.image_not_fount)
                            .into(binding.seImage);
                } else {
                    binding.seImage.setVisibility(View.GONE);
                }

            } else if (param.equalsIgnoreCase("service_center")) {
                binding.chkBoxIsPhysicalDamage.setClickable(false);
                binding.inputRemark.setEnabled(false);
                binding.inputReplacePart.setEnabled(false);
                binding.inputCourierDetail.setEnabled(false);
                binding.inputChallanNumber.setEnabled(false);
                binding.inputResolvedDate.setEnabled(false);
                binding.inputImageHint.setVisibility(View.GONE);
                binding.inputImage.setVisibility(View.GONE);
                binding.inputDSOLetterHint.setVisibility(View.GONE);
                binding.inputDSOLetter.setVisibility(View.GONE);
                binding.seImage.setVisibility(View.VISIBLE);
                binding.buttonResolved.setVisibility(View.GONE);
                binding.buttonSliderSendToSE.setVisibility(View.GONE);
                binding.buttonAcceptComplaint.setVisibility(View.GONE);

                binding.chkBoxIsPhysicalDamage.setChecked(model.getPhysicalDamage() != null && model.getPhysicalDamage());

                if (model.getSeremark() != null && !model.getSeremark().equalsIgnoreCase("null")) {
                    binding.inputRemark.setText(model.getSeremark());
                } else {
                    binding.inputRemark.setText("");
                }

                if (model.getReplacedPartsDetail() != null && !model.getReplacedPartsDetail().equalsIgnoreCase("null")) {
                    binding.layoutReplacePartsByServiceCenter.setVisibility(View.GONE);
                    binding.inputReplacePart.setText(model.getReplacedPartsDetail());
                } else {
                    binding.layoutReplacePartsByServiceCenter.setVisibility(View.GONE);
                    binding.inputReplacePart.setText("");
                }

                if (model.getCourierServicesDetail() != null && !model.getCourierServicesDetail().equalsIgnoreCase("null")) {
                    binding.inputCourierDetail.setText(model.getCourierServicesDetail());
                } else {
                    binding.inputCourierDetail.setText("");
                }

                if (model.getSermarkDateStr() != null && !model.getSermarkDateStr().equalsIgnoreCase("null")) {
                    binding.inputResolvedDate.setText(model.getSermarkDateStr());
                } else {
                    binding.inputResolvedDate.setText("");
                }

                if (model.getChallanNo() != null && !model.getChallanNo().equalsIgnoreCase("null")) {
                    binding.inputChallanNumber.setText(model.getChallanNo());
                } else {
                    binding.inputChallanNumber.setText("");
                }

                if (model.getImagePath() != null && !model.getImagePath().isEmpty() && !model.getImagePath().equalsIgnoreCase("null")) {
                    binding.seImage.setVisibility(View.VISIBLE);
                    Glide.with(mContext)
                            .load(model.getImagePath())
                            .placeholder(R.drawable.image_not_fount)
                            .into(binding.seImage);
                } else {
                    binding.seImage.setVisibility(View.GONE);
                }
            }

        }
        else if (prefManager.getUSER_TYPE_ID().equals("2") && prefManager.getUSER_TYPE().equalsIgnoreCase("Manager")) {
            if (param.equalsIgnoreCase("pending")) {
                binding.chkBoxIsPhysicalDamage.setClickable(false);
                binding.inputRemark.setEnabled(false);
                binding.inputReplacePart.setEnabled(false);
                binding.inputCourierDetail.setEnabled(false);


                binding.inputResolvedDate.setEnabled(false);
                binding.inputImageHint.setVisibility(View.GONE);
                binding.inputImage.setVisibility(View.GONE);
                binding.inputDSOLetterHint.setVisibility(View.GONE);
                binding.inputDSOLetter.setVisibility(View.GONE);
                binding.seImage.setVisibility(View.GONE);
                binding.buttonResolved.setVisibility(View.GONE);
                binding.buttonSliderSendToSE.setVisibility(View.GONE);
                binding.buttonAcceptComplaint.setVisibility(View.GONE);

                binding.chkBoxIsPhysicalDamage.setChecked(model.getPhysicalDamage() != null && model.getPhysicalDamage());

                if (model.getSeremark() != null && !model.getSeremark().equalsIgnoreCase("null")) {
                    binding.inputRemark.setText(model.getSeremark());
                } else {
                    binding.inputRemark.setText("");
                }

                if (model.getReplacedPartsDetail() != null && !model.getReplacedPartsDetail().equalsIgnoreCase("null")) {
                    binding.layoutReplacePartsByServiceCenter.setVisibility(View.GONE);
                    binding.inputReplacePart.setText(model.getReplacedPartsDetail());
                } else {
                    binding.layoutReplacePartsByServiceCenter.setVisibility(View.GONE);
                    binding.inputReplacePart.setText("");
                }

                if (model.getCourierServicesDetail() != null && !model.getCourierServicesDetail().equalsIgnoreCase("null")) {
                    binding.inputCourierDetail.setText(model.getCourierServicesDetail());
                } else {
                    binding.inputCourierDetail.setText("");
                }

                if (model.getSermarkDateStr() != null && !model.getSermarkDateStr().equalsIgnoreCase("null")) {
                    binding.inputResolvedDate.setText(model.getSermarkDateStr());
                } else {
                    binding.inputResolvedDate.setText("");
                }

                if (model.getChallanNo() != null && !model.getChallanNo().equalsIgnoreCase("null")) {
                    binding.inputChallanNumber.setText(model.getChallanNo());
                } else {
                    binding.inputChallanNumber.setText("");
                }

                if (model.getImagePath() != null && !model.getImagePath().isEmpty() && !model.getImagePath().equalsIgnoreCase("null")) {
                    binding.seImage.setVisibility(View.VISIBLE);
                    Glide.with(mContext)
                            .load(model.getImagePath())
                            .placeholder(R.drawable.image_not_fount)
                            .into(binding.seImage);
                } else {
                    binding.seImage.setVisibility(View.GONE);
                }

            } else if (param.equalsIgnoreCase("resolved")) {
                binding.chkBoxIsPhysicalDamage.setClickable(false);
                binding.inputRemark.setEnabled(false);
                binding.inputReplacePart.setEnabled(false);
                binding.inputCourierDetail.setEnabled(false);
                binding.inputChallanNumber.setEnabled(false);
                binding.inputResolvedDate.setEnabled(false);
                binding.inputImageHint.setVisibility(View.GONE);
                binding.inputImage.setVisibility(View.GONE);
                binding.inputDSOLetterHint.setVisibility(View.GONE);
                binding.inputDSOLetter.setVisibility(View.GONE);
                binding.seImage.setVisibility(View.VISIBLE);
                binding.buttonResolved.setVisibility(View.GONE);
                binding.buttonSliderSendToSE.setVisibility(View.GONE);
                binding.buttonAcceptComplaint.setVisibility(View.GONE);

                binding.chkBoxIsPhysicalDamage.setChecked(model.getPhysicalDamage() != null && model.getPhysicalDamage());

                if (model.getSeremark() != null && !model.getSeremark().equalsIgnoreCase("null")) {
                    binding.inputRemark.setText(model.getSeremark());
                } else {
                    binding.inputRemark.setText("");
                }

                if (model.getReplacedPartsDetail() != null && !model.getReplacedPartsDetail().equalsIgnoreCase("null")) {
                    binding.layoutReplacePartsByServiceCenter.setVisibility(View.GONE);
                    binding.inputReplacePart.setText(model.getReplacedPartsDetail());
                } else {
                    binding.layoutReplacePartsByServiceCenter.setVisibility(View.GONE);
                    binding.inputReplacePart.setText("");
                }

                if (model.getCourierServicesDetail() != null && !model.getCourierServicesDetail().equalsIgnoreCase("null")) {
                    binding.inputCourierDetail.setText(model.getCourierServicesDetail());
                } else {
                    binding.inputCourierDetail.setText("");
                }

                if (model.getSermarkDateStr() != null && !model.getSermarkDateStr().equalsIgnoreCase("null")) {
                    binding.inputResolvedDate.setText(model.getSermarkDateStr());
                } else {
                    binding.inputResolvedDate.setText("");
                }

                if (model.getChallanNo() != null && !model.getChallanNo().equalsIgnoreCase("null")) {
                    binding.inputChallanNumber.setText(model.getChallanNo());
                } else {
                    binding.inputChallanNumber.setText("");
                }

                if (model.getImagePath() != null && !model.getImagePath().isEmpty() && !model.getImagePath().equalsIgnoreCase("null")) {
                    binding.seImage.setVisibility(View.VISIBLE);
                    Glide.with(mContext)
                            .load(model.getImagePath())
                            .placeholder(R.drawable.image_not_fount)
                            .into(binding.seImage);
                } else {
                    binding.seImage.setVisibility(View.GONE);
                }

            } else if (param.equalsIgnoreCase("service_center")) {
                binding.chkBoxIsPhysicalDamage.setClickable(false);
                binding.inputRemark.setEnabled(false);
                binding.inputReplacePart.setEnabled(false);
                binding.inputCourierDetail.setEnabled(false);
                binding.inputChallanNumber.setEnabled(false);
                binding.inputResolvedDate.setEnabled(false);
                binding.inputImageHint.setVisibility(View.GONE);
                binding.inputImage.setVisibility(View.GONE);
                binding.inputDSOLetterHint.setVisibility(View.GONE);
                binding.inputDSOLetter.setVisibility(View.GONE);
                binding.seImage.setVisibility(View.VISIBLE);
                binding.buttonResolved.setVisibility(View.GONE);
                binding.buttonSliderSendToSE.setVisibility(View.GONE);
                binding.buttonAcceptComplaint.setVisibility(View.GONE);

                binding.chkBoxIsPhysicalDamage.setChecked(model.getPhysicalDamage() != null && model.getPhysicalDamage());

                if (model.getSeremark() != null && !model.getSeremark().equalsIgnoreCase("null")) {
                    binding.inputRemark.setText(model.getSeremark());
                } else {
                    binding.inputRemark.setText("");
                }

                if (model.getReplacedPartsDetail() != null && !model.getReplacedPartsDetail().equalsIgnoreCase("null")) {
                    binding.layoutReplacePartsByServiceCenter.setVisibility(View.GONE);
                    binding.inputReplacePart.setText(model.getReplacedPartsDetail());
                } else {
                    binding.layoutReplacePartsByServiceCenter.setVisibility(View.GONE);
                    binding.inputReplacePart.setText("");
                }

                if (model.getCourierServicesDetail() != null && !model.getCourierServicesDetail().equalsIgnoreCase("null")) {
                    binding.inputCourierDetail.setText(model.getCourierServicesDetail());
                } else {
                    binding.inputCourierDetail.setText("");
                }

                if (model.getSermarkDateStr() != null && !model.getSermarkDateStr().equalsIgnoreCase("null")) {
                    binding.inputResolvedDate.setText(model.getSermarkDateStr());
                } else {
                    binding.inputResolvedDate.setText("");
                }

                if (model.getChallanNo() != null && !model.getChallanNo().equalsIgnoreCase("null")) {
                    binding.inputChallanNumber.setText(model.getChallanNo());
                } else {
                    binding.inputChallanNumber.setText("");
                }

                if (model.getImagePath() != null && !model.getImagePath().isEmpty() && !model.getImagePath().equalsIgnoreCase("null")) {
                    binding.seImage.setVisibility(View.VISIBLE);
                    Glide.with(mContext)
                            .load(model.getImagePath())
                            .placeholder(R.drawable.image_not_fount)
                            .into(binding.seImage);
                } else {
                    binding.seImage.setVisibility(View.GONE);
                }
            }

        }
        else if (prefManager.getUSER_TYPE_ID().equals("5") && prefManager.getUSER_TYPE().equalsIgnoreCase("ServiceCentre")) {
            if (param.equalsIgnoreCase("pending")) {
                binding.chkBoxIsPhysicalDamage.setClickable(false);
                binding.inputRemark.setEnabled(false);
                binding.inputReplacePart.setEnabled(false);
                binding.inputCourierDetail.setEnabled(false);
                binding.inputChallanNumber.setEnabled(false);
                binding.inputResolvedDate.setEnabled(false);
                binding.inputImageHint.setVisibility(View.GONE);
                binding.inputImage.setVisibility(View.GONE);
                binding.inputDSOLetterHint.setVisibility(View.GONE);
                binding.inputDSOLetter.setVisibility(View.GONE);
                binding.buttonAcceptComplaint.setVisibility(View.GONE);

                binding.seImage.setVisibility(View.GONE);
                binding.buttonResolved.setVisibility(View.GONE);
                binding.buttonSliderSendToSE.setVisibility(View.GONE);

                binding.chkBoxIsPhysicalDamage.setChecked(model.getPhysicalDamage() != null && model.getPhysicalDamage());

                if (model.getSeremark() != null && !model.getSeremark().equalsIgnoreCase("null")) {
                    binding.inputRemark.setText(model.getSeremark());
                } else {
                    binding.inputRemark.setText("");
                }

                if (model.getReplacedPartsDetail() != null && !model.getReplacedPartsDetail().equalsIgnoreCase("null")) {
                    binding.layoutReplacePartsByServiceCenter.setVisibility(View.GONE);
                    binding.inputReplacePart.setText(model.getReplacedPartsDetail());
                } else {
                    binding.layoutReplacePartsByServiceCenter.setVisibility(View.GONE);
                    binding.inputReplacePart.setText("");
                }

                if (model.getCourierServicesDetail() != null && !model.getCourierServicesDetail().equalsIgnoreCase("null")) {
                    binding.inputCourierDetail.setText(model.getCourierServicesDetail());
                } else {
                    binding.inputCourierDetail.setText("");
                }

                if (model.getSermarkDateStr() != null && !model.getSermarkDateStr().equalsIgnoreCase("null")) {
                    binding.inputResolvedDate.setText(model.getSermarkDateStr());
                } else {
                    binding.inputResolvedDate.setText("");
                }

                if (model.getChallanNo() != null && !model.getChallanNo().equalsIgnoreCase("null")) {
                    binding.inputChallanNumber.setText(model.getChallanNo());
                } else {
                    binding.inputChallanNumber.setText("");
                }

                if (model.getImagePath() != null && !model.getImagePath().isEmpty() && !model.getImagePath().equalsIgnoreCase("null")) {
                    binding.seImage.setVisibility(View.VISIBLE);
                    Glide.with(mContext)
                            .load(model.getImagePath())
                            .placeholder(R.drawable.image_not_fount)
                            .into(binding.seImage);
                } else {
                    binding.seImage.setVisibility(View.GONE);
                }

            } else if (param.equalsIgnoreCase("resolved")) {
                binding.chkBoxIsPhysicalDamage.setClickable(false);
                binding.inputRemark.setEnabled(false);
                binding.inputReplacePart.setEnabled(false);
                binding.inputCourierDetail.setEnabled(false);
                binding.inputChallanNumber.setEnabled(false);
                binding.inputResolvedDate.setEnabled(false);
                binding.inputImageHint.setVisibility(View.GONE);
                binding.inputImage.setVisibility(View.GONE);
                binding.inputDSOLetterHint.setVisibility(View.GONE);
                binding.inputDSOLetter.setVisibility(View.GONE);
                binding.seImage.setVisibility(View.VISIBLE);
                binding.buttonResolved.setVisibility(View.GONE);
                binding.buttonSliderSendToSE.setVisibility(View.GONE);
                binding.buttonAcceptComplaint.setVisibility(View.GONE);

                binding.chkBoxIsPhysicalDamage.setChecked(model.getPhysicalDamage() != null && model.getPhysicalDamage());

                if (model.getSeremark() != null && !model.getSeremark().equalsIgnoreCase("null")) {
                    binding.inputRemark.setText(model.getSeremark());
                } else {
                    binding.inputRemark.setText("");
                }

                if (model.getReplacedPartsDetail() != null && !model.getReplacedPartsDetail().equalsIgnoreCase("null")) {
                    binding.layoutReplacePartsByServiceCenter.setVisibility(View.GONE);
                    binding.inputReplacePart.setText(model.getReplacedPartsDetail());
                } else {
                    binding.layoutReplacePartsByServiceCenter.setVisibility(View.GONE);
                    binding.inputReplacePart.setText("");
                }

                if (model.getCourierServicesDetail() != null && !model.getCourierServicesDetail().equalsIgnoreCase("null")) {
                    binding.inputCourierDetail.setText(model.getCourierServicesDetail());
                } else {
                    binding.inputCourierDetail.setText("");
                }

                if (model.getSermarkDateStr() != null && !model.getSermarkDateStr().equalsIgnoreCase("null")) {
                    binding.inputResolvedDate.setText(model.getSermarkDateStr());
                } else {
                    binding.inputResolvedDate.setText("");
                }

                if (model.getChallanNo() != null && !model.getChallanNo().equalsIgnoreCase("null")) {
                    binding.inputChallanNumber.setText(model.getChallanNo());
                } else {
                    binding.inputChallanNumber.setText("");
                }

                if (model.getImagePath() != null && !model.getImagePath().isEmpty() && !model.getImagePath().equalsIgnoreCase("null")) {
                    binding.seImage.setVisibility(View.VISIBLE);
                    Glide.with(mContext)
                            .load(model.getImagePath())
                            .placeholder(R.drawable.image_not_fount)
                            .into(binding.seImage);
                } else {
                    binding.seImage.setVisibility(View.GONE);
                }

            } else if (param.equalsIgnoreCase("service_center")) {
                binding.chkBoxIsPhysicalDamage.setClickable(false);
                binding.inputRemark.setEnabled(false);
                binding.inputReplacePart.setEnabled(false);
                binding.inputCourierDetail.setEnabled(false);
                binding.inputChallanNumber.setEnabled(false);
                binding.inputResolvedDate.setEnabled(false);
                binding.inputImageHint.setVisibility(View.GONE);
                binding.inputImage.setVisibility(View.GONE);
                binding.inputDSOLetterHint.setVisibility(View.GONE);
                binding.inputDSOLetter.setVisibility(View.GONE);
                binding.seImage.setVisibility(View.VISIBLE);
                binding.buttonResolved.setVisibility(View.GONE);
                binding.buttonSliderSendToSE.setVisibility(View.GONE);
                binding.buttonAcceptComplaint.setVisibility(View.GONE);

                binding.chkBoxIsPhysicalDamage.setChecked(model.getPhysicalDamage() != null && model.getPhysicalDamage());

                if (model.getSeremark() != null && !model.getSeremark().equalsIgnoreCase("null")) {
                    binding.inputRemark.setText(model.getSeremark());
                } else {
                    binding.inputRemark.setText("");
                }

                if (model.getReplacedPartsDetail() != null && !model.getReplacedPartsDetail().equalsIgnoreCase("null")) {
                    binding.layoutReplacePartsByServiceCenter.setVisibility(View.GONE);
                    binding.inputReplacePart.setText(model.getReplacedPartsDetail());
                } else {
                    binding.layoutReplacePartsByServiceCenter.setVisibility(View.GONE);
                    binding.inputReplacePart.setText("");
                }

                if (model.getCourierServicesDetail() != null && !model.getCourierServicesDetail().equalsIgnoreCase("null")) {
                    binding.inputCourierDetail.setText(model.getCourierServicesDetail());
                } else {
                    binding.inputCourierDetail.setText("");
                }

                if (model.getSermarkDateStr() != null && !model.getSermarkDateStr().equalsIgnoreCase("null")) {
                    binding.inputResolvedDate.setText(model.getSermarkDateStr());
                } else {
                    binding.inputResolvedDate.setText("");
                }

                if (model.getChallanNo() != null && !model.getChallanNo().equalsIgnoreCase("null")) {
                    binding.inputChallanNumber.setText(model.getChallanNo());
                } else {
                    binding.inputChallanNumber.setText("");
                }

                if (model.getImagePath() != null && !model.getImagePath().isEmpty() && !model.getImagePath().equalsIgnoreCase("null")) {
                    binding.seImage.setVisibility(View.VISIBLE);
                    Glide.with(mContext)
                            .load(model.getImagePath())
                            .placeholder(R.drawable.image_not_fount)
                            .into(binding.seImage);
                } else {
                    binding.seImage.setVisibility(View.GONE);
                }
            }

        }
        else if (prefManager.getUSER_TYPE_ID().equals("1") && prefManager.getUSER_TYPE().equalsIgnoreCase("Admin")) {
            if (param.equalsIgnoreCase("pending")) {
                binding.chkBoxIsPhysicalDamage.setClickable(false);
                binding.inputRemark.setEnabled(false);
                binding.inputReplacePart.setEnabled(false);
                binding.inputCourierDetail.setEnabled(false);
                binding.inputChallanNumber.setEnabled(false);
                binding.inputResolvedDate.setEnabled(false);
                binding.inputImageHint.setVisibility(View.GONE);
                binding.inputImage.setVisibility(View.GONE);
                binding.inputDSOLetterHint.setVisibility(View.GONE);
                binding.inputDSOLetter.setVisibility(View.GONE);
                binding.seImage.setVisibility(View.GONE);
                binding.buttonResolved.setVisibility(View.GONE);
                binding.buttonSliderSendToSE.setVisibility(View.GONE);
                binding.buttonAcceptComplaint.setVisibility(View.GONE);

                binding.chkBoxIsPhysicalDamage.setChecked(model.getPhysicalDamage() != null && model.getPhysicalDamage());

                if (model.getSeremark() != null && !model.getSeremark().equalsIgnoreCase("null")) {
                    binding.inputRemark.setText(model.getSeremark());
                } else {
                    binding.inputRemark.setText("");
                }

                if (model.getReplacedPartsDetail() != null && !model.getReplacedPartsDetail().equalsIgnoreCase("null")) {
                    binding.layoutReplacePartsByServiceCenter.setVisibility(View.GONE);
                    binding.inputReplacePart.setText(model.getReplacedPartsDetail());
                } else {
                    binding.layoutReplacePartsByServiceCenter.setVisibility(View.GONE);
                    binding.inputReplacePart.setText("");
                }

                if (model.getCourierServicesDetail() != null && !model.getCourierServicesDetail().equalsIgnoreCase("null")) {
                    binding.inputCourierDetail.setText(model.getCourierServicesDetail());
                } else {
                    binding.inputCourierDetail.setText("");
                }

                if (model.getSermarkDateStr() != null && !model.getSermarkDateStr().equalsIgnoreCase("null")) {
                    binding.inputResolvedDate.setText(model.getSermarkDateStr());
                } else {
                    binding.inputResolvedDate.setText("");
                }

                if (model.getChallanNo() != null && !model.getChallanNo().equalsIgnoreCase("null")) {
                    binding.inputChallanNumber.setText(model.getChallanNo());
                } else {
                    binding.inputChallanNumber.setText("");
                }

                if (model.getImagePath() != null && !model.getImagePath().isEmpty() && !model.getImagePath().equalsIgnoreCase("null")) {
                    binding.seImage.setVisibility(View.VISIBLE);
                    Glide.with(mContext)
                            .load(model.getImagePath())
                            .placeholder(R.drawable.image_not_fount)
                            .into(binding.seImage);
                } else {
                    binding.seImage.setVisibility(View.GONE);
                }

            } else if (param.equalsIgnoreCase("resolved")) {
                binding.chkBoxIsPhysicalDamage.setClickable(false);
                binding.inputRemark.setEnabled(false);
                binding.inputReplacePart.setEnabled(false);
                binding.inputCourierDetail.setEnabled(false);
                binding.inputChallanNumber.setEnabled(false);
                binding.inputResolvedDate.setEnabled(false);
                binding.inputImageHint.setVisibility(View.GONE);
                binding.inputImage.setVisibility(View.GONE);
                binding.inputDSOLetterHint.setVisibility(View.GONE);
                binding.inputDSOLetter.setVisibility(View.GONE);
                binding.seImage.setVisibility(View.VISIBLE);
                binding.buttonResolved.setVisibility(View.GONE);
                binding.buttonSliderSendToSE.setVisibility(View.GONE);
                binding.buttonAcceptComplaint.setVisibility(View.GONE);

                binding.chkBoxIsPhysicalDamage.setChecked(model.getPhysicalDamage() != null && model.getPhysicalDamage());

                if (model.getSeremark() != null && !model.getSeremark().equalsIgnoreCase("null")) {
                    binding.inputRemark.setText(model.getSeremark());
                } else {
                    binding.inputRemark.setText("");
                }

                if (model.getReplacedPartsDetail() != null && !model.getReplacedPartsDetail().equalsIgnoreCase("null")) {
                    binding.layoutReplacePartsByServiceCenter.setVisibility(View.GONE);
                    binding.inputReplacePart.setText(model.getReplacedPartsDetail());
                } else {
                    binding.layoutReplacePartsByServiceCenter.setVisibility(View.GONE);
                    binding.inputReplacePart.setText("");
                }

                if (model.getCourierServicesDetail() != null && !model.getCourierServicesDetail().equalsIgnoreCase("null")) {
                    binding.inputCourierDetail.setText(model.getCourierServicesDetail());
                } else {
                    binding.inputCourierDetail.setText("");
                }

                if (model.getSermarkDateStr() != null && !model.getSermarkDateStr().equalsIgnoreCase("null")) {
                    binding.inputResolvedDate.setText(model.getSermarkDateStr());
                } else {
                    binding.inputResolvedDate.setText("");
                }

                if (model.getChallanNo() != null && !model.getChallanNo().equalsIgnoreCase("null")) {
                    binding.inputChallanNumber.setText(model.getChallanNo());
                } else {
                    binding.inputChallanNumber.setText("");
                }

                if (model.getImagePath() != null && !model.getImagePath().isEmpty() && !model.getImagePath().equalsIgnoreCase("null")) {
                    binding.seImage.setVisibility(View.VISIBLE);
                    Glide.with(mContext)
                            .load(model.getImagePath())
                            .placeholder(R.drawable.image_not_fount)
                            .into(binding.seImage);
                } else {
                    binding.seImage.setVisibility(View.GONE);
                }

            } else if (param.equalsIgnoreCase("service_center")) {
                binding.chkBoxIsPhysicalDamage.setClickable(false);
                binding.inputRemark.setEnabled(false);
                binding.inputReplacePart.setEnabled(false);
                binding.inputCourierDetail.setEnabled(false);
                binding.inputChallanNumber.setEnabled(false);
                binding.inputResolvedDate.setEnabled(false);
                binding.inputImageHint.setVisibility(View.GONE);
                binding.inputImage.setVisibility(View.GONE);
                binding.inputDSOLetterHint.setVisibility(View.GONE);
                binding.inputDSOLetter.setVisibility(View.GONE);
                binding.seImage.setVisibility(View.VISIBLE);
                binding.buttonResolved.setVisibility(View.GONE);
                binding.buttonSliderSendToSE.setVisibility(View.GONE);
                binding.buttonAcceptComplaint.setVisibility(View.GONE);

                binding.chkBoxIsPhysicalDamage.setChecked(model.getPhysicalDamage() != null && model.getPhysicalDamage());

                if (model.getSeremark() != null && !model.getSeremark().equalsIgnoreCase("null")) {
                    binding.inputRemark.setText(model.getSeremark());
                } else {
                    binding.inputRemark.setText("");
                }

                if (model.getReplacedPartsDetail() != null && !model.getReplacedPartsDetail().equalsIgnoreCase("null")) {
                    binding.layoutReplacePartsByServiceCenter.setVisibility(View.GONE);
                    binding.inputReplacePart.setText(model.getReplacedPartsDetail());
                } else {
                    binding.layoutReplacePartsByServiceCenter.setVisibility(View.GONE);
                    binding.inputReplacePart.setText("");
                }

                if (model.getCourierServicesDetail() != null && !model.getCourierServicesDetail().equalsIgnoreCase("null")) {
                    binding.inputCourierDetail.setText(model.getCourierServicesDetail());
                } else {
                    binding.inputCourierDetail.setText("");
                }

                if (model.getSermarkDateStr() != null && !model.getSermarkDateStr().equalsIgnoreCase("null")) {
                    binding.inputResolvedDate.setText(model.getSermarkDateStr());
                } else {
                    binding.inputResolvedDate.setText("");
                }

                if (model.getChallanNo() != null && !model.getChallanNo().equalsIgnoreCase("null")) {
                    binding.inputChallanNumber.setText(model.getChallanNo());
                } else {
                    binding.inputChallanNumber.setText("");
                }

                if (model.getImagePath() != null && !model.getImagePath().isEmpty() && !model.getImagePath().equalsIgnoreCase("null")) {
                    binding.seImage.setVisibility(View.VISIBLE);
                    Glide.with(mContext)
                            .load(model.getImagePath())
                            .placeholder(R.drawable.image_not_fount)
                            .into(binding.seImage);
                } else {
                    binding.seImage.setVisibility(View.GONE);
                }
            }
        }
    }

    private void timePicker() {
        Calendar mCurrentTime = Calendar.getInstance();
        int hour = mCurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mCurrentTime.get(Calendar.MINUTE);
        int second = mCurrentTime.get(Calendar.SECOND);
        TimePickerDialog mTimePicker;
        mTimePicker = new TimePickerDialog(this, (timePicker, selectedHour, selectedMinute) -> {
            @SuppressLint("DefaultLocale") String timeStr = String.format("%02d:%02d:%02d", selectedHour, selectedMinute, second);
            updateLabelResolvedDateTime(timeStr);
        }, hour, minute, false);
        mTimePicker.setTitle(getResources().getString(R.string.select_time));
        mTimePicker.show();
    }

    @SuppressLint("SetTextI18n")
    private void updateLabelResolvedDateTime(String timeStr) {
        Objects.requireNonNull(binding.inputResolvedDate.getText()).clear();
        String myFormat = "yyyy-MM-dd";
        String myFormatSelectedDate = "dd-MM-yyyy";

        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        SimpleDateFormat sdfSelectedDate = new SimpleDateFormat(myFormatSelectedDate, Locale.US);

        String[] separator = model.getComplainRegDateStr().split(" ");
        String regDateSep = separator[0];
        String regTimeSep = separator[1];
        String selectedDate = sdfSelectedDate.format(myCalendarResolvedDate.getTime());

        try {
            @SuppressLint("SimpleDateFormat") SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm:ss");
            Date regTime = sdfTime.parse(regTimeSep);
            Date selectedTime = sdfTime.parse(timeStr);
            if (regDateSep.equals(selectedDate)) {
                if (Objects.requireNonNull(selectedTime).before(regTime)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage(getResources().getString(R.string.please_select_time_after_registered_complaint_time))
                            .setCancelable(false)
                            .setPositiveButton(getResources().getString(R.string.ok), (dialog, id) -> dialog.cancel());
                    AlertDialog alert = builder.create();
                    alert.setTitle(getResources().getString(R.string.alert));
                    alert.show();
                } else {
                    binding.inputResolvedDate.setText(sdf.format(myCalendarResolvedDate.getTime()) + " " + timeStr);
                }
            } else {
                binding.inputResolvedDate.setText(sdf.format(myCalendarResolvedDate.getTime()) + " " + timeStr);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void onClickSendToSEComplaint() {
        remark = Objects.requireNonNull(binding.inputRemark.getText()).toString().trim();
        replacePart = Objects.requireNonNull(binding.inputReplacePart.getText()).toString().trim();
        courierDetail = Objects.requireNonNull(binding.inputCourierDetail.getText()).toString().trim();
        challanNumber = Objects.requireNonNull(binding.inputChallanNumber.getText()).toString().trim();
        resolvedDate = Objects.requireNonNull(binding.inputResolvedDate.getText()).toString().trim();

        /*replacePartsIds = "";
        replacePart = "";*/

        if (challanNumber.isEmpty()) {
            binding.buttonSliderSendToSE.setOuterColor(Color.RED);
            binding.buttonSliderSendToSE.setCompleteIcon(R.drawable.ic_clear);
            new Handler(Looper.myLooper()).postDelayed(() -> {
                binding.buttonSliderSendToSE.setOuterColor(getResources().getColor(R.color.holo_blue_dark));
                binding.buttonSliderSendToSE.setCompleteIcon(R.drawable.ic_check);
                binding.buttonSliderSendToSE.resetSlider();
            }, 2000);
            makeToast(getResources().getString(R.string.please_input_challan_number));

        } else if (resolvedDate.isEmpty()) {
            binding.buttonSliderSendToSE.setOuterColor(Color.RED);
            binding.buttonSliderSendToSE.setCompleteIcon(R.drawable.ic_clear);
            new Handler(Looper.myLooper()).postDelayed(() -> {
                binding.buttonSliderSendToSE.setOuterColor(getResources().getColor(R.color.holo_blue_dark));
                binding.buttonSliderSendToSE.setCompleteIcon(R.drawable.ic_check);
                binding.buttonSliderSendToSE.resetSlider();
            }, 2000);
            makeToast(getResources().getString(R.string.please_select_resolved_date));

        } else if (remark.isEmpty()) {
            binding.buttonSliderSendToSE.setOuterColor(Color.RED);
            binding.buttonSliderSendToSE.setCompleteIcon(R.drawable.ic_clear);
            new Handler(Looper.myLooper()).postDelayed(() -> {
                binding.buttonSliderSendToSE.setOuterColor(getResources().getColor(R.color.holo_blue_dark));
                binding.buttonSliderSendToSE.setCompleteIcon(R.drawable.ic_check);
                binding.buttonSliderSendToSE.resetSlider();
            }, 2000);
            makeToast(getResources().getString(R.string.please_input_remark));

        } /*else if (replacePart.isEmpty()){
                binding.buttonSliderSendToSE.setOuterColor(Color.RED);
                binding.buttonSliderSendToSE.setCompleteIcon(R.drawable.ic_clear);
                new Handler(Looper.myLooper()).postDelayed(() -> {
                    binding.buttonSliderSendToSE.setOuterColor(getResources().getColor(R.color.holo_blue_dark));
                    binding.buttonSliderSendToSE.setCompleteIcon(R.drawable.ic_check);
                    binding.buttonSliderSendToSE.resetSlider();
                },2000);
                makeToast(getResources().getString(R.string.please_input_replace_part));

            }*/ else if (courierDetail.isEmpty()) {
            binding.buttonSliderSendToSE.setOuterColor(Color.RED);
            binding.buttonSliderSendToSE.setCompleteIcon(R.drawable.ic_clear);
            new Handler(Looper.myLooper()).postDelayed(() -> {
                binding.buttonSliderSendToSE.setOuterColor(getResources().getColor(R.color.holo_blue_dark));
                binding.buttonSliderSendToSE.setCompleteIcon(R.drawable.ic_check);
                binding.buttonSliderSendToSE.resetSlider();
            }, 2000);
            makeToast(getResources().getString(R.string.please_input_courier_detail));

        } else if (imageStoragePath.isEmpty()) {
            binding.buttonSliderSendToSE.setOuterColor(Color.RED);
            binding.buttonSliderSendToSE.setCompleteIcon(R.drawable.ic_clear);
            new Handler(Looper.myLooper()).postDelayed(() -> {
                binding.buttonSliderSendToSE.setOuterColor(getResources().getColor(R.color.holo_blue_dark));
                binding.buttonSliderSendToSE.setCompleteIcon(R.drawable.ic_check);
                binding.buttonSliderSendToSE.resetSlider();
            }, 2000);
            makeToast(getResources().getString(R.string.please_select_image));

        } else if (binding.inputDSOLetter.getVisibility() == View.VISIBLE && dsoletterStoragePath.isEmpty()) {
            binding.buttonSliderSendToSE.setOuterColor(Color.RED);
            binding.buttonSliderSendToSE.setCompleteIcon(R.drawable.ic_clear);
            new Handler(Looper.myLooper()).postDelayed(() -> {
                binding.buttonSliderSendToSE.setOuterColor(getResources().getColor(R.color.holo_blue_dark));
                binding.buttonSliderSendToSE.setCompleteIcon(R.drawable.ic_check);
                binding.buttonSliderSendToSE.resetSlider();
            }, 2000);
            makeToast(getResources().getString(R.string.please_upload_dso_letter));

        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setMessage(getResources().getString(R.string.are_you_sure_you_want_to_send_to_service_center_this_complaint))
                    .setCancelable(false)
                    .setPositiveButton(getResources().getString(R.string.ok), (dialog, id) -> {
                        dialog.cancel();

                        String dsoletterStoragePathStr = "";
                        if (binding.inputDSOLetter.getVisibility() == View.VISIBLE && !dsoletterStoragePath.isEmpty()) {
                            dsoletterStoragePathStr = dsoletterStoragePath;
                        }

                        resolveComplaint(convertStringToUTF8(remark), binding.chkBoxIsPhysicalDamage.isChecked() ? "1" : "0", "SendToSECenter", convertStringToUTF8(replacePart), convertStringToUTF8(courierDetail), imageStoragePath, convertStringToUTF8(challanNumber), resolvedDate, replacePartsIds, dsoletterStoragePathStr, DSO_LETTER_TYPE);

                    }).setNegativeButton(getResources().getString(R.string.no), (dialog, id) -> {
                        dialog.cancel();
                        binding.buttonSliderSendToSE.setOuterColor(Color.RED);
                        binding.buttonSliderSendToSE.setCompleteIcon(R.drawable.ic_clear);
                        new Handler(Looper.myLooper()).postDelayed(() -> {
                            binding.buttonSliderSendToSE.setOuterColor(getResources().getColor(R.color.holo_blue_dark));
                            binding.buttonSliderSendToSE.resetSlider();
                        }, 2000);
                    });
            AlertDialog alert = builder.create();
            alert.setTitle(getResources().getString(R.string.alert));
            alert.show();
        }
    }

    public void onClickResolveComplaint() {
        remark = Objects.requireNonNull(binding.inputRemark.getText()).toString().trim();
        replacePart = Objects.requireNonNull(binding.inputReplacePart.getText()).toString().trim();
        courierDetail = Objects.requireNonNull(binding.inputCourierDetail.getText()).toString().trim();
        challanNumber = Objects.requireNonNull(binding.inputChallanNumber.getText()).toString().trim();
        resolvedDate = Objects.requireNonNull(binding.inputResolvedDate.getText()).toString().trim();

        if (challanNumber.isEmpty()) {
            makeToast(getResources().getString(R.string.please_input_challan_number));
        } else if (resolvedDate.isEmpty()) {
            makeToast(getResources().getString(R.string.please_select_resolved_date));
        } else if (remark.isEmpty()) {
            makeToast(getResources().getString(R.string.please_input_remark));
        } /*else if (replacePart.isEmpty()){
            makeToast(getResources().getString(R.string.please_input_replace_part));
        }*/ else if (courierDetail.isEmpty()) {
            makeToast(getResources().getString(R.string.please_input_courier_detail));
        } else if (imageStoragePath.isEmpty()) {
            makeToast(getResources().getString(R.string.please_select_image));
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(getResources().getString(R.string.are_you_sure_you_want_to_resolve_this_complaint))
                    .setCancelable(false)
                    .setPositiveButton(getResources().getString(R.string.yes), (dialog, id) -> {
                        dialog.cancel();

                        String dsoletterStoragePathStr = "";
                        if (binding.inputDSOLetter.getVisibility() == View.VISIBLE && !dsoletterStoragePath.isEmpty()) {
                            dsoletterStoragePathStr = dsoletterStoragePath;
                        }
                        resolveComplaint(convertStringToUTF8(remark), binding.chkBoxIsPhysicalDamage.isChecked() ? "1" : "0", "Resolved", convertStringToUTF8(replacePart), convertStringToUTF8(courierDetail), imageStoragePath, convertStringToUTF8(challanNumber), resolvedDate, replacePartsIds, dsoletterStoragePathStr, DSO_LETTER_TYPE);
                    })
                    .setNegativeButton(getResources().getString(R.string.no), (dialog, id) -> dialog.cancel());
            AlertDialog alert = builder.create();
            alert.setTitle(getResources().getString(R.string.alert));
            alert.show();
        }
    }

    private void resolveComplaint(String remark, String IsPhysicalDamage, String getStatus, String replacePart, String courierDetail, String se_image, String challanNo, String seRemarkDate, String replacePartsIds, String damageApprovalLetter, String DSO_LETTER_TYPE) {
        isLoading();
        viewModel.resolveComplaint(prefManager.getUSER_Id(), IsPhysicalDamage, remark, model.getComplainRegNo(), getStatus, replacePart, courierDetail, se_image, challanNo, seRemarkDate, replacePartsIds, damageApprovalLetter, DSO_LETTER_TYPE).observe(this, modelResolveComplaint -> {
            isLoading();
            Log.d("replacepartsid", "  " + replacePartsIds);


            if (modelResolveComplaint.getStatus().equals("200")) {

                String msg = modelResolveComplaint.getMessage();
//                Toast.makeText(this, "" + msg, Toast.LENGTH_SHORT).show();
//                binding.buttonSliderSendToSE.setOuterColor(getResources().getColor(R.color.holo_blue_dark));
//                binding.buttonSliderSendToSE.setCompleteIcon(R.drawable.ic_check);
//                binding.buttonSliderSendToSE.resetSlider();
//                Intent returnIntent = new Intent();
//                setResult(Activity.RESULT_OK, returnIntent);
//                finish();

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(msg)
                        .setCancelable(false)
                        .setPositiveButton(getResources().getString(R.string.yes), (dialog, id) -> {
                            dialog.cancel();

                            binding.buttonSliderSendToSE.setOuterColor(getResources().getColor(R.color.holo_blue_dark));
                            binding.buttonSliderSendToSE.setCompleteIcon(R.drawable.ic_check);
                            binding.buttonSliderSendToSE.resetSlider();
                            Intent returnIntent = new Intent();
                            setResult(Activity.RESULT_OK, returnIntent);
                            finish();

                        });
                AlertDialog alert = builder.create();
                alert.setTitle(getResources().getString(R.string.alert));
                alert.show();
            } else {
                String msg = modelResolveComplaint.getMessage();
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(msg)
                        .setCancelable(false)
                        .setPositiveButton(getResources().getString(R.string.yes), (dialog, id) -> {
                            dialog.cancel();
                            binding.buttonSliderSendToSE.setOuterColor(Color.RED);
                            binding.buttonSliderSendToSE.setCompleteIcon(R.drawable.ic_clear);
                            new Handler(Looper.myLooper()).postDelayed(() -> {
                                binding.buttonSliderSendToSE.setOuterColor(getResources().getColor(R.color.holo_blue_dark));
                                binding.buttonSliderSendToSE.resetSlider();
                            }, 2000);
                        });
                AlertDialog alert = builder.create();
                alert.setTitle(getResources().getString(R.string.alert));
                alert.show();
            }
        });
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

    private void isInventoryLoading() {
        inventoryViewModel.getIsLoading().observe(this, aBoolean -> {
            if (aBoolean) {
                showProgress(getResources().getString(R.string.please_wait));
            } else {
                hideProgress();
            }
        });
    }

    private String convertStringToUTF8(String s) {
        String out;
        out = new String(s.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1);
        return out;
    }

    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Dexter.withContext(this)
                    .withPermissions(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.READ_MEDIA_IMAGES,
                            Manifest.permission.READ_MEDIA_VIDEO,
                            Manifest.permission.CAMERA
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
                            Manifest.permission.CAMERA
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


    private void selectImage() {
        try {
            final CharSequence[] items = {getResources().getString(R.string.imagepicker_str_take_photo), getResources().getString(R.string.imagepicker_str_choose_from_gallery), getResources().getString(R.string.imagepicker_str_cancel)};
            TextView title = new TextView(mContext);
            title.setText(getResources().getString(R.string.imagepicker_str_select_challan_image));
            title.setBackgroundColor(getResources().getColor(R.color.colorActionBar));
            title.setPadding(15, 25, 15, 25);
            title.setGravity(Gravity.CENTER);
            title.setTextColor(Color.WHITE);
            title.setTextSize(22);
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setCustomTitle(title);
            // builder.setTitle("Add Photo!");
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
                            .start(REQUEST_PICK_IMAGES_CUSTOM_AAD_COMPLAINT);

                } else if (items[item].equals(getResources().getString(R.string.imagepicker_str_choose_from_gallery))) {
                    ImagePicker.with(mContext)
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
                            .start(REQUEST_PICK_IMAGES_CUSTOM_AAD_COMPLAINT);
                } else if (items[item].equals(getResources().getString(R.string.imagepicker_str_cancel))) {
                    dialog.dismiss();
                }
            });
            builder.show();
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("kdcfjcl", "" + e);
        }
    }

    private void selectMultiCheckboxForReplacePart() {

        /*if (!model.getComplPartsIds().isEmpty()) {
            String[] data = model.getComplPartsIds().split(",");
            for (int i =0; i < data.length; i++){
                for (int j =0; j < listParts.size(); j++){
                    if (listParts.get(j).getItemId().equalsIgnoreCase(data[i])){
                        listParts.remove(j);
                    }
                }
            }
        }*/

        if (!dialogReplaceParts.isShowing()) {
            replacePartsIds = "";
            CustomMultiSelectBinding dialogBinding;
            //    final Dialog dialog = new Dialog(this);
            dialogBinding = CustomMultiSelectBinding.inflate(getLayoutInflater());
            dialogReplaceParts.setContentView(dialogBinding.getRoot());
            dialogReplaceParts.setTitle(getResources().getString(R.string.select_replace_parts));
            dialogReplaceParts.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialogReplaceParts.setCanceledOnTouchOutside(false);

            dialogBinding.recyclerViewParts.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
            dialogBinding.recyclerViewParts.setAdapter(new MutliSelectDistributionPartsAdapter(this, listParts));

            dialogBinding.done.setOnClickListener(view -> {
                replacePartsIds = getCommaSeparatedIds(listParts);

                Log.d("replacePartsIds", replacePartsIds);
                String partName = getCommaSeparatedName(listParts);
                binding.inputReplacePart.setText(partName);
                dialogReplaceParts.dismiss();
            });

            dialogBinding.cancel.setOnClickListener(view -> {

                for (int i = 0; i < listParts.size(); i++) {
                    listParts.get(i).setSelectFlag(false);
                }

                replacePartsIds = getCommaSeparatedIds(listParts);
                Log.d("replacePartsIds", replacePartsIds);
                binding.inputReplacePart.setText("");
                dialogReplaceParts.dismiss();
            });

            dialogReplaceParts.show();
        }

    }

    private String getCommaSeparatedIds(List<ModelPartsList> selectedIds) {
        StringBuilder partsIds = new StringBuilder();
        for (int i = 0; i < selectedIds.size(); i++) {
            if (selectedIds.get(i).isSelectFlag())
                partsIds.append(", ").append(selectedIds.get(i).getItemId());
        }
        if (partsIds.length() > 0) {
            return partsIds.substring(1);
        } else {
            return "";
        }
    }

    private String getCommaSeparatedName(List<ModelPartsList> selectedIds) {
        StringBuilder partName = new StringBuilder();
        for (int i = 0; i < selectedIds.size(); i++) {
            if (selectedIds.get(i).isSelectFlag())
                partName.append(", ").append(selectedIds.get(i).getItemName());
        }
        if (partName.length() > 0) {
            return partName.substring(1);
        } else {
            return "";
        }
    }

    private void getSE_AvlStockPartsList() {
        isInventoryLoading();
        inventoryViewModel.getAvailableStockListForSE(prefManager.getUSER_Id(), "0").observe(this, modelParts -> {
            isInventoryLoading();
            if (modelParts.getStatus().equals("200")) {
                listParts = modelParts.getParts();
                Log.d("fdnf", "  " + listParts);
            }
        });
    }

    private void acceptComplaint() {
        if (Constants.isNetworkAvailable(mContext)) {
            showProgress();
            APIService service = RetrofitInstance.getRetrofitInstance().create(APIService.class);
            Call<ModelResponse> call = service.AcceptBySE(prefManager.getUSER_Id(), model.getComplainRegNo());
            call.enqueue(new Callback<ModelResponse>() {
                @Override
                public void onResponse(@NonNull Call<ModelResponse> call, @NonNull Response<ModelResponse> response) {
                    hideProgress();
                    if (response.isSuccessful()) {
                        if (response.code() == 200) {
                            ModelResponse modelResponse = response.body();
                            if (Objects.requireNonNull(modelResponse).getStatus().equals("200")) {
                                Intent returnIntent = new Intent();
                                setResult(Activity.RESULT_OK, returnIntent);
                                finish();
                            } else {
                                makeToast(getResources().getString(R.string.error));
                            }
                        } else {
                            makeToast(getResources().getString(R.string.error));
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_PICK_IMAGES_CUSTOM_AAD_COMPLAINT && resultCode == RESULT_OK && data != null) {
            ArrayList<Image> images = data.getParcelableArrayListExtra(Config.EXTRA_IMAGES);
            if (images != null && images.size() > 0) {
                Image image = images.get(0);
                imageStoragePath = image.getPath();
                if (imageStoragePath.contains("file:/")) {
                    imageStoragePath = imageStoragePath.replace("file:/", "");
                }
                imageStoragePath = CompressImage.compress(imageStoragePath, this);
                binding.inputImage.setText(image.getPath());
                try {
                    ImageUtilsForRotate.ensurePortrait(imageStoragePath);
                } catch (IOException | NullPointerException e ) {
                    e.printStackTrace();
                }
            }
        }

        if (requestCode == REQUEST_PICK_IMAGES_DSO_LETTER && resultCode == RESULT_OK && data != null) {
            ArrayList<Image> images = data.getParcelableArrayListExtra(Config.EXTRA_IMAGES);
            if (images != null && images.size() > 0) {
                Image image = images.get(0);
                DSO_LETTER_TYPE = "IMAGE";
                dsoletterStoragePath = image.getPath();
                if (dsoletterStoragePath.contains("file:/")) {
                    dsoletterStoragePath = dsoletterStoragePath.replace("file:/", "");
                }
                dsoletterStoragePath = CompressImage.compress(dsoletterStoragePath, this);
                binding.inputDSOLetter.setText(image.getPath());
                try {
                    ImageUtilsForRotate.ensurePortrait(dsoletterStoragePath);
                } catch (IOException | NullPointerException e) {
                    e.printStackTrace();
                }
            }
        }

        if (requestCode == REQUEST_CODE_PICK_PDF && resultCode == RESULT_OK) {
            if (data != null) {
                Uri uri = data.getData();
                //     getFileFromUri(getApplicationContext(),uri);
                //    openPdf(uri);
                DSO_LETTER_TYPE = "PDF";
                dsoletterStoragePath = String.valueOf(uri);
//                dsoletterStoragePath = data.getData().getPath().toString();
//                if (dsoletterStoragePath.contains("file:/")) {
//                    dsoletterStoragePath = dsoletterStoragePath.replace("file:/", "");
//                }
                binding.inputDSOLetter.setText(uri.getPath());
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.inputResolvedDate) {
            DatePickerDialog datePickerDialog = new DatePickerDialog(mContext, dateResolved, myCalendarResolvedDate
                    .get(Calendar.YEAR), myCalendarResolvedDate.get(Calendar.MONTH),
                    myCalendarResolvedDate.get(Calendar.DAY_OF_MONTH));
            @SuppressLint("SimpleDateFormat")
            SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
            try {
                Date date = format.parse(model.getComplainRegDateStr());
                datePickerDialog.getDatePicker().setMinDate(Objects.requireNonNull(date).getTime());
            } catch (Exception e) {
                e.printStackTrace();
            }
            datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
            datePickerDialog.show();
        } else if (id == R.id.inputReplacePart) {
            if (Constants.isNetworkAvailable(mContext)) {
                selectMultiCheckboxForReplacePart();
            } else {
                makeToast(getResources().getString(R.string.no_internet_connection));
            }
        } else if (id == R.id.inputImage) {
            if (permissionGranted) {
                selectImage();
            } else {
                makeToast(getResources().getString(R.string.please_allow_all_permission));
            }
        } else if (id == R.id.inputDSOLetter) {
            //   chooseFile();
            //    checkStoragePermission();
            if (permissionGranted) {
                selectDSOLetter();
            } else {
                makeToast(getResources().getString(R.string.please_allow_all_permission));
            }

        } else if (id == R.id.buttonResolved) {
            onClickResolveComplaint();
        } else if (id == R.id.se_image) {
            startActivity(new Intent(mContext, ZoomInZoomOutActivity.class).putExtra("image", model.getImagePath()));
        } else if (id == R.id.iv_back) {
            onBackPressed();
        } else if (id == R.id.buttonAcceptComplaint) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(getResources().getString(R.string.are_you_sure_you_want_to_accept_this_complaint))
                    .setCancelable(false)
                    .setPositiveButton(getResources().getString(R.string.yes), (dialog, ids) -> {
                        dialog.cancel();
                        acceptComplaint();
                    })
                    .setNegativeButton(getResources().getString(R.string.no), (dialog, ids) -> dialog.cancel());
            AlertDialog alert = builder.create();
            alert.setTitle(getResources().getString(R.string.alert));
            alert.show();
        }
    }

    private void chooseFile() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_READ_STORAGE);
        } else {
            openFilePicker();
        }
    }

//    private void openFilePicker() {
//        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//        intent.setType("*/*");
//        String[] mimeTypes = {"image/*", "application/pdf"};
//        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
//        resultLauncher.launch(intent);
//    }


    private void selectDSOLetter() {
        try {
            final CharSequence[] items = {getResources().getString(R.string.imagepicker_str_take_photo), getResources().getString(R.string.imagepicker_str_choose_from_gallery), getResources().getString(R.string.choose_pdf), getResources().getString(R.string.imagepicker_str_cancel)};
            TextView title = new TextView(mContext);
            title.setText(getResources().getString(R.string.imagepicker_str_select_challan_image));
            title.setBackgroundColor(getResources().getColor(R.color.colorActionBar));
            title.setPadding(15, 25, 15, 25);
            title.setGravity(Gravity.CENTER);
            title.setTextColor(Color.WHITE);
            title.setTextSize(22);
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setCustomTitle(title);
            // builder.setTitle("Add Photo!");
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
                            .start(REQUEST_PICK_IMAGES_DSO_LETTER);

                } else if (items[item].equals(getResources().getString(R.string.imagepicker_str_choose_from_gallery))) {
                    ImagePicker.with(mContext)
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
                            .start(REQUEST_PICK_IMAGES_DSO_LETTER);
                } else if (items[item].equals(getResources().getString(R.string.choose_pdf))) {
                    checkStoragePermission();
                } else if (items[item].equals(getResources().getString(R.string.imagepicker_str_cancel))) {
                    dialog.dismiss();
                }
            });
            builder.show();
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("kdcfjcl", "" + e);
        }
    }

    private void checkStoragePermission() {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_READ_STORAGE);
            } else {
                openFilePicker();
            }
        } else {
            openFilePicker();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_READ_STORAGE && grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            openFilePicker();
        } else {
            Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
        }
    }

    private static final int REQUEST_CODE_PICK_PDF = 101;

    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/pdf");
//        intent.setType("*/*");
//        String[] mimeTypes = {"image/*", "application/pdf"};
//        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(Intent.createChooser(intent, "Select PDF"), REQUEST_CODE_PICK_PDF);
    }


    private void openPdf(Uri uri) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, "application/pdf");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "No application found to open PDF", Toast.LENGTH_SHORT).show();
        }
    }

}