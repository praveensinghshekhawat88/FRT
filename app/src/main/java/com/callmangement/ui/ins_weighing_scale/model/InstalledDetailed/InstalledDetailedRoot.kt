package com.callmangement.ui.ins_weighing_scale.model.InstalledDetailed

import com.google.gson.annotations.SerializedName

class InstalledDetailedRoot(
    @field:SerializedName("Data") var data: InstalledDetailedData,
    var message: String,
    @JvmField var status: String
)
