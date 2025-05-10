package com.callmangement.model.inventrory

import com.google.gson.annotations.SerializedName

class ModelSEUsers {
    @JvmField
    @SerializedName("status")
    var status: String? = null

    @JvmField
    @SerializedName("message")
    var message: String? = null

    @SerializedName("SEUsersList")
    var sEUsersList: MutableList<ModelSEUsersList?>? = ArrayList()
}
