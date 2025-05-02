package com.callmangement.EHR.models;

import java.io.Serializable;

public class FeedbackbyDCListCreatedOn implements Serializable {

    public int year;
    public int monthValue;
    public int dayOfMonth;
    public int hour;
    public int minute;
    public int second;
    public int nano;
    public String dayOfWeek;
    public int dayOfYear;
    public String month;
    public FeedbackbyDCListChronology chronology;


    public FeedbackbyDCListCreatedOn(int year, int monthValue, int dayOfMonth, int hour, int minute, int second, int nano, String dayOfWeek, int dayOfYear, String month, FeedbackbyDCListChronology chronology) {
        this.year = year;
        this.monthValue = monthValue;
        this.dayOfMonth = dayOfMonth;
        this.hour = hour;
        this.minute = minute;
        this.second = second;
        this.nano = nano;
        this.dayOfWeek = dayOfWeek;
        this.dayOfYear = dayOfYear;
        this.month = month;
        this.chronology = chronology;
    }


    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonthValue() {
        return monthValue;
    }

    public void setMonthValue(int monthValue) {
        this.monthValue = monthValue;
    }

    public int getDayOfMonth() {
        return dayOfMonth;
    }

    public void setDayOfMonth(int dayOfMonth) {
        this.dayOfMonth = dayOfMonth;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public int getSecond() {
        return second;
    }

    public void setSecond(int second) {
        this.second = second;
    }

    public int getNano() {
        return nano;
    }

    public void setNano(int nano) {
        this.nano = nano;
    }

    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public int getDayOfYear() {
        return dayOfYear;
    }

    public void setDayOfYear(int dayOfYear) {
        this.dayOfYear = dayOfYear;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public FeedbackbyDCListChronology getChronology() {
        return chronology;
    }

    public void setChronology(FeedbackbyDCListChronology chronology) {
        this.chronology = chronology;
    }
}
