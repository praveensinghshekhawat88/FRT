package com.callmangement.model

import com.google.gson.annotations.SerializedName

class ModelResponse {
    @JvmField
    @SerializedName("status")
    var status: String? = null

    @JvmField
    @SerializedName("message")
    var message: String? = null
}
