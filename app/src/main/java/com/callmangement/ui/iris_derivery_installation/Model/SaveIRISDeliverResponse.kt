package com.callmangement.ui.iris_derivery_installation.Model

import com.google.gson.annotations.SerializedName

class SaveIRISDeliverResponse {
    @SerializedName("Response")
    var response: SaveIRISResponse? = null
    @JvmField
    var status: String? = null

    inner class SaveIRISResponse(var ticketNo: String, var message: String, var isStatus: Boolean)
}
