package com.callmangement.ehr.models

import com.google.gson.annotations.SerializedName

class UpdateAttendanceStatusRoot(
    @SerializedName("Data")
    var data: UpdateAttendanceStatusDatum,
    @JvmField var message: String,
    @JvmField var status: String
)
