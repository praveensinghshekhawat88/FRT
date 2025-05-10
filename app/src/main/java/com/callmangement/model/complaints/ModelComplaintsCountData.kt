package com.callmangement.model.complaints

import com.google.gson.annotations.SerializedName

class ModelComplaintsCountData {
    @SerializedName("total")
    var total: Int? = null

    @SerializedName("resolved")
    var resolved: Int? = null

    @SerializedName("notResolve")
    var notResolve: Int? = null

    @SerializedName("sendToSECenter")
    var sendToSECenter: Int? = null

    @SerializedName("uploadPendingChallan")
    var uploadPendingChallan: Int? = null
}
