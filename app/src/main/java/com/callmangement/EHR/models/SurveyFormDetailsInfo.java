package com.callmangement.EHR.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class SurveyFormDetailsInfo implements Serializable {

    @SerializedName("ticketNo")
    private String ticketNo;

    @SerializedName("status")
    private String status;

    @SerializedName("message")
    private String message;

    @SerializedName("address")
    private String address;

    @SerializedName("createdBy")
    private String createdBy;

    @SerializedName("isActive")
    private String isActive;

    @SerializedName("createdOn")
    private String createdOn;

    @SerializedName("updatedOn")
    private String updatedOn;

    @SerializedName("updatedBy")
    private String updatedBy;

    @SerializedName("customerName")
    private String customerName;

    @SerializedName("gstin_No")
    private String gstin_No;

    @SerializedName("typeOfCall")
    private String typeOfCall;

    @SerializedName("statusId")
    private String statusId;

    @SerializedName("mobileNumber")
    private String mobileNumber;

    @SerializedName("billRemark")
    private String billRemark;

    @SerializedName("serveyFormId")
    private String serveyFormId;

    @SerializedName("typeOfCallId")
    private String typeOfCallId;

    @SerializedName("itemDetail")
    private String itemDetail;

    @SerializedName("accessesory")
    private String accessesory;

    @SerializedName("engineerName")
    private String engineerName;

    @SerializedName("installedMachineSpecification")
    private String installedMachineSpecification;

    @SerializedName("formUploadedOnStr")
    private String formUploadedOnStr;

    @SerializedName("pointOfContact")
    private String pointOfContact;

    @SerializedName("customer_Remark")
    private String customer_Remark;

    @SerializedName("installationDate")
    private String installationDate;

    @SerializedName("installationDone")
    private String installationDone;

    @SerializedName("purchaseOrderDtl")
    private String purchaseOrderDtl;

    @SerializedName("formUploadedOn")
    private String formUploadedOn;

    @SerializedName("bill_ChallanNo")
    private String bill_ChallanNo;

    @SerializedName("installationDateStr")
    private String installationDateStr;

    public String getTicketNo() {
        return ticketNo;
    }

    public void setTicketNo(String ticketNo) {
        this.ticketNo = ticketNo;
    }

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

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getIsActive() {
        return isActive;
    }

    public void setIsActive(String isActive) {
        this.isActive = isActive;
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

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getGstin_No() {
        return gstin_No;
    }

    public void setGstin_No(String gstin_No) {
        this.gstin_No = gstin_No;
    }

    public String getTypeOfCall() {
        return typeOfCall;
    }

    public void setTypeOfCall(String typeOfCall) {
        this.typeOfCall = typeOfCall;
    }

    public String getStatusId() {
        return statusId;
    }

    public void setStatusId(String statusId) {
        this.statusId = statusId;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getBillRemark() {
        return billRemark;
    }

    public void setBillRemark(String billRemark) {
        this.billRemark = billRemark;
    }

    public String getServeyFormId() {
        return serveyFormId;
    }

    public void setServeyFormId(String serveyFormId) {
        this.serveyFormId = serveyFormId;
    }

    public String getTypeOfCallId() {
        return typeOfCallId;
    }

    public void setTypeOfCallId(String typeOfCallId) {
        this.typeOfCallId = typeOfCallId;
    }

    public String getItemDetail() {
        return itemDetail;
    }

    public void setItemDetail(String itemDetail) {
        this.itemDetail = itemDetail;
    }

    public String getAccessesory() {
        return accessesory;
    }

    public void setAccessesory(String accessesory) {
        this.accessesory = accessesory;
    }

    public String getEngineerName() {
        return engineerName;
    }

    public void setEngineerName(String engineerName) {
        this.engineerName = engineerName;
    }

    public String getInstalledMachineSpecification() {
        return installedMachineSpecification;
    }

    public void setInstalledMachineSpecification(String installedMachineSpecification) {
        this.installedMachineSpecification = installedMachineSpecification;
    }

    public String getFormUploadedOnStr() {
        return formUploadedOnStr;
    }

    public void setFormUploadedOnStr(String formUploadedOnStr) {
        this.formUploadedOnStr = formUploadedOnStr;
    }

    public String getPointOfContact() {
        return pointOfContact;
    }

    public void setPointOfContact(String pointOfContact) {
        this.pointOfContact = pointOfContact;
    }

    public String getCustomer_Remark() {
        return customer_Remark;
    }

    public void setCustomer_Remark(String customer_Remark) {
        this.customer_Remark = customer_Remark;
    }

    public String getInstallationDate() {
        return installationDate;
    }

    public void setInstallationDate(String installationDate) {
        this.installationDate = installationDate;
    }

    public String getInstallationDone() {
        return installationDone;
    }

    public void setInstallationDone(String installationDone) {
        this.installationDone = installationDone;
    }

    public String getPurchaseOrderDtl() {
        return purchaseOrderDtl;
    }

    public void setPurchaseOrderDtl(String purchaseOrderDtl) {
        this.purchaseOrderDtl = purchaseOrderDtl;
    }

    public String getFormUploadedOn() {
        return formUploadedOn;
    }

    public void setFormUploadedOn(String formUploadedOn) {
        this.formUploadedOn = formUploadedOn;
    }

    public String getBill_ChallanNo() {
        return bill_ChallanNo;
    }

    public void setBill_ChallanNo(String bill_ChallanNo) {
        this.bill_ChallanNo = bill_ChallanNo;
    }

    public String getInstallationDateStr() {
        return installationDateStr;
    }

    public void setInstallationDateStr(String installationDateStr) {
        this.installationDateStr = installationDateStr;
    }
}

