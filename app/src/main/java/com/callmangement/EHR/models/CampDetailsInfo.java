package com.callmangement.EHR.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class CampDetailsInfo implements Serializable {

    @SerializedName("status")
    private String status;

    @SerializedName("message")
    private String message;

    @SerializedName("address")
    private String address;

    @SerializedName("tehsilNameHi")
    private String tehsilNameHi;

    @SerializedName("tehsilNameEng")
    private String tehsilNameEng;

    @SerializedName("tstatus")
    private String tstatus;

    @SerializedName("organizeDate")
    private String organizeDate;

    @SerializedName("isActive")
    private String isActive;

    @SerializedName("description")
    private String description;

    @SerializedName("districtNameHi")
    private String districtNameHi;

    @SerializedName("tehsilName")
    private String tehsilName;

    @SerializedName("updatedBy")
    private String updatedBy;

    @SerializedName("createdBy")
    private String createdBy;

    @SerializedName("blockName")
    private Object blockName;

    @SerializedName("createdOn")
    private String createdOn;

    @SerializedName("updatedOn")
    private String updatedOn;

    @SerializedName("tstatusId")
    private String tstatusId;

    @SerializedName("title")
    private String title;

    @SerializedName("endDate")
    private String endDate;

    @SerializedName("blockId")
    private String blockId;

    @SerializedName("trainingId")
    private String trainingId;

    @SerializedName("trainingNo")
    private String trainingNo;

    @SerializedName("districtID")
    private String districtID;

    @SerializedName("tehsilID")
    private String tehsilID;

    @SerializedName("startDate")
    private String startDate;

    @SerializedName("districtNameEng")
    private String districtNameEng;

    @SerializedName("UplodedCampformCount")
    private String UplodedCampformCount;

    @SerializedName("UplodedCampPhotoCount")
    private String UplodedCampPhotoCount;

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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTehsilNameHi() {
        return tehsilNameHi;
    }

    public void setTehsilNameHi(String tehsilNameHi) {
        this.tehsilNameHi = tehsilNameHi;
    }

    public String getTehsilNameEng() {
        return tehsilNameEng;
    }

    public void setTehsilNameEng(String tehsilNameEng) {
        this.tehsilNameEng = tehsilNameEng;
    }

    public String getTstatus() {
        return tstatus;
    }

    public void setTstatus(String tstatus) {
        this.tstatus = tstatus;
    }


    public CampDetailsInfo(Object blockName) {
        this.blockName = blockName;
    }



    public String getOrganizeDate() {
        return organizeDate;
    }

    public void setOrganizeDate(String organizeDate) {
        this.organizeDate = organizeDate;
    }

    public String getIsActive() {
        return isActive;
    }

    public void setIsActive(String isActive) {
        this.isActive = isActive;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDistrictNameHi() {
        return districtNameHi;
    }

    public void setDistrictNameHi(String districtNameHi) {
        this.districtNameHi = districtNameHi;
    }

    public String getTehsilName() {
        return tehsilName;
    }

    public void setTehsilName(String tehsilName) {
        this.tehsilName = tehsilName;
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

    public String getTstatusId() {
        return tstatusId;
    }

    public void setTstatusId(String tstatusId) {
        this.tstatusId = tstatusId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
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

    public String getTehsilID() {
        return tehsilID;
    }

    public void setTehsilID(String tehsilID) {
        this.tehsilID = tehsilID;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getDistrictNameEng() {
        return districtNameEng;
    }

    public void setDistrictNameEng(String districtNameEng) {
        this.districtNameEng = districtNameEng;
    }

    public String getUplodedCampformCount() {
        return UplodedCampformCount;
    }

    public void setUplodedCampformCount(String uplodedCampformCount) {
        UplodedCampformCount = uplodedCampformCount;
    }

    public String getUplodedCampPhotoCount() {
        return UplodedCampPhotoCount;
    }

    public void setUplodedCampPhotoCount(String uplodedCampPhotoCount) {
        UplodedCampPhotoCount = uplodedCampPhotoCount;
    }


    public Object getBlockName() {
        return blockName;
    }

    public void setBlockName(Object blockName) {
        this.blockName = blockName;
    }
}

