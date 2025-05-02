package com.callmangement.EHR.models;

import java.io.Serializable;

public class FeedbackbyDCListDatum implements Serializable {


    public int year;
    public int month;
    public Object action;
    public int feedBackId;
    public boolean status;
    public int createdBy;
    public int updatedBy;
    public String latitude;
    public String longitude;
    public FeedbackbyDCListUpdatedOn updatedOn;
    public FeedbackbyDCListCreatedOn createdOn;
    public String msg;
    public String feedBackBy;
    public int districtId;
    public Object userId;
    public String fpscode;
    public String districtName;
    public String selfyWithFPSShopImage;
    public String dealerSignatureImage;
    public String dcVisitingChallan;
    public boolean isActive;
    public Object remarks;
    public String locationAddress;
    public String logInTime;
    public String logOutTime;


    public FeedbackbyDCListDatum(int year, int month, Object action, int feedBackId, boolean status, int createdBy, int updatedBy, String latitude, String longitude, FeedbackbyDCListUpdatedOn updatedOn, FeedbackbyDCListCreatedOn createdOn, String msg, String feedBackBy, int districtId, Object userId, String fpscode, String districtName, String selfyWithFPSShopImage, String dealerSignatureImage, String dcVisitingChallan, boolean isActive, Object remarks, String locationAddress, String logInTime, String logOutTime) {
        this.year = year;
        this.month = month;
        this.action = action;
        this.feedBackId = feedBackId;
        this.status = status;
        this.createdBy = createdBy;
        this.updatedBy = updatedBy;
        this.latitude = latitude;
        this.longitude = longitude;
        this.updatedOn = updatedOn;
        this.createdOn = createdOn;
        this.msg = msg;
        this.feedBackBy = feedBackBy;
        this.districtId = districtId;
        this.userId = userId;
        this.fpscode = fpscode;
        this.districtName = districtName;
        this.selfyWithFPSShopImage = selfyWithFPSShopImage;
        this.dealerSignatureImage = dealerSignatureImage;
        this.dcVisitingChallan = dcVisitingChallan;
        this.isActive = isActive;
        this.remarks = remarks;
        this.locationAddress = locationAddress;
        this.logInTime = logInTime;
        this.logOutTime = logOutTime;
    }


    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public Object getAction() {
        return action;
    }

    public void setAction(Object action) {
        this.action = action;
    }

    public int getFeedBackId() {
        return feedBackId;
    }

    public void setFeedBackId(int feedBackId) {
        this.feedBackId = feedBackId;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public int getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(int createdBy) {
        this.createdBy = createdBy;
    }

    public int getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(int updatedBy) {
        this.updatedBy = updatedBy;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public FeedbackbyDCListUpdatedOn getUpdatedOn() {
        return updatedOn;
    }

    public void setUpdatedOn(FeedbackbyDCListUpdatedOn updatedOn) {
        this.updatedOn = updatedOn;
    }

    public FeedbackbyDCListCreatedOn getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(FeedbackbyDCListCreatedOn createdOn) {
        this.createdOn = createdOn;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getFeedBackBy() {
        return feedBackBy;
    }

    public void setFeedBackBy(String feedBackBy) {
        this.feedBackBy = feedBackBy;
    }

    public int getDistrictId() {
        return districtId;
    }

    public void setDistrictId(int districtId) {
        this.districtId = districtId;
    }

    public Object getUserId() {
        return userId;
    }

    public void setUserId(Object userId) {
        this.userId = userId;
    }

    public String getFpscode() {
        return fpscode;
    }

    public void setFpscode(String fpscode) {
        this.fpscode = fpscode;
    }

    public String getDistrictName() {
        return districtName;
    }

    public void setDistrictName(String districtName) {
        this.districtName = districtName;
    }

    public String getSelfyWithFPSShopImage() {
        return selfyWithFPSShopImage;
    }

    public void setSelfyWithFPSShopImage(String selfyWithFPSShopImage) {
        this.selfyWithFPSShopImage = selfyWithFPSShopImage;
    }

    public String getDealerSignatureImage() {
        return dealerSignatureImage;
    }

    public void setDealerSignatureImage(String dealerSignatureImage) {
        this.dealerSignatureImage = dealerSignatureImage;
    }

    public String getDcVisitingChallan() {
        return dcVisitingChallan;
    }

    public void setDcVisitingChallan(String dcVisitingChallan) {
        this.dcVisitingChallan = dcVisitingChallan;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public Object getRemarks() {
        return remarks;
    }

    public void setRemarks(Object remarks) {
        this.remarks = remarks;
    }

    public String getLocationAddress() {
        return locationAddress;
    }

    public void setLocationAddress(String locationAddress) {
        this.locationAddress = locationAddress;
    }

    public String getLogInTime() {
        return logInTime;
    }

    public void setLogInTime(String logInTime) {
        this.logInTime = logInTime;
    }

    public String getLogOutTime() {
        return logOutTime;
    }

    public void setLogOutTime(String logOutTime) {
        this.logOutTime = logOutTime;
    }
}
