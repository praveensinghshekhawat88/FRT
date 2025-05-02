
package com.callmangement.model.expense;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ModelExpensesList implements Serializable {
    @SerializedName("status")
    @Expose
    private Boolean status;
    @SerializedName("fileName")
    @Expose
    private String fileName;
    @SerializedName("updatedOnStr")
    @Expose
    private Object updatedOnStr;
    @SerializedName("createdBy")
    @Expose
    private Integer createdBy;
    @SerializedName("updatedOn")
    @Expose
    private Object updatedOn;
    @SerializedName("userId")
    @Expose
    private Integer userId;
    @SerializedName("updatedBy")
    @Expose
    private Integer updatedBy;
    @SerializedName("remark")
    @Expose
    private String remark;
    @SerializedName("createdOn")
    @Expose
    private Object createdOn;
    @SerializedName("filePath")
    @Expose
    private String filePath;
    @SerializedName("isActive")
    @Expose
    private Boolean isActive;
    @SerializedName("totalExAmount")
    @Expose
    private Float totalExAmount;
    @SerializedName("fileType")
    @Expose
    private String fileType;
    @SerializedName("fileContentType")
    @Expose
    private String fileContentType;
    @SerializedName("expenseStatusID")
    @Expose
    private Integer expenseStatusID;
    @SerializedName("district")
    @Expose
    private String district;
    @SerializedName("createdOnStr")
    @Expose
    private String createdOnStr;
    @SerializedName("districtId")
    @Expose
    private Integer districtId;
    @SerializedName("msg")
    @Expose
    private String msg;
    @SerializedName("completedOnStr")
    @Expose
    private String completedOnStr;
    @SerializedName("se_Name")
    @Expose
    private String seName;
    @SerializedName("completedOn")
    @Expose
    private Object completedOn;
    @SerializedName("expenseId")
    @Expose
    private Integer expenseId;
    @SerializedName("expenseStatus")
    @Expose
    private String expenseStatus;
    @SerializedName("se_ContactNo")
    @Expose
    private String seContactNo;
    @SerializedName("completedBy")
    @Expose
    private Integer completedBy;


    @SerializedName("courierName")
    @Expose
    private String courierName;



    @SerializedName("docketNo")
    @Expose
    private String docketNo;








    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Object getUpdatedOnStr() {
        return updatedOnStr;
    }

    public void setUpdatedOnStr(Object updatedOnStr) {
        this.updatedOnStr = updatedOnStr;
    }

    public Integer getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Integer createdBy) {
        this.createdBy = createdBy;
    }

    public Object getUpdatedOn() {
        return updatedOn;
    }

    public void setUpdatedOn(Object updatedOn) {
        this.updatedOn = updatedOn;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(Integer updatedBy) {
        this.updatedBy = updatedBy;
    }

    public String getRemark() {
        if (remark == null)
            return "";
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

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public Float getTotalExAmount() {
        return totalExAmount;
    }

    public void setTotalExAmount(Float totalExAmount) {
        this.totalExAmount = totalExAmount;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getFileContentType() {
        return fileContentType;
    }

    public void setFileContentType(String fileContentType) {
        this.fileContentType = fileContentType;
    }

    public Integer getExpenseStatusID() {
        if (expenseStatusID == null)
            return 0;
        return expenseStatusID;
    }

    public void setExpenseStatusID(Integer expenseStatusID) {
        this.expenseStatusID = expenseStatusID;
    }

    public String getDistrict() {
        if (district == null)
            return "";
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getCreatedOnStr() {
        if (createdOnStr == null)
            return "";
        return createdOnStr;
    }

    public void setCreatedOnStr(String createdOnStr) {
        this.createdOnStr = createdOnStr;
    }

    public Integer getDistrictId() {
        return districtId;
    }

    public void setDistrictId(Integer districtId) {
        this.districtId = districtId;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getCompletedOnStr() {
        if (completedOnStr == null)
            return "";
        return completedOnStr;
    }

    public void setCompletedOnStr(String completedOnStr) {
        this.completedOnStr = completedOnStr;
    }

    public String getSeName() {
        if (seName == null)
            return "";
        return seName;
    }

    public void setSeName(String seName) {
        this.seName = seName;
    }

    public Object getCompletedOn() {
        return completedOn;
    }

    public void setCompletedOn(Object completedOn) {
        this.completedOn = completedOn;
    }

    public Integer getExpenseId() {
        if (expenseId == null)
            return 0;
        return expenseId;
    }

    public void setExpenseId(Integer expenseId) {
        this.expenseId = expenseId;
    }

    public String getExpenseStatus() {
        if (expenseStatus == null)
            return "";
        return expenseStatus;
    }

    public void setExpenseStatus(String expenseStatus) {
        this.expenseStatus = expenseStatus;
    }

    public String getSeContactNo() {
        return seContactNo;
    }

    public void setSeContactNo(String seContactNo) {
        this.seContactNo = seContactNo;
    }

    public Integer getCompletedBy() {
        return completedBy;
    }

    public void setCompletedBy(Integer completedBy) {
        this.completedBy = completedBy;
    }


    public String getCourierName() {
        return courierName;
    }

    public void setCourierName(String courierName) {
        this.courierName = courierName;
    }

    public String getDocketNo() {
        return docketNo;
    }

    public void setDocketNo(String docketNo) {
        this.docketNo = docketNo;
    }
}
