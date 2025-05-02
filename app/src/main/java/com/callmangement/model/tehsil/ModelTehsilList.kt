package com.callmangement.model.tehsil

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class ModelTehsilList : Serializable {
    @SerializedName("mobileNo")
    var mobileNo: String? = null

    @SerializedName("updatedBy")
    var updatedBy: String? = null

    @SerializedName("createdBy")
    var createdBy: String? = null

    @SerializedName("updatedOn")
    var updatedOn: String? = null

    @JvmField
    @SerializedName("fk_DistrictId")
    var fk_DistrictId: String? = null

    @SerializedName("orderBySrNo")
    var orderBySrNo: String? = null

    @SerializedName("isActive")
    var isActive: String? = null

    @SerializedName("createdOn")
    var createdOn: String? = null

    @SerializedName("tehsilId")
    var tehsilId: String? = null
        get() {
            if (field == null) return "0"
            return field
        }

    @SerializedName("districtNameHi")
    var districtNameHi: String? = null

    @JvmField
    @SerializedName("districtNameEng")
    var districtNameEng: String? = null

    @JvmField
    @SerializedName("tehsilNameEng")
    var tehsilNameEng: String? = null

    @SerializedName("tehsilEMail")
    var tehsilEMail: String? = null

    @SerializedName("tehsilNameHi")
    var tehsilNameHi: String? = null

    @SerializedName("tehsilCode")
    var tehsilCode: String? = null

    override fun toString(): String {
        return tehsilNameEng!!
    }
}
