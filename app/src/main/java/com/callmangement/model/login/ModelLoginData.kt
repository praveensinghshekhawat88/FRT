package com.callmangement.model.login

import com.google.gson.annotations.SerializedName

class ModelLoginData {
    @SerializedName("userTypeId")
    var userTypeId: String? = null

    @SerializedName("password")
    var password: String? = null

    @SerializedName("check_csrf")
    var check_csrf: String? = null

    @SerializedName("userName")
    var userName: String? = null

    @SerializedName("userId")
    var userId: String? = null

    @SerializedName("emailId")
    var emailId: String? = null

    @SerializedName("userTypeName")
    var userTypeName: String? = null

    @SerializedName("district")
    var district: String? = null

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

    @SerializedName("districtId")
    var districtId: String? = null
}
