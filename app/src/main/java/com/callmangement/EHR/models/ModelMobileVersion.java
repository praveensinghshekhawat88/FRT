package com.callmangement.EHR.models;

import com.google.gson.annotations.SerializedName;

public class ModelMobileVersion {
    @SerializedName("status")
    private String status;
    @SerializedName("message")
    private String message;
    @SerializedName("Version_Code")
    private int Version_Code;

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

    public int getVersion_Code() {
        return Version_Code;
    }

    public void setVersion_Code(int version_Code) {
        Version_Code = version_Code;
    }
}
