package com.callmangement.ui.errors.model

import com.google.gson.annotations.SerializedName

class GetRemarkRoot(
    @field:SerializedName("Data") var data: ArrayList<GetRemarkDatum>,
    var message: String,
    @JvmField var status: String
)
