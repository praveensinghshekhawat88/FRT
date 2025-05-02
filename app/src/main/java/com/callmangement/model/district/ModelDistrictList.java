package com.callmangement.model.district;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ModelDistrictList implements Serializable {
   @SerializedName("createdBy") private String createdBy;
   @SerializedName("districtNameHi") private String districtNameHi;
   @SerializedName("districtNameEng") private String districtNameEng;
   @SerializedName("updatedBy") private String updatedBy;
   @SerializedName("districtId") private String districtId;
   @SerializedName("orderBySrNo") private String orderBySrNo;
   @SerializedName("createdOn") private String createdOn;
   @SerializedName("updatedOn") private String updatedOn;
   @SerializedName("isActive") private String isActive;
   @SerializedName("districtCode") private String districtCode;
   @SerializedName("email") private String email;

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
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

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public String getDistrictId() {
        if (districtId == null)
            return "0";
        return districtId;
    }

    public void setDistrictId(String districtId) {
        this.districtId = districtId;
    }

    public String getOrderBySrNo() {
        return orderBySrNo;
    }

    public void setOrderBySrNo(String orderBySrNo) {
        this.orderBySrNo = orderBySrNo;
    }

    public String getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(String createdOn) {
        this.createdOn = createdOn;
    }

    public String getUpdatedOn() {
        return updatedOn;
    }

    public void setUpdatedOn(String updatedOn) {
        this.updatedOn = updatedOn;
    }

    public String getIsActive() {
        return isActive;
    }

    public void setIsActive(String isActive) {
        this.isActive = isActive;
    }

    public String getDistrictCode() {
        return districtCode;
    }

    public void setDistrictCode(String districtCode) {
        this.districtCode = districtCode;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return districtNameEng;
    }
}
