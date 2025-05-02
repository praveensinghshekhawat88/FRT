package com.callmangement.EHR.models;

import java.io.Serializable;

public class AttendanceDetailsInfo implements Serializable {

    public Object comDateRange;
    public boolean verify_In;
    public boolean verify_Out;
    public int currentPage;
    public int totalPages;
    public int totalItems;
    public int id;
    public boolean status;
    public String username;
    public boolean isPagination;
    public Object pageNo;
    public Object pageSize;
    public String dayName;
    public String email;
    public int attendanceStatusId;
    public boolean isActive;
    public String attendanceStatus;
    public String addressLocPin_Out;
    public String addressLocPin_In;
    public String remark;
    public int userId;
    public Object userTypeId;
    public Object employeeName;
    public Object mobileNumber;
    public String createdOn;
    public String updatedOn;
    public String address_Out;
    public String latitude_Out;
    public String longitude_Out;
    public Object dateFrom;
    public String districtName;
    public Object dateTo;
    public String punchOutTime;
    public String punchInTime;
    public String latitude_In;
    public Object mesg;
    public String address_In;
    public String punchOutDate;
    public String outImagePath;
    public String inImagePath;
    public String longitude_In;
    public String userType;
    public String punchInDate;
    public int districtId;


    public AttendanceDetailsInfo(Object comDateRange, boolean verify_In, boolean verify_Out, int currentPage, int totalPages, int totalItems, int id, boolean status, String username, boolean isPagination, Object pageNo, Object pageSize, String dayName, String email, int attendanceStatusId, boolean isActive, String attendanceStatus, String addressLocPin_Out, String addressLocPin_In, String remark, int userId, Object userTypeId, Object employeeName, Object mobileNumber, String createdOn, String updatedOn, String address_Out, String latitude_Out, String longitude_Out, Object dateFrom, String districtName, Object dateTo, String punchOutTime, String punchInTime, String latitude_In, Object mesg, String address_In, String punchOutDate, String outImagePath, String inImagePath, String longitude_In, String userType, String punchInDate, int districtId) {
        this.comDateRange = comDateRange;
        this.verify_In = verify_In;
        this.verify_Out = verify_Out;
        this.currentPage = currentPage;
        this.totalPages = totalPages;
        this.totalItems = totalItems;
        this.id = id;
        this.status = status;
        this.username = username;
        this.isPagination = isPagination;
        this.pageNo = pageNo;
        this.pageSize = pageSize;
        this.dayName = dayName;
        this.email = email;
        this.attendanceStatusId = attendanceStatusId;
        this.isActive = isActive;
        this.attendanceStatus = attendanceStatus;
        this.addressLocPin_Out = addressLocPin_Out;
        this.addressLocPin_In = addressLocPin_In;
        this.remark = remark;
        this.userId = userId;
        this.userTypeId = userTypeId;
        this.employeeName = employeeName;
        this.mobileNumber = mobileNumber;
        this.createdOn = createdOn;
        this.updatedOn = updatedOn;
        this.address_Out = address_Out;
        this.latitude_Out = latitude_Out;
        this.longitude_Out = longitude_Out;
        this.dateFrom = dateFrom;
        this.districtName = districtName;
        this.dateTo = dateTo;
        this.punchOutTime = punchOutTime;
        this.punchInTime = punchInTime;
        this.latitude_In = latitude_In;
        this.mesg = mesg;
        this.address_In = address_In;
        this.punchOutDate = punchOutDate;
        this.outImagePath = outImagePath;
        this.inImagePath = inImagePath;
        this.longitude_In = longitude_In;
        this.userType = userType;
        this.punchInDate = punchInDate;
        this.districtId = districtId;
    }


    public Object getComDateRange() {
        return comDateRange;
    }

    public void setComDateRange(Object comDateRange) {
        this.comDateRange = comDateRange;
    }

    public boolean isVerify_In() {
        return verify_In;
    }

    public void setVerify_In(boolean verify_In) {
        this.verify_In = verify_In;
    }

    public boolean isVerify_Out() {
        return verify_Out;
    }

    public void setVerify_Out(boolean verify_Out) {
        this.verify_Out = verify_Out;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public int getTotalItems() {
        return totalItems;
    }

    public void setTotalItems(int totalItems) {
        this.totalItems = totalItems;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isPagination() {
        return isPagination;
    }

    public void setPagination(boolean pagination) {
        isPagination = pagination;
    }

    public Object getPageNo() {
        return pageNo;
    }

    public void setPageNo(Object pageNo) {
        this.pageNo = pageNo;
    }

    public Object getPageSize() {
        return pageSize;
    }

    public void setPageSize(Object pageSize) {
        this.pageSize = pageSize;
    }

    public String getDayName() {
        return dayName;
    }

    public void setDayName(String dayName) {
        this.dayName = dayName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getAttendanceStatusId() {
        return attendanceStatusId;
    }

    public void setAttendanceStatusId(int attendanceStatusId) {
        this.attendanceStatusId = attendanceStatusId;
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

    public String getAddressLocPin_Out() {
        return addressLocPin_Out;
    }

    public void setAddressLocPin_Out(String addressLocPin_Out) {
        this.addressLocPin_Out = addressLocPin_Out;
    }

    public String getAddressLocPin_In() {
        return addressLocPin_In;
    }

    public void setAddressLocPin_In(String addressLocPin_In) {
        this.addressLocPin_In = addressLocPin_In;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public Object getUserTypeId() {
        return userTypeId;
    }

    public void setUserTypeId(Object userTypeId) {
        this.userTypeId = userTypeId;
    }

    public Object getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(Object employeeName) {
        this.employeeName = employeeName;
    }

    public Object getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(Object mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(String createdOn) {
        this.createdOn = createdOn;
    }

    public String getUpdatedOn() {
        return updatedOn;
    }

    public void setUpdatedOn(String updatedOn) {
        this.updatedOn = updatedOn;
    }

    public String getAddress_Out() {
        return address_Out;
    }

    public void setAddress_Out(String address_Out) {
        this.address_Out = address_Out;
    }

    public String getLatitude_Out() {
        return latitude_Out;
    }

    public void setLatitude_Out(String latitude_Out) {
        this.latitude_Out = latitude_Out;
    }

    public String getLongitude_Out() {
        return longitude_Out;
    }

    public void setLongitude_Out(String longitude_Out) {
        this.longitude_Out = longitude_Out;
    }

    public Object getDateFrom() {
        return dateFrom;
    }

    public void setDateFrom(Object dateFrom) {
        this.dateFrom = dateFrom;
    }

    public String getDistrictName() {
        return districtName;
    }

    public void setDistrictName(String districtName) {
        this.districtName = districtName;
    }

    public Object getDateTo() {
        return dateTo;
    }

    public void setDateTo(Object dateTo) {
        this.dateTo = dateTo;
    }

    public String getPunchOutTime() {
        return punchOutTime;
    }

    public void setPunchOutTime(String punchOutTime) {
        this.punchOutTime = punchOutTime;
    }

    public String getPunchInTime() {
        return punchInTime;
    }

    public void setPunchInTime(String punchInTime) {
        this.punchInTime = punchInTime;
    }

    public String getLatitude_In() {
        return latitude_In;
    }

    public void setLatitude_In(String latitude_In) {
        this.latitude_In = latitude_In;
    }

    public Object getMesg() {
        return mesg;
    }

    public void setMesg(Object mesg) {
        this.mesg = mesg;
    }

    public String getAddress_In() {
        return address_In;
    }

    public void setAddress_In(String address_In) {
        this.address_In = address_In;
    }

    public String getPunchOutDate() {
        return punchOutDate;
    }

    public void setPunchOutDate(String punchOutDate) {
        this.punchOutDate = punchOutDate;
    }

    public String getOutImagePath() {
        return outImagePath;
    }

    public void setOutImagePath(String outImagePath) {
        this.outImagePath = outImagePath;
    }

    public String getInImagePath() {
        return inImagePath;
    }

    public void setInImagePath(String inImagePath) {
        this.inImagePath = inImagePath;
    }

    public String getLongitude_In() {
        return longitude_In;
    }

    public void setLongitude_In(String longitude_In) {
        this.longitude_In = longitude_In;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getPunchInDate() {
        return punchInDate;
    }

    public void setPunchInDate(String punchInDate) {
        this.punchInDate = punchInDate;
    }

    public int getDistrictId() {
        return districtId;
    }

    public void setDistrictId(int districtId) {
        this.districtId = districtId;
    }
}

