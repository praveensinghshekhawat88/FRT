package com.callmangement.ui.ins_weighing_scale.model.district

import com.google.gson.annotations.SerializedName

class ModelDistrict_w {
    @JvmField
    @SerializedName("status")
    var status: String? = null

    @JvmField
    @SerializedName("message")
    var message: String? = null

    @JvmField
    @SerializedName("District_List")
    var district_List: MutableList<ModelDistrictList_w?>? = ArrayList()
}
