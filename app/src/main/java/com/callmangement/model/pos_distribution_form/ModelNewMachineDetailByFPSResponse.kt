package com.callmangement.model.pos_distribution_form

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ModelNewMachineDetailByFPSResponse {
    @JvmField
    @SerializedName("New_Machine_Data")
    @Expose
    var newMachineData: NewMachineData? = null

    @JvmField
    @SerializedName("message")
    @Expose
    var message: String? = null

    @SerializedName("status")
    @Expose
    var status: String? = null
}
