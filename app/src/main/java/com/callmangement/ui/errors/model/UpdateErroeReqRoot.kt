package com.callmangement.ui.errors.model

import com.google.gson.annotations.SerializedName

class UpdateErroeReqRoot(
    @field:SerializedName("Response") var response: UpdateErrorReqResponse,
    @JvmField var status: String
)
