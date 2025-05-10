package com.callmangement.model.district

import com.google.gson.annotations.SerializedName

class ModelDistrict {
    @JvmField
    @SerializedName("status")
    var status: String? = null

    @JvmField
    @SerializedName("message")
    var message: String? = null

    @JvmField
    @SerializedName("District_List")
    var district_List: MutableList<ModelDistrictList?>? = ArrayList()
}
