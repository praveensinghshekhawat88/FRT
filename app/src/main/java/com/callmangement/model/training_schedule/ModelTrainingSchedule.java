package com.callmangement.model.training_schedule;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ModelTrainingSchedule {
    @SerializedName("status") private String status;
    @SerializedName("message") private String message;
    @SerializedName("Data") private List<ModelTrainingScheduleList> list = new ArrayList<>();

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<ModelTrainingScheduleList> getList() {
        return list;
    }

    public void setList(List<ModelTrainingScheduleList> list) {
        this.list = list;
    }
}
