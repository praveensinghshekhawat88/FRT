package com.callmangement.model.reports;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class SLA_ReportsDetails_Info implements Serializable {
    @SerializedName("district") private String district;
    @SerializedName("districtId") private Integer districtId;
    @SerializedName("complaintCount") private Integer complaintCount;

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public Integer getDistrictId() {
        return districtId;
    }

    public void setDistrictId(Integer districtId) {
        this.districtId = districtId;
    }

    public Integer getComplaintCount() {
        return complaintCount;
    }

    public void setComplaintCount(Integer complaintCount) {
        this.complaintCount = complaintCount;
    }
}
