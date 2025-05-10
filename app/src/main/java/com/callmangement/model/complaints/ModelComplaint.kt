package com.callmangement.model.complaints

import com.google.gson.annotations.SerializedName

class ModelComplaint(var totalItems: Int, var totalPages: Int, var currentPage: Int) {
    @SerializedName("status")
    var status: String? = null

    @SerializedName("message")
    var message: String? = null

    @SerializedName("Complaint_List")
    var complaint_List: List<ModelComplaintList> = ArrayList()
        get() {
            if (field == null) return ArrayList()
            return field
        }
}
