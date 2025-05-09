package com.callmangement.ui.errors.model

import java.io.Serializable

class GetPosDeviceErrorDatum(
    var isStatus: Boolean,
    var message: String,
    @JvmField var districtNameEng: String,
    @JvmField var dealerMobileNo: String,
    @JvmField var fpscode: String,
    var createdBy: Int,
    var userId: Any,
    var districtId: Int,
    var updatedBy: Int,
    @JvmField var dealerName: String,
    @JvmField var errorRegNo: String,
    @JvmField var deviceCode: String,
    @JvmField var remark: String,
    @JvmField var errorStatusId: Int,
    @JvmField var errorId: Int,
    @JvmField var errorTypeId: Int,
    var updatedOn: String,
    var createdOn: String,
    var isActive: Boolean,
    var block: String,
    @JvmField var resolveDate: String,
    var updatedByName: String,
    @JvmField var errorType: String,
    @JvmField var createdbyName: String,
    @JvmField var errorRegDate: String,
    @JvmField var errorStatus: String
) :
    Serializable
