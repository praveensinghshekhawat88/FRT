package com.callmangement.EHR.models;

import java.io.Serializable;

public class FeedbackbyDCListChronology implements Serializable {

    public String id;
    public String calendarType;


    public FeedbackbyDCListChronology(String id, String calendarType) {
        this.id = id;
        this.calendarType = calendarType;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCalendarType() {
        return calendarType;
    }

    public void setCalendarType(String calendarType) {
        this.calendarType = calendarType;
    }
}
