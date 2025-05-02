package com.callmangement.model

import com.google.gson.annotations.SerializedName

class ModelMobileVersion {
    @JvmField
    @SerializedName("status")
    var status: String? = null

    @JvmField
    @SerializedName("message")
    var message: String? = null

    @SerializedName("Version_Code")
    var version_Code: Int = 0
}
