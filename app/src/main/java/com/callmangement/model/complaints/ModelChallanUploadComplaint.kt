package com.callmangement.model.complaints

import com.google.gson.annotations.SerializedName

class ModelChallanUploadComplaint(@JvmField var totalItems: Int, @JvmField var totalPages: Int, @JvmField var currentPage: Int) {
    @JvmField
    @SerializedName("status")
    var status: String? = null

    @JvmField
    @SerializedName("message")
    var message: String? = null

    @SerializedName("Data")
    var complaint_List: MutableList <ModelComplaintList>? = ArrayList()
        get() {
            if (field == null) return ArrayList()
            return field
        }
}
