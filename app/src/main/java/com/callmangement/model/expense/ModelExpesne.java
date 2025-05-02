
package com.callmangement.model.expense;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ModelExpesne {
    @SerializedName("expensesList")
    @Expose
    private List<ModelExpensesList> modelExpensesListList = null;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("status")
    @Expose
    private String status;

    public List<ModelExpensesList> getExpensesList() {
        if (modelExpensesListList == null)
            return new ArrayList<>();
        return modelExpensesListList;
    }

    public void setExpensesList(List<ModelExpensesList> modelExpensesListList) {
        this.modelExpensesListList = modelExpensesListList;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
