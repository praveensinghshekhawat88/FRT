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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.callmangement.Network.APIService;
import com.callmangement.Network.MultipartRequester;
import com.callmangement.Network.RetrofitInstance;
import com.callmangement.R;
import com.callmangement.custom.CustomActivity;
import com.callmangement.databinding.ActivityAddnewErrorPosSeBinding;
import com.callmangement.imagepicker.model.Config;
import com.callmangement.imagepicker.model.Image;
import com.callmangement.imagepicker.ui.imagepicker.ImagePicker;
import com.callmangement.model.Pos_error.SpinnerModel;
import com.callmangement.support.ImageUtilsForRotate;
import com.callmangement.support.OnSingleClickListener;
import com.callmangement.ui.distributor.model.PosDistributionDetail;
import com.callmangement.ui.errors.model.GetErrorTypesRoot;
import com.callmangement.ui.errors.model.GetErrortypesDatum;
import com.callmangement.ui.errors.model.SaveErroeReqRoot;
import com.callmangement.utils.CompressImage;
import com.callmangement.utils.Constants;
import com.callmangement.utils.PrefManager;

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


public class AddnewErrorPosSE extends CustomActivity implements View.OnClickListener {
    private ActivityAddnewErrorPosSeBinding binding;
    private PrefManager prefManager;

    private final List<String> spinnerList = new ArrayList<>();
    private final String myFormat = "yyyy-MM-dd";
    private final int checkFilter = 0;
    private final String fromDate = "";
    private final String toDate = "";
    private final List<PosDistributionDetail> userReportList = new ArrayList<>();
    public final int REQUEST_PICK_IMAGE_ONE = 1111;
    public final int REQUEST_PICK_IMAGE_TWO = 1112;
    public final int REQUEST_PICK_IMAGE_THREE = 1113;

    ArrayList<String> stringArrayListHavingAllFilePath = new ArrayList<>();
    Activity mActivity;
    private ArrayList<SpinnerModel> goodModelArrayList;
    private final ArrayList<String> playerNames = new ArrayList<String>();
   // private Spinner spinner;
    String tv_select;
    SpinnerModel selectItem;
    String ErrorTypeId,Mobile_No;
    String selectedValue,FPS_CODE,Input_Device_Code,Input_Remark;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddnewErrorPosSeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initView();
    }
    private void initView() {
        mActivity = this;
        prefManager = new PrefManager(mContext);
        binding.actionBar.ivBack.setVisibility(View.VISIBLE);
        binding.actionBar.ivThreeDot.setVisibility(View.GONE);
        binding.actionBar.layoutLanguage.setVisibility(View.GONE);
        binding.actionBar.buttonPDF.setVisibility(View.GONE);
        binding.actionBar.textToolbarTitle.setText(getResources().getString(R.string.pos_errors));

        // spinner = findViewById(R.id.spinner);
        fetchSpinnersata();
      setUpClickListener();
       // setUpData();
    }

  /*  private void fetchSpinnersata() {
        if (Constants.isNetworkAvailable(mContext)) {
            showProgress();
            Retrofit retrofit = new Retrofit.Builder().baseUrl(APIService.JSONURL)
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .build();
            APIService api = retrofit.create(APIService.class);
            Call<String> call = api.getJSONString();
            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    Log.i("Responsestring", response.body().toString());
                    hideProgress();
                    //Toast.makeText()
                    if (response.isSuccessful()) {
                        if (response.body() != null) {
                            Log.i("onSuccess", response.body().toString());
                            String jsonresponse = response.body().toString();
                            spinJSON(jsonresponse);
                        } else {
                            Log.i("onEmptyResponse", "Returned empty response");//Toast.makeText(getContext(),"Nothing returned",Toast.LENGTH_LONG).show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    hideProgress();

                }
            });


        }
        else {
            makeToast(getResources().getString(R.string.no_internet_connection));
        }



    }

*/



    private void fetchSpinnersata()
    {
        if (Constants.isNetworkAvailable(mContext)){
            showProgress();
            APIService service = RetrofitInstance.getRetrofitInstance().create(APIService.class);
            Call<GetErrorTypesRoot> call = service.GetErrorTypes(prefManager.getUSER_Id(), "0");
            call.enqueue(new Callback<GetErrorTypesRoot>() {
                @Override
                public void onResponse(@NonNull Call<GetErrorTypesRoot> call, @NonNull Response<GetErrorTypesRoot> response) {
                    hideProgress();
                    if (response.isSuccessful()){
                        if (response.code() == 200){
                            if (response.body() != null) {
                                if (Objects.requireNonNull(response.body()).getStatus().equals("200")) {
                                    GetErrorTypesRoot getErrorTypesRoot = response.body();
                              //      Log.d("getErrorTypesRoot..","getErrorTypesRoot.."+getErrorTypesRoot);

                                    ArrayList<GetErrortypesDatum>  getErrortypesDatum =
                                            getErrorTypesRoot.getData();



                                    for (int i = 0; i < getErrortypesDatum.size(); i++){
                                        playerNames.add(getErrortypesDatum.get(i).getErrorType());
                                    }

                                    ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(AddnewErrorPosSE.this, simple_spinner_item, playerNames);
                                    spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
                                    binding.spinner.setAdapter(spinnerArrayAdapter);



                                    binding.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                                                                  @Override
                                                                                  public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                                                                       ErrorTypeId = String.valueOf(getErrortypesDatum.get(i).getErrorTypeId());
                                                                       //               Log.d("modelExpensestatuslist", " " + ErrorTypeId);

                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> adapterView) {

                                    }
                                });
                                    String msg =  getErrorTypesRoot.getMessage();
                                    String status =  getErrorTypesRoot.getStatus();

                                  /*  ArrayList<GetErrortypesDatum>  getErrortypesDatum =
                                            getErrorTypesRoot.getData();
                                    String msg =  getErrorTypesRoot.getMessage();
                                    String status =  getErrorTypesRoot.getStatus();

                                     JSONArray jsonArray = getErrortypesDatum

                                    String CreatedCampCount = getErrortypesDatum.get(Ar).;
                                    String OrganizedCampCount = dashboardUserDetails.getOrganizedCampCount();
                                    String AttendanceCount = dashboardUserDetails.getAttendanceCount();
                                    binding.textcreatedCampCount.setText(CreatedCampCount);
                                    binding.textCampsCount.setText(OrganizedCampCount);
                                    binding.textAttendanceCount.setText(AttendanceCount);*/
                                 //   Log.d("ghmn", "asmbnmldjfjas" + CreatedCampCount + "  " + OrganizedCampCount + " " + AttendanceCount);
                                } else {
                                    makeToast(String.valueOf(response.body().getMessage()));
                                }
                            } else {
                                Log.i("onEmptyResponse", "Returned empty response");//Toast.makeText(getContext(),"Nothing returned",Toast.LENGTH_LONG).show();

                            }
                        }else {
                            makeToast(getResources().getString(R.string.error));
                        }
                    } else {
                        makeToast(getResources().getString(R.string.error));
                    }
                }
                @Override
                public void onFailure(@NonNull Call<GetErrorTypesRoot> call, @NonNull Throwable t) {
                    hideProgress();
                    makeToast(getResources().getString(R.string.error_message));
                }
            });
        } else {
            makeToast(getResources().getString(R.string.no_internet_connection));
        }
    }





/*

    private void spinJSON(String response){

        try {

            JSONObject obj = new JSONObject(response);
            if(obj.optString("status").equals("true")){

                goodModelArrayList = new ArrayList<>();
                JSONArray dataArray  = obj.getJSONArray("data");

                selectItem  = new SpinnerModel();
              tv_select="--" + getResources().getString(R.string.select_errortype) + "--";
                selectItem.setId(tv_select);
                selectItem.setName(tv_select);
                goodModelArrayList.add(selectItem);


                for (int i = 0; i < dataArray.length(); i++) {

                    SpinnerModel spinnerModel = new SpinnerModel();
                    JSONObject dataobj = dataArray.getJSONObject(i);
                    spinnerModel.setId(dataobj.getString("id"));
                    spinnerModel.setName(dataobj.getString("name"));
                    spinnerModel.setCountry(dataobj.getString("country"));
                    spinnerModel.setCity(dataobj.getString("city"));
                    spinnerModel.setImgURL(dataobj.getString("imgURL"));
                    goodModelArrayList.add(spinnerModel);

                }

                for (int i = 0; i < goodModelArrayList.size(); i++){
                    playerNames.add(goodModelArrayList.get(i).getId().toString());
                }

                ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(AddnewErrorPosSE.this, simple_spinner_item, playerNames);
                spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
                binding.spinner.setAdapter(spinnerArrayAdapter);

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
*/

  private void setUpClickListener(){
     /*   binding.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (++checkFilter > 1) {
                    String item = adapterView.getItemAtPosition(i).toString();
                    if (!item.equalsIgnoreCase("--" + getResources().getString(R.string.select_filter) + "--")) {
                        Objects.requireNonNull(binding.textFromDate.getText()).clear();
                        Objects.requireNonNull(binding.textToDate.getText()).clear();
                        if (item.equalsIgnoreCase("200")) {
                            binding.layoutDateRange.setVisibility(View.GONE);
                            Calendar calendar = Calendar.getInstance();
                            Date today = calendar.getTime();
                            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                            String todayDate = sdf.format(today);
                            fromDate = todayDate;
                            toDate = todayDate;

                            getReportList(fromDate,toDate);

                        } else if (item.equalsIgnoreCase(getResources().getString(R.string.yesterday))) {
                            binding.layoutDateRange.setVisibility(View.GONE);
                            Calendar calendar = Calendar.getInstance();
                            calendar.add(Calendar.DAY_OF_YEAR, -1);
                            Date yesterday = calendar.getTime();
                            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                            String yesterdayDate = sdf.format(yesterday);
                            fromDate = yesterdayDate;
                            toDate = yesterdayDate;
                            getReportList(fromDate,toDate);

                        } else if (item.equalsIgnoreCase(getResources().getString(R.string.current_month))) {
                            binding.layoutDateRange.setVisibility(View.GONE);
                            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

                            Calendar calendar = Calendar.getInstance();
                            calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
                            calendar.set(Calendar.HOUR_OF_DAY, 0);
                            calendar.set(Calendar.MINUTE, 0);
                            calendar.set(Calendar.SECOND, 0);
                            calendar.set(Calendar.MILLISECOND, 0);
                            Date firstDateOfCurrentMonth = calendar.getTime();
                            String date1 = sdf.format(firstDateOfCurrentMonth);

                            Calendar calendar1 = Calendar.getInstance();
                            Date currentDateOfCurrentMonth = calendar1.getTime();
                            String date2 = sdf.format(currentDateOfCurrentMonth);

                            fromDate = date1;
                            toDate = date2;

                            getReportList(fromDate,toDate);

                        } else if (item.equalsIgnoreCase(getResources().getString(R.string.previous_month))) {
                            binding.layoutDateRange.setVisibility(View.GONE);
                            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                            Calendar aCalendar = Calendar.getInstance();
                            aCalendar.add(Calendar.MONTH, -1);
                            aCalendar.set(Calendar.DATE, 1);
                            Date firstDateOfPreviousMonth = aCalendar.getTime();
                            String date1 = sdf.format(firstDateOfPreviousMonth);
                            aCalendar.set(Calendar.DATE, aCalendar.getActualMaximum(Calendar.DAY_OF_MONTH));
                            Date lastDateOfPreviousMonth = aCalendar.getTime();
                            String date2 = sdf.format(lastDateOfPreviousMonth);
                            fromDate = date1;
                            toDate = date2;
                            getReportList(fromDate,toDate);
                        } else if (item.equalsIgnoreCase(getResources().getString(R.string.custom_filter))) {
                            binding.layoutDateRange.setVisibility(View.VISIBLE);
                        }
                    } else {
                        fromDate = "";
                        toDate = "";
                        Objects.requireNonNull(binding.textFromDate.getText()).clear();
                        Objects.requireNonNull(binding.textToDate.getText()).clear();
                        binding.layoutDateRange.setVisibility(View.GONE);
                        getReportList(fromDate,toDate);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
*/
     /*   binding.inputFpsCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!charSequence.toString().isEmpty()){
                    List<PosDistributionDetail> filterList = new ArrayList<>();
                    if (userReportList.size() > 0){
                        for (PosDistributionDetail model : userReportList){
                            if (model.getFpscode().contains(charSequence.toString()))
                                filterList.add(model);
                        }
                    }
                  //  setAdapter(filterList);
                }//else setAdapter(userReportList);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
*/
   /*  binding.inputTicketNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!charSequence.toString().isEmpty()){
                    List<PosDistributionDetail> filterList = new ArrayList<>();
                    if (userReportList.size() > 0){
                        for (PosDistributionDetail model : userReportList){
                            if (model.getTicketNo().contains(charSequence.toString()))
                                filterList.add(model);
                        }
                    }
                  //  setAdapter(filterList);
                }//else setAdapter(userReportList);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        binding.actionBar.ivBack.setOnClickListener(this);
        binding.textFromDate.setOnClickListener(this);
        binding.textToDate.setOnClickListener(this);*/

      binding.actionBar.ivBack.setOnClickListener(this);
    //  binding.textFromDate.setOnClickListener(this);
     // binding.textToDate.setOnClickListener(this);
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

        binding.buttonUploadError.setOnClickListener(view -> {
            selectedValue = binding.spinner.getSelectedItem().toString();

            FPS_CODE = binding.inputFpsCode.getText().toString();
           Input_Device_Code = binding.inputDeviceCode.getText().toString();
            Input_Remark = binding.inputRemark.getText().toString();
            Mobile_No = binding.inputMobileno.getText().toString();

            Log.d("Mobile_No"," "+ Mobile_No);


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

            else if (Mobile_No==null || Mobile_No.isEmpty() || Mobile_No.length()!=10)
                    {
                        makeToast(getResources().getString(R.string.enter_your_exact_mobile_no));

                    }


         else if (stringArrayListHavingAllFilePath == null || stringArrayListHavingAllFilePath.size()  <= 0  ) {
               makeToast(getResources().getString(R.string.please_select_atleast_one_img));
              }
            else {
                uploadImages(FPS_CODE, Input_Device_Code, Input_Remark, stringArrayListHavingAllFilePath);
                Log.d("response","FPS response--------------"+FPS_CODE+" "+Input_Device_Code+" "+Input_Remark);
                Log.d("response","response--------------"+FPS_CODE+" "+selectedValue+" "+Input_Device_Code+" "+Input_Remark+" "+stringArrayListHavingAllFilePath+" "+
                        stringArrayListHavingAllFilePath.size());
            }

        });
    }
  /*  @SuppressLint("SetTextI18n")
    private void setAdapter(List<PosDistributionDetail> list){
        if (list.size() > 0){
            binding.recyclerView.setVisibility(View.VISIBLE);
            binding.tvNoDataFound.setVisibility(View.GONE);
            binding.recyclerView.setLayoutManager(new LinearLayoutManager(mContext,LinearLayoutManager.VERTICAL,false));
            binding.recyclerView.setAdapter(new DistributorPosReportForSEActivityAdapter(mContext,list));
            binding.textTotalCount.setText(""+list.size());
        } else {
            binding.recyclerView.setVisibility(View.GONE);
            binding.tvNoDataFound.setVisibility(View.VISIBLE);
            binding.textTotalCount.setText("0");
        }
    }*/
   @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.iv_back) {
          //  onBackPressed();
            Intent i =new Intent(AddnewErrorPosSE.this, ErrorPosActivity.class);
            startActivity(i);
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
                imageStoragePath = CompressImage .compress(imageStoragePath, this);
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



/*
    private void getReportList(String fromDate, String toDate) {
        if (Constants.isNetworkAvailable(mContext)) {
            showProgress();
            APIService service = RetrofitInstance.getRetrofitInstance().create(APIService.class);
            Call<ResponseBody> call = service.getPosDistributionListSEAPI(prefManager.getUSER_DistrictId(), prefManager.getUSER_Id(), "0","","",fromDate,toDate);
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
                                    JSONObject jsonObject = new JSONObject((responseStr));
                                    String status = jsonObject.optString("status");
                                    JSONArray distributionListJsonArr = jsonObject.optJSONArray("posDistributionDetailList");
                                    if (distributionListJsonArr != null) {
                                        PosDistributionListResponse modelResponse = (PosDistributionListResponse) getObject(responseStr, PosDistributionListResponse.class);
                                        if (modelResponse != null){
                                            if (status.equals("200")) {
                                                if (modelResponse.getPosDistributionDetailList().size() > 0) {
                                                    userReportList.clear();
                                                    userReportList.addAll(modelResponse.getPosDistributionDetailList());
                                                    //   setAdapter(userReportList);
                                                } else {
                                                    //    binding.recyclerView.setVisibility(View.GONE);
                                                    //   binding.tvNoDataFound.setVisibility(View.VISIBLE);
                                                }
                                            } else {
                                                //    binding.recyclerView.setVisibility(View.GONE);
                                                //   binding.tvNoDataFound.setVisibility(View.VISIBLE);
                                            }
                                        } else {
                                            //      binding.recyclerView.setVisibility(View.GONE);
                                            //  binding.tvNoDataFound.setVisibility(View.VISIBLE);
                                        }
                                    } else {
                                        //      binding.recyclerView.setVisibility(View.GONE);
                                        //  binding.tvNoDataFound.setVisibility(View.VISIBLE);
                                    }
                                } else {
                                    //    binding.recyclerView.setVisibility(View.GONE);
                                    //  binding.tvNoDataFound.setVisibility(View.VISIBLE);
                                }
                            } else {
                                //    binding.recyclerView.setVisibility(View.GONE);
                                //  binding.tvNoDataFound.setVisibility(View.VISIBLE);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            //   binding.recyclerView.setVisibility(View.GONE);
                            //  binding.tvNoDataFound.setVisibility(View.VISIBLE);
                        }
                    } else {
                        makeToast(getResources().getString(R.string.error));
                        //    binding.recyclerView.setVisibility(View.GONE);
                        //    binding.tvNoDataFound.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                    hideProgress();
                    makeToast(getResources().getString(R.string.error_message));
                    //   binding.recyclerView.setVisibility(View.GONE);
                    //  binding.tvNoDataFound.setVisibility(View.VISIBLE);
                }
            });
        } else {
            makeToast(getResources().getString(R.string.no_internet_connection));
        }
    }*/


  private void uploadImages(String FPS_CODE, String Input_Device_Code, String Input_Remark, ArrayList<String> arrayHavingAllFilePath) {

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


            String  ErrorStatusId ="1";

            Call<SaveErroeReqRoot> call = service.saveErroeReqApi(
                    MultipartRequester.fromString((prefManager.getUSER_Id())),
                    MultipartRequester.fromString(FPS_CODE),
                    MultipartRequester.fromString(Input_Device_Code),
                    MultipartRequester.fromString(Mobile_No),
                 /*   MultipartRequester.fromString(ErrorTypeId),
                    MultipartRequester.fromString(ErrorStatusId),  */
                    MultipartRequester.fromString(ErrorTypeId),
                    MultipartRequester.fromString(""),
                    MultipartRequester.fromString(Input_Remark),
                    campDocumentsParts);

            Log.d("errorsaveresponse","response-----"+prefManager.getUSER_Id()+"  "+FPS_CODE+" "+Input_Device_Code+" "+Input_Remark+" " +campDocumentsParts);
            call.enqueue(new Callback<SaveErroeReqRoot>() {
                @Override
                public void onResponse(@NonNull Call<SaveErroeReqRoot> call, @NonNull Response<SaveErroeReqRoot> response) {
                    hideProgress();

                    if (response.isSuccessful()) {
                        if (response.code() == 200 && response.body() != null ) {

                               // if (Objects.requireNonNull(response.body()).getStatus().equals("200")) {
                                if (Objects.requireNonNull(response.body()!=null)) {

                                    SaveErroeReqRoot getErrorTypesRoot = response.body();

                                    String status =getErrorTypesRoot.getStatus();

                                    if(status.equals("200"))
                                    {
                                        boolean checkstatus=getErrorTypesRoot.getResponse().status;
                                        if (checkstatus)

                                        {
                                            Log.d("checkstatus","checkstatus"+checkstatus);

                                            Intent i =new Intent(AddnewErrorPosSE.this, ErrorPosActivity.class);
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

                                    Log.d("getErrorTypesRoot..","getErrorTypesRoot.."+getErrorTypesRoot);


                                 //   makeToast(response.body().getResponse().getMessage());




                                    Log.d(String.valueOf(AddnewErrorPosSE.this),"fggafdaf"+getErrorTypesRoot);

                                // makeToast(response.body().getMessage());
                                   // finish();
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
                public void onFailure(@NonNull Call<SaveErroeReqRoot> call, @NonNull Throwable error) {
                    Log.d("responseerror","response-----"+error);

                    hideProgress();
                    makeToast(getResources().getString(R.string.error));
                    call.cancel();
                }
            });
        } else {
            makeToast(getResources().getString(R.string.no_internet_connection));
        }

    }





}