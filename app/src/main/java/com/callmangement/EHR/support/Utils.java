package com.callmangement.EHR.support;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.PopupWindow;

import com.callmangement.R;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class Utils {

    static PopupWindow popupWindow;
    private static final int BUFFER_SIZE = 6 * 1024;
    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if (view == null) {
            view = new View(activity);
        }
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public static void hideDialogKeyboard(Activity context, EditText editText) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }

    @SuppressLint("HardwareIds")
    public static String getDeviceId(Context context) {
        return Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ANDROID_ID);
    }

    public static void openKeyboard(Activity context, EditText editText) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    public static void hideSoftKeyboard(Activity context) {
        InputMethodManager inputManager = (InputMethodManager)
                context.getSystemService(Context.INPUT_METHOD_SERVICE);
        Objects.requireNonNull(inputManager).hideSoftInputFromWindow(Objects.requireNonNull(context.getCurrentFocus()).getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = null;
        if (connectivityManager != null) {
            activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        }
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private static ProgressDialog progressDialogCommonForAll;
    public static void showCustomProgressDialogCommonForAll(Activity context, String message) {
        progressDialogCommonForAll = new ProgressDialog(context);
        progressDialogCommonForAll = new ProgressDialog(context,R.style.AppCompatAlertDialogStyle);
        SpannableString spannableString = new SpannableString(message);
        spannableString.setSpan(new RelativeSizeSpan(1.3f), 0, spannableString.length(), 0);
        spannableString.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.teal_200)), 0, spannableString.length(), 0);
        progressDialogCommonForAll.setMessage(spannableString);
        progressDialogCommonForAll.setCancelable(false);
        try {
            if (progressDialogCommonForAll != null)
                progressDialogCommonForAll.show();
        } catch (Exception e) {
        }
    }

    public static void hideCustomProgressDialogCommonForAll() {
        try {
            if (progressDialogCommonForAll != null) {
                progressDialogCommonForAll.dismiss();
            }
        } catch (Exception e) {
        }
    }

    public static String convertStringToUTF8(String s) {
        String out = null;
        out = new String(s.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1);
        return out;
    }
}




