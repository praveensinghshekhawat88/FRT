package com.callmangement.EHR.models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class ModelAttendanceListing {
    public int totalItems;
    public int totalPages;
    public List<AttendanceDetailsInfo> Data;
    public int currentPage;
    public String message;
    public String status;


    public ModelAttendanceListing(int totalItems, int totalPages, List<AttendanceDetailsInfo> data, int currentPage, String message, String status) {
        this.totalItems = totalItems;
        this.totalPages = totalPages;
        Data = data;
        this.currentPage = currentPage;
        this.message = message;
        this.status = status;
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

    public List<AttendanceDetailsInfo> getData() {
        return Data;
    }

    public void setData(List<AttendanceDetailsInfo> data) {
        Data = data;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
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

