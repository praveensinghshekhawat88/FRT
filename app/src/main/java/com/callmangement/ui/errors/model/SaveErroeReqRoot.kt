package com.callmangement.ui.errors.model

import com.google.gson.annotations.SerializedName

class SaveErroeReqRoot(
    @field:SerializedName("Response") var response: SaveErrorReqResponse,
    @JvmField var status: String
)
