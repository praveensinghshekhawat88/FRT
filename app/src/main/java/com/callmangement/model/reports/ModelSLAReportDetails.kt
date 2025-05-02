package com.callmangement.model.reports;

import com.callmangement.model.complaints.ModelComplaintList;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class ModelSLAReportDetails {
    @SerializedName("status") private String status;
    @SerializedName("message") private String message;
    @SerializedName("SLAReport_Detail") private List<ModelSLAReportDetailsList> sla_reports_infos = new ArrayList<>();

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

    public List<ModelSLAReportDetailsList> getSla_reports_infos() {
        return sla_reports_infos;
    }

    public void setSla_reports_infos(List<ModelSLAReportDetailsList> sla_reports_infos) {
        this.sla_reports_infos = sla_reports_infos;
    }
}
