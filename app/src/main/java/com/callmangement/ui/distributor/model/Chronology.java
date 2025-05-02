
package com.callmangement.ui.distributor.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Chronology implements Serializable {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("calendarType")
    @Expose
    private String calendarType;

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
