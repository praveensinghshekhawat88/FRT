package com.callmangement.ui.ins_weighing_scale.model.challan

import com.google.gson.annotations.SerializedName

class challanRoot(
    @field:SerializedName("Data") var data: challanData,
    @JvmField var message: String,
    @JvmField var status: String
)
