package com.callmangement.ui.errors.model

import com.google.gson.annotations.SerializedName

class GetErrorTypesRoot(
    @field:SerializedName("Data") var data: ArrayList<GetErrortypesDatum>,
    @JvmField var message: String,
    @JvmField var status: String
)
