package com.callmangement.ehr.models

import com.google.gson.annotations.SerializedName

class ModelLoginData {
    @JvmField
    @SerializedName("userTypeId")
    var userTypeId: String? = null

    @SerializedName("password")
    var password: String? = null

    @SerializedName("check_csrf")
    var check_csrf: String? = null

    @JvmField
    @SerializedName("userName")
    var userName: String? = null

    @JvmField
    @SerializedName("userId")
    var userId: String? = null

    @JvmField
    @SerializedName("emailId")
    var emailId: String? = null

    @JvmField
    @SerializedName("userTypeName")
    var userTypeName: String? = null

    @JvmField
    @SerializedName("district")
    var district: String? = null

    @JvmField
    @SerializedName("mobileNo")
    var mobileNo: String? = null

    @SerializedName("createdBy")
    var createdBy: String? = null

    @SerializedName("updatedBy")
    var updatedBy: String? = null

    @SerializedName("updatedOn")
    var updatedOn: String? = null

    @SerializedName("createdOn")
    var createdOn: String? = null

    @SerializedName("isActive")
    var isActive: String? = null

    @JvmField
    @SerializedName("districtId")
    var districtId: String? = null
}
