package com.callmangement.model.WeighingDeliveryDetail

import com.google.gson.annotations.SerializedName

class weighingDelieryRoot(
    @field:SerializedName("Data") var data: weighingDeliveryData,
    var message: String,
    var status: String
)
