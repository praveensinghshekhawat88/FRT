package com.callmangement.model.inventrory;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ModelDisputePartsList implements Serializable {
    @SerializedName("itemName") private String itemName;
    @SerializedName("createdOn") private String createdOn;
    @SerializedName("isActive") private String isActive;
    @SerializedName("userId") private String userId;
    @SerializedName("updatedOn") private String updatedOn;
    @SerializedName("item_Qty") private String item_Qty;
    @SerializedName("itemId") private String itemId;
    @SerializedName("stockId") private String stockId;
    @SerializedName("itemStockStatusId") private String itemStockStatusId;
    @SerializedName("remarks") private String remarks;
    @SerializedName("invoiceId") private String invoiceId;
    @SerializedName("dispatchId") private String dispatchId;

    public String getItemName() {
        if (itemName == null)
            return "";
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getCreatedOn() {
        if (createdOn == null)
            return "";
        return createdOn;
    }

    public void setCreatedOn(String createdOn) {
        this.createdOn = createdOn;
    }

    public String getIsActive() {
        if (isActive == null)
            return "";
        return isActive;
    }

    public void setIsActive(String isActive) {
        this.isActive = isActive;
    }

    public String getUserId() {
        if (userId == null)
            return "0";
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUpdatedOn() {
        if (updatedOn == null)
            return "";
        return updatedOn;
    }

    public void setUpdatedOn(String updatedOn) {
        this.updatedOn = updatedOn;
    }

    public String getItem_Qty() {
        if (item_Qty == null)
            return "0";
        return item_Qty;
    }

    public void setItem_Qty(String item_Qty) {
        this.item_Qty = item_Qty;
    }

    public String getItemId() {
        if (itemId == null)
            return "0";
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getStockId() {
        if (stockId == null)
            return "0";
        return stockId;
    }

    public void setStockId(String stockId) {
        this.stockId = stockId;
    }

    public String getItemStockStatusId() {
        if (itemStockStatusId == null)
            return "0";
        return itemStockStatusId;
    }

    public void setItemStockStatusId(String itemStockStatusId) {
        this.itemStockStatusId = itemStockStatusId;
    }

    public String getRemarks() {
        if (remarks == null)
            return "";
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
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
}
