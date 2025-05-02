package com.callmangement.model.complaints

import com.google.gson.annotations.SerializedName

class ModelComplaintsCountData {
    @JvmField
    @SerializedName("total")
    var total: Int? = null

    @JvmField
    @SerializedName("resolved")
    var resolved: Int? = null

    @JvmField
    @SerializedName("notResolve")
    var notResolve: Int? = null

    @JvmField
    @SerializedName("sendToSECenter")
    var sendToSECenter: Int? = null

    @JvmField
    @SerializedName("uploadPendingChallan")
    var uploadPendingChallan: Int? = null
}
