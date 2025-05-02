package com.callmangement.model.attendance;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class ModelAddLocationList {
    @SerializedName("Status") private String Status;
    @SerializedName("Massage") private String Massage;
    @SerializedName("location_list") List<ModelAddLocationData> location_list = new ArrayList<>();

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

    public List<ModelAddLocationData> getLocation_list() {
        return location_list;
    }

    public void setLocation_list(List<ModelAddLocationData> location_list) {
        this.location_list = location_list;
    }
}
