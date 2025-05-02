package com.callmangement.EHR.models;

public class DashboardUserDetails {
    public Object message;
    public int year;
    public int month;
    public boolean status;
    public int tour;
    public int absent;
    public String attendanceCount;
    public int leave;
    public int halfDay;
    public int persent;
    public String createdCampCount;
    public String organizedCampCount;
    public Object userId;


    public DashboardUserDetails(Object message, int year, int month, boolean status, int tour, int absent, String attendanceCount, int leave, int halfDay, int persent, String createdCampCount, String organizedCampCount, Object userId) {
        this.message = message;
        this.year = year;
        this.month = month;
        this.status = status;
        this.tour = tour;
        this.absent = absent;
        this.attendanceCount = attendanceCount;
        this.leave = leave;
        this.halfDay = halfDay;
        this.persent = persent;
        this.createdCampCount = createdCampCount;
        this.organizedCampCount = organizedCampCount;
        this.userId = userId;
    }


    public Object getMessage() {
        return message;
    }

    public void setMessage(Object message) {
        this.message = message;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public int getTour() {
        return tour;
    }

    public void setTour(int tour) {
        this.tour = tour;
    }

    public int getAbsent() {
        return absent;
    }

    public void setAbsent(int absent) {
        this.absent = absent;
    }

    public String getAttendanceCount() {
        return attendanceCount;
    }

    public void setAttendanceCount(String attendanceCount) {
        this.attendanceCount = attendanceCount;
    }

    public int getLeave() {
        return leave;
    }

    public void setLeave(int leave) {
        this.leave = leave;
    }

    public int getHalfDay() {
        return halfDay;
    }

    public void setHalfDay(int halfDay) {
        this.halfDay = halfDay;
    }

    public int getPersent() {
        return persent;
    }

    public void setPersent(int persent) {
        this.persent = persent;
    }

    public String getCreatedCampCount() {
        return createdCampCount;
    }

    public void setCreatedCampCount(String createdCampCount) {
        this.createdCampCount = createdCampCount;
    }

    public String getOrganizedCampCount() {
        return organizedCampCount;
    }

    public void setOrganizedCampCount(String organizedCampCount) {
        this.organizedCampCount = organizedCampCount;
    }

    public Object getUserId() {
        return userId;
    }

    public void setUserId(Object userId) {
        this.userId = userId;
    }
}
