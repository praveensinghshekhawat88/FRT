package com.callmangement.model.inventrory

import com.google.gson.annotations.SerializedName

class ModelParts {
    @JvmField
    @SerializedName("status")
    var status: String? = null

    @JvmField
    @SerializedName("message")
    var message: String? = null

    @SerializedName("Parts")
    var parts: List<ModelPartsList>? = ArrayList()
}
