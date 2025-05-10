package com.callmangement.model.fps_wise_complaints

import com.google.gson.annotations.SerializedName

class ModelFPSDistTehWise {
    @SerializedName("status")
    var status: String? = null
        get() {
            if (field == null) return ""
            return field
        }

    @SerializedName("message")
    var message: String? = null
        get() {
            if (field == null) return ""
            return field
        }

    @SerializedName("FPSList")
    var modelFPSDistTehWiseList: MutableList<ModelFPSDistTehWiseList>? = ArrayList()
}
