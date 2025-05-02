package com.callmangement.EHR.models;

import com.google.gson.annotations.SerializedName;

public class ModelLogin {
    @SerializedName("status")
    private String status;
    @SerializedName("message")
    private String message;
    @SerializedName("user_details")
    ModelLoginData user_details = new ModelLoginData();

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

    public ModelLoginData getUser_details() {
        return user_details;
    }

    public void setUser_details(ModelLoginData user_details) {
        this.user_details = user_details;
    }
}
