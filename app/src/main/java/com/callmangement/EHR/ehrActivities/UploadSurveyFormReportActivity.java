package com.callmangement.EHR.ehrActivities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.callmangement.EHR.api.APIClient;
import com.callmangement.EHR.api.APIInterface;
import com.callmangement.EHR.api.Constants;
import com.callmangement.EHR.api.MultipartRequester;
import com.callmangement.databinding.ActivityUploadSurveyFormReportBinding;
import com.callmangement.EHR.imagepicker.model.Config;
import com.callmangement.EHR.imagepicker.model.Image;
import com.callmangement.EHR.imagepicker.ui.imagepicker.ImagePicker;
import com.callmangement.EHR.models.ModelUploadSurveyFormReport;
import com.callmangement.EHR.support.CompressImage;
import com.callmangement.EHR.support.OnSingleClickListener;
import com.callmangement.EHR.support.Preference;
import com.callmangement.EHR.support.Utils;

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
import com.callmangement.R;
import com.callmangement.support.ImageUtilsForRotate;
import com.callmangement.utils.PrefManager;

public class UploadSurveyFormReportActivity extends BaseActivity {

    Activity mActivity;
    private ActivityUploadSurveyFormReportBinding binding;

    PrefManager preference;
    String ServeyFormId = "";
    String TicketNo = "";

    public final int REQUEST_PICK_IMAGE_ONE = 1111;
    public final int REQUEST_PICK_IMAGE_TWO = 1112;
    public final int REQUEST_PICK_IMAGE_THREE = 1113;

    ArrayList<String> stringArrayListHavingAllFilePath = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        binding = ActivityUploadSurveyFormReportBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        init();
    }

    private void setUpData() {

    }

    private void init() {
        mActivity = this;
        preference =  new PrefManager(mActivity);

        Bundle bundle = getIntent().getExtras();
        ServeyFormId = bundle.getString("ServeyFormId", "");
        TicketNo = bundle.getString("TicketNo", "");

        binding.actionBar.ivBack.setVisibility(View.VISIBLE);
        binding.actionBar.ivThreeDot.setVisibility(View.GONE);
        binding.actionBar.layoutLanguage.setVisibility(View.GONE);
        binding.actionBar.buttonPDF.setVisibility(View.GONE);
        binding.actionBar.textToolbarTitle.setText(getResources().getString(R.string.upload_images));

        setUpData();
        setClickListener();
    }

    public void makeToast(String string) {
        if (TextUtils.isEmpty(string)) return;
        Toast.makeText(mActivity, string, Toast.LENGTH_SHORT).show();
    }

    private void setClickListener() {

        binding.actionBar.ivBack.setOnClickListener(view -> onBackPressed());

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

        binding.buttonUploadImages.setOnClickListener(view -> {

            if (stringArrayListHavingAllFilePath != null && stringArrayListHavingAllFilePath.size() > 0) {
                String USER_Id = preference.getUSER_Id();
                uploadImages(USER_Id, ServeyFormId, TicketNo, stringArrayListHavingAllFilePath);
            } else {
                makeToast("Please choose at least one image.");
            }

        });
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
                try {
                    binding.ivChallanImage.setImageBitmap(ImageUtilsForRotate.ensurePortrait(imageStoragePath));
                    stringArrayListHavingAllFilePath.add(imageStoragePath);
                } catch (IOException e) {
                    binding.ivChallanImage.setImageBitmap(myBitmap);
                    stringArrayListHavingAllFilePath.add(imageStoragePath);

                    e.printStackTrace();
                }catch (NullPointerException e){
                    e.printStackTrace();
                }
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
                    binding.ivPartsImage1.setImageBitmap(ImageUtilsForRotate.ensurePortrait(imageStoragePath));
                    stringArrayListHavingAllFilePath.add(imageStoragePath);
                } catch (IOException e) {
                    binding.ivPartsImage1.setImageBitmap(myBitmap);
                    stringArrayListHavingAllFilePath.add(imageStoragePath);
                    e.printStackTrace();
                }catch (NullPointerException e){
                    e.printStackTrace();
                }

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
                try {
                    binding.ivPartsImage2.setImageBitmap(ImageUtilsForRotate.ensurePortrait(imageStoragePath));
                    stringArrayListHavingAllFilePath.add(imageStoragePath);
                } catch (IOException e) {
                    binding.ivPartsImage2.setImageBitmap(myBitmap);
                    stringArrayListHavingAllFilePath.add(imageStoragePath);
                    e.printStackTrace();
                }catch (NullPointerException e){
                    e.printStackTrace();
                }
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void uploadImages(String UserID, String ServeyFormId, String TicketNo, ArrayList<String> arrayHavingAllFilePath) {

        if (Utils.isNetworkAvailable(mActivity)) {

            Utils.hideKeyboard(mActivity);
            Utils.showCustomProgressDialogCommonForAll(mActivity, getResources().getString(R.string.please_wait));

            APIInterface apiInterface = APIClient.GetRetrofitClientWithoutHeaders(mActivity, BaseUrl()).create(APIInterface.class);

            MultipartBody.Part[] campDocumentsParts = new MultipartBody.Part[arrayHavingAllFilePath.size()];
            for (int index = 0; index < arrayHavingAllFilePath.size(); index++) {
                File file = new File(arrayHavingAllFilePath
                        .get(index));
                RequestBody surveyBody = RequestBody.create(MediaType.parse("image/*"),
                        file);
                campDocumentsParts[index] = MultipartBody.Part.createFormData("CampDocuments",
                        file.getName(),
                        surveyBody);
            }

            Call<ModelUploadSurveyFormReport> call = apiInterface.callUploadSurveyFormReportImagesApi(UploadDocumentOfSurveyFormReport(),
                    MultipartRequester.fromString(UserID),
                    MultipartRequester.fromString(ServeyFormId),
                    MultipartRequester.fromString(TicketNo),
                    campDocumentsParts);
            call.enqueue(new Callback<ModelUploadSurveyFormReport>() {
                @Override
                public void onResponse(@NonNull Call<ModelUploadSurveyFormReport> call, @NonNull Response<ModelUploadSurveyFormReport> response) {
                    Utils.hideCustomProgressDialogCommonForAll();

                    if (response.isSuccessful()) {
                        if (response.code() == 200) {
                            if (response.body() != null) {
                                if (Objects.requireNonNull(response.body()).getStatus().equals("200")) {
                                    makeToast(response.body().getMessage());
                                    finish();
                                } else {
                                    makeToast(response.body().getMessage());
                                }
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
                public void onFailure(@NonNull Call<ModelUploadSurveyFormReport> call, @NonNull Throwable error) {
                    Utils.hideCustomProgressDialogCommonForAll();

                    makeToast(getResources().getString(R.string.error));

                    call.cancel();
                }
            });
        } else {
            makeToast(getResources().getString(R.string.no_internet_connection));
        }

    }

}