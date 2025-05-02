
package com.callmangement.model.pos_distribution_form;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ModelNewMachineDetailByFPSResponse {

    @SerializedName("New_Machine_Data")
    @Expose
    private NewMachineData newMachineData;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("status")
    @Expose
    private String status;

    public NewMachineData getNewMachineData() {
        return newMachineData;
    }

    public void setNewMachineData(NewMachineData newMachineData) {
        this.newMachineData = newMachineData;
    }

    public String getMessage() {
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
