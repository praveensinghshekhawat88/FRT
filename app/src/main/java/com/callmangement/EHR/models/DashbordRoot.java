package com.callmangement.EHR.models;

public class DashbordRoot {

    public DashboardUserDetails user_details;
    public Object message;
    public String status;


    public DashbordRoot(DashboardUserDetails user_details, Object message, String status) {
        this.user_details = user_details;
        this.message = message;
        this.status = status;
    }


    public DashboardUserDetails getUser_details() {
        return user_details;
    }

    public void setUser_details(DashboardUserDetails user_details) {
        this.user_details = user_details;
    }

    public Object getMessage() {
        return message;
    }

    public void setMessage(Object message) {
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
