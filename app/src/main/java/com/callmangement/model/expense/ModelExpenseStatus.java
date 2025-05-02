package com.callmangement.model.expense;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ModelExpenseStatus implements Serializable {
    @SerializedName("id") private String id;
    @SerializedName("expense_status") private String expense_status;

    public String getId() {
        if (id == null)
            return "0";
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getExpense_status() {
        if (expense_status == null)
            return "";
        return expense_status;
    }

    public void setExpense_status(String expense_status) {
        this.expense_status = expense_status;
    }

    @Override
    public String toString() {
        return expense_status;
    }
}
