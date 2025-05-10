package com.callmangement.model.attendance

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class ModelAddLocationData : Serializable {
    @SerializedName("User_Id")
    var user_Id: String? = null

    @SerializedName("District_Id")
    var district_Id: String? = null

    @SerializedName("Latitude")
    var latitude: String? = null

    @SerializedName("Longitude")
    var longitude: String? = null

    @SerializedName("Address")
    var address: String? = null

    @SerializedName("Location_Date_Time")
    var location_Date_Time: String? = null
}
