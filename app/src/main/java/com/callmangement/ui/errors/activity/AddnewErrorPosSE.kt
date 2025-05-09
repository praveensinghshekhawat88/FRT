package com.callmangement.ui.errors.activity

import android.R.layout
import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.callmangement.R
import com.callmangement.custom.CustomActivity
import com.callmangement.databinding.ActivityAddnewErrorPosSeBinding
import com.callmangement.imagepicker.model.Config
import com.callmangement.imagepicker.model.Image
import com.callmangement.imagepicker.ui.imagepicker.ImagePicker
import com.callmangement.model.Pos_error.SpinnerModel
import com.callmangement.network.APIService
import com.callmangement.network.MultipartRequester
import com.callmangement.network.RetrofitInstance
import com.callmangement.support.ImageUtilsForRotate.ensurePortrait
import com.callmangement.support.OnSingleClickListener
import com.callmangement.ui.distributor.model.PosDistributionDetail
import com.callmangement.ui.errors.model.GetErrorTypesRoot
import com.callmangement.ui.errors.model.SaveErroeReqRoot
import com.callmangement.utils.CompressImage.Companion.compress
import com.callmangement.utils.Constants
import com.callmangement.utils.Constants.isNetworkAvailable
import com.callmangement.utils.PrefManager
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.MultipartBody.Part.Companion.createFormData
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.IOException
import java.util.Objects

class AddnewErrorPosSE : CustomActivity(), View.OnClickListener {
    private var binding: ActivityAddnewErrorPosSeBinding? = null
    private var prefManager: PrefManager? = null

    private val spinnerList: List<String> = ArrayList()
    private val myFormat = "yyyy-MM-dd"
    private val checkFilter = 0
    private val fromDate = ""
    private val toDate = ""
    private val userReportList: List<PosDistributionDetail> = ArrayList()
    val REQUEST_PICK_IMAGE_ONE: Int = 1111
    val REQUEST_PICK_IMAGE_TWO: Int = 1112
    val REQUEST_PICK_IMAGE_THREE: Int = 1113

    var stringArrayListHavingAllFilePath: ArrayList<String> = ArrayList()
    var mActivity: Activity? = null
    private val goodModelArrayList: ArrayList<SpinnerModel>? = null
    private val playerNames = ArrayList<String>()

    // private Spinner spinner;
    var tv_select: String? = null
    var selectItem: SpinnerModel? = null
    var ErrorTypeId: String? = null
    var Mobile_No: String? = null
    var selectedValue: String? = null
    var FPS_CODE: String? = null
    var Input_Device_Code: String? = null
    var Input_Remark: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddnewErrorPosSeBinding.inflate(
            layoutInflater
        )
        setContentView(binding!!.root)

        initView()
    }

    private fun initView() {
        mActivity = this
        prefManager = PrefManager(mContext!!)
        binding!!.actionBar.ivBack.visibility = View.VISIBLE
        binding!!.actionBar.ivThreeDot.visibility = View.GONE
        binding!!.actionBar.layoutLanguage.visibility = View.GONE
        binding!!.actionBar.buttonPDF.visibility = View.GONE
        binding!!.actionBar.textToolbarTitle.text = resources.getString(R.string.pos_errors)

        // spinner = findViewById(R.id.spinner);
        fetchSpinnersata()
        setUpClickListener()
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
    private fun fetchSpinnersata() {
        if (isNetworkAvailable(mContext!!)) {
            showProgress()
            val service = RetrofitInstance.getRetrofitInstance().create(
                APIService::class.java
            )
            val call = service.GetErrorTypes(prefManager!!.useR_Id, "0")
            call.enqueue(object : Callback<GetErrorTypesRoot?> {
                override fun onResponse(
                    call: Call<GetErrorTypesRoot?>,
                    response: Response<GetErrorTypesRoot?>
                ) {
                    hideProgress()
                    if (response.isSuccessful) {
                        if (response.code() == 200) {
                            if (response.body() != null) {
                                if (response.body()!!.status == "200") {
                                    val getErrorTypesRoot = response.body()

                                    //      Log.d("getErrorTypesRoot..","getErrorTypesRoot.."+getErrorTypesRoot);
                                    val getErrortypesDatum =
                                        getErrorTypesRoot!!.data



                                    for (i in getErrortypesDatum.indices) {
                                        playerNames.add(getErrortypesDatum[i].errorType)
                                    }

                                    val spinnerArrayAdapter = ArrayAdapter(
                                        this@AddnewErrorPosSE,
                                        layout.simple_spinner_item,
                                        playerNames
                                    )
                                    spinnerArrayAdapter.setDropDownViewResource(layout.simple_spinner_dropdown_item) // The drop down view
                                    binding!!.spinner.adapter = spinnerArrayAdapter



                                    binding!!.spinner.onItemSelectedListener =
                                        object : AdapterView.OnItemSelectedListener {
                                            override fun onItemSelected(
                                                adapterView: AdapterView<*>?,
                                                view: View,
                                                i: Int,
                                                l: Long
                                            ) {
                                                ErrorTypeId =
                                                    getErrortypesDatum[i].errorTypeId.toString()

                                                //               Log.d("modelExpensestatuslist", " " + ErrorTypeId);
                                            }

                                            override fun onNothingSelected(adapterView: AdapterView<*>?) {
                                            }
                                        }
                                    val msg = getErrorTypesRoot.message
                                    val status = getErrorTypesRoot.status

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
                                    makeToast(response.body()!!.message.toString())
                                }
                            } else {
                                Log.i(
                                    "onEmptyResponse",
                                    "Returned empty response"
                                ) //Toast.makeText(getContext(),"Nothing returned",Toast.LENGTH_LONG).show();
                            }
                        } else {
                            makeToast(resources.getString(R.string.error))
                        }
                    } else {
                        makeToast(resources.getString(R.string.error))
                    }
                }

                override fun onFailure(call: Call<GetErrorTypesRoot?>, t: Throwable) {
                    hideProgress()
                    makeToast(resources.getString(R.string.error_message))
                }
            })
        } else {
            makeToast(resources.getString(R.string.no_internet_connection))
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
    private fun setUpClickListener() {
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

        binding!!.actionBar.ivBack.setOnClickListener(this)
        //  binding.textFromDate.setOnClickListener(this);
        // binding.textToDate.setOnClickListener(this);
        binding!!.linChooseImages1.setOnClickListener(object : OnSingleClickListener() {
            override fun onSingleClick(v: View) {
                selectImage(REQUEST_PICK_IMAGE_ONE)
            }
        })

        binding!!.linChooseImages2.setOnClickListener(object : OnSingleClickListener() {
            override fun onSingleClick(v: View) {
                selectImage(REQUEST_PICK_IMAGE_TWO)
            }
        })

        binding!!.linChooseImages3.setOnClickListener(object : OnSingleClickListener() {
            override fun onSingleClick(v: View) {
                selectImage(REQUEST_PICK_IMAGE_THREE)
            }
        })

        binding!!.buttonUploadError.setOnClickListener { view: View? ->
            selectedValue = binding!!.spinner.selectedItem.toString()
            FPS_CODE = binding!!.inputFpsCode.text.toString()
            Input_Device_Code = binding!!.inputDeviceCode.text.toString()
            Input_Remark = binding!!.inputRemark.text.toString()
            Mobile_No = binding!!.inputMobileno.text.toString()

            Log.d("Mobile_No", " $Mobile_No")


            /*       if (selectItem == null || selectedValue.equals(tv_select) || selectedValue.isEmpty() || selectedValue.length() == 0) {
               makeToast(getResources().getString(R.string.select_errortype));
           }
       else*/
            if (FPS_CODE == null || FPS_CODE!!.isEmpty() || FPS_CODE!!.length == 0) {
                makeToast(resources.getString(R.string.enter_fps_code))
            } else if (Input_Remark == null || Input_Remark!!.isEmpty() || Input_Remark!!.length == 0) {
                makeToast(resources.getString(R.string.please_enter_remark))
            } else if (Mobile_No == null || Mobile_No!!.isEmpty() || Mobile_No!!.length != 10) {
                makeToast(resources.getString(R.string.enter_your_exact_mobile_no))
            } else if (stringArrayListHavingAllFilePath == null || stringArrayListHavingAllFilePath.size <= 0) {
                makeToast(resources.getString(R.string.please_select_atleast_one_img))
            } else {
                uploadImages(
                    FPS_CODE!!,
                    Input_Device_Code!!, Input_Remark!!, stringArrayListHavingAllFilePath
                )
                Log.d(
                    "response",
                    "FPS response--------------$FPS_CODE $Input_Device_Code $Input_Remark"
                )
                Log.d(
                    "response",
                    "response--------------" + FPS_CODE + " " + selectedValue + " " + Input_Device_Code + " " + Input_Remark + " " + stringArrayListHavingAllFilePath + " " +
                            stringArrayListHavingAllFilePath.size
                )
            }
        }
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
    override fun onClick(view: View) {
        val id = view.id
        if (id == R.id.iv_back) {
            //  onBackPressed();
            val i = Intent(this@AddnewErrorPosSE, ErrorPosActivity::class.java)
            startActivity(i)
        }
    }

    private fun selectImage(requestCode: Int) {
        try {
            val items = arrayOf<CharSequence>(
                resources.getString(R.string.imagepicker_str_take_photo),
                resources.getString(R.string.imagepicker_str_choose_from_gallery),
                resources.getString(R.string.imagepicker_str_cancel)
            )
            val title = TextView(mActivity)
            title.text = resources.getString(R.string.imagepicker_str_select_challan_image)
            title.setBackgroundColor(resources.getColor(R.color.colorActionBar))
            title.setPadding(15, 25, 15, 25)
            title.gravity = Gravity.CENTER
            title.setTextColor(Color.WHITE)
            title.textSize = 22f
            val builder = AlertDialog.Builder(
                mActivity!!
            )
            builder.setCustomTitle(title)
            // builder.setTitle("Add Photo!");
            builder.setItems(
                items
            ) { dialog: DialogInterface, item: Int ->
                if (items[item] == resources.getString(R.string.imagepicker_str_take_photo)) {
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
                        .setSelectedImages(ArrayList())
                        .start(requestCode)
                } else if (items[item] == resources.getString(R.string.imagepicker_str_choose_from_gallery)) {
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
                        .setSelectedImages(ArrayList())
                        .start(requestCode)
                } else if (items[item] == resources.getString(R.string.imagepicker_str_cancel)) {
                    dialog.dismiss()
                }
            }
            builder.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_PICK_IMAGE_ONE && resultCode == RESULT_OK && data != null) {
            val images = data.getParcelableArrayListExtra<Image>(Config.EXTRA_IMAGES)
            if (images != null && images.size > 0) {
                val image = images[0]
                var imageStoragePath = image.path
                if (imageStoragePath.contains("file:/")) {
                    imageStoragePath = imageStoragePath.replace("file:/", "")
                }
                imageStoragePath = compress(
                    imageStoragePath,
                    this
                )
                val imgFile = File(imageStoragePath)
                val myBitmap = BitmapFactory.decodeFile(imgFile.absolutePath)

                try {
                    binding!!.ivChallanImage.setImageBitmap(ensurePortrait(imageStoragePath))
                    stringArrayListHavingAllFilePath.add(imageStoragePath)
                } catch (e: IOException) {
                    binding!!.ivChallanImage.setImageBitmap(myBitmap)
                    stringArrayListHavingAllFilePath.add(imageStoragePath)

                    e.printStackTrace()
                } catch (e: NullPointerException) {
                    e.printStackTrace()
                }
            }
        } else if (requestCode == REQUEST_PICK_IMAGE_TWO && resultCode == RESULT_OK && data != null) {
            val images = data.getParcelableArrayListExtra<Image>(Config.EXTRA_IMAGES)
            if (images != null && images.size > 0) {
                val image = images[0]
                var imageStoragePath = image.path
                if (imageStoragePath.contains("file:/")) {
                    imageStoragePath = imageStoragePath.replace("file:/", "")
                }
                imageStoragePath = compress(
                    imageStoragePath,
                    this
                )
                val imgFile = File(imageStoragePath)
                val myBitmap = BitmapFactory.decodeFile(imgFile.absolutePath)

                try {
                    binding!!.ivPartsImage1.setImageBitmap(ensurePortrait(imageStoragePath))
                    stringArrayListHavingAllFilePath.add(imageStoragePath)
                } catch (e: IOException) {
                    binding!!.ivPartsImage1.setImageBitmap(myBitmap)
                    stringArrayListHavingAllFilePath.add(imageStoragePath)
                    e.printStackTrace()
                } catch (e: NullPointerException) {
                    e.printStackTrace()
                }
            }
        } else if (requestCode == REQUEST_PICK_IMAGE_THREE && resultCode == RESULT_OK && data != null) {
            val images = data.getParcelableArrayListExtra<Image>(Config.EXTRA_IMAGES)
            if (images != null && images.size > 0) {
                val image = images[0]
                var imageStoragePath = image.path
                if (imageStoragePath.contains("file:/")) {
                    imageStoragePath = imageStoragePath.replace("file:/", "")
                }
                imageStoragePath = compress(
                    imageStoragePath,
                    this
                )
                val imgFile = File(imageStoragePath)
                val myBitmap = BitmapFactory.decodeFile(imgFile.absolutePath)
                try {
                    binding!!.ivPartsImage2.setImageBitmap(ensurePortrait(imageStoragePath))
                    stringArrayListHavingAllFilePath.add(imageStoragePath)
                } catch (e: IOException) {
                    binding!!.ivPartsImage2.setImageBitmap(myBitmap)
                    stringArrayListHavingAllFilePath.add(imageStoragePath)
                    e.printStackTrace()
                } catch (e: NullPointerException) {
                    e.printStackTrace()
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }


    /*
    private void getReportList(String fromDate, String toDate) {
        if (Constants.isNetworkAvailable(mContext)) {
            showProgress();
            APIService service = RetrofitInstance.getRetrofitInstance().create(APIService.class);
            Call<ResponseBody> call = service.getPosDistributionListSEAPI(prefManager.getUseR_DistrictId(), prefManager.getUseR_Id(), "0","","",fromDate,toDate);
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
    private fun uploadImages(
        FPS_CODE: String,
        Input_Device_Code: String,
        Input_Remark: String,
        arrayHavingAllFilePath: ArrayList<String>
    ) {
        if (isNetworkAvailable(mContext!!)) {
            showProgress()
            val service = RetrofitInstance.getRetrofitInstance().create(
                APIService::class.java
            )
            val campDocumentsParts: Array<MultipartBody.Part?> = arrayOfNulls<MultipartBody.Part>(arrayHavingAllFilePath.size)
            for (index in arrayHavingAllFilePath.indices) {
                val file = File(arrayHavingAllFilePath[index])
                val surveyBody = RequestBody.create(
                    "image/*".toMediaTypeOrNull(),
                    file
                )
                campDocumentsParts[index] = createFormData(
                    "CampDocuments",
                    file.name,
                    surveyBody
                )
            }


            val ErrorStatusId = "1"

            val call = service.saveErroeReqApi(
                MultipartRequester.fromString((prefManager!!.useR_Id)),
                MultipartRequester.fromString(FPS_CODE),
                MultipartRequester.fromString(Input_Device_Code),
                MultipartRequester.fromString(Mobile_No),  /*   MultipartRequester.fromString(ErrorTypeId),
                    MultipartRequester.fromString(ErrorStatusId),  */
                MultipartRequester.fromString(ErrorTypeId),
                MultipartRequester.fromString(""),
                MultipartRequester.fromString(Input_Remark),
                campDocumentsParts
            )

            Log.d(
                "errorsaveresponse",
                "response-----" + prefManager!!.useR_Id + "  " + FPS_CODE + " " + Input_Device_Code + " " + Input_Remark + " " + campDocumentsParts
            )
            call.enqueue(object : Callback<SaveErroeReqRoot?> {
                override fun onResponse(
                    call: Call<SaveErroeReqRoot?>,
                    response: Response<SaveErroeReqRoot?>
                ) {
                    hideProgress()

                    if (response.isSuccessful) {
                        if (response.code() == 200 && response.body() != null) {
                            // if (response.body().getStatus().equals("200")) {

                            if (Objects.requireNonNull(response.body() != null)) {
                                val getErrorTypesRoot = response.body()

                                val status = getErrorTypesRoot!!.status

                                if (status == "200") {
                                    val checkstatus = getErrorTypesRoot.response.isStatus
                                    if (checkstatus) {
                                        Log.d("checkstatus", "checkstatus$checkstatus")

                                        val i = Intent(
                                            this@AddnewErrorPosSE,
                                            ErrorPosActivity::class.java
                                        )
                                        startActivity(i)
                                        makeToast(response.body()!!.response.message)
                                    } else {
                                        makeToast(response.body()!!.response.message)
                                    }
                                } else {
                                    makeToast(response.body()!!.response.message)
                                }

                                Log.d(
                                    "getErrorTypesRoot..",
                                    "getErrorTypesRoot..$getErrorTypesRoot"
                                )


                                //   makeToast(response.body().getResponse().getMessage());
                                Log.d(
                                    this@AddnewErrorPosSE.toString(),
                                    "fggafdaf$getErrorTypesRoot"
                                )

                                // makeToast(response.body().getMessage());
                                // finish();
                            } else {
                                makeToast(resources.getString(R.string.error))
                            }
                        } else {
                            makeToast(resources.getString(R.string.error))
                        }
                    } else {
                        makeToast(resources.getString(R.string.error))
                    }
                }

                override fun onFailure(call: Call<SaveErroeReqRoot?>, error: Throwable) {
                    Log.d("responseerror", "response-----$error")

                    hideProgress()
                    makeToast(resources.getString(R.string.error))
                    call.cancel()
                }
            })
        } else {
            makeToast(resources.getString(R.string.no_internet_connection))
        }
    }
}