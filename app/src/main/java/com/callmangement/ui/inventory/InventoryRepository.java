package com.callmangement.ui.inventory;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.callmangement.network.APIService;
import com.callmangement.network.RetrofitInstance;
import com.callmangement.R;
import com.callmangement.model.inventrory.ModelDispatchInvoice;
import com.callmangement.model.inventrory.ModelDisputeParts;
import com.callmangement.model.inventrory.ModelParts;
import com.callmangement.model.inventrory.ModelSEUsers;
import com.callmangement.model.inventrory.ModelSavePartsDispatchDetails;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InventoryRepository {
    private final static String TAG = "InventoryRepository";
    private final Context context;
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private final MutableLiveData<ModelParts> modelPartsMutableLiveData = new MutableLiveData<>();
    private final MutableLiveData<ModelSEUsers> modelSEUsersMutableLiveData = new MutableLiveData<>();
    private final MutableLiveData<ModelSavePartsDispatchDetails> submitDispatchParts = new MutableLiveData<>();
    private final MutableLiveData<ModelDispatchInvoice> partsDispatchInvoiceList = new MutableLiveData<>();
    private final MutableLiveData<ModelDispatchInvoice> dispatchInvoicePartsList = new MutableLiveData<>();
    private final MutableLiveData<ModelDisputeParts> modelDisputePartsMutableLiveData = new MutableLiveData<>();

    public InventoryRepository(Context context) {
        this.context = context;
    }

    public MutableLiveData<Boolean> getIsLoading(){
        return isLoading;
    }

    public MutableLiveData<ModelParts> getModelPartsMutableLiveData(){
        isLoading.setValue(true);
        APIService service = RetrofitInstance.getRetrofitInstance().create(APIService.class);
        Call<ModelParts> call = service.getPartsList();
        call.enqueue(new Callback<ModelParts>() {
            @Override
            public void onResponse(@NonNull Call<ModelParts> call, @NonNull Response<ModelParts> response) {
                if (response.isSuccessful()){
                    isLoading.setValue(false);
                    ModelParts model = response.body();
                    if (Objects.requireNonNull(model).getStatus().equals("200")) {
                        modelPartsMutableLiveData.setValue(model);
                    }else {
                        Toast.makeText(context, model.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                }else {
                    isLoading.setValue(false);
                    Toast.makeText(context, context.getResources().getString(R.string.error), Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onFailure(@NonNull Call<ModelParts> call, @NonNull Throwable t) {
                isLoading.setValue(false);
                Toast.makeText(context, context.getResources().getString(R.string.error_message), Toast.LENGTH_SHORT).show();
            }
        });
        return modelPartsMutableLiveData;
    }

    public MutableLiveData<ModelParts> getModelAvailableStockPartsMutableLiveData(String user_id, String item_id){
        isLoading.setValue(true);
        APIService service = RetrofitInstance.getRetrofitInstance().create(APIService.class);
        Call<ModelParts> call = service.getAvailableStockPartsList(user_id, item_id);
        call.enqueue(new Callback<ModelParts>() {
            @Override
            public void onResponse(@NonNull Call<ModelParts> call, @NonNull Response<ModelParts> response) {
                if (response.isSuccessful()){
                    isLoading.setValue(false);
                    ModelParts model = response.body();
                    if (Objects.requireNonNull(model).getStatus().equals("200")) {
                        modelPartsMutableLiveData.setValue(model);
                    }else {
                        Toast.makeText(context, model.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                }else {
                    isLoading.setValue(false);
                    Toast.makeText(context, context.getResources().getString(R.string.error), Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onFailure(@NonNull Call<ModelParts> call, @NonNull Throwable t) {
                isLoading.setValue(false);
                Toast.makeText(context, context.getResources().getString(R.string.error_message), Toast.LENGTH_SHORT).show();
            }
        });
        return modelPartsMutableLiveData;
    }

    public MutableLiveData<ModelParts> getPartsCurrentStockList(String user_id, String item_id){
        isLoading.setValue(true);
        APIService service = RetrofitInstance.getRetrofitInstance().create(APIService.class);
        Call<ModelParts> call = service.getPartsCurrentStockList(user_id, item_id);
        call.enqueue(new Callback<ModelParts>() {
            @Override
            public void onResponse(@NonNull Call<ModelParts> call, @NonNull Response<ModelParts> response) {
                if (response.isSuccessful()){
                    isLoading.setValue(false);
                    ModelParts model = response.body();
                    if (Objects.requireNonNull(model).getStatus().equals("200")) {
                        modelPartsMutableLiveData.setValue(model);
                    } else {
                        Toast.makeText(context, model.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                }else {
                    isLoading.setValue(false);
                    Toast.makeText(context, context.getResources().getString(R.string.error), Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onFailure(@NonNull Call<ModelParts> call, @NonNull Throwable t) {
                isLoading.setValue(false);
                Toast.makeText(context, context.getResources().getString(R.string.error_message), Toast.LENGTH_SHORT).show();
            }
        });
        return modelPartsMutableLiveData;
    }

    public MutableLiveData<ModelParts> getSEAvailableStockListForManager(String user_id, String item_id){
        isLoading.setValue(true);
        APIService service = RetrofitInstance.getRetrofitInstance().create(APIService.class);
        Call<ModelParts> call = service.getAllSE_AvlStockPartsList(user_id, item_id);
        call.enqueue(new Callback<ModelParts>() {
            @Override
            public void onResponse(@NonNull Call<ModelParts> call, @NonNull Response<ModelParts> response) {
                if (response.isSuccessful()){
                    isLoading.setValue(false);
                    ModelParts model = response.body();
                    if (Objects.requireNonNull(model).getStatus().equals("200")) {
                        modelPartsMutableLiveData.setValue(model);
                    } else {
                        Toast.makeText(context, model.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                }else {
                    isLoading.setValue(false);
                    Toast.makeText(context, context.getResources().getString(R.string.error), Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onFailure(@NonNull Call<ModelParts> call, @NonNull Throwable t) {
                isLoading.setValue(false);
                Toast.makeText(context, context.getResources().getString(R.string.error_message), Toast.LENGTH_SHORT).show();
            }
        });
        return modelPartsMutableLiveData;
    }

    public MutableLiveData<ModelParts> getAvailableStockListForSE(String user_id, String item_id){
        isLoading.setValue(true);
        APIService service = RetrofitInstance.getRetrofitInstance().create(APIService.class);
        Call<ModelParts> call = service.getSE_AvlStockPartsList(user_id, item_id);
        call.enqueue(new Callback<ModelParts>() {
            @Override
            public void onResponse(@NonNull Call<ModelParts> call, @NonNull Response<ModelParts> response) {
                if (response.isSuccessful()){
                    isLoading.setValue(false);
                    ModelParts model = response.body();
                    if (Objects.requireNonNull(model).getStatus().equals("200")) {
                        modelPartsMutableLiveData.setValue(model);
                    } else {
                        Toast.makeText(context, model.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                }else {
                    isLoading.setValue(false);
                    Toast.makeText(context, context.getResources().getString(R.string.error), Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onFailure(@NonNull Call<ModelParts> call, @NonNull Throwable t) {
                isLoading.setValue(false);
                Toast.makeText(context, context.getResources().getString(R.string.error_message), Toast.LENGTH_SHORT).show();
            }
        });
        return modelPartsMutableLiveData;
    }

    public MutableLiveData<ModelSEUsers> getModelSEUsersMutableLiveData(String districtId){
        isLoading.setValue(true);
        APIService service = RetrofitInstance.getRetrofitInstance().create(APIService.class);
        Call<ModelSEUsers> call = service.getSEUserList(districtId);
        call.enqueue(new Callback<ModelSEUsers>() {
            @Override
            public void onResponse(@NonNull Call<ModelSEUsers> call, @NonNull Response<ModelSEUsers> response) {
                if (response.isSuccessful()){
                    isLoading.setValue(false);
                    ModelSEUsers model = response.body();
                    if (Objects.requireNonNull(model).getStatus().equals("200")) {
                        modelSEUsersMutableLiveData.setValue(model);
                    }else {
                        Toast.makeText(context, model.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                }else {
                    isLoading.setValue(false);
                    Toast.makeText(context, context.getResources().getString(R.string.error), Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onFailure(@NonNull Call<ModelSEUsers> call, @NonNull Throwable t) {
                isLoading.setValue(false);
                Toast.makeText(context, context.getResources().getString(R.string.error_message), Toast.LENGTH_SHORT).show();
            }
        });
        return modelSEUsersMutableLiveData;
    }

    public MutableLiveData<ModelSavePartsDispatchDetails> getSubmitDispatchParts(String UserId, String InvoiceId, String DispatcherRemarks){
        isLoading.setValue(true);
        APIService service = RetrofitInstance.getRetrofitInstance().create(APIService.class);
        Call<ModelSavePartsDispatchDetails> call = service.submitDispatchParts(UserId, InvoiceId, DispatcherRemarks);
        call.enqueue(new Callback<ModelSavePartsDispatchDetails>() {
            @Override
            public void onResponse(@NonNull Call<ModelSavePartsDispatchDetails> call, @NonNull Response<ModelSavePartsDispatchDetails> response) {
                if (response.isSuccessful()){
                    isLoading.setValue(false);
                    ModelSavePartsDispatchDetails model = response.body();
                    if (Objects.requireNonNull(model).getStatus().equals("200")) {
                        submitDispatchParts.setValue(model);
                    }else {
                        Toast.makeText(context, model.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                }else {
                    isLoading.setValue(false);
                    Toast.makeText(context, context.getResources().getString(R.string.error), Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onFailure(@NonNull Call<ModelSavePartsDispatchDetails> call, @NonNull Throwable t) {
                isLoading.setValue(false);
                Toast.makeText(context, context.getResources().getString(R.string.error_message), Toast.LENGTH_SHORT).show();
            }
        });
        return submitDispatchParts;
    }

    public MutableLiveData<ModelDispatchInvoice> getPartsDispatchInvoiceList(String InvoiceId, String UserId, String DistrictId, String date1, String date2){
        isLoading.setValue(true);
        APIService service = RetrofitInstance.getRetrofitInstance().create(APIService.class);
        Call<ModelDispatchInvoice> call = service.getPartsDispatcherInvoices(InvoiceId, UserId, DistrictId, date1, date2);
        call.enqueue(new Callback<ModelDispatchInvoice>() {
            @Override
            public void onResponse(@NonNull Call<ModelDispatchInvoice> call, @NonNull Response<ModelDispatchInvoice> response) {
                if (response.isSuccessful()){
                    isLoading.setValue(false);
                    ModelDispatchInvoice model = response.body();
                    partsDispatchInvoiceList.setValue(model);
//                    if (Objects.requireNonNull(model).getStatus().equals("200")) {
//                        partsDispatchInvoiceList.setValue(model);
//                    }else {
//                        Toast.makeText(context, model.getMessage(), Toast.LENGTH_SHORT).show();
//                    }

                }else {
                    isLoading.setValue(false);
                    Toast.makeText(context, context.getResources().getString(R.string.error), Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onFailure(@NonNull Call<ModelDispatchInvoice> call, @NonNull Throwable t) {
                isLoading.setValue(false);
                Toast.makeText(context, context.getResources().getString(R.string.error_message), Toast.LENGTH_SHORT).show();
            }
        });
        return partsDispatchInvoiceList;
    }

    public MutableLiveData<ModelDispatchInvoice> getDispatchInvoicePartsList(String InvoiceId, String UserId, String DispatchId){
        isLoading.setValue(true);
        APIService service = RetrofitInstance.getRetrofitInstance().create(APIService.class);
        Call<ModelDispatchInvoice> call = service.getDispatchInvoiceParts(InvoiceId, UserId, DispatchId);
        call.enqueue(new Callback<ModelDispatchInvoice>() {
            @Override
            public void onResponse(@NonNull Call<ModelDispatchInvoice> call, @NonNull Response<ModelDispatchInvoice> response) {
                if (response.isSuccessful()){
                    isLoading.setValue(false);
                    ModelDispatchInvoice model = response.body();
                    dispatchInvoicePartsList.setValue(model);
                }else {
                    isLoading.setValue(false);
                    Toast.makeText(context, context.getResources().getString(R.string.error), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ModelDispatchInvoice> call, @NonNull Throwable t) {
                isLoading.setValue(false);
                Toast.makeText(context, context.getResources().getString(R.string.error_message), Toast.LENGTH_SHORT).show();
            }
        });
        return dispatchInvoicePartsList;
    }

    public MutableLiveData<ModelDisputeParts> getModelDisputePartsMutableLiveData(String UserId, String InvoiceId){
        isLoading.setValue(true);
        APIService service = RetrofitInstance.getRetrofitInstance().create(APIService.class);
        Call<ModelDisputeParts> call = service.getDisputePartsList(UserId, InvoiceId);
        call.enqueue(new Callback<ModelDisputeParts>() {
            @Override
            public void onResponse(@NonNull Call<ModelDisputeParts> call, @NonNull Response<ModelDisputeParts> response) {
                if (response.isSuccessful()){
                    isLoading.setValue(false);
                    ModelDisputeParts model = response.body();
                    modelDisputePartsMutableLiveData.setValue(model);
                }else {
                    isLoading.setValue(false);
                    Toast.makeText(context, context.getResources().getString(R.string.error), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ModelDisputeParts> call, @NonNull Throwable t) {
                isLoading.setValue(false);
                Toast.makeText(context, context.getResources().getString(R.string.error_message), Toast.LENGTH_SHORT).show();
            }
        });
        return modelDisputePartsMutableLiveData;
    }
}
