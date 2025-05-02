package com.callmangement.model.reports;

import com.callmangement.model.complaints.ModelComplaintRegistrationDate;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ModelSLAReportDetailsList implements Serializable {
    @SerializedName("complainStatusId") private String complainStatusId;
    @SerializedName("complainStatus") private String complainStatus;
    @SerializedName("fpscode") private String fpscode;
    @SerializedName("mobileNo") private String mobileNo;
    @SerializedName("customerName") private String customerName;
    @SerializedName("complainRegNo") private String complainRegNo;
    @SerializedName("tehsil") private String tehsil;
    @SerializedName("customerId") private String customerId;
    @SerializedName("complainRegDateStr") private String complainRegDateStr;
    @SerializedName("sermarkDateStr") private String sermarkDateStr;
    @SerializedName("resolveTime") private Integer resolveTime;

    public String getComplainStatusId() {
        return complainStatusId;
    }

    public void setComplainStatusId(String complainStatusId) {
        this.complainStatusId = complainStatusId;
    }

    public String getComplainStatus() {
        return complainStatus;
    }

    public void setComplainStatus(String complainStatus) {
        this.complainStatus = complainStatus;
    }

    public String getFpscode() {
        return fpscode;
    }

    public void setFpscode(String fpscode) {
        this.fpscode = fpscode;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getComplainRegNo() {
        return complainRegNo;
    }

    public void setComplainRegNo(String complainRegNo) {
        this.complainRegNo = complainRegNo;
    }

    public String getTehsil() {
        return tehsil;
    }

    public void setTehsil(String tehsil) {
        this.tehsil = tehsil;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getComplainRegDateStr() {
        return complainRegDateStr;
    }

    public void setComplainRegDateStr(String complainRegDateStr) {
        this.complainRegDateStr = complainRegDateStr;
    }

    public String getSermarkDateStr() {
        return sermarkDateStr;
    }

    public void setSermarkDateStr(String sermarkDateStr) {
        this.sermarkDateStr = sermarkDateStr;
    }

    public Integer getResolveTime() {
        return this.resolveTime;
    }

    public void setResolveTime(Integer resolveTime) {
        this.resolveTime = resolveTime;
    }
}
