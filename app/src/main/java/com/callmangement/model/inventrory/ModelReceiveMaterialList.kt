package com.callmangement.model.inventrory

import com.google.gson.annotations.SerializedName

class ModelReceiveMaterialList {
    @SerializedName("status")
    var status: String? = null

    @SerializedName("message")
    var message: String? = null

    @SerializedName("data")
    var data: List<ModelReceiveMaterialListData> = ArrayList()
}
