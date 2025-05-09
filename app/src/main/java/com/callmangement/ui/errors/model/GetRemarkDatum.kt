package com.callmangement.ui.errors.model

class GetRemarkDatum(
    var isStatus: Boolean,
    var message: String,
    var fpscode: String,
    var createdBy: Any,
    @JvmField var userType: String,
    var errorRegNo: String,
    @JvmField var remark: String,
    var errorStatusId: Int,
    var errorId: Int,
    var createdOn: Any,
    var isActive: Boolean,
    var remarkId: Int,
    @JvmField var errorStatus: String,
    @JvmField var remarkDate: String
)
