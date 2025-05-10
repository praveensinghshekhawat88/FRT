package com.callmangement.ui.iris_derivery_installation.Model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class CheckIrisSerialNoResponse : Serializable {
    @JvmField
    @SerializedName("Response")
    var response: Response? = null
    @JvmField
    var message: String? = null
    @JvmField
    var status: String? = null
}



