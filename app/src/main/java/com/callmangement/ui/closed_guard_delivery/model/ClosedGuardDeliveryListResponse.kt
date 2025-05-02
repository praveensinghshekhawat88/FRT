// IrisDeliveryListResponse.java
// YApi QuickType插件生成，具体参考文档:https://plugins.jetbrains.com/plugin/18847-yapi-quicktype/documentation
package com.callmangement.ui.closed_guard_delivery.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class ClosedGuardDeliveryListResponse : Serializable {
    @SerializedName("Data")
    var data: ArrayList<Datum>? = null
    var message: String? = null
    var status: String? = null

    var totalItems: String? = null
    var totalPages: String? = null
    var currentPage: String? = null

    inner class Datum : Serializable {
        var message: String? = null
        var deviceModelId: String? = null
        var deviceTypeId: String? = null
        var fpscode: String? = null
        var status: String? = null
        var districtId: String? = null
        var districtName: String? = null
        var blockName: String? = null
        var dealerName: String? = null
        var dealerMobileNo: String? = null
        var posdeviceCode: String? = null
        var deliveryId: String? = null
        var isActive: String? = null
        var deviceType: String? = null
        var closeGuardDeliveryAddress: String? = null
        var closeGuardDeliverdByName: String? = null
        var closeGuardLatitude: String? = null
        var closeGuardLongitude: String? = null
        var closeGuardDeliverdBy: String? = null
        var closeGuardDeliverdOnStr: String? = null
        var isCloseGuardDeliverd: String? = null
        var closeGuardDeliverdOn: String? = null
        var serialNo: String? = null
        var deviceModelName: String? = null
        var cgphotoPath: String? = null
        var cgsignaturePath: String? = null
    }
} // Datum.java


