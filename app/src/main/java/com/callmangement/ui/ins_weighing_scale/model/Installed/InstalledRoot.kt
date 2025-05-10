package com.callmangement.ui.ins_weighing_scale.model.Installed

import com.google.gson.annotations.SerializedName

class InstalledRoot(
    @field:SerializedName("Data") var data: ArrayList<InstalledDatum>,
    @JvmField var message: String,
    @JvmField var status: String
)
