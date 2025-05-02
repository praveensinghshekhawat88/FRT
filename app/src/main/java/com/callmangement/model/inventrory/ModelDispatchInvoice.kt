package com.callmangement.model.inventrory;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class ModelDispatchInvoice {
    @SerializedName("status") private String status;
    @SerializedName("message") private String message;
    @SerializedName("partsDispatchInvoiceList") private List<ModelPartsDispatchInvoiceList> partsDispatchInvoiceList = new ArrayList();

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

    public List<ModelPartsDispatchInvoiceList> getPartsDispatchInvoiceList() {
        return partsDispatchInvoiceList;
    }

    public void setPartsDispatchInvoiceList(List<ModelPartsDispatchInvoiceList> partsDispatchInvoiceList) {
        this.partsDispatchInvoiceList = partsDispatchInvoiceList;
    }
}
