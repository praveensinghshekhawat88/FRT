package com.callmangement.model.district

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class ModelDistrictList : Serializable {
    @SerializedName("createdBy")
    var createdBy: String? = null

    @JvmField
    @SerializedName("districtNameHi")
    var districtNameHi: String? = null

    @JvmField
    @SerializedName("districtNameEng")
    var districtNameEng: String? = null

    @SerializedName("updatedBy")
    var updatedBy: String? = null

    @SerializedName("districtId")
    var districtId: String? = null
        get() {
            if (field == null) return "0"
            return field
        }

    @SerializedName("orderBySrNo")
    var orderBySrNo: String? = null

    @SerializedName("createdOn")
    var createdOn: String? = null

    @SerializedName("updatedOn")
    var updatedOn: String? = null

    @SerializedName("isActive")
    var isActive: String? = null

    @SerializedName("districtCode")
    var districtCode: String? = null

    @SerializedName("email")
    var email: String? = null

    override fun toString(): String {
        return districtNameEng!!
    }
}
