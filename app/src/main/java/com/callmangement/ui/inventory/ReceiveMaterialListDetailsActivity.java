package com.callmangement.ui.inventory;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.callmangement.Network.APIService;
import com.callmangement.Network.MultipartRequester;
import com.callmangement.Network.RetrofitInstance;
import com.callmangement.R;
import com.callmangement.adapter.ReceiveMaterialListActivityAdapter;
import com.callmangement.adapter.ReceiveMaterialListDetailsActivityAdapter;
import com.callmangement.custom.CustomActivity;
import com.callmangement.databinding.ActivityReceiveMaterialListBinding;
import com.callmangement.databinding.ActivityReceiveMaterialListDetailsBinding;
import com.callmangement.imagepicker.model.Config;
import com.callmangement.imagepicker.model.Image;
import com.callmangement.imagepicker.ui.imagepicker.ImagePicker;
import com.callmangement.model.inventrory.ModelDispatchInvoice;
import com.callmangement.model.inventrory.ModelInventoryResponse;
import com.callmangement.model.inventrory.ModelPartsDispatchInvoiceList;
import com.callmangement.model.inventrory.ModelPartsList;
import com.callmangement.model.inventrory.ModelReceiveMaterialListData;
import com.callmangement.model.inventrory.ModelReceiveMaterialListDataList;
import com.callmangement.support.ImageUtilsForRotate;
import com.callmangement.ui.home.ZoomInZoomOutActivity;
import com.callmangement.ui.model.inventory.receive_invoice_parts.PartsDispatchInvoice;
import com.callmangement.ui.model.inventory.receive_invoice_parts.ReceiveInvoicePartsResponse;
import com.callmangement.utils.CompressImage;
import com.callmangement.utils.Constants;
import com.callmangement.utils.EqualSpacingItemDecoration;
import com.callmangement.utils.PrefManager;
import com.google.gson.Gson;
import com.callmangement.support.dexter.Dexter;
import com.callmangement.support.dexter.MultiplePermissionsReport;
import com.callmangement.support.dexter.PermissionToken;
import com.callmangement.support.dexter.listener.PermissionRequest;
import com.callmangement.support.dexter.listener.multi.MultiplePermissionsListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Multipart;

public class ReceiveMaterialListDetailsActivity extends CustomActivity implements View.OnClickListener {
    private ActivityReceiveMaterialListDetailsBinding binding;
    private PrefManager prefManager;
    private ModelPartsDispatchInvoiceList model;
    private Activity mActivity;
    List<PartsDispatchInvoice> partsList;
    String invoiceId = "";
    String dispatchId = "";
    String userId = "";
    private String challanImageStoragePath = "";
    private boolean permissionGranted;
    public final int REQUEST_PICK_CHALLAN_IMAGES = 1113;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_receive_material_list_details);
        binding.actionBar.ivBack.setVisibility(View.VISIBLE);
        binding.actionBar.ivThreeDot.setVisibility(View.GONE);
        binding.actionBar.layoutLanguage.setVisibility(View.GONE);
        binding.actionBar.textToolbarTitle.setText(getResources().getString(R.string.receive_material));
        prefManager = new PrefManager(mContext);
        model = (ModelPartsDispatchInvoiceList) getIntent().getSerializableExtra("param");
        initView();
    }

    @SuppressLint("SetTextI18n")
    private void initView() {
        mActivity = this;

        if (model!=null) {
            if (model.isReceived.equalsIgnoreCase("true")) {
                binding.buttonReceive.setVisibility(View.GONE);
                binding.ivPartsImage.setVisibility(View.GONE);
                binding.ivPartsImageView.setVisibility(View.VISIBLE);
                Glide.with(mContext)
                        .load(Constants.API_BASE_URL+model.getReceivedPartsImage())
                        .placeholder(R.drawable.image_not_fount)
                        .into(binding.ivPartsImageView);
                /*binding.inputRemark.setEnabled(false);
                binding.inputRemark.setText(""+model.getReceiverRemark());*/
            } else if (model.isReceived.equalsIgnoreCase("false")) {
                binding.buttonReceive.setVisibility(View.VISIBLE);
                binding.ivPartsImage.setVisibility(View.VISIBLE);
                binding.ivPartsImageView.setVisibility(View.GONE);
//                binding.inputRemark.setEnabled(true);
            }
        }

        checkPermission();
        setUpClickListener();
        setUpData();
        setReceiveMaterialListAdapter();
    }

    private void checkPermission(){

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

            Dexter.withContext(this)
                    .withPermissions(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.READ_MEDIA_IMAGES,
                            Manifest.permission.READ_MEDIA_VIDEO,
                            Manifest.permission.CAMERA
                    ).withListener(new MultiplePermissionsListener() {
                        @Override public void onPermissionsChecked(MultiplePermissionsReport report) {
                            if (report.areAllPermissionsGranted()){
                                permissionGranted = true;
                            }
                        }
                        @Override public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                            token.continuePermissionRequest();
                        }
                    }).check();

        }
else {
            Dexter.withContext(this)
                    .withPermissions(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.CAMERA
                    ).withListener(new MultiplePermissionsListener() {
                        @Override public void onPermissionsChecked(MultiplePermissionsReport report) {
                            if (report.areAllPermissionsGranted()){
                                permissionGranted = true;
                            }
                        }
                        @Override public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                            token.continuePermissionRequest();
                        }
                    }).check();

        }






    }

    private void setUpClickListener() {
        binding.actionBar.ivBack.setOnClickListener(this);
        binding.buttonReceive.setOnClickListener(this);
        binding.ivPartsImage.setOnClickListener(this);
        binding.ivPartsImageView.setOnClickListener(this);
    }

    @SuppressLint("SetTextI18n")
    private void setUpData(){
        if (getIntent().getStringExtra("date") != null) {
            String[] separator = getIntent().getStringExtra("date").split("-");
            String year = separator[0];
            String month = separator[1];
            String day = separator[2];
            binding.tvReceiveItemDate.setText(day + "-" + month + "-" + year);
        }
    }

    private void selectImage() {
        try {
            final CharSequence[] items = {getResources().getString(R.string.imagepicker_str_take_photo), getResources().getString(R.string.imagepicker_str_choose_from_gallery), getResources().getString(R.string.imagepicker_str_cancel)};
            TextView title = new TextView(mContext);
            title.setText(getResources().getString(R.string.imagepicker_str_select_parts_image));
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
                            .start(REQUEST_PICK_CHALLAN_IMAGES);

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
                            .start(REQUEST_PICK_CHALLAN_IMAGES);
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
        if (requestCode == REQUEST_PICK_CHALLAN_IMAGES && resultCode == RESULT_OK && data != null) {
            ArrayList<Image> images = data.getParcelableArrayListExtra(Config.EXTRA_IMAGES);
            if (images != null && images.size() > 0) {
                Image image = images.get(0);
                challanImageStoragePath = image.getPath();
                if (challanImageStoragePath.contains("file:/")) {
                    challanImageStoragePath = challanImageStoragePath.replace("file:/", "");
                }
                challanImageStoragePath = CompressImage.compress(challanImageStoragePath, this);
                File imgFile = new  File(challanImageStoragePath);
                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                try {
                    binding.ivPartsImage.setImageBitmap(ImageUtilsForRotate.ensurePortrait(challanImageStoragePath));
                } catch (IOException e) {
                    binding.ivPartsImage.setImageBitmap(myBitmap);
                    e.printStackTrace();
                }catch (NullPointerException e){
                    e.printStackTrace();
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void setReceiveMaterialListAdapter() {
        if (Constants.isNetworkAvailable(mActivity)) {
            showProgress();
            APIService service = RetrofitInstance.getRetrofitInstance().create(APIService.class);
            invoiceId = getIntent().getStringExtra("invoice_id");
            dispatchId = getIntent().getStringExtra("dispatch_id");
            userId = prefManager.getUSER_Id();
         //   Log.e("params","invoice_id : "+invoiceId+" user_id : "+userId+" dispatch_id : "+dispatchId);
            Call<ResponseBody> call = service.getReciveInvoiceParts(invoiceId, userId, dispatchId);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                    hideProgress();
                    try{
                        if (response.isSuccessful()) {
                            if (response.code() == 200) {
                                if (response.body() != null) {
                                    Gson gson = new Gson();
                                    ReceiveInvoicePartsResponse modelResponse = gson.fromJson(response.body().string(), ReceiveInvoicePartsResponse.class);
                                    if (modelResponse != null) {
                                        if (modelResponse.getStatus().equals("200")) {
                                            if (modelResponse.getPartsDispatchInvoiceList().size() > 0) {
                                                partsList = modelResponse.getPartsDispatchInvoiceList();
                                                binding.topLay.setVisibility(View.VISIBLE);
                                                binding.rvMaterialList.setLayoutManager(new LinearLayoutManager(ReceiveMaterialListDetailsActivity.this, LinearLayoutManager.VERTICAL, false));
                                                binding.rvMaterialList.setAdapter(new ReceiveMaterialListDetailsActivityAdapter(mContext,partsList));
                                            }
                                        }
                                    }
                                }
                            }
                        } else {
                            makeToast(getResources().getString(R.string.error));
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
                @Override
                public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                    hideProgress();
                    makeToast(getResources().getString(R.string.error_message));
                }
            });
        } else makeToast(getResources().getString(R.string.no_internet_connection));
    }

    private void saveReceiveItem() {
//        String remarkStr = binding.inputRemark.getText().toString().trim();
//        if (!remarkStr.isEmpty()) {
            JSONArray jsonArray = new JSONArray();
            for (PartsDispatchInvoice model : partsList) {
                try {
                    String isReceivedValue = "0";
                    if (Integer.parseInt(model.getQuanity()) > 0)
                        isReceivedValue = "1";
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("DispatchId", model.getDispatchId());
                    jsonObject.put("InvoiceId", invoiceId);
                    jsonObject.put("ItemId", model.getItemId());
                    jsonObject.put("Received_Item_Qty", model.getQuanity());
                    jsonObject.put("IsReceived", isReceivedValue);
                    jsonArray.put(jsonObject);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (Constants.isNetworkAvailable(mActivity)) {
                showProgress();
//                RequestBody jsonArrBody = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), String.valueOf(jsonArray));
                APIService service = RetrofitInstance.getRetrofitInstance().create(APIService.class);
                Call<ResponseBody> call = service.savePartsReceive(MultipartRequester.fromString(invoiceId),
                        MultipartRequester.fromString(userId),
                        MultipartRequester.fromString("true"),
                        MultipartRequester.fromString(""),
                        MultipartRequester.fromString(String.valueOf(jsonArray)),
                        MultipartRequester.fromFile("RecPartsImage", challanImageStoragePath));
                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                        hideProgress();
                        try {
                            if (response.isSuccessful()) {
                                if (response.code() == 200) {
                                    if (response.body() != null) {
                                        String responseStr = response.body().string();
                                //        Log.e("param_save_receive_item",responseStr);
                                        JSONObject jsonObject = new JSONObject(responseStr);
                                        String status = jsonObject.optString("status");
                                        String message = jsonObject.optString("message");
                                        if (status.equals("200")) {
//                                            Toast.makeText(mActivity, "Save Successfully", Toast.LENGTH_SHORT).show();
                                            onBackPressed();
                                        } else {
                                            makeToast(message);
                                        }
                                    }
                                }
                            } else {
                                makeToast(getResources().getString(R.string.error));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                        hideProgress();
                        makeToast(getResources().getString(R.string.error_message));
                    }
                });
            } else
                makeToast(getResources().getString(R.string.no_internet_connection));
        /*}else makeToast(getResources().getString(R.string.please_enter_remark));*/
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.iv_back) {
            onBackPressed();
        } else if (id == R.id.buttonReceive) {
            if (challanImageStoragePath.isEmpty()){
                makeToast(getResources().getString(R.string.please_select_parts_image));
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setMessage(getResources().getString(R.string.are_you_sure_you_want_to_receive_this_invoice))
                        .setCancelable(false)
                        .setPositiveButton(getResources().getString(R.string.yes), (dlg, ids) -> {
                            dlg.dismiss();
                            saveReceiveItem();
                        })
                        .setNegativeButton(getResources().getString(R.string.no), (dlg, ids) -> dlg.dismiss());
                AlertDialog alert = builder.create();
                alert.setTitle(getResources().getString(R.string.alert));
                alert.show();
            }
        } else if (id == R.id.ivPartsImage){
            if (permissionGranted) {
                selectImage();
            } else {
                makeToast(getResources().getString(R.string.please_allow_all_permission));
            }
        } else if (id == R.id.ivPartsImageView){
            startActivity(new Intent(mContext, ZoomInZoomOutActivity.class).putExtra("image", Constants.API_BASE_URL+model.getReceivedPartsImage()));
        }
    }
}