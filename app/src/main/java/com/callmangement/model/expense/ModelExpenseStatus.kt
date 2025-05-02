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
    private var expense_status: String? = null

    fun getExpense_status(): String {
        if (expense_status == null) return ""
        return expense_status!!
    }

    fun setExpense_status(expense_status: String?) {
        this.expense_status = expense_status
    }

    override fun toString(): String {
        return expense_status!!
    }
}
