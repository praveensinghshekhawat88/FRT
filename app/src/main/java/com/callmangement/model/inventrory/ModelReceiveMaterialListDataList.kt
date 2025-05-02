package com.callmangement.model.inventrory

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class ModelReceiveMaterialListDataList : Serializable {
    @SerializedName("id")
    var id: String? = null

    @SerializedName("product_name")
    var product_name: String? = null

    @SerializedName("product_id")
    var product_id: String? = null

    @SerializedName("district_id")
    var district_id: String? = null

    @SerializedName("quantity")
    var quantity: String? = null

    @SerializedName("created_date")
    var created_date: String? = null

    @SerializedName("updated_date")
    var updated_date: String? = null

    @SerializedName("status")
    var status: String? = null
}
