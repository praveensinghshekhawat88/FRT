package com.callmangement.ui.ins_weighing_scale.model.Count

class CountDatum(
    var isStatus: Boolean,
    var message: String,
    @JvmField var totalDelivered: Int,
    var deliveryPending: Int,
    @JvmField var installationPending: Int,
    var totalDeliveryPending: Int,
    var totalDispatched: Int,
    var totalInstalled: Int,
    var totalInstallationPending: Int,
    var districtId: Int,
    var districtName: Any,
    var totalFPS: Int,
    var dispatched: Int,
    var delivered: Int,
    @JvmField var installed: Int
)
