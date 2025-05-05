package com.callmangement.ehr.models

import com.google.gson.annotations.SerializedName

class ModelLogin {
    @JvmField
    @SerializedName("status")
    var status: String? = null

    @JvmField
    @SerializedName("message")
    var message: String? = null

    @JvmField
    @SerializedName("user_details")
    var user_details: ModelLoginData = ModelLoginData()
}
