package com.callmangement.ehr.models

import com.google.gson.annotations.SerializedName

class ModelDeleteACamp {
    @JvmField
    @SerializedName("status")
    var status: String? = null

    @JvmField
    @SerializedName("message")
    var message: String? = null
}
