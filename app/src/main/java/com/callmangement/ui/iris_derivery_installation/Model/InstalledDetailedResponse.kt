package com.callmangement.ui.iris_derivery_installation.Model

import com.google.gson.annotations.SerializedName


class InstalledDetailedResponse(
    @field:SerializedName("Data") var data: InstalledDtlDataIris,
    var message: String,
    @JvmField var status: String
)
