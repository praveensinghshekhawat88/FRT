package com.callmangement.ui.ins_weighing_scale.model.Count

import com.google.gson.annotations.SerializedName

class CountRoot(
    @field:SerializedName("Data") var data: ArrayList<CountDatum>,
    @JvmField var message: String,
    @JvmField var status: String
)
