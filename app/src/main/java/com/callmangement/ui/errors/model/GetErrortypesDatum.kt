package com.callmangement.ui.errors.model

import com.google.gson.annotations.SerializedName

class GetErrortypesDatum {
    @SerializedName("status")
    var isStatus: Boolean = false

    @SerializedName("message")
    var message: String? = null

    @SerializedName("createdBy")
    var createdBy: Any? = null

    @SerializedName("isActive")
    var isActive: Boolean = false

    @JvmField
    @SerializedName("errorType")
    var errorType: String

    @JvmField
    @SerializedName("errorTypeId")
    var errorTypeId: Int

    @SerializedName("remark")
    var remark: String? = null

    @SerializedName("createdOn")
    var createdOn: Any? = null

    @SerializedName("updatedOn")
    var updatedOn: Any? = null

    @SerializedName("updatedBy")
    var updatedBy: Any? = null

    constructor(
        status: Boolean,
        message: String?,
        createdBy: Any?,
        isActive: Boolean,
        errorType: String,
        errorTypeId: Int,
        remark: String?,
        createdOn: Any?,
        updatedOn: Any?,
        updatedBy: Any?
    ) {
        this.isStatus = status
        this.message = message
        this.createdBy = createdBy
        this.isActive = isActive
        this.errorType = errorType
        this.errorTypeId = errorTypeId
        this.remark = remark
        this.createdOn = createdOn
        this.updatedOn = updatedOn
        this.updatedBy = updatedBy
    }

    constructor(errorType: String, errorTypeId: Int) {
        this.errorType = errorType
        this.errorTypeId = errorTypeId
    }
}
