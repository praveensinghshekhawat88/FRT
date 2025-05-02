package com.callmangement.model.fps_wise_complaints;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ModelFPSDistTehWiseList implements Serializable {
    @SerializedName("status") private String status;
    @SerializedName("districtName") private String districtName;
    @SerializedName("msg") private String msg;
    @SerializedName("tehsilId") private String tehsilId;
    @SerializedName("tehsilName") private String tehsilName;
    @SerializedName("mobileNo") private String mobileNo;
    @SerializedName("complainDesc") private String complainDesc;
    @SerializedName("complainRegNo") private String complainRegNo;
    @SerializedName("fpscode") private String fpscode;
    @SerializedName("complainId") private String complainId;
    @SerializedName("fk_DistrictId") private String fk_DistrictId;
    @SerializedName("complainStatus") private String complainStatus;
    @SerializedName("customerNameEng") private String customerNameEng;
    @SerializedName("complainRegDate") private String complainRegDate;

    public String getStatus() {
        if (status == null)
            return "";
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDistrictName() {
        if (districtName == null)
            return "";
        return districtName;
    }

    public void setDistrictName(String districtName) {
        this.districtName = districtName;
    }

    public String getMsg() {
        if (msg == null)
            return "";
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getTehsilId() {
        if (tehsilId == null)
            return "";
        return tehsilId;
    }

    public void setTehsilId(String tehsilId) {
        this.tehsilId = tehsilId;
    }

    public String getTehsilName() {
        if (tehsilName == null)
            return "";
        return tehsilName;
    }

    public void setTehsilName(String tehsilName) {
        this.tehsilName = tehsilName;
    }

    public String getMobileNo() {
        if (mobileNo == null)
            return "";
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public String getComplainDesc() {
        if (complainDesc == null)
            return "";
        return complainDesc;
    }

    public void setComplainDesc(String complainDesc) {
        this.complainDesc = complainDesc;
    }

    public String getComplainRegNo() {
        if (complainRegNo == null)
            return "";
        return complainRegNo;
    }

    public void setComplainRegNo(String complainRegNo) {
        this.complainRegNo = complainRegNo;
    }

    public String getFpscode() {
        if (fpscode == null)
            return "";
        return fpscode;
    }

    public void setFpscode(String fpscode) {
        this.fpscode = fpscode;
    }

    public String getComplainId() {
        if (complainId == null)
            return "";
        return complainId;
    }

    public void setComplainId(String complainId) {
        this.complainId = complainId;
    }

    public String getFk_DistrictId() {
        if (fk_DistrictId == null)
            return "";
        return fk_DistrictId;
    }

    public void setFk_DistrictId(String fk_DistrictId) {
        this.fk_DistrictId = fk_DistrictId;
    }

    public String getComplainStatus() {
        if (complainStatus == null)
            return "";
        return complainStatus;
    }

    public void setComplainStatus(String complainStatus) {
        this.complainStatus = complainStatus;
    }

    public String getCustomerNameEng() {
        if (customerNameEng == null)
            return "";
        return customerNameEng;
    }

    public void setCustomerNameEng(String customerNameEng) {
        this.customerNameEng = customerNameEng;
    }

    public String getComplainRegDate() {
        if (complainRegDate == null)
            return "";
        return complainRegDate;
    }

    public void setComplainRegDate(String complainRegDate) {
        this.complainRegDate = complainRegDate;
    }
}
