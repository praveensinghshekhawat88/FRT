package com.callmangement.ui.home;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings.Secure;
import android.util.DisplayMetrics;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;

import com.callmangement.network.APIService;
import com.callmangement.network.AppConfig;
import com.callmangement.network.RetrofitInstance;
import com.callmangement.R;
import com.callmangement.custom.CustomActivity;
import com.callmangement.databinding.ActivitySplashBinding;
import com.callmangement.firebase.FirebaseUtils;
import com.callmangement.model.ModelMobileVersion;
import com.callmangement.model.attendance.ModelAttendance;
import com.callmangement.ui.attendance.AttendanceActivity;
import com.callmangement.ui.distributor.activity.DistributorMainActivity;
import com.callmangement.ui.login.LoginActivity;
import com.callmangement.utils.Constants;
import com.callmangement.utils.DateTimeUtils;
import com.callmangement.utils.PrefManager;
import com.google.firebase.messaging.FirebaseMessaging;
import org.json.JSONObject;
import java.util.Locale;
import java.util.Objects;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends CustomActivity implements View.OnClickListener {
    private ActivitySplashBinding binding;
    private PrefManager prefManager;
    private Locale myLocale;
    private final String IsSE_PunchIN = "false";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_splash);
        initView();
    }

    private void initView() {
        setUpOnClickListener();
        setUpData();
    }

    private void setUpOnClickListener() {
        binding.buttonRetry.setOnClickListener(this);
    }
    @SuppressLint("SetTextI18n")
    private void setUpData() {
        prefManager = new PrefManager(mContext);
        FirebaseUtils.registerTopic("all");
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (task.getResult() != null) {
                    String deviceToken = task.getResult();
                    prefManager.setFirebasE_DEVICE_TOKEN(deviceToken);
                    @SuppressLint("HardwareIds") String android_id = Secure.getString(mContext.getContentResolver(), Secure.ANDROID_ID);
                    updateDTokenOnServer(android_id, deviceToken);
                }
            }
        });
        @SuppressLint("HardwareIds") String android_id = Secure.getString(mContext.getContentResolver(), Secure.ANDROID_ID);
        prefManager.setDevicE_ID(android_id);
        binding.textAppVersion.setText(getResources().getString(R.string.app_version) + " " + AppConfig.VERSION_NAME);

       // Log.e("device_token", prefManager.getFIREBASE_DEVICE_TOKEN());
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkVersion();
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

    public void setLocale(String lang) {
        myLocale = new Locale(lang);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
    }

    private void checkVersion() {
        if (Constants.isNetworkAvailable(mContext)) {
            binding.progressBar.setVisibility(View.VISIBLE);
            binding.textCheckingForUpdate.setVisibility(View.VISIBLE);
            binding.buttonRetry.setVisibility(View.GONE);
            APIService service = RetrofitInstance.getRetrofitInstance().create(APIService.class);
            Call<ModelMobileVersion> call = service.getMobileAppVersion(AppConfig.VERSION_CODE);
            call.enqueue(new Callback<ModelMobileVersion>() {
                @Override
                public void onResponse(@NonNull Call<ModelMobileVersion> call, @NonNull Response<ModelMobileVersion> response) {
                    if (response.isSuccessful()) {
                        ModelMobileVersion model = response.body();
                        if (Objects.requireNonNull(model).getStatus().equals("200")) {
                            if (AppConfig.VERSION_CODE >= model.getVersion_Code()) {
                                new Handler().postDelayed(() -> {
                                    if (prefManager.getUserLoginStatus().equals("true")) {
                                        if (prefManager.getUseR_TYPE_ID().equals("4") && prefManager.getUseR_TYPE().equalsIgnoreCase("ServiceEngineer")) {
                                            getCheckedAttendanceStatus();
                                        } else if (prefManager.getUseR_TYPE_ID().equals("8") && prefManager.getUseR_TYPE().equalsIgnoreCase("Distributor")) {
                                            startActivity(DistributorMainActivity.class);
                                        } else {
                                            startActivity(MainActivity.class);
                                        }
                                    } else {
                                        startActivity(LoginActivity.class);
                                    }
                                }, 3000);
                            } else {
                                if (!mContext.isFinishing()) {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                                    builder.setMessage(getResources().getString(R.string.new_version_is_available))
                                            .setCancelable(false)
                                            .setPositiveButton(getResources().getString(R.string.go_to_play_store), (dialog, id) -> {
                                                dialog.cancel();
                                                prefManager.setUserLoginStatus("false");
                                                prefManager.clear();
                                                FirebaseUtils.unregisterTopic("all");
                                                prefManager.setUseR_PunchIn(IsSE_PunchIN);
                                                prefManager.setUser_Id("0");
                                                prefManager.setUseR_DistrictId("0");
                                                final String appPackageName = getPackageName();
                                                try {
                                                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                                                } catch (Exception anfe) {
                                                    try {
                                                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                    }
                                                }

                                            })
                                            .setNegativeButton(getResources().getString(R.string.cancel), (dialog, i) -> {
                                                dialog.cancel();
                                                finish();
                                            });
                                    AlertDialog alert = builder.create();
                                    alert.setTitle(getResources().getString(R.string.alert));
                                    alert.show();
                                }
                            }
                        } else {
                            makeToast(model.getMessage());
                            binding.progressBar.setVisibility(View.GONE);
                            binding.textCheckingForUpdate.setVisibility(View.GONE);
                            binding.buttonRetry.setVisibility(View.VISIBLE);
                        }
                    } else {
                        makeToast(getResources().getString(R.string.error));
                        binding.progressBar.setVisibility(View.GONE);
                        binding.textCheckingForUpdate.setVisibility(View.GONE);
                        binding.buttonRetry.setVisibility(View.VISIBLE);
                    }
                }

                @Override

                public void onFailure(@NonNull Call<ModelMobileVersion> call, @NonNull Throwable t) {
                    makeToast(getResources().getString(R.string.error_message));
                    binding.progressBar.setVisibility(View.GONE);
                    binding.textCheckingForUpdate.setVisibility(View.GONE);
                    binding.buttonRetry.setVisibility(View.VISIBLE);
                }
            });
        } else {
            binding.progressBar.setVisibility(View.GONE);
            binding.textCheckingForUpdate.setVisibility(View.GONE);
            binding.buttonRetry.setVisibility(View.VISIBLE);
            makeToastLong(getResources().getString(R.string.no_internet_connection));
        }
    }

    private void getCheckedAttendanceStatus() {
        APIService service = RetrofitInstance.getRetrofitInstance().create(APIService.class);
        Call<ModelAttendance> call = service.getCheckedAttendance(prefManager.getUseR_Id(), DateTimeUtils.getCurrentDate());
        call.enqueue(new Callback<ModelAttendance>() {
            @Override
            public void onResponse(@NonNull Call<ModelAttendance> call, @NonNull Response<ModelAttendance> response) {
                if (response.isSuccessful()) {
                    ModelAttendance model = response.body();
                    if (Objects.requireNonNull(model).getStatus().equals("200")) {
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
                            //    Log.e("message", message);
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
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.buttonRetry) {
            checkVersion();
        }
    }
}

