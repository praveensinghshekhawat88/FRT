package com.callmangement.ui.distributor.activity;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.callmangement.network.APIService;
import com.callmangement.network.RetrofitInstance;
import com.callmangement.R;
import com.callmangement.custom.CustomActivity;
import com.callmangement.databinding.ActivityDistributorMainBinding;
import com.callmangement.firebase.FirebaseUtils;
import com.callmangement.model.login.ModelLogin;
import com.callmangement.model.logout.ModelLogout;
import com.callmangement.support.permissions.PermissionHandler;
import com.callmangement.support.permissions.Permissions;
import com.callmangement.tracking_service.GpsUtils;
import com.callmangement.ui.login.LoginActivity;
import com.callmangement.utils.PrefManager;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DistributorMainActivity extends CustomActivity implements View.OnClickListener {
    private ActivityDistributorMainBinding binding;
    private PrefManager prefManager;
    private Locale myLocale;
    private int backgroundLocationPermissionApproved;
    private boolean isGPS = false;
    private String checkPermissionTypeStr = "initial";
    int globalId = -1;
    private final String IsSE_PunchIN = "false";
    String[] permissions;

    public static Intent newIntent(Context context, String title) {
        Intent starter = new Intent(context, DistributorMainActivity.class);
        starter.putExtra("param", title);
        return starter;
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDistributorMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.actionBar.ivBack.setVisibility(View.GONE);
        binding.actionBar.ivThreeDot.setVisibility(View.VISIBLE);
        binding.actionBar.layoutLanguage.setVisibility(View.VISIBLE);
        binding.actionBar.buttonPDF.setVisibility(View.GONE);
        binding.actionBar.textToolbarTitle.setText(getResources().getString(R.string.app_name));
        prefManager = new PrefManager(mContext);
        binding.textUsername.setText(prefManager.getUSER_NAME());
        binding.textEmail.setText(prefManager.getUSER_EMAIL());
        initView();
    }

    private void initView() {
        checkPermission();
        setUpClickListener();
        layoutShowAndHide();
    }

    private void layoutShowAndHide() {
        if (!prefManager.getUSER_Change_Language().equals("")) {
            if (prefManager.getUSER_Change_Language().equals("en")) {
                binding.actionBar.ivEngToHindi.setVisibility(View.VISIBLE);
                binding.actionBar.ivHindiToEng.setVisibility(View.GONE);
                prefManager.setUSER_Change_Language("en");
            } else {
                binding.actionBar.ivEngToHindi.setVisibility(View.GONE);
                binding.actionBar.ivHindiToEng.setVisibility(View.VISIBLE);
                prefManager.setUSER_Change_Language("hi");
            }
        } else {
            binding.actionBar.ivEngToHindi.setVisibility(View.VISIBLE);
            binding.actionBar.ivHindiToEng.setVisibility(View.GONE);
        }
    }

    private void checkPermission() {
    //    String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION
          //      , Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions =  new String[]{ Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.READ_MEDIA_VIDEO,Manifest.permission.CAMERA};
            Log.d("checkggg","b");

        }
        else {
            permissions =
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.READ_EXTERNAL_STORAGE,  Manifest.permission.WRITE_EXTERNAL_STORAGE,  Manifest.permission.CAMERA};
            Log.d("checkggg","0");
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
                            if (checkPermissionTypeStr.equalsIgnoreCase("next_process"))
                                onClickWithPermissionCheck();
                        }
                    }
                }
                else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    backgroundLocationPermissionApproved = ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_BACKGROUND_LOCATION);
                    if (backgroundLocationPermissionApproved != 0)
                        new Handler(Looper.getMainLooper()).postDelayed(() -> collectLocationPermissionDataDialog(), 500);
                    else {
                        if (!getGpsStatus())
                            turnOnGps();
                        else {
                            if (checkPermissionTypeStr.equalsIgnoreCase("next_process"))
                                onClickWithPermissionCheck();
                        }
                    }
                } else {
                    if (!getGpsStatus())
                        turnOnGps();
                    else {
                        if (checkPermissionTypeStr.equalsIgnoreCase("next_process"))
                            onClickWithPermissionCheck();
                    }
                }


            }

            @Override
            public void onDenied(Context context, ArrayList<String> deniedPermissions) {
                Toast.makeText(mContext, "Permission denied", Toast.LENGTH_SHORT).show();
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
                    if (checkPermissionTypeStr.equalsIgnoreCase("next_process"))
                        onClickWithPermissionCheck();
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

    private void setUpClickListener() {
        binding.actionBar.ivEngToHindi.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setMessage(getResources().getString(R.string.eng_to_hi_message))
                    .setCancelable(false)
                    .setPositiveButton(getResources().getString(R.string.ok), (dialog, id) -> {
                        dialog.cancel();
                        binding.actionBar.ivEngToHindi.setVisibility(View.VISIBLE);
                        binding.actionBar.ivHindiToEng.setVisibility(View.GONE);
                        prefManager.setUSER_Change_Language("hi");
                        setLocaleUpdate("hi");
                    })
                    .setNegativeButton(getResources().getString(R.string.cancel), (dialog, id) -> dialog.cancel());
            AlertDialog alert = builder.create();
            alert.setTitle(getResources().getString(R.string.alert));
            alert.show();
        });

        binding.actionBar.ivHindiToEng.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setMessage(getResources().getString(R.string.hi_to_eng_message))
                    .setCancelable(false)
                    .setPositiveButton(getResources().getString(R.string.ok), (dialog, id) -> {
                        dialog.cancel();
                        binding.actionBar.ivEngToHindi.setVisibility(View.GONE);
                        binding.actionBar.ivHindiToEng.setVisibility(View.VISIBLE);
                        prefManager.setUSER_Change_Language("en");
                        setLocaleUpdate("en");
                    })
                    .setNegativeButton(getResources().getString(R.string.cancel), (dialog, id) -> dialog.cancel());
            AlertDialog alert = builder.create();
            alert.setTitle(getResources().getString(R.string.alert));
            alert.show();
        });

        binding.actionBar.ivThreeDot.setOnClickListener(view -> {
            PopupMenu popupMenu = new PopupMenu(mContext, binding.actionBar.ivThreeDot);
            popupMenu.getMenuInflater().inflate(R.menu.menu, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(menuItem -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setMessage(getResources().getString(R.string.do_you_want_to_logout_from_this_app))
                        .setCancelable(false)
                        .setPositiveButton(getResources().getString(R.string.logout), (dialog, id) -> {
                            dialog.cancel();
                            logout();
                        })
                        .setNegativeButton(getResources().getString(R.string.cancel), (dialog, id) -> dialog.cancel());
                AlertDialog alert = builder.create();
                alert.setTitle(getResources().getString(R.string.alert));
                alert.show();
                return true;
            });
            popupMenu.show();
        });

        binding.buttonNewPOSDistributionForm.setOnClickListener(this);
        binding.buttonUploadImage.setOnClickListener(this);
        binding.buttonDistributedPosList.setOnClickListener(this);
        binding.buttonPosDistributionReport.setOnClickListener(this);
    }

    private void checkLoginStatus(){
        APIService service = RetrofitInstance.getRetrofitInstance().create(APIService.class);
        Call<ModelLogin> call = service.login(prefManager.getUSER_EMAIL(),prefManager.getUSER_PASSWORD(),prefManager.getDEVICE_ID(), prefManager.getFIREBASE_DEVICE_TOKEN());
        call.enqueue(new Callback<ModelLogin>() {
            @Override
            public void onResponse(@NonNull Call<ModelLogin> call, @NonNull Response<ModelLogin> response) {
                if (response.isSuccessful()) {
                    if (response.code() == 200) {
                        ModelLogin model = response.body();
                        if (!Objects.requireNonNull(model).status.equals("200")) {
                            logout();
                        }
                    } else {
                        makeToast(getResources().getString(R.string.error));
                    }
                } else {
                    makeToast(getResources().getString(R.string.error));
                }
            }

            @Override
            public void onFailure(@NonNull Call<ModelLogin> call, @NonNull Throwable t) {
                makeToast(getResources().getString(R.string.error_message));
            }
        });
    }

    private void logout() {
        showProgress(getResources().getString(R.string.logout));
        APIService service = RetrofitInstance.getRetrofitInstance().create(APIService.class);
        Call<ModelLogout> call = service.logOutApp(prefManager.getUSER_Id(), prefManager.getUSER_EMAIL());
        call.enqueue(new Callback<ModelLogout>() {
            @Override
            public void onResponse(@NonNull Call<ModelLogout> call, @NonNull Response<ModelLogout> response) {
                hideProgress();
                if (response.isSuccessful()) {
                    if (response.code() == 200) {
                        ModelLogout model = response.body();
                        if (Objects.requireNonNull(model).status.equals("200")) {
                            FirebaseUtils.unregisterTopic("all");
                            prefManager.clear();
                            prefManager.setUSER_PunchIn(IsSE_PunchIN);
                            prefManager.setUser_Id("0");
                            prefManager.setUSER_DistrictId("0");
                            @SuppressLint("HardwareIds") String android_id = Settings.Secure.getString(mContext.getContentResolver(), Settings.Secure.ANDROID_ID);
                            prefManager.setDEVICE_ID(android_id);
                            startActivity(new Intent(mContext, LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                            finish();
                        } else {
                            makeToast(model.message);
                        }
                    } else {
                        makeToast(getResources().getString(R.string.error));
                    }
                } else {
                    makeToast(getResources().getString(R.string.error));
                }
            }

            @Override
            public void onFailure(@NonNull Call<ModelLogout> call, @NonNull Throwable t) {
                hideProgress();
                makeToast(getResources().getString(R.string.error_message));
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 102) {
            isGPS = getGpsStatus();
            if (isGPS && checkPermissionTypeStr.equalsIgnoreCase("next_process"))
                onClickWithPermissionCheck();
        }
    }

    private void onClickWithPermissionCheck(){
        if (globalId == R.id.buttonNewPOSDistributionForm) {
            startActivity(NewPosDistributionFormActivity.class);
        } else if (globalId == R.id.buttonUploadImage) {
            startActivity(UploadImageActivity.class);
        } else if (globalId == R.id.buttonDistributedPosList){
            startActivity(DistributedPosListActivity.class);
        } else if (globalId == R.id.button_pos_distribution_report){
            startActivity(PosDistributionReportActivity.class);
        }
    }

    public void setLocale(String lang) {
        myLocale = new Locale(lang);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
    }

    public void setLocaleUpdate(String lang) {
        prefManager.setUSER_Change_Language(lang);
        myLocale = new Locale(lang);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
        startActivity(DistributorMainActivity.class);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage(getResources().getString(R.string.do_you_want_to_exit_from_this_app))
                .setCancelable(false)
                .setPositiveButton(getResources().getString(R.string.ok), (dialog, id) -> {
                    dialog.cancel();
                    finishAffinity();
                })
                .setNegativeButton(getResources().getString(R.string.cancel), (dialog, id) -> dialog.cancel());
        AlertDialog alert = builder.create();
        alert.setTitle(getResources().getString(R.string.cancel));
        alert.show();
    }

    @Override
    protected void onStart() {
        final String locale = prefManager.getUSER_Change_Language();
        if (!locale.equals("")) {
            if (locale.equals("hi")) {
                setLocale("hi");
            } else {
                setLocale("en");
            }
        } else {
            prefManager.setUSER_Change_Language("en");
        }
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkLoginStatus();
    }

    @Override
    public void onClick(View view) {
        globalId = view.getId();
        checkPermissionTypeStr = "next_process";
        checkPermission();
    }
}