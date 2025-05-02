package com.callmangement.model.training_schedule;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ModelTrainingScheduleList implements Serializable {
    @SerializedName("address") private String address;
    @SerializedName("description") private String description;
    @SerializedName("createdBy") private String createdBy;
    @SerializedName("tehsilNameEng") private String tehsilNameEng;
    @SerializedName("tehsilNameHi") private String tehsilNameHi;
    @SerializedName("tehsilID") private String tehsilID;
    @SerializedName("blockId") private String blockId;
    @SerializedName("trainingId") private String trainingId;
    @SerializedName("endDate") private String endDate;
    @SerializedName("trainingNo") private String trainingNo;
    @SerializedName("districtID") private String districtID;
    @SerializedName("startDate") private String startDate;
    @SerializedName("tstatus") private String tstatus;
    @SerializedName("createdOn") private String createdOn;
    @SerializedName("isActive") private String isActive;
    @SerializedName("updatedBy") private String updatedBy;
    @SerializedName("updatedOn") private String updatedOn;
    @SerializedName("districtNameHi") private String districtNameHi;
    @SerializedName("districtNameEng") private String districtNameEng;
    @SerializedName("title") private String title;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getTehsilNameEng() {
        return tehsilNameEng;
    }

    public void setTehsilNameEng(String tehsilNameEng) {
        this.tehsilNameEng = tehsilNameEng;
    }

    public String getTehsilNameHi() {
        return tehsilNameHi;
    }

    public void setTehsilNameHi(String tehsilNameHi) {
        this.tehsilNameHi = tehsilNameHi;
    }

    public String getTehsilID() {
        return tehsilID;
    }

    public void setTehsilID(String tehsilID) {
        this.tehsilID = tehsilID;
    }

    public String getBlockId() {
        return blockId;
    }

    public void setBlockId(String blockId) {
        this.blockId = blockId;
    }

    public String getTrainingId() {
        return trainingId;
    }

    public void setTrainingId(String trainingId) {
        this.trainingId = trainingId;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getTrainingNo() {
        return trainingNo;
    }

    public void setTrainingNo(String trainingNo) {
        this.trainingNo = trainingNo;
    }

    public String getDistrictID() {
        return districtID;
    }

    public void setDistrictID(String districtID) {
        this.districtID = districtID;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getTstatus() {
        return tstatus;
    }

    public void setTstatus(String tstatus) {
        this.tstatus = tstatus;
    }

    public String getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(String createdOn) {
        this.createdOn = createdOn;
    }

    public String getIsActive() {
        return isActive;
    }

    public void setIsActive(String isActive) {
        this.isActive = isActive;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public String getUpdatedOn() {
        return updatedOn;
    }

    public void setUpdatedOn(String updatedOn) {
        this.updatedOn = updatedOn;
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
