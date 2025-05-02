package com.callmangement.model.complaints

import com.google.gson.annotations.SerializedName

class ModelResolveComplaint {
    @JvmField
    @SerializedName("status")
    var status: String? = null

    @JvmField
    @SerializedName("message")
    var message: String? = null
}
