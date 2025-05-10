package com.callmangement.ui.iris_derivery_installation.Model

import com.google.gson.annotations.SerializedName
import java.io.Serializable


class ReplacementTypesResponse : Serializable {
    @JvmField
    @SerializedName("ReplacementTypes_List")
    var replacementTypes_List: ArrayList<ReplacementTypesList>? = null
    @JvmField
    var message: String? = null
    @JvmField
    var status: String? = null

    inner class ReplacementTypesList : Serializable {
        var isStatus: Boolean = false
        var createdBy: String? = null
        @JvmField
        var replaceType: String? = null
        var replaceTypeDesc: String? = null
        var isActive: Boolean = false
        @JvmField
        var replaceTypeId: String? = null
        var createdOn: String? = null
        var updatedOn: String? = null
        var updatedBy: String? = null
    }
}


