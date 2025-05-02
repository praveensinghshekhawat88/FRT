package com.callmangement.EHR.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class DealersInfo implements Serializable {
    @SerializedName("customerNameEng")
    private String customerNameEng;

    @SerializedName("customerNameHi")
    private String customerNameHi;

    @SerializedName("fpscode")
    private String fpscode;

    @SerializedName("mobileNo")
    private String mobileNo;

    public String getCustomerNameEng() {
        return customerNameEng;
    }

    public void setCustomerNameEng(String customerNameEng) {
        this.customerNameEng = customerNameEng;
    }

    public String getCustomerNameHi() {
        return customerNameHi;
    }

    public void setCustomerNameHi(String customerNameHi) {
        this.customerNameHi = customerNameHi;
    }

    public String getFpscode() {
        return fpscode;
    }

    public void setFpscode(String fpscode) {
        this.fpscode = fpscode;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }
}

