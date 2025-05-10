package com.callmangement.ui.model.inventory.receive_invoice_parts

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class PartsDispatchInvoice {
    var isSelectFlag: Boolean = false
    @JvmField
    var quanity: String = ""

    fun setQuantity(quanity: String) {
        this.quanity = quanity
    }

    @SerializedName("msg")
    @Expose
    var msg: Any? = null

    @SerializedName("status")
    @Expose
    var status: Boolean? = null

    @SerializedName("itemName")
    @Expose
    var itemName: String? = null
        get() {
            if (field == null) return ""
            return field
        }

    @SerializedName("saveDateStr")
    @Expose
    var saveDateStr: Any? = null

    @SerializedName("receivedBy")
    @Expose
    var receivedBy: Int? = null

    @SerializedName("reciverName")
    @Expose
    var reciverName: Any? = null

    @SerializedName("dispatchChallanNo")
    @Expose
    var dispatchChallanNo: String? = null

    @SerializedName("dispatchDateStr")
    @Expose
    var dispatchDateStr: String? = null

    @SerializedName("item_Received_Status")
    @Expose
    var itemReceivedStatus: String? = null

    @SerializedName("dispatcherName")
    @Expose
    var dispatcherName: String? = null

    @SerializedName("received_Item_Qty")
    @Expose
    var receivedItemQty: Int? = null
        get() {
            if (field == null) return 0
            return field
        }

    @SerializedName("dispatch_Item_Qty")
    @Expose
    var dispatchItemQty: Int? = null
        get() {
            if (field == null) return 0
            return field
        }

    @SerializedName("receivedDateStr")
    @Expose
    var receivedDateStr: Any? = null

    @SerializedName("item_Submit_Status")
    @Expose
    var itemSubmitStatus: String? = null

    @SerializedName("districtNameEng")
    @Expose
    var districtNameEng: String? = null

    @SerializedName("receiverRemark")
    @Expose
    var receiverRemark: Any? = null

    @SerializedName("dispatcherRemarks")
    @Expose
    var dispatcherRemarks: String? = null

    @SerializedName("dispatchChallanImage")
    @Expose
    var dispatchChallanImage: String? = null

    @SerializedName("districtId")
    @Expose
    var districtId: Any? = null

    @SerializedName("invoiceId")
    @Expose
    var invoiceId: Int? = null

    @SerializedName("isSubmitted")
    @Expose
    var isSubmitted: Boolean? = null

    @JvmField
    @SerializedName("dispatchId")
    @Expose
    var dispatchId: Int? = null

    @SerializedName("isReceived")
    @Expose
    var isReceived: Boolean? = null

    @SerializedName("dispatchFrom")
    @Expose
    var dispatchFrom: Int? = null

    @SerializedName("dispatchTo")
    @Expose
    var dispatchTo: Any? = null

    @SerializedName("createdOn")
    @Expose
    var createdOn: Any? = null

    @SerializedName("isActive")
    @Expose
    var isActive: Boolean? = null

    @SerializedName("updatedOn")
    @Expose
    var updatedOn: Any? = null

    @SerializedName("itemId")
    @Expose
    var itemId: Int? = null
        get() {
            if (field == null) return 0
            return field
        }

    @SerializedName("receivedOn")
    @Expose
    var receivedOn: Any? = null
}
