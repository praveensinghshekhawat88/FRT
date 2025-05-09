package com.callmangement.ui.errors.model

import com.google.gson.annotations.SerializedName

class UpdateErrorStatusResponse(
    var isStatus: Boolean, @JvmField var message: String, @field:SerializedName(
        "ErrorRegNo"
    ) var errorRegNo: String
)
