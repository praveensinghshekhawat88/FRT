package com.callmangement.ehr.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class CampDetailsInfo(@JvmField @field:SerializedName("blockName") var blockName: Any) : Serializable {
    @JvmField
    @SerializedName("status")
    var status: String? = null

    @SerializedName("message")
    var message: String? = null

    @JvmField
    @SerializedName("address")
    var address: String? = null

    @SerializedName("tehsilNameHi")
    var tehsilNameHi: String? = null

    @SerializedName("tehsilNameEng")
    var tehsilNameEng: String? = null

    @SerializedName("tstatus")
    var tstatus: String? = null

    @JvmField
    @SerializedName("organizeDate")
    var organizeDate: String? = null

    @SerializedName("isActive")
    var isActive: String? = null

    @JvmField
    @SerializedName("description")
    var description: String? = null

    @SerializedName("districtNameHi")
    var districtNameHi: String? = null

    @SerializedName("tehsilName")
    var tehsilName: String? = null

    @SerializedName("updatedBy")
    var updatedBy: String? = null

    @SerializedName("createdBy")
    var createdBy: String? = null

    @SerializedName("createdOn")
    var createdOn: String? = null

    @SerializedName("updatedOn")
    var updatedOn: String? = null

    @JvmField
    @SerializedName("tstatusId")
    var tstatusId: String? = null

    @SerializedName("title")
    var title: String? = null

    @JvmField
    @SerializedName("endDate")
    var endDate: String? = null

    @JvmField
    @SerializedName("blockId")
    var blockId: String? = null

    @JvmField
    @SerializedName("trainingId")
    var trainingId: String? = null

    @JvmField
    @SerializedName("trainingNo")
    var trainingNo: String? = null

    @JvmField
    @SerializedName("districtID")
    var districtID: String? = null

    @JvmField
    @SerializedName("tehsilID")
    var tehsilID: String? = null

    @JvmField
    @SerializedName("startDate")
    var startDate: String? = null

    @JvmField
    @SerializedName("districtNameEng")
    var districtNameEng: String? = null

    @SerializedName("UplodedCampformCount")
    var uplodedCampformCount: String? = null

    @SerializedName("UplodedCampPhotoCount")
    var uplodedCampPhotoCount: String? = null
}

