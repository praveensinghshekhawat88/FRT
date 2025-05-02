package com.callmangement.model.inventrory

import com.google.gson.annotations.SerializedName

class ModelInventoryResponse {
    @SerializedName("status")
    var status: String? = null

    @SerializedName("message")
    var message: String? = null
}
