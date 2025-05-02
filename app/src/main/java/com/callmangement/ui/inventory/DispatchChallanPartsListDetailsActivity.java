package com.callmangement.ui.inventory;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.callmangement.Network.APIService;
import com.callmangement.Network.RetrofitInstance;
import com.callmangement.R;
import com.callmangement.adapter.DispatchChallanPartsListDetailsActivityAdapter;
import com.callmangement.custom.CustomActivity;
import com.callmangement.databinding.ActivityDispatchChallanPartsListDetailsBinding;
import com.callmangement.model.inventrory.ModelDisputePartsList;
import com.callmangement.model.inventrory.ModelPartsDispatchInvoiceList;
import com.callmangement.report_pdf.DispatchChallanPDFActivity;
import com.callmangement.ui.home.ZoomInZoomOutActivity;
import com.callmangement.utils.Constants;
import com.callmangement.utils.PrefManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DispatchChallanPartsListDetailsActivity extends CustomActivity implements View.OnClickListener {
    ActivityDispatchChallanPartsListDetailsBinding binding;
    private ModelPartsDispatchInvoiceList model;
    private List<ModelPartsDispatchInvoiceList> list = new ArrayList<>();
    private final List<ModelDisputePartsList> modelDisputePartsList = new ArrayList<>();
    private InventoryViewModel inventoryViewModel;
    private PrefManager prefManager;
    private final String invoiceId = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDispatchChallanPartsListDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initView();
    }

    private void initView() {
        binding.actionBar.ivThreeDot.setVisibility(View.GONE);
        binding.actionBar.ivBack.setVisibility(View.VISIBLE);
        binding.actionBar.layoutLanguage.setVisibility(View.GONE);
        binding.actionBar.buttonPDF.setVisibility(View.GONE);
        binding.actionBar.textToolbarTitle.setText(getResources().getString(R.string.create_new_challan));
        prefManager = new PrefManager(mContext);
        inventoryViewModel = ViewModelProviders.of(this).get(InventoryViewModel.class);

        model = (ModelPartsDispatchInvoiceList) getIntent().getSerializableExtra("param");

        if (model!=null){
            binding.textDistrictName.setText(model.getDistrictNameEng());
            binding.textUsername.setText(model.getReciverName());

            Glide.with(mContext)
                    .load(Constants.API_BASE_URL+model.getDispChalImage())
                    .placeholder(R.drawable.image_not_fount)
                    .into(binding.ivChallanImage);

            Glide.with(mContext)
                    .load(Constants.API_BASE_URL+model.getPartsImage_1())
                    .placeholder(R.drawable.image_not_fount)
                    .into(binding.ivPartsImage1);

            Glide.with(mContext)
                    .load(Constants.API_BASE_URL+model.getPartsImage_2())
                    .placeholder(R.drawable.image_not_fount)
                    .into(binding.ivPartsImage2);

            binding.inputCourierName.setText(model.getCourierName());
            binding.inputCourierTrackingNo.setText(model.getCourierTrackingNo());

//            binding.inputRemark.setText(model.getDispatcherRemarks());

            /*if (model.getIsReceived().equalsIgnoreCase("true")){
                binding.seRemarkLayout.setVisibility(View.VISIBLE);
                binding.inputSERemark.setText(model.getReceiverRemark());
            } else {
                binding.seRemarkLayout.setVisibility(View.GONE);
            }*/

            if (model.isReceived.equalsIgnoreCase("true")){
                binding.sePartsImageLay.setVisibility(View.VISIBLE);
                Glide.with(mContext)
                        .load(Constants.API_BASE_URL+model.getReceivedPartsImage())
                        .placeholder(R.drawable.image_not_fount)
                        .into(binding.ivSEPartsImage);
            } else {
                binding.sePartsImageLay.setVisibility(View.GONE);
            }

            if (model.isSubmitted.equalsIgnoreCase("true")){
//                binding.buttonDispatch.setVisibility(View.GONE);
                binding.actionBar.buttonPDF.setVisibility(View.VISIBLE);
            } else if (model.isSubmitted.equalsIgnoreCase("false")){
//                binding.buttonDispatch.setVisibility(View.VISIBLE);
                binding.actionBar.buttonPDF.setVisibility(View.GONE);
            }
        }

        onClickListener();
        dispatchInvoicePartsList();
//        getDisputePartsList();
    }

    private void onClickListener(){
//        binding.buttonDispatch.setOnClickListener(this);
        binding.actionBar.ivBack.setOnClickListener(this);
        binding.actionBar.buttonPDF.setOnClickListener(this);
        binding.ivChallanImage.setOnClickListener(this);
        binding.ivPartsImage1.setOnClickListener(this);
        binding.ivPartsImage2.setOnClickListener(this);
        binding.ivSEPartsImage.setOnClickListener(this);
//        binding.buttonSendAgainToSE.setOnClickListener(this);
//        binding.buttonSendToStock.setOnClickListener(this);
    }

    private void dispatchInvoicePartsList(){
        if (Constants.isNetworkAvailable(mContext)) {
            isLoading();
            inventoryViewModel.dispatchInvoicePartsList(model.getInvoiceId(), "0", "0").observe(this, modelDispatchInvoice -> {
                isLoading();
                if (modelDispatchInvoice.status.equals("200")) {
                    list = modelDispatchInvoice.partsDispatchInvoiceList;
                    if (list.size() > 0) {
                        binding.challanRecycler.setVisibility(View.VISIBLE);
                        binding.textNoRecordFound.setVisibility(View.GONE);
                        setDispatchInvoicePartsListAdapter();
                    } else {
                        binding.challanRecycler.setVisibility(View.GONE);
                        binding.textNoRecordFound.setVisibility(View.VISIBLE);
                    }
                }
            });
        } else {
            makeToast(getResources().getString(R.string.no_internet_connection));
        }
    }

    private void setDispatchInvoicePartsListAdapter(){
        binding.challanRecycler.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL,false));
        binding.challanRecycler.setAdapter(new DispatchChallanPartsListDetailsActivityAdapter(mContext, list));
    }

    /*private void getDisputePartsList() {
        modelDisputePartsList.clear();
        if (Constants.isNetworkAvailable(mContext)) {
            isLoading();
            inventoryViewModel.getDisputePartsList(prefManager.getUSER_Id(), model.getInvoiceId()).observe(this, modelDisputeParts -> {
                isLoading();
                if (modelDisputeParts.getStatus().equals("200")) {
                    modelDisputePartsList = modelDisputeParts.getPartsDispueStockDetails();
                    if (modelDisputePartsList.size() > 0) {
                        binding.layoutDisputeStockNoReceive.setVisibility(View.VISIBLE);
                        setDisputeStockNoReceivePartsListAdapter();
                    } else {
                        binding.layoutDisputeStockNoReceive.setVisibility(View.GONE);
                    }
                }
            });
        } else {
            makeToast(getResources().getString(R.string.no_internet_connection));
        }
    }*/

    /*private void updatePartsStock(){
        if (Constants.isNetworkAvailable(mContext)) {
            String disputeRemark = binding.inputDisputeRemark.getText().toString().trim();
            if (disputeRemark.isEmpty()) {
                makeToast(getResources().getString(R.string.please_enter_remark));
            } else {
                JSONArray jsonArray = new JSONArray();
                for (ModelDisputePartsList model : modelDisputePartsList) {
                    try {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("itemId", model.getItemId());
                        jsonObject.put("item_Qty", model.getItem_Qty());
                        jsonArray.put(jsonObject);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                RequestBody jsonArrBody = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), String.valueOf(jsonArray));
                APIService service = RetrofitInstance.getRetrofitInstance().create(APIService.class);
                showProgress();
                Call<ResponseBody> call = service.updatePartsStock(model.getInvoiceId(), prefManager.getUSER_Id(), "2", disputeRemark, jsonArrBody);
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
                                            getDisputePartsList();
                                        }else {
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
            }
        } else {
            makeToast(getResources().getString(R.string.no_internet_connection));
        }
    }*/

    /*private void setDisputeStockNoReceivePartsListAdapter(){
        binding.rvDisputeStockNoReceive.setLayoutManager(new LinearLayoutManager(mContext,LinearLayoutManager.VERTICAL,false));
        binding.rvDisputeStockNoReceive.setAdapter(new DisputeStockNoReceiveAdapter(mContext, modelDisputePartsList));
    }*/

   /* private void submitDispatchParts(){
        if (Constants.isNetworkAvailable(mContext)) {
            isLoading();
            inventoryViewModel.submitDispatchParts(prefManager.getUSER_Id(), model.getInvoiceId(), model.getDispatcherRemarks()).observe(this, modelInventoryResponse -> {
                isLoading();
                if (modelInventoryResponse.getStatus().equals("200")) {
                    Constants.modelPartsDispatchInvoiceList = list;
                    startActivity(new Intent(mContext, DispatchChallanPDFActivity.class)
                            .putExtra("invoiceId", model.getInvoiceId())
                            .putExtra("dispatchFrom", prefManager.getUSER_NAME())
                            .putExtra("email", prefManager.getUSER_EMAIL())
                            .putExtra("dispatchTo", model.getDistrictNameEng())
                            .putExtra("username", model.getReciverName()));
                    finish();
                }
            });
        } else {
            makeToast(getResources().getString(R.string.no_internet_connection));
        }
    }*/

    /*private void createDisputeDispatchJSONArray() {
        try {
            JSONArray jsonArray = new JSONArray();
            for (ModelDisputePartsList list : modelDisputePartsList) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("DispatchId", list.getDispatchId());
                jsonObject.put("InvoiceId", list.getInvoiceId());
                jsonObject.put("ItemId", list.getItemId());
                jsonObject.put("Item_Qty", list.getItem_Qty());
                jsonArray.put(jsonObject);
            }
            savePartsDispatchDetails(modelDisputePartsList, jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void savePartsDispatchDetails(List<ModelDisputePartsList> disputePartsList, JSONArray jsonArray) {
        if (Constants.isNetworkAvailable(mContext)) {
            String inputRemark = binding.inputDisputeRemark.getText().toString().trim();
            if (model.getDistrictNameEng().isEmpty()) {
                makeToast(getResources().getString(R.string.please_select_district));
            } else if (model.getReciverName().isEmpty()) {
                makeToast(getResources().getString(R.string.please_select_username));
            } else if (inputRemark.isEmpty()) {
                makeToast(getResources().getString(R.string.please_enter_remark));
            } *//*else if (imageStoragePath.isEmpty()){
                makeToast(getResources().getString(R.string.please_select_image));
            }*//* else {
                RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), String.valueOf(jsonArray));
                APIService service = RetrofitInstance.getRetrofitInstance().create(APIService.class);
                Call<ResponseBody> call = service.savePartsDispatchDetails(invoiceId, prefManager.getUSER_Id(), model.getDispatchTo(), model.getDistrictId(), "", inputRemark,"8", body);
                showProgress();
                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                        hideProgress();
                        if (response.isSuccessful()) {
                            if (response.code() == 200) {
                                try {
                                    JSONObject jsonObject = new JSONObject(Objects.requireNonNull(response.body()).string());
                                    String status = jsonObject.getString("status");
                                    String message = jsonObject.getString("message");
                                    String invoiceId = jsonObject.getJSONObject("partsDispatchDetails").getString("invoiceId");
                                    if (status.equals("200")) {
                                        dialogDisputeDispatchChallan(disputePartsList, inputRemark, invoiceId);
                                    } else {
                                        makeToast(message);
                                    }
                                } catch (JSONException | IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        } else {
                            makeToast(getResources().getString(R.string.error));
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                        hideProgress();
                        makeToast(getResources().getString(R.string.error_message));
                    }
                });
            }
        }else {
            makeToast(getResources().getString(R.string.no_internet_connection));
        }
    }

    @SuppressLint("SetTextI18n")
    private void dialogDisputeDispatchChallan(List<ModelDisputePartsList> disputePartsList, String inputRemark, String invoiceId) {
        try {
            final Dialog dialog = new Dialog(mContext);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.dialog_challan_for_dispatch);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.setCanceledOnTouchOutside(false);

            TextView invoice_id = dialog.findViewById(R.id.textInvoiceNumber);
            TextView dispatchFrom = dialog.findViewById(R.id.textDispatchFrom);
            TextView dispatchTo = dialog.findViewById(R.id.textDispatchTo);
            TextView username = dialog.findViewById(R.id.textUsername);
            RecyclerView rvDispatchChallan = dialog.findViewById(R.id.rv_dispatch_challan);
//            Button buttonSave = dialog.findViewById(R.id.buttonSave);
            Button buttonDispatch = dialog.findViewById(R.id.buttonDispatch);

            invoice_id.setText(invoiceId);
            dispatchFrom.setText(prefManager.getUSER_NAME());
            dispatchTo.setText(model.getDistrictNameEng());
            username.setText(model.getReceiverRemark());

            rvDispatchChallan.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
            rvDispatchChallan.setAdapter(new DialogChallanForDisputeDispatchAdapter(mContext, disputePartsList));

            *//*buttonSave.setOnClickListener(view -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setMessage(getResources().getString(R.string.are_you_sure_you_want_to_save_this_invoice))
                        .setCancelable(false)
                        .setPositiveButton(getResources().getString(R.string.yes), (dlg, id) -> {
                            dialog.dismiss();
                            dlg.dismiss();
                            onBackPressed();
                        })
                        .setNegativeButton(getResources().getString(R.string.no), (dlg, id) -> {
                            dlg.dismiss();
                        });
                AlertDialog alert = builder.create();
                alert.setTitle(getResources().getString(R.string.alert));
                alert.show();
            });*//*

            buttonDispatch.setOnClickListener(view -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setMessage(getResources().getString(R.string.are_you_sure_you_want_to_dispatch_this_invoice))
                        .setCancelable(false)
                        .setPositiveButton(getResources().getString(R.string.yes), (dlg, id) -> {
                            dialog.dismiss();
                            dlg.dismiss();
                            submitDispatchParts(disputePartsList, inputRemark, invoiceId);
                        })
                        .setNegativeButton(getResources().getString(R.string.no), (dlg, id) -> {
                            dlg.dismiss();
                        });
                AlertDialog alert = builder.create();
                alert.setTitle(getResources().getString(R.string.alert));
                alert.show();
            });

            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void submitDispatchParts(List<ModelDisputePartsList> disputePartsList, String inputRemark, String invoiceId) {
        if (Constants.isNetworkAvailable(mContext)) {
            isLoading();
            inventoryViewModel.submitDispatchParts(prefManager.getUSER_Id(), invoiceId, inputRemark).observe(this, modelInventoryResponse -> {
                isLoading();
                if (modelInventoryResponse.getStatus().equals("200")) {
                    Constants.modelDisputePartsList = disputePartsList;
                    startActivity(new Intent(mContext, ChallanPDFActivity.class)
                            .putExtra("invoiceId", invoiceId)
                            .putExtra("dispatchFrom", prefManager.getUSER_NAME())
                            .putExtra("email", prefManager.getUSER_EMAIL())
                            .putExtra("dispatchTo", model.getDistrictNameEng())
                            .putExtra("username", model.getReciverName())
                            .putExtra(Constants.fromWhere, "DispatchChallanPartsListDetailsActivity"));
                    finish();
                }
            });
        } else {
            makeToast(getResources().getString(R.string.no_internet_connection));
        }
    }*/

    private void isLoading(){
        inventoryViewModel.getIsLoading().observe(this, aBoolean -> {
            if (aBoolean){
                showProgress(getResources().getString(R.string.please_wait));
            } else {
                hideProgress();
            }
        });
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.buttonPDF) {
            Constants.modelPartsDispatchInvoiceList = list;
            startActivity(new Intent(mContext, DispatchChallanPDFActivity.class)
                    .putExtra("invoiceId", model.getInvoiceId())
                    .putExtra("dispatchFrom", prefManager.getUSER_NAME())
                    .putExtra("email", prefManager.getUSER_EMAIL())
                    .putExtra("dispatchTo", model.getDistrictNameEng())
                    .putExtra("username", model.getReciverName())
                    .putExtra("datetime", model.getDispatchDateStr())
                    .putExtra("courierName", model.getCourierName())
                    .putExtra("courierTrackingNo", model.getCourierTrackingNo()));
            finish();
        } else if (id == R.id.ivChallanImage){
            startActivity(new Intent(mContext, ZoomInZoomOutActivity.class).putExtra("image", Constants.API_BASE_URL+model.getDispChalImage()));
        } else if (id == R.id.ivPartsImage1){
            startActivity(new Intent(mContext, ZoomInZoomOutActivity.class).putExtra("image", Constants.API_BASE_URL+model.getPartsImage_1()));
        } else if (id == R.id.ivPartsImage2){
            startActivity(new Intent(mContext, ZoomInZoomOutActivity.class).putExtra("image", Constants.API_BASE_URL+model.getPartsImage_2()));
        } else if (id == R.id.ivSEPartsImage){
            startActivity(new Intent(mContext, ZoomInZoomOutActivity.class).putExtra("image", Constants.API_BASE_URL+model.getReceivedPartsImage()));
        }/* else if (id == R.id.buttonDispatch){
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setMessage(getResources().getString(R.string.are_you_sure_you_want_to_dispatch_this_invoice))
                    .setCancelable(false)
                    .setPositiveButton(getResources().getString(R.string.yes), (dlg, ids) -> {
                        dlg.dismiss();
                        submitDispatchParts();
                    })
                    .setNegativeButton(getResources().getString(R.string.no), (dlg, ids) -> {
                        dlg.dismiss();
                    });
            AlertDialog alert = builder.create();
            alert.setTitle(getResources().getString(R.string.alert));
            alert.show();
        } else if (id == R.id.buttonSendAgainToSE){
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setMessage(getResources().getString(R.string.message_send_againg_to_se))
                    .setCancelable(false)
                    .setPositiveButton(getResources().getString(R.string.yes), (dlg, ids) -> {
                        dlg.dismiss();
                        createDisputeDispatchJSONArray();
                    })
                    .setNegativeButton(getResources().getString(R.string.no), (dlg, ids) -> {
                        dlg.dismiss();
                    });
            AlertDialog alert = builder.create();
            alert.setTitle(getResources().getString(R.string.alert));
            alert.show();
        } else if (id == R.id.buttonSendToStock){
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setMessage(getResources().getString(R.string.message_send_to_stock))
                    .setCancelable(false)
                    .setPositiveButton(getResources().getString(R.string.yes), (dlg, ids) -> {
                        dlg.dismiss();
                        updatePartsStock();
                    })
                    .setNegativeButton(getResources().getString(R.string.no), (dlg, ids) -> {
                        dlg.dismiss();
                    });
            AlertDialog alert = builder.create();
            alert.setTitle(getResources().getString(R.string.alert));
            alert.show();
        }*/ else if (id == R.id.iv_back){
            onBackPressed();
        }
    }
}