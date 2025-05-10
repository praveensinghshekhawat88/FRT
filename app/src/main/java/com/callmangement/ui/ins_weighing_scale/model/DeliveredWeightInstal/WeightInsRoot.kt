package com.callmangement.ui.ins_weighing_scale.model.DeliveredWeightInstal

import com.google.gson.annotations.SerializedName

class WeightInsRoot(
    @field:SerializedName("Data") var data: ArrayList<WeighInsData>,
    @JvmField var message: String,
    @JvmField var status: String
)
