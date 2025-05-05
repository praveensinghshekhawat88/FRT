package com.callmangement.ehr.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class ModelSEUserList : Serializable {
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

    @SerializedName("mobileNo")
    var mobileNo: String? = null

    @SerializedName("district")
    var district: String? = null

    @JvmField
    @SerializedName("userId")
    var userId: String? = null

    @SerializedName("updatedBy")
    var updatedBy: String? = null

    @SerializedName("districtId")
    var districtId: String? = null

    @SerializedName("emailId")
    var emailId: String? = null

    @SerializedName("userTypeName")
    var userTypeName: String? = null

    @SerializedName("loginStatus")
    var loginStatus: String? = null

    @SerializedName("loginMessage")
    var loginMessage: String? = null

    @SerializedName("updatedOn")
    var updatedOn: String? = null

    @SerializedName("createdOn")
    var createdOn: String? = null

    @SerializedName("isActive")
    var isActive: String? = null

    @SerializedName("employeeName")
    var employeeName: String? = null

    override fun toString(): String {
        return userName!!
    }
}
