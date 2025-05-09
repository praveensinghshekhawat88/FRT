package com.callmangement.ui.errors.model

import com.google.gson.annotations.SerializedName

class GetPosDeviceErrorsRoot(
    @field:SerializedName("Data") var data: ArrayList<GetPosDeviceErrorDatum>,
    var message: String,
    @JvmField var status: String
)
