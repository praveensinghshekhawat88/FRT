package com.callmangement.ui.ins_weighing_scale.model.Login;

public class LoginUserDetails {

    public int userTypeId;
    public Object password;
    public Object check_csrf;
    public int createdBy;
    public String userName;
    public String userTypeName;
    public boolean loginStatus;
    public String emailId;
    public String loginMessage;
    public int districtId;
    public int userId;
    public String mobileNo;
    public String district;
    public int updatedBy;
    public boolean isActive;
    public Object updatedOn;
    public Object createdOn;
    public Object employeeName;


    public LoginUserDetails(int userTypeId, Object password, Object check_csrf, int createdBy, String userName, String userTypeName, boolean loginStatus, String emailId, String loginMessage, int districtId, int userId, String mobileNo, String district, int updatedBy, boolean isActive, Object updatedOn, Object createdOn, Object employeeName) {
        this.userTypeId = userTypeId;
        this.password = password;
        this.check_csrf = check_csrf;
        this.createdBy = createdBy;
        this.userName = userName;
        this.userTypeName = userTypeName;
        this.loginStatus = loginStatus;
        this.emailId = emailId;
        this.loginMessage = loginMessage;
        this.districtId = districtId;
        this.userId = userId;
        this.mobileNo = mobileNo;
        this.district = district;
        this.updatedBy = updatedBy;
        this.isActive = isActive;
        this.updatedOn = updatedOn;
        this.createdOn = createdOn;
        this.employeeName = employeeName;
    }


    public int getUserTypeId() {
        return userTypeId;
    }

    public void setUserTypeId(int userTypeId) {
        this.userTypeId = userTypeId;
    }

    public Object getPassword() {
        return password;
    }

    public void setPassword(Object password) {
        this.password = password;
    }

    public Object getCheck_csrf() {
        return check_csrf;
    }

    public void setCheck_csrf(Object check_csrf) {
        this.check_csrf = check_csrf;
    }

    public int getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(int createdBy) {
        this.createdBy = createdBy;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserTypeName() {
        return userTypeName;
    }

    public void setUserTypeName(String userTypeName) {
        this.userTypeName = userTypeName;
    }

    public boolean isLoginStatus() {
        return loginStatus;
    }

    public void setLoginStatus(boolean loginStatus) {
        this.loginStatus = loginStatus;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public String getLoginMessage() {
        return loginMessage;
    }

    public void setLoginMessage(String loginMessage) {
        this.loginMessage = loginMessage;
    }

    public int getDistrictId() {
        return districtId;
    }

    public void setDistrictId(int districtId) {
        this.districtId = districtId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
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

    public int getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(int updatedBy) {
        this.updatedBy = updatedBy;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public Object getUpdatedOn() {
        return updatedOn;
    }

    public void setUpdatedOn(Object updatedOn) {
        this.updatedOn = updatedOn;
    }

    public Object getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Object createdOn) {
        this.createdOn = createdOn;
    }

    public Object getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(Object employeeName) {
        this.employeeName = employeeName;
    }
}
