package com.callmangement.model.attendance

import com.google.gson.annotations.SerializedName

class ModelAddLocationList {
    @SerializedName("Status")
    var status: String? = null

    @SerializedName("Massage")
    var massage: String? = null

    @JvmField
    @SerializedName("location_list")
    var location_list: List<ModelAddLocationData> = ArrayList()
}
