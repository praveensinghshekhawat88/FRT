package com.callmangement.model.reports;

import java.io.Serializable;

public class Days_Reports_Info implements Serializable {
    private String day;
    private int days_count;

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public int getDays_count() {
        return days_count;
    }

    public void setDays_count(int days_count) {
        this.days_count = days_count;
    }

    @Override
    public String toString() {
        return day;
    }
}
