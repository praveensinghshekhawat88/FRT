package com.callmangement.model.inventrory

import com.google.gson.annotations.SerializedName

class ModelUpdatePartsStock {
    @SerializedName("status")
    var status: String? = null

    @SerializedName("message")
    var message: String? = null
}
