package com.callmangement.model.inventrory

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class ModelPartsDispatchInvoiceList : Serializable {
    @SerializedName("msg")
    var msg: String? = null

    @SerializedName("status")
    var status: String? = null

    @SerializedName("isActive")
    var isActive: String? = null

    @SerializedName("districtId")
    var districtId: String? = null
        get() {
            if (field == null) return "0"
            return field
        }

    @SerializedName("dispatchTo")
    var dispatchTo: String? = null

    @JvmField
    @SerializedName("isSubmitted")
    var isSubmitted: String? = null

    @SerializedName("dispatchFrom")
    var dispatchFrom: String? = null

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

    @SerializedName("createdOn")
    var createdOn: String? = null

    @SerializedName("reciverName")
    var reciverName: String? = null
        get() {
            if (field == null) return ""
            return field
        }

    @SerializedName("receivedBy")
    var receivedBy: String? = null

    @SerializedName("itemId")
    var itemId: String? = null

    @JvmField
    @SerializedName("itemName")
    var itemName: String? = null

    @JvmField
    @SerializedName("isReceived")
    var isReceived: String? = null

    @SerializedName("dispatcherRemarks")
    var dispatcherRemarks: String? = null

    @SerializedName("dispatchChallanImage")
    var dispatchChallanImage: String? = null

    @SerializedName("dispatcherName")
    var dispatcherName: String? = null
        get() {
            if (field == null) return ""
            return field
        }

    @SerializedName("districtNameEng")
    var districtNameEng: String? = null
        get() {
            if (field == null) return ""
            return field
        }

    @SerializedName("item_Received_Status")
    var item_Received_Status: String? = null

    @SerializedName("dispatch_Item_Qty")
    var dispatch_Item_Qty: String? = null
        get() {
            if (field == null) return "0"
            return field
        }

    @SerializedName("receiverRemark")
    var receiverRemark: String? = null
        get() {
            if (field == null) return ""
            return field
        }

    @SerializedName("item_Submit_Status")
    var item_Submit_Status: String? = null

    @SerializedName("dispatchChallanNo")
    var dispatchChallanNo: String? = null

    @SerializedName("received_Item_Qty")
    var received_Item_Qty: String? = null
        get() {
            if (field == null) return "0"
            return field
        }

    @SerializedName("receivedDateStr")
    var receivedDateStr: String? = null
        get() {
            if (field == null) return ""
            return field
        }

    @SerializedName("dispatchDateStr")
    var dispatchDateStr: String? = null
        get() {
            if (field == null) return ""
            return field
        }

    @SerializedName("itemStockStatus")
    var itemStockStatus: String? = null
        get() {
            if (field == null) return ""
            return field
        }

    @SerializedName("itemStockStatusId")
    var itemStockStatusId: String? = null
        get() {
            if (field == null) return "0"
            return field
        }

    @SerializedName("courierName")
    var courierName: String? = null
        get() {
            if (field == null) return ""
            return field
        }

    @SerializedName("courierTrackingNo")
    var courierTrackingNo: String? = null
        get() {
            if (field == null) return ""
            return field
        }

    @SerializedName("dispChalImage")
    var dispChalImage: String? = null
        get() {
            if (field == null) return ""
            return field
        }

    @SerializedName("partsImage_2")
    var partsImage_2: String? = null
        get() {
            if (field == null) return ""
            return field
        }

    @SerializedName("partsImage_1")
    var partsImage_1: String? = null
        get() {
            if (field == null) return ""
            return field
        }

    @SerializedName("receivedPartsImage")
    var receivedPartsImage: String? = null
        get() {
            if (field == null) return ""
            return field
        }
}
