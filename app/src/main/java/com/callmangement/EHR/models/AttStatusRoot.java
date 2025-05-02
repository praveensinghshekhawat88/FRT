package com.callmangement.EHR.models;

import java.util.List;

public class AttStatusRoot {
    public List<AttStatusDatum> Data;
    public Object message;
    public String status;

    public AttStatusRoot(List<AttStatusDatum> data, Object message, String status) {
        Data = data;
        this.message = message;
        this.status = status;
    }


    public List<AttStatusDatum> getData() {
        return Data;
    }

    public void setData(List<AttStatusDatum> data) {
        Data = data;
    }

    public Object getMessage() {
        return message;
    }

    public void setMessage(Object message) {
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
