package com.callmangement.EHR.models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class ModelAllDealersByBlock {
    @SerializedName("status")
    private String status;
    @SerializedName("message")
    private String message;
    @SerializedName("Data")
    private ArrayList<DealersInfo> list = new ArrayList<>();

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

    public ArrayList<DealersInfo> getList() {
        return list;
    }

    public void setList(ArrayList<DealersInfo> list) {
        this.list = list;
    }
}
