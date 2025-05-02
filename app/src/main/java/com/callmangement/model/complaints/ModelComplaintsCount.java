package com.callmangement.model.complaints;

import com.google.gson.annotations.SerializedName;

public class ModelComplaintsCount {
    @SerializedName("status") private String status;
    @SerializedName("message") private String message;
    @SerializedName("Complaints_Count") private ModelComplaintsCountData Complaints_Count = new ModelComplaintsCountData();

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

    public ModelComplaintsCountData getComplaints_Count() {
        return Complaints_Count;
    }

    public void setComplaints_Count(ModelComplaintsCountData complaints_Count) {
        Complaints_Count = complaints_Count;
    }
}
