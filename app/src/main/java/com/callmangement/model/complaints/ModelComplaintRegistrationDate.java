package com.callmangement.model.complaints;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ModelComplaintRegistrationDate implements Serializable {
    @SerializedName("year") private String year;
    @SerializedName("month") private String month;
    @SerializedName("era") private String era;
    @SerializedName("leapYear") private String leapYear;
    @SerializedName("dayOfMonth") private String dayOfMonth;
    @SerializedName("monthValue") private Integer monthValue;
    @SerializedName("dayOfWeek") private String dayOfWeek;
    @SerializedName("dayOfYear") private String dayOfYear;

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getMonth() {
        if (month == null)
            return "";
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getEra() {
        return era;
    }

    public void setEra(String era) {
        this.era = era;
    }

    public String getLeapYear() {
        return leapYear;
    }

    public void setLeapYear(String leapYear) {
        this.leapYear = leapYear;
    }

    public String getDayOfMonth() {
        return dayOfMonth;
    }

    public void setDayOfMonth(String dayOfMonth) {
        this.dayOfMonth = dayOfMonth;
    }

    public Integer getMonthValue() {
        return monthValue;
    }

    public void setMonthValue(Integer monthValue) {
        this.monthValue = monthValue;
    }

    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public String getDayOfYear() {
        return dayOfYear;
    }

    public void setDayOfYear(String dayOfYear) {
        this.dayOfYear = dayOfYear;
    }
}
