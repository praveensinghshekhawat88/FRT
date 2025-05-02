package com.callmangement.model.complaints;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UpdateResponse {
    @SerializedName("id")
    @Expose
    private transient String id;
    @SerializedName("seRemark")
    @Expose
    private String seRemark;
    @SerializedName("complaintRegNo")
    @Expose
    private String complaintRegNo;

    public String getSeRemark() {
        return seRemark;
    }

    public void setSeRemark(String seRemark) {
        this.seRemark = seRemark;
    }

    public String getComplaintRegNo() {
        return complaintRegNo;
    }

    public void setComplaintRegNo(String complaintRegNo) {
        this.complaintRegNo = complaintRegNo;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
