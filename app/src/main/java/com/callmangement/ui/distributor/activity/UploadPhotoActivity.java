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
import android.text.format.Formatter;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.callmangement.network.APIService;
import com.callmangement.network.RetrofitInstance;
import com.callmangement.R;
import com.callmangement.custom.CustomActivity;
import com.callmangement.databinding.ActivityUploadPhotoBinding;
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

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UploadPhotoActivity extends CustomActivity implements View.OnClickListener {
    private ActivityUploadPhotoBinding binding;
    private boolean permissionGranted;
    private File uploadImageFile = null;
    private String fpsCode = "";
    private String tranId = "";
    private String districtId = "";
    private String flagType = "";
    public final int REQUEST_PICK_PHOTO = 1113;
    public final int REQUEST_PICK_GALLERY_IMAGE = 1114;
    private PrefManager prefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUploadPhotoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.actionBar.ivBack.setVisibility(View.VISIBLE);
        binding.actionBar.ivThreeDot.setVisibility(View.GONE);
        binding.actionBar.layoutLanguage.setVisibility(View.GONE);
        binding.actionBar.buttonPDF.setVisibility(View.GONE);
        binding.actionBar.textToolbarTitle.setText(getResources().getString(R.string.upload_photo));
        prefManager = new PrefManager(mContext);
        init();
    }

    private void init() {
        setUpClickListener();
        checkPermission();
        getIntentData();
        if (permissionGranted) {
            capturePhoto();
        } else {
            makeToast(getResources().getString(R.string.please_allow_all_permission));
        }
    }

    private void setUpClickListener() {
        binding.actionBar.ivBack.setOnClickListener(this);
        binding.tvUpload.setOnClickListener(this);
        binding.ivImage.setOnClickListener(this);
    }

    private void getIntentData() {
        fpsCode = getIntent().getStringExtra("fps_code");
        tranId = getIntent().getStringExtra("tran_id");
        districtId = getIntent().getStringExtra("district_id");
        flagType = getIntent().getStringExtra("flagType");
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
        }

        else {
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

    private void capturePhoto() {
        try {
            final CharSequence[] items = {getResources().getString(R.string.imagepicker_str_take_photo), getResources().getString(R.string.imagepicker_str_choose_from_gallery),getResources().getString(R.string.imagepicker_str_cancel)};
            TextView title = new TextView(mContext);
            title.setText(getResources().getString(R.string.capture_photo));
            title.setBackgroundColor(getResources().getColor(R.color.colorActionBar));
            title.setPadding(15, 25, 15, 25);
            title.setGravity(Gravity.CENTER);
            title.setTextColor(Color.WHITE);
            title.setTextSize(22);
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setCustomTitle(title);
            builder.setCancelable(false);
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
                            .start(REQUEST_PICK_GALLERY_IMAGE);
                } else if (items[item].equals(getResources().getString(R.string.imagepicker_str_cancel))) {
                    onBackPressed();
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
                String photoStoragePath = image.getPath();
                if (photoStoragePath.contains("file:/")) {
                    photoStoragePath = photoStoragePath.replace("file:/", "");
                }
                photoStoragePath = CompressImage.compress(photoStoragePath, this);
                uploadImageFile = new File(photoStoragePath);
                Bitmap myBitmap = BitmapFactory.decodeFile(uploadImageFile.getAbsolutePath());
                try {
                    binding.ivImage.setImageBitmap(ImageUtilsForRotate.ensurePortrait(photoStoragePath));
                } catch (IOException e) {
                    binding.ivImage.setImageBitmap(myBitmap);
                    e.printStackTrace();
                }catch (NullPointerException e){
                    e.printStackTrace();
                }
            }
        } else if (requestCode == REQUEST_PICK_GALLERY_IMAGE && resultCode == RESULT_OK && data != null) {
            ArrayList<Image> images = data.getParcelableArrayListExtra(Config.EXTRA_IMAGES);
            if (images != null && images.size() > 0) {
                Image image = images.get(0);
                String photoStoragePath = image.getPath();
                if (photoStoragePath.contains("file:/")) {
                    photoStoragePath = photoStoragePath.replace("file:/", "");
                }
                photoStoragePath = CompressImage.compress(photoStoragePath, this);
                uploadImageFile = new File(photoStoragePath);
                Bitmap myBitmap = BitmapFactory.decodeFile(uploadImageFile.getAbsolutePath());
                try {
                    binding.ivImage.setImageBitmap(ImageUtilsForRotate.ensurePortrait(photoStoragePath));
                } catch (IOException e) {
                    binding.ivImage.setImageBitmap(myBitmap);
                    e.printStackTrace();
                }catch (NullPointerException e){
                    e.printStackTrace();
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void upload() {
        if (uploadImageFile == null)
            makeToast("Please select photo");
        else {
            if (Constants.isNetworkAvailable(mContext)) {
                showProgress();
                APIService service = RetrofitInstance.getRetrofitInstance().create(APIService.class);

                MultipartBody.Builder builder = new MultipartBody.Builder();
                builder.setType(MultipartBody.FORM);
                builder.addFormDataPart("userId", prefManager.getUSER_Id());
                builder.addFormDataPart("tranId", tranId);
                builder.addFormDataPart("districtId", districtId);
                builder.addFormDataPart("FPSCode", fpsCode);
                builder.addFormDataPart("ipAddress", getNetworkIp());
                builder.addFormDataPart("flagType", flagType);
                builder.addFormDataPart("dealerImage", uploadImageFile.getName(), RequestBody.create(MediaType.parse("multipart/form-data"), uploadImageFile));
                RequestBody requestBody = builder.build();

                Call<ResponseBody> call = service.uploadDistributerPhoto(requestBody);
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
                                    //    Log.e("upload_response", responseStr);
                                        JSONObject jsonObject = new JSONObject((responseStr));
                                        String status = jsonObject.optString("status");
                                        String message = jsonObject.optString("message");
                                        if (status.equals("200")) {
//                                            makeToast(message);
                                            onBackPressed();
                                        }else makeToast(message);
                                    } else
                                        makeToast(getResources().getString(R.string.error));
                                } else
                                    makeToast(getResources().getString(R.string.error));
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

        }
    }

    private String getNetworkIp() {
        WifiManager wm = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        return Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.iv_back)
            onBackPressed();
        else if (id == R.id.tv_upload)
            upload();
        else if (id == R.id.iv_image)
            if (permissionGranted) {
                capturePhoto();
            } else {
                makeToast(getResources().getString(R.string.please_allow_all_permission));
            }
    }
}