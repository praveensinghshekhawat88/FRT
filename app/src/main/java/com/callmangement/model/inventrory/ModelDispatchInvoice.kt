package com.callmangement.model.inventrory

import com.google.gson.annotations.SerializedName

class ModelDispatchInvoice {
    @JvmField
    @SerializedName("status")
    var status: String? = null

    @SerializedName("message")
    var message: String? = null

    @JvmField
    @SerializedName("partsDispatchInvoiceList")
    var partsDispatchInvoiceList: List<ModelPartsDispatchInvoiceList?> = ArrayList()
}
