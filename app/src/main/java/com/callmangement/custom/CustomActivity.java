package com.callmangement.custom;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.callmangement.R;
import com.google.gson.Gson;

public class CustomActivity extends AppCompatActivity {
    public Toolbar toolbar;
    public CustomActivity mContext;
    private ProgressDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        overridePendingTransition(R.anim.left_in, R.anim.left_out);
        Log.e("startedActivity", this.getClass().getSimpleName());
    }

    @Override
    public void setContentView(int id) {
        super.setContentView(id);
    }

    public void setupActionBar(String title) {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (!TextUtils.isEmpty(title)) {
            ActionBar bar = getSupportActionBar();
            assert bar != null;
            bar.setDisplayUseLogoEnabled(false);
            bar.setDisplayShowTitleEnabled(true);
            bar.setDisplayShowHomeEnabled(true);
            bar.setDisplayHomeAsUpEnabled(true);
            bar.setHomeButtonEnabled(true);
            bar.setTitle(title);
        }
    }

    public void setupActionBarWithSubtitle(String title, String subtitle) {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (!TextUtils.isEmpty(title)) {
            ActionBar bar = getSupportActionBar();
            assert bar != null;
            bar.setDisplayUseLogoEnabled(false);
            bar.setDisplayShowTitleEnabled(true);
            bar.setDisplayShowHomeEnabled(true);
            bar.setDisplayHomeAsUpEnabled(true);
            bar.setHomeButtonEnabled(true);
            bar.setTitle(Html.fromHtml("<small>" + title + "</small>"));
            bar.setSubtitle(Html.fromHtml("<small>" + subtitle + "</small>"));
        }
    }

    public void setupActionBar(String title, boolean showBack) {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar bar = getSupportActionBar();
        assert bar != null;
        if (showBack) {
            bar.setDisplayUseLogoEnabled(false);
            bar.setDisplayShowTitleEnabled(true);
            bar.setDisplayShowHomeEnabled(true);
            bar.setDisplayHomeAsUpEnabled(true);
            bar.setHomeButtonEnabled(true);
        }
        bar.setTitle(title);
    }

    public void restartActivity(Class<?> cls) {
        finish();
        Intent intent = new Intent(this, cls);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    public void restartActivity(Intent intent) {
        finish();
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    public void startActivity(Class<?> cls) {
        Intent intent = new Intent(this, cls);
        startActivity(intent);
    }

    public void makeToast(String string) {
        if (TextUtils.isEmpty(string)) return;
        Toast.makeText(mContext, string, Toast.LENGTH_SHORT).show();
    }

    public void makeToastLong(String string) {
        if (TextUtils.isEmpty(string)) return;
        Toast.makeText(mContext, string, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.right_in, R.anim.right_out);
    }

    public void showProgress(String message) {
        hideProgress();
        mDialog = new ProgressDialog(mContext);
        mDialog.setMessage(message);
        mDialog.setCancelable(false);
        mDialog.show();
    }

    public void showProgress() {
        hideProgress();
        mDialog = new ProgressDialog(mContext);
        mDialog.setMessage("Loading...");
        mDialog.setCancelable(false);
        mDialog.show();
    }

    public void hideProgress() {
        try {
            if (mDialog != null) mDialog.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void checkString(String value, String simpleMessage) throws Exception {
        if (TextUtils.isEmpty(value) || "0".equals(value)) {
            makeToast("Please input " + simpleMessage);
            throw new Exception();
        }
    }

    protected void checkStringInInput(String value, String simpleMessage, EditText editText) throws Exception {
        if (TextUtils.isEmpty(value) || "0".equals(value)) {
            //makeToast("Please input " + simpleMessage);
            editText.setError(simpleMessage);
            throw new Exception();
        }
    }

    public Spanned htmlString(String key) {
        return Html.fromHtml(key);
    }

    public boolean isEmailValid(CharSequence email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    public static void hideDialogKeyboard(Activity context, EditText editText) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }

    public static Object getObject(String name, Class<?> aClass) {
        Gson gson = new Gson();
        return gson.fromJson(name, aClass);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

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

    public static void showAlertDialogWithSingleButton(final Context context, final String message) {

        if (message != null) {
            SpannableString spannableString = new SpannableString(message);
            spannableString.setSpan(new RelativeSizeSpan(1.2f), 0, spannableString.length(), 0);
            spannableString.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.black)), 0, spannableString.length(), 0);

            new AlertDialog.Builder(context)
                    .setTitle(context.getResources().getString(R.string.app_name))
                    .setCancelable(false)
                    .setMessage(spannableString)
                    .setPositiveButton("OK", (dialog, which) -> {
                    })
                    .show();
        }
    }

    public static Bitmap rotateBitmap(Bitmap source, float angle)
    {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

}
