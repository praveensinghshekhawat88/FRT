package com.callmangement.model.expense

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class ModelExpensesList : Serializable {
    @SerializedName("status")
    @Expose
    var status: Boolean? = null

    @SerializedName("fileName")
    @Expose
    var fileName: String? = null

    @SerializedName("updatedOnStr")
    @Expose
    var updatedOnStr: Any? = null

    @SerializedName("createdBy")
    @Expose
    var createdBy: Int? = null

    @SerializedName("updatedOn")
    @Expose
    var updatedOn: Any? = null

    @SerializedName("userId")
    @Expose
    var userId: Int? = null

    @SerializedName("updatedBy")
    @Expose
    var updatedBy: Int? = null

    @SerializedName("remark")
    @Expose
    var remark: String? = null
        get() {
            if (field == null) return ""
            return field
        }

    @SerializedName("createdOn")
    @Expose
    var createdOn: Any? = null

    @JvmField
    @SerializedName("filePath")
    @Expose
    var filePath: String? = null

    @SerializedName("isActive")
    @Expose
    var isActive: Boolean? = null

    @JvmField
    @SerializedName("totalExAmount")
    @Expose
    var totalExAmount: Float? = null

    @SerializedName("fileType")
    @Expose
    var fileType: String? = null

    @SerializedName("fileContentType")
    @Expose
    var fileContentType: String? = null

    @SerializedName("expenseStatusID")
    @Expose
    var expenseStatusID: Int? = null
        get() {
            if (field == null) return 0
            return field
        }

    @SerializedName("district")
    @Expose
    var district: String? = null
        get() {
            if (field == null) return ""
            return field
        }

    @SerializedName("createdOnStr")
    @Expose
    var createdOnStr: String? = null
        get() {
            if (field == null) return ""
            return field
        }

    @SerializedName("districtId")
    @Expose
    var districtId: Int? = null

    @SerializedName("msg")
    @Expose
    var msg: String? = null

    @SerializedName("completedOnStr")
    @Expose
    var completedOnStr: String? = null
        get() {
            if (field == null) return ""
            return field
        }

    @SerializedName("se_Name")
    @Expose
    var seName: String? = null
        get() {
            if (field == null) return ""
            return field
        }

    @SerializedName("completedOn")
    @Expose
    var completedOn: Any? = null

    @SerializedName("expenseId")
    @Expose
    var expenseId: Int? = null
        get() {
            if (field == null) return 0
            return field
        }

    @SerializedName("expenseStatus")
    @Expose
    var expenseStatus: String? = null
        get() {
            if (field == null) return ""
            return field
        }

    @SerializedName("se_ContactNo")
    @Expose
    var seContactNo: String? = null

    @SerializedName("completedBy")
    @Expose
    var completedBy: Int? = null


    @JvmField
    @SerializedName("courierName")
    @Expose
    var courierName: String? = null


    @JvmField
    @SerializedName("docketNo")
    @Expose
    var docketNo: String? = null
}
