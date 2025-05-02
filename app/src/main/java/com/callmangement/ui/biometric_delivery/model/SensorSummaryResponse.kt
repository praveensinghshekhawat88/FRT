package com.callmangement.ui.biometric_delivery.model

import java.io.Serializable

class SensorSummaryResponse : Serializable {
    var data: ArrayList<Data>? = null
    var message: String? = null
    var status: String? = null

    inner class Data : Serializable {
        var total_Distributed_BiometricSensor: String? = null
        var total_Mapped_BiometricSensor: String? = null
        var total_L0_Machine: String? = null
        var total_Pending: String? = null
        var districtId: String? = null
        var district: String? = null
    }
}
