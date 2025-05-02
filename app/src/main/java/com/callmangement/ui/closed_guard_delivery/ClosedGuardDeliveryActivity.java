package com.callmangement.ui.closed_guard_delivery;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.ResultReceiver;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.callmangement.Network.APIService;
import com.callmangement.Network.MultipartRequester;
import com.callmangement.Network.RetrofitInstance;
import com.callmangement.R;
import com.callmangement.custom.CustomActivity;
import com.callmangement.databinding.ActivityClosedGuardDeliveryBinding;
import com.callmangement.databinding.ActivityIrisNewDeliveryBinding;
import com.callmangement.imagepicker.model.Config;
import com.callmangement.imagepicker.model.Image;
import com.callmangement.imagepicker.ui.imagepicker.ImagePicker;
import com.callmangement.support.FetchAddressIntentServices;
import com.callmangement.support.ImageUtilsForRotate;
import com.callmangement.support.OnSingleClickListener;
import com.callmangement.support.signatureview.SignatureView;
import com.callmangement.ui.home.MainActivity;
import com.callmangement.ui.ins_weighing_scale.model.fps.DetailByFpsData;
import com.callmangement.ui.ins_weighing_scale.model.fps.DetailByFpsRoot;
import com.callmangement.ui.iris_derivery_installation.Model.CheckIrisSerialNoResponse;
import com.callmangement.ui.iris_derivery_installation.Model.SaveIRISDeliverResponse;
import com.callmangement.ui.qrcodescanner.BarcodeScanningActivity;
import com.callmangement.utils.CompressImage;
import com.callmangement.utils.Constants;
import com.callmangement.utils.PrefManager;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

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
import retrofit2.http.Part;

public class ClosedGuardDeliveryActivity extends CustomActivity {
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
    private Context mContext;
    PrefManager preference;
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
    String formattedDateTime;
    String mydatetime;
    private ActivityClosedGuardDeliveryBinding binding;
    private String partsImageStoragePath1 = "";
    private String partsImageStoragePath2 = "";
    private String partsImageStoragePath3 = "";
    private final String partsImageStoragePath4 = "";
    private final String partsImageStoragePath5 = "";
    private EditText dateAndTimeEditText;
    private Calendar selectedDateTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //will hide the title
//        getSupportActionBar().hide(); // hide the title bar
        binding = ActivityClosedGuardDeliveryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        //  setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        init();
    }

    private void init() {
        mActivity = this;
        mContext = this;
        preference = new PrefManager(mActivity);
        resultReceiver = new ClosedGuardDeliveryActivity.AddressResultReceiver(new Handler());
    //    resultReceiver = new ClosedGuardDeliveryActivity.AddressResultReceiver(new Handler());
        binding.actionBarND.ivBack.setVisibility(View.VISIBLE);
        binding.actionBarND.textToolbarTitle.setText(getResources().getString(R.string.closed_guard_delivery));
        dateAndTimeEditText = findViewById(R.id.dateAndTimeEditText);
        selectedDateTime = Calendar.getInstance();
        CoustomDialoge();
        checkbox();
        setClickListener();
        modelspinner();

    }

    private void modelspinner() {
        modelSpinner = findViewById(R.id.rl_model);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.iris_model, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.inputmodel.setAdapter(adapter);
    }

    private void setClickListener() {
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

        binding.linChooseImages4.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                selectImage(REQUEST_PICK_IMAGE_FOUR);
            }
        });

        binding.actionBarND.ivBack.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                if (alertDialog != null && alertDialog.isShowing()) {
                    // Dismiss the dialog if it's showing
                    alertDialog.dismiss();
                    Intent i = new Intent(mContext, MainActivity.class);
                    startActivity(i);
                } else {
                    onBackPressed();
                }
            }
        });

        binding.linChooseImagessig.setOnClickListener(view -> {
            //     binding.actionFullpage.sigpage.setVisibility(View.VISIBLE);
            //    binding.scroolview.setVisibility(View.GONE);
            signatureDialoge();
        });


        binding.inputSerialno.setOnClickListener(view -> {

        //    binding.inputSerialno.setText("");
        //    startScanning();

        });

        binding.buttonSubmit.setOnClickListener(view -> {
            //    selectedValue = binding.inputmodel.getSelectedItem().toString();
            //   Model = binding.inputmodel.getText().toString();

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

            //    Log.d("Mobile_No"," "+ Mobile_No);
            binding.ivTvspinner.setVisibility(View.GONE);

             /*       if (selectItem == null || selectedValue.equals(tv_select) || selectedValue.isEmpty() || selectedValue.length() == 0) {
                        makeToast(getResources().getString(R.string.select_errortype));
                    }
                else*/

            if (FPS_CODE == null || FPS_CODE.isEmpty() || FPS_CODE.length() == 0) {
                //  makeToast(getResources().getString(R.string.enter_fps_code));
                binding.inputFpsCode.setError("???");
                String msg = getResources().getString(R.string.enter_fps_code);
                showAlertDialogWithSingleButton(mActivity, msg);
            }  else if (Mobile_No == null || Mobile_No.length() != 10) {
                //makeToast(getResources().getString(R.string.enter_your_exact_mobile_no));
                binding.inputMobile.setError("???");
                String msg = getResources().getString(R.string.enter_your_exact_mobile_no);
                showAlertDialogWithSingleButton(mActivity, msg);

            } else if (stringArrayListHavingAllFilePath == null || stringArrayListHavingAllFilePath.size() < 1) {
                //  makeToast(getResources().getString(R.string.please_select_all_img));

            //    String msg = getResources().getString(R.string.please_select_all_img);
                String msg = getResources().getString(R.string.please_select_image1);
                showAlertDialogWithSingleButton(mActivity, msg);
                //  binding.inputSerialno.requestFocus();

            } else if (signatureRequestBody == null) {
                // makeToast(getResources().getString(R.string.please_sign));
                //  binding.inputSerialno.requestFocus();
                String msg = getResources().getString(R.string.please_sign);
                showAlertDialogWithSingleButton(mActivity, msg);
            } else {
                checkLocationServices();
            }
        });
    }

    private void openCameraWithScanner() {

        Intent intent = new Intent(this, BarcodeScanningActivity.class);
        intent.putExtra("scanning_SDK", BarcodeScanningActivity.ScannerSDK.MLKIT);
        resultLauncher.launch(intent);

    }

    private void startScanning() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
        ) {
            openCameraWithScanner();
        } else {
            final String[] permissions = new String[]{Manifest.permission.CAMERA};
            ActivityCompat.requestPermissions(
                    this,
                    permissions,
                    CAMERA_REQUEST_CODE
            );
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == CAMERA_REQUEST_CODE && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCameraWithScanner();
            } else if (!ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.CAMERA
            )
            ) {
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);
                startActivityForResult(intent, CAMERA_REQUEST_CODE);
            }
        }
    }

    private void checkLocationServices() {

        if (ContextCompat.checkSelfPermission(getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(mActivity,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
            String msg = "???? ????????? ?? ?????? ??? ?? ??? ??| ???? ???? ?????? ??????? ???? ?? ??? ?? ! ????? ???? ?? ?????? ?? ?????? ???? ????? ";
            showAlertDialogWithSingleButton(mActivity, msg);
        } else {
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            boolean isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            if (isGpsEnabled || isNetworkEnabled) {
                getCurrentLocation();
                // Location services are enabled
            } else {
                showSettingsAlert();
            }
        }
        //for phone location
    }

    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        // Setting Dialog Title
        alertDialog.setTitle("Permission necessary");
        // Setting Dialog Message
        alertDialog.setMessage("???? ????????? ?? ?????? ??? ?? ??? ??| ???? ???? ?????? ??????? ???? ?? ??? ?? ! " +
                "????? ???? ?? ?????? ?? ?????? ???? ????? ");
        // On pressing Settings button
        alertDialog.setPositiveButton(
                getResources().getString(R.string.button_ok),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(
                                Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(intent);
                    }
                });
        alertDialog.show();
    }

    private void signatureDialoge() {

       /* int currentOrientation = getResources().getConfiguration().orientation;

        if (currentOrientation == Configuration.ORIENTATION_LANDSCAPE) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }*/

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_full_page_iris, null);
        mSignaturePad = dialogView.findViewById(R.id.signature_pad);
        dialogBuilder.setView(dialogView);
        AlertDialog alertDialog = dialogBuilder.create();
        // Optional: Set dialog properties if needed
        // For example: alertDialog.setCancelable(false);
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
                signatureBitmap = rotateBitmap(mSignaturePad.getSignatureBitmap(),90); // Get the signature as a Bitmap
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
        // progressBar.setVisibility(View.VISIBLE);
        showProgress(getResources().getString(R.string.get_location));
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
        LocationServices.getFusedLocationProviderClient(mActivity)
                .requestLocationUpdates(locationRequest, new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        super.onLocationResult(locationResult);
                        LocationServices.getFusedLocationProviderClient(getApplicationContext())
                                .removeLocationUpdates(this);
                        if (locationResult != null && locationResult.getLocations().size() > 0) {
                            hideProgress();
                            int latestlocIndex = locationResult.getLocations().size() - 1;
                            lati = locationResult.getLocations().get(latestlocIndex).getLatitude();
                            longi = locationResult.getLocations().get(latestlocIndex).getLongitude();
                            //    textLatLong.setText(String.format("Latitude : %s\n Longitude: %s", lati, longi));
                            //my
                            Location location = new Location("providerNA");
                            location.setLongitude(longi);
                            location.setLatitude(lati);
                            fetchaddressfromlocation(location);
                            //    Log.d("locationlocation", "" + location);
                            completeAddressStr = getAddressFromLatLong();
                            //   Log.d("completeAddressStr", completeAddressStr);
                            hideProgress();

                            if (completeAddressStr != null && completeAddressStr.length() != 0) {
                                saveIRISCloseGuardDelivery(FPS_CODE, Model, SerialNo, Mobile_No, stringArrayListHavingAllFilePath);
                            } else {
                                String msg = "???? ???? ?????? ??????? ???? ?? ??? ?? ! ???? ?? ?????? ?? ?????? ???? ????? ";
                                showAlertDialogWithSingleButton(mActivity, msg);
                            }

                            // String msg = "???? ???? ?????? ??????? ???? ?? ??? ?? ! ???? ?? ?????? ?? ?????? ???? ?????";}

                           /* String FS = String.valueOf(ed_fps.getText());

                            if(districtId.equals(-1))
                            {
                                Toast.makeText(mActivity, "????? ???? ?? ??? ?????", Toast.LENGTH_SHORT).show();
                            }
                            else if(FS.equals(null) || FS.isEmpty()){
                                Toast.makeText(mActivity, "????? ?????? ?????", Toast.LENGTH_SHORT).show();
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

        final AlertDialog.Builder alert = new AlertDialog.Builder(mContext);
        View mView = getLayoutInflater().inflate(R.layout.activity_coustomfpsdialoge, null);
        //  mView.setBackground(ContextCompat.getDrawable(this, R.drawable.rounded_dialog_bg));
        txt_fpscode = mView.findViewById(R.id.txt_input);
        Button btn_cancel = mView.findViewById(R.id.btn_cancel);
        btn_cancel.setVisibility(View.VISIBLE);

        Button btn_okay = mView.findViewById(R.id.btn_okay);
        alert.setView(mView);
        alertDialog = alert.create();
        alertDialog.setCanceledOnTouchOutside(false);
        //alertDialog.getWindow().setBackgroundDrawable(new BitmapDrawable(getResources(), blurredBitmap));

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(mContext, MainActivity.class);
                startActivity(i);

                alertDialog.dismiss();

                /*InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                alertDialog.dismiss();*/
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

        alertDialog.setOnKeyListener(new Dialog.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode,
                                 KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
                    dialog.dismiss();
                    finish();
                }
                return false;
            }
        });
        alertDialog.show();
    }

    private void GetCustomerDetailsByFPS() {
        Fps_code = txt_fpscode.getText().toString().trim();
        if (Constants.isNetworkAvailable(mActivity)) {
            hideKeyboard(mActivity);
            showProgress(getResources().getString(R.string.please_wait));
            String USER_Id = preference.getUSER_Id();
            //   Log.d("USER_ID", " "+USER_Id);
            APIService apiInterface = RetrofitInstance.getRetrofitInstance().create(APIService.class);
            Call<DetailByFpsRoot> call = apiInterface.getCustomerDetailsByFPSForCloseGuard(Fps_code, USER_Id, "0");
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
                                    Toast.makeText(mContext, fps_message, Toast.LENGTH_SHORT).show();
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

                                        Date currentDate = new Date();
                                        // Define the desired date format
                                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
                                        // Format the current date and time
                                        String formattedDate = dateFormat.format(currentDate);
                                        // Now, 'formattedDate' contains the date in the desired format
                                        //     Log.d("Current Date"," "+ formattedDate);
                                        dateAndTimeEditText.setText(formattedDate);

                                        //   binding.inputSerialno.setText(WeighingScaleSerialNo);
                                        //  binding.inputmodel.setText(WeighingScaleModelName);
                                        //  binding.inputIrisCode.setText(IrisScannerSerialNo);
                                        //    binding.inputIrisName.setText(IrisScannerModelName);

                                        if (ContextCompat.checkSelfPermission(getApplicationContext(),
                                                Manifest.permission.ACCESS_FINE_LOCATION)
                                                != PackageManager.PERMISSION_GRANTED) {
                                            ActivityCompat.requestPermissions(mActivity,
                                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                                    LOCATION_PERMISSION_REQUEST_CODE);
                                        } else {
                                        }
                                    } else {
                                        String msg = response.body().getMessage();
                                        showAlertDialogWithSingleButton(mActivity, msg);
                                        //  makeToast(String.valueOf(response.body().getMessage()));
                                        // Handle the case when countDatum is empty or null
                                    }
                                } else {
                                    alertDialog.show();
                                    //  makeToast(String.valueOf(response.body().getMessage()));
                                    String msg = response.body().getMessage();
                                    showAlertDialogWithSingleButton(mActivity, msg);
                                    // Handle the case when the response status is not 200 or the response body is null
                                }
                            } else {
                                String msg = response.body().getMessage();
                                showAlertDialogWithSingleButton(mActivity, msg);
                                // makeToast(String.valueOf(response.body().getMessage()));
                            }
                        } else {
                            String msg = "HTTP Error: " + response.code();
                            showAlertDialogWithSingleButton(mActivity, msg);
                        }
                    } else {
                        String msg = "HTTP Error: " + response.code();
                        showAlertDialogWithSingleButton(mActivity, msg);
                        // makeToast(getResources().getString(R.string.error));
                    }
                }

                @Override
                public void onFailure(@NonNull Call<DetailByFpsRoot> call, @NonNull Throwable error) {
                    hideProgress();
                    // makeToast(getResources().getString(R.string.error));
                    String msg = error.getMessage();
                    showAlertDialogWithSingleButton(mActivity, msg);
                    call.cancel();
                }
            });
        } else {
            makeToast(getResources().getString(R.string.no_internet_connection));
        }
    }

    private void selectImage(final Integer requestCode) {
        try {
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == CAMERA_REQUEST_CODE) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
            ) {
                openCameraWithScanner();
            }
        }

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

            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }



    private void saveIRISCloseGuardDelivery(String fps_code, String model, String serialNo, String mobile_no, ArrayList<String> arrayHavingAllFilePath) {
        if (Constants.isNetworkAvailable(mActivity)) {
            hideKeyboard(mActivity);
            showProgress(getResources().getString(R.string.please_wait));
            String USER_Id = preference.getUSER_Id();
            //   Log.d("USER_ID"," "+ USER_Id);
            mydatetime = String.valueOf(dateAndTimeEditText.getText());

            APIService apiInterface = RetrofitInstance.getRetrofitInstance().create(APIService.class);
            //   Log.d("apifullapifullapifull", " "+fullAddress);
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

        /*    if (!partsImageStoragePath4.equals("")) {
                fileNamePartsImage4 = new File(partsImageStoragePath4).getName();
                attachmentPartsImage4 = RequestBody.create(MediaType.parse("multipart/form-data"), new File(partsImageStoragePath4));
            } else {
                fileNamePartsImage4 = "";
                attachmentPartsImage4 = RequestBody.create(MediaType.parse("text/plain"), "");
            }*/

  /* if (!partsImageStoragePath5.equals("")) {
                fileNamePartsImage5 = new File(partsImageStoragePath5).getName();
                attachmentPartsImage6= RequestBody.create(MediaType.parse("multipart/form-data"), new File(partsImageStoragePath5));
            } else {
                fileNamePartsImage5 = "";
                attachmentPartsImage6 = RequestBody.create(MediaType.parse("text/plain"), encodedSignature);
            }*/


            Call<SaveIRISDeliverResponse> call = apiInterface.saveIRISCloseGuardDelivery(
                    MultipartRequester.fromString(USER_Id),
                    MultipartRequester.fromString(fps_code),
                    MultipartRequester.fromString(completeAddressStr),
                    MultipartRequester.fromString(String.valueOf(lati)),
                    MultipartRequester.fromString(String.valueOf(longi)),
                 //   MultipartRequester.fromString(mydatetime),
                    MultipartBody.Part.createFormData("DealerImageWithIRISCloseGuard", fileNamePartsImage1, attachmentPartsImage1),
                    MultipartBody.Part.createFormData("DealerSignatureIRISCloseGuard", "signature.png", signatureRequestBody));

            call.enqueue(new Callback<SaveIRISDeliverResponse>() {
                @Override
                public void onResponse(@NonNull Call<SaveIRISDeliverResponse> call, @NonNull Response<SaveIRISDeliverResponse> response) {
                    hideProgress();
                    if (response.isSuccessful()) {
                        if (response.code() == 200) {
                            if (response.body() != null) {
                                if (Objects.requireNonNull(response.body()).getStatus().equals("200")) {
                                    makeToast(String.valueOf(response.body().getResponse().getMessage()));
                                    Intent intent = new Intent(mActivity, MainActivity.class);
                                    startActivity(intent);
                                } else {
                                    // makeToast(String.valueOf(response.body().getResponse().getMessage()));
                                    String msg = response.body().getResponse().getMessage();
                                    showAlertDialogWithSingleButton(mActivity, msg);
                                }
                            } else {
                                String msg = response.body().getResponse().getMessage();
                                showAlertDialogWithSingleButton(mActivity, msg);
                                // makeToast(getResources().getString(R.string.error));
                            }
                        } else {
                            String msg = "HTTP Error: " + response.code();
                            showAlertDialogWithSingleButton(mActivity, msg);
                        }
                    } else {
                        String msg = "HTTP Error: " + response.code();
                        showAlertDialogWithSingleButton(mActivity, msg);
                    }
                }

                @Override
                public void onFailure(@NonNull Call<SaveIRISDeliverResponse> call, @NonNull Throwable error) {
                    hideProgress();
                    showAlertDialogWithSingleButton(mActivity, error.getMessage());
                    //   makeToast(getResources().getString(R.string.error));
                    call.cancel();
                }
            });
        } else {
            // makeToast(getResources().getString(R.string.no_internet_connection));
            showAlertDialogWithSingleButton(mActivity, getResources().getString(R.string.no_internet_connection));
        }
    }

    @Override
    public void onBackPressed() {
        if (alertDialog != null && alertDialog.isShowing()) {
            // Dismiss the dialog if it's showing
            alertDialog.dismiss();
            Intent i = new Intent(mContext, MainActivity.class);
            startActivity(i);
        } else {
            super.onBackPressed();
        }
    }

    public void makeToast(String string) {
        if (TextUtils.isEmpty(string)) return;
        Toast.makeText(mActivity, string, Toast.LENGTH_SHORT).show();
    }

    public void showDateTimePicker(View view) {
        int year = selectedDateTime.get(Calendar.YEAR);
        int month = selectedDateTime.get(Calendar.MONTH);
        int day = selectedDateTime.get(Calendar.DAY_OF_MONTH);

        // Show DatePickerDialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        selectedDateTime.set(year, month, day);
                        showTimePicker();
                    }
                },
                year, month, day
        );
        datePickerDialog.show();
    }

    private void showTimePicker() {

        int hour = selectedDateTime.get(Calendar.HOUR_OF_DAY);
        int minute = selectedDateTime.get(Calendar.MINUTE);
        // Show TimePickerDialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(
                this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                        selectedDateTime.set(Calendar.HOUR_OF_DAY, hour);
                        selectedDateTime.set(Calendar.MINUTE, minute);
                        updateDateTimeEditText();
                    }
                },
                hour, minute, true
        );
        timePickerDialog.show();
    }

    private void updateDateTimeEditText() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        formattedDateTime = dateFormat.format(selectedDateTime.getTime());
        dateAndTimeEditText.setText(formattedDateTime);
    }

    private class AddressResultReceiver extends ResultReceiver {
        public AddressResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            super.onReceiveResult(resultCode, resultData);
            if (resultCode == Constants.SUCCESS_RESULT) {

                String ADDRESS = "ADDRESS " + resultData.getString(Constants.ADDRESS) + ", ";
                String LOCAITY = "LOCAITY " + resultData.getString(Constants.LOCAITY) + ", ";
                String STATE = "STATE " + resultData.getString(Constants.STATE) + ", ";
                String DISTRICT = "DISTRICT " + resultData.getString(Constants.DISTRICT) + ", ";
                String COUNTRY = "COUNTRY " + resultData.getString(Constants.COUNTRY) + ", ";
                String POST_CODE = "POST_CODE " + resultData.getString(Constants.POST_CODE) + " ";

                fullAddress = ADDRESS + LOCAITY + STATE + DISTRICT + COUNTRY + POST_CODE;
                //     Log.d("resultDataresultData", fullAddress);
            } else {
                Toast.makeText(mContext, resultData.getString(Constants.RESULT_DATA_KEY), Toast.LENGTH_SHORT).show();
            }
            hideProgress();
        }
    }

/*
    ActivityResultLauncher<Intent> resultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    // Here, no request code
                    String data = result.getData().getStringExtra("result");
                    binding.inputSerialno.setText(data);

                }
            });
*/

    ActivityResultLauncher<Intent> resultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // Here, no request code
                        //     Intent data = result.getData();
                        //        binding.inputSerialno.setText(result.getData().getStringExtra("result"));
                        checkIrisSerialNo(result.getData().getStringExtra("result"));
                    }
                }
            });



    private void checkIrisSerialNo(String irisScannerSerialNo) {
        if (Constants.isNetworkAvailable(mActivity)) {
            hideKeyboard(mActivity);
            showProgress(getResources().getString(R.string.please_wait));
            String USER_Id = preference.getUSER_Id();
            //    Log.d("USER_ID", USER_Id);
            APIService apiInterface = RetrofitInstance.getRetrofitInstance().create(APIService.class);
            Call<CheckIrisSerialNoResponse> call = apiInterface.checkIrisSerialNo( USER_Id, "3",irisScannerSerialNo);
            call.enqueue(new Callback<CheckIrisSerialNoResponse>() {
                @Override
                public void onResponse(@NonNull Call<CheckIrisSerialNoResponse> call, @NonNull Response<CheckIrisSerialNoResponse> response) {
                    hideProgress();
                    if (response.isSuccessful()) {
                        if (response.code() == 200) {
                            if (response.body() != null) {
                                if (Objects.requireNonNull(response.body()).getStatus().equals("200")) {
                                    //    alertDialog.dismiss();
                                    CheckIrisSerialNoResponse checkIrisSerialNoResponse = response.body();
                                    if (checkIrisSerialNoResponse.getResponse() != null &&
                                            checkIrisSerialNoResponse.getResponse().getStatus()) {
                                        binding.inputSerialno.setText(irisScannerSerialNo);
                                    } else {
                                        String msg = response.body().getMessage();
                                        showAlertDialogWithSingleButton(mActivity, msg);
                                        //  makeToast(String.valueOf(response.body().getMessage()));
                                        // Handle the case when countDatum is empty or null
                                    }
                                } else {
                                    //     alertDialog.show();
                                    //  makeToast(String.valueOf(response.body().getMessage()));
                                    String msg = response.body().getMessage();
                                    showAlertDialogWithSingleButton(mActivity, msg);
                                    // Handle the case when the response status is not 200 or the response body is null
                                }
                            } else {
                                String msg = response.body().getMessage();
                                showAlertDialogWithSingleButton(mActivity, msg);
                                // makeToast(String.valueOf(response.body().getMessage()));
                            }
                        } else {
                            String msg = "HTTP Error: " + response.code();
                            showAlertDialogWithSingleButton(mActivity, msg);
                        }
                    } else {
                        String msg = "HTTP Error: " + response.code();
                        showAlertDialogWithSingleButton(mActivity, msg);
                        // makeToast(getResources().getString(R.string.error));
                    }
                }

                @Override
                public void onFailure(@NonNull Call<CheckIrisSerialNoResponse> call, @NonNull Throwable error) {
                    hideProgress();
                    // makeToast(getResources().getString(R.string.error));
                    String msg = error.getMessage();
                    showAlertDialogWithSingleButton(mActivity, msg);
                    call.cancel();
                }
            });
        } else {
            makeToast(getResources().getString(R.string.no_internet_connection));
        }
    }


}


