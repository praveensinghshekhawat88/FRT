package com.callmangement.EHR.models;

public class UpdateAttendanceStatusDatum {

    public Object userId;
    public String remark;
    public Object toUserId;
    public String mesg;
    public Object attendanceId;
    public boolean flagStatus;
    public Object attendanceStatusDes;
    public String attendanceStatus;
    public int statusID;


    public UpdateAttendanceStatusDatum(Object userId, String remark, Object toUserId, String mesg, Object attendanceId, boolean flagStatus, Object attendanceStatusDes, String attendanceStatus, int statusID) {
        this.userId = userId;
        this.remark = remark;
        this.toUserId = toUserId;
        this.mesg = mesg;
        this.attendanceId = attendanceId;
        this.flagStatus = flagStatus;
        this.attendanceStatusDes = attendanceStatusDes;
        this.attendanceStatus = attendanceStatus;
        this.statusID = statusID;
    }


    public Object getUserId() {
        return userId;
    }

    public void setUserId(Object userId) {
        this.userId = userId;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Object getToUserId() {
        return toUserId;
    }

    public void setToUserId(Object toUserId) {
        this.toUserId = toUserId;
    }

    public String getMesg() {
        return mesg;
    }

    public void setMesg(String mesg) {
        this.mesg = mesg;
    }

    public Object getAttendanceId() {
        return attendanceId;
    }

    public void setAttendanceId(Object attendanceId) {
        this.attendanceId = attendanceId;
    }

    public boolean isFlagStatus() {
        return flagStatus;
    }

    public void setFlagStatus(boolean flagStatus) {
        this.flagStatus = flagStatus;
    }

    public Object getAttendanceStatusDes() {
        return attendanceStatusDes;
    }

    public void setAttendanceStatusDes(Object attendanceStatusDes) {
        this.attendanceStatusDes = attendanceStatusDes;
    }

    public String getAttendanceStatus() {
        return attendanceStatus;
    }

    public void setAttendanceStatus(String attendanceStatus) {
        this.attendanceStatus = attendanceStatus;
    }

    public int getStatusID() {
        return statusID;
    }

    public void setStatusID(int statusID) {
        this.statusID = statusID;
    }
}
