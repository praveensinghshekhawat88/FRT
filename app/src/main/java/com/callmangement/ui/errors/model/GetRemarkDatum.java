package com.callmangement.ui.errors.model;

public class GetRemarkDatum {
    public boolean status;
    public String message;
    public String fpscode;
    public Object createdBy;
    public String userType;
    public String errorRegNo;
    public String remark;
    public int errorStatusId;
    public int errorId;
    public Object createdOn;
    public boolean isActive;
    public int remarkId;
    public String errorStatus;
    public String remarkDate;

    public GetRemarkDatum(boolean status, String message, String fpscode, Object createdBy, String userType, String errorRegNo, String remark, int errorStatusId, int errorId, Object createdOn, boolean isActive, int remarkId, String errorStatus, String remarkDate) {
        this.status = status;
        this.message = message;
        this.fpscode = fpscode;
        this.createdBy = createdBy;
        this.userType = userType;
        this.errorRegNo = errorRegNo;
        this.remark = remark;
        this.errorStatusId = errorStatusId;
        this.errorId = errorId;
        this.createdOn = createdOn;
        this.isActive = isActive;
        this.remarkId = remarkId;
        this.errorStatus = errorStatus;
        this.remarkDate = remarkDate;
    }


    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getFpscode() {
        return fpscode;
    }

    public void setFpscode(String fpscode) {
        this.fpscode = fpscode;
    }

    public Object getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Object createdBy) {
        this.createdBy = createdBy;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getErrorRegNo() {
        return errorRegNo;
    }

    public void setErrorRegNo(String errorRegNo) {
        this.errorRegNo = errorRegNo;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public int getErrorStatusId() {
        return errorStatusId;
    }

    public void setErrorStatusId(int errorStatusId) {
        this.errorStatusId = errorStatusId;
    }

    public int getErrorId() {
        return errorId;
    }

    public void setErrorId(int errorId) {
        this.errorId = errorId;
    }

    public Object getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Object createdOn) {
        this.createdOn = createdOn;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public int getRemarkId() {
        return remarkId;
    }

    public void setRemarkId(int remarkId) {
        this.remarkId = remarkId;
    }

    public String getErrorStatus() {
        return errorStatus;
    }

    public void setErrorStatus(String errorStatus) {
        this.errorStatus = errorStatus;
    }

    public String getRemarkDate() {
        return remarkDate;
    }

    public void setRemarkDate(String remarkDate) {
        this.remarkDate = remarkDate;
    }
}
