package com.callmangement.ui.inventory;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.callmangement.model.inventrory.ModelDispatchInvoice;
import com.callmangement.model.inventrory.ModelDisputeParts;
import com.callmangement.model.inventrory.ModelParts;
import com.callmangement.model.inventrory.ModelSEUsers;
import com.callmangement.model.inventrory.ModelSavePartsDispatchDetails;

public class InventoryViewModel extends AndroidViewModel {
    private final InventoryRepository repository;

    public InventoryViewModel(@NonNull Application application) {
        super(application);
        repository = new InventoryRepository(application.getApplicationContext());
    }

    public LiveData<Boolean> getIsLoading(){
        return repository.getIsLoading();
    }

    public LiveData<ModelParts> getPartsList(){
        return repository.getModelPartsMutableLiveData();
    }

    public LiveData<ModelParts> getAvailableStockPartsList(String user_id, String item_id){
        return repository.getModelAvailableStockPartsMutableLiveData(user_id, item_id);
    }

    public LiveData<ModelSEUsers> getSEUsersList(String districtId){
        return repository.getModelSEUsersMutableLiveData(districtId);
    }

    public LiveData<ModelSavePartsDispatchDetails> submitDispatchParts(String UserId, String invoiceId, String dispatchRemarks){
        return repository.getSubmitDispatchParts(UserId, invoiceId, dispatchRemarks);
    }

    public LiveData<ModelDispatchInvoice> partsDispatchInvoiceList(String invoiceId, String userId, String districtId, String date1, String date2){
        return repository.getPartsDispatchInvoiceList(invoiceId, userId, districtId, date1, date2);
    }

    public LiveData<ModelDispatchInvoice> dispatchInvoicePartsList(String invoiceId, String userId, String dispatchId){
        return repository.getDispatchInvoicePartsList(invoiceId, userId, dispatchId);
    }

    public LiveData<ModelDisputeParts> getDisputePartsList(String userId, String invoiceId){
        return repository.getModelDisputePartsMutableLiveData(userId, invoiceId);
    }

    public LiveData<ModelParts> getPartsCurrentStockList(String user_id, String item_id){
        return repository.getPartsCurrentStockList(user_id, item_id);
    }

    public LiveData<ModelParts> getSEAvailableStockListForManager(String user_id, String item_id){
        return repository.getSEAvailableStockListForManager(user_id, item_id);
    }

    public LiveData<ModelParts> getAvailableStockListForSE(String user_id, String item_id){
        return repository.getAvailableStockListForSE(user_id, item_id);
    }

}
