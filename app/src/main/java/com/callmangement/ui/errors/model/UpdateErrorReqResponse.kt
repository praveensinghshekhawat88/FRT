package com.callmangement.ui.errors.model

import com.google.gson.annotations.SerializedName

class UpdateErrorReqResponse(
    var isStatus: Boolean,
    @JvmField var message:
    String, @field:SerializedName(
        "ErrorRegNo"
    ) var errorRegNo: String
)





