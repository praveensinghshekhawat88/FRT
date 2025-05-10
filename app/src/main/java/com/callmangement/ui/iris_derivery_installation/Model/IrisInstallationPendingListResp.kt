package com.callmangement.ui.iris_derivery_installation.Model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class IrisInstallationPendingListResp : Serializable {
    @JvmField
    @SerializedName("Data")
    var data: ArrayList<Datum>? = null
    @JvmField
    var message: String? = null
    @JvmField
    var status: String? = null

    inner class Datum : Serializable {
        var isStatus: Boolean = false
        var message: String? = null
        var deliveredBy: String? = null
        var serialNo: String? = null
        @JvmField
        var dealerMobileNo: String? = null
        var deviceSerialNo: String? = null
        var deliveredOnStr: String? = null
        var installedOnStr: String? = null
        var remark: String? = null
        var districtId: String? = null
        @JvmField
        var fpscode: String? = null
        @JvmField
        var blockName: String? = null
        var deviceModel: String? = null
        @JvmField
        var dealerName: String? = null
        @JvmField
        var districtName: String? = null
        @JvmField
        var ticketNo: String? = null
        var fpsdeviceCode: String? = null
        var shopAddress: String? = null
        var latitude: String? = null
        var longitude: String? = null
        var deviceType: String? = null
        var lastStatus: String? = null
        var installedBy: String? = null
        var installedOn: String? = null
        var deliveredOn: String? = null
    }
}


