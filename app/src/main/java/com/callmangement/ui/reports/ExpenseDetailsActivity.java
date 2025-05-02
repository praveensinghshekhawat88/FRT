package com.callmangement.ui.reports;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.callmangement.network.APIService;
import com.callmangement.network.RetrofitInstance;
import com.callmangement.R;
import com.callmangement.custom.CustomActivity;
import com.callmangement.databinding.ActivityExpenseDetailsBinding;
import com.callmangement.model.expense.ModelExpensesList;
import com.callmangement.ui.home.ZoomInZoomOutActivity;
import com.callmangement.utils.Constants;
import com.callmangement.utils.PrefManager;
import org.json.JSONObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ExpenseDetailsActivity extends CustomActivity implements View.OnClickListener {
    private ActivityExpenseDetailsBinding binding;
    private ModelExpensesList model;
    private PrefManager prefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityExpenseDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initView();
    }

    private void initView() {
        prefManager = new PrefManager(mContext);
        binding.actionBar.ivBack.setVisibility(View.VISIBLE);
        binding.actionBar.ivThreeDot.setVisibility(View.GONE);
        binding.actionBar.layoutLanguage.setVisibility(View.GONE);
        binding.actionBar.textToolbarTitle.setText(getResources().getString(R.string.expense_detail));
        setUpOnClickListener();
        getIntentData();
        setUpData();
    }

    private void setUpOnClickListener() {
        binding.actionBar.ivBack.setOnClickListener(this);
        binding.ivChallanImage.setOnClickListener(this);
        binding.buttonComplete.setOnClickListener(this);
    }

    private void getIntentData() {
        model = (ModelExpensesList) getIntent().getSerializableExtra("param");
    }

    private void setUpData(){
        binding.inputName.setText(model.getSeName());
        binding.inputDistrict.setText(model.getDistrict());

        binding.inputDocketno.setText(model.docketNo);
        binding.inputcouriername.setText(model.courierName);

        binding.inputRemark.setText(model.getRemark());
        binding.inputRemark.setText(model.getRemark());
        binding.inputTotalExpenseAmount.setText(String.valueOf(model.totalExAmount));
        binding.inputExpenseDate.setText(model.getCreatedOnStr());

        if (prefManager.getUSER_TYPE_ID().equals("1") && prefManager.getUSER_TYPE().equalsIgnoreCase("Admin")) {
            binding.buttonComplete.setVisibility(View.VISIBLE);
        } else if (prefManager.getUSER_TYPE_ID().equals("2") && prefManager.getUSER_TYPE().equalsIgnoreCase("Manager")) {
            binding.buttonComplete.setVisibility(View.VISIBLE);
        } else {
            binding.buttonComplete.setVisibility(View.GONE);
        }

        if (model.getCompletedOnStr().isEmpty()){
            binding.expenseCompletedDateLay.setVisibility(View.GONE);
        } else {
            binding.expenseCompletedDateLay.setVisibility(View.VISIBLE);
            binding.inputExpenseCompletedDate.setText(model.getCompletedOnStr());
        }

        if (model.getExpenseStatusID() == 2) {
            binding.buttonComplete.setVisibility(View.GONE);
        }

        Glide.with(mContext)
                .load(Constants.API_BASE_URL+ model.filePath)
                .placeholder(R.drawable.image_not_fount)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(binding.ivChallanImage);

    }

    private void completeExpense() {
        if (Constants.isNetworkAvailable(mContext)) {
            showProgress();
            APIService service = RetrofitInstance.getRetrofitInstance().create(APIService.class);
            Call<ResponseBody> call = service.completeExpenses(prefManager.getUSER_Id(), String.valueOf(model.getExpenseId()));
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                    hideProgress();
                    try {
                        if (response.isSuccessful()) {
                            if (response.code() == 200) {
                                if (response.body() != null) {
                                    String responseStr = response.body().string();
                                    JSONObject jsonObject = new JSONObject(responseStr);
                                    String status = jsonObject.optString("status");
                                    String message = jsonObject.optString("message");
                                    if (status.equals("200")) {
                                        Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
                                        onBackPressed();
                                    } else {
                                        makeToast(message);
                                    }
                                }
                            }
                        } else {
                            makeToast(getResources().getString(R.string.error));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                    hideProgress();
                    makeToast(getResources().getString(R.string.error_message));
                }
            });
        } else
            makeToast(getResources().getString(R.string.no_internet_connection));
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.iv_back){
            onBackPressed();
        } else if (id == R.id.ivChallanImage){
            startActivity(new Intent(mContext, ZoomInZoomOutActivity.class).putExtra("image", Constants.API_BASE_URL+ model.filePath));
        } else if (id == R.id.buttonComplete) {
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setMessage(getResources().getString(R.string.complete_expense_dialog_msg))
                    .setCancelable(false)
                    .setPositiveButton(getResources().getString(R.string.ok), (dialog, ids) -> {
                        dialog.cancel();
                        completeExpense();
                    })
                    .setNegativeButton(getResources().getString(R.string.cancel), (dialog, ids) -> dialog.cancel());
            AlertDialog alert = builder.create();
            alert.setTitle(getResources().getString(R.string.alert));
            alert.show();
        }
    }
}