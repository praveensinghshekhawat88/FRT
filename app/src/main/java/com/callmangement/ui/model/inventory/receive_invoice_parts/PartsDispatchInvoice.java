
package com.callmangement.ui.model.inventory.receive_invoice_parts;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PartsDispatchInvoice {

    boolean selectFlag = false;
    String quanity = "";

    public String getQuanity() {
        return quanity;
    }

    public void setQuantity(String quanity) {
        this.quanity = quanity;
    }

    public boolean isSelectFlag() {
        return selectFlag;
    }

    public void setSelectFlag(boolean selectFlag) {
        this.selectFlag = selectFlag;
    }

    @SerializedName("msg")
    @Expose
    private Object msg;
    @SerializedName("status")
    @Expose
    private Boolean status;
    @SerializedName("itemName")
    @Expose
    private String itemName;
    @SerializedName("saveDateStr")
    @Expose
    private Object saveDateStr;
    @SerializedName("receivedBy")
    @Expose
    private Integer receivedBy;
    @SerializedName("reciverName")
    @Expose
    private Object reciverName;
    @SerializedName("dispatchChallanNo")
    @Expose
    private String dispatchChallanNo;
    @SerializedName("dispatchDateStr")
    @Expose
    private String dispatchDateStr;
    @SerializedName("item_Received_Status")
    @Expose
    private String itemReceivedStatus;
    @SerializedName("dispatcherName")
    @Expose
    private String dispatcherName;
    @SerializedName("received_Item_Qty")
    @Expose
    private Integer receivedItemQty;
    @SerializedName("dispatch_Item_Qty")
    @Expose
    private Integer dispatchItemQty;
    @SerializedName("receivedDateStr")
    @Expose
    private Object receivedDateStr;
    @SerializedName("item_Submit_Status")
    @Expose
    private String itemSubmitStatus;
    @SerializedName("districtNameEng")
    @Expose
    private String districtNameEng;
    @SerializedName("receiverRemark")
    @Expose
    private Object receiverRemark;
    @SerializedName("dispatcherRemarks")
    @Expose
    private String dispatcherRemarks;
    @SerializedName("dispatchChallanImage")
    @Expose
    private String dispatchChallanImage;
    @SerializedName("districtId")
    @Expose
    private Object districtId;
    @SerializedName("invoiceId")
    @Expose
    private Integer invoiceId;
    @SerializedName("isSubmitted")
    @Expose
    private Boolean isSubmitted;
    @SerializedName("dispatchId")
    @Expose
    private Integer dispatchId;
    @SerializedName("isReceived")
    @Expose
    private Boolean isReceived;
    @SerializedName("dispatchFrom")
    @Expose
    private Integer dispatchFrom;
    @SerializedName("dispatchTo")
    @Expose
    private Object dispatchTo;
    @SerializedName("createdOn")
    @Expose
    private Object createdOn;
    @SerializedName("isActive")
    @Expose
    private Boolean isActive;
    @SerializedName("updatedOn")
    @Expose
    private Object updatedOn;
    @SerializedName("itemId")
    @Expose
    private Integer itemId;
    @SerializedName("receivedOn")
    @Expose
    private Object receivedOn;

    public Object getMsg() {
        return msg;
    }

    public void setMsg(Object msg) {
        this.msg = msg;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public String getItemName() {
        if (itemName == null)
            return "";
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public Object getSaveDateStr() {
        return saveDateStr;
    }

    public void setSaveDateStr(Object saveDateStr) {
        this.saveDateStr = saveDateStr;
    }

    public Integer getReceivedBy() {
        return receivedBy;
    }

    public void setReceivedBy(Integer receivedBy) {
        this.receivedBy = receivedBy;
    }

    public Object getReciverName() {
        return reciverName;
    }

    public void setReciverName(Object reciverName) {
        this.reciverName = reciverName;
    }

    public String getDispatchChallanNo() {
        return dispatchChallanNo;
    }

    public void setDispatchChallanNo(String dispatchChallanNo) {
        this.dispatchChallanNo = dispatchChallanNo;
    }

    public String getDispatchDateStr() {
        return dispatchDateStr;
    }

    public void setDispatchDateStr(String dispatchDateStr) {
        this.dispatchDateStr = dispatchDateStr;
    }

    public String getItemReceivedStatus() {
        return itemReceivedStatus;
    }

    public void setItemReceivedStatus(String itemReceivedStatus) {
        this.itemReceivedStatus = itemReceivedStatus;
    }

    public String getDispatcherName() {
        return dispatcherName;
    }

    public void setDispatcherName(String dispatcherName) {
        this.dispatcherName = dispatcherName;
    }

    public Integer getReceivedItemQty() {
        if (receivedItemQty == null)
            return 0;
        return receivedItemQty;
    }

    public void setReceivedItemQty(Integer receivedItemQty) {
        this.receivedItemQty = receivedItemQty;
    }

    public Integer getDispatchItemQty() {
        if (dispatchItemQty == null)
            return 0;
        return dispatchItemQty;
    }

    public void setDispatchItemQty(Integer dispatchItemQty) {
        this.dispatchItemQty = dispatchItemQty;
    }

    public Object getReceivedDateStr() {
        return receivedDateStr;
    }

    public void setReceivedDateStr(Object receivedDateStr) {
        this.receivedDateStr = receivedDateStr;
    }

    public String getItemSubmitStatus() {
        return itemSubmitStatus;
    }

    public void setItemSubmitStatus(String itemSubmitStatus) {
        this.itemSubmitStatus = itemSubmitStatus;
    }

    public String getDistrictNameEng() {
        return districtNameEng;
    }

    public void setDistrictNameEng(String districtNameEng) {
        this.districtNameEng = districtNameEng;
    }

    public Object getReceiverRemark() {
        return receiverRemark;
    }

    public void setReceiverRemark(Object receiverRemark) {
        this.receiverRemark = receiverRemark;
    }

    public String getDispatcherRemarks() {
        return dispatcherRemarks;
    }

    public void setDispatcherRemarks(String dispatcherRemarks) {
        this.dispatcherRemarks = dispatcherRemarks;
    }

    public String getDispatchChallanImage() {
        return dispatchChallanImage;
    }

    public void setDispatchChallanImage(String dispatchChallanImage) {
        this.dispatchChallanImage = dispatchChallanImage;
    }

    public Object getDistrictId() {
        return districtId;
    }

    public void setDistrictId(Object districtId) {
        this.districtId = districtId;
    }

    public Integer getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(Integer invoiceId) {
        this.invoiceId = invoiceId;
    }

    public Boolean getIsSubmitted() {
        return isSubmitted;
    }

    public void setIsSubmitted(Boolean isSubmitted) {
        this.isSubmitted = isSubmitted;
    }

    public Integer getDispatchId() {
        return dispatchId;
    }

    public void setDispatchId(Integer dispatchId) {
        this.dispatchId = dispatchId;
    }

    public Boolean getIsReceived() {
        return isReceived;
    }

    public void setIsReceived(Boolean isReceived) {
        this.isReceived = isReceived;
    }

    public Integer getDispatchFrom() {
        return dispatchFrom;
    }

    public void setDispatchFrom(Integer dispatchFrom) {
        this.dispatchFrom = dispatchFrom;
    }

    public Object getDispatchTo() {
        return dispatchTo;
    }

    public void setDispatchTo(Object dispatchTo) {
        this.dispatchTo = dispatchTo;
    }

    public Object getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Object createdOn) {
        this.createdOn = createdOn;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public Object getUpdatedOn() {
        return updatedOn;
    }

    public void setUpdatedOn(Object updatedOn) {
        this.updatedOn = updatedOn;
    }

    public Integer getItemId() {
        if (itemId == null)
            return 0;
        return itemId;
    }

    public void setItemId(Integer itemId) {
        this.itemId = itemId;
    }

    public Object getReceivedOn() {
        return receivedOn;
    }

    public void setReceivedOn(Object receivedOn) {
        this.receivedOn = receivedOn;
    }

}
