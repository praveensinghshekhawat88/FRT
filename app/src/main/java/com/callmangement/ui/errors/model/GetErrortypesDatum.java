package com.callmangement.ui.errors.model;

public class GetErrortypesDatum {
    public boolean status;
    public String message;
    public Object createdBy;
    public boolean isActive;
    public String errorType;
    public int errorTypeId;
    public String remark;
    public Object createdOn;
    public Object updatedOn;
    public Object updatedBy;

    public GetErrortypesDatum(boolean status, String message, Object createdBy, boolean isActive, String errorType, int errorTypeId, String remark, Object createdOn, Object updatedOn, Object updatedBy) {
        this.status = status;
        this.message = message;
        this.createdBy = createdBy;
        this.isActive = isActive;
        this.errorType = errorType;
        this.errorTypeId = errorTypeId;
        this.remark = remark;
        this.createdOn = createdOn;
        this.updatedOn = updatedOn;
        this.updatedBy = updatedBy;
    }

  public GetErrortypesDatum(String errorType, int errorTypeId) {
        this.errorType = errorType;
        this.errorTypeId = errorTypeId;
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

    public Object getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Object createdBy) {
        this.createdBy = createdBy;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public String getErrorType() {
        return errorType;
    }

    public void setErrorType(String errorType) {
        this.errorType = errorType;
    }

    public int getErrorTypeId() {
        return errorTypeId;
    }

    public void setErrorTypeId(int errorTypeId) {
        this.errorTypeId = errorTypeId;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Object getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Object createdOn) {
        this.createdOn = createdOn;
    }

    public Object getUpdatedOn() {
        return updatedOn;
    }

    public void setUpdatedOn(Object updatedOn) {
        this.updatedOn = updatedOn;
    }

    public Object getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(Object updatedBy) {
        this.updatedBy = updatedBy;
    }
}
