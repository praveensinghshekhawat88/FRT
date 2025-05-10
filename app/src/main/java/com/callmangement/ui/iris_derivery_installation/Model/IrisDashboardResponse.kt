package com.callmangement.ui.iris_derivery_installation.Model

import com.google.gson.annotations.SerializedName
import java.io.Serializable


class IrisDashboardResponse : Serializable {
    @JvmField
    @SerializedName("Data")
    var data: ArrayList<Data>? = null
    @JvmField
    var message: String? = null
    @JvmField
    var status: String? = null


    inner class Data {
        var isStatus: Boolean = false
        var message: String? = null
        var totalDispatched: Int = 0
        var totalInstallationPending: Int = 0
        var totalIrisDevice: Int = 0
        var totalDeliveryPending: Int = 0
        @JvmField
        var totalIRISDeliverdInstalled: Int = 0
        @JvmField
        var totalIRISDispatched: Int = 0
        @JvmField
        var totalIRISDeliveryPending: Int = 0
        var totalWeinghingScale: Int = 0
        var totalDelivered: Int = 0
        var totalInstalled: Int = 0
        var installationPending: Int = 0
        var deliveryPending: Int = 0
        var districtName: String? = null
        var districtId: Int = 0
        var delivered: Int = 0
        var totalGoldTek: Int = 0
        var totalFPS: Int = 0
        var dispatched: Int = 0
        var installed: Int = 0
        var totalSipTek: Int = 0
    }
}

