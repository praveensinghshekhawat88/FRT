package com.callmangement.EHR.models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class ModelSEUser {
    @SerializedName("status")
    private String status;
    @SerializedName("message")
    private String message;
    @SerializedName("SEUsersList")
    private List<ModelSEUserList> SEUsersList = new ArrayList<>();

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

    public List<ModelSEUserList> getSEUsersList() {
        return SEUsersList;
    }

    public void setSEUsersList(List<ModelSEUserList> SEUsersList) {
        this.SEUsersList = SEUsersList;
    }
}
