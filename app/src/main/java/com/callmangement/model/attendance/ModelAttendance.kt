package com.callmangement.model.attendance

import com.google.gson.annotations.SerializedName

class ModelAttendance {
    //    @SerializedName("data") ModelAttendanceData data = new ModelAttendanceData();
    @JvmField
    @SerializedName("status")
    var status: String? = null

    //    public ModelAttendanceData getData() {
    @SerializedName("message")
    var message: String? = null

    //        return data;
    //    }
    //
    //    public void setData(ModelAttendanceData data) {
    //        this.data = data;
    //    }
}
