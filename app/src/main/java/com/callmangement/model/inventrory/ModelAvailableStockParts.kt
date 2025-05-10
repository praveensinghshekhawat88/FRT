package com.callmangement.model.inventrory

import com.google.gson.annotations.SerializedName

class ModelAvailableStockParts {
    @SerializedName("status")
    var status: String? = null

    @SerializedName("message")
    var message: String? = null

    @SerializedName("Parts")
    var parts: List<ModelAvailableStockPartsList> = ArrayList()
}
