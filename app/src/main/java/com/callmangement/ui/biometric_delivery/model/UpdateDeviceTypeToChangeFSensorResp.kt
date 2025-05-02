package com.callmangement.ui.biometric_delivery.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class UpdateDeviceTypeToChangeFSensorResp : Serializable {
    var message: String? = null
    var status: String? = null

    @SerializedName("Data")
    var data: Data? = null

    inner class Data : Serializable {
        var message: String? = null
        var isStatus: Boolean = false
        var isUpdatedDtype: Boolean = false
        var fpscode: String? = null
        var deviceCode: String? = null
        var infoMsgEn: String? = null
        var infoMsgHi: String? = null
        var infoDescMsgHi: String? = null
        var infoDescMsgEn: String? = null
    }
}
