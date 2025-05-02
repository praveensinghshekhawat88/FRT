package com.callmangement.model.expense

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ModelExpesne {
    @SerializedName("expensesList")
    @Expose
    private var modelExpensesListList: List<ModelExpensesList>? = null

    @SerializedName("message")
    @Expose
    var message: String? = null

    @JvmField
    @SerializedName("status")
    @Expose
    var status: String? = null

    var expensesList: List<ModelExpensesList>?
        get() {
            if (modelExpensesListList == null) return ArrayList()
            return modelExpensesListList
        }
        set(modelExpensesListList) {
            this.modelExpensesListList = modelExpensesListList
        }
}
