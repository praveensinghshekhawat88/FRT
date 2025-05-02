package com.callmangement.ui.biometric_delivery.model

import java.io.Serializable

class SensorDistributionDetailsListResp : Serializable {
    var totalItems: Int = 0
    var totalPages: Int = 0
    var data: ArrayList<Data>? = null
    var currentPage: Int = 0
    var message: String? = null
    var status: String? = null

    class Data : Serializable {
        var isStatus: Boolean = false
        var message: String? = null
        var totalItems: Int = 0
        var totalPages: Int = 0
        var districtId: Int = 0
        var fpscode: String? = null
        var district: String? = null
        var deviceCode: String? = null
        var isBiometricMapped: Boolean = false
        var isBiometrictDistributed: Boolean = false
        var biometricSensorStatus: String? = null
        var biometrictMappedOnStr: String? = null
        var biometricSensorImage: String? = null
    }
}
