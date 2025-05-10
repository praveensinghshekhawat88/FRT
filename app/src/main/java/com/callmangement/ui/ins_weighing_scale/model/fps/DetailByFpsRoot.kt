package com.callmangement.ui.ins_weighing_scale.model.fps

import com.google.gson.annotations.SerializedName

class DetailByFpsRoot(
    @field:SerializedName("Data") var data: DetailByFpsData,
    @JvmField var message: String,
    @JvmField var status: String
)
