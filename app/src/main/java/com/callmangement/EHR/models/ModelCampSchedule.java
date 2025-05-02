package com.callmangement.EHR.models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class ModelCampSchedule {
    @SerializedName("status")
    private String status;
    @SerializedName("message")
    private String message;
    @SerializedName("Data")
    private List<CampDetailsInfo> list = new ArrayList<>();

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

    public List<CampDetailsInfo> getList() {
        return list;
    }

    public void setList(List<CampDetailsInfo> list) {
        this.list = list;
    }
}
