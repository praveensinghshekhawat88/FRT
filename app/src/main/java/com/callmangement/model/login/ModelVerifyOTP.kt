package com.callmangement.model.login;

import com.google.gson.annotations.SerializedName;

public class ModelVerifyOTP {
    @SerializedName("status") private String status;
    @SerializedName("message") private String message;
    @SerializedName("id") private int id;
    @SerializedName("name") private String name;
    @SerializedName("email") private String email;
    @SerializedName("mobile") private String mobile;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
