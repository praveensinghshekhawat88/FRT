package com.callmangement.model.complaints

import com.google.gson.annotations.SerializedName

class ModelChallanUploadComplaint(var totalItems: Int, var totalPages: Int, var currentPage: Int) {
    @SerializedName("status")
    var status: String? = null

    @SerializedName("message")
    var message: String? = null

    @SerializedName("Data")
    var complaint_List: MutableList<ModelComplaintList> = ArrayList()
        get() {
            if (field == null) return ArrayList()
            return field
        }
}
