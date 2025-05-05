package com.callmangement.ehr.models

import com.google.gson.annotations.SerializedName

class ModelSEUser {
    @JvmField
    @SerializedName("status")
    var status: String? = null

    @JvmField
    @SerializedName("message")
    var message: String? = null

    @SerializedName("SEUsersList")
    var sEUsersList: MutableList<ModelSEUserList?> = ArrayList()
}
