package com.callmangement.model.attendance;

import com.google.gson.annotations.SerializedName;

import org.json.JSONArray;

public class ModelAddLocation {
    @SerializedName("Status") private String Status;
    @SerializedName("Massage") private String Massage;

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public String getMassage() {
        return Massage;
    }

    public void setMassage(String massage) {
        Massage = massage;
    }
}
