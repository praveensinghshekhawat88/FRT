package com.callmangement.ehr.models

import com.google.gson.annotations.SerializedName

class ModelTehsil {
    @JvmField
    @SerializedName("status")
    var status: String? = null

    @JvmField
    @SerializedName("message")
    var message: String? = null

    @SerializedName("Tehsil_List")
    var tehsil_List: MutableList<ModelTehsilList?> = ArrayList()
}
