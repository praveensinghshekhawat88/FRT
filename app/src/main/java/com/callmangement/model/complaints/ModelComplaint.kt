package com.callmangement.model.complaints;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class ModelComplaint {
    @SerializedName("status") private String status;
    @SerializedName("message") private String message;
    @SerializedName("Complaint_List") private List<ModelComplaintList> Complaint_List = new ArrayList<>();
    public int totalItems;
    public int totalPages;
    public int currentPage;

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

    public List<ModelComplaintList> getComplaint_List() {
        if (Complaint_List == null)
            return new ArrayList<>();
        return Complaint_List;
    }

    public void setComplaint_List(List<ModelComplaintList> complaint_List) {
        Complaint_List = complaint_List;
    }


    public ModelComplaint(int totalItems, int totalPages, int currentPage) {
        this.totalItems = totalItems;
        this.totalPages = totalPages;
        this.currentPage = currentPage;
    }


    public int getTotalItems() {
        return totalItems;
    }

    public void setTotalItems(int totalItems) {
        this.totalItems = totalItems;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }
}
