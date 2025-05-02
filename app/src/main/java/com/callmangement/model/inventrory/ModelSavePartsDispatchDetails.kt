package com.callmangement.model.inventrory;

import com.google.gson.annotations.SerializedName;

public class ModelSavePartsDispatchDetails {
    @SerializedName("status") private String status;
    @SerializedName("message") private String message;
    @SerializedName("partsDispatchDetails") private ModelPartsDispatchInvoiceList modelPartsDispatchInvoiceList = new ModelPartsDispatchInvoiceList();

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ModelPartsDispatchInvoiceList getModelPartsDispatchInvoiceList() {
        return modelPartsDispatchInvoiceList;
    }

    public void setModelPartsDispatchInvoiceList(ModelPartsDispatchInvoiceList modelPartsDispatchInvoiceList) {
        this.modelPartsDispatchInvoiceList = modelPartsDispatchInvoiceList;
    }
}
