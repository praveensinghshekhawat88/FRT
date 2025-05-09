package com.callmangement.ui.errors.model

import com.google.gson.annotations.SerializedName

class GetErrorImagesRoot(
    @field:SerializedName("Data") var data: ArrayList<GetErrorImagesDatum>, @field:SerializedName(
        "message"
    ) var message: String, @JvmField @field:SerializedName("status") var status: String
)
