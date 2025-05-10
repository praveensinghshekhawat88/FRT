package com.callmangement.model.login

import com.google.gson.annotations.SerializedName

class ModelVerifyOTP {
    @SerializedName("status")
    var status: String? = null

    @SerializedName("message")
    var message: String? = null

    @SerializedName("id")
    var id: Int = 0

    @SerializedName("name")
    var name: String? = null

    @SerializedName("email")
    var email: String? = null

    @SerializedName("mobile")
    var mobile: String? = null
}
