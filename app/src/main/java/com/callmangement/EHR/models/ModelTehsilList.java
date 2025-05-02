package com.callmangement.EHR.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ModelTehsilList implements Serializable {
    @SerializedName("mobileNo")
    private String mobileNo;
    @SerializedName("updatedBy")
    private String updatedBy;
    @SerializedName("createdBy")
    private String createdBy;
    @SerializedName("updatedOn")
    private String updatedOn;
    @SerializedName("fk_DistrictId")
    private String fk_DistrictId;
    @SerializedName("orderBySrNo")
    private String orderBySrNo;
    @SerializedName("isActive")
    private String isActive;
    @SerializedName("createdOn")
    private String createdOn;
    @SerializedName("tehsilId")
    private String tehsilId;
    @SerializedName("districtNameHi")
    private String districtNameHi;
    @SerializedName("districtNameEng")
    private String districtNameEng;
    @SerializedName("tehsilNameEng")
    private String tehsilNameEng;
    @SerializedName("tehsilEMail")
    private String tehsilEMail;
    @SerializedName("tehsilNameHi")
    private String tehsilNameHi;
    @SerializedName("tehsilCode")
    private String tehsilCode;

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getUpdatedOn() {
        return updatedOn;
    }

    public void setUpdatedOn(String updatedOn) {
        this.updatedOn = updatedOn;
    }

    public String getFk_DistrictId() {
        return fk_DistrictId;
    }

    public void setFk_DistrictId(String fk_DistrictId) {
        this.fk_DistrictId = fk_DistrictId;
    }

    public String getOrderBySrNo() {
        return orderBySrNo;
    }

    public void setOrderBySrNo(String orderBySrNo) {
        this.orderBySrNo = orderBySrNo;
    }

    public String getIsActive() {
        return isActive;
    }

    public void setIsActive(String isActive) {
        this.isActive = isActive;
    }

    public String getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(String createdOn) {
        this.createdOn = createdOn;
    }

    public String getTehsilId() {
        if (tehsilId == null)
            return "0";
        return tehsilId;
    }

    public void setTehsilId(String tehsilId) {
        this.tehsilId = tehsilId;
    }

    public String getDistrictNameHi() {
        return districtNameHi;
    }

    public void setDistrictNameHi(String districtNameHi) {
        this.districtNameHi = districtNameHi;
    }

    public String getDistrictNameEng() {
        return districtNameEng;
    }

    public void setDistrictNameEng(String districtNameEng) {
        this.districtNameEng = districtNameEng;
    }

    public String getTehsilNameEng() {
        return tehsilNameEng;
    }

    public void setTehsilNameEng(String tehsilNameEng) {
        this.tehsilNameEng = tehsilNameEng;
    }

    public String getTehsilEMail() {
        return tehsilEMail;
    }

    public void setTehsilEMail(String tehsilEMail) {
        this.tehsilEMail = tehsilEMail;
    }

    public String getTehsilNameHi() {
        return tehsilNameHi;
    }

    public void setTehsilNameHi(String tehsilNameHi) {
        this.tehsilNameHi = tehsilNameHi;
    }

    public String getTehsilCode() {
        return tehsilCode;
    }

    public void setTehsilCode(String tehsilCode) {
        this.tehsilCode = tehsilCode;
    }

    @Override
    public String toString() {
        return tehsilNameEng;
    }

}
