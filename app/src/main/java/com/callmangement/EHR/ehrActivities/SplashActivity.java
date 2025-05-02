package com.callmangement.EHR.ehrActivities;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.callmangement.EHR.api.APIClient;
import com.callmangement.EHR.api.APIInterface;
import com.callmangement.R;
import com.callmangement.EHR.models.ModelMobileVersion;
import com.callmangement.EHR.support.OnSingleClickListener;
import com.callmangement.EHR.support.Preference;
import com.callmangement.EHR.support.Utils;
import com.callmangement.databinding.EhrActivitySplashBinding;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SplashActivity extends BaseActivity {
    Activity mActivity;
    private EhrActivitySplashBinding binding;
    Preference preference;
    int versionCode = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        binding = EhrActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        init();
    }

    private void init() {
        mActivity = this;
        preference = Preference.getInstance(mActivity);
        setClickListener();
        setUpData();
    }

    private void setUpData() {
        try {
            PackageInfo pInfo = mActivity.getPackageManager().getPackageInfo(getPackageName(), 0);
            String versionName = pInfo.versionName;
            binding.textAppVersion.setText(getResources().getString(R.string.app_version)+" " + versionName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        checkVersion();
    }

    private void setClickListener() {
        binding.buttonRetry.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                binding.progressBar.setVisibility(View.VISIBLE);
                binding.textCheckingForUpdate.setVisibility(View.VISIBLE);
                binding.buttonRetry.setVisibility(View.GONE);
                checkVersion();
            }
        });
    }

    private void checkVersion() {
        if (Utils.isNetworkAvailable(mActivity)) {
            Utils.hideKeyboard(mActivity);
            APIInterface apiInterface = APIClient.GetRetrofitClientWithoutHeaders(mActivity, BaseUrl()).create(APIInterface.class);
            try {
                PackageInfo pInfo = mActivity.getPackageManager().getPackageInfo(getPackageName(), 0);
                versionCode = pInfo.versionCode;
            } catch (Exception e) {
                e.printStackTrace();
            }
            String paramStr = CheckVersionQueryParams();
            String[] splitArray = paramStr.split(",");
            Map<String, String> paramData = new HashMap<>();
            paramData.put(splitArray[0], ""+versionCode);
            Call<ModelMobileVersion> call = apiInterface.callCheckVersionApi(CheckVersion(), paramData);
            call.enqueue(new Callback<ModelMobileVersion>() {
                @Override
                public void onResponse(@NonNull Call<ModelMobileVersion> call, @NonNull Response<ModelMobileVersion> response) {
                    if (response.isSuccessful()) {
                        if (response.code() == 200) {
                            if (response.body() != null) {
                                if (Objects.requireNonNull(response.body()).getStatus().equals("200")) {
                                    if(versionCode >= response.body().getVersion_Code()) {
                                        if (preference.getString(Preference.USER_LOGIN_STATUS).equals("true")) {
                                            Intent intent = new Intent(mActivity, MainActivity.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(intent);
                                        } else {
                                            Intent intent = new Intent(mActivity, LoginActivity.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(intent);
                                        }
                                    }
                                    else {
                                        if (!mActivity.isFinishing()) {
                                            AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
                                            builder.setMessage(getResources().getString(R.string.new_version_is_available))
                                                    .setCancelable(false)
                                                    .setPositiveButton(getResources().getString(R.string.go_to_play_store), (dialog, id) -> {
                                                        dialog.cancel();
                                                        clearLoginPreferenceData();
                                                        final String appPackageName = getPackageName();
                                                        try {
                                                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                                                        } catch (Exception e1) {
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
                                    makeToast(response.body().getMessage());
                                }
                            }
                            else {
                                makeToast(getResources().getString(R.string.error));
                                binding.progressBar.setVisibility(View.GONE);
                                binding.textCheckingForUpdate.setVisibility(View.GONE);
                                binding.buttonRetry.setVisibility(View.VISIBLE);

                            }
                        }
                        else {

                            makeToast(getResources().getString(R.string.error)+ response.code());
                            binding.progressBar.setVisibility(View.GONE);
                            binding.textCheckingForUpdate.setVisibility(View.GONE);
                            binding.buttonRetry.setVisibility(View.VISIBLE);

                        }
                    }
                    else {
                        makeToast(getResources().getString(R.string.servererror)+ response.code());
                        binding.progressBar.setVisibility(View.GONE);
                        binding.textCheckingForUpdate.setVisibility(View.GONE);
                        binding.buttonRetry.setVisibility(View.VISIBLE);

                    }
                }

                @Override
                public void onFailure(@NonNull Call<ModelMobileVersion> call, @NonNull Throwable error) {
                    makeToast(getResources().getString(R.string.error));
                    binding.progressBar.setVisibility(View.GONE);
                    binding.textCheckingForUpdate.setVisibility(View.GONE);
                    binding.buttonRetry.setVisibility(View.VISIBLE);
                    call.cancel();
                    Log.d("response","responseresponse"+error.getMessage());

                }
            });
        } else {
            binding.progressBar.setVisibility(View.GONE);
            binding.textCheckingForUpdate.setVisibility(View.GONE);
            binding.buttonRetry.setVisibility(View.VISIBLE);
            makeToast(getResources().getString(R.string.no_internet_connection));
        }
    }


    private void clearLoginPreferenceData() {
        preference.putString(Preference.USER_LOGIN_STATUS, "false");
        preference.putString(Preference.USER_ID, "0");
        preference.putString(Preference.USER_NAME, "");
        preference.putString(Preference.USER_EMAIL, "");
        preference.putString(Preference.USER_Mobile, "");
        preference.putString(Preference.USER_DISTRICT_ID, "0");
        preference.putString(Preference.USER_DISTRICT, "");
        preference.putString(Preference.USER_TYPE, "");
        preference.putString(Preference.USER_TYPE_ID, "");
    }

    public void makeToast(String string) {
        if (TextUtils.isEmpty(string)) return;
        Toast.makeText(mActivity, string, Toast.LENGTH_SHORT).show();
    }

}