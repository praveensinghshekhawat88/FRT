package com.callmangement.model.complaints

import com.google.gson.annotations.SerializedName

class ModelResolveComplaint {
    @SerializedName("status")
    var status: String? = null

    @SerializedName("message")
    var message: String? = null
}
