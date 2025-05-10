package com.callmangement.model.inventrory

import com.google.gson.annotations.SerializedName

class ModelSavePartsDispatchDetails {
    @JvmField
    @SerializedName("status")
    var status: String? = null

    @JvmField
    @SerializedName("message")
    var message: String? = null

    @JvmField
    @SerializedName("partsDispatchDetails")
    var modelPartsDispatchInvoiceList: ModelPartsDispatchInvoiceList =
        ModelPartsDispatchInvoiceList()
}
