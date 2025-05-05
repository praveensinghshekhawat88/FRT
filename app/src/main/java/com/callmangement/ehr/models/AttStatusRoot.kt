package com.callmangement.ehr.models

import com.google.gson.annotations.SerializedName

class AttStatusRoot(
    @SerializedName("Data")
    var data: MutableList<AttStatusDatum>,
    @JvmField var message: Any,
    @JvmField var status: String)
