package com.orion.synevent.models;

import com.google.gson.annotations.SerializedName;

import java.io.ObjectStreamException;
import java.util.List;

public class CalendarRaw {
    @SerializedName("activities")
    private List<DayBody> activities;

    public List<DayBody>  getActivities() {
        return activities;
    }
    public void setActivities(List<DayBody>  list) {
        this.activities = list;
    }
}
