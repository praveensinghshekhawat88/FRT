package com.callmangement.model.training_schedule;

public class ModelTrainingScheduleFormAddItem {
    String name;
    String fpsCode;
    String phone;

    public ModelTrainingScheduleFormAddItem(String name, String fpsCode, String phone) {
        this.name = name;
        this.fpsCode = fpsCode;
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFpsCode() {
        return fpsCode;
    }

    public void setFpsCode(String fpsCode) {
        this.fpsCode = fpsCode;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
