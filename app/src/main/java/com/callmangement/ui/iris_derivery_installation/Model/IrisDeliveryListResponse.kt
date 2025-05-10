// IrisDeliveryListResponse.java
// YApi QuickType插件生成，具体参考文档:https://plugins.jetbrains.com/plugin/18847-yapi-quicktype/documentation
package com.callmangement.ui.iris_derivery_installation.Model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class IrisDeliveryListResponse : Serializable {
    @JvmField
    @SerializedName("Data")
    var data: ArrayList<Datum?>? = null
    @JvmField
    var message: String? = null
    @JvmField
    var status: String? = null

    @JvmField
    var totalItems: String? = null
    @JvmField
    var totalPages: String? = null
    @JvmField
    var currentPage: String? = null

    inner class Datum : Serializable {
        var deviceTypeId: Long = 0
        @JvmField
        var blockName: String? = null
        var latitude: String? = null
        @JvmField
        var fpscode: String? = null
        var isActive: Boolean = false
        @JvmField
        var shopAddress: String? = null
        var deliveryVerifyStatus: String? = null
        var deliverdByName: String? = null
        @JvmField
        var deliveryId: Long = 0
        @JvmField
        var ticketNo: String? = null
        @JvmField
        var deviceModelId: Long = 0
        @JvmField
        var irisdeliveredOnStr: String? = null
        var longitude: String? = null
        var deviceType: String? = null
        var isDeliveryVerify: Boolean = false
        var deliveryRemark: String? = null
        @JvmField
        var dealerName: String? = null
        var updatedBy: Long = 0
        @JvmField
        var districtName: String? = null
        @JvmField
        var deliverdStatus: String? = null
        @JvmField
        var deviceModelName: String? = null
        @JvmField
        var dealerMobileNo: String? = null
        var isDeliverdIRIS: Boolean = false
        var message: String? = null
        @JvmField
        var serialNo: String? = null
        @JvmField
        var districtId: Long = 0
        var deliveryVerifyBy: Long = 0
        var fpsdeviceCode: String? = null
        var status: Boolean = false
        var deliveredBy: String? = null
        var createdBy: String? = null
        var tranDateStr: String? = null
        var images: String? = null
        var createdOn: String? = null
        var updatedOn: String? = null
        var imagesDetail: String? = null
        var tranDate: String? = null
        var deliveryVerifyOnStr: String? = null
        var deliveryVerifyByName: String? = null
    }
} // Datum.java
// YApi QuickType插件生成，具体参考文档:https://plugins.jetbrains.com/plugin/18847-yapi-quicktype/documentation

