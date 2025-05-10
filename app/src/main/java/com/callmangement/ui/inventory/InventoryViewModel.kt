package com.callmangement.ui.inventory

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.callmangement.model.inventrory.ModelDispatchInvoice
import com.callmangement.model.inventrory.ModelDisputeParts
import com.callmangement.model.inventrory.ModelParts
import com.callmangement.model.inventrory.ModelSEUsers
import com.callmangement.model.inventrory.ModelSavePartsDispatchDetails

class InventoryViewModel(application: Application) : AndroidViewModel(application) {
    private val repository =
        InventoryRepository(application.applicationContext)

    val isLoading: LiveData<Boolean>
        get() = repository.isLoading

    val partsList: LiveData<ModelParts?>
        get() = repository.getModelPartsMutableLiveData()

    fun getAvailableStockPartsList(user_id: String?, item_id: String?): LiveData<ModelParts?> {
        return repository.getModelAvailableStockPartsMutableLiveData(user_id, item_id)
    }

    fun getSEUsersList(districtId: String?): LiveData<ModelSEUsers?> {
        return repository.getModelSEUsersMutableLiveData(districtId)
    }

    fun submitDispatchParts(
        UserId: String?,
        invoiceId: String?,
        dispatchRemarks: String?
    ): LiveData<ModelSavePartsDispatchDetails?> {
        return repository.getSubmitDispatchParts(UserId, invoiceId, dispatchRemarks)
    }

    fun partsDispatchInvoiceList(
        invoiceId: String?,
        userId: String?,
        districtId: String?,
        date1: String?,
        date2: String?
    ): LiveData<ModelDispatchInvoice?> {
        return repository.getPartsDispatchInvoiceList(invoiceId, userId, districtId, date1, date2)
    }

    fun dispatchInvoicePartsList(
        invoiceId: String?,
        userId: String?,
        dispatchId: String?
    ): LiveData<ModelDispatchInvoice?> {
        return repository.getDispatchInvoicePartsList(invoiceId, userId, dispatchId)
    }

    fun getDisputePartsList(userId: String?, invoiceId: String?): LiveData<ModelDisputeParts?> {
        return repository.getModelDisputePartsMutableLiveData(userId, invoiceId)
    }

    fun getPartsCurrentStockList(user_id: String?, item_id: String?): LiveData<ModelParts?> {
        return repository.getPartsCurrentStockList(user_id, item_id)
    }

    fun getSEAvailableStockListForManager(
        user_id: String?,
        item_id: String?
    ): LiveData<ModelParts?> {
        return repository.getSEAvailableStockListForManager(user_id, item_id)
    }

    fun getAvailableStockListForSE(user_id: String?, item_id: String?): LiveData<ModelParts?> {
        return repository.getAvailableStockListForSE(user_id, item_id)
    }
}
