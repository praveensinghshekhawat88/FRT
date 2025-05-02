package com.callmangement.EHR.models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class ModelSurveyFormListing {
    @SerializedName("status")
    private String status;
    @SerializedName("message")
    private String message;
    @SerializedName("Data")
    private List<SurveyFormDetailsInfo> list = new ArrayList<>();

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

    public List<SurveyFormDetailsInfo> getList() {
        return list;
    }

    public void setList(List<SurveyFormDetailsInfo> list) {
        this.list = list;
    }
}
