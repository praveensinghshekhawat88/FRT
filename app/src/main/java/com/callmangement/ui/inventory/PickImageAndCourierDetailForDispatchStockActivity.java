package com.callmangement.ui.inventory;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.callmangement.Network.APIService;
import com.callmangement.Network.MultipartRequester;
import com.callmangement.Network.RetrofitInstance;
import com.callmangement.R;
import com.callmangement.adapter.DialogChallanForDispatchAdapter;
import com.callmangement.custom.CustomActivity;
import com.callmangement.databinding.ActivityPickImageAndCourierDetailForDispatchStockBinding;
import com.callmangement.imagepicker.model.Config;
import com.callmangement.imagepicker.model.Image;
import com.callmangement.imagepicker.ui.imagepicker.ImagePicker;
import com.callmangement.model.inventrory.ModelPartsList;
import com.callmangement.model.inventrory.ModelSavePartsDispatchDetails;
import com.callmangement.model.tehsil.ModelTehsilList;
import com.callmangement.report_pdf.ChallanPDFActivity;
import com.callmangement.support.ImageUtilsForRotate;
import com.callmangement.utils.CompressImage;
import com.callmangement.utils.Constants;
import com.callmangement.utils.PrefManager;
import com.callmangement.support.dexter.Dexter;
import com.callmangement.support.dexter.MultiplePermissionsReport;
import com.callmangement.support.dexter.PermissionToken;
import com.callmangement.support.dexter.listener.PermissionRequest;
import com.callmangement.support.dexter.listener.multi.MultiplePermissionsListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PickImageAndCourierDetailForDispatchStockActivity extends CustomActivity implements View.OnClickListener {
    private ActivityPickImageAndCourierDetailForDispatchStockBinding binding;
    private InventoryViewModel inventoryViewModel;
    private List<ModelPartsList> dispatchList = new ArrayList<>();
    private PrefManager prefManager;
    private String challanImageStoragePath = "";
    private String partsImageStoragePath1 = "";
    private String partsImageStoragePath2 = "";
    private boolean permissionGranted;
    public final int REQUEST_PICK_CHALLAN_IMAGES = 1113;
    public final int REQUEST_PICK_PARTS_IMAGES1 = 1114;
    public final int REQUEST_PICK_PARTS_IMAGES2 = 1115;
    private String districtNameEng = "";
    private String seUserName = "";
    private String userTypeName = "";
    private String dispatchId = "0";
    private final String invoiceId = "0";
    private String seUserId = "0";
    private String districtId = "0";
    private final String stockStatusId = "3";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPickImageAndCourierDetailForDispatchStockBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initView();
    }

    private void initView() {
        binding.actionBar.ivThreeDot.setVisibility(View.GONE);
        binding.actionBar.ivBack.setVisibility(View.VISIBLE);
        binding.actionBar.layoutLanguage.setVisibility(View.GONE);
        binding.actionBar.buttonPDF.setVisibility(View.GONE);
        binding.actionBar.textToolbarTitle.setText(getResources().getString(R.string.create_dispatch_stock));
        prefManager = new PrefManager(mContext);
        inventoryViewModel = ViewModelProviders.of(this).get(InventoryViewModel.class);
        checkPermission();
        setUpClickListener();
        getIntentData();
    }

    private void getIntentData(){
        dispatchList = (List<ModelPartsList>) getIntent().getSerializableExtra("dispatchList");
        districtNameEng = getIntent().getStringExtra("districtNameEng");
        seUserName = getIntent().getStringExtra("seUserName");
        dispatchId = getIntent().getStringExtra("dispatchId");
        seUserId = getIntent().getStringExtra("seUserId");
        districtId = getIntent().getStringExtra("districtId");
        userTypeName = getIntent().getStringExtra("userTypeName");
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
        binding.buttonCreateChallan.setOnClickListener(this);
        binding.ivChallanImage.setOnClickListener(this);
        binding.ivPartsImage1.setOnClickListener(this);
        binding.ivPartsImage2.setOnClickListener(this);
        binding.actionBar.ivBack.setOnClickListener(this);
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

    private void selectPartsImage1() {
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
                            .start(REQUEST_PICK_PARTS_IMAGES1);

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
                            .start(REQUEST_PICK_PARTS_IMAGES1);
                } else if (items[item].equals(getResources().getString(R.string.imagepicker_str_cancel))) {
                    dialog.dismiss();
                }
            });
            builder.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void selectPartsImage2() {
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
                            .start(REQUEST_PICK_PARTS_IMAGES2);

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
                            .start(REQUEST_PICK_PARTS_IMAGES2);
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
                    binding.ivChallanImage.setImageBitmap(ImageUtilsForRotate.ensurePortrait(challanImageStoragePath));
                } catch (IOException e) {
                    binding.ivChallanImage.setImageBitmap(myBitmap);
                    e.printStackTrace();
                }catch (NullPointerException e){
                    e.printStackTrace();
                }
            }
        } else if (requestCode == REQUEST_PICK_PARTS_IMAGES1 && resultCode == RESULT_OK && data != null) {
            ArrayList<Image> images = data.getParcelableArrayListExtra(Config.EXTRA_IMAGES);
            if (images != null && images.size() > 0) {
                Image image = images.get(0);
                partsImageStoragePath1 = image.getPath();
                if (partsImageStoragePath1.contains("file:/")) {
                    partsImageStoragePath1 = partsImageStoragePath1.replace("file:/", "");
                }
                partsImageStoragePath1 = CompressImage.compress(partsImageStoragePath1, this);
                File imgFile = new  File(partsImageStoragePath1);
                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());

                try {
                    binding.ivPartsImage1.setImageBitmap(ImageUtilsForRotate.ensurePortrait(partsImageStoragePath1));
                } catch (IOException e) {
                    binding.ivPartsImage1.setImageBitmap(myBitmap);
                    e.printStackTrace();
                }catch (NullPointerException e){
                    e.printStackTrace();
                }
            }
        } else if (requestCode == REQUEST_PICK_PARTS_IMAGES2 && resultCode == RESULT_OK && data != null) {
            ArrayList<Image> images = data.getParcelableArrayListExtra(Config.EXTRA_IMAGES);
            if (images != null && images.size() > 0) {
                Image image = images.get(0);
                partsImageStoragePath2 = image.getPath();
                if (partsImageStoragePath2.contains("file:/")) {
                    partsImageStoragePath2 = partsImageStoragePath2.replace("file:/", "");
                }
                partsImageStoragePath2 = CompressImage.compress(partsImageStoragePath2, this);
                File imgFile = new  File(partsImageStoragePath2);
                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());

                try {
                    binding.ivPartsImage2.setImageBitmap(ImageUtilsForRotate.ensurePortrait(partsImageStoragePath2));
                } catch (IOException e) {
                    binding.ivPartsImage2.setImageBitmap(myBitmap);
                    e.printStackTrace();
                }catch (NullPointerException e){
                    e.printStackTrace();
                }

            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void createChallan(List<ModelPartsList> dispatchList){
        if (Constants.isNetworkAvailable(mContext)) {
            if (!userTypeName.equalsIgnoreCase("ServiceCentre")) {
                if (partsImageStoragePath1.isEmpty()) {
                    makeToast(getResources().getString(R.string.please_select_parts_image1));
                } else if (partsImageStoragePath2.isEmpty()) {
                    makeToast(getResources().getString(R.string.please_select_parts_image2));
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setMessage(getResources().getString(R.string.are_you_sure_you_want_to_create_challan))
                            .setCancelable(false)
                            .setPositiveButton(getResources().getString(R.string.yes), (dlg, ids) -> {
                                dlg.dismiss();
                                dialogDispatchChallan(dispatchList);
                            })
                            .setNegativeButton(getResources().getString(R.string.no), (dlg, ids) -> dlg.dismiss());
                    AlertDialog alert = builder.create();
                    alert.setTitle(getResources().getString(R.string.alert));
                    alert.show();
                }
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setMessage(getResources().getString(R.string.are_you_sure_you_want_to_create_challan))
                        .setCancelable(false)
                        .setPositiveButton(getResources().getString(R.string.yes), (dlg, ids) -> {
                            dlg.dismiss();
                            dialogDispatchChallan(dispatchList);
                        })
                        .setNegativeButton(getResources().getString(R.string.no), (dlg, ids) -> dlg.dismiss());
                AlertDialog alert = builder.create();
                alert.setTitle(getResources().getString(R.string.alert));
                alert.show();
            }

        } else makeToast(getResources().getString(R.string.no_internet_connection));
    }

    @SuppressLint("SetTextI18n")
    private void dialogDispatchChallan(List<ModelPartsList> partsList) {
        try {
            final Dialog dialog = new Dialog(mContext);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.dialog_challan_for_dispatch);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.setCanceledOnTouchOutside(false);

//            TextView invoice_id = dialog.findViewById(R.id.textInvoiceNumber);
            TextView dispatchFrom = dialog.findViewById(R.id.textDispatchFrom);
            TextView textDispatcherEmail = dialog.findViewById(R.id.textDispatcherEmail);
            TextView dispatchTo = dialog.findViewById(R.id.textDispatchTo);
            TextView username = dialog.findViewById(R.id.textUsername);
            RecyclerView rvDispatchChallan = dialog.findViewById(R.id.rv_dispatch_challan);
            Button buttonCancel = dialog.findViewById(R.id.buttonCancel);
            Button buttonDispatch = dialog.findViewById(R.id.buttonDispatch);

//            invoice_id.setText(invoiceId);
            dispatchFrom.setText(prefManager.getUSER_NAME());
            textDispatcherEmail.setText(prefManager.getUSER_EMAIL());
            dispatchTo.setText(districtNameEng);
            username.setText(seUserName);

            rvDispatchChallan.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
            rvDispatchChallan.setAdapter(new DialogChallanForDispatchAdapter(mContext, partsList));

            buttonCancel.setOnClickListener(view -> dialog.dismiss());

            buttonDispatch.setOnClickListener(view -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setMessage(getResources().getString(R.string.are_you_sure_you_want_to_dispatch_this_invoice))
                        .setCancelable(false)
                        .setPositiveButton(getResources().getString(R.string.yes), (dlg, id) -> {
                            dialog.dismiss();
                            dlg.dismiss();
                            createDispatchJSONArray(partsList);
                        })
                        .setNegativeButton(getResources().getString(R.string.no), (dlg, id) -> dlg.dismiss());
                AlertDialog alert = builder.create();
                alert.setTitle(getResources().getString(R.string.alert));
                alert.show();
            });

            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createDispatchJSONArray(List<ModelPartsList> dispatchList) {
        try {
            JSONArray jsonArray = new JSONArray();
            for (ModelPartsList model : dispatchList) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("DispatchId", dispatchId);
                jsonObject.put("InvoiceId", invoiceId);
                jsonObject.put("ItemId", model.getItemId());
                jsonObject.put("Item_Qty", model.getQuantity());
                jsonArray.put(jsonObject);
            }
            savePartsDispatchDetails(dispatchList, jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void savePartsDispatchDetails(List<ModelPartsList> dispatchList, JSONArray jsonArray) {
        RequestBody attachment;
        RequestBody attachmentPartsImage1;
        RequestBody attachmentPartsImage2;

        String fileName = "";
        String fileNamePartsImage1 = "";
        String fileNamePartsImage2 = "";

        if (!challanImageStoragePath.equals("")) {
            fileName = new File(challanImageStoragePath).getName();
            attachment = RequestBody.create(MediaType.parse("multipart/form-data"), new File(challanImageStoragePath));
        } else {
            fileName = "";
            attachment = RequestBody.create(MediaType.parse("text/plain"), "");
        }

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

        String courierTrackingNumber = Objects.requireNonNull(binding.inputCourierTrackingNo.getText()).toString().trim();
        String courierName = Objects.requireNonNull(binding.inputCourierName.getText()).toString().trim();

        APIService service = RetrofitInstance.getRetrofitInstance().create(APIService.class);
        Call<ModelSavePartsDispatchDetails> call = service.savePartsDispatchDetails(
                MultipartRequester.fromString(invoiceId),
                MultipartRequester.fromString(prefManager.getUSER_Id()),
                MultipartRequester.fromString(seUserId),
                MultipartRequester.fromString(districtId),
                MultipartRequester.fromString(""),
                MultipartRequester.fromString(""),
                MultipartRequester.fromString(stockStatusId),
                MultipartRequester.fromString(courierName),
                MultipartRequester.fromString(courierTrackingNumber),
                MultipartRequester.fromString(String.valueOf(jsonArray)),
                MultipartBody.Part.createFormData("DispChalImage", fileName, attachment),
                MultipartBody.Part.createFormData("PartsImage_1", fileNamePartsImage1, attachmentPartsImage1),
                MultipartBody.Part.createFormData("PartsImage_2", fileNamePartsImage2, attachmentPartsImage2));
        showProgress();
        call.enqueue(new Callback<ModelSavePartsDispatchDetails>() {
            @Override
            public void onResponse(@NonNull Call<ModelSavePartsDispatchDetails> call, @NonNull Response<ModelSavePartsDispatchDetails> response) {
                hideProgress();
                if (response.isSuccessful()) {
                    if (response.code() == 200) {
                        ModelSavePartsDispatchDetails model = response.body();
                        if (Objects.requireNonNull(model).getStatus().equals("200")) {
                            submitDispatchParts(dispatchList, model.getModelPartsDispatchInvoiceList().getInvoiceId(), courierName, courierTrackingNumber);
                        } else {
                            makeToast(model.getMessage());
                        }
                    }
                } else {
                    makeToast(getResources().getString(R.string.error));
                }
            }
            @Override
            public void onFailure(@NonNull Call<ModelSavePartsDispatchDetails> call, @NonNull Throwable t) {
                hideProgress();
                makeToast(getResources().getString(R.string.error_message));
            }
        });
    }

    private void submitDispatchParts(List<ModelPartsList> partsList, String invoiceId, String courierName, String courierTrackingNo) {
        if (Constants.isNetworkAvailable(mContext)) {
            isLoading();
            inventoryViewModel.submitDispatchParts(prefManager.getUSER_Id(), invoiceId, "").observe(this, modelSavePartsDispatchDetails -> {
                isLoading();
                if (modelSavePartsDispatchDetails.getStatus().equals("200")) {
                    Constants.modelPartsList = partsList;
                    startActivity(new Intent(mContext, ChallanPDFActivity.class)
                            .putExtra("invoiceId", invoiceId)
                            .putExtra("dispatchFrom", prefManager.getUSER_NAME())
                            .putExtra("email", prefManager.getUSER_EMAIL())
                            .putExtra("dispatchTo", districtNameEng)
                            .putExtra("username", seUserName)
                            .putExtra("datetime", modelSavePartsDispatchDetails.getModelPartsDispatchInvoiceList().getDispatchDateStr())
                            .putExtra("courierName", courierName)
                            .putExtra("courierTrackingNo", courierTrackingNo)
                            .putExtra(Constants.fromWhere, "CreateNewChallanDispatchActivity"));
                    finish();
                }
            });
        } else {
            makeToast(getResources().getString(R.string.no_internet_connection));
        }
    }

    private void isLoading() {
        inventoryViewModel.getIsLoading().observe(this, aBoolean -> {
            if (aBoolean) {
                showProgress(getResources().getString(R.string.please_wait));
            } else {
                hideProgress();
            }
        });
    }

    private boolean getSelectItemHaveValue(List<ModelPartsList> dispatchList) {
        boolean flag = false;
        for (ModelPartsList model : dispatchList) {
            if (Integer.parseInt(model.getQuantity()) > 0)
                flag = true;
            else {
                flag = false;
                break;
            }
        }
        return flag;
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.buttonCreateChallan) {
            createChallan(dispatchList);

        } else if (id == R.id.ivChallanImage){
            if (permissionGranted) {
                selectImage();
            } else {
                makeToast(getResources().getString(R.string.please_allow_all_permission));
            }
        } else if (id == R.id.ivPartsImage1){
            if (permissionGranted) {
                selectPartsImage1();
            } else {
                makeToast(getResources().getString(R.string.please_allow_all_permission));
            }
        } else if (id == R.id.ivPartsImage2){
            if (permissionGranted) {
                selectPartsImage2();
            } else {
                makeToast(getResources().getString(R.string.please_allow_all_permission));
            }
        } else if (id == R.id.iv_back) {
            onBackPressed();
        }
    }
}