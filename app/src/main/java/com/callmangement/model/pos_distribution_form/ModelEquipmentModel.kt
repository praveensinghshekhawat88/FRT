package com.callmangement.model.pos_distribution_form

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class ModelEquipmentModel : Serializable {
    @SerializedName("id")
    var id: String? = null
        get() {
            if (field == null) return "0"
            return field
        }

    @SerializedName("name")
    var name: String? = null

    override fun toString(): String {
        return name!!
    }
}
