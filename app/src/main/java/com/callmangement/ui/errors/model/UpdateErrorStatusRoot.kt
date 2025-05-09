package com.callmangement.ui.errors.model

import com.google.gson.annotations.SerializedName

class UpdateErrorStatusRoot(
    @field:SerializedName("Response") var response: UpdateErrorStatusResponse,
    @JvmField var status: String
)
