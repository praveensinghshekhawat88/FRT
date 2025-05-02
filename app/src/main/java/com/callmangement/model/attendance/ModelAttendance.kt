package com.callmangement.model.attendance;

import com.google.gson.annotations.SerializedName;

public class ModelAttendance {
    @SerializedName("status") private String status;
    @SerializedName("message") private String message;
//    @SerializedName("data") ModelAttendanceData data = new ModelAttendanceData();

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

//    public ModelAttendanceData getData() {
//        return data;
//    }
//
//    public void setData(ModelAttendanceData data) {
//        this.data = data;
//    }
}
