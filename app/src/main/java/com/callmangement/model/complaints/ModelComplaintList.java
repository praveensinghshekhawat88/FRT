package com.callmangement.model.complaints;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ModelComplaintList implements Serializable {
    long registrationComplainDateTimeStamp;

    public long getRegistrationComplainDateTimeStamp() {
        return registrationComplainDateTimeStamp;
    }

    public void setRegistrationComplainDateTimeStamp(long registrationComplainDateTimeStamp) {
        this.registrationComplainDateTimeStamp = registrationComplainDateTimeStamp;
    }

    @SerializedName("check_csrf") private String check_csrf;
    @SerializedName("createdBy") private String createdBy;
    @SerializedName("complainStatusId") private String complainStatusId;
    @SerializedName("complainStatus") private String complainStatus;
    @SerializedName("complainTypeId") private String complainTypeId;
    @SerializedName("complainAssignTo") private String complainAssignTo;
    @SerializedName("assignedUserID") private String assignedUserID;
    @SerializedName("complainAssignDate") private String complainAssignDate;
    @SerializedName("fpscode") private String fpscode;
    @SerializedName("mobileNo") private String mobileNo;
    @SerializedName("district") private String district;
    @SerializedName("customerName") private String customerName;
    @SerializedName("tehsil") private String tehsil;
    @SerializedName("updatedBy") private String updatedBy;
    @SerializedName("complainRegNo") private String complainRegNo;
    @SerializedName("isPhysicalDamage") private Boolean isPhysicalDamage;
    @SerializedName("seremark") private String seremark;
    @SerializedName("complainDesc") private String complainDesc;
    @SerializedName("complainType") private String complainType;
    @SerializedName("customerId") private String customerId;
    @SerializedName("orderBySrNo") private String orderBySrNo;
    @SerializedName("isActive") private String isActive;
    @SerializedName("districtId") private String districtId;
    @SerializedName("complainId") private String complainId;
    @SerializedName("tehsilId") private String tehsilId;
    @SerializedName("replacedPartsDetail") private String replacedPartsDetail;
    @SerializedName("courierServicesDetail") private String courierServicesDetail;
    @SerializedName("complainRegDateStr") private String complainRegDateStr;
    @SerializedName("imagePath") private String imagePath;
    @SerializedName("sermarkDateStr") private String sermarkDateStr;
    @SerializedName("challanNo") private String challanNo;
    @SerializedName("complPartsIds") private String complPartsIds;
    @SerializedName("isSentToServiceCentreOn") private String isSentToServiceCentreOn;
    @SerializedName("isSentToServiceCentre") private String isSentToServiceCentre;
    @SerializedName("cntReptOnSerCenter") private String cntReptOnSerCenter;

 //   @SerializedName("complainRegDate") private ModelComplaintRegistrationDate complainRegDate = new ModelComplaintRegistrationDate();

    public String getCheck_csrf() {
        return check_csrf;
    }

    public void setCheck_csrf(String check_csrf) {
        this.check_csrf = check_csrf;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getComplainStatusId() {
        if (complainStatusId == null)
            return "";
        return complainStatusId;
    }

    public void setComplainStatusId(String complainStatusId) {
        this.complainStatusId = complainStatusId;
    }

    public String getComplainStatus() {
        return complainStatus;
    }

    public void setComplainStatus(String complainStatus) {
        this.complainStatus = complainStatus;
    }

    public String getComplainTypeId() {
        return complainTypeId;
    }

    public void setComplainTypeId(String complainTypeId) {
        this.complainTypeId = complainTypeId;
    }

    public String getComplainAssignTo() {
        return complainAssignTo;
    }

    public void setComplainAssignTo(String complainAssignTo) {
        this.complainAssignTo = complainAssignTo;
    }

    public String getAssignedUserID() {
        return assignedUserID;
    }

    public void setAssignedUserID(String assignedUserID) {
        this.assignedUserID = assignedUserID;
    }

    public String getComplainAssignDate() {
        return complainAssignDate;
    }

    public void setComplainAssignDate(String complainAssignDate) {
        this.complainAssignDate = complainAssignDate;
    }

    public String getFpscode() {
        return fpscode;
    }

    public void setFpscode(String fpscode) {
        this.fpscode = fpscode;
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

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getTehsil() {
        return tehsil;
    }

    public void setTehsil(String tehsil) {
        this.tehsil = tehsil;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public String getComplainRegNo() {
        return complainRegNo;
    }

    public void setComplainRegNo(String complainRegNo) {
        this.complainRegNo = complainRegNo;
    }

    public Boolean getPhysicalDamage() {
        return isPhysicalDamage;
    }

    public void setPhysicalDamage(Boolean physicalDamage) {
        isPhysicalDamage = physicalDamage;
    }

    public String getSeremark() {
        return seremark;
    }

    public void setSeremark(String seremark) {
        this.seremark = seremark;
    }

    public String getComplainDesc() {
        return complainDesc;
    }

    public void setComplainDesc(String complainDesc) {
        this.complainDesc = complainDesc;
    }

    public String getComplainType() {
        return complainType;
    }

    public void setComplainType(String complainType) {
        this.complainType = complainType;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getOrderBySrNo() {
        return orderBySrNo;
    }

    public void setOrderBySrNo(String orderBySrNo) {
        this.orderBySrNo = orderBySrNo;
    }

    public String getIsActive() {
        return isActive;
    }

    public void setIsActive(String isActive) {
        this.isActive = isActive;
    }

    public String getDistrictId() {
        return districtId;
    }

    public void setDistrictId(String districtId) {
        this.districtId = districtId;
    }

    public String getComplainId() {
        return complainId;
    }

    public void setComplainId(String complainId) {
        this.complainId = complainId;
    }

    public String getTehsilId() {
        return tehsilId;
    }

    public void setTehsilId(String tehsilId) {
        this.tehsilId = tehsilId;
    }

//    public ModelComplaintRegistrationDate getComplainRegDate() {
//        return complainRegDate;
//    }
//
//    public void setComplainRegDate(ModelComplaintRegistrationDate complainRegDate) {
//        this.complainRegDate = complainRegDate;
//    }

    public String getReplacedPartsDetail() {
        return replacedPartsDetail;
    }

    public void setReplacedPartsDetail(String replacedPartsDetail) {
        this.replacedPartsDetail = replacedPartsDetail;
    }

    public String getCourierServicesDetail() {
        return courierServicesDetail;
    }

    public void setCourierServicesDetail(String courierServicesDetail) {
        this.courierServicesDetail = courierServicesDetail;
    }

    public String getComplainRegDateStr() {
        return complainRegDateStr;
    }

    public void setComplainRegDateStr(String complainRegDateStr) {
        this.complainRegDateStr = complainRegDateStr;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getSermarkDateStr() {
        return sermarkDateStr;
    }

    public void setSermarkDateStr(String sermarkDateStr) {
        this.sermarkDateStr = sermarkDateStr;
    }

    public String getChallanNo() {
        return challanNo;
    }

    public void setChallanNo(String challanNo) {
        this.challanNo = challanNo;
    }

    public String getComplPartsIds() {
        if (complPartsIds == null || complPartsIds.isEmpty())
            return "";
        return complPartsIds;
    }

    public void setComplPartsIds(String complPartsIds) {
        this.complPartsIds = complPartsIds;
    }

    public String getIsSentToServiceCentreOn() {
        if (isSentToServiceCentreOn == null || isSentToServiceCentreOn.isEmpty())
            return "";
        return isSentToServiceCentreOn;
    }

    public void setIsSentToServiceCentreOn(String isSentToServiceCentreOn) {
        this.isSentToServiceCentreOn = isSentToServiceCentreOn;
    }

    public String getIsSentToServiceCentre() {
        if (isSentToServiceCentre == null || isSentToServiceCentre.isEmpty())
            return "false";
        return isSentToServiceCentre;
    }

    public void setIsSentToServiceCentre(String isSentToServiceCentre) {
        this.isSentToServiceCentre = isSentToServiceCentre;
    }

    public String getCntReptOnSerCenter() {
        if (cntReptOnSerCenter == null)
            return "0";
        return cntReptOnSerCenter;
    }

    public void setCntReptOnSerCenter(String cntReptOnSerCenter) {
        this.cntReptOnSerCenter = cntReptOnSerCenter;
    }
}
