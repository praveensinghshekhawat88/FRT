package com.callmangement.EHR.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ModelSEUserList implements Serializable {
    @SerializedName("userTypeId")
    private String userTypeId;
    @SerializedName("password")
    private String password;
    @SerializedName("check_csrf")
    private String check_csrf;
    @SerializedName("userName")
    private String userName;
    @SerializedName("createdBy")
    private String createdBy;
    @SerializedName("mobileNo")
    private String mobileNo;
    @SerializedName("district")
    private String district;
    @SerializedName("userId")
    private String userId;
    @SerializedName("updatedBy")
    private String updatedBy;
    @SerializedName("districtId")
    private String districtId;
    @SerializedName("emailId")
    private String emailId;
    @SerializedName("userTypeName")
    private String userTypeName;
    @SerializedName("loginStatus")
    private String loginStatus;
    @SerializedName("loginMessage")
    private String loginMessage;
    @SerializedName("updatedOn")
    private String updatedOn;
    @SerializedName("createdOn")
    private String createdOn;
    @SerializedName("isActive")
    private String isActive;
    @SerializedName("employeeName")
    private String employeeName;

    public String getUserTypeId() {
        return userTypeId;
    }

    public void setUserTypeId(String userTypeId) {
        this.userTypeId = userTypeId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCheck_csrf() {
        return check_csrf;
    }

    public void setCheck_csrf(String check_csrf) {
        this.check_csrf = check_csrf;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public String getDistrictId() {
        return districtId;
    }

    public void setDistrictId(String districtId) {
        this.districtId = districtId;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public String getUserTypeName() {
        return userTypeName;
    }

    public void setUserTypeName(String userTypeName) {
        this.userTypeName = userTypeName;
    }

    public String getLoginStatus() {
        return loginStatus;
    }

    public void setLoginStatus(String loginStatus) {
        this.loginStatus = loginStatus;
    }

    public String getLoginMessage() {
        return loginMessage;
    }

    public void setLoginMessage(String loginMessage) {
        this.loginMessage = loginMessage;
    }

    public String getUpdatedOn() {
        return updatedOn;
    }

    public void setUpdatedOn(String updatedOn) {
        this.updatedOn = updatedOn;
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

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    @Override
    public String toString() {
        return userName;
    }
}
