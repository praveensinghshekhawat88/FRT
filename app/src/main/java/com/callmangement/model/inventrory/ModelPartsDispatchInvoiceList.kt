package com.callmangement.model.inventrory;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ModelPartsDispatchInvoiceList implements Serializable {
    @SerializedName("msg") private String msg;
    @SerializedName("status") private String status;
    @SerializedName("isActive") private String isActive;
    @SerializedName("districtId") private String districtId;
    @SerializedName("dispatchTo") private String dispatchTo;
    @SerializedName("isSubmitted") private String isSubmitted;
    @SerializedName("dispatchFrom") private String dispatchFrom;
    @SerializedName("invoiceId") private String invoiceId;
    @SerializedName("dispatchId") private String dispatchId;
    @SerializedName("createdOn") private String createdOn;
    @SerializedName("reciverName") private String reciverName;
    @SerializedName("receivedBy") private String receivedBy;
    @SerializedName("itemId") private String itemId;
    @SerializedName("itemName") private String itemName;
    @SerializedName("isReceived") private String isReceived;
    @SerializedName("dispatcherRemarks") private String dispatcherRemarks;
    @SerializedName("dispatchChallanImage") private String dispatchChallanImage;
    @SerializedName("dispatcherName") private String dispatcherName;
    @SerializedName("districtNameEng") private String districtNameEng;
    @SerializedName("item_Received_Status") private String item_Received_Status;
    @SerializedName("dispatch_Item_Qty") private String dispatch_Item_Qty;
    @SerializedName("receiverRemark") private String receiverRemark;
    @SerializedName("item_Submit_Status") private String item_Submit_Status;
    @SerializedName("dispatchChallanNo") private String dispatchChallanNo;
    @SerializedName("received_Item_Qty") private String received_Item_Qty;
    @SerializedName("receivedDateStr") private String receivedDateStr;
    @SerializedName("dispatchDateStr") private String dispatchDateStr;
    @SerializedName("itemStockStatus") private String itemStockStatus;
    @SerializedName("itemStockStatusId") private String itemStockStatusId;
    @SerializedName("courierName") private String courierName;
    @SerializedName("courierTrackingNo") private String courierTrackingNo;
    @SerializedName("dispChalImage") private String dispChalImage;
    @SerializedName("partsImage_2") private String partsImage_2;
    @SerializedName("partsImage_1") private String partsImage_1;
    @SerializedName("receivedPartsImage") private String receivedPartsImage;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getIsActive() {
        return isActive;
    }

    public void setIsActive(String isActive) {
        this.isActive = isActive;
    }

    public String getDistrictId() {
        if (districtId == null)
            return "0";
        return districtId;
    }

    public void setDistrictId(String districtId) {
        this.districtId = districtId;
    }

    public String getDispatchTo() {
        return dispatchTo;
    }

    public void setDispatchTo(String dispatchTo) {
        this.dispatchTo = dispatchTo;
    }

    public String getIsSubmitted() {
        return isSubmitted;
    }

    public void setIsSubmitted(String isSubmitted) {
        this.isSubmitted = isSubmitted;
    }

    public String getDispatchFrom() {
        return dispatchFrom;
    }

    public void setDispatchFrom(String dispatchFrom) {
        this.dispatchFrom = dispatchFrom;
    }

    public String getInvoiceId() {
        if (invoiceId == null)
            return "0";
        return invoiceId;
    }

    public void setInvoiceId(String invoiceId) {
        this.invoiceId = invoiceId;
    }

    public String getDispatchId() {
        if (dispatchId == null)
            return "0";
        return dispatchId;
    }

    public void setDispatchId(String dispatchId) {
        this.dispatchId = dispatchId;
    }

    public String getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(String createdOn) {
        this.createdOn = createdOn;
    }

    public String getReciverName() {
        if (reciverName == null)
            return "";
        return reciverName;
    }

    public void setReciverName(String reciverName) {
        this.reciverName = reciverName;
    }

    public String getReceivedBy() {
        return receivedBy;
    }

    public void setReceivedBy(String receivedBy) {
        this.receivedBy = receivedBy;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getIsReceived() {
        return isReceived;
    }

    public void setIsReceived(String isReceived) {
        this.isReceived = isReceived;
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

    public String getDispatcherName() {
        if (dispatcherName == null)
            return "";
        return dispatcherName;
    }

    public void setDispatcherName(String dispatcherName) {
        this.dispatcherName = dispatcherName;
    }

    public String getDistrictNameEng() {
        if (districtNameEng == null)
            return "";
        return districtNameEng;
    }

    public void setDistrictNameEng(String districtNameEng) {
        this.districtNameEng = districtNameEng;
    }

    public String getItem_Received_Status() {
        return item_Received_Status;
    }

    public void setItem_Received_Status(String item_Received_Status) {
        this.item_Received_Status = item_Received_Status;
    }

    public String getDispatch_Item_Qty() {
        if (dispatch_Item_Qty == null)
            return "0";
        return dispatch_Item_Qty;
    }

    public void setDispatch_Item_Qty(String dispatch_Item_Qty) {
        this.dispatch_Item_Qty = dispatch_Item_Qty;
    }

    public String getReceiverRemark() {
        if (receiverRemark == null)
            return "";
        return receiverRemark;
    }

    public void setReceiverRemark(String receiverRemark) {
        this.receiverRemark = receiverRemark;
    }

    public String getItem_Submit_Status() {
        return item_Submit_Status;
    }

    public void setItem_Submit_Status(String item_Submit_Status) {
        this.item_Submit_Status = item_Submit_Status;
    }

    public String getDispatchChallanNo() {
        return dispatchChallanNo;
    }

    public void setDispatchChallanNo(String dispatchChallanNo) {
        this.dispatchChallanNo = dispatchChallanNo;
    }

    public String getReceived_Item_Qty() {
        if (received_Item_Qty == null)
            return "0";
        return received_Item_Qty;
    }

    public void setReceived_Item_Qty(String received_Item_Qty) {
        this.received_Item_Qty = received_Item_Qty;
    }

    public String getReceivedDateStr() {
        if (receivedDateStr == null)
            return "";
        return receivedDateStr;
    }

    public void setReceivedDateStr(String receivedDateStr) {
        this.receivedDateStr = receivedDateStr;
    }

    public String getDispatchDateStr() {
        if (dispatchDateStr == null)
            return "";
        return dispatchDateStr;
    }

    public void setDispatchDateStr(String dispatchDateStr) {
        this.dispatchDateStr = dispatchDateStr;
    }

    public String getItemStockStatus() {
        if (itemStockStatus == null)
            return "";
        return itemStockStatus;
    }

    public void setItemStockStatus(String itemStockStatus) {
        this.itemStockStatus = itemStockStatus;
    }

    public String getItemStockStatusId() {
        if (itemStockStatusId == null)
            return "0";
        return itemStockStatusId;
    }

    public void setItemStockStatusId(String itemStockStatusId) {
        this.itemStockStatusId = itemStockStatusId;
    }

    public String getCourierName() {
        if (courierName == null)
            return "";
        return courierName;
    }

    public void setCourierName(String courierName) {
        this.courierName = courierName;
    }

    public String getCourierTrackingNo() {
        if (courierTrackingNo == null)
            return "";
        return courierTrackingNo;
    }

    public void setCourierTrackingNo(String courierTrackingNo) {
        this.courierTrackingNo = courierTrackingNo;
    }

    public String getDispChalImage() {
        if (dispChalImage == null)
            return "";
        return dispChalImage;
    }

    public void setDispChalImage(String dispChalImage) {
        this.dispChalImage = dispChalImage;
    }

    public String getPartsImage_2() {
        if (partsImage_2 == null)
            return "";
        return partsImage_2;
    }

    public void setPartsImage_2(String partsImage_2) {
        this.partsImage_2 = partsImage_2;
    }

    public String getPartsImage_1() {
        if (partsImage_1 == null)
            return "";
        return partsImage_1;
    }

    public void setPartsImage_1(String partsImage_1) {
        this.partsImage_1 = partsImage_1;
    }

    public String getReceivedPartsImage() {
        if (receivedPartsImage == null)
            return "";
        return receivedPartsImage;
    }

    public void setReceivedPartsImage(String receivedPartsImage) {
        this.receivedPartsImage = receivedPartsImage;
    }
}
