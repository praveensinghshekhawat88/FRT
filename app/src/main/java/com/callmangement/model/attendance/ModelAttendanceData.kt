package com.callmangement.model.attendance

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class ModelAttendanceData : Serializable {
    @SerializedName("User_Id")
    var user_Id: Int = 0

    @SerializedName("User_Type")
    var user_Type: String? = null

    @SerializedName("User_Type_Id")
    var user_Type_Id: String? = null

    @SerializedName("District_Id")
    var district_Id: Int = 0

    @SerializedName("Username")
    var username: String? = null

    @SerializedName("Email")
    var email: String? = null

    @SerializedName("Mobile_Number")
    var mobile_Number: String? = null

    @SerializedName("Latitude")
    var latitude: String? = null

    @SerializedName("Longitude")
    var longitude: String? = null

    @SerializedName("Address")
    var address: String? = null

    @SerializedName("Punch_In_Date")
    var punch_In_Date: String? = null

    @SerializedName("Punch_In_Time")
    var punch_In_Time: String? = null

    @SerializedName("Latitude_Out")
    var latitude_Out: String? = null

    @SerializedName("Longitude_Out")
    var longitude_Out: String? = null

    @SerializedName("Address_Out")
    var address_Out: String? = null

    @SerializedName("Punch_Out_Date")
    var punch_Out_Date: String? = null

    @SerializedName("Punch_Out_Time")
    var punch_Out_Time: String? = null

    @SerializedName("CreatedOn")
    var createdOn: String? = null

    @SerializedName("UpdatedOn")
    var updatedOn: String? = null

    @SerializedName("IsActive")
    var isActive: String? = null
}
