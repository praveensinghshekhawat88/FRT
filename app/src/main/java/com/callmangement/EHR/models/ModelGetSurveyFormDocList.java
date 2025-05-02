package com.callmangement.EHR.models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class ModelGetSurveyFormDocList {
    @SerializedName("status")
    private String status;
    @SerializedName("message")
    private String message;
    @SerializedName("Data")
    private ArrayList<SurveyFormDocInfo> list = new ArrayList<>();

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

    public ArrayList<SurveyFormDocInfo> getList() {
        return list;
    }

    public void setList(ArrayList<SurveyFormDocInfo> list) {
        this.list = list;
    }
}
