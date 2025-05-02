package com.callmangement.ui.ins_weighing_scale.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.ResultReceiver;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


import com.callmangement.Network.APIService;
import com.callmangement.Network.MultipartRequester;
import com.callmangement.Network.RetrofitInstance;
import com.callmangement.R;
import com.callmangement.custom.CustomActivity;
import com.callmangement.databinding.ActivityNewdeliveryBinding;
import com.callmangement.imagepicker.model.Config;
import com.callmangement.imagepicker.model.Image;
import com.callmangement.imagepicker.ui.imagepicker.ImagePicker;
import com.callmangement.support.ImageUtilsForRotate;
import com.callmangement.support.OnSingleClickListener;
import com.callmangement.ui.ins_weighing_scale.model.DeliveredWeightInstal.WeighInsData;
import com.callmangement.ui.ins_weighing_scale.model.SaveInstall.SaveRoot;
import com.callmangement.ui.ins_weighing_scale.model.fps.DetailByFpsData;
import com.callmangement.ui.ins_weighing_scale.model.fps.DetailByFpsRoot;
import com.callmangement.utils.CompressImage;

import com.callmangement.support.FetchAddressIntentServices;
import com.callmangement.utils.Constants;
import com.callmangement.utils.PrefManager;
import com.callmangement.support.charting.utils.Utils;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.callmangement.support.signatureview.SignatureView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewDelivery extends CustomActivity {
    private static final int GALLERY_REQUEST_CODE = 123;
    private static final int CAMERA_REQUEST_CODE = 456;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    public final int REQUEST_PICK_IMAGE_ONE = 1111;
    public final int REQUEST_PICK_IMAGE_TWO = 1112;
    public final int REQUEST_PICK_IMAGE_THREE = 1113;
    public final int REQUEST_PICK_IMAGE_FOUR = 1114;
    public final int REQUEST_PICK_IMAGE_FIVE = 1115;
    private final List<String> spinnerList = new ArrayList<>();
    private final String myFormat = "yyyy-MM-dd";
    Activity mActivity;
    EditText txt_fpscode;
    String Fps_code;
    double lati, longi;
    ArrayList<String> stringArrayListHavingAllFilePath = new ArrayList<>();
    String fullAddress;
    ResultReceiver resultReceiver;
    String completeAddressStr = "";
    SignatureView mSignaturePad;
    RelativeLayout modelSpinner;
    String Model, SerialNo, Mobile_No, FPS_CODE, Full_Address, Lati, Longi, Remarks, Delivered_On, IsDeliverd_IRIS, IsDeliverd_WeighingScale;
    AlertDialog alertDialog;
    String encodedSignature;
    Bitmap signatureBitmap;
    RequestBody attachmentPartsImage6;
    RequestBody signatureRequestBody;
    Context mContext;
    PrefManager preference;
    String formattedDateTime;
    String mydatetime;
    private ActivityNewdeliveryBinding binding;
    private String partsImageStoragePath1 = "";
    private String partsImageStoragePath2 = "";
    private String partsImageStoragePath3 = "";
    private String partsImageStoragePath4 = "";
    private final String partsImageStoragePath5 = "";
    private WeighInsData mymodel;
    private EditText dateAndTimeEditText;
    private Calendar selectedDateTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //will hide the title
        //    getSupportActionBar().hide(); // hide the title bar
        binding = ActivityNewdeliveryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        //  setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        init();
    }

    private void init() {
        mActivity = this;
        mContext = this;
        binding.actionBar.textToolbarTitle.setText(getResources().getString(R.string.installation));
        preference = new PrefManager(mContext);
        resultReceiver = new AddressResultReceiver(new Handler());
        dateAndTimeEditText = findViewById(R.id.dateAndTimeEditText);
        selectedDateTime = Calendar.getInstance();
        getIntentData();
        setUpData();

        //  CoustomDialoge();
        checkbox();
        setClickListener();
        modelspinner();
    }

    private void getIntentData() {
        mymodel = (WeighInsData) getIntent().getSerializableExtra("param");
    }


    private void setUpData() {
        binding.inputFpsCode.setText(mymodel.getFpscode());
        binding.inputBlock.setText(mymodel.getBlockName());
        binding.inputDealerName.setText(mymodel.getDealerName());
        binding.inputMobile.setText(mymodel.getDealerMobileNo());
        binding.inputWsSerialno.setText(mymodel.getWeighingScaleSerialNo());
        binding.intputWsModel.setText(mymodel.getWeighingScaleModelName());
        binding.inputDistrict.setText(mymodel.getDistrictName());
        //  binding.inputtickretno.setText(model.getTicketNo());
        //  binding.inputtransdate.setText(model.getTranDateStr());
        //   binding.inputstatus.setText(model.getLast_TicketStatus());
        binding.inputFpsCode.setText(mymodel.getFpscode());
        Date currentDate = new Date();
        // Define the desired date format
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        // Format the current date and time
        String formattedDate = dateFormat.format(currentDate);
        // Now, 'formattedDate' contains the date in the desired format
        Log.d("Current Date", formattedDate);
        dateAndTimeEditText.setText(formattedDate);
    }

    public void showDateTimePicker(View view) {
        int year = selectedDateTime.get(Calendar.YEAR);
        int month = selectedDateTime.get(Calendar.MONTH);
        int day = selectedDateTime.get(Calendar.DAY_OF_MONTH);
        // Show DatePickerDialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                selectedDateTime.set(year, month, day);
                showTimePicker();
            }
        }, year, month, day);
        datePickerDialog.show();
    }

    private void showTimePicker() {
        int hour = selectedDateTime.get(Calendar.HOUR_OF_DAY);
        int minute = selectedDateTime.get(Calendar.MINUTE);
        // Show TimePickerDialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                selectedDateTime.set(Calendar.HOUR_OF_DAY, hour);
                selectedDateTime.set(Calendar.MINUTE, minute);
                updateDateTimeEditText();
            }
        }, hour, minute, true);
        timePickerDialog.show();
    }

    private void updateDateTimeEditText() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        formattedDateTime = dateFormat.format(selectedDateTime.getTime());
        dateAndTimeEditText.setText(formattedDateTime);
    }

    private void modelspinner() {
        modelSpinner = findViewById(R.id.rl_model);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.colors_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.inputmodel.setAdapter(adapter);
    }

    private void setClickListener() {
        binding.linChooseImages1.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
//                CropImage.activity().start(NewDelivery.this);
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

        binding.linChooseImages4.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                selectImage(REQUEST_PICK_IMAGE_FOUR);
            }
        });


        binding.actionBar.ivBack.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });
        binding.linChooseImagessig.setOnClickListener(view -> {
            signatureDialoge();
        });



        binding.buttonSubmit.setOnClickListener(view -> {
            //    selectedValue = binding.inputmodel.getSelectedItem().toString();
            //   Model = binding.inputmodel.getText().toString();
            //   resultReceiver = new AddressResultReceiver(new Handler());
            Model = binding.inputmodel.getSelectedItem().toString();
            SerialNo = binding.inputSerialno.getText().toString();
            Mobile_No = binding.inputMobile.getText().toString();
            FPS_CODE = binding.inputFpsCode.getText().toString();
            Full_Address = fullAddress;
            Lati = String.valueOf(lati);
            Longi = String.valueOf(longi);
            Remarks = "";
            Calendar calendar = Calendar.getInstance();
            Date today = calendar.getTime();
            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
            String todayDate = sdf.format(today);
            Delivered_On = todayDate;
            IsDeliverd_IRIS = "0";
            IsDeliverd_WeighingScale = "1";
            Log.d("Mobile_No", Mobile_No);


             /*       if (selectItem == null || selectedValue.equals(tv_select) || selectedValue.isEmpty() || selectedValue.length() == 0) {
                        makeToast(getResources().getString(R.string.select_errortype));
                    }
                else*/
            if (FPS_CODE == null || FPS_CODE.isEmpty() || FPS_CODE.length() == 0) {
                //  makeToast(getResources().getString(R.string.enter_fps_code));
                String msg = getResources().getString(R.string.enter_fps_code);
                showAlertDialogWithSingleButton(mActivity, msg);

            }

            /////gagandeepiris
          /*  else if(Model == null || Model.isEmpty() || Model.length()==0 || Model.equals("-Select Model-"))
            {
              //  makeToast(getResources().getString(R.string.please_select_model));

                String msg = getResources().getString(R.string.please_select_model);
                showAlertDialogWithSingleButton(mActivity, msg);
            }
            else if(SerialNo == null || SerialNo.isEmpty() || SerialNo.length()==0)
            {
              //  makeToast(getResources().getString(R.string.please_select_serialno));
                String msg = getResources().getString(R.string.please_select_serialno);
                showAlertDialogWithSingleButton(mActivity, msg);
            }*/
            /////gagandeepiris


            else if (Mobile_No == null || Mobile_No.isEmpty() || Mobile_No.length() != 10) {
                //  makeToast(getResources().getString(R.string.enter_your_exact_mobile_no));
                String msg = getResources().getString(R.string.enter_your_exact_mobile_no);
                showAlertDialogWithSingleButton(mActivity, msg);
            } else if (stringArrayListHavingAllFilePath == null || stringArrayListHavingAllFilePath.size() < 3) {
                //makeToast(getResources().getString(R.string.please_select_all_img));
                String msg = getResources().getString(R.string.please_select_all_img);
                showAlertDialogWithSingleButton(mActivity, msg);
            } else if (signatureRequestBody == null) {

                String msg = getResources().getString(R.string.please_sign);
                showAlertDialogWithSingleButton(mActivity, msg);
            }
            else {

                checkLocationServices();
            }
        });
    }


    private void signatureDialoge() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_full_page, null);
        mSignaturePad = dialogView.findViewById(R.id.signature_pad);
        dialogBuilder.setView(dialogView);
        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
        dialogView.findViewById(R.id.btn_clear).setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                mSignaturePad.clearCanvas();
            }
        });
        dialogView.findViewById(R.id.back).setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                alertDialog.dismiss();

            }
        });
        dialogView.findViewById(R.id.btn_clear).setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                mSignaturePad.clearCanvas();
            }
        });

        dialogView.findViewById(R.id.btn_okay).setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
              /*  Bitmap bitmap = mainBinding.signatureView.getSignatureBitmap();
                if(bitmap != null)
                {
                    mainBinding.imgSignature.setImageBitmap(bitmap);
                }*/
                signatureBitmap = rotateBitmap(mSignaturePad.getSignatureBitmap(),90);// Get the signature as a Bitmap
                encodedSignature = encodeBitmapToBase64(signatureBitmap);
                attachmentPartsImage6 = RequestBody.create(MediaType.parse("text/plain"), encodedSignature);
                File signatureFile = createFileFromBitmap(signatureBitmap);
// Create a request body for the signature image
                signatureRequestBody = RequestBody.create(MediaType.parse("image/*"), signatureFile);
// Create MultipartBody.Part for the signature image
                MultipartBody.Part signaturePart = MultipartBody.Part.createFormData("DealerSignImage", "signature.png", signatureRequestBody);
                binding.ivPartsImagesig.setImageBitmap(signatureBitmap);
                alertDialog.dismiss();
            }
        });

        // Optional: Customize dialog window properties (size, position, etc.)
        Window window = alertDialog.getWindow();
        if (window != null) {
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);


            // Other window properties can be set here
        }


    }

    private void checkLocationServices() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(NewDelivery.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            String msg = getResources().getString(R.string.permissionmsg);
            showAlertDialogWithSingleButton(mActivity, msg);

        }


        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if (isGpsEnabled || isNetworkEnabled) {
            getCurrentLocation();
        } else {
            showSettingsAlert();
        }
    }

    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        // Setting Dialog Title
        alertDialog.setTitle("Permission necessary");
        // Setting Dialog Message
        alertDialog.setMessage("External storage permission is necessary");
        // On pressing Settings button
        alertDialog.setPositiveButton(getResources().getString(R.string.button_ok), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        });

        alertDialog.show();
    }


    private File createFileFromBitmap(Bitmap bitmap) {
        try {
            File file = new File(getCacheDir(), "signature.png");
            file.createNewFile();
            if (bitmap != null) {

                // Convert bitmap to byte array
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 0 /*ignored for PNG*/, bos);
                byte[] bitmapData = bos.toByteArray();
                // Write the bytes in file
                FileOutputStream fos = new FileOutputStream(file);
                fos.write(bitmapData);
                fos.flush();
                fos.close();
                return file;
            } else {

            }

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }


    private String encodeBitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        if (bitmap != null) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
            byte[] byteArrayImage = baos.toByteArray();
            return Base64.encodeToString(byteArrayImage, Base64.DEFAULT);
        } else {
        }
        return null;
    }

    private void getCurrentLocation() {
        showProgress();
        Log.d("fulladdrss", "location" + completeAddressStr);

        //  Constants.showCustomProgressDialogCommonForAll(mActivity, getResources().getString(R.string.get_location));
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.getFusedLocationProviderClient(NewDelivery.this).requestLocationUpdates(locationRequest, new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                LocationServices.getFusedLocationProviderClient(getApplicationContext()).removeLocationUpdates(this);
                if (locationResult != null && locationResult.getLocations().size() > 0) {
                    int latestlocIndex = locationResult.getLocations().size() - 1;
                    lati = locationResult.getLocations().get(latestlocIndex).getLatitude();
                    longi = locationResult.getLocations().get(latestlocIndex).getLongitude();
                    //    textLatLong.setText(String.format("Latitude : %s\n Longitude: %s", lati, longi));
                    //my
                    Location location = new Location("providerNA");
                    location.setLongitude(longi);
                    location.setLatitude(lati);
                    fetchaddressfromlocation(location);
                    Log.d("locationlocation", "" + location);
                    completeAddressStr = getAddressFromLatLong();
                    Log.d("completeAddressStr", completeAddressStr);
                    hideProgress();

////////////mine


                    //if (Lati != null && Lati.length() != 0) {

                        if (completeAddressStr != null && completeAddressStr.length() != 0) {
                        saveInstallationReq(FPS_CODE, Model, SerialNo, Mobile_No, stringArrayListHavingAllFilePath);
                        //Toast.makeText(mActivity, "yesssssssssssssss", Toast.LENGTH_SHORT).show();

                    } else {

                        String msg = getResources().getString(R.string.locationmsg);
                        showAlertDialogWithSingleButton(mActivity, msg);
                    }

/////mine


                           /* String FS = String.valueOf(ed_fps.getText());

                            if(districtId.equals(-1))
                            {
                                Toast.makeText(mActivity, "कृपया जिले का चयन करें।", Toast.LENGTH_SHORT).show();

                            }

                            else if(FS.equals(null) || FS.isEmpty()){
                                Toast.makeText(mActivity, "कृपया एफपीएस भरें।", Toast.LENGTH_SHORT).show();

                            }
                            else {
                                dashboardApi();

                            }
*/


                } else {
                    hideProgress();
                }
            }
        }, Looper.getMainLooper());


    }

    private void fetchaddressfromlocation(Location location) {
        Intent intent = new Intent(this, FetchAddressIntentServices.class);
        intent.putExtra(Constants.RECEVIER, resultReceiver);
        intent.putExtra(Constants.LOCATION_DATA_EXTRA, location);
        startService(intent);


    }

    private String getAddressFromLatLong() {
        String localCompleteAddressStr = "";
        try {
            Geocoder gcd = new Geocoder(getApplicationContext(), Locale.getDefault());
            List<Address> addresses = gcd.getFromLocation(lati, longi, 1);
            if (addresses.size() > 0) {
                localCompleteAddressStr = addresses.get(0).getAddressLine(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
            localCompleteAddressStr = "";
        }
        return localCompleteAddressStr;
    }

    private void checkbox() {
        // binding.llWm.setVisibility(View.GONE);
        binding.llIris.setVisibility(View.GONE);
        binding.llWm.setVisibility(View.VISIBLE);
        binding.checkBoxWm.setChecked(true);

    /*    binding.checkBoxWm.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    binding.llWm.setVisibility(View.VISIBLE);
                } else {
                    // Checkbox is unchecked, perform actions here
                    // Example: disable ScrollView scrolling
                    binding.llWm.setVisibility(View.GONE);
                }
            }
        });
             binding.checkBoxIris.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        binding.llIris.setVisibility(View.VISIBLE);
                    } else {
                        // Checkbox is unchecked, perform actions here
                        // Example: disable ScrollView scrolling
                        binding.llIris.setVisibility(View.GONE);

                    }
                }
                });

*/

    }

    private void CoustomDialoge() {

        final AlertDialog.Builder alert = new AlertDialog.Builder(NewDelivery.this);
        View mView = getLayoutInflater().inflate(R.layout.activity_coustomfpsdialoge, null);
        //  mView.setBackground(ContextCompat.getDrawable(this, R.drawable.rounded_dialog_bg));
        txt_fpscode = mView.findViewById(R.id.txt_input);
        Button btn_cancel = mView.findViewById(R.id.btn_cancel);
        Button btn_okay = mView.findViewById(R.id.btn_okay);
        alert.setView(mView);
        alertDialog = alert.create();
        alertDialog.setCanceledOnTouchOutside(false);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                alertDialog.dismiss();
            }
        });
        btn_okay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //    myCustomMessage.setText(txt_inputText.getText().toString());
                InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                GetCustomerDetailsByFPS();
                //  alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }

    private void GetCustomerDetailsByFPS() {
        Fps_code = txt_fpscode.getText().toString().trim();
        if (Constants.isNetworkAvailable(mActivity)) {
            hideKeyboard(mActivity);
            hideProgress();
            String USER_Id = preference.getUSER_Id();
            Log.d("USER_ID", USER_Id);
            APIService service = RetrofitInstance.getRetrofitInstance().create(APIService.class);
            // Call<DetailByFpsRoot> call = service.apiDetailsByFPS(Fps_code,USER_Id,"0");
            Call<DetailByFpsRoot> call = service.apiDetailsByFPS(Fps_code, USER_Id, "0");
            //  APIInterface apiInterface = APIClient.GetRetrofitClientWithoutHeaders(mActivity,Utils.Baseurl).create(APIInterface.class);
            //  Call<DetailByFpsRoot> call = apiInterface.apiDetailsByFPS(Fps_code,USER_Id,"0");
            call.enqueue(new Callback<DetailByFpsRoot>() {
                @Override
                public void onResponse(@NonNull Call<DetailByFpsRoot> call, @NonNull Response<DetailByFpsRoot> response) {
                    hideProgress();
                    if (response.isSuccessful()) {
                        if (response.code() == 200) {
                            if (response.body() != null) {
                                if (Objects.requireNonNull(response.body()).getStatus().equals("200")) {
                                    alertDialog.dismiss();
                                    DetailByFpsRoot detailByFpsRoot = response.body();
                                    String fps_message = detailByFpsRoot.getMessage();
                                    Toast.makeText(NewDelivery.this, fps_message, Toast.LENGTH_SHORT).show();
                                    DetailByFpsData detailByFpsData = detailByFpsRoot.getData();
                                    if (detailByFpsData != null) {
                                        String DistrictName = detailByFpsData.getDistrictName();
                                        String Fpscode = detailByFpsData.getFpscode();
                                        String DealerName = detailByFpsData.getDealerName();
                                        String FpsdeviceCode = detailByFpsData.getFpsdeviceCode();
                                        String DealerMobileNo = detailByFpsData.getDealerMobileNo();
                                        String Block = detailByFpsData.getBlockName();
                                        String WeighingScaleModelName = String.valueOf(detailByFpsData.getWeighingScaleModelName());
                                        String WeighingScaleSerialNo = String.valueOf(detailByFpsData.getWeighingScaleSerialNo());
                                        String IrisScannerModelName = String.valueOf(detailByFpsData.getIrisScannerModelName());
                                        String IrisScannerSerialNo = String.valueOf(detailByFpsData.getIrisScannerSerialNo());
                                        binding.inputDealerName.setText(DealerName);
                                        binding.inputDistrict.setText(DistrictName);
                                        binding.inputFpsCode.setText(Fpscode);
                                        binding.inputMobile.setText(DealerMobileNo);
                                        binding.inputBlock.setText(Block);
                                        //   binding.inputSerialno.setText(WeighingScaleSerialNo);
                                        //  binding.inputmodel.setText(WeighingScaleModelName);
                                        //  binding.inputIrisCode.setText(IrisScannerSerialNo);
                                        //    binding.inputIrisName.setText(IrisScannerModelName);

                                        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                            ActivityCompat.requestPermissions(NewDelivery.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
                                        } else {

                                        }
                                        // Use 'dataValue' or perform operations with other properties

                                    } else {
                                        makeToast(String.valueOf(response.body().getMessage()));

                                        // Handle the case when countDatum is empty or null
                                    }
                                } else {
                                    alertDialog.show();
                                    makeToast(String.valueOf(response.body().getMessage()));

                                    // Handle the case when the response status is not 200 or the response body is null
                                }
                            } else {
                                makeToast(String.valueOf(response.body().getMessage()));
                            }


                        } else {
                            makeToast(getResources().getString(R.string.error));
                        }
                    } else {
                        makeToast(getResources().getString(R.string.error));
                    }
                }

                @Override
                public void onFailure(@NonNull Call<DetailByFpsRoot> call, @NonNull Throwable error) {
                    // Utils.hideCustomProgressDialogCommonForAll();
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

            ImagePicker.with(mActivity).setToolbarColor("#212121").setStatusBarColor("#000000").setToolbarTextColor("#FFFFFF").setToolbarIconColor("#FFFFFF").setProgressBarColor("#4CAF50").setBackgroundColor("#212121").setCameraOnly(true).setMultipleMode(true).setFolderMode(true).setShowCamera(true).setFolderTitle("Albums").setImageTitle("Galleries").setDoneTitle("Done").setMaxSize(1).setSavePath(Constants.saveImagePath).setSelectedImages(new ArrayList<>()).start(requestCode);
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("error", "" + e);
        }
    }

    private void showPreviewPopup(Bitmap myBitmap) {
        // Show pop up window

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_PICK_IMAGE_ONE && resultCode == RESULT_OK && data != null) {
            ArrayList<Image> images = data.getParcelableArrayListExtra(Config.EXTRA_IMAGES);
            if (images != null && images.size() > 0) {
                Image image = images.get(0);
                // String imageStoragePath = image.getPath();
                partsImageStoragePath1 = image.getPath();
                if (partsImageStoragePath1.contains("file:/")) {
                    partsImageStoragePath1 = partsImageStoragePath1.replace("file:/", "");
                }
                partsImageStoragePath1 = CompressImage.compress(partsImageStoragePath1, this);
                File imgFile = new File(partsImageStoragePath1);
                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());

                try {
                    binding.ivPartsImage1.setImageBitmap(ImageUtilsForRotate.ensurePortrait(partsImageStoragePath1));
                    stringArrayListHavingAllFilePath.add(partsImageStoragePath1);
                } catch (IOException e) {
                    binding.ivPartsImage1.setImageBitmap(myBitmap);
                    stringArrayListHavingAllFilePath.add(partsImageStoragePath1);

                    e.printStackTrace();
                }catch (NullPointerException e){
                    e.printStackTrace();
                }
                LayoutInflater layoutInflater = LayoutInflater.from(NewDelivery.this);
                View promptView = layoutInflater.inflate(R.layout.input_photo, null);
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(NewDelivery.this);
                alertDialogBuilder.setView(promptView);
                ImageView imageView = promptView.findViewById(R.id.imageView_photo);
                imageView.setImageBitmap(myBitmap);

                alertDialogBuilder.setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        })

                        .setNegativeButton("Retake", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                selectImage(REQUEST_PICK_IMAGE_ONE);
                            }
                        });
                // create an alert dialog
                AlertDialog alert = alertDialogBuilder.create();
                alert.setCancelable(false);
                alert.show();


            }
        } else if (requestCode == REQUEST_PICK_IMAGE_TWO && resultCode == RESULT_OK && data != null) {
            ArrayList<Image> images = data.getParcelableArrayListExtra(Config.EXTRA_IMAGES);
            if (images != null && images.size() > 0) {
                Image image = images.get(0);
                String imageStoragePath = image.getPath();
                partsImageStoragePath2 = image.getPath();
                if (partsImageStoragePath2.contains("file:/")) {
                    partsImageStoragePath2 = partsImageStoragePath2.replace("file:/", "");
                }
                partsImageStoragePath2 = CompressImage.compress(partsImageStoragePath2, this);
                File imgFile = new File(partsImageStoragePath2);
                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());

                try {
                    binding.ivPartsImage2.setImageBitmap(ImageUtilsForRotate.ensurePortrait(partsImageStoragePath2));
                    stringArrayListHavingAllFilePath.add(partsImageStoragePath2);
                } catch (IOException e) {
                    binding.ivPartsImage2.setImageBitmap(myBitmap);
                    stringArrayListHavingAllFilePath.add(partsImageStoragePath2);
                    e.printStackTrace();
                }catch (NullPointerException e){
                    e.printStackTrace();
                }

                LayoutInflater layoutInflater = LayoutInflater.from(NewDelivery.this);
                View promptView = layoutInflater.inflate(R.layout.input_photo, null);
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(NewDelivery.this);
                alertDialogBuilder.setView(promptView);
                ImageView imageView = promptView.findViewById(R.id.imageView_photo);
                imageView.setImageBitmap(myBitmap);

                alertDialogBuilder.setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        })

                        .setNegativeButton("Retake", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                selectImage(REQUEST_PICK_IMAGE_TWO);
                            }
                        });
                // create an alert dialog
                AlertDialog alert = alertDialogBuilder.create();
                alert.setCancelable(false);
                alert.show();


            }
        } else if (requestCode == REQUEST_PICK_IMAGE_THREE && resultCode == RESULT_OK && data != null) {
            ArrayList<Image> images = data.getParcelableArrayListExtra(Config.EXTRA_IMAGES);
            if (images != null && images.size() > 0) {
                Image image = images.get(0);
                //  String imageStoragePath = image.getPath();
                partsImageStoragePath3 = image.getPath();
                if (partsImageStoragePath3.contains("file:/")) {
                    partsImageStoragePath3 = partsImageStoragePath3.replace("file:/", "");
                }
                partsImageStoragePath3 = CompressImage.compress(partsImageStoragePath3, this);
                File imgFile = new File(partsImageStoragePath3);
                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());

                try {
                    binding.ivPartsImage3.setImageBitmap(ImageUtilsForRotate.ensurePortrait(partsImageStoragePath3));
                    stringArrayListHavingAllFilePath.add(partsImageStoragePath3);
                } catch (IOException e) {
                    binding.ivPartsImage3.setImageBitmap(myBitmap);
                    stringArrayListHavingAllFilePath.add(partsImageStoragePath3);
                    e.printStackTrace();
                }catch (NullPointerException e){
                    e.printStackTrace();
                }
                LayoutInflater layoutInflater = LayoutInflater.from(NewDelivery.this);
                View promptView = layoutInflater.inflate(R.layout.input_photo, null);
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(NewDelivery.this);
                alertDialogBuilder.setView(promptView);
                ImageView imageView = promptView.findViewById(R.id.imageView_photo);
                imageView.setImageBitmap(myBitmap);

                alertDialogBuilder.setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        })

                        .setNegativeButton("Retake", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                selectImage(REQUEST_PICK_IMAGE_THREE);
                            }
                        });
                // create an alert dialog
                AlertDialog alert = alertDialogBuilder.create();
                alert.setCancelable(false);
                alert.show();

            }
        } else if (requestCode == REQUEST_PICK_IMAGE_FOUR && resultCode == RESULT_OK && data != null) {
            ArrayList<Image> images = data.getParcelableArrayListExtra(Config.EXTRA_IMAGES);
            if (images != null && images.size() > 0) {
                Image image = images.get(0);
                //  String imageStoragePath = image.getPath();
                partsImageStoragePath4 = image.getPath();
                if (partsImageStoragePath4.contains("file:/")) {
                    partsImageStoragePath4 = partsImageStoragePath4.replace("file:/", "");
                }
                partsImageStoragePath4 = CompressImage.compress(partsImageStoragePath4, this);
                File imgFile = new File(partsImageStoragePath4);
                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());

                try {
                    binding.ivChallanImage4.setImageBitmap(ImageUtilsForRotate.ensurePortrait(partsImageStoragePath4));
                    stringArrayListHavingAllFilePath.add(partsImageStoragePath4);
                } catch (IOException e) {
                    binding.ivChallanImage4.setImageBitmap(myBitmap);
                    stringArrayListHavingAllFilePath.add(partsImageStoragePath4);
                    e.printStackTrace();
                }catch (NullPointerException e){
                    e.printStackTrace();
                }
                LayoutInflater layoutInflater = LayoutInflater.from(NewDelivery.this);
                View promptView = layoutInflater.inflate(R.layout.input_photo, null);
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(NewDelivery.this);
                alertDialogBuilder.setView(promptView);
                ImageView imageView = promptView.findViewById(R.id.imageView_photo);
                imageView.setImageBitmap(myBitmap);

                alertDialogBuilder.setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        })

                        .setNegativeButton("Retake", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                selectImage(REQUEST_PICK_IMAGE_FOUR);
                            }
                        });
                // create an alert dialog
                AlertDialog alert = alertDialogBuilder.create();
                alert.setCancelable(false);
                alert.show();


            }
        }


        super.onActivityResult(requestCode, resultCode, data);
    }


    private void saveInstallationReq(String fps_code, String model, String serialNo, String mobile_no, ArrayList<String> arrayHavingAllFilePath) {
        if (Constants.isNetworkAvailable(mActivity)) {
            hideKeyboard(mActivity);
            //  Utils.showCustomProgressDialogCommonForAll(mActivity, getResources().getString(R.string.please_wait));
            showProgress();
            String USER_Id = preference.getUSER_Id();
            String deliveryid = String.valueOf(mymodel.getDeliveryId());
            String iris_inputserialno = String.valueOf(binding.inputSerialno.getText());
            mydatetime = String.valueOf(dateAndTimeEditText.getText());
            //Log.d("USER_ID", USER_Id);
            //    APIInterface apiInterface = APIClient.GetRetrofitClientWithoutHeaders(mActivity, Utils.Baseurl).create(APIInterface.class);
            Log.d("apifullapifullapifull",""+ fullAddress);
            APIService service = RetrofitInstance.getRetrofitInstance().create(APIService.class);
            //  Call<CountRoot> call = service.appWeightCountApi(USER_Id,"" ,districtId,"", "0",fromDate,toDate);
          /*  MultipartBody.Part[] campDocumentsParts = new MultipartBody.Part[arrayHavingAllFilePath.size()];
            for (int index = 0; index < arrayHavingAllFilePath.size(); index++) {
                File file = new File(arrayHavingAllFilePath
                        .get(index));
                RequestBody surveyBody = RequestBody.create(MediaType.parse("image/*"),
                        file);
                campDocumentsParts[index] = MultipartBody.Part.createFormData("CampDocuments",
                        file.getName(),
                        surveyBody);
            }*/

          /*  Call<SaveRoot> call = apiInterface.saveInstallationReq(
                    MultipartRequester.fromString(USER_Id),
                    MultipartRequester.fromString(model),
                    MultipartRequester.fromString(serialNo),
                    MultipartRequester.fromString(""),
                    MultipartRequester.fromString(""),
                    MultipartRequester.fromString(mobile_no),
                    MultipartRequester.fromString(fps_code),
                    MultipartRequester.fromString(completeAddressStr),
                    MultipartRequester.fromString(String.valueOf(lati)),
                    MultipartRequester.fromString(String.valueOf(longi)),
                    MultipartRequester.fromString(Remarks),
                    MultipartRequester.fromString("2023-12-12 14:12"),
                    MultipartRequester.fromString(""),
                    MultipartRequester.fromString("0"),
                    MultipartRequester.fromString("1"),
                    campDocumentsParts);*/

            RequestBody attachmentPartsImage1;
            RequestBody attachmentPartsImage2;
            RequestBody attachmentPartsImage3;
            RequestBody attachmentPartsImage4;
            RequestBody attachmentPartsImage5;
            String fileNamePartsImage1 = "";
            String fileNamePartsImage2 = "";
            String fileNamePartsImage3 = "";
            String fileNamePartsImage4 = "";
            String fileNamePartsImage5 = "";
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

            if (!partsImageStoragePath3.equals("")) {
                fileNamePartsImage3 = new File(partsImageStoragePath3).getName();
                attachmentPartsImage3 = RequestBody.create(MediaType.parse("multipart/form-data"), new File(partsImageStoragePath3));
            } else {
                fileNamePartsImage3 = "";
                attachmentPartsImage3 = RequestBody.create(MediaType.parse("text/plain"), "");
            }

            if (!partsImageStoragePath4.equals("")) {
                fileNamePartsImage4 = new File(partsImageStoragePath4).getName();
                attachmentPartsImage4 = RequestBody.create(MediaType.parse("multipart/form-data"), new File(partsImageStoragePath4));
            } else {
                fileNamePartsImage4 = "";
                attachmentPartsImage4 = RequestBody.create(MediaType.parse("text/plain"), "");
            }

  /* if (!partsImageStoragePath5.equals("")) {
                fileNamePartsImage5 = new File(partsImageStoragePath5).getName();
                attachmentPartsImage6= RequestBody.create(MediaType.parse("multipart/form-data"), new File(partsImageStoragePath5));
            } else {
                fileNamePartsImage5 = "";
                attachmentPartsImage6 = RequestBody.create(MediaType.parse("text/plain"), encodedSignature);
            }*/
            Call<SaveRoot> call = service.saveInstallationRe(
                    MultipartRequester.fromString(USER_Id),
                    MultipartRequester.fromString(deliveryid),
                    MultipartRequester.fromString("0"),
                    //  MultipartRequester.fromString(iris_inputserialno),
                    MultipartRequester.fromString(""),
                    MultipartRequester.fromString(completeAddressStr),
                    MultipartRequester.fromString(String.valueOf(lati)),
                    MultipartRequester.fromString(String.valueOf(longi)),
                    MultipartRequester.fromString(Remarks),
                    MultipartRequester.fromString(mydatetime),
                    MultipartRequester.fromString(fps_code),
                    MultipartRequester.fromString(mobile_no),
                    MultipartBody.Part.createFormData("InstallationWeinghingImage", fileNamePartsImage2, attachmentPartsImage2),
                    // MultipartBody.Part.createFormData("InstallationIRISScannerImage", fileNamePartsImage4,attachmentPartsImage4),
                    MultipartBody.Part.createFormData("InstallationIRISScannerImage", fileNamePartsImage1, attachmentPartsImage1),
                    MultipartBody.Part.createFormData("InstallationDealerPhoto", fileNamePartsImage1, attachmentPartsImage1), MultipartBody.Part.createFormData("InstallationDealerSignature", "signature.png", signatureRequestBody),
                    MultipartBody.Part.createFormData("InstallationChallan", fileNamePartsImage3, attachmentPartsImage3));
            //campDocumentsParts);
            Log.d("sendResponse", "-----" + "USER_Id" + USER_Id);
            Log.d("sendResponse", "-----" + "deviceid" + deliveryid);
            Log.d("sendResponse", "-----" + "serialNo" + "3");
            Log.d("sendResponse", "-----" + "iris_inputserialno" + " ");
            Log.d("sendResponse", "-----" + "completeAddressStr" + completeAddressStr);
            Log.d("sendResponse", "-----" + "lati" + lati);
            Log.d("sendResponse", "-----" + "longi" + longi);
            Log.d("sendResponse", "-----" + "Remarks" + Remarks);
            Log.d("sendResponse", "-----" + "mydatetime" + mydatetime);
            Log.d("sendResponse", "-----" + "longi" + longi);
            Log.d("sendResponse", "-----" + "Remarks" + Remarks);
            Log.d("sendResponse", "-----" + "fps_code" + fps_code);
            Log.d("sendResponse", "-----" + "mobile_no" + mobile_no);
            //   Call<SaveRoot> call = apiInterface.saveInstallationReq(USER_Id, model,serialNo,"","",mobile_no,fps_code,completeAddressStr, String.valueOf(lati), String.valueOf(longi), Remarks,Delivered_On,"","0","1", campDocumentsParts);
            call.enqueue(new Callback<SaveRoot>() {
                @Override
                public void onResponse(@NonNull Call<SaveRoot> call, @NonNull Response<SaveRoot> response) {
                    hideProgress();

                    if (response.isSuccessful()) {
                        if (response.code() == 200) {
                            if (response.body() != null) {
                                Log.d("ResponseCode", "" + response.code());
                                if (Objects.requireNonNull(response.body()).getStatus().equals("200")) {
                                    SaveRoot saveRoot = response.body();
                                    makeToast(String.valueOf(response.body().getResponse().getMessage()));
                                   onBackPressed();
                                } else {
                                    String massage = response.body().getResponse().getMessage();
                                    showAlertDialogWithSingleButton(mActivity, massage);
                                    // makeToast(String.valueOf(response.body().getResponse().getMessage()));
                                }
                            } else {
                                showAlertDialogWithSingleButton(mActivity, response.message());
                            }
                        } else {
                            String msg =  "HTTP Error: "+ response.code();
                            showAlertDialogWithSingleButton(mActivity, msg);
                        }
                    } else {
                        String msg =  "HTTP Error: "+ response.code();
                       showAlertDialogWithSingleButton(mActivity, msg);
                        Log.d("Toperror", String.valueOf(response));
                    }
                }

                @Override
                public void onFailure(@NonNull Call<SaveRoot> call, @NonNull Throwable error) {
                    // Utils.hideCustomProgressDialogCommonForAll();
                    hideProgress();
                    showAlertDialogWithSingleButton(mActivity, error.getMessage());

                    call.cancel();
                }
            });
        } else {
            showAlertDialogWithSingleButton(mActivity, getResources().getString(R.string.no_internet_connection));

        }


    }

    @Override
    public void onBackPressed() {
        //  alertDialog.show();
        super.onBackPressed();
        if (alertDialog != null && alertDialog.isShowing()) {
            // Dismiss the dialog if it's showing
            alertDialog.dismiss();
            setResult(RESULT_OK);

            finish();

        } else {
            setResult(RESULT_OK);

            finish();
        }


    }

    public void makeToast(String string) {
        if (TextUtils.isEmpty(string)) return;
        Toast.makeText(mActivity, string, Toast.LENGTH_SHORT).show();
    }

    private class AddressResultReceiver extends ResultReceiver {
        public AddressResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            super.onReceiveResult(resultCode, resultData);
            if (resultCode == Constants.SUCCESS_RESULT) {
               /* address.setText(resultData.getString(Constants.ADDRESS));
                locaity.setText(resultData.getString(Constants.LOCAITY));
                state.setText(resultData.getString(Constants.STATE));
                district.setText(resultData.getString(Constants.DISTRICT));
                country.setText(resultData.getString(Constants.COUNTRY));
                postcode.setText(resultData.getString(Constants.POST_CODE));*/
                String ADDRESS = "ADDRESS " + resultData.getString(Constants.ADDRESS) + ", ";
                String LOCAITY = "LOCAITY " + resultData.getString(Constants.LOCAITY) + ", ";
                String STATE = "STATE " + resultData.getString(Constants.STATE) + ", ";
                String DISTRICT = "DISTRICT " + resultData.getString(Constants.DISTRICT) + ", ";
                String COUNTRY = "COUNTRY " + resultData.getString(Constants.COUNTRY) + ", ";
                String POST_CODE = "POST_CODE " + resultData.getString(Constants.POST_CODE) + " ";
                fullAddress = ADDRESS + LOCAITY + STATE + DISTRICT + COUNTRY + POST_CODE;
                Log.d("resultDataresultData", fullAddress);

/*
                String FS = String.valueOf(ed_fps.getText());
                if (districtId.equals(-1)) {
                    Toast.makeText(mActivity, "कृपया जिले का चयन करें।", Toast.LENGTH_SHORT).show();
                } else if (FS.equals(null) || FS.isEmpty()) {
                    Toast.makeText(mActivity, "कृपया एफपीएस भरें।", Toast.LENGTH_SHORT).show();
                } else {
                    dashboardApi();
                }
*/


            } else {
                Toast.makeText(NewDelivery.this, resultData.getString(Constants.RESULT_DATA_KEY), Toast.LENGTH_SHORT).show();
            }
            hideProgress();
        }
    }
}

