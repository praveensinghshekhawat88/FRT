package com.callmangement.model.reports;

import com.callmangement.model.complaints.ModelComplaintList;

import java.util.List;

public class MonthReportModel {
    String date;
    List<ModelComplaintList> list;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public List<ModelComplaintList> getList() {
        return list;
    }

    public void setList(List<ModelComplaintList> list) {
        this.list = list;
    }
}
