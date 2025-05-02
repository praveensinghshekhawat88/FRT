package com.callmangement.model.inventrory;

import com.google.gson.annotations.SerializedName;

public class ModelSEUsersList {
    @SerializedName("userTypeId") private String userTypeId;
    @SerializedName("password") private String password;
    @SerializedName("check_csrf") private String check_csrf;
    @SerializedName("userName") private String userName;
    @SerializedName("createdBy") private String createdBy;
    @SerializedName("createdOn") private String createdOn;
    @SerializedName("updatedOn") private String updatedOn;
    @SerializedName("districtId") private String districtId;
    @SerializedName("isActive") private String isActive;
    @SerializedName("updatedBy") private String updatedBy;
    @SerializedName("loginMessage") private String loginMessage;
    @SerializedName("loginStatus") private String loginStatus;
    @SerializedName("userId") private String userId;
    @SerializedName("userTypeName") private String userTypeName;
    @SerializedName("emailId") private String emailId;
    @SerializedName("mobileNo") private String mobileNo;
    @SerializedName("district") private String district;

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

    public String getDistrictId() {
        return districtId;
    }

    public void setDistrictId(String districtId) {
        this.districtId = districtId;
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

    public String getLoginMessage() {
        return loginMessage;
    }

    public void setLoginMessage(String loginMessage) {
        this.loginMessage = loginMessage;
    }

    public String getLoginStatus() {
        return loginStatus;
    }

    public void setLoginStatus(String loginStatus) {
        this.loginStatus = loginStatus;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserTypeName() {
        return userTypeName;
    }

    public void setUserTypeName(String userTypeName) {
        this.userTypeName = userTypeName;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
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

    @Override
    public String toString() {
        return userName;
    }
}
