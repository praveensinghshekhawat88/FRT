package com.callmangement.model.inventrory

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class ModelPartsList : Serializable {
    @SerializedName("districtName")
    var districtName: String? = null
        get() {
            if (field == null) return ""
            return field
        }

    @SerializedName("itemName")
    var itemName: String? = null
        get() {
            if (field == null) return ""
            return field
        }

    @SerializedName("sename")
    var sename: String? = null
        get() {
            if (field == null) return ""
            return field
        }

    @SerializedName("createdOn")
    var createdOn: String? = null
        get() {
            if (field == null) return ""
            return field
        }

    @SerializedName("isActive")
    var isActive: String? = null
        get() {
            if (field == null) return ""
            return field
        }

    @SerializedName("userId")
    var userId: String? = null
        get() {
            if (field == null) return "0"
            return field
        }

    @SerializedName("updatedOn")
    var updatedOn: String? = null
        get() {
            if (field == null) return ""
            return field
        }

    @SerializedName("item_Qty")
    var item_Qty: String? = null
        get() {
            if (field == null) return "0"
            return field
        }

    @SerializedName("itemId")
    var itemId: String? = null
        get() {
            if (field == null) return "0"
            return field
        }

    @SerializedName("stockId")
    var stockId: String? = null
        get() {
            if (field == null) return "0"
            return field
        }

    @SerializedName("itemStockStatusId")
    var itemStockStatusId: String? = null
        get() {
            if (field == null) return "0"
            return field
        }

    @SerializedName("quantity")
    private var quantity: String? = null
    var isSelectFlag: Boolean = false
    var isVisibleItemFlag: Boolean = false

    fun getQuantity(): String {
        if (quantity == null || quantity!!.isEmpty()) return "0"
        return quantity!!
    }

    fun setQuantity(quantity: String?) {
        this.quantity = quantity
    }
}
