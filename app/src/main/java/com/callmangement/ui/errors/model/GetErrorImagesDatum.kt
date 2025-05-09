package com.callmangement.ui.errors.model

import com.google.gson.annotations.SerializedName

class GetErrorImagesDatum(
    @field:SerializedName("status") var isStatus: Boolean,
    @field:SerializedName(
        "message"
    ) var message: String,
    @field:SerializedName("id") var id: Int,
    @field:SerializedName("contentType") var contentType: String,
    @field:SerializedName(
        "contentSize"
    ) var contentSize: String,
    @field:SerializedName("uploadedOn") var uploadedOn: Any,
    @field:SerializedName(
        "isActive"
    ) var isActive: Boolean,
    @field:SerializedName("errorId") var errorId: Int,
    @field:SerializedName(
        "errorRegNo"
    ) var errorRegNo: String,
    @JvmField @field:SerializedName("imagePath") var imagePath: String,
    @field:SerializedName(
        "imageName"
    ) var imageName: String,
    @field:SerializedName("uploadedBy") var uploadedBy: Any,
    @field:SerializedName(
        "fileType"
    ) var fileType: String,
    @field:SerializedName("srNo") var srNo: Any
)
