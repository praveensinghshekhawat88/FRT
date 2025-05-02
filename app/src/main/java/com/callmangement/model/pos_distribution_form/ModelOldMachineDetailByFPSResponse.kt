package com.callmangement.model.pos_distribution_form

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ModelOldMachineDetailByFPSResponse {
    @JvmField
    @SerializedName("Old_Machine_Data")
    @Expose
    var oldMachineData: OldMachineData? = null

    @SerializedName("message")
    @Expose
    var message: String? = null
        get() {
            if (field == null) return "null"
            return field
        }

    @SerializedName("status")
    @Expose
    var status: String? = null
}
