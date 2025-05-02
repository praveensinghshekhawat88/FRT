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
    private var name: String? = null

    fun getName(): String {
        if (name == null) return ""
        return name!!
    }

    fun setName(name: String?) {
        this.name = name
    }

    override fun toString(): String {
        return name!!
    }
}
