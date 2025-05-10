package com.callmangement.model.inventrory

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class ModelDisputePartsList : Serializable {
    @SerializedName("itemName")
    var itemName: String? = null
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

    @SerializedName("remarks")
    var remarks: String? = null
        get() {
            if (field == null) return ""
            return field
        }

    @SerializedName("invoiceId")
    var invoiceId: String? = null
        get() {
            if (field == null) return "0"
            return field
        }

    @SerializedName("dispatchId")
    var dispatchId: String? = null
        get() {
            if (field == null) return "0"
            return field
        }
}
