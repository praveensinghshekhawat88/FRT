package com.callmangement.model.login

import com.google.gson.annotations.SerializedName

class ModelLogin {
    @kotlin.jvm.JvmField
    @SerializedName("status")
    var status: String? = null

    @SerializedName("message")
    var message: String? = null

    @SerializedName("user_details")
    var user_details: ModelLoginData = ModelLoginData()
}
