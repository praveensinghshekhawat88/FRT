package com.callmangement.model.logout

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class ModelLogout : Serializable {
    @SerializedName("status")
    var status: String? = null

    @SerializedName("message")
    var message: String? = null
}
