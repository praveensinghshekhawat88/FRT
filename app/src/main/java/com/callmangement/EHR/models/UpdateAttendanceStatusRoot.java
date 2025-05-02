package com.callmangement.EHR.models;

public class UpdateAttendanceStatusRoot {
    public UpdateAttendanceStatusDatum Data;
    public String message;
    public String status;

    public UpdateAttendanceStatusRoot(UpdateAttendanceStatusDatum data, String message, String status) {
        Data = data;
        this.message = message;
        this.status = status;
    }


    public UpdateAttendanceStatusDatum getData() {
        return Data;
    }

    public void setData(UpdateAttendanceStatusDatum data) {
        Data = data;
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
