package com.callmangement.model.inventrory

import com.google.gson.annotations.SerializedName

class ModelSEUsersList {
    @SerializedName("userTypeId")
    var userTypeId: String? = null

    @SerializedName("password")
    var password: String? = null

    @SerializedName("check_csrf")
    var check_csrf: String? = null

    @JvmField
    @SerializedName("userName")
    var userName: String? = null

    @SerializedName("createdBy")
    var createdBy: String? = null

    @SerializedName("createdOn")
    var createdOn: String? = null

    @SerializedName("updatedOn")
    var updatedOn: String? = null

    @SerializedName("districtId")
    var districtId: String? = null

    @SerializedName("isActive")
    var isActive: String? = null

    @SerializedName("updatedBy")
    var updatedBy: String? = null

    @SerializedName("loginMessage")
    var loginMessage: String? = null

    @SerializedName("loginStatus")
    var loginStatus: String? = null

    @JvmField
    @SerializedName("userId")
    var userId: String? = null

    @JvmField
    @SerializedName("userTypeName")
    var userTypeName: String? = null

    @SerializedName("emailId")
    var emailId: String? = null

    @JvmField
    @SerializedName("mobileNo")
    var mobileNo: String? = null

    @SerializedName("district")
    var district: String? = null

    override fun toString(): String {
        return userName!!
    }
}
