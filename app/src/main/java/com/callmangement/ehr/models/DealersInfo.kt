package com.callmangement.ehr.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class DealersInfo : Serializable {
    @JvmField
    @SerializedName("customerNameEng")
    var customerNameEng: String? = null

    @SerializedName("customerNameHi")
    var customerNameHi: String? = null

    @JvmField
    @SerializedName("fpscode")
    var fpscode: String? = null

    @JvmField
    @SerializedName("mobileNo")
    var mobileNo: String? = null
}

