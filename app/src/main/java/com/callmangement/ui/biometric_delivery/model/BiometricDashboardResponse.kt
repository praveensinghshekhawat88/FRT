package com.callmangement.ui.biometric_delivery.model

import com.google.gson.annotations.SerializedName

class BiometricDashboardResponse {
    @SerializedName("Data")
    var data: Data? = null
    var message: String? = null
    var status: String? = null

    inner class Data {
        var isStatus: Boolean = false
        var message: String? = null
        var total_Distributed_BiometricSensor: String? = null
        var total_Mapped_BiometricSensor: String? = null
        var total_L0_Machine: String? = null
        var total_Pending: String? = null
        var districtId: String? = null
        var district: String? = null
    }
}
