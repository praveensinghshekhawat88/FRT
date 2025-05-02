package com.callmangement.model.attendance;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ModelAddLocationData implements Serializable {
    @SerializedName("User_Id") private String User_Id;
    @SerializedName("District_Id") private String District_Id;
    @SerializedName("Latitude") private String Latitude;
    @SerializedName("Longitude") private String Longitude;
    @SerializedName("Address") private String Address;
    @SerializedName("Location_Date_Time") private String Location_Date_Time;

    public String getUser_Id() {
        return User_Id;
    }

    public void setUser_Id(String user_Id) {
        User_Id = user_Id;
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

    public String getDistrict_Id() {
        return District_Id;
    }

    public void setDistrict_Id(String district_Id) {
        District_Id = district_Id;
    }

    public String getLocation_Date_Time() {
        return Location_Date_Time;
    }

    public void setLocation_Date_Time(String location_Date_Time) {
        Location_Date_Time = location_Date_Time;
    }
}
