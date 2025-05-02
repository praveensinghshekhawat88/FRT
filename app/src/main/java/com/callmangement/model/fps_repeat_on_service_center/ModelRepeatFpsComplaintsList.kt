
package com.callmangement.model.fps_repeat_on_service_center;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ModelRepeatFpsComplaintsList implements Serializable {
    @SerializedName("getStatus")
    @Expose
    private Object getStatus;
    @SerializedName("fromDate")
    @Expose
    private Object fromDate;
    @SerializedName("toDate")
    @Expose
    private Object toDate;
    @SerializedName("check_csrf")
    @Expose
    private Object checkCsrf;
    @SerializedName("createdBy")
    @Expose
    private Object createdBy;
    @SerializedName("isSentToServiceCentreOn")
    @Expose
    private Object isSentToServiceCentreOn;
    @SerializedName("complainAssignDate")
    @Expose
    private Object complainAssignDate;
    @SerializedName("assignedUserID")
    @Expose
    private Object assignedUserID;
    @SerializedName("isResolvedInLessTwoDays")
    @Expose
    private Boolean isResolvedInLessTwoDays;
    @SerializedName("complainAssignTo")
    @Expose
    private Object complainAssignTo;
    @SerializedName("customerName")
    @Expose
    private String customerName;
    @SerializedName("mobileNo")
    @Expose
    private String mobileNo;
    @SerializedName("tehsil")
    @Expose
    private Object tehsil;
    @SerializedName("district")
    @Expose
    private String district;
    @SerializedName("fpscode")
    @Expose
    private String fpscode;
    @SerializedName("districtId")
    @Expose
    private Object districtId;
    @SerializedName("complainRegNo")
    @Expose
    private String complainRegNo;
    @SerializedName("complainDesc")
    @Expose
    private String complainDesc;
    @SerializedName("updatedBy")
    @Expose
    private Object updatedBy;
    @SerializedName("scremark")
    @Expose
    private Object scremark;
    @SerializedName("seremark")
    @Expose
    private String seremark;
    @SerializedName("challanNo")
    @Expose
    private String challanNo;
    @SerializedName("complainId")
    @Expose
    private Object complainId;
    @SerializedName("tehsilId")
    @Expose
    private Object tehsilId;
    @SerializedName("complainStatusOnThisDate")
    @Expose
    private Object complainStatusOnThisDate;
    @SerializedName("customerId")
    @Expose
    private Object customerId;
    @SerializedName("orderBySrNo")
    @Expose
    private Object orderBySrNo;
    @SerializedName("sermarkDate")
    @Expose
    private Object sermarkDate;
    @SerializedName("createdOn")
    @Expose
    private Object createdOn;
    @SerializedName("complainType")
    @Expose
    private String complainType;
    @SerializedName("imagePath")
    @Expose
    private String imagePath;
    @SerializedName("updatedOn")
    @Expose
    private Object updatedOn;
    @SerializedName("isActive")
    @Expose
    private Boolean isActive;
    @SerializedName("resolveTime")
    @Expose
    private Object resolveTime;
    @SerializedName("sladays_2")
    @Expose
    private Object sladays2;
    @SerializedName("sladays_RD")
    @Expose
    private Object sladaysRD;
    @SerializedName("complainStatus")
    @Expose
    private Object complainStatus;
    @SerializedName("complainTypeId")
    @Expose
    private Object complainTypeId;
    @SerializedName("complainRegDate")
    @Expose
    private Object complainRegDate;
    @SerializedName("isPhysicalDamage")
    @Expose
    private Boolean isPhysicalDamage;
    @SerializedName("complainStatusId")
    @Expose
    private Object complainStatusId;
    @SerializedName("sermarkDateStr")
    @Expose
    private String sermarkDateStr;
    @SerializedName("screplacedPartsDetail")
    @Expose
    private Object screplacedPartsDetail;
    @SerializedName("sccourierServicesDetail")
    @Expose
    private Object sccourierServicesDetail;
    @SerializedName("courierServicesDetail")
    @Expose
    private String courierServicesDetail;
    @SerializedName("complainRegDateStr")
    @Expose
    private String complainRegDateStr;
    @SerializedName("replacedPartsDetail")
    @Expose
    private String replacedPartsDetail;
    @SerializedName("cntReptOnSerCenter")
    @Expose
    private Integer cntReptOnSerCenter;

    @SerializedName("isSentToServiceCentreOnStr")
    @Expose
    private String isSentToServiceCentreOnStr;

    public Object getGetStatus() {
        return getStatus;
    }

    public void setGetStatus(Object getStatus) {
        this.getStatus = getStatus;
    }

    public Object getFromDate() {
        return fromDate;
    }

    public void setFromDate(Object fromDate) {
        this.fromDate = fromDate;
    }

    public Object getToDate() {
        return toDate;
    }

    public void setToDate(Object toDate) {
        this.toDate = toDate;
    }

    public Object getCheckCsrf() {
        return checkCsrf;
    }

    public void setCheckCsrf(Object checkCsrf) {
        this.checkCsrf = checkCsrf;
    }

    public Object getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Object createdBy) {
        this.createdBy = createdBy;
    }

    public Object getIsSentToServiceCentreOn() {
        return isSentToServiceCentreOn;
    }

    public void setIsSentToServiceCentreOn(Object isSentToServiceCentreOn) {
        this.isSentToServiceCentreOn = isSentToServiceCentreOn;
    }

    public Object getComplainAssignDate() {
        return complainAssignDate;
    }

    public void setComplainAssignDate(Object complainAssignDate) {
        this.complainAssignDate = complainAssignDate;
    }

    public Object getAssignedUserID() {
        return assignedUserID;
    }

    public void setAssignedUserID(Object assignedUserID) {
        this.assignedUserID = assignedUserID;
    }

    public Boolean getIsResolvedInLessTwoDays() {
        return isResolvedInLessTwoDays;
    }

    public void setIsResolvedInLessTwoDays(Boolean isResolvedInLessTwoDays) {
        this.isResolvedInLessTwoDays = isResolvedInLessTwoDays;
    }

    public Object getComplainAssignTo() {
        return complainAssignTo;
    }

    public void setComplainAssignTo(Object complainAssignTo) {
        this.complainAssignTo = complainAssignTo;
    }

    public String getCustomerName() {
        if (customerName == null)
            return "";
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getMobileNo() {
        if (mobileNo == null)
            return "";
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public Object getTehsil() {
        return tehsil;
    }

    public void setTehsil(Object tehsil) {
        this.tehsil = tehsil;
    }

    public String getDistrict() {
        if (district == null)
            return "";
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getFpscode() {
        if (fpscode == null)
            return "";
        return fpscode;
    }

    public void setFpscode(String fpscode) {
        this.fpscode = fpscode;
    }

    public Object getDistrictId() {
        return districtId;
    }

    public void setDistrictId(Object districtId) {
        this.districtId = districtId;
    }

    public String getComplainRegNo() {
        if (complainRegNo == null)
            return "";
        return complainRegNo;
    }

    public void setComplainRegNo(String complainRegNo) {
        this.complainRegNo = complainRegNo;
    }

    public String getComplainDesc() {
        if (complainDesc == null)
            return "";
        return complainDesc;
    }

    public void setComplainDesc(String complainDesc) {
        this.complainDesc = complainDesc;
    }

    public Object getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(Object updatedBy) {
        this.updatedBy = updatedBy;
    }

    public Object getScremark() {
        return scremark;
    }

    public void setScremark(Object scremark) {
        this.scremark = scremark;
    }

    public String getSeremark() {
        if (seremark == null)
            return "";
        return seremark;
    }

    public void setSeremark(String seremark) {
        this.seremark = seremark;
    }

    public String getChallanNo() {
        if (challanNo == null)
            return "";
        return challanNo;
    }

    public void setChallanNo(String challanNo) {
        this.challanNo = challanNo;
    }

    public Object getComplainId() {
        return complainId;
    }

    public void setComplainId(Object complainId) {
        this.complainId = complainId;
    }

    public Object getTehsilId() {
        return tehsilId;
    }

    public void setTehsilId(Object tehsilId) {
        this.tehsilId = tehsilId;
    }

    public Object getComplainStatusOnThisDate() {
        return complainStatusOnThisDate;
    }

    public void setComplainStatusOnThisDate(Object complainStatusOnThisDate) {
        this.complainStatusOnThisDate = complainStatusOnThisDate;
    }

    public Object getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Object customerId) {
        this.customerId = customerId;
    }

    public Object getOrderBySrNo() {
        return orderBySrNo;
    }

    public void setOrderBySrNo(Object orderBySrNo) {
        this.orderBySrNo = orderBySrNo;
    }

    public Object getSermarkDate() {
        return sermarkDate;
    }

    public void setSermarkDate(Object sermarkDate) {
        this.sermarkDate = sermarkDate;
    }

    public Object getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Object createdOn) {
        this.createdOn = createdOn;
    }

    public String getComplainType() {
        if (complainType == null)
            return "";
        return complainType;
    }

    public void setComplainType(String complainType) {
        this.complainType = complainType;
    }

    public String getImagePath() {
        if (imagePath == null)
            return "";
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public Object getUpdatedOn() {
        return updatedOn;
    }

    public void setUpdatedOn(Object updatedOn) {
        this.updatedOn = updatedOn;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public Object getResolveTime() {
        return resolveTime;
    }

    public void setResolveTime(Object resolveTime) {
        this.resolveTime = resolveTime;
    }

    public Object getSladays2() {
        return sladays2;
    }

    public void setSladays2(Object sladays2) {
        this.sladays2 = sladays2;
    }

    public Object getSladaysRD() {
        return sladaysRD;
    }

    public void setSladaysRD(Object sladaysRD) {
        this.sladaysRD = sladaysRD;
    }

    public Object getComplainStatus() {
        return complainStatus;
    }

    public void setComplainStatus(Object complainStatus) {
        this.complainStatus = complainStatus;
    }

    public Object getComplainTypeId() {
        return complainTypeId;
    }

    public void setComplainTypeId(Object complainTypeId) {
        this.complainTypeId = complainTypeId;
    }

    public Object getComplainRegDate() {
        return complainRegDate;
    }

    public void setComplainRegDate(Object complainRegDate) {
        this.complainRegDate = complainRegDate;
    }

    public Boolean getIsPhysicalDamage() {
        return isPhysicalDamage;
    }

    public void setIsPhysicalDamage(Boolean isPhysicalDamage) {
        this.isPhysicalDamage = isPhysicalDamage;
    }

    public Object getComplainStatusId() {
        return complainStatusId;
    }

    public void setComplainStatusId(Object complainStatusId) {
        this.complainStatusId = complainStatusId;
    }

    public String getSermarkDateStr() {
        if (sermarkDateStr == null)
            return "";
        return sermarkDateStr;
    }

    public void setSermarkDateStr(String sermarkDateStr) {
        this.sermarkDateStr = sermarkDateStr;
    }

    public Object getScreplacedPartsDetail() {
        return screplacedPartsDetail;
    }

    public void setScreplacedPartsDetail(Object screplacedPartsDetail) {
        this.screplacedPartsDetail = screplacedPartsDetail;
    }

    public Object getSccourierServicesDetail() {
        return sccourierServicesDetail;
    }

    public void setSccourierServicesDetail(Object sccourierServicesDetail) {
        this.sccourierServicesDetail = sccourierServicesDetail;
    }

    public String getCourierServicesDetail() {
        if (courierServicesDetail == null)
            return "";
        return courierServicesDetail;
    }

    public void setCourierServicesDetail(String courierServicesDetail) {
        this.courierServicesDetail = courierServicesDetail;
    }

    public String getComplainRegDateStr() {
        if (complainRegDateStr == null)
            return "";
        return complainRegDateStr;
    }

    public void setComplainRegDateStr(String complainRegDateStr) {
        this.complainRegDateStr = complainRegDateStr;
    }

    public String getReplacedPartsDetail() {
        if (replacedPartsDetail == null)
            return "";
        return replacedPartsDetail;
    }

    public void setReplacedPartsDetail(String replacedPartsDetail) {
        this.replacedPartsDetail = replacedPartsDetail;
    }

    public Integer getCntReptOnSerCenter() {
        if (cntReptOnSerCenter == null)
            return 0;
        return cntReptOnSerCenter;
    }

    public void setCntReptOnSerCenter(Integer cntReptOnSerCenter) {
        this.cntReptOnSerCenter = cntReptOnSerCenter;
    }

    public String getIsSentToServiceCentreOnStr() {
        if (isSentToServiceCentreOnStr == null)
            return "";
        return isSentToServiceCentreOnStr;
    }

    public void setIsSentToServiceCentreOnStr(String isSentToServiceCentreOnStr) {
        this.isSentToServiceCentreOnStr = isSentToServiceCentreOnStr;
    }
}
