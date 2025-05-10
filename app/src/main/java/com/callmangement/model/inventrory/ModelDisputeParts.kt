package com.callmangement.model.inventrory

import com.google.gson.annotations.SerializedName

class ModelDisputeParts {
    @SerializedName("status")
    var status: String? = null

    @SerializedName("message")
    var message: String? = null

    @SerializedName("partsDispueStockDetails")
    var partsDispueStockDetails: List<ModelDisputePartsList?> = ArrayList()
}
