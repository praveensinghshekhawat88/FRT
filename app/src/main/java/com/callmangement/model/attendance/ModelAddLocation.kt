package com.callmangement.model.attendance

import com.google.gson.annotations.SerializedName

class ModelAddLocation {
    @SerializedName("Status")
    var status: String? = null

    @SerializedName("Massage")
    var massage: String? = null
}
