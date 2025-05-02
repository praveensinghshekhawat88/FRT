package com.callmangement.model.inventrory;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class ModelDisputeParts {
    @SerializedName("status") private String status;
    @SerializedName("message") private String message;
    @SerializedName("partsDispueStockDetails") private List<ModelDisputePartsList> partsDispueStockDetails = new ArrayList();

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

    public List<ModelDisputePartsList> getPartsDispueStockDetails() {
        return partsDispueStockDetails;
    }

    public void setPartsDispueStockDetails(List<ModelDisputePartsList> partsDispueStockDetails) {
        this.partsDispueStockDetails = partsDispueStockDetails;
    }
}
