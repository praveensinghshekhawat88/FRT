
package com.callmangement.model.fps_repeat_on_service_center;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ModelRepeatFpsComplaints {
    @SerializedName("Parts")
    @Expose
    private List<ModelRepeatFpsComplaintsList> parts = null;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("status")
    @Expose
    private String status;

    public List<ModelRepeatFpsComplaintsList> getParts() {
        return parts;
    }

    public void setParts(List<ModelRepeatFpsComplaintsList> parts) {
        this.parts = parts;
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
