
package com.callmangement.model.pos_distribution_form;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ModelOldMachineDetailByFPSResponse {

    @SerializedName("Old_Machine_Data")
    @Expose
    private OldMachineData oldMachineData;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("status")
    @Expose
    private String status;

    public OldMachineData getOldMachineData() {
        return oldMachineData;
    }

    public void setOldMachineData(OldMachineData oldMachineData) {
        this.oldMachineData = oldMachineData;
    }

    public String getMessage() {
        if (message == null)
            return "null";
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
