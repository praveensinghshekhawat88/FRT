package com.callmangement.model.complaints

import com.google.gson.annotations.SerializedName

class ModelComplaint(@JvmField var totalItems: Int, @JvmField var totalPages: Int, @JvmField var currentPage: Int) {
    @JvmField
    @SerializedName("status")
    var status: String? = null

    @JvmField
    @SerializedName("message")
    var message: String? = null

    @SerializedName("Complaint_List")
    var complaint_List: List<ModelComplaintList>? = ArrayList()
        get() {
            if (field == null) return ArrayList()
            return field
        }
}
