package com.callmangement.ui.login;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.callmangement.Network.APIService;
import com.callmangement.Network.RetrofitInstance;
import com.callmangement.R;
import com.callmangement.custom.CustomActivity;
import com.callmangement.databinding.ActivityLoginBinding;
import com.callmangement.firebase.FirebaseUtils;
import com.callmangement.model.attendance.ModelAttendance;
import com.callmangement.model.login.ModelLoginData;
import com.callmangement.tracking_service.GpsUtils;
import com.callmangement.ui.attendance.AttendanceActivity;
import com.callmangement.ui.distributor.activity.DistributorMainActivity;
import com.callmangement.ui.home.MainActivity;
import com.callmangement.utils.Constants;
import com.callmangement.utils.DateTimeUtils;
import com.callmangement.utils.PrefManager;
import com.google.firebase.messaging.FirebaseMessaging;
import com.callmangement.support.permissions.PermissionHandler;
import com.callmangement.support.permissions.Permissions;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends CustomActivity implements View.OnClickListener {
    private ActivityLoginBinding binding;
    private LoginViewModel viewModel;
    private PrefManager prefManager;
    private String email = "";
    private String password = "";
    private int backgroundLocationPermissionApproved;
    private boolean isGPS = false;
    private String checkPermissionTypeStr = "initial";
    String[] permissions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        initView();

    }

    private void initView() {
        prefManager = new PrefManager(mContext);
        viewModel = ViewModelProviders.of(this).get(LoginViewModel.class);

        FirebaseUtils.registerTopic("all");
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (task.getResult() != null) {
                    String deviceToken = task.getResult();
                    prefManager.setFIREBASE_DEVICE_TOKEN(deviceToken);
                    @SuppressLint("HardwareIds")
                    String android_id = Settings.Secure.getString(mContext.getContentResolver(), Settings.Secure.ANDROID_ID);
                    updateDTokenOnServer(android_id, deviceToken);

                }
            }
        });

        @SuppressLint("HardwareIds") String android_id = Settings.Secure.getString(mContext.getContentResolver(), Settings.Secure.ANDROID_ID);
        //  Log.d("deviceiddeviceid", " " + android_id);
        prefManager.setDEVICE_ID(android_id);
        checkPermission();
        setUpOnClickListener();
    }

    private void setUpOnClickListener() {
        binding.buttonLogin.setOnClickListener(this);
    }

    private void doLogin(String email, String password, String device_id, String device_token) {
        if (Constants.isNetworkAvailable(mContext)) {
            isLoading();
            viewModel.login(email, password, device_id, device_token).observe(this, modelLogin -> {
                isLoading();


                if (modelLogin.status.equals("200")) {
                    ModelLoginData data = modelLogin.getUser_details();
                    prefManager.setUserLoginStatus("true");
                    prefManager.setUser_Id(data.getUserId());
                    prefManager.setUSER_NAME(data.getUserName());
                    prefManager.setUSER_EMAIL(data.getEmailId());
                    prefManager.setUSER_Mobile(data.getMobileNo());
                    prefManager.setUSER_DistrictId(data.getDistrictId());
                    prefManager.setUSER_District(data.getDistrict());
                    prefManager.setUSER_TYPE_ID(data.getUserTypeId());
                    prefManager.setUSER_TYPE(data.getUserTypeName());
                    prefManager.setUSER_PASSWORD(password);
                    //     Log.d("loginid", " " + data.getUserId());

                    if (data.getUserTypeId().equals("4") && data.getUserTypeName().equalsIgnoreCase("ServiceEngineer")) {
                        getCheckedAttendanceStatus();
                    } else if (data.getUserTypeId().equals("8") && data.getUserTypeName().equalsIgnoreCase("Distributor")) {
                        startActivity(DistributorMainActivity.class);
                    } else {
                        startActivity(MainActivity.class);
                    }
                }
            });
        } else {
            makeToast(getResources().getString(R.string.no_internet_connection));
        }
    }

    private void getCheckedAttendanceStatus() {
        APIService service = RetrofitInstance.getRetrofitInstance().create(APIService.class);
        Call<ModelAttendance> call = service.getCheckedAttendance(prefManager.getUSER_Id(), DateTimeUtils.getCurrentDate());
        call.enqueue(new Callback<ModelAttendance>() {
            @Override
            public void onResponse(@NonNull Call<ModelAttendance> call, @NonNull Response<ModelAttendance> response) {
                if (response.isSuccessful()) {
                    ModelAttendance model = response.body();
                    if (Objects.requireNonNull(model).status.equals("200")) {
                        startActivity(MainActivity.class);
                    } else {
                        startActivity(AttendanceActivity.class);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ModelAttendance> call, @NonNull Throwable t) {

            }

        });
    }

    private void isLoading() {
        viewModel.getIsLoading().observe(this, aBoolean -> {
            if (aBoolean) {
                showProgress(getResources().getString(R.string.please_wait));
            } else {
                hideProgress();
            }
        });
    }

    private void checkPermission() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.READ_MEDIA_VIDEO, Manifest.permission.CAMERA};
            //     Log.d("checkggg", "b");

        } else {
            permissions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
            //    Log.d("checkggg", "0");
        }

        String rationale = "Please provide location permission so that you can ...";
        Permissions.Options options = new Permissions.Options()
                .setRationaleDialogTitle("Info")
                .setSettingsDialogTitle("Warning");
        Permissions.check(mContext, permissions, rationale, options, new PermissionHandler() {
            @Override
            public void onGranted() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    backgroundLocationPermissionApproved = ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_BACKGROUND_LOCATION);
                    if (backgroundLocationPermissionApproved != 0)
                        new Handler(Looper.getMainLooper()).postDelayed(() -> collectLocationPermissionDataDialog(), 500);
                    else {
                        if (!getGpsStatus())
                            turnOnGps();
                        else {
                            if (checkPermissionTypeStr.equalsIgnoreCase("login"))
                                loginProcess();
                        }
                    }
                    //   Log.d("goodgood", "1");
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    backgroundLocationPermissionApproved = ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_BACKGROUND_LOCATION);
                    if (backgroundLocationPermissionApproved != 0)
                        new Handler(Looper.getMainLooper()).postDelayed(() -> collectLocationPermissionDataDialog(), 500);
                    else {
                        if (!getGpsStatus())
                            turnOnGps();
                        else {
                            if (checkPermissionTypeStr.equalsIgnoreCase("login"))
                                loginProcess();
                        }
                    }
                    //   Log.d("goodgood", "2");

                } else {
                    if (!getGpsStatus())
                        turnOnGps();
                    else {
                        if (checkPermissionTypeStr.equalsIgnoreCase("login"))
                            loginProcess();
                    }
                    //  Log.d("goodgood", "3");
                }
            }

            @Override
            public void onDenied(Context context, ArrayList<String> deniedPermissions) {
                Toast.makeText(mContext, "Permission denied", Toast.LENGTH_SHORT).show();
                //   Log.d("deniedPermissions", "" + deniedPermissions);
            }
        });
    }

    private void collectLocationPermissionDataDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage(getResources().getString(R.string.collect_location_permission_alert))
                .setCancelable(false)
                .setPositiveButton(getResources().getString(R.string.ok), (dialog, id) -> {
                    checkBackgroundLocationPermission();
                    dialog.dismiss();
                });
        AlertDialog alert = builder.create();
        alert.setTitle(getResources().getString(R.string.alert));
        alert.show();
    }

    private void checkBackgroundLocationPermission() {
        String[] permissions = {Manifest.permission.ACCESS_BACKGROUND_LOCATION};
        String rationale = "Please provide Access Background Location";
        Permissions.Options options = new Permissions.Options()
                .setRationaleDialogTitle("Info")
                .setSettingsDialogTitle("Warning");
        Permissions.check(mContext, permissions, rationale, options, new PermissionHandler() {
            @Override
            public void onGranted() {
                if (!getGpsStatus())
                    turnOnGps();
                else {
                    if (checkPermissionTypeStr.equalsIgnoreCase("login"))
                        loginProcess();
                }
            }

            @Override
            public void onDenied(Context context, ArrayList<String> deniedPermissions) {
                Toast.makeText(mContext, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void turnOnGps() {
        new GpsUtils(mContext).turnGPSOn(isGPSEnable -> {
            isGPS = isGPSEnable;
        });
    }

    private boolean getGpsStatus() {
        LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    private void loginProcess() {

        email = Objects.requireNonNull(binding.inputEmail.getText()).toString().trim();
        password = Objects.requireNonNull(binding.inputPassword.getText()).toString().trim();
        if (email.isEmpty()) {
            makeToast(getResources().getString(R.string.please_enter_email_address));
        } else if (!isValidEmail(email)) {
            makeToast(getResources().getString(R.string.please_enter_valid_email_address));
        } else if (password.isEmpty()) {
            makeToast(getResources().getString(R.string.please_enter_password));
        } else {
            doLogin(email, password, prefManager.getDEVICE_ID(), prefManager.getFIREBASE_DEVICE_TOKEN());
            //     Log.d("deviceiddeviceid", " " + prefManager.getDEVICE_ID());
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 102) {
            isGPS = getGpsStatus();
            if (isGPS && checkPermissionTypeStr.equalsIgnoreCase("login"))
                loginProcess();
        }
    }

    private void updateDTokenOnServer(String device_id, String device_token) {
        APIService service = RetrofitInstance.getRetrofitInstance().create(APIService.class);
        Call<ResponseBody> call = service.updateDToken(device_id, device_token);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                try {

                    if (response.isSuccessful()) {
                        if (response.code() == 200) {
                            if (response.body() != null) {
                                String responseStr = response.body().string();
                                JSONObject jsonObject = new JSONObject(responseStr);
                                String message = jsonObject.optString("message");
                                //   Log.e("message", message);
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        finishAffinity();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.buttonLogin) {
            checkPermissionTypeStr = "login";
            checkPermission();
        }
    }
}