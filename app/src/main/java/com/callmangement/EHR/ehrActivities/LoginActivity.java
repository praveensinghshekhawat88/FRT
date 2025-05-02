package com.callmangement.EHR.ehrActivities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import com.callmangement.R;
import com.callmangement.EHR.api.APIClient;
import com.callmangement.EHR.api.APIInterface;
import com.callmangement.EHR.models.ModelLogin;
import com.callmangement.EHR.support.OnSingleClickListener;
import com.callmangement.EHR.support.Preference;
import com.callmangement.EHR.support.Utils;
import com.callmangement.databinding.EhrActivityLoginBinding;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends BaseActivity {

    Activity mActivity;
    private EhrActivityLoginBinding binding;

    private String email = "";
    private String password = "";

    Preference preference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        binding = EhrActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        init();
    }

    private void init() {
        mActivity = this;
        preference = Preference.getInstance(mActivity);

        //Manager
     //   binding.inputEmail.setText("Manager@gmail.com");
    //    binding.inputPassword.setText("Manager@123");

        //ServiceEngineer
     //   binding.inputEmail.setText("jaipur_se17@gmail.com");
      //  binding.inputPassword.setText("jaipur@17");


        binding.inputEmail.setText("");
        binding.inputPassword.setText("");


        //SurveyUser
//        binding.inputEmail.setText("SurveyUser1@gmail.com");
//        binding.inputPassword.setText("SurveyUser1@123");

        setClickListener();
    }

    private void setClickListener() {
        binding.buttonLogin.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                email = Objects.requireNonNull(binding.inputEmail.getText()).toString().trim();
                password = Objects.requireNonNull(binding.inputPassword.getText()).toString().trim();
                if (email.isEmpty()) {
                    makeToast(getResources().getString(R.string.please_enter_email_address));
                } else if (!isValidEmail(email)) {
                    makeToast(getResources().getString(R.string.please_enter_valid_email_address));
                } else if (password.isEmpty()) {
                    makeToast(getResources().getString(R.string.please_enter_password));
                } else {
                   // String android_id = Build.SERIAL;
                @SuppressLint("HardwareIds") String android_id = Settings.Secure.getString(mActivity.getContentResolver(), Settings.Secure.ANDROID_ID);
                   // String android_id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
                    doLogin(email, password, android_id);
                    Log.d("android_idandroid_id","android_id"+android_id);
                }

            }
        });
    }

    private void doLogin(String email, String password, String device_id) {
        Log.d("device_iddevice_id"," "+device_id);

        if (Utils.isNetworkAvailable(mActivity)) {
            Utils.hideKeyboard(mActivity);
            Utils.showCustomProgressDialogCommonForAll(mActivity, getResources().getString(R.string.please_wait));
            APIInterface apiInterface = APIClient.GetRetrofitClientWithoutHeaders(mActivity, BaseUrl()).create(APIInterface.class);
            String paramStr = LoginQueryParams();
            String[] splitArray = paramStr.split(",");
            Map<String, String> paramData = new HashMap<>();
            paramData.put(splitArray[0], email);
            paramData.put(splitArray[1], password);
            paramData.put(splitArray[2], device_id);
            Call<ModelLogin> call = apiInterface.callLoginApi(Login(), paramData);
            call.enqueue(new Callback<ModelLogin>() {
                @Override
                public void onResponse(@NonNull Call<ModelLogin> call, @NonNull Response<ModelLogin> response) {
                    Utils.hideCustomProgressDialogCommonForAll();
                    if (response.isSuccessful()) {
                        if (response.code() == 200) {
                            if (response.body() != null) {
                                if (Objects.requireNonNull(response.body()).getStatus().equals("200")) {
                                    savePreferenceDataOnLogin(response.body());
                                    Intent intent = new Intent(mActivity, MainActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                } else {
                                    makeToast(response.body().getMessage());
                                }
                            }
                            else {
                                makeToast(getResources().getString(R.string.error));
                            }
                        }
                        else {
                            makeToast(getResources().getString(R.string.error));
                        }
                    }
                    else {
                        makeToast(getResources().getString(R.string.error));
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ModelLogin> call, @NonNull Throwable error) {
                    Utils.hideCustomProgressDialogCommonForAll();
                    makeToast(getResources().getString(R.string.error));
                    call.cancel();
                }
            });
        } else {
            makeToast(getResources().getString(R.string.no_internet_connection));
        }
    }

    private void savePreferenceDataOnLogin(ModelLogin data) {
        preference.putString(Preference.USER_LOGIN_STATUS, "true");
        Log.d("usertype"," "+data.getUser_details().getUserId());
        preference.putString(Preference.USER_ID, data.getUser_details().getUserId());
        preference.putString(Preference.USER_NAME, data.getUser_details().getUserName());
        preference.putString(Preference.USER_EMAIL, data.getUser_details().getEmailId());
        preference.putString(Preference.USER_Mobile, data.getUser_details().getMobileNo());
        preference.putString(Preference.USER_DISTRICT_ID, data.getUser_details().getDistrictId());
        preference.putString(Preference.USER_DISTRICT, data.getUser_details().getDistrict());
        preference.putString(Preference.USER_TYPE_ID, data.getUser_details().getUserTypeId());
        preference.putString(Preference.USER_TYPE, data.getUser_details().getUserTypeName());
    }




















    public void makeToast(String string) {
        if (TextUtils.isEmpty(string)) return;
        Toast.makeText(mActivity, string, Toast.LENGTH_SHORT).show();
    }

    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }
}