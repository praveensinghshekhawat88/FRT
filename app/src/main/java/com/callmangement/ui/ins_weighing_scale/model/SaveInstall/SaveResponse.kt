package com.callmangement.ui.ins_weighing_scale.model.SaveInstall

import com.google.gson.annotations.SerializedName

class SaveResponse(
    @field:SerializedName("TicketNo") var ticketNo: String,
    var message: String,
    var isStatus: Boolean
)
