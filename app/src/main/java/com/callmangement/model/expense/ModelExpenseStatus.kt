package com.callmangement.model.expense

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class ModelExpenseStatus : Serializable {
    @SerializedName("id")
    var id: String? = null
        get() {
            if (field == null) return "0"
            return field
        }

    @SerializedName("expense_status")
    var expense_status: String? = null

    override fun toString(): String {
        return expense_status!!
    }
}
