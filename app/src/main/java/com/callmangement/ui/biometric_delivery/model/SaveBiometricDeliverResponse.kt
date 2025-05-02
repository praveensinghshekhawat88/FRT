package com.callmangement.ui.biometric_delivery.model


class SaveBiometricDeliverResponse {
    var response: SaveBiometricResponse? = null
    var status: String? = null

    inner class SaveBiometricResponse(
        var ticketNo: String,
        var message: String,
        var isStatus: Boolean
    )
}

