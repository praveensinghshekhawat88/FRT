package com.callmangement.ui.ins_weighing_scale.model.Installed;

import java.util.ArrayList;

public class InstalledRoot {

    public ArrayList<InstalledDatum> Data;
    public String message;
    public String status;

    public InstalledRoot(ArrayList<InstalledDatum> data, String message, String status) {
        Data = data;
        this.message = message;
        this.status = status;
    }

    public ArrayList<InstalledDatum> getData() {
        return Data;
    }

    public void setData(ArrayList<InstalledDatum> data) {
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
