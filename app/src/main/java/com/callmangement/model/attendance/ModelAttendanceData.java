package com.callmangement.model.attendance;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ModelAttendanceData implements Serializable {
    @SerializedName("User_Id") private int User_Id;
    @SerializedName("User_Type") private String User_Type;
    @SerializedName("User_Type_Id") private String User_Type_Id;
    @SerializedName("District_Id") private int District_Id;
    @SerializedName("Username") private String Username;
    @SerializedName("Email") private String Email;
    @SerializedName("Mobile_Number") private String Mobile_Number;
    @SerializedName("Latitude") private String Latitude;
    @SerializedName("Longitude") private String Longitude;
    @SerializedName("Address") private String Address;
    @SerializedName("Punch_In_Date") private String Punch_In_Date;
    @SerializedName("Punch_In_Time") private String Punch_In_Time;
    @SerializedName("Latitude_Out") private String Latitude_Out;
    @SerializedName("Longitude_Out") private String Longitude_Out;
    @SerializedName("Address_Out") private String Address_Out;
    @SerializedName("Punch_Out_Date") private String Punch_Out_Date;
    @SerializedName("Punch_Out_Time") private String Punch_Out_Time;
    @SerializedName("CreatedOn") private String CreatedOn;
    @SerializedName("UpdatedOn") private String UpdatedOn;
    @SerializedName("IsActive") private String IsActive;

    public int getUser_Id() {
        return User_Id;
    }

    public void setUser_Id(int user_Id) {
        User_Id = user_Id;
    }

    public String getUser_Type() {
        return User_Type;
    }

    public void setUser_Type(String user_Type) {
        User_Type = user_Type;
    }

    public String getUser_Type_Id() {
        return User_Type_Id;
    }

    public void setUser_Type_Id(String user_Type_Id) {
        User_Type_Id = user_Type_Id;
    }

    public int getDistrict_Id() {
        return District_Id;
    }

    public void setDistrict_Id(int district_Id) {
        District_Id = district_Id;
    }

    public String getUsername() {
        return Username;
    }

    public void setUsername(String username) {
        Username = username;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getLatitude() {
        return Latitude;
    }

    public void setLatitude(String latitude) {
        Latitude = latitude;
    }

    public String getLongitude() {
        return Longitude;
    }

    public void setLongitude(String longitude) {
        Longitude = longitude;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getPunch_In_Date() {
        return Punch_In_Date;
    }

    public void setPunch_In_Date(String punch_In_Date) {
        Punch_In_Date = punch_In_Date;
    }

    public String getPunch_In_Time() {
        return Punch_In_Time;
    }

    public void setPunch_In_Time(String punch_In_Time) {
        Punch_In_Time = punch_In_Time;
    }

    public String getPunch_Out_Time() {
        return Punch_Out_Time;
    }

    public void setPunch_Out_Time(String punch_Out_Time) {
        Punch_Out_Time = punch_Out_Time;
    }

    public String getMobile_Number() {
        return Mobile_Number;
    }

    public void setMobile_Number(String mobile_Number) {
        Mobile_Number = mobile_Number;
    }

    public String getLatitude_Out() {
        return Latitude_Out;
    }

    public void setLatitude_Out(String latitude_Out) {
        Latitude_Out = latitude_Out;
    }

    public String getLongitude_Out() {
        return Longitude_Out;
    }

    public void setLongitude_Out(String longitude_Out) {
        Longitude_Out = longitude_Out;
    }

    public String getAddress_Out() {
        return Address_Out;
    }

    public void setAddress_Out(String address_Out) {
        Address_Out = address_Out;
    }

    public String getPunch_Out_Date() {
        return Punch_Out_Date;
    }

    public void setPunch_Out_Date(String punch_Out_Date) {
        Punch_Out_Date = punch_Out_Date;
    }

    public String getCreatedOn() {
        return CreatedOn;
    }

    public void setCreatedOn(String createdOn) {
        CreatedOn = createdOn;
    }

    public String getUpdatedOn() {
        return UpdatedOn;
    }

    public void setUpdatedOn(String updatedOn) {
        UpdatedOn = updatedOn;
    }

    public String getIsActive() {
        return IsActive;
    }

    public void setIsActive(String isActive) {
        IsActive = isActive;
    }
}
