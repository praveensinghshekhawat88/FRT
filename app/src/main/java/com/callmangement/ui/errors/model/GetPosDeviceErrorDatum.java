package com.callmangement.ui.errors.model;

import java.io.Serializable;

public class GetPosDeviceErrorDatum implements Serializable {
    public boolean status;
    public String message;
    public String districtNameEng;
    public String dealerMobileNo;
    public String fpscode;
    public int createdBy;
    public Object userId;
    public int districtId;
    public int updatedBy;
    public String dealerName;
    public String errorRegNo;
    public String deviceCode;
    public String remark;
    public int errorStatusId;
    public int errorId;
    public int errorTypeId;
    public String updatedOn;
    public String createdOn;
    public boolean isActive;
    public String block;
    public String resolveDate;
    public String updatedByName;
    public String errorType;
    public String createdbyName;
    public String errorRegDate;
    public String errorStatus;

    public GetPosDeviceErrorDatum(boolean status, String message, String districtNameEng, String dealerMobileNo, String fpscode, int createdBy, Object userId, int districtId, int updatedBy, String dealerName, String errorRegNo, String deviceCode, String remark, int errorStatusId, int errorId, int errorTypeId, String updatedOn, String createdOn, boolean isActive, String block, String resolveDate, String updatedByName, String errorType, String createdbyName, String errorRegDate, String errorStatus) {
        this.status = status;
        this.message = message;
        this.districtNameEng = districtNameEng;
        this.dealerMobileNo = dealerMobileNo;
        this.fpscode = fpscode;
        this.createdBy = createdBy;
        this.userId = userId;
        this.districtId = districtId;
        this.updatedBy = updatedBy;
        this.dealerName = dealerName;
        this.errorRegNo = errorRegNo;
        this.deviceCode = deviceCode;
        this.remark = remark;
        this.errorStatusId = errorStatusId;
        this.errorId = errorId;
        this.errorTypeId = errorTypeId;
        this.updatedOn = updatedOn;
        this.createdOn = createdOn;
        this.isActive = isActive;
        this.block = block;
        this.resolveDate = resolveDate;
        this.updatedByName = updatedByName;
        this.errorType = errorType;
        this.createdbyName = createdbyName;
        this.errorRegDate = errorRegDate;
        this.errorStatus = errorStatus;
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

    public String getDistrictNameEng() {
        return districtNameEng;
    }

    public void setDistrictNameEng(String districtNameEng) {
        this.districtNameEng = districtNameEng;
    }

    public String getDealerMobileNo() {
        return dealerMobileNo;
    }

    public void setDealerMobileNo(String dealerMobileNo) {
        this.dealerMobileNo = dealerMobileNo;
    }

    public String getFpscode() {
        return fpscode;
    }

    public void setFpscode(String fpscode) {
        this.fpscode = fpscode;
    }

    public int getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(int createdBy) {
        this.createdBy = createdBy;
    }

    public Object getUserId() {
        return userId;
    }

    public void setUserId(Object userId) {
        this.userId = userId;
    }

    public int getDistrictId() {
        return districtId;
    }

    public void setDistrictId(int districtId) {
        this.districtId = districtId;
    }

    public int getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(int updatedBy) {
        this.updatedBy = updatedBy;
    }

    public String getDealerName() {
        return dealerName;
    }

    public void setDealerName(String dealerName) {
        this.dealerName = dealerName;
    }

    public String getErrorRegNo() {
        return errorRegNo;
    }

    public void setErrorRegNo(String errorRegNo) {
        this.errorRegNo = errorRegNo;
    }

    public String getDeviceCode() {
        return deviceCode;
    }

    public void setDeviceCode(String deviceCode) {
        this.deviceCode = deviceCode;
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

    public int getErrorTypeId() {
        return errorTypeId;
    }

    public void setErrorTypeId(int errorTypeId) {
        this.errorTypeId = errorTypeId;
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

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public String getBlock() {
        return block;
    }

    public void setBlock(String block) {
        this.block = block;
    }

    public String getResolveDate() {
        return resolveDate;
    }

    public void setResolveDate(String resolveDate) {
        this.resolveDate = resolveDate;
    }

    public String getUpdatedByName() {
        return updatedByName;
    }

    public void setUpdatedByName(String updatedByName) {
        this.updatedByName = updatedByName;
    }

    public String getErrorType() {
        return errorType;
    }

    public void setErrorType(String errorType) {
        this.errorType = errorType;
    }

    public String getCreatedbyName() {
        return createdbyName;
    }

    public void setCreatedbyName(String createdbyName) {
        this.createdbyName = createdbyName;
    }

    public String getErrorRegDate() {
        return errorRegDate;
    }

    public void setErrorRegDate(String errorRegDate) {
        this.errorRegDate = errorRegDate;
    }

    public String getErrorStatus() {
        return errorStatus;
    }

    public void setErrorStatus(String errorStatus) {
        this.errorStatus = errorStatus;
    }
}
