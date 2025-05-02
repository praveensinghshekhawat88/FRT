package com.callmangement.model.attendance;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class ModelAttendanceList {
    @SerializedName("message") private String message;
    @SerializedName("status") private String status;
    @SerializedName("Data") List<ModelAttendanceData> Data = new ArrayList<>();

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<ModelAttendanceData> getData() {
        return Data;
    }

    public void setData(List<ModelAttendanceData> data) {
        Data = data;
    }
}
