package com.callmangement.ui.ins_weighing_scale.model.Login;

public class LoginRoot {
    public LoginUserDetails user_details;
    public String message;
    public String status;


    public LoginRoot(LoginUserDetails user_details, String message, String status) {
        this.user_details = user_details;
        this.message = message;
        this.status = status;
    }


    public LoginUserDetails getUser_details() {
        return user_details;
    }

    public void setUser_details(LoginUserDetails user_details) {
        this.user_details = user_details;
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
