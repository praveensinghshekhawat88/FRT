package com.callmangement.model.inventrory;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class ModelReceiveMaterialList {
    @SerializedName("status") private String status;
    @SerializedName("message") private String message;
    @SerializedName("data") private List<ModelReceiveMaterialListData> data = new ArrayList<>();

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

    public List<ModelReceiveMaterialListData> getData() {
        return data;
    }

    public void setData(List<ModelReceiveMaterialListData> data) {
        this.data = data;
    }
}
