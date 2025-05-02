package com.callmangement.model.inventrory;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ModelAvailableStockPartsList implements Serializable {
    @SerializedName("itemName") private String itemName;
    @SerializedName("createdOn") private String createdOn;
    @SerializedName("isActive") private String isActive;
    @SerializedName("userId") private String userId;
    @SerializedName("updatedOn") private String updatedOn;
    @SerializedName("item_Qty") private String item_Qty;
    @SerializedName("itemId") private String itemId;
    @SerializedName("stockId") private String stockId;
    @SerializedName("itemStockStatusId") private String itemStockStatusId;
    @SerializedName("quantity") private String quantity;
    boolean selectFlag = false;

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

    public boolean isSelectFlag() {
        return selectFlag;
    }

    public void setSelectFlag(boolean selectFlag) {
        this.selectFlag = selectFlag;
    }

    public String getQuantity() {
        if (quantity == null || quantity.isEmpty())
            return "0";
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }
}
