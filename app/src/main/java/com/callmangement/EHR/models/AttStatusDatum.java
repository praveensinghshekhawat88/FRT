package com.callmangement.EHR.models;

import java.io.Serializable;

public class AttStatusDatum implements Serializable {
    public Object createdOn;
    public Object createdBy;
    public boolean flagStatus;
    public boolean isActive;
    public String attendanceStatus;
    public String attendanceStatusDes;
    public Object mesg;
    public int statusID;


    public AttStatusDatum(Object createdOn, Object createdBy, boolean flagStatus, boolean isActive, String attendanceStatus, String attendanceStatusDes, Object mesg, int statusID) {
        this.createdOn = createdOn;
        this.createdBy = createdBy;
        this.flagStatus = flagStatus;
        this.isActive = isActive;
        this.attendanceStatus = attendanceStatus;
        this.attendanceStatusDes = attendanceStatusDes;
        this.mesg = mesg;
        this.statusID = statusID;
    }


    public Object getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Object createdOn) {
        this.createdOn = createdOn;
    }

    public Object getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Object createdBy) {
        this.createdBy = createdBy;
    }

    public boolean isFlagStatus() {
        return flagStatus;
    }

    public void setFlagStatus(boolean flagStatus) {
        this.flagStatus = flagStatus;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public String getAttendanceStatus() {
        return attendanceStatus;
    }

    public void setAttendanceStatus(String attendanceStatus) {
        this.attendanceStatus = attendanceStatus;
    }

    public String getAttendanceStatusDes() {
        return attendanceStatusDes;
    }

    public void setAttendanceStatusDes(String attendanceStatusDes) {
        this.attendanceStatusDes = attendanceStatusDes;
    }

    public Object getMesg() {
        return mesg;
    }

    public void setMesg(Object mesg) {
        this.mesg = mesg;
    }

    public int getStatusID() {
        return statusID;
    }

    public void setStatusID(int statusID) {
        this.statusID = statusID;
    }
}
