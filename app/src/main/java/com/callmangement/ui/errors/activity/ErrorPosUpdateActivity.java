package com.callmangement.ui.errors.activity;

import static android.R.layout.simple_spinner_item;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.callmangement.Network.APIService;
import com.callmangement.Network.MultipartRequester;
import com.callmangement.Network.RetrofitInstance;
import com.callmangement.R;
import com.callmangement.custom.CustomActivity;
import com.callmangement.databinding.ActivityErrorrPosUpdateBinding;
import com.callmangement.imagepicker.model.Config;
import com.callmangement.imagepicker.model.Image;
import com.callmangement.imagepicker.ui.imagepicker.ImagePicker;
import com.callmangement.support.ImageUtilsForRotate;
import com.callmangement.support.OnSingleClickListener;
import com.callmangement.ui.errors.model.GetErrorTypesRoot;
import com.callmangement.ui.errors.model.GetErrortypesDatum;
import com.callmangement.ui.errors.model.GetPosDeviceErrorDatum;
import com.callmangement.ui.errors.model.SaveErroeReqRoot;
import com.callmangement.ui.errors.model.UpdateErroeReqRoot;
import com.callmangement.ui.home.MainActivity;
import com.callmangement.utils.CompressImage;
import com.callmangement.utils.Constants;
import com.callmangement.utils.PrefManager;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ErrorPosUpdateActivity extends CustomActivity implements View.OnClickListener {
    private ActivityErrorrPosUpdateBinding binding;
    //private ModelExpensesList model;
    private GetPosDeviceErrorDatum model;

    private PrefManager prefManager;
    private final ArrayList<String> playerNames = new ArrayList<String>();
    String selectedValue,FPS_CODE,Input_Device_Code,Input_Remark,inputMobileno,ErrorTypeId;
    ArrayList<String> stringArrayListHavingAllFilePath = new ArrayList<>();
    Activity mActivity;

    public final int REQUEST_PICK_IMAGE_ONE = 1111;
    public final int REQUEST_PICK_IMAGE_TWO = 1112;
    public final int REQUEST_PICK_IMAGE_THREE = 1113;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityErrorrPosUpdateBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initView();
    }

    private void initView() {
        mActivity = this;
        prefManager = new PrefManager(mContext);
        binding.actionBar.ivBack.setVisibility(View.VISIBLE);
        binding.actionBar.ivThreeDot.setVisibility(View.GONE);
        binding.actionBar.layoutLanguage.setVisibility(View.GONE);
        binding.actionBar.textToolbarTitle.setText(getResources().getString(R.string.updateerror));

        setUpOnClickListener();
        getIntentData();
        setUpData();
    }

    private void setUpOnClickListener() {


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






        binding.actionBar.ivBack.setOnClickListener(this);
   //  binding.ivChallanImage.setOnClickListener(this);
        binding.buttonUploadError.setOnClickListener(this);
    }

    private void getIntentData() {
        model = (GetPosDeviceErrorDatum) getIntent().getSerializableExtra("param");
    }

    private void setUpData(){
     String dealername =   model.getDealerName();

        if (dealername!=null){
            binding.inputDealerName.setText(dealername);
        }

        if (model.getDealerMobileNo()!=null){
            binding.inputMobileno.setText(model.getDealerMobileNo());
        }

        if (model.getDistrictNameEng()!=null){
            binding.inputDistrict.setText(model.getDistrictNameEng());
        }

       ArrayList<String> myvalue = new ArrayList<String>();


        myvalue.add(model.getErrorType());


        binding.spinner.setText(model.getErrorType());


        if (model.getFpscode()!=null)
        {
            binding.inputFpsCode.setText(model.getFpscode());
        }
        if (model.getRemark()!=null){
            binding.inputRemark.setText(model.getRemark());

        }
        if (model.getDeviceCode()!=null){
            binding.inputDeviceCode.setText(String.valueOf(model.getDeviceCode()));

        }



        if (prefManager.getUSER_TYPE_ID().equals("1") && prefManager.getUSER_TYPE().equalsIgnoreCase("Admin")) {
            binding.buttonUploadError.setVisibility(View.GONE);
        } else if (prefManager.getUSER_TYPE_ID().equals("2") && prefManager.getUSER_TYPE().equalsIgnoreCase("Manager")) {
            binding.buttonUploadError.setVisibility(View.GONE);
        } else {


            if (model.getErrorStatus().equals("Pending") ) {
                binding.buttonUploadError.setVisibility(View.VISIBLE);


            } else if (model.getErrorStatus().equals("Checking")) {
                binding.buttonUploadError.setVisibility(View.VISIBLE);



            }
            else if (model.getErrorStatus().equals("Resolved")) {
                binding.buttonUploadError.setVisibility(View.GONE);



            }








        }

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









    private void updateerror(String FPS_CODE, String Input_Device_Code, String Input_Remark, ArrayList<String> arrayHavingAllFilePath) {

        if (Constants.isNetworkAvailable(mContext)) {
            showProgress();
            APIService service = RetrofitInstance.getRetrofitInstance().create(APIService.class);
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


            String  ErrorStatusIdd = String.valueOf(model.getErrorStatusId());
            String  ErrorId = String.valueOf(model.getErrorId());

            Call<UpdateErroeReqRoot> call = service.UpdateErroeReqApi(
                    MultipartRequester.fromString(ErrorId),

                    MultipartRequester.fromString((prefManager.getUSER_Id())),
                    MultipartRequester.fromString(FPS_CODE),
                    MultipartRequester.fromString(Input_Device_Code),
                    MultipartRequester.fromString(inputMobileno),
                 /*   MultipartRequester.fromString(ErrorTypeId),
                    MultipartRequester.fromString(ErrorStatusId),  */
                    MultipartRequester.fromString(ErrorTypeId),
                    MultipartRequester.fromString(ErrorStatusIdd),
                    MultipartRequester.fromString(Input_Remark),
                    campDocumentsParts);
           // Log.d("errorsaveresponse","response-----"+ErrorId+" "+ErrorStatusIdd+"  " +ErrorTypeId+" "+prefManager.getUSER_Id()+"  "+FPS_CODE+" "+Input_Device_Code+" "+Input_Remark+" " +campDocumentsParts);
            call.enqueue(new Callback<UpdateErroeReqRoot>() {
                @Override
                public void onResponse(@NonNull Call<UpdateErroeReqRoot> call, @NonNull Response<UpdateErroeReqRoot> response) {
                    hideProgress();
                    if (response.isSuccessful()) {
                        if (response.code() == 200 && response.body() != null ) {
                         //   Toast.makeText(ErrorPosUpdateActivity.this, "susesss", Toast.LENGTH_SHORT).show();

                            // if (Objects.requireNonNull(response.body()).getStatus().equals("200")) {
                            if (Objects.requireNonNull(response.body()!=null)) {

                                UpdateErroeReqRoot getErrorTypesRoot = response.body();

                                String status =getErrorTypesRoot.getStatus();

                                if(status.equals("200"))
                                {
                                    boolean checkstatus=getErrorTypesRoot.getResponse().status;
                                    if (checkstatus)

                                    {
                                        makeToast(response.body().getResponse().getMessage());
                                     //   Log.d("checkstatus","checkstatus"+checkstatus);
                                        Intent i =new Intent(ErrorPosUpdateActivity.this, ErrorPosActivity.class);
                                        startActivity(i);
                                        makeToast(response.body().getResponse().getMessage());
                                    }
                                    else
                                    {
                                        makeToast(response.body().getResponse().getMessage());
                                    }
                                }
                                else {
                                    makeToast(response.body().getResponse().getMessage());


                                }

                          //      Log.d("getErrorTypesRoot..","getErrorTypesRoot.."+getErrorTypesRoot);


                                //   makeToast(response.body().getResponse().getMessage());




                              //  Log.d(String.valueOf(ErrorPosUpdateActivity.this),"fggafdaf"+getErrorTypesRoot);

                                // makeToast(response.body().getMessage());
                                // finish();
                            } else {
                                makeToast(getResources().getString(R.string.error));

                            }

                        } else {
                            makeToast(getResources().getString(R.string.error));
                        }
                    } else {
                      //  makeToast(getResources().getString(R.string.error));
                    }
                }

                @Override
                public void onFailure(@NonNull Call<UpdateErroeReqRoot> call, @NonNull Throwable error) {
                //    Log.d("responseerror","response-----"+error);

                    hideProgress();
                    makeToast(getResources().getString(R.string.error));
                    call.cancel();
                }
            });
        } else {
            makeToast(getResources().getString(R.string.no_internet_connection));
        }

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






    private void completeExpense() {
        if (Constants.isNetworkAvailable(mContext)) {
            showProgress();
            APIService service = RetrofitInstance.getRetrofitInstance().create(APIService.class);
            Call<ResponseBody> call = service.completeExpenses(prefManager.getUSER_Id(), String.valueOf(model.getErrorId()));
            call.enqueue(new Callback<ResponseBody>() {
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
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.iv_back){
            onBackPressed();
       // } else if (id == R.id.ivChallanImage){
         //   startActivity(new Intent(mContext, ZoomInZoomOutActivity.class).putExtra("image", Constants.API_BASE_URL+""+model.getFilePath()));
        } else if (id == R.id.buttonComplete) {
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setMessage(getResources().getString(R.string.complete_expense_dialog_msg))
                    .setCancelable(false)
                    .setPositiveButton(getResources().getString(R.string.ok), (dialog, ids) -> {
                        dialog.cancel();
                        completeExpense();
                    })
                    .setNegativeButton(getResources().getString(R.string.cancel), (dialog, ids) -> dialog.cancel());
            AlertDialog alert = builder.create();
            alert.setTitle(getResources().getString(R.string.alert));
            alert.show();
        }  else if (id==R.id.buttonUploadError)
        {
          //  selectedValue = binding.spinner.getSelectedItem().toString();
         //   Log.d("selectedValue", " "+selectedValue);

            FPS_CODE = binding.inputFpsCode.getText().toString();
            Input_Device_Code = binding.inputDeviceCode.getText().toString();
            inputMobileno = binding.inputMobileno.getText().toString();
            ErrorTypeId = String.valueOf(model.getErrorTypeId());


            Input_Remark = binding.inputRemark.getText().toString();

             /*       if (selectItem == null || selectedValue.equals(tv_select) || selectedValue.isEmpty() || selectedValue.length() == 0) {
                        makeToast(getResources().getString(R.string.select_errortype));
                    }
                else*/  if ( FPS_CODE==null || FPS_CODE.isEmpty() || FPS_CODE.length()==0 )
        {
            makeToast(getResources().getString(R.string.enter_fps_code));

        }
        else if(Input_Remark == null || Input_Remark.isEmpty() || Input_Remark.length()==0)
        {
            makeToast(getResources().getString(R.string.please_enter_remark));
        }
         /*  else if (stringArrayListHavingAllFilePath == null || stringArrayListHavingAllFilePath.size()  <= 0  ) {
               makeToast(getResources().getString(R.string.please_select_atleast_one_img));
              }*/
        else {
            updateerror(FPS_CODE, Input_Device_Code, Input_Remark, stringArrayListHavingAllFilePath);
            Log.d("response","FPS response--------------"+FPS_CODE+" "+Input_Device_Code+" "+Input_Remark);
          //  Log.d("response","response--------------"+FPS_CODE+" "+selectedValue+" "+Input_Device_Code+" "+Input_Remark+" "+stringArrayListHavingAllFilePath+" "+
                 //   stringArrayListHavingAllFilePath.size());
        }
        }
    }
}