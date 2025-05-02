package com.callmangement.model.fps_wise_complaints;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class ModelFPSComplaint {
    @SerializedName("status") private String status;
    @SerializedName("message") private String message;
    @SerializedName("fPSComplainHistoryReqList") private List<ModelFPSComplaintList> fPSComplainHistoryReqList = new ArrayList<>();

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

    public List<ModelFPSComplaintList> getfPSComplainHistoryReqList() {
        return fPSComplainHistoryReqList;
    }

    public void setfPSComplainHistoryReqList(List<ModelFPSComplaintList> fPSComplainHistoryReqList) {
        this.fPSComplainHistoryReqList = fPSComplainHistoryReqList;
    }
}
