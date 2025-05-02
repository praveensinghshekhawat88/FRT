package com.callmangement.model.inventrory

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class ModelReceiveMaterialListData : Serializable {
    @SerializedName("date")
    var date: String? = null

    @SerializedName("quantity")
    var quantity: String? = null

    @SerializedName("status")
    var status: String? = null

    @SerializedName("materialList")
    var materialList: List<ModelReceiveMaterialListDataList> = ArrayList()
}
