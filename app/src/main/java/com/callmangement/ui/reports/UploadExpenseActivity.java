package com.callmangement.ui.reports;

import android.Manifest;
import android.annotation.SuppressLint;
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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.callmangement.Network.APIService;
import com.callmangement.Network.MultipartRequester;
import com.callmangement.Network.RetrofitInstance;
import com.callmangement.R;
import com.callmangement.adapter.ViewExpenseActivityAdapter;
import com.callmangement.custom.CustomActivity;
import com.callmangement.databinding.ActivityUploadExpenseBinding;
import com.callmangement.imagepicker.model.Config;
import com.callmangement.imagepicker.model.Image;
import com.callmangement.imagepicker.ui.imagepicker.ImagePicker;
import com.callmangement.support.ImageUtilsForRotate;
import com.callmangement.utils.CompressImage;
import com.callmangement.utils.Constants;
import com.callmangement.utils.PrefManager;
import com.callmangement.support.dexter.Dexter;
import com.callmangement.support.dexter.MultiplePermissionsReport;
import com.callmangement.support.dexter.PermissionToken;
import com.callmangement.support.dexter.listener.PermissionRequest;
import com.callmangement.support.dexter.listener.multi.MultiplePermissionsListener;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UploadExpenseActivity extends CustomActivity implements View.OnClickListener {
    private ActivityUploadExpenseBinding binding;
    private PrefManager prefManager;
    private String challanImageStoragePath = "";
    private boolean permissionGranted;
    public final int REQUEST_PICK_CHALLAN_IMAGES = 1113;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUploadExpenseBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initView();
    }

    private void initView() {
        prefManager = new PrefManager(mContext);
        binding.actionBar.ivBack.setVisibility(View.VISIBLE);
        binding.actionBar.ivThreeDot.setVisibility(View.GONE);
        binding.actionBar.layoutLanguage.setVisibility(View.GONE);
        binding.actionBar.textToolbarTitle.setText(getResources().getString(R.string.upload_expense));
        setUpOnClickListener();
        checkPermission();
        setUpData();
    }

    private void setUpOnClickListener() {
        binding.actionBar.ivBack.setOnClickListener(this);
        binding.ivChallanImage.setOnClickListener(this);
        binding.buttonSave.setOnClickListener(this);
    }

    private void setUpData(){
        binding.inputName.setText(prefManager.getUSER_NAME());
        binding.inputDistrict.setText(prefManager.getUSER_District());
    }

    private void saveExpense() {
        String docketno = Objects.requireNonNull(binding.inputDocketno.getText()).toString().trim();
        String couriername = Objects.requireNonNull(binding.inputcouriername.getText()).toString().trim();
        String remark = Objects.requireNonNull(binding.inputRemark.getText()).toString().trim();
        String expenseAmount = Objects.requireNonNull(binding.inputTotalExpenseAmount.getText()).toString().trim();
        if (Constants.isNetworkAvailable(mContext)) {
            if (remark.isEmpty()){
                makeToast(getResources().getString(R.string.please_input_remark));
            } else if (expenseAmount.isEmpty()){
                makeToast(getResources().getString(R.string.please_input_expense_amount));
            } else if (challanImageStoragePath.isEmpty()){
                makeToast(getResources().getString(R.string.please_select_challan_image));

        } else if (docketno.isEmpty()){
            makeToast(getResources().getString(R.string.please_select_docket_no_));
        }

            else if (couriername.isEmpty()){
                makeToast(getResources().getString(R.string.please_select_courier_name));
            }



            else {
                showProgress();
                APIService service = RetrofitInstance.getRetrofitInstance().create(APIService.class);
                Call<ResponseBody> call = service.saveExpenses(
                        MultipartRequester.fromString(prefManager.getUSER_Id()),
                        MultipartRequester.fromString(expenseAmount),
                        MultipartRequester.fromString(remark),
                        MultipartRequester.fromString(docketno),
                        MultipartRequester.fromString(couriername),
                        MultipartRequester.fromFile("ExChallanCopy", challanImageStoragePath));
                call.enqueue(new Callback<ResponseBody>() {
                    @SuppressLint("UseCompatLoadingForDrawables")
                    @Override
                    public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                        hideProgress();
                        try {
                            if (response.isSuccessful()) {
                                if (response.code() == 200) {
                                    if (response.body() != null) {
                                        String responseStr = response.body().string();
                                        JSONObject jsonObject = new JSONObject(responseStr);
                                        String status = jsonObject.optString("status");
                                        String message = jsonObject.optString("message");
                                        if (status.equals("200")) {
                                            Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
                                            binding.inputRemark.getText().clear();
                                            binding.inputTotalExpenseAmount.getText().clear();
                                            binding.inputDocketno.getText().clear();
                                            binding.inputcouriername.getText().clear();
                                            binding.ivChallanImage.setImageBitmap(null);
                                            binding.ivChallanImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_image));
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
            }
        } else
            makeToast(getResources().getString(R.string.no_internet_connection));
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
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_PICK_CHALLAN_IMAGES && resultCode == RESULT_OK && data != null) {
            ArrayList<Image> images = data.getParcelableArrayListExtra(Config.EXTRA_IMAGES);
            if (images != null && images.size() > 0) {
                Image image = images.get(0);
                challanImageStoragePath = image.getPath();
                if (challanImageStoragePath.contains("file:/")) {
                    challanImageStoragePath = challanImageStoragePath.replace("file:/", "");
                }
                challanImageStoragePath = CompressImage.compress(challanImageStoragePath, this);
                File imgFile = new File(challanImageStoragePath);
                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                try {
                    binding.ivChallanImage.setImageBitmap(ImageUtilsForRotate.ensurePortrait(challanImageStoragePath));
                } catch (IOException e) {
                    binding.ivChallanImage.setImageBitmap(myBitmap);
                    e.printStackTrace();
                }catch (NullPointerException e){
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.iv_back){
            onBackPressed();
        } else if (id == R.id.ivChallanImage){
            if (permissionGranted) {
                selectImage();
            } else {
                makeToast(getResources().getString(R.string.please_allow_all_permission));
            }
        } else if (id == R.id.buttonSave) {
            saveExpense();
        }
    }
}