package com.callmangement.model.logout

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class ModelLogout : Serializable {
    @JvmField
    @SerializedName("status")
    var status: String? = null

    @JvmField
    @SerializedName("message")
    var message: String? = null
}
