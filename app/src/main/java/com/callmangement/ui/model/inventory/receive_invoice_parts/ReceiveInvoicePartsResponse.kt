package com.callmangement.ui.model.inventory.receive_invoice_parts

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ReceiveInvoicePartsResponse {
    @SerializedName("message")
    @Expose
    var message: Any? = null

    @JvmField
    @SerializedName("status")
    @Expose
    var status: String? = null

    @SerializedName("partsDispatchInvoiceList")
    @Expose
    var partsDispatchInvoiceList: List<PartsDispatchInvoice>? = null
        get() {
            if (field == null) return ArrayList()
            return field
        }
}
