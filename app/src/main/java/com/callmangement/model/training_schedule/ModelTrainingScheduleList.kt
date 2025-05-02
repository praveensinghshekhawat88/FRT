package com.callmangement.model.training_schedule

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class ModelTrainingScheduleList : Serializable {
    @JvmField
    @SerializedName("address")
    var address: String? = null

    @JvmField
    @SerializedName("description")
    var description: String? = null

    @SerializedName("createdBy")
    var createdBy: String? = null

    @JvmField
    @SerializedName("tehsilNameEng")
    var tehsilNameEng: String? = null

    @SerializedName("tehsilNameHi")
    var tehsilNameHi: String? = null

    @JvmField
    @SerializedName("tehsilID")
    var tehsilID: String? = null

    @SerializedName("blockId")
    var blockId: String? = null

    @JvmField
    @SerializedName("trainingId")
    var trainingId: String? = null

    @JvmField
    @SerializedName("endDate")
    var endDate: String? = null

    @JvmField
    @SerializedName("trainingNo")
    var trainingNo: String? = null

    @JvmField
    @SerializedName("districtID")
    var districtID: String? = null

    @JvmField
    @SerializedName("startDate")
    var startDate: String? = null

    @SerializedName("tstatus")
    var tstatus: String? = null

    @SerializedName("createdOn")
    var createdOn: String? = null

    @SerializedName("isActive")
    var isActive: String? = null

    @SerializedName("updatedBy")
    var updatedBy: String? = null

    @SerializedName("updatedOn")
    var updatedOn: String? = null

    @SerializedName("districtNameHi")
    var districtNameHi: String? = null

    @SerializedName("districtNameEng")
    var districtNameEng: String? = null

    @JvmField
    @SerializedName("title")
    var title: String? = null
}
